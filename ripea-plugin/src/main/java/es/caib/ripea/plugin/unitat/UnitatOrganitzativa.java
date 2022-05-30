/**
 * 
 */
package es.caib.ripea.plugin.unitat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Informació d'una unitat organitzativa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class UnitatOrganitzativa implements Serializable, Comparable<UnitatOrganitzativa> {

	@JsonProperty("codigo")
	private String codi;
	@JsonProperty("denominacion")
	private String denominacio;
	private String nifCif;
	private String nivellAdministracio;
	private String tipusEntitatPublica;
	private String tipusUnitatOrganica;
	private String poder;
	private String sigles;
	private String codiUnitatSuperior;
	private String codiUnitatArrel;
	private Long nivellJerarquic;
	private Date dataCreacioOficial;
	private Date dataSupressioOficial;
	private Date dataExtincioFuncional;
	private Date dataAnulacio;
	private String estat; // V: Vigente, E: Extinguido, A: Anulado, T: Transitorio
	private String codiPais;
	private String codiComunitat;
	private String codiProvincia;
	private String codiPostal;
	@JsonProperty("localidad")
	private String nomLocalitat;
	private String adressa;
	private Long tipusVia;
	private String nomVia;
	private String numVia;

	protected List<String> historicosUO;
	private List<UnitatOrganitzativa> lastHistoricosUnitats;


	@Override
	public int compareTo(UnitatOrganitzativa o) {
		return denominacio.compareToIgnoreCase(o.getDenominacio());
	}

	private static final long serialVersionUID = -5602898182576627524L;

}
