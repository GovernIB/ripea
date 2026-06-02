import {useTranslation} from "react-i18next";
import {
    GridPage,
    useMuiDataGridApiRef, useResourceApiService,
} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";
import StyledMuiGrid, {ToolbarButton} from "../../../components/StyledMuiGrid.tsx";
import {Button, Icon} from "@mui/material";
import {useEffect, useMemo, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {setTitlePage} from "../../../TitleHeaderConfigurator.tsx";
import {
    usePermisActions,
    usePermisMetaExpedientNodeCreate,
    usePermisMetaExpedientNodeModify,
    usePermisMetaExpedientOrganCreate, usePermisMetaExpedientOrganModify
} from "../actions/ModifyPermis.tsx";
import Load from "../../../components/Load.tsx";

// MetaExpedientOrgan
const PermisMetaExpedientOrganGrid = (props:any) => {
    const { id } = props;
    const {t} = useTranslation();
    const gridApiRef = useMuiDataGridApiRef();

    const refresh = () => {
        gridApiRef?.current?.refresh?.();
    }

    const columnsOrgan = useMemo(() => [
        {
            field: 'principal',
            headerName: t('page.permision.grid.principal'),
            flex: 1,
        },
        {
            field: 'sid',
            headerName: t('page.permision.grid.sid'),
            flex: 1,
            renderCell: (params:any) => (params?.row?.nomComplet),
        },
        {
            field: 'create',
            headerName: t('page.permision.grid.create'),
            flex: 0.5,
            sortable: false,
            renderCell: (params:any) => (params?.row?.create && <Icon>check</Icon>),
        },
        {
            field: 'read',
            headerName: t('page.permision.grid.read'),
            flex: 0.5,
            sortable: false,
            renderCell: (params:any) => (params?.row?.read && <Icon>check</Icon>),
        },
        {
            field: 'write',
            headerName: t('page.permision.grid.write'),
            flex: 0.5,
            sortable: false,
            renderCell: (params:any) => (params?.row?.write && <Icon>check</Icon>),
        },
        {
            field: 'delete',
            headerName: t('page.permision.grid.delete'),
            flex: 0.5,
            sortable: false,
            renderCell: (params:any) => (params?.row?.delete && <Icon>check</Icon>),
        },
        {
            field: 'estadistic',
            headerName: t('page.permision.grid.estadistic'),
            flex: 0.5,
            sortable: false,
            renderCell: (params:any) => (params?.row?.estadistic && <Icon>check</Icon>),
        },
    ], [t]);

    const { apiIsReady, eliminar } = usePermisActions(refresh)
    const { handleShow: handelCreate, content: contentCreate } = usePermisMetaExpedientOrganCreate(refresh);
    const { handleShow: handelModify, content: contentModify } = usePermisMetaExpedientOrganModify(refresh);
    const actions = [
        {
            label: t('common.update'),
            icon: "edit",
            showInMenu: true,
            onClick: (_rowId:any, row:any) => handelModify(undefined, row, {
                procedimentId: id,
                organGestor: row?.organGestor
            }),
            hidden: (row:any) => !row?.pareId,
        },
        {
            label: t('common.delete'),
            icon: "delete",
            showInMenu: true,
            onClick: (_rowId:any, row:any) => eliminar(row?.originalId, {
                classType: 'MET_EXP_ORG',
                procedimentId: id,
                organGestor: row?.organGestor
            }),
            hidden: (row:any) => !row?.pareId,
        },
    ]

    return <Load value={apiIsReady}>
        <StyledMuiGrid
            apiRef={gridApiRef}
            resourceName={"aclObjIdentityResource"}
            popupEditUpdateActive
            columns={columnsOrgan}
            namedQueries={[`MET_EXP_ORG#${id}`]}
            perspectives={[`SID`]}
            disableColumnSorting

            rowAdditionalActions={actions}
            toolbarElementsWithPositions={[
                {
                    position: 3,
                    element: <ToolbarButton title={t('page.permision.action.new.label')} icon={'add'}
                                            onClick={()=>handelCreate(undefined, {}, {procedimentId: id})}
                                            color={'primary'}>{t('page.permision.action.new.label')}</ToolbarButton>,
                },
            ]}

            treeData
            groupingColDef={{
                headerName: t('page.permision.grid.organGestor'),
                flex: 1.5,
                valueFormatter: (_value: any, row: any) => {
                    return (row?.pareId)
                        ?'' // ?row?.principal + " - " + row?.sid
                        :row?.organGestor?.description;
                },
            }}
            rowsTransformer={(_rows: any) => {
                if (!_rows) return [];
                const additionalRows: any[] = _rows;
                if (_rows!=null){
                    for (const row of _rows) {
                        for (const sid of (row?.sids ?? [])) {
                            const newId = row?.id+"-"+sid.id
                            if (!additionalRows.map((b) => b.id).includes(newId)) {
                                additionalRows.push({
                                    ...sid,
                                    pareId: row?.id,
                                    originalId: sid.id,
                                    id: newId,
                                })
                            }
                        }
                    }
                }
                return additionalRows;
            }}
            getTreeDataPath={(row: any): string[] => {
                return !!row?.pareId ?[`${row?.pareId}`,`${row.id}`]:[`${row.id}`];
            }}
            isGroupExpandedByDefault={()=>true}
            toolbarHideCreate
        />
        {contentCreate}
        {contentModify}
    </Load>
}

// MetaExpedientNode
const sortModelNode: any = [{field: 'sid', sort: 'asc'}]
const columnsNode = [
    {
        field: 'principal',
        flex: 1,
    },
    {
        field: 'sid',
        flex: 1,
        renderCell: (params:any) => (params?.row?.nomComplet),    },
    {
        field: 'create',
        flex: 0.5,
        sortable: false,
        renderCell: (params:any) => (params?.row?.create && <Icon>check</Icon>),
    },
    {
        field: 'read',
        flex: 0.5,
        sortable: false,
        renderCell: (params:any) => (params?.row?.read && <Icon>check</Icon>),
    },
    {
        field: 'write',
        flex: 0.5,
        sortable: false,
        renderCell: (params:any) => (params?.row?.write && <Icon>check</Icon>),
    },
    {
        field: 'delete',
        flex: 0.5,
        sortable: false,
        renderCell: (params:any) => (params?.row?.delete && <Icon>check</Icon>),
    },
    {
        field: 'estadistic',
        flex: 0.5,
        sortable: false,
        renderCell: (params:any) => (params?.row?.estadistic && <Icon>check</Icon>),
    },
]
const PermisMetaExpedientNodeGrid = (props:any) => {
    const { id } = props;
    const {t} = useTranslation();
    const gridApiRef = useMuiDataGridApiRef();

    const refresh = () => {
        gridApiRef?.current?.refresh?.();
    }

    const { eliminar } = usePermisActions(refresh)
    const { handleShow: handelCreate, content: contentCreate } = usePermisMetaExpedientNodeCreate(refresh);
    const { handleShow: handelModify, content: contentModify } = usePermisMetaExpedientNodeModify(refresh);
    const actions = [
        {
            label: t('common.update'),
            icon: "edit",
            showInMenu: true,
            onClick: (_rowId:any, row:any) => handelModify(id, row),
        },
        {
            label: t('common.delete'),
            icon: "delete",
            showInMenu: true,
            onClick: (rowId:any) => eliminar(rowId, {
                classType: 'MET_NOD',
                objectId: id,
            }),
        },
    ]

    return <>
        <StyledMuiGrid
            apiRef={gridApiRef}
            resourceName={"aclSidResource"}
            popupEditUpdateActive
            columns={columnsNode}
            sortModel={sortModelNode}
            namedQueries={[`MET_NOD#${id}`]}
            perspectives={[`PERMISION#MET_NOD#${id}`]}

            rowAdditionalActions={actions}
            toolbarElementsWithPositions={[
                {
                    position: 3,
                    element: <ToolbarButton
                        title={t('page.permision.action.new.label')}
                        icon={'add'}
                        onClick={()=>handelCreate(id)}
                        color={'primary'}>{t('page.permision.action.new.label')}</ToolbarButton>,
                },
            ]}
            toolbarHideCreate
        />
        {contentCreate}
        {contentModify}
    </>
}

const PermisMetaExpedientGrid = ()=> {
    const {t} = useTranslation();
    const { id } = useParams();
    const navigate = useNavigate();

    const {
        isReady: apiIsReady,
        getOne: apiGetOne,
    } = useResourceApiService('metaExpedientResource');
    const [entity, setEntity] = useState<any>();

    useEffect(() => {
        if(apiIsReady && id){
            apiGetOne(id)
                .then((app) => setEntity(app))
            // .catch((error) => {
            //     handleClose()
            //     temporalMessageShow(null, error?.message, 'error');
            // });
        } else {
            setEntity(undefined)
        }
    }, [apiIsReady, id]);

    useEffect(() => {
        setTitlePage(t('page.user.menu.procedimentPermis', {nom: entity?.nom}))
    }, [entity]);

    return <GridPage>
        <Load value={entity}>
            <CardPage title={t('page.user.menu.procedimentPermis', {nom: entity?.nom})}
                      header={<>
                          <Button
                              variant="outlined"
                              color={"inherit"}
                              sx={{ borderRadius: '4px', padding: '0px 10px', marginLeft: "auto !important" }}
                              onClick={()=>navigate('/metaExpedient')}
                          >
                              <Icon>arrow_back</Icon>
                              {t('common.back')}
                          </Button>
                      </>}>
                {entity?.procedimentComu
                    ?<PermisMetaExpedientOrganGrid id={id}/>
                    :<PermisMetaExpedientNodeGrid id={id}/>
                }
            </CardPage>
        </Load>
    </GridPage>
}
export default PermisMetaExpedientGrid;