import {useTranslation} from "react-i18next";
import useMoure from "../actions/Moure.tsx";

const useContingutMassiveActions = (entity:any, refresh?: () => void) => {
    const { t } = useTranslation();

    const {handleMassiveShow: handleMoure, content: contentMoure} = useMoure(refresh)

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
            onClick: (ids:any[])=>handleMoure(ids, entity),
        },
        {
            title: "Cambiar tipo",
            icon: "edit",
        },
    ]

    const components = <>
        {contentMoure}
    </>

    return {
        actions,
        components
    }
}
export default useContingutMassiveActions;