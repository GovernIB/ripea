/**
 * 
 */
package es.caib.ripea.back.command;

import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;

import es.caib.ripea.back.validation.DestiNotEmpty;

/**
 * Command per a copiar, moure o enviar contenidors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@DestiNotEmpty
public class ContingutMoureCopiarEnviarCommand {

	protected String accio;
	protected Long expedientOrigenId;
	protected Long origenId;
	protected Long[] origenIds;
	protected Long destiId;
	protected Long expedientDestiId;
	protected Long carpetaDestiId;
	protected String carpetaNova;
	@Size(max=256)
	protected String comentariEnviar;

	private String estructuraCarpetesJson;


	public Long getOrigenId() {
		return origenId;
	}
	public void setOrigenId(Long origenId) {
		this.origenId = origenId;
	}
	public Long[] getOrigenIds() {
		return origenIds;
	}
	public void setOrigenIds(Long[] origenIds) {
		this.origenIds = origenIds;
	}
	public String getComentariEnviar() {
		return comentariEnviar;
	}
	public void setComentariEnviar(String comentariEnviar) {
		this.comentariEnviar = comentariEnviar != null ? comentariEnviar.trim() : null;
	}
	public String getEstructuraCarpetesJson() {
		return estructuraCarpetesJson;
	}
	public void setEstructuraCarpetesJson(String estructuraCarpetesJson) {
		this.estructuraCarpetesJson = estructuraCarpetesJson;
	}
	public Long getDestiId() {
		return destiId;
	}
	public void setDestiId(Long destiId) {
		this.destiId = destiId;
	}
	public Long getExpedientOrigenId() {
		return expedientOrigenId;
	}
	public void setExpedientOrigenId(Long expedientOrigenId) {
		this.expedientOrigenId = expedientOrigenId;
	}
	public Long getExpedientDestiId() {
		return expedientDestiId;
	}
	public void setExpedientDestiId(Long expedientDestiId) {
		this.expedientDestiId = expedientDestiId;
	}
	public Long getCarpetaDestiId() {
		return carpetaDestiId;
	}
	public void setCarpetaDestiId(Long carpetaDestiId) {
		this.carpetaDestiId = carpetaDestiId;
	}
	public String getCarpetaNova() {
		return carpetaNova;
	}
	public void setCarpetaNova(String carpetaNova) {
		this.carpetaNova = carpetaNova;
	}
	public String getAccio() {
		return accio;
	}
	public void setAccio(String accio) {
		this.accio = accio;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
