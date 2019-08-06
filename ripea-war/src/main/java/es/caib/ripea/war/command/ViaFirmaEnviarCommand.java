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

/**
 * Command per a enviar documents al portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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

	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public String getCodiUsuariViaFirma() {
		return codiUsuariViaFirma;
	}
	public void setCodiUsuariViaFirma(String codiUsuariViaFirma) {
		this.codiUsuariViaFirma = codiUsuariViaFirma;
	}
	public String getCodisUsuariViaFirma() {
		return codisUsuariViaFirma;
	}
	public void setCodisUsuariViaFirma(String codisUsuariViaFirma) {
		this.codisUsuariViaFirma = codisUsuariViaFirma;
	}
	public String getDispositiuViaFirma() {
		return dispositiuViaFirma;
	}
	public void setDispositiuViaFirma(String dispositiuViaFirma) {
		this.dispositiuViaFirma = dispositiuViaFirma;
	}
	
	public static ViaFirmaEnviarDto asDto(ViaFirmaEnviarCommand command) {
		ViaFirmaEnviarDto viaFirmaEnviar = ConversioTipusHelper.convertir(
				command,
				ViaFirmaEnviarDto.class);
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
