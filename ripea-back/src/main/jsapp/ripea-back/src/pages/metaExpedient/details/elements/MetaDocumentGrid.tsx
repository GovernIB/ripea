import {Badge, Icon, Typography} from "@mui/material";
import {useTranslation} from "react-i18next";
import {useMuiDataGridApiRef} from "reactlib";
import {useMemo} from "react";
import LinkButton from "../../../../components/LinkButton.tsx";
import {useMetaDocumentActions} from "../../../metaDocument/details/MetaDocumentActions.tsx";
import StyledMuiGrid from "../../../../components/StyledMuiGrid.tsx";
import {MetaDocumentForm} from "../../../metaDocument/MetaDocumentGrid.tsx";
import * as builder from "../../../../util/springFilterUtils.ts";

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
    ], [t])

    const {actions} = useMetaDocumentActions(refresh);
    const additionalActions = useMemo(() => [
        ...actions,
        {
            label: t('page.metaDocument.action.default.label'),
            icon: "check_box",
            showInMenu: true,
            onClick: ()=>{},
        },
    ], [actions])

    return <StyledMuiGrid
        apiRef={apiRef}
        resourceName={"metaDocumentResource"}
        popupEditUpdateActive
        popupEditFormDialogResourceTitle={t('page.metaDocument.title')}
        popupEditFormContent={<MetaDocumentForm/>}
        columns={additionalColumns}
        toolbarHideQuickFilter={false}
        filter={builder.eq("metaExpedient.id", entity?.id)}
        formAdditionalData={{ metaExpedient: {id: entity?.id} }}
        staticSortModel={sortModel}
        perspectives={perspectives}
        rowAdditionalActions={additionalActions}
        onRowCountChange={onRowCountChange}

        popupEditFormDialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
        toolbarCreateTitle={t('page.metaDocument.action.new.label')}
        popupEditFormI18nKeys={{
            createSuccess: 'page.metaDocument.action.new.ok',
            updateSuccess: 'page.metaDocument.action.update.ok',
            deleteSuccess: 'page.metaDocument.action.delete.ok',
        }}
    />
}