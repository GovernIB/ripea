import { test, expect, Page, Locator, FrameLocator } from '@playwright/test';

import {
    logDebug,
    logInfo,
    expectSuccessAlert,
    getFilesDatatable,
    waitDatatableResponse,
    esperarDatatable,
    esperarModalFormulari,
    enviarModal,
    cancelarModal,
    llegirSuggest,
    seleccionarSuggest,
    assegurarRolJsp,
} from '../utils/jspHelpers';
import { codiUsuariCredencials } from '../auth/login';

// ─────────────────────────────────────────────────────────────────────────────
// Manteniment de grups JSP (rol IPA_ADMIN). Equivalent REACT: grup.admin.spec.ts
//
// Mateixa seqüència que el test REACT: NETEJA PREVIA, CREAR (A i B), proves
// intermèdies, ESBORRAR (B, camí individual), ESBORRAR MASSIU (A, camí massiu) i
// NETEJA FINAL per si algun test ha fallat. No s'usa el mode serial: si un test falla,
// serial saltaria la neteja final.
//
// Limitacions conegudes del backend (no les pot evitar el test):
//   - Esborrar un grup no esborra el seu objecte ACL (IPA_ACL_OBJECT_IDENTITY); per
//     això els permisos s'esborren abans que el grup, però en queda l'objecte buit.
//   - Spring ACL no esborra mai les files d'IPA_ACL_SID: es reutilitzen principals
//     existents per no afegir-ne de noves.
// ─────────────────────────────────────────────────────────────────────────────

const URL_GRUPS = '/ripeaback/grup';

const TAULA_GRUPS    = 'grup';
const TAULA_PERMISOS = 'permisos';
const DT_GRUPS       = '/grup/datatable';
const DT_PERMISOS    = '/permis/datatable';

// Prefix propi de JSP (el REACT usa zz_PW_REACT_GRUP_). Veure grup.admin.spec.ts.
const PREFIX_GRUP = 'zz_PW_JSP_GRUP_';
const CODI_A      = PREFIX_GRUP + 'A';
const DESC_A      = 'zz grup playwright jsp A';
const CODI_B      = PREFIX_GRUP + 'B';
const DESC_B      = 'zz grup playwright jsp B';
// Valors amb què es modifica el grup A
const CODI_M      = PREFIX_GRUP + 'M';
const DESC_M      = 'zz grup playwright jsp modificat';

// Principals dels permisos de prova (ja existeixen a IPA_ACL_SID: el rol el crea el test
// de procediments JSP i l'usuari és el de credentials.json).
const SID_ROL    = 'ROL_PW_TEST';
const SID_USUARI = codiUsuariCredencials('user');

// Text per cercar òrgans gestors al camp de suggeriment (mínim 3 caràcters).
const CERCA_ORGAN = 'GOV';

// ── Helpers de la pàgina ──────────────────────────────────────────────────────

const getFiles = (page: Page) => getFilesDatatable(page, TAULA_GRUPS);
const getFilaGrup = (page: Page, codi: string) => getFiles(page).filter({ hasText: codi });
const getPermisos = (page: Page) => getFilesDatatable(page, TAULA_PERMISOS);
const getFilaPermis = (page: Page, sid: string) => getPermisos(page).filter({ hasText: sid });
const getBadgePermisos = (fila: Locator) => fila.locator('a[href*="grupPermis"] .badge');
// Columnes: selecció, codi, descripció, òrgan gestor, permisos, accions
const getCellaOrgan = (fila: Locator) => fila.locator('td').nth(3);

// El text de l'òrgan és "CODI - Nom"; el camp de suggeriment cerca pel codi.
const codiOrgan = (organ: string) => organ.split(' - ')[0];

// Navega al llistat i neteja el filtre desat a la sessió.
const anarALlistat = async (page: Page) => {
    logDebug('[Llistat] Navegant a ' + URL_GRUPS + '...');
    let dt = waitDatatableResponse(page, DT_GRUPS);
    await page.goto(URL_GRUPS);
    await dt;
    await esperarDatatable(page, TAULA_GRUPS);
    // El botó fa un POST que recarrega la pàgina sense cap filtre actiu.
    dt = waitDatatableResponse(page, DT_GRUPS);
    await page.locator('button[value="netejar"]').click();
    await dt;
    await esperarDatatable(page, TAULA_GRUPS);
};

// Aplica el filtre indicat. El formulari fa un POST que desa el filtre a la sessió i
// recarrega la pàgina. Hi ha un botó "filtrar" ocult (per a la tecla Enter) a més del visible.
type FiltreGrups = { codi?: string; descripcio?: string; organ?: string };
const filtrarGrups = async (page: Page, filtre: FiltreGrups) => {
    logDebug('[Filtre] Aplicant ' + JSON.stringify(filtre));
    const form = page.locator('#grupFiltreForm');
    if (filtre.codi) await form.locator('input[name="codi"]').fill(filtre.codi);
    if (filtre.descripcio) await form.locator('input[name="descripcio"]').fill(filtre.descripcio);
    if (filtre.organ) {
        await seleccionarSuggest(page, page, 'organGestorAscendentId', codiOrgan(filtre.organ), filtre.organ);
    }
    const dt = waitDatatableResponse(page, DT_GRUPS);
    await form.locator('button[value="filtrar"]:not(.hidden)').click();
    await dt;
    await esperarDatatable(page, TAULA_GRUPS);
    logDebug('[Filtre] Aplicat. Num files: ' + await getFiles(page).count());
};

// Crea un grup des de l'enllaç "Nou grup". Retorna l'òrgan triat ('' si no se'n tria).
const crearGrup = async (page: Page, codi: string, descripcio: string, ambOrgan: boolean): Promise<string> => {
    await page.locator('a[href="grup/new"]').click();
    const frame = await esperarModalFormulari(page, 'input[name="codi"]');
    await frame.locator('input[name="codi"]').fill(codi);
    await frame.locator('textarea[name="descripcio"]').fill(descripcio);
    const organ = ambOrgan ? await seleccionarSuggest(page, frame, 'organGestorId', CERCA_ORGAN, 0) : '';
    // L'enllaç té data-datatable-id: en desar, es refresca la taula.
    const dt = waitDatatableResponse(page, DT_GRUPS);
    await enviarModal(page);
    await dt;
    await expectSuccessAlert(page);
    return organ;
};

// Obre una acció del desplegable "Accions" de la fila.
const clicarAccioFila = async (fila: Locator, accio: RegExp) => {
    await fila.getByRole('button', { name: /accions|acciones/i }).click();
    await fila.getByRole('link', { name: accio }).click();
};

// Filtra pel codi i obre el formulari de modificació.
const obrirModificacio = async (page: Page, codi: string): Promise<FrameLocator> => {
    await filtrarGrups(page, { codi });
    const fila = getFilaGrup(page, codi);
    await expect(fila).toHaveCount(1);
    await clicarAccioFila(fila, /modificar/i);
    const frame = await esperarModalFormulari(page, 'input[name="codi"]');
    await expect(frame.locator('input[name="codi"]')).toHaveValue(codi);
    return frame;
};

const verificarFormulari = async (frame: FrameLocator, codi: string, descripcio: string, organ: string) => {
    await expect(frame.locator('input[name="codi"]')).toHaveValue(codi);
    await expect(frame.locator('textarea[name="descripcio"]')).toHaveValue(descripcio);
    // El valor inicial del suggeriment es carrega per AJAX: s'espera l'atribut.
    await expect(frame.locator('#select2-organGestorId-container')).toHaveAttribute('title', organ);
};

// Esborra la fila del grup amb l'acció "Esborrar" (camí individual). L'enllaç té
// data-confirm, que mostra un confirm() natiu del navegador.
const esborrarGrupFila = async (page: Page, fila: Locator) => {
    page.once('dialog', dialog => dialog.accept());
    const dt = waitDatatableResponse(page, DT_GRUPS);
    await clicarAccioFila(fila, /esborrar|eliminar|borrar/i);
    await dt;
    await esperarDatatable(page, TAULA_GRUPS);
};

// Obre la pantalla de permisos del grup amb el botó "Permisos" de la fila.
const anarAPermisosGrup = async (page: Page, codi: string) => {
    await filtrarGrups(page, { codi });
    const fila = getFilaGrup(page, codi);
    await expect(fila).toHaveCount(1);
    const dt = waitDatatableResponse(page, DT_PERMISOS);
    await fila.locator('a[href*="grupPermis"]').click();
    await dt;
    await esperarDatatable(page, TAULA_PERMISOS);
};

const crearPermis = async (page: Page, principalTipus: 'ROL' | 'USUARI', sid: string) => {
    await page.locator('a[href*="/permis/new"]').click();
    const frame = await esperarModalFormulari(page, 'input[name="principalNom"]');
    // El <select> queda ocult per select2; el formulari envia el seu valor.
    await frame.locator('select#principalTipus').selectOption(principalTipus, { force: true });
    await frame.locator('input[name="principalNom"]').fill(sid);
    const dt = waitDatatableResponse(page, DT_PERMISOS);
    await enviarModal(page);
    await dt;
    await esperarDatatable(page, TAULA_PERMISOS);
    // No s'espera l'avís d'èxit: amb el servidor carregat pot arribar després de refrescar
    // la taula i desaparèixer. La comprovació és que la fila del permís hi sigui (al test).
};

// Esborra un permís amb l'acció "Esborrar" de la fila (confirm() natiu).
const esborrarPermisFila = async (page: Page, fila: Locator) => {
    page.once('dialog', dialog => dialog.accept());
    const dt = waitDatatableResponse(page, DT_PERMISOS);
    await clicarAccioFila(fila, /esborrar|eliminar|borrar/i);
    await dt;
    await esperarDatatable(page, TAULA_PERMISOS);
};

// Esborra tots els grups amb el prefix de prova, i abans els seus permisos (l'esborrat
// d'un grup no els elimina). Només actua sobre files amb el prefix, mai sobre altres grups.
const esborrarGrupsDeProva = async (page: Page) => {
    for (let intent = 0; intent < 10; intent++) {
        await anarALlistat(page);
        await filtrarGrups(page, { codi: PREFIX_GRUP });
        const files = getFiles(page).filter({ hasText: PREFIX_GRUP });
        if (await files.count() === 0) {
            logInfo('  -> no queda cap grup de prova');
            return;
        }
        const fila = files.first();
        const codi = (await fila.locator('td').nth(1).textContent() ?? '').trim();
        const numPermisos = Number((await getBadgePermisos(fila).textContent() ?? '0').trim());
        if (numPermisos > 0) {
            logInfo(`  -> esborrant ${numPermisos} permisos del grup ${codi}`);
            await anarAPermisosGrup(page, codi);
            while (await getPermisos(page).count() > 0) {
                const n = await getPermisos(page).count();
                await esborrarPermisFila(page, getPermisos(page).first());
                await expect(getPermisos(page)).toHaveCount(n - 1);
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

test.describe('Prerequisits JSP — rol IPA_ADMIN', () => {

    test('ROL ASSEGURAR IPA_ADMIN', async ({ page }) => {
        await page.goto(URL_GRUPS);
        await assegurarRolJsp(page, 'IPA_ADMIN', URL_GRUPS);
    });

});

// ─────────────────────────────────────────────────────────────────────────────
// Pàgina: Grups JSP  (rol IPA_ADMIN)
// ─────────────────────────────────────────────────────────────────────────────

test.describe('Gestió de Grups JSP — IPA_ADMIN', () => {

    test.beforeEach(async ({ page }) => {
        await anarALlistat(page);
    });

    // ── Disposició ────────────────────────────────────────────────────────────

    test('GRUP VISTA', async ({ page }) => {

        await test.step('taula visible amb dades', async () => {
            logInfo('  -> taula visible amb dades');
            await expect(page.locator('#' + TAULA_GRUPS)).toBeVisible();
            await expect(getFiles(page).first()).toBeVisible();
        });

        await test.step('columnes esperades visibles', async () => {
            logInfo('  -> columnes esperades visibles');
            const headers = page.locator(`#${TAULA_GRUPS} thead th`);
            await expect(headers.filter({ hasText: /codi|c.digo/i }).first()).toBeVisible();
            await expect(headers.filter({ hasText: /descripci/i }).first()).toBeVisible();
            await expect(headers.filter({ hasText: /gestor/i }).first()).toBeVisible();
        });

        await test.step('botó de nou grup i selecció massiva visibles', async () => {
            logInfo('  -> botó de nou grup i selecció massiva visibles');
            await expect(page.locator('a[href="grup/new"]')).toBeVisible();
            await expect(page.locator('#seleccioNone')).toBeVisible();
            await expect(page.locator('a[href="./grup/esborrar"]')).toBeVisible();
        });

        await test.step('formulari de filtre visible', async () => {
            logInfo('  -> formulari de filtre visible');
            const form = page.locator('#grupFiltreForm');
            await expect(form.locator('input[name="codi"]')).toBeVisible();
            await expect(form.locator('input[name="descripcio"]')).toBeVisible();
            await expect(form.locator('#select2-organGestorAscendentId-container')).toBeVisible();
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
            await filtrarGrups(page, { codi: PREFIX_GRUP });
            await expect(getFilaGrup(page, CODI_A)).toHaveCount(1);
            await expect(getFilaGrup(page, CODI_B)).toHaveCount(1);
            await expect(getCellaOrgan(getFilaGrup(page, CODI_A))).toHaveText(organA);
        });

        await test.step('consultar el grup A i verificar les dades desades', async () => {
            logInfo('  -> consultar el grup A i verificar les dades desades');
            const frame = await obrirModificacio(page, CODI_A);
            await verificarFormulari(frame, CODI_A, DESC_A, organA);
            await cancelarModal(page);
        });

    });

    // ── Filtre ────────────────────────────────────────────────────────────────

    test('GRUP FILTRE', async ({ page }) => {

        let organA = '';

        await test.step('filtrar per codi', async () => {
            logInfo('  -> filtrar per codi');
            await filtrarGrups(page, { codi: CODI_A });
            await expect(getFiles(page)).toHaveCount(1);
            await expect(getFilaGrup(page, CODI_A)).toHaveCount(1);
            organA = (await getCellaOrgan(getFilaGrup(page, CODI_A)).textContent() ?? '').trim();
            expect(organA, 'el grup A s\'ha de crear amb òrgan gestor (test GRUP CREAR)').not.toBe('');
        });

        await test.step('filtrar per part del codi', async () => {
            logInfo('  -> filtrar per part del codi');
            await anarALlistat(page);
            await filtrarGrups(page, { codi: PREFIX_GRUP });
            await expect(getFiles(page)).toHaveCount(2);
            await expect(getFilaGrup(page, CODI_A)).toHaveCount(1);
            await expect(getFilaGrup(page, CODI_B)).toHaveCount(1);
        });

        await test.step('filtrar per descripció', async () => {
            logInfo('  -> filtrar per descripció');
            await anarALlistat(page);
            await filtrarGrups(page, { descripcio: DESC_A });
            await expect(getFiles(page)).toHaveCount(1);
            await expect(getFilaGrup(page, CODI_A)).toHaveCount(1);
        });

        await test.step('filtrar per òrgan gestor', async () => {
            logInfo('  -> filtrar per òrgan gestor');
            await anarALlistat(page);
            // Es combina amb el prefix perquè altres grups de l'entitat poden tenir el
            // mateix òrgan (o un de fill: el filtre JSP inclou els descendents). B no en té.
            await filtrarGrups(page, { codi: PREFIX_GRUP, organ: organA });
            await expect(getFiles(page)).toHaveCount(1);
            await expect(getFilaGrup(page, CODI_A)).toHaveCount(1);
        });

        await test.step('filtrar sense resultats', async () => {
            logInfo('  -> filtrar sense resultats');
            await anarALlistat(page);
            await filtrarGrups(page, { codi: PREFIX_GRUP + 'NO_EXISTEIX' });
            await expect(getFiles(page)).toHaveCount(0);
        });

        await test.step('netejar el filtre', async () => {
            logInfo('  -> netejar el filtre');
            const dt = waitDatatableResponse(page, DT_GRUPS);
            await page.locator('button[value="netejar"]').click();
            await dt;
            await esperarDatatable(page, TAULA_GRUPS);
            const form = page.locator('#grupFiltreForm');
            await expect(form.locator('input[name="codi"]')).toHaveValue('');
            await expect(form.locator('input[name="descripcio"]')).toHaveValue('');
            expect(await llegirSuggest(page, 'organGestorAscendentId')).toBe('');
            await expect(getFiles(page).first()).toBeVisible();
        });

    });

    // ── Modificació ───────────────────────────────────────────────────────────

    test('GRUP MODIFICAR', async ({ page }) => {

        let organM = '';

        await test.step('modificar tots els camps del grup A', async () => {
            logInfo('  -> modificar tots els camps del grup A');
            const frame = await obrirModificacio(page, CODI_A);
            const organAnterior = await llegirSuggest(frame, 'organGestorId');
            await frame.locator('input[name="codi"]').fill(CODI_M);
            await frame.locator('textarea[name="descripcio"]').fill(DESC_M);
            // A es crea amb la primera opció de la cerca; aquí es tria la segona.
            organM = await seleccionarSuggest(page, frame, 'organGestorId', CERCA_ORGAN, 1);
            expect(organM).not.toBe('');
            expect(organM).not.toBe(organAnterior);
            await enviarModal(page);
            await expectSuccessAlert(page);
        });

        await test.step('recarregar i verificar les dades desades', async () => {
            logInfo('  -> recarregar i verificar les dades desades');
            await anarALlistat(page);
            const frame = await obrirModificacio(page, CODI_M);
            await verificarFormulari(frame, CODI_M, DESC_M, organM);
            await cancelarModal(page);
        });

        await test.step('el codi original ja no existeix', async () => {
            logInfo('  -> el codi original ja no existeix');
            await anarALlistat(page);
            await filtrarGrups(page, { codi: CODI_A });
            await expect(getFiles(page)).toHaveCount(0);
        });

    });

    // ── Permisos ──────────────────────────────────────────────────────────────

    test('GRUP PERMIS CREAR', async ({ page }) => {

        await test.step('obrir els permisos del grup', async () => {
            logInfo('  -> obrir els permisos del grup');
            await anarAPermisosGrup(page, CODI_M);
            await expect(getPermisos(page)).toHaveCount(0);
        });

        await test.step('crear un permís de rol', async () => {
            logInfo('  -> crear un permís de rol');
            await crearPermis(page, 'ROL', SID_ROL);
            await expect(getFilaPermis(page, SID_ROL)).toHaveCount(1);
        });

        await test.step('crear un permís d\'usuari', async () => {
            logInfo('  -> crear un permís d\'usuari');
            await crearPermis(page, 'USUARI', SID_USUARI);
            await expect(getFilaPermis(page, SID_USUARI)).toHaveCount(1);
            await expect(getPermisos(page)).toHaveCount(2);
        });

        await test.step('el llistat de grups mostra 2 permisos', async () => {
            logInfo('  -> el llistat de grups mostra 2 permisos');
            await anarALlistat(page);
            await filtrarGrups(page, { codi: CODI_M });
            await expect(getBadgePermisos(getFilaGrup(page, CODI_M))).toHaveText('2');
        });

    });

    test('GRUP PERMIS ELIMINAR', async ({ page }) => {

        await test.step('obrir els permisos del grup', async () => {
            logInfo('  -> obrir els permisos del grup');
            await anarAPermisosGrup(page, CODI_M);
            await expect(getPermisos(page)).toHaveCount(2);
        });

        await test.step('esborrar el permís de rol', async () => {
            logInfo('  -> esborrar el permís de rol');
            await esborrarPermisFila(page, getFilaPermis(page, SID_ROL));
            await expect(getFilaPermis(page, SID_ROL)).toHaveCount(0);
            await expect(getPermisos(page)).toHaveCount(1);
        });

        await test.step('esborrar el permís d\'usuari', async () => {
            logInfo('  -> esborrar el permís d\'usuari');
            await esborrarPermisFila(page, getFilaPermis(page, SID_USUARI));
            await expect(getPermisos(page)).toHaveCount(0);
        });

        await test.step('el llistat de grups mostra 0 permisos', async () => {
            logInfo('  -> el llistat de grups mostra 0 permisos');
            await anarALlistat(page);
            await filtrarGrups(page, { codi: CODI_M });
            await expect(getBadgePermisos(getFilaGrup(page, CODI_M))).toHaveText('0');
        });

    });

    // ── Esborrat individual ───────────────────────────────────────────────────

    test('GRUP ESBORRAR', async ({ page }) => {

        await test.step('esborrar el grup B des del menú de la fila', async () => {
            logInfo('  -> esborrar el grup B des del menú de la fila');
            await filtrarGrups(page, { codi: CODI_B });
            await expect(getFilaGrup(page, CODI_B)).toHaveCount(1);
            await esborrarGrupFila(page, getFilaGrup(page, CODI_B));
            await expect(getFilaGrup(page, CODI_B)).toHaveCount(0);
        });

        await test.step('recarregar la pàgina i verificar que no hi és', async () => {
            logInfo('  -> recarregar la pàgina i verificar que no hi és');
            // El filtre per codi queda desat a la sessió: després de recarregar s'aplica igual.
            const dt = waitDatatableResponse(page, DT_GRUPS);
            await page.reload();
            await dt;
            await esperarDatatable(page, TAULA_GRUPS);
            await expect(page.locator('#grupFiltreForm input[name="codi"]')).toHaveValue(CODI_B);
            await expect(getFiles(page)).toHaveCount(0);
        });

    });

    // ── Esborrat massiu ───────────────────────────────────────────────────────

    test('GRUP ESBORRAR MASSIU', async ({ page }) => {

        const comptador = page.locator('#seleccioCount');

        await test.step('seleccionar només el grup modificat', async () => {
            logInfo('  -> seleccionar només el grup modificat');
            await filtrarGrups(page, { codi: CODI_M });
            await expect(getFiles(page)).toHaveCount(1);
            // La selecció es desa a la sessió i l'esborrat massiu actua sobre TOTA la
            // selecció: primer es buida, i després es marca només la fila filtrada.
            const deselect = page.waitForResponse(r => r.url().includes('/grup/deselect') && r.status() === 200);
            await page.locator('#seleccioNone').click();
            await deselect;
            await expect(comptador).toHaveText('0');
            const select = page.waitForResponse(r => r.url().includes('/grup/select') && r.status() === 200);
            await getFilaGrup(page, CODI_M).locator('td').first().click();
            await select;
            // Salvaguarda: si la selecció no és exactament 1, no s'esborra res.
            await expect(comptador).toHaveText('1');
        });

        await test.step('esborrar amb l\'acció massiva', async () => {
            logInfo('  -> esborrar amb l\'acció massiva');
            const dt = waitDatatableResponse(page, DT_GRUPS);
            await page.locator('a[href="./grup/esborrar"]').click();
            await dt;
            await esperarDatatable(page, TAULA_GRUPS);
            await expectSuccessAlert(page);
        });

        await test.step('recarregar la pàgina i verificar que no hi és', async () => {
            logInfo('  -> recarregar la pàgina i verificar que no hi és');
            await anarALlistat(page);
            await filtrarGrups(page, { codi: CODI_M });
            await expect(getFiles(page)).toHaveCount(0);
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
            await filtrarGrups(page, { codi: PREFIX_GRUP });
            await expect(getFiles(page)).toHaveCount(0);
        });

    });

});
