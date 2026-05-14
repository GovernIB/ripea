import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, renderHook } from '@testing-library/react';
import { CanviEstatRevisioForm, CanviEstatRevisio } from '@src/pages/metaExpedient/actions/CanviEstatRevisio';
import useCanviEstatRevisio from '@src/pages/metaExpedient/actions/CanviEstatRevisio';
import { useUserSession } from '@src/components/Session';

vi.mock('react-i18next', () => ({
    useTranslation: () => ({ t: (key: string) => key }),
}));

vi.mock('reactlib', () => ({
    useBaseAppContext: () => ({ temporalMessageShow: vi.fn() }),
    useMuiFormDialogApiRef: () => ({ current: null }),
    MuiFormDialog: ({ children }: any) => (
        <div data-testid="form-dialog">{children}</div>
    ),
    MuiFormDialogApi: undefined,
}));

vi.mock('@src/components/GridFormField', () => ({
    default: ({ name, disabled }: any) => (
        <div data-testid={`field-${name}`} data-disabled={String(!!disabled)} />
    ),
}));

vi.mock('@src/components/Session', () => ({
    useUserSession: vi.fn(() => ({ value: null, rol: {} })),
}));

// ─── CanviEstatRevisioForm ────────────────────────────────────────────────────

describe('CanviEstatRevisioForm', () => {

    it('renderitza els camps revisioEstat i revisioComentari', () => {
        render(<CanviEstatRevisioForm editable={false} />);
        expect(screen.getByTestId('field-revisioEstat')).toBeInTheDocument();
        expect(screen.getByTestId('field-revisioComentari')).toBeInTheDocument();
    });

    it('deshabilita els dos camps quan editable és false', () => {
        render(<CanviEstatRevisioForm editable={false} />);
        expect(screen.getByTestId('field-revisioEstat')).toHaveAttribute('data-disabled', 'true');
        expect(screen.getByTestId('field-revisioComentari')).toHaveAttribute('data-disabled', 'true');
    });

    it('habilita els dos camps quan editable és true', () => {
        render(<CanviEstatRevisioForm editable={true} />);
        expect(screen.getByTestId('field-revisioEstat')).toHaveAttribute('data-disabled', 'false');
        expect(screen.getByTestId('field-revisioComentari')).toHaveAttribute('data-disabled', 'false');
    });

    it('deshabilita per defecte quan editable no es passa', () => {
        render(<CanviEstatRevisioForm />);
        expect(screen.getByTestId('field-revisioEstat')).toHaveAttribute('data-disabled', 'true');
        expect(screen.getByTestId('field-revisioComentari')).toHaveAttribute('data-disabled', 'true');
    });
});

// ─── CanviEstatRevisio ────────────────────────────────────────────────────────

describe('CanviEstatRevisio', () => {

    beforeEach(() => {
        vi.mocked(useUserSession).mockReturnValue({ value: null, rol: {} } as any);
    });

    it('renderitza el diàleg', () => {
        render(<CanviEstatRevisio apiRef={{ current: null }} />);
        expect(screen.getByTestId('form-dialog')).toBeInTheDocument();
    });

    it('el formulari és editable quan rol és admin', () => {
        vi.mocked(useUserSession).mockReturnValue({ value: null, rol: { isAdmin: true } } as any);
        render(<CanviEstatRevisio apiRef={{ current: null }} />);
        expect(screen.getByTestId('field-revisioEstat')).toHaveAttribute('data-disabled', 'false');
    });

    it('el formulari és editable quan rol és revisor', () => {
        vi.mocked(useUserSession).mockReturnValue({ value: null, rol: { isRevisor: true } } as any);
        render(<CanviEstatRevisio apiRef={{ current: null }} />);
        expect(screen.getByTestId('field-revisioEstat')).toHaveAttribute('data-disabled', 'false');
    });

    it('el formulari NO és editable quan no és ni admin ni revisor', () => {
        vi.mocked(useUserSession).mockReturnValue({ value: null, rol: { isAdmin: false, isRevisor: false } } as any);
        render(<CanviEstatRevisio apiRef={{ current: null }} />);
        expect(screen.getByTestId('field-revisioEstat')).toHaveAttribute('data-disabled', 'true');
    });
});

// ─── useCanviEstatRevisio (hook) ──────────────────────────────────────────────

describe('useCanviEstatRevisio', () => {
    it('retorna handleShow i content', () => {
        const { result } = renderHook(() => useCanviEstatRevisio());
        expect(typeof result.current.handleShow).toBe('function');
        expect(result.current.content).toBeDefined();
    });
});
