/**
 * 
 */
package es.caib.ripea.war.command;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.dto.ViaFirmaDispositiuDto;
import es.caib.ripea.core.api.dto.ViaFirmaEnviarDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per a enviar documents al portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ViaFirmaEnviarCommand {

	@Size(max=256)
	private String titol;
	@Size(max=256)
	private String descripcio;
	@NotEmpty
	private String codiUsuariViaFirma;
	private String codisUsuariViaFirma;
	@NotEmpty
	private String dispositiuViaFirma;
	private Long interessatId;
	@NotEmpty
	private String signantNif;
	@NotEmpty
	private String signantNom;
	@Size(max=256)
	private String observacions;
	
	private String firmaParcial;
	
	public static ViaFirmaEnviarDto asDto(ViaFirmaEnviarCommand command) {
		ViaFirmaEnviarDto viaFirmaEnviar = ConversioTipusHelper.convertir(
				command,
				ViaFirmaEnviarDto.class);
		viaFirmaEnviar.setFirmaParcial(Boolean.parseBoolean(command.getFirmaParcial()));
		
		String dispositiuViaFirmaFormatted = command.getDispositiuViaFirma();
		if (dispositiuViaFirmaFormatted != null && !dispositiuViaFirmaFormatted.isEmpty()) {
			String [] dispositiuViaFirma = dispositiuViaFirmaFormatted.split("\\|");
			
			ViaFirmaDispositiuDto dispositiuViaFirmaDto = new ViaFirmaDispositiuDto();
			dispositiuViaFirmaDto.setCodi(dispositiuViaFirma[0]);
			dispositiuViaFirmaDto.setCodiUsuari(dispositiuViaFirma[1]);
			dispositiuViaFirmaDto.setCodiAplicacio(dispositiuViaFirma[2]);
			dispositiuViaFirmaDto.setDescripcio(dispositiuViaFirma[3]);
			dispositiuViaFirmaDto.setLocal(dispositiuViaFirma[4]);
			dispositiuViaFirmaDto.setEstat(dispositiuViaFirma[5]);
			dispositiuViaFirmaDto.setToken(dispositiuViaFirma[6]);
			dispositiuViaFirmaDto.setIdentificador(dispositiuViaFirma[7]);
			dispositiuViaFirmaDto.setTipus(dispositiuViaFirma[8]);
			dispositiuViaFirmaDto.setEmailUsuari(dispositiuViaFirma[9]);
			viaFirmaEnviar.setViaFirmaDispositiu(dispositiuViaFirmaDto);
		}
		return viaFirmaEnviar;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
