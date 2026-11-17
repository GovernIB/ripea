import DocumentsGrid from '../../contingut/DocumentsGrid.tsx';

export type CarpetaProps = {
    expedient: any;
    carpetaId: string | number;
    onRowCountChange?: (n: number) => void;
    // Cada canvi de valor torna a carregar el contingut (veure DocumentsGrid)
    refreshKey?: number;
};

// Vista de contingut d'una carpeta: reutilitza el grid de l'expedient amb 'contingutScopeId'
const Carpeta = (props: CarpetaProps) => {
    const { expedient, carpetaId, onRowCountChange, refreshKey } = props;

    return (
        <DocumentsGrid
            entity={expedient}
            contingutScopeId={carpetaId}
            onRowCountChange={onRowCountChange}
            refreshKey={refreshKey}
        />
    );
};

export default Carpeta;
