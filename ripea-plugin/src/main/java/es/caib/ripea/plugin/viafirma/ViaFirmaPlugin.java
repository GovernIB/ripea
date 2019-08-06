/**
 * 
 */
package es.caib.ripea.plugin.viafirma;

import java.util.List;

import es.caib.ripea.plugin.SistemaExternException;


/**
 * Interfície per enviar els document al sistema viaFirma
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ViaFirmaPlugin {

	public ViaFirmaResponse uploadDocument(ViaFirmaParams parametresViaFirma) throws SistemaExternException;
	
	public ViaFirmaDocument downloadDocument(String codiUsuari, String contrasenya, String messageCode) throws SistemaExternException;
	
	public List<ViaFirmaDispositiu> getDeviceUser(String codiUsuari, String contrasenya) throws SistemaExternException;
	
}
