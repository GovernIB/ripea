/**
 * 
 */
package es.caib.ripea.war.command;

import java.util.Date;

import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.dto.MetaExpedientTascaDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import es.caib.ripea.war.validation.CodiMetaExpedientTascaNoRepetit;

/**
 * Command per a les tasques del meta-expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@CodiMetaExpedientTascaNoRepetit(
		campId = "id",
		campCodi = "codi",
		campEntitatId = "entitatId",
		campMetaExpedientId = "metaExpedientId")
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
	private Long estatIdCrearTasca;
	private Long estatIdFinalitzarTasca;

	private Long entitatId;
	private Long metaExpedientId;
	
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
	public Long getEstatIdCrearTasca() {
		return estatIdCrearTasca;
	}
	public void setEstatIdCrearTasca(Long estatIdCrearTasca) {
		this.estatIdCrearTasca = estatIdCrearTasca;
	}
	public Long getEstatIdFinalitzarTasca() {
		return estatIdFinalitzarTasca;
	}
	public void setEstatIdFinalitzarTasca(Long estatIdFinalitzarTasca) {
		this.estatIdFinalitzarTasca = estatIdFinalitzarTasca;
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

	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}

	public Long getMetaExpedientId() {
		return metaExpedientId;
	}
	public void setMetaExpedientId(Long metaExpedientId) {
		this.metaExpedientId = metaExpedientId;
	}

	public interface Create {}
	public interface Update {}

}
