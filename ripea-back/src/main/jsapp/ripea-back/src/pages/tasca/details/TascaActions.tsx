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
import {potModificar} from "../../expedient/details/Expedient.tsx";

const useActions = (refresh?: () => void) => {
    const { t } = useTranslation();
    const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons();
    const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};

    const {
        artifactAction: apiAction
    } = useResourceApiService('expedientTascaResource');

    const changeEstat = (id:any, estat:string) => {
        apiAction(id,{code:'CHANGE_ESTAT', data:{estat}})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, '', 'success');
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
                    changeEstat(id, 'CANCELLADA')
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

    const potMod = potModificar(entity)

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
            hidden: !potMod,
        },
        {
            title: t('page.tasca.acciones.tramitar'),
            icon: "folder",
            showInMenu: true,
            onClick: (id:any, row:any) => {
                window.location.href = (`${import.meta.env.VITE_BASE_URL}contingut/${row?.expedient?.id}?tascaId=${id}`)
            },
            disabled: disableResponsable,
            hidden: (row:any)=> !potMod || hideByEstat(row),
        },
        {
            title: t('page.tasca.acciones.iniciar'),
            icon: "play_arrow",
            showInMenu: true,
            onClick: (id: any)=> changeEstat(id,'INICIADA'),
            disabled: disableResponsable,
            hidden: (row: any) => !potMod || row?.estat != 'PENDENT',
        },
        {
            title: t('page.tasca.acciones.rebutjar'),
            icon: "reply",
            showInMenu: true,
            onClick: handleRebutjar,
            disabled: disableResponsable,
            hidden: (row: any) => !potMod || row?.estat != 'PENDENT',
        },
        {
            title: t('page.tasca.acciones.cancel'),
            icon: "close",
            showInMenu: true,
            onClick: cancelar,
            disabled: disableResponsable,
            hidden: hideByEstat,
        },
        {
            title: t('page.tasca.acciones.finalitzar'),
            icon: "check",
            showInMenu: true,
            onClick: (id: any)=> changeEstat(id,'FINALITZADA'),
            disabled: disableResponsable,
            hidden: (row: any) => !potMod || hideByEstat(row),
        },
        {
            title: <Divider sx={{px: 1, width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
            hidden: (row: any) => !potMod || hideByEstat(row),
        },
        {
            title: t('page.tasca.acciones.reassignar'),
            icon: "person",
            showInMenu: true,
            onClick: handleReassignar,
            hidden: (row: any) => !potMod || hideByEstat(row),
        },
        {
            title: t('page.tasca.acciones.delegar'),
            icon: "turn_right",
            showInMenu: true,
            onClick: handleDelegar,
            hidden: (row: any) => !potMod || row?.delegat != null || hideByEstat(row),
        },
        {
            title: t('page.tasca.acciones.retomar'),
            icon: "close",
            showInMenu: true,
            onClick: handleRetomar,
            hidden: (row: any) => !potMod || row?.delegat == null || row?.usuariActualDelegat || hideByEstat(row),
        },
        {
            title: <Divider sx={{px: 1, width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
            hidden: (row: any) => !potMod || row?.usuariActualDelegat || hideByEstat(row),
        },
        {
            title: t('page.tasca.acciones.upDataLimit'),
            icon: "info",
            showInMenu: true,
            onClick: handleCambiarDataLimit,
            hidden: (row: any) => !potMod || hideByEstat(row),
        },
        {
            title: t('page.tasca.acciones.upPrioritat'),
            icon: "schedule",
            showInMenu: true,
            onClick: handleCambiarPrioritat,
            hidden: (row: any) => !potMod || hideByEstat(row),
        },
        {
            title: t('page.tasca.acciones.reobrir'),
            icon: "undo",
            showInMenu: true,
            onClick: handleReobrir,
            hidden: (row: any) => !potMod || row?.estat != 'FINALITZADA',
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