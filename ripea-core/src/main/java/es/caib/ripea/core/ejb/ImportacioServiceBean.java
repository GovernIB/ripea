/**
 * 
 */
package es.caib.ripea.core.ejb;

import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.ImportacioDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.ImportacioService;

/**
 * Implementació de ContenidorService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ImportacioServiceBean implements ImportacioService {

	@Autowired
	ImportacioService delegate;

	@Override
	@RolesAllowed("tothom")
	public int importarDocuments(Long entitatId, Long contingutId, ImportacioDto dades) throws NotFoundException {
		return delegate.importarDocuments(
				entitatId, 
				contingutId,
				dades);
	}

	@Override
	@RolesAllowed("tothom")
	public List<DocumentDto> consultaExpedientsAmbImportacio() {
		return delegate.consultaExpedientsAmbImportacio();
	}

	@Override
	@RolesAllowed("tothom")
	public Map<String, String> consultaDocumentsWithExpedient() {
		return delegate.consultaDocumentsWithExpedient();
	}

	
}
