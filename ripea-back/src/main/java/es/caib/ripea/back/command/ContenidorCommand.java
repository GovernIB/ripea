/**
 * 
 */
package es.caib.ripea.back.command;

import es.caib.ripea.back.command.DocumentCommand.CreateDigital;
import es.caib.ripea.back.command.DocumentCommand.CreateFisic;
import es.caib.ripea.back.command.DocumentCommand.UpdateDigital;
import es.caib.ripea.back.command.DocumentCommand.UpdateFisic;
import es.caib.ripea.back.command.DocumentGenericCommand.ConcatenarDigital;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Command per al manteniment de contenidors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ContenidorCommand {

	protected Long id;
	@NotNull(groups = {Create.class, Update.class})
	protected Long entitatId;
	protected Long pareId;
	@NotEmpty(groups = {Create.class, Update.class, CreateDigital.class, CreateFisic.class, UpdateDigital.class, UpdateFisic.class, ConcatenarDigital.class})
	@Size(groups = {Create.class, Update.class, CreateDigital.class, CreateFisic.class, UpdateDigital.class, UpdateFisic.class, ConcatenarDigital.class}, max=256)
	protected String nom;



	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
	public Long getPareId() {
		return pareId;
	}
	public void setPareId(Long pareId) {
		this.pareId = pareId;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom != null ? nom.trim() : null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public interface Create {}
	public interface Update {}

}
