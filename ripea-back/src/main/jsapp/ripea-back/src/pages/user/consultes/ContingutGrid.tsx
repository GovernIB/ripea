import {useTranslation} from "react-i18next";
import {useMemo, useState} from "react";
import {
    GridPage,
    MuiDialog,
    useMuiDataGridApiRef,
    useFormContext,
    useResourceApiService,
    useBaseAppContext, useConfirmDialogButtons
} from "reactlib";
import {CardPage, DetailCard} from "../../../components/CardData.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {Icon, Link} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import useHistoric from "../../Historic.tsx";
import Load from "../../../components/Load.tsx";
import {Link as RouterLink} from "react-router-dom";
import useAssignar from "../../expedient/actions/Assignar.tsx";
import {useSession} from "../../../components/SessionStorageContext.tsx";
import {FieldData, MuiDetail} from "../../../components/MuiDetail.tsx";
import Box from "@mui/material/Box";

const useActions = (refresh?: () => void) => {
    const {t} = useTranslation();

    const {
        artifactAction: apiAction,
    } = useResourceApiService('contingutResource');
    const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons().reverse();
    const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};

    const action = (id:any, code:string, msg:string) => {
        apiAction(undefined, {code: code, data:{ ids: [id], massivo: false }})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, msg, 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const actionMassive = (ids:any, code:string, msg:string) => {
        apiAction(undefined, {code: code, data:{ ids, massivo: true }})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, msg, 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const recuperar= (id:any): void => action(id, 'RECUPERAR', t('page.contingut.action.replay.ok'));
    const esborrar= (id:any): void => {
        messageDialogShow(
            '',
            t('page.expedient.results.checkDelete'),
            confirmDialogButtons,
            confirmDialogComponentProps)
            .then((value: any) => {
                if (value) {
                    action(id, 'DELETE', t('page.contingut.action.delete.ok'))
                }
            });
    }

    const recuperarMassive= (ids:any[]): void => actionMassive(ids, 'RECUPERAR', t('page.contingut.action.replay.massiveOk'));
    const esborrarMassive= (ids:any[]): void => {
        messageDialogShow(
            '',
            t('page.expedient.results.checkDelete'),
            confirmDialogButtons,
            confirmDialogComponentProps)
            .then((value: any) => {
                if (value) {
                    actionMassive(ids, 'DELETE', t('page.contingut.action.delete.massiveOk'))
                }
            });
    }

    return {
        recuperar,
        recuperarMassive,
        esborrar,
        esborrarMassive,
    }
}

// Detail
const useDetail = () => {
    const { t } = useTranslation();

    const {
        isReady: apiIsReady,
        getOne: apiGetOne,
        currentFields: fields,
    } = useResourceApiService('contingutResource');
    const {temporalMessageShow} = useBaseAppContext();

    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const handleOpen = (id:any) => {
        if(apiIsReady && id){
            apiGetOne(id, {perspectives})
                .then((app) => setEntity(app))
                .catch((error) => {
                    handleClose()
                    temporalMessageShow(null, error?.message, 'error');
                });
        }
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setEntity(undefined);
            setOpen(false);
        }
    };

    const buttons :any[] = [
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
                <MuiDetail entity={entity} fields={fields}>
                    <DetailCard>
                        <FieldData titleSize={4} textSize={8} field={"nom"} renderCell={(formattedValue:string) => (<Box display={'flex'} alignItems={'center'}>
                            {entity?.tipus == "EXPEDIENT" && <Icon>folder_open</Icon>}
                            {entity?.tipus == "CARPETA" && <Icon>folder</Icon>}
                            {entity?.tipus == "DOCUMENT" && <Icon>description</Icon>}
                            {formattedValue}
                        </Box>)} hiddenIfEmpty/>
                        <FieldData titleSize={4} textSize={8} field={"metaNode"} hiddenIfEmpty/>
                        <FieldData titleSize={4} textSize={8} title={t('page.document.detall.createdDate')} field={"createdDate"} hidden={!entity?.createdDate}>{formatDate(entity?.createdDate)}</FieldData>
                        <FieldData titleSize={4} textSize={8} field={"estat"} hiddenIfEmpty/>
                        <FieldData titleSize={4} textSize={8} field={"ntiVersion"} hiddenIfEmpty/>
                        <FieldData titleSize={4} textSize={8} field={"ntiIdentificador"} hiddenIfEmpty/>
                        <FieldData titleSize={4} textSize={8} field={"ntiOrgano"} hiddenIfEmpty/>
                        <FieldData titleSize={4} textSize={8} field={"dataCaptura"} hidden={!entity?.dataCaptura}>{formatDate(entity?.dataCaptura)}</FieldData>
                        <FieldData titleSize={4} textSize={8} field={"ntiOrigen"} hiddenIfEmpty/>
                        <FieldData titleSize={4} textSize={8} field={"ntiEstadoElaboracion"} hiddenIfEmpty/>
                        <FieldData titleSize={4} textSize={8} field={"ntiTipoDocumental"} hiddenIfEmpty/>
                    </DetailCard>
                </MuiDetail>
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
    const isOther = !isExpedient && !isDocument;

    return <>
        <GridFormField xs={3} name="nom"/>
        <GridFormField xs={3} name="createdBy"/>
        <GridFormField xs={3} name="tipus"/>
        <GridFormField xs={3} name="expedient" disabled={isOther}/>
        {/*<GridFormField xs={3} name="metaExpedient" hidden={!isExpedient}/>*/}
        {/*<GridFormField xs={3} name="metaDocument" hidden={!isDocument}/>*/}
        <GridFormField xs={1.9} name="dataEsborratInici" type={"date"}/>
        <GridFormField xs={1.9} name="dataEsborratFi" type={"date"}/>
        <GridFormField xs={2} name="esborrat" required/>
        <GridFormField xs={1.9} name="dataInici" type={"date"}/>
        <GridFormField xs={1.9} name="dataFi" type={"date"}/>
    </>
}

const springFilterBuilder = (data:any) => {
    return builder.and(
        builder.like('nom', data?.nom),
        builder.eq('createdBy', `'${data?.createdBy?.id}'`),
        builder.eq('tipus', `'${data?.tipus}'`),
        builder.betweenDates('esborratData', data?.dataEsborratInici, data?.dataEsborratFi),
        data?.esborrat != null && data?.esborrat !== 'ESBORRATS_I_NO_ESBORRATS'
            && builder.equals('esborrat', 1, data?.esborrat == 'NOMES_ESBORRATS'),
        data?.tipus === 'EXPEDIENT' && builder.eq('id', data?.expedient?.id),
        data?.tipus === 'DOCUMENT' && builder.eq('expedient.id', data?.expedient?.id),
        builder.betweenDates('createdDate', data?.dataInici, data?.dataFi),
    );
}

const ContingutFilter = (props: any) => {
    return <StyledMuiFilter
        resourceName={"contingutResource"}
        code={"FILTER"}
        springFilterBuilder={springFilterBuilder}
        {...props}
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
]

const ContingutGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = useState<string>();

    const additionalColumns = useMemo(()=> [
        ...columns,
        {
            field: 'expedient',
            headerName: t('page.contingut.grid.path'),
            flex: 1.5,
            renderCell: (params:any) => <>
                {!!params?.row?.expedient?.id && <>/<Link component={RouterLink} to={`/contingut/${params?.row?.expedient?.id}`} style={{ display: 'flex', alignItems: 'center' }}><Icon>folder_open</Icon>{params?.formattedValue}</Link></>}
                {params?.row?.pare?.id != params?.row?.expedient?.id ?<>/.../<Icon>folder</Icon>{params?.row?.pare?.description}</> :"" }
                {/*{!!params?.row?.fitxerNom && <>/<Icon>description</Icon>{params?.row?.fitxerNom}</>}*/}
                /
                {params?.row?.tipus == "EXPEDIENT" && <Icon>folder_open</Icon>}
                {params?.row?.tipus == "CARPETA" && <Icon>folder</Icon>}
                {params?.row?.tipus == "DOCUMENT" && <Icon>description</Icon>}
                {params?.row?.nom}
            </>,
        },
    ], [t])

    const sessionKey = "MASSIVE_CONTINGUT_FILTER";
    const { value: filterData } = useSession(sessionKey);
    const haveRequirements = useMemo(() =>
        filterData?.esborrat === "NOMES_ESBORRATS",
        [filterData?.esborrat])

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {handleOpen: handleDetail, dialog: dialogDetail} = useDetail();
    const {handleOpen: handleHistoricOpen, dialog: dialogHistoric} = useHistoric();
    const {handleShow: hanldeAssignar, content: assignarContent} = useAssignar(refresh);
    const {recuperar, recuperarMassive, esborrar, esborrarMassive} = useActions(refresh)

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
        {
            label: t('page.contingut.action.replay.label'),
            icon: "replay",
            showInMenu: true,
            onClick: recuperar,
            hidden: (row:any) => row?.esborrat != 1,
        },
        {
            label: t('common.delete'),
            icon: "delete",
            showInMenu: true,
            onClick: esborrar,
            hidden: (row:any) => row?.esborrat != 1,
        },
        {
            label: t('page.expedient.action.assignar.label'),
            icon: "person",
            showInMenu: true,
            onClick: hanldeAssignar,
            disabled: (row:any) => row?.esborrat == 1,
            hidden: (row:any) => row?.tipus !== 'EXPEDIENT',
        },
    ]
    const massiveActions = [
        {
            label: t('page.contingut.action.replay.label'),
            icon: "replay",
            showInMenu: true,
            onClick: recuperarMassive,
        },
        {
            label: t('common.delete'),
            icon: "delete",
            showInMenu: true,
            onClick: esborrarMassive,
        },
    ]

    return <GridPage disableMargins>
        <CardPage title={t('page.user.menu.continguts')}>
            <ContingutFilter
                sessionKey={sessionKey}
                onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={"contingutResource"}
                columns={additionalColumns}
                filter={springFilter}
                sortModel={sortModel}
                perspectives={perspectives}
                rowAdditionalActions={actions}
                toolbarMassiveActions={massiveActions}
                onRowClick={(params: any) => handleDetail(params?.row?.id)}
                isRowSelectable={(params:any) => params?.row?.esborrat == 1 }
                disabledMassiveDefSelector={!haveRequirements}
                toolbarHideCreate
            />
        </CardPage>
        {dialogHistoric}
        {dialogDetail}
        {assignarContent}
    </GridPage>
}
export default ContingutGrid;