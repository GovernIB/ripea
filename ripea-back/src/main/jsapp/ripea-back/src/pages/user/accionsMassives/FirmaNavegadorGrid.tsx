import {useTranslation} from "react-i18next";
import {GridPage, useMuiDataGridApiRef} from "reactlib";
import {Box} from "@mui/material";
import ContingutLink from "../../../components/ContingutLink.tsx";
import {useState} from "react";
import {CardPage} from "../../../components/CardData.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {EnviarPortafirmesFilter} from "./EnviarPortafirmesGrid.tsx";
import {GridSortDirection} from "@mui/x-data-grid-pro";
import useFirmaNavegador, {useFirmaNavegadorMassive} from "../../contingut/actions/FirmaNavegador.tsx";
import {useExecucioMassivaContingut} from "../actions/ExecucioMassivaGrid.tsx";

const namedQueries: string[] = ['MASSIU_PASARELA']
const perspectives:any = ['EN_PROCES_FIRMA_WEB', 'RESUM'];
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
        renderCell: (params:any) => <ContingutLink id={params?.row?.expedient?.id}>{params?.formattedValue}</ContingutLink>,
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

const FirmaNavegadorGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {handleShow: handleFirmaShow, content: contentFirma} = useFirmaNavegador(refresh);
    const {handleShow: handleFirmaMassive, content: contentFirmaMassive} = useFirmaNavegadorMassive(refresh);
    const {handleOpen: handleContingutOpen, dialog: dialogContingut} = useExecucioMassivaContingut();

    const actions:any[] = [
        {
            label: t('page.document.action.firma.label'),
            icon: "edit_document",
            showInMenu: false,
            onClick: handleFirmaShow,
            hidden: (row:any) => row?.execucioMassivaFirmaWebId,
        },
        {
            label: t('page.user.action.massives.pending'),
            icon: <Box sx={{ color: 'warning.main' }}>schedule</Box>,
            showInMenu: false,
            onClick: (_id:any, row:any) => handleContingutOpen(row?.execucioMassivaFirmaWebId),
            hidden: (row:any) => !row?.execucioMassivaFirmaWebId,
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

    return <GridPage autoHeight>
        <CardPage title={t('navigate.massiu.firmasimpleweb')}>
            <EnviarPortafirmesFilter
                sessionKey={"MASSIVE_FIRMA_NAVEGADOR_FILTER"}
                onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={"documentResource"}
                persistentStateKey={"documentResource_massFirmaNavegador"}
                columns={columns}
                filter={springFilter}
                toolbarShowFilterCount
                perspectives={perspectives}
				namedQueries={namedQueries}
                sortModel={sortModel}
                rowAdditionalActions={actions}
                toolbarMassiveActions={massiveActions}
                isRowSelectable={(params:any) => !params?.row?.execucioMassivaFirmaWebId}
                toolbarHideCreate
            />
        </CardPage>
        {contentFirma}
        {contentFirmaMassive}
        {dialogContingut}
    </GridPage>
}
export default FirmaNavegadorGrid;