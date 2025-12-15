import {GridPage} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import {useTranslation} from "react-i18next";
import {useState} from "react";
import {EstatMessage} from "../../remesa/RemesaGrid.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";
import GridFormField from "../../../components/GridFormField.tsx";
import {Link as RouterLink} from "react-router-dom";
import {Grid, Link} from "@mui/material";

const ConsultesPinbalFilterForm = () => {
    return <>
        <GridFormField xs={4} name="expedient"/>
        <GridFormField xs={4} name="metaExpedient"/>
        <GridFormField xs={4} name="servei"/>
        <GridFormField xs={4} name="createdBy"/>
        <GridFormField xs={4} name="createdDateInici" type={"date"}/>
        <GridFormField xs={4} name="createdDateFi" type={"date"}/>
        <GridFormField xs={4} name="estat"/>
        <Grid item xs={5.6}/>
    </>
}

const springFilterBuilder = (data:any) => {
    return builder.and(
        builder.eq('expedient.id', data?.expedient?.id),
        builder.eq('metaExpedient.id', data?.metaExpedient?.id),
        builder.eq('servei?.id',  data?.servei?.id),
        builder.eq('createdBy',  data?.createdBy?.id),
        builder.betweenDates('createdDate', data?.createdDateInici, data?.createdDateFi),
        builder.eq('estat', `'${data?.estat}'`),
    );
}

const ConsultesPinbalFilter = (props: any) => {
    const {onSpringFilterChange} = props;

    return <StyledMuiFilter
        resourceName={"consultaPinbalResource"}
        code={"FILTER_CONSULTA_PINBAL"}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
    >
        <ConsultesPinbalFilterForm/>
    </StyledMuiFilter>
}

// Grid
const StyledEstat = (props:any) => {
    const { entity, children } = props;

    switch (entity?.estat) {
        case 'TRAMITADA':
            return <EstatMessage icon={"check"} color='success'>{children}</EstatMessage>
        case 'ERROR':
            return <EstatMessage icon={"warning"} color={'error'} title={entity?.error}>{children}</EstatMessage>
    }

    return <></>
}

const sortModel: any = [{field: 'createdDate', sort: 'desc'}]
const columns = [
    {
        field: 'expedient',
        flex: 1,
        renderCell: (params:any) => <Link component={RouterLink} to={`/contingut/${params?.row?.expedient?.id}`}>{params?.formattedValue}</Link>,
    },
    {
        field: 'metaExpedient',
        flex: 1,
    },
    {
        field: 'servei',
        flex: 1,
    },
    {
        field: 'createdByFullName',
        flex: 0.5,
    },
    {
        field: 'createdDate',
        flex: 0.75,
        valueFormatter: (value: any) => formatDate(value),
    },
    {
        field: 'estat',
        flex: 0.5,
        renderCell: (params:any) => <StyledEstat entity={params?.row}>{params.formattedValue}</StyledEstat>
    },
    {
        field: 'document',
        flex: 1,
    },
]

const ConsultesPinbalGrid = () => {
    const {t} = useTranslation();
    const [springFilter, setSpringFilter] = useState<string>();

    return <GridPage disableMargins>
        <CardPage title={t('page.user.menu.pinbalEnviades')}>
            <ConsultesPinbalFilter onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                resourceName={"consultaPinbalResource"}
                columns={columns}
                filter={springFilter}
                sortModel={sortModel}
                readOnly
            />
        </CardPage>
    </GridPage>
}
export default ConsultesPinbalGrid;