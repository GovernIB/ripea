import {Grid2 as Grid, Icon, Divider} from "@mui/material";
import GridFormField from "../../../../components/GridFormField.tsx";
import {StyledBadge} from "../../../../components/StyledBadge.tsx";
import {useTranslation} from "react-i18next";
import {DndMuiGrid} from "../../../../components/StyledMuiGrid.tsx";
import * as builder from "../../../../util/springFilterUtils.ts";
import {useBaseAppContext, useMuiDataGridApiRef, useResourceApiService} from "reactlib";
import {useMemo} from "react";
import useMetaExpEstatDetail from "./details/MetaExpEstatDetail.tsx";

const useActions = (refresh?: () => void) => {
    const {
        artifactAction: apiAction,
    } = useResourceApiService('metaExpedientEstatResource');
    const {temporalMessageShow} = useBaseAppContext();

    const reordering = (id:any, ordre:number) => {
        apiAction(id, { code: 'REORDENAR', data: ordre })
            .then(() => refresh?.())
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {reordering}
}

const MetaExpedientEstatForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="codi"/>
        <GridFormField name="nom"/>
        <GridFormField name="color" type={'color'}/>
        <GridFormField name="inicial"/>
        <GridFormField name="responsable"/>
    </Grid>
}

const sortModel: any = [{field: 'ordre', sort: 'asc'}]
const perspectives: string[] = [];
const columns:any = [
    {
        field: 'codi',
        flex: 1,
    },
    {
        field: 'nom',
        flex: 1,
    },
    {
        field: 'inicial',
        flex: 0.5,
        renderCell: (params:any) => (params?.row?.inicial && <Icon>check</Icon>),
    },
    {
        field: 'responsable',
        flex: 1,
    },
    {
        field: 'color',
        flex: 0.5,
        renderCell: (params:any) => (params?.row?.color && <StyledBadge badgecolor={params?.formattedValue} overlap="circular" badgeContent=" "/>)
    },
]
export const MetaExpedientEstatGrid = ({ entity, onRowCountChange, readOnly } :any) => {
    const {t} = useTranslation()
    const apiRef = useMuiDataGridApiRef();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {reordering} = useActions(refresh)
    const {apiIsReady, handleOpen, dialog} = useMetaExpEstatDetail()
    const actions:any[] = useMemo(() => readOnly ?[
        {
            label: t('page.metaExpedient.action.consultar.label'),
            icon: "search",
            showInMenu: false,
            onClick: handleOpen,
        },
    ]:[
        {
            label: t('common.update'),
            icon: "edit",
            showInMenu: true,
            clickShowUpdateDialog: true,
        },
        {
            label: <Divider sx={{px: 1, width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
        },
        {
            label: t('common.delete'),
            icon: "delete",
            showInMenu: true,
            clickTriggerDelete: true,
        },
    ], [t, readOnly, apiIsReady]);

    const handleDragEnd = (event: any) => {
        const sourceData = event.active.data.current;
        const targetData = event.over.data.current;
        // console.log('>>> ', sourceData.codi, '(', sourceData.ordre, ') ->', targetData.codi, '(', targetData.ordre, ')')
        if (sourceData.id != targetData.id) {
            reordering(sourceData.id, targetData.ordre)
        }
    }

    return <><DndMuiGrid
        apiRef={apiRef}
        resourceName={'metaExpedientEstatResource'}
        popupEditUpdateActive
        popupEditFormDialogResourceTitle={t('page.expedientEstat.title')}
        popupEditFormContent={<MetaExpedientEstatForm/>}
        columns={columns}
        filter={builder.eq("metaExpedient.id", entity?.id)}
        formAdditionalData={{ metaExpedient: {id: entity?.id} }}
        staticSortModel={sortModel}
        perspectives={perspectives}
        rowAdditionalActions={actions}
        onRowCountChange={onRowCountChange}

        onDragEnd={handleDragEnd}

        popupEditFormDialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
        toolbarCreateTitle={t('page.expedientEstat.action.new.label')}
        popupEditFormI18nKeys={{
            createSuccess: 'page.expedientEstat.action.new.ok',
            updateSuccess: 'page.expedientEstat.action.update.ok',
            deleteSuccess: 'page.expedientEstat.action.delete.ok',
        }}
        readOnly={readOnly}
    />{dialog}</>
}