package es.caib.ripea.plugin.summarize;

import es.caib.ripea.core.api.dto.Resum;
import es.caib.ripea.plugin.SistemaExternException;

public interface SummarizePlugin {

    public Resum getSummarize(String text) throws SistemaExternException;
    public boolean isActive();

}
