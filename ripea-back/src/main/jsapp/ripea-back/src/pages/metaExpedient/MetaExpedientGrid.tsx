import {useTranslation} from "react-i18next";
import {useMemo, useState} from "react";
import {GridPage, useFilterApiRef, useFormContext, useMuiDataGridApiRef} from "reactlib";
import {CardPage} from "../../components/CardData.tsx";
import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";
import {Alert, Badge, Chip, Grid, Icon, IconButton, MenuItem} from "@mui/material";
import GridFormField from "../../components/GridFormField.tsx";
import {MetaExpedientComment} from "../CommentDialog.tsx";
import LinkIcon from "../../components/LinkIcon.tsx";
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
import useActualitzar from "./actions/Actualitzar.tsx";
import {useSessionContext} from "../../components/SessionStorageContext.tsx";
import Load from "../../components/Load.tsx";

// Form
export const MetaExpedientForm = ({ isAdmin }:any) => {
    const {t} = useTranslation();
    const {data} = useFormContext()

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="tipusProcedimentServei" required/>
        <GridFormField name="codi"/>
        <GridFormField size={2} name="tipusClassificacio" required/>
        <GridFormField size={10} name="classificacio" debounce disabled={data?.tipusClassificacio == 'ID'}/>
        <Grid size={12} hidden={data?.msgSiaRolsac == null}>
            <Alert severity={'warning'} sx={{ mt: 0.5 }}>{data.msgSiaRolsac}</Alert>
        </Grid>
        <GridFormField size={4} name="crearReglaDistribucio" disabled={!isAdmin || data?.id}/>
        <GridFormField name="nom"/>
        <GridFormField name="descripcio"/>
        <GridFormField name="serieDocumental"/>
        <GridFormField size={4} name="procedimentComu"/>
        <GridFormField size={8} name="organGestor" required hidden={data?.procedimentComu}/>
        <GridFormField name="expressioNumero"
                       componentProps={{ helperText: t('page.metaExpedient.detall.expressioNumero') }}/>

        <GridFormField size={6} name="permetMetadocsGenerals"/>
        <GridFormField size={6} name="gestioAmbGrupsActiva"/>
        <GridFormField size={6} name="interessatObligatori"/>
        <GridFormField size={6} name="permisDirecte" disabled={!isAdmin}
                       componentProps={{ helperText: t('page.metaExpedient.detall.permisDirecte') }}/>

        {data?.id &&
            <Grid size={12} sx={{ pl: '8px', pt: '8px' }}>
                <Alert severity={'info'}>
                    {t('common.auditoria.create', {createdDate: formatDate(data.createdDate), createdBy: data.createdByFullName})}
                    &nbsp;
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
        renderCell: (params:any) => {
            const isProcediment = params?.row?.tipusProcedimentServei === 'PROCEDIMENT'
            return <>
                {params?.row?.tipusProcedimentServei &&
                    <Chip label={isProcediment ?'P' :'S'}
                          color={isProcediment ?"primary" :"success"}
                          size={"small"}
                          sx={{mr: 1}}/>}
                {params?.formattedValue}
            </>
        }
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
]

const sortModel: any = [{field: 'nom', sort: 'asc'}]
const perspectives = ["PERMISOS", "ELEMENTS_COUNT"];

const MetaExpedientGrid = () => {

    const {t} = useTranslation();
    const navigate = useNavigate();
    const {value: user, rol} = useUserSession();
    const [springFilter, setSpringFilter] = useState<string>();
    const apiRef = useMuiDataGridApiRef();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const readOnly = useMemo(() => {
        return rol.isRevisor || rol.isAdminLectura
    }, [rol])

    const additionalColumns = useMemo(() => [
        ...columns,
        {
            field: 'procedimentComu',
            headerName: t('page.metaExpedient.columnes.comu'),
            flex: 0.25,
            minWidth: 90,
            renderCell: (params:any) => (params?.row?.procedimentComu && <Icon>check</Icon>),
        },
        {
            field: 'permisDirecte',
            headerName: t('page.metaExpedient.columnes.directe'),
            flex: 0.25,
            minWidth: 90,
            renderCell: (params:any) => (params?.row?.permisDirecte && <Icon>check</Icon>),
        },
        {
            field: 'gestioAmbGrupsActiva',
            headerName: t('page.metaExpedient.columnes.grups'),
            flex: 0.25,
            minWidth: 90,
            renderCell: (params:any) => (params?.row?.gestioAmbGrupsActiva && <Icon>check</Icon>),
        },
        {
            field: 'actiu',
            headerName: t('page.metaExpedient.columnes.actiu'),
            flex: 0.25,
            minWidth: 90,
            renderCell: (params:any) => (params?.row?.actiu && <Icon>check</Icon>),
        },
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
            hidden: !(rol?.isAdmin || rol?.isOrganAdmin),
            renderCell: (params:any) => <LinkIcon
                aria-label="key"
                color="inherit"
                title="Permisos"
                to={`/metaExpedient/${params?.row?.id}/permis`}
            >
                <Badge badgeContent={params?.row?.numPermisos} color="primary" showZero>
                    <Icon>key</Icon>
                </Badge>
            </LinkIcon>
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
    ].filter((col:any)=>!col?.hidden), [t, user?.sessionScope?.isRevisioActiva])

    const {handleShow: handleImportRolsac, content: contentImportRolsac} = useImportRolsac(apiRef)
    const {handleShow: handleImportFitxer, content: contentImportFitxer} = useImportFitxer(refresh)
    const {handleShow: handleActualitzar, content: contentActualitzar } = useActualitzar(refresh);
    const {actions, components} = useMetaExpedientActions(refresh);

    const massiveActions :any[] = [
        {
            title: t('page.metaExpedient.action.actualize.label'),
            label: t('common.actualize'),
            icon: "cached",
            showInMenu: false,
            onClick: handleActualitzar,
        },
    ]

    const elementsWithPositions = useMemo(() => [
        {
            position: 2,
            hidden: readOnly,
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
    ],[t])

    const filterRef = useFilterApiRef();
    const [load, setLoad] = useState<boolean>(true);
    const {value: revisioEstatMssg, save: setRevisioEstatMssg} = useSessionContext('revisioEstatMssg')

    return <GridPage autoHeight>
        <CardPage title={ rol?.isRevisor ? t('page.user.menu.revisar') : t('page.user.menu.procedimentsTitle')}>

            { rol?.isAdmin && user?.sessionScope?.numProcsPendentsRevisio > 0 && !revisioEstatMssg &&
                <Alert severity={'info'} sx={{mb:1}}>
                    {t('page.metaExpedient.alert.pendentsRevisio', {num: user?.sessionScope?.numProcsPendentsRevisio})}
                    <IconButton sx={{ml: 1, p: 0}} onClick={() => {
                        filterRef.current?.setFieldValue?.('revisioEstat', 'PENDENT')
                        filterRef.current?.filter()
                        setLoad(true)
                        setRevisioEstatMssg(true)
                    }}>
                        <Icon>open_in_new</Icon>
                    </IconButton>
                </Alert>
            }

            <MetaExpedientFilter apiRef={filterRef} onSpringFilterChange={(filter:string) => {
                setSpringFilter(filter)
                setLoad(false)
            }}/>

            <Load value={!load}>
            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={"metaExpedientResource"}
                popupEditFormDialogResourceTitle={t('page.metaExpedient.title')}
                popupEditCreateActive
                popupEditFormContent={<MetaExpedientForm isAdmin={rol?.isAdmin}/>}
                columns={additionalColumns}
                filter={springFilter}
                perspectives={perspectives}
                sortModel={sortModel}
                rowAdditionalActions={actions}
                toolbarMassiveActions={massiveActions}
                toolbarElementsWithPositions={elementsWithPositions}

                toolbarCreateTitle={t('page.metaExpedient.action.new.label')}
                popupEditFormI18nKeys={{
                    createSuccess: 'page.metaExpedient.action.new.ok',
                    updateSuccess: 'page.metaExpedient.action.update.ok',
                    deleteSuccess: 'page.metaExpedient.action.delete.ok',
                }}
                readOnly={readOnly}
            /></Load>
            {contentImportRolsac}
            {contentImportFitxer}
            {contentActualitzar}
            {components}
        </CardPage>
    </GridPage>
}
export default MetaExpedientGrid;