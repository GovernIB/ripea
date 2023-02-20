/**
 * 
 */
package es.caib.ripea.war.command;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.dto.PinbalConsentimentEnumDto;
import es.caib.ripea.core.api.dto.PinbalConsultaDto;
import es.caib.ripea.core.api.dto.SexeEnumDto;
import es.caib.ripea.core.api.dto.SiNoEnumDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per a la gestió de les peticions a PINBAL.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
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
	private String municipiCodi;
	private String dataConsulta;
	private String dataNaixement;
	private SiNoEnumDto consentimentTipusDiscapacitat;
	private String numeroTitol;
	
	
	private String codiNacionalitat = "724";
	private String paisNaixament = "724";
	private String provinciaNaixament;
	private String poblacioNaixament;
	private String codiPoblacioNaixament;
	private String municipiNaixament;
	private SexeEnumDto sexe;
	private String nomPare;
	private String nomMare;
	private String dataNaixementObligatori;
	private String telefon;
	private String email;
	
	
	public static PinbalConsultaDto asDto(PinbalConsultaCommand command) {
		PinbalConsultaDto dto = ConversioTipusHelper.convertir(
				command,
				PinbalConsultaDto.class);
		return dto;
	}

}
