/**
 * 
 */
package es.caib.ripea.war.command;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.dto.ArxiuDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;

/**
 * Command per al manteniment d'arxius.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuCommand {

	private Long id;
	@NotEmpty @Size(max=256)
	private String nom;
	@NotEmpty @Size(max=9)
	private String unitatCodi;
	private Long pareId;

	private Long entitatId;



	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getUnitatCodi() {
		return unitatCodi;
	}
	public void setUnitatCodi(String unitatCodi) {
		this.unitatCodi = unitatCodi;
	}
	public Long getPareId() {
		return pareId;
	}
	public void setPareId(Long pareId) {
		this.pareId = pareId;
	}
	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}

	public static ArxiuCommand asCommand(ArxiuDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				ArxiuCommand.class);
	}
	public static ArxiuDto asDto(ArxiuCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				ArxiuDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
