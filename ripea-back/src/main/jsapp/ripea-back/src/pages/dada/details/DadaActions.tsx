import useDataGrid from "./DataGrid.tsx";
import {useTranslation} from "react-i18next";

export const useDadaActions = (contingut:any, refresh?: () => void) => {
    const { t } = useTranslation();

    const {handleOpen, content} = useDataGrid(contingut, refresh)

    const actions = [
        {
            title: t('common.detail'),
            icon: "info",
            showInMenu: true,
            onClick: handleOpen,
            hidden: (row:any) => row?.readOnly,
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