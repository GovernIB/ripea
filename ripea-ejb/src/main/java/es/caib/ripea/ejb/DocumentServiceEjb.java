/**
 * 
 */
package es.caib.ripea.ejb;

import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.exception.PinbalException;
import es.caib.ripea.service.intf.exception.SistemaExternException;
import es.caib.ripea.service.intf.service.DocumentService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementació de ContenidorService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class DocumentServiceEjb implements DocumentService {

	@Delegate
	private DocumentService delegateService;

	protected void setDelegateService(DocumentService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("**")
	public DocumentDto create(
			Long entitatId,
			Long contenidorId,
			DocumentDto document,
			boolean comprovarMetaExpedient, 
			String rolActual, 
			Long tascaId) {
		return delegateService.create(
				entitatId,
				contenidorId,
				document,
				comprovarMetaExpedient, 
				rolActual, 
				tascaId);
	}

	@Override
	@RolesAllowed("**")
	public DocumentDto update(
			Long entitatId,
			DocumentDto document,
			boolean comprovarMetaExpedient, 
			String rolActual, Long tascaId) {
		return delegateService.update(
				entitatId,
				document,
				comprovarMetaExpedient, 
				rolActual, 
				tascaId);
	}

	@Override
	@RolesAllowed("**")
	public DocumentDto findById(
			Long entitatId,
			Long documentId, 
			Long tascaId) {
		return delegateService.findById(
				entitatId, 
				documentId, 
				tascaId);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto descarregar(
			Long entitatId,
			Long id,
			String versio, 
			Long tascaId) {
		return delegateService.descarregar(
				entitatId,
				id,
				versio, 
				tascaId);
	}
	
	@Override
	@RolesAllowed("**")
	public FitxerDto descarregarContingutOriginal(
			Long entitatId,
			Long id,
			Long tascaId) {
		return delegateService.descarregarContingutOriginal(
				entitatId,
				id,
				tascaId);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto descarregarImprimible(
			Long entitatId,
			Long id,
			String versio) {
		return delegateService.descarregarImprimible(
				entitatId,
				id,
				versio);
	}

	@Override
	@RolesAllowed("**")
	public Exception pinbalNovaConsulta(
			Long entitatId,
			Long pareId,
			Long metaDocumentId,
			PinbalConsultaDto consulta, 
			String rolActual) throws NotFoundException, PinbalException {
		return delegateService.pinbalNovaConsulta(
				entitatId, 
				pareId,
				metaDocumentId,
				consulta, 
				rolActual);
	}

	@Override
	@RolesAllowed("**")
	public void portafirmesEnviar(
			Long entitatId,
			Long id,
			String motiu,
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
			boolean firmaParcial) {
		delegateService.portafirmesEnviar(
				entitatId,
				id,
				motiu,
				prioritat,
				portafirmesFluxId,
				portafirmesResponsables,
				portafirmesSeqTipus,
				portafirmesFluxTipus,
				annexosIds,
				transaccioId, 
				rolActual, 
				tascaId,
				avisFirmaParcial,
				firmaParcial);
	}

	@Override
	@RolesAllowed("**")
	public void portafirmesCancelar(
			Long entitatId,
			Long id, 
			String rolActual, 
			Long tascaId) {
		delegateService.portafirmesCancelar(
				entitatId,
				id, rolActual, 
				tascaId);
	}

	@Override
	public Exception portafirmesCallback(
			long documentId,
			PortafirmesCallbackEstatEnumDto estat,
			String motiuRebuig,
			String administrationId,
			String name) {
		return delegateService.portafirmesCallback(documentId, estat, motiuRebuig, administrationId, name);
	}


	@Override
	@RolesAllowed("**")
	public DocumentPortafirmesDto portafirmesInfo(
			Long entitatId,
			Long documentId, 
			Long enviamentId) {
		return delegateService.portafirmesInfo(
				entitatId,
				documentId, 
				enviamentId);
	}

	@Override
	@RolesAllowed("**")
	public List<ViaFirmaUsuariDto> viaFirmaUsuaris(
			UsuariDto usuariActual)
			throws NotFoundException, IllegalStateException, SistemaExternException {
		return delegateService.viaFirmaUsuaris(usuariActual);
	}

	@Override
	@RolesAllowed("**")
	public void viaFirmaEnviar(
			Long entitatId, 
			Long documentId, 
			ViaFirmaEnviarDto viaFirmaEnviarDto,
			UsuariDto usuariActual) throws NotFoundException, IllegalStateException, SistemaExternException {
		delegateService.viaFirmaEnviar(
				entitatId, 
				documentId, 
				viaFirmaEnviarDto, 
				usuariActual);
	}
	
	@Override
	@RolesAllowed("**")
	public void viaFirmaCancelar(Long entitatId, Long documentId)
			throws NotFoundException, IllegalStateException, SistemaExternException {
		delegateService.viaFirmaCancelar(entitatId, documentId);
	}
	
	@Override
	@RolesAllowed("**")
	public void viaFirmaReintentar(Long entitatId, Long documentId) throws NotFoundException, SistemaExternException {
		delegateService.viaFirmaReintentar(entitatId, documentId);
	}

	@Override
	@RolesAllowed("**")
	public DocumentViaFirmaDto viaFirmaInfo(Long entitatId, Long documentId) throws NotFoundException {
		return delegateService.viaFirmaInfo(entitatId, documentId);
	}
	
	@Override
	@RolesAllowed("**")
	public List<ViaFirmaDispositiuDto> viaFirmaDispositius(
			String viaFirmaUsuari, 
			UsuariDto usuariActual)
			throws NotFoundException, IllegalStateException, SistemaExternException {
		return delegateService.viaFirmaDispositius(
				viaFirmaUsuari,
				usuariActual);
	}

	@Override
	@RolesAllowed("**")
	public Exception processarRespostaViaFirma(String messageJson) {
		return delegateService.processarRespostaViaFirma(messageJson);
	}
	
	@Override
	public Exception viaFirmaCallback(
			String messageCode,
			ViaFirmaCallbackEstatEnumDto estat) {
		return delegateService.viaFirmaCallback(messageCode, estat);
	}
	
	@Override
	@RolesAllowed("**")
	public FitxerDto convertirPdfPerFirmaClient(
			Long entitatId,
			Long id) {
		return delegateService.convertirPdfPerFirmaClient(
				entitatId,
				id);
	}
	
	@Override
	@RolesAllowed("**")
	public FitxerDto getFitxerPDF(
			Long entitatId,
			Long id) {
		return delegateService.getFitxerPDF(
				entitatId,
				id);
	}

	@Override
	@RolesAllowed("**")
	public String generarIdentificadorFirmaClient(
			Long entitatId,
			Long id) {
		return delegateService.generarIdentificadorFirmaClient(
				entitatId,
				id);
	}

	@Override
	@RolesAllowed("**")
	public void processarFirmaClient(
			Long entitatId,
			Long documentId,
			String arxiuNom, 
			byte[] arxiuContingut, 
			String rolActual, 
			Long tascaId) {
		delegateService.processarFirmaClient(
				entitatId,
				documentId,
				arxiuNom, 
				arxiuContingut, 
				rolActual, 
				tascaId);
	}

	@Override	
	@RolesAllowed("**")
	public FitxerDto infoDocument(
			Long entitatId, 
			Long id, 
			String versio) throws NotFoundException {
		return delegateService.infoDocument(
				entitatId, 
				id, 
				versio);
	}

	@Override	
	@RolesAllowed("**")
	public List<ArxiuFirmaDetallDto> getDetallSignants(
			Long entitatId,
			Long id,
			String versio) throws NotFoundException {
		return delegateService.getDetallSignants(
				entitatId, 
				id, 
				versio);
	}

	@Override
	public void notificacioActualitzarEstat(String identificador,
			String referencia) {
		delegateService.notificacioActualitzarEstat(identificador, referencia);
	}

	@Override
	public byte[] notificacioConsultarIDescarregarCertificacio(Long documentEnviamentInteressatId) {
		return delegateService.notificacioConsultarIDescarregarCertificacio(documentEnviamentInteressatId);
	}

	@Override
	@RolesAllowed("**")
	public void documentActualitzarEstat(Long entitatId, Long documentId, DocumentEstatEnumDto nouEstat) {
		delegateService.documentActualitzarEstat(
				entitatId, 
				documentId, 
				nouEstat);
	}

	@Override
	@RolesAllowed("**")
	public List<DocumentDto> findAnnexosAmbExpedient(Long entitatId, DocumentDto document) {
		return delegateService.findAnnexosAmbExpedient(entitatId, document);
	}

	@Override
	@RolesAllowed("**")
	public List<PortafirmesBlockDto> recuperarBlocksFirmaEnviament(Long entitatId, Long documentId, Long enviamentId) {
		return delegateService.recuperarBlocksFirmaEnviament(entitatId, documentId, enviamentId);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<DocumentDto> findDocumentsPerCustodiarMassiu(
			Long entitatId,
			String rolActual,
			ContingutMassiuFiltreDto filtre, 
			PaginacioParamsDto paginacioParams) throws NotFoundException {
		return delegateService.findDocumentsPerCustodiarMassiu(entitatId, rolActual, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public Exception portafirmesReintentar(
			Long entitatId,
			Long id, 
			String rolActual, 
			Long tascaId) {
		return delegateService.portafirmesReintentar(
				entitatId, 
				id, 
				rolActual, 
				tascaId);
	}

	@Override
	@RolesAllowed("**")
	public List<Long> findDocumentsIdsPerCustodiarMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre, String rolActual) throws NotFoundException {
		return delegateService.findDocumentsIdsPerCustodiarMassiu(entitatId, filtre, rolActual);
	}

	@Override
	@RolesAllowed("**")
	public Exception guardarDocumentArxiu(Long docId) {
		return delegateService.guardarDocumentArxiu(docId);
	}

	@Override
	@RolesAllowed("**")
	public boolean updateTipusDocument(
			Long entitatId, 
			Long documentId, 
			Long tipusDocumentId,
			boolean comprovarMetaExpedient, 
			Long tascaId, 
			String rolActual) {
		return delegateService.updateTipusDocument(
				entitatId,
				documentId,
				tipusDocumentId,
				comprovarMetaExpedient,
				tascaId,
				rolActual);
	}

    @Override
	@RolesAllowed("**")
    public void updateTipusDocumentDefinitiu(Long entitatId, Long documentId, Long tipusDocumentId) {
        delegateService.updateTipusDocumentDefinitiu(entitatId, documentId, tipusDocumentId);
    }

    @Override
	@RolesAllowed("**")
	public RespostaJustificantEnviamentNotibDto notificacioDescarregarJustificantEnviamentNotib(Long notificacioId) {
		return delegateService.notificacioDescarregarJustificantEnviamentNotib(notificacioId);
	}

	@Override
	@RolesAllowed("**")
	public SignatureInfoDto checkIfSignedAttached(
			byte[] contingut,
			String contentType) {
		return delegateService.checkIfSignedAttached(
				contingut,
				contentType);
	}

    @Override
	@RolesAllowed("**")
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Resum getSummarize(byte[] bytes, String contentType) {
        return delegateService.getSummarize(bytes, contentType);
    }

    @Override
    @RolesAllowed("**")
	public long countByMetaDocument(
			Long entitatId,
			Long metaDocumentId) {
		return delegateService.countByMetaDocument(
				entitatId,
				metaDocumentId);
	}

	@Override
	@RolesAllowed("**")
	public List<DocumentDto> findDocumentsNoFirmatsOAmbFirmaInvalidaONoGuardatsEnArxiu(
			Long entitatId,
			Long expedientId) {
		return delegateService.findDocumentsNoFirmatsOAmbFirmaInvalidaONoGuardatsEnArxiu(
				entitatId,
				expedientId);
	}

	@Override
	@RolesAllowed("**")
	public void actualitzarEstatADefinititu(
			Long documentId) {
		delegateService.actualitzarEstatADefinititu(documentId);
	}

	@Override
	@RolesAllowed("**")
	public DocumentDto findAmbId(
			Long documentId,
			String rolActual,
			PermissionEnumDto permission,
			Long tascaId) {
		return delegateService.findAmbId(documentId,
				rolActual,
				permission,
				tascaId);
	}

	@Override
	public void portafirmesCallbackIntegracioOk(
			String descripcio,
			Map<String, String> parametres) {
		delegateService.portafirmesCallbackIntegracioOk(
				descripcio,
				parametres);

	}

	@Override
	public void portafirmesCallbackIntegracioError(
			String descripcio,
			Map<String, String> parametres,
			String errorDescripcio,
			Throwable throwable) {
		delegateService.portafirmesCallbackIntegracioError(
				descripcio,
				parametres,
				errorDescripcio,
				throwable);
	}

	@Override
	@RolesAllowed("**")
	public String firmaSimpleWebStart(
			FitxerDto fitxerPerFirmar,
			String motiu, 
			String urlReturnToRipea) {
		return delegateService.firmaSimpleWebStart(
				fitxerPerFirmar,
				motiu, 
				urlReturnToRipea);
	}

	@Override
	@RolesAllowed("**")
	public FirmaResultatDto firmaSimpleWebEnd(
			String transactionID) {
		return delegateService.firmaSimpleWebEnd(transactionID);
	}

	@Override
	@RolesAllowed("**")
	public String recuperarUrlViewEstatFluxDeFirmes(long portafirmesId)  throws SistemaExternException {
		return delegateService.recuperarUrlViewEstatFluxDeFirmes(portafirmesId);
	}

	@Override
	@RolesAllowed("**")
	public Long getAndSaveFitxerTamanyFromArxiu(Long documentId) {
		return delegateService.getAndSaveFitxerTamanyFromArxiu(documentId);
	}

	@Override
	@RolesAllowed("**")
	public void notificacioActualitzarEstat(
			String identificador) {
		delegateService.notificacioActualitzarEstat(identificador);
	}

	@Override
	@RolesAllowed("**")
	public void notificacioActualitzarEstat(Long id) {
		delegateService.notificacioActualitzarEstat(id);
	}

	@Override
	@RolesAllowed("**")
	public List<Long> findIdsAllDocumentsOfExpedient(Long expedientId) {
		return delegateService.findIdsAllDocumentsOfExpedient(expedientId);
	}

	@Override
	@RolesAllowed("**")
	public String firmaSimpleWebStartMassiu(
			Set<Long> ids,
			String motiu,
			String urlReturnToRipea,
			Long entitatId) {
		return delegateService.firmaSimpleWebStartMassiu(ids,
				motiu,
				urlReturnToRipea,
				entitatId);
	}

	@Override
	@RolesAllowed("**")
	public List<DocumentDto> findByExpedient(Long id, Long expedientId, String rolActual) {
		return delegateService.findByExpedient(id, expedientId, rolActual);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto descarregarAllDocumentsOfExpedientWithSelectedFolders(
			Long entitatId,
			Long expedientId, 
			List<ArbreJsonDto> selectedElements,
			String rolActual, 
			Long tascaId) throws IOException {
		return delegateService.descarregarAllDocumentsOfExpedientWithSelectedFolders(
				entitatId,
				expedientId, 
				selectedElements, 
				rolActual, 
				tascaId);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto descarregarFirmaSeparada(Long entitatId, Long id, Long tascaId) {
		return delegateService.descarregarFirmaSeparada(entitatId, id, tascaId);
	}

	@Override
	@RolesAllowed("**")
	public DocumentDto updateCsvInfo(Long documentId) throws NotFoundException {
		return delegateService.updateCsvInfo(documentId);
	}

    @Override
    @RolesAllowed("**")
    public void enviarDocument(Long documentId, List<String> emails, List<String> desinataris){
        delegateService.enviarDocument(documentId, emails, desinataris);
    }
}