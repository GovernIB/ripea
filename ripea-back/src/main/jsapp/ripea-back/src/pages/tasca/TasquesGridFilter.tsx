import StyledMuiFilter from "../../components/StyledMuiFilter.tsx";
import * as builder from '../../util/springFilterUtils';
import GridFormField from "../../components/GridFormField.tsx";
import {useFormContext} from "reactlib";

const TasquesGridFilterForm = () => {
    const {data} = useFormContext()

    return <>
        <GridFormField xs={3} name="metaExpedient"/>
        <GridFormField xs={3} name="expedient" filter={builder.and(builder.eq("metaExpedient", data?.metaExpedient))}/>
        <GridFormField xs={2} name="metaExpedientTasca"/>
        <GridFormField xs={2} name="titol"/>
        <GridFormField xs={2} name="prioritat"/>
        <GridFormField xs={2} name="dataInici" type={"date"}/>
        <GridFormField xs={2} name="dataFi" type={"date"}/>
        <GridFormField xs={2} name="dataLimitInici" type={"date"}/>
        <GridFormField xs={2} name="dataLimitFi" type={"date"}/>
        <GridFormField xs={4} name="estat" multiple/>
    </>
}

const springFilterBuilder = (data:any) => {
    const filterStr: string = builder.and(
        builder.eq("metaExpedient.id", data?.metaExpedient?.id),
        builder.eq("expedient.id", data?.expedient?.id),
        builder.eq("metaExpedientTasca.id", data?.metaExpedientTasca?.id),
        builder.like("titol", data?.titol),
        builder.eq("prioritat", `${data?.prioritat}`),
        builder.betweenDates("dataInici", data?.dataInici, data?.dataFi),
        builder.betweenDates("dataLimit", data?.dataLimitInici, data?.dataLimitFi),
        builder.inside("estat", ...(data?.estat.map((v:any)=>`'${v}'`) ?? []))
    );
    // console.log('>>> springFilterBuilder:', filterStr)
    return filterStr;
}

const TasquesGridFilter = (props:any) => {
    const {onSpringFilterChange} = props;
    return <StyledMuiFilter
        resourceName={"expedientTascaResource"}
        code={"TASCA_FILTER"}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
    >
        <TasquesGridFilterForm/>
    </StyledMuiFilter>
}
export default TasquesGridFilter;