/**
 * 
 */
package es.caib.ripea.core.ejb;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.dto.AlertaDto;
import es.caib.ripea.core.api.dto.ArxiuDetallDto;
import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.ContingutFiltreDto;
import es.caib.ripea.core.api.dto.ContingutLogDetallsDto;
import es.caib.ripea.core.api.dto.ContingutLogDto;
import es.caib.ripea.core.api.dto.ContingutMassiuFiltreDto;
import es.caib.ripea.core.api.dto.ContingutMovimentDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.ValidacioErrorDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;
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
	public void dadaSave(
			Long entitatId,
			Long contingutId,
			Map<String, Object> valors) {
		delegate.dadaSave(
				entitatId,
				contingutId,
				valors);
	}

	@Override
	@RolesAllowed("tothom")
	public ContingutDto deleteReversible(
			Long entitatId,
			Long contingutId, String rolActual) throws IOException {
		return delegate.deleteReversible(entitatId, contingutId, rolActual);
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
			Long contingutId) throws IOException {
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
	public ContingutDto findAmbIdUser(
			Long entitatId,
			Long contingutId,
			boolean ambFills,
			boolean ambVersions, String rolActual) {
		return delegate.findAmbIdUser(
				entitatId,
				contingutId,
				ambFills,
				ambVersions, null);
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
	@RolesAllowed("tothom")
	public List<AlertaDto> findAlertes(
			Long entitatId,
			Long contingutId) throws NotFoundException {
		return delegate.findAlertes(entitatId, contingutId);
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
	public ContingutLogDetallsDto findLogDetallsPerContingutAdmin(
			Long entitatId,
			Long contingutId,
			Long contingutLogId) throws NotFoundException {
		return delegate.findLogDetallsPerContingutAdmin(
				entitatId,
				contingutId,
				contingutLogId);
	}

	@Override
	@RolesAllowed("tothom")
	public ContingutLogDetallsDto findLogDetallsPerContingutUser(
			Long entitatId,
			Long contingutId,
			Long contingutLogId) throws NotFoundException {
		return delegate.findLogDetallsPerContingutUser(
				entitatId,
				contingutId,
				contingutLogId);
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

	@Override
	@RolesAllowed("tothom")
	public ArxiuDetallDto getArxiuDetall(
			Long entitatId,
			Long contingutId) {
		return delegate.getArxiuDetall(
				entitatId,
				contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public FitxerDto exportacioEni(
			Long entitatId,
			Long contingutId) {
		return delegate.exportacioEni(
				entitatId,
				contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<Long> findIdsDocumentsPerFirmaMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre, String rolActual)
			throws NotFoundException {
		return delegate.findIdsDocumentsPerFirmaMassiu(
				entitatId,
				filtre, rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public PaginaDto<DocumentDto> findDocumentsPerFirmaMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams, 
			String rolActual) throws NotFoundException {
		return delegate.findDocumentsPerFirmaMassiu(
				entitatId,
				filtre,
				paginacioParams, 
				rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public ContingutDto link(
			Long entitatId, 
			Long contingutOrigenId, 
			Long contingutDestiId, 
			boolean recursiu)
			throws NotFoundException, ValidationException {
		return delegate.link(
				entitatId, 
				contingutOrigenId, 
				contingutDestiId, 
				recursiu);
	}

	@Override
	@RolesAllowed("tothom")
	public ContingutDto findAmbIdUser(
			Long entitatId,
			Long contingutId,
			boolean ambFills,
			boolean ambVersions,
			boolean ambPermis, String rolActual) {
		return delegate.findAmbIdUser(
				entitatId,
				contingutId,
				ambFills,
				ambVersions,
				ambPermis, null);
	}

	@Override
	@RolesAllowed("tothom")
	public void order(
			Long entitatId,
			Long contingutId, 
			Map<Integer, Long> orderedElements)
			throws NotFoundException, ValidationException {
		delegate.order(entitatId, contingutId, orderedElements);
	}

}
