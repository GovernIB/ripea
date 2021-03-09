/**
 * 
 */
package es.caib.ripea.war.command;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.ripea.core.api.dto.ExpedientPeticioAccioEnumDto;

/**
 * Command per al expedient peticio rebutjar
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientPeticioAcceptarCommand {

	private Long id;
	private Long metaExpedientId;
	private Long expedientId;
	
	private String newExpedientTitol;
	private int any;
	private boolean associarInteressats;
	private ExpedientPeticioAccioEnumDto expedientPeticioAccioEnumDto;
	
	
	public ExpedientPeticioAccioEnumDto getExpedientPeticioAccioEnumDto() {
		return expedientPeticioAccioEnumDto;
	}
	public void setExpedientPeticioAccioEnumDto(ExpedientPeticioAccioEnumDto expedientPeticioAccioEnumDto) {
		this.expedientPeticioAccioEnumDto = expedientPeticioAccioEnumDto;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getMetaExpedientId() {
		return metaExpedientId;
	}
	public void setMetaExpedientId(Long metaExpedientId) {
		this.metaExpedientId = metaExpedientId;
	}
	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	public String getNewExpedientTitol() {
		return newExpedientTitol;
	}
	public void setNewExpedientTitol(String newExpedientTitol) {
		this.newExpedientTitol = newExpedientTitol.trim();
	}
	public int getAny() {
		return any;
	}
	public void setAny(int any) {
		this.any = any;
	}
	public boolean isAssociarInteressats() {
		return associarInteressats;
	}
	public void setAssociarInteressats(boolean associarInteressats) {
		this.associarInteressats = associarInteressats;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(
				this);
	}

}
