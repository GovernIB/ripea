import { describe, it, expect, vi } from 'vitest';
import { springFilterBuilder } from '@src/pages/metaExpedient/MetaExpedientFilter';

vi.mock('reactlib', () => ({
    useFormContext: vi.fn(() => ({ data: {}, fields: [] })),
}));

vi.mock('@src/components/StyledMuiFilter', () => ({
    default: () => null,
}));

vi.mock('@src/components/GridFormField', () => ({
    default: () => null,
    GridButtonField: () => null,
}));

vi.mock('@src/components/Session', () => ({
    useUserSession: vi.fn(() => ({ value: null })),
}));

describe('springFilterBuilder', () => {

    describe('sense filtres', () => {
        it('retorna cadena buida quan data és {}', () => {
            expect(springFilterBuilder({})).toBe('');
        });

        it('retorna cadena buida quan data és null', () => {
            expect(springFilterBuilder(null)).toBe('');
        });
    });

    describe('filtre per camps de text (like)', () => {
        it('filtra per codi', () => {
            expect(springFilterBuilder({ codi: 'ABC' })).toBe("codi~'%ABC%'");
        });

        it('filtra per nom', () => {
            expect(springFilterBuilder({ nom: 'Procediment Test' })).toBe("nom~'%Procediment Test%'");
        });

        it('filtra per classificacio', () => {
            expect(springFilterBuilder({ classificacio: 'A01' })).toBe("classificacio~'%A01%'");
        });

        it('ignora codi quan és undefined', () => {
            expect(springFilterBuilder({ codi: undefined })).toBe('');
        });
    });

    describe('filtre per organGestor', () => {
        it('filtra per organGestor.id', () => {
            expect(springFilterBuilder({ organGestor: { id: 42 } })).toBe('organGestor.id:42');
        });

        it('ignora organGestor quan id és undefined', () => {
            expect(springFilterBuilder({ organGestor: { id: undefined } })).toBe('');
        });
    });

    describe('filtre per actiu', () => {
        it('filtra actiu quan és true', () => {
            expect(springFilterBuilder({ actiu: true })).toBe('actiu:true');
        });

        it('ignora actiu quan és false', () => {
            expect(springFilterBuilder({ actiu: false })).toBe('');
        });

        it('ignora actiu quan és null', () => {
            expect(springFilterBuilder({ actiu: null })).toBe('');
        });
    });

    describe('filtre per revisioEstat (enumerat amb cometes)', () => {
        it('filtra revisioEstat PENDENT amb cometes simples', () => {
            expect(springFilterBuilder({ revisioEstat: 'PENDENT' })).toBe("revisioEstat:'PENDENT'");
        });

        it('filtra revisioEstat APROVAT amb cometes simples', () => {
            expect(springFilterBuilder({ revisioEstat: 'APROVAT' })).toBe("revisioEstat:'APROVAT'");
        });

        it('ignora revisioEstat quan és null', () => {
            expect(springFilterBuilder({ revisioEstat: null })).toBe('');
        });
    });

    describe('filtre per tipus (enumerat amb cometes)', () => {
        it('filtra tipus PROCEDIMENT amb cometes simples', () => {
            expect(springFilterBuilder({ tipus: 'PROCEDIMENT' })).toBe("tipusProcedimentServei:'PROCEDIMENT'");
        });

        it('filtra tipus SERVEI amb cometes simples', () => {
            expect(springFilterBuilder({ tipus: 'SERVEI' })).toBe("tipusProcedimentServei:'SERVEI'");
        });

        it('ignora tipus quan és null', () => {
            expect(springFilterBuilder({ tipus: null })).toBe('');
        });
    });

    describe('filtre per permisDirecte', () => {
        it('filtra permisDirecte quan és true', () => {
            expect(springFilterBuilder({ permisDirecte: true })).toBe('permisDirecte:true');
        });

        it('ignora permisDirecte quan és false', () => {
            expect(springFilterBuilder({ permisDirecte: false })).toBe('');
        });
    });

    describe('filtre per ambit', () => {
        it('filtra COMUNS com organGestor.id is null', () => {
            expect(springFilterBuilder({ ambit: 'COMUNS' })).toBe('organGestor.id is null');
        });

        it('filtra PROPIS com organGestor.id is not null', () => {
            expect(springFilterBuilder({ ambit: 'PROPIS' })).toBe('organGestor.id is not null');
        });

        it('ignora ambit quan és null', () => {
            expect(springFilterBuilder({ ambit: null })).toBe('');
        });
    });

    describe('combinació de múltiples filtres', () => {
        it('combina codi i nom amb AND', () => {
            const result = springFilterBuilder({ codi: 'A', nom: 'Test' });
            expect(result).toContain("codi~'%A%'");
            expect(result).toContain("nom~'%Test%'");
            expect(result).toContain(' AND ');
        });

        it('combina tipus, revisioEstat i actiu correctament', () => {
            const result = springFilterBuilder({ tipus: 'PROCEDIMENT', revisioEstat: 'PENDENT', actiu: true });
            expect(result).toContain("tipusProcedimentServei:'PROCEDIMENT'");
            expect(result).toContain("revisioEstat:'PENDENT'");
            expect(result).toContain('actiu:true');
            expect(result).toContain(' AND ');
        });

        it('combina ambit COMUNS i nom', () => {
            const result = springFilterBuilder({ ambit: 'COMUNS', nom: 'Procediment' });
            expect(result).toContain('organGestor.id is null');
            expect(result).toContain("nom~'%Procediment%'");
        });

        it('ignora camps buits i només inclou els que tenen valor', () => {
            const result = springFilterBuilder({ codi: 'ABC', nom: undefined, actiu: false });
            expect(result).toBe("codi~'%ABC%'");
        });
    });
});
