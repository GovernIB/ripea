import {useTranslation} from "react-i18next";

const useContingutMassiveActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const actions = [
        {
            title: t('common.download'),
            icon: "download",
        },
        {
            title: t('page.document.acciones.notificar'),
            icon: "mail",
        },
        {
            title: t('page.document.acciones.move'),
            icon: "open_with",
        },
        {
            title: "Cambiar tipo",
            icon: "edit",
        },
    ]

    const components = <>
    </>

    return {
        actions,
        components
    }
}
export default useContingutMassiveActions;