import {useTranslation} from "react-i18next";
import useAnotacioDetail from "../../anotacions/details/AnotacioDetail.tsx";

const useAnotacioActions = () => {
    const { t } = useTranslation();
    const {handleOpen, dialog} = useAnotacioDetail();

    const actions = [
        {
            title: t('common.detail'),
            icon: "info",
            showInMenu: true,
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