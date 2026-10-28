import { test, expect, Page, Locator, FrameLocator } from '@playwright/test';
import path from 'path';

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
    assegurarRolJsp,
} from '../utils/jspHelpers';
import {
    CIF_CREAR, CIF_MODIFICAR, UNITAT_CREAR, UNITAT_MODIFICAR,
    COLORS_CREAR, COLORS_MODIFICAR, IMATGE_A, IMATGE_B, SID_ROL_JSP,
    llegirEntitatApi, expectImatge, expectColors,
} from './entitatComu';

// ─────────────────────────────────────────────────────────────────────────────
// Manteniment d'entitats JSP (superusuari, usuari propi: veure auth/super.setup.ts).
// Equivalent REACT: entitat.super.spec.ts
//
// Mateixa seqüència que el test REACT. Diferències de la interfície JSP:
//   - El llistat no té filtre ni cerca: l'entitat es localitza pel codi a la taula
//     (la cerca ràpida només es prova a REACT).
//   - El formulari té 6 imatges (també els logos de menú, que el REACT no mostra).
// ─────────────────────────────────────────────────────────────────────────────

const URL_ENTITATS = '/ripeaback/entitat';

const TAULA_ENTITATS = 'entitats';
const TAULA_PERMISOS = 'permisos';
const DT_ENTITATS    = '/entitat/datatable';
const DT_PERMISOS    = '/permis/datatable';

// Codi propi de JSP (el REACT usa ZZPWREACT): els dos projectes poden executar-se en paral·lel.
const CODI          = 'ZZPWJSP';
const NOM_CREAR     = 'zz entitat playwright jsp';
const NOM_MODIFICAR = 'zz entitat playwright jsp modificada';

// <input type="file"> de cada imatge (id) i camp de l'API amb el seu contingut.
const CAMPS_IMATGE = ['logoImg', 'menuImg', 'faviconImg', 'blackLogoImg', 'blackMenuImg', 'blackFaviconImg'] as const;
type CampImatge = typeof CAMPS_IMATGE[number];
const BYTES: Record<CampImatge, string> = {
    logoImg: 'logoImgBytes',
    menuImg: 'menuImgBytes',
    faviconImg: 'faviconImgBytes',
    blackLogoImg: 'blackLogoImgBytes',
    blackMenuImg: 'blackMenuImgBytes',
    blackFaviconImg: 'blackFaviconImgBytes',
};

type DadesEntitat = {
    nom: string; cif: string; unitat: string; postal: boolean;
    colors: Record<string, string>; imatges: Record<CampImatge, string | null>;
};

const DADES_CREAR: DadesEntitat = {
    nom: NOM_CREAR, cif: CIF_CREAR, unitat: UNITAT_CREAR, postal: false, colors: COLORS_CREAR,
    imatges: {
        logoImg: IMATGE_A, menuImg: IMATGE_A, faviconImg: IMATGE_A,
        blackLogoImg: IMATGE_A, blackMenuImg: IMATGE_A, blackFaviconImg: IMATGE_A,
    },
};
// Es canvien tots els camps (menys el codi, que no es pot modificar) i es treu el favicon.
const DADES_MODIFICAR: DadesEntitat = {
    nom: NOM_MODIFICAR, cif: CIF_MODIFICAR, unitat: UNITAT_MODIFICAR, postal: true, colors: COLORS_MODIFICAR,
    imatges: {
        logoImg: IMATGE_B, menuImg: IMATGE_B, faviconImg: null,
        blackLogoImg: IMATGE_B, blackMenuImg: IMATGE_B, blackFaviconImg: IMATGE_B,
    },
};

// ── Helpers de la pàgina ──────────────────────────────────────────────────────

const getFiles = (page: Page) => getFilesDatatable(page, TAULA_ENTITATS);
const getFilaEntitat = (page: Page, codi: string) => getFiles(page).filter({ hasText: codi });
// Columnes: codi, nom, CIF, activa, permisos, accions (la de l'id és oculta)
const getCellaActiva = (fila: Locator) => fila.locator('td').nth(3);

// Id de l'entitat a partir de l'enllaç de permisos de la fila (entitat/{id}/permis).
const getIdFila = async (fila: Locator): Promise<string> => {
    const href = await fila.locator('a[href*="/permis"]').getAttribute('href');
    return /entitat\/(\d+)\/permis/.exec(href ?? '')![1];
};

const anarALlistat = async (page: Page) => {
    logDebug('[Llistat] Navegant a ' + URL_ENTITATS + '...');
    const dt = waitDatatableResponse(page, DT_ENTITATS);
    await page.goto(URL_ENTITATS);
    await dt;
    await esperarDatatable(page, TAULA_ENTITATS);
};

// Obre una acció del desplegable "Accions" de la fila.
const clicarAccioFila = async (fila: Locator, accio: RegExp | string) => {
    await fila.getByRole('button', { name: /accions|acciones/i }).click();
    await fila.getByRole('link', typeof accio === 'string' ? { name: accio, exact: true } : { name: accio }).click();
};

// Bloc jasny "fileinput" que conté l'<input type="file"> de la imatge.
const getFileinput = (frame: FrameLocator, camp: CampImatge) =>
    frame.locator(`.fileinput:has(#${camp})`);

const omplirColors = async (frame: FrameLocator, colors: Record<string, string>) => {
    for (const [camp, valor] of Object.entries(colors)) {
        await frame.locator(`input[name="${camp}"]`).fill(valor);
    }
};

// Omple el formulari (alta o modificació) amb les dades indicades. Les imatges a null es
// treuen amb el botó "Netejar" del camp.
const omplirFormulari = async (frame: FrameLocator, dades: DadesEntitat) => {
    await frame.locator('input[name="nom"]').fill(dades.nom);
    await frame.locator('input[name="cif"]').fill(dades.cif);
    await frame.locator('input[name="unitatArrel"]').fill(dades.unitat);
    await frame.locator('#permetreEnviamentPostal').setChecked(dades.postal);
    for (const camp of CAMPS_IMATGE) {
        const fitxer = dades.imatges[camp];
        if (fitxer) {
            // El plugin jasny s'inicialitza amb el primer clic i setInputFiles no clica: el nom
            // no es pinta a la pantalla, però el fitxer queda a l'input i s'envia.
            await frame.locator(`#${camp}`).setInputFiles(fitxer);
            await expect.poll(() => frame.locator(`#${camp}`).evaluate((e: HTMLInputElement) => e.files?.[0]?.name))
                .toBe(path.basename(fitxer));
        } else {
            await getFileinput(frame, camp).locator('a[data-dismiss="fileinput"]').click();
            await expect(getFileinput(frame, camp)).toHaveClass(/fileinput-new/);
        }
    }
    await omplirColors(frame, dades.colors);
};

// Obre el formulari de modificació de la fila (ja visible al llistat).
const obrirModificacio = async (page: Page, codi: string): Promise<FrameLocator> => {
    const fila = getFilaEntitat(page, codi);
    await expect(fila).toHaveCount(1);
    await clicarAccioFila(fila, /modificar/i);
    const frame = await esperarModalFormulari(page, 'input[name="codi"]');
    await expect(frame.locator('input[name="codi"]')).toHaveValue(codi);
    return frame;
};

// Comprova el formulari (el que veu l'usuari) i l'API (el contingut real de les imatges).
// Amb expect.soft: es comproven tots els camps encara que algun falli (el test queda en error
// igualment), per saber en una sola execució tot el que no s'ha desat bé.
const verificarEntitat = async (page: Page, frame: FrameLocator, id: string, dades: DadesEntitat) => {
    await expect.soft(frame.locator('input[name="codi"]')).toHaveAttribute('readonly', /.*/);
    await expect.soft(frame.locator('input[name="nom"]')).toHaveValue(dades.nom);
    await expect.soft(frame.locator('input[name="cif"]')).toHaveValue(dades.cif);
    await expect.soft(frame.locator('input[name="unitatArrel"]')).toHaveValue(dades.unitat);
    const postal = frame.locator('#permetreEnviamentPostal');
    if (dades.postal) await expect.soft(postal).toBeChecked(); else await expect.soft(postal).not.toBeChecked();
    for (const [camp, valor] of Object.entries(dades.colors)) {
        await expect.soft(frame.locator(`input[name="${camp}"]`), camp).toHaveValue(valor);
    }
    // Una imatge desada es mostra amb el bloc en estat "fileinput-exists".
    for (const camp of CAMPS_IMATGE) {
        await expect.soft(getFileinput(frame, camp), camp)
            .toHaveClass(dades.imatges[camp] ? /fileinput-exists/ : /fileinput-new/);
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

// Esborra la fila de l'entitat (l'enllaç té data-confirm: confirm() natiu del navegador).
const esborrarEntitatFila = async (page: Page, fila: Locator) => {
    page.once('dialog', dialog => dialog.accept());
    const dt = waitDatatableResponse(page, DT_ENTITATS);
    await clicarAccioFila(fila, /esborrar|eliminar|borrar/i);
    await dt;
    await esperarDatatable(page, TAULA_ENTITATS);
};

const esborrarEntitatDeProva = async (page: Page) => {
    await anarALlistat(page);
    const fila = getFilaEntitat(page, CODI);
    if (await fila.count() === 0) {
        logInfo('  -> no hi ha cap entitat de prova');
        return;
    }
    logInfo('  -> esborrant l\'entitat de prova ' + CODI);
    await esborrarEntitatFila(page, fila);
    await expect(getFilaEntitat(page, CODI)).toHaveCount(0);
};

// ─────────────────────────────────────────────────────────────────────────────
// Prerequisit: assegurar el rol actiu (bloc propi SENSE beforeEach)
// ─────────────────────────────────────────────────────────────────────────────

test.describe('Prerequisits JSP — rol IPA_SUPER', () => {

    test('ROL ASSEGURAR IPA_SUPER', async ({ page }) => {
        await page.goto(URL_ENTITATS);
        await assegurarRolJsp(page, 'IPA_SUPER', URL_ENTITATS);
    });

});

// ─────────────────────────────────────────────────────────────────────────────
// Pàgina: Entitats JSP  (rol IPA_SUPER)
// ─────────────────────────────────────────────────────────────────────────────

test.describe('Gestió d\'Entitats JSP — IPA_SUPER', () => {

    test.beforeEach(async ({ page }) => {
        await anarALlistat(page);
    });

    // ── Disposició ────────────────────────────────────────────────────────────

    test('ENTITAT VISTA', async ({ page }) => {

        await test.step('taula visible amb dades', async () => {
            logInfo('  -> taula visible amb dades');
            await expect(page.locator('#' + TAULA_ENTITATS)).toBeVisible();
            await expect(getFiles(page).first()).toBeVisible();
        });

        await test.step('columnes esperades visibles', async () => {
            logInfo('  -> columnes esperades visibles');
            const headers = page.locator(`#${TAULA_ENTITATS} thead th`);
            await expect(headers.filter({ hasText: /codi|c.digo/i }).first()).toBeVisible();
            await expect(headers.filter({ hasText: /\bnom\b|nombre/i }).first()).toBeVisible();
            await expect(headers.filter({ hasText: /cif/i }).first()).toBeVisible();
            await expect(headers.filter({ hasText: /activa/i }).first()).toBeVisible();
        });

        await test.step('botó de nova entitat visible', async () => {
            logInfo('  -> botó de nova entitat visible');
            await expect(page.locator('a[href="entitat/new"]')).toBeVisible();
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
            await page.locator('a[href="entitat/new"]').click();
            const frame = await esperarModalFormulari(page, 'input[name="codi"]');
            await frame.locator('input[name="codi"]').fill(CODI);
            // Permetre enviament postal es deixa desmarcat (valor contrari al per defecte de
            // l'entitat a la BD), per comprovar que es desa el que ha triat l'usuari.
            await omplirFormulari(frame, DADES_CREAR);
            await enviarModal(page);
            await expectSuccessAlert(page);
        });

        await test.step('l\'entitat apareix al llistat i està activa', async () => {
            logInfo('  -> l\'entitat apareix al llistat i està activa');
            await anarALlistat(page);
            const fila = getFilaEntitat(page, CODI);
            await expect(fila).toHaveCount(1);
            await expect(getCellaActiva(fila).locator('.fa-check'), 'una entitat nova ha de quedar activa').toHaveCount(1);
        });

        await test.step('consultar l\'entitat i verificar les dades desades', async () => {
            logInfo('  -> consultar l\'entitat i verificar les dades desades');
            const id = await getIdFila(getFilaEntitat(page, CODI));
            const frame = await obrirModificacio(page, CODI);
            await verificarEntitat(page, frame, id, DADES_CREAR);
            await cancelarModal(page);
        });

    });

    // ── Modificació ───────────────────────────────────────────────────────────

    test('ENTITAT MODIFICAR', async ({ page }) => {

        await test.step('modificar tots els camps', async () => {
            logInfo('  -> modificar tots els camps');
            const frame = await obrirModificacio(page, CODI);
            await expect(frame.locator('input[name="codi"]')).toHaveAttribute('readonly', /.*/);
            await omplirFormulari(frame, DADES_MODIFICAR);
            await enviarModal(page);
            await expectSuccessAlert(page);
        });

        await test.step('recarregar i verificar les dades desades', async () => {
            logInfo('  -> recarregar i verificar les dades desades');
            await anarALlistat(page);
            const id = await getIdFila(getFilaEntitat(page, CODI));
            const frame = await obrirModificacio(page, CODI);
            await verificarEntitat(page, frame, id, DADES_MODIFICAR);
            await cancelarModal(page);
        });

    });

    // ── Permisos ──────────────────────────────────────────────────────────────

    test('ENTITAT PERMIS', async ({ page }) => {

        await test.step('donar permís d\'usuari a un rol de prova', async () => {
            logInfo('  -> donar permís d\'usuari a un rol de prova');
            const fila = getFilaEntitat(page, CODI);
            await expect(fila).toHaveCount(1);
            let dt = waitDatatableResponse(page, DT_PERMISOS);
            await fila.locator('a[href*="/permis"]').click();
            await dt;
            await esperarDatatable(page, TAULA_PERMISOS);
            await expect(getFilesDatatable(page, TAULA_PERMISOS)).toHaveCount(0);

            await page.locator('a[href*="/permis/new"]').click();
            const frame = await esperarModalFormulari(page, 'input[name="principalNom"]');
            // El <select> queda ocult per select2; el formulari envia el seu valor.
            await frame.locator('select#principalTipus').selectOption('ROL', { force: true });
            await frame.locator('input[name="principalNom"]').fill(SID_ROL_JSP);
            await frame.locator('#read').check();
            dt = waitDatatableResponse(page, DT_PERMISOS);
            await enviarModal(page);
            await dt;
            await esperarDatatable(page, TAULA_PERMISOS);
            await expectSuccessAlert(page);
            await expect(getFilesDatatable(page, TAULA_PERMISOS).filter({ hasText: SID_ROL_JSP })).toHaveCount(1);
        });

        await test.step('el llistat d\'entitats mostra 1 permís', async () => {
            logInfo('  -> el llistat d\'entitats mostra 1 permís');
            await anarALlistat(page);
            await expect(getFilaEntitat(page, CODI).locator('a[href*="/permis"] .badge')).toHaveText('1');
        });

    });

    // ── Activar / Desactivar ──────────────────────────────────────────────────

    test('ENTITAT ACTIVAR', async ({ page }) => {

        const fila = getFilaEntitat(page, CODI);
        const activa = getCellaActiva(fila).locator('.fa-check');
        await expect(fila).toHaveCount(1);

        // El nom accessible dels enllaços comença amb la icona (caràcter de Font Awesome) i
        // &nbsp;: ^\W* evita que "activar" coincideixi també amb "Desactivar".
        const canviar = async (accio: RegExp, actiuDespres: boolean) => {
            const dt = waitDatatableResponse(page, DT_ENTITATS);
            await clicarAccioFila(fila, accio);
            await dt;
            await esperarDatatable(page, TAULA_ENTITATS);
            await expect(activa).toHaveCount(actiuDespres ? 1 : 0);
        };
        const DESACTIVAR = /^\W*desactivar\s*$/i;
        const ACTIVAR = /^\W*activar\s*$/i;

        // No depèn de l'estat inicial (el test de creació ja comprova que una entitat nova
        // quedi activa): es fan els dos canvis i l'entitat acaba en el mateix estat.
        if (await activa.count() === 1) {
            await test.step('desactivar l\'entitat', async () => { logInfo('  -> desactivar l\'entitat'); await canviar(DESACTIVAR, false); });
            await test.step('tornar-la a activar', async () => { logInfo('  -> tornar-la a activar'); await canviar(ACTIVAR, true); });
        } else {
            await test.step('activar l\'entitat', async () => { logInfo('  -> activar l\'entitat'); await canviar(ACTIVAR, true); });
            await test.step('tornar-la a desactivar', async () => { logInfo('  -> tornar-la a desactivar'); await canviar(DESACTIVAR, false); });
        }

    });

    // ── Esborrat ──────────────────────────────────────────────────────────────

    test('ENTITAT ESBORRAR', async ({ page }) => {

        await test.step('esborrar l\'entitat (amb el permís assignat)', async () => {
            logInfo('  -> esborrar l\'entitat (amb el permís assignat)');
            const fila = getFilaEntitat(page, CODI);
            await expect(fila).toHaveCount(1);
            await esborrarEntitatFila(page, fila);
            await expect(getFilaEntitat(page, CODI)).toHaveCount(0);
        });

        await test.step('recarregar la pàgina i verificar que no hi és', async () => {
            logInfo('  -> recarregar la pàgina i verificar que no hi és');
            await anarALlistat(page);
            await expect(getFilaEntitat(page, CODI)).toHaveCount(0);
        });

    });

    // ── Neteja final ──────────────────────────────────────────────────────────

    test('ENTITAT NETEJA FINAL', async ({ page }) => {
        await esborrarEntitatDeProva(page);
    });

});
