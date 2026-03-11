import * as builder from "../../util/springFilterUtils.ts";
import StyledMuiFilter from "../../components/StyledMuiFilter.tsx";
import GridFormField from "../../components/GridFormField.tsx";


const OrganGestorFilterForm = () => {
    return <>
        <GridFormField xs={2} name="codi"/>
        <GridFormField xs={2} name="nom"/>
        <GridFormField xs={3} name="organGestor"/>
        <GridFormField xs={2} name="estat"/>
    </>
}

const springFilterBuilder = (data: any) => {
    return builder.and(
        builder.like('codi', data?.codi),
        builder.like('nom', data?.nom),
        builder.eq('pare.id', data?.organGestor?.id),
        builder.eq('estat', `'${data?.estat}'`),
    );
}

export const OrganGestorFilter = (props: any) => {
    const {onSpringFilterChange} = props;

    return <StyledMuiFilter
        resourceName={"organGestorResource"}
        code={"FILTER"}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
    >
        <OrganGestorFilterForm/>
    </StyledMuiFilter>
}