/**
 * 
 */
package es.caib.ripea.war.command;

import es.caib.ripea.core.api.dto.OrganGestorFiltreDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
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
	private Long pareId;
	
	public void setCodi(String codi) {
		this.codi = codi != null ? codi.trim() : null;
	}
	public void setNom(String nom) {
		this.nom = nom != null ? nom.trim() : null;
	}
	public void setPareId(Long pareId) {
		this.pareId = pareId;
	}
	
	public OrganGestorFiltreDto asDto() {
		return ConversioTipusHelper.convertir(
				this,
				OrganGestorFiltreDto.class);
	}
	
}