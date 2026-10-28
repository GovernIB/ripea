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
    assegurarRolJsp,
} from '../utils/jspHelpers';
import { finestres, DATES_CREAR, DATES_MODIFICAR } from './avisComu';

// ─────────────────────────────────────────────────────────────────────────────
// Manteniment d'avisos JSP (superusuari). Equivalent REACT: avis.super.spec.ts
//
// Mateixa seqüència que el test REACT. Diferències de la interfície JSP:
//   - El formulari no té entitat: tots els avisos que s'hi creen són globals.
//   - Els avisos es mostren a la capçalera (.avisCard) en carregar la pàgina (sense SSE).
//   - El llistat no té cerca i pagina de 10 en 10: les files es localitzen per l'assumpte.
//   - La selecció massiva es desa a la sessió i no es pot buidar: es comprova que estigui
//     buida abans de seleccionar (el servidor la buida després de cada acció massiva).
// ─────────────────────────────────────────────────────────────────────────────

const URL_AVISOS = '/ripeaback/avis';
const TAULA      = 'avisos';
const DT         = '/avis/datatable';

// Prefix propi de JSP (el REACT usa "zz PW REACT avis"). Cap assumpte en conté un altre.
const PREFIX           = 'zz PW JSP avis';
const ASSUMPTE_CREAR   = PREFIX + ' principal';
const ASSUMPTE_MODIF   = PREFIX + ' modificat';
const MISSATGE_CREAR   = 'Missatge de l\'avís principal de prova';
const MISSATGE_MODIF   = 'Missatge modificat de l\'avís de prova';
const assumpteFinestra = (sufix: string) => `${PREFIX} ${sufix}`;

// ── Helpers de la pàgina ──────────────────────────────────────────────────────

const getFiles = (page: Page) => getFilesDatatable(page, TAULA);
const getFilaAvis = (page: Page, assumpte: string) => getFiles(page).filter({ hasText: assumpte });
// Columnes: selecció, assumpte, data inici, data final, activa, nivell, accions
const getCella = (fila: Locator, index: number) => fila.locator('td').nth(index);
const getBaner = (page: Page, assumpte: string) => page.locator('.avisCard').filter({ hasText: assumpte });
const getComptador = (page: Page) => page.locator('#seleccioCount');

const anarALlistat = async (page: Page) => {
    logDebug('[Llistat] Navegant a ' + URL_AVISOS + '...');
    const dt = waitDatatableResponse(page, DT);
    await page.goto(URL_AVISOS);
    await dt;
    await esperarDatatable(page, TAULA);
};

// Les accions que recarreguen la pàgina (modal amb data-refresh-pagina, enllaços d'activar,
// esborrar i acció massiva): s'espera la càrrega del datatable de la pàgina nova.
const esperantRecarrega = async (page: Page, accio: () => Promise<void>) => {
    const dt = waitDatatableResponse(page, DT);
    await accio();
    await dt;
    await esperarDatatable(page, TAULA);
};

// El nom accessible dels enllaços d'Accions comença amb la icona (Font Awesome) i &nbsp;.
const clicarAccioFila = async (fila: Locator, accio: RegExp) => {
    await fila.getByRole('button', { name: /accions|acciones/i }).click();
    await fila.getByRole('link', { name: accio }).click();
};

type DadesAvis = { assumpte: string; missatge: string; inici: string; final: string | null; nivell: 'INFO' | 'WARNING' | 'ERROR' };

const omplirFormulari = async (frame: FrameLocator, dades: DadesAvis) => {
    await frame.locator('input[name="assumpte"]').fill(dades.assumpte);
    await frame.locator('textarea[name="missatge"]').fill(dades.missatge);
    // bootstrap-datepicker torna a escriure el seu valor en perdre el focus: fill no basta.
    // Es fixa la data amb la seva API ('update' amb text buit la buida).
    for (const [camp, valor] of [['#dataInici', dades.inici], ['#dataFinal', dades.final ?? '']] as const) {
        await frame.locator(camp).evaluate((el, v) => {
            const $ = (window as any).$;
            $(el).datepicker('update', v);
            $(el).val(v).trigger('change');
        }, valor);
        await expect(frame.locator(camp)).toHaveValue(valor);
    }
    // El <select> queda ocult per select2; el formulari envia el seu valor.
    await frame.locator('select#avisNivell').selectOption(dades.nivell, { force: true });
};

const crearAvis = async (page: Page, dades: DadesAvis) => {
    logInfo(`  -> crear l'avís "${dades.assumpte}"`);
    await page.locator('a[href="avis/new"]').click();
    const frame = await esperarModalFormulari(page, 'input[name="assumpte"]');
    await omplirFormulari(frame, dades);
    await esperantRecarrega(page, () => enviarModal(page));
    await expectSuccessAlert(page);
};

const obrirModificacio = async (page: Page, assumpte: string): Promise<FrameLocator> => {
    const fila = getFilaAvis(page, assumpte);
    await expect(fila).toHaveCount(1);
    await clicarAccioFila(fila, /^\W*modificar\s*$/i);
    const frame = await esperarModalFormulari(page, 'input[name="assumpte"]');
    await expect(frame.locator('input[name="assumpte"]')).toHaveValue(assumpte);
    return frame;
};

const verificarFormulari = async (frame: FrameLocator, dades: DadesAvis) => {
    await expect.soft(frame.locator('input[name="assumpte"]')).toHaveValue(dades.assumpte);
    await expect.soft(frame.locator('textarea[name="missatge"]')).toHaveValue(dades.missatge);
    await expect.soft(frame.locator('#dataInici')).toHaveValue(dades.inici);
    await expect.soft(frame.locator('#dataFinal')).toHaveValue(dades.final ?? '');
    await expect.soft(frame.locator('select#avisNivell')).toHaveValue(dades.nivell);
};

// Esborrat individual: l'enllaç té data-confirm (confirm() natiu) i recarrega la pàgina.
const esborrarFila = async (page: Page, fila: Locator) => {
    page.once('dialog', dialog => dialog.accept());
    await esperantRecarrega(page, () => clicarAccioFila(fila, /^\W*esborrar\s*$/i));
};

const canviarActiuFila = async (page: Page, assumpte: string, activar: boolean) => {
    await esperantRecarrega(page, () =>
        clicarAccioFila(getFilaAvis(page, assumpte), activar ? /^\W*activar\s*$/i : /^\W*desactivar\s*$/i));
};

// Selecciona exactament les files indicades i executa l'acció del menú "Opcions".
// Salvaguarda: la selecció de la sessió ha d'estar buida abans i ha de valer N després.
const accioMassiva = async (page: Page, assumptes: string[], accio: RegExp) => {
    await expect(getComptador(page), 'la selecció de la sessió havia d\'estar buida').toHaveText('0');
    for (const assumpte of assumptes) {
        const resp = page.waitForResponse(r => r.url().includes('/avis/toggleSelection') && r.status() === 200);
        await getFilaAvis(page, assumpte).locator('td').first().click();
        await resp;
    }
    await expect(getComptador(page)).toHaveText(String(assumptes.length));
    const opcions = page.locator('.dropdown:has(#seleccioCount)');
    await opcions.locator('button').click();
    page.once('dialog', dialog => dialog.accept());
    await esperantRecarrega(page, () => opcions.getByRole('link', { name: accio }).click());
    await expectSuccessAlert(page);
};

const esborrarAvisosDeProva = async (page: Page) => {
    for (let intent = 0; intent < 20; intent++) {
        await anarALlistat(page);
        const files = getFiles(page).filter({ hasText: PREFIX });
        if (await files.count() === 0) {
            logInfo('  -> no queda cap avís de prova');
            return;
        }
        await esborrarFila(page, files.first());
    }
    throw new Error('No s\'han pogut esborrar els avisos de prova');
};

// ─────────────────────────────────────────────────────────────────────────────

test.describe('Prerequisits JSP — rol IPA_SUPER', () => {

    test('ROL ASSEGURAR IPA_SUPER', async ({ page }) => {
        await page.goto(URL_AVISOS);
        await assegurarRolJsp(page, 'IPA_SUPER', URL_AVISOS);
    });

});

test.describe('Gestió d\'Avisos JSP — IPA_SUPER', () => {

    test.beforeEach(async ({ page }) => {
        await anarALlistat(page);
    });

    test('AVIS VISTA', async ({ page }) => {

        await test.step('taula, columnes i enllaç de nou avís', async () => {
            logInfo('  -> taula, columnes i enllaç de nou avís');
            await expect(page.locator('#' + TAULA)).toBeVisible();
            const headers = page.locator(`#${TAULA} thead th`);
            for (const h of [/assumpte|asunto/i, /data inici|fecha inicio/i, /data final|fecha final/i, /activ/i, /nivell|nivel/i]) {
                await expect(headers.filter({ hasText: h }).first()).toBeVisible();
            }
            await expect(page.locator('a[href="avis/new"]')).toBeVisible();
            await expect(getComptador(page)).toBeVisible();
        });

    });

    test('AVIS NETEJA PREVIA', async ({ page }) => {
        await esborrarAvisosDeProva(page);
    });

    test('AVIS CREAR', async ({ page }) => {

        const dades: DadesAvis = { assumpte: ASSUMPTE_CREAR, missatge: MISSATGE_CREAR, ...DATES_CREAR, nivell: 'WARNING' };

        await test.step('crear l\'avís amb tots els camps', async () => {
            await crearAvis(page, dades);
        });

        await test.step('l\'avís apareix al llistat amb les dades', async () => {
            logInfo('  -> l\'avís apareix al llistat amb les dades');
            const fila = getFilaAvis(page, ASSUMPTE_CREAR);
            await expect(fila).toHaveCount(1);
            await expect.soft(getCella(fila, 2)).toHaveText(dades.inici);
            await expect.soft(getCella(fila, 3)).toHaveText(dades.final!);
            await expect.soft(getCella(fila, 4).locator('.fa-check')).toHaveCount(1);
        });

        await test.step('consultar l\'avís i verificar les dades desades', async () => {
            logInfo('  -> consultar l\'avís i verificar les dades desades');
            const frame = await obrirModificacio(page, ASSUMPTE_CREAR);
            await verificarFormulari(frame, dades);
            await cancelarModal(page);
        });

    });

    test('AVIS LLISTAT', async ({ page }) => {
        test.setTimeout(150_000);

        await test.step('crear els avisos de finestra', async () => {
            for (const f of finestres()) {
                await crearAvis(page, {
                    assumpte: assumpteFinestra(f.sufix), missatge: 'Avís de prova: ' + f.sufix,
                    inici: f.inici, final: f.final, nivell: 'INFO',
                });
            }
        });

        await test.step('en recarregar es mostren els vigents i no els futurs ni els caducats', async () => {
            logInfo('  -> bàners després de recarregar');
            await anarALlistat(page);
            for (const f of finestres()) {
                await expect.soft(getBaner(page, assumpteFinestra(f.sufix)), `bàner ${f.sufix}`)
                    .toHaveCount(f.visible ? 1 : 0);
            }
        });

    });

    test('AVIS ACTIVAR', async ({ page }) => {

        const vigent = assumpteFinestra('vigent');
        const massius = [assumpteFinestra('sense final'), assumpteFinestra('avui')];

        await test.step('desactivar i activar un avís amb l\'acció individual', async () => {
            logInfo('  -> individual');
            await expect(getBaner(page, vigent)).toHaveCount(1);
            await canviarActiuFila(page, vigent, false);
            await expect(getCella(getFilaAvis(page, vigent), 4).locator('.fa-check')).toHaveCount(0);
            await expect(getBaner(page, vigent)).toHaveCount(0);
            await canviarActiuFila(page, vigent, true);
            await expect(getCella(getFilaAvis(page, vigent), 4).locator('.fa-check')).toHaveCount(1);
            await expect(getBaner(page, vigent)).toHaveCount(1);
        });

        await test.step('desactivar i activar dos avisos amb l\'acció massiva', async () => {
            logInfo('  -> massiu');
            await accioMassiva(page, massius, /^\W*desactivar\s*$/i);
            for (const a of massius) {
                await expect(getCella(getFilaAvis(page, a), 4).locator('.fa-check')).toHaveCount(0);
                await expect(getBaner(page, a)).toHaveCount(0);
            }
            await accioMassiva(page, massius, /^\W*activar\s*$/i);
            for (const a of massius) {
                await expect(getCella(getFilaAvis(page, a), 4).locator('.fa-check')).toHaveCount(1);
                await expect(getBaner(page, a)).toHaveCount(1);
            }
        });

    });

    // El formulari JSP no té entitat (tots els avisos que s'hi creen són globals). La
    // visibilitat per entitat, també a la interfície JSP, la comprova avis.super.spec.ts.
    test('AVIS ENTITAT', async () => {
        test.skip(true, 'El formulari JSP no té entitat: ho cobreix el test REACT (pantalles REACT i JSP)');
    });

    test('AVIS MODIFICAR', async ({ page }) => {

        const dades: DadesAvis = { assumpte: ASSUMPTE_MODIF, missatge: MISSATGE_MODIF, ...DATES_MODIFICAR, nivell: 'ERROR' };

        await test.step('modificar tots els camps', async () => {
            logInfo('  -> modificar tots els camps');
            const frame = await obrirModificacio(page, ASSUMPTE_CREAR);
            await omplirFormulari(frame, dades);
            await esperantRecarrega(page, () => enviarModal(page));
            await expectSuccessAlert(page);
        });

        await test.step('recarregar i verificar les dades desades', async () => {
            logInfo('  -> recarregar i verificar');
            await anarALlistat(page);
            const frame = await obrirModificacio(page, ASSUMPTE_MODIF);
            await verificarFormulari(frame, dades);
            await cancelarModal(page);
        });

    });

    test('AVIS ESBORRAR', async ({ page }) => {

        await test.step('esborrar l\'avís principal amb l\'acció individual', async () => {
            logInfo('  -> esborrar individual');
            await expect(getFilaAvis(page, ASSUMPTE_MODIF)).toHaveCount(1);
            await esborrarFila(page, getFilaAvis(page, ASSUMPTE_MODIF));
            await expect(getFilaAvis(page, ASSUMPTE_MODIF)).toHaveCount(0);
        });

        await test.step('recarregar la pàgina i verificar que no hi és', async () => {
            await anarALlistat(page);
            await expect(getFilaAvis(page, ASSUMPTE_MODIF)).toHaveCount(0);
        });

    });

    test('AVIS ESBORRAR MASSIU', async ({ page }) => {

        await test.step('esborrar amb l\'acció massiva els avisos de finestra', async () => {
            logInfo('  -> esborrar massiu');
            const assumptes = (await getFiles(page).filter({ hasText: PREFIX })
                .locator('td:nth-child(2)').allTextContents()).map(t => t.trim());
            expect(assumptes.length).toBeGreaterThan(0);
            await accioMassiva(page, assumptes, /^\W*esborrar\s*$/i);
        });

        await test.step('recarregar la pàgina i verificar que no en queda cap', async () => {
            await anarALlistat(page);
            await expect(getFiles(page).filter({ hasText: PREFIX })).toHaveCount(0);
        });

    });

    test('AVIS NETEJA FINAL', async ({ page }) => {
        await esborrarAvisosDeProva(page);
    });

});
