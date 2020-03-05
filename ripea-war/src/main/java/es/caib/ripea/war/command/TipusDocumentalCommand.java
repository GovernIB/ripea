/**
 * 
 */
package es.caib.ripea.war.command;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.dto.TipusDocumentalDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;

/**
 * Command per al manteniment de meta-documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TipusDocumentalCommand {

	private Long id;
	@NotEmpty @Size(max=64)
	private String codi;
	@NotEmpty @Size(max=256)
	private String nom;
	private Long entitatId;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
	
	public static TipusDocumentalCommand asCommand(TipusDocumentalDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				TipusDocumentalCommand.class);
	}
	public static TipusDocumentalDto asDto(TipusDocumentalCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				TipusDocumentalDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
