/**
 * 
 */
package es.caib.ripea.war.command;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.dto.FluxFirmaUsuariDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;

/**
 * Command per al manteniment de fluxos de firma d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public class FluxFirmaUsuariCommand {

	private Long id;
	
	@NotEmpty
	private String nom;
	private String descripcio;
	
	@Size(max=64)
	private String portafirmesFluxId;
	
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
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public String getPortafirmesFluxId() {
		return portafirmesFluxId;
	}
	public void setPortafirmesFluxId(String portafirmesFluxId) {
		this.portafirmesFluxId = portafirmesFluxId;
	}
	public static FluxFirmaUsuariCommand asCommand(FluxFirmaUsuariDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				FluxFirmaUsuariCommand.class);
	}
	public static FluxFirmaUsuariDto asDto(FluxFirmaUsuariCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				FluxFirmaUsuariDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
