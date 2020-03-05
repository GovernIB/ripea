/**
 * 
 */
package es.caib.ripea.core.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.DigitalitzacioPerfilDto;
import es.caib.ripea.core.api.dto.DigitalitzacioResultatDto;
import es.caib.ripea.core.api.dto.DigitalitzacioTransaccioRespostaDto;

/**
 * Declaració dels mètodes comuns de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DigitalitzacioService {

	/**
	 * Obté la versió actual de l'aplicació.
	 * 
	 * @return La versió actual.
	 */
	@PreAuthorize("hasRole('IPA_SUPER') or hasRole('IPA_ADMIN') or hasRole('tothom')")
	public List<DigitalitzacioPerfilDto> getPerfilsDisponibles();
	
	@PreAuthorize("hasRole('IPA_SUPER') or hasRole('IPA_ADMIN') or hasRole('tothom')")
	public DigitalitzacioTransaccioRespostaDto iniciarDigitalitzacio(
			String codiPerfil,
			String urlReturn);
	
	@PreAuthorize("hasRole('IPA_SUPER') or hasRole('IPA_ADMIN') or hasRole('tothom')")
	public DigitalitzacioResultatDto recuperarResultat(
			String idTransaccio,
			boolean returnScannedFile,
			boolean returnSignedFile);
	
	@PreAuthorize("hasRole('IPA_SUPER') or hasRole('IPA_ADMIN') or hasRole('tothom')")
	public void tancarTransaccio(
			String idTransaccio);

}
