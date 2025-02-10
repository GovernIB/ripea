/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Informació d'un interessat de tipus administració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InteressatAdministracioDto extends InteressatDto {

	protected String organCodi;
	protected String organNom;

	protected Boolean ambOficinaSir;
	
	public String getOrganNom() {
		return organNom;
	}

	public void setOrganNom(String organNom) {
		this.organNom = organNom;
	}

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
	public Boolean getAmbOficinaSir() {
		return ambOficinaSir;
	}
	public void setAmbOficinaSir(Boolean ambOficinaSir) {
		this.ambOficinaSir = ambOficinaSir;
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
