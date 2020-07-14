/**
 * 
 */
package es.caib.ripea.core.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.UsuariDto;

/**
 * Declaració dels mètodes per a gestionar els interessats dels
 * expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientSeguidorService {

	/**
	 * Afegeix l'usuari actual com a seguidor de l'expedient indicat per paràmetre.
	 * 
	 * @param entitatId
	 *            L'identificador de l'entitat actual.
	 * @param expedientId
	 *            L'identificador de l'expedient a seguir.
	 * @return el llistat de municipis.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void follow(
			Long entitatId,
			Long expedientId);
	
	/**
	 * Esborra l'usuari actual de la llista de seguidors de l'expedient.
	 * 
	 * @param entitatId
	 *            L'identificador de l'entitat actual.
	 * @param expedientId
	 *            L'identificador de l'expedient a seguir.
	 * @return el llistat de municipis.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void unfollow(
			Long entitatId,
			Long expedientId);
	
	/**
	 * Afegeix l'usuari actual com a seguidor de l'expedient indicat per paràmetre.
	 * 
	 * @param entitatId
	 *            L'identificador de l'entitat actual.
	 * @param expedientId
	 *            L'identificador de l'expedient a seguir.
	 * @return el llistat de municipis.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<UsuariDto> getFollowersExpedient(
			Long entitatId,
			Long expedientId);
}
