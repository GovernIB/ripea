import {
    useFilterApiRef, useFormApiRef,
} from 'reactlib';
import {Grid} from "@mui/material";
import {formatIso} from '../../util/dateUtils';
import * as builder from '../../util/springFilterUtils';
import GridFormField from "../../components/GridFormField.tsx";
import {useUserSession} from "../../components/Session.tsx";
import StyledMuiFilter from "../../components/StyledMuiFilter.tsx";

const ExpedientFilterForm = (props:any) => {
    const { user } = props;

    return <>
        <GridFormField xs={2} name="numero"/>
        <GridFormField xs={4} name="nom"/>
        <GridFormField xs={3} name="estat"/>
        <GridFormField xs={3} name="interessat"/>
        <GridFormField xs={3} name="organGestor"/>
        <GridFormField xs={3} name="metaExpedient"/>
        <GridFormField xs={3} name="dataCreacioInici"/>
        <GridFormField xs={3} name="dataCreacioFinal"/>

        <GridFormField xs={2} name="numeroRegistre"/>
        <GridFormField xs={3} name="grup"/>
        <GridFormField xs={3} name="agafatPer" hidden={user?.rolActual == "tothom"}/>

        <Grid item xs={user?.rolActual == "tothom" ?6 :4}></Grid>
        <GridFormField xs={2} name="agafat" type={"checkbox"}/>
        <GridFormField xs={2} name="pendentFirmar" type={"checkbox"}/>
        <GridFormField xs={2} name="seguit" type={"checkbox"} hidden={user?.rolActual != "tothom"}/>
    </>
}

const springFilterBuilder = (data: any) :string => {
    let filterStr :string = '';
    filterStr += builder.and(
        builder.like("numero", data.numero),
        builder.like("nom", data.nom),
        data.estat && builder.equals("estat",`'TANCAT'`, (data.estat==='TANCAT')),
        builder.exists(
            builder.or(
                builder.like("interessats.documentNum", data.interessat),
                builder.like(builder.concat("interessats.nom", "interessats.llinatge1", "interessats.llinatge2"), data.interessat),
                builder.like("interessats.raoSocial", data.interessat),
                builder.like("interessats.organNom", data.interessat)
            )
        ),
        builder.eq("organGestor.id", data.organGestor?.id),
        builder.eq("metaExpedient.id", data.metaExpedient?.id),

        builder.between("createdDate", `'${formatIso(data.dataCreacioInici)}'`, `'${formatIso(data.dataCreacioFinal)}'`),

        builder.like("registresImportats", data.numeroRegistre),
        builder.eq("grup.codi", data.grup?.id),
        builder.eq("agafatPer.codi", `'${data.agafatPer?.id}'`),


        data.pendentFirmar && (
            builder.exists(
                builder.and(
                    builder.or(
                        builder.eq("portafirmes.estat", `'PENDENT'`),
                        builder.eq("portafirmes.estat", `'ENVIAT'`),
                    ),
                    builder.neq("portafirmes.error", true),
                )
            )
        ),

    )
    // console.log('>>> springFilterBuilder:', filterStr)
    return filterStr;
}

const ExpedientFilter = (props:any) => {
    const {onSpringFilterChange} = props;
    const filterRef = useFilterApiRef();
    const formApiRef = useFormApiRef();

    const { value: user } = useUserSession();

    const additionalSpringFilterBuilder = (data: any) :string => {
        return builder.and(
            springFilterBuilder(data),
            data.agafat && builder.eq("agafatPer.codi", `'${user.codi}'`),
            data.seguit && (
                builder.exists(
                    builder.eq("seguidors.codi", `'${user.codi}'`)
                )
            )
        )
    }

    return <StyledMuiFilter
        resourceName={"expedientResource"}
        code={"EXPEDIENT_FILTER"}
        apiRef={filterRef}
        formApiRef={formApiRef}
        componentProps={{ style: {minHeight: '206px' } }}
        springFilterBuilder={additionalSpringFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
    >
        <ExpedientFilterForm user={user}/>
    </StyledMuiFilter>
}

export default ExpedientFilter;
