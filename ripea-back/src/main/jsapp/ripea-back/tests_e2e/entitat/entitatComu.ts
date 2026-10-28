import { expect, Page } from '@playwright/test';
import path from 'path';
import fs from 'fs';

// ─────────────────────────────────────────────────────────────────────────────
// Dades i comprovacions compartides pels tests d'entitats REACT i JSP.
//
// Cada interfície té el seu codi d'entitat (es poden executar en paral·lel); la resta
// de valors són comuns. El CIF i la unitat arrel no són únics a IPA_ENTITAT.
// ─────────────────────────────────────────────────────────────────────────────

// CIF sintètics vàlids per a @DocumentIdentitat (lletra de control calculada).
export const CIF_CREAR     = 'Q0799901D';
export const CIF_MODIFICAR = 'S0799902B';

// Codis amb format DIR3 que no existeixen: si algun procés sincronitzés l'organigrama de
// l'entitat de prova, no hi crearia òrgans (que impedirien esborrar-la per FK).
export const UNITAT_CREAR     = 'A99999901';
export const UNITAT_MODIFICAR = 'A99999902';

// Colors de capçalera (input type=color a REACT: #rrggbb en minúscules).
export const COLORS_CREAR = {
    capsaleraColorFons: '#1a2b3c',
    capsaleraColorLletra: '#d4e5f6',
    blackCapsaleraColorFons: '#203040',
    blackCapsaleraColorLletra: '#a0b0c0',
};
export const COLORS_MODIFICAR = {
    capsaleraColorFons: '#3c2b1a',
    capsaleraColorLletra: '#f6e5d4',
    blackCapsaleraColorFons: '#403020',
    blackCapsaleraColorLletra: '#c0b0a0',
};

// Imatges de prova (PNG 8x8 de colors diferents) per comprovar que es substitueixen.
export const IMATGE_A = path.join('tests_e2e', 'fixtures', 'entitat-imatge-a.png');
export const IMATGE_B = path.join('tests_e2e', 'fixtures', 'entitat-imatge-b.png');

// Rol que no té cap usuari, per donar permisos sense efectes sobre els usuaris de prova.
// Ja existeix a IPA_ACL_SID (el creen els tests de procediments): no se n'afegeixen.
export const SID_ROL_REACT = 'ROL_PW_REACT_TEST';
export const SID_ROL_JSP   = 'ROL_PW_TEST';

const base64 = (fitxer: string) => fs.readFileSync(fitxer).toString('base64');

// Llegeix l'entitat de l'API REST (les imatges vénen en base64; si no n'hi ha, el camp no hi és).
export const llegirEntitatApi = async (page: Page, id: string): Promise<any> => {
    const resp = await page.request.get(`/ripeaback/api/entitats/${id}`);
    expect(resp.status(), `GET /api/entitats/${id}`).toBe(200);
    return resp.json();
};

// Comprova el contingut d'una imatge desada: igual al fitxer, o absent si fitxer és null.
export const expectImatge = (entitat: any, campBytes: string, fitxer: string | null) => {
    if (fitxer) {
        expect.soft(entitat[campBytes], `${campBytes} ha de ser el contingut de ${path.basename(fitxer)}`).toBe(base64(fitxer));
    } else {
        expect.soft(entitat[campBytes] ?? null, `${campBytes} s'ha d'haver esborrat`).toBeNull();
    }
};

export const expectColors = (entitat: any, colors: Record<string, string>) => {
    for (const [camp, valor] of Object.entries(colors)) {
        expect.soft(entitat[camp], camp).toBe(valor);
    }
};
