/**
 * 
 */
package es.caib.ripea.war.command;

import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.dto.MetaExpedientDominiDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;

/**
 * Command per a els dominis del meta-expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MetaExpedientDominiCommand {

	private Long id;
	@NotEmpty
	private String codi;
	@NotEmpty
	private String nom;
	private String descripcio;

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
		this.codi = codi.trim();
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom.trim();
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio.trim();
	}

	public static MetaExpedientDominiCommand asCommand(MetaExpedientDominiDto dto) {
		MetaExpedientDominiCommand command = ConversioTipusHelper.convertir(
				dto,
				MetaExpedientDominiCommand.class);
		return command;
	}
	public static MetaExpedientDominiDto asDto(MetaExpedientDominiCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				MetaExpedientDominiDto.class);
	}

	public interface Create {}
	public interface Update {}

}
