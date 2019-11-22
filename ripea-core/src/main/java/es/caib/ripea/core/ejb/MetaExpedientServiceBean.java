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

import es.caib.ripea.core.api.dto.MetaExpedientDominiDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaExpedientTascaDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.exception.NotFoundException;
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
	public PaginaDto<MetaExpedientDto> findByEntitat(
			Long entitatId,
			PaginacioParamsDto paginacioParams) {
		return delegate.findByEntitat(entitatId, paginacioParams);
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

	@Override
	@RolesAllowed("tothom")
	public long getProximNumeroSequencia(
			Long entitatId,
			Long id,
			int any) throws NotFoundException {
		return delegate.getProximNumeroSequencia(
				entitatId,
				id,
				any);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public MetaExpedientTascaDto tascaCreate(
			Long entitatId,
			Long metaExpedientId,
			MetaExpedientTascaDto metaExpedientTasca) throws NotFoundException {
		return delegate.tascaCreate(
				entitatId,
				metaExpedientId,
				metaExpedientTasca);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public MetaExpedientTascaDto tascaUpdate(
			Long entitatId,
			Long metaExpedientId,
			MetaExpedientTascaDto metaExpedientTasca) throws NotFoundException {
		return delegate.tascaUpdate(
				entitatId,
				metaExpedientId,
				metaExpedientTasca);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public MetaExpedientTascaDto tascaUpdateActiu(
			Long entitatId,
			Long metaExpedientId,
			Long id,
			boolean activa) throws NotFoundException {
		return delegate.tascaUpdateActiu(
				entitatId,
				metaExpedientId,
				id,
				activa);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public MetaExpedientTascaDto tascaDelete(
			Long entitatId,
			Long metaExpedientId,
			Long id) throws NotFoundException {
		return delegate.tascaDelete(
				entitatId,
				metaExpedientId,
				id);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public MetaExpedientTascaDto tascaFindById(
			Long entitatId,
			Long metaExpedientId,
			Long id) throws NotFoundException {
		return delegate.tascaFindById(
				entitatId,
				metaExpedientId,
				id);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public PaginaDto<MetaExpedientTascaDto> tascaFindPaginatByMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			PaginacioParamsDto paginacioParams) throws NotFoundException {
		return delegate.tascaFindPaginatByMetaExpedient(
				entitatId,
				metaExpedientId,
				paginacioParams);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public List<PermisDto> permisFind(
			Long entitatId,
			Long id) {
		return delegate.permisFind(
				entitatId,
				id);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public void permisUpdate(
			Long entitatId,
			Long id,
			PermisDto permis) {
		delegate.permisUpdate(
				entitatId,
				id,
				permis);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public void permisDelete(Long entitatId, Long id, Long permisId) {
		delegate.permisDelete(
				entitatId,
				id,
				permisId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public MetaExpedientDominiDto dominiCreate(
			Long entitatId, 
			Long metaExpedientId,
			MetaExpedientDominiDto metaExpedientTasca) throws NotFoundException {
		return delegate.dominiCreate(
				entitatId, 
				metaExpedientId, 
				metaExpedientTasca);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public MetaExpedientDominiDto dominiUpdate(
			Long entitatId, 
			Long metaExpedientId,
			MetaExpedientDominiDto metaExpedientTasca) throws NotFoundException {
		return delegate.dominiUpdate(
				entitatId,
				metaExpedientId, 
				metaExpedientTasca);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public MetaExpedientDominiDto dominiDelete(
			Long entitatId, 
			Long metaExpedientId, 
			Long id) throws NotFoundException {
		return delegate.dominiDelete(
				entitatId, 
				metaExpedientId, 
				id);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public MetaExpedientDominiDto dominiFindById(
			Long entitatId, 
			Long metaExpedientId, 
			Long id)
			throws NotFoundException {
		return delegate.dominiFindById(
				entitatId, 
				metaExpedientId, 
				id);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public PaginaDto<MetaExpedientDominiDto> dominiFindPaginatByMetaExpedient(Long entitatId, Long metaExpedientId,
			PaginacioParamsDto paginacioParams) throws NotFoundException {
		return delegate.dominiFindPaginatByMetaExpedient(
				entitatId,
				metaExpedientId, 
				paginacioParams);
	}

	@Override
	@RolesAllowed("tothom")
	public List<MetaExpedientDominiDto> dominiFindByMetaExpedient(
			Long entitatId, 
			Long metaExpedientId)
			throws NotFoundException {
		return delegate.dominiFindByMetaExpedient(
				entitatId, 
				metaExpedientId);
	}

}
