import { test as setup, expect } from '@playwright/test';
import path from 'path';
import fs from 'fs';
import { performLogin } from './login';

// ─────────────────────────────────────────────────────────────────────────────
// Autenticació del superusuari (IPA_SUPER), amb un usuari propi.
//
// El rol actiu es desa per usuari a la BD (IPA_USUARI.ROL_ACTUAL), no per sessió: si
// els tests de superusuari canviessin de rol l'usuari admin, trencarien els tests
// d'admin que s'executen en paral·lel amb el mateix usuari.
//
// Les credencials es llegeixen de tests_e2e/auth/credentials.json (NO pujat a git).
// Copia tests_e2e/auth/credentials.json.example i omple-ho amb les teves dades.
// ─────────────────────────────────────────────────────────────────────────────
// Es pot apuntar a un altre fitxer amb la variable d'entorn E2E_CREDENTIALS_FILE
// (útil per executar contra un entorn amb usuaris diferents, p.ex. dev.caib.es).
const CREDENTIALS_FILE = process.env.E2E_CREDENTIALS_FILE || path.join('tests_e2e', 'auth', 'credentials.json');

interface Credentials {
    super: { usuari: string; password: string };
    [role: string]: { usuari: string; password: string };
}

function loadCredentials(): Credentials {
    if (!fs.existsSync(CREDENTIALS_FILE)) {
        throw new Error(
            `Fitxer de credencials no trobat: ${CREDENTIALS_FILE}\n` +
            `Copia tests_e2e/auth/credentials.json.example a credentials.json i omple les dades.`
        );
    }
    return JSON.parse(fs.readFileSync(CREDENTIALS_FILE, 'utf-8')) as Credentials;
}

const creds = loadCredentials();
if (!creds.super) {
    throw new Error(
        `Falta l'entrada "super" a ${CREDENTIALS_FILE}. ` +
        `Afegeix { "super": { "usuari": "rip_super", "password": "..." } }.`
    );
}
const USUARI   = creds.super.usuari;
const PASSWORD = creds.super.password;

// ─────────────────────────────────────────────────────────────────────────────

const SESSION_FILE = path.join('tests_e2e', '.auth', 'super.json');

setup('autenticar com a superusuari (IPA_SUPER)', async ({ page }) => {

    // 1. Navegar a l'aplicació → l'IdP redirigeix automàticament al login
    await page.goto('/ripeaback/reactapp/');

    // 2. Login adaptatiu segons l'IdP detectat (Keycloak en local, Soffid a dev.caib.es)
    await performLogin(page, { usuari: USUARI, password: PASSWORD });

    // 3. Esperar que l'aplicació React hagi carregat completament
    await page.waitForURL('**/ripeaback/reactapp/**');
    await expect(page.locator('body')).not.toBeEmpty();

    // 4. Guardar la sessió (cookies + localStorage) per reutilitzar-la a tots els tests
    await page.context().storageState({ path: SESSION_FILE });

    console.log(`Sessió superusuari guardada a ${SESSION_FILE}`);
});
