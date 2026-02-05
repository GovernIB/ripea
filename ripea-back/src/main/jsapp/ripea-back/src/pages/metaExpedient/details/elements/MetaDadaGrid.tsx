import {useTranslation} from "react-i18next";
import {GridPage, useBaseAppContext, useFormContext, useMuiDataGridApiRef, useResourceApiService} from "reactlib";
import {CardPage} from "../../../../components/CardData.tsx";
import {DndMuiGrid} from "../../../../components/StyledMuiGrid.tsx";
import {Grid, Icon, Divider} from "@mui/material";
import * as builder from "../../../../util/springFilterUtils.ts";
import {useParams} from "react-router-dom";
import GridFormField from "../../../../components/GridFormField.tsx";
import {useEffect, useMemo, useState} from "react";
import {setTitlePage} from "../../../../TitleHeaderConfigurator.tsx";
import {useUserSession} from "../../../../components/Session.tsx";
import {MultiplicitatStyled} from "../../../contingut/details/MetaExpedient.tsx";
import useMetaDadaDetail from "./details/MetaDadaDetail.tsx";

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
            .then(() => refresh?.())
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {active, desactive, reordering}
}

// Form
const MetaDocumentDadaForm = ({ enviable }:any) => {
    const {data} = useFormContext()
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="codi"/>
        <GridFormField xs={12} name="nom"/>
        <GridFormField xs={12} name="tipus" disabled={!!data?.id} required/>
        <GridFormField xs={12} name="multiplicitat" required/>

        <GridFormField xs={12} name="valorString" hidden={data?.tipus!="TEXT"}/>
        <GridFormField xs={12} name="valorData" type={"date"} hidden={data?.tipus!="DATA"}/>
        <GridFormField xs={12} name="valorImport" decimalScale={2} hidden={data?.tipus!="IMPORT"}/>
        <GridFormField xs={12} name="valorSencer" decimalScale={0} hidden={data?.tipus!="SENCER"}/>
        <GridFormField xs={12} name="valorFlotant" hidden={data?.tipus!="FLOTANT"}/>
        <GridFormField xs={12} name="valorBoolea" hidden={data?.tipus!="BOOLEA"}/>
        <GridFormField xs={12} name="domini" hidden={data?.tipus!="DOMINI"}/>
        <GridFormField xs={12} name="noAplica" hidden={data?.tipus!="DOMINI"}/>

        <GridFormField xs={12} name="descripcio" type={"textarea"}/>

        <GridFormField xs={12} name="enviable" hidden={!enviable}/>
        <GridFormField xs={12} name="metadadaArxiu" hidden={!enviable || !data?.enviable} required/>
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

export const MetDadaGrid = ({ id, enviable = false, readOnly, ...other }: any) => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const {value: user} = useUserSession()

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {active, desactive, reordering} = useActions(refresh)
    const {apiIsReady, handleOpen, dialog} = useMetaDadaDetail()
    const actions = useMemo(() => readOnly ?[
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

    const handleDragEnd = (event: any) => {
        const sourceData = event.active.data.current;
        const targetData = event.over.data.current;
        // console.log('>>> ', sourceData.codi, '(', sourceData.ordre, ') ->', targetData.codi, '(', targetData.ordre, ')')
        if (sourceData.id != targetData.id) {
            reordering(sourceData.id, targetData.ordre)
        }
    }

    return <><DndMuiGrid
        apiRef={apiRef}
        resourceName={"metaDadaResource"}
        popupEditUpdateActive
        popupEditFormDialogResourceTitle={t('page.metaDada.title')}
        popupEditFormContent={<MetaDocumentDadaForm enviable={enviable && user?.sessionScope?.isPropagarMetadades}/>}
        columns={columns}
        toolbarHideQuickFilter={false}
        filter={builder.eq("metaNode.id", id)}
        formAdditionalData={{ metaNode: {id} }}
        staticSortModel={sortModel}
        // perspectives={perspectives}
        rowAdditionalActions={actions}
        {...other}

        onDragEnd={handleDragEnd}

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

const MetaDadaGrid = () => {
    const {t} = useTranslation();
    const { id } = useParams();
    const {rol} = useUserSession();

    const {
        isReady: apiIsReady,
        getOne: appGetOne,
    } = useResourceApiService('metaDocumentResource');
    const [metaDocument, setMetaDocument] = useState<any>();

    useEffect(()=>{
        if (apiIsReady) {
            appGetOne(id).then((app) => setMetaDocument(app))
        }
    },[apiIsReady, id])

    useEffect(() => {
        if (metaDocument) {
            setTitlePage(t('page.user.menu.documentDada', {nom: metaDocument?.nom}))
        }
    }, [metaDocument]);

    const readOnly = useMemo(() => {
        return !(rol.isAdmin || (rol.isOrganAdmin && metaDocument?.metaExpedientRevisioEstat == 'REVISAT') || rol.isDissenyOrgan)
    }, [metaDocument, rol])

    return <GridPage disableMargins>
        <CardPage title={t('page.user.menu.documentDada', {nom: metaDocument?.nom})}>
            <MetDadaGrid id={id} readOnly={readOnly}/>
        </CardPage>
    </GridPage>
}
export default MetaDadaGrid;