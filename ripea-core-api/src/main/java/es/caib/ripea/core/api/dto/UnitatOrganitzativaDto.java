/**
 * 
 */
package es.caib.ripea.core.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Informació d'una unitat organitzativa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class UnitatOrganitzativaDto implements Serializable {

	private String codi;
	private String denominacio;
	private String nifCif;
	private String codiUnitatSuperior;
	private String codiUnitatArrel;
	private Date dataCreacioOficial;
	private Date dataSupressioOficial;
	private Date dataExtincioFuncional;
	private Date dataAnulacio;
	private String estat; // V: Vigente, E: Extinguido, A: Anulado, T: Transitorio

	private String codiPais;
	private String codiComunitat;
	private String codiProvincia;
	private String codiPostal;
	private String nomLocalitat;
	private String localitat;

	private String adressa;
	private Long tipusVia;
	private String nomVia;
	private String numVia;

	private List<UnitatOrganitzativaDto> lastHistoricosUnitats;


	public String getNom() {
		return this.denominacio + " (" + this.codi + ")";
	}
	
	private static final long serialVersionUID = -5602898182576627524L;

}
