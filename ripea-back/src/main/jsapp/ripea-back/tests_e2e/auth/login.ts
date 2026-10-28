import { Page } from '@playwright/test';
import path from 'path';
import fs from 'fs';

// ─────────────────────────────────────────────────────────────────────────────
// Login adaptatiu segons el proveïdor d'identitat que mostri la baseURL configurada
// a playwright.config.ts:
//   - localhost   → Keycloak (authdev.limit.es): formulari únic
//                   #username / #password / #kc-login
//   - dev.caib.es → Soffid SAML (idp.caib.es): flux en DUES pantalles
//                   (1) #j_username  + #loginpassbutton ("Accediu")  → /userNameAction
//                   (2) #j_password  + #loginpassbutton ("Accediu")  → /passwordLoginAction
//
// performLogin() detecta automàticament quin formulari hi ha i aplica el flux
// corresponent, de manera que els setups d'autenticació no depenen de l'entorn.
// Cal haver navegat abans a l'app (que redirigeix a l'IdP).
// ─────────────────────────────────────────────────────────────────────────────

export interface Credentials {
    usuari: string;
    password: string;
}

// Keycloak (entorn local): formulari únic amb usuari + contrasenya.
const loginKeycloak = async (page: Page, { usuari, password }: Credentials): Promise<void> => {
    await page.locator('#username').fill(usuari);
    await page.locator('#password').fill(password);
    await page.locator('#kc-login').click();
};

// Soffid SAML (dev.caib.es): dues pantalles, totes dues amb el botó #loginpassbutton.
//   Pantalla 1: #j_username editable → clic Accediu → es carrega la pantalla de contrasenya.
//   Pantalla 2: #j_password editable → clic Accediu.
// Si l'IdP recorda l'usuari, la pantalla 1 surt amb #j_username en readonly; en aquest
// cas s'omet i s'introdueix directament la contrasenya.
const loginSoffid = async (page: Page, { usuari, password }: Credentials): Promise<void> => {
    const userField = page.locator('#j_username');
    if (await userField.isEditable().catch(() => false)) {
        await userField.fill(usuari);
        await page.locator('#loginpassbutton').click();
    }
    const passField = page.locator('#j_password');
    await passField.waitFor({ state: 'visible', timeout: 10_000 });
    await passField.fill(password);
    await page.locator('#loginpassbutton').click();
};

// Detecta el proveïdor pel formulari mostrat i executa el flux corresponent.
export const performLogin = async (page: Page, creds: Credentials): Promise<void> => {
    await page.waitForSelector('#j_username, #username', { timeout: 15_000 });
    if (await page.locator('#j_username').count() > 0) {
        await loginSoffid(page, creds);   // dev.caib.es → Soffid SAML
    } else {
        await loginKeycloak(page, creds); // localhost → Keycloak
    }
};

// Codi d'usuari d'un rol ('admin', 'user'...) del fitxer de credencials (el mateix que
// fan servir els setups: E2E_CREDENTIALS_FILE o tests_e2e/auth/credentials.json).
// Per als tests que necessiten un usuari que existeixi a l'entorn (p.ex. per donar-li
// un permís) sense escriure'l al codi.
export const codiUsuariCredencials = (rol: string): string => {
    const fitxer = process.env.E2E_CREDENTIALS_FILE || path.join('tests_e2e', 'auth', 'credentials.json');
    const creds = JSON.parse(fs.readFileSync(fitxer, 'utf-8'));
    if (!creds?.[rol]?.usuari) {
        throw new Error(`Falta l'entrada "${rol}" a ${fitxer}.`);
    }
    return creds[rol].usuari;
};
