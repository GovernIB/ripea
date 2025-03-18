/**
 * 
 */
package es.caib.ripea.plugin.caib.ciutada;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Test de generació de la clau de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientClauGeneracioTest {

	public static void main(String[] args) {
		try {
			System.out.println(">>>" + getExpedientClau("ES_A04003003_2016_EXP_RIP000000000000000000000000050", "1"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static String getExpedientClau(
			String expedientIdentificador,
			String unitatAdministrativa) throws NoSuchAlgorithmException {
		String missatge = expedientIdentificador + "/" + unitatAdministrativa;
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] digest = md.digest(missatge.getBytes());
		StringBuilder hexString = new StringBuilder();
	    for (int i = 0; i < digest.length; i++) {
	        String hex = Integer.toHexString(0xFF & digest[i]);
	        if (hex.length() == 1) {
	            hexString.append('0');
	        }
	        hexString.append(hex);
	    }
	    return hexString.toString().toUpperCase();
	}

}
