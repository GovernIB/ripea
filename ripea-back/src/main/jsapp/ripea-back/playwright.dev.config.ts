import base from './playwright.config';

// ─────────────────────────────────────────────────────────────────────────────
// Execució de la suite contra l'entorn DEV (dev.caib.es) sense tocar
// playwright.config.ts, que apunta a localhost.
//
//   npx playwright test -c playwright.dev.config.ts <fitxer> --project=admin
//
// PREREQUISIT: els *.setup.ts llegeixen tests_e2e/auth/credentials.json amb una
// ruta fixa, de manera que cal posar-hi les credencials de DEV abans d'executar
// (i restaurar les locals després). L'IdP el detecta performLogin() sol.
//
// La sessió es desa a tests_e2e/.auth/*.json (ruta fixada als setups): quedarà
// amb la sessió de DEV, però es regenera sola a la propera execució local.
// ─────────────────────────────────────────────────────────────────────────────

export default {
    ...base,
    use: {
        ...base.use,
        baseURL: 'https://dev.caib.es',
    },
};
