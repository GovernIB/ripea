/**
 * 
 */
package es.caib.ripea.core.api.dto;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Informació d'un interessat de tipus administració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InteressatAdministracioDto extends InteressatDto {

	protected String organCodi;

	@Override
	public InteressatTipusEnumDto getTipus() {
		return InteressatTipusEnumDto.ADMINISTRACIO;
	}
	
	public String getOrganCodi() {
		return organCodi;
	}
	public void setOrganCodi(String organCodi) {
		this.organCodi = organCodi;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String getNomComplet() {
		return organCodi;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
