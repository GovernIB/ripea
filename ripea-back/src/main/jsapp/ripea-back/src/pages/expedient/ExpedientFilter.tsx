import {
    MuiFilter,
    useFilterApiRef, useFormApiRef,
} from 'reactlib';
import {Button, Grid, Icon} from "@mui/material";
import {formatIso} from '../../util/dateUtils';
import * as builder from '../../util/springFilterUtils';
import GridFormField from "../../components/GridFormField.tsx";
import {useSession} from "../../components/SessionStorageContext.tsx";
import {useEffect} from "react";
import {useUserSession} from "../../components/Session.tsx";

const filterStyle = {mb: 2, p: 2, backgroundColor: '#f5f5f5', border: '1px solid #e3e3e3', borderRadius: '4px'}

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

const ExpedientFilter = (props:any) => {
    const {onSpringFilterChange} = props;
    const filterRef = useFilterApiRef();
    const formApiRef = useFormApiRef();

    const { value: user } = useUserSession();
    const { value: filterData, save: saveFilterData } = useSession('EXPEDIENT_FILTER');

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

            data.agafat && builder.eq("agafatPer.codi", `'${user.codi}'`),
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
            data.seguit && (
                builder.exists(
                    builder.eq("seguidors.codi", `'${user.codi}'`)
                )
            )
        )
        console.log('>>> springFilterBuilder:', filterStr)
        return filterStr;
    }

    const cercar = ()=> {
        filterRef.current.filter()
        saveFilterData(formApiRef.current.getData())
    }
    const netejar = ()=> {
        filterRef.current.clear()
        saveFilterData(formApiRef.current.getData())
    }

    useEffect(() => {
        if (!!filterData) {
            onSpringFilterChange(
                springFilterBuilder(filterData)
            )
        }
    }, []);

    return <MuiFilter
        resourceName={"expedientResource"}
        code={"EXPEDIENT_FILTER"}
        apiRef={filterRef}
        formApiRef={formApiRef}
        commonFieldComponentProps={{ size: 'small' }}
        componentProps={{ sx: { ...filterStyle, minHeight: '206px' } }}
        additionalData={filterData}
        onDataChange={(data) => {
            if (data && Object.keys(data).length > 0 && !filterData) {
                cercar()
            }
        }}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
        buttonControlled
    >
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            <ExpedientFilterForm user={user}/>

            <Grid item xs={user?.rolActual != "tothom" ?8 :6} sx={{ display: 'flex', justifyContent: 'end' }}>
                <Button variant="outlined" sx={{borderRadius: 1}} onClick={netejar}>Netejar</Button>
                <Button onClick={cercar} variant="contained" sx={{borderRadius: 1}}><Icon>filter_alt</Icon>Filtrar</Button>
            </Grid>
        </Grid>
    </MuiFilter>
}

export default ExpedientFilter;
