import { expect, Page, Locator } from '@playwright/test';

// ─────────────────────────────────────────────────────────────────────────────
// Utilitats compartides pels tests E2E de la interfície REACT (Material UI).
//
// Conté constants, logging i helpers genèrics (graella MUI DataGrid, diàlegs,
// formularis i filtres, esperes d'API, selecció en Autocomplete) i el helper de
// canvi de rol del menú d'usuari.
// Els helpers específics de cada pàgina (getToolbar, filtres concrets, etc.) es
// mantenen al seu propi spec.
// ─────────────────────────────────────────────────────────────────────────────

// ── Constants ───────────────────────────────────────────────────────────────
export const DEBUG_ACTIVAT = true;
export const SYSTEM_DELAY   = 500; // milisegons de retard abans de certes accions, NO TOCAR.
export const HUMAN_DELAY    = 100; // milisegons de retard entre execució de accions

// ── Logging ───────────────────────────────────────────────────────────────────
export const logDebug = (message: string) => { if (DEBUG_ACTIVAT) { console.log(message); } };
export const logInfo  = (message: string) => { console.log(message); };

// ── Retard "humà" entre accions ────────────────────────────────────────────────
export const humanDelay = async (page: Page) => {
    if (HUMAN_DELAY > 0) { await page.waitForTimeout(HUMAN_DELAY); }
};

// ── Graella (MUI DataGrid) ──────────────────────────────────────────────────────
export const getGrid = (page: Page) => page.locator('.MuiDataGrid-root').first();
export const getRows = (page: Page) => page.locator('.MuiDataGrid-row');

export const expectSuccessAlert = (page: Page) =>
    expect(page.locator('.MuiAlert-standardSuccess')).toBeVisible({ timeout: 10_000 });

// Obre el menú d'accions d'una fila del DataGrid.
// Usa button[aria-haspopup="menu"] dins .MuiDataGrid-actionsCell, independent de l'idioma
// (l'aria-label varia: "more" / "més" / "más" segons la llengua activa).
export const obrirMenuAccions = (fila: Locator) =>
    fila.locator('.MuiDataGrid-actionsCell button[aria-haspopup="menu"]').click();

// Clica un botó que obre un diàleg i espera que aparegui. Alguns diàlegs (FormActionDialog)
// no s'obren si el clic arriba abans que el component estigui llest: es torna a clicar.
// Retorna el diàleg.
export const obrirDialegAmbReintent = async (page: Page, boto: Locator, intents: number = 3): Promise<Locator> => {
    const dialog = page.locator('[role="dialog"]');
    for (let i = 1; i < intents; i++) {
        await boto.click();
        if (await dialog.waitFor({ state: 'visible', timeout: 3_000 }).then(() => true).catch(() => false)) {
            return dialog;
        }
        logDebug(`[Diàleg] No s'ha obert (intent ${i}), es torna a clicar...`);
    }
    await boto.click();
    await expect(dialog).toBeVisible({ timeout: 5_000 });
    return dialog;
};

// Accepta el diàleg MUI de confirmació obert (esborrats, etc.).
export const confirmarDialeg = async (page: Page) => {
    const dialog = page.locator('[role="dialog"]');
    await expect(dialog).toBeVisible({ timeout: 5_000 });
    await dialog.getByRole('button').filter({ hasText: /accepta|acepta|confirma|ok/i }).click();
};

// ── Formularis i filtres ─────────────────────────────────────────────────────────

// Espera SYSTEM_DELAY abans de clicar (p.ex. el botó Guarda d'un diàleg), perquè els
// camps acabin de propagar el seu valor al formulari.
export const guardaAmbDelay = async (page: Page, locator: Locator) => {
    await page.waitForTimeout(SYSTEM_DELAY);
    await locator.click();
};

// Clica un MUI Checkbox ocult (opacity:0) a través del wrapper visible MuiButtonBase-root.
// `checkbox` és un selector CSS de l'<input> (p.ex. 'input[name="read"]').
// Assumeix que el checkbox passarà a estat marcat després del clic.
export const checkBoxOcultClick = async (dialog: Locator, checkbox: string) => {
    const wrapper = dialog.locator(`span.MuiButtonBase-root:has(${checkbox})`);
    await expect(wrapper).toBeVisible({ timeout: 5_000 });
    await wrapper.click();
    await expect(dialog.locator(checkbox)).toBeChecked({ timeout: 3_000 });
};

// Interacciona amb un MUI Select (native input ocult) i selecciona una opció per text/regex
export const triaMuiSelect = async (page: Page, container: Locator, inputName: string, matcher: string | RegExp) => {
    const muiRoot = container.locator(`.MuiInputBase-root:has(input[name="${inputName}"])`);
    await muiRoot.locator('[role="combobox"]').click();
    await page.waitForSelector('[role="listbox"]', { timeout: 5_000 });
    await page.getByRole('option', { name: matcher }).first().click();
    await page.waitForSelector('[role="listbox"]', { state: 'detached', timeout: 3_000 }).catch(() => {});
};

// Clica el botó "Neteja" del filtre de la pàgina i espera que el reset sigui efectiu al DOM.
//
// NO usa waitApiGet: el GET de Netejar pot arribar tard (mentre el test ja ha aplicat el
// seu propi filtre), sobreescrivint el resultat i causant errors. En comptes, verifica
// directament l'estat DOM esperat:
//   - camp `campBuit` buit           → el formulari s'ha netejat
//   - .MuiDataGrid-overlay absent    → el grid no està carregant
//   - almenys 1 fila visible         → la recàrrega sense filtre ha acabat
export const netejarFiltre = async (page: Page, campBuit: string = 'codi') => {
    logDebug('[Filtre] Fent click a Netejar...');
    // Nom accessible exacte i ancorat: evita col·lisió amb "Neteja la selecció"
    // (botó de la selecció del DataGrid), que també conté "Neteja".
    await page.getByRole('button', { name: /^(Neteja|Limpia)$/ }).click();
    logDebug('[Filtre] Esperant reset DOM: ' + campBuit + ' buit + grid carregat amb files...');
    await page.waitForFunction((camp: string) => {
        const input = document.querySelector(`input[name="${camp}"]`);
        if (!(input instanceof HTMLInputElement) || input.value !== '') return false;
        const grid = document.querySelector('.MuiDataGrid-root');
        if (!grid || grid.querySelector('.MuiDataGrid-overlay')) return false;
        return document.querySelectorAll('.MuiDataGrid-row').length > 0;
    }, campBuit, { timeout: 15_000 });
    logDebug('[Filtre] Netejar OK. Num files: ' + await getRows(page).count());
};

// Espera que el DataGrid estigui completament carregat, hagi o no resultats.
// Condició "carregat" = .MuiDataGrid-root al DOM  AND  .MuiDataGrid-overlay absent.
export const esperarGridCarregat = async (page: Page) => {
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

// ── Esperes d'API ───────────────────────────────────────────────────────────────

// Registra un listener de resposta GET i, un cop rebuda, espera que el grid hagi acabat
// de renderitzar via esperarGridCarregat.
// IMPORTANT: cridar ABANS de l'acció que dispara la petició (click/goto) per evitar la
// race condition en que la resposta arriba abans que el listener estigui actiu.
//
// urlMatcher: string → coincidència per includes(); funció → predicat personalitzat.
// Usa un predicat quan calgui excloure sub-rutes (p.ex. /metaExpedient/artifacts).
export const waitApiGet = async (page: Page, urlMatcher: string | ((url: string) => boolean)) => {
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

// Escriu `text` al filtre ràpid de la barra d'eines de la graella i espera la resposta
// d'aquest text concret (paràmetre quickFilter). El camp té debounce: esperar només
// "una" resposta podria agafar la d'un text anterior. Amb text buit, espera la
// resposta sense quickFilter.
// esLlistat: predicat de la URL del GET del llistat (p.ex. pathname acabat en '/entitats').
// IMPORTANT: si el text és el mateix que ja hi ha al camp, no hi ha petició nova.
export const aplicarFiltreRapid = async (page: Page, text: string, esLlistat: (url: string) => boolean) => {
    logDebug(`[Filtre ràpid] "${text}"`);
    const resp = waitApiGet(page, url =>
        esLlistat(url) && (new URL(url).searchParams.get('quickFilter') ?? '') === text);
    await page.getByRole('textbox', { name: /filtre r.pid|filtro r.pido/i }).fill(text);
    await resp;
};

// Registra un listener per esperar la resposta GET de l'apiGetOne d'una entitat concreta.
// En els formularis de modificació, Form.tsx crida apiGetOne(id) i, quan respon, executa
// reset(entityData) que marca isReady=true i renderitza els camps del formulari.
// IMPORTANT: cridar ABANS de l'acció que obre el diàleg de modificació.
export const waitApiEntityLoad = (page: Page, id: string | null) =>
    page.waitForResponse(
        resp => {
            if (!id) return false;
            // Comparar pel pathname, no per la URL completa: algunes entitats (p.ex.
            // metaDocument) carreguen amb query string (?perspectives=...), i un
            // endsWith(`/${id}`) sobre la URL sencera no hi coincidiria.
            let pathname: string;
            try { pathname = new URL(resp.url()).pathname; } catch { return false; }
            return pathname.endsWith(`/${id}`) &&
                   resp.request().method() === 'GET' &&
                   resp.status() === 200;
        },
        { timeout: 10_000 }
    );

// ── MUI Autocomplete ─────────────────────────────────────────────────────────────

// Ordinals disponibles per seleccionar opcions d'un desplegable
export type OrdinalOpcio = 'first' | 'second' | 'third';
export const ORDINAL_IDX: Record<OrdinalOpcio, number> = { first: 0, second: 1, third: 2 };

// Selecciona una opció d'un camp MUI Autocomplete (tipus llista amb cerca):
//   1. Clica l'input per obrir la llista
//   2. Espera que desaparegui el MuiCircularProgress-root (indica càrrega de dades)
//   3. Espera que el listbox contingui almenys una opció visible
//   4. Selecciona l'opció indicada per ordinal
//
// inputSelector: selector CSS de l'<input> del camp (p.ex. '[name="organGestor"] input[type="text"]')
// ordinal:       'first' | 'second' | 'third' (per defecte: 'first')
// Retorna el text que mostra el camp després de la selecció.
export const seleccionarOpcioAutocompletament = async (
    page: Page,
    container: Page | Locator,
    inputSelector: string,
    ordinal: OrdinalOpcio = 'first'
): Promise<string> => {
    logDebug(`[Autocomplete] Obrint "${inputSelector}", seleccionant: ${ordinal}`);
    // En buidar un camp amb valor, MUI obre el desplegable tot sol: si ja és obert, un clic
    // a l'input el tancaria.
    if (await container.locator(inputSelector).getAttribute('aria-expanded') !== 'true') {
        await container.locator(inputSelector).click();
    }
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
    return container.locator(inputSelector).inputValue();
};

// Com seleccionarOpcioAutocompletament, però escrivint `cerca` al camp (el backend filtra
// les opcions pel quickFilter) i triant l'opció que té exactament el text `textOpcio`.
// Útil quan cal triar un valor conegut (p.ex. l'òrgan d'un registre creat abans): l'ordre
// de les opcions pot no ser el mateix en camps diferents (formulari vs filtre).
// Retorna el text que mostra el camp després de la selecció.
export const seleccionarOpcioAutocompletamentPerText = async (
    page: Page,
    container: Page | Locator,
    inputSelector: string,
    cerca: string,
    textOpcio: string
): Promise<string> => {
    logDebug(`[Autocomplete] Cercant "${cerca}" a "${inputSelector}", seleccionant: ${textOpcio}`);
    const input = container.locator(inputSelector);
    await input.click();
    await input.fill(cerca);
    const opcio = page.getByRole('option', { name: textOpcio, exact: true });
    await expect(opcio).toBeVisible({ timeout: 15_000 });
    await opcio.click();
    await page.waitForSelector('[role="listbox"]', { state: 'detached', timeout: 5_000 }).catch(() => {});
    return input.inputValue();
};

// ── Rol (menú d'usuari REACT) ────────────────────────────────────────────────────
//
// El selector de rol del menú d'usuari té data-testid="user-menu-rol" (afegit a
// UserMenu.tsx). El seu <input> ocult conté el valor intern del rol (p.ex.
// "IPA_ADMIN" o "tothom"); l'opció visible mostra l'etiqueta traduïda (CA).
//
// En canviar de rol, l'app crida POST usuari/actual/changeInfo i, en rebre la
// resposta, navega a '/'. Per això esperem la resposta i que el menú es tanqui.

export const ROL_LABEL: Record<string, string> = {
    'IPA_ADMIN': "Administrador d'entitat",
    'IPA_SUPER': 'Superusuari',
    'tothom':    'Usuari',
};

// Obre el menú d'usuari (capçalera) i retorna el valor intern del rol actiu.
export const llegirRolActual = async (page: Page): Promise<string> => {
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
export const assegurarRol = async (page: Page, rolDesitjat: string, urlDesti: string): Promise<void> => {
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
