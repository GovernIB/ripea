package es.caib.ripea.plugin.firmaweb;

import es.caib.ripea.core.api.dto.FirmaResultatDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.UsuariDto;


public interface FirmaWebPlugin {

	public String firmaSimpleWebStart(
			FitxerDto fitxerPerFirmar,
			String motiu,
			UsuariDto usuariActual, String base);

	public FirmaResultatDto firmaSimpleWebEnd(String transactionID);



}
