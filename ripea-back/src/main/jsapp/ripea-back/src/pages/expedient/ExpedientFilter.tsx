import {Grid} from "@mui/material";
import {useFormContext} from 'reactlib';
import GridFormField, {GridButtonField} from "../../components/GridFormField.tsx";
import {useUserSession} from "../../components/Session.tsx";
import StyledMuiFilter from "../../components/StyledMuiFilter.tsx";
import * as builder from '../../util/springFilterUtils';

const ExpedientFilterForm = () => {
    const {data} = useFormContext()
    const { value: user, rol } = useUserSession();

    if (!data?.advanced) {
        return <>
            <GridFormField xs={2.4} name="numero"/>
            <GridFormField xs={2.4} name="estat" requestParams={{metaExpedientId: data?.metaExpedient?.id}} />
            <GridFormField xs={2.4} name="dataCreacioInici"/>
            <GridFormField xs={2.4} name="dataCreacioFinal"/>
        </>
    }

    const filterMetaExpedient = builder.and(
        builder.eq('organGestor.id', data?.organGestor?.id),
    );

    return <>
        <GridFormField xs={3} name="numero"/>
        <GridFormField xs={3} name="nom"/>
        <GridFormField xs={3} name="estat" requestParams={{metaExpedientId: data?.metaExpedient?.id}} />
        <GridFormField xs={3} name="interessat"/>
        <GridFormField xs={3} name="organGestor" />
        <GridFormField xs={3} name="metaExpedient" filter={filterMetaExpedient}/>
        <GridFormField xs={3} name="dataCreacioInici"/>
        <GridFormField xs={3} name="dataCreacioFinal"/>

        <GridFormField xs={3} name="domini" hidden={!user?.sessionScope?.isDominisEnabled}/>
        <GridFormField xs={3} name="dominiValor"
                       requestParams={{domini: data?.domini?.id}}
                       disabled={!data?.domini}
                       reanOnly={!data?.domini}
                       hidden={!user?.sessionScope?.isDominisEnabled}/>

        <GridFormField xs={2} name="numeroRegistre"/>
        <GridFormField xs={2} name="grup" hidden={!user?.sessionScope?.isFiltreGrupsVisible}/>
        <GridFormField xs={2} name="agafatPer" hidden={rol?.isUser}/>
            <Grid item xs={2} hidden={user?.sessionScope?.isFiltreGrupsVisible}/>
            <Grid item xs={2} hidden={!rol?.isUser}/>
            <Grid item xs={6} hidden={user?.sessionScope?.isDominisEnabled}/>

        <GridButtonField xs={1.5} name={'agafat'} icon={'lock'} whitLabel/>
        <GridButtonField xs={1.5} name={'pendentFirmar'} icon={'edit'} whitLabel/>
        <GridButtonField xs={1.5} name={'seguit'} icon={'group_add'} hidden={!rol?.isUser} whitLabel/>
    </>
}

export const springFilterBuilder = (data: any, user?: any, rol?: any): string => {
    let filterStr: string = '';
//	console.log('>>> data:', JSON.stringify(data, null, 2));
    filterStr += builder.and(
        builder.like("numero", data.numero),
        builder.like("nom", data.nom),
		data.estat && (
		    (data.estat === 'OBERT' || data.estat === '0')
		        ? builder.neq("estat", `'TANCAT'`)
		        : (data.estat === 'TANCAT' || data.estat === '-1')
		            ? builder.eq("estat", `'TANCAT'`)
		            : data.metaExpedient?.id && builder.eq("estatAdditional.id", data.estat)
		),
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
        filterOnFieldEnterKeyPressed
    >
        <ExpedientFilterForm/>
    </StyledMuiFilter>
}

export default ExpedientFilter;
