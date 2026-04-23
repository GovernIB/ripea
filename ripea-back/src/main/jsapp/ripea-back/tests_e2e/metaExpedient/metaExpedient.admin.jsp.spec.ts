import { test, expect, Page } from '@playwright/test';

const URL_PROCEDIMENTS = '/ripeaback/metaExpedient';
const CODI_TEST      = 'zz_PLAYWRIGHT_JSP_zz';
const NOM_MODIFICAT  = 'prova modificació playwright';
const DESC_MODIFICADA = 'descripció de prova per playwright';

const CODI_DOC1     = 'DOC_PW_JSP_01';
const NOM_DOC1      = 'document tipus doc pw 1';
const CODI_DOC2     = 'DOC_PW_JSP_02';
const NOM_DOC1_MOD  = 'doc modificat pw 1';
const DESC_DOC1_MOD = 'descripció modificada doc 1';

// Helpers per localitzar elements de la pàgina
const getGrid       = (page: Page) => page.locator('#metaexpedients');
const getRows       = (page: Page) => page.locator('#metaexpedients tbody tr').filter({ hasNot: page.locator('td.dataTables_empty') });
const getToolbar    = (page: Page) => page.locator('[data-toggle="botons-titol"]');
const getFilterArea = (page: Page) => page.locator('input[name="codi"]');

// Helper: verificar missatge d'èxit (reutilitzable al llarg de tot el test)
const expectSuccessAlert = (page: Page) =>
    expect(page.locator('.alert.alert-success')).toBeVisible({ timeout: 10_000 });

// Helper: crear Promise d'espera de resposta del datatable
const waitDatatable = (page: Page) =>
    page.waitForResponse(
        resp => resp.url().includes('/metaExpedient/datatable') && resp.status() === 200,
    );

// Helper: filtrar per codi intern i esperar que el datatable es recarregui
const filtrarPerCodi = async (page: Page, codi: string) => {
    const dt = waitDatatable(page);
    await page.locator('input[name="codi"]').fill(codi);
	// Netejar el filtre "Actiu" perquè el procediment pot estar inactiu i no aparèixer.
	// El botó × (select2-selection__clear) només existeix quan hi ha un valor seleccionat.
	const clearActiu = page.locator('#select2-actiu-container .select2-selection__clear');
	if (await clearActiu.isVisible()) {
	    await clearActiu.click();
	}
    await page.locator('button[value="filtrar"]').click();
    await dt;
    await expect(page.locator('#metaexpedients_processing')).toBeHidden();
};

// ── Helpers per a Tipus de Documents ─────────────────────────────────────────

const getDocRows = (p: Page) =>
    p.locator('#metadocuments tbody tr').filter({ hasNot: p.locator('td.dataTables_empty') });

const waitDatatableDocs = (p: Page) =>
    p.waitForResponse(resp => resp.url().includes('/metaDocument/datatable') && resp.status() === 200);

const quickFilterDocs = async (p: Page, text: string) => {
    const dt = waitDatatableDocs(p);
    await p.locator('#metadocuments_filter input').fill(text);
    await dt;
    await expect(p.locator('#metadocuments_processing')).toBeHidden();
};

const anarATipusDocs = async (page: Page): Promise<Page> => {
    await filtrarPerCodi(page, CODI_TEST);
    await expect(getRows(page)).toHaveCount(1);
    const fila = getRows(page).first();
    await fila.getByRole('button', { name: /elements/i }).click();
    const tipusDocsPagePromise = page.context().waitForEvent('page');
    await fila.getByRole('link', { name: /tipus docs/i }).click();
    const tipusDocsPage = await tipusDocsPagePromise;
    await tipusDocsPage.waitForLoadState('load');
    await expect(tipusDocsPage.locator('#metadocuments_processing')).toBeHidden({ timeout: 10_000 });
    return tipusDocsPage;
};

const crearDocument = async (tipusDocsPage: Page, codi: string, nom: string) => {
    await tipusDocsPage.locator('a[href*="metaDocument/new"]').click();
    await expect(tipusDocsPage.locator('.modal.in')).toBeVisible();
    const frame = tipusDocsPage.locator('.modal.in').frameLocator('.modal-body iframe');
    await expect(frame.locator('input[name="codi"]')).toBeVisible();
    await frame.locator('input[name="codi"]').fill(codi);
    await frame.locator('input[name="nom"]').fill(nom);
    await frame.locator('a[href="#dades-nti"]').click();
    await frame.locator('select[name="ntiOrigen"]').selectOption('O0');
    await frame.locator('select[name="ntiTipoDocumental"]').selectOption('TD10');
    await frame.locator('select[name="ntiEstadoElaboracion"]').selectOption('EE01');
    const dtRefresh = waitDatatableDocs(tipusDocsPage);
    const submitBtn = tipusDocsPage.locator('.modal.in .modal-footer button[type="submit"]');
    await submitBtn.waitFor({ state: 'visible' });
    await submitBtn.click();
    await dtRefresh;
    await expectSuccessAlert(tipusDocsPage);
};

// ─────────────────────────────────────────────────────────────────────────────
// Pàgina: Gestió de Procediments JSP  (rol IPA_ADMIN)
// ─────────────────────────────────────────────────────────────────────────────

test.describe('Gestió de Procediments JSP — IPA_ADMIN', () => {

	test.beforeEach(async ({ page }) => {
	    // Esperar la resposta AJAX del DataTable per assegurar que les dades han carregat
	    let dt = waitDatatable(page);
	    await page.goto(URL_PROCEDIMENTS);
	    await dt;
	    await expect(page.locator('#metaexpedients_processing')).toBeHidden();

	    // Netejar filtres guardats en sessió: el botó fa un POST que recarrega la pàgina
	    // sense cap filtre actiu, evitant que resultats d'un test anterior contaminin el següent.
	    dt = waitDatatable(page);
	    await page.locator('button[value="netejar"]').click();
	    await dt;
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

    // ── Filtre i neteja prèvia ────────────────────────────────────────────────

    test('filtrar per codi i esborrar si el procediment de test ja existeix', async ({ page }) => {
		
        await filtrarPerCodi(page, CODI_TEST);

        const count = await getRows(page).count();
        if (count === 0) {
			console.log("No existeix, l'entorn ja és net"); 
			return;
		} else {
			console.log("Existeix → esborrar-lo per deixar l'entorn net per al test de creació"); 
		}

        const fila = getRows(page).first();
        page.on('dialog', dialog => dialog.accept());
        const dt = waitDatatable(page);
        await fila.getByRole('button', { name: /accions/i }).click();
        await fila.getByRole('link', { name: /esborrar/i }).click();
        await dt;
        await expect(page.locator('#metaexpedients_processing')).toBeHidden();
        await expect(getRows(page)).toHaveCount(0);
    });

    // ── Creació ───────────────────────────────────────────────────────────────

    test('creació d\'un nou procediment', async ({ page }) => {

        await test.step('obrir formulari de nou procediment', async () => {
            await page.getByRole('link', { name: /nou/i }).click();
            // S'usa .modal.in per evitar conflicte amb el modal ocult #modal-copied.
            await expect(page.locator('.modal.in')).toBeVisible();
            // Esperar que l'iframe hagi carregat el seu contingut (codi intern visible)
            const frame = page.locator('.modal.in').frameLocator('.modal-body iframe');
            await expect(frame.locator('input[name="codi"]')).toBeVisible();
        });

        await test.step('omplir el formulari dins l\'iframe del modal', async () => {
            const frame = page.locator('.modal.in').frameLocator('.modal-body iframe');

            await frame.locator('input[name="codi"]').fill(CODI_TEST);
            await frame.locator('textarea[name="nom"]').fill('prova creació play wright');
            await frame.locator('input[name="serieDocumental"]').fill('S0002');

            // Desmarcar crearReglaDistribucio per evitar errors en entorns sense el servei
            const checkRegla = frame.getByRole('checkbox', { name: /crear regla/i });
            if (await checkRegla.isChecked()) {
                await checkRegla.uncheck();
            }

            // Desmarcar "Procediment comú" per poder seleccionar un òrgan gestor.
            // Mentre #comu està marcat, el JS amaga #organGestorContainer.
            const checkComu = frame.locator('#comu');
            if (await checkComu.isChecked()) {
                await checkComu.uncheck();
            }
            await expect(frame.locator('#organGestorContainer')).toBeVisible();

            // Seleccionar el primer òrgan gestor disponible via select2 suggest (AJAX).
            // El camp requereix mínim 3 caràcters per disparar la cerca.
            // El dropdown (span.select2-dropdown) s'afegeix al body de l'iframe en posició absoluta.
            await frame.locator('#organGestorContainer .select2-selection').click();
            const suggestResponse = page.waitForResponse(
                resp => resp.url().includes('/organgestorajax/organgestor') && resp.status() === 200,
            );
            await frame.locator('.select2-search__field').fill('GOV');
            await suggestResponse;
            // Esperar que les opcions siguin visibles abans de fer clic
            const primerResultat = frame.locator('.select2-results__option').first();
            await primerResultat.waitFor({ timeout: 5_000 });
            await primerResultat.click();
			
            // El dropdown del select2 és un span posicionat absolutament que pot cobrir el footer.
            // Esperar que desaparegui del DOM i que l'AJAX calculateClassificacioId completi.
            await frame.locator('.select2-dropdown').waitFor({ state: 'detached', timeout: 5_000 }).catch(() => {});
            await page.waitForResponse(
                resp => resp.url().includes('/calculateClassificacioId/') && resp.status() === 200,
                { timeout: 5_000 },
            ).catch(() => {});

            // Seleccionar la classificació per ID (auto-calculada a partir de l'òrgan gestor).
            // El botó "ID" del btn-group conté el radio input; és habilitat un cop s'ha seleccionat organ gestor.
            await frame.locator('button:has(input[value="ID"][name="tipusClassificacio"])').click();
            // Quan es selecciona "ID", showHideClassificacioInput() mostra el camp classificacioId (readonly)
            await expect(frame.locator('#classificacioId')).toBeVisible();
        });

        await test.step('enviar el formulari', async () => {
            // El botó submit és clonat al .modal-footer de la pàgina pare (l'original dins l'iframe s'amaga).
            // S'usa button[type="submit"] directe per evitar ambigüitats d'accessible name amb la icona FA.
            const submitBtn = page.locator('.modal.in .modal-footer button[type="submit"]');
            await submitBtn.waitFor({ state: 'visible' });
            await submitBtn.click();
        });

        await test.step('verificar missatge d\'èxit', async () => {
            await expectSuccessAlert(page);
        });

    });

    // ── Modificació ───────────────────────────────────────────────────────────

    test('modificació d\'un procediment', async ({ page }) => {

        await test.step('filtrar i obrir modal de modificació', async () => {
            await filtrarPerCodi(page, CODI_TEST);
            await expect(getRows(page)).toHaveCount(1);
            const fila = getRows(page).first();
            await fila.getByRole('button', { name: /accions/i }).click();
            await fila.getByRole('link', { name: /modificar/i }).click();
            await expect(page.locator('.modal.in')).toBeVisible();
            const frame = page.locator('.modal.in').frameLocator('.modal-body iframe');
            await expect(frame.locator('input[name="codi"]')).toBeVisible();
        });

        await test.step('modificar camps dins l\'iframe del modal', async () => {
            const frame = page.locator('.modal.in').frameLocator('.modal-body iframe');

            await frame.locator('textarea[name="nom"]').fill(NOM_MODIFICAT);
            await frame.locator('textarea[name="descripcio"]').fill(DESC_MODIFICADA);

            const checkGestio = frame.locator('#gestioAmbGrupsActiva');
            if (!await checkGestio.isChecked()) await checkGestio.check();

            const checkInteressat = frame.locator('#interessatObligatori');
            if (!await checkInteressat.isChecked()) await checkInteressat.check();

            const checkPermis = frame.locator('#permisDirecte');
            if (!await checkPermis.isChecked()) await checkPermis.check();
        });

        await test.step('guardar la modificació', async () => {
            const submitBtn = page.locator('.modal.in .modal-footer button[type="submit"]');
            await submitBtn.waitFor({ state: 'visible' });
            await submitBtn.click();
        });

        await test.step('verificar missatge d\'èxit', async () => {
            await expectSuccessAlert(page);
        });

    });

    // ── Verificació de la modificació ─────────────────────────────────────────

    test('verificació de la modificació del procediment', async ({ page }) => {

        await test.step('filtrar per codi i nom parcial', async () => {
            const dt = waitDatatable(page);
            await page.locator('input[name="codi"]').fill(CODI_TEST);
            await page.locator('input[name="nom"]').fill('modificació');
            const clearActiu = page.locator('#select2-actiu-container .select2-selection__clear');
            if (await clearActiu.isVisible()) await clearActiu.click();
            await page.locator('button[value="filtrar"]').click();
            await dt;
            await expect(page.locator('#metaexpedients_processing')).toBeHidden();
        });

        await test.step('activar filtre de permís directe i verificar resultat', async () => {
            // permisDirecteBtn actualitza el camp ocult permisDirecteActive i refresca el datatable AJAX.
            // Cal registrar la Promise ABANS del click per no perdre la resposta.
            const dt = waitDatatable(page);
            await page.locator('#permisDirecteBtn').click();
            await dt;
            await expect(page.locator('#metaexpedients_processing')).toBeHidden();
            await expect(getRows(page)).toHaveCount(1);
        });

    });

    // ── Tipus de Documents ────────────────────────────────────────────────────

    test('accedir a tipus docs, verificar buit i crear dos documents', async ({ page }) => {

        const tipusDocsPage = await anarATipusDocs(page);

        await test.step('verificar que la llista de documents està buida', async () => {
            await expect(getDocRows(tipusDocsPage)).toHaveCount(0);
        });

        await test.step('crear el primer document', async () => {
            await crearDocument(tipusDocsPage, CODI_DOC1, NOM_DOC1);
        });

        await test.step('crear el segon document', async () => {
            await crearDocument(tipusDocsPage, CODI_DOC2, 'document tipus doc pw 2');
        });

        await test.step('verificar que hi ha dos documents', async () => {
            await expect(getDocRows(tipusDocsPage)).toHaveCount(2);
        });

        await tipusDocsPage.close();
    });

    test('quickfilter, activar i desactivar un tipus de document', async ({ page }) => {

        const tipusDocsPage = await anarATipusDocs(page);

        await test.step('quickfilter mostra només el document filtrat', async () => {
            await quickFilterDocs(tipusDocsPage, CODI_DOC1);
            await expect(getDocRows(tipusDocsPage)).toHaveCount(1);
        });

        const fila = getDocRows(tipusDocsPage).first();

        await test.step('obrir menú accions', async () => {
            await fila.getByRole('button', { name: /accions/i }).click();
        });

        const estaActiu = await fila.getByRole('link', { name: /desactivar/i }).isVisible();

        if (estaActiu) {
            await test.step('desactivar el document', async () => {
                const dt = waitDatatableDocs(tipusDocsPage);
                await fila.getByRole('link', { name: /desactivar/i }).click();
                await dt;
                await expectSuccessAlert(tipusDocsPage);
            });

            await test.step('activar el document', async () => {
                await fila.getByRole('button', { name: /accions/i }).click();
                const dt = waitDatatableDocs(tipusDocsPage);
                await fila.getByRole('link', { name: /activar/i }).click();
                await dt;
                await expectSuccessAlert(tipusDocsPage);
            });
        } else {
            await test.step('activar el document', async () => {
                const dt = waitDatatableDocs(tipusDocsPage);
                await fila.getByRole('link', { name: /activar/i }).click();
                await dt;
                await expectSuccessAlert(tipusDocsPage);
            });

            await test.step('desactivar el document', async () => {
                await fila.getByRole('button', { name: /accions/i }).click();
                const dt = waitDatatableDocs(tipusDocsPage);
                await fila.getByRole('link', { name: /desactivar/i }).click();
                await dt;
                await expectSuccessAlert(tipusDocsPage);
            });
        }

        await tipusDocsPage.close();
    });

    test('modificació d\'un tipus de document', async ({ page }) => {

        const tipusDocsPage = await anarATipusDocs(page);

        await test.step('filtrar i obrir modal de modificació', async () => {
            await quickFilterDocs(tipusDocsPage, CODI_DOC1);
            await expect(getDocRows(tipusDocsPage)).toHaveCount(1);
            const fila = getDocRows(tipusDocsPage).first();
            await fila.getByRole('button', { name: /accions/i }).click();
            await fila.getByRole('link', { name: /modificar/i }).click();
            await expect(tipusDocsPage.locator('.modal.in')).toBeVisible();
            const frame = tipusDocsPage.locator('.modal.in').frameLocator('.modal-body iframe');
            await expect(frame.locator('input[name="codi"]')).toBeVisible();
        });

        await test.step('modificar camps del document', async () => {
            const frame = tipusDocsPage.locator('.modal.in').frameLocator('.modal-body iframe');
            await frame.locator('input[name="nom"]').fill(NOM_DOC1_MOD);
            await frame.locator('textarea[name="descripcio"]').fill(DESC_DOC1_MOD);
            await frame.locator('select[name="multiplicitat"]').selectOption('M_0_N');
        });

        await test.step('guardar la modificació', async () => {
            const dtRefresh = waitDatatableDocs(tipusDocsPage);
            const submitBtn = tipusDocsPage.locator('.modal.in .modal-footer button[type="submit"]');
            await submitBtn.waitFor({ state: 'visible' });
            await submitBtn.click();
            await dtRefresh;
            await expectSuccessAlert(tipusDocsPage);
        });

        await tipusDocsPage.close();
    });

    test('verificació de la modificació del tipus de document via quickfilter', async ({ page }) => {

        const tipusDocsPage = await anarATipusDocs(page);

        await test.step('quickfilter pel nom modificat mostra el document', async () => {
            await quickFilterDocs(tipusDocsPage, NOM_DOC1_MOD);
            await expect(getDocRows(tipusDocsPage)).toHaveCount(1);
        });

        await tipusDocsPage.close();
    });

    test('marcar per defecte un tipus de document', async ({ page }) => {

        const tipusDocsPage = await anarATipusDocs(page);

        await test.step('filtrar i marcar per defecte el primer document', async () => {
            await quickFilterDocs(tipusDocsPage, CODI_DOC1);
            await expect(getDocRows(tipusDocsPage)).toHaveCount(1);
            const fila = getDocRows(tipusDocsPage).first();
            await fila.getByRole('button', { name: /accions/i }).click();
            const dt = waitDatatableDocs(tipusDocsPage);
            await fila.getByRole('link', { name: /marcar per defecte/i }).click();
            await dt;
            await expectSuccessAlert(tipusDocsPage);
        });

        await tipusDocsPage.close();
    });

    test('eliminar un tipus de document', async ({ page }) => {

        const tipusDocsPage = await anarATipusDocs(page);

        await test.step('filtrar i eliminar el segon document', async () => {
            await quickFilterDocs(tipusDocsPage, CODI_DOC2);
            await expect(getDocRows(tipusDocsPage)).toHaveCount(1);
            const fila = getDocRows(tipusDocsPage).first();
            tipusDocsPage.on('dialog', dialog => dialog.accept());
            await fila.getByRole('button', { name: /accions/i }).click();
            const dt = waitDatatableDocs(tipusDocsPage);
            await fila.getByRole('link', { name: /esborrar/i }).click();
            await dt;
            await expectSuccessAlert(tipusDocsPage);
            await expect(getDocRows(tipusDocsPage)).toHaveCount(0);
        });

        await tipusDocsPage.close();
    });

    // ── Activar / Desactivar ──────────────────────────────────────────────────

    test('activar i desactivar procediment', async ({ page }) => {

        await test.step('filtrar per localitzar el procediment de test', async () => {
            await filtrarPerCodi(page, CODI_TEST);
            await expect(getRows(page)).toHaveCount(1);
        });

        const fila = getRows(page).first();

        // Determinar estat inicial per executar el cicle en l'ordre correcte
        await fila.getByRole('button', { name: /accions/i }).click();
        const estaActiu = await fila.getByRole('link', { name: /desactivar/i }).isVisible();

        if (estaActiu) {
            await test.step('desactivar el procediment', async () => {
                const dt = waitDatatable(page);
                await fila.getByRole('link', { name: /desactivar/i }).click();
                await dt;
                await expectSuccessAlert(page);
            });

            await test.step('activar el procediment', async () => {
                await fila.getByRole('button', { name: /accions/i }).click();
                const dt = waitDatatable(page);
                await fila.getByRole('link', { name: /activar/i }).click();
                await dt;
                await expectSuccessAlert(page);
            });
        } else {
            await test.step('activar el procediment', async () => {
                const dt = waitDatatable(page);
                await fila.getByRole('link', { name: /activar/i }).click();
                await dt;
                await expectSuccessAlert(page);
            });

            await test.step('desactivar el procediment', async () => {
                await fila.getByRole('button', { name: /accions/i }).click();
                const dt = waitDatatable(page);
                await fila.getByRole('link', { name: /desactivar/i }).click();
                await dt;
                await expectSuccessAlert(page);
            });
        }

    });

});
