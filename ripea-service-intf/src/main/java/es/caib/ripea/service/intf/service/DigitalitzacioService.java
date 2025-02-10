/**
 * 
 */
package es.caib.ripea.service.intf.service;

import es.caib.ripea.service.intf.dto.DigitalitzacioPerfilDto;
import es.caib.ripea.service.intf.dto.DigitalitzacioResultatDto;
import es.caib.ripea.service.intf.dto.DigitalitzacioTransaccioRespostaDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Declaració dels mètodes comuns de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DigitalitzacioService {

	/**
	 * Recupera els perfils disponibles per un usuari d'aplicació.
	 * 
	 * @return La llista dels perfils.
	 */
	@PreAuthorize("hasRole('IPA_SUPER') or hasRole('IPA_ADMIN') or hasRole('tothom')")
	public List<DigitalitzacioPerfilDto> getPerfilsDisponibles();
	
	/**
	 * Inicia el procés de digitalització mostrant el fomulari per escanejar documents.
	 * 
	 * @param codiPerfil
	 * 				El codi del perfil que s'ha seleccionat per iniciar l'escaneig.
	 * @param urlReturn
	 * 				Url on es retornarà la cridada de Portafib. 
	 * @return Resposta de DigutalIB amb el id de la transacció.
	 */
	@PreAuthorize("hasRole('IPA_SUPER') or hasRole('IPA_ADMIN') or hasRole('tothom')")
	public DigitalitzacioTransaccioRespostaDto iniciarDigitalitzacio(
			String codiPerfil,
			String urlReturn);
	
	/**
	 * Recupera el resultat d'un escaneig.
	 * 
	 * @param idTransaccio
	 * 				Id de la transacció de la qual es vol recuperar el resultat.
	 * @param returnScannedFile
	 * 				Indica si s'ha escanejat un document sense firma. 
	 * @param returnSignedFile
	 * 				Indica si s'ha escanejat un document amb firma. 
	 * @return L'estat i el document escanejat.
	 */
	@PreAuthorize("hasRole('IPA_SUPER') or hasRole('IPA_ADMIN') or hasRole('tothom')")
	public DigitalitzacioResultatDto recuperarResultat(
			String idTransaccio,
			boolean returnScannedFile,
			boolean returnSignedFile);
	
	/**
	 * Tanca un transacció.
	 * 
	 * @param transaccioId
	 * 				Id de la transacció.
	 */
	@PreAuthorize("hasRole('IPA_SUPER') or hasRole('IPA_ADMIN') or hasRole('tothom')")
	public void tancarTransaccio(
			String idTransaccio);

}
