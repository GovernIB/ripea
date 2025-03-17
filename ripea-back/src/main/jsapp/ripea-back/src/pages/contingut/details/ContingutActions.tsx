import useDocumentDetail from "./DocumentDetail.tsx";
import useInformacioArxiu from "../../expedient/actions/InformacioArxiu.tsx";

export const useContingutActions = (refresh?: () => void) => {
    // const { temporalMessageShow } = useBaseAppContext();
    // const {
    //     patch: apiPatch,
    // } = useResourceApiService('expedientResource');

    const {handleOpen: detallhandleOpen, dialog: detallDialog} = useDocumentDetail();
    const {handleOpen: arxiuhandleOpen, dialog: arxiuDialog} = useInformacioArxiu("archivo");

    const actions = [
        {
            title: "Detalles",
            icon: "folder",
            showInMenu: true,
            onClick: detallhandleOpen
        },
        {
            title: "Mover...",
            icon: "open_with",
            showInMenu: true,
        },
        {
            title: "Descargar",
            icon: "download",
            showInMenu: true,
        },
        {
            title: "Visualizar",
            icon: "search",
            showInMenu: true,
            disabled: true,
        },
        {
            title: "Enviar a portafirmas...",
            icon: "mail",
            showInMenu: true,
        },
        {
            title: "Firma desde el navegador...",
            icon: "edit_document",
            showInMenu: true,
        },
        {
            title: "Enviar via email...",
            icon: "mail",
            showInMenu: true,
        },
        {
            title: "Histórico de acciones",
            icon: "list",
            showInMenu: true,
        },
        {
            title: "Información archivo",
            icon: "info",
            showInMenu: true,
            onClick: arxiuhandleOpen
        },
        {
            title: "Exportación EIN...",
            icon: "download",
            showInMenu: true,
            disabled: true,
        },
    ]
    const components = <>
        {detallDialog}
        {arxiuDialog}
    </>;
    return {
        actions,
        components
    }
}