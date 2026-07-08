import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MetaExpedientForm } from '@src/pages/metaExpedient/MetaExpedientGrid';
import { useFormContext } from 'reactlib';
import { useUserSession } from '@src/components/Session';

// ─── Mocks ──────────────────────────────────────────────────────────────────

vi.mock('react-i18next', () => ({
    useTranslation: () => ({ t: (key: string, _opts?: any) => key }),
}));

vi.mock('reactlib', () => ({
    useFormContext: vi.fn(() => ({ data: {}, fields: [] })),
    GridPage: ({ children }: any) => <div data-testid="grid-page">{children}</div>,
    useFilterApiRef: () => ({ current: null }),
    useFormApiRef: () => ({ current: null }),
    useMuiDataGridApiRef: () => ({ current: { refresh: vi.fn() } }),
    useBaseAppContext: () => ({ messageDialogShow: vi.fn() }),
}));

vi.mock('react-router-dom', () => ({
    useNavigate: () => vi.fn(),
}));

vi.mock('@src/components/Session', () => ({
    useUserSession: vi.fn(() => ({ value: { sessionScope: {} }, rol: {} })),
}));

vi.mock('@src/components/SessionStorageContext', () => ({
    useSessionContext: () => ({ value: null, save: vi.fn() }),
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

vi.mock('@src/util/dateUtils', () => ({
    formatDate: () => '01/01/2024',
}));

vi.mock('@src/components/CardData', () => ({
    CardPage: ({ children }: any) => <div data-testid="card-page">{children}</div>,
}));

vi.mock('@src/components/StyledMuiGrid', () => ({
    default: () => <div data-testid="styled-grid" />,
}));

vi.mock('@src/components/Load', () => ({
    default: ({ children }: any) => <div>{children}</div>,
}));

vi.mock('@src/pages/CommentDialog', () => ({
    MetaExpedientComment: () => null,
}));

vi.mock('@src/components/LinkIcon', () => ({
    default: () => null,
}));

vi.mock('@src/components/MenuButton', () => ({
    default: () => null,
    MenuActionButton: () => null,
}));

vi.mock('@src/pages/user/consultes/RevisioMetaExpedientGrid', () => ({
    StyledEstat: ({ children }: any) => <span>{children}</span>,
}));

vi.mock('@src/components/StyledBadge', () => ({
    StyledBadge: ({ children }: any) => <span>{children}</span>,
}));

vi.mock('@src/pages/metaExpedient/MetaExpedientFilter', () => ({
    MetaExpedientFilter: () => <div data-testid="meta-expedient-filter" />,
    springFilterBuilder: vi.fn(),
}));

vi.mock('@src/pages/metaExpedient/details/MetaExpedientActions', () => ({
    useMetaExpedientActions: () => ({ actions: [], components: null }),
}));

vi.mock('@src/pages/metaExpedient/actions/ImportRolsac', () => ({
    useImportRolsac: () => ({ handleShow: vi.fn(), content: null }),
}));

vi.mock('@src/pages/metaExpedient/actions/ImportFitxer', () => ({
    useImportFitxer: () => ({ handleShow: vi.fn(), content: null }),
}));

vi.mock('@src/pages/metaExpedient/actions/Actualitzar', () => ({
    default: vi.fn(() => ({ handleShow: vi.fn(), content: null })),
}));

// ─── MetaExpedientForm ──────────────────────────────────────────────────────

describe('MetaExpedientForm', () => {

    beforeEach(() => {
        vi.mocked(useFormContext).mockReturnValue({ data: {}, fields: [] } as any);
    });

    it('renderitza sense errors amb data buida', () => {
        render(<MetaExpedientForm isAdmin={false} />);
        expect(screen.getByTestId('field-nom')).toBeInTheDocument();
    });

    it('no mostra alert msgSiaRolsac quan és null', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: { msgSiaRolsac: null }, fields: [] } as any);
        render(<MetaExpedientForm isAdmin={false} />);
        expect(screen.queryByRole('alert')).not.toBeInTheDocument();
    });

    it('mostra alert warning msgSiaRolsac quan té valor', () => {
        vi.mocked(useFormContext).mockReturnValue({
            data: { msgSiaRolsac: 'El procediment no existeix a ROLSAC' },
            fields: [],
        } as any);
        render(<MetaExpedientForm isAdmin={false} />);
        const alert = screen.getByRole('alert');
        expect(alert).toBeInTheDocument();
        expect(alert).toHaveTextContent('El procediment no existeix a ROLSAC');
    });

    it('no mostra alert d\'auditoria quan data no té id', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: {}, fields: [] } as any);
        render(<MetaExpedientForm isAdmin={false} />);
        expect(screen.queryByRole('alert')).not.toBeInTheDocument();
    });

    it('mostra alert info d\'auditoria quan data té id', () => {
        vi.mocked(useFormContext).mockReturnValue({
            data: { id: 1, createdDate: '2024-01-01', createdByFullName: 'Admin', lastModifiedDate: null },
            fields: [],
        } as any);
        render(<MetaExpedientForm isAdmin={false} />);
        const alert = screen.getByRole('alert');
        expect(alert).toBeInTheDocument();
        expect(alert).toHaveTextContent('common.auditoria.create');
    });

    it('deshabilita classificacio quan tipusClassificacio és ID', () => {
        vi.mocked(useFormContext).mockReturnValue({
            data: { tipusClassificacio: 'ID' },
            fields: [],
        } as any);
        render(<MetaExpedientForm isAdmin={true} />);
        expect(screen.getByTestId('field-classificacio')).toHaveAttribute('data-disabled', 'true');
    });

    it('habilita classificacio quan tipusClassificacio no és ID', () => {
        vi.mocked(useFormContext).mockReturnValue({
            data: { tipusClassificacio: 'MANUAL' },
            fields: [],
        } as any);
        render(<MetaExpedientForm isAdmin={true} />);
        expect(screen.getByTestId('field-classificacio')).toHaveAttribute('data-disabled', 'false');
    });

    it('deshabilita crearReglaDistribucio quan isAdmin és false', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: {}, fields: [] } as any);
        render(<MetaExpedientForm isAdmin={false} />);
        expect(screen.getByTestId('field-crearReglaDistribucio')).toHaveAttribute('data-disabled', 'true');
    });

    it('habilita crearReglaDistribucio quan isAdmin és true i no hi ha id', () => {
        vi.mocked(useFormContext).mockReturnValue({ data: {}, fields: [] } as any);
        render(<MetaExpedientForm isAdmin={true} />);
        expect(screen.getByTestId('field-crearReglaDistribucio')).toHaveAttribute('data-disabled', 'false');
    });
});

// ─── MetaExpedientGrid (smoke test) ─────────────────────────────────────────

describe('MetaExpedientGrid', () => {
    it('renderitza sense errors', async () => {
        vi.mocked(useUserSession).mockReturnValue({
            value: { sessionScope: {} },
            rol: { isAdmin: false, isRevisor: false, isAdminLectura: false, isOrganAdmin: false },
        } as any);
        const { default: MetaExpedientGrid } = await import('@src/pages/metaExpedient/MetaExpedientGrid');
        render(<MetaExpedientGrid />);
        expect(screen.getByTestId('card-page')).toBeInTheDocument();
    });
});
