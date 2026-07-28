import {useTranslation} from "react-i18next";
import {useMemo, useState} from "react";
import {GridPage, useFormContext, useMuiDataGridApiRef} from "reactlib";
import {CardPage} from "../../components/CardData.tsx";
import StyledMuiGrid, {ToolbarButton} from "../../components/StyledMuiGrid.tsx";
import {Grid, Icon, Badge, IconButton} from "@mui/material";
import GridFormField from "../../components/GridFormField.tsx";
import {OrganGestorFilter} from "./OrganGestorFilter.tsx";
import {useOrganGestorSyncDialog} from "./actions/OrganGestorSync.tsx";
import {useSession} from "../../components/SessionStorageContext.tsx";
import {useOrganGestorPermisDialog} from "../user/configurar/PermisOrganGestorGrid.tsx";

const OrganGestorForm = () => {
    const {data} = useFormContext()
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="codi" disabled readOnly/>
        <GridFormField name="nom" disabled readOnly/>
        <GridFormField name="pare" disabled readOnly/>
        <GridFormField name="cif" disabled readOnly/>
        <GridFormField name="utilitzarCifPinbal"/>
        <GridFormField name="permetreEnviamentPostal"/>
        <GridFormField name="permetreEnviamentPostalDescendents" hidden={!data?.permetreEnviamentPostal}/>
    </Grid>
}

const OrganGestorGrid = () => {
    const {t} = useTranslation();
    const [springFilter, setSpringFilter] = useState<string>();
    const {value: treeViewStored, save: saveTreeView} = useSession('organGestorTreeView');
    const treeView = !!treeViewStored;

    const gridApiRef = useMuiDataGridApiRef();
    const refresh = () => gridApiRef?.current?.refresh?.();

    const {handleOpen, dialog} = useOrganGestorSyncDialog();
    const {handleShow: handleShowPermis, dialog: permisDialog} = useOrganGestorPermisDialog(refresh);
    const actions = [
        {
            label: t('common.update'),
            icon: "edit",
            showInMenu: true,
            clickShowUpdateDialog: true,
        },
    ]

    // Es recalcula en canviar de vista perquè, en tornar a la vista taula, es torni a aplicar
    // l'ordenació per nom (en vista arbre no hi ha columna 'nom' i la graella neteja l'ordenació).
    const sortModel: any = useMemo(() => (!treeView ? [{field: 'nom', sort: 'asc'}] : []), [treeView])
    const perspectives = useMemo(() => treeView?['PATH','COUNT_PERMISOS']:['COUNT_PERMISOS'], [treeView])
    const columns:any[] = useMemo(()=>[
        ...(!treeView ?[
            {
                field: 'codi',
                flex: 0.4,
            },
            {
                field: 'nom',
                flex: 1,
            },
            {
                field: 'pare',
                flex: 1,
            }
        ] :[]),
        {
            field: 'cif',
            flex: 0.4,
        },
        {
            field: 'estat',
            flex: 0.5,
        },
        {
            field: 'permis',
            headerName: '',
            sortable: false,
            flex: 0.2,
            renderCell: (params:any) => <IconButton
                aria-label="key"
                color="inherit"
                title="Permisos"
                onClick={(e) => {
                    e.stopPropagation();
                    handleShowPermis(params?.row?.id);
                }}
            >
                <Badge badgeContent={params?.row?.numPermisos} color="primary" showZero>
                    <Icon>key</Icon>
                </Badge>
            </IconButton>
        }
    ], [treeView])

    return (
        <GridPage autoHeight>
            <CardPage title={t('page.user.menu.organs')}>
                <OrganGestorFilter onSpringFilterChange={setSpringFilter} />

                <StyledMuiGrid
                    apiRef={gridApiRef}
                    resourceName={'organGestorResource'}
                    persistentStateKey={treeView ? 'organGestorResourceArbre' : 'organGestorResource'}
                    popupEditUpdateActive
                    popupEditFormDialogResourceTitle={t('page.organGestor.title')}
                    popupEditFormContent={<OrganGestorForm />}
                    columns={columns}
                    filter={springFilter}
                    toolbarShowFilterCount
                    perspectives={perspectives}
                    sortModel={sortModel}
                    rowAdditionalActions={actions}
                    toolbarElementsWithPositions={[
                        {
                            position: 3,
                            element: (
                                <ToolbarButton onClick={handleOpen} icon={'cached'} color={'primary'}>
                                    {t('page.organGestor.action.actualitzar.label')}
                                </ToolbarButton>
                            ),
                        },
                        {
                            position: 3,
                            element: (
                                <ToolbarButton
                                    icon={'visibility'}
                                    variant={treeView ? 'contained' : 'outlined'}
                                    onClick={() => saveTreeView(!treeView)}
                                    color={'primary'}
                                >
                                    {t('page.organGestor.action.vista')}
                                </ToolbarButton>
                            ),
                        },
                    ]}
                    paginationActive={!treeView}
                    // autoHeight={treeView}
                    treeData={treeView}
                    groupingColDef={{
                        headerName: t('page.contingut.grid.nom'),
                        flex: 1,
                        valueFormatter: (_value: any, row: any) => row?.codi + ' - ' + row?.nom,
                    }}
                    rowsTransformer={(rows: any) => {
                        if (!rows) return [];
                        const additionalRows: any[] = rows;
                        if (treeView) {
                            for (const row of rows) {
                                if (row?.path != null) {
                                    for (const r of row.path) {
                                        if (
                                            !additionalRows.map((b: any) => b.id).includes(r?.id) &&
                                            !rows.map((b: any) => b.id).includes(r?.id)
                                        ) {
                                            additionalRows.push(r);
                                        }
                                    }
                                }
                            }
                        }
                        return additionalRows;
                    }}
                    getTreeDataPath={(row: any): string[] => {
                        return !!row?.pathName ? row?.pathName : [`${row.id}`];
                    }}
                    toolbarHideCreate
                    toolbarHideRefresh
                    popupEditFormI18nKeys={{
                        updateSuccess: 'page.organGestor.action.update.ok',
                    }}
                />
            </CardPage>
            {dialog}
            {permisDialog}
        </GridPage>
    );
}
export default OrganGestorGrid;