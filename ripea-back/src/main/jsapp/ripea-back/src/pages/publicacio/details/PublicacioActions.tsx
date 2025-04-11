import {useTranslation} from "react-i18next";
import usePublicacioDetail from "./PublicacioDetail.tsx";

const usePublicacioActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {handleOpen: handleDetallOpen, dialog: dialogDetall} = usePublicacioDetail();

    const actions = [
        {
            title: t('common.detail'),
            icon: "info",
            showInMenu: true,
            onClick: handleDetallOpen,
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