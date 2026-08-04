import {useTranslation} from "react-i18next";
import {GridPage, useBaseAppContext, useFormContext, useMuiDataGridApiRef, useResourceApiService} from "reactlib";
import {CardPage} from "@src/components/CardData.tsx";
import StyledMuiGrid from "@src/components/StyledMuiGrid.tsx";
import {Alert, Grid, Icon, Divider, Button} from "@mui/material";
import * as builder from "@src/util/springFilterUtils.ts";
import {useNavigate, useParams} from "react-router-dom";
import GridFormField from "@src/components/GridFormField.tsx";
import {useEffect, useMemo, useState} from "react";
import {setTitlePage} from "@src/TitleHeaderConfigurator.tsx";
import {useUserSession} from "@src/components/Session.tsx";
import {MultiplicitatStyled} from "@src/pages/contingut/details/MetaExpedient.tsx";
import useMetaDadaDetail from "./details/MetaDadaDetail.tsx";
import {ErrorPage} from "@src/components/ErrorPage.tsx";
import {icons} from "@src/util/icons.ts";
import {esMetaDocumentPerDefecte} from "@src/util/metaDocumentUtils.ts";
import Load from "@src/components/Load.tsx";

const useActions = (refresh?: () => void) => {
    const {t} = useTranslation();
    const {
        patch: apiPatch,
        artifactAction: apiAction,
    } = useResourceApiService('metaDadaResource');
    const {temporalMessageShow} = useBaseAppContext();

    const active = (id:any) => {
        apiPatch(id, {data: { activa: true }})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.metaDada.action.activar.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const desactive = (id:any) => {
        apiPatch(id, {data: { activa: false }})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.metaDada.action.desactivar.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const reordering = (id:any, ordre:number) => {
        apiAction(id, { code: 'REORDENAR', data: ordre })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {active, desactive, reordering}
}

// Form
export const MetaDocumentDadaForm = ({ enviable }:any) => {
    const {data} = useFormContext()
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="codi"/>
        <GridFormField name="nom"/>
        <GridFormField name="tipus" disabled={!!data?.id} required/>
        <GridFormField name="multiplicitat" required/>

        <GridFormField name="valorString" hidden={data?.tipus!="TEXT"}/>
        <GridFormField name="valorData" type={"date"} hidden={data?.tipus!="DATA"}/>
        <GridFormField name="valorImport" decimalScale={2} hidden={data?.tipus!="IMPORT"}/>
        <GridFormField name="valorSencer" decimalScale={0} hidden={data?.tipus!="SENCER"}/>
        <GridFormField name="valorFlotant" hidden={data?.tipus!="FLOTANT"}/>
        <GridFormField name="valorBoolea" hidden={data?.tipus!="BOOLEA"}/>
        <GridFormField name="domini" hidden={data?.tipus!="DOMINI"}/>
        <GridFormField name="noAplica" hidden={data?.tipus!="DOMINI"}/>

        <GridFormField name="descripcio" type={"textarea"}/>

        <GridFormField name="enviable" hidden={!enviable}/>
        <GridFormField name="metadadaArxiu" hidden={!enviable || !data?.enviable} required/>
    </Grid>
}

// Grid
const sortModel: any = [{field: 'ordre', sort: 'asc'}]
// const perspectives = [];
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
        field: 'multiplicitat',
        flex: 0.5,
        renderCell: (params:any) => <MultiplicitatStyled multiplicitat={params?.formattedValue}/>
    },
    {
        field: 'tipus',
        flex: 1,
    },
    {
        field: 'activa',
        flex: 0.5,
        renderCell: (params:any) => (params?.row?.activa && <Icon>check</Icon>),
    },
]

export const MetDadaGrid = ({ id, enviable = false, readOnly, persistentStateKey = "metaDadaResource_procedimentTab", ...other }: any) => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const {value: user} = useUserSession()

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {active, desactive, reordering} = useActions(refresh)
    const {apiIsReady, handleOpen, dialog} = useMetaDadaDetail()
    const actions:any[] = useMemo(() => readOnly ?[
        {
            label: t('page.metaExpedient.action.consultar.label'),
            icon: icons.detall,
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
            label: t('page.metaDada.action.activar.label'),
            icon: "check",
            showInMenu: true,
            onClick: active,
            hidden: (row:any) => row?.activa,
        },
        {
            label: t('page.metaDada.action.desactivar.label'),
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
    ], [t, readOnly, apiIsReady])

    const handleDragEnd = (params: any) => {
        if (params.targetIndex != params.oldIndex) {
            reordering(params.row.id, params.targetIndex)
        }
    }

    return <><StyledMuiGrid
        apiRef={apiRef}
        resourceName={"metaDadaResource"}
		persistentStateKey={persistentStateKey}
        popupEditUpdateActive
        popupEditFormDialogResourceTitle={t('page.metaDada.title')}
        popupEditFormContent={<MetaDocumentDadaForm enviable={enviable && user?.sessionScope?.isPropagarMetadades}/>}
        columns={columns}
        toolbarShowQuickFilter
        filter={builder.eq("metaNode.id", id)}
        formAdditionalData={{ metaNode: {id} }}
        staticSortModel={sortModel}
        // perspectives={perspectives}
        rowAdditionalActions={actions}
        {...other}

        rowReordering={!readOnly}
        onRowOrderChange={handleDragEnd}

        toolbarCreateTitle={t('page.metaDada.action.new.label')}
        popupEditFormI18nKeys={{
            createSuccess: 'page.metaDada.action.new.ok',
            updateSuccess: 'page.metaDada.action.update.ok',
            deleteSuccess: 'page.metaDada.action.delete.ok',
        }}
        readOnly={readOnly}
    />
        {dialog}
    </>
}

// metaExpedientRevisioEstat és un camp @Transient que només omple la perspectiva REVISIO_ESTAT.
// Sense demanar-la arriba buit i la comprovació de "procediment revisat" no s'aplicaria mai, de
// manera que un administrador d'òrgan podria editar les metadades d'un procediment ja revisat.
const metaDocumentPerspectives = ["REVISIO_ESTAT"];

const MetaDadaGrid = () => {
    const {t} = useTranslation();
    const { id } = useParams();
    const {rol} = useUserSession();
    const navigate = useNavigate();
    const [error, setError] = useState<any>();

    const {
        isReady: apiIsReady,
        getOne: appGetOne,
    } = useResourceApiService('metaDocumentResource');
    const [metaDocument, setMetaDocument] = useState<any>();

    useEffect(()=>{
        if (apiIsReady) {
            appGetOne(id, {perspectives: metaDocumentPerspectives})
                .then((app) => setMetaDocument(app))
                .catch((error) => setError(error))
        }
    },[apiIsReady, id])

    useEffect(() => {
        if (metaDocument) {
            setTitlePage(t('page.user.menu.documentDada', {nom: metaDocument?.nom}))
        }
    }, [metaDocument]);

    // Les metadades dels tipus de document creats per defecte a l'alta del procediment
    // formen part de la seva definició: només les pot mantenir un administrador d'entitat,
    // per a la resta de rols són de només consulta i se n'informa el motiu.
    const reservat = useMemo(
        () => esMetaDocumentPerDefecte(metaDocument?.codi) && !rol.isAdmin,
        [metaDocument, rol])

    const readOnly = useMemo(() => {
        if (reservat) {
            return true
        }
        return !(rol.isAdmin || (rol.isOrganAdmin && metaDocument?.metaExpedientRevisioEstat != 'REVISAT') || rol.isDissenyOrgan)
    }, [metaDocument, rol, reservat])

    if (error)
        return <ErrorPage error={error}/>

    // El grid s'ha de muntar amb el readOnly ja definitiu: les accions de fila es
    // construeixen un únic cop i no es refan si readOnly canvia després (les dependències
    // del useMemo de les columnes a la llibreria no inclouen les accions). Fins que no
    // arriba el tipus de document no se sap si el procediment està revisat, així que
    // s'espera amb Load, igual que fa la pantalla del procediment.
    return <GridPage autoHeight>
        <Load value={metaDocument}>
            <CardPage title={t('page.user.menu.documentDada', {nom: metaDocument?.nom})}
                      header={<>
                          <Button
                              variant="outlined"
                              color={"inherit"}
                              sx={{ borderRadius: '4px', padding: '0px 10px', marginLeft: "auto !important" }}
                              onClick={()=>navigate(`/metaExpedient/${metaDocument?.metaExpedient?.id}/metaDocument`)}
                          >
                              <Icon>arrow_back</Icon>
                              {t('common.back')}
                          </Button>
                      </>}>
                {metaDocument?.metaExpedientRevisioEstat === 'REVISAT' && rol?.isOrganAdmin &&
                    <Alert severity={'info'} sx={{mb: 1}}>
                        {t('page.metaExpedient.action.consultar.revisat')}
                    </Alert>
                }
                {reservat && <Alert severity={'info'} sx={{mb: 2}}>{t('page.metaDocument.reservat')}</Alert>}
                <MetDadaGrid id={id} readOnly={readOnly} persistentStateKey={"metaDadaResource_metaDocumentTab"}/>
            </CardPage>
        </Load>
    </GridPage>
}
export default MetaDadaGrid;