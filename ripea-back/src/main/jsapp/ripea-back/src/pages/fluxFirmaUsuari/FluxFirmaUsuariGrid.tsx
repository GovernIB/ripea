import {useState} from "react";
import {useTranslation} from "react-i18next";
import {GridPage, MuiDialog, useBaseAppContext, useMuiDataGridApiRef, useResourceApiService} from "reactlib";
import {Typography} from "@mui/material";
import DOMPurify from "dompurify";
import {CardPage} from "@src/components/CardData.tsx";
import StyledMuiGrid, {ToolbarButton} from "@src/components/StyledMuiGrid.tsx";
import Iframe from "@src/components/Iframe.tsx";
import Load from "@src/components/Load.tsx";
import {useFluxFinalitzatSession} from "@src/components/SseClient.tsx";

const RESOURCE_NAME = 'fluxFirmaUsuariResource';

// Marca que el servidor deixa dins de l'HTML dels destinataris perquè la interfície hi posi
// el text traduït (mateix mecanisme que fluxFirmaUsuariList.jsp).
const OBLIGATORI_PLACEHOLDER = 'OBLIGATORI_TEXT';

const sortModel: any = [{field: 'nom', sort: 'asc'}];

/**
 * El camp destinataris arriba del servidor com a HTML (una línia per destinatari, amb els
 * revisors entre claudàtors i una marca per als firmants obligatoris). Es sanititza abans
 * de pintar-lo perquè el contingut prové de PortaFIB.
 */
const DestinatarisCell = (props: {value?: string}) => {
    const {t} = useTranslation();
    const {value} = props;

    if (!value) {
        return null;
    }

    const html = DOMPurify.sanitize(
        value.split(OBLIGATORI_PLACEHOLDER).join(t('page.fluxFirmaUsuari.destinataris.obligatori')));

    return <Typography
        variant="body2"
        sx={{py: 1, '& #firma-obligat, & .firma-obligat': {color: 'error.main', fontWeight: 'bold'}}}
        dangerouslySetInnerHTML={{__html: html}}/>
}

const columns = [
    {
        field: 'nom',
        flex: 0.8,
    },
    {
        field: 'destinataris',
        flex: 1.5,
        sortable: false,
        renderCell: (params: any) => <DestinatarisCell value={params?.value}/>,
    },
    {
        field: 'createdDate',
        flex: 0.5,
    },
];

/**
 * Creació i modificació del flux: totes dues es fan dins d'un iframe amb la interfície de
 * PortaFIB. L'acció del recurs només retorna la url a mostrar; quan l'usuari acaba, PortaFIB
 * torna a RIPEA (FluxFirmaUsuariController), que desa el flux i avisa per SSE. És l'event
 * SSE el que tanca el diàleg i refresca el llistat.
 */
const useFluxPortafirmesDialog = (refresh?: () => void) => {
    const {t} = useTranslation();
    const {temporalMessageShow} = useBaseAppContext();
    const {isReady: apiIsReady, artifactAction: apiAction} = useResourceApiService(RESOURCE_NAME);
    const [open, setOpen] = useState(false);
    const [url, setUrl] = useState<string>();
    const [edicio, setEdicio] = useState(false);

    const handleOpen = (id?: any) => {
        if (!apiIsReady) {
            return;
        }
        setEdicio(!!id);
        setUrl(undefined);
        setOpen(true);
        const action = id
            ? apiAction(id, {code: 'EDITAR_FLUX'})
            : apiAction(undefined, {code: 'CREAR_FLUX'});
        action
            .then((result: any) => setUrl(result?.url))
            .catch((error: any) => {
                // Si PortaFIB no respon no hi ha url per mostrar: es tanca el diàleg i s'avisa
                // l'usuari en lloc de deixar-lo amb l'indicador de càrrega indefinidament.
                setOpen(false);
                temporalMessageShow(
                    null,
                    error?.message ?? t('page.fluxFirmaUsuari.action.error'),
                    'error');
            });
    }

    const handleClose = (reason?: string) => {
        if (reason !== 'backdropClick') {
            setUrl(undefined);
            setOpen(false);
            // Xarxa de seguretat: si l'event SSE no ha arribat (connexió caiguda) el llistat
            // es refresca igualment en tancar el diàleg.
            refresh?.();
        }
    };

    const {onChange, remove} = useFluxFinalitzatSession();
    onChange((flux) => {
        // Els fluxos dels meta-documents comparteixen event: aquests duen meta-document.
        if (flux?.metaDocumentId) {
            return;
        }
        remove();
        setOpen(false);
        setUrl(undefined);
        if (flux?.fluxCreat?.error) {
            temporalMessageShow(
                null,
                flux?.fluxCreat?.descripcio ?? t('page.fluxFirmaUsuari.action.error'),
                'error');
        } else {
            temporalMessageShow(
                null,
                t(edicio ? 'page.fluxFirmaUsuari.action.update.ok' : 'page.fluxFirmaUsuari.action.new.ok'),
                'success');
        }
        refresh?.();
    });

    const dialog = <MuiDialog
        open={open}
        closeCallback={handleClose}
        title={t(edicio ? 'page.fluxFirmaUsuari.action.update.title' : 'page.fluxFirmaUsuari.action.new.title')}
        componentProps={{fullWidth: true, maxWidth: 'lg'}}
        buttons={[
            {
                value: 'close',
                text: t('common.close'),
                componentProps: {variant: 'outlined'}
            },
        ]}
        buttonCallback={(value: any): void => {
            if (value == 'close') {
                handleClose();
            }
        }}
    >
        <Load value={url}>
            <Iframe src={url} style={{height: '70vh'}}/>
        </Load>
    </MuiDialog>

    return {
        apiIsReady,
        handleOpen,
        dialog,
    }
}

const FluxFirmaUsuariGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {apiIsReady, handleOpen, dialog} = useFluxPortafirmesDialog(refresh);

    const actions = [
        {
            label: t('common.update'),
            icon: 'edit',
            showInMenu: true,
            onClick: (id: any) => handleOpen(id),
        },
        {
            label: t('common.delete'),
            icon: 'delete',
            showInMenu: true,
            clickTriggerDelete: true,
        },
    ];

    return <GridPage autoHeight>
        <CardPage title={t('page.user.menu.flux')}>
            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={RESOURCE_NAME}
                columns={columns}
                sortModel={sortModel}
                rowAdditionalActions={actions}
                toolbarShowQuickFilter
                toolbarShowCreate={false}
                toolbarElementsWithPositions={[
                    {
                        position: 3,
                        element: <ToolbarButton
                            title={t('page.fluxFirmaUsuari.action.new.label')}
                            icon={'add'}
                            color={'primary'}
                            disabled={!apiIsReady}
                            onClick={() => handleOpen()}>{t('page.fluxFirmaUsuari.action.new.label')}</ToolbarButton>,
                    },
                ]}
                popupEditFormI18nKeys={{
                    deleteSuccess: 'page.fluxFirmaUsuari.action.delete.ok',
                }}
            />
            {dialog}
        </CardPage>
    </GridPage>
}

export default FluxFirmaUsuariGrid;
