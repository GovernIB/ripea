/**
 * 
 */
package es.caib.ripea.back.command;

/**
 * Command per canvi massiu d'estat d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientMassiuCanviEstatCommand {


	protected Long metaNodeId;

	private Long expedientEstatId;

	public Long getExpedientEstatId() {
		return expedientEstatId;
	}
	public void setExpedientEstatId(Long expedientEstatId) {
		this.expedientEstatId = expedientEstatId;
	}
	public Long getMetaNodeId() {
		return metaNodeId;
	}
	public void setMetaNodeId(Long metaNodeId) {
		this.metaNodeId = metaNodeId;
	}


}
