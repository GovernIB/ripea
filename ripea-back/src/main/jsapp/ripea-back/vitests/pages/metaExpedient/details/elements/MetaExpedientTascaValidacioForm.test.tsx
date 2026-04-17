import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MetaExpedientTascaValidacioForm, columns } from '@src/pages/metaExpedient/details/elements/MetaExpedientTascaValidacioGrid';
import { useFormContext } from 'reactlib';

vi.mock('react-i18next', () => ({
    useTranslation: () => ({ t: (key: string) => key }),
}));

vi.mock('reactlib', () => ({
    useFormContext: vi.fn(() => ({ data: {}, fields: [] })),
    GridPage: ({ children }: any) => <div>{children}</div>,
    useBaseAppContext: () => ({ temporalMessageShow: vi.fn() }),
    useMuiDataGridApiRef: () => ({ current: null }),
    useResourceApiService: () => ({ isReady: false, getOne: vi.fn(), patch: vi.fn() }),
}));

vi.mock('@src/components/GridFormField', () => ({
    default: ({ name, disabled, hidden }: any) => (
        <div
            data-testid={`field-${name}`}
            data-disabled={String(!!disabled)}
            data-hidden={String(!!hidden)}
        />
    ),
}));

// ─── MetaExpedientTascaValidacioForm ──────────────────────────────────────────

describe('MetaExpedientTascaValidacioForm - camps base', () => {

    beforeEach(() => {
        vi.mocked(useFormContext).mockReturnValue({ data: {}, fields: [] } as any);
    });

    it('renderitza els camps itemValidacio, metaDada, metaDocument i tipusValidacio', () => {
        render(<MetaExpedientTascaValidacioForm />);
        expect(screen.getByTestId('field-itemValidacio')).toBeInTheDocument();
        expect(screen.getByTestId('field-metaDada')).toBeInTheDocument();
        expect(screen.getByTestId('field-metaDocument')).toBeInTheDocument();
        expect(screen.getByTestId('field-tipusValidacio')).toBeInTheDocument();
    });
});

describe('MetaExpedientTascaValidacioForm - visibilitat condicional', () => {

    it('camp metaDada ocult quan itemValidacio no és DADA', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: { itemValidacio: 'DOCUMENT' }, fields: [] } as any);
        render(<MetaExpedientTascaValidacioForm />);
        expect(screen.getByTestId('field-metaDada')).toHaveAttribute('data-hidden', 'true');
    });

    it('camp metaDada visible quan itemValidacio és DADA', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: { itemValidacio: 'DADA' }, fields: [] } as any);
        render(<MetaExpedientTascaValidacioForm />);
        expect(screen.getByTestId('field-metaDada')).toHaveAttribute('data-hidden', 'false');
    });

    it('camp metaDocument ocult quan itemValidacio no és DOCUMENT', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: { itemValidacio: 'DADA' }, fields: [] } as any);
        render(<MetaExpedientTascaValidacioForm />);
        expect(screen.getByTestId('field-metaDocument')).toHaveAttribute('data-hidden', 'true');
    });

    it('camp metaDocument visible quan itemValidacio és DOCUMENT', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: { itemValidacio: 'DOCUMENT' }, fields: [] } as any);
        render(<MetaExpedientTascaValidacioForm />);
        expect(screen.getByTestId('field-metaDocument')).toHaveAttribute('data-hidden', 'false');
    });

    it('ambdós camps metaDada i metaDocument ocults quan itemValidacio no té valor', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: {}, fields: [] } as any);
        render(<MetaExpedientTascaValidacioForm />);
        expect(screen.getByTestId('field-metaDada')).toHaveAttribute('data-hidden', 'true');
        expect(screen.getByTestId('field-metaDocument')).toHaveAttribute('data-hidden', 'true');
    });
});

describe('MetaExpedientTascaValidacioForm - camp tipusValidacio disabled', () => {

    it('tipusValidacio deshabilitat quan itemValidacio és DADA', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: { itemValidacio: 'DADA' }, fields: [] } as any);
        render(<MetaExpedientTascaValidacioForm />);
        expect(screen.getByTestId('field-tipusValidacio')).toHaveAttribute('data-disabled', 'true');
    });

    it('tipusValidacio habilitat quan itemValidacio és DOCUMENT', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: { itemValidacio: 'DOCUMENT' }, fields: [] } as any);
        render(<MetaExpedientTascaValidacioForm />);
        expect(screen.getByTestId('field-tipusValidacio')).toHaveAttribute('data-disabled', 'false');
    });

    it('tipusValidacio habilitat quan itemValidacio no té valor', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: {}, fields: [] } as any);
        render(<MetaExpedientTascaValidacioForm />);
        expect(screen.getByTestId('field-tipusValidacio')).toHaveAttribute('data-disabled', 'false');
    });
});

// ─── columns - columna itemId (valueGetter i valueFormatter) ─────────────────

describe('columns - columna itemId', () => {

    const itemIdCol = columns.find(c => c.field === 'itemId');

    it('la columna itemId existeix a la definició de columnes', () => {
        expect(itemIdCol).toBeDefined();
    });

    it('valueGetter retorna metaDocument quan itemValidacio és DOCUMENT', () => {
        const row = { itemValidacio: 'DOCUMENT', metaDocument: { id: 1, description: 'Doc A' }, metaDada: { id: 2, description: 'Dada B' } };
        expect(itemIdCol?.valueGetter(undefined, row)).toEqual(row.metaDocument);
    });

    it('valueGetter retorna metaDada quan itemValidacio és DADA', () => {
        const row = { itemValidacio: 'DADA', metaDocument: { id: 1, description: 'Doc A' }, metaDada: { id: 2, description: 'Dada B' } };
        expect(itemIdCol?.valueGetter(undefined, row)).toEqual(row.metaDada);
    });

    it('valueGetter retorna metaDada quan itemValidacio no té valor', () => {
        const row = { metaDocument: { id: 1 }, metaDada: { id: 2, description: 'Dada B' } };
        expect(itemIdCol?.valueGetter(undefined, row)).toEqual(row.metaDada);
    });

    it('valueFormatter retorna la propietat description de l\'objecte', () => {
        const value = { id: 5, description: 'Validació manual' };
        expect(itemIdCol?.valueFormatter(value)).toBe('Validació manual');
    });

    it('valueFormatter retorna undefined quan el valor és null', () => {
        expect(itemIdCol?.valueFormatter(null)).toBeUndefined();
    });
});
