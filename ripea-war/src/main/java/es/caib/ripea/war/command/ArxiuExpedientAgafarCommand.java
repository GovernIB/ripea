/**
 * 
 */
package es.caib.ripea.war.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Command per a agafar expedients de l'arxiu per part de 
 * l'usuari administrador.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuExpedientAgafarCommand {

	@NotNull
	private Long arxiuId;
	@NotNull
	private Long expedientId;
	@NotEmpty @Size(max=64)
	private String usuariCodi;


	public ArxiuExpedientAgafarCommand() {
	}
	public ArxiuExpedientAgafarCommand(
			Long arxiuId,
			Long expedientId) {
		this.arxiuId = arxiuId;
		this.expedientId = expedientId;
	}

	public Long getArxiuId() {
		return arxiuId;
	}
	public void setArxiuId(Long arxiuId) {
		this.arxiuId = arxiuId;
	}
	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	public String getUsuariCodi() {
		return usuariCodi;
	}
	public void setUsuariCodi(String usuariCodi) {
		this.usuariCodi = usuariCodi.trim();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
