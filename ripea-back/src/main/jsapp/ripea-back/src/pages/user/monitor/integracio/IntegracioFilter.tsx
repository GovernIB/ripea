import GridFormField, {GridButton} from "../../../../components/GridFormField.tsx";
import * as builder from "../../../../util/springFilterUtils.ts";
import StyledMuiFilter from "../../../../components/StyledMuiFilter.tsx";
import {Grid, Icon} from "@mui/material";
import {useIntegracioDiagnostic} from "./IntegracioDiagnostic.tsx";
import {useTranslation} from "react-i18next";

const IntegracioFilterForm = ({handleOpen}:any) => {
    const {t} = useTranslation()
    return <>
        <GridFormField xs={3} name="entitat"/>
        <GridFormField xs={3} name="dataInici"/>
        <GridFormField xs={3} name="dataFi"/>
        <GridFormField xs={3} name="tipus"/>
        <GridFormField xs={3} name="descripcio"/>
        <GridFormField xs={3} name="estat"/>
        <Grid xs={1.6}/>
        <GridButton xs={2} onClick={handleOpen} variant={'contained'} color={'success'}>
            <Icon>build</Icon>{t('page.integracio.action.diagnosticAll.label')}
        </GridButton>
    </>
}

const springFilterBuilder = (data:any) => {
    return builder.and(
        builder.eq('entitat.id', data?.entitat?.id),
        builder.betweenDates('data', data?.dataInici, data?.dataFi),
        builder.eq('tipus', `'${data?.tipus}'`),
        builder.like('descripcio', data?.descripcio),
        builder.eq('estat', `'${data?.estat}'`),
    )
}

export const IntegracioFilter = (props: any) => {
    const {integracions, onSpringFilterChange} = props;
    const {handleOpen, dialog} = useIntegracioDiagnostic(integracions);
    return <StyledMuiFilter
        resourceName={"integracioResource"}
        code={"FILTER"}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
    >
        <IntegracioFilterForm handleOpen={handleOpen}/>
        {dialog}
    </StyledMuiFilter>
}