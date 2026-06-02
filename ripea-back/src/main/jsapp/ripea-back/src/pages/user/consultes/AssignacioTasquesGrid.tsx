import {useTranslation} from "react-i18next";
import {useState} from "react";
import {GridPage, useFormContext} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import GridFormField from "../../../components/GridFormField.tsx";
import {Link as RouterLink} from "react-router-dom";
import {Link} from "@mui/material";
import * as builder from "../../../util/springFilterUtils.ts";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";
import useTascaDetail from "../../tasca/details/TascaDetail.tsx";

const AssignacioTasquesFilterForm = () => {
    const {data} = useFormContext();
    return <>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="expedient"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="metaExpedientTasca" namedQueries={[`BY_EXPEDIENT#${data?.expedient?.id ?? 0}`]}/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="estat"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="responsable"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="dataInici" type={"date"}/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="dataFi" type={"date"}/>
    </>
}

const springFilterBuilder = (data:any) => {
    return builder.and(
        builder.eq('expedient.id', data?.expedient?.id),
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
        field: 'metaExpedientTasca',
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
        flex: 0.75,
    },
    {
        field: 'dataInici',
        flex: 0.5,
        valueFormatter: (value: any) => formatDate(value),
    },
]

const perspectives:any = ['AUDITORIA']
const AssignacioTasquesGrid = () => {
    const {t} = useTranslation();
    const [springFilter, setSpringFilter] = useState<string>();

    const { handleOpen, dialog } = useTascaDetail();

    const actions = [
        {
            label: t('common.detail'),
            icon: "info",
            showInMenu: false,
            onClick: handleOpen,
        },
    ]

    return <GridPage>
        <CardPage title={t('page.user.menu.assignacio')}>
            <AssignacioTasquesFilter onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                resourceName={"expedientTascaResource"}
                columns={columns}
                filter={springFilter}
                perspectives={perspectives}
                sortModel={sortModel}
                rowAdditionalActions={actions}
                readOnly
            />
        </CardPage>
        {dialog}
    </GridPage>
}
export default AssignacioTasquesGrid;