/**
 * 
 */
package es.caib.ripea.core.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.dto.CarpetaDto;
import es.caib.ripea.core.api.service.CarpetaService;

/**
 * Implementació de ContenidorService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class CarpetaServiceBean implements CarpetaService {

	@Autowired
	CarpetaService delegate;



	@Override
	@RolesAllowed("tothom")
	public CarpetaDto create(
			Long entitatId,
			Long contenidorId,
			String nom) {
		return delegate.create(
				entitatId,
				contenidorId,
				nom);
	}

	@Override
	@RolesAllowed("tothom")
	public CarpetaDto update(
			Long entitatId,
			Long id,
			String nom) {
		return delegate.update(
				entitatId,
				id,
				nom);
	}

	@Override
	@RolesAllowed("tothom")
	public CarpetaDto findById(
			Long entitatId,
			Long id) {
		return delegate.findById(entitatId, id);
	}

}
