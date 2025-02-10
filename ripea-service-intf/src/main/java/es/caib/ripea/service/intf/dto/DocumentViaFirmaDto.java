/**
 * 
 */
package es.caib.ripea.service.intf.dto;

/**
 * Informació de l'enviament d'un document a viaFirma.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentViaFirmaDto extends DocumentEnviamentDto {

	private String codiUsuari;
	private String titol;
	private String descripcio;
	private String codiDispositiu;
	private String lecturaObligatoria;
	private String messageCode;
	private ViaFirmaCallbackEstatEnumDto callbackEstat;

	public String getCodiUsuari() {
		return codiUsuari;
	}
	public void setCodiUsuari(String codiUsuari) {
		this.codiUsuari = codiUsuari;
	}
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
	public String getCodiDispositiu() {
		return codiDispositiu;
	}
	public void setCodiDispositiu(String codiDispositiu) {
		this.codiDispositiu = codiDispositiu;
	}
	public String getLecturaObligatoria() {
		return lecturaObligatoria;
	}
	public void setLecturaObligatoria(String lecturaObligatoria) {
		this.lecturaObligatoria = lecturaObligatoria;
	}
	public String getMessageCode() {
		return messageCode;
	}
	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}
	public ViaFirmaCallbackEstatEnumDto getCallbackEstat() {
		return callbackEstat;
	}
	public void setCallbackEstat(ViaFirmaCallbackEstatEnumDto callbackEstat) {
		this.callbackEstat = callbackEstat;
	}
	@Override
	public String getDestinatari() {
		return codiUsuari;
	}
	@Override
	public String getDestinatariAmbDocument() {
		return codiUsuari;
	}
}
