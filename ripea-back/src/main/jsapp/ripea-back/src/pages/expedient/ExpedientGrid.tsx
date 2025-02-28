import React, {useState} from 'react';
import {
    GridPage,
    MuiGrid,
    MuiFilter,
    FormField,
    useFilterApiRef,
} from 'reactlib';
import {Button, Box, Typography, Grid} from "@mui/material";
import {formatDate} from '../../util/dateUtils';
import * as builder from '../../util/springFilterUtils';
import { Icon } from '@mui/material';

const ExpedientGrid: React.FC = () => {
    // const { t } = useTranslation();
    const [springFilter, setSpringFilter] = useState("");
    const filterRef = useFilterApiRef();

    const columns = [
        {
            field: 'numero',
            flex: 0.5,
        },
        {
            field: 'nom',
            flex: 1,
        },
        {
            field: 'avisos',
            headerName: 'Avisos',
            flex: 0.5,
            renderCell: (params: any) => (<>
                {!params.row.valid && <Icon color={"warning"} title="validacio">warning_rounded</Icon>}
                {params.row.errorLastEnviament && <Icon color={"error"} title="enviaments">mode_square</Icon>}
                {params.row.errorLastNotificacio && <Icon color={"error"} title="notificacions">email_square</Icon>}
                {params.row.ambEnviamentsPendents && <Icon color={"primary"} title="enviaments">mode_square</Icon>}
                {params.row.ambNotificacionsPendents && <Icon color={"primary"} title="notificacions">email_square</Icon>}
                {params.row.alerta && <Icon color={"error"} title="alertes">warning_circle</Icon>}
                {params.row.arxiuUuid == null && <Icon color={"error"} title="pendentGuardarArxiu">warning_triangle</Icon>}
            </>),
        },
        {
            field: 'tipusStr',
            flex: 1,
        },
        {
            field: 'createdDate',
            flex: 1,
            valueFormatter: (value: any) => {
                return formatDate(value);
            }
        },
        {
            field: 'estat',
            flex: 0.5,
        },
        {
            field: 'prioritat',
            flex: 0.5,
        },
        {
            field: 'agafatPer',
            flex: 1,
            valueFormatter: (value: any) => {
                return value?.description;
            }
        },
        {
            field: 'interessatsResum',
            flex: 1,
        },
        {
            field: 'grup',
            flex: 0.5,
            valueFormatter: (value: any) => {
                return value?.description;
            }
        },
    ];

    const springFilterBuilder = (data: any) :string => {
        let filterStr :string = '';console.log(data)
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
            builder.between("createdDate", `'${data.dataCreacioInici}'`, `'${data.dataCreacioFinal}'`),

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
        console.log('>>> springFilterBuilder:', filterStr)
        return filterStr;
    }
    const cercar = ()=> {
        filterRef.current.filter()
    }
    const netejar = ()=> {
        filterRef.current.clear()
    }
    const fieldProps={sx: {backgroundColor: 'white'}}
    return <GridPage>
        <div style={{border: '1px solid #e3e3e3'}}>
            <Box sx={{backgroundColor: '#f5f5f5', borderBottom: '1px solid #e3e3e3', p: 1}}>
                <Typography variant="h5">Buscador de expedientes</Typography>
            </Box>
            <MuiFilter
                resourceName="expedientResource"
                code="EXPEDIENT_FILTER"
                springFilterBuilder={springFilterBuilder}
                commonFieldComponentProps={{size: 'small'}}
                componentProps={{
                    sx: {m: 3, p: 2, backgroundColor: '#f5f5f5', border: '1px solid #e3e3e3', borderRadius: 1}
                }}
                apiRef={filterRef}
                onSpringFilterChange={setSpringFilter}
                buttonControlled
                container
            >
                <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
                    <Grid item xs={2}><FormField name="numero" componentProps={fieldProps}/></Grid>
                    <Grid item xs={4}><FormField name="nom" componentProps={fieldProps}/></Grid>
                    <Grid item xs={3}><FormField name="estat" componentProps={fieldProps}/></Grid>
                    <Grid item xs={3}><FormField name="interessat" componentProps={fieldProps}/></Grid>
                    <Grid item xs={3}><FormField name="organGestor" componentProps={fieldProps}/></Grid>
                    <Grid item xs={3}><FormField name="metaExpedient" componentProps={fieldProps}/></Grid>
                    <Grid item xs={3}><FormField name="dataCreacioInici" componentProps={fieldProps}/></Grid>
                    <Grid item xs={3}><FormField name="dataCreacioFinal" componentProps={fieldProps}/></Grid>

                    <Grid item xs={2}><FormField name="numeroRegistre" componentProps={fieldProps}/></Grid>
                    <Grid item xs={3}><FormField name="grup" componentProps={fieldProps}/></Grid>
                    <Grid item xs={3}><FormField name="agafatPer" componentProps={fieldProps}/></Grid>

                    <Grid item xs={2}><FormField name="agafat" componentProps={fieldProps}/></Grid>
                    <Grid item xs={2}><FormField name="pendentFirmar" componentProps={fieldProps}/></Grid>

                    <Grid item xs={12} sx={{ display: 'flex', justifyContent: 'end' }}>
                        <Button onClick={netejar}><Icon>eraser</Icon> Netejar</Button>
                        <Button onClick={cercar} variant="contained"><Icon>filter_alt</Icon> Cercar</Button>
                    </Grid>
                </Grid>
            </MuiFilter>
            <MuiGrid
                resourceName="expedientResource"
                columns={columns}
                paginationActive
                filter={springFilter}
                rowUpdateLink={"/contingut/{{id}}"}
                sortModel={[{field: 'createdDate', sort: 'desc'}]}
                perspectives={["INTERESSATS_RESUM"]}
            />
        </div>
    </GridPage>
}

export default ExpedientGrid;