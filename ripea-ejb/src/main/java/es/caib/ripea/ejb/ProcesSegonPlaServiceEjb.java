package es.caib.ripea.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.service.ProcesSegonPlaService;
import lombok.experimental.Delegate;

@Stateless
@RolesAllowed("**")
public class ProcesSegonPlaServiceEjb extends AbstractServiceEjb<ProcesSegonPlaService> implements ProcesSegonPlaService {

	@Delegate private ProcesSegonPlaService delegateService;

	protected void setDelegateService(ProcesSegonPlaService delegateService) {
		this.delegateService = delegateService;
	}

}
