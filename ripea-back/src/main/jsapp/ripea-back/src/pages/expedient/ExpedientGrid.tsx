import {useState, useRef, useEffect, useMemo} from "react";
import {Typography, Icon, Grid, CardContent, Card, Box} from "@mui/material";
import {
    GridPage,
    MuiGrid,
    useFormContext,
    useMuiDataGridApiRef,
} from 'reactlib';
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { formatDate } from '../../util/dateUtils';
import GridFormField from "../../components/GridFormField.tsx";
import { useCommonActions } from "./details/CommonActions.tsx";
import {CommentDialog} from "../CommentDialog.tsx";
import {FollowersDialog} from "../FollowersDialog.tsx";
import ExpedientFilter from "./ExpedientFilter.tsx";
import ExpedientGridToolbar from "./ExpedientGridToolbar.tsx";
import {useGridApiRef as useMuiDatagridApiRef} from "@mui/x-data-grid-pro/hooks/utils/useGridApiRef";

const labelStyle = { padding: '1px 4px', fontSize: '11px', fontWeight: '500', borderRadius: '2px' }
const commonStyle = { p: 0.5, display: 'flex', alignItems: 'center', borderRadius: '5px', width: 'max-content' }
const obertStyle = { border: '1px dashed #AAA', ...labelStyle }
const tancatStyle = { backgroundColor: 'grey', color: 'white', ...labelStyle }

const ExpedientGridForm = () => {
    const { data }  = useFormContext();

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="metaExpedient" hidden={!!data?.id} />
        <GridFormField xs={12} name="nom" />
        <GridFormField xs={12} name="organGestor" disabled={!!data?.id || data?.disableOrganGestor} /*TODO: filter={'organGestorService.findPermesosByEntitatAndExpedientTipusIdAndFiltre'}*/ />
        <GridFormField xs={12} name="sequencia" disabled />
        <GridFormField xs={12} name="any" />
        <GridFormField xs={12} name="prioritat" required />
        <GridFormField xs={12} name="prioritatMotiu" hidden={data?.prioritat == 'B_NORMAL'} required />
    </Grid>
}

export const StyledEstat = (props:any) => {
    const { entity: expedient, icon } = props;
    const { t } = useTranslation();

    const additionalStyle = { backgroundColor: expedient?.estatAdditionalInfo?.color, padding: '1px 4px', fontSize: '11px', fontWeight: '500', borderRadius: '2px' }

    const style = expedient?.estatAdditionalInfo
        ? additionalStyle
        : expedient?.estat == 'TANCAT'
            ? tancatStyle
            :obertStyle;

    const icona = expedient?.estat == 'TANCAT' ? 'folder' : 'folder_open'

    return <Typography variant="caption" sx={{...commonStyle, ...style }}>
        { icon && <Icon fontSize={"inherit"}>{icona}</Icon>}
        {expedient?.estatAdditionalInfo?.nom ?? t(`enum.estat.${expedient?.estat}`)}
    </Typography>
}

export const StyledPrioritat = (props:any) => {
    const { entity: expedient } = props;
    const { t } = useTranslation();
    const labelStyle = { padding: '1px 4px', fontSize: '11px', fontWeight: '500', borderRadius: '2px' }

    let style;

    switch (expedient?.prioritat){
        case "D_MOLT_ALTA":
            style = {backgroundColor: '#d99b9d', color: 'white', ...labelStyle}
            break;
        case "C_ALTA":
            style = {backgroundColor: '#ffebae', ...labelStyle}
            break;
        case "B_NORMAL":
            style = {border: '1px dashed #AAA', ...labelStyle}
            break;
        case "A_BAIXA":
            style = {backgroundColor: '#c3e8d1', ...labelStyle}
            break;
    }

    return <Typography variant="caption" sx={{...commonStyle, ...style }}>
        {t(`enum.prioritat.${expedient?.prioritat}`)}
    </Typography>
}

const columns = [
    {
        field: 'numero',
        flex: 0.75,
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
        valueFormatter: (value: any) => formatDate(value)
    },
    {
        field: 'estat',
        flex: 0.75,
        renderCell: (params: any) => <StyledEstat entity={params?.row} icon={"folder"}/>
    },
    {
        field: 'prioritat',
        flex: 0.5,
        renderCell: (params: any) => <StyledPrioritat entity={params?.row}/>
    },
    {
        field: 'agafatPer',
        flex: 0.75,
    },
    {
        field: 'interessats',
        flex: 1,
        valueFormatter: (value: any) => {
            let resum = '';
            for (const interessat of value) {
                switch (interessat.tipus) {
                    case 'InteressatPersonaFisicaEntity':
                        resum += interessat?.nom == null ? "" : interessat?.nom + " ";
                        resum += interessat?.llinatge1 == null ? "" : interessat?.llinatge1 + " ";
                        resum += interessat?.llinatge2 == null ? "" : interessat?.llinatge2 + " ";
                        resum += "(" + interessat?.documentNum + ")" + "\n";
                        break;
                    case 'InteressatPersonaJuridicaEntity':
                        resum += interessat?.raoSocial + " ";
                        resum += "(" + interessat?.documentNum + ")" + "\n";
                        break;
                    case 'InteressatAdministracioEntity':
                        resum += interessat?.nomComplet + " ";
                        resum += "(" + interessat?.documentNum + ")" + "\n";
                        break;
                }
            }
            return resum;
        }
    },
    // {
    //     field: 'grup',
    //     flex: 0.5,
    //     sortable: false,
    //     disableColumnMenu: true,
    // },
];


// sortModel i perspectives per prevenir re-renders
const sortModel = [{ field: 'createdDate', sort: 'desc' }];
const perspectives = ["INTERESSATS_RESUM", "ESTAT"];

const ExpedientGrid = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const [springFilter, setSpringFilter] = useState<string>();
    const [selectedRows, setSelectedRows] = useState<any[]>([]);
    const [gridRows, setGridRows] = useState<any[]>([]);
    const apiRef = useMuiDataGridApiRef();
    const datagridApiRef = useMuiDatagridApiRef();
    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {actions, components} = useCommonActions(refresh);

    const columnsAddition = [
        ...columns,
        {
            field: 'numComentaris',
            headerName: '',
            sortable: false,
            disableColumnMenu: true,
            flex: 0.25,
            renderCell: (params: any) => <CommentDialog
                entity={params?.row}
                title={`${t('page.comment.expedient')}: ${params?.row?.nom}`}
                resourceName={'expedientComentariResource'}
                resourceReference={'expedient'}
            />
        },
		{
		    field: 'numSeguidors',
		    headerName: '',
		    sortable: false,
		    disableColumnMenu: true,
		    flex: 0.25,
		    renderCell: (params: any) => <FollowersDialog entity={params?.row} />
		},
    ];

    // Custom row styling with colored bar
    const getRowClassName = (params: any) => {
        const color = params.row?.estatAdditionalInfo?.color;
        const className = (color ? `row-with-color-${params.row.id} ` : '') + (params.indexRelativeToCurrentPage % 2 === 0 ? 'even' : 'odd');
        console.log('Row className:', className);
        return className;
    };

    // Apply custom CSS for rows with color
    const getRowStyle = () => {
        const styles: any = {};
        if (gridRows.length > 0) {
            gridRows.forEach((row: any) => {
                const color = row?.estatAdditionalInfo?.color;
                if (color) {
                    styles[`.row-with-color-${row.id}`] = {
                        'box-shadow': `${color} -6px 0px 0px`,
                        'border-left': `6px solid ${color}`
                    };
                }
            });
        }
        return styles;
    };

    // Applica word wrap a totes les columnes
    const columnsWithWordWrap = columnsAddition.map(col => ({
        ...col,
        flex: col.flex || 1,
        cellClassName: 'cell-with-wrap',
    }));

    return <GridPage>
        <Card sx={{border: '1px solid #e3e3e3', borderRadius: '4px', height: '100%', display: 'flex', flexDirection: 'column'}}>
            <CardContent sx={{backgroundColor: '#f5f5f5', borderBottom: '1px solid #e3e3e3'}}>
                <Typography variant="h5">{t('page.expedient.filter.title')}</Typography>
            </CardContent>

            <CardContent sx={{height: '100%', display: 'flex', flexDirection: 'column'}} >

                <Grid item xs={12}>
                    <ExpedientFilter onSpringFilterChange={setSpringFilter}/>
                </Grid>

                <style>
                    {`
                    .cell-with-wrap {
                        // white-space: normal !important;
                        // line-height: 1.2em;
                        // word-break: break-word;
                        // padding: 5px 10px !important;
                        // overflow: auto;
                        // display: flex;
                        // align-items: start !important;
                        text-overflow: ellipsis !important;
                    }
                    
                    .MuiDataGrid-checkboxInput {
                        transform: scale(0.8);
                    }
                    .MuiDataGrid-cell--withRenderer {
                        align-items: flex-start !important;
                    }
                    .MuiDataGrid-columnHeaderCheckbox, 
                    .MuiDataGrid-cellCheckbox {
                        align-items: flex-start !important;
                        padding-top: 4px !important;
                    }
                    [class^="row-with-color-"] .MuiDataGrid-cellCheckbox {
                        width: 48px !important;
                        max-width: 48px !important;
                        min-width: 48px !important;
                        margin-left: -4px !important;
                    }
                    ${Object.entries(getRowStyle()).map(([className, style]) => 
                        `${className} { ${Object.entries(style as any).map(([prop, value]) => 
                            `${prop}: ${value};`).join(' ')} }`
                    ).join('\n')}
                    `}
                </style>

                <ExpedientGridToolbar
                    selectedRows={selectedRows}
                    setSelectedRows={setSelectedRows}
                    gridRows={gridRows}
                    apiRef={apiRef}
                    datagridApiRef={datagridApiRef}
                />


                <MuiGrid
                    titleDisabled
                    resourceName="expedientResource"
                    popupEditFormDialogResourceTitle={t('page.expedient.title')}
                    columns={columnsWithWordWrap}
                    filter={springFilter}
                    sortModel={sortModel}
                    perspectives={perspectives}
                    apiRef={apiRef}
                    datagridApiRef={datagridApiRef}
                    popupEditCreateActive
                    popupEditFormContent={<ExpedientGridForm />}
                    onRowDoubleClick={(row) => navigate(`/contingut/${row?.id}`)}
                    rowAdditionalActions={actions}
                    paginationActive
                    rowHideDeleteButton={(row:any) => row?.estat == "TANCAT"}
                    disableColumnMenu
                    toolbarHide
                    selectionActive
                    checkboxSelection
                    keepNonExistentRowsSelected
                    rowSelectionModel={selectedRows}
                    onRowSelectionModelChange={(newSelection) => {
                        // console.log('Selection changed:', newSelection);
                        setSelectedRows([...newSelection]);
                    }}
                    getRowClassName={getRowClassName}
                    onRowsChange={(rows) => { setGridRows([...rows]); }}
                    // getRowHeight={(params) => "auto"}
                />

                {components}
            </CardContent>
        </Card>
    </GridPage>
}

export default ExpedientGrid;
