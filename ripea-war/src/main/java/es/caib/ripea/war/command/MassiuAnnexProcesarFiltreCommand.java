/**
 * 
 */
package es.caib.ripea.war.command;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.ripea.core.api.dto.MassiuAnnexEstatProcessamentEnumDto;
import es.caib.ripea.core.api.dto.MassiuAnnexProcesarFiltreDto;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import lombok.Getter;

@Getter
public class MassiuAnnexProcesarFiltreCommand {

	private String nom;
	private String numero;
	private Date dataInici;
	private Date dataFi;	
	private MassiuAnnexEstatProcessamentEnumDto estatProcessament;
	private Long metaExpedientId;
	private Long expedientId;
	
	
	public static MassiuAnnexProcesarFiltreCommand asCommand(MassiuAnnexProcesarFiltreDto dto) {
		MassiuAnnexProcesarFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				MassiuAnnexProcesarFiltreCommand.class);
		return command;
	}
	public static MassiuAnnexProcesarFiltreDto asDto(MassiuAnnexProcesarFiltreCommand command) {
		MassiuAnnexProcesarFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				MassiuAnnexProcesarFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public void setNom(String nom) {
		this.nom = Utils.trim(nom);
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}
	public void setDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}

	public void setNumero(String numero) {
		this.numero = Utils.trim(numero);
	}

	public void setEstatProcessament(MassiuAnnexEstatProcessamentEnumDto estatProcessament) {
		this.estatProcessament = estatProcessament;
	}
	public void setMetaExpedientId(Long metaExpedientId) {
		this.metaExpedientId = metaExpedientId;
	}
	
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	
	
}
