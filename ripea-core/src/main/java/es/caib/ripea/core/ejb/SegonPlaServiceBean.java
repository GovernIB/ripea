/**
 * 
 */
package es.caib.ripea.core.ejb;

import es.caib.ripea.core.api.service.SegonPlaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

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
	public void consultarIGuardarAnotacionsPeticionsPendents() throws Throwable {
		delegate.consultarIGuardarAnotacionsPeticionsPendents();	
	}

	@Override
	@RolesAllowed("tothom")
	public void buidarCacheDominis() {
		delegate.buidarCacheDominis();
	}

	@Override
	@RolesAllowed("tothom")
	public void enviarEmailsPendentsAgrupats() {
		delegate.enviarEmailsPendentsAgrupats();
	}

	@Override
	public void testEmailsAgrupats() {
		delegate.testEmailsAgrupats();
	}

	@Override
	@RolesAllowed("tothom")
	public void guardarExpedientsDocumentsArxiu() {
		delegate.guardarExpedientsDocumentsArxiu();		
	}

	@Override
	@RolesAllowed("tothom")
	public void guardarInteressatsArxiu() {
		delegate.guardarInteressatsArxiu();
	}

    @Override
    public void actualitzarProcediments() {
        delegate.actualitzarProcediments();
    }

    @Override
    public void consultaCanvisOrganigrama() {
        delegate.consultaCanvisOrganigrama();
    }
    
	@Override
	public void reintentarCanviEstatDistribucio() {
		delegate.reintentarCanviEstatDistribucio();
	}

	@Override
	public void enviarEmailPerComentariMetaExpedient() {
		delegate.enviarEmailPerComentariMetaExpedient();
		
	}

	@Override
	public void restartSchedulledTasks() {
		delegate.restartSchedulledTasks();		
	}

	@Override
	public void tancarExpedientsArxiu() {
		delegate.tancarExpedientsArxiu();
	}

}