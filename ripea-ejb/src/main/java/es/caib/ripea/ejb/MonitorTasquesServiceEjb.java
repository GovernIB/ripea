package es.caib.ripea.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.MonitorTascaInfo;
import es.caib.ripea.service.intf.service.MonitorTasquesService;
import lombok.experimental.Delegate;

@Stateless
public class MonitorTasquesServiceEjb extends AbstractServiceEjb<MonitorTasquesService> implements MonitorTasquesService {

	@Delegate private MonitorTasquesService delegateService;

	protected void setDelegateService(MonitorTasquesService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("**")
	public MonitorTascaInfo addTasca(String codiTasca) {
		return delegateService.addTasca(codiTasca);
	}

	@Override
	@RolesAllowed("**")
	public List<MonitorTascaInfo> findAll() {
		return delegateService.findAll();
	}

	@Override
	@RolesAllowed("**")
	public MonitorTascaInfo findByCodi(String codi) {
		return delegateService.findByCodi(codi);
	}

	@Override
	@RolesAllowed("**")
	public void updateProperaExecucio(String codi, Long plusValue) {
		delegateService.updateProperaExecucio(codi, plusValue);
	}

	@Override
	@RolesAllowed("**")
	public void inici(String codiTasca) {
		delegateService.inici(codiTasca);
	}

	@Override
	@RolesAllowed("**")
	public void fi(String codiTasca) {
		delegateService.fi(codiTasca);
	}

	@Override
	@RolesAllowed("**")
	public void error(String codiTasca, String error) {
		delegateService.error(codiTasca, error);
	}

	@Override
	@RolesAllowed("**")
	public void reiniciarTasquesEnSegonPla(String codiTasca) {
		delegateService.reiniciarTasquesEnSegonPla(codiTasca);
	}

}
