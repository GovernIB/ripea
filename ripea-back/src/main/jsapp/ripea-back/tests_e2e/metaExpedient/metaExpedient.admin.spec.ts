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

	// ── Filtre ────────────────────────────────────────────────────────────────

	test('filtrar per codi del test i esborrar si existeix', async ({ page }) => {
	    const campCodi = page.locator('input[name="codi"]');
	    await campCodi.fill('PLAYWRIGHT_REACT');

	    const responsePromise = page.waitForResponse(
	        resp => resp.url().includes('/metaExpedient') && resp.request().method() === 'GET' && resp.status() === 200
	    );
	    await page.getByRole('button', { name: /filtrar/i }).click();
	    await responsePromise;

	    const filesFiltrades = await getRows(page).count();
	    if (filesFiltrades === 0) {
	        return;
	    }

	    const row = getRows(page).first();
	    await row.getByRole('menuitem').click();
	    await page.getByRole('menuitem', { name: /esborrar/i }).click();
	    await page.getByRole('button', { name: /acceptar/i }).click();
	    await expect(page.locator('.MuiAlert-standardSuccess')).toBeVisible({ timeout: 10_000 });
	});
	
    // ── Creació ───────────────────────────────────────────────────────────────

    test('creació d\'un nou procediment', async ({ page }) => {

        await test.step('obrir formulari de nou procediment', async () => {
            await page.getByRole('button', { name: /nou procediment|nuevo procediment|new/i }).click();
            await expect(page.locator('[role="dialog"]')).toBeVisible();
        });

        await test.step('omplir el formulari', async () => {
            const dialog = page.locator('[role="dialog"]');

            await dialog.locator('input[name="codi"]').fill('PLAYWRIGHT_REACT');

            // tipusClassificacio: seleccionar ID
            await dialog.getByLabel(/tipus classificaci/i).click();
            await page.getByRole('option', { name: /^ID$/i }).click();
            // classificacio: amb tipusClassificacio=ID el camp és readonly (s'omple automàticament)

            await dialog.locator('input[name="nom"], textarea[name="nom"]').fill('prova creació play wright');
            await dialog.locator('input[name="serieDocumental"]').fill('S0002');

            // procedimentComu: ha d'estar desmarcat
            const checkComu = dialog.locator('input[name="procedimentComu"]');
            if (await checkComu.isChecked()) {
                await checkComu.uncheck();
            }

            // crearReglaDistribucio: ha d'estar desmarcat; el servei de Distribució pot no
            // estar disponible i causaria un error de transacció en desar.
            // L'element és un checkbox MUI sense atribut name → s'usa getByRole.
            const checkRegla = dialog.getByRole('checkbox', { name: /crear regla/i });
            if (await checkRegla.count() > 0 && await checkRegla.isChecked()) {
                await checkRegla.uncheck();
            }

            // organGestor: el label és "Òrgan gestor" (Ò acentuada); s'usa el name de l'input
            // perquè getByLabel amb /i no captura accents Unicode
            await dialog.locator('[name="organGestor"]').click();
            await page.getByRole('option').first().click();
        });

        await test.step('enviar el formulari', async () => {
            await page.locator('[role="dialog"]').getByRole('button', { name: /desar|guarda|guardar|save/i }).click();
        });

        await test.step('verificar missatge d\'èxit', async () => {
            await expect(page.locator('.MuiAlert-standardSuccess')).toBeVisible({ timeout: 10_000 });
        });

    });

});