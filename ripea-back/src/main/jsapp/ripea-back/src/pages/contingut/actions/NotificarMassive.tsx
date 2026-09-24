import {Grid} from "@mui/material";
import {useMuiFormDialogApiRef, useBaseAppContext, useFormContext, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import GridFormField from "../../../components/GridFormField.tsx";
import useNotificar from "./Notificar.tsx";
import useOrdenarDocuments from "./OrdenarDocuments.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import * as builder from "../../../util/springFilterUtils.ts";

const CONTENT_TYPE_PDF = 'application/pdf';
const CODI_METADOC_NOTIFICACIO_MULTIPLE = 'NOTIFICACIO_MULTIPLE';

/** Els mateixos tipus de document que s'ofereixen en crear un document nou a l'expedient. */
const metaDocumentFilter = (metaExpedientId:any) :string => builder.and(
    builder.eq('metaExpedient.id', metaExpedientId),
    builder.eq('actiu', true),
    builder.eq('pinbalActiu', false),
)
const metaDocumentNamedQueries = (expedientId:any) :string[] => [`CREATE_NEW_DOC#${expedientId}`];

const NotificarMassiveForm = () => {
    const {data} = useFormContext();

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="metaDocument"
                       filter={metaDocumentFilter(data?.metaExpedient?.id)}
                       namedQueries={metaDocumentNamedQueries(data?.expedientId)}
                       required/>
        <GridFormField name="ntiOrigen" required/>
        <GridFormField name="ntiEstadoElaboracion" required/>
    </Grid>
}

const NotificarMassive = (props:any) => {
    const { t } = useTranslation();
    return <FormActionDialog
        resourceName={"documentResource"}
        action={"MASSIVE_NOTIFICAR"}
        title={t('page.document.action.notificarMasiva.tipusDoc.title')}
        formDialogButtons={[
            {icon: 'send', text: t('page.document.action.notificarMasiva.tipusDoc.button'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        {...props}
    >
        <NotificarMassiveForm/>
    </FormActionDialog>
}

/**
 * Notificació conjunta dels documents seleccionats a la graella de contingut de l'expedient.
 *
 * - Un sol document: no es genera res, es notifica el document seleccionat.
 * - Tots PDF i concatenació activa: l'usuari tria entre combinar-los en un únic PDF, en l'ordre
 *   que indiqui, o comprimir-los en un zip.
 * - Altrament: es genera un zip amb els documents.
 *
 * En els dos darrers casos, el tipus de document que s'aplica al document generat depèn de la
 * propietat es.caib.ripea.notificacio.multiple.tipusdoc: si està activada es demana a l'usuari,
 * si no s'aplica el tipus NOTIFICACIO_MULTIPLE del procediment, que és qui aporta les dades NTI.
 * Si el procediment no té el tipus NOTIFICACIO_MULTIPLE (es pot haver desactivat amb la propietat
 * es.caib.ripea.metadocument.defecte.notificacio.multiple.actiu), també es demana a l'usuari.
 */
const useNotificarMassive = (entity:any, refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    const {temporalMessageShow} = useBaseAppContext();
    const { value: user } = useUserSession();
    const {
        isReady: apiIsReady,
        find: apiFindAll,
        artifactAction: apiAction,
    } = useResourceApiService('documentResource');
    const {
        find: apiMetaDocumentFindAll,
    } = useResourceApiService('metaDocumentResource');

    const {handleShow: handleNotificar, content} = useNotificar(refresh)

    const documentGenerat = (result:any) :void => {
        refresh?.();
        temporalMessageShow(null, t('page.document.action.notificarMasiva.ok'), 'success');
        handleNotificar(result?.id, result);
    }

    // El servidor és qui decideix si combina els documents en un PDF o els comprimeix en un zip,
    // i els agrupa en l'ordre en què li arriben els identificadors. Amb concatenar a false es
    // genera un zip encara que es puguin combinar (l'usuari ho ha triat al diàleg d'ordenació).
    const generarDocument = (ids:any[], concatenar?:boolean) :void => {
        apiAction(undefined, {code: 'MASSIVE_NOTIFICAR', data: {ids, massivo: true, concatenar}})
            .then(documentGenerat)
            .catch((error:any) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    // Tipus de document NOTIFICACIO_MULTIPLE del procediment, si existeix i és un dels que
    // s'ofereixen al desplegable del diàleg. Si no es troba o la consulta falla, el diàleg
    // s'obre igualment sense tipus preseleccionat.
    const findMetaDocumentNotificacioMultiple = () :Promise<any> =>
        apiMetaDocumentFindAll({
            unpaged: true,
            filter: builder.and(
                metaDocumentFilter(entity?.metaExpedient?.id),
                builder.eq('codi', `'${CODI_METADOC_NOTIFICACIO_MULTIPLE}'`),
            ),
            namedQueries: metaDocumentNamedQueries(entity?.id),
        })
            .then((resposta:any) => resposta?.rows?.[0])
            .catch(() => undefined);

    // Amb la propietat activada, o si el procediment no té el tipus NOTIFICACIO_MULTIPLE, el tipus
    // de document del document generat el tria l'usuari, i és el propi diàleg qui executa l'acció;
    // si no, es genera directament. Si existeix, es proposa el tipus NOTIFICACIO_MULTIPLE del
    // procediment amb les seves dades NTI (l'onChange del servidor només les omple quan l'usuari
    // canvia el tipus).
    const generarDocumentTriantTipus = (ids:any[], concatenar?:boolean) :void => {
        findMetaDocumentNotificacioMultiple().then((metaDocument:any) => {
            if (!user?.sessionScope?.isNotificacioMultipleTipusDocActiu && metaDocument) {
                generarDocument(ids, concatenar);
                return;
            }
            apiRef.current?.show?.(undefined, {
                ids,
                massivo: true,
                concatenar,
                metaExpedient: entity?.metaExpedient,
                expedientId: entity?.id,
                ...(metaDocument && {
                    metaDocument: {id: metaDocument.id, description: metaDocument.nom},
                    ntiOrigen: metaDocument.ntiOrigen,
                    ntiEstadoElaboracion: metaDocument.ntiEstadoElaboracion,
                }),
            });
        });
    }

    const {handleOpen: handleOrdenarOpen, dialog: dialogOrdenar} = useOrdenarDocuments(
        (documents:any[], concatenar:boolean) =>
            generarDocumentTriantTipus(documents.map((doc:any) => doc?.id), concatenar));

    const handleMassiveShow = (ids:any[]) :void => {
        if (!apiIsReady || !ids?.length) {
            return;
        }
        apiFindAll({unpaged: true, filter: builder.inside('id', ids)})
            .then((resposta:any) => {
                // La consulta no manté l'ordre de la selecció: es recompon per poder oferir a
                // l'usuari el mateix ordre en què ha seleccionat els documents.
                const documents = ids
                    .map((id:any) => resposta?.rows?.find((row:any) => row?.id == id))
                    .filter((doc:any) => doc != null);
                if (documents.length === 0) {
                    return;
                }
                // Mateixa validació que la interfície clàssica: no es notifica cap document
                // sense firmar, ni tot sol ni agrupat amb els altres.
                const senseFirma = documents.filter((doc:any) => doc?.documentFirmaTipus === 'SENSE_FIRMA');
                if (senseFirma.length) {
                    temporalMessageShow(
                        null,
                        t('page.document.action.notificarMasiva.error.noFirmats',
                            {noms: senseFirma.map((doc:any) => doc?.nom).join(', ')}),
                        'error');
                    return;
                }
                if (documents.length === 1) {
                    handleNotificar(documents[0]?.id, documents[0]);
                    return;
                }
                const totsPdf = documents.every((doc:any) => doc?.fitxerContentType === CONTENT_TYPE_PDF);
                if (totsPdf && user?.sessionScope?.isConcatenarPdfsActiu) {
                    handleOrdenarOpen(documents);
                } else {
                    generarDocumentTriantTipus(documents.map((doc:any) => doc?.id));
                }
            })
            .catch((error:any) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {
        handleMassiveShow,
        content: <>
            {dialogOrdenar}
            <NotificarMassive apiRef={apiRef} onSuccess={documentGenerat}/>
            {content}
        </>
    }
}
export default useNotificarMassive;
