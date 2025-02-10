package es.caib.ripea.service.helper;

import es.caib.plugins.arxiu.api.IArxiuPlugin;

public class IArxiuPluginWrapper {

	private IArxiuPlugin plugin;
	private String endpoint;
	
	public IArxiuPluginWrapper(IArxiuPlugin plugin, String endpoint) {
		super();
		this.plugin = plugin;
		this.endpoint = endpoint;
	}
	public IArxiuPlugin getPlugin() {
		return plugin;
	}
	public void setPlugin(IArxiuPlugin plugin) {
		this.plugin = plugin;
	}
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}	
}