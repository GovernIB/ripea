import {useBaseAppContext, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import useNotificar from "./Notificar.tsx";
import useOrdenarDocuments from "./OrdenarDocuments.tsx";
import {useUserSession} from "../../../components/Session.tsx";
import * as builder from "../../../util/springFilterUtils.ts";

const CONTENT_TYPE_PDF = 'application/pdf';

/**
 * Notificació conjunta dels documents seleccionats a la graella de contingut de l'expedient.
 *
 * - Un sol document: no es genera res, es notifica el document seleccionat.
 * - Tots PDF i concatenació activa: es demana l'ordre i es genera un únic PDF amb tots ells.
 * - Altrament: es genera un zip amb els documents.
 *
 * En els dos darrers casos el document generat s'afegeix a l'expedient amb el tipus de document
 * NOTIFICACIO_MULTIPLE, que és qui aporta les dades NTI; per això ja no hi ha cap pas intermedi
 * per demanar-les a l'usuari.
 */
const useNotificarMassive = (refresh?: () => void) => {
    const { t } = useTranslation();
    const {temporalMessageShow} = useBaseAppContext();
    const { value: user } = useUserSession();
    const {
        isReady: apiIsReady,
        find: apiFindAll,
        artifactAction: apiAction,
    } = useResourceApiService('documentResource');

    const {handleShow: handleNotificar, content} = useNotificar(refresh)

    // El servidor és qui decideix si combina els documents en un PDF o els comprimeix en un zip,
    // i els agrupa en l'ordre en què li arriben els identificadors.
    const generarDocument = (ids:any[]) :void => {
        apiAction(undefined, {code: 'MASSIVE_NOTIFICAR', data: {ids, massivo: true}})
            .then((result:any) => {
                refresh?.();
                temporalMessageShow(null, t('page.document.action.notificarMasiva.ok'), 'success');
                handleNotificar(result?.id, result);
            })
            .catch((error:any) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const {handleOpen: handleOrdenarOpen, dialog: dialogOrdenar} = useOrdenarDocuments(
        (documents:any[]) => generarDocument(documents.map((doc:any) => doc?.id)));

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
                    generarDocument(documents.map((doc:any) => doc?.id));
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
            {content}
        </>
    }
}
export default useNotificarMassive;
