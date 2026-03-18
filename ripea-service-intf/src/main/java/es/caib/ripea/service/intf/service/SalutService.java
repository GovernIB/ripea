package es.caib.ripea.service.intf.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.comanda.model.server.monitoring.ContextInfo;
import es.caib.comanda.model.server.monitoring.IntegracioInfo;
import es.caib.comanda.model.server.monitoring.SalutInfo;
import es.caib.comanda.model.server.monitoring.SubsistemaInfo;

public interface SalutService {
	@PreAuthorize("hasRole('IPA_COM')")
    public List<IntegracioInfo> getIntegracions();
	@PreAuthorize("hasRole('IPA_COM')")
    public List<SubsistemaInfo> getSubsistemes();
	@PreAuthorize("hasRole('IPA_COM')")
    public List<ContextInfo> getContexts(String baseUrl);
    public SalutInfo checkSalut(String versio, String performanceUrl);
}