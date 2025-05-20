import {Grid} from "@mui/material";
import {useFormContext,} from 'reactlib';
import GridFormField, {GridButtonField} from "../../components/GridFormField.tsx";
import {useUserSession} from "../../components/Session.tsx";
import StyledMuiFilter from "../../components/StyledMuiFilter.tsx";
import {formatIso} from '../../util/dateUtils';
import * as builder from '../../util/springFilterUtils';

const ExpedientFilterForm = (props:any) => {
    const {data} = useFormContext()
    const { user } = props;

    const filterMetaExpedient = builder.and(
        builder.eq('organGestor.id', data?.organGestor?.id),
        builder.eq('actiu', true),
        builder.eq('revisioEstat', "'REVISAT'"),
    );

    const filtErestatAdditionalInfo = builder.and(
        builder.eq('metaExpedient.id', data?.metaExpedient?.id),
    );

    return <>
        <GridFormField xs={3} name="numero"/>
        <GridFormField xs={3} name="nom"/>
        <GridFormField xs={3} name="estat"/>
        <GridFormField xs={3} name="estatAdditionalInfo" filter={filtErestatAdditionalInfo} hidden/>
        <GridFormField xs={3} name="interessat"/>
        <GridFormField xs={3} name="organGestor"/>
        <GridFormField xs={3} name="metaExpedient" filter={filterMetaExpedient}/>
        <GridFormField xs={3} name="dataCreacioInici"/>
        <GridFormField xs={3} name="dataCreacioFinal"/>

        <GridFormField xs={3} name="numeroRegistre"/>
        <GridFormField xs={3} name="grup"/>
        <GridFormField xs={3} name="agafatPer" hidden={user?.rolActual == "tothom"}/>

        <Grid item xs={user?.rolActual == "tothom" ?6 :3}></Grid>

        <GridButtonField xs={1} name={'agafat'} icon={'lock'}/>
        <GridButtonField xs={1} name={'pendentFirmar'} icon={'edit'}/>
        <GridButtonField xs={1} name={'seguit'} icon={'group_add'} hidden={user?.rolActual != "tothom"}/>
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

    const { value: user } = useUserSession();

    const additionalSpringFilterBuilder = (data: any) :string => {
        return builder.and(
            springFilterBuilder(data),
            (user?.rolActual != "tothom") && data.agafat && builder.eq("agafatPer.codi", `'${user.codi}'`),
            (user?.rolActual == "tothom") && data.seguit && (
                builder.exists(
                    builder.eq("seguidors.codi", `'${user.codi}'`)
                )
            )
        )
    }

    return <StyledMuiFilter
        resourceName={"expedientResource"}
        code={"EXPEDIENT_FILTER"}
        componentProps={{ style: {minHeight: '206px' } }}
        springFilterBuilder={additionalSpringFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
    >
        <ExpedientFilterForm user={user}/>
    </StyledMuiFilter>
}

export default ExpedientFilter;
