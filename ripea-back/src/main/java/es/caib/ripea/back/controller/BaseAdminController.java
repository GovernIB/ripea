package es.caib.ripea.back.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.ripea.back.helper.EntitatHelper;
import es.caib.ripea.back.helper.RolHelper;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.MetaExpedientDto;
import es.caib.ripea.service.intf.dto.OrganGestorDto;
import es.caib.ripea.service.intf.service.MetaExpedientService;
import es.caib.ripea.service.intf.service.OrganGestorService;

/**
 * Controlador base que implementa funcionalitats comunes per als controladors
 * de les accions de l'administrador.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseAdminController extends BaseController {

	@Autowired public MetaExpedientService metaExpedientService;
	@Autowired public OrganGestorService organGestorService;
	
	public EntitatDto getEntitatActualComprovantPermisAdminEntitat(HttpServletRequest request) {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat == null)
			throw new SecurityException("No te cap entitat assignada");
		if (!entitat.isUsuariActualAdministration())
			throw new SecurityException("No te permisos per accedir a aquesta entitat com a administrador");
		return entitat;
	}

	public EntitatDto getEntitatActualComprovantPermisAdminEntitatOrganOrDissenyador(HttpServletRequest request) {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat == null) {
			throw new SecurityException("No te cap entitat assignada");
		}

		if (!entitat.isUsuariActualAdministration() && !entitat.isUsuariActualTeOrgans() && !RolHelper.isRolActualDissenyadorOrgan(request)) {
			throw new SecurityException("No te permisos per accedir a aquesta entitat com a administrador");
		}
		return entitat;
	}

	public EntitatDto getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(
			HttpServletRequest request) {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat == null) {
			throw new SecurityException("No te cap entitat assignada");
		}
		if (!entitat.isUsuariActualAdministration() &&
			!entitat.isUsuariActualTeOrgans() &&
			!RolHelper.isRolActualRevisor(request) && 
			!RolHelper.isRolActualDissenyadorOrgan(request)) {
			throw new SecurityException("No te permisos per accedir a aquesta entitat com a administrador");
		}
		return entitat;
	}
	
	public EntitatDto getEntitatActualComprovantPermisAdminUserEntitatOrganOrRevisor(
			HttpServletRequest request) {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat == null) {
			throw new SecurityException("No te cap entitat assignada");
		}
		if (!entitat.isUsuariActualAdministration() && !entitat.isUsuariActualTeOrgans() && !RolHelper.isRolActualRevisor(request) && !entitat.isUsuariActualRead()) {
			throw new SecurityException("No te permisos per accedir a aquesta entitat");
		}
		return entitat;
	}

	public EntitatDto getEntitatActualComprovantPermisos(
			HttpServletRequest request) {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat == null) {
			throw new SecurityException("No te cap entitat assignada");
		}
		
		if (!entitat.isUsuariActualRead() && !entitat.isUsuariActualAdministration() && !entitat.isUsuariActualTeOrgans()) {
			throw new SecurityException("No te permisos per accedir a aquesta entitat com a usuari o administrador o administrator de l'organ");
		}
		return entitat;
	}
	
	protected boolean hasPermisAdmComu(HttpServletRequest request) {
		boolean hasPermisAdmComu = RolHelper.isRolActualAdministrador(request);
		if (RolHelper.isRolAmbFiltreOrgan(request)) {
			OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
			if (organActual!=null)
				hasPermisAdmComu = organGestorService.hasPermisAdminComu(organActual.getId());
		}
		return hasPermisAdmComu;
	}
	
	protected MetaExpedientDto comprovarAccesMetaExpedient(HttpServletRequest request, Long metaExpedientId) {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		
		MetaExpedientDto metaExpedient = null;
		if (RolHelper.isRolActualRevisor(request)) {
			metaExpedient = metaExpedientService.findById(entitat.getId(), metaExpedientId);
		} else if (RolHelper.isRolAmbFiltreOrgan(request)) {
			OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
			metaExpedient = metaExpedientService.getAndCheckOrganPermission(
					entitat.getId(),
					metaExpedientId,
					organActual,
					RolHelper.isRolActualDissenyadorOrgan(request));
		} else {
			OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
			metaExpedient = metaExpedientService.getAndCheckAdminPermission(
					entitat.getId(),
					metaExpedientId,
					organActual != null ? organActual.getId() : null);
		}
		return metaExpedient;
	}
}
