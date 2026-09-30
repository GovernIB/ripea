import path from 'path';
import base from './playwright.config';

// ─────────────────────────────────────────────────────────────────────────────
// Execució de la suite contra l'entorn DEV (dev.caib.es) sense tocar
// playwright.config.ts, que apunta a localhost.
//
//   npx playwright test -c playwright.dev.config.ts tests_e2e/metaExpedient --workers=1
//
// CREDENCIALS: per defecte es llegeixen de tests_e2e/auth/credentials.dev.json
// (mateixa estructura que credentials.json, amb usuaris del directori corporatiu;
// IGNORAT pel git). Es pot apuntar a un altre fitxer amb E2E_CREDENTIALS_FILE.
// L'IdP (Soffid SAML) el detecta performLogin() sol.
//
// Es fixa aquí la variable d'entorn perquè el config s'avalua tant al procés
// principal com a cada worker, i els *.setup.ts la llegeixen en carregar-se.
//
// La sessió es desa a tests_e2e/.auth/*.json (ruta fixada als setups): quedarà
// amb la sessió de DEV, però es regenera sola a la propera execució local.
//
// ATENCIÓ: els tests de procediments creen, modifiquen i esborren dades a DEV
// (codis zz_PLAYWRIGHT_*). Amb --workers=1 s'eviten col·lisions entre els projectes
// admin i admin-jsp i la càrrega extra sobre el servidor compartit.
// ─────────────────────────────────────────────────────────────────────────────

process.env.E2E_CREDENTIALS_FILE ??= path.join('tests_e2e', 'auth', 'credentials.dev.json');

export default {
    ...base,
    use: {
        ...base.use,
        baseURL: 'https://dev.caib.es',
    },
};
