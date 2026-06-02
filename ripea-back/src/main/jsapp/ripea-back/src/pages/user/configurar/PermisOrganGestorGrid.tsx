import {useTranslation} from "react-i18next";
import {
    GridPage,
    useMuiDataGridApiRef, useResourceApiService,
} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";
import StyledMuiGrid, {ToolbarButton} from "../../../components/StyledMuiGrid.tsx";
import {Button, Icon} from "@mui/material";
import {useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {setTitlePage} from "../../../TitleHeaderConfigurator.tsx";
import {
    usePermisActions,
    usePermisOrganGestorCreate,
    usePermisOrganGestorModify
} from "../actions/ModifyPermis.tsx";
import Load from "../../../components/Load.tsx";

const sortModel: any = [{field: 'sid', sort: 'asc'}]
const columns = [
    {
        field: 'principal',
        flex: 1,
    },
    {
        field: 'sid',
        flex: 1,
        renderCell: (params:any) => (params?.row?.nomComplet),
    },
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
        renderCell: (params:any) => (params?.row?.user && <Icon>check</Icon>),
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
        field: 'procedimentsComuns',
        flex: 0.5,
        sortable: false,
        renderCell: (params:any) => (params?.row?.procedimentsComuns && <Icon>check</Icon>),
    },
    {
        field: 'admin',
        flex: 0.5,
        sortable: false,
        renderCell: (params:any) => (params?.row?.admin && <Icon>check</Icon>),
    },
    {
        field: 'adminComuns',
        flex: 0.5,
        sortable: false,
        renderCell: (params:any) => (params?.row?.adminComuns && <Icon>check</Icon>),
    },
    {
        field: 'disseny',
        flex: 0.5,
        sortable: false,
        renderCell: (params:any) => (params?.row?.disseny && <Icon>check</Icon>),
    },
]

const PermisOrganGestorGrid = ()=> {
    const {t} = useTranslation();
    const { id } = useParams();
    const gridApiRef = useMuiDataGridApiRef();
    const navigate = useNavigate();

    const {
        isReady: apiIsReady,
        getOne: apiGetOne,
    } = useResourceApiService('organGestorResource');
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

    const refresh = () => {
        gridApiRef?.current?.refresh?.();
    }

    const { eliminar } = usePermisActions(refresh)
    const { handleShow: handelCreate, content: contentCreate } = usePermisOrganGestorCreate(refresh);
    const { handleShow: handelModify, content: contentModify } = usePermisOrganGestorModify(refresh);
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
            onClick: (permisId:any) => eliminar(permisId, {
                classType: 'ORGAN',
                objectId: id,
            }),
        },
    ]

    useEffect(() => {
        setTitlePage(t('page.user.menu.organPermis', {nom: entity?.nom}))
    }, [entity]);

    return <GridPage>
        <Load value={entity}>
            <CardPage title={t('page.user.menu.organPermis', {nom: entity?.nom})}
                      header={<>
                          <Button
                              variant="outlined"
                              color={"inherit"}
                              sx={{ borderRadius: '4px', padding: '0px 10px', marginLeft: "auto !important" }}
                              onClick={()=>navigate('/organgestor')}
                          >
                              <Icon>arrow_back</Icon>
                              {t('common.back')}
                          </Button>
                      </>}>
                <StyledMuiGrid
                    apiRef={gridApiRef}
                    resourceName={"aclSidResource"}
                    popupEditUpdateActive
                    columns={columns}
                    sortModel={sortModel}
                    namedQueries={[`ORGAN#${id}`]}
                    perspectives={[`PERMISION#ORGAN#${id}`]}
                    rowAdditionalActions={actions}
                    toolbarElementsWithPositions={[
                        {
                            position: 3,
                            element: <ToolbarButton title={t('common.create')} icon={'add'} onClick={()=>handelCreate(id)} color={'primary'}>
                                {t('common.nouPermis')}
                            </ToolbarButton>,
                        },
                    ]}
                    toolbarHideCreate
                />
            </CardPage>
            {contentCreate}
            {contentModify}
        </Load>
    </GridPage>
}
export default PermisOrganGestorGrid;