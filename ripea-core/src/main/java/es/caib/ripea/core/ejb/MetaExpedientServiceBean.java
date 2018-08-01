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

import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaExpedientMetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaNodeMetaDadaDto;
import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.service.MetaExpedientService;

/**
 * Implementació de MetaExpedientService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class MetaExpedientServiceBean implements MetaExpedientService {

	@Autowired
	MetaExpedientService delegate;



	@Override
	@RolesAllowed("IPA_ADMIN")
	public MetaExpedientDto create(
			Long entitatId,
			MetaExpedientDto metaExpedient) {
		return delegate.create(entitatId, metaExpedient);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public MetaExpedientDto update(
			Long entitatId,
			MetaExpedientDto metaExpedient) {
		return delegate.update(entitatId, metaExpedient);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public MetaExpedientDto updateActiu(
			Long entitatId,
			Long id,
			boolean actiu) {
		return delegate.updateActiu(entitatId, id, actiu);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public MetaExpedientDto delete(
			Long entitatId,
			Long metaExpedientId) {
		return delegate.delete(entitatId, metaExpedientId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public MetaExpedientDto findById(
			Long entitatId,
			Long id) {
		return delegate.findById(
				entitatId,
				id);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public MetaExpedientDto findByEntitatCodi(
			Long entitatId,
			String codi) {
		return delegate.findByEntitatCodi(entitatId, codi);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public PaginaDto<MetaExpedientDto> findByEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams) {
		return delegate.findByEntitatPaginat(entitatId, paginacioParams);
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
	public void metaDocumentCreate(
			Long entitatId,
			Long id,
			Long metaDocumentId,
			MultiplicitatEnumDto multiplicitat,
			boolean readOnly) {
		delegate.metaDocumentCreate(entitatId, id, metaDocumentId, multiplicitat, readOnly);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public void metaDocumentUpdate(
			Long entitatId,
			Long id,
			Long metaExpedientMetaDocumentId,
			MultiplicitatEnumDto multiplicitat,
			boolean readOnly) {
		delegate.metaDocumentUpdate(entitatId, id, metaExpedientMetaDocumentId, multiplicitat, readOnly);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public void metaDocumentDelete(
			Long entitatId,
			Long id,
			Long metaDocumentId) {
		delegate.metaDocumentDelete(entitatId, id, metaDocumentId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public void metaDocumentMove(
			Long entitatId,
			Long id,
			Long metaDocumentId,
			int posicio) {
		delegate.metaDocumentMove(entitatId, id, metaDocumentId, posicio);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public MetaExpedientMetaDocumentDto findMetaDocument(
			Long entitatId,
			Long id,
			Long metaExpedientMetaDocumentId) {
		return delegate.findMetaDocument(entitatId, id, metaExpedientMetaDocumentId);
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
	@RolesAllowed("IPA_ADMIN")
	public List<MetaExpedientDto> findByEntitat(
			Long entitatId) {
		return delegate.findByEntitat(entitatId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public List<MetaExpedientDto> findActiusAmbEntitatPerAdmin(
			Long entitatId) {
		return delegate.findActiusAmbEntitatPerAdmin(entitatId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<MetaExpedientDto> findActiusAmbEntitatPerCreacio(
			Long entitatId) {
		return delegate.findActiusAmbEntitatPerCreacio(entitatId);
	}
	
	@Override
	@RolesAllowed("tothom")
	public List<MetaExpedientDto> findActiusAmbEntitatPerModificacio(
			Long entitatId) {
		return delegate.findActiusAmbEntitatPerModificacio(entitatId);
	}	

	@Override
	@RolesAllowed("tothom")
	public List<MetaExpedientDto> findActiusAmbEntitatPerLectura(
			Long entitatId) {
		return delegate.findActiusAmbEntitatPerLectura(entitatId);
	}

}
