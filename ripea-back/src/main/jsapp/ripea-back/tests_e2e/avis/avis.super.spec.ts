import { test, expect, Page, Locator, Browser } from '@playwright/test';

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
    aplicarFiltreRapid,
    waitApiEntityLoad,
    seleccionarOpcioAutocompletamentPerText,
    assegurarRol,
} from '../utils/reactHelpers';
import { finestres, DATES_CREAR, DATES_MODIFICAR, dia } from './avisComu';

// ─────────────────────────────────────────────────────────────────────────────
// Manteniment d'avisos REACT (superusuari). Equivalent JSP: avis.super.jsp.spec.ts
//
// Seqüència: NETEJA PREVIA, CREAR (avís principal), LLISTAT (avisos de finestra: quins es
// mostren segons les dates, en directe per SSE i després de recarregar), ACTIVAR (individual
// i massiu), ENTITAT (només es mostren als usuaris de l'entitat de l'avís), MODIFICAR,
// ESBORRAR (individual, avís principal), ESBORRAR MASSIU (la resta) i NETEJA FINAL.
//
// Els avisos globals es mostren a tots els usuaris connectats mentre existeixen: si s'executa
// en paral·lel amb altres tests, aquests en veuran els bàners uns segons.
// ─────────────────────────────────────────────────────────────────────────────

const URL_AVISOS = '/ripeaback/reactapp/avis';

// Prefix propi de REACT (el JSP usa "zz PW JSP avis"). Cap assumpte en conté un altre.
const PREFIX           = 'zz PW REACT avis';
const ASSUMPTE_CREAR   = PREFIX + ' principal';
const ASSUMPTE_MODIF   = PREFIX + ' modificat';
const MISSATGE_CREAR   = 'Missatge de l\'avís principal de prova';
const MISSATGE_MODIF   = 'Missatge modificat de l\'avís de prova';
const assumpteFinestra = (sufix: string) => `${PREFIX} ${sufix}`;
const ASSUMPTE_GOIB    = PREFIX + ' entitat GOIB';
const ASSUMPTE_GONA    = PREFIX + ' entitat GONA';

// Entitats (es cerquen pel codi; l'opció mostra el nom)
const ENTITAT_GOIB = { cerca: 'GOIB', nom: 'Govern de les Illes Balears' };
const ENTITAT_GONA = { cerca: 'GONA', nom: 'Gobierno de Navarra' };

// Usuari observador (amb entitat GOIB) per comprovar els avisos d'entitat.
const SESSIO_USUARI = 'tests_e2e/.auth/user.json';

const SELECTOR_ENTITAT = '[name="entitat"] input[type="text"]';
const NIVELL = { INFO: /^Informaci/i, WARNING: /^Advert/i, ERROR: /^Error$/i };

// ── Helpers de la pàgina ──────────────────────────────────────────────────────

const esGetLlistat = (url: string) => new URL(url).pathname.endsWith('/api/avis');
const getDialog = (page: Page) => page.locator('[role="dialog"]');
const getFiltreRapid = (page: Page) => page.getByRole('textbox', { name: /filtre r.pid|filtro r.pido/i });
const getFilaAvis = (page: Page, assumpte: string) =>
    getRows(page).filter({ has: page.locator('[data-field="assumpte"]', { hasText: assumpte }) });
// Bàner de l'avís (MUI Alert que BaseApp pinta damunt el contingut).
const getBaner = (page: Page, assumpte: string) => page.locator('.MuiAlert-root').filter({ hasText: assumpte });

const anarALlistat = async (page: Page) => {
    logDebug('[Llistat] Navegant a ' + URL_AVISOS + '...');
    await page.goto(URL_AVISOS);
    await esperarGridCarregat(page);
    if (await getFiltreRapid(page).inputValue() !== '') {
        await aplicarFiltreRapid(page, '', esGetLlistat);
    }
};

// Camp de data (MUI DateField): es tecleja per seccions (dia, mes, any) a partir del dia.
// El valor desat queda a l'<input name=...> ocult, en format DD/MM/YYYY.
const fixarData = async (page: Page, dialog: Locator, camp: string, data: string | null) => {
    const arrel = dialog.locator(`div:has(> input[name="${camp}"])`);
    const ocult = dialog.locator(`input[name="${camp}"]`);
    if (data == null) {
        if (await ocult.inputValue() !== '') {
            await arrel.locator('button[title="Clear"]').click({ force: true });
        }
        await expect(ocult).toHaveValue('');
        return;
    }
    // Es tecleja secció per secció (dia, mes, any): teclejar tota la data d'un cop pot perdre
    // dígits si MUI encara no ha passat a la secció següent. Es reintenta si no queda bé.
    const [dd, mm, aaaa] = data.split('/');
    for (let intent = 1; intent <= 3; intent++) {
        for (const [seccio, valor] of [['Day', dd], ['Month', mm], ['Year', aaaa]]) {
            await arrel.locator(`[role="spinbutton"][aria-label="${seccio}"]`).click();
            await page.keyboard.type(valor);
        }
        if (await ocult.inputValue() === data) break;
        logDebug(`[Data] ${camp}: valor inesperat "${await ocult.inputValue()}" (intent ${intent})`);
    }
    await expect(ocult).toHaveValue(data);
};

type DadesAvis = {
    assumpte: string; missatge: string; inici: string; final: string | null;
    nivell: keyof typeof NIVELL; entitat?: { cerca: string; nom: string } | null;
};

const omplirFormulari = async (page: Page, dialog: Locator, dades: DadesAvis) => {
    await dialog.locator('input[name="assumpte"]').fill(dades.assumpte);
    await dialog.locator('textarea[name="missatge"]').fill(dades.missatge);
    await fixarData(page, dialog, 'dataInici', dades.inici);
    await fixarData(page, dialog, 'dataFinal', dades.final);
    await triaMuiSelect(page, dialog, 'avisNivell', NIVELL[dades.nivell]);
    if (dades.entitat) {
        const actual = await dialog.locator(SELECTOR_ENTITAT).inputValue();
        if (actual !== dades.entitat.nom) {
            if (actual !== '') await dialog.locator(SELECTOR_ENTITAT).fill('');
            await seleccionarOpcioAutocompletamentPerText(page, dialog, SELECTOR_ENTITAT, dades.entitat.cerca, dades.entitat.nom);
        }
    }
};

const crearAvis = async (page: Page, dades: DadesAvis) => {
    logInfo(`  -> crear l'avís "${dades.assumpte}"`);
    await page.getByRole('button').filter({ hasText: /nou av/i }).click();
    const dialog = getDialog(page);
    await expect(dialog).toBeVisible();
    await omplirFormulari(page, dialog, dades);
    await guardaAmbDelay(page, dialog.getByRole('button').filter({ hasText: /guarda/i }));
    await expectSuccessAlert(page);
    await expect(dialog).toBeHidden();
};

// Obre el formulari de modificació de l'avís (la fila ha de ser visible al llistat).
const obrirModificacio = async (page: Page, assumpte: string): Promise<Locator> => {
    const fila = getFilaAvis(page, assumpte);
    await expect(fila).toHaveCount(1);
    const entityResp = waitApiEntityLoad(page, await fila.getAttribute('data-id'));
    await obrirMenuAccions(fila);
    await page.getByRole('menuitem').filter({ hasText: /modifica/i }).click();
    const dialog = getDialog(page);
    await expect(dialog).toBeVisible();
    await entityResp;
    await expect(dialog.locator('input[name="assumpte"]')).toHaveValue(assumpte);
    return dialog;
};

const verificarFormulari = async (dialog: Locator, dades: DadesAvis) => {
    await expect.soft(dialog.locator('input[name="assumpte"]')).toHaveValue(dades.assumpte);
    await expect.soft(dialog.locator('textarea[name="missatge"]')).toHaveValue(dades.missatge);
    await expect.soft(dialog.locator('input[name="dataInici"]')).toHaveValue(dades.inici);
    await expect.soft(dialog.locator('input[name="dataFinal"]')).toHaveValue(dades.final ?? '');
    await expect.soft(dialog.locator('input[name="avisNivell"]')).toHaveValue(dades.nivell);
    await expect.soft(dialog.locator(SELECTOR_ENTITAT)).toHaveValue(dades.entitat?.nom ?? '');
};

const tancarDialeg = async (dialog: Locator) => {
    await dialog.getByRole('button').filter({ hasText: /cancel/i }).click();
    await expect(dialog).toBeHidden();
};

const esborrarFila = async (page: Page, fila: Locator) => {
    await obrirMenuAccions(fila);
    await page.getByRole('menuitem').filter({ hasText: /esborra|elimina|borra/i }).click();
    await confirmarDialeg(page);
    await expectSuccessAlert(page);
};

// Activa o desactiva una fila amb l'acció del menú (individual).
const canviarActiuFila = async (page: Page, fila: Locator, activar: boolean) => {
    await obrirMenuAccions(fila);
    const item = activar
        ? page.getByRole('menuitem').filter({ hasText: /activa/i }).filter({ hasNotText: /desactiva/i })
        : page.getByRole('menuitem').filter({ hasText: /desactiva/i });
    await item.click();
    await expectSuccessAlert(page);
};

// Selecciona exactament les files indicades (buidant abans la selecció) i executa una acció
// del menú "Opcions" de la selecció massiva. Salvaguarda: el comptador ha de coincidir.
const accioMassiva = async (page: Page, assumptes: string[], accio: RegExp, excepte?: RegExp) => {
    const selector = page.locator('.massive-selector');
    await selector.getByRole('button', { name: /neteja la selecci|limpia la selecci/i }).click();
    await expect(selector.locator('.MuiChip-label')).toHaveText('0');
    for (const assumpte of assumptes) {
        await getFilaAvis(page, assumpte).getByRole('checkbox').check();
    }
    await expect(selector.locator('.MuiChip-label')).toHaveText(String(assumptes.length));
    await selector.getByRole('button', { name: /opcions|opciones/i }).click();
    const item = page.getByRole('menuitem').filter({ hasText: accio });
    await (excepte ? item.filter({ hasNotText: excepte }) : item).click();
    await expectSuccessAlert(page);
};

// Esborra tots els avisos amb el prefix de prova (un per un, només files amb el prefix).
const esborrarAvisosDeProva = async (page: Page) => {
    for (let intent = 0; intent < 20; intent++) {
        await anarALlistat(page);
        await aplicarFiltreRapid(page, PREFIX, esGetLlistat);
        const files = getRows(page).filter({ hasText: PREFIX });
        if (await files.count() === 0) {
            logInfo('  -> no queda cap avís de prova');
            return;
        }
        await esborrarFila(page, files.first());
    }
    throw new Error('No s\'han pogut esborrar els avisos de prova');
};

// Obre una pàgina amb la sessió de l'usuari observador (rip_user, entitat GOIB).
const obrirObservador = async (browser: Browser): Promise<Page> => {
    const context = await browser.newContext({ storageState: SESSIO_USUARI, locale: 'ca-ES' });
    return context.newPage();
};

// ─────────────────────────────────────────────────────────────────────────────

test.describe('Prerequisits — rol IPA_SUPER', () => {

    test('ROL ASSEGURAR IPA_SUPER', async ({ page }) => {
        await page.goto(URL_AVISOS);
        await assegurarRol(page, 'IPA_SUPER', URL_AVISOS);
    });

});

test.describe('Gestió d\'Avisos — IPA_SUPER', () => {

    test.beforeEach(async ({ page }) => {
        await anarALlistat(page);
    });

    test('AVIS VISTA', async ({ page }) => {

        await test.step('graella, columnes, botó de nou avís i filtre ràpid', async () => {
            logInfo('  -> graella, columnes, botó de nou avís i filtre ràpid');
            await expect(getGrid(page)).toBeVisible();
            const headers = page.locator('.MuiDataGrid-columnHeaderTitle');
            for (const h of [/assumpte|asunto/i, /entitat|entidad/i, /data inici|fecha inicio/i, /data final|fecha final/i, /activ/i, /nivell|nivel/i]) {
                await expect(headers.filter({ hasText: h }).first()).toBeVisible();
            }
            await expect(page.getByRole('button').filter({ hasText: /nou av/i })).toBeVisible();
            await expect(getFiltreRapid(page)).toBeVisible();
        });

    });

    test('AVIS NETEJA PREVIA', async ({ page }) => {
        await esborrarAvisosDeProva(page);
    });

    test('AVIS CREAR', async ({ page }) => {

        const dades: DadesAvis = {
            assumpte: ASSUMPTE_CREAR, missatge: MISSATGE_CREAR, ...DATES_CREAR,
            nivell: 'WARNING', entitat: ENTITAT_GOIB,
        };

        await test.step('crear l\'avís amb tots els camps', async () => {
            await crearAvis(page, dades);
        });

        await test.step('l\'avís apareix al llistat amb les dades', async () => {
            logInfo('  -> l\'avís apareix al llistat amb les dades');
            await aplicarFiltreRapid(page, ASSUMPTE_CREAR, esGetLlistat);
            const fila = getFilaAvis(page, ASSUMPTE_CREAR);
            await expect(fila).toHaveCount(1);
            await expect.soft(fila.locator('[data-field="entitat"]')).toHaveText(ENTITAT_GOIB.nom);
            await expect.soft(fila.locator('[data-field="dataInici"]')).toHaveText(dades.inici);
            await expect.soft(fila.locator('[data-field="dataFinal"]')).toHaveText(dades.final!);
            await expect.soft(fila.locator('[data-field="actiu"]')).toHaveText('check');
        });

        await test.step('consultar l\'avís i verificar les dades desades', async () => {
            logInfo('  -> consultar l\'avís i verificar les dades desades');
            const dialog = await obrirModificacio(page, ASSUMPTE_CREAR);
            await verificarFormulari(dialog, dades);
            await tancarDialeg(dialog);
        });

    });

    test('AVIS LLISTAT', async ({ page }) => {
        test.setTimeout(150_000);

        await test.step('crear els avisos de finestra', async () => {
            for (const f of finestres()) {
                await crearAvis(page, {
                    assumpte: assumpteFinestra(f.sufix), missatge: 'Avís de prova: ' + f.sufix,
                    inici: f.inici, final: f.final, nivell: 'INFO', entitat: null,
                });
            }
        });

        const comprovarBaners = async () => {
            // Primer els visibles (el missatge SSE porta la llista sencera): quan hi són,
            // l'absència dels altres ja és definitiva.
            for (const f of finestres().filter(f => f.visible)) {
                await expect(getBaner(page, assumpteFinestra(f.sufix)), `bàner ${f.sufix}`).toBeVisible({ timeout: 15_000 });
            }
            for (const f of finestres().filter(f => !f.visible)) {
                await expect(getBaner(page, assumpteFinestra(f.sufix)), `bàner ${f.sufix}`).toHaveCount(0);
            }
        };

        await test.step('es mostren els vigents i no els futurs ni els caducats (SSE, sense recarregar)', async () => {
            logInfo('  -> bàners en directe');
            await comprovarBaners();
        });

        await test.step('el mateix després de recarregar la pàgina', async () => {
            logInfo('  -> bàners després de recarregar');
            await anarALlistat(page);
            await comprovarBaners();
        });

    });

    test('AVIS ACTIVAR', async ({ page }) => {
        test.setTimeout(150_000);

        await aplicarFiltreRapid(page, PREFIX, esGetLlistat);
        const vigent = assumpteFinestra('vigent');
        const massius = [assumpteFinestra('sense final'), assumpteFinestra('avui')];

        await test.step('desactivar i activar un avís amb l\'acció individual', async () => {
            logInfo('  -> individual');
            await expect(getBaner(page, vigent)).toBeVisible({ timeout: 15_000 });
            await canviarActiuFila(page, getFilaAvis(page, vigent), false);
            await expect(getFilaAvis(page, vigent).locator('[data-field="actiu"]')).toHaveText('');
            await expect(getBaner(page, vigent)).toHaveCount(0, { timeout: 15_000 });
            await canviarActiuFila(page, getFilaAvis(page, vigent), true);
            await expect(getFilaAvis(page, vigent).locator('[data-field="actiu"]')).toHaveText('check');
            await expect(getBaner(page, vigent)).toBeVisible({ timeout: 15_000 });
        });

        await test.step('desactivar i activar dos avisos amb l\'acció massiva', async () => {
            logInfo('  -> massiu');
            // El bàner en directe (SSE) és soft: amb diversos avisos el push arriba amb l'estat
            // anterior a l'acció (bug detectat el 28/10/2026). Després de recarregar, la comprovació
            // és ferma: verifica que l'acció massiva ha canviat realment els avisos.
            await accioMassiva(page, massius, /desactiva/i);
            for (const a of massius) {
                await expect(getFilaAvis(page, a).locator('[data-field="actiu"]')).toHaveText('');
                await expect.soft(getBaner(page, a), `SSE: el bàner "${a}" ha de desaparèixer sense recarregar`)
                    .toHaveCount(0, { timeout: 15_000 });
            }
            await anarALlistat(page);
            await expect(getBaner(page, assumpteFinestra('vigent'))).toBeVisible({ timeout: 15_000 });
            for (const a of massius) {
                await expect(getBaner(page, a)).toHaveCount(0);
            }
            await aplicarFiltreRapid(page, PREFIX, esGetLlistat);
            await accioMassiva(page, massius, /activa/i, /desactiva/i);
            for (const a of massius) {
                await expect(getFilaAvis(page, a).locator('[data-field="actiu"]')).toHaveText('check');
                await expect.soft(getBaner(page, a), `SSE: el bàner "${a}" ha d'aparèixer sense recarregar`)
                    .toBeVisible({ timeout: 15_000 });
            }
            await anarALlistat(page);
            for (const a of massius) {
                await expect(getBaner(page, a)).toBeVisible({ timeout: 15_000 });
            }
        });

    });

    test('AVIS ENTITAT', async ({ page, browser }) => {
        test.setTimeout(120_000);

        await test.step('crear un avís vigent per a GOIB i un per a GONA', async () => {
            for (const [assumpte, entitat] of [[ASSUMPTE_GOIB, ENTITAT_GOIB], [ASSUMPTE_GONA, ENTITAT_GONA]] as const) {
                await crearAvis(page, {
                    assumpte, missatge: 'Avís de prova d\'entitat', inici: dia(-1), final: dia(1),
                    nivell: 'ERROR', entitat,
                });
            }
        });

        await test.step('el superusuari (sense entitat) no en veu cap', async () => {
            logInfo('  -> superusuari');
            await anarALlistat(page);
            // Un avís global vigent assegura que els avisos ja s'han rebut.
            await expect(getBaner(page, assumpteFinestra('vigent'))).toBeVisible({ timeout: 15_000 });
            await expect(getBaner(page, ASSUMPTE_GOIB)).toHaveCount(0);
            await expect(getBaner(page, ASSUMPTE_GONA)).toHaveCount(0);
        });

        const observador = await obrirObservador(browser);
        try {
            await test.step('un usuari de GOIB veu el de GOIB i no el de GONA (REACT)', async () => {
                logInfo('  -> observador REACT');
                await observador.goto('/ripeaback/reactapp/');
                await expect(getBaner(observador, assumpteFinestra('vigent'))).toBeVisible({ timeout: 20_000 });
                await expect(getBaner(observador, ASSUMPTE_GOIB)).toBeVisible();
                await expect(getBaner(observador, ASSUMPTE_GONA)).toHaveCount(0);
            });

            await test.step('el mateix a la interfície JSP (recarregant)', async () => {
                logInfo('  -> observador JSP');
                await observador.goto('/ripeaback/expedient');
                const cards = observador.locator('.avisCard');
                await expect(cards.filter({ hasText: assumpteFinestra('vigent') })).toHaveCount(1);
                await expect(cards.filter({ hasText: ASSUMPTE_GOIB })).toHaveCount(1);
                await expect(cards.filter({ hasText: ASSUMPTE_GONA })).toHaveCount(0);
            });
        } finally {
            await observador.context().close();
        }

    });

    test('AVIS MODIFICAR', async ({ page }) => {

        const dades: DadesAvis = {
            assumpte: ASSUMPTE_MODIF, missatge: MISSATGE_MODIF, ...DATES_MODIFICAR,
            nivell: 'ERROR', entitat: ENTITAT_GONA,
        };

        await test.step('modificar tots els camps', async () => {
            logInfo('  -> modificar tots els camps');
            await aplicarFiltreRapid(page, ASSUMPTE_CREAR, esGetLlistat);
            const dialog = await obrirModificacio(page, ASSUMPTE_CREAR);
            await omplirFormulari(page, dialog, dades);
            await guardaAmbDelay(page, dialog.getByRole('button').filter({ hasText: /guarda/i }));
            await expectSuccessAlert(page);
            await expect(dialog).toBeHidden();
        });

        await test.step('recarregar i verificar les dades desades', async () => {
            logInfo('  -> recarregar i verificar');
            await anarALlistat(page);
            await aplicarFiltreRapid(page, ASSUMPTE_MODIF, esGetLlistat);
            const dialog = await obrirModificacio(page, ASSUMPTE_MODIF);
            await verificarFormulari(dialog, dades);
            await tancarDialeg(dialog);
        });

    });

    test('AVIS ESBORRAR', async ({ page }) => {

        await test.step('esborrar l\'avís principal amb l\'acció individual', async () => {
            logInfo('  -> esborrar individual');
            await aplicarFiltreRapid(page, ASSUMPTE_MODIF, esGetLlistat);
            await expect(getFilaAvis(page, ASSUMPTE_MODIF)).toHaveCount(1);
            await esborrarFila(page, getFilaAvis(page, ASSUMPTE_MODIF));
            await expect(getFilaAvis(page, ASSUMPTE_MODIF)).toHaveCount(0);
        });

        await test.step('recarregar la pàgina i verificar que no hi és', async () => {
            await anarALlistat(page);
            await aplicarFiltreRapid(page, ASSUMPTE_MODIF, esGetLlistat);
            await expect(getRows(page)).toHaveCount(0);
        });

    });

    test('AVIS ESBORRAR MASSIU', async ({ page }) => {

        await test.step('esborrar amb l\'acció massiva els avisos de finestra i d\'entitat', async () => {
            logInfo('  -> esborrar massiu');
            await aplicarFiltreRapid(page, PREFIX, esGetLlistat);
            const assumptes = (await getRows(page).filter({ hasText: PREFIX })
                .locator('[data-field="assumpte"]').allTextContents()).map(t => t.trim());
            expect(assumptes.length).toBeGreaterThan(0);
            await accioMassiva(page, assumptes, /esborra|elimina|borra/i);
        });

        await test.step('recarregar la pàgina i verificar que no en queda cap', async () => {
            await anarALlistat(page);
            await aplicarFiltreRapid(page, PREFIX, esGetLlistat);
            await expect(getRows(page)).toHaveCount(0);
        });

    });

    test('AVIS NETEJA FINAL', async ({ page }) => {
        await esborrarAvisosDeProva(page);
    });

});
