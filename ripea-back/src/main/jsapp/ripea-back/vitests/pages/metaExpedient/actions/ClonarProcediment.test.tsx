import { describe, it, expect, vi } from 'vitest';
import { render, screen, renderHook } from '@testing-library/react';
import { ClonarProcedimentForm } from '@src/pages/metaExpedient/actions/ClonarProcediment';
import useClonarProcediment from '@src/pages/metaExpedient/actions/ClonarProcediment';

vi.mock('react-i18next', () => ({
    useTranslation: () => ({ t: (key: string) => key }),
}));

vi.mock('reactlib', () => ({
    useBaseAppContext: () => ({ temporalMessageShow: vi.fn() }),
    MuiFormDialogApi: undefined,
}));

vi.mock('@src/components/GridFormField', () => ({
    default: ({ name }: any) => <div data-testid={`field-${name}`} />,
}));

vi.mock('@src/components/FormActionDialog', () => ({
    default: ({ children, resourceName, action, title }: any) => (
        <div
            data-testid="form-action-dialog"
            data-resource={resourceName}
            data-action={action}
            data-title={title}
        >
            {children}
        </div>
    ),
}));

// ─── ClonarProcedimentForm ────────────────────────────────────────────────────

describe('ClonarProcedimentForm', () => {

    it('renderitza els camps codi i classificacio', () => {
        render(<ClonarProcedimentForm />);
        expect(screen.getByTestId('field-codi')).toBeInTheDocument();
        expect(screen.getByTestId('field-classificacio')).toBeInTheDocument();
    });
});

// ─── useClonarProcediment (hook) ──────────────────────────────────────────────

describe('useClonarProcediment', () => {
    it('retorna handleShow i content', () => {
        const { result } = renderHook(() => useClonarProcediment());
        expect(typeof result.current.handleShow).toBe('function');
        expect(result.current.content).toBeDefined();
    });

    it('el content renderitza FormActionDialog amb acció CLONAR', () => {
        const { result } = renderHook(() => useClonarProcediment());
        render(result.current.content);
        const dialog = screen.getByTestId('form-action-dialog');
        expect(dialog).toHaveAttribute('data-resource', 'metaExpedientResource');
        expect(dialog).toHaveAttribute('data-action', 'CLONAR');
    });

    it('el títol del diàleg és la clau de traducció correcta', () => {
        const { result } = renderHook(() => useClonarProcediment());
        render(result.current.content);
        expect(screen.getByTestId('form-action-dialog')).toHaveAttribute(
            'data-title', 'page.metaExpedient.action.clonar.title'
        );
    });

    it('el content inclou els camps del formulari de clonació', () => {
        const { result } = renderHook(() => useClonarProcediment());
        render(result.current.content);
        expect(screen.getByTestId('field-codi')).toBeInTheDocument();
        expect(screen.getByTestId('field-classificacio')).toBeInTheDocument();
    });
});
