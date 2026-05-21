import GridFormField from "../../components/GridFormField.tsx";
import StyledMuiFilter from "../../components/StyledMuiFilter.tsx";
import * as builder from '../../util/springFilterUtils';
import {useFormContext} from "reactlib";

const AnotacioFilterForm = () => {
    const {data} = useFormContext()

    return <>
        {(!data?.advanced) && <>
            <GridFormField size={{xs: 12, sm: 6, md: 3.5}} name="metaExpedient"/>
            <GridFormField size={{xs: 12, sm: 6, md: 2}} name="estat"/>
            <GridFormField size={{xs: 12, sm: 6, md: 2}} name="dataRecepcioInicial" type={"date"}/>
            <GridFormField size={{xs: 12, sm: 6, md: 2}} name="dataRecepcioFinal" type={"date"}/>
        </>}
        {(data?.advanced) && <>
            <GridFormField size={{xs: 12, sm: 6, md: 4}} name="numRegistre"/>
            <GridFormField size={{xs: 12, sm: 6, md: 4}} name="extracte"/>
            <GridFormField size={{xs: 12, sm: 6, md: 4}} name="destinacio"/>
            <GridFormField size={{xs: 12, sm: 6, md: 4}} name="metaExpedient"/>
            <GridFormField size={{xs: 12, sm: 6, md: 4}} name="dataRecepcioInicial" type={"date"}/>
            <GridFormField size={{xs: 12, sm: 6, md: 4}} name="dataRecepcioFinal" type={"date"}/>
            <GridFormField size={{xs: 12, sm: 6, md: 4}} name="estat"/>
            <GridFormField size={{xs: 12, sm: 6, md: 4}} name="interessat"/>
        </>}
    </>
}

const springFilterBuilder = (data: any): string => {
    return builder.and(
        builder.like("identificador", data.numRegistre),
        builder.like("registre.extracte", data.extracte),
        builder.like("registre.destiCodiINom", data.destinacio),
        builder.eq("metaExpedient.id", data?.metaExpedient?.id),
        builder.betweenDates("registre.data", data.dataRecepcioInicial, data.dataRecepcioFinal),

        data.estat == 'ACCEPTAT'
            ? builder.like("estat", "PROCESSAT")
            : builder.eq("estat", `'${data.estat}'`),
        builder.exists(
            builder.or(
                builder.like("registre.interessats.documentNumero", data.interessat),
                builder.like(builder.concat("registre.interessats.nom", "registre.interessats.llinatge1", "registre.interessats.llinatge2"), data.interessat),
                builder.like("registre.interessats.raoSocial", data.interessat),
                builder.like("registre.interessats.organCodi", data.interessat)
            )
        ),
    )
}

const AnotacioFilter = (props: any) => {
    const {onSpringFilterChange} = props;

    return <StyledMuiFilter
        resourceName={"expedientPeticioResource"}
        code={"ANOTACIO_FILTER"}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
		filterOnFieldEnterKeyPressed
        advancedSearch
    >
        <AnotacioFilterForm/>
    </StyledMuiFilter>
}

export default AnotacioFilter;
