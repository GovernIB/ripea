package es.caib.ripea.ejb;

import java.util.List;
import java.util.Locale;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.ArbreDto;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.OrganGestorDto;
import es.caib.ripea.service.intf.dto.OrganGestorFiltreDto;
import es.caib.ripea.service.intf.dto.OrganismeDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.PermisDto;
import es.caib.ripea.service.intf.dto.PermisOrganGestorDto;
import es.caib.ripea.service.intf.dto.PrediccioSincronitzacio;
import es.caib.ripea.service.intf.dto.ProgresActualitzacioDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.OrganGestorService;
import lombok.experimental.Delegate;

@Stateless
public class OrganGestorServiceEjb extends AbstractServiceEjb<OrganGestorService> implements OrganGestorService {

	@Delegate private OrganGestorService delegateService;

	protected void setDelegateService(OrganGestorService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("**")
	public List<OrganGestorDto> findAll() {
		return delegateService.findAll();
	}

	@Override
	@RolesAllowed("**")
	public OrganGestorDto findItem(Long id) {
		return delegateService.findItem(id);
	}

	@Override
	@RolesAllowed("**")
	public List<OrganGestorDto> findByEntitat(Long entitatId) {
		return delegateService.findByEntitat(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public List<OrganGestorDto> findByEntitat(Long entitatId, String filterText) {
		return delegateService.findByEntitat(entitatId, filterText);
	}
	
	@Override
	@RolesAllowed("IPA_ADMIN")
	public PaginaDto<OrganGestorDto> findAmbFiltrePaginat(
			Long entitatId,
			OrganGestorFiltreDto filtre, PaginacioParamsDto paginacioParams) {
		return delegateService.findAmbFiltrePaginat(entitatId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public List<OrganismeDto> findPermesosByEntitatAndExpedientTipusIdAndFiltre(
			Long entitatId,
			Long metaExpedientId,
			String filter, 
			Long expedientId,
			String rolActual, Long organActualId) {
		return delegateService.findPermesosByEntitatAndExpedientTipusIdAndFiltre(
				entitatId,
				metaExpedientId,
				filter, 
				expedientId,
				rolActual, organActualId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public Object[] syncDir3OrgansGestors(EntitatDto entitat, Locale locale) throws Exception {
		return delegateService.syncDir3OrgansGestors(entitat, locale);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public PrediccioSincronitzacio predictSyncDir3OrgansGestors(Long entitatId) throws Exception {
		return delegateService.predictSyncDir3OrgansGestors(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public ProgresActualitzacioDto getProgresActualitzacio(String entitatCodi) {
		return delegateService.getProgresActualitzacio(entitatCodi);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public List<PermisOrganGestorDto> findPermisos(Long entitatId) throws NotFoundException {
		return delegateService.findPermisos(entitatId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public List<PermisOrganGestorDto> findPermisos(Long entitatId, Long organId) throws NotFoundException {
		return delegateService.findPermisos(entitatId, organId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public void updatePermis(Long id, PermisDto permis, Long entitatId) throws NotFoundException {
		delegateService.updatePermis(id, permis, entitatId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public void deletePermis(Long id, Long permisId, Long entitatId) throws NotFoundException {
		delegateService.deletePermis(id, permisId, entitatId);
	}

	@Override
	@RolesAllowed("**")
	public List<OrganGestorDto> findAccessiblesUsuariActualRolAdminOrDisseny(Long entitatId, Long organGestorId) {
		return delegateService.findAccessiblesUsuariActualRolAdminOrDisseny(entitatId, organGestorId);
	}
	
	@Override
	@RolesAllowed("**")
	public List<OrganGestorDto> findAccessiblesUsuariActualRolAdminOrDisseny(Long entitatId, Long organGestorId, String filterText) {
		return delegateService.findAccessiblesUsuariActualRolAdminOrDisseny(entitatId, organGestorId, filterText);
	}
	
	@Override
	@RolesAllowed("**")
	public List<OrganGestorDto> findOrganismesEntitatAmbPermis(Long entitatId) {
		return delegateService.findOrganismesEntitatAmbPermis(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public List<OrganGestorDto> findAccessiblesUsuariActualRolUsuari(
			Long entitatId,
			String filter,
			boolean directOrganPermisRequired) {
		return delegateService.findAccessiblesUsuariActualRolUsuari(
				entitatId,
				filter,
				directOrganPermisRequired);
	}

	@Override
	@RolesAllowed("**")
	public OrganGestorDto findItemByEntitatAndCodi(
			Long entitatId,
			String codi) {
		return delegateService.findItemByEntitatAndCodi(
				entitatId, 
				codi);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_ORGAN_ADMIN"})
	public OrganGestorDto findById(
			Long entitatId,
			Long id) {
		return delegateService.findById(
				entitatId,
				id);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public OrganGestorDto create(
			Long entitatId,
			OrganGestorDto organGestorDto) {
		return delegateService.create(
				entitatId,
				organGestorDto);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public OrganGestorDto update(
			Long entitatId,
			OrganGestorDto organGestorDto) {
		return delegateService.update(
				entitatId,
				organGestorDto);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public String delete(
			Long entitatId,
			Long id) {
		 return delegateService.delete(
				 entitatId, 
				 id);
	}

	@Override
	@RolesAllowed("**")
	public boolean hasPermisAdminComu(Long organId) {
		return delegateService.hasPermisAdminComu(organId);
	}

	@Override
	@RolesAllowed("**")
	public void evictOrganismesEntitatAmbPermis(
			Long entitatId,
			String usuariCodi) {
		delegateService.evictOrganismesEntitatAmbPermis(entitatId, usuariCodi);
		
	}

	@Override
	@RolesAllowed("**")
	public List<OrganGestorDto> findOrganismesEntitatAmbPermisCache(
			Long entitatId) {
		return delegateService.findOrganismesEntitatAmbPermisCache(
				entitatId);
	}

	@Override
	@RolesAllowed("**")
	public List<OrganGestorDto> findOrgansSuperiorByEntitat(Long entitatId) {
		return delegateService.findOrgansSuperiorByEntitat(entitatId);
	}
	
	@Override
	@RolesAllowed("IPA_ADMIN")
	public ArbreDto<OrganGestorDto> findOrgansArbreAmbFiltre(
			Long entitatId,
			OrganGestorFiltreDto filtre) {
		return delegateService.findOrgansArbreAmbFiltre(
				entitatId,
				filtre);
	}

	@Override
	@RolesAllowed("**")
	public List<OrganGestorDto> findAll(Long entitatId, String filter) {
		return delegateService.findAll(entitatId, filter);
	}

	@Override
	@RolesAllowed("**")
	public String getOrganCodiFromContingutId(
			Long contingutId) {
		return delegateService.getOrganCodiFromContingutId(contingutId);
	}

	@Override
	@RolesAllowed("**")
	public void actualitzarOrganCodi(
			String organCodi) {
		delegateService.actualitzarOrganCodi(organCodi);
		
	}

	@Override
	@RolesAllowed("**")
	public String getOrganCodiFromAnnexId(
			Long annexId) {
		return delegateService.getOrganCodiFromAnnexId(annexId);
	}

	@Override
	@RolesAllowed("**")
	public String getOrganCodi() {
		return delegateService.getOrganCodi();
	}

	@Override
	@RolesAllowed("**")
	public String getOrganCodiFromMetaDocumentId(Long metaDocumentId) {
		return delegateService.getOrganCodiFromMetaDocumentId(metaDocumentId);
	}

	@Override
	@RolesAllowed("**")
	public String getOrganCodiFromMetaExpedientId(Long metaExpedientId) {
		return delegateService.getOrganCodiFromMetaExpedientId(metaExpedientId);
	}

	@Override
	@RolesAllowed("**")
	public List<OrganGestorDto> findOrganismesEntitatAmbPermisDissenyCache(Long entitatId) {
		return delegateService.findOrganismesEntitatAmbPermisDissenyCache(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public Boolean isPermisEnviamentPostalOrganOrAntecesor(Long organGestorId) {
		return delegateService.isPermisEnviamentPostalOrganOrAntecesor(organGestorId);
	}
}