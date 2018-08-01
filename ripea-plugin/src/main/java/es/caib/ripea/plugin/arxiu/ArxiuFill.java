/**
 * 
 */
package es.caib.ripea.plugin.arxiu;

/**
 * Estructura que representa un node fill d'un expedient o carpeta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuFill {

	protected String nodeId;
	private ArxiuFillTipusEnum tipus;
	protected String nom;

	public ArxiuFill(
			String nodeId,
			ArxiuFillTipusEnum tipus,
			String nom) {
		this.nodeId = nodeId;
		this.tipus = tipus;
		this.nom = nom;
	}

	public String getNodeId() {
		return nodeId;
	}
	public ArxiuFillTipusEnum getTipus() {
		return tipus;
	}
	public String getNom() {
		return nom;
	}

}
