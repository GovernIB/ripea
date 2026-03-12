import StyledMuiFilter from "../../components/StyledMuiFilter.tsx";
import * as builder from '../../util/springFilterUtils';
import GridFormField from "../../components/GridFormField.tsx";
import {useFormContext} from "reactlib";

const TasquesFilterForm = () => {
    const {data} = useFormContext()

    const expedientFilter = builder.and(builder.eq("metaExpedient.id", data?.metaExpedient?.id))

    return <>
        {(!data?.advanced) && <>
            <GridFormField xs={3} name="metaExpedient"/>
            <GridFormField xs={2} name="titol"/>
            <GridFormField xs={2} name="prioritat"/>
            <GridFormField xs={2.5} name="estat" multiple/>
        </>}
        {(data?.advanced) && <>
            <GridFormField xs={3} name="metaExpedient"/>
            <GridFormField xs={3} name="expedient" filter={expedientFilter}/>
            <GridFormField xs={2} name="metaExpedientTasca"/>
            <GridFormField xs={2} name="titol"/>
            <GridFormField xs={2} name="prioritat"/>
            <GridFormField xs={1.5} name="dataInici" type={"date"}/>
            <GridFormField xs={1.5} name="dataFi" type={"date"}/>
            <GridFormField xs={1.5} name="dataLimitInici" type={"date"}/>
            <GridFormField xs={1.5} name="dataLimitFi" type={"date"}/>
            <GridFormField xs={3.5} name="estats" multiple/>
        </>}
    </>
}

const springFilterBuilder = (data:any) => {
    return builder.and(
        builder.eq("expedient.metaExpedient.id", data?.metaExpedient?.id),
        builder.eq("expedient.id", data?.expedient?.id),
        builder.eq("metaExpedientTasca.id", data?.metaExpedientTasca?.id),
        builder.like("titol", data?.titol),
        builder.eq("prioritat", `'${data?.prioritat}'`),
        builder.betweenDates("dataInici", data?.dataInici, data?.dataFi),
        builder.betweenDates("dataLimit", data?.dataLimitInici, data?.dataLimitFi),
        builder.inside("estat", ...(data?.estats?.map?.((v:any)=>`'${v}'`) ?? [])),
        builder.eq("expedient.esborrat", 0)
    );
}

const TasquesFilter = (props:any) => {
    const {onSpringFilterChange} = props;
    return <StyledMuiFilter
        resourceName={"expedientTascaResource"}
        code={"TASCA_FILTER"}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
        filterOnFieldEnterKeyPressed
        advancedSearch
    >
        <TasquesFilterForm/>
    </StyledMuiFilter>
}
export default TasquesFilter;