import { test, expect, Page, Locator } from '@playwright/test';

const DEBUG_ACTIVAT	= true;
const SYSTEM_DELAY	= 500; //milisegons de retard abans de certes accions, NO TOCAR.
const HUMAN_DELAY	= 100; //milisegons de retard entre execució de accions

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

const CODI_DOCMETA1     = 'dmeTADPWREACT01';
const NOM_DOCMETA1      = 'doc meta-dada pw react 1';
const CODI_DOCMETA2     = 'dmeTADPWREACT02';
const NOM_DOCMETA1_MOD  = 'doc meta-dada mod pw react 1';
const DESC_DOCMETA1_MOD = 'desc mod doc meta-dada 1 react';

const CODI_TASCA1    = 'tscPWREACT01';
const NOM_TASCA1     = 'tasca pw react 1';
const CODI_TASCA2    = 'tscPWREACT02';
const NOM_TASCA1_MOD = 'tasca modificada pw react 1';
const DESC_TASCA1_MOD = 'descripció modificada tasca 1 react';

const CODI_ESTAT1    = 'estPWREACT01';
const NOM_ESTAT1     = 'estat pw react 1';
const CODI_ESTAT2    = 'estPWREACT02';
const NOM_ESTAT1_MOD = 'estat modificat pw react 1';

const PRINCIPAL_NOM_TEST = 'ROL_PW_REACT_TEST';

// ── Helpers generals ──────────────────────────────────────────────────────────

const getGrid               = (page: Page) => page.locator('.MuiDataGrid-root').first();
const getRows               = (page: Page) => page.locator('.MuiDataGrid-row');
const getToolbar            = (page: Page) => page.locator('.MuiToolbar-root').filter({ hasText: /nou procediment|nuevo procedimiento/i });
const expectSuccessAlert    = (page: Page) => expect(page.locator('.MuiAlert-standardSuccess')).toBeVisible({ timeout: 10_000 });

// Obre el menú d'accions d'una fila del DataGrid.
// Usa button[aria-haspopup="menu"] dins .MuiDataGrid-actionsCell, independent de l'idioma
// (l'aria-label varia: "more" / "més" / "más" segons la llengua activa).
const obrirMenuAccions = (fila: Locator) =>
    fila.locator('.MuiDataGrid-actionsCell button[aria-haspopup="menu"]').click();

const humanDelay = async (page: Page) => { 
    if (HUMAN_DELAY>0) { 
        logDebug('Esperant ' + HUMAN_DELAY + 'ms'); 
        await page.waitForTimeout(HUMAN_DELAY);
    }
};

const guardaAmbDelay = async (page: Page, locator: Locator) => {
	await page.waitForTimeout(SYSTEM_DELAY);
	await locator.click();
};

const aplicaQuickFilter = async (page: Page, locator: Locator, text: string) => {
	await page.waitForTimeout(SYSTEM_DELAY);
	await locator.fill(text);
	esperarGridCarregat(page);
};

// Clica un MUI Checkbox ocult (opacity:0) a través del wrapper visible MuiButtonBase-root.
// `checkbox` és un selector CSS de l'<input> (p.ex. 'input[name="read"]').
// Assumeix que el checkbox passarà a estat marcat després del clic.
const checkBoxOcultClick = async (dialog: Locator, checkbox: string) => {
    const wrapper = dialog.locator(`span.MuiButtonBase-root:has(${checkbox})`);
    await expect(wrapper).toBeVisible({ timeout: 5_000 });
    await wrapper.click();
    await expect(dialog.locator(checkbox)).toBeChecked({ timeout: 3_000 });
};

// Espera que el DataGrid estigui completament carregat, hagi o no resultats.
//
// Estats observats als HTMLs renderitzats (RENDERS/REACT/GRID/):
//   Loading:     .MuiDataGrid-overlay  PRESENT  (conté el CircularProgress)
//   Amb dades:   .MuiDataGrid-overlay  ABSENT   (.MuiDataGrid-row presents)
//   Sense dades: .MuiDataGrid-overlay  ABSENT   (.MuiDataGrid-overlayWrapper amb "Sense dades")
//
// Condició "carregat" = .MuiDataGrid-root al DOM  AND  .MuiDataGrid-overlay absent.
const esperarGridCarregat = async (page: Page) => {
    logDebug('[Grid] Esperant que el grid estigui completament carregat...');
    await page.waitForFunction(
        () => {
            const grid = document.querySelector('.MuiDataGrid-root');
            if (!grid) return false;                              // Grid encara no muntat
            if (grid.querySelector('.MuiDataGrid-overlay')) return false; // Overlay de càrrega present
            return true;
        },
        { timeout: 15_000 }
    );
    logDebug('[Grid] Carregat OK. Num files: ' + await getRows(page).count());
};

// Registra un listener de resposta GET i, un cop rebuda, espera que el grid hagi acabat
// de renderitzar via esperarGridCarregat.
// IMPORTANT: cridar ABANS de l'acció que dispara la petició (click/goto) per evitar la
// race condition en que la resposta arriba abans que el listener estigui actiu.
//
// urlMatcher: string → coincidència per includes(); funció → predicat personalitzat.
// Usa un predicat quan calgui excloure sub-rutes (p.ex. /metaExpedient/artifacts).
const waitApiGet = async (page: Page, urlMatcher: string | ((url: string) => boolean)) => {
    const urlCheck = typeof urlMatcher === 'string'
        ? (url: string) => url.includes(urlMatcher)
        : urlMatcher;
    logDebug('[API] Listener registrat...');
    const response = await page.waitForResponse(
        resp =>
            urlCheck(resp.url()) &&
            resp.request().method() === 'GET' &&
            resp.status() === 200,
        { timeout: 15_000 }
    );
    logDebug('[API] GET rebut: ' + response.url().split('?')[0] + ' → status ' + response.status());
    await esperarGridCarregat(page);
};

// Registra un listener per esperar la resposta GET de l'apiGetOne d'una entitat concreta.
// En els formularis de modificació, Form.tsx crida apiGetOne(id) i, quan respon, executa
// reset(entityData) que marca isReady=true i renderitza els camps del formulari.
// IMPORTANT: cridar ABANS de l'acció que obre el diàleg de modificació.
const waitApiEntityLoad = (page: Page, id: string | null) =>
    page.waitForResponse(
        resp => !!id &&
                resp.url().endsWith(`/${id}`) &&
                resp.request().method() === 'GET' &&
                resp.status() === 200,
        { timeout: 10_000 }
    );

// Ordinals disponibles per seleccionar opcions d'un desplegable
type OrdinalOpcio = 'first' | 'second' | 'third';
const ORDINAL_IDX: Record<OrdinalOpcio, number> = { first: 0, second: 1, third: 2 };

// Selecciona una opció d'un camp MUI Autocomplete (tipus llista amb cerca):
//   1. Clica l'input per obrir la llista
//   2. Espera que desaparegui el MuiCircularProgress-root (indica càrrega de dades)
//   3. Espera que el listbox contingui almenys una opció visible
//   4. Selecciona l'opció indicada per ordinal
//
// inputSelector: selector CSS de l'<input> del camp (p.ex. '[name="organGestor"] input[type="text"]')
// ordinal:       'first' | 'second' | 'third' (per defecte: 'first')
const seleccionarOpcioAutocompletament = async (
    page: Page,
    container: Page | Locator,
    inputSelector: string,
    ordinal: OrdinalOpcio = 'first'
): Promise<void> => {
    logDebug(`[Autocomplete] Obrint "${inputSelector}", seleccionant: ${ordinal}`);
    await container.locator(inputSelector).click();
    await page.waitForFunction(
        (sel: string) => {
            const input = document.querySelector(sel);
            if (!input) return false;
            const root = input.closest('[class*="MuiAutocomplete-root"]');
            if (!root || root.querySelector('.MuiCircularProgress-root')) return false;
            const listbox = document.querySelector('[role="listbox"]');
            return !!listbox && listbox.querySelectorAll('[role="option"]').length > 0;
        },
        inputSelector,
        { timeout: 15_000 }
    );
    await page.getByRole('option').nth(ORDINAL_IDX[ordinal]).click();
    await page.waitForSelector('[role="listbox"]', { state: 'detached', timeout: 5_000 }).catch(() => {});
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
const triaMuiSelectFirst = async (page: Page, container: Locator, inputName: string, allowBlank: boolean=false) => {
    const muiRoot = container.locator(`.MuiInputBase-root:has(input[name="${inputName}"])`);
    await muiRoot.locator('[role="combobox"]').click();
    await page.waitForSelector('[role="listbox"]', { timeout: 5_000 });
    const opts = page.getByRole('option');
    const firstText = await opts.first().textContent();
    if (!firstText || firstText.trim() === '') {
        if (allowBlank) {
            await opts.first().click();
        } else {
            await opts.nth(1).click();
        }
    } else {
        await opts.first().click();
    }
    await page.waitForSelector('[role="listbox"]', { state: 'detached', timeout: 3_000 }).catch(() => {});
};

// Filtra la llista principal per codi intern i espera la resposta de l'API.
//
// IMPORTANT: el listener s'ha de registrar ABANS de qualsevol interacció al formulari,
// no només abans del click a Filtrar. El motiu: el select "actiu" (triaMuiSelectFirst)
// pot disparar un GET automàtic quan canvia el seu valor (React reactiu). Si el listener
// es registrés després del select, perdria aquesta resposta i el waitForResponse
// esperaria en va 15 s perquè el click a Filtrar no envia una nova petició redundant.
// Registrant-lo primer, es captura el primer GET que arribi (del select o del botó).
const aplicarFiltreProcediments = async (page: Page, codi: string, permisDirecte: boolean) => {
    logDebug('[Filtre] Omplint codi="' + codi + '"' + (permisDirecte ? ' + permisDirecte' : ''));
    // Exclou sub-rutes com /metaExpedient/artifacts que es criden en paral·lel.
    const resp = waitApiGet(page, url => url.includes('/metaExpedient') && !url.includes('/metaExpedient/'));
    logDebug('[Filtre] Listener registrat. Omplint formulari...');
    await page.locator('input[name="codi"]').fill(codi);
    const dialog = page.locator('.styledFilter');
    await triaMuiSelectFirst(page, dialog, 'actiu', true);
    if (permisDirecte) {
        await page.getByRole('button').filter({ hasText: /amb permis directe|con permiso directo/i }).click();
    }
    logDebug('[Filtre] Fent click a Filtrar...');
    await page.getByRole('button', { name: 'Filtrar', exact: true }).click();
    await resp;
    logDebug('[Filtre] Filtre aplicat. Num files resultants: ' + await getRows(page).count());
};

// Navega a la sub-pàgina del procediment de test i activa la pestanya indicada
const anarASubPagina = async (page: Page, tabId: 'metaDocument' | 'metaDada' | 'tasca' | 'estat' | 'grup'): Promise<void> => {
    await aplicarFiltreProcediments(page, CODI_TEST, false);
    await expect(getRows(page)).toHaveCount(1);
    const id = await getRows(page).first().getAttribute('data-id');
    await page.goto(`${URL_PROCEDIMENTS}/${id}/metaDocument`);
    await expect(page.locator('[role="tablist"]')).toBeVisible({ timeout: 10_000 });

    const TAB_LABEL: Record<string, RegExp> = {
        metaDocument: /tipus de doc|tipos de doc/i,
        metaDada:     /meta-dades|metadatos/i,
        estat:        /estats|estados/i,
        tasca:        /tasques|tareas/i,
        grup:         /grups|grupos/i,
    };

    if (tabId !== 'metaDocument') {
        await page.getByRole('tab').filter({ hasText: TAB_LABEL[tabId] }).click();
        await expect(page.locator(`#simple-tabpanel-${tabId}:not([hidden])`)).toBeVisible({ timeout: 5_000 });
    }
    await expect(page.locator(`#simple-tabpanel-${tabId} .MuiDataGrid-root`)).toBeVisible({ timeout: 10_000 });
	//Esperar a que el grid de la pipella del element estigui carregada.
	await esperarGridCarregat(page);
};

const logDebug = (message: string) => { if (DEBUG_ACTIVAT) { console.log(message); } };
const logInfo  = (message: string) => { console.log(message); };

// ── Helpers per a Tipus de Documents ─────────────────────────────────────────

const getDocRows = (page: Page) => page.locator('#simple-tabpanel-metaDocument .MuiDataGrid-row');

const crearDocument = async (page: Page, codi: string, nom: string) => {
	await page.waitForTimeout(SYSTEM_DELAY);
    await page.getByRole('button').filter({ hasText: /nou tipus de document|nuevo tipo de documento/i }).click();
    const dialog = page.locator('[role="dialog"]');
    await expect(dialog).toBeVisible({ timeout: 5_000 });
    // Anar primer a NTI per dues raons:
    // 1. Deixar que qualsevol reset asíncron del formulari (getInitialData.then(reset))
    //    s'executi mentre esperem que els selects NTI es renderitzin.
    // 2. Evitar que el canvi de pestanya posterior perdi els valors de codi/nom
    //    (quan la pestanya Dades s'amaga, els seus inputs poden desregistrar-se i
    //    perdre el valor si react-hook-form té shouldUnregister actiu).
    // Omplim NTI primer i, un cop segurs que el reset ja ha passat, tornem a Dades.
    await dialog.getByRole('tab').filter({ hasText: /dades nti|datos nti/i }).click();
    await expect(dialog.locator('.MuiInputBase-root:has(input[name="ntiOrigen"])')).toBeVisible({ timeout: 5_000 });
    await triaMuiSelectFirst(page, dialog, 'ntiOrigen');
    await triaMuiSelectFirst(page, dialog, 'ntiTipoDocumental');
    await triaMuiSelectFirst(page, dialog, 'ntiEstadoElaboracion');
    // Tornar a Dades i omplir codi/nom just abans de guardar (sense cap canvi de pestanya posterior).
    // No es pot usar getByRole('tab', {name:/^dades$/i}) perquè el MuiBadge afegeix "0" al
    // accessible name real del botó ("Dades0"), fent que el regex no coincideixi.
    // Es selecciona el primer tab del tablist (que sempre és "Dades") per evitar-ho.
    await dialog.locator('[role="tablist"] button[role="tab"]').first().click();
    await expect(dialog.locator('input[name="codi"]')).toBeVisible({ timeout: 5_000 });
    await dialog.locator('input[name="codi"]').fill(codi);
    await dialog.locator('input[name="nom"]').fill(nom);
	await guardaAmbDelay(page, dialog.getByRole('button').filter({ hasText: /guarda/i }));
    //await dialog.getByRole('button').filter({ hasText: /guarda/i }).click();
    await expectSuccessAlert(page);
};

// ── Helpers per a Meta-dades ──────────────────────────────────────────────────

const getMetaRows = (page: Page) => page.locator('#simple-tabpanel-metaDada .MuiDataGrid-row');

const crearMetaDada = async (page: Page, codi: string, nom: string, full = false) => {
	await page.waitForTimeout(SYSTEM_DELAY);
    await page.getByRole('button').filter({ hasText: /nova metadada|nuevo meta-dato/i }).click();
    const dialog = page.locator('[role="dialog"]');
    await expect(dialog).toBeVisible({ timeout: 5_000 });
    await dialog.locator('input[name="codi"]').fill(codi);
    await dialog.locator('input[name="nom"]').fill(nom);
    if (full) {
        await triaMuiSelectFirst(page, dialog, 'multiplicitat', false);
        await dialog.locator('textarea[name="descripcio"]').fill('descripció meta-dada completa 2');
        await dialog.locator('input[name="valorString"]').fill('pw react valor text 2');
        const enviable = dialog.locator('input[name="enviable"]');
        if (await enviable.isVisible()) {
            if (!await enviable.isChecked()) await enviable.click();
            await expect(dialog.locator('input[name="metadadaArxiu"]')).toBeVisible();
            await dialog.locator('input[name="metadadaArxiu"]').fill('metadada_arxiu_pw_2');
        }

    }
	await guardaAmbDelay(page, dialog.getByRole('button').filter({ hasText: /guarda/i }));
    //await dialog.getByRole('button').filter({ hasText: /guarda/i }).click();
    await expectSuccessAlert(page);
};

// ── Helpers per a Meta-dades de Meta-document ────────────────────────────────

const anarAMetaDadesDoc = async (page: Page): Promise<void> => {
    await anarASubPagina(page, 'metaDocument');
    await aplicaQuickFilter(page, page.locator('#simple-tabpanel-metaDocument .MuiToolbar-root input[type="text"]'), CODI_DOC1);
    await expect(getDocRows(page)).toHaveCount(1);
    await getDocRows(page).first().locator('[aria-label="key"]').click();
    await esperarGridCarregat(page);
    await page.waitForTimeout(SYSTEM_DELAY);
};

// ── Helpers per a Estats ──────────────────────────────────────────────────────

const getEstatRows = (page: Page) => page.locator('#simple-tabpanel-estat .MuiDataGrid-row');

const crearEstat = async (page: Page, codi: string, nom: string) => {
	await page.waitForTimeout(SYSTEM_DELAY);
    await page.getByRole('button').filter({ hasText: /nou estat|nuevo estado/i }).click();
    const dialog = page.locator('[role="dialog"]');
    await expect(dialog).toBeVisible({ timeout: 5_000 });
    await dialog.locator('input[name="codi"]').fill(codi);
    await dialog.locator('input[name="nom"]').fill(nom);
	await guardaAmbDelay(page, dialog.getByRole('button').filter({ hasText: /guarda/i }));
    //await dialog.getByRole('button').filter({ hasText: /guarda/i }).click();
    await expectSuccessAlert(page);
};

// ── Helpers per a Grups ───────────────────────────────────────────────────────

const getGrupRows = (page: Page) => page.locator('#simple-tabpanel-grup .MuiDataGrid-row');

const vincularGrup = async (page: Page) => {
    await page.getByRole('button').filter({ hasText: /vincular grup(o)?/i }).click();
    const dialog = page.locator('[role="dialog"]');
    await expect(dialog).toBeVisible({ timeout: 5_000 }).catch(() => { 
        //throw new Error('No s\'ha obert el diàleg de vincular grup');
        logDebug('Error: No s\'ha obert el diàleg de vincular grup');
        page.getByRole('button').filter({ hasText: /vincular grup(o)?/i }).click();
        expect(dialog).toBeVisible({ timeout: 5_000 }).catch(() => { 
            throw new Error('No s\'ha obert el diàleg de vincular grup després de reintentar');
        });
    });
    const grupInput = dialog.locator('[name="grup"] input[type="text"]');
    await grupInput.click();
    await page.waitForSelector('[role="listbox"]', { timeout: 5_000 });
    await page.getByRole('option').first().click();
    await page.waitForTimeout(SYSTEM_DELAY);
    await page.waitForSelector('[role="listbox"]', { state: 'detached', timeout: 3_000 }).catch(() => {});
    await page.waitForTimeout(SYSTEM_DELAY);
	await guardaAmbDelay(page, dialog.getByRole('button').filter({ hasText: /vincula grup(o)?/i }));
    await expectSuccessAlert(page);
};

// ── Helpers per a Tasques ─────────────────────────────────────────────────────

const getTascaRows = (page: Page) => page.locator('#simple-tabpanel-tasca .MuiDataGrid-row');

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
	await page.waitForTimeout(SYSTEM_DELAY);
    await page.getByRole('button').filter({ hasText: /nova tasca|nueva tarea/i }).click();
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
	await guardaAmbDelay(page, dialog.getByRole('button').filter({ hasText: /guarda/i }));
    //await dialog.getByRole('button').filter({ hasText: /guarda/i }).click();
    await expectSuccessAlert(page);
};

// ── Helpers per a Validacions de Tasca ───────────────────────────────────────

const anarAValidacionsTasca = async (page: Page): Promise<void> => {
    await anarASubPagina(page, 'tasca');
    await aplicaQuickFilter(page, page.locator('#simple-tabpanel-tasca .MuiToolbar-root input[type="text"]'), CODI_TASCA1);
    await expect(getTascaRows(page)).toHaveCount(1);
    await getTascaRows(page).first().locator('[aria-label="key"]').click();
    await esperarGridCarregat(page);
    await page.waitForTimeout(SYSTEM_DELAY);
};

const crearValidacioReact = async (page: Page, tipus: 'DADA' | 'DOCUMENT') => {
    await page.waitForTimeout(SYSTEM_DELAY);
    await page.getByRole('button').filter({ hasText: /nova validaci|nueva validaci/i }).click();
    const dialog = page.locator('[role="dialog"]');
    await expect(dialog).toBeVisible({ timeout: 5_000 });
    const matcher = tipus === 'DADA' ? /dato|dada/i : /document/i;
    await triaMuiSelect(page, dialog, 'itemValidacio', matcher);
    const elemName = tipus === 'DADA' ? 'metaDada' : 'metaDocument';
    const elemInput = dialog.locator(`[name="${elemName}"] input[type="text"]`);
    await elemInput.click();
    await page.waitForSelector('[role="listbox"]', { timeout: 5_000 });
    await page.getByRole('option').first().click();
    await page.waitForSelector('[role="listbox"]', { state: 'detached', timeout: 3_000 }).catch(() => {});
    await guardaAmbDelay(page, dialog.getByRole('button').filter({ hasText: /guarda/i }));
    await expectSuccessAlert(page);
};

// ── Helpers per a Permisos ────────────────────────────────────────────────────

const anarAPermisos = async (page: Page): Promise<void> => {
    await aplicarFiltreProcediments(page, CODI_TEST, false);
    await expect(getRows(page)).toHaveCount(1);
    const id = await getRows(page).first().getAttribute('data-id');
    await page.goto(`${URL_PROCEDIMENTS}/${id}/permis`);
    await esperarGridCarregat(page);
    await page.waitForTimeout(SYSTEM_DELAY);
};

// ── Helper: assegurar el rol actiu del menú d'usuari ─────────────────────────
//
// El selector de rol del menú d'usuari té data-testid="user-menu-rol" (afegit a
// UserMenu.tsx). El seu <input> ocult conté el valor intern del rol (p.ex.
// "IPA_ADMIN" o "tothom"); l'opció visible mostra l'etiqueta traduïda (CA).
//
// En canviar de rol, l'app crida POST usuari/actual/changeInfo i, en rebre la
// resposta, navega a '/'. Per això esperem la resposta i que el menú es tanqui.

const ROL_LABEL: Record<string, string> = {
    'IPA_ADMIN': "Administrador d'entitat",
    'tothom':    'Usuari',
};

// Obre el menú d'usuari (capçalera) i retorna el valor intern del rol actiu.
const llegirRolActual = async (page: Page): Promise<string> => {
    await page.getByRole('button', { name: 'auth menu' }).click();
    const selectorRol = page.getByTestId('user-menu-rol');
    await expect(selectorRol).toBeVisible({ timeout: 5_000 });
    return (await selectorRol.locator('input').first().inputValue()).trim();
};

// Comprova que el rol actiu sigui `rolDesitjat` i, si no ho és, el canvia.
//
// urlDesti: pàgina on ha de quedar la sessió després del canvi. En canviar de rol
// l'app redirigeix a la interfície per defecte del nou rol, que pot no ser la
// desitjada (fins i tot pot saltar de React a JSP). Si la URL resultant no és la
// esperada, hi tornem abans de verificar (la verificació depèn de la interfície).
const assegurarRol = async (page: Page, rolDesitjat: string, urlDesti: string): Promise<void> => {
    logInfo(`[Rol] Comprovant que el rol actiu sigui "${rolDesitjat}"...`);
    const rolActual = await llegirRolActual(page);
    logDebug(`[Rol] Rol actiu detectat: "${rolActual}"`);

    if (rolActual === rolDesitjat) {
        logInfo('[Rol] El rol ja és el desitjat; no cal canviar-lo.');
        await page.keyboard.press('Escape'); // tanca el menú d'usuari
        return;
    }

    logInfo(`[Rol] Canviant de "${rolActual}" a "${rolDesitjat}"...`);
    // Clicar el MenuItem del selector obre el desplegable de rols.
    await page.getByTestId('user-menu-rol').click();
    // En aplicar el canvi, l'app fa POST changeInfo i després navega a '/'.
    const respCanvi = page.waitForResponse(
        r => r.url().includes('usuari/actual/changeInfo') &&
             r.request().method() === 'POST' &&
             r.status() === 200,
        { timeout: 10_000 }
    );
    await page.getByRole('option', { name: ROL_LABEL[rolDesitjat], exact: true }).click();
    await respCanvi;
    // El menú es tanca en navegar a '/'.
    await page.locator('#auth-menu').waitFor({ state: 'detached', timeout: 5_000 }).catch(() => {});

    // Si la redirecció ens ha tret de la interfície/pàgina desitjada, hi tornem.
    if (!page.url().includes(urlDesti)) {
        logDebug(`[Rol] Redirigit a ${page.url()}; tornant a ${urlDesti}`);
        await page.goto(urlDesti);
    }

    // Re-verifica que el rol s'ha aplicat correctament.
    const rolNou = await llegirRolActual(page);
    expect(rolNou).toBe(rolDesitjat);
    await page.keyboard.press('Escape');
    logInfo(`[Rol] Rol canviat correctament a "${rolDesitjat}".`);
};

// ─────────────────────────────────────────────────────────────────────────────
// Prerequisit: assegurar el rol actiu (bloc propi SENSE beforeEach)
//
// Es declara abans del bloc principal perquè s'executi primer (ordre del fitxer).
// No comparteix el beforeEach del bloc principal: aquí només cal la capçalera per
// llegir/canviar el rol, no cal carregar ni netejar la graella de procediments.
// ─────────────────────────────────────────────────────────────────────────────

test.describe('Prerequisits — rol IPA_ADMIN', () => {

    test('ROL ASSEGURAR IPA_ADMIN', async ({ page }) => {
        // Navegació mínima per disposar de la capçalera amb el menú d'usuari.
        await page.goto(URL_PROCEDIMENTS);
        await assegurarRol(page, 'IPA_ADMIN', URL_PROCEDIMENTS);
    });

});

// ─────────────────────────────────────────────────────────────────────────────
// Pàgina: Gestió de Procediments REACT  (rol IPA_ADMIN)
// ─────────────────────────────────────────────────────────────────────────────

test.describe('Gestió de Procediments — IPA_ADMIN', () => {

    test.beforeEach(async ({ page }) => {
        // 1. Navegar i esperar càrrega inicial del grid.
        logDebug('[beforeEach] Navegant a ' + URL_PROCEDIMENTS + '...');
        await page.goto(URL_PROCEDIMENTS);
        await esperarGridCarregat(page);
        logDebug('[beforeEach] Càrrega inicial OK.');

        // 2. Netejar filtres de sessió i esperar que el reset sigui efectiu al DOM.
        //    NO usem waitApiGet aquí: el GET de Netejar pot arribar tard (mentre el test
        //    ja ha aplicat el seu propi filtre), sobreescrivint el resultat i causant
        //    errors. En comptes, verifiquem directament l'estat DOM esperat:
        //      - camp codi buit  → el formulari s'ha netejat
        //      - .MuiDataGrid-overlay absent → el grid no està carregant
        //      - almenys 1 fila visible → la recàrrega sense filtre ha acabat
        logDebug('[beforeEach] Fent click a Netejar...');
        await page.getByRole('button').filter({ hasText: /(netejar|limpiar)/i }).click();
        logDebug('[beforeEach] Esperant reset DOM: codi buit + grid carregat amb files...');
        await page.waitForFunction(() => {
            const codi = document.querySelector('input[name="codi"]');
            if (!(codi instanceof HTMLInputElement) || codi.value !== '') return false;
            const grid = document.querySelector('.MuiDataGrid-root');
            if (!grid || grid.querySelector('.MuiDataGrid-overlay')) return false;
            return document.querySelectorAll('.MuiDataGrid-row').length > 0;
        }, { timeout: 15_000 });
        logDebug('[beforeEach] Netejar OK. Num files: ' + await getRows(page).count());
    });

    // ── Disposició ────────────────────────────────────────────────────────────

    test('PROCEDIMENT VISTA', async ({ page }) => {

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
            await expect(headers.filter({ hasText: /codi|c.digo/i }).first()).toBeVisible();
            await expect(headers.filter({ hasText: /class?ific/i }).first()).toBeVisible();
            await expect(headers.filter({ hasText: /\bnom\b|nombre/i }).first()).toBeVisible();
            await expect(headers.filter({ hasText: /documental/i }).first()).toBeVisible();
            await expect(headers.filter({ hasText: /gestor/i }).first()).toBeVisible();
            await expect(headers.filter({ hasText: /revis/i }).first()).toBeVisible();
        });

        await test.step('botons d\'acció per a IPA_ADMIN visibles', async () => {
            logInfo('  -> botons d\'acció per a IPA_ADMIN visibles');
            await expect(page.getByRole('button').filter({ hasText: /nou procediment|nuevo procedimiento/i })).toBeVisible();
            await expect(page.getByRole('button').filter({ hasText: /importa/i })).toBeVisible();
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

    test('PROCEDIMENT FILTRE', async ({ page }) => {

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
            await obrirMenuAccions(fila);
            await page.getByRole('menuitem').filter({ hasText: /esborrar|eliminar|borrar/i }).click();
            // Confirmació de l'esborrat (diàleg MUI)
            await expect(page.locator('[role="dialog"]')).toBeVisible({ timeout: 5_000 });
            await page.locator('[role="dialog"]').getByRole('button').filter({ hasText: /acceptar|aceptar|confirmar|ok/i }).click();
            await expectSuccessAlert(page);
            // Esperar que el grid acabi el seu auto-reload post-esborrat abans de filtrar,
            // per evitar que waitApiGet capturi la resposta de l'auto-reload en lloc de la del filtre.
//            await esperarGridCarregat(page);
//            await aplicarFiltreProcediments(page, CODI_TEST, false);
//            await expect(getRows(page)).toHaveCount(0);
        }     
    });

    // ── Creació ───────────────────────────────────────────────────────────────

    test('PROCEDIMENT CREAR', async ({ page }) => {

        await test.step('obrir formulari de nou procediment', async () => {
            logInfo('  -> obrir formulari de nou procediment');
            await page.getByRole('button').filter({ hasText: /nou procediment|nuevo procedimiento/i }).click();
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
            await seleccionarOpcioAutocompletament(page, dialog, '[name="organGestor"] input[type="text"]');
        });

        await test.step('enviar el formulari', async () => {
            logInfo('  -> enviar el formulari');
			await guardaAmbDelay(page, page.locator('[role="dialog"]').getByRole('button').filter({ hasText: /guarda/i }));
            //await page.locator('[role="dialog"]').getByRole('button').filter({ hasText: /guarda/i }).click();
        });

        await test.step('verificar missatge d\'èxit', async () => {
            logInfo('  -> verificar missatge d\'èxit');
            await expectSuccessAlert(page);
        });

    });

    // ── Modificació ───────────────────────────────────────────────────────────

    test('PROCEDIMENT MODIFICAR', async ({ page }) => {

        await test.step('filtrar i obrir modal de modificació', async () => {
            logInfo('  -> filtrar i obrir modal de modificació');
            await aplicarFiltreProcediments(page, CODI_TEST, false);
            await expect(getRows(page)).toHaveCount(1);
            const fila = getRows(page).first();
            const rowId = await fila.getAttribute('data-id');
            // Registrar ABANS de clicar: Form.tsx fa apiGetOne(id) en obrir la modal de
            // modificació; reset(entityData) renderitza els camps (isReady=true). Si
            // no esperem la resposta, el fill pot arribar abans que reset sobreescrigui.
            const entityResp = waitApiEntityLoad(page, rowId);
			await humanDelay(page);
            await obrirMenuAccions(fila);
			await humanDelay(page);
            await page.getByRole('menuitem').filter({ hasText: /modifica(r)?/i }).click();
			await humanDelay(page);
            await expect(page.locator('[role="dialog"]')).toBeVisible();
            await entityResp;
        });

        await test.step('modificar camps dins el diàleg', async () => {
            logInfo('  -> modificar camps dins el diàleg');
            const dialog = page.locator('[role="dialog"]');
            // Après entityResp, reset(entityData) ja s'ha executat i React renderitza els
            // camps (isReady=true en Form.tsx). Esperem visibilitat per al cicle de render.
            const nomField = dialog.locator('input[name="nom"]');
            await expect(nomField).toBeVisible({ timeout: 3_000 });

            await nomField.fill(NOM_MODIFICAT);
            await dialog.locator('input[name="descripcio"]').fill(DESC_MODIFICADA);

            const checkGestio = dialog.locator('input[name="gestioAmbGrupsActiva"]');
            if (!await checkGestio.isChecked()) await checkGestio.click();

            const checkInteressat = dialog.locator('input[name="interessatObligatori"]');
            if (!await checkInteressat.isChecked()) await checkInteressat.click();

            const checkPermis = dialog.locator('input[name="permisDirecte"]');
            if (!await checkPermis.isChecked()) await checkPermis.click();
        });

        await test.step('guardar la modificació', async () => {
            logInfo('  -> guardar la modificació');
			await guardaAmbDelay(page, page.locator('[role="dialog"]').getByRole('button').filter({ hasText: /guarda/i }));
        });

        await test.step('verificar missatge d\'èxit', async () => {
            logInfo('  -> verificar missatge d\'èxit');
            await expectSuccessAlert(page);
        });

    });

    // ── Verificació de la modificació ─────────────────────────────────────────

    test('PROCEDIMENT VERIFICAR MODIFICACIO', async ({ page }) => {

        await test.step('filtrar per codi, nom parcial i permís directe i verificar resultat', async () => {
            logInfo('  -> filtrar per codi i nom parcial');
            await page.locator('input[name="codi"]').fill(CODI_TEST);
            await page.locator('input[name="nom"]').fill('modificació');
			await humanDelay(page);
			await page.getByRole('button').filter({ hasText: /amb permis directe|con permiso directo/i }).click();
			await humanDelay(page);
			const resp = waitApiGet(page, url => url.includes('/metaExpedient') && !url.includes('/metaExpedient/'));
			await page.getByRole('button', { name: 'Filtrar', exact: true }).click();
			await resp;
			await expect(getRows(page)).toHaveCount(1);
        });

    });

    // ── Tipus de Documents ────────────────────────────────────────────────────

    test('METADOC CREAR', async ({ page }) => {

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

    test('METADOC METADADA CREAR', async ({ page }) => {

        await anarAMetaDadesDoc(page);

        await test.step('verificar que la llista de meta-dades del document és buida', async () => {
            logInfo('  -> verificar que la llista de meta-dades del document és buida');
            await expect(getRows(page)).toHaveCount(0);
        });

        await test.step('crear la primera meta-dada del document (camps mínims)', async () => {
            logInfo('  -> crear la primera meta-dada del document (camps mínims)');
            await crearMetaDada(page, CODI_DOCMETA1, NOM_DOCMETA1);
        });

        await test.step('crear la segona meta-dada del document (tots els camps)', async () => {
            logInfo('  -> crear la segona meta-dada del document (tots els camps)');
            await crearMetaDada(page, CODI_DOCMETA2, 'doc meta-dada pw react 2', true);
        });

        await test.step('verificar que hi ha dos meta-dades al document', async () => {
            logInfo('  -> verificar que hi ha dos meta-dades al document');
            await expect(getRows(page)).toHaveCount(2);
        });

    });

    test('METADOC METADADA MODIFICAR', async ({ page }) => {

        await anarAMetaDadesDoc(page);

        await test.step('filtrar i obrir modal de modificació', async () => {
            logInfo('  -> filtrar i obrir modal de modificació');
            const fila = getRows(page).filter({ hasText: CODI_DOCMETA1 });
            await expect(fila).toHaveCount(1);
            const rowId = await fila.getAttribute('data-id');
            const entityResp = waitApiEntityLoad(page, rowId);
            await obrirMenuAccions(fila);
            await page.getByRole('menuitem').filter({ hasText: /modifica(r)?/i }).click();
            await expect(page.locator('[role="dialog"]')).toBeVisible();
            await entityResp;
        });

        await test.step('modificar camps de la meta-dada del document', async () => {
            logInfo('  -> modificar camps de la meta-dada del document');
            const dialog = page.locator('[role="dialog"]');
            await expect(dialog.locator('input[name="nom"]')).toBeVisible({ timeout: 3_000 });
            await dialog.locator('input[name="nom"]').fill(NOM_DOCMETA1_MOD);
            await dialog.locator('textarea[name="descripcio"]').fill(DESC_DOCMETA1_MOD);
            await triaMuiSelectFirst(page, dialog, 'multiplicitat', false);
        });

        await test.step('guardar la modificació', async () => {
            logInfo('  -> guardar la modificació');
            await guardaAmbDelay(page, page.locator('[role="dialog"]').getByRole('button').filter({ hasText: /guarda/i }));
            await expectSuccessAlert(page);
        });

    });

    test('METADOC METADADA ELIMINAR', async ({ page }) => {

        await anarAMetaDadesDoc(page);

        await test.step('eliminar la segona meta-dada del document', async () => {
            logInfo('  -> eliminar la segona meta-dada del document');
            const fila = getRows(page).filter({ hasText: CODI_DOCMETA2 });
            await expect(fila).toHaveCount(1);
            await obrirMenuAccions(fila);
            await page.getByRole('menuitem').filter({ hasText: /esborrar|eliminar|borrar/i }).click();
            await expect(page.locator('[role="dialog"]')).toBeVisible({ timeout: 5_000 });
            await humanDelay(page);
            await page.locator('[role="dialog"]').getByRole('button').filter({ hasText: /acceptar|aceptar|confirmar|ok/i }).click();
            await expectSuccessAlert(page);
        });

        await test.step('verificar que la meta-dada ha estat eliminada', async () => {
            logInfo('  -> verificar que la meta-dada ha estat eliminada');
            await esperarGridCarregat(page);
            await expect(getRows(page).filter({ hasText: CODI_DOCMETA2 })).toHaveCount(0);
        });

    });

    test('METADOC ACTIVAR', async ({ page }) => {

        await anarASubPagina(page, 'metaDocument');

        await test.step('quickfilter mostra només el document filtrat', async () => {
            logInfo('  -> quickfilter mostra només el document filtrat');
			await aplicaQuickFilter(page, page.locator('#simple-tabpanel-metaDocument .MuiToolbar-root input[type="text"]'), CODI_DOC1);
            await expect(getDocRows(page)).toHaveCount(1);
        });

        const fila = getDocRows(page).first();
        await obrirMenuAccions(fila);
        const estaActiu = await page.getByRole('menuitem').filter({ hasText: /desactivar/i }).isVisible();

        if (estaActiu) {
            await test.step('desactivar el document', async () => {
                logInfo('  -> desactivar el document');
                await page.getByRole('menuitem').filter({ hasText: /desactivar/i }).click();
                await expectSuccessAlert(page);
            });

            await test.step('activar el document', async () => {
                logInfo('  -> activar el document');
                await obrirMenuAccions(fila);
                await page.getByRole('menuitem').filter({ hasText: /activar/i }).click();
                await expectSuccessAlert(page);
            });
        } else {
            await test.step('activar el document', async () => {
                logInfo('  -> activar el document');
                await page.getByRole('menuitem').filter({ hasText: /activar/i }).click();
                await expectSuccessAlert(page);
            });

            await test.step('desactivar el document', async () => {
                logInfo('  -> desactivar el document');
                await obrirMenuAccions(fila);
                await page.getByRole('menuitem').filter({ hasText: /desactivar/i }).click();
                await expectSuccessAlert(page);
            });
        }

    });

    test('METADOC MODIFICAR', async ({ page }) => {

        await anarASubPagina(page, 'metaDocument');

        await test.step('filtrar i obrir modal de modificació', async () => {
            logInfo('  -> filtrar i obrir modal de modificació');
			await aplicaQuickFilter(page, page.locator('#simple-tabpanel-metaDocument .MuiToolbar-root input[type="text"]'), CODI_DOC1);
            await expect(getDocRows(page)).toHaveCount(1);
            const fila = getDocRows(page).first();
            const rowId = await fila.getAttribute('data-id');
            const entityResp = waitApiEntityLoad(page, rowId);
            await obrirMenuAccions(fila);
            await page.getByRole('menuitem').filter({ hasText: /modifica(r)?/i }).click();
            await expect(page.locator('[role="dialog"]')).toBeVisible();
            await entityResp;
        });

        await test.step('modificar camps del document', async () => {
            logInfo('  -> modificar camps del document');
            const dialog = page.locator('[role="dialog"]');
            const nomField = dialog.locator('input[name="nom"]');
            await expect(nomField).toBeVisible({ timeout: 3_000 });
            await nomField.fill(NOM_DOC1_MOD);
            await dialog.locator('textarea[name="descripcio"]').fill(DESC_DOC1_MOD);
            await triaMuiSelectFirst(page, dialog, 'multiplicitat', true);
        });

        await test.step('guardar la modificació', async () => {
            logInfo('  -> guardar la modificació');
			await guardaAmbDelay(page, page.locator('[role="dialog"]').getByRole('button').filter({ hasText: /guarda/i }));
            //await page.locator('[role="dialog"]').getByRole('button').filter({ hasText: /guarda/i }).click();
            await expectSuccessAlert(page);
        });

    });

    test('METADOC VERIFICAR MODIFICACIO', async ({ page }) => {

        await anarASubPagina(page, 'metaDocument');

        await test.step('quickfilter pel nom modificat mostra el document', async () => {
            logInfo('  -> quickfilter pel nom modificat mostra el document');
			await aplicaQuickFilter(page, page.locator('#simple-tabpanel-metaDocument .MuiToolbar-root input[type="text"]'), NOM_DOC1_MOD);
            await expect(getDocRows(page)).toHaveCount(1);
        });

    });

    test('METADOC PER DEFECTE', async ({ page }) => {

        await anarASubPagina(page, 'metaDocument');

        await test.step('filtrar i marcar per defecte el primer document', async () => {
            logInfo('  -> filtrar i marcar per defecte el primer document');
			await aplicaQuickFilter(page, page.locator('#simple-tabpanel-metaDocument .MuiToolbar-root input[type="text"]'), CODI_DOC1);
            await expect(getDocRows(page)).toHaveCount(1);
            const fila = getDocRows(page).first();
            await obrirMenuAccions(fila);
            await page.getByRole('menuitem').filter({ hasText: /per defecte|por defecto/i }).click();
            await expectSuccessAlert(page);
        });

    });

    test('METADOC ELIMINAR', async ({ page }) => {

        await anarASubPagina(page, 'metaDocument');

        await test.step('filtrar i eliminar el segon document', async () => {
            logInfo('  -> filtrar i eliminar el segon document');
			await aplicaQuickFilter(page, page.locator('#simple-tabpanel-metaDocument .MuiToolbar-root input[type="text"]'), CODI_DOC2);
            await expect(getDocRows(page)).toHaveCount(1);
            const fila = getDocRows(page).first();
            await obrirMenuAccions(fila);
            await page.getByRole('menuitem').filter({ hasText: /esborrar|eliminar|borrar/i }).click();
            await expect(page.locator('[role="dialog"]')).toBeVisible({ timeout: 5_000 });
            await humanDelay(page);
            await page.locator('[role="dialog"]').getByRole('button').filter({ hasText: /acceptar|aceptar|confirmar|ok/i }).click();
            await expectSuccessAlert(page);
//            await expect(getDocRows(page)).toHaveCount(0);
        });

    });

    // ── Meta-dades ────────────────────────────────────────────────────────────

    test('METADADA CREAR', async ({ page }) => {

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

    test('METADADA ACTIVAR', async ({ page }) => {

        await anarASubPagina(page, 'metaDada');

        await test.step('quickfilter mostra només la meta-dada filtrada', async () => {
            logInfo('  -> quickfilter mostra només la meta-dada filtrada');
			await aplicaQuickFilter(page, page.locator('#simple-tabpanel-metaDada .MuiToolbar-root input[type="text"]'), CODI_META1);
            await expect(getMetaRows(page)).toHaveCount(1);
        });

        const fila = getMetaRows(page).first();
        await obrirMenuAccions(fila);
        const estaActiva = await page.getByRole('menuitem').filter({ hasText: /desactivar/i }).isVisible();

        if (estaActiva) {
            await test.step('desactivar la meta-dada', async () => {
                logInfo('  -> desactivar la meta-dada');
                await page.getByRole('menuitem').filter({ hasText: /desactivar/i }).click();
                await expectSuccessAlert(page);
            });

            await test.step('activar la meta-dada', async () => {
                logInfo('  -> activar la meta-dada');
                await obrirMenuAccions(fila);
                await page.getByRole('menuitem').filter({ hasText: /activar/i }).click();
                await expectSuccessAlert(page);
            });
        } else {
            await test.step('activar la meta-dada', async () => {
                logInfo('  -> activar la meta-dada');
                await page.getByRole('menuitem').filter({ hasText: /activar/i }).click();
                await expectSuccessAlert(page);
            });

            await test.step('desactivar la meta-dada', async () => {
                logInfo('  -> desactivar la meta-dada');
                await obrirMenuAccions(fila);
                await page.getByRole('menuitem').filter({ hasText: /desactivar/i }).click();
                await expectSuccessAlert(page);
            });
        }

    });

    test('METADADA MODIFICAR', async ({ page }) => {

        await anarASubPagina(page, 'metaDada');

        await test.step('filtrar i obrir modal de modificació', async () => {
            logInfo('  -> filtrar i obrir modal de modificació');
			await aplicaQuickFilter(page, page.locator('#simple-tabpanel-metaDada .MuiToolbar-root input[type="text"]'), CODI_META1);
            await expect(getMetaRows(page)).toHaveCount(1);
            const fila = getMetaRows(page).first();
            const rowId = await fila.getAttribute('data-id');
            const entityResp = waitApiEntityLoad(page, rowId);
            await obrirMenuAccions(fila);
            await page.getByRole('menuitem').filter({ hasText: /modifica(r)?/i }).click();
            await expect(page.locator('[role="dialog"]')).toBeVisible();
            await entityResp;
        });

        await test.step('modificar camps de la meta-dada', async () => {
            logInfo('  -> modificar camps de la meta-dada');
            const dialog = page.locator('[role="dialog"]');
            const nomField = dialog.locator('input[name="nom"]');
            await expect(nomField).toBeVisible({ timeout: 3_000 });
            await nomField.fill(NOM_META1_MOD);
            await dialog.locator('textarea[name="descripcio"]').fill(DESC_META1_MOD);
            await triaMuiSelectFirst(page, dialog, 'multiplicitat', false);
        });

        await test.step('guardar la modificació', async () => {
            logInfo('  -> guardar la modificació');
			await guardaAmbDelay(page, page.locator('[role="dialog"]').getByRole('button').filter({ hasText: /guarda/i }));
            //await page.locator('[role="dialog"]').getByRole('button').filter({ hasText: /guarda/i }).click();
            await expectSuccessAlert(page);
        });

    });

    test('METADADA VERIFICAR MODIFICAR', async ({ page }) => {

        await anarASubPagina(page, 'metaDada');

        await test.step('quickfilter pel nom modificat mostra la meta-dada', async () => {
            logInfo('  -> quickfilter pel nom modificat mostra la meta-dada');
			await aplicaQuickFilter(page, page.locator('#simple-tabpanel-metaDada .MuiToolbar-root input[type="text"]'), NOM_META1_MOD);
            await expect(getMetaRows(page)).toHaveCount(1);
        });

    });

    test('METADADA ELIMINAR', async ({ page }) => {

        await anarASubPagina(page, 'metaDada');

        await test.step('filtrar i eliminar la segona meta-dada', async () => {
            logInfo('  -> filtrar i eliminar la segona meta-dada');
			await aplicaQuickFilter(page, page.locator('#simple-tabpanel-metaDada .MuiToolbar-root input[type="text"]'), CODI_META2);
            await expect(getMetaRows(page)).toHaveCount(1);
            const fila = getMetaRows(page).first();
            await obrirMenuAccions(fila);
            await page.getByRole('menuitem').filter({ hasText: /esborrar|eliminar|borrar/i }).click();
            await expect(page.locator('[role="dialog"]')).toBeVisible({ timeout: 5_000 });
            await page.locator('[role="dialog"]').getByRole('button').filter({ hasText: /acceptar|aceptar|confirmar|ok/i }).click();
            await expectSuccessAlert(page);
//            await expect(getMetaRows(page)).toHaveCount(0);
        });

    });

    // ── Tasques ───────────────────────────────────────────────────────────────

    test('TASCA CREAR', async ({ page }) => {

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

    test('TASCA VALIDACIO CREAR', async ({ page }) => {

        await anarAValidacionsTasca(page);

        await test.step('verificar que la llista de validacions és buida', async () => {
            logInfo('  -> verificar que la llista de validacions és buida');
            await expect(getRows(page)).toHaveCount(0);
        });

        await test.step('crear validació de tipus DADA', async () => {
            logInfo('  -> crear validació de tipus DADA');
            await crearValidacioReact(page, 'DADA');
        });

        await test.step('crear validació de tipus DOCUMENT', async () => {
            logInfo('  -> crear validació de tipus DOCUMENT');
            await crearValidacioReact(page, 'DOCUMENT');
        });

        await test.step('verificar que hi ha dues validacions', async () => {
            logInfo('  -> verificar que hi ha dues validacions');
            await esperarGridCarregat(page);
            await expect(getRows(page)).toHaveCount(2);
        });

    });

    test('TASCA VALIDACIO MODIFICAR', async ({ page }) => {

        await anarAValidacionsTasca(page);

        await test.step('filtrar i obrir modal de modificació de la validació DADA', async () => {
            logInfo('  -> filtrar i obrir modal de modificació de la validació DADA');
            const fila = getRows(page).filter({ hasText: /dato|dada/i }).first();
            await expect(fila).toBeVisible({ timeout: 5_000 });
            const rowId = await fila.getAttribute('data-id');
            const entityResp = waitApiEntityLoad(page, rowId);
            await obrirMenuAccions(fila);
            await page.getByRole('menuitem').filter({ hasText: /modifica(r)?/i }).click();
            await expect(page.locator('[role="dialog"]')).toBeVisible();
            await entityResp;
        });

        await test.step('re-seleccionar la meta-dada i guardar', async () => {
            logInfo('  -> re-seleccionar la meta-dada i guardar');
            const dialog = page.locator('[role="dialog"]');
            await seleccionarOpcioAutocompletament(page, dialog, '[name="metaDada"] input[type="text"]');
            await guardaAmbDelay(page, dialog.getByRole('button').filter({ hasText: /guarda/i }));
            await expectSuccessAlert(page);
        });

    });

    test('TASCA VALIDACIO ELIMINAR', async ({ page }) => {

        await anarAValidacionsTasca(page);

        await test.step('eliminar la validació de tipus DOCUMENT', async () => {
            logInfo('  -> eliminar la validació de tipus DOCUMENT');
            const fila = getRows(page).filter({ hasText: /document/i }).first();
            await expect(fila).toBeVisible({ timeout: 5_000 });
            await obrirMenuAccions(fila);
            await page.getByRole('menuitem').filter({ hasText: /esborrar|eliminar|borrar/i }).click();
            await expect(page.locator('[role="dialog"]')).toBeVisible({ timeout: 5_000 });
            await humanDelay(page);
            await page.locator('[role="dialog"]').getByRole('button').filter({ hasText: /acceptar|aceptar|confirmar|ok/i }).click();
            await expectSuccessAlert(page);
        });

        await test.step('verificar que la validació ha estat eliminada', async () => {
            logInfo('  -> verificar que la validació ha estat eliminada');
            await esperarGridCarregat(page);
            await expect(getRows(page).filter({ hasText: /document/i })).toHaveCount(0);
        });

    });

    test('TASCA ACTIVAR', async ({ page }) => {

        await anarASubPagina(page, 'tasca');

        await test.step('quickfilter mostra només la tasca filtrada', async () => {
            logInfo('  -> quickfilter mostra només la tasca filtrada');
			await aplicaQuickFilter(page, page.locator('#simple-tabpanel-tasca .MuiToolbar-root input[type="text"]'), CODI_TASCA1);
            await expect(getTascaRows(page)).toHaveCount(1);
        });

        const fila = getTascaRows(page).first();
        await obrirMenuAccions(fila);
        const estaActiva = await page.getByRole('menuitem').filter({ hasText: /desactivar/i }).isVisible();

        if (estaActiva) {
            await test.step('desactivar la tasca', async () => {
                logInfo('  -> desactivar la tasca');
                await page.getByRole('menuitem').filter({ hasText: /desactivar/i }).click();
                await expectSuccessAlert(page);
            });

            await test.step('activar la tasca', async () => {
                logInfo('  -> activar la tasca');
                await obrirMenuAccions(fila);
                await page.getByRole('menuitem').filter({ hasText: /activar/i }).click();
                await expectSuccessAlert(page);
            });
        } else {
            await test.step('activar la tasca', async () => {
                logInfo('  -> activar la tasca');
                await page.getByRole('menuitem').filter({ hasText: /activar/i }).click();
                await expectSuccessAlert(page);
            });

            await test.step('desactivar la tasca', async () => {
                logInfo('  -> desactivar la tasca');
                await obrirMenuAccions(fila);
                await page.getByRole('menuitem').filter({ hasText: /desactivar/i }).click();
                await expectSuccessAlert(page);
            });
        }

    });

    test('TASCA MODIFICAR', async ({ page }) => {

        await anarASubPagina(page, 'tasca');

        await test.step('filtrar i obrir modal de modificació', async () => {
            logInfo('  -> filtrar i obrir modal de modificació');
			await aplicaQuickFilter(page, page.locator('#simple-tabpanel-tasca .MuiToolbar-root input[type="text"]'), CODI_TASCA1);
            await expect(getTascaRows(page)).toHaveCount(1);
            const fila = getTascaRows(page).first();
            const rowId = await fila.getAttribute('data-id');
            const entityResp = waitApiEntityLoad(page, rowId);
            await obrirMenuAccions(fila);
            await page.getByRole('menuitem').filter({ hasText: /modifica(r)?/i }).click();
            await expect(page.locator('[role="dialog"]')).toBeVisible();
            await entityResp;
        });

        await test.step('modificar camps de la tasca', async () => {
            logInfo('  -> modificar camps de la tasca');
            const dialog = page.locator('[role="dialog"]');
            const nomField = dialog.locator('input[name="nom"]');
            await expect(nomField).toBeVisible({ timeout: 3_000 });
            await nomField.fill(NOM_TASCA1_MOD);
            await dialog.locator('input[name="duracio"]').clear();
            await dialog.locator('input[name="duracio"]').fill('20');
            await dialog.locator('textarea[name="descripcio"]').fill(DESC_TASCA1_MOD);
            await triaMuiSelect(page, dialog, 'prioritat', /alta/i);
        });

        await test.step('guardar la modificació', async () => {
            logInfo('  -> guardar la modificació');
			await guardaAmbDelay(page, page.locator('[role="dialog"]').getByRole('button').filter({ hasText: /guarda/i }));
            //await page.locator('[role="dialog"]').getByRole('button').filter({ hasText: /guarda/i }).click();
            await expectSuccessAlert(page);
        });

    });

    test('TASCA VERIFICAR MODIFICACIO', async ({ page }) => {

        await anarASubPagina(page, 'tasca');

        await test.step('quickfilter pel nom modificat mostra la tasca', async () => {
            logInfo('  -> quickfilter pel nom modificat mostra la tasca');
			await aplicaQuickFilter(page, page.locator('#simple-tabpanel-tasca .MuiToolbar-root input[type="text"]'), NOM_TASCA1_MOD);
            await expect(getTascaRows(page)).toHaveCount(1);
        });

    });

    test('TASCA ELIMINAR', async ({ page }) => {

        await anarASubPagina(page, 'tasca');

        await test.step('filtrar i eliminar la segona tasca', async () => {
            logInfo('  -> filtrar i eliminar la segona tasca');
			await aplicaQuickFilter(page, page.locator('#simple-tabpanel-tasca .MuiToolbar-root input[type="text"]'), CODI_TASCA2);
            await expect(getTascaRows(page)).toHaveCount(1);
            const fila = getTascaRows(page).first();
            await obrirMenuAccions(fila);
            await page.getByRole('menuitem').filter({ hasText: /esborrar|eliminar|borrar/i }).click();
            await expect(page.locator('[role="dialog"]')).toBeVisible({ timeout: 5_000 });
            await page.locator('[role="dialog"]').getByRole('button').filter({ hasText: /acceptar|aceptar|confirmar|ok/i }).click();
            await expectSuccessAlert(page);
//            await expect(getTascaRows(page)).toHaveCount(0);
        });

    });

    // ── Estats ────────────────────────────────────────────────────────────────

    test('ESTAT CREAR', async ({ page }) => {

        await anarASubPagina(page, 'estat');

        await test.step('verificar que la llista d\'estats està buida', async () => {
            logInfo('  -> verificar que la llista d\'estats està buida');
            await expect(getEstatRows(page)).toHaveCount(0);
        });

        await test.step('crear el primer estat (camps mínims)', async () => {
            logInfo('  -> crear el primer estat (camps mínims)');
            await crearEstat(page, CODI_ESTAT1, NOM_ESTAT1);
        });

        await test.step('crear el segon estat', async () => {
            logInfo('  -> crear el segon estat');
            await crearEstat(page, CODI_ESTAT2, 'estat pw react 2');
        });

        await test.step('verificar que hi ha dos estats', async () => {
            logInfo('  -> verificar que hi ha dos estats');
            await expect(getEstatRows(page)).toHaveCount(2);
        });

    });

    test('ESTAT MODIFICAR', async ({ page }) => {

        await anarASubPagina(page, 'estat');

        await test.step('obrir modal de modificació del primer estat', async () => {
            logInfo('  -> obrir modal de modificació del primer estat');
            const fila = getEstatRows(page).filter({ hasText: CODI_ESTAT1 });
            const rowId = await fila.getAttribute('data-id');
            const entityResp = waitApiEntityLoad(page, rowId);
            await obrirMenuAccions(fila);
            await page.getByRole('menuitem').filter({ hasText: /modifica(r)?/i }).click();
            await expect(page.locator('[role="dialog"]')).toBeVisible();
            await entityResp;
        });

        await test.step('modificar nom i activar Inicial', async () => {
            logInfo('  -> modificar nom i activar Inicial');
            const dialog = page.locator('[role="dialog"]');
            const nomField = dialog.locator('input[name="nom"]');
            await expect(nomField).toBeVisible({ timeout: 3_000 });
            await nomField.fill(NOM_ESTAT1_MOD);
            const inicial = dialog.locator('input[name="inicial"]');
            if (!await inicial.isChecked()) await inicial.click();
        });

        await test.step('guardar la modificació', async () => {
            logInfo('  -> guardar la modificació');
			await guardaAmbDelay(page, page.locator('[role="dialog"]').getByRole('button').filter({ hasText: /guarda/i }));
            //await page.locator('[role="dialog"]').getByRole('button').filter({ hasText: /guarda/i }).click();
            await expectSuccessAlert(page);
        });

    });

    test('ESTAT VERIFICAR MODIFICACIO', async ({ page }) => {

        await anarASubPagina(page, 'estat');

        await test.step('l\'estat modificat apareix al grid', async () => {
            logInfo('  -> l\'estat modificat apareix al grid');
            await expect(getEstatRows(page).filter({ hasText: NOM_ESTAT1_MOD })).toHaveCount(1);
        });

    });

    test('ESTAT ELIMINAR', async ({ page }) => {

        await anarASubPagina(page, 'estat');

        await test.step('eliminar el segon estat', async () => {
            logInfo('  -> eliminar el segon estat');
            const fila = getEstatRows(page).filter({ hasText: CODI_ESTAT2 });
            await obrirMenuAccions(fila);
            await page.getByRole('menuitem').filter({ hasText: /esborrar|eliminar|borrar/i }).click();
            await expect(page.locator('[role="dialog"]')).toBeVisible({ timeout: 5_000 });
            await page.locator('[role="dialog"]').getByRole('button').filter({ hasText: /acceptar|aceptar|confirmar|ok/i }).click();
            await expectSuccessAlert(page);
//            await expect(getEstatRows(page).filter({ hasText: CODI_ESTAT2 })).toHaveCount(0);
        });

    });

    // ── Grups ─────────────────────────────────────────────────────────────────

    test('GRUP VINCULAR', async ({ page }) => {

        await anarASubPagina(page, 'grup');

        const countInici = await getGrupRows(page).count();
        logDebug('Grups vinculats inicialment: ' + countInici);

        await test.step('vincular un nou grup', async () => {
            logInfo('  -> vincular un nou grup');
            await vincularGrup(page);
            await expect(getGrupRows(page)).toHaveCount(countInici + 1);
        });

    });

    test('GRUP PER DEFECTE', async ({ page }) => {

        await anarASubPagina(page, 'grup');

        const fila = getGrupRows(page).last();

        await test.step('marcar per defecte el darrer grup', async () => {
            logInfo('  -> marcar per defecte el darrer grup');
            await obrirMenuAccions(fila);
            const esPotMarcar = await page.getByRole('menuitem').filter({ hasText: /marcar (per defecte|por defecto)/i }).isVisible();
            if (esPotMarcar) {
                await page.getByRole('menuitem').filter({ hasText: /marcar (per defecte|por defecto)/i }).click();
                await expectSuccessAlert(page);
            } else {
                await page.keyboard.press('Escape');
                logInfo('  -> el grup ja és per defecte, saltem marcar');
            }
        });

        await page.waitForTimeout(SYSTEM_DELAY);

        await test.step('llevar per defecte', async () => {
            logInfo('  -> llevar per defecte');
            await obrirMenuAccions(fila);
            await page.getByRole('menuitem').filter({ hasText: /llevar per defecte|quitar por defecto/i }).click();
            await expectSuccessAlert(page);
        });

    });

    test('GRUP DESVINCULAR', async ({ page }) => {
        await anarASubPagina(page, 'grup');
        await test.step('desvincular el darrer grup de la llista', async () => {
            logInfo('  -> desvincular el darrer grup');
            const fila = getGrupRows(page).last();
            await obrirMenuAccions(fila);
            await page.getByRole('menuitem').filter({ hasText: /desvincular/i }).click();
            await expectSuccessAlert(page);
        });

    });

    // ── Permisos ─────────────────────────────────────────────────────────────

    test('PROCEDIMENT PERMIS ASSIGNAR', async ({ page }) => {

        await anarAPermisos(page);

        await test.step('crear un nou permís de rol', async () => {
            logInfo('  -> crear un nou permís de rol');
            await page.getByRole('button').filter({ hasText: /nou perm|nuevo perm/i }).click();
            const dialog = page.locator('[role="dialog"]');
            await expect(dialog).toBeVisible({ timeout: 5_000 });
            await triaMuiSelect(page, dialog, 'principal', /rol/i);
            await dialog.locator('input[name="sid"]').fill(PRINCIPAL_NOM_TEST);
            await dialog.locator('span.MuiButtonBase-root:has(input[name="read"])').click();
            await expect(dialog.locator('input[name="read"]')).toBeChecked({ timeout: 3_000 });
            await guardaAmbDelay(page, dialog.getByRole('button').filter({ hasText: /guarda/i }));
            await expectSuccessAlert(page);
        });

        await test.step('verificar que el permís apareix a la taula', async () => {
            logInfo('  -> verificar que el permís apareix a la taula');
            await esperarGridCarregat(page);
            await expect(getRows(page).filter({ hasText: PRINCIPAL_NOM_TEST })).toHaveCount(1);
        });

    });

    test('PROCEDIMENT PERMIS MODIFICAR', async ({ page }) => {

        await anarAPermisos(page);

        await test.step('marcar tots els permisos', async () => {
            logInfo('  -> marcar tots els permisos');
            const fila = getRows(page).filter({ hasText: PRINCIPAL_NOM_TEST });
            await expect(fila).toHaveCount(1);
            await obrirMenuAccions(fila);
            await page.getByRole('menuitem').filter({ hasText: /modifica(r)?/i }).click();
            const dialog = page.locator('[role="dialog"]');
            await expect(dialog).toBeVisible({ timeout: 5_000 });
            await checkBoxOcultClick(dialog, 'input[name="all"]');
            await guardaAmbDelay(page, dialog.getByRole('button').filter({ hasText: /guarda|modifica/i }));
            await expectSuccessAlert(page);
        });

    });

    test('PROCEDIMENT PERMIS ELIMINAR', async ({ page }) => {

        await anarAPermisos(page);

        await test.step('eliminar el permís de rol de test', async () => {
            logInfo('  -> eliminar el permís de rol de test');
            const fila = getRows(page).filter({ hasText: PRINCIPAL_NOM_TEST });
            await obrirMenuAccions(fila);
            await page.getByRole('menuitem').filter({ hasText: /esborrar|eliminar|borrar/i }).click();
            await page.locator('[role="dialog"]').getByRole('button').filter({ hasText: /acceptar|aceptar|confirmar|ok/i }).click();
            await expectSuccessAlert(page);
        });

        await test.step('verificar que el permís ha estat eliminat', async () => {
            logInfo('  -> verificar que el permís ha estat eliminat');
            await esperarGridCarregat(page);
            await expect(getRows(page).filter({ hasText: PRINCIPAL_NOM_TEST })).toHaveCount(0);
        });

    });

    // ── Activar / Desactivar ──────────────────────────────────────────────────

    test('PROCEDIMENT ACTIVAR', async ({ page }) => {

        await test.step('filtrar per localitzar el procediment de test', async () => {
            logInfo('  -> filtrar per localitzar el procediment de test');
            await aplicarFiltreProcediments(page, CODI_TEST, true);
            await expect(getRows(page)).toHaveCount(1);
        });

        const fila = getRows(page).first();
        await obrirMenuAccions(fila);
        const estaActiu = await page.getByRole('menuitem').filter({ hasText: /desactivar/i }).isVisible();

        if (estaActiu) {
            await test.step('desactivar el procediment', async () => {
                logInfo('  -> desactivar el procediment');
                await page.getByRole('menuitem').filter({ hasText: /desactivar/i }).click();
                await expectSuccessAlert(page);
            });

            await test.step('activar el procediment', async () => {
                logInfo('  -> activar el procediment');
                await obrirMenuAccions(fila);
                await page.getByRole('menuitem').filter({ hasText: /activar/i }).click();
                await expectSuccessAlert(page);
            });
        } else {
            await test.step('activar el procediment', async () => {
                logInfo('  -> activar el procediment');
                await page.getByRole('menuitem').filter({ hasText: /activar/i }).click();
                await expectSuccessAlert(page);
            });

            await test.step('desactivar el procediment', async () => {
                logInfo('  -> desactivar el procediment');
                await obrirMenuAccions(fila);
                await page.getByRole('menuitem').filter({ hasText: /desactivar/i }).click();
                await expectSuccessAlert(page);
            });
        }

    });

    // ── Eliminar procediment de proves ────────────────────────────────────────

    test('PROCEDIMENT ELIMINAR', async ({ page }) => {

        await aplicarFiltreProcediments(page, CODI_TEST, false);
        await expect(getRows(page)).toHaveCount(1);

        await test.step('eliminar el procediment de proves', async () => {
            logInfo('  -> eliminar el procediment de proves');
            const fila = getRows(page).first();
            await humanDelay(page);
            await obrirMenuAccions(fila);
            await humanDelay(page);
            await page.getByRole('menuitem').filter({ hasText: /esborrar|eliminar|borrar/i }).click();
            await expect(page.locator('[role="dialog"]')).toBeVisible({ timeout: 5_000 });
            await humanDelay(page);
            await page.locator('[role="dialog"]').getByRole('button').filter({ hasText: /acceptar|aceptar|confirmar|ok/i }).click();
            await expectSuccessAlert(page);
        });

    });

});
