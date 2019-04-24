/**
 * 
 */
package es.caib.ripea.war.command;

import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.dto.ExpedientEstatDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientEstatCommand {

	private Long id;
	@NotEmpty
	private String codi;
	@NotEmpty
	private String nom;
	private int ordre;
	private String color;
	private Long metaExpedientId;
	private boolean inicial;
	private String responsableCodi;
	
	
	public String getResponsableCodi() {
		return responsableCodi;
	}

	public void setResponsableCodi(String responsableCodi) {
		this.responsableCodi = responsableCodi;
	}

	public boolean isInicial() {
		return inicial;
	}

	public void setInicial(boolean inicial) {
		this.inicial = inicial;
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

	public String getCodi() {
		return codi;
	}

	public void setCodi(String codi) {
		this.codi = codi;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public int getOrdre() {
		return ordre;
	}

	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	public static ExpedientEstatCommand asCommand(ExpedientEstatDto dto) {
		ExpedientEstatCommand command = ConversioTipusHelper.convertir(
				dto,
				ExpedientEstatCommand.class);
		return command;
	}
	public static ExpedientEstatDto asDto(ExpedientEstatCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				ExpedientEstatDto.class);
	}
	


	
	public interface Create {}
	public interface Update {}

}
