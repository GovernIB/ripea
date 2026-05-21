import DocumentsGrid from '../../contingut/DocumentsGrid.tsx';

export type CarpetaProps = {
    expedient: any;
    carpetaId: string | number;
    onRowCountChange?: (n: number) => void;
};

// Vista de contingut d'una carpeta: reutilitza el grid de l'expedient amb 'contingutScopeId'
const Carpeta = (props: CarpetaProps) => {
    const { expedient, carpetaId, onRowCountChange } = props;

    return (
        <DocumentsGrid
            entity={expedient}
            contingutScopeId={carpetaId}
            onRowCountChange={onRowCountChange}
        />
    );
};

export default Carpeta;
