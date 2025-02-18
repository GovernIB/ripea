/**
 * 
 */
package es.caib.ripea.back.command;

import es.caib.ripea.back.helper.ConversioTipusHelper;
import es.caib.ripea.back.validation.CodiTipusNoRepetit;
import es.caib.ripea.service.intf.dto.TipusDocumentalDto;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

/**
 * Command per al manteniment de meta-documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@CodiTipusNoRepetit(campId = "id", campCodi = "codi", campEntitatId = "entitatId")
public class TipusDocumentalCommand {

	private Long id;
	@NotEmpty @Size(max=64)
	private String codi;
	@Size(max=64)
	private String codiEspecific;
	@NotEmpty @Size(max=256)
	private String nomEspanyol;
	@Size(max=256)
	private String nomCatala;
	private Long entitatId;
	

	public void setId(Long id) {
		this.id = id;
	}
	public void setCodi(String codi) {
		this.codi = Utils.trim(codi);
	}
	public void setCodiEspecific(String codiEspecific) {
		this.codiEspecific = Utils.trim(codiEspecific);
	}
	public void setNomEspanyol(String nomEspanyol) {
		this.nomEspanyol = Utils.trim(nomEspanyol); 
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
	public void setNomCatala(String nomCatala) {
		this.nomCatala = Utils.trim(nomCatala);
	}
	public static TipusDocumentalCommand asCommand(TipusDocumentalDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				TipusDocumentalCommand.class);
	}
	public static TipusDocumentalDto asDto(TipusDocumentalCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				TipusDocumentalDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
