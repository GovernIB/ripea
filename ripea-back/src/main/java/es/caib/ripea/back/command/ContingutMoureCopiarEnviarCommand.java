/**
 * 
 */
package es.caib.ripea.back.command;

import es.caib.ripea.back.validation.DestiNotEmpty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Command per a copiar, moure o enviar contenidors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@DestiNotEmpty
public class ContingutMoureCopiarEnviarCommand {

	protected Long origenId;
	protected Long[] origenIds;
	@NotNull
	protected Long destiId;
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
	public Long getDestiId() {
		return destiId;
	}
	public void setDestiId(Long destiId) {
		this.destiId = destiId;
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
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
