/**
 * 
 */
package es.caib.ripea.war.command;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.dto.MetaExpedientTascaDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;

/**
 * Command per a les tasques del meta-expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MetaExpedientTascaCommand {

	private Long id;
	@NotEmpty
	private String codi;
	@NotEmpty
	private String nom;
	@NotEmpty
	private String descripcio;
	private String responsable;
	private boolean activa;
	private Date dataLimit;

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
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public String getResponsable() {
		return responsable;
	}
	public void setResponsable(String responsable) {
		this.responsable = responsable;
	}
	public boolean isActiva() {
		return activa;
	}
	public void setActiva(boolean activa) {
		this.activa = activa;
	}

	public static MetaExpedientTascaCommand asCommand(MetaExpedientTascaDto dto) {
		MetaExpedientTascaCommand command = ConversioTipusHelper.convertir(
				dto,
				MetaExpedientTascaCommand.class);
		return command;
	}
	public static MetaExpedientTascaDto asDto(MetaExpedientTascaCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				MetaExpedientTascaDto.class);
	}

	public Date getDataLimit() {
		return dataLimit;
	}
	public void setDataLimit(Date dataLimit) {
		this.dataLimit = dataLimit;
	}

	public interface Create {}
	public interface Update {}

}
