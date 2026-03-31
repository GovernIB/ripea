import {GridPage, useBaseAppContext, useFormContext, useMuiDataGridApiRef, useResourceApiService} from "reactlib";
import StyledMuiGrid from "../../../../components/StyledMuiGrid.tsx";
import {Grid, Icon, Divider, Button} from "@mui/material";
import {useTranslation} from "react-i18next";
import GridFormField from "../../../../components/GridFormField.tsx";
import * as builder from "../../../../util/springFilterUtils.ts";
import {useNavigate, useParams} from "react-router-dom";
import {CardPage} from "../../../../components/CardData.tsx";
import {useEffect, useMemo, useState} from "react";
import Load from "../../../../components/Load.tsx";
import {useUserSession} from "../../../../components/Session.tsx";
import useMetaExpTasaValidacioDetail from "./details/MetaExpTasaValidacioDetail.tsx";
import {ErrorPage} from "../../../../components/ErrorPage.tsx";

const useActions = (refresh?: () => void) => {
    const {t} = useTranslation();
    const {
        patch: apiPatch,
    } = useResourceApiService('metaExpedientTascaValidacioResource');
    const {temporalMessageShow} = useBaseAppContext();

    const active = (id:any) => {
        apiPatch(id, {data: { activa: true }})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.metaExpedientTascaValidacio.action.activar.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const desactive = (id:any) => {
        apiPatch(id, {data: { activa: false }})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.metaExpedientTascaValidacio.action.desactivar.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {active, desactive}
}
const MetaExpedientTascaValidacioForm = () => {
    const {data} = useFormContext()
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="itemValidacio" required/>
        <GridFormField xs={12} name="metaDada" hidden={data?.itemValidacio!="DADA"} filter={builder.eq("metaNode.id", data?.metaExpedient?.id)}/>
        <GridFormField xs={12} name="metaDocument" hidden={data?.itemValidacio!="DOCUMENT"} filter={builder.eq("metaExpedient.id", data?.metaExpedient?.id)}/>
        <GridFormField xs={12} name="tipusValidacio" disabled={data?.itemValidacio=="DADA"} required/>
    </Grid>
}

const sortModel: any = [{field: 'id', sort: 'asc'}]
const perspectives: string[] = [];
const columns = [
    {
        field: 'itemValidacio',
        flex: 1,
    },
    {
        field: 'itemId',
        flex: 1,
        valueGetter: (_id:any, row:any) => row?.itemValidacio == "DOCUMENT" ?row?.metaDocument :row?.metaDada,
        valueFormatter: (value:any) => value?.description,
    },
    {
        field: 'tipusValidacio',
        flex: 1,
    },
    {
        field: 'activa',
        flex: 0.5,
        renderCell: (params:any) => (params?.row?.activa && <Icon>check</Icon>),
        hidden: true,
    },
]
const MetaExpedientTascaValidacioGrid = () => {
    const {t} = useTranslation()
    const { id, tascaId } = useParams();
    const {rol} = useUserSession();
    const apiRef = useMuiDataGridApiRef();
    const navigate = useNavigate();
    const [error, setError] = useState<any>();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {
        isReady: apiIsReady,
        getOne: appGetOne,
    } = useResourceApiService('metaExpedientTascaResource');
    const [metaExpedientTasca, setMetaExpedientTasca] = useState<any>();
    useEffect(()=>{
        if (apiIsReady) {
            appGetOne(tascaId, {perspectives: ['REVISIO_ESTAT']})
                .then((app) => setMetaExpedientTasca(app))
                .catch((error) => setError(error))
        }
    },[apiIsReady, tascaId])

    const readOnly = useMemo(() => {
        return !(rol.isAdmin || (rol.isOrganAdmin && metaExpedientTasca?.metaExpedientRevisioEstat != 'REVISAT') || rol.isDissenyOrgan)
    }, [metaExpedientTasca, rol])

    const {active, desactive} = useActions(refresh)
    const {apiIsReady: apiValidIsReady, handleOpen, dialog} = useMetaExpTasaValidacioDetail()
    const actions:any[] = useMemo(() => readOnly ?[
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
            label: t('page.metaExpedientTascaValidacio.action.activar.label'),
            icon: "check",
            showInMenu: true,
            onClick: active,
            hidden: (row:any) => row?.activa,
        },
        {
            label: t('page.metaExpedientTascaValidacio.action.desactivar.label'),
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
    ], [t, readOnly, apiValidIsReady]);

    if (error)
        return <ErrorPage error={error}/>

    return <GridPage disableMargins>
        <Load value={metaExpedientTasca}>
            <CardPage title={t('page.metaExpedientTasca.detall.validacio', {nom: metaExpedientTasca?.nom})}
                      header={<>
                          <Button
                              variant="outlined"
                              color={"inherit"}
                              sx={{ borderRadius: '4px', padding: '0px 10px', marginLeft: "auto !important" }}
                              onClick={()=>navigate(`/metaExpedient/${id}/tasca`)}
                          >
                              <Icon>arrow_back</Icon>
                              {t('common.back')}
                          </Button>
                      </>}>
                <StyledMuiGrid
                    apiRef={apiRef}
                    resourceName={'metaExpedientTascaValidacioResource'}
                    popupEditUpdateActive
                    popupEditFormDialogResourceTitle={t('page.metaExpedientTascaValidacio.title')}
                    popupEditFormContent={<MetaExpedientTascaValidacioForm/>}
                    columns={columns}
                    toolbarHideQuickFilter={false}
                    filter={builder.eq("metaExpedientTasca.id", tascaId)}
                    formAdditionalData={{ metaExpedient: {id}, metaExpedientTasca: {id: tascaId} }}
                    staticSortModel={sortModel}
                    perspectives={perspectives}
                    rowAdditionalActions={actions}

                    // popupEditFormDialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
                    toolbarCreateTitle={t('page.metaExpedientTascaValidacio.action.new.label')}
                    popupEditFormI18nKeys={{
                        createSuccess: 'page.metaExpedientTascaValidacio.action.new.ok',
                        updateSuccess: 'page.metaExpedientTascaValidacio.action.update.ok',
                        deleteSuccess: 'page.metaExpedientTascaValidacio.action.delete.ok',
                    }}
                    readOnly={readOnly}
                />
            </CardPage>
            {dialog}
        </Load>
    </GridPage>
}
export default MetaExpedientTascaValidacioGrid;