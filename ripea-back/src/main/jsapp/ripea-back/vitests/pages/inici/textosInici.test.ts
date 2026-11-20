import { describe, it, expect } from 'vitest';
import translationCa from '@src/i18n/translationCa';
import translationEs from '@src/i18n/translationEs';
import translationEn from '@src/i18n/translationEn';

// Codis que envia el backend: enums IndicadorInici i LlistaInici. Si se n'hi afegeix un, cal afegir-lo aquí, al
// catàleg del front (catalegIndicadors.ts o catalegLlistes.tsx) i a les traduccions.
const INDICADORS = [
    'EXPEDIENTS_AGAFATS', 'EXPEDIENTS_ANTICS', 'EXPEDIENTS_OBERTS', 'EXPEDIENTS_TANCATS_RECENTS', 'ALERTES_NO_LLEGIDES',
    'ANOTACIONS_PENDENTS', 'TASQUES_EN_CURS', 'TASQUES_PENDENTS', 'TASQUES_VENCIMENT_PROPER', 'TASQUES_FORA_TERMINI',
    'TASQUES_SENSE_ACTIVITAT', 'PROCEDIMENTS_PENDENTS_REVISIO', 'PROCEDIMENTS_EN_DISSENY', 'PROCEDIMENTS_REBUTJATS',
    'PROCEDIMENTS_REVISATS', 'PROCEDIMENTS_ACTIUS', 'PROCEDIMENTS_INACTIUS', 'INTEGRACIONS_ERROR', 'EXCEPCIONS', 'MEMORIA_LLIURE', 'DISC_LLIURE',
];
const LLISTES = [
    'TASQUES_PER_DATA_LIMIT', 'EXPEDIENTS_LLESTOS_TANCAR', 'TASQUES_SENSE_ACTIVITAT', 'DARRERES_INTEGRACIONS',
    'DARRERES_EXCEPCIONS', 'DARRERS_COMENTARIS', 'PROCEDIMENTS_PENDENTS_REVISIO', 'PROCEDIMENTS_REBUTJATS',
];

const idiomes: [string, any][] = [['ca', translationCa], ['es', translationEs], ['en', translationEn]];

describe.each(idiomes)('textos de la pantalla d\'inici (%s)', (_idioma, traduccio) => {
    const inici = traduccio.page.inici;

    it.each(INDICADORS)('targeta %s amb títol i descripció', (codi) => {
        expect(inici.indicador[codi]?.titol).toBeTruthy();
        expect(inici.indicador[codi]?.descripcio).toBeTruthy();
    });

    it.each(LLISTES)('llista %s amb títol i descripció', (codi) => {
        expect(inici.llista[codi]?.titol).toBeTruthy();
        expect(inici.llista[codi]?.descripcio).toBeTruthy();
    });

    it('mateixes capçaleres de columna que en català', () => {
        expect(Object.keys(inici.columna).sort()).toEqual(Object.keys(translationCa.page.inici.columna).sort());
    });
});
