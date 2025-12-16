import {useTranslation} from "react-i18next";
import {useState} from "react";
import {GridPage} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import GridFormField from "../../../components/GridFormField.tsx";
import {Link as RouterLink} from "react-router-dom";
import {Grid, Link} from "@mui/material";
import * as builder from "../../../util/springFilterUtils.ts";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";

const AssignacioTasquesFilterForm = () => {
    return <>
        <GridFormField xs={3} name="titol"/>
        <GridFormField xs={3} name="metaExpedientTasca"/>
        <GridFormField xs={3} name="estat"/>
        <GridFormField xs={3} name="responsable"/>
        <GridFormField xs={4} name="dataInici" type={"date"}/>
        <GridFormField xs={4} name="dataFi" type={"date"}/>
        <Grid item xs={1.6}/>
    </>
}

const springFilterBuilder = (data:any) => {
    return builder.and(
        builder.like('titol', data?.titol),
        builder.eq('metaExpedientTasca.id', data?.metaExpedientTasca?.id),
        builder.eq('estat', `'${data?.estat}'`),
        builder.eq("responsableActual.id", data?.responsable),
        builder.betweenDates('dataInici', data?.dataInici, data?.dataFi),
    );
}

const AssignacioTasquesFilter = (props: any) => {
    const {onSpringFilterChange} = props;

    return <StyledMuiFilter
        resourceName={"expedientTascaResource"}
        code={"TASCA_FILTER"}
        sessionKey={"ASSIGNACIO_TASCA_FILTER"}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
    >
        <AssignacioTasquesFilterForm/>
    </StyledMuiFilter>
}

// Grid
const sortModel: any = [{field: 'dataInici', sort: 'desc'}]
const columns = [
    {
        field: 'expedient',
        flex: 1,
        renderCell: (params:any) => <Link component={RouterLink} to={`/contingut/${params?.row?.expedient?.id}`}>{params?.formattedValue}</Link>,
    },
    {
        field: 'titol',
        flex: 1,
    },
    {
        field: 'estat',
        flex: 0.5,
    },
    {
        field: 'responsablesStr',
        flex: 1,
    },
    {
        field: 'createdByFullName',
        flex: 0.5,
    },
    {
        field: 'dataInici',
        flex: 0.75,
        valueFormatter: (value: any) => formatDate(value),
    },
]

const perspectives:any = ['AUDITORIA']
const AssignacioTasquesGrid = () => {
    const {t} = useTranslation();
    const [springFilter, setSpringFilter] = useState<string>();

    return <GridPage disableMargins>
        <CardPage title={t('page.user.menu.assignacio')}>
            <AssignacioTasquesFilter onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                resourceName={"expedientTascaResource"}
                columns={columns}
                filter={springFilter}
                perspectives={perspectives}
                sortModel={sortModel}
                readOnly
            />
        </CardPage>
    </GridPage>
}
export default AssignacioTasquesGrid;