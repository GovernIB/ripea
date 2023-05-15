package es.caib.ripea.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.URLInstruccioFiltreDto;
import es.caib.ripea.core.api.dto.URLInstruccioDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.URLInstruccioService;

/**
 * Implementació de URLInstruccioService com a EJB que empra una clase delegada
 * per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class URLInstruccioServiceBean implements URLInstruccioService {

	@Autowired
	URLInstruccioService delegate;

	@Override
	@RolesAllowed("IPA_ADMIN")
	public URLInstruccioDto create(Long entitatId, URLInstruccioDto url) throws NotFoundException {
		return delegate.create(entitatId, url);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public URLInstruccioDto update(Long entitatId, URLInstruccioDto url) throws NotFoundException {
		return delegate.update(entitatId, url);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public URLInstruccioDto delete(Long entitatId, Long id) throws NotFoundException {
		return delegate.delete(entitatId, id);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public URLInstruccioDto findById(Long entitatId, Long id) throws NotFoundException {
		return delegate.findById(entitatId, id);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public PaginaDto<URLInstruccioDto> findByEntitatPaginat(Long entitatId, URLInstruccioFiltreDto filtre,
			PaginacioParamsDto paginacioParams) throws NotFoundException {
		return delegate.findByEntitatPaginat(entitatId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"tothom", "IPA_ADMIN"})
	public List<URLInstruccioDto> findByEntitat(Long entitatId) throws NotFoundException {
		return delegate.findByEntitat(entitatId);
	}

	@Override
	@RolesAllowed("tothom")
	public String getURLInstruccio(Long entitatId, Long contingutId, Long urlInstruccioId) {
		return delegate.getURLInstruccio(entitatId, contingutId, urlInstruccioId);
	}



}
