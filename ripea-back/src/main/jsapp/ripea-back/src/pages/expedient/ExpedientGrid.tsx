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
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

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
                {!params.row.valid && <FontAwesomeIcon icon={"triangle-exclamation"} className="text-warning" title="validacio"/>}
                {params.row.errorLastEnviament && <FontAwesomeIcon icon={"pencil-square"} className="text-danger" title="enviaments"/>}
                {params.row.errorLastNotificacio && <FontAwesomeIcon icon={"envelope-square"} className="text-danger" title="notificacions"/>}
                {params.row.ambEnviamentsPendents && <FontAwesomeIcon icon={"pencil-square"} className="text-primary" title="enviaments"/>}
                {params.row.ambNotificacionsPendents && <FontAwesomeIcon icon={"envelope-square"} className="text-primary" title="notificacions"/>}
                {params.row.alerta && <FontAwesomeIcon icon={"exclamation-circle"} className="text-danger" title="alertes"/>}
                {params.row.arxiuUuid == null && <FontAwesomeIcon icon={"exclamation-triangle"} className="text-danger" title="pendentGuardarArxiu"/>}
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
    ];

    const springFilterBuilder = (data: any) :string => {
        let filterStr :string = '';
        filterStr += builder.and(
            builder.like("numero", data.numero),
            builder.like("nom", data.nom),
            (data.estat == null || data.estat==='TANCAT')
                ?builder.eq("estat",`'${data.estat}'`)
                :builder.neq("estat", `'TANCAT'`),
            // in("interessat", data.interessat),
            // eq("organGestor", data.organGestor),
            // eq("metaExpedient", data.metaExpedient),
            builder.between("createdDate", `'${data.dataCreacioInici}'`, `'${data.dataCreacioFinal}'`),
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
                    {/*<Grid item xs={3}><FormField name="interessat" componentProps={fieldProps}/></Grid>*/}
                    {/*<Grid item xs={3}><FormField name="organGestor" componentProps={fieldProps}/></Grid>*/}
                    {/*<Grid item xs={3}><FormField name="metaExpedient" componentProps={fieldProps}/></Grid>*/}
                    <Grid item xs={3}><FormField name="dataCreacioInici" componentProps={fieldProps}/></Grid>
                    <Grid item xs={3}><FormField name="dataCreacioFinal" componentProps={fieldProps}/></Grid>
                    <Grid item xs={3}><FormField name="organGestor" componentProps={fieldProps}/></Grid>

                    <Grid item xs={12} sx={{ display: 'flex', justifyContent: 'end' }}>
                        <Button onClick={netejar}><FontAwesomeIcon icon="eraser" /> Netejar</Button>
                        <Button onClick={cercar} variant="contained"><FontAwesomeIcon icon="filter" /> Cercar</Button>
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