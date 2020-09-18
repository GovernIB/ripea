/**
 * 
 */
package es.caib.ripea.war.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import es.caib.ripea.war.command.MetaExpedientCommand;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi d'entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class OrganGestorMetaExpedientNotNullValidator implements ConstraintValidator<OrganGestorMetaExpedientNotNull, MetaExpedientCommand> {

	@Override
	public void initialize(final OrganGestorMetaExpedientNotNull constraintAnnotation) {

	}

	@Override
	public boolean isValid(final MetaExpedientCommand value, final ConstraintValidatorContext context) {
		if (!value.isRolAdminOrgan()) {
			return true;
		}
		
        return value.getOrganGestorId() != null;
	}

}
