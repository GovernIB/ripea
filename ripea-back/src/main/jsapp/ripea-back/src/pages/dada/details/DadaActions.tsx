import useDataGrid from "./DataGrid.tsx";
import {useTranslation} from "react-i18next";

export const useDadaActions = (entity:any, refresh?: () => void) => {
    const { t } = useTranslation();

    const {handleOpen, content} = useDataGrid(entity, refresh)

    const actions = [
        {
            label: t('common.update'),
            icon: "edit",
            showInMenu: false,
            onClick: handleOpen,
            hidden: (row:any) => row?.readOnly || !entity?.potModificar,
        }
    ]

    const components = <>
        {content}
    </>;
    return {
        actions,
        components,
    }
}