package es.caib.ripea.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.TipusDocumentalDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.TipusDocumentalService;
import lombok.experimental.Delegate;

@Stateless
@RolesAllowed("**")
public class TipusDocumentalServiceEjb extends AbstractServiceEjb<TipusDocumentalService> implements TipusDocumentalService {

	@Delegate private TipusDocumentalService delegateService;

	protected void setDelegateService(TipusDocumentalService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public TipusDocumentalDto create(
			Long entitatId, 
			TipusDocumentalDto tipusDocumental) throws NotFoundException {
		return delegateService.create(
				entitatId, 
				tipusDocumental);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public TipusDocumentalDto update(
			Long entitatId, 
			TipusDocumentalDto tipusDocumental) throws NotFoundException {
		return delegateService.update(
				entitatId,
				tipusDocumental);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public TipusDocumentalDto delete(
			Long entitatId, 
			Long id) throws NotFoundException {
		return delegateService.delete(
				entitatId, 
				id);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public TipusDocumentalDto findById(
			Long entitatId, 
			Long id) throws NotFoundException {
		return delegateService.findById(
				entitatId, 
				id);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public PaginaDto<TipusDocumentalDto> findByEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams)
			throws NotFoundException {
		return delegateService.findByEntitatPaginat(
				entitatId, 
				paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public List<TipusDocumentalDto> findByEntitat(
			Long entitatId) throws NotFoundException {
		return delegateService.findByEntitat(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public TipusDocumentalDto findByCodiAndEntitat(String codi, Long entitatId) throws NotFoundException {
		return delegateService.findByCodiAndEntitat(codi, entitatId);

	}
}
