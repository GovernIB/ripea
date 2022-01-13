/**
 * 
 */
package es.caib.ripea.war.validation;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.service.OrganGestorService;
import es.caib.ripea.war.command.MetaExpedientCommand;
import es.caib.ripea.war.helper.EntitatHelper;
import es.caib.ripea.war.helper.RolHelper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi d'entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class OrganGestorMetaExpedientNotNullValidator implements ConstraintValidator<OrganGestorMetaExpedientNotNull, MetaExpedientCommand> {

	@Autowired
	private OrganGestorService organGestorService;
	@Autowired
	private HttpServletRequest request;

	@Override
	public void initialize(final OrganGestorMetaExpedientNotNull constraintAnnotation) {

	}

	@Override
	public boolean isValid(final MetaExpedientCommand value, final ConstraintValidatorContext context) {
		if (!value.isRolAdminOrgan()) {
			return true;
		}

		if (value.getOrganGestorId() != null) {
			return true;
		}

		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		return organGestorService.hasPermisAdminComu(organActual.getId());
	}

}
