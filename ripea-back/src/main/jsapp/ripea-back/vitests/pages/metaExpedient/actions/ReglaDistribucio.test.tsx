import { describe, it, expect, vi } from 'vitest';
import { render, screen, renderHook } from '@testing-library/react';
import { ReglaDistribucio } from '@src/pages/metaExpedient/actions/ReglaDistribucio';
import useReglaDistribucio from '@src/pages/metaExpedient/actions/ReglaDistribucio';

vi.mock('react-i18next', () => ({
    useTranslation: () => ({ t: (key: string) => key }),
}));

vi.mock('reactlib', () => ({
    useResourceApiService: () => ({ isReady: true, getOne: vi.fn().mockResolvedValue({}) }),
    useBaseAppContext: () => ({ temporalMessageShow: vi.fn() }),
    MuiDialog: ({ children }: any) => <div data-testid="mui-dialog">{children}</div>,
}));

vi.mock('@src/components/CardData', () => ({
    ContenidoData: ({ children, title }: any) => (
        <div data-testid="contenido-data" data-title={title}>{children}</div>
    ),
}));

vi.mock('@src/components/Load', () => ({
    default: ({ children }: any) => <div>{children}</div>,
}));

vi.mock('@src/pages/metaExpedient/details/MetaExpedientActions', () => ({
    useActions: () => ({ crearRegla: vi.fn(), toogleRegla: vi.fn() }),
}));

// ─── ReglaDistribucio ─────────────────────────────────────────────────────────

describe('ReglaDistribucio', () => {

    it('mostra icona "close" quan entity és null (sense regla)', () => {
        render(<ReglaDistribucio entity={null} />);
        const sections = screen.getAllByTestId('contenido-data');
        // Primer ContenidoData → "create" section
        expect(sections[0]).toHaveTextContent('close');
        expect(sections[0]).not.toHaveTextContent('check');
    });

    it('mostra icona "check" quan entity existeix (regla creada)', () => {
        render(<ReglaDistribucio entity={{ data: '2024-01-01', activa: true, nom: 'Regla test' }} />);
        const sections = screen.getAllByTestId('contenido-data');
        expect(sections[0]).toHaveTextContent('check');
        expect(sections[0]).not.toHaveTextContent('close');
    });

    it('no renderitza seccions de detall quan entity és null', () => {
        render(<ReglaDistribucio entity={null} />);
        // Sense entity, només el primer ContenidoData (create) és visible
        expect(screen.getAllByTestId('contenido-data')).toHaveLength(1);
    });

    it('renderitza totes les seccions de detall quan entity existeix', () => {
        render(<ReglaDistribucio entity={{ data: '2024-01-01', activa: true, nom: 'Regla test' }} />);
        // create + data + activa + nom = 4 seccions
        expect(screen.getAllByTestId('contenido-data')).toHaveLength(4);
    });

    it('mostra "check" per activa quan entity.activa és true', () => {
        render(<ReglaDistribucio entity={{ data: '2024-01-01', activa: true, nom: 'Regla' }} />);
        const sections = screen.getAllByTestId('contenido-data');
        // Secció "activa" és la tercera (index 2)
        const activaSection = sections.find(s =>
            s.getAttribute('data-title') === 'page.metaExpedient.detall.regla.activa'
        );
        expect(activaSection).toHaveTextContent('check');
    });

    it('mostra "close" per activa quan entity.activa és false', () => {
        render(<ReglaDistribucio entity={{ data: '2024-01-01', activa: false, nom: 'Regla' }} />);
        const activaSection = screen.getAllByTestId('contenido-data').find(s =>
            s.getAttribute('data-title') === 'page.metaExpedient.detall.regla.activa'
        );
        expect(activaSection).toHaveTextContent('close');
    });

    it('mostra el nom i la data de la regla', () => {
        render(<ReglaDistribucio entity={{ data: '01/01/2024', activa: true, nom: 'La meva regla' }} />);
        expect(screen.getByText('01/01/2024')).toBeInTheDocument();
        expect(screen.getByText('La meva regla')).toBeInTheDocument();
    });
});

// ─── useReglaDistribucio (hook) ───────────────────────────────────────────────

describe('useReglaDistribucio', () => {
    it('retorna handleOpen, handleClose i dialog', () => {
        const { result } = renderHook(() => useReglaDistribucio());
        expect(typeof result.current.handleOpen).toBe('function');
        expect(typeof result.current.handleClose).toBe('function');
        expect(result.current.dialog).toBeDefined();
    });

    it('el dialog renderitza sense errors (diàleg tancat per defecte)', () => {
        const { result } = renderHook(() => useReglaDistribucio());
        // open=false per defecte, MuiDialog reb open=false
        render(result.current.dialog);
        expect(screen.getByTestId('mui-dialog')).toBeInTheDocument();
    });
});
