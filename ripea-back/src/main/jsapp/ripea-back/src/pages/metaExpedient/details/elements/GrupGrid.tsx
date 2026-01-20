import {useTranslation} from "react-i18next";
import StyledMuiGrid, { ToolbarButton } from "../../../../components/StyledMuiGrid.tsx";
import * as builder from "../../../../util/springFilterUtils.ts";
import {Icon} from "@mui/material";

const sortModel: any = [{field: 'codi', sort: 'asc'}]
const perspectives: string[] = [];
export const GrupGrid = ({ entity, onRowCountChange } :any) => {
    const {t} = useTranslation()

    const grupColumns = [
        {
            field: 'codi',
            flex: 0.5,
        },
        {
            field: 'descripcio',
            flex: 1,
        },
        {
            field: 'organGestor',
            flex: 1,
        },
        {
            field: 'id',
            headerName: 'Per defecte',
            flex: 0.5,
            renderCell: (params:any) => (params?.id == entity?.grupPerDefecte?.id && <Icon>check</Icon>),
        },
    ]

    const actions = [
        {
            label: t('page.grup.action.unlink.label'),
            icon: "link_off",
            showInMenu: true,
            onClick: () => {},
        },
        {
            label: t('page.grup.action.default.label'),
            icon: "check",
            showInMenu: true,
            onClick: () => {},
        },
    ]

    return <StyledMuiGrid
        resourceName={'grupResource'}
        columns={grupColumns}
        toolbarHideQuickFilter={false}
        filter={builder.exists(builder.eq("metaExpedients.id", entity?.id))}
        sortModel={sortModel}
        perspectives={perspectives}
        rowAdditionalActions={actions}
        toolbarHideCreate
        onRowCountChange={onRowCountChange}

        toolbarElementsWithPositions={[
            {
                position: 3,
                element: <ToolbarButton icon={'add'} onClick={()=>{}}>
                    {t('page.grup.action.link.label')}
                </ToolbarButton>,
            },
        ]}
    />
}