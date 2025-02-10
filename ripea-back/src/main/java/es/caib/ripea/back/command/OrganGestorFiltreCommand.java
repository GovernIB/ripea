/**
 * 
 */
package es.caib.ripea.back.command;

import es.caib.ripea.back.helper.ConversioTipusHelper;
import es.caib.ripea.service.intf.dto.OrganEstatEnumDto;
import es.caib.ripea.service.intf.dto.OrganGestorFiltreDto;
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
    private OrganEstatEnumDto estat;
	
	public void setCodi(String codi) {
		this.codi = codi != null ? codi.trim() : null;
	}
	public void setNom(String nom) {
		this.nom = nom != null ? nom.trim() : null;
	}
	public void setPareId(Long pareId) {
		this.pareId = pareId;
	}
	public void setEstat(OrganEstatEnumDto estat) {
		this.estat = estat;
	}
	public OrganGestorFiltreDto asDto() {
		return ConversioTipusHelper.convertir(
				this,
				OrganGestorFiltreDto.class);
	}

	
}