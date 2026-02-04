import {Grid, Icon} from "@mui/material";
import GridFormField from "../../../../components/GridFormField.tsx";
import {StyledBadge} from "../../../../components/StyledBadge.tsx";
import {useTranslation} from "react-i18next";
import StyledMuiGrid from "../../../../components/StyledMuiGrid.tsx";
import * as builder from "../../../../util/springFilterUtils.ts";
import {DraggableGridRow, DraggableGridRowHandler} from "../../../../components/DraggableContext.tsx";
import {GridSlots} from "@mui/x-data-grid-pro";
import {DndContext} from "@dnd-kit/core";
import {useBaseAppContext, useMuiDataGridApiRef, useResourceApiService} from "reactlib";

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
        <GridFormField xs={12} name="codi"/>
        <GridFormField xs={12} name="nom"/>
        <GridFormField xs={12} name="color" type={'color'}/>
        <GridFormField xs={12} name="inicial"/>
        <GridFormField xs={12} name="responsable"/>
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
    {
        renderCell: () => <DraggableGridRowHandler />,
        flex: 0.1
    }
]
export const MetaExpedientEstatGrid = ({ entity, onRowCountChange } :any) => {
    const {t} = useTranslation()
    const apiRef = useMuiDataGridApiRef();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {reordering} = useActions(refresh)
    const actions = [
        {
            label: t('common.update'),
            icon: "edit",
            showInMenu: true,
            clickShowUpdateDialog: true,
        },
        {
            label: t('common.delete'),
            icon: "delete",
            showInMenu: true,
            clickTriggerDelete: true,
        },
    ]

    const handleDragEnd = (event: any) => {
        const sourceData = event.active.data.current;
        const targetData = event.over.data.current;
        // console.log('>>> ', sourceData.codi, '(', sourceData.ordre, ') ->', targetData.codi, '(', targetData.ordre, ')')
        if (sourceData.id != targetData.id) {
            reordering(sourceData.id, targetData.ordre)
        }
    }

    return <DndContext onDragEnd={handleDragEnd}><StyledMuiGrid
        apiRef={apiRef}
        resourceName={'metaExpedientEstatResource'}
        popupEditUpdateActive
        popupEditFormDialogResourceTitle={t('page.expedientEstat.title')}
        popupEditFormContent={<MetaExpedientEstatForm/>}
        columns={columns}
        rowActionsColumnIndex={-1}
        filter={builder.eq("metaExpedient.id", entity?.id)}
        formAdditionalData={{ metaExpedient: {id: entity?.id} }}
        staticSortModel={sortModel}
        perspectives={perspectives}
        rowAdditionalActions={actions}
        onRowCountChange={onRowCountChange}

        slots={{
            row: DraggableGridRow as GridSlots['row'],
        }}

        popupEditFormDialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
        toolbarCreateTitle={t('page.expedientEstat.action.new.label')}
        popupEditFormI18nKeys={{
            createSuccess: 'page.expedientEstat.action.new.ok',
            updateSuccess: 'page.expedientEstat.action.update.ok',
            deleteSuccess: 'page.expedientEstat.action.delete.ok',
        }}
    /></DndContext>
}