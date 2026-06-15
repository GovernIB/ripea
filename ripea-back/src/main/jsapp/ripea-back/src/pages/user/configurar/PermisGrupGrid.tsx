import {useTranslation} from "react-i18next";
import {
    GridPage,
    useMuiDataGridApiRef,
} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";
import StyledMuiGrid, {ToolbarButton} from "../../../components/StyledMuiGrid.tsx";
import {useEffect} from "react";
import {useParams} from "react-router-dom";
import {setTitlePage} from "../../../TitleHeaderConfigurator.tsx";
import {usePermisGrupCreate, usePermisActions} from "../actions/ModifyPermis.tsx";

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
]

const PermisGrupGrid = ()=> {
    const {t} = useTranslation();
    const { id } = useParams();
    const gridApiRef = useMuiDataGridApiRef();

    const refresh = () => {
        gridApiRef?.current?.refresh?.();
    }

    const { eliminar } = usePermisActions(refresh)
    const { handleShow: handelCreate, content: contentCreate } = usePermisGrupCreate(refresh);
    const actions = [
        {
            label: t('common.delete'),
            icon: "delete",
            showInMenu: false,
            onClick: (permisId:any) => eliminar(permisId, {
                classType: 'GRUP',
                objectId: id,
            }),
        },
    ]

    useEffect(() => {
        setTitlePage(t('page.user.menu.grupPermis'))
    }, [t]);

    return <GridPage autoHeight>
        <CardPage title={t('page.user.menu.grupPermis')}>
            <StyledMuiGrid
                apiRef={gridApiRef}
                resourceName={"aclSidResource"}
                persistentStateActive={false}
                popupEditUpdateActive
                columns={columns}
                sortModel={sortModel}
                namedQueries={[`GRUP#${id}`]}
                perspectives={[`PERMISION#GRUP#${id}`]}
                rowAdditionalActions={actions}
                toolbarElementsWithPositions={[
                    {
                        position: 3,
                        element: <ToolbarButton 
                            title={t('common.nouPermis')}
                            icon={'add'}
                            onClick={()=>handelCreate(id)}
                            color={'primary'}>{t('common.nouPermis')}</ToolbarButton>,
                    },
                ]}
                toolbarHideCreate
            />
        </CardPage>
        {contentCreate}
    </GridPage>
}
export default PermisGrupGrid;