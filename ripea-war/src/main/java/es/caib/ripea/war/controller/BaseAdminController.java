/**
 * 
 */
package es.caib.ripea.war.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.helper.EntitatHelper;
import es.caib.ripea.war.helper.RolHelper;

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

	public EntitatDto getEntitatActualComprovantPermisAdminEntitatOrgan(HttpServletRequest request) {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat == null) {
			throw new SecurityException("No te cap entitat assignada");
		}

		if (!entitat.isUsuariActualTeOrgans()) {
			throw new SecurityException("No te permisos per accedir a aquesta entitat com a administrador");
		}
		return entitat;
	}

	public EntitatDto getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(
			HttpServletRequest request) {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat == null) {
			throw new SecurityException("No te cap entitat assignada");
		}
		if (!entitat.isUsuariActualAdministration() && !entitat.isUsuariActualTeOrgans() && !RolHelper.isRolActualRevisor(request)) {
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
		if (!entitat.isUsuariActualAdministration()) {
				
		}	
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		MetaExpedientDto metaExpedient = metaExpedientService.getAndCheckAdminPermission(
				entitat.getId(),
				metaExpedientId,
				organActual != null ? organActual.getId() : null);
		return metaExpedient;
	}
}
