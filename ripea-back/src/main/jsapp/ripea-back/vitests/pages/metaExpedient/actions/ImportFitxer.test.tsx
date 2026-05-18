import { describe, it, expect, vi } from 'vitest';
import { render, screen, renderHook } from '@testing-library/react';
import { editarElemento, ImportFitxerFormBase, useImportFitxer } from '@src/pages/metaExpedient/actions/ImportFitxer';
import { useFormContext } from 'reactlib';

vi.mock('react-i18next', () => ({
    useTranslation: () => ({ t: (key: string) => key }),
}));

vi.mock('reactlib', () => ({
    useFormContext: vi.fn(() => ({ data: {}, fields: [], apiRef: { current: null } })),
    useBaseAppContext: () => ({ temporalMessageShow: vi.fn() }),
    useMuiFormDialogApiRef: () => ({ current: null }),
    FormField: () => null,
    MuiFormDialogApi: undefined,
}));

vi.mock('@src/components/GridFormField', () => ({
    default: ({ name, disabled, hidden }: any) => (
        <div
            data-testid={`field-${name}`}
            data-disabled={String(!!disabled)}
            data-hidden={String(!!hidden)}
        />
    ),
    FileFormField: ({ name, hidden }: any) => (
        <div data-testid={`file-field-${name}`} data-hidden={String(!!hidden)} />
    ),
}));

vi.mock('@src/components/CardData', () => ({
    CardData: ({ children }: any) => <div data-testid="card-data">{children}</div>,
}));

vi.mock('@src/components/TabComponent', () => ({
    default: () => <div data-testid="tab-component" />,
}));

vi.mock('@src/components/FormActionDialog', () => ({
    default: ({ children }: any) => <div data-testid="form-action-dialog">{children}</div>,
}));

vi.mock('@src/util/springFilterUtils', () => ({
    neq: vi.fn(() => 'nif is not null'),
}));

// ─── editarElemento ──────────────────────────────────────────────────────────

describe('editarElemento', () => {

    it('actualitza l\'element que coincideix per codi', () => {
        const array = [
            { codi: 'A', nom: 'Alfa', importar: false },
            { codi: 'B', nom: 'Beta', importar: false },
        ];
        const result = editarElemento(array, 'A', { importar: true });
        expect(result[0].importar).toBe(true);
        expect(result[1].importar).toBe(false);
    });

    it('no modifica els elements que no coincideixen', () => {
        const array = [{ codi: 'X', nom: 'Original' }];
        const result = editarElemento(array, 'Z', { nom: 'Modificat' });
        expect(result[0].nom).toBe('Original');
    });

    it('suporta identificador personalitzat', () => {
        const array = [
            { id: 1, nom: 'Primer' },
            { id: 2, nom: 'Segon' },
        ];
        const result = editarElemento(array, 1 as any, { nom: 'Actualitzat' }, 'id');
        expect(result[0].nom).toBe('Actualitzat');
        expect(result[1].nom).toBe('Segon');
    });

    it('retorna array buit sense modificar quan la llista és buida', () => {
        const result = editarElemento([], 'X', { nom: 'Test' });
        expect(result).toHaveLength(0);
    });

    it('aplica múltiples canvis a l\'element coincident', () => {
        const array = [{ codi: 'A', nom: 'Alfa', actiu: false }];
        const result = editarElemento(array, 'A', { nom: 'Nou nom', actiu: true });
        expect(result[0].nom).toBe('Nou nom');
        expect(result[0].actiu).toBe(true);
    });
});

// ─── ImportFitxerFormBase ─────────────────────────────────────────────────────

describe('ImportFitxerFormBase', () => {

    it('renderitza tots els camps principals', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: {}, fields: [] } as any);
        render(<ImportFitxerFormBase />);
        expect(screen.getByTestId('field-codi')).toBeInTheDocument();
        expect(screen.getByTestId('field-nom')).toBeInTheDocument();
        expect(screen.getByTestId('field-classificacio')).toBeInTheDocument();
        expect(screen.getByTestId('field-organGestor')).toBeInTheDocument();
    });

    it('deshabilita codi quan procediment ja existeix', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: { procediment: { id: 1 } }, fields: [] } as any);
        render(<ImportFitxerFormBase />);
        expect(screen.getByTestId('field-codi')).toHaveAttribute('data-disabled', 'true');
    });

    it('habilita codi quan procediment és null', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: { procediment: null }, fields: [] } as any);
        render(<ImportFitxerFormBase />);
        expect(screen.getByTestId('field-codi')).toHaveAttribute('data-disabled', 'false');
    });

    it('deshabilita classificacio quan tipusClassificacio és ID', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: { tipusClassificacio: 'ID' }, fields: [] } as any);
        render(<ImportFitxerFormBase />);
        expect(screen.getByTestId('field-classificacio')).toHaveAttribute('data-disabled', 'true');
    });

    it('deshabilita classificacio quan procediment ja existeix', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: { procediment: { id: 1 } }, fields: [] } as any);
        render(<ImportFitxerFormBase />);
        expect(screen.getByTestId('field-classificacio')).toHaveAttribute('data-disabled', 'true');
    });

    it('habilita classificacio quan tipusClassificacio no és ID i no hi ha procediment', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: { tipusClassificacio: 'MANUAL' }, fields: [] } as any);
        render(<ImportFitxerFormBase />);
        expect(screen.getByTestId('field-classificacio')).toHaveAttribute('data-disabled', 'false');
    });

    it('amaga organGestor quan procedimentComu és true', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: { procedimentComu: true }, fields: [] } as any);
        render(<ImportFitxerFormBase />);
        expect(screen.getByTestId('field-organGestor')).toHaveAttribute('data-hidden', 'true');
    });

    it('mostra organGestor quan procedimentComu és false', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: { procedimentComu: false }, fields: [] } as any);
        render(<ImportFitxerFormBase />);
        expect(screen.getByTestId('field-organGestor')).toHaveAttribute('data-hidden', 'false');
    });

    it('no mostra l\'alert msgSiaRolsac quan és null', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: { msgSiaRolsac: null }, fields: [] } as any);
        render(<ImportFitxerFormBase />);
        expect(screen.queryByRole('alert')).not.toBeInTheDocument();
    });

    it('mostra l\'alert msgSiaRolsac quan té valor', () => {
        vi.mocked(useFormContext).mockReturnValue({
            data: { msgSiaRolsac: 'Advertència ROLSAC' }, fields: [],
        } as any);
        render(<ImportFitxerFormBase />);
        expect(screen.getByRole('alert')).toHaveTextContent('Advertència ROLSAC');
    });
});

// ─── useImportFitxer (hook) ───────────────────────────────────────────────────

describe('useImportFitxer', () => {
    it('retorna handleShow i content', () => {
        const { result } = renderHook(() => useImportFitxer());
        expect(typeof result.current.handleShow).toBe('function');
        expect(result.current.content).toBeDefined();
    });
});
