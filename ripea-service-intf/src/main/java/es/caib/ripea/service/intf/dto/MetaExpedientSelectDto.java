/**
 * 
 */
package es.caib.ripea.service.intf.dto;

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

}
