import {useMemo, useState} from "react";
import {Typography, Icon, Grid} from "@mui/material";
import {GridPage, useFormContext, useMuiDataGridApiRef,} from 'reactlib';
import {useTranslation} from "react-i18next";
import {useNavigate} from "react-router-dom";
import {formatDate} from '../../util/dateUtils';
import GridFormField from "../../components/GridFormField.tsx";
import {useCommonActions} from "./details/CommonActions.tsx";
import {ExpedientComment} from "../CommentDialog.tsx";
import {FollowersDialog} from "../FollowersDialog.tsx";
import ExpedientFilter from "./ExpedientFilter.tsx";
import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";
import useMassiveActions from "./details/ExpedientMassiveActions.tsx";
import {CardPage} from "../../components/CardData.tsx";
import Load from "../../components/Load.tsx";
import {useUserSession} from "../../components/Session.tsx";
import {GridSortDirection} from "@mui/x-data-grid-pro";
import useAlerta from "./details/Alerta.tsx";
import useErrorValidacio from "./details/ErrorValidacio.tsx";
import * as builder from '../../util/springFilterUtils';

const labelStyle = {padding: '1px 4px', fontSize: '11px', fontWeight: '500', borderRadius: '2px', display: 'flex', alignItems: 'center', width: 'max-content'}
const obertStyle = {border: '1px dashed #AAA'}
const tancatStyle = {backgroundColor: 'grey', color: 'white'}

export const ExpedientGridForm = () => {
    const {data} = useFormContext();
    const filterMetaExpedientCrear = builder.and(
        builder.eq('actiu', true),
    );
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField  xs={12} name="metaExpedient"
                        hidden={!!data?.id}
                        filter={filterMetaExpedientCrear}
                        namedQueries={[ data?.id ? 'EXPEDIENT_UPDATE' : 'EXPEDIENT_CREATE' ]}/>
        <GridFormField xs={12} name="nom"/>
        <GridFormField xs={12} name="organGestor"
                       namedQueries={[`EXPEDIENT_FORM#${data?.metaExpedient?.id || 0}`]}
                       disabled={!!data?.id || !data?.metaExpedient || data?.disableOrganGestor}
                       readOnly={!!data?.id || !data?.metaExpedient || data?.disableOrganGestor}/>
        <GridFormField xs={6} name="sequencia" disabled/>
        <GridFormField xs={6} name="any" thousandSeparator={false}/>
        <GridFormField xs={12} name="grup"
                       namedQueries={[`BY_PROCEDIMENT#${data?.metaExpedient?.id ?? 0}`]}
                       hidden={!data?.grup && !data?.gestioAmbGrupsActiva} required/>
        <GridFormField xs={12} name="prioritat" required/>
        <GridFormField xs={12} name="prioritatMotiu" type={"textarea"} hidden={data?.prioritat == 'B_NORMAL'} required/>
        <GridFormField xs={12} name="asignarSeguidor" type={"checkbox"} hidden={!!data?.id}/>
    </Grid>
}

export const StyledEstat = (props: any) => {
    const {entity: expedient, icon, children} = props;

    const style = expedient?.estatAdditionalInfo
        ? {backgroundColor: expedient?.estatAdditionalInfo?.color}
        : expedient?.estat == 'TANCAT'
            ? tancatStyle
            : obertStyle;

    const icona = expedient?.estat == 'TANCAT' ? 'folder' : 'folder_open'

    return <Typography variant="caption" sx={{...labelStyle, ...style}}>
        {icon && <Icon fontSize={"inherit"}>{icona}</Icon>}
        {expedient?.estatAdditionalInfo?.nom ?? children}
    </Typography>
}

export const StyledPrioritat = (props: any) => {
    const {entity: expedient, children} = props;

    let style: any = {};

    switch (expedient?.prioritat) {
        case "D_MOLT_ALTA":
            style = {backgroundColor: '#d99b9d', color: 'white'}
            break;
        case "C_ALTA":
            style = {backgroundColor: '#ffebae'}
            break;
        case "B_NORMAL":
            style = obertStyle
            break;
        case "A_BAIXA":
            style = {backgroundColor: '#c3e8d1'}
            break;
    }

    return <Typography variant="caption" sx={{...labelStyle, ...style}}>{children}</Typography>
}

const beforeAvis = [
    {
        field: 'numero',
        flex: 0.70,
    },
    {
        field: 'metaExpedient',
        flex: 1.15,
        sortable: false,
    },
    {
        field: 'nom',
        flex: 1.15,
    },    
];
const afterAvis = [
    {
        field: 'createdDate',
        flex: 0.8,
        valueFormatter: (value: any) => formatDate(value)
    },
    {
        field: 'estat',
        flex: 0.75,
        renderCell: (params: any) => <StyledEstat entity={params?.row} icon={"folder"}>{params.formattedValue}</StyledEstat>,
        sortProcessor: (field: string, sort: GridSortDirection) => {
            return [
                { field: "estatAdditional", sort },
                { field: field, sort },
                { field: "id", sort }
            ]
        }
    },
    {
        field: 'prioritat',
        flex: 0.45,
        renderCell: (params: any) => <StyledPrioritat entity={params?.row}>{params.formattedValue}</StyledPrioritat>
    },
];

// sortModel i perspectives per prevenir re-renders
const sortModel: any = [{field: 'createdDate', sort: 'desc'}];
const perspectives = ["INTERESSATS_RESUM", "ESTAT", 'RELACIONAT', "COUNT", "AUDITORIA"];

const ExpedientGrid = () => {
    const {t} = useTranslation();
    const navigate = useNavigate();
    const [springFilter, setSpringFilter] = useState<string>();
    const [load, setLoad] = useState<boolean>(false);
    const apiRef = useMuiDataGridApiRef();

    const {value: user} = useUserSession();
    const isRolActualAdminLectura = user?.rolActual == 'IPA_ADMIN_LECTURA';

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {actions, components} = useCommonActions(refresh);
    const {actions: massiveActions, components: massiveComponents} = useMassiveActions(refresh);

    const {handleOpen: handelAlert, dialog: dialogAlert} = useAlerta();
    const {handleOpen: hanldeErrorValidacio, dialog: dialogErrorValidacio} = useErrorValidacio();

    const columnsAddition :any[] = useMemo(() => [
        ...beforeAvis,
        {
            headerName: t('page.expedient.detall.avisos'),
            field: 'avisos',
            sortable: false,
            flex: 0.5,
            renderCell: (params: any) => (<>
                {!params.row?.valid &&
                    <Icon color={"warning"}
                          title={t('page.expedient.alert.validation')}
                          onClick={(event:any) => {
                              event.stopPropagation()
                              hanldeErrorValidacio(params.row?.id, params.row)
                          }}>warning</Icon>}
                {params.row?.errorLastEnviament &&
                    <Icon color={"error"} title={t('page.expedient.alert.errorEnviament')}>edit</Icon>}
                {params.row?.errorLastNotificacio &&
                    <Icon color={"error"} title={t('page.expedient.alert.errorNotificacio')}>mail</Icon>}
                {params.row?.ambEnviamentsPendents &&
                    <Icon color={"primary"} title={t('page.expedient.alert.ambEnviamentsPendents')}>edit</Icon>}
                {params.row?.ambNotificacionsPendents &&
                    <Icon color={"primary"} title={t('page.expedient.alert.ambNotificacionsPendents')}>mail</Icon>}
                {params.row?.numAlert != 0 &&
                    <Icon color={"error"}
                          title={t('page.expedient.alert.alert')}
                          onClick={(event:any) => {
                              event.stopPropagation()
                              handelAlert(params.row?.id, params.row)
                          }}>error</Icon>}
                {params.row?.arxiuUuid == null &&
                    <Icon color={"error"} title={t('page.contingut.alert.guardarPendent')}>warning</Icon>}
            </>),
        },
        ...afterAvis,
        {
            field: 'dataDarrerEnviament',
            flex: 0.75,
            valueFormatter: (value: any) => formatDate(value),
            hidden: !user?.conf?.expedientListDataDarrerEnviament,
        },
        {
            field: 'agafatPer',
            flex: 0.75,
            hidden: !user?.conf?.expedientListAgafatPer,
            valueFormatter: (value: any) => value?.description,
        },
        {
            field: 'interessats',
            sortable: false,
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
            },
            hidden: !user?.conf?.expedientListInteressats,
        },
        {
            field: 'grup',
            flex: 0.5,
            sortable: false,
            hidden: !user?.conf?.expedientListGrup,
        },
        {
            field: 'numComentaris',
            headerName: '',
            sortable: false,
            flex: 0.25,
            renderCell: (params: any) => <ExpedientComment
                entity={params?.row}
                onClose={refresh}
            />,
            hidden: !user?.conf?.expedientListComentaris,
        },
        {
            field: 'numSeguidors',
            headerName: '',
            sortable: false,
            flex: 0.25,
            renderCell: (params: any) => <FollowersDialog entity={params?.row}/>
        },
    ]
        .filter((col:any)=>!col?.hidden), [user]);

    return <GridPage disableMargins>
        <CardPage title={t('page.expedient.filter.title')}>
            <ExpedientFilter onSpringFilterChange={(value:any)=>{
                setSpringFilter(value)
                setLoad(true)
            }}/>

            <Load value={load} noEffect>
            <StyledMuiGrid
                resourceName={"expedientResource"}
                popupEditFormDialogResourceTitle={t('page.expedient.title')}
                columns={columnsAddition}
                filter={springFilter}
                sortModel={sortModel}
                perspectives={perspectives}
                apiRef={apiRef}
                popupEditCreateActive
                popupEditFormContent={<ExpedientGridForm/>}
                popupEditFormDialogTitle={t('page.expedient.action.new.title')}
                readOnly={isRolActualAdminLectura}
                onRowClick={(params: any) => navigate(`/contingut/${params?.id}`)}
                rowAdditionalActions={actions}
                toolbarCreateTitle={t('page.expedient.action.new.label')}
                toolbarMassiveActions={massiveActions}
                rowProps={(row: any) => {
                    const color = row?.estatAdditionalInfo?.color;
                    return color
                        ? {
                            'box-shadow': `${color} -6px 0px 0px`,
                            'border-left': `6px solid ${color}`,
                        }
                        : {
                            'padding-left': '6px'
                        }
                }}
                popupEditFormI18nKeys={{
                    createSuccess: 'page.expedient.action.new.ok',
                    updateSuccess: 'page.expedient.action.update.ok',
                }}
                popupEditFormDialogButtons={[
                    {icon: 'save', text: t('common.create'), componentProps: { variant: 'contained' }, value: true },
                    {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false }, 
                ]}
            />
            </Load>

            {components}
            {massiveComponents}
            {dialogAlert}
            {dialogErrorValidacio}
        </CardPage>
    </GridPage>
}

export default ExpedientGrid;
