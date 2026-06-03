import {Grid} from "@mui/material";
import {useMemo} from "react";
import {useFormContext} from 'reactlib';
import GridFormField, {GridButtonField} from "../../components/GridFormField.tsx";
import {useUserSession} from "../../components/Session.tsx";
import StyledMuiFilter from "../../components/StyledMuiFilter.tsx";
import * as builder from '../../util/springFilterUtils';

const ExpedientFilterForm = () => {
    const {data} = useFormContext()
    const { value: user, rol } = useUserSession();

    const filterMetaExpedient = useMemo(() =>
        builder.and(builder.eq('organGestor.id', data?.organGestor?.id)),
    [data?.organGestor?.id]);

    const requestParamsEstat = useMemo(() =>
        ({metaExpedientId: data?.metaExpedient?.id}),
    [data?.metaExpedient?.id]);

    const requestParamsDominiValor = useMemo(() =>
        ({domini: data?.domini?.id}),
    [data?.domini?.id]);

    const mostrarGrups = user?.sessionScope?.isFiltreGrupsVisible;

    return <>
        {(!data?.advanced) && <>
            <GridFormField size={{xs: 12, sm: 6, md: 2}} name="numero"/>
            <GridFormField size={{xs: 12, sm: 6, md: 2}} name="nom"/>
            <GridFormField size={{xs: 12, sm: 6, md: 2}} name="metaExpedient" filter={filterMetaExpedient}/>
            <GridFormField size={{xs: 12, sm: 6, md: 2}} name="estatCustom" requestParams={requestParamsEstat} />
            <GridFormField size={{xs: 12, sm: 6, md: 2}} name="dataCreacioInici"/>
        </>}
        {(data?.advanced) && <>
            <GridFormField size={{xs: 12, sm: 6, md: 3}} name="numero"/>
            <GridFormField size={{xs: 12, sm: 6, md: 3}} name="nom"/>
            <GridFormField size={{xs: 12, sm: 6, md: 3}} name="estatCustom" requestParams={requestParamsEstat} />
            <GridFormField size={{xs: 12, sm: 6, md: 3}} name="interessat"/>
            <GridFormField size={{xs: 12, sm: 6, md: 3}} name="organGestor" />
            <GridFormField size={{xs: 12, sm: 6, md: 3}} name="metaExpedient" filter={filterMetaExpedient}/>
            <GridFormField size={{xs: 12, sm: 6, md: 3}} name="dataCreacioInici"/>
            <GridFormField size={{xs: 12, sm: 6, md: 3}} name="dataCreacioFinal"/>

            <GridFormField size={{xs: 12, sm: 6, md: 3}} name="domini" hidden={!user?.sessionScope?.isDominisEnabled}/>
            <GridFormField size={{xs: 12, sm: 6, md: 3}} name="dominiValor"
                           requestParams={requestParamsDominiValor}
                           disabled={!data?.domini}
                           reanOnly={!data?.domini}
                           hidden={!user?.sessionScope?.isDominisEnabled}/>

            <GridFormField size={{xs: 12, sm: (12 / (1 + (mostrarGrups ?1: 0) + (!rol?.isUser ?1 :0))), md: 2}} name="numeroRegistre"/>
            <GridFormField size={{xs: 12, sm: (12 / (1 + (mostrarGrups ?1: 0) + (!rol?.isUser ?1 :0))), md: 2}} name="grup" hidden={!mostrarGrups}/>
            <GridFormField size={{xs: 12, sm: (12 / (1 + (mostrarGrups ?1: 0) + (!rol?.isUser ?1 :0))), md: 2}} name="agafatPer" hidden={rol?.isUser}/>
                <Grid size={{xs: 12, sm: 12, md: 2}} hidden={mostrarGrups}/>
                <Grid size={{xs: 12, sm: 12, md: 2}} hidden={!rol?.isUser}/>
                <Grid size={{xs: 12, sm: 12, md: 6}} hidden={user?.sessionScope?.isDominisEnabled}/>

            <GridButtonField size={{xs: rol?.isUser ?4 :6, sm: 1.5}} name={'agafat'} icon={'lock'} whitLabel/>
            <GridButtonField size={{xs: rol?.isUser ?4 :6, sm: 1.5}} name={'pendentFirmar'} icon={'edit'} whitLabel/>
            <GridButtonField size={{xs: 4, sm: 1.5}} name={'seguit'} icon={'group_add'} hidden={!rol?.isUser} whitLabel/>
        </>}
    </>
}

export const springFilterBuilder = (data: any, user?: any, rol?: any): string => {
    let filterStr: string = '';
//	console.log('>>> data:', JSON.stringify(data, null, 2));
    filterStr += builder.and(
        builder.like("numero", data.numero),
        builder.like("nom", data.nom),
        data.estatCustom && builder.equals("estat", `'TANCAT'`, (data.estatCustom === '-1')),
        data.estatCustom && (data.estatCustom != '0' && data.estatCustom != '-1') && builder.eq("estatAdditional.id", data.estatCustom),
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

        builder.betweenDates("createdDate", data.dataCreacioInici, data.dataCreacioFinal),

        data?.dominiValor && builder.exists(
            builder.like("dades.valor", data?.dominiValor)
        ),

        builder.like("registresImportats", data.numeroRegistre),
        builder.eq("grup.id", data.grup?.id),
        (!rol?.isUser) && builder.eq("agafatPer.codi", `'${data.agafatPer?.id}'`),

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
        (rol?.isUser) && data.seguit && (
            builder.exists(
                builder.eq("seguidors.codi", `'${user.codi}'`)
            )
        )
    )
//    console.log('>>> springFilterBuilder:', filterStr)
    return filterStr;
}

const ExpedientFilter = (props: any) => {
    const {onSpringFilterChange} = props;
    const {value: user, rol} = useUserSession();
    return <StyledMuiFilter
        resourceName={"expedientResource"}
        code={"EXPEDIENT_FILTER"}
        springFilterBuilder={(data: any)=> (
            springFilterBuilder(data, user, rol)
        )}
        onSpringFilterChange={onSpringFilterChange}
        advancedSearch
        buttonGridProps={{size: {xs: 12, sm: 6, md: 2}}}
        buttonIconOnlyBreakpoint={'xl'}
    >
        <ExpedientFilterForm/>
    </StyledMuiFilter>
}

export default ExpedientFilter;
