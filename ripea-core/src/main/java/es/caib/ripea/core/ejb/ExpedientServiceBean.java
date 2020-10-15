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

import es.caib.ripea.core.api.dto.ContingutMassiuFiltreDto;
import es.caib.ripea.core.api.dto.ExpedientComentariDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientFiltreDto;
import es.caib.ripea.core.api.dto.ExpedientSelectorDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
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
	private ExpedientService delegate;

	@Override
	@RolesAllowed("tothom")
	public ExpedientDto create(
			Long entitatId,
			Long contenidorId,
			Long metaExpedientId,
			Long metaExpedientDominiId,
			Integer any,
			Long sequencia,
			String nom,
			Long expedientPeticioId,
			boolean associarInteressats,
			Long grupId) {
		return delegate.create(
				entitatId,
				contenidorId,
				metaExpedientId,
				metaExpedientDominiId,
				any,
				sequencia,
				nom,
				expedientPeticioId,
				associarInteressats,
				grupId);
	}
	public ExpedientDto findByMetaExpedientAndPareAndNomAndEsborrat(
			Long entitatId,
			Long metaExpedientId,
			Long pareId,
			String nom,
			int esborrat) {
		return delegate.findByMetaExpedientAndPareAndNomAndEsborrat(entitatId,
																	metaExpedientId,
																	pareId,
																	nom,
																	esborrat);
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
			String motiu,
			Long[] documentsPerFirmar) {
		delegate.tancar(entitatId, id, motiu, documentsPerFirmar);
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
			Collection<Long> expedientIds,
			String format) throws IOException {
		return delegate.exportacio(
				entitatId,
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
	public List<ExpedientDto> findByEntitatAndMetaExpedient(
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
	public ExpedientDto update(Long entitatId, Long id, String nom, int any, Long metaExpedientDominiId) {
		return delegate.update(entitatId, id, nom, any, metaExpedientDominiId);
	}


	@Override
	public boolean retryCreateDocFromAnnex(Long registreAnnexId,
			Long expedientPeticioId) {
		return delegate.retryCreateDocFromAnnex(registreAnnexId, expedientPeticioId);		
	}

	@Override
	public boolean retryNotificarDistribucio(Long expedientPeticioId) {
		return delegate.retryNotificarDistribucio(expedientPeticioId);
	}

	@Override
	public boolean incorporar(Long entitatId,
			Long expedientId,
			Long expedientPeticioId,
			boolean associarInteressats) {
		return delegate.incorporar(entitatId, expedientId, expedientPeticioId,  associarInteressats);
		
	}

	@Override
	@RolesAllowed("tothom")
	public FitxerDto exportIndexExpedients(Long entitatId, Collection<Long> expedientIds) throws IOException {
		return delegate.exportIndexExpedients(
				entitatId, 
				expedientIds);
	}

	@Override
	@RolesAllowed("tothom")
	public FitxerDto exportIndexExpedient(Long entitatId, Long expedientId) throws IOException {
		return delegate.exportIndexExpedient(
				entitatId,
				expedientId);
	}
	@Override
	@RolesAllowed("tothom")
	public PaginaDto<ExpedientDto> findExpedientsPerTancamentMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams) throws NotFoundException {
		return delegate.findExpedientsPerTancamentMassiu(
				entitatId,
				filtre,
				paginacioParams);
	}
	@Override
	@RolesAllowed("tothom")
	public List<Long> findIdsExpedientsPerTancamentMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre) throws NotFoundException {
		return delegate.findIdsExpedientsPerTancamentMassiu(
				entitatId,
				filtre);
	}
}