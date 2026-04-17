import { describe, it, expect, vi } from 'vitest';
import { render, screen, renderHook } from '@testing-library/react';
import { ImportRolsacForm, useImportRolsac } from '@src/pages/metaExpedient/actions/ImportRolsac';

vi.mock('react-i18next', () => ({
    useTranslation: () => ({ t: (key: string) => key }),
}));

vi.mock('reactlib', () => ({
    MuiFormDialogApi: undefined,
    MuiDataGridApi: undefined,
}));

vi.mock('@src/components/GridFormField', () => ({
    default: ({ name }: any) => (
        <div data-testid={`field-${name}`} />
    ),
}));

vi.mock('@src/components/FormActionDialog', () => ({
    default: ({ children, resourceName, action }: any) => (
        <div data-testid="form-action-dialog" data-resource={resourceName} data-action={action}>
            {children}
        </div>
    ),
}));

// ─── ImportRolsacForm ─────────────────────────────────────────────────────────

describe('ImportRolsacForm', () => {
    it('renderitza el camp codiSia', () => {
        render(<ImportRolsacForm />);
        expect(screen.getByTestId('field-codiSia')).toBeInTheDocument();
    });
});

// ─── useImportRolsac (hook) ───────────────────────────────────────────────────

describe('useImportRolsac', () => {
    it('retorna handleShow i content', () => {
        const mockGridRef = { current: { showCreateDialog: vi.fn() } as any };
        const { result } = renderHook(() => useImportRolsac(mockGridRef));
        expect(typeof result.current.handleShow).toBe('function');
        expect(result.current.content).toBeDefined();
    });

    it('el content renderitza FormActionDialog amb acció IMPORT_ROLSAC', () => {
        const mockGridRef = { current: { showCreateDialog: vi.fn() } as any };
        const { result } = renderHook(() => useImportRolsac(mockGridRef));
        render(result.current.content);
        const dialog = screen.getByTestId('form-action-dialog');
        expect(dialog).toHaveAttribute('data-resource', 'metaExpedientResource');
        expect(dialog).toHaveAttribute('data-action', 'IMPORT_ROLSAC');
    });

    it('el content inclou el camp codiSia', () => {
        const mockGridRef = { current: { showCreateDialog: vi.fn() } as any };
        const { result } = renderHook(() => useImportRolsac(mockGridRef));
        render(result.current.content);
        expect(screen.getByTestId('field-codiSia')).toBeInTheDocument();
    });
});
