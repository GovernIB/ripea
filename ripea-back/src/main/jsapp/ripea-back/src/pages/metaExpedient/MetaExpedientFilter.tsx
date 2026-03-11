import {useFormContext} from "reactlib";
import GridFormField, {GridButtonField} from "../../components/GridFormField.tsx";
import StyledMuiFilter from "../../components/StyledMuiFilter.tsx";
import * as builder from "../../util/springFilterUtils.ts";
import {Grid} from "@mui/material";
import {useUserSession} from "../../components/Session.tsx";

const MetaExpedientFilterForm = ({ user }:any) => {
    const { data } = useFormContext();
    return <>
        <GridFormField xs={2} name="tipus"/>
        <GridFormField xs={2} name="codi"/>
        <GridFormField xs={3} name="nom"/>
        <GridFormField xs={3} name="classificacio"/>
        <GridFormField xs={2} name="actiu"/>
        <GridFormField xs={2} name="revisioEstat" hidden={!user?.sessionScope?.isRevisioActiva}/>
        <GridFormField xs={2} name="ambit"/>
        <GridFormField xs={3} name="organGestor" disabled={data?.ambit == 'COMUNS'}/>
        <GridButtonField xs={1.6}
            name="permisDirecte"
            icon={"pan_tool_alt"}
            whitLabel
            iconSx={{ transform: 'rotate(180deg)' }}/>
        <Grid xs={2} hidden={user?.sessionScope?.isRevisioActiva}/>
        <Grid xs={1}/>
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
    const {value: user} = useUserSession()

    return <StyledMuiFilter
        resourceName={"metaExpedientResource"}
        code={"FILTER_GESTIO"}
        springFilterBuilder={springFilterBuilder}
        {...props}
    >
        <MetaExpedientFilterForm user={user}/>
    </StyledMuiFilter>
}