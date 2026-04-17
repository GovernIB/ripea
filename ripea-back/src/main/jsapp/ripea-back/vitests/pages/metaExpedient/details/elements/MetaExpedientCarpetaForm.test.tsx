import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MetaExpedientCarpetaForm } from '@src/pages/metaExpedient/details/elements/MetaExpedientCarpetaGrid';
import { useFormContext } from 'reactlib';

vi.mock('react-i18next', () => ({
    useTranslation: () => ({ t: (key: string) => key }),
}));

vi.mock('reactlib', () => ({
    useFormContext: vi.fn(() => ({ data: {}, fields: [] })),
}));

vi.mock('@src/components/GridFormField', () => ({
    default: ({ name, disabled, componentProps }: any) => (
        <div
            data-testid={`field-${name}`}
            data-disabled={String(!!disabled)}
            data-helper-text={componentProps?.helperText ?? ''}
        />
    ),
}));

vi.mock('@src/util/springFilterUtils', () => ({
    and: vi.fn(() => ''),
    eq: vi.fn(() => ''),
}));

// ─── MetaExpedientCarpetaForm ─────────────────────────────────────────────────

describe('MetaExpedientCarpetaForm', () => {

    beforeEach(() => {
        vi.mocked(useFormContext).mockReturnValue({ data: {}, fields: [] } as any);
    });

    it('renderitza els camps nom i pare', () => {
        render(<MetaExpedientCarpetaForm />);
        expect(screen.getByTestId('field-nom')).toBeInTheDocument();
        expect(screen.getByTestId('field-pare')).toBeInTheDocument();
    });

    it('camp pare habilitat en mode creació (sense id)', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: {}, fields: [] } as any);
        render(<MetaExpedientCarpetaForm />);
        expect(screen.getByTestId('field-pare')).toHaveAttribute('data-disabled', 'false');
    });

    it('camp pare deshabilitat en mode edició (amb id)', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: { id: 3 }, fields: [] } as any);
        render(<MetaExpedientCarpetaForm />);
        expect(screen.getByTestId('field-pare')).toHaveAttribute('data-disabled', 'true');
    });

    it('mostra text d\'ajuda sobre carpeta principal en mode creació', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: {}, fields: [] } as any);
        render(<MetaExpedientCarpetaForm />);
        expect(screen.getByTestId('field-pare')).toHaveAttribute(
            'data-helper-text',
            'Si es deixa en blanc, es creará com a carpeta principal.'
        );
    });

    it('oculta el text d\'ajuda en mode edició', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: { id: 3 }, fields: [] } as any);
        render(<MetaExpedientCarpetaForm />);
        expect(screen.getByTestId('field-pare')).toHaveAttribute('data-helper-text', '');
    });

    it('camp nom sempre habilitat', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: { id: 99 }, fields: [] } as any);
        render(<MetaExpedientCarpetaForm />);
        expect(screen.getByTestId('field-nom')).toHaveAttribute('data-disabled', 'false');
    });
});
