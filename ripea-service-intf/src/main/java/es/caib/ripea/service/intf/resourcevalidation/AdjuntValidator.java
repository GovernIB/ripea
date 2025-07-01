package es.caib.ripea.service.intf.resourcevalidation;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;

import es.caib.ripea.service.intf.model.DocumentResource;

public class AdjuntValidator implements ConstraintValidator<AdjuntValid, DocumentResource>{
	
	@Autowired private Environment springEnvironment;
	@Autowired private MessageSource messageSource;
	
	@Override
	public boolean isValid(DocumentResource resource, ConstraintValidatorContext context) {
        
		//Aqui no tenim accés al ConfigHelper, la propietat esta a la taula IPA_CONFIG sense possibilitat de configuració per entitat.
		int maxLength = Integer.parseInt(springEnvironment.getProperty("es.caib.ripea.maxUploadSize", "52428800"));

		String mensaje = messageSource.getMessage(
			    "es.caib.ripea.service.intf.resourcevalidation.AdjuntValid.adjunt",
			    new Object[] {(maxLength/1024)/1024},
			    LocaleContextHolder.getLocale());
		
		if (resource.getAdjunt()!=null) {
	        if ((long) maxLength < resource.getAdjunt().getContentLength()) {
	            context
	                .buildConstraintViolationWithTemplate(mensaje)
	                .addPropertyNode(DocumentResource.Fields.adjunt)
	                .addConstraintViolation()
	                .disableDefaultConstraintViolation();
	            return false;
	        }
		}

		if (resource.getFirmaAdjunt()!=null) {
	        if ((long) maxLength < resource.getFirmaAdjunt().getContentLength()) {
	            context
	                .buildConstraintViolationWithTemplate(mensaje)
	                .addPropertyNode(DocumentResource.Fields.firmaAdjunt)
	                .addConstraintViolation()
	                .disableDefaultConstraintViolation();
	            return false;
	        }
		}

        return true;
	}
}