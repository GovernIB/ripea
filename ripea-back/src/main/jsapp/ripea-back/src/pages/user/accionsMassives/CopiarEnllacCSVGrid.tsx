import {GridPage, useMuiDataGridApiRef} from "reactlib";
import { CardPage } from "../../../components/CardData.tsx";
import {useTranslation} from "react-i18next";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {useState} from "react";
import {GridSortDirection} from "@mui/x-data-grid-pro";
import {Link} from "@mui/material";
import {Link as RouterLink } from 'react-router-dom';
import {EnviarPortafirmesFilter} from "./EnviarPortafirmesGrid.tsx";
import {useActions} from "../../contingut/details/ContingutActions.tsx";
import {useMassiveActions} from "../../contingut/details/ContingutMassiveActions.tsx";

const namedQueriesCsv: string[] = ['MASSIU_ENLLAC_CSV']
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

const CopiarEnllacCSVGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const {getLinkCSV} = useActions()
    const {enllacCSV} = useMassiveActions()

    const actions = [
        {
            label: t('page.document.action.csv.label'),
            icon: "file_copy",
            showInMenu: false,
            onClick: getLinkCSV,
        },
    ]
    const massiveActions = [
        {
            label: t('page.document.action.csv.label'),
            icon: "file_copy",
            showInMenu: false,
            onClick: enllacCSV,
        },
    ]

    return <GridPage>
        <CardPage title={t('navigate.massiu.csv')}>
            <EnviarPortafirmesFilter
                sessionKey={"MASSIVE_CSV_FILTER"}
                onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={"documentResource"}
                columns={columns}
                filter={springFilter}
                sortModel={sortModel}
                namedQueries={namedQueriesCsv}
                rowAdditionalActions={actions}
                toolbarMassiveActions={massiveActions}
                toolbarHideCreate
            />
        </CardPage>
    </GridPage>
}
export default CopiarEnllacCSVGrid;