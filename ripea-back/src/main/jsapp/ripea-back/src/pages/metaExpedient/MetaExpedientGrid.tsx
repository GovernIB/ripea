import {useTranslation} from "react-i18next";
import {useMemo, useState} from "react";
import {GridPage, useFormContext, useMuiDataGridApiRef} from "reactlib";
import {CardPage} from "../../components/CardData.tsx";
import StyledMuiGrid, {ToolbarButton} from "../../components/StyledMuiGrid.tsx";
import {Alert, Badge, Grid, Icon} from "@mui/material";
import GridFormField from "../../components/GridFormField.tsx";
import {MetaExpedientComment} from "../CommentDialog.tsx";
import LinkButton from "../../components/LinkButton.tsx";
import {formatDate} from "../../util/dateUtils.ts";
import {useMetaExpedientActions} from "./details/MetaExpedientActions.tsx";
import {MetaExpedientFilter} from "./MetaExpedientFilter.tsx";
import {useUserSession} from "../../components/Session.tsx";
import {StyledEstat} from "../user/consultes/RevisioMetaExpedientGrid.tsx";

// Form
export const MetaExpedientForm = () => {
    const {t} = useTranslation();
    const {data} = useFormContext()

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="codi"/>
        <GridFormField xs={2} name="tipusClassificacio" required/>
        <GridFormField xs={10} name="classificacio" debounce disabled={data?.tipusClassificacio == 'ID'}/>
        <Grid item xs={12} hidden={data?.msgSiaRolsac == null}>
            <Alert severity={'warning'} sx={{ mt: 0.5 }}>{data.msgSiaRolsac}</Alert>
        </Grid>
        <GridFormField xs={12} name="nom"/>
        <GridFormField xs={12} name="descripcio"/>
        <GridFormField xs={12} name="serieDocumental"/>
        <GridFormField xs={4} name="procedimentComu"/>
        <GridFormField xs={8} name="organGestor" required hidden={data?.procedimentComu}/>
        <GridFormField xs={12} name="expressioNumero"/>

        <GridFormField xs={6} name="permetMetadocsGenerals"/>
        <GridFormField xs={6} name="gestioAmbGrupsActiva"/>
        <GridFormField xs={6} name="interessatObligatori"/>
        <GridFormField xs={6} name="permisDirecte"/>

        {data?.id &&
            <Grid xs={12} sx={{ pl: '8px', pt: '8px' }}>
                <Alert severity={'info'}>
                    {t('common.auditoria.create', {createdDate: formatDate(data.createdDate), createdBy: data.createdByFullName})}
                    {data.lastModifiedDate != null &&
                        t('common.auditoria.update', {lastModifiedDate: formatDate(data.lastModifiedDate), lastModifiedBy: data.lastModifiedByFullName})}
                </Alert>
            </Grid>
        }
    </Grid>
}

// Grid
const columns = [
    {
        field: 'codi',
        flex: 1,
    },
    {
        field: 'classificacio',
        flex: 1,
    },
    {
        field: 'nom',
        flex: 2,
    },
    {
        field: 'serieDocumental',
        flex: 1,
    },
    {
        field: 'organGestor',
        flex: 2,
    },
    {
        field: 'procedimentComu',
        flex: 0.25,
        renderCell: (params:any) => (params?.row?.procedimentComu && <Icon>check</Icon>),
    },
    {
        field: 'permisDirecte',
        flex: 0.25,
        renderCell: (params:any) => (params?.row?.permisDirecte && <Icon>check</Icon>),
    },
    {
        field: 'gestioAmbGrupsActiva',
        flex: 0.25,
        renderCell: (params:any) => (params?.row?.gestioAmbGrupsActiva && <Icon>check</Icon>),
    },
    {
        field: 'actiu',
        flex: 0.25,
        renderCell: (params:any) => (params?.row?.actiu && <Icon>check</Icon>),
    },
]
const sortModel: any = [{field: 'nom', sort: 'asc'}]
const MetaExpedientGrid = () => {
    const {t} = useTranslation();
    const {value: user} = useUserSession();
    const [springFilter, setSpringFilter] = useState<string>();
    const apiRef = useMuiDataGridApiRef();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const additionalColumns = useMemo(() => [
        ...columns,
        {
            field: 'revisioEstat',
            flex: 0.5,
            renderCell: (params:any) => <StyledEstat entity={params?.row}>{params.formattedValue}</StyledEstat>,
            hidden: user?.sessionScope?.isRevisioActiva
        },
        {
            field: 'numComentaris',
            headerName: '',
            sortable: false,
            flex: 0.25,
            renderCell: (params: any) => <MetaExpedientComment
                entity={params?.row}
                readOnly={params?.row?.usuariActualOnlyObservador}
                onClose={refresh}
            />
        },
        {
            field: 'numPermisos',
            headerName: '',
            sortable: false,
            flex: 0.25,
            renderCell: (params:any) => <LinkButton
                aria-label="key"
                color="inherit"
                title="Permisos"
                to={`/metaExpedient/${params?.row?.id}/permis`}
            >
                <Badge badgeContent={params?.row?.numPermisos} color="primary" showZero>
                    <Icon>key</Icon>
                </Badge>
            </LinkButton>
        }
    ].filter((col:any)=>!col?.hidden), [user?.sessionScope?.isRevisioActiva])

    const {actions, content} = useMetaExpedientActions(refresh);

    return <GridPage disableMargins>
        <CardPage title={t('page.user.menu.procedimentsTitle')}>
            <MetaExpedientFilter onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={"metaExpedientResource"}
                popupEditFormDialogResourceTitle={t('page.metaExpedient.title')}
                popupEditCreateActive
                popupEditFormContent={<MetaExpedientForm/>}
                columns={additionalColumns}
                filter={springFilter}
                sortModel={sortModel}
                rowAdditionalActions={actions}
                toolbarElementsWithPositions={[
                    {
                        position: 2,
                        element: <ToolbarButton
                            title={t('common.import')}
                            icon={'download'}
                            onClick={()=>{}}
                            variant={"contained"}
                            color={'success'}/>,
                    },
                    {
                        position: 2,
                        element: <ToolbarButton
                            title={t('common.actualize')}
                            icon={'cached'}
                            onClick={()=>{}}
                            color={'primary'}/>,
                    },
                ]}

                toolbarCreateTitle={t('page.metaExpedient.action.new.label')}
                popupEditFormI18nKeys={{
                    createSuccess: 'page.metaExpedient.action.new.ok',
                    updateSuccess: 'page.metaExpedient.action.update.ok',
                    deleteSuccess: 'page.metaExpedient.action.delete.ok',
                }}
                toolbarHideRefresh
            />
            {content}
        </CardPage>
    </GridPage>
}
export default MetaExpedientGrid;