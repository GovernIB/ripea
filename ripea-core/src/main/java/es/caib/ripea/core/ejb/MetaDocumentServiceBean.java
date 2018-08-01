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
import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaNodeMetaDadaDto;
import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermisDto;
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
			MetaDocumentDto metaDocument,
			String plantillaNom,
			String plantillaContentType,
			byte[] plantillaContingut) {
		return delegate.create(
				entitatId,
				metaDocument,
				plantillaNom,
				plantillaContentType,
				plantillaContingut);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public MetaDocumentDto update(
			Long entitatId,
			MetaDocumentDto metaDocument,
			String plantillaNom,
			String plantillaContentType,
			byte[] plantillaContingut) {
		return delegate.update(
				entitatId,
				metaDocument,
				plantillaNom,
				plantillaContentType,
				plantillaContingut);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public MetaDocumentDto updateActiu(
			Long entitatId,
			Long id,
			boolean actiu) {
		return delegate.updateActiu(entitatId, id, actiu);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public MetaDocumentDto delete(
			Long entitatId,
			Long metaDocumentId) {
		return delegate.delete(entitatId, metaDocumentId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public MetaDocumentDto findById(
			Long entitatId,
			Long id) {
		return delegate.findById(
				entitatId,
				id);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public MetaDocumentDto findByEntitatCodi(
			Long entitatId,
			String codi) {
		return delegate.findByEntitatCodi(entitatId, codi);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public List<MetaDocumentDto> findByEntitat(
			Long entitatId) {
		return delegate.findByEntitat(entitatId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public PaginaDto<MetaDocumentDto> findByEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams) {
		return delegate.findByEntitatPaginat(entitatId, paginacioParams);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public List<MetaDocumentDto> findByEntitatAndActiveTrue(
			Long entitatId,
			boolean incloureGlobalsExpedient) {
		return delegate.findByEntitatAndActiveTrue(
				entitatId,
				incloureGlobalsExpedient);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "tothom"})
	public FitxerDto getPlantilla(
			Long entitatId,
			Long id) {
		return delegate.getPlantilla(entitatId, id);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public void metaDadaCreate(
			Long entitatId,
			Long id,
			Long metaDadaId,
			MultiplicitatEnumDto multiplicitat,
			boolean readOnly) {
		delegate.metaDadaCreate(entitatId, id, metaDadaId, multiplicitat, readOnly);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public void metaDadaUpdate(
			Long entitatId,
			Long id,
			Long metaNodeMetaDadaId,
			MultiplicitatEnumDto multiplicitat,
			boolean readOnly) {
		delegate.metaDadaUpdate(entitatId, id, metaNodeMetaDadaId, multiplicitat, readOnly);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public void metaDadaDelete(
			Long entitatId,
			Long id,
			Long metaDadaId) {
		delegate.metaDadaDelete(entitatId, id, metaDadaId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public void metaDadaMoveUp(
			Long entitatId,
			Long id,
			Long metaDadaId) {
		delegate.metaDadaMoveUp(entitatId, id, metaDadaId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public void metaDadaMoveDown(
			Long entitatId,
			Long id,
			Long metaDadaId) {
		delegate.metaDadaMoveDown(entitatId, id, metaDadaId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public void metaDadaMoveTo(
			Long entitatId,
			Long id,
			Long metaDadaId,
			int posicio) {
		delegate.metaDadaMoveTo(entitatId, id, metaDadaId, posicio);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public MetaNodeMetaDadaDto metaDadaFind(
			Long entitatId,
			Long id,
			Long metaNodeMetaDadaId) {
		return delegate.metaDadaFind(entitatId, id, metaNodeMetaDadaId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public List<MetaDadaDto> metaDadaFindGlobals(
			Long entitatId) {
		return delegate.metaDadaFindGlobals(entitatId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public List<PermisDto> findPermis(Long entitatId, Long id) {
		return delegate.findPermis(entitatId, id);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public void updatePermis(Long entitatId, Long id, PermisDto permis) {
		delegate.updatePermis(entitatId, id, permis);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public void deletePermis(Long entitatId, Long id, Long permisId) {
		delegate.deletePermis(entitatId, id, permisId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<MetaDocumentDto> findActiveByEntitatAndContenidorPerCreacio(
			Long entitatId,
			Long contenidorId) {
		return delegate.findActiveByEntitatAndContenidorPerCreacio(
				entitatId,
				contenidorId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<MetaDocumentDto> findActiveByEntitatAndDocumentPerModificacio(
			Long entitatId,
			Long documentId) throws NotFoundException {
		return delegate.findActiveByEntitatAndDocumentPerModificacio(
				entitatId,
				documentId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public List<PortafirmesDocumentTipusDto> findPortafirmesDocumentTipus() {
		return delegate.findPortafirmesDocumentTipus();
	}

}
