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
			boolean signarAnnexos,
			String transaccioId) throws SistemaExternException;

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

	/**
	 * Inicia un nou flux de firma a partir dels paràmetres d'entrada
	 * proporcionats.
	 *
	 * @param idioma
	 * 			Idioma per crear el flux.
	 * @param isPlantilla
	 * 			Indicar si guardar una plantilla de flux.
	 * @param nom
	 * 			Nom de flux a crear.
	 * @param descripcio
	 * 			Una descripció del flux a crear.
	 * @param descripcioVisible
	 * 			Indicar si visualitzar la descripció.
	 * @param returnUrl
	 * 			Url redirecció on redirigir la resposta una vegada s'ha iniciat
	 * 			la transacció.
	 * @return la informació del flux de firma creat (idTransacció i urlRedirecció).
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public PortafirmesIniciFluxResposta iniciarFluxDeFirma(
			String idioma,
			boolean isPlantilla,
			String nom,
			String descripcio,
			boolean descripcioVisible,
			String returnUrl) throws SistemaExternException;

	/**
	 * Recupera el id del flux de firma creat.
	 *
	 * @param idTransaccio
	 * 				Id de la transacció creada anteriorment.
	 * @return el id del flux de firma o error.
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public PortafirmesFluxResposta recuperarFluxDeFirmaByIdTransaccio(
			String idTransaccio) throws SistemaExternException;
	
	/**
	 * Recupera un llistat de les plantilles disponibles per un usuari aplicació.
	 *
	 * @param idioma
	 * 				idioma plantilles.
	 * @return el id del flux de firma o error.
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public List<PortafirmesFluxResposta> recuperarPlantillesDisponibles(String idioma) throws SistemaExternException;

	/**
	 * Recupera el detall d'un flux de firma creat (nom + descripció).
	 *
	 * @param idPlantilla
	 * 				Id de la plantilla.
	 * @param idioma
	 * 				Idioma flux.
	 * @return la informació del flux de firma.
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public PortafirmesFluxInfo recuperarFluxDeFirmaByIdPlantilla(
			String idTransaccio,
			String idioma) throws SistemaExternException;
	
	/**
	 * Recupera una url per mostrar/editar la informació de la plantilal de forma gràfica.
	 *
	 * @param idPlantilla
	 * 				Id de la plantilla.
	 * @param idioma
	 * 				Idioma plantilla/flux.
	 * @param edicio
	 * 		  		Indicar si recuperar url per visualitzar o editar plantilla.
	 * @return la url de Portafirmes.
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public String recuperarUrlViewEditPlantilla(
			String idPlantilla,
			String idioma,
			String urlReturn,
			boolean edicio) throws SistemaExternException;
	
	/**
	 * Recupera una url per mostrar/editar la informació de la plantilal de forma gràfica.
	 *
	 * @param idioma
	 * 				Idioma plantilla/flux.
	 * @param plantillaFluxId
	 * 		  		Id de la plantilla a esborrar.
	 * @return true = esborrat / false = no trobada.
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public boolean esborrarPlantillaFirma(
			String idioma,
			String plantillaFluxId) throws SistemaExternException;

	/**
	 * Tanca una transacció.
	 *
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public void tancarTransaccioFlux(
			String idTransaccio) throws SistemaExternException;

//	/**
//	 * Recupera els blocks de firma d'una petició de firma
//	 * 
//	 * @param idPlantilla
//	 * 				Id de la plantilla en cas de ser una petició a partir d'una plantilla
//	 * @param idTransaccio
//	 * 				Id de la transacció en cas de ser un flux nou
//	 * @param portafirmesFluxAsync
//	 * 				Indicar si és un tipus de firma simple (responsables) o de Portafib (flux)
//	 * @param portafirmesId
//	 * 				Id de la petició de portafirmes
//	 * @param idioma
//	 * 				Idioma en el que es vol recuerar el flux
//	 * 
//	 * @return Una llista amb els blocks de firma
//	 * @throws SistemaExternException
//	 * 			Si hi ha hagut algun problema per dur a terme l'acció.
//	 */
//	List<PortafirmesBlockInfo> recuperarBlocksFirmes(
//			String idPlantilla, 
//			String idTransaccio,
//			boolean portafirmesFluxAsync,
//			Long portafirmesId,
//			String idioma)
//			throws SistemaExternException;

	/**
	 * Recupera els càrrecs disponibles per un usuari aplicació
	 * 
	 * @return Una llista dels càrrecs
	 * @throws SistemaExternException
	 * 			Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	List<PortafirmesCarrec> recuperarCarrecs() throws SistemaExternException;

	/**
	 * Recupera la informació d'un càrrec a partir del seu id
	 * 
	 * @param carrecId
	 * 				ID del càrrec a consultar
	 * @return La informació del càrrec
	 * @throws SistemaExternException
	 * 			Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	PortafirmesCarrec recuperarCarrec(String carrecId) throws SistemaExternException;

	public List<PortafirmesFluxResposta> recuperarPlantillesPerFiltre(
			String idioma,
			String descripcio) throws SistemaExternException;
	
	/**
	 * Recupera url per visualitzar l'estat d'un flux de firmes d'una petició.
	 *
	 * @param portafirmesId
	 * 				Id de la petició de firma.
	 * @param idioma
	 * 				Idioma plantilla/flux.
	 * @return la url de Portafirmes.
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public String recuperarUrlViewEstatFluxDeFirmes(
			long portafirmesId,
			String idioma) throws SistemaExternException;

}
