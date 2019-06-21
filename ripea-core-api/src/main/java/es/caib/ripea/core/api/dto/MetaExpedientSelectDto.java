/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'un MetaExpedient per desplegable.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MetaExpedientSelectDto {

	protected Long id;
	private String nom;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
