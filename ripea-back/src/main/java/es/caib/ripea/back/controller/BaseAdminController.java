/**
 * 
 */
package es.caib.ripea.back.controller;

import es.caib.ripea.back.helper.EntitatHelper;
import es.caib.ripea.back.helper.RolHelper;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.MetaExpedientDto;
import es.caib.ripea.service.intf.dto.OrganGestorDto;
import es.caib.ripea.service.intf.service.MetaExpedientService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * Controlador base que implementa funcionalitats comunes per als controladors
 * de les accions de l'administrador.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseAdminController extends BaseController {

	@Autowired
	public MetaExpedientService metaExpedientService;
	
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
	
	protected MetaExpedientDto comprovarAccesMetaExpedient(HttpServletRequest request, Long metaExpedientId) {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		
		MetaExpedientDto metaExpedient = null;
		if (RolHelper.isRolActualRevisor(request)) {
			metaExpedient = metaExpedientService.findById(entitat.getId(), metaExpedientId);
		} else if (RolHelper.isRolActualDissenyadorOrgan(request)) {
			metaExpedient = metaExpedientService.getAndCheckOrganPermission(
					entitat.getId(),
					metaExpedientId,
					EntitatHelper.getOrganGestorActual(request),
					true);
		} else if (RolHelper.isRolActualAdministradorOrgan(request)) {
			metaExpedient = metaExpedientService.getAndCheckOrganPermission(
					entitat.getId(),
					metaExpedientId,
					EntitatHelper.getOrganGestorActual(request),
					false);
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
