/**
 * 
 */
package es.caib.ripea.war.command;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.dto.PinbalConsentimentEnumDto;
import es.caib.ripea.core.api.dto.PinbalConsultaDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;

/**
 * Command per a la gestió de les peticions a PINBAL.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PinbalConsultaCommand {

	@NotNull
	protected Long entitatId;
	@NotNull
	protected Long pareId;
	@NotNull
	private Long metaDocumentId;
	@NotNull
	private Long interessatId;
	@NotEmpty
	private String finalitat;
	@NotNull
	private PinbalConsentimentEnumDto consentiment;
	private String comunitatAutonomaCodi;
	private String provinciaCodi;

	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
	public Long getPareId() {
		return pareId;
	}
	public void setPareId(Long pareId) {
		this.pareId = pareId;
	}
	public Long getMetaDocumentId() {
		return metaDocumentId;
	}
	public void setMetaDocumentId(Long metaDocumentId) {
		this.metaDocumentId = metaDocumentId;
	}
	public Long getInteressatId() {
		return interessatId;
	}
	public void setInteressatId(Long interessatId) {
		this.interessatId = interessatId;
	}
	public String getFinalitat() {
		return finalitat;
	}
	public void setFinalitat(String finalitat) {
		this.finalitat = finalitat;
	}
	public PinbalConsentimentEnumDto getConsentiment() {
		return consentiment;
	}
	public void setConsentiment(PinbalConsentimentEnumDto consentiment) {
		this.consentiment = consentiment;
	}
	public String getComunitatAutonomaCodi() {
		return comunitatAutonomaCodi;
	}
	public void setComunitatAutonomaCodi(String comunitatAutonomaCodi) {
		this.comunitatAutonomaCodi = comunitatAutonomaCodi;
	}
	public String getProvinciaCodi() {
		return provinciaCodi;
	}
	public void setProvinciaCodi(String provinciaCodi) {
		this.provinciaCodi = provinciaCodi;
	}

	public static PinbalConsultaDto asDto(PinbalConsultaCommand command) {
		PinbalConsultaDto dto = ConversioTipusHelper.convertir(
				command,
				PinbalConsultaDto.class);
		return dto;
	}

}
