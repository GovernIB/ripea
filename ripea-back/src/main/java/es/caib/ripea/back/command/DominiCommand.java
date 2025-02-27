/**
 * 
 */
package es.caib.ripea.back.command;

import es.caib.ripea.back.helper.ConversioTipusHelper;
import es.caib.ripea.back.validation.validDomini;
import es.caib.ripea.service.intf.dto.DominiDto;
import org.apache.commons.lang3.builder.ToStringBuilder;
import javax.validation.constraints.NotEmpty;

import javax.validation.constraints.Size;

/**
 * Command per al manteniment de dominis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@validDomini
public class DominiCommand {

	private Long id;
	@NotEmpty @Size(max=64)
	private String codi;
	@NotEmpty @Size(max=256)
	private String nom;
	private String descripcio;
	private Long entitatId;
	@NotEmpty @Size(max=256)
	private String consulta;
	@NotEmpty @Size(max=1000)
	private String cadena;
	@NotEmpty @Size(max=256)
	private String contrasenya;
	
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
	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
	public String getConsulta() {
		return consulta;
	}
	public void setConsulta(String consulta) {
		this.consulta = consulta != null ? consulta.trim() : null;
	}
	public String getCadena() {
		return cadena;
	}
	public void setCadena(String cadena) {
		this.cadena = cadena != null ? cadena.trim() : null;
	}
	public String getContrasenya() {
		return contrasenya;
	}
	public void setContrasenya(String contrasenya) {
		this.contrasenya = contrasenya != null ? contrasenya.trim() : null;
	}
	
	public static DominiCommand asCommand(DominiDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				DominiCommand.class);
	}
	public static DominiDto asDto(DominiCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				DominiDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
