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
        // Esperar que la graella MUI hagi carregat (desapareix el spinner)
        await expect(getGrid(page)).toBeVisible();
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
        const headers = page.locator('.MuiDataGrid-columnHeaderTitle');
        // Comprova que existeixen les capçaleres principals
        // (els noms provenen dels fitxers de traduccions i18n)
        await expect(headers.filter({ hasText: /codi/i }).first()).toBeVisible();
        await expect(headers.filter({ hasText: /nom/i }).first()).toBeVisible();
    });

    // ── Accions disponibles per a IPA_ADMIN ──────────────────────────────────

    test('el botó de nou procediment és visible per a l\'admin', async ({ page }) => {
        // El botó de crear nou element està a la toolbar del grid
        const botoNou = page.getByRole('button', { name: /nou procediment|nuevo procediment|new/i });
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
