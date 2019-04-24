/**
 * 
 */
package es.caib.ripea.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;
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
	public List<DocumentDto> getDocuments(
			Long entitatId, 
			Long contingutId, 
			String numeroRegistre)
			throws NotFoundException, ValidationException {
		return delegate.getDocuments(
				entitatId, 
				contingutId,
				numeroRegistre);
	}

	
}
