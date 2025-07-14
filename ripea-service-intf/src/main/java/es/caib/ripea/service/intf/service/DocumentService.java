/**
 * 
 */
package es.caib.ripea.service.intf.service;

import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.exception.PinbalException;
import es.caib.ripea.service.intf.exception.SistemaExternException;
import es.caib.ripea.service.intf.exception.ValidationException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.security.PermitAll;


/**
 * Declaració dels mètodes per a gestionar documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@PreAuthorize("isAuthenticated()")
public interface DocumentService {

	@PreAuthorize("isAuthenticated()")
	public String getEnllacCsv(Long entitatId, Long documentId);
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
	 * @param tascaId TODO
	 * @return El document creat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws ValidationException
	 *             Si el nom del contenidor conté caràcters invàlids.
	 */
	@PreAuthorize("isAuthenticated()")
	public DocumentDto create(
			Long entitatId,
			Long pareId,
			DocumentDto document,
			boolean comprovarMetaExpedient, 
			String rolActual, 
			Long tascaId) throws NotFoundException, ValidationException;

	/**
	 * Modifica un document.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param document
	 *            Informació del document que es vol crear.
	 * @param rolActual TODO
	 * @param tascaId TODO
	 * @return El document modificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws ValidationException
	 *             Si el nom del contenidor conté caràcters invàlids.
	 */
	@PreAuthorize("isAuthenticated()")
	public DocumentDto update(
			Long entitatId,
			DocumentDto document,
			boolean comprovarMetaExpedient, 
			String rolActual, 
			Long tascaId) throws NotFoundException, ValidationException;

	/**
	 * Consulta un document donat el seu id.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param id
	 *            Atribut id del document que es vol trobar.
	 * @param tascaId TODO
	 * @return El document.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public DocumentDto findById(
			Long entitatId,
			Long documentId, 
			Long tascaId) throws NotFoundException;

	/**
	 * Actualitza i retorna la informació NTI de un document (codi CSV, etc)
	 * https://github.com/GovernIB/ripea/issues/1451
	 */
	@PreAuthorize("isAuthenticated()")
	public DocumentDto updateCsvInfo(Long documentId) throws NotFoundException;
	
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
	/*@PreAuthorize("isAuthenticated()")
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
	/*@PreAuthorize("isAuthenticated()")
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
	/*@PreAuthorize("isAuthenticated()")
	public DocumentVersioDto findVersio(
			Long entitatId,
			Long id,
			int versio) throws NotFoundException;*/




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
	@PreAuthorize("isAuthenticated()")
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
	 * @param tascaId TODO
	 * @return el fitxer amb el contingut.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public FitxerDto descarregar(Long entitatId, Long id, String versio, Long tascaId) throws NotFoundException;
	
	@PreAuthorize("isAuthenticated()")
	public FitxerDto descarregarContingutOriginal(Long entitatId, Long id, Long tascaId) throws NotFoundException;

	@PreAuthorize("isAuthenticated()")
	public FitxerDto descarregarFirmaSeparada(Long entitatId, Long id, Long tascaId);
	
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
	 * @return TODO
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws PinbalException
	 *             Si s'han produit errors en la consulta a PINBAL.
	 */
	@PreAuthorize("isAuthenticated()")
	public Exception pinbalNovaConsulta(
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
	 * @param tascaId TODO
	 * @param avisFirmaParcial 
	 * 			  Indicar si rebre correus canvi estat de firmes parcials
	 * @param dataCaducitat
	 *            La data màxima per a firmar el document.
	 * @param firmaParcial 
	 * 			  Tractar la firma final de Portafirmes com una firma parcial
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws IllegalStateException
	 *             Si hi ha enviaments a portafirmes pendents per aquest document.
	 * @throws SistemaExternException
	 *             Hi ha hagut algun error en la comunicació amb el portafirmes.
	 */
	@PreAuthorize("isAuthenticated()")
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
			String transaccioId, 
			String rolActual, 
			Long tascaId,
			boolean avisFirmaParcial,
			boolean firmaParcial) throws NotFoundException, IllegalStateException, SistemaExternException;
	
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
	@PreAuthorize("isAuthenticated()")
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
	@PreAuthorize("isAuthenticated()")
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
	@PreAuthorize("isAuthenticated()")
	public void viaFirmaEnviar(Long entitatId, Long documentId, ViaFirmaEnviarDto viaFirmaEnviarDto) throws NotFoundException, IllegalStateException, SistemaExternException;
	
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
	@PreAuthorize("isAuthenticated()")
	public void viaFirmaCancelar(
			Long entitatId,
			Long documentId) throws NotFoundException, IllegalStateException, SistemaExternException;
	
	/**
	 * Cancela l'enviament d'un document a firmar al portafirmes.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param rolActual TODO
	 * @param tascaId TODO
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
	@PreAuthorize("isAuthenticated()")
	public void portafirmesCancelar(
			Long entitatId,
			Long documentId, 
			String rolActual, 
			Long tascaId) throws NotFoundException, IllegalStateException, SistemaExternException;

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
	@PermitAll
	public Exception portafirmesCallback(
			long documentId,
			PortafirmesCallbackEstatEnumDto estat,
			String motiuRebuig,
			String administrationId,
			String name) throws NotFoundException;

	@PermitAll
	public void portafirmesCallbackIntegracioOk(
			String descripcio,
			Map<String, String> parametres);

	@PermitAll
	public void portafirmesCallbackIntegracioError(
			String descripcio,
			Map<String, String> parametres,
			String errorDescripcio,
			Throwable throwable);	
	
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
	@PreAuthorize("isAuthenticated()")
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
	@PreAuthorize("isAuthenticated()")
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
	@PreAuthorize("isAuthenticated()")
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
	@PreAuthorize("isAuthenticated()")
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
	@PreAuthorize("isAuthenticated()")
	public FitxerDto convertirPdfPerFirmaClient(
			Long entitatId,
			Long id) throws NotFoundException, SistemaExternException;

	@PreAuthorize("isAuthenticated()")
	public FitxerDto getFitxerPDF(
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
	@PreAuthorize("isAuthenticated()")
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
	 * @param tascaId TODO
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws SistemaExternException
	 *             Hi ha hagut algun error en la comunicació amb la custòdia.
	 */
	@PreAuthorize("isAuthenticated()")
	public Long processarFirmaClient(
			Long entitatId,
			Long documentId,
			String arxiuNom, 
			byte[] arxiuContingut, 
			String rolActual, 
			Long tascaId) throws NotFoundException, SistemaExternException;

	FitxerDto descarregarImprimible(Long entitatId, Long id, String versio);
	
	@PermitAll
	void notificacioActualitzarEstat(String identificador, String referencia);

	public byte[] notificacioConsultarIDescarregarCertificacio(Long documentEnviamentInteressatId);

	
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
	 * @param enviamentId
	 * 			  Atribut id de l'enviament
	 */
	List<PortafirmesBlockDto> recuperarBlocksFirmaEnviament(
			Long entitatId, 
			Long documentId,
			Long enviamentId);

	public PaginaDto<DocumentDto> findDocumentsPerCustodiarMassiu(
			Long entitatId,
			String rolActual,
			ContingutMassiuFiltreDto filtre, 
			PaginacioParamsDto paginacioParams) throws NotFoundException;

	public Exception portafirmesReintentar(
			Long entitatId,
			Long id, 
			String rolActual, 
			Long tascaId);

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
	@PreAuthorize("isAuthenticated()")
	public boolean updateTipusDocument(
			Long entitatId, 
			Long documentId, 
			Long tipusDocumentId,
			boolean comprovarMetaExpedient, 
			Long tascaId, 
			String rolActual);

	// Mètode implementat únicament per solucionar error de documents que s'han creat sense el seu tipus, i ja estan com a definitius
	@PreAuthorize("isAuthenticated()")
	public void updateTipusDocumentDefinitiu(
			Long entitatId,
			Long documentId,
			Long tipusDocumentId);

	@PreAuthorize("isAuthenticated()")
	public RespostaJustificantEnviamentNotibDto notificacioDescarregarJustificantEnviamentNotib(Long notificacioId);

	@PreAuthorize("isAuthenticated()")
	SignatureInfoDto checkIfSignedAttached(byte[] contingut, String contentType);

	@PreAuthorize("isAuthenticated()")
	public Resum getSummarize(byte[] bytes, String contentType);

	@PreAuthorize("isAuthenticated()")
	public long countByMetaDocument(
			Long entitatId,
			Long metaDocumentId);

	@PreAuthorize("isAuthenticated()")
	public List<DocumentDto> findDocumentsNoFirmatsOAmbFirmaInvalidaONoGuardatsEnArxiu(
			Long entitatId,
			Long expedientId);


	@PreAuthorize("isAuthenticated()")
	public void actualitzarEstatADefinititu(
			Long documentId);

	@PreAuthorize("isAuthenticated()")
	public DocumentDto findAmbId(
			Long documentId,
			String rolActual,
			PermissionEnumDto permission,
			Long tascaId);

	/**
	 * Recupera url per visualitzar l'estat d'un flux de firmes d'una petició.
	 *
	 * @param portafirmesId
	 * 				Id de la petició de firma.
	 * @return la url de Portafirmes.
	 * @throws SistemaExternException
	 *            Hi ha hagut algun error en la comunicació amb el portafirmes.
	 */
	@PreAuthorize("isAuthenticated()")
	public String recuperarUrlViewEstatFluxDeFirmes(long portafirmesId)  throws SistemaExternException;

	@PreAuthorize("isAuthenticated()")
	public String firmaSimpleWebStart(
			Long entitatActualId,
			Long documentId,
			String motiu,
			String base);

	@PreAuthorize("isAuthenticated()")
	public FirmaResultatDto firmaSimpleWebEnd(String transactionID);

	@PreAuthorize("isAuthenticated()")
	public Long getAndSaveFitxerTamanyFromArxiu(Long documentId);

	@PreAuthorize("isAuthenticated()")
	public void notificacioActualitzarEstat(String identificador);

	@PreAuthorize("isAuthenticated()")
	public void notificacioActualitzarEstat(Long id);

	@PreAuthorize("isAuthenticated()")
	public List<Long> findIdsAllDocumentsOfExpedient(Long expedientId);

	@PreAuthorize("isAuthenticated()")
	public String firmaSimpleWebStartMassiu(Set<Long> ids, String motiu, String urlReturnToRipea, Long entitatId);

	@PreAuthorize("isAuthenticated()")
	public List<DocumentDto> findByExpedient(Long id, Long expedientId, String rolActual);

	@PreAuthorize("isAuthenticated()")
	public FitxerDto descarregarAllDocumentsOfExpedientWithSelectedFolders(
			Long entitatId,
			Long expedientId, 
			List<ArbreJsonDto> selectedElements,
			String rolActual, 
			Long tascaId) throws IOException;

    @PreAuthorize("isAuthenticated()")
    public void enviarDocument(Long documentId, List<String> emails, List<String> desinataris, VersioDocumentEnum versioDocument);
    

    @PreAuthorize("isAuthenticated()")
    public byte[] getPlantillaImportacioZip();
    
}