import {Grid} from "@mui/material";
import GridFormField from "../../../../components/GridFormField.tsx";
import {useTranslation} from "react-i18next";
import StyledMuiGrid from "../../../../components/StyledMuiGrid.tsx";
import * as builder from "../../../../util/springFilterUtils.ts";
import {useFormContext} from "reactlib";
import {GridTreeDataGroupingCell} from "@mui/x-data-grid-pro";
import {useMemo} from "react";
import useMetaExpCarpetaDetail from "./details/MetaExpCarpetaDetail.tsx";

const MetaExpedientCarpetaForm = () => {
    const {data} = useFormContext()
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="nom"/>
        <GridFormField xs={12} name="pare"
                       filter={builder.and(
                           builder.eq("metaExpedient.id", data?.metaExpedient?.id),
                           builder.eq("pare", null),
                       )}
                       componentProps={{ helperText: data?.id ?"":"Si es deixa en blanc, es creará com a carpeta principal." }}
                       disabled={data?.id}/>
    </Grid>
}

const sortModel: any = [{field: 'nom', sort: 'asc'}]
const perspectives: string[] = [];
const columns:any = [
    // {
    //     field: 'codi',
    //     flex: 1,
    // },
    // {
    //     field: 'nom',
    //     flex: 1,
    // },
]
export const MetaExpedientCarpetaGrid = ({ entity, onRowCountChange, readOnly } :any) => {
    const {t} = useTranslation()

    const {apiIsReady, handleOpen, dialog} = useMetaExpCarpetaDetail()
    const actions = useMemo(() => readOnly ?[
        {
            label: t('page.metaExpedient.action.consultar.label'),
            icon: "search",
            showInMenu: false,
            onClick: handleOpen
        },
    ]:[
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
    ], [t, readOnly, apiIsReady]);

    return <><StyledMuiGrid
        resourceName={'metaExpedientCarpetaResource'}
        popupEditUpdateActive
        popupEditFormDialogResourceTitle={t('page.carpeta.title')}
        popupEditFormContent={<MetaExpedientCarpetaForm/>}
        columns={columns}
        filter={builder.eq("metaExpedient.id", entity?.id)}
        formAdditionalData={{ metaExpedient: {id: entity?.id} }}
        staticSortModel={sortModel}
        perspectives={perspectives}
        rowAdditionalActions={actions}
        onRowCountChange={onRowCountChange}

        treeData
        isGroupExpandedByDefault={() => true}
        groupingColDef={{
            headerName: t('page.contingut.grid.nom'),
            flex: 1.5,
            valueFormatter: (_value: any, row: any) => row?.nom,
            renderCell: (params: any) => <GridTreeDataGroupingCell {...params} />,
        }}
        getTreeDataPath={(row: any): string[] => {
            return row?.pare ?[`${row?.pare?.id}`, `${row.id}`] :[`${row.id}`];
        }}

        // popupEditFormDialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
        toolbarCreateTitle={t('page.carpeta.action.new.label')}
        popupEditFormI18nKeys={{
            createSuccess: 'page.carpeta.action.new.ok',
            updateSuccess: 'page.carpeta.action.update.ok',
            deleteSuccess: 'page.carpeta.action.delete.ok',
        }}
        readOnly={readOnly}
    />{dialog}</>
}