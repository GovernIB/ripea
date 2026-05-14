import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MetaDocumentDadaForm } from '@src/pages/metaExpedient/details/elements/MetaDadaGrid';
import { useFormContext } from 'reactlib';

vi.mock('react-i18next', () => ({
    useTranslation: () => ({ t: (key: string) => key }),
}));

vi.mock('reactlib', () => ({
    useFormContext: vi.fn(() => ({ data: {}, fields: [] })),
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

vi.mock('@src/components/StyledMuiGrid', () => ({
    default: ({ children }: any) => <div>{children}</div>,
}));

// ─── Camps base ───────────────────────────────────────────────────────────────

describe('MetaDocumentDadaForm - camps base', () => {

    beforeEach(() => {
        vi.mocked(useFormContext).mockReturnValue({ data: {}, fields: [] } as any);
    });

    it('renderitza els camps fixes (codi, nom, tipus, multiplicitat, descripcio)', () => {
        render(<MetaDocumentDadaForm enviable={false} />);
        expect(screen.getByTestId('field-codi')).toBeInTheDocument();
        expect(screen.getByTestId('field-nom')).toBeInTheDocument();
        expect(screen.getByTestId('field-tipus')).toBeInTheDocument();
        expect(screen.getByTestId('field-multiplicitat')).toBeInTheDocument();
        expect(screen.getByTestId('field-descripcio')).toBeInTheDocument();
    });

    it('camp tipus habilitat en mode creació (sense id)', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: {}, fields: [] } as any);
        render(<MetaDocumentDadaForm enviable={false} />);
        expect(screen.getByTestId('field-tipus')).toHaveAttribute('data-disabled', 'false');
    });

    it('camp tipus deshabilitat en mode edició (amb id)', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: { id: 10 }, fields: [] } as any);
        render(<MetaDocumentDadaForm enviable={false} />);
        expect(screen.getByTestId('field-tipus')).toHaveAttribute('data-disabled', 'true');
    });
});

// ─── Camps valor condicionals per tipus ───────────────────────────────────────

const TIPUS_CAMPS: Array<{ tipus: string; camp: string }> = [
    { tipus: 'TEXT',    camp: 'valorString' },
    { tipus: 'DATA',    camp: 'valorData'   },
    { tipus: 'IMPORT',  camp: 'valorImport' },
    { tipus: 'SENCER',  camp: 'valorSencer' },
    { tipus: 'FLOTANT', camp: 'valorFlotant'},
    { tipus: 'BOOLEA',  camp: 'valorBoolea' },
    { tipus: 'DOMINI',  camp: 'domini'      },
    { tipus: 'DOMINI',  camp: 'noAplica'    },
];

describe('MetaDocumentDadaForm - camps valor per tipus', () => {

    it.each(TIPUS_CAMPS)('camp $camp visible quan tipus és $tipus', ({ tipus, camp }) => {
        vi.mocked(useFormContext).mockReturnValue({ data: { tipus }, fields: [] } as any);
        render(<MetaDocumentDadaForm enviable={false} />);
        expect(screen.getByTestId(`field-${camp}`)).toHaveAttribute('data-hidden', 'false');
    });

    it.each(TIPUS_CAMPS)('camp $camp ocult quan tipus NO és $tipus', ({ tipus, camp }) => {
        const altreTipus = tipus === 'TEXT' ? 'DATA' : 'TEXT';
        vi.mocked(useFormContext).mockReturnValue({ data: { tipus: altreTipus }, fields: [] } as any);
        render(<MetaDocumentDadaForm enviable={false} />);
        expect(screen.getByTestId(`field-${camp}`)).toHaveAttribute('data-hidden', 'true');
    });

    it('tots els camps valor estan ocults quan tipus no té valor', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: {}, fields: [] } as any);
        render(<MetaDocumentDadaForm enviable={false} />);
        const campValor = ['valorString', 'valorData', 'valorImport', 'valorSencer', 'valorFlotant', 'valorBoolea', 'domini', 'noAplica'];
        campValor.forEach(camp => {
            expect(screen.getByTestId(`field-${camp}`)).toHaveAttribute('data-hidden', 'true');
        });
    });

    it('seleccionant TEXT, només valorString és visible entre els camps valor', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: { tipus: 'TEXT' }, fields: [] } as any);
        render(<MetaDocumentDadaForm enviable={false} />);
        expect(screen.getByTestId('field-valorString')).toHaveAttribute('data-hidden', 'false');
        ['valorData', 'valorImport', 'valorSencer', 'valorFlotant', 'valorBoolea', 'domini', 'noAplica'].forEach(camp => {
            expect(screen.getByTestId(`field-${camp}`)).toHaveAttribute('data-hidden', 'true');
        });
    });

    it('seleccionant DOMINI, domini i noAplica són visibles', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: { tipus: 'DOMINI' }, fields: [] } as any);
        render(<MetaDocumentDadaForm enviable={false} />);
        expect(screen.getByTestId('field-domini')).toHaveAttribute('data-hidden', 'false');
        expect(screen.getByTestId('field-noAplica')).toHaveAttribute('data-hidden', 'false');
    });
});

// ─── Camps enviable / metadadaArxiu ───────────────────────────────────────────

describe('MetaDocumentDadaForm - camps enviable', () => {

    it('camp enviable ocult quan prop enviable és false', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: {}, fields: [] } as any);
        render(<MetaDocumentDadaForm enviable={false} />);
        expect(screen.getByTestId('field-enviable')).toHaveAttribute('data-hidden', 'true');
    });

    it('camp enviable visible quan prop enviable és true', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: {}, fields: [] } as any);
        render(<MetaDocumentDadaForm enviable={true} />);
        expect(screen.getByTestId('field-enviable')).toHaveAttribute('data-hidden', 'false');
    });

    it('camp metadadaArxiu ocult quan prop enviable és false', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: {}, fields: [] } as any);
        render(<MetaDocumentDadaForm enviable={false} />);
        expect(screen.getByTestId('field-metadadaArxiu')).toHaveAttribute('data-hidden', 'true');
    });

    it('camp metadadaArxiu ocult quan prop enviable és true però data.enviable és false', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: { enviable: false }, fields: [] } as any);
        render(<MetaDocumentDadaForm enviable={true} />);
        expect(screen.getByTestId('field-metadadaArxiu')).toHaveAttribute('data-hidden', 'true');
    });

    it('camp metadadaArxiu visible quan prop enviable és true I data.enviable és true', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: { enviable: true }, fields: [] } as any);
        render(<MetaDocumentDadaForm enviable={true} />);
        expect(screen.getByTestId('field-metadadaArxiu')).toHaveAttribute('data-hidden', 'false');
    });
});
