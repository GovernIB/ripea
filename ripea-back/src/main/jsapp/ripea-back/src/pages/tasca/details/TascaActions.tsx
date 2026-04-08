import {Divider} from "@mui/material";
import {useBaseAppContext, useConfirmDialogButtons,useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import useTascaDetail from "./TascaDetail.tsx";
import useRebutjar from "../actions/Rebutjar.tsx";
import useReassignar from "../actions/Reassignar.tsx";
import useDelegar from "../actions/Delegar.tsx";
import useReobrir from "../actions/Reobrir.tsx";
import useCambiarDataLimit from "../actions/CambiarDataLimit.tsx";
import useCambiarPrioritat from "../actions/CambiarPrioritat.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import useRetomar from "../actions/Retomar.tsx";
import {useNavigate} from "react-router-dom";
import useHistoric, {HistoricContingutTipusEnum} from "../../Historic.tsx";

export const useActions = (refresh?: () => void) => {
    const { t } = useTranslation();
    const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons().reverse();
    const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};

    const {
        artifactAction: apiAction
    } = useResourceApiService('expedientTascaResource');

    const changeEstat = (id:any, estat:string, mssg:string, onSuccess?: () =>  void) => {
        apiAction(id,{code:'CHANGE_ESTAT', data:{estat}})
            .then(() => {
                refresh?.()
                onSuccess?.()
                temporalMessageShow(null, mssg, 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error.message, 'error');
            });
    }

    const cancelar = (id:any) => {
        messageDialogShow(
            t('page.tasca.action.cancel.label'),
            t('page.tasca.action.cancel.title'),
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
    const navigate = useNavigate();
    const {changeEstat, cancelar} = useActions(refresh)
    const { value: user } = useUserSession();

    const {handleOpen: handleHistoricOpen, dialog: dialogHistoric} = useHistoric(HistoricContingutTipusEnum.TASCA);
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
    const hiddenByEstat = (row: any): boolean => {
        return isInOptions(row?.estat, 'CANCELLADA', 'FINALITZADA', 'REBUTJADA');
    }
    // const nomesMostraDetalls = (row: any): boolean => {
    //     return isInOptions(row?.estat, 'CANCELLADA', 'REBUTJADA');
    // }
    const isInOptions = (value:string, ...options:string[]) => {
        return options.includes(value)
    }
    const isOnlyObservador = (row: any): boolean => {
         return row?.observadors?.map((obs:any)=>obs?.id).includes(user.codi) && !row?.usuariActualResponsable && !row?.usuariActualDelegat;
    }
    const hiddenTramitable = (row:any) => isOnlyObservador(row) || hiddenByEstat(row)

    const actions:any[] = [
        {
            label: t('common.detail'),
            icon: "info",
            showInMenu: true,
            onClick: handleOpen,
        },
        {
            label: <Divider sx={{px: 1, width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
        },
        {
            label: t('page.tasca.action.tramitar.label'),
            icon: "folder",
            showInMenu: true,
            onClick: (id:any, row:any) => navigate(`/contingut/${row?.expedient?.id}/tasca/${id}`),
            disabled: disableResponsable,
            hidden: (row: any) => hiddenTramitable(row) || !entity?.potModificar,
        },
        {
            label: t('page.tasca.action.iniciar.label'),
            icon: "play_arrow",
            showInMenu: true,
            onClick: (id: any)=> changeEstat(id,'INICIADA', t('page.tasca.action.iniciar.ok')),
            disabled: disableResponsable,
            hidden: (row: any) => isOnlyObservador(row) || row?.estat != 'PENDENT' || !entity?.potModificar,
        },
        {
            label: t('page.tasca.action.rebutjar.label'),
            icon: "reply",
            showInMenu: true,
            onClick: handleRebutjar,
            disabled: disableResponsable,
            hidden: (row: any) => isOnlyObservador(row) || row?.estat != 'PENDENT' || !entity?.potModificar,
        },
        {
            label: t('page.tasca.action.cancel.label'),
            icon: "close",
            showInMenu: true,
            onClick: cancelar,
            disabled: disableResponsable,
            hidden: (row: any) => isOnlyObservador(row) || hiddenByEstat(row) || !entity?.potModificar,
        },
        {
            label: t('page.tasca.action.finalitzar.label'),
            icon: "check",
            showInMenu: true,
            onClick: (id: any)=> changeEstat(id,'FINALITZADA', t('page.tasca.action.finalitzar.ok')),
            disabled: disableResponsable,
            hidden: (row: any) => isOnlyObservador(row) || hiddenByEstat(row) || !entity?.potModificar,
        },
        {
            label: <Divider sx={{px: 1, width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
            hidden: (row: any) => isOnlyObservador(row) || hiddenByEstat(row) || !entity?.potModificar,
        },
        {
            label: t('page.tasca.action.reassignar.label'),
            icon: "person",
            showInMenu: true,
            onClick: handleReassignar,
            hidden: (row: any) => isOnlyObservador(row) || hiddenByEstat(row) || !entity?.potModificar,
        },
        {
            label: t('page.tasca.action.delegar.label'),
            icon: "turn_right",
            showInMenu: true,
            onClick: handleDelegar,
            hidden: (row: any) => isOnlyObservador(row) || row?.delegat != null || hiddenByEstat(row) || !entity?.potModificar,
        },
        {
            label: t('page.tasca.action.retomar.label'),
            icon: "close",
            showInMenu: true,
            onClick: handleRetomar,
            hidden: (row: any) => isOnlyObservador(row) || row?.delegat == null || row?.usuariActualDelegat || hiddenByEstat(row) || !entity?.potModificar,
        },
        {
            label: <Divider sx={{px: 1, width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
            hidden: (row: any) => isOnlyObservador(row) || row?.usuariActualDelegat || hiddenByEstat(row) || !entity?.potModificar,
        },
        {
            label: t('page.contingut.action.history.label'),
            icon: "list",
            showInMenu: true,
            onClick: handleHistoricOpen,
        },
        {
            label: t('page.tasca.action.changeDataLimit.label'),
            icon: "schedule",
            showInMenu: true,
            onClick: handleCambiarDataLimit,
            hidden: (row: any) => isOnlyObservador(row) || hiddenByEstat(row) || !entity?.potModificar,
        },
        {
            label: t('page.tasca.action.changePrioritat.label'),
            icon: "info",
            showInMenu: true,
            onClick: handleCambiarPrioritat,
            hidden: (row: any) => isOnlyObservador(row) || hiddenByEstat(row) || !entity?.potModificar,
        },
        {
            label: t('page.tasca.action.reobrir.label'),
            icon: "undo",
            showInMenu: true,
            onClick: handleReobrir,
            hidden: (row: any) => isOnlyObservador(row) || row?.estat != 'FINALITZADA' || !entity?.potModificar,
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
        {dialogHistoric}
        {dialog}
    </>;

    return {
        actions,
        components,
        isTramitable: (row:any) => !hiddenTramitable(row),
    }
}
export default useTascaActions;