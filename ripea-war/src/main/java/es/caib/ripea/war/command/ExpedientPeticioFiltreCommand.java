/**
 * 
 */
package es.caib.ripea.war.command;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.ripea.core.api.dto.ExpedientPeticioAccioEnumDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioEstatPendentDistribucioEnumDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioEstatViewEnumDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioFiltreDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import lombok.Getter;

@Getter
public class ExpedientPeticioFiltreCommand {

	private String procediment;
	private String numero;
	private String extracte;
	private String destinacioCodi;
	private ExpedientPeticioAccioEnumDto accioEnum;
	private Date dataInicial;
	private Date dataFinal;
	private ExpedientPeticioEstatViewEnumDto estat;
	private ExpedientPeticioEstatEnumDto estatAll;
	private Long metaExpedientId;
	private String procedimentCodi;
	private String interessat;
	
	private ExpedientPeticioEstatPendentDistribucioEnumDto estatPendentEnviarDistribucio;
	
	private boolean nomesPendentEnviarDistribucio;


	public void setMetaExpedientId(Long metaExpedientId) {
		this.metaExpedientId = metaExpedientId;
	}
	public void setAccioEnum(ExpedientPeticioAccioEnumDto accioEnum) {
		this.accioEnum = accioEnum;
	}
	public void setEstat(ExpedientPeticioEstatViewEnumDto estat) {
		this.estat = estat;
	}
	public void setProcediment(String procediment) {
		this.procediment =  StringUtils.trim(procediment);
	}
	public void setNumero(String numero) {
		this.numero = StringUtils.trim(numero);
	}
	public void setExtracte(String extracte) {
		this.extracte = StringUtils.trim(extracte);
	}
	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}
	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}
	public void setEstatPendentEnviarDistribucio(ExpedientPeticioEstatPendentDistribucioEnumDto estatPendentEnviarDistribucio) {
		this.estatPendentEnviarDistribucio = estatPendentEnviarDistribucio;
	}
	public void setNomesPendentEnviarDistribucio(boolean nomesPendentEnviarDistribucio) {
		this.nomesPendentEnviarDistribucio = nomesPendentEnviarDistribucio;
	}
	public void setDestinacioCodi(String destinacioCodi) {
		this.destinacioCodi = destinacioCodi;
	}
	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = StringUtils.trim(procedimentCodi);
	}
	public void setInteressat(String interessat) {
		this.interessat = StringUtils.trim(interessat);
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
	public void setEstatAll(
			ExpedientPeticioEstatEnumDto estatAll) {
		this.estatAll = estatAll;
	}

}
