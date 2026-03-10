import {useTranslation} from "react-i18next";
import {useMemo, useState} from "react";
import {GridPage, useFormContext} from "reactlib";
import {CardPage} from "../../components/CardData.tsx";
import StyledMuiGrid, {ToolbarButton} from "../../components/StyledMuiGrid.tsx";
import {Grid, Icon, Badge} from "@mui/material";
import GridFormField from "../../components/GridFormField.tsx";
import {OrganGestorFilter} from "./OrganGestorFilter.tsx";
import {useOrganGestorSyncDialog} from "./actions/OrganGestorSync.tsx";
import LinkIcon from "../../components/LinkIcon.tsx";

const OrganGestorForm = () => {
    const {data} = useFormContext()
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="codi" disabled readOnly/>
        <GridFormField xs={12} name="nom" disabled readOnly/>
        <GridFormField xs={12} name="pare" disabled readOnly/>
        <GridFormField xs={12} name="cif" disabled readOnly/>
        <GridFormField xs={12} name="utilitzarCifPinbal"/>
        <GridFormField xs={12} name="permetreEnviamentPostal"/>
        <GridFormField xs={12} name="permetreEnviamentPostalDescendents" hidden={!data?.permetreEnviamentPostal}/>
    </Grid>
}

const sortModel: any = [{field: 'nom', sort: 'asc'}]

const OrganGestorGrid = () => {
    const {t} = useTranslation();
    const [springFilter, setSpringFilter] = useState<string>();
    const [treeView, setTreeView] = useState<boolean>(false);

    const {handleOpen, dialog} = useOrganGestorSyncDialog();
    const actions = [
        {
            label: t('common.update'),
            icon: "edit",
            showInMenu: true,
            clickShowUpdateDialog: true,
        },
    ]

    const perspectives = useMemo(() => treeView?['PATH','COUNT_PERMISOS']:['COUNT_PERMISOS'], [treeView])
    const columns:any[] = useMemo(()=>[
        ...(!treeView ?[
            {
                field: 'codi',
                flex: 0.5,
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
            flex: 0.5,
        },
        {
            field: 'estat',
            flex: 0.5,
        },
        {
            filed: 'permis',
            headerName: '',
            sortable: false,
            flex: 0.25,
            renderCell: (params:any) => <LinkIcon
                aria-label="key"
                color="inherit"
                title="Permisos"
                to={`/organgestor/${params?.row?.id}/permis`}
            >
                <Badge badgeContent={params?.row?.numPermisos} color="primary" showZero>
                    <Icon>key</Icon>
                </Badge>
            </LinkIcon>
        }
    ], [treeView])

    return <GridPage disableMargins>
        <CardPage title={t('page.user.menu.organs')}>
            <OrganGestorFilter onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                resourceName={"organGestorResource"}
                popupEditUpdateActive
                popupEditFormDialogResourceTitle={t('page.organGestor.title')}
                popupEditFormContent={<OrganGestorForm/>}
                columns={columns}
                filter={springFilter}
                perspectives={perspectives}
                sortModel={sortModel}
                rowAdditionalActions={actions}
                toolbarElementsWithPositions={[
                    {
                        position: 3,
                        element: <ToolbarButton
                            onClick={handleOpen}
                            icon={'cached'}
                            color={'primary'}>{t('page.organGestor.action.actualitzar.label')}</ToolbarButton>,
                    },
                    {
                        position: 3,
                        element: <ToolbarButton
                            icon={'visibility'}
                            variant={treeView ?"contained" :"outlined"}
                            onClick={()=>setTreeView(prev=>!prev)}
                            color={'primary'}>{t('page.organGestor.action.vista')}</ToolbarButton>,
                    },
                ]}

                paginationActive={!treeView}
                // autoHeight={treeView}
                treeData={treeView}
                groupingColDef={{
                    headerName: t('page.contingut.grid.nom'),
                    flex: 1,
                    valueFormatter: (_value: any, row: any) => row?.codi +" - "+ row?.nom,
                }}
                treeDataAdditionalRows={(rows: any) => {
                    const additionalRows: any[] = [];
                        if (rows!=null && treeView){
                            for (const row of rows) {
                                for (const r of row?.path) {
                                    if (!additionalRows.map((b:any) => b.id).includes(r?.id)
                                        && !rows.map((b:any) => b.id).includes(r?.id))
                                    {
                                        additionalRows.push(r)
                                    }
                                }
                            }
                        }
                    return additionalRows;
                }}
                getTreeDataPath={(row: any): string[] => {
                        return !!row?.pathName ?row?.pathName :[`${row.id}`];
                }}

                toolbarHideCreate
                toolbarHideRefresh
                popupEditFormI18nKeys={{
                    updateSuccess: 'page.organGestor.action.update.ok',
                }}
            />
        </CardPage>
        {dialog}
    </GridPage>
}
export default OrganGestorGrid;