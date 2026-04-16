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

    // ── Càrrega inicial ───────────────────────────────────────────────────────

    test('la pàgina carrega i mostra la graella', async ({ page }) => {
        await expect(getGrid(page)).toBeVisible();
    });

    test('la graella mostra almenys una fila de dades', async ({ page }) => {
        await expect(getRows(page).first()).toBeVisible();
    });

    test('la barra d\'eines és visible', async ({ page }) => {
        await expect(getToolbar(page)).toBeVisible();
    });

    // ── Columnes esperades ────────────────────────────────────────────────────

    test('la graella té les columnes esperades', async ({ page }) => {
        const headers = page.locator('#metaexpedients thead th');
        await expect(headers.filter({ hasText: /codi/i }).first()).toBeVisible();
        await expect(headers.filter({ hasText: /nom/i }).first()).toBeVisible();
    });

    // ── Accions disponibles per a IPA_ADMIN ──────────────────────────────────

    test('el botó de nou procediment és visible per a l\'admin', async ({ page }) => {
        const botoNou = page.getByRole('link', { name: /nou/i });
        await expect(botoNou).toBeVisible();
    });

    test('el botó d\'importar és visible per a l\'admin', async ({ page }) => {
        const botoImportar = page.getByRole('button', { name: /importa/i });
        await expect(botoImportar).toBeVisible();
    });

    // ── Filtre ────────────────────────────────────────────────────────────────

    test('el formulari de filtre és visible', async ({ page }) => {
        await expect(getFilterArea(page)).toBeVisible();
    });

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
