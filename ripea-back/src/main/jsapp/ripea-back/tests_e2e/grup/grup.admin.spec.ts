import { test, expect, Page, Locator } from '@playwright/test';

import {
    logDebug,
    logInfo,
    getGrid,
    getRows,
    expectSuccessAlert,
    obrirMenuAccions,
    confirmarDialeg,
    esperarGridCarregat,
    guardaAmbDelay,
    triaMuiSelect,
    obrirDialegAmbReintent,
    netejarFiltre,
    waitApiGet,
    waitApiEntityLoad,
    seleccionarOpcioAutocompletament,
    seleccionarOpcioAutocompletamentPerText,
    assegurarRol,
} from '../utils/reactHelpers';
import { codiUsuariCredencials } from '../auth/login';

// ─────────────────────────────────────────────────────────────────────────────
// Manteniment de grups REACT (rol IPA_ADMIN). Equivalent JSP: grup.admin.jsp.spec.ts
//
// Els tests s'executen en l'ordre del fitxer i deixen la BD com l'han trobada:
// NETEJA PREVIA esborra les restes d'una execució interrompuda, CREAR crea dos grups
// (A i B), les proves intermèdies treballen sobre aquests grups, ESBORRAR elimina B pel
// camí individual i ESBORRAR MASSIU elimina A pel camí massiu. NETEJA FINAL esborra el
// que hagi quedat si algun test ha fallat. No s'usa el mode serial: si un test falla,
// serial saltaria la neteja final.
//
// Limitacions conegudes del backend (no les pot evitar el test):
//   - Esborrar un grup no esborra el seu objecte ACL (IPA_ACL_OBJECT_IDENTITY); per
//     això els permisos s'esborren abans que el grup, però en queda l'objecte buit.
//   - Spring ACL no esborra mai les files d'IPA_ACL_SID: es reutilitzen principals
//     existents per no afegir-ne de noves.
// ─────────────────────────────────────────────────────────────────────────────

const URL_GRUPS = '/ripeaback/reactapp/grup';

// Prefix propi de REACT (el JSP usa zz_PW_JSP_GRUP_): els dos projectes poden executar-se
// en paral·lel. Cap codi és prefix d'un altre (A, B, M), així un filtre per codi en troba
// exactament un. El prefix zz deixa els grups al final de l'ordenació per codi, i el test
// de procediments (que vincula el primer grup disponible) no els agafa.
const PREFIX_GRUP = 'zz_PW_REACT_GRUP_';
const CODI_A      = PREFIX_GRUP + 'A';
const DESC_A      = 'zz grup playwright react A';
const CODI_B      = PREFIX_GRUP + 'B';
const DESC_B      = 'zz grup playwright react B';
// Valors amb què es modifica el grup A
const CODI_M      = PREFIX_GRUP + 'M';
const DESC_M      = 'zz grup playwright react modificat';

// Principals dels permisos de prova (ja existeixen a IPA_ACL_SID: el rol el crea el test
// de procediments i l'usuari és el de credentials.json).
const SID_ROL          = 'ROL_PW_REACT_TEST';
const SID_USUARI       = codiUsuariCredencials('user');
const PRINCIPAL_ROL    = /^Rol$/i;
const PRINCIPAL_USUARI = /^Usuari(o)?$/i;

const SELECTOR_ORGAN = '[name="organGestor"] input[type="text"]';

// ── Helpers de la pàgina ──────────────────────────────────────────────────────

const esGetLlistatGrups = (url: string) => new URL(url).pathname.endsWith('/grups');
const esGetPermisos     = (url: string) => new URL(url).pathname.endsWith('/aclSids');

const getFiltre = (page: Page) => page.locator('.styledFilter');
const getDialog = (page: Page) => page.locator('[role="dialog"]');
const getFilaGrup = (page: Page, codi: string) =>
    getRows(page).filter({ has: page.locator('[data-field="codi"]', { hasText: codi }) });
const getBadgePermisos = (fila: Locator) => fila.locator('[data-field="permis"] .MuiBadge-badge');
const getFilaPermis = (page: Page, sid: string) => getRows(page).filter({ hasText: sid });

// El text de l'òrgan és "CODI - Nom"; el quickFilter del desplegable cerca pel codi.
const codiOrgan = (organ: string) => organ.split(' - ')[0];

// Navega al llistat i neteja el filtre desat a la sessió.
// Si el llistat queda buit, la causa habitual és que cap procediment de l'entitat té
// activada la gestió amb grups: el backend REACT (GrupResourceServiceImpl.
// additionalSpringFilter) només mostra grups a IPA_ADMIN en aquest cas.
const anarALlistat = async (page: Page) => {
    logDebug('[Llistat] Navegant a ' + URL_GRUPS + '...');
    // El filtre es desa al sessionStorage (clau amb el nom del recurs) i es restaura en
    // tornar a la pàgina. Si es restaura, Netejar buida la graella però l'estat del filtre
    // pot quedar amb el valor antic (debounce), i tornar-lo a aplicar no fa cap petició.
    // S'esborra abans de navegar perquè la pàgina arrenqui sense filtre.
    if (page.url().startsWith('http')) {
        await page.evaluate(() => Object.keys(sessionStorage)
            .filter(k => k.includes('grupResource'))
            .forEach(k => sessionStorage.removeItem(k)));
    }
    await page.goto(URL_GRUPS);
    await esperarGridCarregat(page);
    try {
        await netejarFiltre(page);
    } catch (e) {
        throw new Error(
            'El llistat de grups no mostra cap fila després de Netejar. Comproveu que algun ' +
            'procediment de l\'entitat tingui activada la gestió amb grups (a REACT, IPA_ADMIN ' +
            'només veu grups en aquest cas). Error original: ' + (e as Error).message);
    }
};

// Aplica el filtre indicat (els camps buits no es toquen) i espera la resposta de l'API.
// Si el filtre resultant és el mateix que el de la graella (p.ex. restaurat de la sessió i
// Netejar encara no propagat per debounce), no hi ha petició nova: el resultat ja és el
// del filtre, i amb codi es comprova igualment que totes les files hi corresponguin.
type FiltreGrups = { codi?: string; descripcio?: string; organ?: string };
const aplicarFiltreGrups = async (page: Page, filtre: FiltreGrups) => {
    logDebug('[Filtre] Aplicant ' + JSON.stringify(filtre));
    const resp = page.waitForResponse(
        r => esGetLlistatGrups(r.url()) && r.request().method() === 'GET' && r.status() === 200,
        { timeout: 5_000 }).catch(() => null);
    const form = getFiltre(page);
    if (filtre.codi) await form.locator('input[name="codi"]').fill(filtre.codi);
    if (filtre.descripcio) await form.locator('input[name="descripcio"]').fill(filtre.descripcio);
    if (filtre.organ) {
        await seleccionarOpcioAutocompletamentPerText(page, form, SELECTOR_ORGAN, codiOrgan(filtre.organ), filtre.organ);
    }
    // Els camps de text propaguen el valor amb debounce: sense el retard, el clic pot
    // arribar abans i el filtre s'aplica sense el text (cap petició nova).
    await guardaAmbDelay(page, page.getByRole('button', { name: 'Filtra', exact: true }));
    if (!await resp) {
        logDebug('[Filtre] Sense petició nova: la graella ja tenia aquest filtre');
    }
    await esperarGridCarregat(page);
    if (filtre.codi) {
        // La resposta pot ser la de la recàrrega anterior (Netejar): s'espera que totes
        // les files visibles corresponguin al filtre per codi abans de continuar.
        await expect(async () => {
            const total = await getRows(page).count();
            const filtrades = await getRows(page).filter({ hasText: filtre.codi! }).count();
            expect(filtrades).toBe(total);
        }).toPass({ timeout: 15_000 });
    }
    logDebug('[Filtre] Aplicat. Num files: ' + await getRows(page).count());
};

// Crea un grup des del botó "Nou Grup". Retorna l'òrgan triat ('' si no se'n tria).
const crearGrup = async (page: Page, codi: string, descripcio: string, ambOrgan: boolean): Promise<string> => {
    await page.getByRole('button').filter({ hasText: /nou grup|nuevo grupo/i }).click();
    const dialog = getDialog(page);
    await expect(dialog).toBeVisible();
    await dialog.locator('input[name="codi"]').fill(codi);
    await dialog.locator('textarea[name="descripcio"]').fill(descripcio);
    const organ = ambOrgan ? await seleccionarOpcioAutocompletament(page, dialog, SELECTOR_ORGAN) : '';
    await guardaAmbDelay(page, dialog.getByRole('button').filter({ hasText: /guarda/i }));
    await expectSuccessAlert(page);
    await expect(dialog).toBeHidden();
    return organ;
};

// Filtra pel codi i obre el formulari de modificació, esperant que Form.tsx hagi
// carregat les dades del grup (apiGetOne + reset).
const obrirModificacio = async (page: Page, codi: string): Promise<Locator> => {
    await aplicarFiltreGrups(page, { codi });
    const fila = getFilaGrup(page, codi);
    await expect(fila).toHaveCount(1);
    const entityResp = waitApiEntityLoad(page, await fila.getAttribute('data-id'));
    await obrirMenuAccions(fila);
    await page.getByRole('menuitem').filter({ hasText: /modifica/i }).click();
    const dialog = getDialog(page);
    await expect(dialog).toBeVisible();
    await entityResp;
    await expect(dialog.locator('input[name="codi"]')).toHaveValue(codi);
    return dialog;
};

const verificarFormulari = async (dialog: Locator, codi: string, descripcio: string, organ: string) => {
    await expect(dialog.locator('input[name="codi"]')).toHaveValue(codi);
    await expect(dialog.locator('textarea[name="descripcio"]')).toHaveValue(descripcio);
    await expect(dialog.locator(SELECTOR_ORGAN)).toHaveValue(organ);
};

const tancarDialeg = async (dialog: Locator) => {
    await dialog.getByRole('button').filter({ hasText: /cancel/i }).click();
    await expect(dialog).toBeHidden();
};

// Esborra la fila del grup pel menú d'accions (camí individual).
const esborrarGrupFila = async (page: Page, fila: Locator) => {
    await obrirMenuAccions(fila);
    await page.getByRole('menuitem').filter({ hasText: /esborra|elimina|borra/i }).click();
    await confirmarDialeg(page);
    await expectSuccessAlert(page);
};

// Obre la pantalla de permisos del grup des de la icona de la fila.
const anarAPermisosGrup = async (page: Page, codi: string) => {
    await aplicarFiltreGrups(page, { codi });
    const fila = getFilaGrup(page, codi);
    await expect(fila).toHaveCount(1);
    const resp = waitApiGet(page, esGetPermisos);
    await fila.locator('a[to*="/grupPermis/"]').click();
    await expect(page).toHaveURL(/\/grupPermis\/\d+\/permis/);
    await resp;
};

const crearPermis = async (page: Page, principal: RegExp, sid: string) => {
    const dialog = await obrirDialegAmbReintent(page,
        page.getByRole('button').filter({ hasText: /nou perm|nuevo perm/i }));
    await triaMuiSelect(page, dialog, 'principal', principal);
    await dialog.locator('input[name="sid"]').fill(sid);
    await guardaAmbDelay(page, dialog.getByRole('button').filter({ hasText: /guarda/i }));
    await expectSuccessAlert(page);
    await expect(dialog).toBeHidden();
};

// L'acció d'esborrar d'un permís és un botó de la fila (no està dins el menú).
const esborrarPermisFila = async (page: Page, fila: Locator) => {
    await fila.getByRole('menuitem', { name: /esborra|elimina|borra/i }).click();
    await confirmarDialeg(page);
    await expectSuccessAlert(page);
};

// Esborra tots els grups amb el prefix de prova, i abans els seus permisos (l'esborrat
// d'un grup no els elimina). Només actua sobre files amb el prefix, mai sobre altres grups.
const esborrarGrupsDeProva = async (page: Page) => {
    for (let intent = 0; intent < 10; intent++) {
        await anarALlistat(page);
        await aplicarFiltreGrups(page, { codi: PREFIX_GRUP });
        const files = getRows(page).filter({ hasText: PREFIX_GRUP });
        if (await files.count() === 0) {
            logInfo('  -> no queda cap grup de prova');
            return;
        }
        const fila = files.first();
        const codi = (await fila.locator('[data-field="codi"]').textContent() ?? '').trim();
        const numPermisos = Number((await getBadgePermisos(fila).textContent() ?? '0').trim());
        if (numPermisos > 0) {
            logInfo(`  -> esborrant ${numPermisos} permisos del grup ${codi}`);
            await anarAPermisosGrup(page, codi);
            while (await getRows(page).count() > 0) {
                const n = await getRows(page).count();
                await esborrarPermisFila(page, getRows(page).first());
                await expect(getRows(page)).toHaveCount(n - 1);
            }
            continue; // torna al llistat i comprova de nou el grup
        }
        logInfo(`  -> esborrant el grup ${codi}`);
        await esborrarGrupFila(page, fila);
        await expect(getFilaGrup(page, codi)).toHaveCount(0);
    }
    throw new Error('No s\'han pogut esborrar els grups de prova després de 10 intents');
};

// ─────────────────────────────────────────────────────────────────────────────
// Prerequisit: assegurar el rol actiu (bloc propi SENSE beforeEach)
// ─────────────────────────────────────────────────────────────────────────────

test.describe('Prerequisits — rol IPA_ADMIN', () => {

    test('ROL ASSEGURAR IPA_ADMIN', async ({ page }) => {
        await page.goto(URL_GRUPS);
        await assegurarRol(page, 'IPA_ADMIN', URL_GRUPS);
    });

});

// ─────────────────────────────────────────────────────────────────────────────
// Pàgina: Grups REACT  (rol IPA_ADMIN)
// ─────────────────────────────────────────────────────────────────────────────

test.describe('Gestió de Grups — IPA_ADMIN', () => {

    test.beforeEach(async ({ page }) => {
        await anarALlistat(page);
    });

    // ── Disposició ────────────────────────────────────────────────────────────

    test('GRUP VISTA', async ({ page }) => {

        await test.step('graella visible amb dades', async () => {
            logInfo('  -> graella visible amb dades');
            await expect(getGrid(page)).toBeVisible();
            await expect(getRows(page).first()).toBeVisible();
        });

        await test.step('columnes esperades visibles', async () => {
            logInfo('  -> columnes esperades visibles');
            const headers = page.locator('.MuiDataGrid-columnHeaderTitle');
            await expect(headers.filter({ hasText: /codi|c.digo/i }).first()).toBeVisible();
            await expect(headers.filter({ hasText: /descripci/i }).first()).toBeVisible();
            await expect(headers.filter({ hasText: /gestor/i }).first()).toBeVisible();
        });

        await test.step('botó de nou grup i selecció massiva visibles', async () => {
            logInfo('  -> botó de nou grup i selecció massiva visibles');
            await expect(page.getByRole('button').filter({ hasText: /nou grup|nuevo grupo/i })).toBeVisible();
            await expect(page.locator('.massive-selector')).toBeVisible();
        });

        await test.step('formulari de filtre visible', async () => {
            logInfo('  -> formulari de filtre visible');
            const form = getFiltre(page);
            await expect(form.locator('input[name="codi"]')).toBeVisible();
            await expect(form.locator('input[name="descripcio"]')).toBeVisible();
            await expect(form.locator(SELECTOR_ORGAN)).toBeVisible();
        });

    });

    // ── Neteja prèvia ─────────────────────────────────────────────────────────

    test('GRUP NETEJA PREVIA', async ({ page }) => {
        await esborrarGrupsDeProva(page);
    });

    // ── Creació ───────────────────────────────────────────────────────────────

    test('GRUP CREAR', async ({ page }) => {

        let organA = '';

        await test.step('crear el grup A amb tots els camps', async () => {
            logInfo('  -> crear el grup A amb tots els camps');
            organA = await crearGrup(page, CODI_A, DESC_A, true);
            expect(organA).not.toBe('');
        });

        await test.step('crear el grup B sense òrgan gestor', async () => {
            logInfo('  -> crear el grup B sense òrgan gestor');
            await crearGrup(page, CODI_B, DESC_B, false);
        });

        await test.step('els dos grups apareixen al llistat', async () => {
            logInfo('  -> els dos grups apareixen al llistat');
            await aplicarFiltreGrups(page, { codi: PREFIX_GRUP });
            await expect(getFilaGrup(page, CODI_A)).toHaveCount(1);
            await expect(getFilaGrup(page, CODI_B)).toHaveCount(1);
            await expect(getFilaGrup(page, CODI_A).locator('[data-field="organGestor"]')).toHaveText(organA);
        });

        await test.step('consultar el grup A i verificar les dades desades', async () => {
            logInfo('  -> consultar el grup A i verificar les dades desades');
            await netejarFiltre(page);
            const dialog = await obrirModificacio(page, CODI_A);
            await verificarFormulari(dialog, CODI_A, DESC_A, organA);
            await tancarDialeg(dialog);
        });

    });

    // ── Filtre ────────────────────────────────────────────────────────────────

    test('GRUP FILTRE', async ({ page }) => {

        let organA = '';

        await test.step('filtrar per codi', async () => {
            logInfo('  -> filtrar per codi');
            await aplicarFiltreGrups(page, { codi: CODI_A });
            await expect(getRows(page)).toHaveCount(1);
            await expect(getFilaGrup(page, CODI_A)).toHaveCount(1);
            await expect(page.getByText(/filtres aplicats|filtros aplicados/i)).toBeVisible();
            organA = (await getFilaGrup(page, CODI_A).locator('[data-field="organGestor"]').textContent() ?? '').trim();
            expect(organA, 'el grup A s\'ha de crear amb òrgan gestor (test GRUP CREAR)').not.toBe('');
        });

        await test.step('filtrar per part del codi', async () => {
            logInfo('  -> filtrar per part del codi');
            await netejarFiltre(page);
            await aplicarFiltreGrups(page, { codi: PREFIX_GRUP });
            await expect(getRows(page)).toHaveCount(2);
            await expect(getFilaGrup(page, CODI_A)).toHaveCount(1);
            await expect(getFilaGrup(page, CODI_B)).toHaveCount(1);
        });

        await test.step('filtrar per descripció', async () => {
            logInfo('  -> filtrar per descripció');
            await netejarFiltre(page);
            await aplicarFiltreGrups(page, { descripcio: DESC_A });
            await expect(getRows(page)).toHaveCount(1);
            await expect(getFilaGrup(page, CODI_A)).toHaveCount(1);
        });

        await test.step('filtrar per òrgan gestor', async () => {
            logInfo('  -> filtrar per òrgan gestor');
            await netejarFiltre(page);
            // Es combina amb el prefix perquè altres grups de l'entitat poden tenir el
            // mateix òrgan. B no en té: només ha de sortir A.
            await aplicarFiltreGrups(page, { codi: PREFIX_GRUP, organ: organA });
            await expect(getRows(page)).toHaveCount(1);
            await expect(getFilaGrup(page, CODI_A)).toHaveCount(1);
        });

        await test.step('filtrar sense resultats', async () => {
            logInfo('  -> filtrar sense resultats');
            await netejarFiltre(page);
            await aplicarFiltreGrups(page, { codi: PREFIX_GRUP + 'NO_EXISTEIX' });
            await expect(getRows(page)).toHaveCount(0);
        });

        await test.step('netejar el filtre', async () => {
            logInfo('  -> netejar el filtre');
            await netejarFiltre(page);
            const form = getFiltre(page);
            await expect(form.locator('input[name="codi"]')).toHaveValue('');
            await expect(form.locator('input[name="descripcio"]')).toHaveValue('');
            await expect(form.locator(SELECTOR_ORGAN)).toHaveValue('');
            await expect(page.getByText(/filtres aplicats|filtros aplicados/i)).toHaveCount(0);
            await expect(getRows(page).first()).toBeVisible();
        });

    });

    // ── Modificació ───────────────────────────────────────────────────────────

    test('GRUP MODIFICAR', async ({ page }) => {

        let organM = '';

        await test.step('modificar tots els camps del grup A', async () => {
            logInfo('  -> modificar tots els camps del grup A');
            const dialog = await obrirModificacio(page, CODI_A);
            const organAnterior = await dialog.locator(SELECTOR_ORGAN).inputValue();
            await dialog.locator('input[name="codi"]').fill(CODI_M);
            await dialog.locator('textarea[name="descripcio"]').fill(DESC_M);
            // Buidar el camp abans de triar: amb un valor seleccionat, la llista d'opcions
            // es podria filtrar pel text actual. En buidar-lo, MUI obre el desplegable.
            await dialog.locator(SELECTOR_ORGAN).fill('');
            await expect(dialog.locator(SELECTOR_ORGAN)).toHaveAttribute('aria-expanded', 'true');
            organM = await seleccionarOpcioAutocompletament(page, dialog, SELECTOR_ORGAN, 'second');
            expect(organM).not.toBe('');
            expect(organM).not.toBe(organAnterior);
            await guardaAmbDelay(page, dialog.getByRole('button').filter({ hasText: /guarda/i }));
            await expectSuccessAlert(page);
            await expect(dialog).toBeHidden();
        });

        await test.step('recarregar i verificar les dades desades', async () => {
            logInfo('  -> recarregar i verificar les dades desades');
            await anarALlistat(page);
            const dialog = await obrirModificacio(page, CODI_M);
            await verificarFormulari(dialog, CODI_M, DESC_M, organM);
            await tancarDialeg(dialog);
        });

        await test.step('el codi original ja no existeix', async () => {
            logInfo('  -> el codi original ja no existeix');
            await netejarFiltre(page);
            await aplicarFiltreGrups(page, { codi: CODI_A });
            await expect(getRows(page)).toHaveCount(0);
        });

    });

    // ── Permisos ──────────────────────────────────────────────────────────────

    test('GRUP PERMIS CREAR', async ({ page }) => {

        await test.step('obrir els permisos del grup', async () => {
            logInfo('  -> obrir els permisos del grup');
            await anarAPermisosGrup(page, CODI_M);
            await expect(getRows(page)).toHaveCount(0);
        });

        await test.step('crear un permís de rol', async () => {
            logInfo('  -> crear un permís de rol');
            await crearPermis(page, PRINCIPAL_ROL, SID_ROL);
            await expect(getFilaPermis(page, SID_ROL)).toHaveCount(1);
        });

        await test.step('crear un permís d\'usuari', async () => {
            logInfo('  -> crear un permís d\'usuari');
            await crearPermis(page, PRINCIPAL_USUARI, SID_USUARI);
            await expect(getFilaPermis(page, SID_USUARI)).toHaveCount(1);
            await expect(getRows(page)).toHaveCount(2);
        });

        await test.step('el llistat de grups mostra 2 permisos', async () => {
            logInfo('  -> el llistat de grups mostra 2 permisos');
            await anarALlistat(page);
            await aplicarFiltreGrups(page, { codi: CODI_M });
            await expect(getBadgePermisos(getFilaGrup(page, CODI_M))).toHaveText('2');
        });

    });

    test('GRUP PERMIS ELIMINAR', async ({ page }) => {

        await test.step('obrir els permisos del grup', async () => {
            logInfo('  -> obrir els permisos del grup');
            await anarAPermisosGrup(page, CODI_M);
            await expect(getRows(page)).toHaveCount(2);
        });

        await test.step('esborrar el permís de rol', async () => {
            logInfo('  -> esborrar el permís de rol');
            await esborrarPermisFila(page, getFilaPermis(page, SID_ROL));
            await expect(getFilaPermis(page, SID_ROL)).toHaveCount(0);
            await expect(getRows(page)).toHaveCount(1);
        });

        await test.step('esborrar el permís d\'usuari', async () => {
            logInfo('  -> esborrar el permís d\'usuari');
            await esborrarPermisFila(page, getFilaPermis(page, SID_USUARI));
            await expect(getRows(page)).toHaveCount(0);
        });

        await test.step('el llistat de grups mostra 0 permisos', async () => {
            logInfo('  -> el llistat de grups mostra 0 permisos');
            await anarALlistat(page);
            await aplicarFiltreGrups(page, { codi: CODI_M });
            await expect(getBadgePermisos(getFilaGrup(page, CODI_M))).toHaveText('0');
        });

    });

    // ── Esborrat individual ───────────────────────────────────────────────────

    test('GRUP ESBORRAR', async ({ page }) => {

        await test.step('esborrar el grup B des del menú de la fila', async () => {
            logInfo('  -> esborrar el grup B des del menú de la fila');
            await aplicarFiltreGrups(page, { codi: CODI_B });
            await expect(getFilaGrup(page, CODI_B)).toHaveCount(1);
            await esborrarGrupFila(page, getFilaGrup(page, CODI_B));
            await expect(getFilaGrup(page, CODI_B)).toHaveCount(0);
        });

        await test.step('recarregar la pàgina i verificar que no hi és', async () => {
            logInfo('  -> recarregar la pàgina i verificar que no hi és');
            await anarALlistat(page);
            await aplicarFiltreGrups(page, { codi: CODI_B });
            await expect(getRows(page)).toHaveCount(0);
        });

    });

    // ── Esborrat massiu ───────────────────────────────────────────────────────

    test('GRUP ESBORRAR MASSIU', async ({ page }) => {

        const selector = page.locator('.massive-selector');

        await test.step('seleccionar només el grup modificat', async () => {
            logInfo('  -> seleccionar només el grup modificat');
            await aplicarFiltreGrups(page, { codi: CODI_M });
            await expect(getRows(page)).toHaveCount(1);
            await page.getByRole('button', { name: /neteja la selecci|limpia la selecci/i }).click();
            await getFilaGrup(page, CODI_M).getByRole('checkbox').check();
            // Salvaguarda: l'esborrat massiu actua sobre tota la selecció.
            await expect(selector.locator('.MuiChip-label')).toHaveText('1');
        });

        await test.step('esborrar amb l\'acció massiva', async () => {
            logInfo('  -> esborrar amb l\'acció massiva');
            await selector.getByRole('button', { name: /esborra|elimina|borra/i }).click();
            // Si l'acció demana confirmació, s'accepta.
            const demanaConfirmacio = await getDialog(page)
                .waitFor({ state: 'visible', timeout: 3_000 }).then(() => true).catch(() => false);
            if (demanaConfirmacio) {
                await confirmarDialeg(page);
                // Els DELETE són asíncrons: s'espera el missatge de resultat abans de recarregar,
                // perquè la navegació no talli les peticions.
                await expectSuccessAlert(page);
            }
        });

        await test.step('recarregar la pàgina i verificar que no hi és', async () => {
            logInfo('  -> recarregar la pàgina i verificar que no hi és');
            await anarALlistat(page);
            await aplicarFiltreGrups(page, { codi: CODI_M });
            await expect(getRows(page),
                'L\'acció massiva "Esborra" no ha esborrat el grup seleccionat').toHaveCount(0);
        });

    });

    // ── Neteja final ──────────────────────────────────────────────────────────

    test('GRUP NETEJA FINAL', async ({ page }) => {

        await test.step('esborrar els grups de prova que quedin', async () => {
            logInfo('  -> esborrar els grups de prova que quedin');
            await esborrarGrupsDeProva(page);
        });

        await test.step('verificar que no queda cap grup de prova', async () => {
            logInfo('  -> verificar que no queda cap grup de prova');
            await anarALlistat(page);
            await aplicarFiltreGrups(page, { codi: PREFIX_GRUP });
            await expect(getRows(page)).toHaveCount(0);
        });

    });

});
