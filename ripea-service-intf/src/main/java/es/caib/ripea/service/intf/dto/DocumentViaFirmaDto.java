package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DocumentViaFirmaDto extends DocumentEnviamentDto {

	private String titol;
	private String descripcio;
	private String codiDispositiu;
	private String lecturaObligatoria;
	private String messageCode;
	private ViaFirmaCallbackEstatEnumDto callbackEstat;
	private ViaFirmaTipusDestinatariEnum tipusDestinatari;
	private String codiUsuari;
	private String signantEmail;

	@Override
	public String getDestinatari() {
		return codiUsuari;
	}
	@Override
	public String getDestinatariAmbDocument() {
		return codiUsuari;
	}
}
