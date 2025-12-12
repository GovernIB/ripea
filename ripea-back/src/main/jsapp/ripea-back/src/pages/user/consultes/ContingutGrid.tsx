import {useTranslation} from "react-i18next";
import {useState} from "react";
import {GridPage, MuiDialog, useMuiDataGridApiRef, useFormContext} from "reactlib";
import {CardPage, ContenidoData} from "../../../components/CardData.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {Grid, Icon} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import useHistoric from "../../Historic.tsx";
import Load from "../../../components/Load.tsx";

// Detail
const useDetail = () => {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const handleOpen = (_id:any, row:any) => {
        // console.log(_id, row)
        setEntity(row)
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setEntity(undefined);
            setOpen(false);
        }
    };

    let buttons :any[] = [
        {
            value: 'close',
            text: t('common.close'),
        },
    ]

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.contingut.detalle.title')}
            componentProps={{ fullWidth: true, maxWidth: 'md' }}
            buttons={buttons}
            buttonCallback={() => {
                handleClose();
            }}
        >
            <Load value={entity}>
                <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
                    <ContenidoData title={t('page.contingut.detalle.nom')}>
                        {entity?.tipus == "EXPEDIENT" && <Icon>folder_open</Icon>}
                        {entity?.tipus == "CARPETA" && <Icon>folder</Icon>}
                        {entity?.tipus == "DOCUMENT" && <Icon>description</Icon>}
                        {entity?.nom}</ContenidoData>
                    <ContenidoData title={t('page.contingut.detalle.metaExpedient')} hiddenIfEmpty>{entity?.metaNode?.description}</ContenidoData>
                    <ContenidoData title={t('page.contingut.detalle.data')} hiddenIfEmpty>{formatDate(entity?.createdDate)}</ContenidoData>
                    <ContenidoData title={t('page.contingut.detalle.estat')} hiddenIfEmpty>{entity?.estat}</ContenidoData>
                    <ContenidoData title={t('page.arxiu.detall.versions')} hiddenIfEmpty>{entity?.ntiVersion}</ContenidoData>
                    <ContenidoData title={t('page.arxiu.detall.identificador')} hiddenIfEmpty>{entity?.ntiIdentificador}</ContenidoData>
                    <ContenidoData title={t('page.arxiu.detall.organ')} hiddenIfEmpty>{entity?.ntiOrgano}</ContenidoData>
                    <ContenidoData title={t('page.arxiu.detall.dataCaptura')} hiddenIfEmpty>{formatDate(entity?.dataCaptura)}</ContenidoData>
                    <ContenidoData title={t('page.arxiu.detall.origen')} hiddenIfEmpty>{entity?.ntiOrigen}</ContenidoData>
                    <ContenidoData title={t('page.arxiu.detall.estadoElaboracion')} hiddenIfEmpty>{entity?.ntiEstadoElaboracion}</ContenidoData>
                    <ContenidoData title={t('page.arxiu.detall.tipoDocumental')} hiddenIfEmpty>{entity?.ntiTipoDocumental}</ContenidoData>
                </Grid>
            </Load>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}

// Filter
const ContingutFilterForm = () => {
    const { data } = useFormContext();

    const isExpedient = data?.tipus === 'EXPEDIENT';
    const isDocument = data?.tipus === 'DOCUMENT';
    const isOther = data?.tipus && !isExpedient && !isDocument;

    return <>
        <GridFormField xs={3} name="nom"/>
        <GridFormField xs={3} name="createdBy"/>
        <GridFormField xs={3} name="tipus"/>
        <GridFormField xs={3} name="expedient"/>
        {/* <GridFormField xs={3} name="metaExpedient" hidden={!isExpedient} disabled={isOther} />
        <GridFormField xs={3} name="metaDocument" hidden={!isDocument} disabled={isOther} /> */}
        <GridFormField xs={2} name="dataEsborratInici" type={"date"}/>
        <GridFormField xs={2} name="dataEsborratFi" type={"date"}/>
        <GridFormField xs={3} name="esborrat"/>        
        <GridFormField xs={2} name="dataInici" type={"date"}/>
        <GridFormField xs={2} name="dataFi" type={"date"}/>
        <Grid item xs={1.6}/>
    </>
}

const springFilterBuilder = (data:any) => {
    const esborratFilter =
        data?.esborrat == null || data?.esborrat === 'ESBORRATS_I_NO_ESBORRATS'
            ? undefined
            : data?.esborrat === 'NOMES_ESBORRATS'
                ? builder.equals('esborrat', 1, true)
                : data?.esborrat === 'NOMES_NO_ESBORRATS'
                    ? builder.equals('esborrat', 0, true)
                    : undefined;

    return builder.and(
        builder.like('nom', data?.nom),
        builder.eq('createdBy', `'${data?.createdBy?.id}'`),
        builder.eq('tipus', `'${data?.tipus}'`),
        /*builder.eq('metaNode.id', data?.metaNode?.id),*/
        builder.betweenDates('esborratData', data?.dataEsborratInici, data?.dataEsborratFi),
        esborratFilter,
        builder.eq('expedient.id', data?.expedient?.id),
        builder.betweenDates('createdDate', data?.dataInici, data?.dataFi),
    );
}

const ContingutFilter = (props: any) => {
    const {onSpringFilterChange} = props;

    return <StyledMuiFilter
        resourceName={"contingutResource"}
        code={"FILTER"}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
    >
        <ContingutFilterForm/>
    </StyledMuiFilter>
}

// Grid
const perspectives:any = ['AUDITORIA']
const sortModel: any = [{field: 'createdDate', sort: 'desc'}]
const columns = [
    {
        field: 'nom',
        flex: 0.75,
        renderCell: (params:any) => <>
            {params?.row?.tipus == "EXPEDIENT" && <Icon>folder_open</Icon>}
            {params?.row?.tipus == "CARPETA" && <Icon>folder</Icon>}
            {params?.row?.tipus == "DOCUMENT" && <Icon>description</Icon>}
            {params?.formattedValue}
        </>
    },
    {
        field: 'numero',
        flex: 0.5,
    },
    {
        field: 'metaNode',
        flex: 1,
    },
    {
        field: 'createdByFullName',
        flex: 0.8,
    },
    {
        field: 'createdDate',
        flex: 0.7,
        valueFormatter: (value: any) => formatDate(value),
    },
    {
        field: 'esborratData',
        flex: 0.7,
        valueFormatter: (value: any) => formatDate(value),
    },
    {
        field: 'expedient',
        headerName: '',
        flex: 1.5,
        renderCell: (params:any) => <>
            {/** TODO: revisar columna ubicación */}
            {!!params?.row?.expedient?.id && <>/<a href={`/contingut/${params?.row?.expedient?.id}`} style={{ display: 'flex', alignItems: 'center' }}><Icon>folder_open</Icon>{params?.formattedValue}</a></>}
            {params?.row?.pare?.id != params?.row?.expedient?.id ?<>/.../<Icon>folder</Icon>{params?.row?.pare?.description}</> :"" }
            {/*{!!params?.row?.fitxerNom && <>/<Icon>description</Icon>{params?.row?.fitxerNom}</>}*/}
            /
            {params?.row?.tipus == "EXPEDIENT" && <Icon>folder_open</Icon>}
            {params?.row?.tipus == "CARPETA" && <Icon>folder</Icon>}
            {params?.row?.tipus == "DOCUMENT" && <Icon>description</Icon>}
            {params?.row?.nom}
        </>,
    },
]

const ContingutGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const { handleOpen: handleDetail, dialog: dialogDetail} = useDetail();
    const {handleOpen: handleHistoricOpen, dialog: dialogHistoric} = useHistoric();

    const actions = [
        {
            label: t('common.detail'),
            icon: "info",
            showInMenu: true,
            onClick: handleDetail,
        },
        {
            label: t('page.contingut.action.history.label'),
            icon: "list",
            showInMenu: true,
            onClick: handleHistoricOpen,
        },
    ]
    const massiveActions = [
        {
            label: t('page.contingut.action.replay.label'),
            icon: "replay",
            showInMenu: true,
        },
        {
            label: t('common.delete'),
            icon: "delete",
            showInMenu: true,
        },
    ]

    return <GridPage disableMargins>
        <CardPage title={t('page.user.menu.continguts')}>
            <ContingutFilter onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={"contingutResource"}
                columns={columns}
                filter={springFilter}
                sortModel={sortModel}
                perspectives={perspectives}
                rowAdditionalActions={actions}
                toolbarMassiveActions={massiveActions}
                onRowClick={(params: any) => handleDetail(params?.row?.id, params?.row) }
                toolbarHideCreate
            />
        </CardPage>
        {dialogHistoric}
        {dialogDetail}
    </GridPage>
}
export default ContingutGrid;