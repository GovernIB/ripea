import {useTranslation} from "react-i18next";
import { useNavigate } from "react-router-dom";
import {useState} from "react";
import {GridPage} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";
import StyledMuiGrid, {ToolbarButton} from "../../../components/StyledMuiGrid.tsx";
import {Grid, Icon, Badge, IconButton} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";

const OrganGestorFilterForm = () => {
    return <>
        <GridFormField xs={2} name="codi"/>
        <GridFormField xs={2} name="nom"/>
        <GridFormField xs={3} name="organGestor"/>
        <GridFormField xs={2} name="estat"/>
        <Grid item xs={0.6}/>
    </>
}

const springFilterBuilder = (data:any) => {
    return builder.and(
        builder.like('codi', data?.codi),
        builder.like('nom', data?.nom),
        builder.eq('pare.id', data?.organGestor?.id),
        builder.eq('estat', `'${data?.estat}'`),
    );
}

const OrganGestorFilter = (props: any) => {
    const {onSpringFilterChange} = props;

    return <StyledMuiFilter
        resourceName={"organGestorResource"}
        code={"FILTER"}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
    >
        <OrganGestorFilterForm/>
    </StyledMuiFilter>
}

// Grid
const OrganGestorForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="codi" disabled readOnly/>
        <GridFormField xs={12} name="nom" disabled readOnly/>
        <GridFormField xs={12} name="pare" disabled readOnly/>
        <GridFormField xs={12} name="cif" disabled readOnly/>
        <GridFormField xs={12} name="utilitzarCifPinbal"/>
        <GridFormField xs={12} name="permetreEnviamentPostal"/>
    </Grid>
}

const sortModel: any = [{field: 'nom', sort: 'asc'}]

const OrganGestorGrid = () => {

    const {t} = useTranslation();
    const navigate = useNavigate();
    const [springFilter, setSpringFilter] = useState<string>();
    const [treeView, setTreeView] = useState<boolean>(false);

    const actions = [
        {
            label: t('common.update'),
            icon: "edit",
            showInMenu: true,
            clickShowUpdateDialog: true,
        },
    ]

    const columns = [
        {
            field: 'codi',
            flex: 0.5,
            hidden: treeView
        },
        {
            field: 'nom',
            flex: 1,
            hidden: treeView
        },
        {
            field: 'pare',
            flex: 1,
            hidden: treeView
        },
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
            renderCell: (params:any) => <IconButton 
                aria-label="key" 
                color="inherit"
                title="Permisos"
                onClick={(e:any) => { e.stopPropagation(); navigate(`/organgestor/${params?.row?.id}/permis`); }}
            >
                <Badge badgeContent={params?.row?.numPermisos} color="primary" showZero>
                    <Icon>key</Icon>
                </Badge>
            </IconButton>
        }
    ]
        .filter((col:any)=>!col?.hidden);

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
                perspectives={treeView?['PATH','COUNT_PERMISOS']:['COUNT_PERMISOS']}
                sortModel={sortModel}
                rowAdditionalActions={actions}
                toolbarElementsWithPositions={[
                    {
                        position: 3,
                        element: <ToolbarButton
                            icon={'cached'}
                            color={'primary'}>&nbsp;{t('page.organGestor.action.actualitzar')}</ToolbarButton>,
                    },
                    {
                        position: 3,
                        element: <ToolbarButton
                            icon={'visibility'}
                            variant={treeView ?"contained" :"outlined"}
                            onClick={()=>setTreeView(prev=>!prev)}
                            color={'primary'}>&nbsp;{t('page.organGestor.action.vista')}</ToolbarButton>,
                    },
                ]}

                paginationActive={!treeView}
                autoHeight={treeView}
                treeData={treeView}
                groupingColDef={{
                    headerName: t('page.contingut.grid.nom'),
                    flex: 1,
                    valueFormatter: (value: any, row: any) => row?.codi +" - "+ row?.nom,
                }}
                treeDataAdditionalRows={(_rows: any) => {
                    const additionalRows: any[] = [];
                        if (_rows!=null && treeView){
                            for (const row of _rows) {
                                for (const r of row?.path) {
                                    if (!additionalRows.map((b:any) => b.id).includes(r?.id)
                                        && !_rows.map((b:any) => b.id).includes(r?.id))
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
    </GridPage>
}
export default OrganGestorGrid;