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

import es.caib.ripea.core.api.dto.AvisDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.service.AvisService;

/**
 * Implementació de AvisService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class AvisServiceBean implements AvisService {

	@Autowired
	AvisService delegate;

	@Override
	@RolesAllowed("IPA_SUPER")
	public AvisDto create(AvisDto avis) {
		return delegate.create(avis);
	}

	@Override
	@RolesAllowed("IPA_SUPER")
	public AvisDto update(AvisDto avis) {
		return delegate.update(avis);
	}

	@Override
	@RolesAllowed("IPA_SUPER")
	public AvisDto updateActiva(Long id, boolean activa) {
		return delegate.updateActiva(id, activa);
	}

	@Override
	@RolesAllowed("IPA_SUPER")
	public AvisDto delete(Long id) {
		return delegate.delete(id);
	}

	@Override
	@RolesAllowed("tothom")
	public AvisDto findById(Long id) {
		return delegate.findById(id);
	}

	@Override
	@RolesAllowed("tothom")
	public PaginaDto<AvisDto> findPaginat(PaginacioParamsDto paginacioParams) {
		return delegate.findPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed("tothom")
	public List<AvisDto> findActive() {
		return delegate.findActive();
	}

}
