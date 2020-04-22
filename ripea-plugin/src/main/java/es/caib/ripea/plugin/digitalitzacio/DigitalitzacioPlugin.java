package es.caib.ripea.plugin.digitalitzacio;

import java.util.List;

import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.plugin.SistemaExternException;

/**
 * Plugin per a la integració amb portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DigitalitzacioPlugin {

	/**
	 * Recupera els perfils disponibles i els mostra amb l'idioma indicat.
	 * 
	 * @return Llistat dels perfils disponibles per l'usuari d'aplicació.
	 */
	public List<DigitalitzacioPerfil> recuperarPerfilsDisponibles(
			String idioma) throws SistemaExternException;
	
	/**
	 * Inicia el procés de digitalització mostrant el fomulari per escanejar documents.
	 * 
	 * @param codiPerfil
	 * 				El codi del perfil que s'ha seleccionat per iniciar l'escaneig.
	 * @param idioma
	 * 				El codi del perfil que s'ha seleccionat per iniciar l'escaneig.
	 * @param funcionari
	 * 				El usuari que realitza l'operació.
	 * @param urlReturn
	 * 				Url on es retornarà la cridada de Portafib. 
	 * @return Resposta de DigutalIB amb el id de la transacció.
	 */
	public DigitalitzacioTransaccioResposta iniciarProces(
			String codiPerfil,
			String idioma,
			UsuariDto funcionari,
			String returnUrl) throws SistemaExternException;
	
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
	public DigitalitzacioResultat recuperarResultat(
			String idTransaccio,
			boolean returnScannedFile,
			boolean returnSignedFile) throws SistemaExternException;

	/**
	 * Tanca un transacció.
	 * 
	 * @param transaccioId
	 * 				Id de la transacció.
	 */
	public void tancarTransaccio(
			String idTransaccio) throws SistemaExternException;
}
