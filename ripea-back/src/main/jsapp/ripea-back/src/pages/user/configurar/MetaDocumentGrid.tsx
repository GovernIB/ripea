import {useTranslation} from "react-i18next";
import { useNavigate } from "react-router-dom";
import {GridPage, useBaseAppContext, useFormContext, useMuiDataGridApiRef, useResourceApiService} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {Alert, Typography, Grid, Icon, Badge, IconButton} from "@mui/material";
import GridFormField, {FileFormField} from "../../../components/GridFormField.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import TabComponent from "../../../components/TabComponent.tsx";
import {useMemo} from "react";

const useActions = (refresh?: () => void) => {
    const {t} = useTranslation();
    const {
        patch: apiPatch
    } = useResourceApiService('metaDocumentResource');
    const {temporalMessageShow} = useBaseAppContext();

    const active = (id:any) => {
        apiPatch(id, {data: { actiu: true }})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.metaDocument.action.activar.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const desactive = (id:any) => {
        apiPatch(id, {data: { actiu: false }})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.metaDocument.action.desactivar.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {active, desactive}
}

// Form
const portafibColumns = [
    {
        field: 'portafirmesFluxId',
        flex: 1,
    },
    {
        field: 'portafirmesFluxDesc',
        flex: 1,
    },
]
const PortafirmesMetaDocumentForm = () => {
    const {data} = useFormContext()
    const filterResponsables = builder.neq('nif', null)

    const filter = useMemo(() => {
        return builder.inside("id",
            data?.fluxosFirma?.length > 0
                ? data?.fluxosFirma?.map(flux => (flux?.id))
                : [0])
    }, [data?.fluxosFirma])

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="firmaPortafirmesActiva"/>
        <GridFormField xs={12} name="portafirmesFluxTipus" required/>
        {data?.portafirmesFluxTipus == "SIMPLE" && <>
            <GridFormField xs={12} name="portafirmesResponsables" multiple autocomplete
                           filter={filterResponsables} namedQueries={[`ADD_PLUGIN_USERS`]}/>
            <GridFormField xs={12} name="portafirmesSequenciaTipus" required/>
        </>}
        {data?.portafirmesFluxTipus == "PORTAFIB" && <>
            <GridFormField xs={12} name="fluxosFirma" multiple required/>
            <StyledMuiGrid
                resourceName={'metaDocumentFluxPortafibResource'}
                columns={portafibColumns}
                filter={filter}
                toolbarHide
                sx={{ ml: 1, mt: 1 }}
            />
        </>}
    </Grid>
}

const MetaDocumentForm = () => {
    const {t} = useTranslation();
    const {fieldErrors} = useFormContext()

    const tabs = [
        {
            value: "dades",
            label: t('page.metaDocument.tabs.dades'),
            content: <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
                <GridFormField xs={12} name="codi"/>
                <GridFormField xs={12} name="nom"/>
                <GridFormField xs={12} name="descripcio" type={"textarea"}/>
                <GridFormField xs={12} name="multiplicitat" required/>
                <FileFormField xs={12} name="plantilla"/>
            </Grid>,
            error: ["codi", "nom"].some(field =>
                fieldErrors?.some?.(error => error.field === field)
            ),
        },
        {
            value: "nti",
            label: t('page.metaDocument.tabs.nti'),
            content: <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
                <GridFormField xs={12} name="ntiOrigen"/>
                <GridFormField xs={12} name="ntiTipoDocumental" required/>
                <GridFormField xs={12} name="ntiEstadoElaboracion"/>
            </Grid>,
            error: ["ntiOrigen", "ntiTipoDocumental"].some(field =>
                fieldErrors?.some?.(error => error.field === field)
            ),
        },
        {
            value: "portafirmes",
            label: t('page.metaDocument.tabs.portafirmes'),
            content: <PortafirmesMetaDocumentForm/>,
        },
        {
            value: "navegador",
            label: t('page.metaDocument.tabs.navegador'),
            content: <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
                <GridFormField xs={12} name="firmaPassarelaActiva"/>
            </Grid>,
        },
        {
            value: "viaFirma",
            label: t('page.metaDocument.tabs.viaFirma'),
            content: <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
                <GridFormField xs={12} name="firmaBiometricaActiva"/>
                <GridFormField xs={12} name="biometricaLectura"/>
            </Grid>,
        },
        {
            value: "pinbal",
            label: t('page.metaDocument.tabs.pinbal'),
            content: <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
                <Alert severity={"warning"}>Recordau que per poder realizar consultes a un servei concret de PINBAL en producció, s'ha de demanar permis a l'usuari d'integració de RIPEA per aquest procediment</Alert>
                <GridFormField xs={12} name="pinbalActiu"/>
                <GridFormField xs={12} name="pinbalServei" required/>
                <GridFormField xs={12} name="pinbalFinalitat" type={"textarea"} required/>
                <GridFormField xs={12} name="pinbalUtilitzarCifOrgan"/>
            </Grid>,
        },
    ]

    return <TabComponent tabs={tabs} scrollButtons/>
}

// Grid
const sortModel: any = [{field: 'nom', sort: 'asc'}]
const perspectives = ["COUNT_METADADES"];

const MetaDocumentGrid = () => {

    const {t} = useTranslation();
    const navigate = useNavigate();
    const apiRef = useMuiDataGridApiRef();
    
    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {active, desactive} = useActions(refresh)
    const actions = [
        {
            label: t('common.update'),
            icon: "edit",
            showInMenu: true,
            clickShowUpdateDialog: true,
        },
        {
            label: t('page.metaDocument.action.activar.label'),
            icon: "check",
            showInMenu: true,
            onClick: active,
            hidden: (row:any) => row?.actiu,
        },
        {
            label: t('page.metaDocument.action.desactivar.label'),
            icon: "close",
            showInMenu: true,
            onClick: desactive,
            hidden: (row:any) => !row?.actiu,
        },
        {
            label: t('common.delete'),
            icon: "delete",
            showInMenu: true,
            clickTriggerDelete: true,
        },
    ]

    const columns = [
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
            flex: 0.5,
            renderCell: (params:any) => (params?.row?.actiu && <Icon>check</Icon>),
        },
        {
            field: 'id',
            headerName: '',
            flex: 0.5,
            sortable: false,
            //renderCell: (props:any) => <Button component={Link} variant={"outlined"} href={`/metaDocument/${props?.id}/metaDada`}>Meta-dades <Chip label={props?.row?.numMetadades ?? 0}/></Button>
            renderCell: (params:any) => <IconButton 
                aria-label="key" 
                color="inherit"
                title={t('page.metaDada.plural')}
                onClick={(e:any) => { e.stopPropagation(); navigate(`/metaDocument/${params?.row?.id}/metaDada`); }}
            >
                <Badge badgeContent={params?.row?.numMetadades} color="primary" showZero>
                    <Typography sx={{fontSize: '1rem', paddingRight: '10px'}}>{t('page.metaDada.plural')}</Typography>
                </Badge>
            </IconButton>
        },
    ]

    return <GridPage disableMargins>
        <CardPage title={t('page.user.menu.documents')}>
            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={"metaDocumentResource"}
                popupEditUpdateActive
                popupEditFormDialogResourceTitle={t('page.metaDocument.title')}
                popupEditFormContent={<MetaDocumentForm/>}
                columns={columns}
                toolbarHideQuickFilter={false}
                filter={builder.eq("metaExpedient", null)}
                sortModel={sortModel}
                perspectives={perspectives}
                rowAdditionalActions={actions}

                popupEditFormDialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
                toolbarCreateTitle={t('page.metaDocument.action.new.label')}
                popupEditFormI18nKeys={{
                    createSuccess: 'page.metaDocument.action.new.ok',
                    updateSuccess: 'page.metaDocument.action.update.ok',
                    deleteSuccess: 'page.metaDocument.action.delete.ok',
                }}
            />
        </CardPage>
    </GridPage>
}
export default MetaDocumentGrid;