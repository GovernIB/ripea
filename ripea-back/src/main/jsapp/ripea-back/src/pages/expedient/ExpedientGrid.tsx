import React, {useState} from 'react';
import {
    GridPage,
    MuiGrid,
    useFormContext,
} from 'reactlib';
import {Box, Typography, Icon, Grid} from "@mui/material";
import {formatDate} from '../../util/dateUtils';
import {useNavigate} from "react-router-dom";
import CommentDialog from "./CommentDialog.tsx";
import ExpedientFilter from "./ExpedientFilter.tsx";
import GridFormField from "../../components/GridFormField.tsx";

const ExpedientGridForm = () => {
    const formContext = useFormContext();
    const {data} = formContext;
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="metaExpedient" hidden={!!data?.id}/>
        <GridFormField xs={12} name="nom"/>
        <GridFormField xs={12} name="organGestor" disabled={!!data?.id} /*filter={''}*//>
        <GridFormField xs={12} name="sequencia" disabled/>
        <GridFormField xs={12} name="any"/>
        <GridFormField xs={12} name="prioritat" required/>
        <GridFormField xs={12} name="prioritatMotiu" hidden={data?.prioritat=='B_NORMAL'} required/>
    </Grid>
}

const ExpedientGrid: React.FC = () => {
    // const { t } = useTranslation();
    let navigate = useNavigate();
    const [springFilter, setSpringFilter] = useState("");

    const columns = [
        {
            field: 'numero',
            flex: 1,
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
    ];
    const actions = [
        // {
        //     title: "",
        //     icon: "forum",
        // },
        {
            title: "",
            icon: "people",
        },
        ////

        {
            title: "Gestionar",
            icon: "folder",
            linkTo: "/contingut/{{id}}",
            showInMenu: true,
        },
        {
            title: "Seguir",
            icon: "person_add",
            showInMenu: true,
        },
        {
            title: "Modificar...",
            icon: "edit",
            showInMenu: true,
        },
        {
            title: "Coger",
            icon: "lock",
            showInMenu: true,
        },
        {
            title: "Liberar",
            icon: "lock_open",
            showInMenu: true,
        },
        {
            title: "Cambiar prioridad...",
            icon: "",
            showInMenu: true,
        },
        {
            title: "Cambiar estado...",
            icon: "",
            showInMenu: true,
        },
        {
            title: "Relacionar...",
            icon: "link",
            showInMenu: true,
        },
        {
            title: "Cerrar...",
            icon: "check",
            showInMenu: true,
        },
        {
            title: "Borrar",
            icon: "delete",
            showInMenu: true,
        },
        {
            title: "Histórico de acciones",
            icon: "list",
            showInMenu: true,
        },
        {
            title: "Descargar documentos...",
            icon: "download",
            showInMenu: true,
        },
        {
            title: "Exportar indice PDF...",
            icon: "format_list_numbered",
            showInMenu: true,
        },
        {
            title: "Indice PDF y exportación EIN...",
            icon: "format_list_numbered",
            showInMenu: true,
            disabled: true,
        },
        {
            title: "Información archivo",
            icon: "info",
            showInMenu: true,
            disabled: true,
        },
        {
            title: "Sincronizar estado con archivo",
            icon: "autorenew",
            showInMenu: true,
        },
    ]

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
                rowAdditionalActions={actions}
            />
        </div>
    </GridPage>
}

export default ExpedientGrid;