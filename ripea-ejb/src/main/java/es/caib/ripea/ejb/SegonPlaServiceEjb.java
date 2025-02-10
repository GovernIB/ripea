/**
 * 
 */
package es.caib.ripea.ejb;

import es.caib.ripea.service.intf.service.SegonPlaService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * Implementació de SegonPlaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class SegonPlaServiceEjb implements SegonPlaService {

	@Delegate
	private SegonPlaService delegateService;

	protected void delegate(SegonPlaService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("tothom")
	public void consultarIGuardarAnotacionsPeticionsPendents() throws Throwable {
		delegateService.consultarIGuardarAnotacionsPeticionsPendents();
	}

	@Override
	@RolesAllowed("tothom")
	public void buidarCacheDominis() {
		delegateService.buidarCacheDominis();
	}

	@Override
	@RolesAllowed("tothom")
	public void enviarEmailsPendentsAgrupats() {
		delegateService.enviarEmailsPendentsAgrupats();
	}

	@Override
	public void testEmailsAgrupats() {
		delegateService.testEmailsAgrupats();
	}

	@Override
	@RolesAllowed("tothom")
	public void guardarExpedientsDocumentsArxiu() {
		delegateService.guardarExpedientsDocumentsArxiu();
	}

	@Override
	@RolesAllowed("tothom")
	public void guardarInteressatsArxiu() {
		delegateService.guardarInteressatsArxiu();
	}

    @Override
    public void actualitzarProcediments() {
        delegateService.actualitzarProcediments();
    }

    @Override
    public void consultaCanvisOrganigrama() {
        delegateService.consultaCanvisOrganigrama();
    }
    
	@Override
	public void reintentarCanviEstatDistribucio() {
		delegateService.reintentarCanviEstatDistribucio();
	}

	@Override
	public void enviarEmailPerComentariMetaExpedient() {
		delegateService.enviarEmailPerComentariMetaExpedient();
		
	}

	@Override
	public void restartSchedulledTasks(String taskCodi) {
		delegateService.restartSchedulledTasks(taskCodi);
	}

	@Override
	public void tancarExpedientsArxiu() {
		delegateService.tancarExpedientsArxiu();
	}

}