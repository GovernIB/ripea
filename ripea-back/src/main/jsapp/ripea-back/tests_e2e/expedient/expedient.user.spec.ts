import { test, expect, Page, Locator } from '@playwright/test';

// ─────────────────────────────────────────────────────────────────────────────
// Gestió d'Expedients REACT  (rol usuari base / tramitador)
//
// Cobreix el cicle de vida d'un expedient des de la pàgina /expedient:
//   - Vista del llistat (graella, barra d'eines, columnes, formulari de filtre)
//   - Filtre + neteja prèvia (esborra restes de proves anteriors)
//   - Creació d'un expedient nou
//   - Modificació del títol
//   - Verificació de la modificació
//   - Detall de l'expedient (/contingut/:id)
//   - Eliminació
//
// Convencions reutilitzades de metaExpedient.admin.spec.ts: helpers d'espera de
// graella (overlay), espera de respostes API abans d'accions, i selectors
// bilingües (català/castellà) per ser robustos davant l'idioma actiu.
// ─────────────────────────────────────────────────────────────────────────────

const DEBUG_ACTIVAT  = true;
const SYSTEM_DELAY   = 500; // milisegons de retard abans de certes accions, NO TOCAR.
const HUMAN_DELAY    = 100; // milisegons de retard entre execució de accions

const URL_EXPEDIENTS = '/ripeaback/reactapp/expedient';

// Prefix comú per a tots els títols de prova: permet netejar restes amb un sol filtre.
const PREFIX_TEST    = 'zz_PLAYWRIGHT_EXP_REACT';
const NOM_TEST       = `${PREFIX_TEST}_zz`;
const NOM_MODIFICAT  = `${PREFIX_TEST}_MOD_zz`;

// ── Helpers generals ──────────────────────────────────────────────────────────

const logDebug = (message: string) => { if (DEBUG_ACTIVAT) { console.log(message); } };
const logInfo  = (message: string) => { console.log(message); };

const getGrid            = (page: Page) => page.locator('.MuiDataGrid-root').first();
const getRows            = (page: Page) => page.locator('.MuiDataGrid-row');
const getToolbar         = (page: Page) => page.locator('.MuiToolbar-root').filter({ hasText: /nou expedient|nuevo expediente/i });
const expectSuccessAlert = (page: Page) => expect(page.locator('.MuiAlert-standardSuccess')).toBeVisible({ timeout: 10_000 });

// Obre el menú d'accions d'una fila del DataGrid (botó "més" de la cel·la d'accions).
const obrirMenuAccions = (fila: Locator) =>
    fila.locator('.MuiDataGrid-actionsCell button[aria-haspopup="menu"]').click();

const humanDelay = async (page: Page) => {
    if (HUMAN_DELAY > 0) { await page.waitForTimeout(HUMAN_DELAY); }
};

// Espera que el DataGrid estigui completament carregat, hagi o no resultats.
// Condició "carregat" = .MuiDataGrid-root al DOM  AND  .MuiDataGrid-overlay absent.
const esperarGridCarregat = async (page: Page) => {
    logDebug('[Grid] Esperant que el grid estigui completament carregat...');
    await page.waitForFunction(
        () => {
            const grid = document.querySelector('.MuiDataGrid-root');
            if (!grid) return false;                                        // Grid encara no muntat
            if (grid.querySelector('.MuiDataGrid-overlay')) return false;   // Overlay de càrrega present
            return true;
        },
        { timeout: 15_000 }
    );
    logDebug('[Grid] Carregat OK. Num files: ' + await getRows(page).count());
};

// Llistat d'expedients: GET /api/expedients?... (exclou sub-rutes com /artifacts o /{id}).
const esGetLlistat = (url: string) => url.includes('/api/expedients') && !url.includes('/expedients/');

// Registra un listener de resposta GET i, un cop rebuda, espera que el grid acabi
// de renderitzar. IMPORTANT: cridar ABANS de l'acció que dispara la petició.
const waitApiGet = async (page: Page, urlMatcher: (url: string) => boolean) => {
    logDebug('[API] Listener GET registrat...');
    const response = await page.waitForResponse(
        resp => urlMatcher(resp.url()) && resp.request().method() === 'GET' && resp.status() === 200,
        { timeout: 15_000 }
    );
    logDebug('[API] GET rebut: ' + response.url().split('?')[0] + ' → status ' + response.status());
    await esperarGridCarregat(page);
};

// Espera la resposta GET de l'apiGetOne d'un expedient concret (GET /api/expedients/{id}).
// En obrir la modal de modificació, Form.tsx crida apiGetOne(id) i, quan respon,
// reset(entityData) renderitza els camps. Cridar ABANS d'obrir el diàleg.
const waitApiEntityLoad = (page: Page, id: string | null) =>
    page.waitForResponse(
        resp => !!id &&
                resp.url().endsWith(`/${id}`) &&
                resp.request().method() === 'GET' &&
                resp.status() === 200,
        { timeout: 10_000 }
    );

// Selecciona la primera opció d'un MUI Autocomplete (camp tipus llista amb cerca):
//   1. Clica l'input per obrir la llista
//   2. Espera que desaparegui el CircularProgress (càrrega de dades)
//   3. Espera que el listbox tingui almenys una opció
//   4. Selecciona la primera opció
const seleccionarPrimeraOpcioAutocompletament = async (
    page: Page,
    container: Page | Locator,
    inputSelector: string
): Promise<void> => {
    logDebug(`[Autocomplete] Obrint "${inputSelector}" i seleccionant la primera opció`);
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
    await page.getByRole('option').first().click();
    await page.waitForSelector('[role="listbox"]', { state: 'detached', timeout: 5_000 }).catch(() => {});
};

// Filtra el llistat d'expedients pel títol i espera la resposta del llistat.
// El filtre conserva l'estat per defecte "Obert" (estat != TANCAT), de manera que
// els expedients acabats de crear (oberts) sempre hi apareixen.
const filtrarPerTitol = async (page: Page, titol: string) => {
    logDebug('[Filtre] Filtrant per títol="' + titol + '"');
    // Omplir el camp ABANS de registrar el listener i clicar Filtra: així ens assegurem
    // que el valor ja està compromès al formulari i que el GET que esperem és el del
    // botó Filtra (no pas el de la càrrega inicial en muntar-se el filtre).
    await page.locator('input[name="nom"]').fill(titol);
    await humanDelay(page);
    const resp = waitApiGet(page, esGetLlistat);
    await page.locator('.styledFilter').getByRole('button', { name: 'Filtra', exact: true }).click();
    await resp;
    logDebug('[Filtre] Filtre aplicat. Num files resultants: ' + await getRows(page).count());
};

// Esborra una fila d'expedient (obre el menú d'accions, clica Eliminar i confirma).
const eliminarFila = async (page: Page, fila: Locator) => {
    await obrirMenuAccions(fila);
    await page.getByRole('menuitem').filter({ hasText: /eliminar/i }).click();
    const dialog = page.locator('[role="dialog"]');
    await expect(dialog).toBeVisible({ timeout: 5_000 });
    await humanDelay(page);
    await dialog.getByRole('button').filter({ hasText: /accepta|acepta|confirmar|ok/i }).click();
    await expectSuccessAlert(page);
    await esperarGridCarregat(page);
};

// ── Helper: assegurar el rol actiu del menú d'usuari ─────────────────────────
//
// El selector de rol del menú d'usuari té data-testid="user-menu-rol" (afegit a
// UserMenu.tsx). El seu <input> ocult conté el valor intern del rol (p.ex.
// "tothom" o "IPA_ADMIN"); l'opció visible mostra l'etiqueta traduïda (CA).
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
// llegir/canviar el rol, no cal carregar la graella d'expedients.
// ─────────────────────────────────────────────────────────────────────────────

test.describe('Prerequisits — rol usuari base', () => {

    test('ROL ASSEGURAR USUARI', async ({ page }) => {
        // Navegació mínima per disposar de la capçalera amb el menú d'usuari.
        await page.goto(URL_EXPEDIENTS);
        await assegurarRol(page, 'tothom', URL_EXPEDIENTS);
    });

});

// ─────────────────────────────────────────────────────────────────────────────
// Pàgina: Gestió d'Expedients REACT  (rol usuari base)
// ─────────────────────────────────────────────────────────────────────────────

test.describe('Gestió d\'Expedients — usuari base', () => {

    test.beforeEach(async ({ page }) => {
        logDebug('[beforeEach] Navegant a ' + URL_EXPEDIENTS + '...');
        await page.goto(URL_EXPEDIENTS);
        // En muntar-se, el filtre dispara automàticament la cerca amb l'estat per
        // defecte "Obert"; esperem que la graella acabi la càrrega inicial.
        await esperarGridCarregat(page);
        logDebug('[beforeEach] Càrrega inicial OK.');
    });

    // ── Vista / disposició ──────────────────────────────────────────────────────

    test('EXPEDIENT VISTA', async ({ page }) => {

        await test.step('graella visible', async () => {
            logInfo('  -> graella visible');
            await expect(getGrid(page)).toBeVisible();
        });

        await test.step('barra d\'eines amb "Nou expedient" visible', async () => {
            logInfo('  -> barra d\'eines amb "Nou expedient" visible');
            await expect(getToolbar(page)).toBeVisible();
            await expect(page.getByRole('button').filter({ hasText: /nou expedient|nuevo expediente/i })).toBeVisible();
        });

        await test.step('columnes esperades visibles', async () => {
            logInfo('  -> columnes esperades visibles');
            const headers = page.locator('.MuiDataGrid-columnHeaderTitle');
            await expect(headers.filter({ hasText: /n.mero/i }).first()).toBeVisible();
            await expect(headers.filter({ hasText: /procediment|procedimiento/i }).first()).toBeVisible();
            await expect(headers.filter({ hasText: /t.tol|t.tulo/i }).first()).toBeVisible();
            await expect(headers.filter({ hasText: /estat|estado/i }).first()).toBeVisible();
            await expect(headers.filter({ hasText: /prioritat|prioridad/i }).first()).toBeVisible();
        });

        await test.step('formulari de filtre visible', async () => {
            logInfo('  -> formulari de filtre visible');
            await expect(page.locator('input[name="numero"]')).toBeVisible();
            await expect(page.locator('input[name="nom"]')).toBeVisible();
            await expect(page.locator('.styledFilter').getByRole('button', { name: 'Filtra', exact: true })).toBeVisible();
        });

    });

    // ── Filtre + neteja prèvia ────────────────────────────────────────────────

    test('EXPEDIENT FILTRE', async ({ page }) => {

        // Filtra pel prefix comú (el filtre "nom" és un LIKE → captura NOM_TEST i NOM_MODIFICAT)
        await filtrarPerTitol(page, PREFIX_TEST);

        // Només es consideren les files que contenen el prefix de prova: així mai
        // s'esborra un expedient real, encara que el filtre de servidor no s'hagués
        // aplicat (els expedients reals no tenen el prefix al títol).
        const filesProva = () => getRows(page).filter({ hasText: PREFIX_TEST });

        let count = await filesProva().count();
        logDebug('Expedients de prova existents: ' + count);
        if (count === 0) {
            logInfo("No existeix cap expedient de prova, l'entorn ja és net");
            return;
        }

        logInfo("Existeixen expedients de prova → esborrar-los per deixar l'entorn net");
        // Esborra'ls un a un (la graella es recarrega després de cada esborrat).
        while (count > 0) {
            await humanDelay(page);
            await eliminarFila(page, filesProva().first());
            count = await filesProva().count();
            logDebug('Files de prova restants: ' + count);
        }
    });

    // ── Creació ─────────────────────────────────────────────────────────────────

    test('EXPEDIENT CREAR', async ({ page }) => {

        await test.step('obrir formulari de nou expedient', async () => {
            logInfo('  -> obrir formulari de nou expedient');
            await page.getByRole('button').filter({ hasText: /nou expedient|nuevo expediente/i }).click();
            await expect(page.locator('[role="dialog"]')).toBeVisible();
        });

        await test.step('omplir el formulari', async () => {
            logInfo('  -> omplir el formulari');
            const dialog = page.locator('[role="dialog"]');

            // Procediment o servei: primer disponible. En seleccionar-lo, l'òrgan gestor
            // i la seqüència s'omplen automàticament; la prioritat queda "Normal" per defecte.
            await seleccionarPrimeraOpcioAutocompletament(page, dialog, '[name="metaExpedient"] input[type="text"]');
            // Esperar que l'òrgan gestor s'hagi autoemplenat abans de continuar.
            await expect(dialog.locator('[name="organGestor"] input[type="text"]')).not.toHaveValue('', { timeout: 10_000 });

            await dialog.locator('input[name="nom"]').fill(NOM_TEST);
        });

        await test.step('enviar el formulari', async () => {
            logInfo('  -> enviar el formulari');
            await page.waitForTimeout(SYSTEM_DELAY);
            await page.locator('[role="dialog"]').getByRole('button').filter({ hasText: /guarda/i }).click();
        });

        await test.step('verificar missatge d\'èxit', async () => {
            logInfo('  -> verificar missatge d\'èxit');
            await expectSuccessAlert(page);
        });

        await test.step('verificar que l\'expedient apareix al llistat', async () => {
            logInfo('  -> verificar que l\'expedient apareix al llistat');
            await filtrarPerTitol(page, NOM_TEST);
            await expect(getRows(page).filter({ hasText: NOM_TEST })).toHaveCount(1);
        });

    });

    // ── Modificació ─────────────────────────────────────────────────────────────

    test('EXPEDIENT MODIFICAR', async ({ page }) => {

        await test.step('filtrar i obrir modal de modificació', async () => {
            logInfo('  -> filtrar i obrir modal de modificació');
            await filtrarPerTitol(page, NOM_TEST);
            const fila = getRows(page).filter({ hasText: NOM_TEST });
            await expect(fila).toHaveCount(1);
            const rowId = await fila.getAttribute('data-id');
            // Registrar ABANS de clicar: en obrir la modal, Form.tsx fa apiGetOne(id) i
            // reset(entityData) renderitza els camps. Esperem la resposta abans d'omplir.
            const entityResp = waitApiEntityLoad(page, rowId);
            await humanDelay(page);
            await obrirMenuAccions(fila);
            await humanDelay(page);
            await page.getByRole('menuitem').filter({ hasText: /modificar/i }).click();
            await expect(page.locator('[role="dialog"]')).toBeVisible();
            await entityResp;
        });

        await test.step('modificar el títol', async () => {
            logInfo('  -> modificar el títol');
            const dialog = page.locator('[role="dialog"]');
            const nomField = dialog.locator('input[name="nom"]');
            await expect(nomField).toBeVisible({ timeout: 3_000 });
            await nomField.fill(NOM_MODIFICAT);
        });

        await test.step('guardar la modificació', async () => {
            logInfo('  -> guardar la modificació');
            await page.waitForTimeout(SYSTEM_DELAY);
            // El botó del diàleg de modificació és "Modifica" (CA) / "Modificar" (ES).
            await page.locator('[role="dialog"]').getByRole('button').filter({ hasText: /modifica/i }).click();
        });

        await test.step('verificar missatge d\'èxit', async () => {
            logInfo('  -> verificar missatge d\'èxit');
            await expectSuccessAlert(page);
        });

    });

    // ── Verificació de la modificació ───────────────────────────────────────────

    test('EXPEDIENT VERIFICAR MODIFICACIO', async ({ page }) => {

        await test.step('filtrar pel nou títol i verificar resultat', async () => {
            logInfo('  -> filtrar pel nou títol i verificar resultat');
            await filtrarPerTitol(page, NOM_MODIFICAT);
            await expect(getRows(page).filter({ hasText: NOM_MODIFICAT })).toHaveCount(1);
        });

    });

    // ── Detall de l'expedient ─────────────────────────────────────────────────────

    test('EXPEDIENT DETALL', async ({ page }) => {

        await test.step('filtrar i obrir el detall de l\'expedient', async () => {
            logInfo('  -> filtrar i obrir el detall de l\'expedient');
            await filtrarPerTitol(page, NOM_MODIFICAT);
            const fila = getRows(page).filter({ hasText: NOM_MODIFICAT });
            await expect(fila).toHaveCount(1);
            const id = await fila.getAttribute('data-id');
            await page.goto(`/ripeaback/reactapp/contingut/${id}`);
        });

        await test.step('verificar la capçalera amb el títol', async () => {
            logInfo('  -> verificar la capçalera amb el títol');
            await expect(page.getByRole('heading', { name: NOM_MODIFICAT })).toBeVisible({ timeout: 10_000 });
        });

        await test.step('verificar el panell d\'informació de l\'expedient', async () => {
            logInfo('  -> verificar el panell d\'informació de l\'expedient');
            await expect(page.getByText(/informaci. de l.expedient|informaci.n del expediente/i)).toBeVisible();
        });

        await test.step('verificar les pestanyes del detall', async () => {
            logInfo('  -> verificar les pestanyes del detall');
            await expect(page.locator('[role="tablist"]')).toBeVisible({ timeout: 10_000 });
            await expect(page.getByRole('tab').filter({ hasText: /contingut|contenido/i })).toBeVisible();
            await expect(page.getByRole('tab').filter({ hasText: /dades|datos/i })).toBeVisible();
            await expect(page.getByRole('tab').filter({ hasText: /interessats|interesados/i })).toBeVisible();
            await expect(page.getByRole('tab').filter({ hasText: /tasques|tareas/i })).toBeVisible();
        });

    });

    // ── Eliminació ────────────────────────────────────────────────────────────────

    test('EXPEDIENT ELIMINAR', async ({ page }) => {

        await test.step('filtrar i eliminar l\'expedient de prova', async () => {
            logInfo('  -> filtrar i eliminar l\'expedient de prova');
            await filtrarPerTitol(page, NOM_MODIFICAT);
            const fila = getRows(page).filter({ hasText: NOM_MODIFICAT });
            await expect(fila).toHaveCount(1);
            await eliminarFila(page, fila);
        });

        await test.step('verificar que l\'expedient s\'ha eliminat', async () => {
            logInfo('  -> verificar que l\'expedient s\'ha eliminat');
            await filtrarPerTitol(page, NOM_MODIFICAT);
            await expect(getRows(page).filter({ hasText: NOM_MODIFICAT })).toHaveCount(0);
        });

    });

});
