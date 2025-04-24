import {
    useResourceApiService,
    useBaseAppContext,
} from 'reactlib';
import useAssignar from "../actions/Assignar.tsx";
import useCambiarEstat from "../actions/CambiarEstat.tsx";
import useCambiarPrioritat from "../actions/CambiarPrioritat.tsx";
import {useTranslation} from "react-i18next";
import useRelacionar from "../actions/Relacionar.tsx";
import useInformacioArxiu from "../../InformacioArxiu.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import {Divider} from "@mui/material";

const useActions = (refresh?: () => void) =>{
    const {temporalMessageShow} = useBaseAppContext();

    const {
        patch: apiPatch,
        artifactAction: apiAction
    } = useResourceApiService('expedientResource');

    const follow = (id: any): void => {
        apiAction(id, {code : 'FOLLOW'})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, '', 'success');
            })
            .catch((error) => {
                error && temporalMessageShow('Error', error.message, 'error');
            });
    }
    const unfollow = (id: any): void => {
        apiAction(id, {code : 'UNFOLLOW'})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, '', 'success');
            })
            .catch((error) => {
                error && temporalMessageShow('Error', error.message, 'error');
            });
    }
    const agafar = (id: any): void => {
        apiAction(id, {code : 'AGAFAR'})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, '', 'success');
            })
            .catch((error) => {
                error && temporalMessageShow('Error', error.message, 'error');
            });
    }
    const retornar = (id: any) :void => {
        apiAction(id, {code : 'RETORNAR'})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, '', 'success');
            })
            .catch((error) => {
                error && temporalMessageShow('Error', error.message, 'error');
            });
    }
    const lliberar = (id: any): void => {
        apiPatch(id, {
            data: {agafatPer: null,}
        })
            .then(() => {
                refresh?.()
                temporalMessageShow(null, '', 'success');
            })
            .catch((error) => {
                error && temporalMessageShow('Error', error.message, 'error');
            });
    }

    return {follow, unfollow, agafar, retornar, lliberar}
}

export const useCommonActions = (refresh?: () => void) => {
    const { t } = useTranslation();
    const { value: user } = useUserSession();
    const isRolActualAdmin = user?.rolActual == 'IPA_ADMIN';
    const isRolActualOrganAdmin = user?.rolActual == 'IPA_ORGAN_ADMIN';

    const {follow, unfollow, agafar, retornar, lliberar} = useActions(refresh);

    const {handleOpen: handleArxiuOpen, dialog: arxiuDialog} = useInformacioArxiu('expedientResource', 'ARXIU_EXPEDIENT');
    const {handleShow: hanldeAssignar, content: assignarContent} = useAssignar(refresh);
    const {handleShow: hanldeCambiarEstado, content: cambiarEstadoContent} = useCambiarEstat(refresh);
    const {handleShow: hanldeCambiarPrioridad, content: cambiarPrioridadContent} = useCambiarPrioritat(refresh);
    const {handleShow: hanldeRelacionar, content: cambiarRelacionar} = useRelacionar(refresh);

    const actions = [
        {
            title: t('page.expedient.acciones.detall'),
            icon: "folder",
            linkTo: "/contingut/{{id}}",
            showInMenu: true,
        },
        {
            title: t('page.expedient.acciones.follow'),
            icon: "person_add",
            showInMenu: true,
            onClick: follow,
            hidden: (row:any) => row?.seguidor,// si el usuario actual es seguidor
        },
        {
            title: t('page.expedient.acciones.unfollow'),
            icon: "person_remove",
            showInMenu: true,
            onClick: unfollow,
            hidden: (row:any) => !row?.seguidor,// si el usuario actual no es seguidor
        },
        {
            title: <Divider sx={{px: 1, width: '100%'}}/>,
            showInMenu: true,
        },
        {
            title: t('page.expedient.acciones.assignar'),
            icon: "person",
            showInMenu: true,
            onClick: hanldeAssignar,
            hidden: !( isRolActualAdmin || isRolActualOrganAdmin ),// si el usuario actual no admin o organo
        },
        {
            title: <Divider sx={{px: 1, width: '100%'}}/>,
            showInMenu: true,
            hidden: !( isRolActualAdmin || isRolActualOrganAdmin ),
        },
        {
            title: t('page.expedient.acciones.agafar'),
            icon: "lock",
            showInMenu: true,
            onClick: agafar,
            hidden: (row:any) => row?.agafatPer?.id == user?.codi
        },
        {
            title: t('page.expedient.acciones.retornar'),
            icon: "undo",
            showInMenu: true,
            onClick: retornar,
            hidden: (row:any) => row?.agafatPer?.id == row?.createdBy
        },
        {
            title: t('page.expedient.acciones.lliberar'),
            icon: "lock_open",
            showInMenu: true,
            onClick: lliberar,
            hidden: (row:any) => !row?.agafatPer,
        },
        {
            title: t('page.expedient.acciones.upPrioritat'),
            icon: "logout",
            showInMenu: true,
            onClick: hanldeCambiarPrioridad
        },
        {
            title: t('page.expedient.acciones.upEstat'),
            icon: "logout",
            showInMenu: true,
            onClick: hanldeCambiarEstado,
            hidden: (row:any) => row?.estat != "OBERT",
        },
        {
            title: t('page.expedient.acciones.relacio'),
            icon: "link",
            showInMenu: true,
            onClick: hanldeRelacionar,
        },
        {
            title: t('page.expedient.acciones.close'),
            icon: "check",
            showInMenu: true,
            disabled: (row:any) => !row?.potTancar,
            hidden: (row:any) => row?.estat != "OBERT",
        },
        {
            title: <Divider sx={{px: 1, width: '100%'}}/>,
            showInMenu: true,
        },
        {
            title: t('page.expedient.acciones.history'),
            icon: "list",
            showInMenu: true,
        },
        {
            title: t('page.expedient.acciones.download'),
            icon: "download",
            showInMenu: true,
            hidden: (row:any) => !row?.conteDocuments,
        },
        {
            title: t('page.expedient.acciones.exportPDF'),
            icon: "format_list_numbered",
            showInMenu: true,
            hidden: (row:any) => !row?.conteDocuments,
        },
        {
            title: t('page.expedient.acciones.exportEXCEL'),
            icon: "lists",
            showInMenu: true,
            hidden: (row:any) => !(row?.conteDocuments && user?.sessionScope?.isExportacioExcelActiva),
        },
        {
            title: t('page.expedient.acciones.exportPDF_EIN'),
            icon: "format_list_numbered",
            showInMenu: true,
            disabled: (row:any) => !row?.conteDocumentsDefinitius,
            hidden: (row:any) => !row?.conteDocuments,
        },
        {
            title: t('page.expedient.acciones.exportEIN'),
            icon: "folder_code",
            showInMenu: true,
            hidden: (row:any) => !(row?.conteDocuments && row?.conteDocumentsDefinitius),
        },
        {
            title: t('page.expedient.acciones.exportINSIDE'),
            icon: "folder_zip",
            showInMenu: true,
            disabled: (row:any) => !row?.conteDocumentsDefinitius,
            hidden: (row:any) => !(row?.conteDocuments && user?.sessionScope?.isExportacioInsideActiva),
        },
        {
            title: t('page.expedient.acciones.infoArxiu'),
            icon: "info",
            showInMenu: true,
            onClick: handleArxiuOpen,
            disabled: (row:any) => !row?.arxiuUuid,
        },
        {
            title: t('page.expedient.acciones.sincronitzar'),
            icon: "autorenew",
            showInMenu: true,
        },
    ]

    const components = <>
        {cambiarPrioridadContent}
        {cambiarEstadoContent}
        {arxiuDialog}
        {assignarContent}
        {cambiarRelacionar}
    </>;

    return {
        actions,
        components
    }
}