import { test, expect, Page, Locator } from '@playwright/test';
import path from 'path';

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
    checkBoxOcultClick,
    obrirDialegAmbReintent,
    aplicarFiltreRapid,
    waitApiGet,
    waitApiEntityLoad,
    assegurarRol,
} from '../utils/reactHelpers';
import {
    CIF_CREAR, CIF_MODIFICAR, UNITAT_CREAR, UNITAT_MODIFICAR,
    COLORS_CREAR, COLORS_MODIFICAR, IMATGE_A, IMATGE_B, SID_ROL_REACT,
    llegirEntitatApi, expectImatge, expectColors,
} from './entitatComu';

// ─────────────────────────────────────────────────────────────────────────────
// Manteniment d'entitats REACT (superusuari, usuari propi: veure auth/super.setup.ts).
// Equivalent JSP: entitat.super.jsp.spec.ts
//
// Els tests s'executen en l'ordre del fitxer i deixen la BD com l'han trobada:
// NETEJA PREVIA esborra l'entitat de prova d'una execució interrompuda, CREAR la crea,
// les proves intermèdies hi treballen i ESBORRAR l'elimina (amb un permís assignat, per
// comprovar que l'esborrat també elimina els permisos). NETEJA FINAL l'esborra si algun
// test ha fallat. No s'usa el mode serial: si un test falla, saltaria la neteja final.
//
// El filtre ràpid (codi, nom i CIF) es prova en localitzar l'entitat: cada test hi
// cerca per un camp diferent.
// ─────────────────────────────────────────────────────────────────────────────

const URL_ENTITATS = '/ripeaback/reactapp/entitat';

// Codi propi de REACT (el JSP usa ZZPWJSP): els dos projectes poden executar-se en paral·lel.
const CODI          = 'ZZPWREACT';
const NOM_CREAR     = 'zz entitat playwright react';
const NOM_MODIFICAR = 'zz entitat playwright react modificada';
// Part del nom, per provar el filtre ràpid amb coincidència parcial.
const CERCA_NOM     = 'playwright react';

// Camps d'imatge del formulari REACT (el JSP també té els logos de menú, que aquí no hi són).
const CAMPS_IMATGE = ['logoImgFile', 'faviconImgFile', 'blackLogoImgFile', 'blackFaviconImgFile'] as const;
type CampImatge = typeof CAMPS_IMATGE[number];
// Camp de l'API amb el contingut de cada imatge
const BYTES: Record<CampImatge, string> = {
    logoImgFile: 'logoImgBytes',
    faviconImgFile: 'faviconImgBytes',
    blackLogoImgFile: 'blackLogoImgBytes',
    blackFaviconImgFile: 'blackFaviconImgBytes',
};

// ── Helpers de la pàgina ──────────────────────────────────────────────────────

const esGetLlistat = (url: string) => new URL(url).pathname.endsWith('/entitats');
const esGetPermisos = (url: string) => new URL(url).pathname.endsWith('/aclSids');

const getDialog = (page: Page) => page.locator('[role="dialog"]');
const getFiltreRapid = (page: Page) => page.getByRole('textbox', { name: /filtre r.pid|filtro r.pido/i });
const getFilaEntitat = (page: Page, codi: string) =>
    getRows(page).filter({ has: page.locator('[data-field="codi"]', { hasText: codi }) });

// Navega al llistat. Si el filtre ràpid s'ha restaurat amb un text, el buida: aplicar
// després el mateix text no faria cap petició nova.
const anarALlistat = async (page: Page) => {
    logDebug('[Llistat] Navegant a ' + URL_ENTITATS + '...');
    await page.goto(URL_ENTITATS);
    await esperarGridCarregat(page);
    if (await getFiltreRapid(page).inputValue() !== '') {
        await aplicarFiltreRapid(page, '', esGetLlistat);
    }
};

// <input type="file"> ocult que precedeix el camp de text de cada imatge.
const getInputFitxer = (dialog: Locator, camp: CampImatge) =>
    dialog.locator(`xpath=.//input[@name="${camp}"]/ancestor::div[input[@type="file"]][1]/input[@type="file"]`);

// Les imatges tenen onChange al backend (copia el contingut als bytes), que el formulari
// envia com a PATCH /api/entitats: s'espera la resposta abans de continuar.
const esperarOnChange = (page: Page) => page.waitForResponse(
    r => r.request().method() === 'PATCH' && new URL(r.url()).pathname.endsWith('/api/entitats'),
    { timeout: 10_000 }).catch(() => null);

const adjuntarImatge = async (page: Page, dialog: Locator, camp: CampImatge, fitxer: string) => {
    const onChange = esperarOnChange(page);
    await getInputFitxer(dialog, camp).setInputFiles(fitxer);
    await expect(dialog.locator(`input[name="${camp}"]`)).toHaveValue(path.basename(fitxer));
    await onChange;
};

// Treu la imatge amb el botó d'esborrar del camp (només apareix si n'hi ha).
const esborrarImatge = async (page: Page, dialog: Locator, camp: CampImatge) => {
    const onChange = esperarOnChange(page);
    await dialog.locator(`xpath=.//input[@name="${camp}"]/following-sibling::button`).click();
    await expect(dialog.locator(`input[name="${camp}"]`)).toHaveValue('');
    await onChange;
};

const omplirColors = async (dialog: Locator, colors: Record<string, string>) => {
    for (const [camp, valor] of Object.entries(colors)) {
        await dialog.locator(`input[name="${camp}"]`).fill(valor);
    }
};

type DadesEntitat = {
    nom: string; cif: string; unitat: string; postal: boolean;
    colors: Record<string, string>; imatges: Record<CampImatge, string | null>;
};

const DADES_CREAR: DadesEntitat = {
    nom: NOM_CREAR, cif: CIF_CREAR, unitat: UNITAT_CREAR, postal: false, colors: COLORS_CREAR,
    imatges: { logoImgFile: IMATGE_A, faviconImgFile: IMATGE_A, blackLogoImgFile: IMATGE_A, blackFaviconImgFile: IMATGE_A },
};
// Es canvien tots els camps (menys el codi, que no es pot modificar) i es treu el favicon.
const DADES_MODIFICAR: DadesEntitat = {
    nom: NOM_MODIFICAR, cif: CIF_MODIFICAR, unitat: UNITAT_MODIFICAR, postal: true, colors: COLORS_MODIFICAR,
    imatges: { logoImgFile: IMATGE_B, faviconImgFile: null, blackLogoImgFile: IMATGE_B, blackFaviconImgFile: IMATGE_B },
};

// Obre el formulari de modificació de la fila (ja visible al llistat) i espera que
// Form.tsx hagi carregat les dades. Retorna el diàleg i l'id de l'entitat.
const obrirModificacio = async (page: Page, codi: string): Promise<{ dialog: Locator; id: string }> => {
    const fila = getFilaEntitat(page, codi);
    await expect(fila).toHaveCount(1);
    const id = (await fila.getAttribute('data-id'))!;
    const entityResp = waitApiEntityLoad(page, id);
    await obrirMenuAccions(fila);
    await page.getByRole('menuitem').filter({ hasText: /modifica/i }).click();
    const dialog = getDialog(page);
    await expect(dialog).toBeVisible();
    await entityResp;
    await expect(dialog.locator('input[name="codi"]')).toHaveValue(codi);
    return { dialog, id };
};

const tancarDialeg = async (dialog: Locator) => {
    await dialog.getByRole('button').filter({ hasText: /cancel/i }).click();
    await expect(dialog).toBeHidden();
};

// Comprova el formulari (el que veu l'usuari) i l'API (el contingut real de les imatges).
// Amb expect.soft: es comproven tots els camps encara que algun falli (el test queda en error
// igualment), per saber en una sola execució tot el que no s'ha desat bé.
const verificarEntitat = async (page: Page, dialog: Locator, id: string, dades: DadesEntitat) => {
    await expect.soft(dialog.locator('input[name="codi"]')).toBeDisabled();
    await expect.soft(dialog.locator('input[name="nom"]')).toHaveValue(dades.nom);
    await expect.soft(dialog.locator('input[name="cif"]')).toHaveValue(dades.cif);
    await expect.soft(dialog.locator('input[name="unitatArrel"]')).toHaveValue(dades.unitat);
    const postal = dialog.locator('input[name="permetreEnviamentPostal"]');
    if (dades.postal) await expect.soft(postal).toBeChecked(); else await expect.soft(postal).not.toBeChecked();
    for (const [camp, valor] of Object.entries(dades.colors)) {
        await expect.soft(dialog.locator(`input[name="${camp}"]`), camp).toHaveValue(valor);
    }
    for (const camp of CAMPS_IMATGE) {
        const input = dialog.locator(`input[name="${camp}"]`);
        if (dades.imatges[camp]) await expect.soft(input, camp).not.toHaveValue(''); else await expect.soft(input, camp).toHaveValue('');
    }

    const entitat = await llegirEntitatApi(page, id);
    expect.soft(entitat.nom).toBe(dades.nom);
    expect.soft(entitat.cif).toBe(dades.cif);
    expect.soft(entitat.unitatArrel).toBe(dades.unitat);
    expect.soft(entitat.permetreEnviamentPostal, 'permetreEnviamentPostal').toBe(dades.postal);
    expectColors(entitat, dades.colors);
    for (const camp of CAMPS_IMATGE) {
        expectImatge(entitat, BYTES[camp], dades.imatges[camp]);
    }
};

// Esborra la fila de l'entitat pel menú d'accions.
const esborrarEntitatFila = async (page: Page, fila: Locator) => {
    await obrirMenuAccions(fila);
    await page.getByRole('menuitem').filter({ hasText: /esborra|elimina|borra/i }).click();
    await confirmarDialeg(page);
    await expectSuccessAlert(page);
};

// Esborra l'entitat de prova si existeix (busca pel codi). L'esborrat elimina també les
// seves configuracions, tipus documentals i permisos.
const esborrarEntitatDeProva = async (page: Page) => {
    await anarALlistat(page);
    await aplicarFiltreRapid(page, CODI, esGetLlistat);
    const fila = getFilaEntitat(page, CODI);
    if (await fila.count() === 0) {
        logInfo('  -> no hi ha cap entitat de prova');
        return;
    }
    logInfo('  -> esborrant l\'entitat de prova ' + CODI);
    await esborrarEntitatFila(page, fila);
    await expect(fila).toHaveCount(0);
};

// ─────────────────────────────────────────────────────────────────────────────
// Prerequisit: assegurar el rol actiu (bloc propi SENSE beforeEach)
// ─────────────────────────────────────────────────────────────────────────────

test.describe('Prerequisits — rol IPA_SUPER', () => {

    test('ROL ASSEGURAR IPA_SUPER', async ({ page }) => {
        await page.goto(URL_ENTITATS);
        await assegurarRol(page, 'IPA_SUPER', URL_ENTITATS);
    });

});

// ─────────────────────────────────────────────────────────────────────────────
// Pàgina: Entitats REACT  (rol IPA_SUPER)
// ─────────────────────────────────────────────────────────────────────────────

test.describe('Gestió d\'Entitats — IPA_SUPER', () => {

    test.beforeEach(async ({ page }) => {
        await anarALlistat(page);
    });

    // ── Disposició ────────────────────────────────────────────────────────────

    test('ENTITAT VISTA', async ({ page }) => {

        await test.step('graella visible amb dades', async () => {
            logInfo('  -> graella visible amb dades');
            await expect(getGrid(page)).toBeVisible();
            await expect(getRows(page).first()).toBeVisible();
        });

        await test.step('columnes esperades visibles', async () => {
            logInfo('  -> columnes esperades visibles');
            const headers = page.locator('.MuiDataGrid-columnHeaderTitle');
            await expect(headers.filter({ hasText: /codi|c.digo/i }).first()).toBeVisible();
            await expect(headers.filter({ hasText: /\bnom\b|nombre/i }).first()).toBeVisible();
            await expect(headers.filter({ hasText: /cif/i }).first()).toBeVisible();
            await expect(headers.filter({ hasText: /activa/i }).first()).toBeVisible();
        });

        await test.step('botó de nova entitat i filtre ràpid visibles', async () => {
            logInfo('  -> botó de nova entitat i filtre ràpid visibles');
            await expect(page.getByRole('button').filter({ hasText: /nova entitat|nueva entidad/i })).toBeVisible();
            await expect(getFiltreRapid(page)).toBeVisible();
        });

    });

    // ── Neteja prèvia ─────────────────────────────────────────────────────────

    test('ENTITAT NETEJA PREVIA', async ({ page }) => {
        await esborrarEntitatDeProva(page);
    });

    // ── Creació ───────────────────────────────────────────────────────────────

    test('ENTITAT CREAR', async ({ page }) => {

        await test.step('crear l\'entitat amb tots els camps', async () => {
            logInfo('  -> crear l\'entitat amb tots els camps');
            await page.getByRole('button').filter({ hasText: /nova entitat|nueva entidad/i }).click();
            const dialog = getDialog(page);
            await expect(dialog).toBeVisible();
            await dialog.locator('input[name="codi"]').fill(CODI);
            await dialog.locator('input[name="nom"]').fill(DADES_CREAR.nom);
            await dialog.locator('input[name="cif"]').fill(DADES_CREAR.cif);
            await dialog.locator('input[name="unitatArrel"]').fill(DADES_CREAR.unitat);
            // Es deixa desmarcat (valor contrari al per defecte de l'entitat a la BD), per
            // comprovar que es desa el que ha triat l'usuari.
            await expect(dialog.locator('input[name="permetreEnviamentPostal"]')).not.toBeChecked();
            for (const camp of CAMPS_IMATGE) {
                await adjuntarImatge(page, dialog, camp, DADES_CREAR.imatges[camp]!);
            }
            await omplirColors(dialog, DADES_CREAR.colors);
            await guardaAmbDelay(page, dialog.getByRole('button').filter({ hasText: /guarda/i }));
            await expectSuccessAlert(page);
            await expect(dialog).toBeHidden();
        });

        await test.step('cercar-la per part del nom: apareix i està activa', async () => {
            logInfo('  -> cercar-la per part del nom: apareix i està activa');
            await aplicarFiltreRapid(page, CERCA_NOM, esGetLlistat);
            const fila = getFilaEntitat(page, CODI);
            await expect(fila).toHaveCount(1);
            // soft: si falla, el test queda en error però continua verificant les dades desades.
            await expect.soft(fila.locator('[data-field="activa"]'),
                'una entitat nova ha de quedar activa (com quan es crea des del JSP)').toHaveText('check');
        });

        await test.step('consultar l\'entitat i verificar les dades desades', async () => {
            logInfo('  -> consultar l\'entitat i verificar les dades desades');
            const { dialog, id } = await obrirModificacio(page, CODI);
            await verificarEntitat(page, dialog, id, DADES_CREAR);
            await tancarDialeg(dialog);
        });

    });

    // ── Modificació ───────────────────────────────────────────────────────────

    test('ENTITAT MODIFICAR', async ({ page }) => {

        await test.step('cercar-la pel CIF i modificar tots els camps', async () => {
            logInfo('  -> cercar-la pel CIF i modificar tots els camps');
            await aplicarFiltreRapid(page, CIF_CREAR, esGetLlistat);
            const { dialog } = await obrirModificacio(page, CODI);
            await expect(dialog.locator('input[name="codi"]')).toBeDisabled();
            await dialog.locator('input[name="nom"]').fill(DADES_MODIFICAR.nom);
            await dialog.locator('input[name="cif"]').fill(DADES_MODIFICAR.cif);
            await dialog.locator('input[name="unitatArrel"]').fill(DADES_MODIFICAR.unitat);
            await checkBoxOcultClick(dialog, 'input[name="permetreEnviamentPostal"]');
            for (const camp of CAMPS_IMATGE) {
                const fitxer = DADES_MODIFICAR.imatges[camp];
                if (fitxer) await adjuntarImatge(page, dialog, camp, fitxer);
                else await esborrarImatge(page, dialog, camp);
            }
            await omplirColors(dialog, DADES_MODIFICAR.colors);
            await guardaAmbDelay(page, dialog.getByRole('button').filter({ hasText: /guarda/i }));
            await expectSuccessAlert(page);
            await expect(dialog).toBeHidden();
        });

        await test.step('recarregar: el CIF antic ja no la troba i el nou sí', async () => {
            logInfo('  -> recarregar: el CIF antic ja no la troba i el nou sí');
            await anarALlistat(page);
            await aplicarFiltreRapid(page, CIF_CREAR, esGetLlistat);
            await expect(getFilaEntitat(page, CODI)).toHaveCount(0);
            await aplicarFiltreRapid(page, CIF_MODIFICAR, esGetLlistat);
            await expect(getFilaEntitat(page, CODI)).toHaveCount(1);
        });

        await test.step('verificar les dades desades', async () => {
            logInfo('  -> verificar les dades desades');
            const { dialog, id } = await obrirModificacio(page, CODI);
            await verificarEntitat(page, dialog, id, DADES_MODIFICAR);
            await tancarDialeg(dialog);
        });

    });

    // ── Permisos ──────────────────────────────────────────────────────────────

    test('ENTITAT PERMIS', async ({ page }) => {

        await test.step('donar permís d\'usuari a un rol de prova', async () => {
            logInfo('  -> donar permís d\'usuari a un rol de prova');
            await aplicarFiltreRapid(page, CODI, esGetLlistat);
            const fila = getFilaEntitat(page, CODI);
            await expect(fila).toHaveCount(1);
            const resp = waitApiGet(page, esGetPermisos);
            await fila.locator('a[to*="/permis"]').click();
            await expect(page).toHaveURL(/\/entitat\/\d+\/permis/);
            await resp;
            await expect(getRows(page)).toHaveCount(0);

            const dialog = await obrirDialegAmbReintent(page,
                page.getByRole('button').filter({ hasText: /nou perm|nuevo perm/i }));
            await triaMuiSelect(page, dialog, 'principal', /^Rol$/i);
            await dialog.locator('input[name="sid"]').fill(SID_ROL_REACT);
            await checkBoxOcultClick(dialog, 'input[name="user"]');
            await guardaAmbDelay(page, dialog.getByRole('button').filter({ hasText: /guarda/i }));
            await expectSuccessAlert(page);
            await expect(getRows(page).filter({ hasText: SID_ROL_REACT })).toHaveCount(1);
        });

        await test.step('el llistat d\'entitats mostra 1 permís', async () => {
            logInfo('  -> el llistat d\'entitats mostra 1 permís');
            await anarALlistat(page);
            await aplicarFiltreRapid(page, CODI, esGetLlistat);
            await expect(getFilaEntitat(page, CODI).locator('[data-field="numPermisos"] .MuiBadge-badge')).toHaveText('1');
        });

    });

    // ── Activar / Desactivar ──────────────────────────────────────────────────

    test('ENTITAT ACTIVAR', async ({ page }) => {

        await aplicarFiltreRapid(page, CODI, esGetLlistat);
        const fila = getFilaEntitat(page, CODI);
        const activa = fila.locator('[data-field="activa"]');
        await expect(fila).toHaveCount(1);

        const desactivar = async () => {
            await obrirMenuAccions(fila);
            await page.getByRole('menuitem').filter({ hasText: /desactiva/i }).click();
            await expectSuccessAlert(page);
            await expect(activa).toHaveText('');
        };
        const activar = async () => {
            await obrirMenuAccions(fila);
            await page.getByRole('menuitem').filter({ hasText: /activa/i }).filter({ hasNotText: /desactiva/i }).click();
            await expectSuccessAlert(page);
            await expect(activa).toHaveText('check');
        };

        // No depèn de l'estat inicial (el test de creació ja comprova que una entitat nova
        // quedi activa): es fan els dos canvis i l'entitat acaba en el mateix estat.
        if (await activa.textContent() === 'check') {
            await test.step('desactivar l\'entitat', async () => { logInfo('  -> desactivar l\'entitat'); await desactivar(); });
            await test.step('tornar-la a activar', async () => { logInfo('  -> tornar-la a activar'); await activar(); });
        } else {
            await test.step('activar l\'entitat', async () => { logInfo('  -> activar l\'entitat'); await activar(); });
            await test.step('tornar-la a desactivar', async () => { logInfo('  -> tornar-la a desactivar'); await desactivar(); });
        }

    });

    // ── Esborrat ──────────────────────────────────────────────────────────────

    test('ENTITAT ESBORRAR', async ({ page }) => {

        await test.step('cercar-la pel nom i esborrar-la (amb el permís assignat)', async () => {
            logInfo('  -> cercar-la pel nom i esborrar-la (amb el permís assignat)');
            await aplicarFiltreRapid(page, NOM_MODIFICAR, esGetLlistat);
            const fila = getFilaEntitat(page, CODI);
            await expect(fila).toHaveCount(1);
            await esborrarEntitatFila(page, fila);
            await expect(fila).toHaveCount(0);
        });

        await test.step('recarregar la pàgina i verificar que no hi és', async () => {
            logInfo('  -> recarregar la pàgina i verificar que no hi és');
            await anarALlistat(page);
            await aplicarFiltreRapid(page, NOM_MODIFICAR, esGetLlistat);
            await expect(getRows(page)).toHaveCount(0);
        });

    });

    // ── Neteja final ──────────────────────────────────────────────────────────

    test('ENTITAT NETEJA FINAL', async ({ page }) => {
        await esborrarEntitatDeProva(page);
    });

});
