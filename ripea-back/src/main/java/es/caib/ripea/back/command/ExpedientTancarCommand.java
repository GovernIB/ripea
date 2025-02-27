/**
 * 
 */
package es.caib.ripea.back.command;

import org.apache.commons.lang3.builder.ToStringBuilder;
import javax.validation.constraints.NotEmpty;

import javax.validation.constraints.Size;

/**
 * Command per a l'acció de tancar expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientTancarCommand extends ContenidorCommand {

	protected Long id;
	@NotEmpty @Size(max=1024)
	protected String motiu;
	private Long[] documentsPerFirmar;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMotiu() {
		return motiu;
	}
	public void setMotiu(String motiu) {
		this.motiu = motiu != null ? motiu.trim() : null;
	}
	public Long[] getDocumentsPerFirmar() {
		return documentsPerFirmar;
	}
	public void setDocumentsPerFirmar(Long[] documentsPerFirmar) {
		this.documentsPerFirmar = documentsPerFirmar;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
