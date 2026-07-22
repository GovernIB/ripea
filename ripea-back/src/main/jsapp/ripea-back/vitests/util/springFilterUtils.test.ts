import { describe, it, expect } from 'vitest';
import { like, likeNormalized } from '@src/util/springFilterUtils';

// El filtre avançat construeix la cadena Spring Filter al client (a diferència del
// quickFilter, que la construeix el servidor a BaseReadonlyResourceService i escapa
// la cometa amb cleanReservedFilterCharacters: ' -> \'). Aquests tests asseguren que
// el client fa el mateix escapat, per no reproduir el bug del token "recognition
// error at: '%'" amb noms com "Expedients d'ajuts".
describe('springFilterUtils', () => {

    describe('like', () => {
        it('embolcalla el valor amb comodins %', () => {
            expect(like('nom', 'Test')).toBe("nom~'%Test%'");
        });

        it("escapa la cometa simple amb barra invertida (com fa el quickFilter al servidor)", () => {
            expect(like('nom', "Expedients d'ajuts")).toBe("nom~'%Expedients d\\'ajuts%'");
        });

        it('escapa múltiples cometes dins del mateix valor', () => {
            expect(like('nom', "d'un d'altre")).toBe("nom~'%d\\'un d\\'altre%'");
        });

        it('deixa el marcador undefined perquè and() el descarti quan el valor és undefined', () => {
            expect(like('codi', undefined as any)).toBe("codi~'%undefined%'");
        });
    });

    describe('likeNormalized', () => {
        it('normalitza accents i passa a majúscules', () => {
            expect(likeNormalized('nom', 'Institució')).toBe("nom~'%INSTITUCIO%'");
        });

        it('escapa la cometa simple després de normalitzar', () => {
            expect(likeNormalized('nom', "Expedients d'ajuts")).toBe("nom~'%EXPEDIENTS D\\'AJUTS%'");
        });
    });
});
