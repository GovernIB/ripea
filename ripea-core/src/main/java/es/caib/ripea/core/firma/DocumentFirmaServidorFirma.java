package es.caib.ripea.core.firma;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.ArxiuFirmaDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaPerfilEnumDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaTipusEnumDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.helper.ContingutLogHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.plugin.firmaservidor.FirmaServidorPlugin.TipusFirma;

@Component
public class DocumentFirmaServidorFirma extends DocumentFirmaHelper{

	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private ContingutLogHelper contingutLogHelper;
	
	public ArxiuFirmaDto firmar(DocumentEntity document, FitxerDto fitxer, String motiu) {
		byte[] firma = pluginHelper.firmaServidorFirmar(document, fitxer, TipusFirma.CADES, motiu, "ca");
		ArxiuFirmaDto arxiuFirma = new ArxiuFirmaDto();
		arxiuFirma.setFitxerNom("firma.cades");
		arxiuFirma.setContingut(firma);
		arxiuFirma.setTipusMime("application/octet-stream");
		arxiuFirma.setTipus(ArxiuFirmaTipusEnumDto.CADES_DET);
		arxiuFirma.setPerfil(ArxiuFirmaPerfilEnumDto.BES);
		pluginHelper.arxiuDocumentGuardarFirmaCades(document, fitxer, Arrays.asList(arxiuFirma));
		logAll(document, LogTipusEnumDto.SFIRMA_FIRMA);
		return arxiuFirma;
	}

	/**
	 * Registra el log al document i al expedient on està el document.
	 * Pots especificar directament el document firmat.
	 * Usar quan el document no està associat a l'objecte documentPortafirmes.
	 *   
	 * @param documentPortafirmes 
	 * @param tipusLog
	 */
	private void logAll(DocumentEntity document, LogTipusEnumDto tipusLog) {
		contingutLogHelper.log(
				document,
				tipusLog,
				null,
				null,
				false,
				false);
		logExpedient(document, tipusLog);
	}
}
