/**
 * 
 */
package es.caib.ripea.back.command;

import es.caib.ripea.back.helper.ConversioTipusHelper;
import es.caib.ripea.back.validation.URLInstruccio;
import es.caib.ripea.service.intf.dto.URLInstruccioDto;
import org.apache.commons.lang3.builder.ToStringBuilder;
import javax.validation.constraints.NotEmpty;

/**
 * Command per al manteniment d'urls d'instrucció.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@URLInstruccio
public class URLInstruccioCommand {

	private Long id;
	@NotEmpty
	private String codi;
	@NotEmpty
	private String nom;
	private String descripcio;
	
	@NotEmpty
	private String url;
	
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
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public static URLInstruccioCommand asCommand(URLInstruccioDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				URLInstruccioCommand.class);
	}
	public static URLInstruccioDto asDto(URLInstruccioCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				URLInstruccioDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
