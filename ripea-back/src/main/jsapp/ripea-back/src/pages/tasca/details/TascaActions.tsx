import {
    useResourceApiService,
} from "reactlib";
import useTascaDetail from "./TascaDetail.tsx";
import useRebutjar from "../actions/Rebutjar.tsx";
import useReassignar from "../actions/Reassignar.tsx";
import useDelegar from "../actions/Delegar.tsx";
import useReobrir from "../actions/Reobrir.tsx";
import useCambiarDataLimit from "../actions/CambiarDataLimit.tsx";
import useCambiarPrioritat from "../actions/CambiarPrioritat.tsx";
import useRetomar from "../actions/Retomar.tsx";

const useTascaActions = (refresh?: () => void) => {
    const {
        action: apiAction
    } = useResourceApiService('expedientTascaResource');

    const {handleShow: handleRebutjar, content: rebutjarContent} = useRebutjar(refresh);
    const {handleShow: handleReassignar, content: reassignarContent} = useReassignar(refresh);
    const {handleShow: handleDelegar, content: delegarContent} = useDelegar(refresh);
    const {handleShow: handleReobrir, content: reobrirContent} = useReobrir(refresh);
    const {handleShow: handleCambiarDataLimit, content: cambiarDataLimitContent} = useCambiarDataLimit(refresh);
    const {handleShow: handleCambiarPrioritat, content: cambiarPrioritatContent} = useCambiarPrioritat(refresh);
    const {handleShow: handleRetomar, content: retomarContent} = useRetomar(refresh);
    const { handleOpen, dialog } = useTascaDetail();

    const disableResponsable = (row: any): boolean => {
        return !row?.usuariActualResponsable && !row?.usuariActualDelegat;
    }
    const hideByEstat = (row: any): boolean => {
        return row?.estat == 'CANCELLADA' || row?.estat == 'FINALITZADA' || row?.estat == 'REBUTJADA';
    }

    const changeEstat = (id:any, estat:string) => {
        return apiAction(id,{code:'ACTION_CHANGE_ESTAT', data:{estat}})
            .then(()=>{
                refresh?.()
            })
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
            onClick: (id: any)=> changeEstat(id,'INICIADA'),
            // disabled: disableResponsable,
            hidden: (row: any): boolean => row?.estat != 'PENDENT',
        },
        {
            title: "Rechazar",
            icon: "reply",
            showInMenu: true,
            onClick: handleRebutjar,
            disabled: disableResponsable,
            hidden: (row: any): boolean => row?.estat != 'PENDENT',
        },
        {
            title: "Cancelar",
            icon: "close",
            showInMenu: true,
            onClick: (id: any)=> changeEstat(id,'CANCELLADA'),
            disabled: disableResponsable,
            hidden: hideByEstat,
        },
        {
            title: "Finalizar",
            icon: "check",
            showInMenu: true,
            onClick: (id: any)=> changeEstat(id,'FINALITZADA'),
            disabled: disableResponsable,
            hidden: hideByEstat,
        },
        {
            title: "Reasignar",
            icon: "person",
            showInMenu: true,
            onClick: handleReassignar,
            hidden: hideByEstat,
        },
        {
            title: "Delegar",
            icon: "turn_right",
            showInMenu: true,
            onClick: handleDelegar,
            hidden: (row: any): boolean => row?.delegat != null || hideByEstat(row),
        },
        {
            title: "Retomar",
            icon: "close",
            showInMenu: true,
            onClick: handleRetomar,
            hidden: (row: any): boolean => row?.delegat == null || row?.usuariActualDelegat || hideByEstat(row),
        },
        {
            title: "Modificar fecha limite...",
            icon: "info",
            showInMenu: true,
            onClick: handleCambiarDataLimit,
            hidden: hideByEstat,
        },
        {
            title: "Cambiar prioridad...",
            icon: "schedule",
            showInMenu: true,
            onClick: handleCambiarPrioritat,
            hidden: hideByEstat,
        },
        {
            title: "Reabrir",
            icon: "undo",
            showInMenu: true,
            onClick: handleReobrir,
            hidden: (row: any): boolean => row?.estat != 'FINALITZADA',
        },
    ];

    const components = <>
        {rebutjarContent}
        {reassignarContent}
        {delegarContent}
        {reobrirContent}
        {cambiarPrioritatContent}
        {cambiarDataLimitContent}
        {retomarContent}
        {dialog}
    </>;

    return {
        actions,
        components
    }
}
export default useTascaActions;