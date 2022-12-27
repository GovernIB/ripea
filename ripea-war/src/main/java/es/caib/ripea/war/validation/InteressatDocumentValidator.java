/**
 * 
 */
package es.caib.ripea.war.validation;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.ripea.core.api.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.InteressatTipusEnumDto;
import es.caib.ripea.war.command.InteressatCommand;
import es.caib.ripea.war.helper.MessageHelper;

/**
 * Constraint de validació que controla que no es repeteixi
 * l'interessat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InteressatDocumentValidator implements ConstraintValidator<InteressatDocument, Object> {


	@Override
	public void initialize(final InteressatDocument constraintAnnotation) {
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		try {
			
			InteressatCommand interessat = (InteressatCommand)value;
			String docNum = interessat.getDocumentNum();
			boolean valid = true;
			
			if (docNum != null && !docNum.isEmpty()) {

				if (interessat.getDocumentTipus() == InteressatDocumentTipusEnumDto.NIF) {
					
					if (interessat.getTipus() == InteressatTipusEnumDto.PERSONA_FISICA) {
						valid = validacioDni(docNum);
					} else {
						valid = validacioCif(docNum);
					}

				} else if (interessat.getDocumentTipus() == InteressatDocumentTipusEnumDto.DOCUMENT_IDENTIFICATIU_ESTRANGERS) {
					valid = validacioNie(docNum);
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

	
	
	
	// ============= Validació del DNI/NIE ============
	public static final String DNI_O_NIE_STRING_ASOCIATION = "TRWAGMYFPDXBNJZSQVHLCKE";
	public static String lletraDniONie(int dni) {
		return "" + DNI_O_NIE_STRING_ASOCIATION.charAt(dni % 23);
	}
	// Validació del DNI
	private static final Pattern dniPattern = Pattern.compile("[0-9]{8}[A-Z]");
	private static boolean validacioDni(String dni) {
		if (!dniPattern.matcher(dni).matches()) {
			return false;
		}
		String nums = dni.substring(0, 8);
		String lletra = dni.substring(8);
		return lletra.equals(lletraDniONie(new Integer(nums).intValue()));
	}
	// Validació del NIE
	private static final Pattern niePattern = Pattern.compile("[XYZ][0-9]{7}[A-Z]");
	private static boolean validacioNie(String nie) {
		if (!niePattern.matcher(nie).matches()) {
			return false;
		}
		String nums = (char)(nie.charAt(0) - 40) + nie.substring(1, 8);
		String lletra = nie.substring(8);
		return lletra.equals(lletraDniONie(new Integer(nums).intValue()));
	}

	
	
	// ========== Validació CIF ================
	// Només s'admeten nombres com a caràcter de control
	private static final String CONTROL_SOLO_NUMEROS = "ABEH";
	// Només s'admeten lletres com a caràcter de control
	private static final String CONTROL_SOLO_LETRAS = "KPQS";
	// Conversió de dígit a lletra de control
	private static final String CONTROL_NUMERO_A_LETRA = "JABCDEFGHI";
	private static final Pattern cifPattern = Pattern.compile("[[A-H][J-N][P-S]UVW][0-9]{7}[0-9A-J]");
	
	private static boolean validacioCif(String cif) {
		if (!cifPattern.matcher(cif).matches()) {
			return false;
		}
		int parA = 0;
		for (int i = 2; i < 8; i += 2) {
			final int digito = Character.digit(cif.charAt(i), 10);
			if (digito < 0) {
				return false;
			}
			parA += digito;
		}
		int nonB = 0;
		for (int i = 1; i < 9; i += 2) {
			final int digito = Character.digit(cif.charAt(i), 10);
			if (digito < 0) {
				return false;
			}
			int nn = 2 * digito;
			if (nn > 9) {
				nn = 1 + (nn - 10);
			}
			nonB += nn;
		}
		final int parcialC = parA + nonB;
		final int digitoE = parcialC % 10;
		final int digitoD = (digitoE > 0) ? (10 - digitoE) : 0;
		final char letraIni = cif.charAt(0);
		final char caracterFin = cif.charAt(8);
		final boolean esControlValido =
				// El caràcter de control es vàlid com a lletra?
				(CONTROL_SOLO_NUMEROS.indexOf(letraIni) < 0 && CONTROL_NUMERO_A_LETRA.charAt(digitoD) == caracterFin) ||
				// El caràcter de control es vàlid com a dígit?
				(CONTROL_SOLO_LETRAS.indexOf(letraIni) < 0 && digitoD == Character.digit(caracterFin, 10));
		return esControlValido;
	}



		
	private static final Logger LOGGER = LoggerFactory.getLogger(InteressatDocumentValidator.class);

}
