/**
 * 
 */
package es.caib.ripea.core.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.ImportacioDto;
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
	 *            	Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param contingutId
	 * 				Id expedient pare.
	 * @param params
	 *            	Paràmetres per la importació.
	 * @return Número de documents ja importats prèviament, per llançar avís.
	 * 
	 * @throws ValidationException
	 *             Si el nom del contenidor conté caràcters invàlids.
	 */
	@PreAuthorize("hasRole('tothom')")
	public int importarDocuments(
			Long entitatId,
			Long contingutId,
			ImportacioDto params) throws ValidationException;
	/**
	 * Retorna la llista dels expedients on s'ha importat el document que s'intetna importar actualment
	 * 
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<DocumentDto> consultaExpedientsAmbImportacio();


}
