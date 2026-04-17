import { describe, it, expect, vi } from 'vitest';
import { render, screen, renderHook } from '@testing-library/react';
import { VincularGrupForm, useVincularGrup } from '@src/pages/metaExpedient/actions/VincularGrup';
import { useFormContext } from 'reactlib';

vi.mock('react-i18next', () => ({
    useTranslation: () => ({ t: (key: string) => key }),
}));

vi.mock('reactlib', () => ({
    useFormContext: vi.fn(() => ({ data: {}, fields: [], apiRef: { current: null } })),
    useBaseAppContext: () => ({ temporalMessageShow: vi.fn() }),
    MuiFormDialogApi: undefined,
}));

vi.mock('@src/components/GridFormField', () => ({
    default: ({ name, disabled, namedQueries }: any) => (
        <div
            data-testid={`field-${name}`}
            data-disabled={String(!!disabled)}
            data-named-queries={namedQueries ?? ''}
        />
    ),
}));

vi.mock('@src/components/FormActionDialog', () => ({
    default: ({ children, resourceName, action }: any) => (
        <div data-testid="form-action-dialog" data-resource={resourceName} data-action={action}>
            {children}
        </div>
    ),
}));

// ─── VincularGrupForm ─────────────────────────────────────────────────────────

describe('VincularGrupForm', () => {

    it('renderitza els camps grup, organGestor i perDefecte', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: {}, fields: [] } as any);
        render(<VincularGrupForm />);
        expect(screen.getByTestId('field-grup')).toBeInTheDocument();
        expect(screen.getByTestId('field-organGestor')).toBeInTheDocument();
        expect(screen.getByTestId('field-perDefecte')).toBeInTheDocument();
    });

    it('el camp organGestor sempre és disabled', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: {}, fields: [] } as any);
        render(<VincularGrupForm />);
        expect(screen.getByTestId('field-organGestor')).toHaveAttribute('data-disabled', 'true');
    });

    it('el camp grup inclou l\'id del procediment a la namedQuery', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: { id: 7 }, fields: [] } as any);
        render(<VincularGrupForm />);
        expect(screen.getByTestId('field-grup')).toHaveAttribute(
            'data-named-queries', 'VINCULAR_PROCEDIMENT#7'
        );
    });

    it('la namedQuery del grup és undefined quan data.id no existeix', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: {}, fields: [] } as any);
        render(<VincularGrupForm />);
        expect(screen.getByTestId('field-grup')).toHaveAttribute(
            'data-named-queries', 'VINCULAR_PROCEDIMENT#undefined'
        );
    });
});

// ─── useVincularGrup (hook) ───────────────────────────────────────────────────

describe('useVincularGrup', () => {
    it('retorna handleShow i content', () => {
        const { result } = renderHook(() => useVincularGrup());
        expect(typeof result.current.handleShow).toBe('function');
        expect(result.current.content).toBeDefined();
    });

    it('el content renderitza FormActionDialog amb acció VINCULAR_GRUP', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: {}, fields: [] } as any);
        const { result } = renderHook(() => useVincularGrup());
        render(result.current.content);
        const dialog = screen.getByTestId('form-action-dialog');
        expect(dialog).toHaveAttribute('data-resource', 'metaExpedientResource');
        expect(dialog).toHaveAttribute('data-action', 'VINCULAR_GRUP');
    });
});
