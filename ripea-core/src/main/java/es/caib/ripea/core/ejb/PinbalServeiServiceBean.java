package es.caib.ripea.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;
import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PinbalServeiDto;
import es.caib.ripea.core.api.service.PinbalServeiService;

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class PinbalServeiServiceBean implements PinbalServeiService {

    @Autowired
    private PinbalServeiService delegate;

	@Override
	@RolesAllowed("IPA_SUPER")
	public PaginaDto<PinbalServeiDto> findPaginat(PaginacioParamsDto paginacioParams) {
		return delegate.findPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed("IPA_SUPER")
	public PinbalServeiDto update(PinbalServeiDto pinbalServei) {
		return delegate.update(pinbalServei);
	}

	@Override
	@RolesAllowed("IPA_SUPER")
	public PinbalServeiDto findById(Long id) {
		return delegate.findById(id);
	}

	@Override
	@PreAuthorize("hasRole('tothom')")
	public List<PinbalServeiDto> findActius() {
		return delegate.findActius();
	}
	
	@Override
	@PreAuthorize("hasRole('tothom')")
	public List<PinbalServeiDto> findAll() {
		return delegate.findAll();
	}
}