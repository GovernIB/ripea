import {GridPage, useBaseAppContext, useFormContext, useMuiDataGridApiRef, useResourceApiService} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";
import {useTranslation} from "react-i18next";
import {useParams} from "react-router-dom";
import {useEffect, useMemo, useState} from "react";
import Load from "../../../components/Load.tsx";
import TabComponent from "../../../components/TabComponent.tsx";
import StyledMuiGrid, {ToolbarButton} from "../../../components/StyledMuiGrid.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import {MetaDocumentForm} from "../../metaDocument/MetaDocumentGrid.tsx";
import {Badge, Grid, Icon, Typography} from "@mui/material";
import LinkButton from "../../../components/LinkButton.tsx";
import {useMetaDocumentActions} from "../../metaDocument/details/MetaDocumentActions.tsx";
import {MetDadaGrid} from "../../metaDocument/details/MetaDadaGrid.tsx";
import {StyledBadge} from "../../../components/StyledBadge.tsx";
import GridFormField from "../../../components/GridFormField.tsx";
import {StyledPrioritat} from "../../expedient/ExpedientGrid.tsx";

// MetaDocument
const metaDocumentSortModel: any = [{field: 'ordre', sort: 'asc'}]
const metaDocumentPerspectives = ["COUNT_METADADES"];
const metaDocumentColumns: any[] = [
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
const MetaDocumentTab = ({ entity, onRowCountChange } :any) => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const additionalColumns = useMemo(() => [
        ...metaDocumentColumns,
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
        staticSortModel={metaDocumentSortModel}
        perspectives={metaDocumentPerspectives}
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

// ExpedientEstat
const ExpedientEstatForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="codi"/>
        <GridFormField xs={12} name="nom"/>
        <GridFormField xs={12} name="color" type={'color'}/>
        <GridFormField xs={12} name="inicial"/>
        <GridFormField xs={12} name="responsable"/>
    </Grid>
}

const expedientEstatSortModel: any = [{field: 'ordre', sort: 'asc'}]
const expedientEstatPerspectives: string[] = [];
const expedientEstatColumns = [
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
const ExpedientEstatTab = ({ entity, onRowCountChange } :any) => {
    const {t} = useTranslation()

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

    return <StyledMuiGrid
        resourceName={'metaExpedientEstatResource'}
        popupEditUpdateActive
        popupEditFormDialogResourceTitle={t('page.expedientEstat.title')}
        popupEditFormContent={<ExpedientEstatForm/>}
        columns={expedientEstatColumns}
        filter={builder.eq("metaExpedient.id", entity?.id)}
        formAdditionalData={{ metaExpedient: {id: entity?.id} }}
        staticSortModel={expedientEstatSortModel}
        perspectives={expedientEstatPerspectives}
        rowAdditionalActions={actions}
        onRowCountChange={onRowCountChange}

        popupEditFormDialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
        toolbarCreateTitle={t('page.expedientEstat.action.new.label')}
        popupEditFormI18nKeys={{
            createSuccess: 'page.expedientEstat.action.new.ok',
            updateSuccess: 'page.expedientEstat.action.update.ok',
            deleteSuccess: 'page.expedientEstat.action.delete.ok',
        }}
    />
}

// MetaExpedientTasca
const useMetaExpedientTascaActions = (refresh?: () => void) => {
    const {t} = useTranslation();
    const {
        patch: apiPatch,
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

    return {active, desactive}
}
const MetaExpedientTascaForm = () => {
    const {t} = useTranslation()
    const {data} = useFormContext()

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="codi"/>
        <GridFormField xs={12} name="nom"/>
        <GridFormField xs={12} name="responsable"/>
        <GridFormField xs={12} name="duracio" decimalScale={0}
                       componentProps={{helperText: t('page.metaExpedientTasca.detall.duracio')}}/>
        <GridFormField xs={12} name="descripcio" type={"textarea"}/>
        <GridFormField xs={12} name="prioritat" required/>
        <GridFormField xs={12} name="estatCrearTasca" filter={builder.eq("metaExpedient.id", data?.metaExpedient?.id)}/>
        <GridFormField xs={12} name="estatFinalitzarTasca" filter={builder.eq("metaExpedient.id", data?.metaExpedient?.id)}/>
    </Grid>
}

const metaExpedientTascaSortModel: any = [{field: 'codi', sort: 'asc'}]
const metaExpedientTascaPerspectives: string[] = [];
const metaExpedientTascaColumns = [
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
    },
    {
        field: 'estatFinalitzarTasca',
        flex: 1,
    },
    {
        field: 'activa',
        flex: 0.5,
        renderCell: (params:any) => (params?.row?.activa && <Icon>check</Icon>),
    },
]
const MetaExpedientTascaTab = ({ entity, onRowCountChange } :any) => {
    const {t} = useTranslation()
    const apiRef = useMuiDataGridApiRef();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {active, desactive} = useMetaExpedientTascaActions(refresh)
    const actions = [
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
            label: t('common.delete'),
            icon: "delete",
            showInMenu: true,
            clickTriggerDelete: true,
        },
    ]

    return <StyledMuiGrid
        apiRef={apiRef}
        resourceName={'metaExpedientTascaResource'}
        popupEditUpdateActive
        popupEditFormDialogResourceTitle={t('page.metaExpedientTasca.title')}
        popupEditFormContent={<MetaExpedientTascaForm/>}
        columns={metaExpedientTascaColumns}
        toolbarHideQuickFilter={false}
        filter={builder.eq("metaExpedient.id", entity?.id)}
        formAdditionalData={{ metaExpedient: {id: entity?.id} }}
        sortModel={metaExpedientTascaSortModel}
        perspectives={metaExpedientTascaPerspectives}
        rowAdditionalActions={actions}
        onRowCountChange={onRowCountChange}

        popupEditFormDialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
        toolbarCreateTitle={t('page.metaExpedientTasca.action.new.label')}
        popupEditFormI18nKeys={{
            createSuccess: 'page.metaExpedientTasca.action.new.ok',
            updateSuccess: 'page.metaExpedientTasca.action.update.ok',
            deleteSuccess: 'page.metaExpedientTasca.action.delete.ok',
        }}
    />
}

// Grup
const grupSortModel: any = [{field: 'codi', sort: 'asc'}]
const grupPerspectives: string[] = [];
const GrupTab = ({ entity, onRowCountChange } :any) => {
    const {t} = useTranslation()

    const grupColumns = [
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
            headerName: 'Per defecte',
            flex: 0.5,
            renderCell: (params:any) => (params?.id == entity?.grupPerDefecte?.id && <Icon>check</Icon>),
        },
    ]

    const actions = [
        {
            label: t('page.grup.action.unlink.label'),
            icon: "link_off",
            showInMenu: true,
            onClick: () => {},
        },
        {
            label: t('page.grup.action.default.label'),
            icon: "check",
            showInMenu: true,
            onClick: () => {},
        },
    ]

    return <StyledMuiGrid
        resourceName={'grupResource'}
        columns={grupColumns}
        toolbarHideQuickFilter={false}
        filter={builder.exists(builder.eq("metaExpedients.id", entity?.id))}
        sortModel={grupSortModel}
        perspectives={grupPerspectives}
        rowAdditionalActions={actions}
        toolbarHideCreate
        onRowCountChange={onRowCountChange}

        toolbarElementsWithPositions={[
            {
                position: 3,
                element: <ToolbarButton icon={'add'} onClick={()=>{}}>
                    {t('page.grup.action.link.label')}
                </ToolbarButton>,
            },
        ]}
    />
}

// Elements
const perspectives :string[] = []
export const MetaExpedientElements = () => {
    const {t} = useTranslation()
    const { id, element } = useParams();

    const {
        isReady: apiIsReady,
        getOne: appGetOne,
    } = useResourceApiService('metaExpedientResource');
    const [metaExpedient, setMetaExpedient] = useState<any>();

    useEffect(()=>{
        if (apiIsReady) {
            appGetOne(id, {perspectives})
                .then((app) => setMetaExpedient(app))
        }
    },[apiIsReady, id])

    const [numMetaDocument, setNumMetaDocument] = useState<number>(metaExpedient?.numMetaDocument);
    const [numMetaDada, setNumMetaDada] = useState<number>(metaExpedient?.numMetaDada);
    const [numEstat, setNumEstat] = useState<number>(metaExpedient?.numEstat);
    const [numTasca, setNumTasca] = useState<number>(metaExpedient?.numTasca);
    const [numGrup, setNumGrup] = useState<number>(metaExpedient?.numGrup);

    const tabs :any[] = [
        {
            value: "metaDocument",
            label: t('page.metaExpedient.tabs.metaDocument'),
            content: <MetaDocumentTab entity={metaExpedient} onRowCountChange={setNumMetaDocument}/>,
            badge: numMetaDocument,
            showZero: true,
        },
        {
            value: "metaDada",
            label: t('page.metaExpedient.tabs.metaDada'),
            content: <MetDadaGrid id={id} onRowCountChange={setNumMetaDada}/>,
            badge: numMetaDada,
            showZero: true,
        },
        {
            value: "estat",
            label: t('page.metaExpedient.tabs.expedientEstat'),
            content: <ExpedientEstatTab entity={metaExpedient} onRowCountChange={setNumEstat}/>,
            badge: numEstat,
            showZero: true,
        },
        {
            value: "tasca",
            label: t('page.metaExpedient.tabs.tasca'),
            content: <MetaExpedientTascaTab entity={metaExpedient} onRowCountChange={setNumTasca}/>,
            badge: numTasca,
            showZero: true,
        },
        {
            value: "grup",
            label: t('page.metaExpedient.tabs.grup'),
            content: <GrupTab entity={metaExpedient} onRowCountChange={setNumGrup}/>,
            badge: numGrup,
            showZero: true,
            hidden: !metaExpedient?.gestioAmbGrupsActiva,
        },
    ]

    return <GridPage disableMargins>
        <Load value={metaExpedient}>
            <CardPage title={t('page.metaExpedient.detall.elements', {nom: metaExpedient?.nom})}>
                <TabComponent defaultValue={element} tabs={tabs}/>
            </CardPage>
        </Load>
    </GridPage>
}