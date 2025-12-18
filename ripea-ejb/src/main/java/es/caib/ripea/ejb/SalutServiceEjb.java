package es.caib.ripea.ejb;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.comanda.model.v1.salut.ContextInfo;
import es.caib.comanda.model.v1.salut.IntegracioInfo;
import es.caib.comanda.model.v1.salut.SalutInfo;
import es.caib.comanda.model.v1.salut.SubsistemaInfo;
import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.service.SalutService;
import lombok.experimental.Delegate;

@Stateless
@RolesAllowed("**")
public class SalutServiceEjb extends AbstractServiceEjb<SalutService> implements SalutService {

	@Delegate private SalutService delegateService;

	protected void setDelegateService(SalutService delegateService) {
		this.delegateService = delegateService;
	}
	
	@Override
	@PermitAll
	public List<IntegracioInfo> getIntegracions() {
		return delegateService.getIntegracions();
	}

	@Override
	@PermitAll
	public List<SubsistemaInfo> getSubsistemes() {
		return delegateService.getSubsistemes();
	}

	@Override
	@PermitAll
	public List<ContextInfo> getContexts(String baseUrl) {
		return delegateService.getContexts(baseUrl);
	}

	@Override
	@PermitAll
	public SalutInfo checkSalut(String versio, String performanceUrl) {
		return delegateService.checkSalut(versio, performanceUrl);
	}
}