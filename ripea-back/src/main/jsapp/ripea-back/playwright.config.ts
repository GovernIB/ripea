import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
    testDir: './tests_e2e',
    // Temps màxim per test
    timeout: 30_000,
    // Reintents en cas de fallada (0 en local, 2 en CI)
    retries: 0,
    // Informe HTML generat a tests_e2e/report/ + llistat per consola
    reporter: [
        ['list'],
        ['html', { outputFolder: 'tests_e2e/report', open: 'never' }],
    ],

    use: {
        // URL base de l'aplicació sobre JBoss
        //baseURL: 'https://dev.caib.es',
		baseURL: 'http://localhost:8080',
        // Captura screenshot només en cas de fallada
        screenshot: 'only-on-failure',
        // Vídeo de tots els tests (èxit i fallada)
        video: 'on',
        // Traces per depurar fallades
        trace: 'retain-on-failure',
		launchOptions: {
		  args: ['--window-size=1920,1080']
		},
		viewport: null, // IMPORTANTE: permite que la ventana use el tamaño real	
    },

    projects: [
        // ── Setups d'autenticació (s'executen una vegada i guarden la sessió) ──
        {
            name: 'setup-admin',
            testMatch: /auth[\/\\]admin\.setup\.ts/,
			workers: 1,
            // Força el català al detector d'idioma de i18next (navigator.language),
            // perquè la sessió desada quedi en català i coincideixi amb les etiquetes
            // del frontend (common.filter, common.clear...) i amb els selectors dels tests.
            use: { locale: 'ca-ES' },
        },
        {
            name: 'setup-user',
            testMatch: /auth[\/\\]user\.setup\.ts/,
			workers: 1,
            // Força el català al detector d'idioma de i18next (navigator.language),
            // perquè la sessió desada quedi en català i coincideixi amb les etiquetes
            // del backend i amb els selectors dels tests.
            use: { locale: 'ca-ES' },
        },

        // ── Tests per rol ──
        {
            name: 'admin',
            dependencies: ['setup-admin'],
            use: {
                ...devices['Desktop Chrome'],
                // Usa el Chrome instal·lat al sistema en lloc del Chromium de Playwright
                channel: 'chrome',
                // Força el català perquè la UI del frontend (i18next) coincideixi amb els
                // selectors del test; sense això, Chrome arrenca en anglès.
                locale: 'ca-ES',
                storageState: 'tests_e2e/.auth/admin.json',
            },
            testMatch: '**/*.admin.spec.ts',
        },
        {
            name: 'admin-jsp',
            dependencies: ['setup-admin'],
            use: {
                ...devices['Desktop Chrome'],
                channel: 'chrome',
                // Reutilitza la mateixa sessió Keycloak que la interfície React
                // (mateix WAR → mateixa cookie de sessió)
                storageState: 'tests_e2e/.auth/admin.json',
            },
            testMatch: '**/*.admin.jsp.spec.ts',
        },
        {
            name: 'user',
            dependencies: ['setup-user'],
            use: {
                ...devices['Desktop Chrome'],
                channel: 'chrome',
                // Força el català perquè la UI traducida (i18next) coincideixi amb els
                // selectors del test; sense això, Chrome arrenca en anglès.
                locale: 'ca-ES',
                storageState: 'tests_e2e/.auth/user.json',
            },
            testMatch: '**/*.user.spec.ts',
        },
    ],
});
