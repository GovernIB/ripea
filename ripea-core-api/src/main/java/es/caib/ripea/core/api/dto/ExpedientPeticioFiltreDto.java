/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació del filtre d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientPeticioFiltreDto implements Serializable {

	private String procediment;
	private String numero;
	private String extracte;
	private String destinacio;
	private ExpedientPeticioAccioEnumDto accioEnum;
	private Date dataInicial;
	private Date dataFinal;
	private ExpedientPeticioEstatFiltreEnumDto estat;



	public ExpedientPeticioAccioEnumDto getAccioEnum() {
		return accioEnum;
	}

	public void setAccioEnum(ExpedientPeticioAccioEnumDto accioEnum) {
		this.accioEnum = accioEnum;
	}

	public ExpedientPeticioEstatFiltreEnumDto getEstat() {
		return estat;
	}

	public void setEstat(ExpedientPeticioEstatFiltreEnumDto estat) {
		this.estat = estat;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String getProcediment() {
		return procediment;
	}

	public void setProcediment(String procediment) {
		this.procediment = procediment;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getExtracte() {
		return extracte;
	}

	public void setExtracte(String extracte) {
		this.extracte = extracte;
	}

	public String getDestinacio() {
		return destinacio;
	}

	public void setDestinacio(String destinacio) {
		this.destinacio = destinacio;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	private static final long serialVersionUID = -139254994389509932L;

}