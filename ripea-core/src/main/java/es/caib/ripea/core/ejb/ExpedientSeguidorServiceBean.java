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

import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.service.ExpedientSeguidorService;

/**
 * Implementació de ExpedientSeguidorService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ExpedientSeguidorServiceBean implements ExpedientSeguidorService {

	@Autowired
	ExpedientSeguidorService delegate;
	
	@Override
	@RolesAllowed("tothom")
	public void follow(Long entitatId, Long expedientId) {
		delegate.follow(
				entitatId, 
				expedientId);
	}

	@Override
	@RolesAllowed("tothom")
	public void unfollow(Long entitatId, Long expedientId) {
		delegate.unfollow(
				entitatId,
				expedientId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<UsuariDto> getFollowersExpedient(Long entitatId, Long expedientId) {
		return delegate.getFollowersExpedient(
				entitatId, 
				expedientId);
	}

}
