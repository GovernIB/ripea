import React, {useState} from 'react';
import {
    GridPage,
    MuiGrid,
} from 'reactlib';
import {Button, Box, Typography, Icon, Grid} from "@mui/material";
import {formatDate} from '../../util/dateUtils';
import ExpedientActionButton from "./ExpedientActionButton.tsx";
import {useNavigate} from "react-router-dom";
import CommentDialog from "./CommentDialog.tsx";
import ExpedientFilter from "./ExpedientFilter.tsx";
import GridFormField from "../../components/GridFormField.tsx";
import {useFormContext} from "../../../lib/components/form/FormContext.tsx";

const ExpedientGridForm = () => {
    const formContext = useFormContext();
    const {data} = formContext;
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        {!data?.id && <GridFormField xs={12} name="metaExpedient"/>}
        <GridFormField xs={12} name="nom" />
        <GridFormField xs={12} name="organGestor" disabled={!!data?.id}/>
        <GridFormField xs={12} name="sequencia" disabled/>
        <GridFormField xs={12} name="any"/>
        <GridFormField xs={12} name="prioritat"/>
    </Grid>
}

const ExpedientGrid: React.FC = () => {
    // const { t } = useTranslation();
    let navigate = useNavigate();
    const [springFilter, setSpringFilter] = useState("");

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
            sortable: false,
            disableColumnMenu: true,
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
            field: 'interessats',
            flex: 1,
            valueFormatter: (value: any) => {
                let resum='';
                for(const interessat of value){
                    switch (interessat.tipus) {
						case 'InteressatPersonaFisicaEntity':
							resum += interessat?.nom == null ? "" : interessat?.nom + " ";
							resum += interessat?.llinatge1 == null ? "" : interessat?.llinatge1 + " ";
							resum += interessat?.llinatge2 == null ? "" : interessat?.llinatge2 + " ";
							resum += "("+interessat?.documentNum+")"+"\n";
							break;
						case 'InteressatPersonaJuridicaEntity':
							resum += interessat?.raoSocial+" ";
							resum += "("+interessat?.documentNum+")"+"\n";
							break;
						case 'InteressatAdministracioEntity':
							resum += interessat?.nomComplet+" ";
							resum += "("+interessat?.documentNum+")"+"\n";
							break;
					}
                }
                return resum;
            }
        },
        {
            field: 'grup',
            flex: 0.5,
            sortable: false,
            disableColumnMenu: true,
            valueFormatter: (value: any) => {
                return value?.description;
            }
        },
        {
            field: 'numComentaris',
            headerName: '',
            sortable: false,
            disableColumnMenu: true,
            flex: 0.5,
            renderCell: (params: any)=> {
                return <CommentDialog entity={params?.row}/>;
            }
        },
        {
            field: 'numSeguidors',
            headerName: '',
            sortable: false,
            disableColumnMenu: true,
            flex: 0.5,
            renderCell: (params: any)=> {
                return (<Button variant="contained" sx={{borderRadius: 1}} color={"inherit"}><Icon>people</Icon>{params.row.numSeguidors}</Button>);
            }
        },
        {
            field: 'id',
            headerName: '',
            sortable: false,
            disableColumnMenu: true,
            flex: 1,
            renderCell: (params: any) => {
                return <ExpedientActionButton entity={params?.row}/>;
            }
        },
    ];

    return <GridPage>
        <div style={{border: '1px solid #e3e3e3'}}>
            <Box sx={{backgroundColor: '#f5f5f5', borderBottom: '1px solid #e3e3e3', p: 1}}>
                <Typography variant="h5">Buscador de expedientes</Typography>
            </Box>

            <ExpedientFilter onSpringFilterChange={setSpringFilter}/>

            <MuiGrid
                resourceName="expedientResource"
                columns={columns}
                paginationActive
                filter={springFilter}
                sortModel={[{field: 'createdDate', sort: 'desc'}]}
                perspectives={["INTERESSATS_RESUM"]}
                titleDisabled
                popupEditCreateActive
                popupEditFormDialogTitle={"Crear nuevo expediente"}
                popupEditFormContent={<ExpedientGridForm/>}
                // readOnly
                onRowDoubleClick={(prop)=>navigate(`/contingut/${prop?.id}`)}
            />
        </div>
    </GridPage>
}

export default ExpedientGrid;