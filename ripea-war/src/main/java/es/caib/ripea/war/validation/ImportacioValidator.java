/**
 * 
 */
package es.caib.ripea.war.validation;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import es.caib.ripea.core.api.dto.TipusDestiEnumDto;
import es.caib.ripea.core.api.dto.TipusImportEnumDto;
import es.caib.ripea.war.command.ImportacioCommand;
import es.caib.ripea.war.helper.MessageHelper;

/**
 * Constraint de validació que controla alguns camps del formulari d'importació
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ImportacioValidator implements ConstraintValidator<Importacio, ImportacioCommand> {
	
	@Override
	public void initialize(final Importacio constraintAnnotation) {
	}

	@Override
	public boolean isValid(final ImportacioCommand value, final ConstraintValidatorContext context) {
		ImportacioCommand command = (ImportacioCommand)value;
		boolean valid = true;
		if (command.getTipusImportacio().equals(TipusImportEnumDto.NUMERO_REGISTRE)) {
			String numeroRegistre = command.getNumeroRegistre();
			String dataRegistre = command.getDataPresentacio();
			if (numeroRegistre == null || dataRegistre.isEmpty()) {
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty"))
				.addNode("numeroRegistre")
				.addConstraintViolation();
				valid = false;
			}
			if (dataRegistre == null || dataRegistre.isEmpty()) {
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty"))
				.addNode("dataPresentacio")
				.addConstraintViolation();
				valid = false;
			}
		} else {
			String codiEni = command.getCodiEni();
			if (codiEni == null || codiEni.isEmpty()) {
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty"))
				.addNode("codiEni")
				.addConstraintViolation();
				valid = false;
			}
			if (codiEni != null && !isValid(codiEni)) {
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("contingut.importacio.form.camp.eni.notvalid"))
				.addNode("codiEni")
				.addConstraintViolation();
				valid = false;
			}
		}
		if (command.getDestiTipus().equals(TipusDestiEnumDto.CARPETA_NOVA) && command.getCarpetaNom().isEmpty()) {
			context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty"))
			.addNode("carpetaNom")
			.addConstraintViolation();
			valid = false;
		}
		if (!valid)
			context.disableDefaultConstraintViolation();
		return valid;
	}

	private boolean isValid(String codiEni) {
		String uuidPattern = "^ES_[A-Za-z0-9]{9}_[0-9]{4}_[A-Za-z0-9]{30}$";
        Pattern pUuid = Pattern.compile(uuidPattern);
        Matcher mUuid = pUuid.matcher(codiEni);
		return mUuid.matches();
	}
}
