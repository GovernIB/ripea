import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MetaExpedientFilter } from '@src/pages/metaExpedient/MetaExpedientFilter';
import { useFormContext } from 'reactlib';
import { useUserSession } from '@src/components/Session';

vi.mock('reactlib', () => ({
    useFormContext: vi.fn(() => ({ data: {}, fields: [] })),
}));

vi.mock('@src/components/StyledMuiFilter', () => ({
    default: ({ children, resourceName }: any) => (
        <div data-testid="styled-filter" data-resource={resourceName}>{children}</div>
    ),
}));

vi.mock('@src/components/GridFormField', () => ({
    default: ({ name, disabled, hidden }: any) => (
        <div
            data-testid={`field-${name}`}
            data-disabled={String(!!disabled)}
            data-hidden={String(!!hidden)}
        />
    ),
    GridButtonField: ({ name }: any) => (
        <div data-testid={`button-field-${name}`} />
    ),
}));

vi.mock('@src/components/Session', () => ({
    useUserSession: vi.fn(() => ({ value: null })),
}));

describe('MetaExpedientFilter', () => {

    beforeEach(() => {
        vi.mocked(useFormContext).mockReturnValue({ data: {}, fields: [] } as any);
        vi.mocked(useUserSession).mockReturnValue({ value: null } as any);
    });

    it('renderitza sense errors', () => {
        render(<MetaExpedientFilter onSpringFilterChange={vi.fn()} />);
        expect(screen.getByTestId('styled-filter')).toBeInTheDocument();
    });

    it('passa resourceName correcte a StyledMuiFilter', () => {
        render(<MetaExpedientFilter onSpringFilterChange={vi.fn()} />);
        expect(screen.getByTestId('styled-filter')).toHaveAttribute('data-resource', 'metaExpedientResource');
    });

    it('renderitza tots els camps del filtre', () => {
        render(<MetaExpedientFilter onSpringFilterChange={vi.fn()} />);
        expect(screen.getByTestId('field-tipus')).toBeInTheDocument();
        expect(screen.getByTestId('field-codi')).toBeInTheDocument();
        expect(screen.getByTestId('field-nom')).toBeInTheDocument();
        expect(screen.getByTestId('field-classificacio')).toBeInTheDocument();
        expect(screen.getByTestId('field-actiu')).toBeInTheDocument();
        expect(screen.getByTestId('field-ambit')).toBeInTheDocument();
        expect(screen.getByTestId('field-organGestor')).toBeInTheDocument();
        expect(screen.getByTestId('button-field-permisDirecte')).toBeInTheDocument();
    });

    it('amaga el camp revisioEstat quan isRevisioActiva és false (usuari null)', () => {
        vi.mocked(useUserSession).mockReturnValue({ value: null } as any);
        render(<MetaExpedientFilter onSpringFilterChange={vi.fn()} />);
        expect(screen.getByTestId('field-revisioEstat')).toHaveAttribute('data-hidden', 'true');
    });

    it('mostra el camp revisioEstat quan isRevisioActiva és true', () => {
        vi.mocked(useUserSession).mockReturnValue({
            value: { sessionScope: { isRevisioActiva: true } },
        } as any);
        render(<MetaExpedientFilter onSpringFilterChange={vi.fn()} />);
        expect(screen.getByTestId('field-revisioEstat')).toHaveAttribute('data-hidden', 'false');
    });

    it('deshabilita organGestor quan ambit és COMUNS', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: { ambit: 'COMUNS' }, fields: [] } as any);
        render(<MetaExpedientFilter onSpringFilterChange={vi.fn()} />);
        expect(screen.getByTestId('field-organGestor')).toHaveAttribute('data-disabled', 'true');
    });

    it('habilita organGestor quan ambit no és COMUNS', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: { ambit: 'PROPIS' }, fields: [] } as any);
        render(<MetaExpedientFilter onSpringFilterChange={vi.fn()} />);
        expect(screen.getByTestId('field-organGestor')).toHaveAttribute('data-disabled', 'false');
    });
});
