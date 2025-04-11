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

const useActions = (refresh?: () => void) =>{
    const {temporalMessageShow} = useBaseAppContext();

    const {
        patch: apiPatch,
    } = useResourceApiService('expedientResource');

    const agafar = (id: any): void => {
        apiPatch(id, {
            data: {
                agafatPer: {
                    // TODO: change for user session
                    id: "rip_admin"
                },
            }
        })
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
            data: {
                agafatPer: null,
            }
        })
            .then(() => {
                refresh?.()
                temporalMessageShow(null, '', 'success');
            })
            .catch((error) => {
                error && temporalMessageShow('Error', error.message, 'error');
            });
    }

    return {agafar, lliberar}
}

export const useCommonActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {agafar, lliberar} = useActions(refresh);

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
            // hidden: // si el usuario actual es seguidor
        },
        {
            title: t('page.expedient.acciones.unfollow'),
            icon: "person_remove",
            showInMenu: true,
            hidden: true,// si el usuario actual no es seguidor
        },
        {
            title: t('page.expedient.acciones.assignar'),
            icon: "person",
            showInMenu: true,
            onClick: hanldeAssignar,
            // hidden: // si el usuario actual no admin o organo
        },
        {
            title: t('page.expedient.acciones.agafar'),
            icon: "lock",
            showInMenu: true,
            onClick: agafar,
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
            icon: "",
            showInMenu: true,
            onClick: hanldeCambiarPrioridad
        },
        {
            title: t('page.expedient.acciones.upEstat'),
            icon: "",
            showInMenu: true,
            onClick: hanldeCambiarEstado,
            disabled: (row:any) => row?.estat != "OBERT",
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
            disabled: (row:any) => row?.estat != "OBERT",
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
        },
        {
            title: t('page.expedient.acciones.exportPDF'),
            icon: "format_list_numbered",
            showInMenu: true,
        },
        {
            title: t('page.expedient.acciones.exportEIN'),
            icon: "format_list_numbered",
            showInMenu: true,
            disabled: true,
        },
        {
            title: t('page.expedient.acciones.infoArxiu'),
            icon: "info",
            showInMenu: true,
            onClick: handleArxiuOpen
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