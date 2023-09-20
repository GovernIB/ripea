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
import es.caib.ripea.core.api.dto.ResultDto;
import es.caib.ripea.core.api.dto.ResultEnumDto;
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
	public ExpedientEstatDto createExpedientEstat(Long entitatId, ExpedientEstatDto estat, String rolActual, Long organId) {
		return delegate.createExpedientEstat(entitatId, estat, rolActual, organId);
	}

	@Override
	@RolesAllowed("tothom")
	public ExpedientEstatDto updateExpedientEstat(Long entitatId, ExpedientEstatDto estat, String rolActual, Long organId) {
		return delegate.updateExpedientEstat(entitatId, estat, rolActual, organId);
	}

	@Override
	@RolesAllowed("tothom")
	public ExpedientEstatDto moveTo(
			Long entitatId,
			Long metaExpedientId,
			Long expedientEstatId,
			int posicio, String rolActual) throws NotFoundException {
		return delegate.moveTo(entitatId, metaExpedientId, expedientEstatId, posicio, rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public ExpedientEstatDto deleteExpedientEstat(Long entitatId, Long expedientEstatId, String rolActual, Long organId) throws NotFoundException {
		return delegate.deleteExpedientEstat(entitatId, expedientEstatId, rolActual, organId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ExpedientEstatDto> findExpedientEstats(Long entitatId, Long expedientId, String rolActual) {
		return delegate.findExpedientEstats(entitatId, expedientId, rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public ExpedientDto changeExpedientEstat(
			Long entitatId,
			Long expedientId,
			Long expedientEstatId) {
		return delegate.changeExpedientEstat(
				entitatId,
				expedientId,
				expedientEstatId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ExpedientEstatDto> findExpedientEstatsByMetaExpedient(Long entitatId, Long metaExpedientId) {
		return delegate.findExpedientEstatsByMetaExpedient(entitatId, metaExpedientId);
	}

	@Override
	@RolesAllowed("tothom")
	public ResultDto<ExpedientDto> findExpedientsPerCanviEstatMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			String rolActual,
			ResultEnumDto resultEnum) throws NotFoundException {
		return delegate.findExpedientsPerCanviEstatMassiu(
				entitatId,
				filtre,
				paginacioParams,
				rolActual,
				resultEnum);
	}



}
