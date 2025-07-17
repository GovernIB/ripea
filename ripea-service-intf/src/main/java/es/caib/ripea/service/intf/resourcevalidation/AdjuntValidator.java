package es.caib.ripea.service.intf.resourcevalidation;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;

import es.caib.ripea.service.intf.model.DocumentResource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdjuntValidator implements ConstraintValidator<AdjuntValid, DocumentResource>{

	private final Environment springEnvironment;
	private final MessageSource messageSource;

    public static String formatByteCount(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "B";
        return String.format("%.2f %s", bytes / Math.pow(1024, exp), pre);
    }

	@Override
	public boolean isValid(DocumentResource resource, ConstraintValidatorContext context) {
        int maxLength = Integer.parseInt(springEnvironment.getProperty("es.caib.ripea.maxUploadSize", "52428800"));
        String formatedMaxLength = formatByteCount(maxLength);
        String message = messageSource.getMessage("es.caib.ripea.service.intf.resourcevalidation.AdjuntValid.adjunt", new Object[]{formatedMaxLength}, LocaleContextHolder.getLocale());

		if (resource.getAdjunt()!=null && (long) maxLength < resource.getAdjunt().getContentLength()) {
            context
                .buildConstraintViolationWithTemplate(message)
                .addPropertyNode(DocumentResource.Fields.adjunt)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
            return false;
		}

		if (resource.getFirmaAdjunt()!=null && resource.getFirmaAdjunt().getContentLength()!=null && (long) maxLength < resource.getFirmaAdjunt().getContentLength()) {
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