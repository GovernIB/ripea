/**
 * 
 */
package es.caib.ripea.plugin.caib.ciutada;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.ciutada.CiutadaDocument;
import es.caib.ripea.plugin.ciutada.CiutadaExpedientInformacio;
import es.caib.ripea.plugin.ciutada.CiutadaNotificacioEstat;
import es.caib.ripea.plugin.ciutada.CiutadaNotificacioEstat.ZonaperJustificantEstat;
import es.caib.ripea.plugin.ciutada.CiutadaNotificacioResultat;
import es.caib.ripea.plugin.ciutada.CiutadaPersona;
import es.caib.ripea.plugin.ciutada.CiutadaPlugin;

/**
 * Implementació de proves pel plugin de comunicació amb el ciutadà.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CiutadaPluginMock implements CiutadaPlugin {

	@Override
	public CiutadaExpedientInformacio expedientCrear(
			String expedientIdentificador,
			String unitatAdministrativa,
			String identificadorProcedimiento,
			String idioma,
			String descripcio,
			CiutadaPersona destinatari,
			CiutadaPersona representat,
			String bantelNumeroEntrada,
			boolean avisosHabilitats,
			String avisosEmail,
			String avisosMobil) throws SistemaExternException {
		String clauSistra = "<buit>";
		try {
			clauSistra = getExpedientClau(
					expedientIdentificador,
					unitatAdministrativa);
			return new CiutadaExpedientInformacio(
					expedientIdentificador,
					clauSistra);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut crear l'expedient a la zona personal (" +
					"identificadorSistra=" + expedientIdentificador + ", " +
					"clauSistra=" + clauSistra + ", " +
					"unitatAdministrativa=" + unitatAdministrativa + ", " +
					"descripcio=" + descripcio + ", " +
					"destinatariNif=" + destinatari.getNif() + ")",
					ex);
		}
	}

	@Override
	public void avisCrear(
			String expedientIdentificador,
			String unitatAdministrativa,
			String titol,
			String text,
			String textSms,
			List<CiutadaDocument> annexos) throws SistemaExternException {
		
	}

	@Override
	public CiutadaNotificacioResultat notificacioCrear(
			String expedientIdentificador,
			String unitatAdministrativa,
			String registreOficinaCodi,
			String registreOficinaOrganCodi,
			CiutadaPersona destinatari,
			CiutadaPersona representat,
			String idioma,
			String oficiTitol,
			String oficiText,
			String avisTitol,
			String avisText,
			String avisTextSms,
			boolean confirmarRecepcio,
			List<CiutadaDocument> annexos) throws SistemaExternException {
		String clauSistra = "<buit>";
		String identificadorSistra = "<buit>";
		try {
			clauSistra = getExpedientClau(
					expedientIdentificador,
					unitatAdministrativa);
			System.out.println(">>> Paràmetres: (" +
					"identificadorSistra=" + expedientIdentificador + ", " +
					"clauSistra=" + clauSistra + ", " +
					"unitatAdministrativa=" + unitatAdministrativa + ", " +
					"registreOficinaCodi=" + registreOficinaCodi + ", " +
					"registreOficinaOrganCodi=" + registreOficinaOrganCodi + ", " +
					"destinatari=" + destinatari + ", " +
					"representat=" + representat + ", " +
					"idioma=" + idioma + ", " +
					"oficiTitol=" + oficiTitol + ", " +
					"avisTitol=" + avisTitol + ", " +
					"confirmarRecepcio=" + confirmarRecepcio + ")");
			CiutadaNotificacioResultat resultat = new CiutadaNotificacioResultat();
			resultat.setRegistreData(new Date());
			resultat.setRegistreNumero(new Long(System.currentTimeMillis()).toString());
			return resultat;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut crear la notificació (" +
							"identificadorSistra=" + identificadorSistra + ", " +
							"clauSistra=" + clauSistra + ", " +
							"unitatAdministrativa=" + unitatAdministrativa + ", " +
							"oficiTitol=" + oficiTitol + ", " +
							"destinatariNif=" + destinatari.getNif() + ")",
					ex);
		}
	}

	@Override
	public CiutadaNotificacioEstat notificacioObtenirJustificantRecepcio(
			String registreNumero) throws SistemaExternException {
		CiutadaNotificacioEstat notificacioEstat = new CiutadaNotificacioEstat();
		notificacioEstat.setEstat(ZonaperJustificantEstat.PENDENT);
		return notificacioEstat;
	}



	private String getExpedientClau(
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
