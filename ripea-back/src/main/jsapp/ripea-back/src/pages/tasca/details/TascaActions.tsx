import {useRef} from "react";
import {
    MuiFormDialogApi,
} from "reactlib";
import useTascaDetail from "./TascaDetail.tsx";
import CambiarPrioritat from "../actions/CambiarPrioritat.tsx";
import CambiarDataLimit from "../actions/CambiarDataLimit.tsx";
import Delegar from "../actions/Delegar.tsx";
import Reassignar from "../actions/Reassignar.tsx";

const useTascaActions = (refresh?: () => void) => {
    // const {
    //     delette: apiDelete,
    //     patch: apiPatch,
    //     getOne
    // } = useResourceApiService('expedientTascaResource');

    // const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
    // const confirmDialogButtons = useConfirmDialogButtons();
    // const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};

    const reassignarApiRef = useRef<MuiFormDialogApi>();
    const delegarApiRef = useRef<MuiFormDialogApi>();
    const cambiarPrioridadApiRef = useRef<MuiFormDialogApi>();
    const cambiarFechaLimiteApiRef = useRef<MuiFormDialogApi>();
    const {handleOpen, dialog} = useTascaDetail();

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
            disabled: true,
        },
        {
            title: "Iniciar",
            icon: "play_arrow",
            showInMenu: true,
            disabled: true,
        },
        {
            title: "Rechazar",
            icon: "reply",
            showInMenu: true,
            disabled: true,
        },
        {
            title: "Cancelar",
            icon: "close",
            showInMenu: true,
            disabled: true,
        },
        {
            title: "Finalizar",
            icon: "check",
            showInMenu: true,
            disabled: true,
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
        },
    ];

    const components = <>
        <Reassignar apiRef={reassignarApiRef}/>
        <Delegar apiRef={delegarApiRef}/>
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