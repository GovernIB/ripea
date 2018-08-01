/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;

/**
 * Informació d'una unitat organitzativa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TipusViaDto implements Serializable {

	private Long codi;
	private String descripcio;

	public TipusViaDto(
			Long codi,
			String descripcio) {
		this.codi = codi;
		this.descripcio = descripcio;
	}

	public Long getCodi() {
		return codi;
	}
	public void setCodi(Long codi) {
		this.codi = codi;
	}

	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	private static final long serialVersionUID = -5602898182576627524L;

}
