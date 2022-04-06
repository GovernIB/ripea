/**
 * 
 */
package es.caib.ripea.core.ejb;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import es.caib.ripea.core.api.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.service.ExpedientTascaService;

/**
 * Implementació de ExpedientTascaService com a EJB que empra una clase delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ExpedientTascaServiceBean implements ExpedientTascaService {

	@Autowired
	ExpedientTascaService delegate;

	@Override
	@RolesAllowed("tothom")
	public ExpedientTascaDto findOne(Long expedientPeticioId) {
		return delegate.findOne(expedientPeticioId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ExpedientTascaDto> findAmbExpedient(Long entitatId,
			Long expedientId) {
		return delegate.findAmbExpedient(entitatId,
				expedientId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<MetaExpedientTascaDto> findAmbMetaExpedient(Long entitatId,
			Long metaExpedientId) {
		return delegate.findAmbMetaExpedient(entitatId,
				metaExpedientId);
	}

	@Override
	@RolesAllowed("tothom")
	public ExpedientTascaDto createTasca(
			Long entitatId,
			Long expedientId,
			ExpedientTascaDto expedientTasca) {
		return delegate.createTasca(
				entitatId,
				expedientId,
				expedientTasca);
	}

	@Override
	@RolesAllowed("tothom")
	public MetaExpedientTascaDto findMetaExpedientTascaById(Long metaExpedientTascaId) {
		return delegate.findMetaExpedientTascaById(metaExpedientTascaId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ExpedientTascaDto> findAmbAuthentication(Long entitatId, PaginacioParamsDto paginacioParam) {
		return delegate.findAmbAuthentication(entitatId, paginacioParam);
	}

	@Override
	@RolesAllowed("tothom")
	public long countTasquesPendents() {
		return delegate.countTasquesPendents();
	}


	@Override
	@RolesAllowed("tothom")
	public ContingutDto findTascaExpedient(Long entitatId,
			Long contingutId,
			Long tascaId,
			boolean ambFills,
			boolean ambVersions) {
		return delegate.findTascaExpedient(entitatId,
				contingutId,
				tascaId,
				ambFills,
				ambVersions);
	}

	@Override
	@RolesAllowed("tothom")
	public FitxerDto descarregar(Long entitatId,
			Long contingutId,
			Long tascaId,
			String versio) {
		return delegate.descarregar(entitatId,
				contingutId,
				tascaId,
				versio);
	}

	@Override
	@RolesAllowed("tothom")
	public DocumentDto createDocument(Long entitatId,
			Long pareId,
			Long tascaId,
			DocumentDto document,
			boolean comprovarMetaExpedient) {
		delegate.createDocument(entitatId,
				pareId,
				tascaId,
				document,
				comprovarMetaExpedient);
		return null;
	}

	@Override
	@RolesAllowed("tothom")
	public DocumentDto findDocumentById(
			Long entitatId,
			Long tascaId,
			Long documentId) {
		return delegate.findDocumentById(
				entitatId,
				tascaId,
				documentId);
	}

	@Override
	@RolesAllowed("tothom")
	public DocumentDto updateDocument(Long entitatId,
			Long tascaId,
			DocumentDto documentDto,
			boolean comprovarMetaExpedient) {
		return delegate.updateDocument(
				entitatId,
				tascaId,
				documentDto,
				comprovarMetaExpedient);
	}

	@Override
	@RolesAllowed("tothom")
	public ContingutDto deleteTascaReversible(
			Long entitatId,
			Long tascaId,
			Long contingutId) throws IOException {
		return delegate.deleteTascaReversible(
				entitatId,
				tascaId,
				contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public void portafirmesEnviar(
			Long entitatId,
			Long documentId,
			String assumpte,
			PortafirmesPrioritatEnumDto prioritat,
			Date dataCaducitat,
			String[] portafirmesResponsables,
			MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSeqTipus,
			MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus,
			Long[] annexosIds,
			Long tascaId,
			String transaccioId) {
		delegate.portafirmesEnviar(
				entitatId,
				documentId,
				assumpte,
				prioritat,
				dataCaducitat,
				portafirmesResponsables,
				portafirmesSeqTipus,
				portafirmesFluxTipus,
				annexosIds,
				tascaId,
				transaccioId);
	}

	@Override
	@RolesAllowed("tothom")
	public DocumentPortafirmesDto portafirmesInfo(
			Long entitatId,
			Long tascaId,
			Long documentId) {
		return delegate.portafirmesInfo(
				entitatId,
				tascaId,
				documentId);
	}

	@Override
	@RolesAllowed("tothom")
	public void portafirmesCancelar(
			Long entitatId,
			Long tascaId,
			Long docuemntId, String rolActual) {
		delegate.portafirmesCancelar(
				entitatId,
				tascaId,
				docuemntId, rolActual);
		
	}

	@Override
	public FitxerDto convertirPdfPerFirmaClient(
			Long entitatId,
			Long tascaId,
			Long documentId) {
		return delegate.convertirPdfPerFirmaClient(
				entitatId,
				tascaId,
				documentId);
	}

	@Override
	public String generarIdentificadorFirmaClient(
			Long entitatId,
			Long tascaId,
			Long id) {
		return delegate.generarIdentificadorFirmaClient(
				entitatId,
				tascaId,
				id);
	}

	@Override
	public void processarFirmaClient(
			String identificador,
			String arxiuNom,
			byte[] arxiuContingut,
			Long tascaId) {

		delegate.processarFirmaClient(
				identificador,
				arxiuNom,
				arxiuContingut,
				tascaId);
	}

	@Override
	public void portafirmesReintentar(
			Long entitatId,
			Long id, 
			Long tascaId) {
		delegate.portafirmesReintentar(
				entitatId,
				id,
				tascaId);
	}

	@Override
	public void enviarEmailCrearTasca(
			Long expedientTascaId) {
		delegate.enviarEmailCrearTasca(
				expedientTascaId);
	}

	@Override
	public ExpedientTascaDto canviarEstat(
			Long expedientTascaId,
			TascaEstatEnumDto tascaEstatEnumDto,
			String motiu) {
		return delegate.canviarEstat(
				expedientTascaId,
				tascaEstatEnumDto,
				motiu);
	}
	public ExpedientTascaDto updateResponsables(Long expedientTascaId, 
			String usuariCodi) {
		return delegate.updateResponsables(
				expedientTascaId,
				usuariCodi);
	}
		
	@Override
	public List<MetaExpedientTascaDto> findAmbEntitat(Long entitatId) {
		return delegate.findAmbEntitat(entitatId);
	}

    @Override
    public boolean publicarComentariPerExpedientTasca(Long entitatId, Long expedientTascaId, String text, String rolActual) {
        return delegate.publicarComentariPerExpedientTasca(entitatId, expedientTascaId, text, rolActual);
    }

	@Override
	public List<ExpedientTascaComentariDto> findComentarisPerTasca(Long entitatId, Long expedientTascaId) {
		return delegate.findComentarisPerTasca(entitatId, expedientTascaId);
	}

}