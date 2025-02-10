/**
 * 
 */
package es.caib.ripea.back.validation;


import es.caib.ripea.back.command.ImportacioCommand;
import es.caib.ripea.back.helper.MessageHelper;
import es.caib.ripea.service.intf.dto.TipusImportEnumDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Constraint de validació que controla alguns camps del formulari d'importació
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ImportacioValidator implements ConstraintValidator<Importacio, ImportacioCommand> {
	
	@Autowired
	private HttpServletRequest request;
	
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
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty", null, new RequestContext(request).getLocale()))
				.addNode("numeroRegistre")
				.addConstraintViolation();
				valid = false;
			}
			if (dataRegistre == null || dataRegistre.isEmpty()) {
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty", null, new RequestContext(request).getLocale()))
				.addNode("dataPresentacio")
				.addConstraintViolation();
				valid = false;
			}
		} else {
			String codiEni = command.getCodiEni();
			if (codiEni == null || codiEni.isEmpty()) {
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty", null, new RequestContext(request).getLocale()))
				.addNode("codiEni")
				.addConstraintViolation();
				valid = false;
			}
			if (codiEni != null && !isValid(codiEni)) {
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("contingut.importacio.form.camp.eni.notvalid", null, new RequestContext(request).getLocale()))
				.addNode("codiEni")
				.addConstraintViolation();
				valid = false;
			}
		}
				
		if (command.getDestiId() == null || command.getDestiId().isEmpty()) {
			context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty", null, new RequestContext(request).getLocale()))
			.addNode("estructuraCarpetesJson")
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
