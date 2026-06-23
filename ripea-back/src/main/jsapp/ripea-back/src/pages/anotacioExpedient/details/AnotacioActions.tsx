import {useTranslation} from "react-i18next";
import useAnotacioDetail from "../../anotacions/details/AnotacioDetail.tsx";
import useSubsanarAnnexos from "../../anotacions/actions/SubsanarAnnexos.tsx";

export const useAnotacioActions = (refresh?: () => void) => {
    const { t } = useTranslation();
    const {handleOpen, dialog} = useAnotacioDetail();
    const {handleShow: handleSubsanarAnnexos, content: contentSubsanarAnnexos} = useSubsanarAnnexos(refresh);

    const actions = [
        {
            label: t('common.detail'),
            icon: "info",
            showInMenu: false,
            onClick: handleOpen,
        },
        {
            label: t('page.anotacio.action.subsanarAnnexos.label'),
            icon: "healing",
            showInMenu: true,
            onClick: handleSubsanarAnnexos,
            hidden: (row:any) => !row?.teAnnexosAmbError,
        },
    ];

    const components = <>
        {dialog}
        {contentSubsanarAnnexos}
    </>;

    return {
        actions,
        components
    }
}
export default useAnotacioActions;