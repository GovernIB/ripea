import {useState} from "react";
import {Alert, Box, Button, Grid, Icon, Typography, Link} from "@mui/material";
import {GridPage, useResourceApiService, MuiDialog, useBaseAppContext} from "reactlib";
import {useTranslation} from "react-i18next";
import TabComponent from "../../../components/TabComponent.tsx";
import {CardButton, DetailCard, DetailCardContent} from "../../../components/CardData.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import MetaDadaGrid from "../../dada/MetaDadaGrid.tsx";
import Load from "../../../components/Load.tsx";
import {useActions} from "./ContingutActions.tsx";
import {icons} from "../../user/UserHeadToolbar.tsx";
import useErrorValidacio from "../../expedient/details/ErrorValidacio.tsx";
import {FieldData, MuiDetail} from "../../../components/MuiDetail.tsx";

const Contenido = (props:any) => {
    const {entity, fields} = props;
    const { t } = useTranslation();

    return <Load value={entity}>
        <MuiDetail entity={entity} fields={fields}>
            <DetailCard>
                <FieldData size={6} field={"fitxerNom"}/>
                <FieldData size={6} field={"fitxerContentType"}/>
                <FieldData size={6} field={"metaDocument"}/>
                <FieldData size={6} title={t('page.document.detall.createdDate')} field={"createdDate"}>{formatDate(entity?.createdDate)}</FieldData>
                <FieldData size={6} field={"estat"}/>
                <FieldData size={6} title={t('page.document.detall.dataCaptura')} field={"dataCaptura"}>{formatDate(entity?.dataCaptura)}</FieldData>
                <FieldData size={6} field={"ntiOrigen"}/>
                <FieldData size={6} field={"ntiTipoDocumental"}/>
                <FieldData size={6} field={"ntiEstadoElaboracion"}/>
                <FieldData size={6} field={"ntiTipoFirma"}/>
                <FieldData size={12} field={"ntiCsv"} hidden={!entity?.ntiCsv}>
                    {entity?.ntiCsv} {entity?.csvLinkUrl &&
                    <Link href={entity?.csvLinkUrl+entity?.ntiCsv} target={"_blank"} rel="noopener noreferrer"><Icon>launch</Icon></Link>}
                </FieldData>
                <FieldData size={12} field={"ntiCsvRegulacion"} hiddenIfEmpty/>
            </DetailCard>
        </MuiDetail>
    </Load>
}

const Dada = (props:any) => {
    const { entity, potModificar, onRowCountChange, refresh } = props
    if (entity!=null && potModificar!=null) {
        entity['potModificar'] = potModificar;
    }

    return <GridPage autoHeight>
        <Box width={'100%'} height={110 + 52 * 4}>
            <MetaDadaGrid entity={entity} onRowCountChange={onRowCountChange} onRefresh={refresh}/>
        </Box>
    </GridPage>
}

const Versiones = (props:any) => {
    const {entity} = props;
    const { t } = useTranslation();
    const {descarregarVersio} = useActions()

    return <Grid container flexDirection={"column"} columnSpacing={1} rowSpacing={1}>
        {entity?.versions?.map((version:any) =>
            <DetailCard key={version?.id} title={t('page.document.versio.title') + ' ' + version?.id}
                        header={entity?.documentTipus != 'FISIC' && <Box ml="auto">
                            <CardButton icon={'download'}
                                        text={t('common.download')}
                                        onClick={()=>descarregarVersio(entity?.id, version?.id)}/>
                        </Box>}
            >
                <DetailCardContent title={t('page.document.versio.data')}>{!version?.data && formatDate(version?.data)}</DetailCardContent>
                <DetailCardContent title={t('page.document.versio.arxiuUuid')}>{version?.arxiuUuid}</DetailCardContent>
            </DetailCard>
        )}
    </Grid>;
}

export const Firmes = (props:any) => {
    const { entity } = props;

    return <Grid container flexDirection={"column"} columnSpacing={1} rowSpacing={1}>
        {
            entity?.firmes?.map((firma:any, index:number) => {
                return firma?.errorFirma
                    ? <Grid size={12} key={index}><Alert severity={"error"}>{firma?.errorDesc}</Alert></Grid>
                    : <>
                        {firma?.detalls?.map((detall: any, i: number) =>
                            <Grid size={12} key={`${index}-${i}`}>
                                <Icon>{icons.firma}</Icon> {detall?.responsableNom} - {detall?.responsableNif} - {formatDate(detall?.data)} - {detall?.emissorCertificat}
                            </Grid>
                        )}
                    </>
            })
        }
    </Grid>
}

const perspectives = ['VERSIONS', 'COUNT', 'FIRMES', 'RESUM']
const useDocumentDetail = (expedient:any, refresh?: () => void) => {
    const { t } = useTranslation();

    const {
        isReady: apiIsReady,
        getOne: apiGetOne,
        currentFields: fields,
    } = useResourceApiService('documentResource');
    const {temporalMessageShow} = useBaseAppContext();

    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();
    const [numDades, setNumDades] = useState<number>(entity?.numDades);

    const {apiDownload} = useActions()
    const {handleOpen: hanldeErrorValidacio, dialog: dialogErrorValidacio} = useErrorValidacio();

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

    const tabs = [
        {
            value: 'resum',
            label: t('page.document.tabs.resum'),
            content: <Contenido entity={entity} fields={fields}/>,
        },
        {
            value: "dades",
            label: t('page.contingut.tabs.dades'),
            content: <Dada entity={entity} potModificar={expedient?.potModificar} onRowCountChange={setNumDades} refresh={refresh}/>,
            badge: numDades ?? entity?.numDades,
            hidden: !entity?.numMetaDades,
        },
        {
            value: "version",
            label: t('page.document.tabs.version'),
            content: <Versiones entity={entity}/>,
            badge: entity?.versions?.length,
            hidden: !entity?.versions || entity?.versions?.length == 0,
        },
        {
            value: "firmes",
            label: t('page.document.tabs.firmes'),
            content: <Firmes entity={entity}/>,
            badge: entity?.firmes?.length,
            hidden: !entity?.firmes || entity?.firmes?.length == 0,
        },
    ]

    const buttons :any[] = [
        {
            value: 'download',
            text: t('page.document.action.descarregarOriginal.label'),
            icon: 'download',
            hidden: entity?.documentTipus == 'FISIC'
        },
        {
            value: 'descarregarImprimible',
            text: t('page.document.action.imprimible.label'),
            icon: 'download',
            hidden: entity?.estat != 'CUSTODIAT'
        },
    ]
        .filter((button:any)=>!button?.hidden)

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={entity?.nom}
            componentProps={{ fullWidth: true, maxWidth: 'md' }}
            buttons={buttons}
            buttonCallback={(value :any) :void => {
                switch (value){
                    case 'download':
                        apiDownload(entity?.id, 'adjunt', t('page.document.action.descarregarOriginal.ok'))
                        break;
                    case 'descarregarImprimible':
                        apiDownload(entity?.id, 'imprimible', t('page.document.action.imprimible.ok'))
                        break;
                }
                handleClose();
            }}
        >
            <Load value={entity}>
            {!entity?.valid &&
                <Alert severity="warning"
                       action={
                           <Button  sx={{py: 0}} variant="outlined"
                                    onClick={() => hanldeErrorValidacio(entity?.id, entity)}>
                               <Icon>search</Icon>
                               <Typography variant={"subtitle2"}>{t('common.consult')}</Typography>
                           </Button>
                       }
                >{t('page.contingut.alert.valid')}</Alert>}
            {dialogErrorValidacio}
            <TabComponent
                indicatorColor={"primary"}
                textColor={"primary"}
                aria-label="scrollable force tabs"
                tabs={tabs}
                variant="scrollable"
            />
            </Load>
        </MuiDialog>

    return {
        apiIsReady,
        handleOpen,
        handleClose,
        dialog
    }
}
export default useDocumentDetail;