import React from 'react';
import CambiarEstado from './CambiarEstado';
import CambiarPrioritat from './CambiarPrioritat';
import {MuiFormDialogApi} from 'reactlib';
import useDocumentDetall from "../detall/DocumentDetall.tsx";
import useInformacioArxiu from "../detall/InformacioArxiu.tsx";

export const useContingutActions = (refresh?: () => void) => {
    // const { temporalMessageShow } = useBaseAppContext();
    // const {
    //     patch: apiPatch,
    // } = useResourceApiService('expedientResource');
    const cambiarPrioridadApiRef = React.useRef<MuiFormDialogApi>();
    const cambiarEstadoApiRef = React.useRef<MuiFormDialogApi>();

    const {handleOpen: detallhandleOpen, dialog: detallDialog} = useDocumentDetall();
    const {handleOpen: arxiuhandleOpen, dialog: arxiuDialog} = useInformacioArxiu();

    const actions = [
        {
            title: "Detalles",
            icon: "folder",
            showInMenu: true,
            onClick: (id:number,row:any)=>{
                detallhandleOpen(row)
            }
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
            onClick: (id:number,row:any)=>{
                arxiuhandleOpen(row)
            }
        },
        {
            title: "Exportación EIN...",
            icon: "download",
            showInMenu: true,
            disabled: true,
        },
    ]
    const components = <>
        <CambiarPrioritat apiRef={cambiarPrioridadApiRef} />
        <CambiarEstado apiRef={cambiarEstadoApiRef} />
        {detallDialog}
        {arxiuDialog}
    </>;
    return {
        actions,
        components
    }
}