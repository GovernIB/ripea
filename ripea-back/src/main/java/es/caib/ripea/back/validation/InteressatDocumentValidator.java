package es.caib.ripea.back.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.ripea.back.command.InteressatCommand;
import es.caib.ripea.back.helper.MessageHelper;
import es.caib.ripea.service.intf.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.InteressatTipusEnumDto;
import es.caib.ripea.service.intf.utils.Utils;

public class InteressatDocumentValidator implements ConstraintValidator<InteressatDocument, Object> {

	@Override
	public void initialize(final InteressatDocument constraintAnnotation) {}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {

		try {

			InteressatCommand interessat = (InteressatCommand)value;
			String docNum = interessat.getDocumentNum();
			boolean valid = true;
			
			if (docNum != null && !docNum.isEmpty()) {

				if (interessat.getDocumentTipus() == InteressatDocumentTipusEnumDto.NIF) {
					
					if (interessat.getTipus() == InteressatTipusEnumDto.PERSONA_FISICA) {
						valid = Utils.validacioDni(docNum);
					} else {
						valid = Utils.validacioCif(docNum);
					}

				} else if (interessat.getDocumentTipus() == InteressatDocumentTipusEnumDto.DOCUMENT_IDENTIFICATIU_ESTRANGERS) {
					valid = Utils.validacioNie(docNum);
				}

				if (!valid) {
					context
					.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("interessat.form.valid.documentNum"))
					.addNode("documentNum")
					.addConstraintViolation();
					context.disableDefaultConstraintViolation();
				}
				
			} else {
				valid = false;
			}
			return valid;
		} catch (final Exception ex) {
        	LOGGER.error("Ha d'informar el número de document", ex);
        	return false;
        }
	}
		
	private static final Logger LOGGER = LoggerFactory.getLogger(InteressatDocumentValidator.class);
}
