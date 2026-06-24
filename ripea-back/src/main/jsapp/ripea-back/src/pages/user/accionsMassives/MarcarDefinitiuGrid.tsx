import {GridPage, useMuiDataGridApiRef} from "reactlib";
import { CardPage } from "../../../components/CardData.tsx";
import {useTranslation} from "react-i18next";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {useState} from "react";
import {Link} from "@mui/material";
import {Link as RouterLink } from 'react-router-dom';
import {EnviarPortafirmesFilter} from "./EnviarPortafirmesGrid.tsx";
import {GridSortDirection} from "@mui/x-data-grid-pro";
import {useActions} from "../../contingut/details/ContingutActions.tsx";
import {useMassiveActions} from "../../contingut/details/ContingutMassiveActions.tsx";
import {useGridApiRef as useMuiDatagridApiRef} from "@mui/x-data-grid-pro";

const namedQueries: string[] = ['MASSIU_PORTAFIRMES']
// La perspectiva RESUM omple createdByFullName amb "Nom (codi)"; sense ella
// la columna només mostra el codi de l'usuari.
const perspectives: string[] = ['RESUM']
const sortModel: any = [{field: 'createdDate', sort: 'desc'}]
const columns = [
    {
        field: 'nom',
        flex: 0.8,
    },
    {
        field: 'metaDocument',
        flex: 0.6,
    },
    {
        field: 'expedient',
        flex: 0.6,
        renderCell: (params:any) => <Link component={RouterLink} to={`/contingut/${params?.row?.expedient?.id}`}>{params?.formattedValue}</Link>,
    },
    {
        field: 'createdDate',
        flex: 0.4,
    },
    {
        field: 'createdByFullName',
        flex: 0.6,
        sortProcessor: (_field: string, sort: GridSortDirection) => [ { field: "createdBy", sort } ]
    },
]

const MarcarDefinitiuGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const dataApiRef = useMuiDatagridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {definitiu} = useActions(refresh)
    const {definitiu: definitiuMassive} = useMassiveActions(refresh)

    const actions = [
        {
            label: t('page.document.action.definitive.label'),
            icon: "check_circle",
            showInMenu: false,
            onClick: definitiu,
        },
    ]
    const massiveActions = [
        {
            label: t('page.document.action.definitive.label'),
            icon: "check_circle",
            showInMenu: false,
            onClick: definitiuMassive,
        },
    ]

    return <GridPage autoHeight>
        <CardPage title={t('navigate.massiu.definitiu')}>
            <EnviarPortafirmesFilter
                sessionKey={"MASSIVE_DEFINITIVE_FILTER"}
                onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                apiRef={apiRef}
                datagridApiRef={dataApiRef}
                resourceName={"documentResource"}
                persistentStateKey={"documentResource_massMarcarDefinitiu"}
                columns={columns}
                filter={springFilter}
                toolbarShowFilterCount
                perspectives={perspectives}
                namedQueries={namedQueries}
                sortModel={sortModel}
                rowAdditionalActions={actions}
                toolbarMassiveActions={massiveActions}
                toolbarHideCreate
            />
        </CardPage>
    </GridPage>
}
export default MarcarDefinitiuGrid;