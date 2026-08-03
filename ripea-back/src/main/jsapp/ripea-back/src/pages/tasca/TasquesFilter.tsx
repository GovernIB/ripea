import StyledMuiFilter, {FILTER_ADVANCED_ICON_ONLY_BREAKPOINT} from "../../components/StyledMuiFilter.tsx";
import * as builder from '../../util/springFilterUtils';
import GridFormField from "../../components/GridFormField.tsx";
import {useFormContext} from "reactlib";

const TasquesFilterForm = () => {
    const {data} = useFormContext()

    const procedimentFilter = builder.and(builder.eq("metaExpedient.id", data?.metaExpedient?.id))

    return <>
        {(!data?.advanced) && <>
            <GridFormField size={{xs: 12, sm: 6, md: 2}} name="metaExpedient"/>
			<GridFormField size={{xs: 12, sm: 6, md: 2}} name="metaExpedientTasca" filter={procedimentFilter}/>
            <GridFormField size={{xs: 12, sm: 6, md: 2}} name="titol"/>
            <GridFormField size={{xs: 12, sm: 6, md: 2}} name="prioritat"/>
            <GridFormField size={{xs: 12, sm: 6, md: 2}} name="estats" multiple/>
        </>}
        {(data?.advanced) && <>
            <GridFormField size={{xs: 12, sm: 6, md: 2}} name="metaExpedient"/>
            <GridFormField size={{xs: 12, sm: 6, md: 2}} name="expedient" filter={procedimentFilter}/>
            <GridFormField size={{xs: 12, sm: 4, md: 2}} name="metaExpedientTasca" filter={procedimentFilter}/>
            <GridFormField size={{xs: 12, sm: 4, md: 2}} name="titol"/>
            <GridFormField size={{xs: 12, sm: 4, md: 2}} name="prioritat"/>
            <GridFormField size={{xs: 12, sm: 6, md: 2}} name="dataInici" type={"date"}/>
            <GridFormField size={{xs: 12, sm: 6, md: 2}} name="dataFi" type={"date"}/>
            <GridFormField size={{xs: 12, sm: 6, md: 2}} name="dataLimitInici" type={"date"}/>
            <GridFormField size={{xs: 12, sm: 6, md: 2}} name="dataLimitFi" type={"date"}/>
            <GridFormField size={{xs: 12, sm: 6, md: 2}} name="estats" multiple/>
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
    return <StyledMuiFilter
        resourceName={"expedientTascaResource"}
        code={"TASCA_FILTER"}
        springFilterBuilder={springFilterBuilder}
        filterOnFieldEnterKeyPressed
        advancedSearch
        buttonGridProps={{size: {xs: 12, sm: 6, md: 2}}}
        buttonIconOnlyBreakpoint={FILTER_ADVANCED_ICON_ONLY_BREAKPOINT}
        {...props}
    >
        <TasquesFilterForm/>
    </StyledMuiFilter>
}
export default TasquesFilter;
