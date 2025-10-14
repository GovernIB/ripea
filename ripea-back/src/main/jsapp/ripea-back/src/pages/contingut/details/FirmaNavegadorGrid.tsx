import {useTranslation} from "react-i18next";
import {GridPage, useMuiDataGridApiRef} from "reactlib";
import {useState} from "react";
import {CardPage} from "../../../components/CardData.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {EnviarPortafirmesFilter} from "./EnviarPortafirmesGrid.tsx";

const sortModel: any = [{field: 'createdDate', sort: 'desc'}]
const columns = [
    {
        field: 'nom',
        flex: 0.5,
    },
    {
        field: 'metaDocument',
        flex: 0.6,
    },
    {
        field: 'expedient',
        flex: 0.75,
        renderCell: (params:any) => <a href={`/contingut/${params?.id}`}>{params?.formattedValue}</a>,
    },
    {
        field: 'createdDate',
        flex: 0.55,
    },
    {
        field: 'createdByFullName',
        flex: 0.45,
    },
]

const FirmaNavegadorGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const actions = [
        {
            label: t('page.document.action.firma.label'),
            icon: "edit_document",
            showInMenu: false,
        },
    ]
    // TODO: crear acción massiva
    const massiveActions = [
        {
            label: t('page.document.action.firma.label'),
            icon: "edit_document",
            showInMenu: false,
        },
    ]

    return <GridPage disableMargins>
        <CardPage title={t('navigate.massiu.firmasimpleweb')}>
            <EnviarPortafirmesFilter sessionKey={"MASSIVE_FIRMA_NAVEGADOR_FILTER"} onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={"documentResource"}
                columns={columns}
                filter={springFilter}
                // TODO: filtrar por permisos y pot firmar al navegador
                // perspectives={perspectives}
                sortModel={sortModel}

                rowAdditionalActions={actions}
                toolbarMassiveActions={massiveActions}
                // isRowSelectable={() => haveRequirements}

                // disabledMassiveDefSelector={!haveRequirements}
                // hiddenMassiveDefSelector={true}

                toolbarHideCreate
            />
        </CardPage>
        {/*{contentEviarPortafirmes}*/}
    </GridPage>
}
export default FirmaNavegadorGrid;