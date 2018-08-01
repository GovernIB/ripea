package es.caib.ripea.plugin.portafirmes;

import java.util.Date;
import java.util.List;

import es.caib.ripea.plugin.SistemaExternException;

/**
 * Plugin per a la integració amb portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PortafirmesPlugin {

	/**
	 * Envia un document a firmar al portafirmes.
	 * 
	 * @param document
	 *            L'arxiu a firmar.
	 * @param documentTipus
	 *            El tipus del document a firmar.
	 * @param motiu
	 *            El motiu per la firma.
	 * @param remitent
	 *            El remitent.
	 * @param prioritat
	 *            La importància del document.
	 * @param dataCaducitat
	 *            La data límit per a firmar el document.
	 * @param flux
	 *            Els blocs del flux de firmes.
	 * @param plantillaFluxId
	 *            L'id del flux de firmes ja definit al portafirmes.
	 * @param annexos
	 *            Els documents annexos.
	 * @param signarAnnexos
	 *            Indica si els annexos s'han de firmar o no.
	 * @return L'identificador assignat pel portafirmes.
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public String upload(
			PortafirmesDocument document,
			String documentTipus,
			String motiu,
			String remitent,
			PortafirmesPrioritatEnum prioritat,
			Date dataCaducitat,
			List<PortafirmesFluxBloc> flux,
			String plantillaFluxId,
			List<PortafirmesDocument> annexos,
			boolean signarAnnexos) throws SistemaExternException;

	/**
	 * Descarrega un document del portafirmes.
	 * 
	 * @param id
	 *            L'identificador del document a descarregar.
	 * @return El document.
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public PortafirmesDocument download(
			String id) throws SistemaExternException;

	/**
	 * Esborra un document del portafirmes.
	 * 
	 * @param id
	 *            L'identificador del document a esborrar.
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public void delete(
			String id) throws SistemaExternException;

	/**
	 * Retorna la llista amb tots els tipus de document permesos.
	 * 
	 * @return La llista de tipus de document o null si aquesta
	 *            característica no està suportada.
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public List<PortafirmesDocumentTipus> findDocumentTipus() throws SistemaExternException;

	/**
	 * Indica si el plugin suporta la custòdia de documents i si aquesta
	 * es fa de manera automàtica una vegada firmat el document.
	 * 
	 * @return true si està suportada i es fa de forma automàtica o false
	 *            en cas contrari.
	 */
	public boolean isCustodiaAutomatica();

}
