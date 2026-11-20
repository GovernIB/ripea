import { describe, it, expect } from 'vitest';
import { formatDia, formatDiaHora } from '@src/util/dateUtils';

// La pantalla d'inici rep les dates en tres formats segons el tipus del camp al backend (veure FilesLlistesInici).
describe('formatDia i formatDiaHora', () => {

    it('data sense hora (@Temporal DATE): el dia, sense desplaçar-lo per la zona horària', () => {
        expect(formatDia('2025-08-05')).toBe('05/08/2025');
    });

    it('data i hora sense zona (LocalDateTime, amb microsegons): hora local', () => {
        expect(formatDiaHora('2025-06-23T17:21:58.013123')).toBe('23/06/2025 17:21');
        expect(formatDia('2025-06-23T17:21:58.013123')).toBe('23/06/2025');
    });

    it('data i hora amb desplaçament (java.util.Date): convertida a l\'hora local', () => {
        const valor = '2026-11-19T13:38:34.410+00:00';
        const local = new Date(valor);
        const dosDigits = (n: number) => String(n).padStart(2, '0');
        const esperat = `${dosDigits(local.getDate())}/${dosDigits(local.getMonth() + 1)}/${local.getFullYear()} `
            + `${dosDigits(local.getHours())}:${dosDigits(local.getMinutes())}`;
        expect(formatDiaHora(valor)).toBe(esperat);
    });

    it('sense valor: text buit', () => {
        expect(formatDia(undefined)).toBe('');
        expect(formatDiaHora(null)).toBe('');
    });
});
