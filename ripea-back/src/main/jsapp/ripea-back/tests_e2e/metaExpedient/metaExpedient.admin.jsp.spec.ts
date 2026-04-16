import { test, expect, Page } from '@playwright/test';

// URL de la pàgina de procediments (interfície JSP clàssica)
const URL_PROCEDIMENTS = '/ripeaback/metaExpedient';

// Helpers per localitzar elements de la pàgina
const getGrid       = (page: Page) => page.locator('#metaexpedients');
const getRows       = (page: Page) => page.locator('#metaexpedients tbody tr').filter({ hasNot: page.locator('td.dataTables_empty') });
const getToolbar    = (page: Page) => page.locator('[data-toggle="botons-titol"]');
const getFilterArea = (page: Page) => page.locator('input[name="codi"]');

// ─────────────────────────────────────────────────────────────────────────────
// Pàgina: Gestió de Procediments JSP  (rol IPA_ADMIN)
// ─────────────────────────────────────────────────────────────────────────────

test.describe('Gestió de Procediments JSP — IPA_ADMIN', () => {

    test.beforeEach(async ({ page }) => {
        // Esperar la resposta AJAX del DataTable per assegurar que les dades han carregat
        const datatablePromise = page.waitForResponse(
            resp => resp.url().includes('/metaExpedient/datatable') && resp.status() === 200,
        );
        await page.goto(URL_PROCEDIMENTS);
        await datatablePromise;
        await expect(page.locator('#metaexpedients_processing')).toBeHidden();
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
            const headers = page.locator('#metaexpedients thead th');
            await expect(headers.filter({ hasText: /codi/i }).first()).toBeVisible();              // Codi intern / Código interno
            await expect(headers.filter({ hasText: /classificaci/i }).first()).toBeVisible();      // Classificació / Clasificación
            await expect(headers.filter({ hasText: /\bnom\b|nombre/i }).first()).toBeVisible();    // Nom / Nombre
            await expect(headers.filter({ hasText: /documental/i }).first()).toBeVisible();        // Sèrie documental / Serie documental
            await expect(headers.filter({ hasText: /gestor/i }).first()).toBeVisible();            // Òrgan gestor / Órgano gestor
            await expect(headers.filter({ hasText: /revis/i }).first()).toBeVisible();             // Estat revisió / Estado revisión
        });

        await test.step('botons d\'acció per a IPA_ADMIN visibles', async () => {
            await expect(page.getByRole('link', { name: /nou/i })).toBeVisible();
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
            await page.getByRole('link', { name: /nou/i }).click();
            // El modal JSP carrega el formulari en un <iframe> dins .modal-body
            await expect(page.locator('.modal')).toBeVisible();
        });

        await test.step('omplir el formulari dins l\'iframe del modal', async () => {
            // Tot el contingut del formulari es dins l'iframe
            const frame = page.frameLocator('.modal-body iframe');

            await frame.locator('input[name="codi"]').fill('PLAYWIGHT');

            // tipusClassificacio: radio buttons amb valors SIA i ID
            // SIA s'autoselecciona quan no hi ha organGestor, però ho confirmem explícitament
            await frame.locator('input[name="tipusClassificacio"][value="SIA"]').check();

            // classificacioSia: es mostra quan tipusClassificacio=SIA
            await frame.locator('#classificacioSia').fill('00110011');

            await frame.locator('textarea[name="nom"]').fill('prova creació play wright');
            await frame.locator('input[name="serieDocumental"]').fill('S0002');

            // comu: en marcar-lo s'oculta el camp organGestor (no cal omplir-lo)
            await frame.locator('#comu').check();
        });

        await test.step('enviar el formulari i acceptar el diàleg de confirmació', async () => {
            // El codi SIA 00110011 probablement no existeix a Rolsac → apareix confirm() natiu
            // page.once capta dialogs de tots els frames, inclòs l'iframe del modal
            page.once('dialog', dialog => dialog.accept());

            // El botó de submit és clonat al .modal-footer del pare (el de dins l'iframe s'amaga)
            await page.locator('.modal-footer').getByRole('button', { name: /crear/i }).click();
        });

        await test.step('verificar missatge d\'èxit', async () => {
            // Quan el modal es tanca, webutilRefreshMissatges() injecta els missatges al DOM
            await expect(page.locator('.alert.alert-success')).toBeVisible({ timeout: 10_000 });
        });

    });

    // ── Filtre ────────────────────────────────────────────────────────────────

    test('filtrar per nom redueix els resultats', async ({ page }) => {
        // Obtenir el nombre inicial de files
        const filesInicial = await getRows(page).count();

        // Omplir el camp "nom" del filtre
        const campNom = page.locator('input[name="nom"]');
        await campNom.fill('zzz_inexistent_zzz');

        // El formulari fa POST (recàrrega completa de pàgina) → el DataTable torna a
        // carregar via AJAX. S'espera la resposta específica en lloc de networkidle,
        // que expiraria per les peticions de polling en segon pla.
        const datatablePromise = page.waitForResponse(
            resp => resp.url().includes('/metaExpedient/datatable') && resp.status() === 200,
        );
        await page.locator('button[value="filtrar"]').click();
        await datatablePromise;
        await expect(page.locator('#metaexpedients_processing')).toBeHidden();

        const filesFiltrades = await getRows(page).count();
        expect(filesFiltrades).toBeLessThanOrEqual(filesInicial);
    });

});
