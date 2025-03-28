import useDataGrid from "./DataGrid.tsx";
import {useTranslation} from "react-i18next";

export const useDadaActions = (contingut:any, refresh?: () => void) => {
    const { t } = useTranslation();

    const {handleOpen, content} = useDataGrid(contingut, refresh)

    const actions = [
        {
            title: t('page.metaDada.acciones.detail'),
            icon: "info",
            showInMenu: true,
            onClick: handleOpen
        }
    ]

    const components = <>
        {content}
    </>;
    return {
        actions,
        components
    }
}