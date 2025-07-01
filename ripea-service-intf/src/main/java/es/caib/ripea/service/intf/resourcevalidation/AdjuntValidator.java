package es.caib.ripea.service.intf.resourcevalidation;

import es.caib.ripea.service.intf.model.DocumentResource;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AdjuntValidator implements ConstraintValidator<AdjuntValid, DocumentResource>{

	@Override
	public boolean isValid(DocumentResource resource, ConstraintValidatorContext context) {
        int maxLength = 52428800; //es.caib.ripea.back.config.WebMvcConfig.MAX_UPLOAD_SIZE;

        if (resource.getAdjunt()!=null && (long) maxLength < resource.getAdjunt().getContentLength()) {
            context
                .buildConstraintViolationWithTemplate("{es.caib.ripea.service.intf.resourcevalidation.AdjuntValid.adjunt}")
                .addPropertyNode(DocumentResource.Fields.adjunt)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
            return false;
        }

        if (resource.getFirmaAdjunt()!=null && (long) maxLength < resource.getFirmaAdjunt().getContentLength()) {
            context
                .buildConstraintViolationWithTemplate("{es.caib.ripea.service.intf.resourcevalidation.AdjuntValid.firma}")
                .addPropertyNode(DocumentResource.Fields.firmaAdjunt)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
            return false;
        }

        return true;
	}
}