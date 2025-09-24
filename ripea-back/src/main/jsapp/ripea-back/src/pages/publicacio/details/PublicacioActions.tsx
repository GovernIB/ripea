import {useTranslation} from "react-i18next";
import usePublicacioDetail from "./PublicacioDetail.tsx";

const usePublicacioActions = (entity:any) => {
    const { t } = useTranslation();

    const {handleOpen: handleDetallOpen, dialog: dialogDetall} = usePublicacioDetail();

    const actions = [
        {
            label: t('common.detail'),
            icon: "info",
            showInMenu: entity?.potModificar,
            onClick: handleDetallOpen,
        },
        {
            label: t('common.update')+'...',
            icon: 'edit',
            showInMenu: true,
            clickShowUpdateDialog: true,
            hidden: !entity?.potModificar,
        },
        {
            label: t('common.delete')+'...',
            icon: 'edit',
            showInMenu: true,
            clickTriggerDelete: true,
            hidden: !entity?.potModificar,
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