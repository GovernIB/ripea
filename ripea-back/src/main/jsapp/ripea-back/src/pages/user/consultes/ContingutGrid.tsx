import {useTranslation} from "react-i18next";
import {useMemo, useState} from "react";
import {
    GridPage,
    MuiDialog,
    useMuiDataGridApiRef,
    useFormContext,
    useResourceApiService,
    useBaseAppContext,
    BasePage
} from "reactlib";
import {CardPage, DetailCard, DetailCardContent} from "../../../components/CardData.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {Grid, Icon, Link} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import useHistoric from "../../Historic.tsx";
import Load from "../../../components/Load.tsx";
import {Link as RouterLink} from "react-router-dom";
import useAssignar from "../../expedient/actions/Assignar.tsx";
import {useSession} from "../../../components/SessionStorageContext.tsx";
import Box from "@mui/material/Box";
import { useConfirmDialogButtons } from "@src/util/buttonsOverride.tsx";

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
            componentProps: { variant: 'outlined' }
        },
    ]
    const getLabelByName = (fields: any, name:string) => {
        const field = fields?.find((f: { name: string; label: string }) => f.name === name);
        return field?.label ?? field?.name;
    }
    
    const dialog = (
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.contingut.detalle.title')}
            componentProps={{ fullWidth: true, maxWidth: 'lg' }}
            dialogContentProps={{ sx: { px: 1, py: 0 } }}
            buttons={buttons}
            buttonCallback={() => {
                handleClose();
            }}
        >
            <Load value={entity}>
                <BasePage>
                    <Grid container direction={'row'} columnSpacing={1} rowSpacing={1}>
                        <DetailCard>
                            {entity?.nom && (
                                <DetailCardContent title={getLabelByName(fields, 'nom')} size={12}>
                                    <Box display={'flex'} alignItems={'center'}>
                                        {entity?.tipus == 'EXPEDIENT' && <Icon>folder_open</Icon>}
                                        {entity?.tipus == 'CARPETA' && <Icon>folder</Icon>}
                                        {entity?.tipus == 'DOCUMENT' && <Icon>description</Icon>}
                                        {entity?.nom}
                                    </Box>
                                </DetailCardContent>
                            )}
                            {entity?.metaNode && (
                                <DetailCardContent title={getLabelByName(fields, 'metaNode')} size={12}>
                                    {entity?.metaNode?.description}
                                </DetailCardContent>
                            )}
                            {entity?.createdDate && (
                                <DetailCardContent title={t('page.document.detall.createdDate')} size={4}>
                                    {formatDate(entity?.createdDate)}
                                </DetailCardContent>
                            )}
                            {entity?.estat && (
                                <DetailCardContent title={getLabelByName(fields, 'estat')} size={4}>
                                    {entity?.estat}
                                </DetailCardContent>
                            )}
                            {entity?.ntiVersion && (
                                <DetailCardContent title={getLabelByName(fields, 'ntiVersion')} size={4}>
                                    {entity?.ntiVersion}
                                </DetailCardContent>
                            )}
                            {entity?.ntiIdentificador && (
                                <DetailCardContent title={getLabelByName(fields, 'ntiIdentificador')} size={12}>
                                    {entity?.ntiIdentificador}
                                </DetailCardContent>
                            )}
                            {entity?.ntiOrgano && (
                                <DetailCardContent title={getLabelByName(fields, 'ntiOrgano')} size={6}>
                                    {entity?.ntiOrgano}
                                </DetailCardContent>
                            )}
                            {entity?.dataCaptura && (
                                <DetailCardContent title={getLabelByName(fields, 'dataCaptura')} size={6}>
                                    {formatDate(entity?.dataCaptura)}
                                </DetailCardContent>
                            )}
                            {entity?.ntiOrigen && (
                                <DetailCardContent title={getLabelByName(fields, 'ntiOrigen')} size={4}>
                                    {entity?.ntiOrigen}
                                </DetailCardContent>
                            )}
                            {entity?.ntiEstadoElaboracion && (
                                <DetailCardContent title={getLabelByName(fields, 'ntiEstadoElaboracion')} size={4}>
                                    {entity?.ntiEstadoElaboracion}
                                </DetailCardContent>
                            )}
                            {entity?.ntiTipoDocumental && (
                                <DetailCardContent title={getLabelByName(fields, 'ntiTipoDocumental')} size={4}>
                                    {entity?.ntiTipoDocumental}
                                </DetailCardContent>
                            )}
                        </DetailCard>
                    </Grid>
                </BasePage>
            </Load>
        </MuiDialog>
    );

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
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="nom"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="createdBy"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="tipus"/>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="expedient" disabled={isOther}/>
        {/*<GridFormField size={3} name="metaExpedient" hidden={!isExpedient}/>*/}
        {/*<GridFormField size={3} name="metaDocument" hidden={!isDocument}/>*/}
        <GridFormField size={{xs: 12, sm: 6, md: 1.9}} name="dataEsborratInici" type={"date"}/>
        <GridFormField size={{xs: 12, sm: 6, md: 1.9}} name="dataEsborratFi" type={"date"}/>
        <GridFormField size={{xs: 12, sm: 6, md: 1.9}} name="dataInici" type={"date"}/>
        <GridFormField size={{xs: 12, sm: 6, md: 1.9}} name="dataFi" type={"date"}/>
        <GridFormField size={{xs: 12, sm: 6, md: 2}} name="esborrat" required/>
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
    const [esborratFilter, setEsborratFilter] = useState<string | undefined>(() => filterData?.esborrat);

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
            hidden: (row:any) => row?.esborrat == 0,
        },
        {
            label: t('common.delete'),
            icon: "delete",
            showInMenu: true,
            onClick: esborrar,
            hidden: (row:any) => row?.esborrat == 1,
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
    const massiveActions = useMemo(() => [
        {
            label: t('page.contingut.action.replay.label'),
            icon: "replay",
            showInMenu: true,
            onClick: recuperarMassive,
            hidden: esborratFilter === 'NOMES_NO_ESBORRATS',
        },
        {
            label: t('common.delete'),
            icon: "delete",
            showInMenu: true,
            onClick: esborrarMassive,
            hidden: esborratFilter === 'NOMES_ESBORRATS',
        },
    ], [esborratFilter, t, recuperarMassive, esborrarMassive])

    return <GridPage autoHeight>
        <CardPage title={t('page.user.menu.continguts')}>
            <ContingutFilter
                sessionKey={sessionKey}
                onSpringFilterChange={setSpringFilter}
                onDataChange={(data: any) => setEsborratFilter(data?.esborrat)}/>

            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={"contingutResource"}
				persistentStateKey={"contingutResource_consulta"}
                columns={additionalColumns}
                filter={springFilter}
                sortModel={sortModel}
                perspectives={perspectives}
                rowAdditionalActions={actions}
                toolbarMassiveActions={massiveActions}
                onRowClick={(params: any) => handleDetail(params?.row?.id)}
                toolbarHideCreate
            />
        </CardPage>
        {dialogHistoric}
        {dialogDetail}
        {assignarContent}
    </GridPage>
}
export default ContingutGrid;