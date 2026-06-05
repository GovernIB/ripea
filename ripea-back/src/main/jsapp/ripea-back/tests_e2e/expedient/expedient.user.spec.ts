import { test, expect, Page, Locator } from '@playwright/test';

import {
    SYSTEM_DELAY,
    logDebug,
    logInfo,
    humanDelay,
    getGrid,
    getRows,
    expectSuccessAlert,
    obrirMenuAccions,
    esperarGridCarregat,
    waitApiGet,
    waitApiEntityLoad,
    seleccionarOpcioAutocompletament,
    assegurarRol,
} from '../utils/reactHelpers';

// ─────────────────────────────────────────────────────────────────────────────
// Gestió d'Expedients REACT  (rol usuari base / tramitador)
//
// Cobreix el cicle de vida d'un expedient des de la pàgina /expedient:
//   - Vista del llistat (graella, barra d'eines, columnes, formulari de filtre)
//   - Filtre + neteja prèvia (esborra restes de proves anteriors)
//   - Creació d'un expedient nou
//   - Modificació del títol
//   - Verificació de la modificació
//   - Detall de l'expedient (/contingut/:id)
//   - Eliminació
//
// Els helpers genèrics (graella, esperes d'API, autocomplete, rol) viuen a
// ../utils/reactHelpers. Aquí només es defineixen els específics d'aquesta pàgina.
// ─────────────────────────────────────────────────────────────────────────────

const URL_EXPEDIENTS = '/ripeaback/reactapp/expedient';

// Prefix comú per a tots els títols de prova: permet netejar restes amb un sol filtre.
const PREFIX_TEST    = 'zz_PLAYWRIGHT_EXP_REACT';
const NOM_TEST       = `${PREFIX_TEST}_zz`;
const NOM_MODIFICAT  = `${PREFIX_TEST}_MOD_zz`;

// ── Helpers específics d'aquesta pàgina ─────────────────────────────────────────

const getToolbar = (page: Page) =>
    page.locator('.MuiToolbar-root').filter({ hasText: /nou expedient|nuevo expediente/i });

// Llistat d'expedients: GET /api/expedients?... (exclou sub-rutes com /artifacts o /{id}).
const esGetLlistat = (url: string) => url.includes('/api/expedients') && !url.includes('/expedients/');

// Filtra el llistat d'expedients pel títol i espera la resposta del llistat.
// El filtre conserva l'estat per defecte "Obert" (estat != TANCAT), de manera que
// els expedients acabats de crear (oberts) sempre hi apareixen.
const filtrarPerTitol = async (page: Page, titol: string) => {
    logDebug('[Filtre] Filtrant per títol="' + titol + '"');
    // Omplir el camp ABANS de registrar el listener i clicar Filtra: així ens assegurem
    // que el valor ja està compromès al formulari i que el GET que esperem és el del
    // botó Filtra (no pas el de la càrrega inicial en muntar-se el filtre).
    await page.locator('input[name="nom"]').fill(titol);
    await humanDelay(page);
    const resp = waitApiGet(page, esGetLlistat);
    await page.locator('.styledFilter').getByRole('button', { name: 'Filtra', exact: true }).click();
    await resp;
    logDebug('[Filtre] Filtre aplicat. Num files resultants: ' + await getRows(page).count());
};

// Esborra una fila d'expedient (obre el menú d'accions, clica Eliminar i confirma).
const eliminarFila = async (page: Page, fila: Locator) => {
    await obrirMenuAccions(fila);
    await page.getByRole('menuitem').filter({ hasText: /eliminar/i }).click();
    const dialog = page.locator('[role="dialog"]');
    await expect(dialog).toBeVisible({ timeout: 5_000 });
    await humanDelay(page);
    await dialog.getByRole('button').filter({ hasText: /accepta|acepta|confirmar|ok/i }).click();
    await expectSuccessAlert(page);
    await esperarGridCarregat(page);
};

// ─────────────────────────────────────────────────────────────────────────────
// Prerequisit: assegurar el rol actiu (bloc propi SENSE beforeEach)
//
// Es declara abans del bloc principal perquè s'executi primer (ordre del fitxer).
// No comparteix el beforeEach del bloc principal: aquí només cal la capçalera per
// llegir/canviar el rol, no cal carregar la graella d'expedients.
// ─────────────────────────────────────────────────────────────────────────────

test.describe('Prerequisits — rol usuari base', () => {

    test('ROL ASSEGURAR USUARI', async ({ page }) => {
        // Navegació mínima per disposar de la capçalera amb el menú d'usuari.
        await page.goto(URL_EXPEDIENTS);
        await assegurarRol(page, 'tothom', URL_EXPEDIENTS);
    });

});

// ─────────────────────────────────────────────────────────────────────────────
// Pàgina: Gestió d'Expedients REACT  (rol usuari base)
// ─────────────────────────────────────────────────────────────────────────────

test.describe('Gestió d\'Expedients — usuari base', () => {

    test.beforeEach(async ({ page }) => {
        logDebug('[beforeEach] Navegant a ' + URL_EXPEDIENTS + '...');
        await page.goto(URL_EXPEDIENTS);
        // En muntar-se, el filtre dispara automàticament la cerca amb l'estat per
        // defecte "Obert"; esperem que la graella acabi la càrrega inicial.
        await esperarGridCarregat(page);
        logDebug('[beforeEach] Càrrega inicial OK.');
    });

    // ── Vista / disposició ──────────────────────────────────────────────────────

    test('EXPEDIENT VISTA', async ({ page }) => {

        await test.step('graella visible', async () => {
            logInfo('  -> graella visible');
            await expect(getGrid(page)).toBeVisible();
        });

        await test.step('barra d\'eines amb "Nou expedient" visible', async () => {
            logInfo('  -> barra d\'eines amb "Nou expedient" visible');
            await expect(getToolbar(page)).toBeVisible();
            await expect(page.getByRole('button').filter({ hasText: /nou expedient|nuevo expediente/i })).toBeVisible();
        });

        await test.step('columnes esperades visibles', async () => {
            logInfo('  -> columnes esperades visibles');
            const headers = page.locator('.MuiDataGrid-columnHeaderTitle');
            await expect(headers.filter({ hasText: /n.mero/i }).first()).toBeVisible();
            await expect(headers.filter({ hasText: /procediment|procedimiento/i }).first()).toBeVisible();
            await expect(headers.filter({ hasText: /t.tol|t.tulo/i }).first()).toBeVisible();
            await expect(headers.filter({ hasText: /estat|estado/i }).first()).toBeVisible();
            await expect(headers.filter({ hasText: /prioritat|prioridad/i }).first()).toBeVisible();
        });

        await test.step('formulari de filtre visible', async () => {
            logInfo('  -> formulari de filtre visible');
            await expect(page.locator('input[name="numero"]')).toBeVisible();
            await expect(page.locator('input[name="nom"]')).toBeVisible();
            await expect(page.locator('.styledFilter').getByRole('button', { name: 'Filtra', exact: true })).toBeVisible();
        });

    });

    // ── Filtre + neteja prèvia ────────────────────────────────────────────────

    test('EXPEDIENT FILTRE', async ({ page }) => {

        // Filtra pel prefix comú (el filtre "nom" és un LIKE → captura NOM_TEST i NOM_MODIFICAT)
        await filtrarPerTitol(page, PREFIX_TEST);

        // Només es consideren les files que contenen el prefix de prova: així mai
        // s'esborra un expedient real, encara que el filtre de servidor no s'hagués
        // aplicat (els expedients reals no tenen el prefix al títol).
        const filesProva = () => getRows(page).filter({ hasText: PREFIX_TEST });

        let count = await filesProva().count();
        logDebug('Expedients de prova existents: ' + count);
        if (count === 0) {
            logInfo("No existeix cap expedient de prova, l'entorn ja és net");
            return;
        }

        logInfo("Existeixen expedients de prova → esborrar-los per deixar l'entorn net");
        // Esborra'ls un a un (la graella es recarrega després de cada esborrat).
        while (count > 0) {
            await humanDelay(page);
            await eliminarFila(page, filesProva().first());
            count = await filesProva().count();
            logDebug('Files de prova restants: ' + count);
        }
    });

    // ── Creació ─────────────────────────────────────────────────────────────────

    test('EXPEDIENT CREAR', async ({ page }) => {

        await test.step('obrir formulari de nou expedient', async () => {
            logInfo('  -> obrir formulari de nou expedient');
            await page.getByRole('button').filter({ hasText: /nou expedient|nuevo expediente/i }).click();
            await expect(page.locator('[role="dialog"]')).toBeVisible();
        });

        await test.step('omplir el formulari', async () => {
            logInfo('  -> omplir el formulari');
            const dialog = page.locator('[role="dialog"]');

            // Procediment o servei: primer disponible. En seleccionar-lo, l'òrgan gestor
            // i la seqüència s'omplen automàticament; la prioritat queda "Normal" per defecte.
            await seleccionarOpcioAutocompletament(page, dialog, '[name="metaExpedient"] input[type="text"]');
            // Esperar que l'òrgan gestor s'hagi autoemplenat abans de continuar.
            await expect(dialog.locator('[name="organGestor"] input[type="text"]')).not.toHaveValue('', { timeout: 10_000 });

            await dialog.locator('input[name="nom"]').fill(NOM_TEST);
        });

        await test.step('enviar el formulari', async () => {
            logInfo('  -> enviar el formulari');
            await page.waitForTimeout(SYSTEM_DELAY);
            await page.locator('[role="dialog"]').getByRole('button').filter({ hasText: /guarda/i }).click();
        });

        await test.step('verificar missatge d\'èxit', async () => {
            logInfo('  -> verificar missatge d\'èxit');
            await expectSuccessAlert(page);
        });

        await test.step('verificar que l\'expedient apareix al llistat', async () => {
            logInfo('  -> verificar que l\'expedient apareix al llistat');
            await filtrarPerTitol(page, NOM_TEST);
            await expect(getRows(page).filter({ hasText: NOM_TEST })).toHaveCount(1);
        });

    });

    // ── Modificació ─────────────────────────────────────────────────────────────

    test('EXPEDIENT MODIFICAR', async ({ page }) => {

        await test.step('filtrar i obrir modal de modificació', async () => {
            logInfo('  -> filtrar i obrir modal de modificació');
            await filtrarPerTitol(page, NOM_TEST);
            const fila = getRows(page).filter({ hasText: NOM_TEST });
            await expect(fila).toHaveCount(1);
            const rowId = await fila.getAttribute('data-id');
            // Registrar ABANS de clicar: en obrir la modal, Form.tsx fa apiGetOne(id) i
            // reset(entityData) renderitza els camps. Esperem la resposta abans d'omplir.
            const entityResp = waitApiEntityLoad(page, rowId);
            await humanDelay(page);
            await obrirMenuAccions(fila);
            await humanDelay(page);
            await page.getByRole('menuitem').filter({ hasText: /modificar/i }).click();
            await expect(page.locator('[role="dialog"]')).toBeVisible();
            await entityResp;
        });

        await test.step('modificar el títol', async () => {
            logInfo('  -> modificar el títol');
            const dialog = page.locator('[role="dialog"]');
            const nomField = dialog.locator('input[name="nom"]');
            await expect(nomField).toBeVisible({ timeout: 3_000 });
            await nomField.fill(NOM_MODIFICAT);
        });

        await test.step('guardar la modificació', async () => {
            logInfo('  -> guardar la modificació');
            await page.waitForTimeout(SYSTEM_DELAY);
            // El botó del diàleg de modificació és "Modifica" (CA) / "Modificar" (ES).
            await page.locator('[role="dialog"]').getByRole('button').filter({ hasText: /modifica/i }).click();
        });

        await test.step('verificar missatge d\'èxit', async () => {
            logInfo('  -> verificar missatge d\'èxit');
            await expectSuccessAlert(page);
        });

    });

    // ── Verificació de la modificació ───────────────────────────────────────────

    test('EXPEDIENT VERIFICAR MODIFICACIO', async ({ page }) => {

        await test.step('filtrar pel nou títol i verificar resultat', async () => {
            logInfo('  -> filtrar pel nou títol i verificar resultat');
            await filtrarPerTitol(page, NOM_MODIFICAT);
            await expect(getRows(page).filter({ hasText: NOM_MODIFICAT })).toHaveCount(1);
        });

    });

    // ── Detall de l'expedient ─────────────────────────────────────────────────────

    test('EXPEDIENT DETALL', async ({ page }) => {

        await test.step('filtrar i obrir el detall de l\'expedient', async () => {
            logInfo('  -> filtrar i obrir el detall de l\'expedient');
            await filtrarPerTitol(page, NOM_MODIFICAT);
            const fila = getRows(page).filter({ hasText: NOM_MODIFICAT });
            await expect(fila).toHaveCount(1);
            const id = await fila.getAttribute('data-id');
            await page.goto(`/ripeaback/reactapp/contingut/${id}`);
        });

        await test.step('verificar la capçalera amb el títol', async () => {
            logInfo('  -> verificar la capçalera amb el títol');
            await expect(page.getByRole('heading', { name: NOM_MODIFICAT })).toBeVisible({ timeout: 10_000 });
        });

        await test.step('verificar el panell d\'informació de l\'expedient', async () => {
            logInfo('  -> verificar el panell d\'informació de l\'expedient');
            await expect(page.getByText(/informaci. de l.expedient|informaci.n del expediente/i)).toBeVisible();
        });

        await test.step('verificar les pestanyes del detall', async () => {
            logInfo('  -> verificar les pestanyes del detall');
            await expect(page.locator('[role="tablist"]')).toBeVisible({ timeout: 10_000 });
            await expect(page.getByRole('tab').filter({ hasText: /contingut|contenido/i })).toBeVisible();
            await expect(page.getByRole('tab').filter({ hasText: /dades|datos/i })).toBeVisible();
            await expect(page.getByRole('tab').filter({ hasText: /interessats|interesados/i })).toBeVisible();
            await expect(page.getByRole('tab').filter({ hasText: /tasques|tareas/i })).toBeVisible();
        });

    });

    // ── Eliminació ────────────────────────────────────────────────────────────────

    test('EXPEDIENT ELIMINAR', async ({ page }) => {

        await test.step('filtrar i eliminar l\'expedient de prova', async () => {
            logInfo('  -> filtrar i eliminar l\'expedient de prova');
            await filtrarPerTitol(page, NOM_MODIFICAT);
            const fila = getRows(page).filter({ hasText: NOM_MODIFICAT });
            await expect(fila).toHaveCount(1);
            await eliminarFila(page, fila);
        });

        await test.step('verificar que l\'expedient s\'ha eliminat', async () => {
            logInfo('  -> verificar que l\'expedient s\'ha eliminat');
            await filtrarPerTitol(page, NOM_MODIFICAT);
            await expect(getRows(page).filter({ hasText: NOM_MODIFICAT })).toHaveCount(0);
        });

    });

});
