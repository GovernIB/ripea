/**
 * 
 */
package es.caib.ripea.core.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.service.SegonPlaService;

/**
 * Implementació de SegonPlaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class SegonPlaServiceBean implements SegonPlaService {

	@Autowired
	SegonPlaService delegate;


	@Override
	@RolesAllowed("tothom")
	public void consultarIGuardarAnotacionsPeticionsPendents() {
		delegate.consultarIGuardarAnotacionsPeticionsPendents();	
	}

	@Override
	@RolesAllowed("tothom")
	public void buidarCacheDominis() {
		delegate.buidarCacheDominis();
	}

}