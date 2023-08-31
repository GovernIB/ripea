package es.caib.ripea.core.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.dto.ArxiuPendentTipusEnumDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioFiltreDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioListDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.ResultDto;
import es.caib.ripea.core.api.dto.ResultEnumDto;
import es.caib.ripea.core.api.dto.SeguimentArxiuPendentsDto;
import es.caib.ripea.core.api.dto.SeguimentArxiuPendentsFiltreDto;
import es.caib.ripea.core.api.dto.SeguimentDto;
import es.caib.ripea.core.api.dto.SeguimentFiltreDto;
import es.caib.ripea.core.api.service.SeguimentService;

/**
 * Implementació de SeguimentService com a EJB que empra una clase delegada
 * per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public  class SeguimentServiceBean implements SeguimentService {

	@Autowired
	SeguimentService delegate;

	
	@Override
	@RolesAllowed("IPA_ADMIN")
	public PaginaDto<SeguimentDto> findPortafirmesEnviaments(
			Long entitatId,
			SeguimentFiltreDto filtre, 
			PaginacioParamsDto paginacioParams) {
		return delegate.findPortafirmesEnviaments(entitatId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public ResultDto<SeguimentDto> findNotificacionsEnviaments(
			Long entitatId,
			SeguimentFiltreDto filtre,
			PaginacioParamsDto paginacioParams, 
			ResultEnumDto resultEnum) {
		return delegate.findNotificacionsEnviaments(
				entitatId,
				filtre,
				paginacioParams, 
				resultEnum);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public PaginaDto<SeguimentDto> findTasques(
			Long entitatId,
			SeguimentFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findTasques(
				entitatId,
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public PaginaDto<ExpedientPeticioListDto> findAnotacionsPendents(
			Long entitatId,
			ExpedientPeticioFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			String rolActual) {
		return delegate.findAnotacionsPendents(
				entitatId,
				filtre,
				paginacioParams,
				rolActual);
	}

	@Override
	@RolesAllowed({ "IPA_ADMIN", "IPA_ORGAN_ADMIN", "tothom" })
	public ResultDto<SeguimentArxiuPendentsDto> findPendentsArxiu(
			Long entitatId,
			SeguimentArxiuPendentsFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			String rolActual,
			ResultEnumDto resultEnum,
			ArxiuPendentTipusEnumDto arxiuPendentTipusEnum) {
		return delegate.findPendentsArxiu(
				entitatId,
				filtre,
				paginacioParams,
				rolActual,
				resultEnum,
				arxiuPendentTipusEnum);
	}


}
