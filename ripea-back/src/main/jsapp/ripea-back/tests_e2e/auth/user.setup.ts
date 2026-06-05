import { test as setup, expect } from '@playwright/test';
import path from 'path';
import fs from 'fs';

// ─────────────────────────────────────────────────────────────────────────────
// Autenticació de l'usuari base (rol tramitador, sense perfil admin).
// Les credencials es llegeixen de tests_e2e/auth/credentials.json (NO pujat a git).
// Copia tests_e2e/auth/credentials.json.example i omple-ho amb les teves dades.
// ─────────────────────────────────────────────────────────────────────────────
const CREDENTIALS_FILE = path.join('tests_e2e', 'auth', 'credentials.json');

interface Credentials {
    admin: { usuari: string; password: string };
    user: { usuari: string; password: string };
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
if (!creds.user) {
    throw new Error(
        `Falta l'entrada "user" a ${CREDENTIALS_FILE}. ` +
        `Afegeix { "user": { "usuari": "rip_user", "password": "..." } }.`
    );
}
const USUARI   = creds.user.usuari;
const PASSWORD = creds.user.password;

// ─────────────────────────────────────────────────────────────────────────────

const SESSION_FILE = path.join('tests_e2e', '.auth', 'user.json');

setup('autenticar com a usuari base (rol tramitador)', async ({ page }) => {

    // 1. Navegar a l'aplicació → Keycloak redirigeix automàticament al login
    await page.goto('/ripeaback/reactapp/');

    // 2. Omplir el formulari de login de Keycloak
    //    Els selectors #username i #password són els estàndards de Keycloak.
    await page.locator('#username').fill(USUARI);
    await page.locator('#password').fill(PASSWORD);
    await page.locator('#kc-login').click();

    // 3. Esperar que l'aplicació React hagi carregat completament
    //    (la URL ja no conté el path de Keycloak)
    await page.waitForURL('**/ripeaback/reactapp/**');
    await expect(page.locator('body')).not.toBeEmpty();

    // 4. Guardar la sessió (cookies + localStorage) per reutilitzar-la a tots els tests
    await page.context().storageState({ path: SESSION_FILE });

    console.log(`Sessió usuari base guardada a ${SESSION_FILE}`);
});
