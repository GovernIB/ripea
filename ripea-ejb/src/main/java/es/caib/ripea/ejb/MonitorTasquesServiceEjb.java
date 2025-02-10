package es.caib.ripea.ejb;

import es.caib.ripea.service.intf.dto.MonitorTascaInfo;
import es.caib.ripea.service.intf.service.MonitorTasquesService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class MonitorTasquesServiceEjb implements MonitorTasquesService {

	@Delegate
	private MonitorTasquesService delegateService;

	protected void setDelegateService(MonitorTasquesService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	public MonitorTascaInfo addTasca(String codiTasca) {
		return delegateService.addTasca(codiTasca);
	}

	@Override
	public List<MonitorTascaInfo> findAll() {
		return delegateService.findAll();
	}

	@Override
	public MonitorTascaInfo findByCodi(String codi) {
		return delegateService.findByCodi(codi);
	}

	@Override
	public void updateProperaExecucio(String codi, Long plusValue) {
		delegateService.updateProperaExecucio(codi, plusValue);
	}

	@Override
	public void inici(String codiTasca) {
		delegateService.inici(codiTasca);
	}

	@Override
	public void fi(String codiTasca) {
		delegateService.fi(codiTasca);
	}

	@Override
	public void error(String codiTasca, String error) {
		delegateService.error(codiTasca, error);
	}

	@Override
	public void reiniciarTasquesEnSegonPla(String codiTasca) {
		delegateService.reiniciarTasquesEnSegonPla(codiTasca);
	}

}
