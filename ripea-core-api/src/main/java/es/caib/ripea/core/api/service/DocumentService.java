/**
 * 
 */
package es.caib.ripea.core.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.ArxiuFirmaDetallDto;
import es.caib.ripea.core.api.dto.ContingutMassiuFiltreDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentPortafirmesDto;
import es.caib.ripea.core.api.dto.DocumentViaFirmaDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.core.api.dto.NotificacioInfoRegistreDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermissionEnumDto;
import es.caib.ripea.core.api.dto.PinbalConsultaDto;
import es.caib.ripea.core.api.dto.PortafirmesBlockDto;
import es.caib.ripea.core.api.dto.PortafirmesCallbackEstatEnumDto;
import es.caib.ripea.core.api.dto.PortafirmesPrioritatEnumDto;
import es.caib.ripea.core.api.dto.RespostaJustificantEnviamentNotibDto;
import es.caib.ripea.core.api.dto.SignatureInfoDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.dto.ViaFirmaCallbackEstatEnumDto;
import es.caib.ripea.core.api.dto.ViaFirmaDispositiuDto;
import es.caib.ripea.core.api.dto.ViaFirmaEnviarDto;
import es.caib.ripea.core.api.dto.ViaFirmaUsuariDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.PinbalException;
import es.caib.ripea.core.api.exception.SistemaExternException;
import es.caib.ripea.core.api.exception.ValidationException;


/**
 * Declaració dels mètodes per a gestionar documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentService {

	/**
	 * Crea un nou document a dins un contenidor.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param pareId
	 *            Atribut id del contenidor a on es vol crear el document.
	 * @param document
	 *            Informació del document que es vol crear.
	 * @param rolActual TODO
	 * @return El document creat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws ValidationException
	 *             Si el nom del contenidor conté caràcters invàlids.
	 */
	@PreAuthorize("hasRole('tothom')")
	public DocumentDto create(
			Long entitatId,
			Long pareId,
			DocumentDto document,
			boolean comprovarMetaExpedient, String rolActual) throws NotFoundException, ValidationException;

	/**
	 * Modifica un document.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param document
	 *            Informació del document que es vol crear.
	 * @param rolActual TODO
	 * @return El document modificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws ValidationException
	 *             Si el nom del contenidor conté caràcters invàlids.
	 */
	@PreAuthorize("hasRole('tothom')")
	public DocumentDto update(
			Long entitatId,
			DocumentDto document,
			boolean comprovarMetaExpedient, String rolActual) throws NotFoundException, ValidationException;

	/**
	 * Consulta un document donat el seu id.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param id
	 *            Atribut id del document que es vol trobar.
	 * @return El document.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public DocumentDto findById(
			Long entitatId,
			Long id) throws NotFoundException;

	/**
	 * Consulta les versions d'un document.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param id
	 *            Atribut id del document del qual es volen recuperar les versions.
	 * @return La llista de versions.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	/*@PreAuthorize("hasRole('tothom')")
	public List<DocumentVersioDto> findVersionsByDocument(
			Long entitatId,
			Long id) throws NotFoundException;*/

	/**
	 * Consulta la darrera versió del document.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param id
	 *            Atribut id del document del qual es vol descarregar el contingut.
	 * @return la darrera versió del document.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	/*@PreAuthorize("hasRole('tothom')")
	public DocumentVersioDto findDarreraVersio(
			Long entitatId,
			Long id) throws NotFoundException;*/

	/**
	 * Consulta una versió del document.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param id
	 *            Atribut id del document del qual es vol descarregar el contingut.
	 * @param versio
	 *            El número de versió del document que es vol descarregar.
	 * @return la versió del document.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	/*@PreAuthorize("hasRole('tothom')")
	public DocumentVersioDto findVersio(
			Long entitatId,
			Long id,
			int versio) throws NotFoundException;*/

	/**
	 * Consulta els documents d'un expedient.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @return la llistat de documents.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	public List<DocumentDto> findAmbExpedient(
			Long entitatId,
			Long expedientId) throws NotFoundException;

	/**
	 * Consulta els documents d'un expedient amb un estat determinat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param estat
	 *            L'estat dels documents.
	 * @return la llistat de documents.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	public List<DocumentDto> findAmbExpedientIEstat(
			Long entitatId,
			Long expedientId,
			DocumentEstatEnumDto estat) throws NotFoundException;

	/**
	 * Consulta els documents d'un expedient.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param document
	 *            Document del que es volen recuperar els annexos (documents) disponibles
	 * @return la llistat de documents.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	List<DocumentDto> findAnnexosAmbExpedient(Long entitatId, DocumentDto document);

	/**
	 * Recupera el contingut d'un document.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param id
	 *            Atribut id del document del qual es vol descarregar el contingut.
	 * @param versio
	 *            El número de versió del document que es vol descarregar.
	 * @return el fitxer amb el contingut.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public FitxerDto infoDocument(
			Long entitatId,
			Long id,
			String versio) throws NotFoundException;

	/**
	 * Consulta el detall de les signatures del document.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param id
	 *            Atribut id del document del qual es vol descarregar el contingut.
	 * @param versio
	 *            El número de versió del document que es vol descarregar.
	 * @return la informació de les signatures del document.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'informacio amb el contingut en bytes especificat.
	 */
	public List<ArxiuFirmaDetallDto> getDetallSignants(
			Long entitatId,
			Long id,
			String versio) throws NotFoundException;

	/**
	 * Descarrega el contingut d'un document.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param id
	 *            Atribut id del document del qual es vol descarregar el contingut.
	 * @param versio
	 *            El número de versió del document que es vol descarregar.
	 * @return el fitxer amb el contingut.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public FitxerDto descarregar(
			Long entitatId,
			Long id,
			String versio) throws NotFoundException;

	/**
	 * Crea un nou document associat a una consulta a PINBAL.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param pareId
	 *            Atribut id del contenidor a on es vol crear el document.
	 * @param metaDocumentId
	 *            Atribut id del meta-document.
	 * @param rolActual
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws PinbalException
	 *             Si s'han produit errors en la consulta a PINBAL.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void pinbalNovaConsulta(
			Long entitatId,
			Long pareId,
			Long metaDocumentId,
			PinbalConsultaDto consulta, 
			String rolActual) throws NotFoundException, PinbalException;

	/**
	 * Envia un document a firmar al portafirmes.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param documentId
	 *            Atribut id del document que es vol enviar a firmar.
	 * @param assumpte
	 *            L'assumpte de l'enviament.
	 * @param prioritat
	 *            La prioritat de l'enviament.
	 * @param rolActual TODO
	 * @param dataCaducitat
	 *            La data màxima per a firmar el document.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws IllegalStateException
	 *             Si hi ha enviaments a portafirmes pendents per aquest document.
	 * @throws SistemaExternException
	 *             Hi ha hagut algun error en la comunicació amb el portafirmes.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void portafirmesEnviar(
			Long entitatId,
			Long documentId,
			String assumpte,
			PortafirmesPrioritatEnumDto prioritat,
			String portafirmesFluxId,
			String[] portafirmesResponsables,
			MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSeqTipus,
			MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus,
			Long[] annexosIds,
			String transaccioId, String rolActual) throws NotFoundException, IllegalStateException, SistemaExternException;
	
	/**
	 * Recupera els dispositius disponibles per un usuari
	 * 
	 * @param viaFirmaUsuari
	 *            Codi de l'usuari del que es volen recuperar els dispositius.
	 * @param usuariActual
	 *            Usuari actual autenticat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws IllegalStateException
	 *             Si hi ha enviaments a portafirmes pendents per aquest document.
	 * @throws SistemaExternException
	 *             Hi ha hagut algun error en la comunicació amb el portafirmes.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<ViaFirmaDispositiuDto> viaFirmaDispositius(
			String viaFirmaUsuari,
			UsuariDto usuariActual) throws NotFoundException, IllegalStateException, SistemaExternException;
	
	/**
	 * Recupera els usuaris de viaFirma relacionats amb el usuari de Ripea.
	 * 
	 * @param usuariActual
	 *            Usuari actual autenticat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws IllegalStateException
	 *             Si hi ha enviaments a portafirmes pendents per aquest document.
	 * @throws SistemaExternException
	 *             Hi ha hagut algun error en la comunicació amb el portafirmes.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<ViaFirmaUsuariDto> viaFirmaUsuaris(UsuariDto usuariActual) throws NotFoundException, IllegalStateException, SistemaExternException;
	
	
	/**
	 * Envia un document a firmar a ViaFirma.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param documentId
	 *            Atribut id del document que es vol enviar a firmar.
	 * @param viaFirmaEnviarDto
	 *            Informació de l'usuari i dispositiu de viaFirma.
	 * @param usuariActual
	 *            Usuari actual autenticat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws IllegalStateException
	 *             Si hi ha enviaments a portafirmes pendents per aquest document.
	 * @throws SistemaExternException
	 *             Hi ha hagut algun error en la comunicació amb el portafirmes.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void viaFirmaEnviar(
			Long entitatId,
			Long documentId,
			ViaFirmaEnviarDto viaFirmaEnviarDto,
			UsuariDto usuariActual) throws NotFoundException, IllegalStateException, SistemaExternException;
	
	/**
	 * Cancela l'enviament d'un document a firmar a ViaFirma.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param documentId
	 *            Atribut id del document que es vol enviar a firmar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws IllegalStateException
	 *             Si no s'ha trobat l'enviament al portafirmes pel document.
	 * @throws SistemaExternException
	 *             Hi ha hagut algun error en la comunicació amb el portafirmes.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void viaFirmaCancelar(
			Long entitatId,
			Long documentId) throws NotFoundException, IllegalStateException, SistemaExternException;
	
	/**
	 * Cancela l'enviament d'un document a firmar al portafirmes.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param rolActual TODO
	 * @param id
	 *            Atribut id del document que es vol enviar a firmar.
	 * @param versio
	 *            El número de versió del document que es vol enviar a firmar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws IllegalStateException
	 *             Si no s'ha trobat l'enviament al portafirmes pel document.
	 * @throws SistemaExternException
	 *             Hi ha hagut algun error en la comunicació amb el portafirmes.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void portafirmesCancelar(
			Long entitatId,
			Long documentId, String rolActual) throws NotFoundException, IllegalStateException, SistemaExternException;

	/**
	 * Processa una petició del callback de portafirmes.
	 * 
	 * @param documentId
	 *            Atribut id del document del portafirmes.
	 * @param estat
	 *            Nou estat del document.
	 * @param administrationId
	 * 			  Identificador de la persona que ha firmat/rebutjat
	 * @param name
	 * 			  Nom de la persona que ha firmat/rebutjat
	 * @return null si tot ha anat bé o una excepció si s'ha produit algun error
	 *            al processar el document firmat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	public Exception portafirmesCallback(
			long documentId,
			PortafirmesCallbackEstatEnumDto estat,
			String motiuRebuig,
			String administrationId,
			String name) throws NotFoundException;



	/**
	 * Retorna la informació del darrer enviament a portafirmes del document.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param documentId
	 *            Atribut id del document que es vol convertir.
	 * @param enviamentId TODO
	 * @return la informació de l'enviament a portafirmes.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public DocumentPortafirmesDto portafirmesInfo(
			Long entitatId,
			Long documentId, 
			Long enviamentId) throws NotFoundException;
	
	/**
	 * Reintenta la custòdia d'un document firmat amb ViaFirma que ha donat
	 * error al custodiar.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param documentId
	 *            Atribut id del document que es vol custodiar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws SistemaExternException
	 *             Hi ha hagut algun error en la comunicació amb la custòdia.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void viaFirmaReintentar(
			Long entitatId,
			Long documentId) throws NotFoundException, SistemaExternException;
	
	/**
	 * Retorna la informació del darrer enviament a ViaFirma del document.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param documentId
	 *            Atribut id del document que es vol convertir.
	 * @return la informació de l'enviament a portafirmes.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public DocumentViaFirmaDto viaFirmaInfo(
			Long entitatId,
			Long documentId) throws NotFoundException;
	
	/**
	 * Converteix el missatge retornat per el callback de viaFirma a Message 
	 * i crida el callback per actualitzar estat
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param documentId
	 *            Atribut id del document que es vol convertir.
	 * @return la informació de l'enviament a portafirmes.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public Exception processarRespostaViaFirma(String messageJson);

	/**
	 * Processa una petició del callback de ViaFirma.
	 * 
	 * @param messageCode
	 *            Codi del missatge del document dins ViaFirma.
	 * @param estat
	 *            Nou estat del document.
	 * @return null si tot ha anat bé o una excepció si s'ha produit algun error
	 *            al processar el document firmat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	public Exception viaFirmaCallback(
			String messageCode,
			ViaFirmaCallbackEstatEnumDto estat) throws NotFoundException;
	
	/**
	 * Converteix el document a format PDF per a firmar-lo al navegador.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param id
	 *            Atribut id del document que es vol convertir.
	 * @return el fitxer convertit.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws SistemaExternException
	 *             Hi ha hagut algun error en la comunicació amb el portafirmes.
	 */
	@PreAuthorize("hasRole('tothom')")
	public FitxerDto convertirPdfPerFirmaClient(
			Long entitatId,
			Long id) throws NotFoundException, SistemaExternException;

	/**
	 * Genera un identificador del document per firmar en el navegador
	 * del client.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param id
	 *            Atribut id del document que es vol convertir.
	 * @return l'identificador generat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws SistemaExternException
	 *             Hi ha hagut algun error en la comunicació amb la custòdia.
	 */
	@PreAuthorize("hasRole('tothom')")
	public String generarIdentificadorFirmaClient(
			Long entitatId,
			Long id) throws NotFoundException, SistemaExternException;

	/**
	 * Envia a custòdia un document firmat al navegador.
	 * @param entitatId TODO
	 * @param documentId TODO
	 * @param arxiuNom
	 *            Nom de l'arxiu firmat.
	 * @param arxiuContingut
	 *            Contingut de l'arxiu firmat.
	 * @param rolActual TODO
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws SistemaExternException
	 *             Hi ha hagut algun error en la comunicació amb la custòdia.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void processarFirmaClient(
			Long entitatId,
			Long documentId,
			String arxiuNom, byte[] arxiuContingut, String rolActual) throws NotFoundException, SistemaExternException;

	FitxerDto descarregarImprimible(Long entitatId, Long id, String versio);
	

	void notificacioActualitzarEstat(String identificador,
			String referencia);

	public byte[] notificacioConsultarIDescarregarCertificacio(Long documentEnviamentInteressatId);

	/**
	 * Recupera la informació d'un registre d'una notificació amb el justificant.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param documentId
	 *            Atribut id del document relacionat amb la notificació.
	 * @param documentEnviamentId
	 *            Atribut ID de l'enviament del qual es vol recuperar la informació.
	 * @throws SistemaExternException
	 *             Hi ha hagut algun error en la comunicació amb Notib.
	 */
	public NotificacioInfoRegistreDto notificacioConsultarIDescarregarJustificant(
			Long entitatId,
			Long documentId,
			Long documentEnviamentId);
	
	/**
	 * Actualitza l'estat d'un document de forma manual.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param documentId
	 *            Atribut id del document del que es vol canviar l'estat.
	 */
	public void documentActualitzarEstat(
			Long entitatId,
			Long documentId,
			DocumentEstatEnumDto nouEstat);

	/**
	 * Recupera els blocks de firma relacionats amb l'enviament d'un document
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param documentId
	 *            Atribut id del document del que es vol canviar l'estat.
	 * @param enviamentId TODO
	 */
	List<PortafirmesBlockDto> recuperarBlocksFirmaEnviament(
			Long entitatId, 
			Long documentId, Long enviamentId);

	public PaginaDto<DocumentDto> findDocumentsPerCustodiarMassiu(
			Long entitatId,
			String rolActual,
			ContingutMassiuFiltreDto filtre, 
			PaginacioParamsDto paginacioParams) throws NotFoundException;

	public Exception portafirmesReintentar(
			Long entitatId,
			Long id, String rolActual);

	public List<Long> findDocumentsIdsPerCustodiarMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre, String rolActual) throws NotFoundException;

	public Exception guardarDocumentArxiu(Long docId);

	/**
	 * Actualitza un document amb un tipus de document nou
	 * 
	 * @param entitatId
	 * 			Id entitat actual
	 * @param documentId
	 * 			Id del document a actualitzar
	 * @param tipusDocumentId
	 * 			Id del nou tipus de document
	 * @param comprovarMetaExpedient
	 * 			Comprovar permisos metaExpedient
	 * @param tascaId TODO
	 * @param rolActual TODO
	 * @return true si s'ha actualitzat
	 */
	@PreAuthorize("hasRole('tothom')")
	public boolean updateTipusDocument(
			Long entitatId, 
			Long documentId, 
			Long tipusDocumentId,
			boolean comprovarMetaExpedient, 
			Long tascaId, 
			String rolActual);

	@PreAuthorize("hasRole('tothom')")
	public RespostaJustificantEnviamentNotibDto notificacioDescarregarJustificantEnviamentNotib(Long notificacioId);

	@PreAuthorize("hasRole('tothom')")
	SignatureInfoDto checkIfSignedAttached(byte[] contingut, String contentType);

	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_ORGAN_ADMIN') or hasRole('IPA_REVISIO')")
	public long countByMetaDocument(
			Long entitatId,
			Long metaDocumentId);

	@PreAuthorize("hasRole('tothom')")
	public List<DocumentDto> findDocumentsNoFirmatsOAmbFirmaInvalidaONoGuardatsEnArxiu(
			Long entitatId,
			Long expedientId);

	@PreAuthorize("hasRole('tothom')")
	public void actualitzarEstatADefinititu(
			Long documentId);

	@PreAuthorize("hasRole('tothom')")
	public DocumentDto findAmbId(
			Long documentId, String rolActual, PermissionEnumDto permission);

	

}
