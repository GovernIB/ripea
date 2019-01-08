/**
 * 
 */
package es.caib.ripea.core.ejb;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.ExpedientComentariDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientEstatDto;
import es.caib.ripea.core.api.dto.ExpedientFiltreDto;
import es.caib.ripea.core.api.dto.ExpedientSelectorDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.ExpedientService;

/**
 * Implementació de ContenidorService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ExpedientServiceBean implements ExpedientService {

	@Autowired
	ExpedientService delegate;

	@Override
	@RolesAllowed("tothom")
	public ExpedientDto create(
			Long entitatId,
			Long contenidorId,
			Long metaExpedientId,
			Integer any,
			String nom) {
		return delegate.create(
				entitatId,
				contenidorId,
				metaExpedientId,
				any,
				nom);
	}

	@Override
	@RolesAllowed("tothom")
	public ExpedientDto update(
			Long entitatId,
			Long id,
			String nom) {
		return delegate.update(
				entitatId,
				id,
				nom);
	}

	@Override
	@RolesAllowed("tothom")
	public ExpedientDto findById(
			Long entitatId,
			Long id) {
		return delegate.findById(entitatId, id);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public PaginaDto<ExpedientDto> findAmbFiltreAdmin(
			Long entitatId,
			ExpedientFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findAmbFiltreAdmin(entitatId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed("tothom")
	public PaginaDto<ExpedientDto> findAmbFiltreUser(
			Long entitatId,
			ExpedientFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findAmbFiltreUser(entitatId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed("tothom")
	public List<Long> findIdsAmbFiltre(
			Long entitatId,
			ExpedientFiltreDto filtre) throws NotFoundException {
		return delegate.findIdsAmbFiltre(entitatId, filtre);
	}

	@Override
	@RolesAllowed("tothom")
	public void agafarUser(
			Long entitatId,
			Long id) {
		delegate.agafarUser(entitatId, id);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public void agafarAdmin(
			Long entitatId,
			Long arxiuId,
			Long id,
			String usuariCodi) {
		delegate.agafarAdmin(entitatId, arxiuId, id, usuariCodi);
	}

	@Override
	@RolesAllowed("tothom")
	public void alliberarUser(
			Long entitatId,
			Long id) {
		delegate.alliberarUser(entitatId, id);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public void alliberarAdmin(
			Long entitatId,
			Long id) {
		delegate.alliberarAdmin(entitatId, id);
	}

	@Override
	@RolesAllowed("tothom")
	public void tancar(
			Long entitatId,
			Long id,
			String motiu) {
		delegate.tancar(entitatId, id, motiu);
	}

	@Override
	@RolesAllowed("tothom")
	public void reobrir(
			Long entitatId,
			Long id) throws NotFoundException {
		delegate.reobrir(entitatId, id);
	}

	@Override
	@RolesAllowed("tothom")
	public void relacioCreate(
			Long entitatId,
			Long id,
			Long relacionatId) {
		delegate.relacioCreate(
				entitatId,
				id,
				relacionatId);
	}

	@Override
	@RolesAllowed("tothom")
	public boolean relacioDelete(
			Long entitatId, 
			Long expedientId, 
			Long relacionatId) {
		return delegate.relacioDelete(
				entitatId,
				expedientId,
				relacionatId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ExpedientDto> relacioFindAmbExpedient(
			Long entitatId, 
			Long expedientId) {
		return delegate.relacioFindAmbExpedient(entitatId, expedientId);
	}

	@Override
	@RolesAllowed("tothom")
	public FitxerDto exportacio(
			Long entitatId,
			Long metaExpedientId,
			Collection<Long> expedientIds,
			String format) throws IOException {
		return delegate.exportacio(
				entitatId,
				metaExpedientId,
				expedientIds,
				format);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ExpedientSelectorDto> findPerUserAndTipus(Long entitatId, Long metaExpedientId) throws NotFoundException {
		return delegate.findPerUserAndTipus(entitatId, metaExpedientId);
	}

	@Override
	@RolesAllowed("tothom")
	public PaginaDto<ExpedientDto> findAmbFiltreNoRelacionat(
			Long entitatId,
			ExpedientFiltreDto filtre,
			Long expedientId,
			PaginacioParamsDto paginacioParams) {
		return delegate.findAmbFiltreNoRelacionat(
				entitatId,
				filtre,
				expedientId,
				paginacioParams);
	}

	@Override
	public List<ContingutDto> findByEntitatAndMetaExpedient(
			Long entitatId,
			Long metaExpedientId) {
		return delegate.findByEntitatAndMetaExpedient(
				entitatId,
				metaExpedientId);
	}

	@Override
	public boolean publicarComentariPerExpedient(Long entitatId, Long expedientId, String text) {
		return delegate.publicarComentariPerExpedient(
				entitatId,
				expedientId,
				text);
	}

	@Override
	public List<ExpedientComentariDto> findComentarisPerContingut(Long entitatId, Long expedientId) {
		return delegate.findComentarisPerContingut(
				entitatId,
				expedientId);
	}

	@Override
	public boolean hasWritePermission(Long expedientId) {
		return delegate.hasWritePermission(expedientId);
	}

	@Override
	public ExpedientDto update(Long entitatId, Long id, String nom, int any) {
		return delegate.update(entitatId, id, nom, any);
	}

	public PaginaDto<ExpedientEstatDto> findExpedientEstatByMetaExpedientPaginat(Long entitatId, Long metaExpedientId,
			PaginacioParamsDto paginacioParams) {
		return delegate.findExpedientEstatByMetaExpedientPaginat(entitatId, metaExpedientId, paginacioParams);
	}

	@Override
	public ExpedientEstatDto findExpedientEstatById(Long entitatId, Long id) {
		return delegate.findExpedientEstatById(entitatId, id);
	}

	@Override
	public ExpedientEstatDto createExpedientEstat(Long entitatId, ExpedientEstatDto estat) {
		return delegate.createExpedientEstat(entitatId, estat);
	}

	@Override
	public ExpedientEstatDto updateExpedientEstat(Long entitatId, ExpedientEstatDto estat) {
		return delegate.updateExpedientEstat(entitatId, estat);
	}

	@Override
	public ExpedientEstatDto moveTo(Long entitatId, Long metaExpedientId, Long expedientEstatId, int posicio)
			throws NotFoundException {
		return delegate.moveTo(entitatId, metaExpedientId, expedientEstatId, posicio);
	}

	@Override
	public ExpedientEstatDto deleteExpedientEstat(Long entitatId, Long expedientEstatId) throws NotFoundException {
		return delegate.deleteExpedientEstat(entitatId, expedientEstatId);
	}

	@Override
	public List<ExpedientEstatDto> findExpedientEstats(Long entitatId, Long expedientId) {
		return delegate.findExpedientEstats(entitatId, expedientId);
	}


	@Override
	public ExpedientDto changeEstatOfExpedient(Long entitatId, Long expedientId, Long expedientEstatId) {
		return delegate.changeEstatOfExpedient(entitatId, expedientId, expedientEstatId);
	}

	@Override
	public List<PermisDto> estatPermisFind(Long entitatId, Long estatId) {
		return delegate.estatPermisFind(entitatId, estatId);
	}

	@Override
	public void estatPermisUpdate(Long entitatId, Long estatId, PermisDto permis) {
		delegate.estatPermisUpdate(entitatId, estatId, permis);
	}

	@Override
	public void estatPermisDelete(Long entitatId, Long estatId, Long permisId) {
		delegate.estatPermisDelete(entitatId, estatId, permisId);
	}

	@Override
	public List<ExpedientEstatDto> findExpedientEstatByMetaExpedient(Long entitatId, Long metaExpedientId) {
		return delegate.findExpedientEstatByMetaExpedient(entitatId, metaExpedientId);
	}



}