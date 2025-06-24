import {Grid} from "@mui/material";
import {useFormContext} from 'reactlib';
import GridFormField, {GridButtonField} from "../../components/GridFormField.tsx";
import {useUserSession} from "../../components/Session.tsx";
import StyledMuiFilter from "../../components/StyledMuiFilter.tsx";
import {formatIso} from '../../util/dateUtils';
import * as builder from '../../util/springFilterUtils';

const ExpedientFilterForm = () => {
    const {data} = useFormContext()
    const { value: user } = useUserSession();

    const filterMetaExpedient = builder.and(
        builder.eq('organGestor.id', data?.organGestor?.id),
        builder.eq('actiu', true),
        builder.eq('revisioEstat', "'REVISAT'"),
    );

    return <>
        <GridFormField xs={3} name="numero"/>
        <GridFormField xs={3} name="nom"/>
        <GridFormField xs={3} name="estat" requestParams={{metaExpedientId: data?.metaExpedient?.id}}/>
        <GridFormField xs={3} name="interessat"/>
        <GridFormField xs={3} name="organGestor"/>
        <GridFormField xs={3} name="metaExpedient" filter={filterMetaExpedient}/>
        <GridFormField xs={3} name="dataCreacioInici"/>
        <GridFormField xs={3} name="dataCreacioFinal"/>

        <GridFormField xs={3} name="numeroRegistre"/>
        <GridFormField xs={3} name="grup"/>
        <GridFormField xs={3} name="agafatPer" hidden={user?.rolActual == "tothom"}/>

        <Grid item xs={user?.rolActual == "tothom" ?6 :3}/>

        <GridButtonField xs={1} name={'agafat'} icon={'lock'}/>
        <GridButtonField xs={1} name={'pendentFirmar'} icon={'edit'}/>
        <GridButtonField xs={1} name={'seguit'} icon={'group_add'} hidden={user?.rolActual != "tothom"}/>
    </>
}

export const springFilterBuilder = (data: any, user?: any): string => {
    let filterStr: string = '';
    filterStr += builder.and(
        builder.like("numero", data.numero),
        builder.like("nom", data.nom),
        data.estat && builder.equals("estat", `'TANCAT'`, (data.estat === '-1')),
        data.estat && (data.estat != '0' && data.estat != '-1') && builder.eq("estatAdditional.id", data.estat),
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
        (user?.rolActual != "tothom") && builder.eq("agafatPer.codi", `'${data.agafatPer?.id}'`),

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

        data.agafat && builder.eq("agafatPer.codi", `'${user.codi}'`),
        (user?.rolActual == "tothom") && data.seguit && (
            builder.exists(
                builder.eq("seguidors.codi", `'${user.codi}'`)
            )
        )
    )
    // console.log('>>> springFilterBuilder:', filterStr)
    return filterStr;
}

const ExpedientFilter = (props: any) => {
    const {onSpringFilterChange} = props;
    const {value: user} = useUserSession();
    return <StyledMuiFilter
        resourceName={"expedientResource"}
        code={"EXPEDIENT_FILTER"}
        springFilterBuilder={(data: any)=>springFilterBuilder(data, user)}
        onSpringFilterChange={onSpringFilterChange}
        filterOnFieldEnterKeyPressed
    >
        <ExpedientFilterForm/>
    </StyledMuiFilter>
}

export default ExpedientFilter;
