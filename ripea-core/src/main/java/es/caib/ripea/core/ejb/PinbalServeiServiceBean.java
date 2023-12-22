package es.caib.ripea.core.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

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
	public void update(PinbalServeiDto pinbalServei) {
		delegate.update(pinbalServei);
	}

	@Override
	@RolesAllowed("IPA_SUPER")
	public PinbalServeiDto findById(Long id) {
		return delegate.findById(id);
	}

	@Override
	@RolesAllowed("IPA_SUPER")
	public PinbalServeiDto findByCodi(String codi) {
		return delegate.findByCodi(codi);
	}

}