package es.caib.ripea.war.command;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import es.caib.ripea.core.api.dto.SeguimentArxiuPendentsFiltreDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
public class SeguimentArxiuPendentsFiltreCommand {


	private String elementNom;
	private Long expedientId;
	private Long metaExpedientId;
	private Date dataCreacioInici;
	private Date dataCreacioFi;
	
	
	public static SeguimentArxiuPendentsFiltreCommand asCommand(SeguimentArxiuPendentsFiltreDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				SeguimentArxiuPendentsFiltreCommand.class);
	}
	
	public static SeguimentArxiuPendentsFiltreDto asDto(SeguimentArxiuPendentsFiltreCommand command){
		SeguimentArxiuPendentsFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				SeguimentArxiuPendentsFiltreDto.class);
		return dto;
	}

	public void setElementNom(String elementNom) {
		this.elementNom = StringUtils.trim(elementNom);
	}
	public void setMetaExpedientId(Long metaExpedientId) {
		this.metaExpedientId = metaExpedientId;
	}
	
	
	
	public String getElementNom2() {
		return elementNom;
	}
	public void setElementNom2(String elementNom) {
		this.elementNom = StringUtils.trim(elementNom);
	}
	public Long getExpedientId2() {
		return expedientId;
	}
	public void setExpedientId2(Long expedientId) {
		this.expedientId = expedientId;
	}
	public Long getMetaExpedientId2() {
		return metaExpedientId;
	}
	public void setMetaExpedientId2(Long metaExpedientId) {
		this.metaExpedientId = metaExpedientId;
	}
	public String getElementNom3() {
		return elementNom;
	}
	public void setElementNom3(String elementNom) {
		this.elementNom = StringUtils.trim(elementNom);
	}
	public Long getExpedientId3() {
		return expedientId;
	}
	public void setExpedientId3(Long expedientId) {
		this.expedientId = expedientId;
	}
	public Long getMetaExpedientId3() {
		return metaExpedientId;
	}
	public void setMetaExpedientId3(Long metaExpedientId) {
		this.metaExpedientId = metaExpedientId;
	}

	public void setDataCreacioInici(Date dataCreacioInici) {
		this.dataCreacioInici = dataCreacioInici;
	}

	public void setDataCreacioFi(
			Date dataCreacioFi) {this.dataCreacioFi = dataCreacioFi;
	}

	

}
