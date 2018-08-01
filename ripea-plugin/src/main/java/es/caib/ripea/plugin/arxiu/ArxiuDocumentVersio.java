/**
 * 
 */
package es.caib.ripea.plugin.arxiu;

import java.util.Date;

/**
 * Estructura que representa una versió d'un document de l'arxiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuDocumentVersio {

	private String id;
	private String nodeId;
	private Date data;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}

}
