package es.caib.ripea.ejb;

import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.DominiService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class DominiServiceEjb implements DominiService {

	@Delegate
	private DominiService delegateService;

	protected void setDelegateService(DominiService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	public DominiDto create(
			Long entitatId, 
			DominiDto tipusDocumental) throws NotFoundException {
		return delegateService.create(
				entitatId, 
				tipusDocumental);
	}

	@Override
	public DominiDto update(
			Long entitatId, 
			DominiDto tipusDocumental) throws NotFoundException {
		return delegateService.update(
				entitatId,
				tipusDocumental);
	}

	@Override
	public DominiDto delete(
			Long entitatId, 
			Long id) throws NotFoundException {
		return delegateService.delete(
				entitatId, 
				id);
	}

	@Override
	public DominiDto findById(
			Long entitatId, 
			Long id) throws NotFoundException {
		return delegateService.findById(
				entitatId, 
				id);
	}

	@Override
	public PaginaDto<DominiDto> findByEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams)
			throws NotFoundException {
		return delegateService.findByEntitatPaginat(
				entitatId, 
				paginacioParams);
	}

	@Override
	public List<DominiDto> findByEntitat(
			Long entitatId) throws NotFoundException {
		return delegateService.findByEntitat(entitatId);
	}

	@Override
	public DominiDto findByCodiAndEntitat(String codi, Long entitatId) throws NotFoundException {
		return delegateService.findByCodiAndEntitat(codi, entitatId);

	}

	@Override
	public ResultatDominiDto getResultDomini(Long entitatId, DominiDto domini, String filter, int page, int resultCount)
			throws NotFoundException {
		return delegateService.getResultDomini(entitatId, domini, filter, page, resultCount);
	}

	@Override
	public ResultatConsultaDto getSelectedDomini(Long entitatId, DominiDto domini, String dadaValor)
			throws NotFoundException {
		return delegateService.getSelectedDomini(entitatId, domini, dadaValor);
	}

	@Override
	public List<DominiDto> findByMetaNodePermisLecturaAndTipusDomini(Long entitatId, MetaExpedientDto metaExpedient) {
		return delegateService.findByMetaNodePermisLecturaAndTipusDomini(entitatId, metaExpedient);
	}

	@Override
	public void evictDominiCache() {
		delegateService.evictDominiCache();
	}
}
