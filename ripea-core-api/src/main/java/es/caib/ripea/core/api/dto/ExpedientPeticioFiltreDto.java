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
	private ExpedientPeticioEstatViewEnumDto estat;
	private Long metaExpedientId;

	private ExpedientPeticioEstatPendentDistribucioEnumDto estatPendentEnviarDistribucio;
	private boolean nomesPendentEnviarDistribucio;
	
	public Long getMetaExpedientId() {
		return metaExpedientId;
	}

	public void setMetaExpedientId(Long metaExpedientId) {
		this.metaExpedientId = metaExpedientId;
	}

	public ExpedientPeticioAccioEnumDto getAccioEnum() {
		return accioEnum;
	}

	public void setAccioEnum(ExpedientPeticioAccioEnumDto accioEnum) {
		this.accioEnum = accioEnum;
	}

	public ExpedientPeticioEstatViewEnumDto getEstat() {
		return estat;
	}

	public void setEstat(ExpedientPeticioEstatViewEnumDto estat) {
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

	public ExpedientPeticioEstatPendentDistribucioEnumDto getEstatPendentEnviarDistribucio() {
		return estatPendentEnviarDistribucio;
	}

	public void setEstatPendentEnviarDistribucio(ExpedientPeticioEstatPendentDistribucioEnumDto estatPendentEnviarDistribucio) {
		this.estatPendentEnviarDistribucio = estatPendentEnviarDistribucio;
	}

	public boolean isNomesPendentEnviarDistribucio() {
		return nomesPendentEnviarDistribucio;
	}

	public void setNomesPendentEnviarDistribucio(boolean nomesPendentEnviarDistribucio) {
		this.nomesPendentEnviarDistribucio = nomesPendentEnviarDistribucio;
	}


	private static final long serialVersionUID = -139254994389509932L;

}
