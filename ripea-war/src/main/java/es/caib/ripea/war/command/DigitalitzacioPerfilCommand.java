/**
 * 
 */
package es.caib.ripea.war.command;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.ripea.core.api.dto.DigitalitzacioPerfilDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;

/**
 * Command per a enviar documents al portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DigitalitzacioPerfilCommand {

	private String codi;
	private String nom;
	private String descripcio;
	
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
	
	public static DigitalitzacioPerfilCommand asCommand(DigitalitzacioPerfilDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				DigitalitzacioPerfilCommand.class);
	}
	public static DigitalitzacioPerfilDto asDto(DigitalitzacioPerfilCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				DigitalitzacioPerfilDto.class);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
