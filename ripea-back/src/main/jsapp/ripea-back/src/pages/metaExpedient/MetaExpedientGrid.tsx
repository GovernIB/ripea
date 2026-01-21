import {useTranslation} from "react-i18next";
import {useMemo, useState} from "react";
import {GridPage, useFormContext, useMuiDataGridApiRef} from "reactlib";
import {CardPage} from "../../components/CardData.tsx";
import StyledMuiGrid, {ToolbarButton} from "../../components/StyledMuiGrid.tsx";
import {Alert, Badge, Grid, Icon, MenuItem} from "@mui/material";
import GridFormField from "../../components/GridFormField.tsx";
import {MetaExpedientComment} from "../CommentDialog.tsx";
import LinkButton from "../../components/LinkButton.tsx";
import {formatDate} from "../../util/dateUtils.ts";
import {useMetaExpedientActions} from "./details/MetaExpedientActions.tsx";
import {MetaExpedientFilter} from "./MetaExpedientFilter.tsx";
import {useUserSession} from "../../components/Session.tsx";
import {StyledEstat} from "../user/consultes/RevisioMetaExpedientGrid.tsx";
import MenuButton, {MenuActionButton} from "../../components/MenuButton.tsx";
import {useNavigate} from "react-router-dom";
import {StyledBadge} from "../../components/StyledBadge.tsx";
import {useImportRolsac} from "./actions/ImportRolsac.tsx";
import {useImportFitxer} from "./actions/ImportFitxer.tsx";

// Form
export const MetaExpedientForm = ({ isAdmin }:any) => {
    const {t} = useTranslation();
    const {data} = useFormContext()

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="codi"/>
        <GridFormField xs={2} name="tipusClassificacio" required/>
        <GridFormField xs={10} name="classificacio" debounce disabled={data?.tipusClassificacio == 'ID'}/>
        <Grid item xs={12} hidden={data?.msgSiaRolsac == null}>
            <Alert severity={'warning'} sx={{ mt: 0.5 }}>{data.msgSiaRolsac}</Alert>
        </Grid>
        <GridFormField xs={4} name="crearReglaDistribucio" disabled={!isAdmin || data?.id}/>
        <GridFormField xs={12} name="nom"/>
        <GridFormField xs={12} name="descripcio"/>
        <GridFormField xs={12} name="serieDocumental"/>
        <GridFormField xs={4} name="procedimentComu"/>
        <GridFormField xs={8} name="organGestor" required hidden={data?.procedimentComu}/>
        <GridFormField xs={12} name="expressioNumero"
                       componentProps={{ helperText: t('page.metaExpedient.detall.expressioNumero') }}/>

        <GridFormField xs={6} name="permetMetadocsGenerals"/>
        <GridFormField xs={6} name="gestioAmbGrupsActiva"/>
        <GridFormField xs={6} name="interessatObligatori"/>
        <GridFormField xs={6} name="permisDirecte" disabled={!isAdmin}
                       componentProps={{ helperText: t('page.metaExpedient.detall.permisDirecte') }}/>

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
const perspectives = ["PERMISOS", "ELEMENTS_COUNT"];

const MetaExpedientGrid = () => {

    const {t} = useTranslation();
    const navigate = useNavigate();
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
            hidden: !user?.sessionScope?.isRevisioActiva
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
        },
        {
            field: 'id',
            headerName: '',
            sortable: false,
            flex: 0.75,
            renderCell: (params: any) => <MenuButton
                id={`elements-${params?.id}`}
                buttonLabel={"Elements"}
                buttonProps={{color: 'black'}}
            >
                <MenuItem onClick={() => navigate(`/metaExpedient/${params?.id}/metaDocument`)}>
                    <StyledBadge badgeContent={params?.row?.numMetaDocument} showZero sx={{ '& .MuiBadge-badge': {right: -3, top: 10 } }}>
                        {t('page.metaExpedient.tabs.metaDocument')}
                    </StyledBadge>
                </MenuItem>
                <MenuItem onClick={() => navigate(`/metaExpedient/${params?.id}/metaDada`)}>
                    <StyledBadge badgeContent={params?.row?.numMetaDada} showZero sx={{ '& .MuiBadge-badge': {right: -3, top: 10 } }}>
                        {t('page.metaExpedient.tabs.metaDada')}
                    </StyledBadge>
                </MenuItem>
                <MenuItem onClick={() => navigate(`/metaExpedient/${params?.id}/estat`)}>
                    <StyledBadge badgeContent={params?.row?.numEstat} showZero sx={{ '& .MuiBadge-badge': {right: -3, top: 10 } }}>
                        {t('page.metaExpedient.tabs.expedientEstat')}
                    </StyledBadge>
                </MenuItem>
                <MenuItem onClick={() => navigate(`/metaExpedient/${params?.id}/tasca`)}>
                    <StyledBadge badgeContent={params?.row?.numTasca} showZero sx={{ '& .MuiBadge-badge': {right: -3, top: 10 } }}>
                        {t('page.metaExpedient.tabs.tasca')}
                    </StyledBadge>
                </MenuItem>
                {params?.row?.gestioAmbGrupsActiva && <MenuItem onClick={() => navigate(`/metaExpedient/${params?.id}/grup`)}>
                    <StyledBadge badgeContent={params?.row?.numGrup} showZero sx={{ '& .MuiBadge-badge': {right: -3, top: 10 } }}>
                        {t('page.metaExpedient.tabs.grup')}
                    </StyledBadge>
                </MenuItem>}
                {user?.sessionScope?.isCarpetesDefecte && <MenuItem onClick={() => navigate(`/metaExpedient/${params?.id}/carpeta`)}>
                    <StyledBadge badgeContent={params?.row?.numCarpetes} showZero sx={{ '& .MuiBadge-badge': {right: -3, top: 10 } }}>
                        {t('page.metaExpedient.tabs.carpeta')}
                    </StyledBadge>
                </MenuItem>}
            </MenuButton>
        },
    ].filter((col:any)=>!col?.hidden), [user?.sessionScope?.isRevisioActiva])

    const {handleShow: handleImportRolsac, content: contentImportRolsac} = useImportRolsac(apiRef)
    const {handleShow: handleImportFitxer, content: contentImportFitxer} = useImportFitxer(refresh)
    const {actions, components} = useMetaExpedientActions(refresh);

    return <GridPage disableMargins>
        <CardPage title={t('page.user.menu.procedimentsTitle')}>
            <MetaExpedientFilter onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={"metaExpedientResource"}
                popupEditFormDialogResourceTitle={t('page.metaExpedient.title')}
                popupEditCreateActive
                popupEditFormContent={<MetaExpedientForm isAdmin={user?.rolActual === 'IPA_ADMIN'}/>}
                columns={additionalColumns}
                filter={springFilter}
                perspectives={perspectives}
                sortModel={sortModel}
                rowAdditionalActions={actions}
                toolbarElementsWithPositions={[
                    {
                        position: 2,
                        element: <MenuActionButton
                            id={'metaExpedient-import'}
                            buttonLabel={t('common.import')}
                            buttonProps={{
                                // icon: 'download',
                                variant: "contained",
                                color: 'success',
                                size: "small",
                                startIcon: <Icon>download</Icon>,
                                sx: { borderRadius: '4px',  minWidth: '20px', minHeight: '32px' }
                            }}
                            actions={[
                                {
                                    label: t('page.metaExpedient.action.importRolsac.label'),
                                    onClick: () => handleImportRolsac(),
                                },
                                {
                                    label: t('page.metaExpedient.action.importFitxer.label'),
                                    onClick: () => handleImportFitxer(),
                                },
                            ]}
                        />,
                    },
                    {
                        position: 2,
                        element: <ToolbarButton
                            icon={'cached'}
                            onClick={()=>{}}
                            color={'primary'}>{t('common.actualize')}</ToolbarButton>,
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
            {contentImportRolsac}
            {contentImportFitxer}
            {components}
        </CardPage>
    </GridPage>
}
export default MetaExpedientGrid;