import {useRef} from "react";
import {
    MuiFormDialogApi, useResourceApiService,
} from "reactlib";
import useTascaDetail from "./TascaDetail.tsx";
import CambiarPrioritat from "../actions/CambiarPrioritat.tsx";
import CambiarDataLimit from "../actions/CambiarDataLimit.tsx";
import Delegar from "../actions/Delegar.tsx";
import Reassignar from "../actions/Reassignar.tsx";
import Rebutjar from "../actions/Rebutjar.tsx";
import Reobrir from "../actions/Reobrir.tsx";

const useTascaActions = (refresh?: () => void) => {
    const {
        action: apiAction
    } = useResourceApiService('expedientTascaResource');

    // const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
    // const confirmDialogButtons = useConfirmDialogButtons();
    // const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};

    const rebutjarApiRef = useRef<MuiFormDialogApi>();
    const reassignarApiRef = useRef<MuiFormDialogApi>();
    const delegarApiRef = useRef<MuiFormDialogApi>();
    const reobrirApiRef = useRef<MuiFormDialogApi>();
    const cambiarPrioridadApiRef = useRef<MuiFormDialogApi>();
    const cambiarFechaLimiteApiRef = useRef<MuiFormDialogApi>();
    const {handleOpen, dialog} = useTascaDetail();

    const disableResponsable = (row: any):boolean => {
        return !row?.usuariActualResponsable && !row?.usuariActualDelegat;
    }
    const hideByEstat= (row: any):boolean => {
        return row?.estat == 'CANCELLADA' || row?.estat == 'FINALITZADA' || row?.estat == 'REBUTJADA';
    }

    const actions = [
        {
            title: "Detalle",
            icon: "info",
            showInMenu: true,
            onClick: handleOpen,
        },
        {
            title: "Tramitar",
            icon: "folder",
            showInMenu: true,
            disabled: disableResponsable,
            hidden: hideByEstat,
        },
        {
            title: "Iniciar",
            icon: "play_arrow",
            showInMenu: true,
            onClick: (rowId: any)=> {
                apiAction({code:'ACTION_CHANGE_ESTAT', data:{
                    id:rowId, estat: 'INICIADA'
                }})
                    .then(()=>{
                        refresh?.()
                    })
            },
            // disabled: disableResponsable,
            hidden: (row: any):boolean => row?.estat != 'PENDENT',
        },
        {
            title: "Rechazar",
            icon: "reply",
            showInMenu: true,
            // onClick: (rowId: any)=> {
            //     rebutjarApiRef.current?.show(rowId,{data: {
            //             estat: 'REBUTJADA',
            //         }})
            //         .then(()=>{
            //             refresh?.()
            //         })
            // },
            disabled: disableResponsable,
            hidden: (row: any):boolean => row?.estat != 'PENDENT',
        },
        {
            title: "Cancelar",
            icon: "close",
            showInMenu: true,
            onClick: (rowId: any)=> {
                apiAction({code:'ACTION_CHANGE_ESTAT', data:{
                        id:rowId, estat: 'CANCELLADA'
                }})
                    .then(()=>{
                        refresh?.()
                    })
            },
            disabled: disableResponsable,
            hidden: hideByEstat,
        },
        {
            title: "Finalizar",
            icon: "check",
            showInMenu: true,
            onClick: (rowId: any)=> {
                apiAction({code:'ACTION_CHANGE_ESTAT', data:{
                        id:rowId, estat: 'FINALITZADA'
                    }})
                    .then(()=>{
                        refresh?.()
                    })
            },
            disabled: disableResponsable,
            hidden: hideByEstat,
        },
        {
            title: "Reasignar",
            icon: "person",
            showInMenu: true,
            onClick: (rowId: any) => {
                reassignarApiRef.current?.show(rowId)
                    .then(()=>{
                        refresh?.()
                    })
            },
            hidden: hideByEstat,
        },
        {
            title: "Delegar",
            icon: "turn_right",
            showInMenu: true,
            onClick: (rowId: any) => {
                delegarApiRef.current?.show(rowId)
                    .then(()=>{
                        refresh?.()
                    })
            },
            hidden: (row: any):boolean => row?.delegat!=null || hideByEstat(row),
        },
        {
            title: "Retomar",
            icon: "close",
            showInMenu: true,
            // onClick: (rowId: any)=> {
            //     apiPatch(rowId,{data: {
            //             delegat: null,
            //             comentari: null,
            //         }})
            //         .then(()=>{
            //             refresh?.()
            //         })
            // },
            hidden: (row: any):boolean => row?.delegat==null || row?.usuariActualDelegat || hideByEstat(row),
        },
        {
            title: "Modificar fecha limite...",
            icon: "info",
            showInMenu: true,
            onClick: (rowId: any) => {
                cambiarFechaLimiteApiRef.current?.show(rowId)
                    .then(()=>{
                        refresh?.()
                    })
            },
            hidden: hideByEstat,
        },
        {
            title: "Cambiar prioridad...",
            icon: "schedule",
            showInMenu: true,
            onClick: (rowId: any) => {
                cambiarPrioridadApiRef.current?.show(rowId)
                    .then(()=>{
                        refresh?.()
                    })
            },
            hidden: hideByEstat,
        },
        {
            title: "Reabrir",
            icon: "undo",
            showInMenu: true,
            // onClick: (rowId: any) => {
            //     reobrirApiRef.current?.show(rowId)
            //         .then(()=>{
            //             refresh?.()
            //         })
            // },
            hidden: (row:any):boolean => row?.estat != 'FINALITZADA',
        },
    ];

    const components = <>
        <Rebutjar apiRef={rebutjarApiRef}/>
        <Reassignar apiRef={reassignarApiRef}/>
        <Delegar apiRef={delegarApiRef}/>
        <Reobrir apiRef={reobrirApiRef}/>
        <CambiarDataLimit apiRef={cambiarFechaLimiteApiRef}/>
        <CambiarPrioritat apiRef={cambiarPrioridadApiRef}/>
        {dialog}
    </>;

    return {
        actions,
        components
    }
}
export default useTascaActions;