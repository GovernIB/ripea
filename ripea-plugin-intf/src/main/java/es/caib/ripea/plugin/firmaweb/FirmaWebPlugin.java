package es.caib.ripea.plugin.firmaweb;

import java.util.List;

import es.caib.ripea.plugin.RipeaEndpointPluginInfo;
import es.caib.ripea.service.intf.dto.FirmaResultatDto;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.dto.UsuariDto;

public interface FirmaWebPlugin extends RipeaEndpointPluginInfo {

	public String firmaSimpleWebStart(
			List<FitxerDto> fitxersPerFirmar,
			String motiu,
			UsuariDto usuariActual,
			String base,
			String iframeVista);

	public FirmaResultatDto firmaSimpleWebEnd(String transactionID);
}