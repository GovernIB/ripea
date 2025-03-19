package es.caib.ripea.service.intf.base.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Informació sobre un artefacte d'un recurs.
 * 
 * @author Límit Tecnologies
 */
@Getter
@Setter
@AllArgsConstructor
public class ResourceArtifact {

	private ResourceArtifactType type;
	private String code;
	private Boolean requiresId;
	@JsonIgnore
	private Class<?> formClass;

	public boolean isFormClassActive() {
		return formClass != null;
	}

}
