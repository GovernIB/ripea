import {
    MuiFilter,
    useFilterApiRef,
} from 'reactlib';
import {Button, Grid, Icon} from "@mui/material";
import {formatIso} from '../../util/dateUtils';
import * as builder from '../../util/springFilterUtils';
import GridFormField from "../../components/GridFormField.tsx";
import {useState} from "react";

const springFilterBuilder = (data: any) :string => {
    let filterStr :string = '';
    filterStr += builder.and(
        builder.like("numero", data?.numero),
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

        data.agafat && builder.equals("agafatPer", null, (data.agafat === 'false')),
        // data.pendentFirmar && (
        //     builder.exists(
        //         builder.and(
        //             builder.or(
        //                 builder.equals("DocumentPortafirmesEntity.estat", `'PENDENT'`, (data.pendentFirmar === 'true')),
        //                 builder.equals("DocumentPortafirmesEntity.estat", `'ENVIAT'`, (data.pendentFirmar === 'true')),
        //             ),
        //             builder.equals("DocumentPortafirmesEntity.error", false, (data.pendentFirmar === 'true')),
        //         )
        //     )
        // )
    )
    // console.log('>>> springFilterBuilder:', filterStr)
    return filterStr;
}

const useExpedientFilter = () => {
    const filterRef = useFilterApiRef();
    const [springFilter, setSpringFilter] = useState<string>(
        // springFilterBuilder({
        //     estat: 'OBERT',
        //     dataCreacioInici: new Date(),
        // })
    );

    const cercar = ()=> {
        filterRef.current.filter()
    }
    const netejar = ()=> {
        filterRef.current.clear()
    }

    const content = <MuiFilter
        resourceName="expedientResource"
        code="EXPEDIENT_FILTER"
        springFilterBuilder={springFilterBuilder}
        commonFieldComponentProps={{size: 'small'}}
        componentProps={{
            sx: {mb: 3, p: 2, backgroundColor: '#f5f5f5', border: '1px solid #e3e3e3', borderRadius: '10px'}
        }}
        apiRef={filterRef}
        onSpringFilterChange={(value?:string)=>value && setSpringFilter(value)}
        buttonControlled
    >
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
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
            <GridFormField xs={3} name="agafatPer"/>

            <GridFormField xs={2} name="agafat"/>
            <GridFormField xs={2} name="pendentFirmar"/>

            <Grid item xs={12} sx={{ display: 'flex', justifyContent: 'end' }}>
                <Button onClick={netejar}><Icon>eraser</Icon>Netejar</Button>
                <Button onClick={cercar} variant="contained" sx={{borderRadius: 1}}><Icon>filter_alt</Icon> Cercar</Button>
            </Grid>
        </Grid>
    </MuiFilter>

    return {
        springFilter,
        content
    }
}

export default useExpedientFilter;