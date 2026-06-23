import GridFormField, {GridButton} from "../../../../components/GridFormField.tsx";
import * as builder from "../../../../util/springFilterUtils.ts";
import StyledMuiFilter from "../../../../components/StyledMuiFilter.tsx";
import {useIntegracioDiagnostic} from "./IntegracioDiagnostic.tsx";
import {useTranslation} from "react-i18next";

const IntegracioFilterForm = ({handleOpen}:any) => {
    const {t} = useTranslation()
    return <>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="entitat"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="dataInici"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="dataFi"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="tipus"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="descripcio"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="estat"/>
        <GridButton size={{xs: 12, sm: 3, md: 2}} icon={'build'} onClick={handleOpen} variant={'contained'} color={'success'}>
            {t('page.integracio.action.diagnosticAll.label')}
        </GridButton>
    </>
}

const springFilterBuilder = (data:any) => {
    return builder.and(
        builder.eq('entitat.id', data?.entitat?.id), //Al back (additionalSpringFilter) es substitueix per entitatCodi
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