package es.caib.ripea.service.intf.resourcevalidation;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import es.caib.ripea.service.intf.dto.DocumentFirmaTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNtiEstadoElaboracionEnumDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;

import es.caib.ripea.service.intf.model.DocumentResource;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

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

    private static final String REGEX = "^ES_[A-Z0-9]{9}_[0-9]{4}_[A-Z0-9]{1,30}$";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    public static boolean esValidoDocumentoOrigen(String codigo) {
        if (codigo == null) {
            return false;
        }
        return PATTERN.matcher(codigo).matches();
    }

	@Override
	public boolean isValid(DocumentResource resource, ConstraintValidatorContext context) {
        boolean valid = true;

        if (resource.getNtiEstadoElaboracion() != null
                && resource.getNtiEstadoElaboracion() != DocumentNtiEstadoElaboracionEnumDto.EE01
                && resource.getNtiEstadoElaboracion() != DocumentNtiEstadoElaboracionEnumDto.EE99) {
            if (resource.getNtiIdDocumentoOrigen() == null) {
                valid = false;
                context
                        .buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                        .addPropertyNode(DocumentResource.Fields.ntiIdDocumentoOrigen)
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
            } else {
                if (!esValidoDocumentoOrigen(resource.getNtiIdDocumentoOrigen())) {
                    valid = false;
                    context
                            .buildConstraintViolationWithTemplate("{es.caib.ripea.service.intf.resourcevalidation.AdjuntValid.ntiIdDocumentoOrigen}")
                            .addPropertyNode(DocumentResource.Fields.ntiIdDocumentoOrigen)
                            .addConstraintViolation()
                            .disableDefaultConstraintViolation();
                }
            }
        }

        // Adjunts
        int maxLength = Integer.parseInt(springEnvironment.getProperty("es.caib.ripea.maxUploadSize", "52428800"));
        String formatedMaxLength = formatByteCount(maxLength);
        String message = messageSource.getMessage("es.caib.ripea.service.intf.resourcevalidation.AdjuntValid.adjunt", new Object[]{formatedMaxLength}, LocaleContextHolder.getLocale());

        if (resource.getAdjunt()!=null && (long) maxLength < resource.getAdjunt().getContentLength()) {
            valid = false;
            context
                .buildConstraintViolationWithTemplate(message)
                .addPropertyNode(DocumentResource.Fields.adjunt)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
		}

		if (resource.getFirmaAdjunt()!=null && resource.getFirmaAdjunt().getContentLength()!=null && (long) maxLength < resource.getFirmaAdjunt().getContentLength()) {
            valid = false;
            context
                .buildConstraintViolationWithTemplate(message)
                .addPropertyNode(DocumentResource.Fields.firmaAdjunt)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
		}

        if (resource.getHasFirma() != null 
        		&& resource.getHasFirma()
                && DocumentFirmaTipusEnumDto.FIRMA_SEPARADA.equals(resource.getDocumentFirmaTipus())
                && (resource.getFirmaAdjunt()==null || resource.getFirmaAdjunt().getContentLength()==null)
        ){
            valid = false;
            context
                    .buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                    .addPropertyNode(DocumentResource.Fields.firmaAdjunt)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
        }

        return valid;
	}
}