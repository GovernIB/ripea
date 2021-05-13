package es.caib.ripea.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.dto.PermisOrganGestorDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.OrganGestorService;

/**
 * Implementació de OrganGestorService com a EJB que empra una clase delegada
 * per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class OrganGestorServiceBean implements OrganGestorService {

	@Autowired
	OrganGestorService delegate;

	@Override
	@RolesAllowed("IPA_ADMIN")
	public List<OrganGestorDto> findAll() {
		return delegate.findAll();
	}

	@Override
	@RolesAllowed("tothom")
	public OrganGestorDto findItem(Long id) {
		return delegate.findItem(id);
	}

	@Override
	@RolesAllowed("tothom")
	public List<OrganGestorDto> findByEntitat(Long entitatId) {
		return delegate.findByEntitat(entitatId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<OrganGestorDto> findByEntitat(Long entitatId, String filterText) {
		return delegate.findByEntitat(entitatId, filterText);
	}
	
	@Override
	@RolesAllowed("IPA_ADMIN")
	public PaginaDto<OrganGestorDto> findOrgansGestorsAmbFiltrePaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams) {
		return delegate.findOrgansGestorsAmbFiltrePaginat(entitatId, paginacioParams);
	}

	@Override
	@RolesAllowed("tothom")
	public List<OrganGestorDto> findPermesosByEntitatAndExpedientTipusIdAndFiltre(
			Long entitatId,
			Long metaExpedientId,
			String filter, 
			Long expedientId) {
		return delegate.findPermesosByEntitatAndExpedientTipusIdAndFiltre(
				entitatId,
				metaExpedientId,
				filter, 
				expedientId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public boolean syncDir3OrgansGestors(Long entitatId) throws Exception {
		return delegate.syncDir3OrgansGestors(entitatId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public List<PermisOrganGestorDto> findPermisos(Long entitatId) throws NotFoundException {
		return delegate.findPermisos(entitatId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public List<PermisOrganGestorDto> findPermisos(Long entitatId, Long organId) throws NotFoundException {
		return delegate.findPermisos(entitatId, organId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public void updatePermis(Long id, PermisDto permis, Long entitatId) throws NotFoundException {
		delegate.updatePermis(id, permis, entitatId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public void deletePermis(Long id, Long permisId, Long entitatId) throws NotFoundException {
		delegate.deletePermis(id, permisId, entitatId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<OrganGestorDto> findAccessiblesUsuariActualRolAdmin(Long entitatId, Long organGestorId) {
		return delegate.findAccessiblesUsuariActualRolAdmin(entitatId, organGestorId);
	}
	
	@Override
	@RolesAllowed("tothom")
	public List<OrganGestorDto> findAccessiblesUsuariActualRolAdmin(Long entitatId, Long organGestorId, String filterText) {
		return delegate.findAccessiblesUsuariActualRolAdmin(entitatId, organGestorId, filterText);
	}
	
	@Override
	@RolesAllowed("tothom")
	public List<OrganGestorDto> findOrganismesEntitatAmbPermis(Long entitatId) {
		return delegate.findOrganismesEntitatAmbPermis(entitatId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<OrganGestorDto> findAccessiblesUsuariActualRolUsuari(
			Long entitatId,
			String filter) {
		return delegate.findAccessiblesUsuariActualRolUsuari(entitatId, filter);
	}

	@Override
	@RolesAllowed("tothom")
	public OrganGestorDto findItemByEntitatAndCodi(
			Long entitatId,
			String codi) {
		return delegate.findItemByEntitatAndCodi(
				entitatId, 
				codi);
	}

}
