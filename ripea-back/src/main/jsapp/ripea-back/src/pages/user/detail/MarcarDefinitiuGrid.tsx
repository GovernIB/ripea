import {GridPage, useMuiDataGridApiRef} from "reactlib";
import { CardPage } from "../../../components/CardData.tsx";
import {useTranslation} from "react-i18next";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {useState} from "react";
import {Icon} from "@mui/material";
import {EnviarPortafirmesFilter} from "./EnviarPortafirmesGrid.tsx";

const sortModel: any = [{field: 'createdDate', sort: 'desc'}]
const columns = [
    {
        field: 'nom',
        flex: 0.75,
    },
    {
        field: 'metaDocument',
        flex: 0.5,
    },
    {
        field: 'expedient',
        flex: 1.75,
        renderCell: (params:any) => <>
            {/** TODO: revisar columna ubicación */}
            /<a href={`/contingut/${params?.id}`} style={{ display: 'flex', alignItems: 'center' }}><Icon>folder</Icon>{params?.formattedValue}</a>
            {params?.row?.pare?.id != params?.row?.expedient?.id ?<>/.../<Icon>folder</Icon>{params?.row?.pare?.description}</> :"" }
            /<Icon>description</Icon>{params?.row?.fitxerNom}
        </>,
    },
    {
        field: 'createdDate',
        flex: 0.5,
    },
    {
        field: 'createdByFullName',
        flex: 0.5,
    },
]

const MarcarDefinitiuGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const actions = [
        {
            label: t('page.document.action.definitive.label'),
            icon: "check_circle",
            showInMenu: false,
        },
    ]
    // TODO: crear acción massiva
    const massiveActions = [
        {
            label: t('page.document.action.definitive.label'),
            icon: "check_circle",
            showInMenu: false,
        },
    ]

    return <GridPage disableMargins>
        <CardPage title={t('navigate.massiu.definitiu')}>
            <EnviarPortafirmesFilter
                sessionKey={"MASSIVE_DEFINITIVE_FILTER"}
                onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={"documentResource"}
                columns={columns}
                filter={springFilter}
                // TODO: filtrar por permisos y puede marcar definitivo
                sortModel={sortModel}

                rowAdditionalActions={actions}
                toolbarMassiveActions={massiveActions}

                toolbarHideCreate
            />
        </CardPage>
    </GridPage>
}
export default MarcarDefinitiuGrid;