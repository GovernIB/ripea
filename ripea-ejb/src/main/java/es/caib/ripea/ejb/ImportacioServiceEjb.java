/**
 * 
 */
package es.caib.ripea.ejb;

import es.caib.ripea.service.intf.dto.DocumentDto;
import es.caib.ripea.service.intf.dto.ImportacioDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.ImportacioService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 * Implementació de ContenidorService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ImportacioServiceEjb implements ImportacioService {

	@Delegate
	private ImportacioService delegateService;

	protected void setDelegateService(ImportacioService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("tothom")
	public int importarDocuments(Long entitatId, Long contingutId, ImportacioDto dades) throws NotFoundException {
		return delegateService.importarDocuments(
				entitatId, 
				contingutId,
				dades);
	}

	@Override
	@RolesAllowed("tothom")
	public List<DocumentDto> consultaExpedientsAmbImportacio() {
		return delegateService.consultaExpedientsAmbImportacio();
	}

	@Override
	@RolesAllowed("tothom")
	public Map<String, String> consultaDocumentsWithExpedient() {
		return delegateService.consultaDocumentsWithExpedient();
	}

	
}
