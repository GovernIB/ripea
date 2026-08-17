import {useEffect, useState} from "react";
import {Box, Grid} from "@mui/material";
import {BasePage, MuiDialog, useBaseAppContext, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import {CardButton, DetailCard, DetailCardContent} from "../../../components/CardData.tsx";
import Load from "../../../components/Load.tsx";
import TabComponent from "../../../components/TabComponent.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import {useUserSession} from "../../../components/Session.tsx";
import {useActions, useAnexxActions} from "./AnotacioActions.tsx";
import useRegistreInteressatDetail from "./RegistreInteressatDetail.tsx";
import useVisualitzar from "../actions/Visualitzar.tsx";
import useAnnexFirma from "./AnnexFirma.tsx";
import { icons as iconsAppMenu } from '@src/util/icons';
import {icons} from "@src/util/icons.ts";

const Resum = (props:any) => {
    const { entity, setNumInteressats, setNumAnnexos } = props;
    const registre = entity?.registreInfo;
    const { t } = useTranslation();

    return <BasePage>
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            <DetailCard title={t('page.registre.detall.identifier')}>
                <DetailCardContent title={t('page.registre.detall.identificador')} size={6}>{registre?.identificador}</DetailCardContent>
                <DetailCardContent title={t('page.registre.detall.data')}          size={6}>{formatDate(registre?.data)}</DetailCardContent>
                <DetailCardContent title={t('page.registre.detall.oficina')}       size={6}>{registre?.oficinaDescripcio} ({registre?.oficinaCodi})</DetailCardContent>
                <DetailCardContent title={t('page.registre.detall.extracte')}      size={6}>{registre?.extracte}</DetailCardContent>
            </DetailCard>

            <DetailCard title={t('page.registre.detall.registre')}>
                <DetailCardContent title={t('page.registre.detall.observacions')}      size={6}>{registre?.observacions}</DetailCardContent>
                <DetailCardContent title={t('page.registre.detall.identificador')}     size={6}>{registre?.identificador}</DetailCardContent>
                <DetailCardContent title={t('page.registre.detall.data')}              size={6}>{formatDate(registre?.data)}</DetailCardContent>
                <DetailCardContent title={t('page.registre.detall.oficinaDescripcio')} size={6}>{registre?.oficinaDescripcio}</DetailCardContent>
            </DetailCard>

            <DetailCard title={t('page.registre.detall.infoResumida')}>
                <DetailCardContent title={t('page.registre.detall.docFisica')}         size={6}>{registre?.docFisicaCodi} - {registre?.docFisicaDescripcio}</DetailCardContent>
                <DetailCardContent title={t('page.registre.detall.desti')}             size={6}>{registre?.destiDescripcio} ({registre?.destiCodi})</DetailCardContent>
                <DetailCardContent title={t('page.registre.detall.refExterna')}        size={6}>{registre?.refExterna}</DetailCardContent>
                <DetailCardContent title={t('page.registre.detall.expedientNumero')}   size={6}>{registre?.expedientNumero}</DetailCardContent>
                <DetailCardContent title={t('page.registre.detall.procediment')}       size={12}>{registre?.procedimentCodi} - {entity?.metaExpedient?.description}</DetailCardContent>
            </DetailCard>

            <DetailCard title={t('page.registre.detall.interessats')}>
                <Interessats entity={registre} onRowCountChange={setNumInteressats} autoHeight/>
            </DetailCard>

            <DetailCard title={t('page.registre.detall.annexos')}>
                <Annexos entity={registre} onRowCountChange={setNumAnnexos} autoHeight/>
            </DetailCard>
        </Grid>
    </BasePage>
}

const Estat = (props:any) => {
    const { entity } = props;
    const { t } = useTranslation();

    return <BasePage>
        <DetailCard>
            <DetailCardContent title={t('page.anotacio.detall.estatView')} size={6}>{entity?.estatView}</DetailCardContent>
            <DetailCardContent title={t('page.anotacio.detall.dataAlta')} size={6}>{formatDate(entity?.dataAlta)}</DetailCardContent>
            {entity?.estat == 'REBUTJAT' &&
                <DetailCardContent title={t('page.anotacio.detall.observacions')} size={6}>{entity?.observacions}</DetailCardContent>
            }
            {entity?.estat != 'PENDENT' &&
                <>
                    <DetailCardContent title={entity?.estat == 'REBUTJAT'
                        ?t('page.anotacio.detall.rejectedDate')
                        :t('page.anotacio.detall.acceptedDate')}
                        size={6}
                    >
                        {formatDate(entity?.dataActualitzacio)}
                    </DetailCardContent>

                    <DetailCardContent title={t('page.anotacio.detall.usuariActualitzacio')} size={6}>{entity?.usuariActualitzacio?.description}</DetailCardContent>
                </>
            }
        </DetailCard>
    </BasePage>
}

const InformeRegistre = (props:any) => {
    const { entity } = props;
    const { t } = useTranslation();

    return <BasePage>
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            <DetailCard>
                <DetailCardContent title={t('page.registre.detall.tipus')}          size={6}>{t('page.registre.detall.entrada')}</DetailCardContent>
                <DetailCardContent title={t('page.registre.detall.identificador')}  size={6}>{entity?.identificador}</DetailCardContent>
                <DetailCardContent title={t('page.registre.detall.data')}           size={6}>{formatDate(entity?.data)}</DetailCardContent>
                <DetailCardContent title={t('page.registre.detall.oficina')}        size={6}>{entity?.oficinaDescripcio} ({entity?.oficinaCodi})</DetailCardContent>
            </DetailCard>

            <DetailCard size={6} title={t('page.registre.detall.required')}>
                <DetailCardContent title={t('page.registre.detall.llibre')}     >{entity?.llibreDescripcio} ({entity?.llibreCodi})</DetailCardContent>
                <DetailCardContent title={t('page.registre.detall.extracte')}   >{entity?.extracte}</DetailCardContent>
                <DetailCardContent title={t('page.registre.detall.docFisica')}  >{entity?.docFisicaCodi} - {entity?.docFisicaDescripcio}</DetailCardContent>
                <DetailCardContent title={t('page.registre.detall.desti')}      >{entity?.destiDescripcio} ({entity?.destiCodi})</DetailCardContent>
                <DetailCardContent title={t('page.registre.detall.assumpte')}   >{entity?.assumpteTipusDescripcio} ({entity?.assumpteTipusCodi})</DetailCardContent>
                <DetailCardContent title={t('page.registre.detall.idioma')}     >{entity?.idiomaDescripcio} ({entity?.idiomaCodi})</DetailCardContent>
            </DetailCard>

            <DetailCard size={6} title={t('page.registre.detall.optional')}>
                <DetailCardContent title={t('page.registre.detall.assumpteCodi')}         >{entity?.assumpteCodiCodi}</DetailCardContent>
                <DetailCardContent title={t('page.registre.detall.refExterna')}           >{entity?.refExterna}</DetailCardContent>
                <DetailCardContent title={t('page.registre.detall.expedientNumero')}      >{entity?.expedientNumero}</DetailCardContent>
                <DetailCardContent title={t('page.registre.detall.transport')}            >{entity?.transportTipusDescripcio}</DetailCardContent>
                <DetailCardContent title={t('page.registre.detall.transportNumero')}      >{entity?.transportNumero}</DetailCardContent>
                <DetailCardContent title={t('page.registre.detall.origenRegistreNumero')} >{entity?.origenRegistreNumero}</DetailCardContent>
                <DetailCardContent title={t('page.registre.detall.origenData')}           >{formatDate(entity?.origenData)}</DetailCardContent>
                <DetailCardContent title={t('page.registre.detall.observacions')}         >{entity?.observacions}</DetailCardContent>
            </DetailCard>
        </Grid>
    </BasePage>
}

const interessatsColumns = [
    {
        field: 'tipus',
        flex: 0.5,
    },
    {
        field: 'documentNumero',
        flex: 0.5,
        valueFormatter: (value:any, row:any) => row?.documentTipus + ":" + value,
    },
    {
        field: 'nom',
        flex: 1,
        valueFormatter: (value:any, row:any) => {
            return row?.tipus == 'PERSONA_FISICA'
                ? value + " " + row?.llinatge1 + " " + row?.llinatge2
                : row?.raoSocial;
        },
    },
];

const interessatsPerspectives:any[] = ['REPRESENTANT']
const interessatsSortModel:any = [{field: 'id', sort: 'asc'}];
const Interessats = (props:any) => {
    const { entity, onRowCountChange, autoHeight = false } = props;
    const { t } = useTranslation()

    const {handleOpen, dialog} = useRegistreInteressatDetail();

    const actions = [
        {
            label: t('common.detail'),
            icon: "info",
            showInMenu: false,
            onClick: handleOpen,
            hidden: (row:any) => row?.tipus == "ADMINISTRACIO"
        },
    ]

    return <>
        <StyledMuiGrid
            resourceName={'registreInteressatResource'}
            persistentStateActive={false}
            columns={interessatsColumns}
            filter={`registre.id:${entity?.id}`}
            perspectives={interessatsPerspectives}
            staticSortModel={interessatsSortModel}
            rowAdditionalActions={actions}
            toolbarHide
            disableColumnSorting
            readOnly
            autoHeight={autoHeight}
            paginationActive={false}
            onRowCountChange={onRowCountChange}
            onRowClick={(params: any) => {
                if (params?.row?.tipus != "ADMINISTRACIO") {
                    handleOpen(params?.id, params?.row)
                }
            }}
        />
        {dialog}
    </>
}

const annexosColumns = [
    {
        field: 'titol',
        flex: 1,
    },
    {
        field: 'ntiTipoDocumental',
        flex: 0.5,
    },
    {
        field: 'observacions',
        flex: 1,
    },
    {
        field: 'ntiFechaCaptura',
        flex: 1,
        valueFormatter: (value: any) => formatDate(value)
    },
    {
        field: 'ntiOrigen',
        flex: 0.5,
    },
    {
        field: 'ntiEstadoElaboracion',
        flex: 0.5,
    },
    {
        field: 'annexArxiuEstat',
        flex: 0.5,
    },
];

const annexosSortModel:any = [{field: 'id', sort: 'asc'}];
const Annexos = (props:any) => {
    const { entity, onRowCountChange, autoHeight = false } = props;
    const { t } = useTranslation();

    const { handleOpen: handleAnnexFirma, dialog: dialogAnnexFirma } = useAnnexFirma();
    const { handleOpen: handleVisualitzar, dialog: dialogVisualitzar, isValid } = useVisualitzar()
    const {download} = useAnexxActions()

    const actions = [
        {
            label: t('page.document.action.view.label'),
            icon: icons.visualitza,
            showInMenu: true,
            onClick: handleVisualitzar,
            hidden: (row:any) => !isValid(row),
        },
        {
            label: t('page.anotacio.action.firma.label'),
            icon: "edit",
            showInMenu: true,
            onClick: handleAnnexFirma,
            //Només es mira el tipus de firma, que és un camp de l'annex a BBDD. Les firmes en si les
            //carrega el diàleg en obrir-se, perquè obtenir-les costa dues crides remotes per annex.
            hidden: (row:any) => row?.firmaTipus == null,
        },
        {
            label: t('page.anotacio.action.descargarAnnex.label'),
            icon: "download",
            showInMenu: true,
            onClick: download,
            hidden: (row:any) => !row?.uuid,
        },
    ]

    return <>
        <StyledMuiGrid
            resourceName="registreAnnexResource"
            persistentStateActive={false}
            columns={annexosColumns}
            filter={builder.and(builder.eq('registre.id',entity?.id))}
            staticSortModel={annexosSortModel}
            toolbarHide
            disableColumnSorting
            rowAdditionalActions={actions}
            readOnly
            autoHeight={autoHeight}
            paginationActive={false}
            onRowCountChange={onRowCountChange}
            onRowClick={(params: any) => {
                if (isValid(params?.row)) {
                    handleVisualitzar(params?.id)
                }
            }}
        />
        {dialogAnnexFirma}
        {dialogVisualitzar}
    </>
}

// Les metadades del justificant surten d'una consulta a l'Arxiu, així que es demanen amb una perspectiva
// pròpia i només en obrir aquesta pestanya. El llistat només rep teJustificant (perspectiva REGISTRE).
const justificantPerspectives = ['REGISTRE', 'JUSTIFICANT'];

const Justificant = (props:any) => {
    const { id, value, onChange } = props;
    const { t } = useTranslation();
    const { isReady, getOne } = useResourceApiService('expedientPeticioResource');
    const { temporalMessageShow } = useBaseAppContext();

    const {downloadJustificant} = useActions()

    // El TabComponent només munta el contingut de la pestanya activa, així que la consulta es fa en
    // clicar-la. El resultat es guarda al hook del diàleg (value) per no repetir-la si s'hi torna.
    useEffect(() => {
        if (!id || !isReady || value !== undefined) {
            return;
        }
        let vigent = true;
        getOne(id, {perspectives: justificantPerspectives})
            .then((anotacio:any) => {
                if (vigent) {
                    onChange(anotacio?.registreInfo?.justificant ?? null);
                }
            })
            .catch((error:any) => {
                if (vigent) {
                    temporalMessageShow(null, error.message, 'error');
                    onChange(null);
                }
            });
        return () => { vigent = false };
    }, [id, isReady, value]);

    const entity = value;

    return <Load value={value !== undefined}>
    <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <DetailCard icon={'description'} title={entity?.titol}
                    header={<Box ml="auto">
                        <CardButton icon={'download'}
                                    text={t('page.anotacio.action.justificant.label')}
                                    onClick={()=>downloadJustificant(id)}/>
                    </Box>}
        >
            <DetailCardContent title={t('page.registre.justificant.ntiFechaCaptura')}   size={6}>{formatDate(entity?.ntiFechaCaptura)}</DetailCardContent>
            <DetailCardContent title={t('page.registre.justificant.ntiOrigen')}         size={6}>{entity?.ntiOrigen}</DetailCardContent>
            <DetailCardContent title={t('page.registre.justificant.ntiTipoDocumental')} size={6}>{entity?.ntiTipoDocumental}</DetailCardContent>
            <DetailCardContent title={t('page.registre.justificant.uuid')}              size={6}>{entity?.uuid}</DetailCardContent>
            <DetailCardContent title={t('page.registre.justificant.titol')}             size={12}>{entity?.titol}</DetailCardContent>
        </DetailCard>

        <DetailCard icon={iconsAppMenu.firma} title={t('page.arxiu.firma.title')}>
            <DetailCardContent title={t('page.registre.justificant.firmaTipus')}    size={6}>{entity?.firmaTipus}</DetailCardContent>
            <DetailCardContent title={t('page.registre.justificant.firmaPerfil')}   size={6}>{entity?.firmaPerfil}</DetailCardContent>
        </DetailCard>
    </Grid>
    </Load>
}

const useAnotacioDetail = () => {
    const { t } = useTranslation();
    const { value: user } = useUserSession();

    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();
    const [numInteressats, setNumInteressats] = useState<number>();
    const [numAnnexos, setNumAnnexos] = useState<number>();
    //undefined = encara no consultat a l'Arxiu; null = consultat i sense dades
    const [justificant, setJustificant] = useState<any>();

    const handleOpen = (id:any, row:any) => {
        setEntity(row);
        setJustificant(undefined);
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setOpen(false);
        }
    };

    const tabs = [
        {
            value: "resum",
            label: t('page.anotacio.tabs.resum'),
            content: <Resum entity={entity} setNumInteressats={setNumInteressats} setNumAnnexos={setNumAnnexos}/>,
        },
        {
            value: "estat",
            label: t('page.anotacio.tabs.estat'),
            content: <Estat entity={entity}/>,
        },
        {
            value: "registre",
            label: t('page.anotacio.tabs.registre'),
            content: <InformeRegistre entity={entity?.registreInfo}/>,
        },
        {
            value: "interessats",
            label: t('page.anotacio.tabs.interessats'),
            content: <Interessats entity={entity?.registreInfo} onRowCountChange={setNumInteressats}/>,
            badge: numInteressats,
        },
        {
            value: "annexos",
            label: t('page.anotacio.tabs.annexos'),
            content: <Annexos entity={entity?.registreInfo} onRowCountChange={setNumAnnexos}/>,
            badge: numAnnexos,
        },
        {
            value: "justificant",
            label: t('page.anotacio.tabs.justificant'),
            content: <Justificant id={entity?.id} value={justificant} onChange={setJustificant}/>,
            hidden: !user?.sessionScope?.isIncorporacioJustificantActiva || !entity?.teJustificant,
        },
    ]

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.anotacio.detall.title')}
            componentProps={{ fullWidth: true, maxWidth: 'lg' }}
            dialogContentProps={{ sx: { height: '100%', px: 2, py: 0 } }}
            buttons={[
                {
                    value: 'close',
                    text: t('common.close'),
                    componentProps: { variant: 'outlined' }
                },
            ]}
            buttonCallback={(value :any) :void=>{
                if (value=='close') {
                    handleClose();
                }
            }}
        >
            <Box sx={{ height: '700px', minHeight: 0 }}>
                <TabComponent
                    indicatorColor={"primary"}
                    textColor={"primary"}
                    aria-label="scrollable force tabs"
                    tabs={tabs}
                    variant="scrollable"
                />
            </Box>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}

export default useAnotacioDetail;