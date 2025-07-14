import {useTranslation} from "react-i18next";
import usePublicacioDetail from "./PublicacioDetail.tsx";

const usePublicacioActions = () => {
    const { t } = useTranslation();

    const {handleOpen: handleDetallOpen, dialog: dialogDetall} = usePublicacioDetail();

    const actions = [
        {
            title: t('common.detail'),
            icon: "info",
            showInMenu: true,
            onClick: handleDetallOpen,
        },
        {
            title: t('common.update')+'...',
            icon: 'edit',
            showInMenu: true,
            clickShowUpdateDialog: true,
        },
        {
            title: t('common.delete')+'...',
            icon: 'edit',
            showInMenu: true,
            clickTriggerDelete: true,
        },
    ];

    const components = <>
        {dialogDetall}
    </>;

    return {
        actions,
        components
    }
}
export default usePublicacioActions;