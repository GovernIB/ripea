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

import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaDocumentTipusGenericEnumDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PortafirmesDocumentTipusDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.MetaDocumentService;

/**
 * Implementació de MetaDocumentService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class MetaDocumentServiceBean implements MetaDocumentService {

	@Autowired
	MetaDocumentService delegate;

	@Override
	@RolesAllowed("tothom")
	public MetaDocumentDto create(
			Long entitatId,
			Long metaExpedientId,
			MetaDocumentDto metaDocument,
			String plantillaNom,
			String plantillaContentType,
			byte[] plantillaContingut, String rolActual, Long organId) {
		return delegate.create(
				entitatId,
				metaExpedientId,
				metaDocument,
				plantillaNom,
				plantillaContentType,
				plantillaContingut, rolActual, organId);
	}
	
	@Override
	@RolesAllowed("IPA_ADMIN")
	public MetaDocumentDto create(Long entitatId, 
			MetaDocumentDto metaDocument, String plantillaNom,
			String plantillaContentType, byte[] plantillaContingut) throws NotFoundException {
		return delegate.create(
				entitatId,
				metaDocument,
				plantillaNom,
				plantillaContentType,
				plantillaContingut);
	}


	@Override
	@RolesAllowed("tothom")
	public MetaDocumentDto update(
			Long entitatId,
			Long metaExpedientId,
			MetaDocumentDto metaDocument,
			String plantillaNom,
			String plantillaContentType,
			byte[] plantillaContingut, String rolActual, Long organId) {
		return delegate.update(
				entitatId,
				metaExpedientId,
				metaDocument,
				plantillaNom,
				plantillaContentType,
				plantillaContingut, rolActual, organId);
	}
	
	@Override
	@RolesAllowed("IPA_ADMIN")
	public MetaDocumentDto update(Long entitatId, MetaDocumentDto metaDocument, String plantillaNom,
			String plantillaContentType, byte[] plantillaContingut) throws NotFoundException {
		return delegate.update(
				entitatId,
				metaDocument,
				plantillaNom,
				plantillaContentType,
				plantillaContingut);
	}
	
	@Override
	@RolesAllowed("tothom")
	public MetaDocumentDto updateActiu(
			Long entitatId,
			Long metaExpedientId,
			Long id,
			boolean actiu, String rolActual) {
		return delegate.updateActiu(
				entitatId,
				metaExpedientId,
				id,
				actiu, rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public MetaDocumentDto delete(
			Long entitatId,
			Long metaExpedientId,
			Long metaDocumentId, String rolActual, Long organId) {
		return delegate.delete(
				entitatId,
				metaExpedientId,
				metaDocumentId, rolActual, organId);
	}

	@Override
	@RolesAllowed("tothom")
	public MetaDocumentDto findById(
			Long entitatId,
			Long metaExpedientId,
			Long id) {
		return delegate.findById(
				entitatId,
				metaExpedientId,
				id);
	}

	@Override
	@RolesAllowed("tothom")
	public MetaDocumentDto findByCodi(
			Long entitatId,
			Long metaExpedientId,
			String codi) {
		return delegate.findByCodi(
				entitatId,
				metaExpedientId,
				codi);
	}

	@Override
	@RolesAllowed("tothom")
	public PaginaDto<MetaDocumentDto> findByMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			PaginacioParamsDto paginacioParams) {
		return delegate.findByMetaExpedient(
				entitatId,
				metaExpedientId,
				paginacioParams);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public PaginaDto<MetaDocumentDto> findWithoutMetaExpedient(
			Long entitatId,
			PaginacioParamsDto paginacioParams) {
		return delegate.findWithoutMetaExpedient(
				entitatId,
				paginacioParams);
	}
	
	@Override
	@RolesAllowed("IPA_ADMIN")
	public List<MetaDocumentDto> findByEntitat(
			Long entitatId) throws NotFoundException {
		return delegate.findByEntitat(entitatId);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "tothom"})
	public FitxerDto getPlantilla(
			Long entitatId,
			Long contingutId,
			Long id) {
		return delegate.getPlantilla(
				entitatId,
				contingutId,
				id);
	}

	@Override
	@RolesAllowed("tothom")
	public List<MetaDocumentDto> findActiusPerCreacio(
			Long entitatId,
			Long contenidorId, 
			Long metaExpedientId) {
		return delegate.findActiusPerCreacio(
				entitatId,
				contenidorId, 
				metaExpedientId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<MetaDocumentDto> findActiusPerModificacio(
			Long entitatId,
			Long documentId) throws NotFoundException {
		return delegate.findActiusPerModificacio(
				entitatId,
				documentId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<PortafirmesDocumentTipusDto> portafirmesFindDocumentTipus() {
		return delegate.portafirmesFindDocumentTipus();
	}

	@Override
	@RolesAllowed("tothom")
	public List<MetaDocumentDto> findByMetaExpedient(Long entitatId, Long metaExpedientId) {
		return delegate.findByMetaExpedient(entitatId, metaExpedientId);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "tothom"})
	public MetaDocumentDto getDadesNti(Long entitatId, Long contingutId, Long id) throws NotFoundException {
		return delegate.getDadesNti(
				entitatId, 
				contingutId, 
				id);
	}

	@Override
	@RolesAllowed("tothom")
	public MetaDocumentDto findById(
			Long entitatId,
			Long metaDocumentId) {
		return delegate.findById(entitatId, metaDocumentId);
	}

	@Override
	@RolesAllowed("tothom")
	public MetaDocumentDto findByTipusGeneric(
			Long entitatId, 
			MetaDocumentTipusGenericEnumDto tipusGeneric) {
		return delegate.findByTipusGeneric(
				entitatId, 
				tipusGeneric);
	}

	@Override
	@RolesAllowed("tothom")
	public List<MetaDocumentDto> findByMetaExpedientAndFirmaPortafirmesActiva(Long entitatId, Long metaExpedientId) {
		return delegate.findByMetaExpedientAndFirmaPortafirmesActiva(entitatId, metaExpedientId);
	}

	@Override
	@RolesAllowed("tothom")
	public void marcarPerDefecte(Long entitatId, Long metaExpedientId, Long metaDocumentId, boolean remove) throws NotFoundException {
		delegate.marcarPerDefecte(entitatId, metaExpedientId, metaDocumentId, remove);
	}

	@Override
	@RolesAllowed("tothom")
	public MetaDocumentDto findByMetaExpedientAndPerDefecteTrue(
			Long entitatId,
			Long metaExpedientId) throws NotFoundException {
		return delegate.findByMetaExpedientAndPerDefecteTrue(
				entitatId, 
				metaExpedientId);
	}

}
