package es.caib.ripea.plugin.firmaweb;

import java.util.List;

import es.caib.ripea.core.api.dto.FirmaResultatDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.plugin.RipeaEndpointPluginInfo;

public interface FirmaWebPlugin extends RipeaEndpointPluginInfo {

	public String firmaSimpleWebStart(
			List<FitxerDto> fitxersPerFirmar,
			String motiu,
			UsuariDto usuariActual, String base);

	public FirmaResultatDto firmaSimpleWebEnd(String transactionID);
}