package es.caib.ripea.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.URLInstruccioDto;
import es.caib.ripea.service.intf.dto.URLInstruccioFiltreDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.URLInstruccioService;
import lombok.experimental.Delegate;

@Stateless
public class URLInstruccioServiceEjb extends AbstractServiceEjb<URLInstruccioService> implements URLInstruccioService {

	@Delegate private URLInstruccioService delegateService;

	protected void setDelegateService(URLInstruccioService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public URLInstruccioDto create(Long entitatId, URLInstruccioDto url) throws NotFoundException {
		return delegateService.create(entitatId, url);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public URLInstruccioDto update(Long entitatId, URLInstruccioDto url) throws NotFoundException {
		return delegateService.update(entitatId, url);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public URLInstruccioDto delete(Long entitatId, Long id) throws NotFoundException {
		return delegateService.delete(entitatId, id);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public URLInstruccioDto findById(Long entitatId, Long id) throws NotFoundException {
		return delegateService.findById(entitatId, id);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public PaginaDto<URLInstruccioDto> findByEntitatPaginat(Long entitatId, URLInstruccioFiltreDto filtre,
			PaginacioParamsDto paginacioParams) throws NotFoundException {
		return delegateService.findByEntitatPaginat(entitatId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public List<URLInstruccioDto> findByEntitat(Long entitatId) throws NotFoundException {
		return delegateService.findByEntitat(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public String getURLInstruccio(Long entitatId, Long contingutId, Long urlInstruccioId) {
		return delegateService.getURLInstruccio(entitatId, contingutId, urlInstruccioId);
	}



}
