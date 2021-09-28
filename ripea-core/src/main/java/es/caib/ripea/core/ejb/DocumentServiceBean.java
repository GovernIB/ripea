/**
 * 
 */
package es.caib.ripea.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

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
import es.caib.ripea.core.api.dto.PinbalConsultaDto;
import es.caib.ripea.core.api.dto.PortafirmesBlockDto;
import es.caib.ripea.core.api.dto.PortafirmesCallbackEstatEnumDto;
import es.caib.ripea.core.api.dto.PortafirmesPrioritatEnumDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.dto.ViaFirmaCallbackEstatEnumDto;
import es.caib.ripea.core.api.dto.ViaFirmaDispositiuDto;
import es.caib.ripea.core.api.dto.ViaFirmaEnviarDto;
import es.caib.ripea.core.api.dto.ViaFirmaUsuariDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.PinbalException;
import es.caib.ripea.core.api.exception.SistemaExternException;
import es.caib.ripea.core.api.service.DocumentService;

/**
 * Implementació de ContenidorService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class DocumentServiceBean implements DocumentService {

	@Autowired
	DocumentService delegate;

	@Override
	@RolesAllowed("tothom")
	public DocumentDto create(
			Long entitatId,
			Long contenidorId,
			DocumentDto document,
			boolean comprovarMetaExpedient, String rolActual) {
		return delegate.create(
				entitatId,
				contenidorId,
				document,
				comprovarMetaExpedient, 
				rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public DocumentDto update(
			Long entitatId,
			DocumentDto document,
			boolean comprovarMetaExpedient, 
			String rolActual) {
		return delegate.update(
				entitatId,
				document,
				comprovarMetaExpedient, 
				rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public DocumentDto findById(
			Long entitatId,
			Long id) {
		return delegate.findById(entitatId, id);
	}

	@Override
	public List<DocumentDto> findAmbExpedient(
			Long entitatId,
			Long expedientId) throws NotFoundException {
		return delegate.findAmbExpedient(
				entitatId,
				expedientId);
	}

	@Override
	public List<DocumentDto> findAmbExpedientIEstat(
			Long entitatId,
			Long expedientId,
			DocumentEstatEnumDto estat) throws NotFoundException {
		return delegate.findAmbExpedientIEstat(
				entitatId,
				expedientId,
				estat);
	}

	@Override
	@RolesAllowed("tothom")
	public FitxerDto descarregar(
			Long entitatId,
			Long id,
			String versio) {
		return delegate.descarregar(
				entitatId,
				id,
				versio);
	}

	@Override
	@RolesAllowed("tothom")
	public FitxerDto descarregarImprimible(
			Long entitatId,
			Long id,
			String versio) {
		return delegate.descarregarImprimible(
				entitatId,
				id,
				versio);
	}

	@Override
	@RolesAllowed("tothom")
	public void pinbalNovaConsulta(
			Long entitatId,
			Long pareId,
			Long metaDocumentId,
			PinbalConsultaDto consulta) throws NotFoundException, PinbalException {
		delegate.pinbalNovaConsulta(
				entitatId, 
				pareId,
				metaDocumentId,
				consulta);
	}

	@Override
	@RolesAllowed("tothom")
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
			String rolActual) {
		delegate.portafirmesEnviar(
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
				rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public void portafirmesCancelar(
			Long entitatId,
			Long id) {
		delegate.portafirmesCancelar(
				entitatId,
				id);
	}

	@Override
	public Exception portafirmesCallback(
			long documentId,
			PortafirmesCallbackEstatEnumDto estat,
			String motiuRebuig,
			String administrationId,
			String name) {
		return delegate.portafirmesCallback(documentId, estat, motiuRebuig, administrationId, name);
	}

	@Override
	@RolesAllowed("tothom")
	public Exception portafirmesReintentar(
			Long entitatId,
			Long id) {
		return delegate.portafirmesReintentar(
				entitatId,
				id);
	}

	@Override
	@RolesAllowed("tothom")
	public DocumentPortafirmesDto portafirmesInfo(
			Long entitatId,
			Long documentId, 
			Long enviamentId) {
		return delegate.portafirmesInfo(
				entitatId,
				documentId, 
				enviamentId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ViaFirmaUsuariDto> viaFirmaUsuaris(
			UsuariDto usuariActual)
			throws NotFoundException, IllegalStateException, SistemaExternException {
		return delegate.viaFirmaUsuaris(usuariActual);
	}

	@Override
	@RolesAllowed("tothom")
	public void viaFirmaEnviar(
			Long entitatId, 
			Long documentId, 
			ViaFirmaEnviarDto viaFirmaEnviarDto,
			UsuariDto usuariActual) throws NotFoundException, IllegalStateException, SistemaExternException {
		delegate.viaFirmaEnviar(
				entitatId, 
				documentId, 
				viaFirmaEnviarDto, 
				usuariActual);
	}
	
	@Override
	@RolesAllowed("tothom")
	public void viaFirmaCancelar(Long entitatId, Long documentId)
			throws NotFoundException, IllegalStateException, SistemaExternException {
		delegate.viaFirmaCancelar(entitatId, documentId);
	}
	
	@Override
	@RolesAllowed("tothom")
	public void viaFirmaReintentar(Long entitatId, Long documentId) throws NotFoundException, SistemaExternException {
		delegate.viaFirmaReintentar(entitatId, documentId);
	}

	@Override
	@RolesAllowed("tothom")
	public DocumentViaFirmaDto viaFirmaInfo(Long entitatId, Long documentId) throws NotFoundException {
		return delegate.viaFirmaInfo(entitatId, documentId);
	}
	
	@Override
	@RolesAllowed("tothom")
	public List<ViaFirmaDispositiuDto> viaFirmaDispositius(
			String viaFirmaUsuari, 
			UsuariDto usuariActual)
			throws NotFoundException, IllegalStateException, SistemaExternException {
		return delegate.viaFirmaDispositius(
				viaFirmaUsuari,
				usuariActual);
	}

	@Override
	@RolesAllowed("tothom")
	public Exception processarRespostaViaFirma(String messageJson) {
		return delegate.processarRespostaViaFirma(messageJson);
	}
	
	@Override
	public Exception viaFirmaCallback(
			String messageCode,
			ViaFirmaCallbackEstatEnumDto estat) {
		return delegate.viaFirmaCallback(messageCode, estat);
	}
	
	@Override
	@RolesAllowed("tothom")
	public FitxerDto convertirPdfPerFirmaClient(
			Long entitatId,
			Long id) {
		return delegate.convertirPdfPerFirmaClient(
				entitatId,
				id);
	}

	@Override
	@RolesAllowed("tothom")
	public String generarIdentificadorFirmaClient(
			Long entitatId,
			Long id) {
		return delegate.generarIdentificadorFirmaClient(
				entitatId,
				id);
	}

	@Override
	@RolesAllowed("tothom")
	public void processarFirmaClient(
			String identificador,
			String arxiuNom,
			byte[] arxiuContingut, 
			String rolActual) {
		delegate.processarFirmaClient(
				identificador,
				arxiuNom,
				arxiuContingut, 
				rolActual);
	}

	@Override	
	@RolesAllowed("tothom")
	public FitxerDto infoDocument(
			Long entitatId, 
			Long id, 
			String versio) throws NotFoundException {
		return delegate.infoDocument(
				entitatId, 
				id, 
				versio);
	}

	@Override	
	@RolesAllowed("tothom")
	public List<ArxiuFirmaDetallDto> getDetallSignants(
			Long entitatId,
			Long id,
			String versio) throws NotFoundException {
		return delegate.getDetallSignants(
				entitatId, 
				id, 
				versio);
	}

	@Override
	public void notificacioActualitzarEstat(String identificador,
			String referencia) {
		delegate.notificacioActualitzarEstat(identificador, referencia);
	}

	@Override
	public byte[] notificacioConsultarIDescarregarCertificacio(Long documentEnviamentInteressatId) {
		return delegate.notificacioConsultarIDescarregarCertificacio(documentEnviamentInteressatId);
	}

	@Override
	@RolesAllowed("tothom")
	public NotificacioInfoRegistreDto notificacioConsultarIDescarregarJustificant(Long entitatId, Long documentId,
			Long documentNotificacioId) {
		return delegate.notificacioConsultarIDescarregarJustificant(entitatId, documentId, documentNotificacioId);
	}

	@Override
	@RolesAllowed("tothom")
	public void documentActualitzarEstat(Long entitatId, Long documentId, DocumentEstatEnumDto nouEstat) {
		delegate.documentActualitzarEstat(
				entitatId, 
				documentId, 
				nouEstat);
	}

	@Override
	@RolesAllowed("tothom")
	public List<DocumentDto> findAnnexosAmbExpedient(Long entitatId, DocumentDto document) {
		return delegate.findAnnexosAmbExpedient(entitatId, document);
	}

	@Override
	@RolesAllowed("tothom")
	public List<PortafirmesBlockDto> recuperarBlocksFirmaEnviament(Long entitatId, Long documentId, Long enviamentId) {
		return delegate.recuperarBlocksFirmaEnviament(entitatId, documentId, enviamentId);
	}

	@Override
	@RolesAllowed("tothom")
	public PaginaDto<DocumentDto> findDocumentsPerCustodiarMassiu(
			Long entitatId,
			String rolActual,
			ContingutMassiuFiltreDto filtre, 
			PaginacioParamsDto paginacioParams) throws NotFoundException {
		return delegate.findDocumentsPerCustodiarMassiu(entitatId, rolActual, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed("tothom")
	public Exception portafirmesReintentar(
			Long entitatId,
			Long id, String rolActual) {
		return delegate.portafirmesReintentar(entitatId, id, rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public List<Long> findDocumentsIdsPerCustodiarMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre, String rolActual) throws NotFoundException {
		return delegate.findDocumentsIdsPerCustodiarMassiu(entitatId, filtre, rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public Exception guardarEnArxiuDocumentAdjunt(Long docId) {
		return delegate.guardarEnArxiuDocumentAdjunt(docId);
	}

	@Override
	@RolesAllowed("tothom")
	public boolean updateTipusDocumental(
			Long entitatId, 
			Long documentId, 
			Long tipusDocumentId,
			boolean comprovarMetaExpedient) {
		return delegate.updateTipusDocumental(entitatId, documentId, tipusDocumentId, comprovarMetaExpedient);
	}


}
