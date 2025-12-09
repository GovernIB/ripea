import {useTranslation} from "react-i18next";
import {GridPage, useMuiDataGridApiRef} from "reactlib";
import {Link} from "@mui/material";
import {Link as RouterLink } from 'react-router-dom';
import {useState} from "react";
import {CardPage} from "../../../components/CardData.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {EnviarPortafirmesFilter} from "./EnviarPortafirmesGrid.tsx";
import {GridSortDirection} from "@mui/x-data-grid-pro";
import useFirmaNavegador, {useFirmaNavegadorMassive} from "../../contingut/actions/FirmaNavegador.tsx";

const namedQueries: string[] = ['MASSIU_PASARELA']
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
		sortProcessor: (field: string, sort: GridSortDirection) => [ { field: "createdBy", sort } ]
    },
]

const FirmaNavegadorGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {handleShow: handleFirmaShow, content: contentFirma} = useFirmaNavegador(refresh);
    const {handleShow: handleFirmaMassive, content: contentFirmaMassive} = useFirmaNavegadorMassive(refresh);

    const actions = [
        {
            label: t('page.document.action.firma.label'),
            icon: "edit_document",
            showInMenu: false,
            onClick: handleFirmaShow,
        },
    ]
    const massiveActions = [
        {
            label: t('page.document.action.firma.label'),
            icon: "edit_document",
            showInMenu: false,
            onClick: handleFirmaMassive,
        },
    ]

    return <GridPage disableMargins>
        <CardPage title={t('navigate.massiu.firmasimpleweb')}>
            <EnviarPortafirmesFilter
                sessionKey={"MASSIVE_FIRMA_NAVEGADOR_FILTER"}
                onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={"documentResource"}
                columns={columns}
                filter={springFilter}
				namedQueries={namedQueries}
                sortModel={sortModel}
                rowAdditionalActions={actions}
                toolbarMassiveActions={massiveActions}
                toolbarHideCreate
            />
        </CardPage>
        {contentFirma}
        {contentFirmaMassive}
    </GridPage>
}
export default FirmaNavegadorGrid;