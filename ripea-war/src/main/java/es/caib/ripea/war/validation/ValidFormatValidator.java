/**
 * 
 */
package es.caib.ripea.war.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import es.caib.ripea.core.api.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.service.ExpedientInteressatService;
import es.caib.ripea.war.command.DocumentNotificacionsCommand;
import es.caib.ripea.war.helper.MessageHelper;

/**
 * Constraint de validació que controla el format dels camps concepte i
 * descripció d'una notificació
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidFormatValidator implements ConstraintValidator<ValidFormat, DocumentNotificacionsCommand> {

	@Autowired
	private ExpedientInteressatService expedientInteressatService;
	
	private static final String CARACTERS_VALIDS = " aàáäbcçdeèéëfghiìíïjklmnñoòóöpqrstuùúüvwxyzAÀÁÄBCÇDEÈÉËFGHIÌÍÏJKLMNÑOÒÓÖPQRSTUÙÚÜVWXYZ0123456789-_'\"/:().,¿?!¡;·";

	@Override
	public void initialize(final ValidFormat constraintAnnotation) {
	}

	@Override
	public boolean isValid(
			final DocumentNotificacionsCommand command, 
			final ConstraintValidatorContext context) {
		boolean valid = true;
		try {
			// Validació del Concepte
			if (command.getAssumpte() != null && !command.getAssumpte().isEmpty()) {
				List<Character> caractersNoValids = validFormat(command.getAssumpte());
				if (!caractersNoValids.isEmpty()) {
					valid = false;
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("notificacio.form.valid.concepte", new Object[] {listToString(caractersNoValids)}))
							.addNode("assumpte").addConstraintViolation();
				}
			}
			// Validació de la descripció
			if (command.getObservacions() != null && !command.getObservacions().isEmpty()) {
				List<Character> caractersNoValids = validFormat(command.getObservacions());
				if (!caractersNoValids.isEmpty()) {
					valid = false;
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("notificacio.form.valid.descripcio",new Object[] {listToString(caractersNoValids)}))
							.addNode("observacions").addConstraintViolation();
				}
			}
			
			// Validació de email per interessats sin NIF
			if (command.getInteressatsIds() != null) {
				boolean val = true;
				for (Long id : command.getInteressatsIds()) {
					
					InteressatDto interessatDto = expedientInteressatService.findById(id, false);
					if (interessatDto.isPersonaFisica()) {
						if (interessatDto.getRepresentant() == null && interessatDto.getDocumentTipus() != InteressatDocumentTipusEnumDto.NIF && interessatDto.getDocumentTipus() != InteressatDocumentTipusEnumDto.DOCUMENT_IDENTIFICATIU_ESTRANGERS && (interessatDto.getEmail() == null || interessatDto.getEmail().isEmpty())) {
							val = false;
						} else if (interessatDto.getRepresentant() != null && interessatDto.getRepresentant().getDocumentTipus() != InteressatDocumentTipusEnumDto.NIF && interessatDto.getRepresentant().getDocumentTipus() != InteressatDocumentTipusEnumDto.DOCUMENT_IDENTIFICATIU_ESTRANGERS && (interessatDto.getRepresentant().getEmail() == null || interessatDto.getRepresentant().getEmail().isEmpty())) {
							val = false;
						}
					}
				}
				if (!val) {
					valid = false;
					context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("notificacio.form.valid.sin.nif.no.email")).addConstraintViolation();
				}
			}
			
			context.disableDefaultConstraintViolation();
			return valid;
		} catch (final Exception ex) {
			LOGGER.error("Error al validar el format dels camps [concepte=" + command.getAssumpte() + ", descripció=" + command.getObservacions() + "]",
					ex);
		}
		return false;
	}

	private List<Character> validFormat(String campValue) {
		List<Character> caractersNoValids = new ArrayList<Character>();
		char[] campValueArr = campValue.replace("\n", "").replace("\r", "").toCharArray();
		boolean isCaracterValid = true;

		for (char caracter : campValueArr) {
			isCaracterValid = !(CARACTERS_VALIDS.indexOf(caracter) < 0);
			if (!isCaracterValid) {
				caractersNoValids.add(caracter);
			}
		}
		return caractersNoValids;
	}
	
	private StringBuilder listToString(List<?> list) {
	    StringBuilder str = new StringBuilder();
	    for (int i = 0; i < list.size(); i++) {
	    	str.append(list.get(i));
	    }
	    return str;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ValidFormatValidator.class);

}
