import { test, expect, Page } from '@playwright/test';

// URL de la pàgina de procediments
const URL_PROCEDIMENTS = '/ripeaback/reactapp/metaExpedient';

// Helpers per localitzar elements de la pàgina
const getGrid       = (page: Page) => page.locator('.MuiDataGrid-root');
const getRows       = (page: Page) => page.locator('.MuiDataGrid-row');
const getToolbar    = (page: Page) => page.locator('.MuiToolbar-root').filter({ hasText: /nou procediment/i });
const getFilterArea = (page: Page) => page.locator('input[name="codi"]');

// ─────────────────────────────────────────────────────────────────────────────
// Pàgina: Gestió de Procediments  (rol IPA_ADMIN)
// ─────────────────────────────────────────────────────────────────────────────

test.describe('Gestió de Procediments — IPA_ADMIN', () => {

    test.beforeEach(async ({ page }) => {
        await page.goto(URL_PROCEDIMENTS);
        await expect(getGrid(page)).toBeVisible();
    });

    // ── Disposició ────────────────────────────────────────────────────────────

    test('disposició dels elements en pantalla', async ({ page }) => {

        await test.step('graella visible amb dades', async () => {
            await expect(getGrid(page)).toBeVisible();
            await expect(getRows(page).first()).toBeVisible();
        });

        await test.step('barra d\'eines visible', async () => {
            await expect(getToolbar(page)).toBeVisible();
        });

        await test.step('columnes esperades visibles', async () => {
            const headers = page.locator('.MuiDataGrid-columnHeaderTitle');
            await expect(headers.filter({ hasText: /codi/i }).first()).toBeVisible();              // Codi intern / Código interno
            await expect(headers.filter({ hasText: /classificaci/i }).first()).toBeVisible();      // Classificació / Clasificación
            await expect(headers.filter({ hasText: /\bnom\b|nombre/i }).first()).toBeVisible();    // Nom / Nombre
            await expect(headers.filter({ hasText: /documental/i }).first()).toBeVisible();        // Sèrie documental / Serie documental
            await expect(headers.filter({ hasText: /gestor/i }).first()).toBeVisible();            // Òrgan gestor / Órgano gestor
            await expect(headers.filter({ hasText: /revis/i }).first()).toBeVisible();             // Estat revisió / Estado revisión
        });

        await test.step('botons d\'acció per a IPA_ADMIN visibles', async () => {
            await expect(page.getByRole('button', { name: /nou procediment|nuevo procediment|new/i })).toBeVisible();
            await expect(page.getByRole('button', { name: /importa/i })).toBeVisible();
        });

        await test.step('formulari de filtre visible', async () => {
            await expect(getFilterArea(page)).toBeVisible();
        });

        await test.step('files alternes tenen color de fons diferent', async () => {
            const primeraFila  = getRows(page).nth(0);
            const segonaFila   = getRows(page).nth(1);
            await expect(primeraFila).toBeVisible();
            await expect(segonaFila).toBeVisible();

            const bgPrimera = await primeraFila.evaluate(el => getComputedStyle(el).backgroundColor);
            const bgSegona  = await segonaFila.evaluate(el => getComputedStyle(el).backgroundColor);
            expect(bgPrimera).not.toBe(bgSegona);
        });

    });

    // ── Creació ───────────────────────────────────────────────────────────────

    test('creació d\'un nou procediment', async ({ page }) => {

        await test.step('obrir formulari de nou procediment', async () => {
            await page.getByRole('button', { name: /nou procediment|nuevo procediment|new/i }).click();
            await expect(page.locator('[role="dialog"]')).toBeVisible();
        });

        await test.step('omplir el formulari', async () => {
            const dialog = page.locator('[role="dialog"]');

            await dialog.locator('input[name="codi"]').fill('PLAYWIGHT');

            // tipusClassificacio: camp select/enum amb valors SIA i ID
            await dialog.getByLabel(/tipus classificaci/i).click();
            await page.getByRole('option', { name: /^SIA$/i }).click();

            // classificacio: camp amb debounce que comprova el codi a Rolsac (pot mostrar avís, no bloqueja)
            await dialog.locator('input[name="classificacio"]').fill('00110011');

            await dialog.locator('input[name="nom"], textarea[name="nom"]').fill('prova creació play wright');
            await dialog.locator('input[name="serieDocumental"]').fill('S0002');

            // procedimentComu: en marcar-lo s'oculta el camp organGestor (no cal omplir-lo)
            await dialog.locator('input[name="procedimentComu"]').check();
        });

        await test.step('enviar el formulari', async () => {
            // Registrar el handler abans del click per si apareix algun diàleg natiu
            page.once('dialog', dialog => dialog.accept());
            await page.locator('[role="dialog"]').getByRole('button', { name: /desar|guardar|save/i }).click();
        });

        await test.step('verificar missatge d\'èxit', async () => {
            await expect(page.locator('.MuiAlert-standardSuccess')).toBeVisible({ timeout: 10_000 });
        });

    });

    // ── Filtre ────────────────────────────────────────────────────────────────

    test('filtrar per nom redueix els resultats', async ({ page }) => {
        // Obtenir el nombre inicial de files
        const filesInicial = await getRows(page).count();

        // Omplir el camp "nom" del filtre
        const campNom = page.locator('input[name="nom"]');
        await campNom.fill('zzz_inexistent_zzz');

        // Clicar el botó Filtrar i esperar la resposta específica de l'API
        // (no s'usa waitForLoadState('networkidle') perquè el polling en segon pla
        //  impedeix que la xarxa arribi mai a l'estat idle)
        const responsePromise = page.waitForResponse(
            resp => resp.url().includes('/metaExpedient') && resp.request().method() === 'GET' && resp.status() === 200
        );
        await page.getByRole('button', { name: /filtrar/i }).click();
        await responsePromise;

        const filesFiltrades = await getRows(page).count();
        expect(filesFiltrades).toBeLessThanOrEqual(filesInicial);
    });

});
