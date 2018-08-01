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

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.service.EntitatService;

/**
 * Implementació de EntitatService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class EntitatServiceBean implements EntitatService {

	@Autowired
	EntitatService delegate;



	@Override
	@RolesAllowed("IPA_SUPER")
	public EntitatDto create(EntitatDto entitat) {
		return delegate.create(entitat);
	}

	@Override
	@RolesAllowed("IPA_SUPER")
	public EntitatDto update(
			EntitatDto entitat) {
		return delegate.update(entitat);
	}

	@Override
	@RolesAllowed("IPA_SUPER")
	public EntitatDto updateActiva(
			Long id,
			boolean activa) {
		return delegate.updateActiva(id, activa);
	}

	@Override
	@RolesAllowed("IPA_SUPER")
	public EntitatDto delete(
			Long id) {
		return delegate.delete(id);
	}

	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public EntitatDto findById(Long id) {
		return delegate.findById(id);
	}

	@Override
	@RolesAllowed("IPA_SUPER")
	public EntitatDto findByCodi(String codi) {
		return delegate.findByCodi(codi);
	}

	@Override
	@RolesAllowed("IPA_SUPER")
	public PaginaDto<EntitatDto> findPaginat(PaginacioParamsDto paginacioParams) {
		return delegate.findPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public List<EntitatDto> findAccessiblesUsuariActual() {
		return delegate.findAccessiblesUsuariActual();
	}

	@Override
	@RolesAllowed("IPA_SUPER")
	public List<PermisDto> findPermisSuper(Long id) {
		return delegate.findPermisSuper(id);
	}

	@Override
	@RolesAllowed("IPA_SUPER")
	public void updatePermisSuper(
			Long id,
			PermisDto permis) {
		delegate.updatePermisSuper(
				id,
				permis);
	}
	@Override
	@RolesAllowed("IPA_SUPER")
	public void deletePermisSuper(
			Long id,
			Long permisId) {
		delegate.deletePermisSuper(
				id,
				permisId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public List<PermisDto> findPermisAdmin(Long id) {
		return delegate.findPermisAdmin(id);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public void updatePermisAdmin(
			Long id,
			PermisDto permis) {
		delegate.updatePermisAdmin(
				id,
				permis);
	}
	@Override
	@RolesAllowed("IPA_ADMIN")
	public void deletePermisAdmin(
			Long id,
			Long permisId) {
		delegate.deletePermisAdmin(
				id,
				permisId);
	}

}
