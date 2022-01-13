/**
 * 
 */
package es.caib.ripea.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.dto.ArbreDto;
import es.caib.ripea.core.api.dto.GrupDto;
import es.caib.ripea.core.api.dto.MetaExpedientCarpetaDto;
import es.caib.ripea.core.api.dto.MetaExpedientComentariDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaExpedientFiltreDto;
import es.caib.ripea.core.api.dto.MetaExpedientTascaDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.dto.PermissionEnumDto;
import es.caib.ripea.core.api.dto.ProcedimentDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.MetaExpedientService;

/**
 * Implementació de MetaExpedientService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class MetaExpedientServiceBean implements MetaExpedientService {

	@Autowired
	MetaExpedientService delegate;

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_ORGAN_ADMIN"})
	public MetaExpedientDto create(
			Long entitatId,
			MetaExpedientDto metaExpedient, String rolActual, Long organId) {
		return delegate.create(entitatId, metaExpedient, rolActual, organId);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_ORGAN_ADMIN"})
	public MetaExpedientDto update(
			Long entitatId,
			MetaExpedientDto metaExpedient, String rolActual,
			boolean isCanviEstatDissenyAPendentByOrganAdmin, Long organId) {
		return delegate.update(entitatId, metaExpedient, rolActual, isCanviEstatDissenyAPendentByOrganAdmin, organId);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_ORGAN_ADMIN"})
	public MetaExpedientDto updateActiu(
			Long entitatId,
			Long id,
			boolean actiu, String rolActual, Long organId) {
		return delegate.updateActiu(entitatId, id, actiu, rolActual, organId);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_ORGAN_ADMIN"})
	public MetaExpedientDto delete(
			Long entitatId,
			Long metaExpedientId, Long organId) {
		return delegate.delete(entitatId, metaExpedientId, organId);
	}

	@Override
	@RolesAllowed({"tothom"})
	public MetaExpedientDto findById(
			Long entitatId,
			Long id) {
		return delegate.findById(
				entitatId,
				id);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_ORGAN_ADMIN"})
	public MetaExpedientDto findByEntitatCodi(
			Long entitatId,
			String codi) {
		return delegate.findByEntitatCodi(entitatId, codi);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_SUPER"})
	public List<MetaExpedientDto> findByEntitat(
			Long entitatId) {
		return delegate.findByEntitat(entitatId);
	}

	@Override
	@RolesAllowed({"tothom"})
	public List<MetaExpedientDto> findActiusAmbEntitatPerCreacio(
			Long entitatId, String rolActual) {
		return delegate.findActiusAmbEntitatPerCreacio(entitatId, rolActual);
	}
	
	@Override
	@RolesAllowed({"tothom"})
	public List<MetaExpedientDto> findActiusAmbEntitatPerModificacio(
			Long entitatId, String rolActual) {
		return delegate.findActiusAmbEntitatPerModificacio(entitatId, rolActual);
	}	

	@Override
	@RolesAllowed({"tothom"})
	public List<MetaExpedientDto> findActius(
			Long entitatId, String filtreNomOrCodiSia, String rolActual, boolean comu, Long organId) {
		return delegate.findActius(entitatId, filtreNomOrCodiSia, rolActual, comu, organId);
	}

	@Override
	@RolesAllowed({"tothom"})
	public long getProximNumeroSequencia(
			Long entitatId,
			Long id,
			int any) throws NotFoundException {
		return delegate.getProximNumeroSequencia(
				entitatId,
				id,
				any);
	}

	@Override
	@RolesAllowed({"tothom"})
	public MetaExpedientTascaDto tascaCreate(
			Long entitatId,
			Long metaExpedientId,
			MetaExpedientTascaDto metaExpedientTasca, String rolActual, Long organId) throws NotFoundException {
		return delegate.tascaCreate(
				entitatId,
				metaExpedientId,
				metaExpedientTasca, rolActual, organId);
	}

	@Override
	@RolesAllowed({"tothom"})
	public MetaExpedientTascaDto tascaUpdate(
			Long entitatId,
			Long metaExpedientId,
			MetaExpedientTascaDto metaExpedientTasca, String rolActual, Long organId) throws NotFoundException {
		return delegate.tascaUpdate(
				entitatId,
				metaExpedientId,
				metaExpedientTasca, rolActual, organId);
	}

	@Override
	@RolesAllowed({"tothom"})
	public MetaExpedientTascaDto tascaUpdateActiu(
			Long entitatId,
			Long metaExpedientId,
			Long id,
			boolean activa, String rolActual, Long organId) throws NotFoundException {
		return delegate.tascaUpdateActiu(
				entitatId,
				metaExpedientId,
				id,
				activa, rolActual, organId);
	}

	@Override
	@RolesAllowed({"tothom"})
	public MetaExpedientTascaDto tascaDelete(
			Long entitatId,
			Long metaExpedientId,
			Long id, String rolActual, Long organId) throws NotFoundException {
		return delegate.tascaDelete(
				entitatId,
				metaExpedientId,
				id, rolActual, organId);
	}

	@Override
	@RolesAllowed({"tothom"})
	public MetaExpedientTascaDto tascaFindById(
			Long entitatId,
			Long metaExpedientId,
			Long id) throws NotFoundException {
		return delegate.tascaFindById(
				entitatId,
				metaExpedientId,
				id);
	}

	@Override
	@RolesAllowed({"tothom"})
	public PaginaDto<MetaExpedientTascaDto> tascaFindPaginatByMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			PaginacioParamsDto paginacioParams) throws NotFoundException {
		return delegate.tascaFindPaginatByMetaExpedient(
				entitatId,
				metaExpedientId,
				paginacioParams);
	}

	@Override
	@RolesAllowed({"tothom"})
	public List<PermisDto> permisFind(
			Long entitatId,
			Long id) {
		return delegate.permisFind(
				entitatId,
				id);
	}

	@Override
	@RolesAllowed({"tothom"})
	public void permisUpdate(
			Long entitatId,
			Long id,
			PermisDto permis, String rolActual, Long organId) {
		delegate.permisUpdate(
				entitatId,
				id,
				permis, rolActual, organId);
	}

	@Override
	@RolesAllowed({"tothom"})
	public void permisDelete(Long entitatId, Long id, Long permisId, Long organGestorId, String rolActual, Long organId) {
		delegate.permisDelete(
				entitatId,
				id,
				permisId,
				organGestorId, rolActual, organId);
	}

	@Override
	@RolesAllowed({"tothom"})
	public PaginaDto<MetaExpedientDto> findByEntitatOrOrganGestor(
			Long entitatId,
			Long organGestorId,
			MetaExpedientFiltreDto filtre,
			boolean isRolActualAdministradorOrgan,
			PaginacioParamsDto paginacioParams,
			String rolActual,
			boolean hasPermisAdmComu) {

	    return delegate.findByEntitatOrOrganGestor(
	    		entitatId,
	    		organGestorId,
	    		filtre,
	    		isRolActualAdministradorOrgan, 
	    		paginacioParams,
				rolActual,
				hasPermisAdmComu);

	}
	
	@Override
	@RolesAllowed({"tothom"})
	public MetaExpedientDto getAndCheckAdminPermission(
			Long entitatId,
			Long id, Long organId) {
		return delegate.getAndCheckAdminPermission(
				entitatId,
				id, null);
	}

	@Override
	@RolesAllowed({"tothom"})
	public List<GrupDto> findGrupsAmbMetaExpedient(
			Long entitatId,
			Long metaExpedientId) {
		return delegate.findGrupsAmbMetaExpedient(
				entitatId, 
				metaExpedientId);
	}

	@Override
	@RolesAllowed({"tothom"})
	public List<MetaExpedientDto> findActiusAmbOrganGestorPermisLectura(
			Long entitatId,
			Long organGestorId, String filtre) {
		return delegate.findActiusAmbOrganGestorPermisLectura(
				entitatId,
				organGestorId, filtre);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_ORGAN_ADMIN"})
	public List<ArbreDto<MetaExpedientCarpetaDto>> findArbreCarpetesMetaExpedient(Long entitatId,
			Long metaExpedientId) {
		return delegate.findArbreCarpetesMetaExpedient(entitatId, metaExpedientId);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_ORGAN_ADMIN"})
	public MetaExpedientCarpetaDto deleteCarpetaMetaExpedient(Long entitatId, Long metaExpedientCarpetaId) {
		return delegate.deleteCarpetaMetaExpedient(entitatId, metaExpedientCarpetaId);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_ORGAN_ADMIN"})
	public ProcedimentDto findProcedimentByCodiSia(
			Long entitatId,
			String codiDir3, String codiSia) {
		return delegate.findProcedimentByCodiSia(
				entitatId, 
				codiDir3, codiSia);
	}

	@Override
	@RolesAllowed({"tothom"})
	public List<MetaExpedientDto> findByCodiSia(
			Long entitatId,
			String codiSia) {
		return delegate.findByCodiSia(
				entitatId, 
				codiSia);
	}

	@Override
	@RolesAllowed({"tothom"})
	public MetaExpedientDto canviarEstatRevisioASellecionat(
			Long entitatId,
			MetaExpedientDto metaExpedient) {
		return delegate.canviarEstatRevisioASellecionat(
				entitatId, 
				metaExpedient);
	}


	@Override
	@RolesAllowed({"tothom"})
	public int countMetaExpedientsPendentRevisar(Long entitatId) {
		return delegate.countMetaExpedientsPendentRevisar(entitatId);
	}

	@Override
	@RolesAllowed({"tothom"})
	public boolean isMetaExpedientPendentRevisio(
			Long entitatId,
			Long id) {
		return delegate.isMetaExpedientPendentRevisio(entitatId, id);
	}

	@Override
	@RolesAllowed({"tothom"})
	public boolean comprovarPermisosMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			PermissionEnumDto permission) {
		return delegate.comprovarPermisosMetaExpedient(
				entitatId,
				metaExpedientId,
				permission);
	}

	@Override
	@RolesAllowed({"tothom"})
	public List<MetaExpedientDto> findCreateWritePerm(
			Long entitatId,
			boolean isAdmin) {
		return delegate.findCreateWritePerm(entitatId, isAdmin);
	}

	@Override
	@RolesAllowed({"tothom"})
	public boolean isRevisioActiva() {
		return delegate.isRevisioActiva();
	}

	@Override
	@RolesAllowed({"tothom"})
	public List<MetaExpedientDto> findActiusAmbEntitatPerConsultaEstadistiques(
			Long entitatId,
			String filtreNomOrCodiSia, 
			String rolActual) {
		return delegate.findActiusAmbEntitatPerConsultaEstadistiques(entitatId, filtreNomOrCodiSia, rolActual);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_ORGAN_ADMIN"})
	public MetaExpedientDto marcarPendentRevisio(Long entitatId, Long id, Long organId) {
		return delegate.marcarPendentRevisio(entitatId, id, organId);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_ORGAN_ADMIN"})
	public MetaExpedientDto marcarProcesDisseny(Long entitatId, Long id) {
		return delegate.marcarProcesDisseny(entitatId, id);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_ORGAN_ADMIN", "IPA_REVISIO"})
	public boolean publicarComentariPerMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			String text,
			String rolActual) {
		return delegate.publicarComentariPerMetaExpedient(
				entitatId,
				metaExpedientId,
				text,
				rolActual);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_ORGAN_ADMIN", "IPA_REVISIO"})
	public List<MetaExpedientComentariDto> findComentarisPerMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			String rolActual) {
		return delegate.findComentarisPerMetaExpedient(
				entitatId,
				metaExpedientId,
				rolActual);
	}

}
