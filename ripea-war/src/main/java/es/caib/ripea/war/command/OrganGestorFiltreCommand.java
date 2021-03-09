/**
 * 
 */
package es.caib.ripea.war.command;

import lombok.Getter;
import lombok.ToString;

/**
 * Command per al filtre d'expedients dels arxius.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Getter
@ToString
public class OrganGestorFiltreCommand {

	private String codi;
	private String nom;
	
	public void setCodi(String codi) {
		this.codi = codi != null ? codi.trim() : null;
	}
	public void setNom(String nom) {
		this.nom = nom != null ? nom.trim() : null;
	}

	
}