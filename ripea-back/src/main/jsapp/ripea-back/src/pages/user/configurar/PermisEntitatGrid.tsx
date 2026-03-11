import {useTranslation} from "react-i18next";
import {
    GridPage,
    useMuiDataGridApiRef,
} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";
import StyledMuiGrid, {ToolbarButton} from "../../../components/StyledMuiGrid.tsx";
import {Button, Icon} from "@mui/material";
import {usePermisEntitatCreate, usePermisEntitatModify, usePermisActions} from "../actions/ModifyPermis.tsx";
import {useNavigate, useParams} from "react-router-dom";
import {useEntitatSession} from "../../../components/Session.tsx";
import {useMemo} from "react";

const sortModel: any = [{field: 'principal', sort: 'asc'}]
const columns = [
    {
        field: 'principal',
        flex: 1,
    },
    {
        field: 'sid',
        flex: 1,
    },
    {
        field: 'admin',
        flex: 0.5,
        sortable: false,
        renderCell: (params:any) => (params?.row?.admin && <Icon>check</Icon>),
    },
    {
        field: 'adminLectura',
        flex: 0.5,
        sortable: false,
        renderCell: (params:any) => (params?.row?.adminLectura && <Icon>check</Icon>),
    },
    {
        field: 'user',
        flex: 0.5,
        sortable: false,
        renderCell: (params:any) => (params?.row?.user && <Icon>check</Icon>),
    },
]

const PermisEntitatGrid = ()=> {
    const {t} = useTranslation();
    const { id } = useParams();
    const {value: entitat} = useEntitatSession()
    const gridApiRef = useMuiDataGridApiRef();
    const navigate = useNavigate();

    const entitatId = useMemo(() => (
        id || entitat?.id
    ), [id, entitat?.id])

    const refresh = () => {
        gridApiRef?.current?.refresh?.();
    }

    const { eliminar } = usePermisActions(refresh)
    const { handleShow: handelCreate, content: contentCreate } = usePermisEntitatCreate(refresh);
    const { handleShow: handelModify, content: contentModify } = usePermisEntitatModify(refresh);
    const actions = [
        {
            label: t('common.update'),
            icon: "edit",
            showInMenu: true,
            onClick: (_id:any, row:any) => handelModify(entitatId, row),
        },
        {
            label: t('common.delete'),
            icon: "delete",
            showInMenu: true,
            onClick: (id:any) => eliminar(id, {
                classType: 'ENTITY',
                objectId: entitatId,
            }),
        },
    ]

    return <GridPage disableMargins>
        <CardPage
            title={t('page.user.menu.permisos')}
            header={ id &&
                <Button
                    variant="outlined"
                    color={"inherit"}
                    sx={{ borderRadius: '4px', padding: '0px 10px', marginLeft: "auto !important" }}
                    onClick={()=>navigate('/entitat')}
                >
                    <Icon>arrow_back</Icon>
                    {t('common.back')}
                </Button>
            }
        >
            <StyledMuiGrid
                apiRef={gridApiRef}
                resourceName={"aclSidResource"}
                popupEditUpdateActive
                columns={columns}
                sortModel={sortModel}
                namedQueries={[`ENTITY#${entitatId}`]}
                perspectives={[`PERMISION#ENTITY#${entitatId}`]}
                rowAdditionalActions={actions}
                toolbarElementsWithPositions={[
                    {
                        position: 3,
                        element: <ToolbarButton 
                            title={t('page.permision.action.new.label')}
                            icon={'add'} 
                            onClick={()=>handelCreate(entitatId)}
                            color={'primary'}>{t('page.permision.action.new.label')}</ToolbarButton>,
                    },
                ]}
                toolbarHideCreate
            />
        </CardPage>
        {contentCreate}
        {contentModify}
    </GridPage>
}
export default PermisEntitatGrid;