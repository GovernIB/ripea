/**
 * 
 */
package es.caib.ripea.plugin.conversio;

import es.caib.ripea.plugin.RipeaEndpointPluginInfo;
import es.caib.ripea.plugin.SistemaExternException;


/**
 * Interfície per a la conversió de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ConversioPlugin extends RipeaEndpointPluginInfo {

	/**
	 * Converteix un arxiu a format PDF.
	 * 
	 * @param arxiu
	 *            Arxiu original.
	 * @return L'arxiu convertit.
	 * @throws SistemaExternException
	 *             Si hi ha hagut algun problema per a fer la conversió.
	 */
	public ConversioArxiu convertirPdf(
			ConversioArxiu arxiu) throws SistemaExternException;

	/**
	 * Converteix un arxiu a format PDF estampant un codi de barres
	 * amb informació d'una URL.
	 * 
	 * @param arxiu
	 *            Arxiu original.
	 * @param arxiu
	 *            URL a estampar.
	 * @return L'arxiu convertit i estampat.
	 * @throws SistemaExternException
	 *             Si hi ha hagut algun problema per a fer la conversió.
	 */
	public ConversioArxiu convertirPdfIEstamparUrl(
			ConversioArxiu arxiu,
			String url) throws SistemaExternException;

	/**
	 * Retorna el nom de l'arxiu convertit.
	 * 
	 * @param nomOriginal
	 *            El nom de l'arxiu original.
	 * @return el nom de l'arxiu convertit.
	 */
	public String getNomArxiuConvertitPdf(
			String nomOriginal);

}
