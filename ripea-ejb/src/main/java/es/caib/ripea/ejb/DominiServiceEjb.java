package es.caib.ripea.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.DominiDto;
import es.caib.ripea.service.intf.dto.MetaExpedientDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.ResultatConsultaDto;
import es.caib.ripea.service.intf.dto.ResultatDominiDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.DominiService;
import lombok.experimental.Delegate;

@Stateless
public class DominiServiceEjb extends AbstractServiceEjb<DominiService> implements DominiService {

	@Delegate private DominiService delegateService;

	protected void setDelegateService(DominiService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("**")
	public DominiDto create(
			Long entitatId, 
			DominiDto tipusDocumental) throws NotFoundException {
		return delegateService.create(
				entitatId, 
				tipusDocumental);
	}

	@Override
	@RolesAllowed("**")
	public DominiDto update(
			Long entitatId, 
			DominiDto tipusDocumental) throws NotFoundException {
		return delegateService.update(
				entitatId,
				tipusDocumental);
	}

	@Override
	@RolesAllowed("**")
	public DominiDto delete(
			Long entitatId, 
			Long id) throws NotFoundException {
		return delegateService.delete(
				entitatId, 
				id);
	}

	@Override
	@RolesAllowed("**")
	public DominiDto findById(
			Long entitatId, 
			Long id) throws NotFoundException {
		return delegateService.findById(
				entitatId, 
				id);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<DominiDto> findByEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams)
			throws NotFoundException {
		return delegateService.findByEntitatPaginat(
				entitatId, 
				paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public List<DominiDto> findByEntitat(
			Long entitatId) throws NotFoundException {
		return delegateService.findByEntitat(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public DominiDto findByCodiAndEntitat(String codi, Long entitatId) throws NotFoundException {
		return delegateService.findByCodiAndEntitat(codi, entitatId);

	}

	@Override
	@RolesAllowed("**")
	public ResultatDominiDto getResultDomini(Long entitatId, DominiDto domini, String filter, int page, int resultCount)
			throws NotFoundException {
		return delegateService.getResultDomini(entitatId, domini, filter, page, resultCount);
	}

	@Override
	@RolesAllowed("**")
	public ResultatConsultaDto getSelectedDomini(Long entitatId, DominiDto domini, String dadaValor)
			throws NotFoundException {
		return delegateService.getSelectedDomini(entitatId, domini, dadaValor);
	}

	@Override
	@RolesAllowed("**")
	public List<DominiDto> findByMetaNodePermisLecturaAndTipusDomini(Long entitatId, MetaExpedientDto metaExpedient) {
		return delegateService.findByMetaNodePermisLecturaAndTipusDomini(entitatId, metaExpedient);
	}

	@Override
	@RolesAllowed("**")
	public void evictDominiCache() {
		delegateService.evictDominiCache();
	}
}
