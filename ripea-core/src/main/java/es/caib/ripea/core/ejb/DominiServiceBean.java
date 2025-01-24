package es.caib.ripea.core.ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.dto.DominiDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.ResultatConsultaDto;
import es.caib.ripea.core.api.dto.ResultatDominiDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.DominiService;

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class DominiServiceBean implements DominiService {

	@Autowired
	private DominiService delegate;
	
	@Override
	public DominiDto create(
			Long entitatId, 
			DominiDto tipusDocumental) throws NotFoundException {
		return delegate.create(
				entitatId, 
				tipusDocumental);
	}

	@Override
	public DominiDto update(
			Long entitatId, 
			DominiDto tipusDocumental) throws NotFoundException {
		return delegate.update(
				entitatId,
				tipusDocumental);
	}

	@Override
	public DominiDto delete(
			Long entitatId, 
			Long id) throws NotFoundException {
		return delegate.delete(
				entitatId, 
				id);
	}

	@Override
	public DominiDto findById(
			Long entitatId, 
			Long id) throws NotFoundException {
		return delegate.findById(
				entitatId, 
				id);
	}

	@Override
	public PaginaDto<DominiDto> findByEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams)
			throws NotFoundException {
		return delegate.findByEntitatPaginat(
				entitatId, 
				paginacioParams);
	}

	@Override
	public List<DominiDto> findByEntitat(
			Long entitatId) throws NotFoundException {
		return delegate.findByEntitat(entitatId);
	}

	@Override
	public DominiDto findByCodiAndEntitat(String codi, Long entitatId) throws NotFoundException {
		return delegate.findByCodiAndEntitat(codi, entitatId);

	}

	@Override
	public ResultatDominiDto getResultDomini(Long entitatId, DominiDto domini, String filter, int page, int resultCount)
			throws NotFoundException {
		return delegate.getResultDomini(entitatId, domini, filter, page, resultCount);
	}

	@Override
	public ResultatConsultaDto getSelectedDomini(Long entitatId, DominiDto domini, String dadaValor)
			throws NotFoundException {
		return delegate.getSelectedDomini(entitatId, domini, dadaValor);
	}

	@Override
	public List<DominiDto> findByMetaNodePermisLecturaAndTipusDomini(Long entitatId, MetaExpedientDto metaExpedient) {
		return delegate.findByMetaNodePermisLecturaAndTipusDomini(entitatId, metaExpedient);
	}

	@Override
	public void evictDominiCache() {
		delegate.evictDominiCache();
	}
}
