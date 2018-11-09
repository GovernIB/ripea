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
	@RolesAllowed("IPA_ADMIN")
	public MetaDocumentDto create(
			Long entitatId,
			Long metaExpedientId,
			MetaDocumentDto metaDocument,
			String plantillaNom,
			String plantillaContentType,
			byte[] plantillaContingut) {
		return delegate.create(
				entitatId,
				metaExpedientId,
				metaDocument,
				plantillaNom,
				plantillaContentType,
				plantillaContingut);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public MetaDocumentDto update(
			Long entitatId,
			Long metaExpedientId,
			MetaDocumentDto metaDocument,
			String plantillaNom,
			String plantillaContentType,
			byte[] plantillaContingut) {
		return delegate.update(
				entitatId,
				metaExpedientId,
				metaDocument,
				plantillaNom,
				plantillaContentType,
				plantillaContingut);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public MetaDocumentDto updateActiu(
			Long entitatId,
			Long metaExpedientId,
			Long id,
			boolean actiu) {
		return delegate.updateActiu(
				entitatId,
				metaExpedientId,
				id,
				actiu);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public MetaDocumentDto delete(
			Long entitatId,
			Long metaExpedientId,
			Long metaDocumentId) {
		return delegate.delete(
				entitatId,
				metaExpedientId,
				metaDocumentId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
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
	@RolesAllowed("IPA_ADMIN")
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
	@RolesAllowed("IPA_ADMIN")
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
			Long contenidorId) {
		return delegate.findActiusPerCreacio(
				entitatId,
				contenidorId);
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
	@RolesAllowed("IPA_ADMIN")
	public List<PortafirmesDocumentTipusDto> portafirmesFindDocumentTipus() {
		return delegate.portafirmesFindDocumentTipus();
	}

}
