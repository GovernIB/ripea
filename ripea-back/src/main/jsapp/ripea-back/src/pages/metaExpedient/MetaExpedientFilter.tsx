import {useFormContext} from "reactlib";
import GridFormField, {GridButtonField} from "../../components/GridFormField.tsx";
import StyledMuiFilter from "../../components/StyledMuiFilter.tsx";
import * as builder from "../../util/springFilterUtils.ts";
import {Grid} from "@mui/material";

const MetaExpedientFilterForm = () => {
    const { data } = useFormContext();
    return <>
        <GridFormField xs={2} name="tipus"/>
        <GridFormField xs={2} name="codi"/>
        <GridFormField xs={3} name="nom"/>
        <GridFormField xs={3} name="classificacio"/>
        <GridFormField xs={2} name="actiu"/>
        <GridFormField xs={2} name="revisioEstat"/>
        <GridFormField xs={2} name="ambit"/>
        <GridFormField xs={3} name="organGestor" disabled={data?.ambit == 'COMUNS'}/>
        <Grid xs={2}/>
        <GridButtonField xs={0.6} name="permisDirecte" icon={"pan_tool_alt"}/>
    </>
}

const springFilterBuilder = (data:any) => {
    return builder.and(
        builder.like('codi', data?.codi),
        builder.like('nom', data?.nom),
        builder.like('classificacio', data?.classificacio),
        builder.eq('organGestor.id', data?.organGestor?.id),
        data?.actiu && builder.eq('actiu', data?.actiu),
        data?.revisioEstat && builder.eq('revisioEstat', `'${data?.revisioEstat}'`),
        data?.tipus && builder.eq('tipusProcedimentServei', `'${data?.tipus}'`),
        data?.permisDirecte && builder.eq('permisDirecte', data?.permisDirecte),
        data?.ambit && builder.equals('organGestor.id', null, data?.ambit == 'COMUNS'),
    );
}

export const MetaExpedientFilter = (props: any) => {
    // const {onSpringFilterChange} = props;

    return <StyledMuiFilter
        resourceName={"metaExpedientResource"}
        code={"FILTER_GESTIO"}
        springFilterBuilder={springFilterBuilder}
        {...props}
    >
        <MetaExpedientFilterForm/>
    </StyledMuiFilter>
}