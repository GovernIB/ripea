import {Badge, Icon, Typography} from "@mui/material";
import {useTranslation} from "react-i18next";
import {useMuiDataGridApiRef} from "reactlib";
import {useMemo} from "react";
import LinkButton from "../../../../components/LinkButton.tsx";
import {useActions, useMetaDocumentActions} from "../../../metaDocument/details/MetaDocumentActions.tsx";
import StyledMuiGrid from "../../../../components/StyledMuiGrid.tsx";
import {MetaDocumentForm} from "../../../metaDocument/MetaDocumentGrid.tsx";
import * as builder from "../../../../util/springFilterUtils.ts";
import {DndContext} from "@dnd-kit/core";
import {DraggableGridRow, DraggableGridRowHandler} from "../../../../components/DraggableContext.tsx";
import {GridSlots} from "@mui/x-data-grid-pro";

const sortModel: any = [{field: 'ordre', sort: 'asc'}]
const perspectives = ["COUNT_METADADES"];
const columns: any[] = [
    {
        field: 'codi',
        flex: 1,
    },
    {
        field: 'nom',
        flex: 1,
    },
    {
        field: 'actiu',
        flex: 0.25,
        renderCell: (params:any) => (params?.row?.actiu && <Icon>check</Icon>),
    },
    {
        field: 'perDefecte',
        flex: 0.25,
        renderCell: (params:any) => (params?.row?.perDefecte && <Icon>check</Icon>),
    },
    {
        field: 'multiplicitat',
        flex: 0.5,
    },
    {
        field: 'ntiOrigen',
        flex: 0.5,
    },
    {
        field: 'ntiTipoDocumental',
        flex: 1,
    },
    {
        field: 'firmaPortafirmesActiva',
        flex: 0.25,
        renderCell: (params:any) => (params?.row?.firmaPortafirmesActiva && <Icon>check</Icon>),
    },
    {
        field: 'firmaPassarelaActiva',
        flex: 0.25,
        renderCell: (params:any) => (params?.row?.firmaPassarelaActiva && <Icon>check</Icon>),
    },
    {
        field: 'pinbalActiu',
        flex: 0.25,
        renderCell: (params:any) => (params?.row?.pinbalActiu && <Icon>check</Icon>),
    },
]
export const MetaDocumentGrid = ({ entity, onRowCountChange } :any) => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const additionalColumns = useMemo(() => [
        ...columns,
        {
            field: 'id',
            headerName: '',
            flex: 0.5,
            sortable: false,
            renderCell: (params:any) => <LinkButton
                aria-label="key"
                color="inherit"
                title={t('page.metaDada.plural')}
                to={`/metaDocument/${params?.row?.id}/metaDada`}
            >
                <Badge badgeContent={params?.row?.numMetadades} color="primary" showZero>
                    <Typography sx={{fontSize: '1rem', paddingRight: '10px'}}>{t('page.metaDada.plural')}</Typography>
                </Badge>
            </LinkButton>
        },
        {
            renderCell: () => <DraggableGridRowHandler />,
            flex: 0.1
        }
    ], [t])

    const {marcarDefecte, desmarcarDefecte, reordering} = useActions(refresh)
    const {actions} = useMetaDocumentActions(refresh);
    const additionalActions = useMemo(() => [
        ...actions,
        {
            label: t('page.metaDocument.action.default.label'),
            icon: "check_box",
            showInMenu: true,
            onClick: marcarDefecte,
            hidden: (row:any) => row?.perDefecte,
        },
        {
            label: t('page.metaDocument.action.undefault.label'),
            icon: "close",
            showInMenu: true,
            onClick: desmarcarDefecte,
            hidden: (row:any) => !row?.perDefecte,
        },
    ], [t, actions])

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
        resourceName={"metaDocumentResource"}
        popupEditUpdateActive
        popupEditFormDialogResourceTitle={t('page.metaDocument.title')}
        popupEditFormContent={<MetaDocumentForm/>}
        columns={additionalColumns}
        rowActionsColumnIndex={-1}
        toolbarHideQuickFilter={false}
        filter={builder.eq("metaExpedient.id", entity?.id)}
        formAdditionalData={{ metaExpedient: {id: entity?.id} }}
        staticSortModel={sortModel}
        perspectives={perspectives}
        rowAdditionalActions={additionalActions}
        onRowCountChange={onRowCountChange}

        slots={{
            row: DraggableGridRow as GridSlots['row'],
        }}

        popupEditFormDialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
        toolbarCreateTitle={t('page.metaDocument.action.new.label')}
        popupEditFormI18nKeys={{
            createSuccess: 'page.metaDocument.action.new.ok',
            updateSuccess: 'page.metaDocument.action.update.ok',
            deleteSuccess: 'page.metaDocument.action.delete.ok',
        }}
    /></DndContext>
}