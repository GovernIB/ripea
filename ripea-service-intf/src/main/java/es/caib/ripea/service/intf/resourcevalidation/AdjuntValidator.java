package es.caib.ripea.service.intf.resourcevalidation;

import es.caib.ripea.service.intf.model.DocumentResource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
@RequiredArgsConstructor
public class AdjuntValidator implements ConstraintValidator<AdjuntValid, DocumentResource>{

    private final MessageSource messageSource;

    @Override
	public boolean isValid(DocumentResource resource, ConstraintValidatorContext context) {
        int maxLength = 52428800; //es.caib.ripea.back.config.WebMvcConfig.MAX_UPLOAD_SIZE;

        if (resource.getAdjunt()!=null && (long) maxLength < resource.getAdjunt().getContentLength()) {
            String message = messageSource.getMessage("es.caib.ripea.service.intf.resourcevalidation.AdjuntValid.adjunt", new Object[]{maxLength}, LocaleContextHolder.getLocale());
            context
                .buildConstraintViolationWithTemplate(message)
                .addPropertyNode(DocumentResource.Fields.adjunt)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
            return false;
        }

        if (resource.getFirmaAdjunt()!=null && (long) maxLength < resource.getFirmaAdjunt().getContentLength()) {
            String message = messageSource.getMessage("es.caib.ripea.service.intf.resourcevalidation.AdjuntValid.firma", new Object[]{maxLength}, LocaleContextHolder.getLocale());
            context
                .buildConstraintViolationWithTemplate(message)
                .addPropertyNode(DocumentResource.Fields.firmaAdjunt)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
            return false;
        }

        return true;
	}
}