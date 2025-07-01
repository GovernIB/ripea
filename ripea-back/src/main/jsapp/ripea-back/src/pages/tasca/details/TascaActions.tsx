import {Divider} from "@mui/material";
import {
    useBaseAppContext, useConfirmDialogButtons,
    useResourceApiService,
} from "reactlib";
import {useTranslation} from "react-i18next";
import useTascaDetail from "./TascaDetail.tsx";
import useRebutjar from "../actions/Rebutjar.tsx";
import useReassignar from "../actions/Reassignar.tsx";
import useDelegar from "../actions/Delegar.tsx";
import useReobrir from "../actions/Reobrir.tsx";
import useCambiarDataLimit from "../actions/CambiarDataLimit.tsx";
import useCambiarPrioritat from "../actions/CambiarPrioritat.tsx";
import useRetomar from "../actions/Retomar.tsx";

const useActions = (refresh?: () => void) => {
    const { t } = useTranslation();
    const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons();
    const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};

    const {
        artifactAction: apiAction
    } = useResourceApiService('expedientTascaResource');

    const changeEstat = (id:any, estat:string, mssg:string) => {
        apiAction(id,{code:'CHANGE_ESTAT', data:{estat}})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, mssg, 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error.message, 'error');
            });
    }

    const cancelar = (id:any) => {
        messageDialogShow(
            t('page.tasca.action.cancelar'),
            '',
            confirmDialogButtons,
            confirmDialogComponentProps)
            .then((value: any) => {
                if (value) {
                    changeEstat(id, 'CANCELLADA', t('page.tasca.action.cancel.ok'))
                }
            });
    }

    return {changeEstat, cancelar}
}

const useTascaActions = (entity:any, refresh?: () => void) => {
    const { t } = useTranslation();

    const {changeEstat, cancelar} = useActions(refresh)

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
        return isInOptions(row?.estat, 'CANCELLADA', 'FINALITZADA', 'REBUTJADA');
    }

    const isInOptions = (value:string, ...options:string[]) => {
        return options.includes(value)
    }

    const actions = [
        {
            title: t('common.detail'),
            icon: "info",
            showInMenu: true,
            onClick: handleOpen,
        },
        {
            title: <Divider sx={{px: 1, width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
            hidden: !entity?.potModificar,
        },
        {
            title: t('page.tasca.action.tramitar.label'),
            icon: "folder",
            showInMenu: true,
            onClick: (id:any, row:any) => {
                window.location.href = (`${import.meta.env.VITE_BASE_URL}contingut/${row?.expedient?.id}?tascaId=${id}`)
            },
            disabled: disableResponsable,
            hidden: (row:any)=> !entity?.potModificar || hideByEstat(row),
        },
        {
            title: t('page.tasca.action.iniciar.label'),
            icon: "play_arrow",
            showInMenu: true,
            onClick: (id: any)=> changeEstat(id,'INICIADA', t('page.tasca.action.iniciar.ok')),
            disabled: disableResponsable,
            hidden: (row: any) => !entity?.potModificar || row?.estat != 'PENDENT',
        },
        {
            title: t('page.tasca.action.rebutjar.label'),
            icon: "reply",
            showInMenu: true,
            onClick: handleRebutjar,
            disabled: disableResponsable,
            hidden: (row: any) => !entity?.potModificar || row?.estat != 'PENDENT',
        },
        {
            title: t('page.tasca.action.cancel.label'),
            icon: "close",
            showInMenu: true,
            onClick: cancelar,
            disabled: disableResponsable,
            hidden: hideByEstat,
        },
        {
            title: t('page.tasca.action.finalitzar.label'),
            icon: "check",
            showInMenu: true,
            onClick: (id: any)=> changeEstat(id,'FINALITZADA', t('page.tasca.action.finalitzar.ok')),
            disabled: disableResponsable,
            hidden: (row: any) => !entity?.potModificar || hideByEstat(row),
        },
        {
            title: <Divider sx={{px: 1, width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
            hidden: (row: any) => !entity?.potModificar || hideByEstat(row),
        },
        {
            title: t('page.tasca.action.reassignar.label'),
            icon: "person",
            showInMenu: true,
            onClick: handleReassignar,
            hidden: (row: any) => !entity?.potModificar || hideByEstat(row),
        },
        {
            title: t('page.tasca.action.delegar.label'),
            icon: "turn_right",
            showInMenu: true,
            onClick: handleDelegar,
            hidden: (row: any) => !entity?.potModificar || row?.delegat != null || hideByEstat(row),
        },
        {
            title: t('page.tasca.action.retomar.label'),
            icon: "close",
            showInMenu: true,
            onClick: handleRetomar,
            hidden: (row: any) => !entity?.potModificar || row?.delegat == null || row?.usuariActualDelegat || hideByEstat(row),
        },
        {
            title: <Divider sx={{px: 1, width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
            hidden: (row: any) => !entity?.potModificar || row?.usuariActualDelegat || hideByEstat(row),
        },
        {
            title: t('page.tasca.action.changeDataLimit.label'),
            icon: "info",
            showInMenu: true,
            onClick: handleCambiarDataLimit,
            hidden: (row: any) => !entity?.potModificar || hideByEstat(row),
        },
        {
            title: t('page.tasca.action.changePrioritat.label'),
            icon: "schedule",
            showInMenu: true,
            onClick: handleCambiarPrioritat,
            hidden: (row: any) => !entity?.potModificar || hideByEstat(row),
        },
        {
            title: t('page.tasca.action.reobrir.label'),
            icon: "undo",
            showInMenu: true,
            onClick: handleReobrir,
            hidden: (row: any) => !entity?.potModificar || row?.estat != 'FINALITZADA',
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
        components,
    }
}
export default useTascaActions;