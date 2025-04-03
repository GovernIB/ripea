import {useTranslation} from "react-i18next";
import useAmpliarPlac from "../actions/AmpliarPlac.tsx";
import useNotificacioInteressatDetail from "./NotificacioInteressatDetail.tsx";

const useNotificacioInteressatActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {handleOpen, dialog} = useNotificacioInteressatDetail();
    const {handleShow, content} = useAmpliarPlac(refresh);

    const actions = [
        {
            title: t('common.detail'),
            icon: 'info',
            showInMenu: true,
            onClick: handleOpen,
        },
        {
            title: t('page.notificacioInteressat.acciones.ampliarPlac'),
            icon: "edit_calendar",
            showInMenu: true,
            onClick: handleShow,
            hidden: (row:any) => row?.finalitzat,
        }
    ]

    const components = <>
        {dialog}
        {content}
    </>;
    return {
        actions,
        components
    }
}
export default useNotificacioInteressatActions;