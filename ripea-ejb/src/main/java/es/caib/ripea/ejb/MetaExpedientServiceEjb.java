/**
 * 
 */
package es.caib.ripea.ejb;

import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.MetaExpedientService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Locale;

/**
 * Implementació de MetaExpedientService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class MetaExpedientServiceEjb implements MetaExpedientService {

	@Delegate
	private MetaExpedientService delegateService;

	protected void setDelegateService(MetaExpedientService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_DISSENY", "IPA_ORGAN_ADMIN"})
	public MetaExpedientDto create(
			Long entitatId,
			MetaExpedientDto metaExpedient, String rolActual, Long organId) {
		return delegateService.create(entitatId, metaExpedient, rolActual, organId);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_DISSENY", "IPA_ORGAN_ADMIN"})
	public MetaExpedientDto update(
			Long entitatId,
			MetaExpedientDto metaExpedient, String rolActual,
			MetaExpedientRevisioEstatEnumDto isCanviEstatDissenyAPendentByOrganAdmin, Long organId) {
		return delegateService.update(entitatId, metaExpedient, rolActual, isCanviEstatDissenyAPendentByOrganAdmin, organId);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_DISSENY", "IPA_ORGAN_ADMIN"})
	public MetaExpedientDto updateActiu(
			Long entitatId,
			Long id,
			boolean actiu, String rolActual, Long organId) {
		return delegateService.updateActiu(entitatId, id, actiu, rolActual, organId);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_DISSENY", "IPA_ORGAN_ADMIN"})
	public MetaExpedientDto delete(
			Long entitatId,
			Long metaExpedientId, Long organId) {
		return delegateService.delete(entitatId, metaExpedientId, organId);
	}

	@Override
	@RolesAllowed({"tothom"})
	public MetaExpedientDto findById(
			Long entitatId,
			Long id) {
		return delegateService.findById(
				entitatId,
				id);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_DISSENY", "IPA_ORGAN_ADMIN"})
	public MetaExpedientDto findByEntitatCodi(
			Long entitatId,
			String codi) {
		return delegateService.findByEntitatCodi(entitatId, codi);
	}

	@Override
	@RolesAllowed({"tothom"})
	public List<MetaExpedientDto> findByEntitat(
			Long entitatId) {
		return delegateService.findByEntitat(entitatId);
	}

	@Override
	@RolesAllowed({"tothom"})
	public List<MetaExpedientDto> findActiusAmbEntitatPerCreacio(
			Long entitatId, String rolActual) {
		return delegateService.findActiusAmbEntitatPerCreacio(entitatId, rolActual);
	}
	
	@Override
	@RolesAllowed({"tothom"})
	public List<MetaExpedientDto> findActiusAmbEntitatPerModificacio(
			Long entitatId, String rolActual) {
		return delegateService.findActiusAmbEntitatPerModificacio(entitatId, rolActual);
	}	

	@Override
	@RolesAllowed({"tothom"})
	public List<MetaExpedientDto> findActius(
			Long entitatId, String filtreNomOrCodiSia, String rolActual, boolean comu, Long organId) {
		return delegateService.findActius(entitatId, filtreNomOrCodiSia, rolActual, comu, organId);
	}

	@Override
	@RolesAllowed({"tothom"})
	public long getProximNumeroSequencia(
			Long entitatId,
			Long id,
			int any) throws NotFoundException {
		return delegateService.getProximNumeroSequencia(
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
		return delegateService.tascaCreate(
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
		return delegateService.tascaUpdate(
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
		return delegateService.tascaUpdateActiu(
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
		return delegateService.tascaDelete(
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
		return delegateService.tascaFindById(
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
		return delegateService.tascaFindPaginatByMetaExpedient(
				entitatId,
				metaExpedientId,
				paginacioParams);
	}

	@Override
	@RolesAllowed({"tothom"})
	public List<PermisDto> permisFind(
			Long entitatId,
			Long id) {
		return delegateService.permisFind(
				entitatId,
				id);
	}

	@Override
	@RolesAllowed({"tothom"})
	public void permisUpdate(
			Long entitatId,
			Long id,
			PermisDto permis, String rolActual, Long organId) {
		delegateService.permisUpdate(
				entitatId,
				id,
				permis, rolActual, organId);
	}

	@Override
	@RolesAllowed({"tothom"})
	public void permisDelete(Long entitatId, Long id, Long permisId, Long organGestorId, String rolActual, Long organId) {
		delegateService.permisDelete(
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
			boolean isRolFiltreoOrgan,
			PaginacioParamsDto paginacioParams,
			String rolActual,
			boolean hasPermisAdmComu) {
	    return delegateService.findByEntitatOrOrganGestor(
	    		entitatId,
	    		organGestorId,
	    		filtre,
	    		isRolFiltreoOrgan, 
	    		paginacioParams,
				rolActual,
				hasPermisAdmComu);
	}
	
	@Override
	@RolesAllowed({"tothom"})
	public MetaExpedientDto getAndCheckAdminPermission(
			Long entitatId,
			Long id, Long organId) {
		return delegateService.getAndCheckAdminPermission(
				entitatId,
				id, 
				organId);
	}

	@Override
	@RolesAllowed({"tothom"})
	public List<GrupDto> findGrupsAmbMetaExpedient(
			Long entitatId,
			Long metaExpedientId, 
			String rolActual) {
		return delegateService.findGrupsAmbMetaExpedient(
				entitatId, 
				metaExpedientId, 
				rolActual);
	}

	@Override
	@RolesAllowed({"tothom"})
	public List<MetaExpedientDto> findActiusAmbOrganGestorPermisLectura(
			Long entitatId,
			Long organGestorId, String filtre) {
		return delegateService.findActiusAmbOrganGestorPermisLectura(
				entitatId,
				organGestorId, filtre);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_DISSENY", "IPA_ORGAN_ADMIN"})
	public List<ArbreDto<MetaExpedientCarpetaDto>> findArbreCarpetesMetaExpedient(Long entitatId,
			Long metaExpedientId) {
		return delegateService.findArbreCarpetesMetaExpedient(entitatId, metaExpedientId);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_DISSENY", "IPA_ORGAN_ADMIN"})
	public MetaExpedientCarpetaDto deleteCarpetaMetaExpedient(Long entitatId, Long metaExpedientCarpetaId) {
		return delegateService.deleteCarpetaMetaExpedient(entitatId, metaExpedientCarpetaId);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_DISSENY", "IPA_ORGAN_ADMIN"})
	public ProcedimentDto findProcedimentByCodiSia(
			Long entitatId,
			String codiDir3, String codiSia) {
		return delegateService.findProcedimentByCodiSia(
				entitatId, 
				codiDir3, codiSia);
	}

	@Override
	@RolesAllowed({"tothom"})
	public List<MetaExpedientDto> findByClassificacio(
			Long entitatId,
			String codiSia) {
		return delegateService.findByClassificacio(
				entitatId, 
				codiSia);
	}

	@Override
	@RolesAllowed({"tothom"})
	public MetaExpedientDto canviarEstatRevisioASellecionat(
			Long entitatId,
			MetaExpedientDto metaExpedient, 
			String rolActual) {
		return delegateService.canviarEstatRevisioASellecionat(
				entitatId, 
				metaExpedient, 
				rolActual);
	}


	@Override
	@RolesAllowed({"tothom"})
	public int countMetaExpedientsPendentRevisar(Long entitatId) {
		return delegateService.countMetaExpedientsPendentRevisar(entitatId);
	}

	@Override
	@RolesAllowed({"tothom"})
	public boolean isMetaExpedientPendentRevisio(
			Long entitatId,
			Long id) {
		return delegateService.isMetaExpedientPendentRevisio(entitatId, id);
	}

	@Override
	@RolesAllowed({"tothom"})
	public boolean comprovarPermisosMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			PermissionEnumDto permission) {
		return delegateService.comprovarPermisosMetaExpedient(
				entitatId,
				metaExpedientId,
				permission);
	}

	@Override
	@RolesAllowed({"tothom"})
	public List<MetaExpedientDto> findCreateWritePerm(
			Long entitatId,
			String rolActual) {
		return delegateService.findCreateWritePerm(entitatId, rolActual);
	}

	@Override
	@RolesAllowed({"tothom"})
	public boolean isRevisioActiva() {
		return delegateService.isRevisioActiva();
	}

	@Override
	@RolesAllowed({"tothom"})
	public List<MetaExpedientDto> findActiusAmbEntitatPerConsultaEstadistiques(
			Long entitatId,
			String filtreNomOrCodiSia, 
			String rolActual) {
		return delegateService.findActiusAmbEntitatPerConsultaEstadistiques(entitatId, filtreNomOrCodiSia, rolActual);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_DISSENY", "IPA_ORGAN_ADMIN"})
	public MetaExpedientDto marcarPendentRevisio(Long entitatId, Long id, Long organId) {
		return delegateService.marcarPendentRevisio(entitatId, id, organId);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_DISSENY", "IPA_ORGAN_ADMIN"})
	public MetaExpedientDto marcarProcesDisseny(Long entitatId, Long id, Long organId) {
		return delegateService.marcarProcesDisseny(entitatId, id, organId);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_DISSENY", "IPA_ORGAN_ADMIN", "IPA_REVISIO"})
	public boolean publicarComentariPerMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			String text,
			String rolActual) {
		return delegateService.publicarComentariPerMetaExpedient(
				entitatId,
				metaExpedientId,
				text,
				rolActual);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_DISSENY", "IPA_ORGAN_ADMIN", "IPA_REVISIO"})
	public List<MetaExpedientComentariDto> findComentarisPerMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			String rolActual) {
		return delegateService.findComentarisPerMetaExpedient(
				entitatId,
				metaExpedientId,
				rolActual);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_DISSENY", "IPA_ORGAN_ADMIN", "IPA_REVISIO"})
	public String export(
			Long entitatId,
			Long id,
			Long organActualId) {
		return delegateService.export(
				entitatId,
				id,
				organActualId);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_DISSENY", "IPA_ORGAN_ADMIN", "IPA_REVISIO"})
	public void createFromImport(
			Long entitatId,
			MetaExpedientExportDto metaExpedient,
			String rolActual,
			Long organId) {
		delegateService.createFromImport(
				entitatId,
				metaExpedient,
				rolActual,
				organId);
	}
	
	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_DISSENY", "IPA_ORGAN_ADMIN", "IPA_REVISIO"})
	public void updateFromImport(
			Long entitatId,
			MetaExpedientExportDto metaExpedient,
			String rolActual,
			Long organId) {
		delegateService.updateFromImport(
				entitatId,
				metaExpedient,
				rolActual,
				organId);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_DISSENY", "IPA_ORGAN_ADMIN", "IPA_REVISIO"})
	public CrearReglaResponseDto reintentarCreacioReglaDistribucio(
			Long entitatId,
			Long metaExpedientId) {
		return delegateService.reintentarCreacioReglaDistribucio(
				entitatId,
				metaExpedientId);
	}

    @Override
    @RolesAllowed({"IPA_ADMIN", "IPA_DISSENY", "IPA_ORGAN_ADMIN"})
    public boolean isUpdatingProcediments(EntitatDto entitatDto) {
        return delegateService.isUpdatingProcediments(entitatDto);
    }

    @Override
	@RolesAllowed({"IPA_ADMIN", "IPA_DISSENY", "IPA_ORGAN_ADMIN"})
    public ProgresActualitzacioDto getProgresActualitzacio(String codi) {
        return delegateService.getProgresActualitzacio(codi);
    }

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_DISSENY", "IPA_ORGAN_ADMIN"})
	public void actualitzaProcediments(
			EntitatDto entitat,
			OrganGestorDto organActual,
			boolean isRolFiltreOrgan,
			boolean hasPermisAdmComu,
			Locale locale) {
		delegateService.actualitzaProcediments(entitat, organActual, isRolFiltreOrgan, hasPermisAdmComu, locale);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_DISSENY", "IPA_ORGAN_ADMIN"})
	public ReglaDistribucioDto consultarReglaDistribucio(
			Long metaExpedientId) {
		return delegateService.consultarReglaDistribucio(metaExpedientId);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_DISSENY", "IPA_ORGAN_ADMIN"})
	public CrearReglaResponseDto canviarEstatReglaDistribucio(
			Long metaExpedientId, 
			boolean activa) {
		return delegateService.canviarEstatReglaDistribucio(
				metaExpedientId, 
				activa);
	}

	@Override
	@RolesAllowed({"tothom"})
	public MetaExpedientDto findByIdAmbElements(
			Long entitatId,
			Long id, 
			Long adminOrganId) {
		return delegateService.findByIdAmbElements(
				entitatId,
				id,
				adminOrganId);
	}

	@Override
	@RolesAllowed("tothom")
	public boolean hasPermissionForAnyProcediment(Long entitatId, String rolActual, PermissionEnumDto permis) {
		return delegateService.hasPermissionForAnyProcediment(entitatId, rolActual, permis);
	}

	@Override
	@RolesAllowed("tothom")
	public List<MetaExpedientTascaValidacioDto> findValidacionsTasca(Long metaExpedientTascaId) {
		return delegateService.findValidacionsTasca(metaExpedientTascaId);
	}

	@Override
	@RolesAllowed("tothom")
	public boolean createValidacioTasca(MetaExpedientTascaValidacioDto metaExpedientTascaValidacioDto) {
		return delegateService.createValidacioTasca(metaExpedientTascaValidacioDto);
	}

	@Override
	@RolesAllowed("tothom")
	public MetaExpedientTascaValidacioDto updateValidacioTasca(Long metaExpedientTascaValidacioId, String accio) {
		return delegateService.updateValidacioTasca(metaExpedientTascaValidacioId, accio);
	}

	@Override
	@RolesAllowed("tothom")
	public int createValidacionsTasca(
			Long entitatId,
			Long tascaID,
			List<MetaExpedientTascaValidacioDto> validacions) {
		return delegateService.createValidacionsTasca(entitatId, tascaID, validacions);
	}

	@Override
	@RolesAllowed("tothom")
	public MetaExpedientDto getAndCheckOrganPermission(
			Long entitatId,
			Long id,
			OrganGestorDto organActual,
			boolean checkAdmin) {
		return delegateService.getAndCheckOrganPermission(entitatId, id, organActual, checkAdmin);
	}
}