/**
 * 
 */
package es.caib.ripea.war.escaneig;

import java.util.List;
import java.util.Set;

import org.fundaciobit.plugins.scanweb.api.ScanWebConfig;
import org.fundaciobit.plugins.scanweb.api.ScanWebMode;
import org.fundaciobit.plugins.utils.Metadata;

import es.caib.ripea.war.command.DocumentCommand;

/**
 * Classe tester per a l'escaneig de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EscaneigConfig extends ScanWebConfig {

	private String pluginId;
	private DocumentCommand command;
	private final long expiryTransaction;
	private String urlFinalRipea;

	public EscaneigConfig(
			Long scanWebId,
			String scanType,
			Set<String> flags,
			List<Metadata> metadades,
			ScanWebMode mode,
			String languageUI,
			DocumentCommand command,
			String urlFinal,
			long expiryTransaction,
			String urlFinalRipea) {
		super(	scanWebId,
				scanType,
				flags,
				metadades,
				mode,
				languageUI,
				urlFinal);
		this.command = command;
		this.expiryTransaction = expiryTransaction;
		this.urlFinalRipea = urlFinalRipea;
	}

	public String getPluginId() {
		return pluginId;
	}
	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}
	public DocumentCommand getCommand() {
		return command;
	}
	public long getExpiryTransaction() {
		return expiryTransaction;
	}
	public String getUrlFinalRipea() {
		return urlFinalRipea;
	}

}
