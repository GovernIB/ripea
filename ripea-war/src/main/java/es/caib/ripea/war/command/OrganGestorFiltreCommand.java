/**
 * 
 */
package es.caib.ripea.war.command;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Command per al filtre d'expedients dels arxius.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Getter
@Setter
@ToString
public class OrganGestorFiltreCommand {

	private String codi;
	private String nom;

}