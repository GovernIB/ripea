/**
 * 
 */
package es.caib.ripea.war.command;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.ripea.core.api.dto.ExpedientPeticioAccioEnumDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioEstatPendentDistribucioEnumDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioEstatViewEnumDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioFiltreDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;

/**
 * Command per al filtre d'expedients dels arxius.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientPeticioFiltreCommand {

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
	public String getProcediment() {
		return procediment;
	}
	public void setProcediment(String procediment) {
		this.procediment = procediment != null ? procediment.trim() : null;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero != null ? numero.trim() : null;
	}
	public String getExtracte() {
		return extracte;
	}
	public void setExtracte(String extracte) {
		this.extracte = extracte != null ? extracte.trim() : null;
	}
	public String getDestinacio() {
		return destinacio;
	}
	public void setDestinacio(String destinacio) {
		this.destinacio = destinacio != null ? destinacio.trim() : null;
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
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	public static ExpedientPeticioFiltreCommand asCommand(ExpedientPeticioFiltreDto dto) {
		return ConversioTipusHelper.convertir(dto,
				ExpedientPeticioFiltreCommand.class);
	}
	public static ExpedientPeticioFiltreDto asDto(ExpedientPeticioFiltreCommand command) {
		return ConversioTipusHelper.convertir(command,
				ExpedientPeticioFiltreDto.class);
	}

}
