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

const CODI_META1     = 'meTADDPWJSP01';
const NOM_META1      = 'meta-dada pw 1';
const CODI_META2     = 'meTADDPWJSP02';
const NOM_META1_MOD  = 'meta-dada modificada pw 1';
const DESC_META1_MOD = 'descripció modificada meta-dada 1';

const CODI_TASCA1     = 'tscPWJSP01';
const NOM_TASCA1      = 'tasca pw 1';
const CODI_TASCA2     = 'tscPWJSP02';
const NOM_TASCA1_MOD  = 'tasca modificada pw 1';
const DESC_TASCA1_MOD = 'descripció modificada tasca 1';

const CODI_ESTAT1     = 'estPWJSP01';
const NOM_ESTAT1      = 'estat pw 1';
const CODI_ESTAT2     = 'estPWJSP02';
const NOM_ESTAT1_MOD  = 'estat modificat pw 1';

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
    await fila.getByRole('button', { name: /elements|elementos/i }).click();
    const tipusDocsPagePromise = page.context().waitForEvent('page');
    await fila.getByRole('link', { name: /tipus docs|tipos docs/i }).click();
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

// ── Helpers per a Meta-dades ──────────────────────────────────────────────────

const getMetaRows = (p: Page) =>
    p.locator('#metadades tbody tr').filter({ hasNot: p.locator('td.dataTables_empty') });

const waitDatatableMeta = (p: Page) =>
    p.waitForResponse(resp => resp.url().includes('/metaDada/datatable') && resp.status() === 200);

const quickFilterMeta = async (p: Page, text: string) => {
    const dt = waitDatatableMeta(p);
    await p.locator('#metadades_filter input').fill(text);
    await dt;
    await expect(p.locator('#metadades_processing')).toBeHidden();
};

const anarAMetaDades = async (page: Page): Promise<Page> => {
    await filtrarPerCodi(page, CODI_TEST);
    await expect(getRows(page)).toHaveCount(1);
    const fila = getRows(page).first();
    await fila.getByRole('button', { name: /elements|elementos/i }).click();
    const metaDadesPagePromise = page.context().waitForEvent('page');
    await fila.getByRole('link', { name: /meta-dades|meta-datos/i }).click();
    const metaDadesPage = await metaDadesPagePromise;
    await metaDadesPage.waitForLoadState('load');
    await expect(metaDadesPage.locator('#metadades_processing')).toBeHidden({ timeout: 10_000 });
    return metaDadesPage;
};

const crearMetaDada = async (metaDadesPage: Page, codi: string, nom: string, full = false) => {
    await metaDadesPage.locator('a[href*="metaDada/new"]').click();
    await expect(metaDadesPage.locator('.modal.in')).toBeVisible();
    const frame = metaDadesPage.locator('.modal.in').frameLocator('.modal-body iframe');
    await expect(frame.locator('input[name="codi"]')).toBeVisible();
    await frame.locator('input[name="codi"]').fill(codi);
    await frame.locator('input[name="nom"]').fill(nom);
    if (full) {
        await frame.locator('select[name="multiplicitat"]').selectOption('M_0_N');
        await frame.locator('textarea[name="descripcio"]').fill('descripció meta-dada completa 2');
        await frame.locator('#enviable').check();
        await expect(frame.locator('input[name="metadadaArxiu"]')).toBeVisible();
        await frame.locator('input[name="metadadaArxiu"]').fill('metadada_arxiu_pw_2');
    }
    const dtRefresh = waitDatatableMeta(metaDadesPage);
    const submitBtn = metaDadesPage.locator('.modal.in .modal-footer button[type="submit"]');
    await submitBtn.waitFor({ state: 'visible' });
    await submitBtn.click();
    await dtRefresh;
    await expectSuccessAlert(metaDadesPage);
};

// ── Helpers per a Tasques ─────────────────────────────────────────────────────

// La pàgina de tasques reutilitza id="metadades" i #metadades_filter; només difereix la URL del datatable.
const getTascaRows = (p: Page) =>
    p.locator('#metadades tbody tr').filter({ hasNot: p.locator('td.dataTables_empty') });

const waitDatatableTasques = (p: Page) =>
    p.waitForResponse(resp => resp.url().includes('/tasca/datatable') && resp.status() === 200);

const quickFilterTasques = async (p: Page, text: string) => {
    const dt = waitDatatableTasques(p);
    await p.locator('#metadades_filter input').fill(text);
    await dt;
    await expect(p.locator('#metadades_processing')).toBeHidden();
};

const anarATasques = async (page: Page): Promise<Page> => {
    await filtrarPerCodi(page, CODI_TEST);
    await expect(getRows(page)).toHaveCount(1);
    const fila = getRows(page).first();
    await fila.getByRole('button', { name: /elements|elementos/i }).click();
    const tascaPagePromise = page.context().waitForEvent('page');
    await fila.getByRole('link', { name: /tasques|tareas/i }).click();
    const tascaPage = await tascaPagePromise;
    await tascaPage.waitForLoadState('load');
    await expect(tascaPage.locator('#metadades_processing')).toBeHidden({ timeout: 10_000 });
    return tascaPage;
};

// Selecciona el primer responsable retornat pel suggest d'usuaris amb la cerca 'adm'.
const seleccionarResponsable = async (tascaPage: Page, frame: ReturnType<typeof tascaPage.frameLocator>) => {
    await frame.locator('.form-group:has(label[for="responsable"]) .select2-selection').click();
    const suggestResp = tascaPage.waitForResponse(
        resp => resp.url().includes('/userajax/usuaris') && resp.status() === 200,
    );
    await frame.locator('.select2-search__field').fill('adm');
    await suggestResp;
    const primerResultat = frame.locator('.select2-results__option').first();
    await primerResultat.waitFor({ timeout: 5_000 });
    await primerResultat.click();
    await frame.locator('.select2-dropdown').waitFor({ state: 'detached', timeout: 5_000 }).catch(() => {});
};

const crearTasca = async (tascaPage: Page, codi: string, nom: string, full = false) => {
    await tascaPage.locator('a[href*="tasca/new"]').click();
    await expect(tascaPage.locator('.modal.in')).toBeVisible();
    const frame = tascaPage.locator('.modal.in').frameLocator('.modal-body iframe');
    await expect(frame.locator('input[name="codi"]')).toBeVisible();
    await frame.locator('input[name="codi"]').fill(codi);
    await frame.locator('input[name="nom"]').fill(nom);
    await seleccionarResponsable(tascaPage, frame);
    await frame.locator('textarea[name="descripcio"]').fill(full ? 'descripció tasca completa 2' : 'descripció tasca 1');
    if (full) {
        await frame.locator('input[name="duracio"]').fill('5');
        await frame.locator('select[name="prioritat"]').selectOption('C_ALTA');
    }
    const dtRefresh = waitDatatableTasques(tascaPage);
    const submitBtn = tascaPage.locator('.modal.in .modal-footer button[type="submit"]');
    await submitBtn.waitFor({ state: 'visible' });
    await submitBtn.click();
    await dtRefresh;
    await expectSuccessAlert(tascaPage);
};

// ── Helpers per a Estats ─────────────────────────────────────────────────────

const getEstatRows = (p: Page) =>
    p.locator('#estats tbody tr').filter({ hasNot: p.locator('td.dataTables_empty') });

const waitDatatableEstats = (p: Page) =>
    p.waitForResponse(resp => resp.url().includes('/expedientEstat/') && resp.url().includes('/datatable') && resp.status() === 200);

const anarAEstats = async (page: Page): Promise<Page> => {
    await filtrarPerCodi(page, CODI_TEST);
    await expect(getRows(page)).toHaveCount(1);
    const fila = getRows(page).first();
    await fila.getByRole('button', { name: /elements|elementos/i }).click();
    const estatPagePromise = page.context().waitForEvent('page');
    await fila.getByRole('link', { name: /estats|estados/i }).click();
    const estatPage = await estatPagePromise;
    await estatPage.waitForLoadState('load');
    await expect(estatPage.locator('#estats_processing')).toBeHidden({ timeout: 10_000 });
    return estatPage;
};

const crearEstat = async (estatPage: Page, codi: string, nom: string) => {
    // El botó "Nou estat" és renderitzat pel jsrender template #botonsTemplate via webutil.datatable.js
    await estatPage.getByRole('link', { name: /nou estat|nuevo estado/i }).click();
    await expect(estatPage.locator('.modal.in')).toBeVisible();
    const frame = estatPage.locator('.modal.in').frameLocator('.modal-body iframe');
    await expect(frame.locator('input[name="codi"]')).toBeVisible();
    await frame.locator('input[name="codi"]').fill(codi);
    await frame.locator('input[name="nom"]').fill(nom);
    // data-refresh-pagina="true" al botó → recàrrega de pàgina o del datatable.
    // waitDatatableEstats captura la petició tant si és via refresc de datatable
    // com si és via recàrrega completa de la pàgina (el nou datatable re-fa la petició).
    const dtRefresh = waitDatatableEstats(estatPage);
    const submitBtn = estatPage.locator('.modal.in .modal-footer button[type="submit"]');
    await submitBtn.waitFor({ state: 'visible' });
    await submitBtn.click();
    await dtRefresh;
    await expectSuccessAlert(estatPage);
};

// ── Helpers per a Grups ─────────────────────────────────────────────────────

const getGrupRows = (p: Page) =>
    p.locator('#metadades tbody tr').filter({ hasNot: p.locator('td.dataTables_empty') });

const waitDatatableGrups = (p: Page) =>
    p.waitForResponse(resp => resp.url().includes('/grup/datatable') && resp.status() === 200);

const anarAGrups = async (page: Page): Promise<Page> => {
    await filtrarPerCodi(page, CODI_TEST);
    await expect(getRows(page)).toHaveCount(1);
    const fila = getRows(page).first();
    await fila.getByRole('button', { name: /elements|elementos/i }).click();
    const grupPagePromise = page.context().waitForEvent('page');
    await fila.getByRole('link', { name: /grups|grupos/i }).click();
    const grupPage = await grupPagePromise;
    await grupPage.waitForLoadState('load');
    await expect(grupPage.locator('#metadades_processing')).toBeHidden({ timeout: 10_000 });
    return grupPage;
};

const vincularGrupJsp = async (grupPage: Page) => {
    await grupPage.locator('a[href*="grup/relacionar"]').click();
    await expect(grupPage.locator('.modal.in')).toBeVisible();
    const frame = grupPage.locator('.modal.in').frameLocator('.modal-body iframe');
    await expect(frame.locator('select#grupId')).toBeVisible();
    // El primer option és buit; seleccionem el segon (primer grup disponible).
    await frame.locator('select#grupId').selectOption({ index: 1 });
    const dtRefresh = waitDatatableGrups(grupPage);
    const submitBtn = grupPage.locator('.modal.in .modal-footer button[type="submit"]');
    await submitBtn.waitFor({ state: 'visible' });
    await submitBtn.click();
    await dtRefresh;
    await expectSuccessAlert(grupPage);
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

    test('PROCEDIMENT VISTA', async ({ page }) => {

        await test.step('graella visible amb dades', async () => {
            console.log('  -> graella visible amb dades');
            await expect(getGrid(page)).toBeVisible();
            await expect(getRows(page).first()).toBeVisible();
        });

        await test.step('barra d\'eines visible', async () => {
            console.log('  -> barra d\'eines visible');
            await expect(getToolbar(page)).toBeVisible();
        });

        await test.step('columnes esperades visibles', async () => {
            console.log('  -> columnes esperades visibles');
            const headers = page.locator('#metaexpedients thead th');
            await expect(headers.filter({ hasText: /codi|c.digo/i }).first()).toBeVisible();         // Codi intern / Código interno
            await expect(headers.filter({ hasText: /class?ific/i }).first()).toBeVisible();        // Classificació / Clasificación
            await expect(headers.filter({ hasText: /\bnom\b|nombre/i }).first()).toBeVisible();    // Nom / Nombre
            await expect(headers.filter({ hasText: /documental/i }).first()).toBeVisible();        // Sèrie documental / Serie documental
            await expect(headers.filter({ hasText: /gestor/i }).first()).toBeVisible();            // Òrgan gestor / Órgano gestor
            await expect(headers.filter({ hasText: /revis/i }).first()).toBeVisible();             // Estat revisió / Estado revisión
        });

        await test.step('botons d\'acció per a IPA_ADMIN visibles', async () => {
            console.log('  -> botons d\'acció per a IPA_ADMIN visibles');
            await expect(page.getByRole('link', { name: /nou|nuevo/i })).toBeVisible();
            await expect(page.getByRole('button', { name: /importa/i })).toBeVisible();
        });

        await test.step('formulari de filtre visible', async () => {
            console.log('  -> formulari de filtre visible');
            await expect(getFilterArea(page)).toBeVisible();
        });

        await test.step('files alternes tenen color de fons diferent', async () => {
            console.log('  -> files alternes tenen color de fons diferent');
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

    test('PROCEDIMENT FILTRE', async ({ page }) => {
		
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
        await fila.getByRole('button', { name: /accions|acciones/i }).click();
        await fila.getByRole('link', { name: /esborrar|eliminar|borrar/i }).click();
        await dt;
        await expect(page.locator('#metaexpedients_processing')).toBeHidden();
        await expect(getRows(page)).toHaveCount(0);
    });

    // ── Creació ───────────────────────────────────────────────────────────────

    test('PROCEDIMENT CREAR', async ({ page }) => {

        await test.step('obrir formulari de nou procediment', async () => {
            console.log('  -> obrir formulari de nou procediment');
            await page.getByRole('link', { name: /nou|nuevo/i }).click();
            // S'usa .modal.in per evitar conflicte amb el modal ocult #modal-copied.
            await expect(page.locator('.modal.in')).toBeVisible();
            // Esperar que l'iframe hagi carregat el seu contingut (codi intern visible)
            const frame = page.locator('.modal.in').frameLocator('.modal-body iframe');
            await expect(frame.locator('input[name="codi"]')).toBeVisible();
        });

        await test.step('omplir el formulari dins l\'iframe del modal', async () => {
            console.log('  -> omplir el formulari dins l\'iframe del modal');
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
            console.log('  -> enviar el formulari');
            // El botó submit és clonat al .modal-footer de la pàgina pare (l'original dins l'iframe s'amaga).
            // S'usa button[type="submit"] directe per evitar ambigüitats d'accessible name amb la icona FA.
            const submitBtn = page.locator('.modal.in .modal-footer button[type="submit"]');
            await submitBtn.waitFor({ state: 'visible' });
            await submitBtn.click();
        });

        await test.step('verificar missatge d\'èxit', async () => {
            console.log('  -> verificar missatge d\'èxit');
            await expectSuccessAlert(page);
        });

    });

    // ── Modificació ───────────────────────────────────────────────────────────

    test('PROCEDIMENT MODIFICAR', async ({ page }) => {

        await test.step('filtrar i obrir modal de modificació', async () => {
            console.log('  -> filtrar i obrir modal de modificació');
            await filtrarPerCodi(page, CODI_TEST);
            await expect(getRows(page)).toHaveCount(1);
            const fila = getRows(page).first();
            await fila.getByRole('button', { name: /accions|acciones/i }).click();
            await fila.getByRole('link', { name: /modificar/i }).click();
            await expect(page.locator('.modal.in')).toBeVisible();
            const frame = page.locator('.modal.in').frameLocator('.modal-body iframe');
            await expect(frame.locator('input[name="codi"]')).toBeVisible();
        });

        await test.step('modificar camps dins l\'iframe del modal', async () => {
            console.log('  -> modificar camps dins l\'iframe del modal');
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
            console.log('  -> guardar la modificació');
            const submitBtn = page.locator('.modal.in .modal-footer button[type="submit"]');
            await submitBtn.waitFor({ state: 'visible' });
            await submitBtn.click();
        });

        await test.step('verificar missatge d\'èxit', async () => {
            console.log('  -> verificar missatge d\'èxit');
            await expectSuccessAlert(page);
        });

    });

    // ── Verificació de la modificació ─────────────────────────────────────────

    test('PROCEDIMENT VERIFICAR MODIFICACIO', async ({ page }) => {

        await test.step('filtrar per codi i nom parcial', async () => {
            console.log('  -> filtrar per codi i nom parcial');
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
            console.log('  -> activar filtre de permís directe i verificar resultat');
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

    test('METADOC CREAR', async ({ page }) => {

        const tipusDocsPage = await anarATipusDocs(page);

        await test.step('verificar que la llista de documents està buida', async () => {
            console.log('  -> verificar que la llista de documents està buida');
            await expect(getDocRows(tipusDocsPage)).toHaveCount(0);
        });

        await test.step('crear el primer document', async () => {
            console.log('  -> crear el primer document');
            await crearDocument(tipusDocsPage, CODI_DOC1, NOM_DOC1);
        });

        await test.step('crear el segon document', async () => {
            console.log('  -> crear el segon document');
            await crearDocument(tipusDocsPage, CODI_DOC2, 'document tipus doc pw 2');
        });

        await test.step('verificar que hi ha dos documents', async () => {
            console.log('  -> verificar que hi ha dos documents');
            await expect(getDocRows(tipusDocsPage)).toHaveCount(2);
        });

        await tipusDocsPage.close();
    });

    test('METADOC ACTIVAR', async ({ page }) => {

        const tipusDocsPage = await anarATipusDocs(page);

        await test.step('quickfilter mostra només el document filtrat', async () => {
            console.log('  -> quickfilter mostra només el document filtrat');
            await quickFilterDocs(tipusDocsPage, CODI_DOC1);
            await expect(getDocRows(tipusDocsPage)).toHaveCount(1);
        });

        const fila = getDocRows(tipusDocsPage).first();

        await test.step('obrir menú accions', async () => {
            console.log('  -> obrir menú accions');
            await fila.getByRole('button', { name: /accions|acciones/i }).click();
        });

        const estaActiu = await fila.getByRole('link', { name: /desactivar/i }).isVisible();

        if (estaActiu) {
            await test.step('desactivar el document', async () => {
                console.log('  -> desactivar el document');
                const dt = waitDatatableDocs(tipusDocsPage);
                await fila.getByRole('link', { name: /desactivar/i }).click();
                await dt;
                await expectSuccessAlert(tipusDocsPage);
            });

            await test.step('activar el document', async () => {
                console.log('  -> activar el document');
                await fila.getByRole('button', { name: /accions|acciones/i }).click();
                const dt = waitDatatableDocs(tipusDocsPage);
                await fila.getByRole('link', { name: /activar/i }).click();
                await dt;
                await expectSuccessAlert(tipusDocsPage);
            });
        } else {
            await test.step('activar el document', async () => {
                console.log('  -> activar el document');
                const dt = waitDatatableDocs(tipusDocsPage);
                await fila.getByRole('link', { name: /activar/i }).click();
                await dt;
                await expectSuccessAlert(tipusDocsPage);
            });

            await test.step('desactivar el document', async () => {
                console.log('  -> desactivar el document');
                await fila.getByRole('button', { name: /accions|acciones/i }).click();
                const dt = waitDatatableDocs(tipusDocsPage);
                await fila.getByRole('link', { name: /desactivar/i }).click();
                await dt;
                await expectSuccessAlert(tipusDocsPage);
            });
        }

        await tipusDocsPage.close();
    });

    test('METADOC MODIFICAR', async ({ page }) => {

        const tipusDocsPage = await anarATipusDocs(page);

        await test.step('filtrar i obrir modal de modificació', async () => {
            console.log('  -> filtrar i obrir modal de modificació');
            await quickFilterDocs(tipusDocsPage, CODI_DOC1);
            await expect(getDocRows(tipusDocsPage)).toHaveCount(1);
            const fila = getDocRows(tipusDocsPage).first();
            await fila.getByRole('button', { name: /accions|acciones/i }).click();
            await fila.getByRole('link', { name: /modificar/i }).click();
            await expect(tipusDocsPage.locator('.modal.in')).toBeVisible();
            const frame = tipusDocsPage.locator('.modal.in').frameLocator('.modal-body iframe');
            await expect(frame.locator('input[name="codi"]')).toBeVisible();
        });

        await test.step('modificar camps del document', async () => {
            console.log('  -> modificar camps del document');
            const frame = tipusDocsPage.locator('.modal.in').frameLocator('.modal-body iframe');
            await frame.locator('input[name="nom"]').fill(NOM_DOC1_MOD);
            await frame.locator('textarea[name="descripcio"]').fill(DESC_DOC1_MOD);
            await frame.locator('select[name="multiplicitat"]').selectOption('M_0_N');
        });

        await test.step('guardar la modificació', async () => {
            console.log('  -> guardar la modificació');
            const dtRefresh = waitDatatableDocs(tipusDocsPage);
            const submitBtn = tipusDocsPage.locator('.modal.in .modal-footer button[type="submit"]');
            await submitBtn.waitFor({ state: 'visible' });
            await submitBtn.click();
            await dtRefresh;
            await expectSuccessAlert(tipusDocsPage);
        });

        await tipusDocsPage.close();
    });

    test('METADOC VERIFICAR MODIFICACIO', async ({ page }) => {

        const tipusDocsPage = await anarATipusDocs(page);

        await test.step('quickfilter pel nom modificat mostra el document', async () => {
            console.log('  -> quickfilter pel nom modificat mostra el document');
            await quickFilterDocs(tipusDocsPage, NOM_DOC1_MOD);
            await expect(getDocRows(tipusDocsPage)).toHaveCount(1);
        });

        await tipusDocsPage.close();
    });

    test('METADOC PER DEFECTE', async ({ page }) => {

        const tipusDocsPage = await anarATipusDocs(page);

        await test.step('filtrar i marcar per defecte el primer document', async () => {
            console.log('  -> filtrar i marcar per defecte el primer document');
            await quickFilterDocs(tipusDocsPage, CODI_DOC1);
            await expect(getDocRows(tipusDocsPage)).toHaveCount(1);
            const fila = getDocRows(tipusDocsPage).first();
            await fila.getByRole('button', { name: /accions|acciones/i }).click();
            const dt = waitDatatableDocs(tipusDocsPage);
            await fila.getByRole('link', { name: /marcar (per defecte|por defecto)/i }).click();
            await dt;
            await expectSuccessAlert(tipusDocsPage);
        });

        await tipusDocsPage.close();
    });

    test('METADOC ELIMINAR', async ({ page }) => {

        const tipusDocsPage = await anarATipusDocs(page);

        await test.step('filtrar i eliminar el segon document', async () => {
            console.log('  -> filtrar i eliminar el segon document');
            await quickFilterDocs(tipusDocsPage, CODI_DOC2);
            await expect(getDocRows(tipusDocsPage)).toHaveCount(1);
            const fila = getDocRows(tipusDocsPage).first();
            tipusDocsPage.on('dialog', dialog => dialog.accept());
            await fila.getByRole('button', { name: /accions|acciones/i }).click();
            const dt = waitDatatableDocs(tipusDocsPage);
            await fila.getByRole('link', { name: /esborrar|eliminar|borrar/i }).click();
            await dt;
            await expectSuccessAlert(tipusDocsPage);
            await expect(getDocRows(tipusDocsPage)).toHaveCount(0);
        });

        await tipusDocsPage.close();
    });

    // ── Meta-dades ────────────────────────────────────────────────────────────

    test('METADADA CREAR', async ({ page }) => {

        const metaDadesPage = await anarAMetaDades(page);

        await test.step('verificar que la llista de meta-dades està buida', async () => {
            console.log('  -> verificar que la llista de meta-dades està buida');
            await expect(getMetaRows(metaDadesPage)).toHaveCount(0);
        });

        await test.step('crear la primera meta-dada (camps mínims)', async () => {
            console.log('  -> crear la primera meta-dada (camps mínims)');
            await crearMetaDada(metaDadesPage, CODI_META1, NOM_META1);
        });

        await test.step('crear la segona meta-dada (tots els camps)', async () => {
            console.log('  -> crear la segona meta-dada (tots els camps)');
            await crearMetaDada(metaDadesPage, CODI_META2, 'meta-dada pw 2', true);
        });

        await test.step('verificar que hi ha dos meta-dades', async () => {
            console.log('  -> verificar que hi ha dos meta-dades');
            await expect(getMetaRows(metaDadesPage)).toHaveCount(2);
        });

        await metaDadesPage.close();
    });

    test('METADADA ACTIVAR', async ({ page }) => {

        const metaDadesPage = await anarAMetaDades(page);

        await test.step('quickfilter mostra només la meta-dada filtrada', async () => {
            console.log('  -> quickfilter mostra només la meta-dada filtrada');
            await quickFilterMeta(metaDadesPage, CODI_META1);
            await expect(getMetaRows(metaDadesPage)).toHaveCount(1);
        });

        const fila = getMetaRows(metaDadesPage).first();

        await test.step('obrir menú accions', async () => {
            console.log('  -> obrir menú accions');
            await fila.getByRole('button', { name: /accions|acciones/i }).click();
        });

        const estaActiva = await fila.getByRole('link', { name: /desactivar/i }).isVisible();

        if (estaActiva) {
            await test.step('desactivar la meta-dada', async () => {
                console.log('  -> desactivar la meta-dada');
                const dt = waitDatatableMeta(metaDadesPage);
                await fila.getByRole('link', { name: /desactivar/i }).click();
                await dt;
                await expectSuccessAlert(metaDadesPage);
            });

            await test.step('activar la meta-dada', async () => {
                console.log('  -> activar la meta-dada');
                await fila.getByRole('button', { name: /accions|acciones/i }).click();
                const dt = waitDatatableMeta(metaDadesPage);
                await fila.getByRole('link', { name: /activar/i }).click();
                await dt;
                await expectSuccessAlert(metaDadesPage);
            });
        } else {
            await test.step('activar la meta-dada', async () => {
                console.log('  -> activar la meta-dada');
                const dt = waitDatatableMeta(metaDadesPage);
                await fila.getByRole('link', { name: /activar/i }).click();
                await dt;
                await expectSuccessAlert(metaDadesPage);
            });

            await test.step('desactivar la meta-dada', async () => {
                console.log('  -> desactivar la meta-dada');
                await fila.getByRole('button', { name: /accions|acciones/i }).click();
                const dt = waitDatatableMeta(metaDadesPage);
                await fila.getByRole('link', { name: /desactivar/i }).click();
                await dt;
                await expectSuccessAlert(metaDadesPage);
            });
        }

        await metaDadesPage.close();
    });

    test('METADADA MODIFICAR', async ({ page }) => {

        const metaDadesPage = await anarAMetaDades(page);

        await test.step('filtrar i obrir modal de modificació', async () => {
            console.log('  -> filtrar i obrir modal de modificació');
            await quickFilterMeta(metaDadesPage, CODI_META1);
            await expect(getMetaRows(metaDadesPage)).toHaveCount(1);
            const fila = getMetaRows(metaDadesPage).first();
            await fila.getByRole('button', { name: /accions|acciones/i }).click();
            await fila.getByRole('link', { name: /modificar/i }).click();
            await expect(metaDadesPage.locator('.modal.in')).toBeVisible();
            const frame = metaDadesPage.locator('.modal.in').frameLocator('.modal-body iframe');
            await expect(frame.locator('input[name="codi"]')).toBeVisible();
        });

        await test.step('modificar camps de la meta-dada', async () => {
            console.log('  -> modificar camps de la meta-dada');
            const frame = metaDadesPage.locator('.modal.in').frameLocator('.modal-body iframe');
            await frame.locator('input[name="nom"]').fill(NOM_META1_MOD);
            await frame.locator('textarea[name="descripcio"]').fill(DESC_META1_MOD);
            await frame.locator('select[name="multiplicitat"]').selectOption('M_0_N');
        });

        await test.step('guardar la modificació', async () => {
            console.log('  -> guardar la modificació');
            const dtRefresh = waitDatatableMeta(metaDadesPage);
            const submitBtn = metaDadesPage.locator('.modal.in .modal-footer button[type="submit"]');
            await submitBtn.waitFor({ state: 'visible' });
            await submitBtn.click();
            await dtRefresh;
            await expectSuccessAlert(metaDadesPage);
        });

        await metaDadesPage.close();
    });

    test('METADADA VERIFICAR MODIFICAR', async ({ page }) => {

        const metaDadesPage = await anarAMetaDades(page);

        await test.step('quickfilter pel nom modificat mostra la meta-dada', async () => {
            console.log('  -> quickfilter pel nom modificat mostra la meta-dada');
            await quickFilterMeta(metaDadesPage, NOM_META1_MOD);
            await expect(getMetaRows(metaDadesPage)).toHaveCount(1);
        });

        await metaDadesPage.close();
    });

    test('METADADA ELIMINAR', async ({ page }) => {

        const metaDadesPage = await anarAMetaDades(page);

        await test.step('filtrar i eliminar la segona meta-dada', async () => {
            console.log('  -> filtrar i eliminar la segona meta-dada');
            await quickFilterMeta(metaDadesPage, CODI_META2);
            await expect(getMetaRows(metaDadesPage)).toHaveCount(1);
            const fila = getMetaRows(metaDadesPage).first();
            metaDadesPage.on('dialog', dialog => dialog.accept());
            await fila.getByRole('button', { name: /accions|acciones/i }).click();
            const dt = waitDatatableMeta(metaDadesPage);
            await fila.getByRole('link', { name: /esborrar|eliminar|borrar/i }).click();
            await dt;
            await expectSuccessAlert(metaDadesPage);
            await expect(getMetaRows(metaDadesPage)).toHaveCount(0);
        });

        await metaDadesPage.close();
    });

    // ── Tasques ───────────────────────────────────────────────────────────────

    test('TASCA CREAR', async ({ page }) => {

        const tascaPage = await anarATasques(page);

        await test.step('verificar que la llista de tasques està buida', async () => {
            console.log('  -> verificar que la llista de tasques està buida');
            await expect(getTascaRows(tascaPage)).toHaveCount(0);
        });

        await test.step('crear la primera tasca (camps mínims + responsable)', async () => {
            console.log('  -> crear la primera tasca (camps mínims + responsable)');
            await crearTasca(tascaPage, CODI_TASCA1, NOM_TASCA1);
        });

        await test.step('crear la segona tasca (tots els camps)', async () => {
            console.log('  -> crear la segona tasca (tots els camps)');
            await crearTasca(tascaPage, CODI_TASCA2, 'tasca pw 2', true);
        });

        await test.step('verificar que hi ha dues tasques', async () => {
            console.log('  -> verificar que hi ha dues tasques');
            await expect(getTascaRows(tascaPage)).toHaveCount(2);
        });

        await tascaPage.close();
    });

    test('TASCA ACTIVAR', async ({ page }) => {

        const tascaPage = await anarATasques(page);

        await test.step('quickfilter mostra només la tasca filtrada', async () => {
            console.log('  -> quickfilter mostra només la tasca filtrada');
            await quickFilterTasques(tascaPage, CODI_TASCA1);
            await expect(getTascaRows(tascaPage)).toHaveCount(1);
        });

        const fila = getTascaRows(tascaPage).first();

        await test.step('obrir menú accions', async () => {
            console.log('  -> obrir menú accions');
            await fila.getByRole('button', { name: /accions|acciones/i }).click();
        });

        const estaActiva = await fila.getByRole('link', { name: /desactivar/i }).isVisible();

        if (estaActiva) {
            await test.step('desactivar la tasca', async () => {
                console.log('  -> desactivar la tasca');
                const dt = waitDatatableTasques(tascaPage);
                await fila.getByRole('link', { name: /desactivar/i }).click();
                await dt;
                await expectSuccessAlert(tascaPage);
            });

            await test.step('activar la tasca', async () => {
                console.log('  -> activar la tasca');
                await fila.getByRole('button', { name: /accions|acciones/i }).click();
                const dt = waitDatatableTasques(tascaPage);
                await fila.getByRole('link', { name: /activar/i }).click();
                await dt;
                await expectSuccessAlert(tascaPage);
            });
        } else {
            await test.step('activar la tasca', async () => {
                console.log('  -> activar la tasca');
                const dt = waitDatatableTasques(tascaPage);
                await fila.getByRole('link', { name: /activar/i }).click();
                await dt;
                await expectSuccessAlert(tascaPage);
            });

            await test.step('desactivar la tasca', async () => {
                console.log('  -> desactivar la tasca');
                await fila.getByRole('button', { name: /accions|acciones/i }).click();
                const dt = waitDatatableTasques(tascaPage);
                await fila.getByRole('link', { name: /desactivar/i }).click();
                await dt;
                await expectSuccessAlert(tascaPage);
            });
        }

        await tascaPage.close();
    });

    test('TASCA MODIFICAR', async ({ page }) => {

        const tascaPage = await anarATasques(page);

        await test.step('filtrar i obrir modal de modificació', async () => {
            console.log('  -> filtrar i obrir modal de modificació');
            await quickFilterTasques(tascaPage, CODI_TASCA1);
            await expect(getTascaRows(tascaPage)).toHaveCount(1);
            const fila = getTascaRows(tascaPage).first();
            await fila.getByRole('button', { name: /accions|acciones/i }).click();
            await fila.getByRole('link', { name: /modificar/i }).click();
            await expect(tascaPage.locator('.modal.in')).toBeVisible();
            const frame = tascaPage.locator('.modal.in').frameLocator('.modal-body iframe');
            await expect(frame.locator('input[name="codi"]')).toBeVisible();
        });

        await test.step('modificar camps de la tasca', async () => {
            console.log('  -> modificar camps de la tasca');
            const frame = tascaPage.locator('.modal.in').frameLocator('.modal-body iframe');
            await frame.locator('input[name="nom"]').fill(NOM_TASCA1_MOD);
            await frame.locator('input[name="duracio"]').fill('20');
            await frame.locator('textarea[name="descripcio"]').fill(DESC_TASCA1_MOD);
            await frame.locator('select[name="prioritat"]').selectOption('C_ALTA');
        });

        await test.step('guardar la modificació', async () => {
            console.log('  -> guardar la modificació');
            const dtRefresh = waitDatatableTasques(tascaPage);
            const submitBtn = tascaPage.locator('.modal.in .modal-footer button[type="submit"]');
            await submitBtn.waitFor({ state: 'visible' });
            await submitBtn.click();
            await dtRefresh;
            await expectSuccessAlert(tascaPage);
        });

        await tascaPage.close();
    });

    test('TASCA VERIFICAR MODIFICACIO', async ({ page }) => {

        const tascaPage = await anarATasques(page);

        await test.step('quickfilter pel nom modificat mostra la tasca', async () => {
            console.log('  -> quickfilter pel nom modificat mostra la tasca');
            await quickFilterTasques(tascaPage, NOM_TASCA1_MOD);
            await expect(getTascaRows(tascaPage)).toHaveCount(1);
        });

        await tascaPage.close();
    });

    test('TASCA ELIMINAR', async ({ page }) => {

        const tascaPage = await anarATasques(page);

        await test.step('filtrar i eliminar la segona tasca', async () => {
            console.log('  -> filtrar i eliminar la segona tasca');
            await quickFilterTasques(tascaPage, CODI_TASCA2);
            await expect(getTascaRows(tascaPage)).toHaveCount(1);
            const fila = getTascaRows(tascaPage).first();
            tascaPage.on('dialog', dialog => dialog.accept());
            await fila.getByRole('button', { name: /accions|acciones/i }).click();
            const dt = waitDatatableTasques(tascaPage);
            await fila.getByRole('link', { name: /esborrar|eliminar|borrar/i }).click();
            await dt;
            await expectSuccessAlert(tascaPage);
            await expect(getTascaRows(tascaPage)).toHaveCount(0);
        });

        await tascaPage.close();
    });

    // ── Estats ────────────────────────────────────────────────────────────────

    test('ESTAT CREAR', async ({ page }) => {

        const estatPage = await anarAEstats(page);

        await test.step('verificar que la llista d\'estats està buida', async () => {
            console.log('  -> verificar que la llista d\'estats està buida');
            await expect(getEstatRows(estatPage)).toHaveCount(0);
        });

        await test.step('crear el primer estat', async () => {
            console.log('  -> crear el primer estat');
            await crearEstat(estatPage, CODI_ESTAT1, NOM_ESTAT1);
        });

        await test.step('crear el segon estat', async () => {
            console.log('  -> crear el segon estat');
            await crearEstat(estatPage, CODI_ESTAT2, 'estat pw 2');
        });

        await test.step('verificar que hi ha dos estats', async () => {
            console.log('  -> verificar que hi ha dos estats');
            await expect(getEstatRows(estatPage)).toHaveCount(2);
        });

        await estatPage.close();
    });

    test('ESTAT MODIFICAR', async ({ page }) => {

        const estatPage = await anarAEstats(page);

        await test.step('obrir modal de modificació del primer estat', async () => {
            console.log('  -> obrir modal de modificació del primer estat');
            const fila = getEstatRows(estatPage).filter({ hasText: CODI_ESTAT1 });
            await fila.getByRole('button', { name: /accions|acciones/i }).click();
            await fila.getByRole('link', { name: /modificar/i }).click();
            await expect(estatPage.locator('.modal.in')).toBeVisible();
            const frame = estatPage.locator('.modal.in').frameLocator('.modal-body iframe');
            await expect(frame.locator('input[name="codi"]')).toBeVisible();
        });

        await test.step('modificar nom i activar Inicial', async () => {
            console.log('  -> modificar nom i activar Inicial');
            const frame = estatPage.locator('.modal.in').frameLocator('.modal-body iframe');
            await frame.locator('input[name="nom"]').fill(NOM_ESTAT1_MOD);
            const inicial = frame.locator('input[name="inicial"]');
            if (!await inicial.isChecked()) await inicial.check();
        });

        await test.step('guardar la modificació', async () => {
            console.log('  -> guardar la modificació');
            const dtRefresh = waitDatatableEstats(estatPage);
            const submitBtn = estatPage.locator('.modal.in .modal-footer button[type="submit"]');
            await submitBtn.waitFor({ state: 'visible' });
            await submitBtn.click();
            await dtRefresh;
            await expectSuccessAlert(estatPage);
        });

        await estatPage.close();
    });

    test('ESTAT VERIFICAR MODIFICACIO', async ({ page }) => {

        const estatPage = await anarAEstats(page);

        await test.step('l\'estat modificat apareix al grid', async () => {
            console.log('  -> l\'estat modificat apareix al grid');
            await expect(getEstatRows(estatPage).filter({ hasText: NOM_ESTAT1_MOD })).toHaveCount(1);
        });

        await estatPage.close();
    });

    test('ESTAT ELIMINAR', async ({ page }) => {

        const estatPage = await anarAEstats(page);

        await test.step('eliminar el segon estat', async () => {
            console.log('  -> eliminar el segon estat');
            const fila = getEstatRows(estatPage).filter({ hasText: CODI_ESTAT2 });
            estatPage.on('dialog', dialog => dialog.accept());
            await fila.getByRole('button', { name: /accions|acciones/i }).click();
            const dtRefresh = waitDatatableEstats(estatPage);
            await fila.getByRole('link', { name: /esborrar|eliminar|borrar/i }).click();
            await dtRefresh;
            await expectSuccessAlert(estatPage);
        });

        await estatPage.close();
    });

    // ── Grups ─────────────────────────────────────────────────────────────────

    test('GRUP VINCULAR', async ({ page }) => {

        const grupPage = await anarAGrups(page);
        const countInici = await getGrupRows(grupPage).count();
        console.log('Grups vinculats inicialment: ' + countInici);

        await test.step('vincular un nou grup', async () => {
            console.log('  -> vincular un nou grup');
            await vincularGrupJsp(grupPage);
            //await expect(getGrupRows(grupPage)).toHaveCount(countInici + 1);
            await expectSuccessAlert(grupPage);
        });

        await grupPage.close();
    });

    test('GRUP PER DEFECTE', async ({ page }) => {

        const grupPage = await anarAGrups(page);

        const fila = getGrupRows(grupPage).last();

        await test.step('marcar per defecte el darrer grup', async () => {
            console.log('  -> marcar per defecte el darrer grup');
            await fila.getByRole('button', { name: /accions|acciones/i }).click();
            const esPotMarcar = await fila.getByRole('link', { name: /marcar (per defecte|por defecto)/i }).isVisible();
            if (esPotMarcar) {
                const dt = waitDatatableGrups(grupPage);
                await fila.getByRole('link', { name: /marcar (per defecte|por defecto)/i }).click();
                await dt;
                await expectSuccessAlert(grupPage);
            } else {
                await grupPage.keyboard.press('Escape');
                console.log('  -> el grup ja és per defecte, saltem marcar');
            }
        });

        await grupPage.waitForTimeout(500);

        await test.step('llevar per defecte (esborrar per defecte)', async () => {
            console.log('  -> llevar per defecte');
            await fila.getByRole('button', { name: /accions|acciones/i }).click();
            const dt = waitDatatableGrups(grupPage);
            await fila.getByRole('link', { name: /esborrar per defecte|borrar por defecto/i }).click();
            await dt;
            await expectSuccessAlert(grupPage);
        });

        await grupPage.close();
    });

    test('GRUP DESVINCULAR', async ({ page }) => {

        const grupPage = await anarAGrups(page);

        await test.step('desvincular el darrer grup de la llista', async () => {
            console.log('  -> desvincular el darrer grup');
            const fila = getGrupRows(grupPage).last();
            grupPage.on('dialog', dialog => dialog.accept());
            await fila.getByRole('button', { name: /accions|acciones/i }).click();
            const dt = waitDatatableGrups(grupPage);
            await fila.getByRole('link', { name: /desvincular/i }).click();
            await dt;
            await expectSuccessAlert(grupPage);
        });

        await grupPage.close();
    });

    // ── Activar / Desactivar ──────────────────────────────────────────────────

    test('PROCEDIMENT ACTIVAR', async ({ page }) => {

        await test.step('filtrar per localitzar el procediment de test', async () => {
            console.log('  -> filtrar per localitzar el procediment de test');
            await filtrarPerCodi(page, CODI_TEST);
            await expect(getRows(page)).toHaveCount(1);
        });

        const fila = getRows(page).first();

        // Determinar estat inicial per executar el cicle en l'ordre correcte
        await fila.getByRole('button', { name: /accions|acciones/i }).click();
        const estaActiu = await fila.getByRole('link', { name: /desactivar/i }).isVisible();

        if (estaActiu) {
            await test.step('desactivar el procediment', async () => {
                console.log('  -> desactivar el procediment');
                const dt = waitDatatable(page);
                await fila.getByRole('link', { name: /desactivar/i }).click();
                await dt;
                await expectSuccessAlert(page);
            });

            await test.step('activar el procediment', async () => {
                console.log('  -> activar el procediment');
                await fila.getByRole('button', { name: /accions|acciones/i }).click();
                const dt = waitDatatable(page);
                await fila.getByRole('link', { name: /activar/i }).click();
                await dt;
                await expectSuccessAlert(page);
            });
        } else {
            await test.step('activar el procediment', async () => {
                console.log('  -> activar el procediment');
                const dt = waitDatatable(page);
                await fila.getByRole('link', { name: /activar/i }).click();
                await dt;
                await expectSuccessAlert(page);
            });

            await test.step('desactivar el procediment', async () => {
                console.log('  -> desactivar el procediment');
                await fila.getByRole('button', { name: /accions|acciones/i }).click();
                const dt = waitDatatable(page);
                await fila.getByRole('link', { name: /desactivar/i }).click();
                await dt;
                await expectSuccessAlert(page);
            });
        }

    });

    // ── Eliminar procediment de proves ────────────────────────────────────────

    test('PROCEDIMENT ELIMINAR', async ({ page }) => {

        await filtrarPerCodi(page, CODI_TEST);
        await expect(getRows(page)).toHaveCount(1);

        await test.step('eliminar el procediment de proves', async () => {
            console.log('  -> eliminar el procediment de proves');
            const fila = getRows(page).first();
            page.on('dialog', dialog => dialog.accept());
            const dt = waitDatatable(page);
            await fila.getByRole('button', { name: /accions|acciones/i }).click();
            await fila.getByRole('link', { name: /esborrar|eliminar|borrar/i }).click();
            await dt;
            await expect(page.locator('#metaexpedients_processing')).toBeHidden();
            await expect(getRows(page)).toHaveCount(0);
        });

    });

});
