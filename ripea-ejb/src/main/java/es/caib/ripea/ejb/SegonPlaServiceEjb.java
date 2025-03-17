/**
 * 
 */
package es.caib.ripea.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.service.SegonPlaService;
import lombok.experimental.Delegate;

@Stateless
public class SegonPlaServiceEjb extends AbstractServiceEjb<SegonPlaService> implements SegonPlaService {

	@Delegate private SegonPlaService delegateService;

	protected void setDelegateService(SegonPlaService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("**")
	public void consultarIGuardarAnotacionsPeticionsPendents() throws Throwable {
		delegateService.consultarIGuardarAnotacionsPeticionsPendents();
	}

	@Override
	@RolesAllowed("**")
	public void buidarCacheDominis() {
		delegateService.buidarCacheDominis();
	}

	@Override
	@RolesAllowed("**")
	public void enviarEmailsPendentsAgrupats() {
		delegateService.enviarEmailsPendentsAgrupats();
	}

	@Override
	@RolesAllowed("**")
	public void testEmailsAgrupats() {
		delegateService.testEmailsAgrupats();
	}

	@Override
	@RolesAllowed("**")
	public void guardarExpedientsDocumentsArxiu() {
		delegateService.guardarExpedientsDocumentsArxiu();
	}

	@Override
	@RolesAllowed("**")
	public void guardarInteressatsArxiu() {
		delegateService.guardarInteressatsArxiu();
	}

    @Override
    @RolesAllowed("**")
    public void actualitzarProcediments() {
        delegateService.actualitzarProcediments();
    }

    @Override
    @RolesAllowed("**")
    public void consultaCanvisOrganigrama() {
        delegateService.consultaCanvisOrganigrama();
    }
    
	@Override
	@RolesAllowed("**")
	public void reintentarCanviEstatDistribucio() {
		delegateService.reintentarCanviEstatDistribucio();
	}

	@Override
	@RolesAllowed("**")
	public void enviarEmailPerComentariMetaExpedient() {
		delegateService.enviarEmailPerComentariMetaExpedient();
		
	}

	@Override
	@RolesAllowed("**")
	public void restartSchedulledTasks(String taskCodi) {
		delegateService.restartSchedulledTasks(taskCodi);
	}

	@Override
	@RolesAllowed("**")
	public void tancarExpedientsArxiu() {
		delegateService.tancarExpedientsArxiu();
	}
}