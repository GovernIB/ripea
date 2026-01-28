import {useTranslation} from "react-i18next";
import {
    GridPage,
    useMuiDataGridApiRef,
} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";
import StyledMuiGrid, {ToolbarButton} from "../../../components/StyledMuiGrid.tsx";
import {Icon} from "@mui/material";
import {usePermisEntitatCreate, usePermisEntitatModify, usePermisActions} from "../actions/ModifyPermis.tsx";

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
        renderCell: (params:any) => (params?.row?.admin && <Icon>check</Icon>),
    },
    {
        field: 'adminLectura',
        flex: 0.5,
        renderCell: (params:any) => (params?.row?.adminLectura && <Icon>check</Icon>),
    },
    {
        field: 'user',
        flex: 0.5,
        renderCell: (params:any) => (params?.row?.user && <Icon>check</Icon>),
    },
]

const PermisEntitatGrid = ()=> {
    const {t} = useTranslation();
    const gridApiRef = useMuiDataGridApiRef();

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
            onClick: (id:any, row:any) => handelModify(undefined, row),
        },
        {
            label: t('common.delete'),
            icon: "delete",
            showInMenu: true,
            onClick: (id:any) => eliminar(id, {classType: 'ENTITY'}),
        },
    ]

    return <GridPage disableMargins>
        <CardPage title={t('page.user.menu.permisos')}>
            <StyledMuiGrid
                apiRef={gridApiRef}
                resourceName={"aclSidResource"}
                popupEditUpdateActive
                columns={columns}
                sortModel={sortModel}
                namedQueries={['ENTITY']}
                perspectives={['PERMISION#ENTITY']}
                rowAdditionalActions={actions}
                toolbarElementsWithPositions={[
                    {
                        position: 3,
                        element: <ToolbarButton 
                            title={t('page.permision.action.new.label')}
                            icon={'add'} 
                            onClick={()=>handelCreate()} 
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