package es.caib.ripea.war.passarelafirma;

import java.util.Date;

import org.fundaciobit.plugins.signature.api.CommonInfoSignature;
import org.fundaciobit.plugins.signature.api.FileInfoSignature;
import org.fundaciobit.plugins.signatureweb.api.SignaturesSetWeb;

/**
 * Bean amb informació sobre un o varis documents a firmar amb
 * la passarel·la.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SignaturesSetExtend extends SignaturesSetWeb {

	protected String pluginId = null;
	protected final String urlFinalRipea;

	public SignaturesSetExtend(
			String signaturesSetId,
			Date expiryDate,
			CommonInfoSignature commonInfoSignature,
			FileInfoSignature[] fileInfoSignatureArray,
			String urlFinal,
			String urlFinalRipea) {
		super(	signaturesSetId,
				expiryDate,
				commonInfoSignature,
				fileInfoSignatureArray,
				urlFinal);
		this.urlFinalRipea = urlFinalRipea;
	}

	public String getPluginId() {
		return pluginId;
	}
	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}
	public String getUrlFinalRipea() {
		return urlFinalRipea;
	}

}
