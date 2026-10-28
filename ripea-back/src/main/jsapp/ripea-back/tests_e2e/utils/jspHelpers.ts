import { expect, Page, FrameLocator } from '@playwright/test';

// ─────────────────────────────────────────────────────────────────────────────
// Utilitats compartides pels tests E2E de la interfície clàssica (JSP).
//
// Conté constants, logging i helpers genèrics de les pantalles JSP: avisos,
// datatables (webutil.datatable.js), modals amb iframe (webutil.modal.js),
// camps select2 de suggeriment (rip:inputSuggest) i el canvi de rol del
// decorador. Els helpers específics de cada pàgina es mantenen al seu spec.
// ─────────────────────────────────────────────────────────────────────────────

// ── Constants ───────────────────────────────────────────────────────────────
export const DEBUG_ACTIVAT  = true;
export const HUMAN_DELAY    = 1000; // milisegons de retard entre execució de accions
// Temps màxim perquè l'iframe d'un modal carregui el formulari. Amb els projectes
// React, JSP i usuari executant-se en paral·lel el servidor pot superar els 5 s per defecte.
export const TIMEOUT_IFRAME = 15_000;

// ── Logging ───────────────────────────────────────────────────────────────────
export const logDebug = (message: string) => { if (DEBUG_ACTIVAT) { console.log(message); } };
export const logInfo  = (message: string) => { console.log(message); };

// ── Retard "humà" entre accions ────────────────────────────────────────────────
export const humanDelay = async (page: Page) => {
    if (HUMAN_DELAY > 0) {
        logDebug('Esperant ' + HUMAN_DELAY + 'ms');
        await page.waitForTimeout(HUMAN_DELAY);
    }
};

// ── Avisos ────────────────────────────────────────────────────────────────────
export const expectSuccessAlert = (page: Page) =>
    expect(page.locator('.alert.alert-success')).toBeVisible({ timeout: 10_000 });

// ── Datatables ────────────────────────────────────────────────────────────────

// Files d'un datatable, excloent la fila "Cap resultat" (td.dataTables_empty).
export const getFilesDatatable = (page: Page, taulaId: string) =>
    page.locator(`#${taulaId} tbody tr`).filter({ hasNot: page.locator('td.dataTables_empty') });

// Promise d'espera de la resposta AJAX d'un datatable.
// IMPORTANT: cridar ABANS de l'acció que provoca la càrrega (goto, click...).
export const waitDatatableResponse = (page: Page, urlPart: string) =>
    page.waitForResponse(resp => resp.url().includes(urlPart) && resp.status() === 200);

// Espera que el datatable hagi acabat de pintar (indicador de càrrega ocult).
export const esperarDatatable = (page: Page, taulaId: string) =>
    expect(page.locator(`#${taulaId}_processing`)).toBeHidden({ timeout: 10_000 });

// ── Modals amb iframe ─────────────────────────────────────────────────────────

// Iframe del modal obert. S'usa .modal.in per evitar conflicte amb modals ocults.
export const getModalFrame = (page: Page) =>
    page.locator('.modal.in').frameLocator('.modal-body iframe');

// Espera que el modal sigui visible i que l'iframe hagi carregat el formulari
// (camp `campSelector` visible). Retorna el FrameLocator del formulari.
export const esperarModalFormulari = async (page: Page, campSelector: string): Promise<FrameLocator> => {
    await expect(page.locator('.modal.in')).toBeVisible();
    const frame = getModalFrame(page);
    await expect(frame.locator(campSelector)).toBeVisible({ timeout: TIMEOUT_IFRAME });
    return frame;
};

// Envia el formulari del modal. El botó submit és clonat al .modal-footer de la pàgina
// pare (l'original dins l'iframe s'amaga); s'usa button[type="submit"] directe per evitar
// ambigüitats d'accessible name amb la icona FA.
export const enviarModal = async (page: Page) => {
    const submitBtn = page.locator('.modal.in .modal-footer button[type="submit"]');
    await submitBtn.waitFor({ state: 'visible' });
    await submitBtn.click();
};

// Tanca el modal amb el botó Cancel·lar del peu i espera que desaparegui.
export const cancelarModal = async (page: Page) => {
    await page.locator('.modal.in .modal-footer').getByText(/cancel/i).click();
    await expect(page.locator('.modal.in')).toHaveCount(0, { timeout: 5_000 });
};

// ── Camps select2 de suggeriment (rip:inputSuggest) ─────────────────────────────
//
// El <select id=campId> queda ocult i select2 pinta el valor a #select2-<campId>-container
// (atribut title = text de l'opció). Les opcions es carreguen per AJAX des de l'URL de
// l'atribut data-url-llistat, i cal escriure un mínim de caràcters (per defecte 3).

// Text del valor seleccionat d'un camp select2 ('' si no n'hi ha).
export const llegirSuggest = async (ctx: Page | FrameLocator, campId: string): Promise<string> =>
    (await ctx.locator(`#select2-${campId}-container`).getAttribute('title')) ?? '';

// Obre el camp, escriu `cerca`, espera la resposta AJAX i tria l'opció indicada:
//   - number → posició dins la llista de resultats (0 = primera)
//   - string → primera opció que conté aquest text
// ctx: la pàgina o el FrameLocator del modal on és el camp.
// Retorna el text del valor seleccionat.
export const seleccionarSuggest = async (
    page: Page,
    ctx: Page | FrameLocator,
    campId: string,
    cerca: string,
    opcio: number | string = 0,
): Promise<string> => {
    logDebug(`[Suggest] Cercant "${cerca}" a #${campId}, seleccionant: ${opcio}`);
    const urlLlistat = await ctx.locator(`#${campId}`).getAttribute('data-url-llistat');
    await ctx.locator(`.select2-selection:has(#select2-${campId}-container)`).click();
    const resp = page.waitForResponse(r => !!urlLlistat && r.url().includes(urlLlistat) && r.status() === 200);
    await ctx.locator('.select2-container--open .select2-search__field').fill(cerca);
    await resp;
    // Exclou l'opció temporal "Cercant..." i els missatges (p.ex. "Cap resultat").
    const opcions = ctx.locator(
        '.select2-container--open .select2-results__option:not(.loading-results):not(.select2-results__message)');
    const triada = typeof opcio === 'number' ? opcions.nth(opcio) : opcions.filter({ hasText: opcio }).first();
    await triada.waitFor({ timeout: 5_000 });
    await triada.click();
    // El desplegable és un span posicionat absolutament que pot tapar altres elements.
    await ctx.locator('.select2-container--open').waitFor({ state: 'detached', timeout: 5_000 }).catch(() => {});
    const valor = await llegirSuggest(ctx, campId);
    logDebug(`[Suggest] Seleccionat: ${valor}`);
    return valor;
};

// ── Rol (decorador JSP) ─────────────────────────────────────────────────────────
//
// El selector de rol del decorador JSP (default.jsp) té data-testid="user-menu-rol"
// i exposa el codi del rol actiu a l'atribut data-rol (p.ex. "IPA_ADMIN", "tothom"),
// independent de l'idioma. Quan l'usuari té més d'un rol, el desplegable conté un
// enllaç per cada rol alternatiu amb data-rol="<codi>" que navega a
// /index?canviRol=<codi> (recàrrega completa de pàgina).

export const SELECTOR_ROL = '[data-testid="user-menu-rol"]';

// Comprova que el rol actiu sigui `rolDesitjat` i, si no ho és, el canvia.
//
// urlDesti: pàgina on ha de quedar la sessió després del canvi. En canviar de rol,
// /index redirigeix a la interfície per defecte del nou rol, que pot no ser la JSP
// desitjada (fins i tot pot anar a React, on no existeix l'atribut data-rol). Si la
// URL resultant no és la esperada, hi tornem abans de verificar.
export const assegurarRolJsp = async (page: Page, rolDesitjat: string, urlDesti: string): Promise<void> => {
    console.log(`[Rol] Comprovant que el rol actiu sigui "${rolDesitjat}"...`);
    const rolContainer = page.locator(SELECTOR_ROL);
    await expect(rolContainer).toBeVisible({ timeout: 5_000 });

    const rolActual = await rolContainer.getAttribute('data-rol');

    console.log(`[Rol] Rol actiu detectat: "${rolActual}"`);

    if (rolActual === rolDesitjat) {
        console.log('[Rol] El rol ja és el desitjat; no cal canviar-lo.');
        return;
    }

    console.log(`[Rol] Canviant de "${rolActual}" a "${rolDesitjat}"...`);
    // Obrir el desplegable de rols (només existeix quan l'usuari té més d'un rol).
    await rolContainer.locator('a[data-toggle="dropdown"]').click();
    // Clicar l'enllaç del rol desitjat: navega a /index?canviRol=<codi> i recarrega.
    await rolContainer.locator(`a[data-rol="${rolDesitjat}"]`).click();
    // Esperar la recàrrega i que el rol actiu reflecteixi el canvi.
    await page.waitForLoadState('load');

    // Si la redirecció ens ha tret de la interfície/pàgina desitjada, hi tornem.
    if (!page.url().includes(urlDesti)) {
        console.log(`[Rol] Redirigit a ${page.url()}; tornant a ${urlDesti}`);
        await page.goto(urlDesti);
    }

    // Verificar que el rol actiu reflecteixi el canvi.
    await expect(page.locator(SELECTOR_ROL)).toHaveAttribute('data-rol', rolDesitjat, { timeout: 10_000 });
    console.log(`[Rol] Rol canviat correctament a "${rolDesitjat}".`);
};
