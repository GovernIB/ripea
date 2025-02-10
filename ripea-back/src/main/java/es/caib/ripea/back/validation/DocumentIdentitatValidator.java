/**
 * 
 */
package es.caib.ripea.back.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Valida que el nombre de document d'identitat sigui vàlid. Els tipus de
 * document suportats son: NIF, DNI, NIE i CIF.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentIdentitatValidator implements ConstraintValidator<DocumentIdentitat, String> {
	
	@Override
	public void initialize(final DocumentIdentitat constraintAnnotation) {
	}

	@Override
	public boolean isValid(
			final String value,
			final ConstraintValidatorContext context) {
		try {
			if (value == null || value.isEmpty())
				return true;
			return validacioNif(value);
		} catch (final Exception ex) {
			LOGGER.error("Error en la validació del NIF", ex);
			return false;
		}
	}

	// Validació del DNI
	private static final Pattern dniPattern = Pattern.compile("[0-9]{8}[A-Z]");
	private static boolean validacioDni(String dni) {
		if (!dniPattern.matcher(dni).matches()) {
			return false;
		}
		String nums = dni.substring(0, 8);
		String lletra = dni.substring(8);
		return lletra.equals(lletraNif(new Integer(nums).intValue()));
	}
	// Validació del NIE
	private static final Pattern niePattern = Pattern.compile("[XYZ][0-9]{7}[A-Z]");
	private static boolean validacioNie(String nie) {
		if (!niePattern.matcher(nie).matches()) {
			return false;
		}
		String nums = nie.substring(1, 8);
		String lletra = nie.substring(8);
		return lletra.equals(lletraNif(new Integer(nums).intValue()));
	}
	// Validació del NIF
	// Només s'admeten nombres com a caràcter de control
	private static final String CONTROL_SOLO_NUMEROS = "ABEH";
	// Només s'admeten lletres com a caràcter de control
	private static final String CONTROL_SOLO_LETRAS = "KPQS";
	// Conversió de dígit a lletra de control
	private static final String CONTROL_NUMERO_A_LETRA = "JABCDEFGHI";
	private static final Pattern nifPattern = Pattern.compile("[[A-H][J-N][P-S]UVW][0-9]{7}[0-9A-J]");
	private static boolean validacioNif(String nif) {
		if (dniPattern.matcher(nif).matches())
			return validacioDni(nif);
		else if (niePattern.matcher(nif).matches())
			return validacioNie(nif);
		else if (nifPattern.matcher(nif).matches()) {
			int parA = 0;
			for (int i = 2; i < 8; i += 2) {
				final int digito = Character.digit(nif.charAt(i), 10);
				if (digito < 0) {
					return false;
				}
				parA += digito;
			}
			int nonB = 0;
			for (int i = 1; i < 9; i += 2) {
				final int digito = Character.digit(nif.charAt(i), 10);
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
			final char letraIni = nif.charAt(0);
			final char caracterFin = nif.charAt(8);
			final boolean esControlValido =
					// El caràcter de control es vàlid com a lletra?
					(CONTROL_SOLO_NUMEROS.indexOf(letraIni) < 0 && CONTROL_NUMERO_A_LETRA.charAt(digitoD) == caracterFin) ||
					// El caràcter de control es vàlid com a dígit?
					(CONTROL_SOLO_LETRAS.indexOf(letraIni) < 0 && digitoD == Character.digit(caracterFin, 10));
			return esControlValido;
		} else {
			return false;
		}
	}

	public static final String NIF_STRING_ASOCIATION = "TRWAGMYFPDXBNJZSQVHLCKE";
	public static String lletraNif(int dni) {
		return "" + NIF_STRING_ASOCIATION.charAt(dni % 23);
	}



	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentIdentitatValidator.class);

}
