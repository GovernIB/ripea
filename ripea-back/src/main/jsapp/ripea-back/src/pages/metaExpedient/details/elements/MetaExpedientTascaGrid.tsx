import {useTranslation} from "react-i18next";
import {Badge, Grid, Icon, Divider} from "@mui/material";
import StyledMuiGrid from "../../../../components/StyledMuiGrid.tsx";
import * as builder from "../../../../util/springFilterUtils.ts";
import {useBaseAppContext, useFormContext, useMuiDataGridApiRef, useResourceApiService} from "reactlib";
import GridFormField from "../../../../components/GridFormField.tsx";
import {StyledPrioritat} from "../../../expedient/ExpedientGrid.tsx";
import LinkIcon from "../../../../components/LinkIcon.tsx";
import { StyledBadge } from "../../../../components/StyledBadge.tsx";
import {useMemo} from "react";
import useMetaExpTascaDetail from "./details/MetaExpTascaDetail.tsx";

const useActions = (refresh?: () => void) => {
    const {t} = useTranslation();
    const {
        patch: apiPatch,
        artifactAction: apiAction,
    } = useResourceApiService('metaExpedientTascaResource');
    const {temporalMessageShow} = useBaseAppContext();

    const active = (id:any) => {
        apiPatch(id, {data: { activa: true }})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.metaExpedientTasca.action.activar.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const desactive = (id:any) => {
        apiPatch(id, {data: { activa: false }})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.metaExpedientTasca.action.desactivar.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const reordering = (id:any, ordre:number) => {
        apiAction(id, { code: 'REORDENAR', data: ordre })
            .then(() => refresh?.())
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {active, desactive, reordering}
}
const MetaExpedientTascaForm = () => {
    const {t} = useTranslation()
    const {data} = useFormContext()

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="codi"/>
        <GridFormField name="nom"/>
        <GridFormField name="responsable"/>
        <GridFormField name="duracio" decimalScale={0}
                       componentProps={{helperText: t('page.metaExpedientTasca.detall.duracio')}}/>
        <GridFormField name="descripcio" type={"textarea"}/>
        <GridFormField name="prioritat" required/>
        <GridFormField name="estatCrearTasca" filter={builder.eq("metaExpedient.id", data?.metaExpedient?.id)}/>
        <GridFormField name="estatFinalitzarTasca" filter={builder.eq("metaExpedient.id", data?.metaExpedient?.id)}/>
        <GridFormField name="activa"/>
    </Grid>
}

const sortModel: any = [{field: 'ordre', sort: 'asc'}]
const perspectives: string[] = ['COUNT_VALIDACIONS'];
const columns = [
    {
        field: 'codi',
        flex: 0.5,
    },
    {
        field: 'nom',
        flex: 0.5,
    },
    {
        field: 'responsable',
        flex: 1,
    },
    {
        field: 'duracio',
        flex: 0.5,
    },
    {
        field: 'prioritat',
        flex: 0.5,
        renderCell: (params:any) => <StyledPrioritat entity={params?.row}>{params?.formattedValue}</StyledPrioritat>
    },
    {
        field: 'estatCrearTasca',
        flex: 1,
        renderCell: (params:any) => (params?.row?.estatColorCrearTasca && <StyledBadge badgecolor={params?.row?.estatColorCrearTasca} overlap="circular" badgeContent=" " sx={{ '& .MuiBadge-badge': {right: -3, top: 10 } }}>{params?.formattedValue}</StyledBadge>),
    },
    {
        field: 'estatFinalitzarTasca',
        flex: 1,
        renderCell: (params:any) => (params?.row?.estatColorFinalitzarTasca && <StyledBadge badgecolor={params?.row?.estatColorFinalitzarTasca} overlap="circular" badgeContent=" " sx={{ '& .MuiBadge-badge': {right: -3, top: 10 } }}>{params?.formattedValue}</StyledBadge>),
    },
    {
        field: 'activa',
        flex: 0.5,
        renderCell: (params:any) => (params?.row?.activa && <Icon>check</Icon>),
    },
    {
        field: 'numValidacio',
        headerName: '',
        sortable: false,
        flex: 0.25,
        renderCell: (params:any) => <LinkIcon
            aria-label="key"
            color="inherit"
            title="Validacions"
            to={`/metaExpedient/${params?.row?.metaExpedient.id}/tasca/${params?.row?.id}/validacio`}
        >
            <Badge badgeContent={params?.row?.numValidacio} color="primary" showZero>
                <Icon>checklist</Icon>
            </Badge>
        </LinkIcon>
    },
]
export const MetaExpedientTascaGrid = ({ entity, onRowCountChange, readOnly } :any) => {
    const {t} = useTranslation()
    const apiRef = useMuiDataGridApiRef();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const handleDragEnd = (params: any) => {
        if (params.targetIndex != params.oldIndex) {
            reordering(params.row.id, params.targetIndex)
        }
    }

    const {active, desactive, reordering} = useActions(refresh)
    const {apiIsReady, handleOpen, dialog} = useMetaExpTascaDetail()
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
            label: t('page.metaExpedientTasca.action.activar.label'),
            icon: "check",
            showInMenu: true,
            onClick: active,
            hidden: (row:any) => row?.activa,
        },
        {
            label: t('page.metaExpedientTasca.action.desactivar.label'),
            icon: "close",
            showInMenu: true,
            onClick: desactive,
            hidden: (row:any) => !row?.activa,
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

    return (
        <>
            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={'metaExpedientTascaResource'}
                popupEditUpdateActive
                popupEditFormDialogResourceTitle={t('page.metaExpedientTasca.title')}
                popupEditFormContent={<MetaExpedientTascaForm />}
                columns={columns}
                toolbarShowQuickFilter
                filter={builder.eq('metaExpedient.id', entity?.id)}
                formAdditionalData={{ metaExpedient: { id: entity?.id } }}
                staticSortModel={sortModel}
                perspectives={perspectives}
                rowAdditionalActions={actions}
                onRowCountChange={onRowCountChange}
                rowReordering={!readOnly}
                onRowOrderChange={handleDragEnd}
                popupEditFormDialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
                toolbarCreateTitle={t('page.metaExpedientTasca.action.new.label')}
                popupEditFormI18nKeys={{
                    createSuccess: 'page.metaExpedientTasca.action.new.ok',
                    updateSuccess: 'page.metaExpedientTasca.action.update.ok',
                    deleteSuccess: 'page.metaExpedientTasca.action.delete.ok',
                }}
                readOnly={readOnly}
            />
            {dialog}
        </>
    );
}
