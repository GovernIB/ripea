import GridFormField from "../../components/GridFormField.tsx";
import StyledMuiFilter from "../../components/StyledMuiFilter.tsx";
import * as builder from '../../util/springFilterUtils';
import {formatEndOfDay} from "../../util/dateUtils.ts";

const AnotacioFilterForm = () => {
    return <>
        <GridFormField xs={4} name="numRegistre"/>
        <GridFormField xs={4} name="extracte"/>
        <GridFormField xs={4} name="destinacio"/>
        <GridFormField xs={4} name="metaExpedient"/>
        <GridFormField xs={4} name="dataRecepcioInicial" type={"date"}/>
        <GridFormField xs={4} name="dataRecepcioFinal" type={"date"}/>
        <GridFormField xs={4} name="estat"/>
        <GridFormField xs={4} name="interessat"/>
    </>
}

const springFilterBuilder = (data: any): string => {
    let filterStr: string = '';
    filterStr += builder.and(
        builder.like("identificador", data.numRegistre),
        builder.like("registre.extracte", data.extracte),
        builder.like("registre.destiCodiINom", data.destinacio),
        builder.like("metaExpedient", data.metaExpedient),
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
    // console.log('>>> springFilterBuilder:', filterStr)
    return filterStr;
}

const AnotacioFilter = (props: any) => {
    const {onSpringFilterChange} = props;

    return <StyledMuiFilter
        resourceName={"expedientPeticioResource"}
        code={"ANOTACIO_FILTER"}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
		filterOnFieldEnterKeyPressed
    >
        <AnotacioFilterForm/>
    </StyledMuiFilter>
}

export default AnotacioFilter;
