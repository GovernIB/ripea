import {potModificar} from "../../expedient/details/Expedient.tsx";
import {useTranslation} from "react-i18next";
import { useUserSession } from "../../../components/Session.tsx";
import useCrearCarpeta from "../actions/CrearCarpeta.tsx";
import {Divider} from "@mui/material";
import useHistoric from "../../Historic.tsx";

const useCarpetaActions = (entity:any, refresh?: () => void) => {
    const { t } = useTranslation();
    const { value: user } = useUserSession()
    const potMod = potModificar(entity)

    const {handleOpen: handleHistoricOpen, dialog: dialogHistoric} = useHistoric();
    const {handelChange: handleModifyCarpeta, content: contentCrearCarpeta} = useCrearCarpeta(entity, refresh)

    const actions = [
        {
            title: t('page.carpeta.action.update.label'),
            icon: 'edit',
            showInMenu: true,
            onClick: handleModifyCarpeta,
            hidden: !potMod || !user?.sessionScope?.isCreacioCarpetesActiva,
        },
        {
            title: t('page.expedient.action.exportPDF.label'),
            icon: 'format_list_numbered',
            showInMenu: true,
            // onClick: ,
            hidden: !potMod || !user?.sessionScope?.isCreacioCarpetesActiva,
        },
        {
            title: t('page.expedient.action.exportEXCEL.label'),
            icon: 'lists',
            showInMenu: true,
            // onClick: ,
            hidden: !potMod || !user?.sessionScope?.isCreacioCarpetesActiva || !user?.sessionScope?.isExportacioExcelActiva,
        },
        {
            title: t('page.document.action.move.label'),
            icon: "open_with",
            showInMenu: true,
            // onClick: ,
            hidden: !potMod || !user?.sessionScope?.isCreacioCarpetesActiva,
        },
        {
            title: t('page.document.action.copy.label'),
            icon: "file_copy",
            showInMenu: true,
            // onClick: ,
            hidden: !potMod || !user?.sessionScope?.isCreacioCarpetesActiva || !user?.sessionScope?.isMostrarCopiar,
        },
        {
            title: t('common.delete')+'...',
            icon: "delete",
            showInMenu: true,
            // onClick: ,
            hidden: !potMod || !user?.sessionScope?.isCreacioCarpetesActiva,
        },
        {
            title: <Divider sx={{width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
        },
        {
            title: t('page.contingut.action.history.label'),
            icon: "list",
            showInMenu: true,
            onClick: handleHistoricOpen,
        },
    ]
        .map(({ hidden, ...rest }) => ({
            ...rest,
            hidden: (row: any) => (typeof hidden === 'function' ? hidden(row) : !!hidden) || row?.tipus!="CARPETA"
        }));

    const components = <>
        {dialogHistoric}
        {contentCrearCarpeta}
    </>

    return {
        actions,
        components,
    }
}
export default useCarpetaActions;