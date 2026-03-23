package es.caib.ripea.ejb;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.comanda.model.server.monitoring.FitxerContingut;
import es.caib.comanda.model.server.monitoring.FitxerInfo;
import es.caib.comanda.ms.log.helper.LogFileStream;
import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.service.LogService;
import lombok.experimental.Delegate;

@Stateless
@RolesAllowed("**")
public class LogServiceEjb extends AbstractServiceEjb<LogService> implements LogService {

	@Delegate private LogService delegateService;

	protected void setDelegateService(LogService delegateService) {
		this.delegateService = delegateService;
	}
	
	@Override
	@PermitAll
	public List<FitxerInfo> llistarFitxers() {
		return delegateService.llistarFitxers();
	}

	@Override
	@PermitAll
	public FitxerContingut getFitxerByNom(String nom) {
		return delegateService.getFitxerByNom(nom);
	}

	@Override
	@PermitAll
	public LogFileStream tailLogFile(String filePath) {
		return delegateService.tailLogFile(filePath);
	}

	@Override
	@PermitAll
	public BlockingQueue<String> getQueue() {
		return delegateService.getQueue();
	}

	@Override
	@PermitAll
	public List<String> readLastNLines(String nomFitxer, Long nLinies) {
		return delegateService.readLastNLines(nomFitxer, nLinies);
	}
}