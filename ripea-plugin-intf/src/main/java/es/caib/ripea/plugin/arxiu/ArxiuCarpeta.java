/**
 * 
 */
package es.caib.ripea.plugin.arxiu;

import java.util.List;

/**
 * Estructura que representa un document de l'arxiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuCarpeta extends ArxiuContingut {

	private List<ArxiuFill> fills;

	public ArxiuCarpeta(
			String nodeId,
			String titol,
			List<ArxiuFill> fills,
			String json) {
		super(
				nodeId,
				titol,
				null,
				null,
				json);
		this.fills = fills;
	}

	public List<ArxiuFill> getFills() {
		return fills;
	}
	public String getNom() {
		return titol;
	}

}
