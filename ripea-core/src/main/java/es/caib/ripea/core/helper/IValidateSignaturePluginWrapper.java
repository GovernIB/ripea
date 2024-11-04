package es.caib.ripea.core.helper;

import org.fundaciobit.plugins.validatesignature.api.IValidateSignaturePlugin;

public class IValidateSignaturePluginWrapper {

	private IValidateSignaturePlugin plugin;
	private String endpoint;
	
	public IValidateSignaturePluginWrapper(IValidateSignaturePlugin plugin, String endpoint) {
		super();
		this.plugin = plugin;
		this.endpoint = endpoint;
	}

	public IValidateSignaturePlugin getPlugin() {
		return plugin;
	}

	public void setPlugin(
			IValidateSignaturePlugin plugin) {
		this.plugin = plugin;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(
			String endpoint) {
		this.endpoint = endpoint;
	}
	
}