import {useTranslation} from "react-i18next";
import {
    GridPage,
    MuiDialog,
    useFormContext,
    useMuiDataGridApiRef,
    useResourceApiService
} from "reactlib";
import {CardPage} from "../../components/CardData.tsx";
import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";
import {Alert, Typography, Grid, Icon, Badge} from "@mui/material";
import GridFormField, {FileFormField, GridButton} from "../../components/GridFormField.tsx";
import * as builder from "../../util/springFilterUtils.ts";
import TabComponent from "../../components/TabComponent.tsx";
import {useMemo, useState} from "react";
import Load from "../../components/Load.tsx";
import Iframe from "../../components/Iframe.tsx";
import {useFluxFinalitzatSession} from "../../components/SseClient.tsx";
import {useMetaDocumentActions} from "./details/MetaDocumentActions.tsx";
import LinkIcon from "../../components/LinkIcon.tsx";

const useFluxActions = () => {
    const {
        isReady: apiIsReady,
        artifactAction: apiAction,
    } = useResourceApiService('metaDocumentFluxPortafibResource');

    const [open, setOpen] = useState(false);
    const [url, setUrl] = useState<string>();

    const handleOpen = (id:any, row:any) => {
        if(apiIsReady){
            if (!id) {
                apiAction(undefined, {code: 'CREACIO_FLUXE', data: {metaDocumentId: row?.metaDocument?.id}})
                    .then((app) => setUrl(app?.url))
            } else {
                apiAction(id, {code: 'EDITAR_FLUXE'})
                    .then((app) => setUrl(app?.url))
            }
        }
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setUrl(undefined);
            setOpen(false);
        }
    };

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={''}
            componentProps={{ fullWidth: true, maxWidth: 'md' }}
        >
            <Load value={url}>
                <Iframe src={url}/>
            </Load>
        </MuiDialog>

    return {
        apiIsReady,
        handleOpen,
        handleClose,
        dialog
    }
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
    const {t} = useTranslation()
    const apiRef = useMuiDataGridApiRef();
    const {data, apiRef: formApiRef} = useFormContext()

    const filterResponsables = builder.neq('nif', null)

    const filter = useMemo(() => {
        return builder.inside("id",
            data?.fluxosFirma?.length > 0
                ? data?.fluxosFirma?.map((flux:any) => (flux?.id))
                : [0])
    }, [data?.fluxosFirma?.length])

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {handleOpen, handleClose, dialog} = useFluxActions();
    const actions = [
        {
            label: t('common.update'),
            icon: "edit",
            showInMenu: false,
            onClick: handleOpen,
        },
    ]

    const { onChange, remove } = useFluxFinalitzatSession();
    onChange((flux) => {
        if(!flux?.error) {
            const fluxos: any[] = data?.fluxosFirma ?? [];
            fluxos.push({ id: flux?.fluxCreat?.id, description: flux?.fluxCreat?.nom })
            formApiRef?.current?.setFieldValue("fluxosFirma", fluxos);
            remove()
            handleClose()
            refresh?.()
        }
    })

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="firmaPortafirmesActiva"/>
        <GridFormField name="portafirmesFluxTipus" required/>
        {data?.portafirmesFluxTipus == "SIMPLE" && <>
            <GridFormField name="portafirmesResponsables" multiple autocomplete
                           filter={filterResponsables} namedQueries={[`ADD_PLUGIN_USERS`]}/>
            <GridFormField name="portafirmesSequenciaTipus" required/>
        </>}
        {data?.portafirmesFluxTipus == "PORTAFIB" && <>
            <GridFormField size={11} name="fluxosFirma" multiple required/>
            <GridButton size={1}
                        icon={'add'}
                        onClick={() => handleOpen(undefined, { metaDocument: { id: data?.id } })}
            />
            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={'metaDocumentFluxPortafibResource'}
                columns={portafibColumns}
                filter={filter}
                rowAdditionalActions={actions}
                toolbarHide
                autoHeight
                paginationModel={{page: 0, pageSize: 5}}
                sx={{ ml: 1, mt: 1 }}
            />
        </>}
        {dialog}
    </Grid>
}

export const MetaDocumentForm = () => {
    const {t} = useTranslation();
    const {fieldErrors} = useFormContext()

    const tabs = [
        {
            value: "dades",
            label: t('page.metaDocument.tabs.dades'),
            content: <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
                <GridFormField name="codi"/>
                <GridFormField name="nom"/>
                <GridFormField name="descripcio" type={"textarea"}/>
                <GridFormField name="multiplicitat" required/>
                <FileFormField name="plantilla"/>
            </Grid>,
            error: ["codi", "nom"].some(field =>
                fieldErrors?.some?.(error => error.field === field)
            ),
        },
        {
            value: "nti",
            label: t('page.metaDocument.tabs.nti'),
            content: <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
                <GridFormField name="ntiOrigen"/>
                <GridFormField name="ntiTipoDocumental" required/>
                <GridFormField name="ntiEstadoElaboracion"/>
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
                <GridFormField name="firmaPassarelaActiva"/>
            </Grid>,
        },
        {
            value: "viaFirma",
            label: t('page.metaDocument.tabs.viaFirma'),
            content: <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
                <GridFormField name="firmaBiometricaActiva"/>
                <GridFormField name="biometricaLectura"/>
            </Grid>,
        },
        {
            value: "pinbal",
            label: t('page.metaDocument.tabs.pinbal'),
            content: <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
                <Alert severity={"warning"}>Recordau que per poder realizar consultes a un servei concret de PINBAL en producció, s'ha de demanar permis a l'usuari d'integració de RIPEA per aquest procediment</Alert>
                <GridFormField name="pinbalActiu"/>
                <GridFormField name="pinbalServei" required/>
                <GridFormField name="pinbalFinalitat" type={"textarea"} required/>
                <GridFormField name="pinbalUtilitzarCifOrgan"/>
            </Grid>,
        },
    ]

    return <TabComponent tabs={tabs} scrollButtons/>
}

// Grid
const sortModel: any = [{field: 'nom', sort: 'asc'}]
const perspectives = ["COUNT_METADADES"];
const editPerspectives = ["PORTAFIRMES_RESPONSABLES"];

const MetaDocumentGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    
    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {actions} = useMetaDocumentActions(refresh);
    const columns = useMemo(() => [
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
    ], [t]);

    return <GridPage autoHeight>
        <CardPage title={t('page.user.menu.documents')}>
            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={"metaDocumentResource"}
				persistentStateKey={"metaDocumentResource_generics"}
                popupEditUpdateActive
                popupEditFormDialogResourceTitle={t('page.metaDocument.title')}
                popupEditFormContent={<MetaDocumentForm/>}
                columns={columns}
                toolbarShowQuickFilter
                filter={builder.eq("metaExpedient", null)}
                sortModel={sortModel}
                perspectives={perspectives}
                rowAdditionalActions={actions}
                popupEditFormComponentProps={{ perspectives: editPerspectives }}
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