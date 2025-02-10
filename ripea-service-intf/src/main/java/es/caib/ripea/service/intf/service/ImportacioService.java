/**
 * 
 */
package es.caib.ripea.service.intf.service;

import es.caib.ripea.service.intf.dto.DocumentDto;
import es.caib.ripea.service.intf.dto.ImportacioDto;
import es.caib.ripea.service.intf.exception.ValidationException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;

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
	
	@PreAuthorize("hasRole('tothom')")
	public Map<String, String> consultaDocumentsWithExpedient();


}
