package es.caib.ripea.ejb;

import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.DocumentDto;
import es.caib.ripea.service.intf.dto.ImportacioDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.ImportacioService;
import lombok.experimental.Delegate;

@Stateless
@RolesAllowed("**")
public class ImportacioServiceEjb extends AbstractServiceEjb<ImportacioService> implements ImportacioService {

	@Delegate private ImportacioService delegateService;

	protected void setDelegateService(ImportacioService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("**")
	public int importarDocuments(Long entitatId, Long contingutId, ImportacioDto dades) throws NotFoundException {
		return delegateService.importarDocuments(
				entitatId, 
				contingutId,
				dades);
	}

	@Override
	@RolesAllowed("**")
	public List<DocumentDto> consultaExpedientsAmbImportacio() {
		return delegateService.consultaExpedientsAmbImportacio();
	}

	@Override
	@RolesAllowed("**")
	public Map<String, String> consultaDocumentsWithExpedient() {
		return delegateService.consultaDocumentsWithExpedient();
	}

	
}
