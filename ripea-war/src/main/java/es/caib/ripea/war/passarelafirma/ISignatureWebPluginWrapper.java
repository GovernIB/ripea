package es.caib.ripea.war.passarelafirma;

import java.util.Properties;

import org.fundaciobit.plugins.signatureweb.api.ISignatureWebPlugin;

/**
 * Bean amb informació d'un plugin de firma.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ISignatureWebPluginWrapper {

	String pluginId;
	String nom;
	String descripcioCurta;
	String classe;
	Properties properties;
	ISignatureWebPlugin plugin;

	public ISignatureWebPluginWrapper() {
	}
	
	public ISignatureWebPluginWrapper(
			Properties properties,
			String pluginId, 
			ISignatureWebPlugin plugin) {
		this.properties = properties;
		this.pluginId = pluginId;
		this.plugin = plugin;
		
		this.nom = properties.getProperty(PassarelaFirmaHelper.PROPERTIES_BASE + pluginId + ".nom");
		this.classe = properties.getProperty(PassarelaFirmaHelper.PROPERTIES_BASE + pluginId + ".class");
		this.descripcioCurta = properties.getProperty(PassarelaFirmaHelper.PROPERTIES_BASE + pluginId + ".desc");

	}
	

	public String getPluginId() {
		return (pluginId);
	}

	public String getNom() {
		return (nom);
	}
	public String getDescripcioCurta() {
		return (descripcioCurta);
	}
	public String getClasse() {
		return (classe);
	}
	public Properties getProperties() {
		return (properties);
	}
	public ISignatureWebPlugin getPlugin() {
		return plugin;
	}

}
