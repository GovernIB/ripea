/**
 * 
 */
package es.caib.ripea.war.command;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.dto.URLInstruccioDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import es.caib.ripea.war.validation.URLInstruccio;

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
