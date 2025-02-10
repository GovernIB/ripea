/**
 * 
 */
package es.caib.ripea.back.command;

import es.caib.ripea.back.helper.ConversioTipusHelper;
import es.caib.ripea.service.intf.dto.DigitalitzacioPerfilDto;
import org.apache.commons.lang.builder.ToStringBuilder;

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
		this.codi = codi != null ? codi.trim() : null;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom != null ? nom.trim() : null;
	}

	public String getDescripcio() {
		return descripcio;
	}

	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio != null ? descripcio.trim() : null;
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
