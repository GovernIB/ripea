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
import es.caib.ripea.war.validation.ViaFirma;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per a enviar documents al portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@ViaFirma
public class ViaFirmaEnviarCommand {

	@Size(max=256)
	private String titol;
	@Size(max=256)
	private String descripcio;
	@NotEmpty
	private String codiUsuariViaFirma;
	private String codisUsuariViaFirma;
	private String dispositiuViaFirma;
	private Long interessatId;
	@NotEmpty
	private String signantNif;
	@NotEmpty
	private String signantNom;
	@Size(max=256)
	private String observacions;
	
	private String firmaParcial;
	private Boolean validateCodeEnabled;
	private String validateCode;
	
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

	public void setCodiUsuariViaFirma(String codiUsuariViaFirma) {
		this.codiUsuariViaFirma = codiUsuariViaFirma != null ? codiUsuariViaFirma.trim() : null;
	}

	public void setCodisUsuariViaFirma(String codisUsuariViaFirma) {
		this.codisUsuariViaFirma = codisUsuariViaFirma != null ? codisUsuariViaFirma.trim() : null;
	}

	public void setDispositiuViaFirma(String dispositiuViaFirma) {
		this.dispositiuViaFirma = dispositiuViaFirma != null ? dispositiuViaFirma.trim() : null;
	}
	public String getDispositiuViaFirma() {
		return dispositiuViaFirma;
	}
	public void setSignantNif(String signantNif) {
		this.signantNif = signantNif != null ? signantNif.trim() : null;
	}

	public void setSignantNom(String signantNom) {
		this.signantNom = signantNom != null ? signantNom.trim() : null;
	}

	public void setObservacions(String observacions) {
		this.observacions = observacions != null ? observacions.trim() : null;
	}
	public void setFirmaParcial(String firmaParcial) {
		this.firmaParcial = firmaParcial != null ? firmaParcial.trim() : null;
	}
	public Boolean isValidateCodeEnabled() {
		return validateCodeEnabled;
	}
	
}
