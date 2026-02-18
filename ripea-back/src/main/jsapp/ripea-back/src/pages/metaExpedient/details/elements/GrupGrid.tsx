import {useTranslation} from "react-i18next";
import StyledMuiGrid, { ToolbarButton } from "../../../../components/StyledMuiGrid.tsx";
import * as builder from "../../../../util/springFilterUtils.ts";
import {Icon} from "@mui/material";
import {useVincularGrup} from "../../actions/VincularGrup.tsx";
import {useActions} from "../MetaExpedientActions.tsx";
import {useMemo} from "react";
import useGrupDetail from "./details/GrupDetail.tsx";

const sortModel: any = [{field: 'codi', sort: 'asc'}]
const perspectives: string[] = [];
export const GrupGrid = ({ entity, refresh: refreshEntity, onRowCountChange, readOnly } :any) => {
    const {t} = useTranslation()

    const refresh = () => {
        refreshEntity?.();
    }

    const columns = useMemo(() => [
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
            headerName: t('page.grup.grid.default'),
            flex: 0.5,
            renderCell: (params:any) => (params?.id == entity?.grupPerDefecte?.id && <Icon>check</Icon>),
        },
    ], [t, entity]);

    const {handleShow: handleVincular, content: contentVincular} = useVincularGrup(refresh);
    const {defecte, llevarDefecte, desvincularGrup} = useActions(refresh)
    const {apiIsReady, handleOpen, dialog} = useGrupDetail()
    const actions = useMemo(() => readOnly ?[
        {
            label: t('page.metaExpedient.action.consultar.label'),
            icon: "search",
            showInMenu: false,
            onClick: handleOpen
        },
    ]:[
        {
            label: t('page.grup.action.unlink.label'),
            icon: "link_off",
            showInMenu: true,
            onClick: (id:any) => desvincularGrup(entity?.id, id),
        },
        {
            label: t('page.grup.action.default.label'),
            icon: "check",
            showInMenu: true,
            onClick: (id:any) => defecte(entity?.id, id),
            hidden: (row:any) => row?.id == entity?.grupPerDefecte?.id
        },
        {
            label: t('page.grup.action.undefault.label'),
            icon: "close",
            showInMenu: true,
            onClick: () => llevarDefecte(entity?.id),
            hidden: (row:any) => row?.id != entity?.grupPerDefecte?.id
        },
    ], [t, readOnly, apiIsReady]);

    const elementsWithPositions:any[] = useMemo(() => [
        {
            position: 3,
            element: <ToolbarButton icon={'add'} onClick={() => handleVincular(entity?.id)}>
                {t('page.grup.action.link.label')}
            </ToolbarButton>,
            hidden: readOnly,
        },
    ], [t, readOnly]);

    return <>
        <StyledMuiGrid
            resourceName={'grupResource'}
            columns={columns}
            toolbarHideQuickFilter={false}
            filter={builder.exists(builder.eq("metaExpedients.id", entity?.id))}
            sortModel={sortModel}
            perspectives={perspectives}
            rowAdditionalActions={actions}
            toolbarHideCreate
            onRowCountChange={onRowCountChange}

            toolbarElementsWithPositions={elementsWithPositions}
            readOnly={readOnly}
        />
        {contentVincular}
        {dialog}
    </>
}