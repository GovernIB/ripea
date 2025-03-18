/**
 * 
 */
package es.caib.ripea.plugin.custodia;

import java.io.InputStream;

import es.caib.ripea.plugin.SistemaExternException;


/**
 * Interfície per a la custòdia de documents firmats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface CustodiaPlugin {

	/**
	 * Custòdia d'un document PDF firmat.
	 * 
	 * @param documentId
	 *            Identificador del document a custodiar.
	 * @param documentTipus
	 *            Tipus del document a custodiar.
	 * @param arxiuNom
	 *            Nom de l'arxiu a custodiar.
	 * @param arxiuContingut
	 *            Contingut de l'arxiu a custodiar.
	 * @throws SistemaExternException
	 *             Si hi ha hagut algun problema per custodiar el document.
	 */
	public void custodiarPdfFirmat(
			String documentId,
			String documentTipus,
			String arxiuNom,
			InputStream arxiuContingut) throws SistemaExternException;

	/**
	 * Obté el document custodiat.
	 * 
	 * @param documentId
	 *            Identificador del document a consultar.
	 * @return el contingut del document custodiat.
	 * @throws SistemaExternException
	 *             Si hi ha hagut algun problema al consultar el document.
	 */
	public byte[] obtenirDocumentCustodiat(
			String documentId) throws SistemaExternException;

	/**
	 * Esbòrra un document custodiat prèviament.
	 * 
	 * @param documentId
	 *            Identificador del document a esborrar.
	 * @param ignorarNotFound
	 *            Indica si s'ha d'ignorar l'error de document no trobat.
	 * @throws SistemaExternException
	 *             Si hi ha hagut algun problema per esborrar el document.
	 */
	public void esborrarDocumentCustodiat(
			String documentId,
			boolean ignorarNotFound) throws SistemaExternException;

	/**
	 * Reserva una URL de custòdia prèvia a la firma del document.
	 * 
	 * @param documentId
	 *            Identificador del document a reservar.
	 * @return La URL reservada.
	 * @throws SistemaExternException
	 *             Si hi ha hagut algun problema per a fer la reserva.
	 */
	public String reservarUrl(
			String documentId) throws SistemaExternException;

}
