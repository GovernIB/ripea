package es.caib.ripea.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.dto.ContingutMassiuFiltreDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientEstatDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.ExpedientEstatService;

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ExpedientEstatServiceBean implements ExpedientEstatService {

	@Autowired
	ExpedientEstatService delegate;

	@RolesAllowed("tothom")
	public PaginaDto<ExpedientEstatDto> findExpedientEstatByMetaExpedientPaginat(
			Long entitatId,
			Long metaExpedientId,
			PaginacioParamsDto paginacioParams) {
		return delegate.findExpedientEstatByMetaExpedientPaginat(entitatId, metaExpedientId, paginacioParams);
	}

	@Override
	@RolesAllowed("tothom")
	public ExpedientEstatDto findExpedientEstatById(Long entitatId, Long id) {
		return delegate.findExpedientEstatById(entitatId, id);
	}

	@Override
	@RolesAllowed("tothom")
	public ExpedientEstatDto createExpedientEstat(Long entitatId, ExpedientEstatDto estat) {
		return delegate.createExpedientEstat(entitatId, estat);
	}

	@Override
	@RolesAllowed("tothom")
	public ExpedientEstatDto updateExpedientEstat(Long entitatId, ExpedientEstatDto estat) {
		return delegate.updateExpedientEstat(entitatId, estat);
	}

	@Override
	@RolesAllowed("tothom")
	public ExpedientEstatDto moveTo(
			Long entitatId,
			Long metaExpedientId,
			Long expedientEstatId,
			int posicio) throws NotFoundException {
		return delegate.moveTo(entitatId, metaExpedientId, expedientEstatId, posicio);
	}

	@Override
	@RolesAllowed("tothom")
	public ExpedientEstatDto deleteExpedientEstat(Long entitatId, Long expedientEstatId) throws NotFoundException {
		return delegate.deleteExpedientEstat(entitatId, expedientEstatId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ExpedientEstatDto> findExpedientEstats(Long entitatId, Long expedientId) {
		return delegate.findExpedientEstats(entitatId, expedientId);
	}

	@Override
	@RolesAllowed("tothom")
	public ExpedientDto changeEstatOfExpedient(Long entitatId, Long expedientId, Long expedientEstatId) {
		return delegate.changeEstatOfExpedient(entitatId, expedientId, expedientEstatId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ExpedientEstatDto> findExpedientEstatsByMetaExpedient(Long entitatId, Long metaExpedientId) {
		return delegate.findExpedientEstatsByMetaExpedient(entitatId, metaExpedientId);
	}

	@Override
	@RolesAllowed("tothom")
	public PaginaDto<ExpedientDto> findExpedientsPerCanviEstatMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams) throws NotFoundException {
		return delegate.findExpedientsPerCanviEstatMassiu(
				entitatId,
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed("tothom")
	public List<Long> findIdsExpedientsPerCanviEstatMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre) throws NotFoundException {
		return delegate.findIdsExpedientsPerCanviEstatMassiu(
				entitatId,
				filtre);
	}

}
