package es.caib.ripea.plugin.summarize;

import es.caib.ripea.core.api.dto.Resum;
import es.caib.ripea.plugin.RipeaEndpointPluginInfo;
import es.caib.ripea.plugin.SistemaExternException;

public interface SummarizePlugin extends RipeaEndpointPluginInfo {

    public Resum getSummarize(String text) throws SistemaExternException;
    public Resum getSummarize(String text, int longitudDesc) throws SistemaExternException;
    public Resum getSummarize(String text, int longitudDesc, int longitudTitol) throws SistemaExternException;
    public boolean isActive();
}