/**
 * 
 */
package es.caib.ripea.core.ejb;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.ContingutFiltreDto;
import es.caib.ripea.core.api.dto.ContingutLogDto;
import es.caib.ripea.core.api.dto.ContingutMovimentDto;
import es.caib.ripea.core.api.dto.DadaDto;
import es.caib.ripea.core.api.dto.EscriptoriDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.ValidacioErrorDto;
import es.caib.ripea.core.api.service.ContingutService;

/**
 * Implementació de ContenidorService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ContingutServiceBean implements ContingutService {

	@Autowired
	ContingutService delegate;



	@Override
	@RolesAllowed("tothom")
	public ContingutDto rename(
			Long entitatId,
			Long contingutId,
			String nom) {
		return delegate.rename(
				entitatId,
				contingutId,
				nom);
	}

	@Override
	@RolesAllowed("tothom")
	public DadaDto dadaCreate(
			Long entitatId,
			Long contingutId,
			Long metaDadaId,
			Object valor) {
		return delegate.dadaCreate(
				entitatId,
				contingutId,
				metaDadaId,
				valor);
	}

	@Override
	@RolesAllowed("tothom")
	public DadaDto dadaUpdate(
			Long entitatId,
			Long contingutId,
			Long dadaId,
			Object valor) {
		return delegate.dadaUpdate(entitatId, contingutId, dadaId, valor);
	}

	@Override
	@RolesAllowed("tothom")
	public DadaDto dadaDelete(
			Long entitatId,
			Long contingutId,
			Long dadaId) {
		return delegate.dadaDelete(entitatId, contingutId, dadaId);
	}

	@Override
	@RolesAllowed("tothom")
	public DadaDto dadaFindById(
			Long entitatId,
			Long contingutId,
			Long dadaId) {
		return delegate.dadaFindById(entitatId, contingutId, dadaId);
	}

	@Override
	@RolesAllowed("tothom")
	public ContingutDto deleteReversible(
			Long entitatId,
			Long contingutId) {
		return delegate.deleteReversible(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public ContingutDto deleteDefinitiu(
			Long entitatId,
			Long contingutId) {
		return delegate.deleteDefinitiu(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public ContingutDto undelete(
			Long entitatId,
			Long contingutId) {
		return delegate.undelete(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public ContingutDto move(
			Long entitatId,
			Long contingutOrigenId,
			Long contingutDestiId) {
		return delegate.move(entitatId, contingutOrigenId, contingutDestiId);
	}

	@Override
	@RolesAllowed("tothom")
	public ContingutDto copy(
			Long entitatId,
			Long contingutOrigenId,
			Long contingutDestiId,
			boolean recursiu) {
		return delegate.copy(entitatId, contingutOrigenId, contingutDestiId, recursiu);
	}

	@Override
	@RolesAllowed("tothom")
	public EscriptoriDto getEscriptoriPerUsuariActual(
			Long entitatId) {
		return delegate.getEscriptoriPerUsuariActual(entitatId);
	}

	@Override
	@RolesAllowed("tothom")
	public ContingutDto findAmbIdUser(
			Long entitatId,
			Long contingutId,
			boolean ambFills) {
		return delegate.findAmbIdUser(
				entitatId,
				contingutId,
				ambFills);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public ContingutDto findAmbIdAdmin(
			Long entitatId,
			Long contingutId,
			boolean ambFills) {
		return delegate.findAmbIdAdmin(
				entitatId,
				contingutId,
				ambFills);
	}

	@Override
	@RolesAllowed("tothom")
	public ContingutDto getContingutAmbFillsPerPath(
			Long entitatId,
			String path) {
		return delegate.getContingutAmbFillsPerPath(entitatId, path);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ValidacioErrorDto> findErrorsValidacio(
			Long entitatId,
			Long contingutId) {
		return delegate.findErrorsValidacio(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public List<ContingutLogDto> findLogsPerContingutAdmin(
			Long entitatId,
			Long contingutId) {
		return delegate.findLogsPerContingutAdmin(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ContingutLogDto> findLogsPerContingutUser(
			Long entitatId,
			Long contingutId) {
		return delegate.findLogsPerContingutUser(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public List<ContingutMovimentDto> findMovimentsPerContingutAdmin(
			Long entitatId,
			Long contingutId) {
		return delegate.findMovimentsPerContingutAdmin(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ContingutMovimentDto> findMovimentsPerContingutUser(
			Long entitatId,
			Long contingutId) {
		return delegate.findMovimentsPerContingutUser(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public PaginaDto<ContingutDto> findAdmin(
			Long entitatId,
			ContingutFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findAdmin(
				entitatId,
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public PaginaDto<ContingutDto> findEsborrats(
			Long entitatId,
			String nom,
			String usuariCodi,
			Date dataInici,
			Date dataFi,
			PaginacioParamsDto paginacioParams) {
		return delegate.findEsborrats(
				entitatId,
				nom,
				usuariCodi,
				dataInici,
				dataFi,
				paginacioParams);
	}

}
