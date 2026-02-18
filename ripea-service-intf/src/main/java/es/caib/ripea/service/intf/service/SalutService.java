package es.caib.ripea.service.intf.service;

import java.util.List;

import es.caib.comanda.model.server.monitoring.ContextInfo;
import es.caib.comanda.model.server.monitoring.IntegracioInfo;
import es.caib.comanda.model.server.monitoring.SalutInfo;
import es.caib.comanda.model.server.monitoring.SubsistemaInfo;

public interface SalutService {
    public List<IntegracioInfo> getIntegracions();
    public List<SubsistemaInfo> getSubsistemes();
    public List<ContextInfo> getContexts(String baseUrl);
    public SalutInfo checkSalut(String versio, String performanceUrl);
}