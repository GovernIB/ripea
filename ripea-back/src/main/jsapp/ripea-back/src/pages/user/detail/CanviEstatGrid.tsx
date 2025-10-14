import {useTranslation} from "react-i18next";
import {GridPage, useFormContext, useMuiDataGridApiRef} from "reactlib";
import {useState} from "react";
import { CardPage } from "../../../components/CardData.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {Alert, Grid} from "@mui/material";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import GridFormField from "../../../components/GridFormField.tsx";
import {GridSortDirection} from "@mui/x-data-grid-pro";
import {StyledEstat, StyledPrioritat} from "../../expedient/ExpedientGrid.tsx";

const CanviEstatFilterFrom = () => {
    const {data} = useFormContext();

    const expedientFilter = builder.and(builder.eq('metaExpedient.id', data?.procediment?.id));

    return <>
        <GridFormField xs={3} name="procediment"/>
        <GridFormField xs={3} name="expedient" filter={expedientFilter}/>
        <GridFormField xs={3} name="dataCreacioInici" type={"date"}/>
        <GridFormField xs={3} name="dataCreacioFi" type={"date"}/>
        <GridFormField xs={3} name="estat" requestParams={{metaExpedientId: data?.procediment?.id, withoutTancar: true}}/>
        <GridFormField xs={3} name="prioritat"/>
        <Grid item xs={3.6}/>
    </>
}

const springFilterBuilder = (data: any) => {
    return builder.and(
        builder.eq("metaExpedient.id", data?.procediment?.id),
        builder.eq("id", data?.expedient?.id),
        builder.betweenDates("createdDate", data?.dataCreacioInici, data?.dataCreacioFi),
        builder.eq("estat", `'OBERT'`),
        data.estat && (data.estat != '0' && data.estat != '-1') && builder.eq("estatAdditional.id", data.estat),
        builder.eq("prioritat", data?.prioritat),
    );
}

const CanviEstatFilter = (props: any) => {
    const {onSpringFilterChange, setRequirements} = props;
    return <StyledMuiFilter
        resourceName={"expedientResource"}
        code={"MASSIVE_CANVI_ESTAT_FILTER"}
        springFilterBuilder={(data:any) => {
            setRequirements?.(!!data?.procediment);
            return springFilterBuilder(data)
        }}
        onSpringFilterChange={onSpringFilterChange}
    >
        <CanviEstatFilterFrom/>
    </StyledMuiFilter>
}

const sortModel: any = [{field: 'createdDate', sort: 'desc'}]
const columns = [
    {
        field: 'nom',
        flex: 1,
        renderCell: (params:any) => <a href={`/contingut/${params?.id}`}>{params?.formattedValue}</a>,
    },
    {
        field: 'metaExpedient',
        flex: 1.5,
    },
    {
        field: 'estat',
        flex: 0.75,
        renderCell: (params: any) => <StyledEstat entity={params?.row} icon={"folder"}>{params.formattedValue}</StyledEstat>,
        sortProcessor: (field: string, sort: GridSortDirection) => {
            return [
                { field: "estatAdditional", sort },
                { field: field, sort },
                { field: "id", sort }
            ]
        }
    },
    {
        field: 'prioritat',
        flex: 0.45,
        renderCell: (params: any) => <StyledPrioritat entity={params?.row}>{params.formattedValue}</StyledPrioritat>
    },
    {
        field: 'createdDate',
        flex: 1,
    },
    {
        field: 'createdByFullName',
        flex: 0.5,
    },
]

const CanviEstatGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();
    const [haveRequirements, setRequirements] = useState<boolean>(false)

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const actions = [
        {
            label: t('page.document.action.portafirmes.label'),
            icon: "logout",
            showInMenu: false,
        },
    ]
    // TODO: crear acción massiva
    const massiveActions = [
        {
            label: t('page.document.action.portafirmes.label'),
            icon: "logout",
            showInMenu: false,
        },
    ]

    return <GridPage disableMargins>
        <CardPage title={t('navigate.massiu.canviEstat')}>
            <Alert severity={'info'} sx={{mb: 1}}>{t('page.expedient.alert.canviEstat')}</Alert>

            <CanviEstatFilter setRequirements={setRequirements} onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={"expedientResource"}
                columns={columns}
                filter={springFilter}
                sortModel={sortModel}

                rowAdditionalActions={actions}
                toolbarMassiveActions={massiveActions}
                isRowSelectable={() => haveRequirements}

                disabledMassiveDefSelector={!haveRequirements}
                toolbarHideCreate
            />
        </CardPage>
    </GridPage>
}
export default CanviEstatGrid;