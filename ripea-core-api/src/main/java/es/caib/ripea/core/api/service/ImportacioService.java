/**
 * 
 */
package es.caib.ripea.core.api.service;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;

/**
 * Declaració dels mètodes per a gestionar contenidors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ImportacioService {

	/**
	 * Obté una llista de documents desde l'arxiu donat un número de registre.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param numeroRegistre
	 *            número registre dels documents que es volen importar desde l'arxiu.
	 * @return Llista de documents trobats.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws ValidationException
	 *             Si el nom del contenidor conté caràcters invàlids.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<DocumentDto> getDocuments(
			Long entitatId,
			Long contingutId,
			String numeroRegistre) throws NotFoundException, ValidationException;


}
