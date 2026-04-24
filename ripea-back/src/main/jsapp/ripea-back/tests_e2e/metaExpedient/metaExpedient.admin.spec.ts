import { test, expect, Page, Locator } from '@playwright/test';

const DEBUG_ACTIVAT	= true;
const HUMAN_DELAY	= 2000; //milisegons de retard entre execució de accions

const URL_PROCEDIMENTS = '/ripeaback/reactapp/metaExpedient';

const CODI_TEST      = 'zz_PLAYWRIGHT_REACT_zz';
const NOM_MODIFICAT  = 'prova modificació playwright react';
const DESC_MODIFICADA = 'descripció de prova per playwright react';

const CODI_DOC1      = 'DOC_PW_REACT_01';
const NOM_DOC1       = 'document tipus doc pw react 1';
const CODI_DOC2      = 'DOC_PW_REACT_02';
const NOM_DOC1_MOD   = 'doc modificat pw react 1';
const DESC_DOC1_MOD  = 'descripció modificada doc 1 react';

const CODI_META1     = 'meTADPWREACT01';
const NOM_META1      = 'meta-dada pw react 1';
const CODI_META2     = 'meTADPWREACT02';
const NOM_META1_MOD  = 'meta-dada modificada pw react 1';
const DESC_META1_MOD = 'descripció modificada meta-dada 1 react';

const CODI_TASCA1    = 'tscPWREACT01';
const NOM_TASCA1     = 'tasca pw react 1';
const CODI_TASCA2    = 'tscPWREACT02';
const NOM_TASCA1_MOD = 'tasca modificada pw react 1';
const DESC_TASCA1_MOD = 'descripció modificada tasca 1 react';

// ── Helpers generals ──────────────────────────────────────────────────────────

const getGrid               = (page: Page) => page.locator('.MuiDataGrid-root').first();
const getRows               = (page: Page) => page.locator('.MuiDataGrid-row');
const getToolbar            = (page: Page) => page.locator('.MuiToolbar-root').filter({ hasText: /nou procediment/i });
const expectSuccessAlert    = (page: Page) => expect(page.locator('.MuiAlert-standardSuccess')).toBeVisible({ timeout: 10_000 });

const humanDelay = async (page: Page) => { 
    if (HUMAN_DELAY>0) { 
        logDebug('Esperant ' + HUMAN_DELAY + 'ms'); 
        await page.waitForTimeout(HUMAN_DELAY);
    }
};

const waitApiGet = async (page: Page, urlFragment: string) => {
    logDebug('Esperant resposta GET a' + urlFragment);
    await page.waitForResponse(
      resp =>
        resp.url().includes(urlFragment) &&
        resp.request().method() === 'GET' &&
        resp.status() === 200,
      { timeout: 15_000 }
    );
    logDebug('Esperant spinner de MUI...');
    // Espera a que desaparezca el spinner de MUI --> las filas del datatable estan renderizadas
    await page.waitForSelector('.MuiCircularProgress-root', {
      state: 'detached',
      timeout: 15_000,
    });
    logDebug('Loading ha desaparegut, esta carregant la info...');
    const count = await getRows(page).count();
    logDebug('Num files: ' + count);
};

// Interacciona amb un MUI Select (native input ocult) i selecciona una opció per text/regex
const triaMuiSelect = async (page: Page, container: Locator, inputName: string, matcher: string | RegExp) => {
    const muiRoot = container.locator(`.MuiInputBase-root:has(input[name="${inputName}"])`);
    await muiRoot.locator('[role="combobox"]').click();
    await page.waitForSelector('[role="listbox"]', { timeout: 5_000 });
    await page.getByRole('option', { name: matcher }).first().click();
    await page.waitForSelector('[role="listbox"]', { state: 'detached', timeout: 3_000 }).catch(() => {});
};

// Selecciona la primera opció no buida d'un MUI Select
const triaMuiSelectFirst = async (page: Page, container: Locator, inputName: string) => {
    const muiRoot = container.locator(`.MuiInputBase-root:has(input[name="${inputName}"])`);
    await muiRoot.locator('[role="combobox"]').click();
    await page.waitForSelector('[role="listbox"]', { timeout: 5_000 });
    const opts = page.getByRole('option');
    const firstText = await opts.first().textContent();
    if (!firstText || firstText.trim() === '') {
        await opts.nth(1).click();
    } else {
        await opts.first().click();
    }
    await page.waitForSelector('[role="listbox"]', { state: 'detached', timeout: 3_000 }).catch(() => {});
};

// Filtra la llista principal per codi intern i espera la resposta de l'API
const aplicarFiltreProcediments = async (page: Page, codi: string, permisDirecte: boolean) => {
	
    //Primer de tot neteja el filtre de nom per evitar contaminació entre tests, ja que el filtre es guarda en sessió i es comparteix entre tests
    //await page.getByRole('button', { name: 'Netejar', exact: true }).click();
    //await waitApiGet(page, '/metaExpedient');
	//await humanDelay(page);
    await page.locator('input[name="codi"]').fill(codi);
    if (permisDirecte) {
        await page.getByRole('button', { name: /amb permis directe/i }).click();
    }
    await page.getByRole('button', { name: 'Filtrar', exact: true }).click();
    await humanDelay(page);
	//Esperam a que el grid carregui amb el filtre aplicat
	const resp = waitApiGet(page, '/metaExpedient');
    await resp;
};

// Navega a la sub-pàgina del procediment de test i activa la pestanya indicada
const anarASubPagina = async (page: Page, tabId: 'metaDocument' | 'metaDada' | 'tasca'): Promise<void> => {
    await aplicarFiltreProcediments(page, CODI_TEST, false);
    await expect(getRows(page)).toHaveCount(1);
    const id = await getRows(page).first().getAttribute('data-id');
    await page.goto(`${URL_PROCEDIMENTS}/${id}/metaDocument`);
    await expect(page.locator('[role="tablist"]')).toBeVisible({ timeout: 10_000 });

    const TAB_LABEL: Record<string, RegExp> = {
        metaDocument: /tipus de doc/i,
        metaDada:     /meta-dades/i,
        tasca:        /tasques/i,
    };

    if (tabId !== 'metaDocument') {
        await page.getByRole('tab', { name: TAB_LABEL[tabId] }).click();
        await expect(page.locator(`#simple-tabpanel-${tabId}:not([hidden])`)).toBeVisible({ timeout: 5_000 });
    }
    await expect(page.locator(`#simple-tabpanel-${tabId} .MuiDataGrid-root`)).toBeVisible({ timeout: 10_000 });
};

const logDebug = (message: string) => { if (DEBUG_ACTIVAT) { console.log(message); } };
const logInfo  = (message: string) => { console.log(message); };

// ── Helpers per a Tipus de Documents ─────────────────────────────────────────

const getDocRows = (page: Page) =>
    page.locator('#simple-tabpanel-metaDocument .MuiDataGrid-row');

const quickFilterDocs = async (page: Page, text: string) => {
    await page.locator('#simple-tabpanel-metaDocument .MuiToolbar-root input[type="text"]').fill(text);
};

const crearDocument = async (page: Page, codi: string, nom: string) => {
    await page.getByRole('button', { name: /nou tipus de document/i }).click();
    const dialog = page.locator('[role="dialog"]');
    await expect(dialog).toBeVisible({ timeout: 5_000 });
    await dialog.locator('input[name="codi"]').fill(codi);
    await dialog.locator('input[name="nom"]').fill(nom);
    // Pestanya Dades NTI: camps requerits per NTI — cal fer scope al dialog, no a la pàgina
    await dialog.getByRole('tab', { name: /dades nti/i }).click();
    await expect(dialog.locator('.MuiInputBase-root:has(input[name="ntiOrigen"])')).toBeVisible({ timeout: 5_000 });
    await triaMuiSelectFirst(page, dialog, 'ntiOrigen');
    await triaMuiSelectFirst(page, dialog, 'ntiTipoDocumental');
    await triaMuiSelectFirst(page, dialog, 'ntiEstadoElaboracion');
    await dialog.getByRole('button', { name: /guarda/i }).click();
    await expectSuccessAlert(page);
};

// ── Helpers per a Meta-dades ──────────────────────────────────────────────────

const getMetaRows = (page: Page) =>
    page.locator('#simple-tabpanel-metaDada .MuiDataGrid-row');

const quickFilterMeta = async (page: Page, text: string) => {
    await page.locator('#simple-tabpanel-metaDada .MuiToolbar-root input[type="text"]').fill(text);
};

const crearMetaDada = async (page: Page, codi: string, nom: string, full = false) => {
    await page.getByRole('button', { name: /nova metadada/i }).click();
    const dialog = page.locator('[role="dialog"]');
    await expect(dialog).toBeVisible({ timeout: 5_000 });
    await dialog.locator('input[name="codi"]').fill(codi);
    await dialog.locator('input[name="nom"]').fill(nom);
    if (full) {
        await triaMuiSelect(page, dialog, 'multiplicitat', /0 a n/i);
        await dialog.locator('textarea[name="descripcio"]').fill('descripció meta-dada completa 2');
        const enviable = dialog.locator('input[name="enviable"]');
        if (!await enviable.isChecked()) await enviable.click();
        await expect(dialog.locator('input[name="metadadaArxiu"]')).toBeVisible();
        await dialog.locator('input[name="metadadaArxiu"]').fill('metadada_arxiu_pw_2');
    }
    await dialog.getByRole('button', { name: /guarda/i }).click();
    await expectSuccessAlert(page);
};

// ── Helpers per a Tasques ─────────────────────────────────────────────────────

const getTascaRows = (page: Page) =>
    page.locator('#simple-tabpanel-tasca .MuiDataGrid-row');

const quickFilterTasques = async (page: Page, text: string) => {
    await page.locator('#simple-tabpanel-tasca .MuiToolbar-root input[type="text"]').fill(text);
};

const seleccionarResponsable = async (page: Page, dialog: Locator) => {
    const responsableInput = dialog.locator('[name="responsable"] input[type="text"]');
    await responsableInput.click();
    await responsableInput.fill('adm');
    // Esperar que les opcions de l'autocomplete apareguin
    await page.waitForSelector('[role="listbox"]', { timeout: 5_000 });
    const primerResultat = page.getByRole('option').first();
    await primerResultat.waitFor({ timeout: 5_000 });
    await primerResultat.click();
    await page.waitForSelector('[role="listbox"]', { state: 'detached', timeout: 3_000 }).catch(() => {});
};

const crearTasca = async (page: Page, codi: string, nom: string, full = false) => {
    await page.getByRole('button', { name: /nova tasca/i }).click();
    const dialog = page.locator('[role="dialog"]');
    await expect(dialog).toBeVisible({ timeout: 5_000 });
    await dialog.locator('input[name="codi"]').fill(codi);
    await dialog.locator('input[name="nom"]').fill(nom);
    await seleccionarResponsable(page, dialog);
    await dialog.locator('textarea[name="descripcio"]').fill(full ? 'descripció tasca completa 2' : 'descripció tasca 1');
    if (full) {
        await dialog.locator('input[name="duracio"]').clear();
        await dialog.locator('input[name="duracio"]').fill('5');
        await triaMuiSelect(page, dialog, 'prioritat', /alta/i);
    }
    await dialog.getByRole('button', { name: /guarda/i }).click();
    await expectSuccessAlert(page);
};

// ─────────────────────────────────────────────────────────────────────────────
// Pàgina: Gestió de Procediments REACT  (rol IPA_ADMIN)
// ─────────────────────────────────────────────────────────────────────────────

test.describe('Gestió de Procediments — IPA_ADMIN', () => {

    test.beforeEach(async ({ page }) => {
        await page.goto(URL_PROCEDIMENTS);
        //await expect(getGrid(page)).toBeVisible({ timeout: 15_000 });
        // Netejar filtres guardats en sessió per evitar contaminació entre tests
        // "Netejar selecció" és un altre botó de la DataGrid → cal exact: true
        const respCarregaInicial = waitApiGet(page, '/metaExpedient');
        await respCarregaInicial;
        //await humanDelay(page);
        await page.getByRole('button', { name: 'Netejar', exact: true }).click();
        const respCarregaReset = waitApiGet(page, '/metaExpedient');
        await respCarregaReset;
    });

    // ── Disposició ────────────────────────────────────────────────────────────

    test('disposició dels elements en pantalla', async ({ page }) => {

        await test.step('graella visible amb dades', async () => {
            logInfo('  -> graella visible amb dades');
            await expect(getGrid(page)).toBeVisible();
            await expect(getRows(page).first()).toBeVisible();
        });

        await test.step('barra d\'eines visible', async () => {
            logInfo('  -> barra d\'eines visible');
            await expect(getToolbar(page)).toBeVisible();
        });

        await test.step('columnes esperades visibles', async () => {
            logInfo('  -> columnes esperades visibles');
            const headers = page.locator('.MuiDataGrid-columnHeaderTitle');
            await expect(headers.filter({ hasText: /codi/i }).first()).toBeVisible();
            await expect(headers.filter({ hasText: /classificaci/i }).first()).toBeVisible();
            await expect(headers.filter({ hasText: /\bnom\b|nombre/i }).first()).toBeVisible();
            await expect(headers.filter({ hasText: /documental/i }).first()).toBeVisible();
            await expect(headers.filter({ hasText: /gestor/i }).first()).toBeVisible();
            await expect(headers.filter({ hasText: /revis/i }).first()).toBeVisible();
        });

        await test.step('botons d\'acció per a IPA_ADMIN visibles', async () => {
            logInfo('  -> botons d\'acció per a IPA_ADMIN visibles');
            await expect(page.getByRole('button', { name: /nou procediment/i })).toBeVisible();
            await expect(page.getByRole('button', { name: /importa/i })).toBeVisible();
        });

        await test.step('formulari de filtre visible', async () => {
            logInfo('  -> formulari de filtre visible');
            await expect(page.locator('input[name="codi"]')).toBeVisible();
        });

        await test.step('files alternes tenen color de fons diferent', async () => {
            logInfo('  -> files alternes tenen color de fons diferent');
            const primeraFila = getRows(page).nth(0);
            const segonaFila  = getRows(page).nth(1);
            await expect(primeraFila).toBeVisible();
            await expect(segonaFila).toBeVisible();
            const bgPrimera = await primeraFila.evaluate(el => getComputedStyle(el).backgroundColor);
            const bgSegona  = await segonaFila.evaluate(el => getComputedStyle(el).backgroundColor);
            expect(bgPrimera).not.toBe(bgSegona);
        });

    });

    // ── Filtre i neteja prèvia ────────────────────────────────────────────────

    test('filtrar per codi i esborrar si el procediment de test ja existeix', async ({ page }) => {

        await aplicarFiltreProcediments(page, CODI_TEST, false);

        const count = await getRows(page).count();
        logDebug('Procediments amb codi' + CODI_TEST + ':' + count);
        if (count === 0) {
            logInfo("No existeix, l'entorn ja és net");
            return;
        } else {
            logInfo("Existeix → esborrar-lo per deixar l'entorn net per al test de creació");
            await humanDelay(page);
            const fila = getRows(page).first();
            await fila.locator('button[aria-label="more"]').click();
            await page.getByRole('menuitem', { name: /esborrar/i }).click();
            // Confirmació de l'esborrat (diàleg MUI)
            await expect(page.locator('[role="dialog"]')).toBeVisible({ timeout: 5_000 });
            await page.locator('[role="dialog"]').getByRole('button', { name: /acceptar|confirmar|ok/i }).click();
            await expectSuccessAlert(page);
            await aplicarFiltreProcediments(page, CODI_TEST, false);
            await expect(getRows(page)).toHaveCount(0);
        }     
    });

    // ── Creació ───────────────────────────────────────────────────────────────

    test('creació d\'un nou procediment', async ({ page }) => {

        await test.step('obrir formulari de nou procediment', async () => {
            logInfo('  -> obrir formulari de nou procediment');
            await page.getByRole('button', { name: /nou procediment/i }).click();
            await expect(page.locator('[role="dialog"]')).toBeVisible();
        });

        await test.step('omplir el formulari', async () => {
            logInfo('  -> omplir el formulari');
            const dialog = page.locator('[role="dialog"]');

            await dialog.locator('input[name="codi"]').fill(CODI_TEST);

            // Tipus classificació → ID (perquè el camp de classificació s'ompli automàticament)
            await triaMuiSelect(page, dialog, 'tipusClassificacio', /^ID$/i);

            await dialog.locator('input[name="nom"], textarea[name="nom"]').fill('prova creació play wright react');
            await dialog.locator('input[name="serieDocumental"]').fill('S0002');

            // Desmarcar "Procediment comú" per poder seleccionar òrgan gestor
            const checkComu = dialog.locator('input[name="procedimentComu"]');
            if (await checkComu.isChecked()) await checkComu.click();

            // Desmarcar "Crear regla a Distribució" per evitar errors sense el servei
            const checkRegla = dialog.locator('input[name="crearReglaDistribucio"]');
            if (await checkRegla.isChecked()) await checkRegla.click();

            // Seleccionar el primer òrgan gestor disponible via Autocomplete
            const organGestorInput = dialog.locator('[name="organGestor"] input[type="text"]');
            await organGestorInput.click();
            await page.waitForSelector('[role="listbox"]', { timeout: 5_000 });
            await page.getByRole('option').first().click();
            await page.waitForSelector('[role="listbox"]', { state: 'detached', timeout: 3_000 }).catch(() => {});
        });

        await test.step('enviar el formulari', async () => {
            logInfo('  -> enviar el formulari');
            await page.locator('[role="dialog"]').getByRole('button', { name: /guarda/i }).click();
        });

        await test.step('verificar missatge d\'èxit', async () => {
            logInfo('  -> verificar missatge d\'èxit');
            await expectSuccessAlert(page);
        });

    });

    // ── Modificació ───────────────────────────────────────────────────────────

    test('modificació d\'un procediment', async ({ page }) => {

        await test.step('filtrar i obrir modal de modificació', async () => {
            logInfo('  -> filtrar i obrir modal de modificació');
            await aplicarFiltreProcediments(page, CODI_TEST, true);
            await expect(getRows(page)).toHaveCount(1);
            const fila = getRows(page).first();
			await humanDelay(page);
            await fila.locator('button[aria-label="more"]').click();
			await humanDelay(page);
            await page.getByRole('menuitem', { name: 'Modifica', exact: true }).click();
			await humanDelay(page);
            await expect(page.locator('[role="dialog"]')).toBeVisible();
        });

        await test.step('modificar camps dins el diàleg', async () => {
            logInfo('  -> modificar camps dins el diàleg');
            const dialog = page.locator('[role="dialog"]');
            await dialog.locator('input[name="nom"], textarea[name="nom"]').fill(NOM_MODIFICAT);
            await dialog.locator('input[name="descripcio"], textarea[name="descripcio"]').fill(DESC_MODIFICADA);

            const checkGestio = dialog.locator('input[name="gestioAmbGrupsActiva"]');
            if (!await checkGestio.isChecked()) await checkGestio.click();

            const checkInteressat = dialog.locator('input[name="interessatObligatori"]');
            if (!await checkInteressat.isChecked()) await checkInteressat.click();

            const checkPermis = dialog.locator('input[name="permisDirecte"]');
            if (!await checkPermis.isChecked()) await checkPermis.click();
        });

        await test.step('guardar la modificació', async () => {
            logInfo('  -> guardar la modificació');
            await page.locator('[role="dialog"]').getByRole('button', { name: /guarda/i }).click();
        });

        await test.step('verificar missatge d\'èxit', async () => {
            logInfo('  -> verificar missatge d\'èxit');
            await expectSuccessAlert(page);
        });

    });

    // ── Verificació de la modificació ─────────────────────────────────────────

    test('verificació de la modificació del procediment', async ({ page }) => {

        await test.step('filtrar per codi i nom parcial', async () => {
            logInfo('  -> filtrar per codi i nom parcial');
            await page.locator('input[name="codi"]').fill(CODI_TEST);
            await page.locator('input[name="nom"]').fill('modificació');
            await page.getByRole('button', { name: 'Filtrar', exact: true }).click();
            const resp = waitApiGet(page, '/metaExpedient');
            await page.getByRole('button', { name: 'Filtrar', exact: true }).click();
            await resp;
        });

        await test.step('activar filtre de permís directe i verificar resultat', async () => {
            logInfo('  -> activar filtre de permís directe i verificar resultat');
            const resp = waitApiGet(page, '/metaExpedient');
            await page.getByRole('button', { name: /amb permis directe/i }).click();
            await resp;
            await expect(getRows(page)).toHaveCount(1);
        });

    });

    // ── Tipus de Documents ────────────────────────────────────────────────────

    test('accedir a tipus docs, verificar buit i crear dos documents', async ({ page }) => {

        await anarASubPagina(page, 'metaDocument');

        await test.step('verificar que la llista de documents està buida', async () => {
            logInfo('  -> verificar que la llista de documents està buida');
            await expect(getDocRows(page)).toHaveCount(0);
        });

        await test.step('crear el primer document', async () => {
            logInfo('  -> crear el primer document');
            await crearDocument(page, CODI_DOC1, NOM_DOC1);
        });

        await test.step('crear el segon document', async () => {
            logInfo('  -> crear el segon document');
            await crearDocument(page, CODI_DOC2, 'document tipus doc pw react 2');
        });

        await test.step('verificar que hi ha dos documents', async () => {
            logInfo('  -> verificar que hi ha dos documents');
            await expect(getDocRows(page)).toHaveCount(2);
        });

    });

    test('quickfilter, activar i desactivar un tipus de document', async ({ page }) => {

        await anarASubPagina(page, 'metaDocument');

        await test.step('quickfilter mostra només el document filtrat', async () => {
            logInfo('  -> quickfilter mostra només el document filtrat');
            await quickFilterDocs(page, CODI_DOC1);
            await expect(getDocRows(page)).toHaveCount(1);
        });

        const fila = getDocRows(page).first();
        await fila.locator('button[aria-label="more"]').click();
        const estaActiu = await page.getByRole('menuitem', { name: /desactivar/i }).isVisible();

        if (estaActiu) {
            await test.step('desactivar el document', async () => {
                logInfo('  -> desactivar el document');
                await page.getByRole('menuitem', { name: /desactivar/i }).click();
                await expectSuccessAlert(page);
            });

            await test.step('activar el document', async () => {
                logInfo('  -> activar el document');
                await fila.locator('button[aria-label="more"]').click();
                await page.getByRole('menuitem', { name: /activar/i }).click();
                await expectSuccessAlert(page);
            });
        } else {
            await test.step('activar el document', async () => {
                logInfo('  -> activar el document');
                await page.getByRole('menuitem', { name: /activar/i }).click();
                await expectSuccessAlert(page);
            });

            await test.step('desactivar el document', async () => {
                logInfo('  -> desactivar el document');
                await fila.locator('button[aria-label="more"]').click();
                await page.getByRole('menuitem', { name: /desactivar/i }).click();
                await expectSuccessAlert(page);
            });
        }

    });

    test('modificació d\'un tipus de document', async ({ page }) => {

        await anarASubPagina(page, 'metaDocument');

        await test.step('filtrar i obrir modal de modificació', async () => {
            logInfo('  -> filtrar i obrir modal de modificació');
            await quickFilterDocs(page, CODI_DOC1);
            await expect(getDocRows(page)).toHaveCount(1);
            const fila = getDocRows(page).first();
            await fila.locator('button[aria-label="more"]').click();
            await page.getByRole('menuitem', { name: 'Modifica', exact: true }).click();
            await expect(page.locator('[role="dialog"]')).toBeVisible();
        });

        await test.step('modificar camps del document', async () => {
            logInfo('  -> modificar camps del document');
            const dialog = page.locator('[role="dialog"]');
            await dialog.locator('input[name="nom"]').fill(NOM_DOC1_MOD);
            await dialog.locator('textarea[name="descripcio"]').fill(DESC_DOC1_MOD);
            await triaMuiSelect(page, dialog, 'multiplicitat', /0 a n/i);
        });

        await test.step('guardar la modificació', async () => {
            logInfo('  -> guardar la modificació');
            await page.locator('[role="dialog"]').getByRole('button', { name: /guarda/i }).click();
            await expectSuccessAlert(page);
        });

    });

    test('verificació de la modificació del tipus de document via quickfilter', async ({ page }) => {

        await anarASubPagina(page, 'metaDocument');

        await test.step('quickfilter pel nom modificat mostra el document', async () => {
            logInfo('  -> quickfilter pel nom modificat mostra el document');
            await quickFilterDocs(page, NOM_DOC1_MOD);
            await expect(getDocRows(page)).toHaveCount(1);
        });

    });

    test('marcar per defecte un tipus de document', async ({ page }) => {

        await anarASubPagina(page, 'metaDocument');

        await test.step('filtrar i marcar per defecte el primer document', async () => {
            logInfo('  -> filtrar i marcar per defecte el primer document');
            await quickFilterDocs(page, CODI_DOC1);
            await expect(getDocRows(page)).toHaveCount(1);
            const fila = getDocRows(page).first();
            await fila.locator('button[aria-label="more"]').click();
            await page.getByRole('menuitem', { name: /per defecte/i }).click();
            await expectSuccessAlert(page);
        });

    });

    test('eliminar un tipus de document', async ({ page }) => {

        await anarASubPagina(page, 'metaDocument');

        await test.step('filtrar i eliminar el segon document', async () => {
            logInfo('  -> filtrar i eliminar el segon document');
            await quickFilterDocs(page, CODI_DOC2);
            await expect(getDocRows(page)).toHaveCount(1);
            const fila = getDocRows(page).first();
            await fila.locator('button[aria-label="more"]').click();
            await page.getByRole('menuitem', { name: /esborrar/i }).click();
            await expect(page.locator('[role="dialog"]')).toBeVisible({ timeout: 5_000 });
            await humanDelay(page);
            await page.locator('[role="dialog"]').getByRole('button', { name: /acceptar|confirmar|ok/i }).click();
            await expectSuccessAlert(page);
            await expect(getDocRows(page)).toHaveCount(0);
        });

    });

    // ── Meta-dades ────────────────────────────────────────────────────────────

    test('accedir a meta-dades, verificar buit i crear dos meta-dades', async ({ page }) => {

        await anarASubPagina(page, 'metaDada');

        await test.step('verificar que la llista de meta-dades està buida', async () => {
            logInfo('  -> verificar que la llista de meta-dades està buida');
            await expect(getMetaRows(page)).toHaveCount(0);
        });

        await test.step('crear la primera meta-dada (camps mínims)', async () => {
            logInfo('  -> crear la primera meta-dada (camps mínims)');
            await crearMetaDada(page, CODI_META1, NOM_META1);
        });

        await test.step('crear la segona meta-dada (tots els camps)', async () => {
            logInfo('  -> crear la segona meta-dada (tots els camps)');
            await crearMetaDada(page, CODI_META2, 'meta-dada pw react 2', true);
        });

        await test.step('verificar que hi ha dos meta-dades', async () => {
            logInfo('  -> verificar que hi ha dos meta-dades');
            await expect(getMetaRows(page)).toHaveCount(2);
        });

    });

    test('quickfilter, activar i desactivar una meta-dada', async ({ page }) => {

        await anarASubPagina(page, 'metaDada');

        await test.step('quickfilter mostra només la meta-dada filtrada', async () => {
            logInfo('  -> quickfilter mostra només la meta-dada filtrada');
            await quickFilterMeta(page, CODI_META1);
            await expect(getMetaRows(page)).toHaveCount(1);
        });

        const fila = getMetaRows(page).first();
        await fila.locator('button[aria-label="more"]').click();
        const estaActiva = await page.getByRole('menuitem', { name: /desactivar/i }).isVisible();

        if (estaActiva) {
            await test.step('desactivar la meta-dada', async () => {
                logInfo('  -> desactivar la meta-dada');
                await page.getByRole('menuitem', { name: /desactivar/i }).click();
                await expectSuccessAlert(page);
            });

            await test.step('activar la meta-dada', async () => {
                logInfo('  -> activar la meta-dada');
                await fila.locator('button[aria-label="more"]').click();
                await page.getByRole('menuitem', { name: /activar/i }).click();
                await expectSuccessAlert(page);
            });
        } else {
            await test.step('activar la meta-dada', async () => {
                logInfo('  -> activar la meta-dada');
                await page.getByRole('menuitem', { name: /activar/i }).click();
                await expectSuccessAlert(page);
            });

            await test.step('desactivar la meta-dada', async () => {
                logInfo('  -> desactivar la meta-dada');
                await fila.locator('button[aria-label="more"]').click();
                await page.getByRole('menuitem', { name: /desactivar/i }).click();
                await expectSuccessAlert(page);
            });
        }

    });

    test('modificació d\'una meta-dada', async ({ page }) => {

        await anarASubPagina(page, 'metaDada');

        await test.step('filtrar i obrir modal de modificació', async () => {
            logInfo('  -> filtrar i obrir modal de modificació');
            await quickFilterMeta(page, CODI_META1);
            await expect(getMetaRows(page)).toHaveCount(1);
            const fila = getMetaRows(page).first();
            await fila.locator('button[aria-label="more"]').click();
            await page.getByRole('menuitem', { name: 'Modifica', exact: true }).click();
            await expect(page.locator('[role="dialog"]')).toBeVisible();
        });

        await test.step('modificar camps de la meta-dada', async () => {
            logInfo('  -> modificar camps de la meta-dada');
            const dialog = page.locator('[role="dialog"]');
            await dialog.locator('input[name="nom"]').fill(NOM_META1_MOD);
            await dialog.locator('textarea[name="descripcio"]').fill(DESC_META1_MOD);
            await triaMuiSelect(page, dialog, 'multiplicitat', /0 a n/i);
        });

        await test.step('guardar la modificació', async () => {
            logInfo('  -> guardar la modificació');
            await page.locator('[role="dialog"]').getByRole('button', { name: /guarda/i }).click();
            await expectSuccessAlert(page);
        });

    });

    test('verificació de la modificació de la meta-dada via quickfilter', async ({ page }) => {

        await anarASubPagina(page, 'metaDada');

        await test.step('quickfilter pel nom modificat mostra la meta-dada', async () => {
            logInfo('  -> quickfilter pel nom modificat mostra la meta-dada');
            await quickFilterMeta(page, NOM_META1_MOD);
            await expect(getMetaRows(page)).toHaveCount(1);
        });

    });

    test('eliminar una meta-dada', async ({ page }) => {

        await anarASubPagina(page, 'metaDada');

        await test.step('filtrar i eliminar la segona meta-dada', async () => {
            logInfo('  -> filtrar i eliminar la segona meta-dada');
            await quickFilterMeta(page, CODI_META2);
            await expect(getMetaRows(page)).toHaveCount(1);
            const fila = getMetaRows(page).first();
            await fila.locator('button[aria-label="more"]').click();
            await page.getByRole('menuitem', { name: /esborrar/i }).click();
            await expect(page.locator('[role="dialog"]')).toBeVisible({ timeout: 5_000 });
            await page.locator('[role="dialog"]').getByRole('button', { name: /acceptar|confirmar|ok/i }).click();
            await expectSuccessAlert(page);
            await expect(getMetaRows(page)).toHaveCount(0);
        });

    });

    // ── Tasques ───────────────────────────────────────────────────────────────

    test('accedir a tasques, verificar buit i crear dues tasques', async ({ page }) => {

        await anarASubPagina(page, 'tasca');

        await test.step('verificar que la llista de tasques està buida', async () => {
            logInfo('  -> verificar que la llista de tasques està buida');
            await expect(getTascaRows(page)).toHaveCount(0);
        });

        await test.step('crear la primera tasca (camps mínims + responsable)', async () => {
            logInfo('  -> crear la primera tasca (camps mínims + responsable)');
            await crearTasca(page, CODI_TASCA1, NOM_TASCA1);
        });

        await test.step('crear la segona tasca (tots els camps)', async () => {
            logInfo('  -> crear la segona tasca (tots els camps)');
            await crearTasca(page, CODI_TASCA2, 'tasca pw react 2', true);
        });

        await test.step('verificar que hi ha dues tasques', async () => {
            logInfo('  -> verificar que hi ha dues tasques');
            await expect(getTascaRows(page)).toHaveCount(2);
        });

    });

    test('quickfilter, activar i desactivar una tasca', async ({ page }) => {

        await anarASubPagina(page, 'tasca');

        await test.step('quickfilter mostra només la tasca filtrada', async () => {
            logInfo('  -> quickfilter mostra només la tasca filtrada');
            await quickFilterTasques(page, CODI_TASCA1);
            await expect(getTascaRows(page)).toHaveCount(1);
        });

        const fila = getTascaRows(page).first();
        await fila.locator('button[aria-label="more"]').click();
        const estaActiva = await page.getByRole('menuitem', { name: /desactivar/i }).isVisible();

        if (estaActiva) {
            await test.step('desactivar la tasca', async () => {
                logInfo('  -> desactivar la tasca');
                await page.getByRole('menuitem', { name: /desactivar/i }).click();
                await expectSuccessAlert(page);
            });

            await test.step('activar la tasca', async () => {
                logInfo('  -> activar la tasca');
                await fila.locator('button[aria-label="more"]').click();
                await page.getByRole('menuitem', { name: /activar/i }).click();
                await expectSuccessAlert(page);
            });
        } else {
            await test.step('activar la tasca', async () => {
                logInfo('  -> activar la tasca');
                await page.getByRole('menuitem', { name: /activar/i }).click();
                await expectSuccessAlert(page);
            });

            await test.step('desactivar la tasca', async () => {
                logInfo('  -> desactivar la tasca');
                await fila.locator('button[aria-label="more"]').click();
                await page.getByRole('menuitem', { name: /desactivar/i }).click();
                await expectSuccessAlert(page);
            });
        }

    });

    test('modificació d\'una tasca', async ({ page }) => {

        await anarASubPagina(page, 'tasca');

        await test.step('filtrar i obrir modal de modificació', async () => {
            logInfo('  -> filtrar i obrir modal de modificació');
            await quickFilterTasques(page, CODI_TASCA1);
            await expect(getTascaRows(page)).toHaveCount(1);
            const fila = getTascaRows(page).first();
            await fila.locator('button[aria-label="more"]').click();
            await page.getByRole('menuitem', { name: 'Modifica', exact: true }).click();
            await expect(page.locator('[role="dialog"]')).toBeVisible();
        });

        await test.step('modificar camps de la tasca', async () => {
            logInfo('  -> modificar camps de la tasca');
            const dialog = page.locator('[role="dialog"]');
            await dialog.locator('input[name="nom"]').fill(NOM_TASCA1_MOD);
            await dialog.locator('input[name="duracio"]').clear();
            await dialog.locator('input[name="duracio"]').fill('20');
            await dialog.locator('textarea[name="descripcio"]').fill(DESC_TASCA1_MOD);
            await triaMuiSelect(page, dialog, 'prioritat', /alta/i);
        });

        await test.step('guardar la modificació', async () => {
            logInfo('  -> guardar la modificació');
            await page.locator('[role="dialog"]').getByRole('button', { name: /guarda/i }).click();
            await expectSuccessAlert(page);
        });

    });

    test('verificació de la modificació de la tasca via quickfilter', async ({ page }) => {

        await anarASubPagina(page, 'tasca');

        await test.step('quickfilter pel nom modificat mostra la tasca', async () => {
            logInfo('  -> quickfilter pel nom modificat mostra la tasca');
            await quickFilterTasques(page, NOM_TASCA1_MOD);
            await expect(getTascaRows(page)).toHaveCount(1);
        });

    });

    test('eliminar una tasca', async ({ page }) => {

        await anarASubPagina(page, 'tasca');

        await test.step('filtrar i eliminar la segona tasca', async () => {
            logInfo('  -> filtrar i eliminar la segona tasca');
            await quickFilterTasques(page, CODI_TASCA2);
            await expect(getTascaRows(page)).toHaveCount(1);
            const fila = getTascaRows(page).first();
            await fila.locator('button[aria-label="more"]').click();
            await page.getByRole('menuitem', { name: /esborrar/i }).click();
            await expect(page.locator('[role="dialog"]')).toBeVisible({ timeout: 5_000 });
            await page.locator('[role="dialog"]').getByRole('button', { name: /acceptar|confirmar|ok/i }).click();
            await expectSuccessAlert(page);
            await expect(getTascaRows(page)).toHaveCount(0);
        });

    });

    // ── Activar / Desactivar ──────────────────────────────────────────────────

    test('activar i desactivar procediment', async ({ page }) => {

        await test.step('filtrar per localitzar el procediment de test', async () => {
            logInfo('  -> filtrar per localitzar el procediment de test');
            await aplicarFiltreProcediments(page, CODI_TEST, true);
            await expect(getRows(page)).toHaveCount(1);
        });

        const fila = getRows(page).first();
        await fila.locator('button[aria-label="more"]').click();
        const estaActiu = await page.getByRole('menuitem', { name: /desactivar/i }).isVisible();

        if (estaActiu) {
            await test.step('desactivar el procediment', async () => {
                logInfo('  -> desactivar el procediment');
                await page.getByRole('menuitem', { name: /desactivar/i }).click();
                await expectSuccessAlert(page);
            });

            await test.step('activar el procediment', async () => {
                logInfo('  -> activar el procediment');
                await fila.locator('button[aria-label="more"]').click();
                await page.getByRole('menuitem', { name: /activar/i }).click();
                await expectSuccessAlert(page);
            });
        } else {
            await test.step('activar el procediment', async () => {
                logInfo('  -> activar el procediment');
                await page.getByRole('menuitem', { name: /activar/i }).click();
                await expectSuccessAlert(page);
            });

            await test.step('desactivar el procediment', async () => {
                logInfo('  -> desactivar el procediment');
                await fila.locator('button[aria-label="more"]').click();
                await page.getByRole('menuitem', { name: /desactivar/i }).click();
                await expectSuccessAlert(page);
            });
        }

    });

});
