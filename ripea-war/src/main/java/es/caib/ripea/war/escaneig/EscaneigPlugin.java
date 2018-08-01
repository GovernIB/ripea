/**
 * 
 */
package es.caib.ripea.war.escaneig;

import java.util.Properties;

/**
 * Bean amb informació d'un plugin d'escaneig.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EscaneigPlugin {

	String pluginId;
	String nom;
	String descripcioCurta;
	String classe;
	Properties properties;

	public EscaneigPlugin() {
	}

	public EscaneigPlugin(
			String pluginId,
			String nom,
			String descripcioCurta,
			String classe,
			Properties properties) {
		this.pluginId = pluginId;
		this.nom = nom;
		this.descripcioCurta = descripcioCurta;
		this.classe = classe;
		this.properties = properties;

	}

	public String getPluginId() {
		return pluginId;
	}
	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getDescripcioCurta() {
		return descripcioCurta;
	}
	public void setDescripcioCurta(String descripcioCurta) {
		this.descripcioCurta = descripcioCurta;
	}
	public String getClasse() {
		return classe;
	}
	public void setClasse(String classe) {
		this.classe = classe;
	}
	public Properties getProperties() {
		return properties;
	}
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

}
