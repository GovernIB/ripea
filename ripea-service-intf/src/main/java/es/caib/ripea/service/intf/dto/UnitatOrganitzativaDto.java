/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import es.caib.ripea.service.intf.utils.Utils;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Informació d'una unitat organitzativa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
public class UnitatOrganitzativaDto implements Serializable {

	private String codi;
	private String denominacio;
	private String denominacioCooficial;
	private String oldDenominacio;
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

	private Boolean ambOficinaSir;
	
	public String getDenominacioCooficial() {
		return Utils.isNotEmpty(denominacioCooficial) ? denominacioCooficial : denominacio;
	}
	
	public String getCodi() {
		return codi;
	}

	public String getDenominacio() {
		return denominacio;
	}

	public String getOldDenominacio() {
		return oldDenominacio;
	}

	public String getNifCif() {
		return nifCif;
	}

	public String getCodiUnitatSuperior() {
		return codiUnitatSuperior;
	}

	public String getCodiUnitatArrel() {
		return codiUnitatArrel;
	}

	public Date getDataCreacioOficial() {
		return dataCreacioOficial;
	}

	public Date getDataSupressioOficial() {
		return dataSupressioOficial;
	}

	public Date getDataExtincioFuncional() {
		return dataExtincioFuncional;
	}

	public Date getDataAnulacio() {
		return dataAnulacio;
	}

	public String getEstat() {
		return estat;
	}

	public String getCodiPais() {
		return codiPais;
	}

	public String getCodiComunitat() {
		return codiComunitat;
	}

	public String getCodiProvincia() {
		return codiProvincia;
	}

	public String getCodiPostal() {
		return codiPostal;
	}

	public String getNomLocalitat() {
		return nomLocalitat;
	}

	public String getLocalitat() {
		return localitat;
	}

	public String getAdressa() {
		return adressa;
	}

	public Long getTipusVia() {
		return tipusVia;
	}

	public String getNomVia() {
		return nomVia;
	}

	public String getNumVia() {
		return numVia;
	}

	public List<UnitatOrganitzativaDto> getLastHistoricosUnitats() {
		return lastHistoricosUnitats;
	}

	public Boolean getAmbOficinaSir() {
		return ambOficinaSir;
	}



	private static final long serialVersionUID = -5602898182576627524L;

}
