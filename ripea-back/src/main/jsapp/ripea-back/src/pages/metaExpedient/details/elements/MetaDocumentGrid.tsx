import {Badge, Icon, Typography} from "@mui/material";
import {useTranslation} from "react-i18next";
import {useMuiDataGridApiRef} from "reactlib";
import {useCallback, useMemo} from "react";
import LinkIcon from "../../../../components/LinkIcon.tsx";
import {useActions, useMetaDocumentActions} from "../../../metaDocument/details/MetaDocumentActions.tsx";
import {MetaDocumentForm} from "../../../metaDocument/MetaDocumentGrid.tsx";
import * as builder from "../../../../util/springFilterUtils.ts";
import StyledMuiGrid from "../../../../components/StyledMuiGrid.tsx";
import useMetaDocumentDetail from "./details/MetaDocumentDetail.tsx";
import {MultiplicitatStyled} from "../../../contingut/details/MetaExpedient.tsx";
import {icons} from "@src/util/icons.ts";
import {esMetaDocumentPerDefecte} from "@src/util/metaDocumentUtils.ts";
import {useUserSession} from "../../../../components/Session.tsx";

const sortModel: any = [{field: 'ordre', sort: 'asc'}]
const perspectives = ["COUNT_METADADES"];
const editPerspectives = ["PORTAFIRMES_RESPONSABLES"];
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
        renderCell: (params:any) => <MultiplicitatStyled multiplicitat={params?.formattedValue}/>
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

export const MetaDocumentGrid = ({ entity, onRowCountChange, readOnly } :any) => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const {rol} = useUserSession();

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
            renderCell: (params:any) => <LinkIcon
                aria-label="key"
                color="inherit"
                title={t('page.metaDada.plural')}
                to={`/metaDocument/${params?.row?.id}/metaDada`}
            >
                <Badge badgeContent={params?.row?.numMetadades} color="primary" showZero>
                    <Typography sx={{fontSize: '1rem', paddingRight: '10px'}}>{t('page.metaDada.plural')}</Typography>
                </Badge>
            </LinkIcon>
        },
    ], [t])

    const {apiIsReady, handleOpen, dialog} = useMetaDocumentDetail()
    const {reordering} = useActions()
    const {actions} = useMetaDocumentActions(refresh);

    // Els tipus de document creats per defecte a l'alta del procediment només els pot
    // modificar un administrador d'entitat; per a la resta de rols només es consulten.
    const nomesConsulta = useCallback(
        (row:any) => readOnly || (esMetaDocumentPerDefecte(row?.codi) && !rol?.isAdmin),
        [readOnly, rol])

    const additionalActions:any[] = useMemo(() => [
        {
            label: t('page.metaExpedient.action.consultar.label'),
            icon: icons.detall,
            showInMenu: false,
            onClick: handleOpen,
            hidden: (row:any) => !nomesConsulta(row),
        },
        ...actions.map((action:any) => ({
            ...action,
            hidden: (row:any) => nomesConsulta(row) ||
                (typeof action.hidden === 'function' ? action.hidden(row) : !!action.hidden),
        })),
    ], [t, actions, nomesConsulta, apiIsReady])

    const handleDragEnd = (params: any) => {
        if (params.targetIndex != params.oldIndex) {
            reordering(params.row.id, params.targetIndex)
        }
    }

    return <><StyledMuiGrid
        apiRef={apiRef}
        resourceName={"metaDocumentResource"}
		persistentStateKey={"metaDocumentResource_procedimentTab"}
        popupEditUpdateActive
        popupEditFormDialogResourceTitle={t('page.metaDocument.title')}
        popupEditFormContent={<MetaDocumentForm/>}
        columns={additionalColumns}
        toolbarShowQuickFilter
        filter={builder.eq("metaExpedient.id", entity?.id)}
        formAdditionalData={{ metaExpedient: {id: entity?.id} }}
        staticSortModel={sortModel}
        perspectives={perspectives}
        rowAdditionalActions={additionalActions}
        onRowCountChange={onRowCountChange}
        popupEditFormComponentProps={{ perspectives: editPerspectives }}
        rowReordering={!readOnly}
        onRowOrderChange={handleDragEnd}
        popupEditFormDialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
        toolbarCreateTitle={t('page.metaDocument.action.new.label')}
        popupEditFormI18nKeys={{
            createSuccess: 'page.metaDocument.action.new.ok',
            updateSuccess: 'page.metaDocument.action.update.ok',
            deleteSuccess: 'page.metaDocument.action.delete.ok',
        }}
        readOnly={readOnly}
    />
        {dialog}
    </>
}