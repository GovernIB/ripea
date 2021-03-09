/**
 * 
 */
package es.caib.ripea.war.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Command per a copiar, moure o enviar contenidors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ContingutMoureCopiarEnviarCommand {

	protected Long origenId;
	protected Long[] origenIds;
	@NotNull
	protected Long destiId;
	@Size(max=256)
	protected String comentariEnviar;



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
		this.comentariEnviar = comentariEnviar.trim();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
