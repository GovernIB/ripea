import {
    useBaseAppContext,
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
import {useTranslation} from "react-i18next";

const useActions = (refresh?: () => void) => {

    const {temporalMessageShow} = useBaseAppContext();

    const {
        artifactAction: apiAction
    } = useResourceApiService('expedientTascaResource');

    const changeEstat = (id:any, estat:string) => {
        apiAction(id,{code:'ACTION_CHANGE_ESTAT', data:{estat}})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, '', 'success');
            })
            .catch((error) => {
                temporalMessageShow('Error', error.message, 'error');
            });
    }

    return {changeEstat}
}

const useTascaActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {changeEstat} = useActions(refresh)

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

    const actions = [
        {
            title: t('common.detall'),
            icon: "info",
            showInMenu: true,
            onClick: handleOpen,
        },
        {
            title: t('page.tasca.acciones.tramitar'),
            icon: "folder",
            showInMenu: true,
            disabled: disableResponsable,
            hidden: hideByEstat,
        },
        {
            title: t('page.tasca.acciones.iniciar'),
            icon: "play_arrow",
            showInMenu: true,
            onClick: (id: any)=> changeEstat(id,'INICIADA'),
            disabled: disableResponsable,
            hidden: (row: any): boolean => row?.estat != 'PENDENT',
        },
        {
            title: t('page.tasca.acciones.rebutjar'),
            icon: "reply",
            showInMenu: true,
            onClick: handleRebutjar,
            disabled: disableResponsable,
            hidden: (row: any): boolean => row?.estat != 'PENDENT',
        },
        {
            title: t('page.tasca.acciones.cancel'),
            icon: "close",
            showInMenu: true,
            onClick: (id: any)=> changeEstat(id,'CANCELLADA'),
            disabled: disableResponsable,
            hidden: hideByEstat,
        },
        {
            title: t('page.tasca.acciones.finalitzar'),
            icon: "check",
            showInMenu: true,
            onClick: (id: any)=> changeEstat(id,'FINALITZADA'),
            disabled: disableResponsable,
            hidden: hideByEstat,
        },
        {
            title: t('page.tasca.acciones.reassignar'),
            icon: "person",
            showInMenu: true,
            onClick: handleReassignar,
            hidden: hideByEstat,
        },
        {
            title: t('page.tasca.acciones.delegar'),
            icon: "turn_right",
            showInMenu: true,
            onClick: handleDelegar,
            hidden: (row: any): boolean => row?.delegat != null || hideByEstat(row),
        },
        {
            title: t('page.tasca.acciones.retomar'),
            icon: "close",
            showInMenu: true,
            onClick: handleRetomar,
            hidden: (row: any): boolean => row?.delegat == null || row?.usuariActualDelegat || hideByEstat(row),
        },
        {
            title: t('page.tasca.acciones.upDataLimit'),
            icon: "info",
            showInMenu: true,
            onClick: handleCambiarDataLimit,
            hidden: hideByEstat,
        },
        {
            title: t('page.tasca.acciones.upPrioritat'),
            icon: "schedule",
            showInMenu: true,
            onClick: handleCambiarPrioritat,
            hidden: hideByEstat,
        },
        {
            title: t('page.tasca.acciones.reobrir'),
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