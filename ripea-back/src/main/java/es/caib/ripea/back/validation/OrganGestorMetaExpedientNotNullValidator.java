/**
 * 
 */
package es.caib.ripea.back.validation;

import es.caib.ripea.back.command.MetaExpedientCommand;
import es.caib.ripea.back.helper.EntitatHelper;
import es.caib.ripea.service.intf.dto.OrganGestorDto;
import es.caib.ripea.service.intf.service.OrganGestorService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

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
