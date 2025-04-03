import {
    GridPage,
    MuiGrid,
    useFormContext,
    useMuiDataGridApiRef,
} from 'reactlib';
import {Typography, Icon, Grid, Card, CardContent} from "@mui/material";
import { formatDate } from '../../util/dateUtils';
import { useNavigate } from "react-router-dom";
import GridFormField from "../../components/GridFormField.tsx";
import { useCommonActions } from "./details/CommonActions.tsx";
import { useTranslation } from "react-i18next";
import useExpedientFilter from "./ExpedientFilter.tsx";
import {CommentDialog} from "../CommentDialog.tsx";
import {FollowersDialog} from "../FollowersDialog.tsx";

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
        valueFormatter: (value: any) => formatDate(value)
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
    {
        field: 'grup',
        flex: 0.5,
        sortable: false,
        disableColumnMenu: true,
    },
    {
        field: 'numComentaris',
        headerName: '',
        sortable: false,
        disableColumnMenu: true,
        flex: 0.5,
        renderCell: (params: any) => <CommentDialog entity={params?.row} />
    },
	{
	    field: 'numSeguidors',
	    headerName: '',
	    sortable: false,
	    disableColumnMenu: true,
	    flex: 0.5,
	    renderCell: (params: any) => <FollowersDialog entity={params?.row} />
	},	
];

const commonStyle = {p: 0.5, display: 'flex', alignItems: 'center', borderRadius: '5px', width: 'max-content'}

export const StyledEstat = (props:any) => {
    const { entity: expedient, icon } = props;
    const { t } = useTranslation();

    const obertStyle = { border: '1px dashed #AAA' }
    const tancatStyle = { backgroundColor: 'grey', color: 'white' }
    const additionalStyle = { backgroundColor: expedient?.estatAdditionalInfo?.color }

    const style = expedient?.estatAdditionalInfo
        ? additionalStyle
        : expedient?.estat == 'TANCAT'
            ? tancatStyle
            :obertStyle;

    return <Typography variant="caption" sx={{...commonStyle, ...style }}>
        { icon && <Icon fontSize={"inherit"}>{icon}</Icon>}
        {expedient?.estatAdditionalInfo?.nom ?? t(`page.estat.${expedient?.estat}`)}
    </Typography>
}

export const StyledPrioritat = (props:any) => {
    const { entity: expedient } = props;
    const { t } = useTranslation();

    let style;

    switch (expedient?.prioritat){
        case "D_MOLT_ALTA":
            style = {backgroundColor: '#d99b9d', color: 'white'}
            break;
        case "C_ALTA":
            style = {backgroundColor: '#ffebae'}
            break;
        case "B_NORMAL":
            style = {border: '1px dashed #AAA'}
            break;
        case "A_BAIXA":
            style = {backgroundColor: '#c3e8d1'}
            break;
    }

    return <Typography variant="caption" sx={{...commonStyle, ...style }}>
        {t(`page.prioritat.${expedient?.prioritat}`)}
    </Typography>
}

const ExpedientGrid = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();

    const apiRef = useMuiDataGridApiRef()
    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {actions, components} = useCommonActions(refresh);
    const { springFilter, content } = useExpedientFilter();

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
            flex: 0.75,
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
        {
            field: 'grup',
            flex: 0.5,
            sortable: false,
            disableColumnMenu: true,
        },
        {
            field: 'numComentaris',
            headerName: '',
            sortable: false,
            disableColumnMenu: true,
            flex: 0.5,
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
		    flex: 0.5,
		    renderCell: (params: any) => <FollowersDialog entity={params?.row} />
		},
    ];

    return <GridPage>
        <Card sx={{border: '1px solid #e3e3e3', borderRadius: '10px', height: '100%' }}>
            <CardContent sx={{backgroundColor: '#f5f5f5', borderBottom: '1px solid #e3e3e3'}}>
                <Typography variant="h5">{t('page.expedient.filter.title')}</Typography>
            </CardContent>

            <CardContent sx={{height: '58%'}} >
                {content}

                <MuiGrid
                    resourceName="expedientResource"
                    popupEditFormDialogResourceTitle={t('page.expedient.title')}
                    columns={columns}
                    paginationActive
                    filter={springFilter}
                    sortModel={[{ field: 'createdDate', sort: 'desc' }]}
                    perspectives={["INTERESSATS_RESUM", "ESTAT"]}
                    titleDisabled
                    popupEditCreateActive
                    apiRef={apiRef}
                    // popupEditFormDialogTitle={"Crear nuevo expediente"}
                    popupEditFormContent={<ExpedientGridForm />}
                    onRowDoubleClick={(row) => navigate(`/contingut/${row?.id}`)}
                    rowAdditionalActions={actions}
                    rowHideDeleteButton
                    disableColumnMenu
                />

                {components}
            </CardContent>
        </Card>
    </GridPage>
}

export default ExpedientGrid;