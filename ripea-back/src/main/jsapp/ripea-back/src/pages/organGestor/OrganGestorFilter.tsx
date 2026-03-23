import * as builder from "../../util/springFilterUtils.ts";
import StyledMuiFilter from "../../components/StyledMuiFilter.tsx";
import GridFormField from "../../components/GridFormField.tsx";


const OrganGestorFilterForm = () => {
    return <>
        <GridFormField size={{xs: 12, sm: 6, md: 2}} name="codi"/>
        <GridFormField size={{xs: 12, sm: 6, md: 2.6}} name="nom"/>
        <GridFormField size={{xs: 12, sm: 12, md: 3}} name="organGestor"/>
        <GridFormField size={{xs: 12, sm: 6, md: 2}} name="estat"/>
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