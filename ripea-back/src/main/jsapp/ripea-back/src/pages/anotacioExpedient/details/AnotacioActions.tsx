import {useTranslation} from "react-i18next";
import useAnotacioDetail from "../../anotacions/details/AnotacioDetail.tsx";

export const useAnotacioActions = () => {
    const { t } = useTranslation();
    const {handleOpen, dialog} = useAnotacioDetail();

    const actions = [
        {
            label: t('common.detail'),
            icon: "info",
            showInMenu: false,
            onClick: handleOpen,
        },
    ];

    const components = <>
        {dialog}
    </>;

    return {
        actions,
        components
    }
}
export default useAnotacioActions;