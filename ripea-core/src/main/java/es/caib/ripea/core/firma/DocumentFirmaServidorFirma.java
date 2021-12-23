package es.caib.ripea.core.firma;

import java.util.Arrays;

import es.caib.ripea.plugin.firmaservidor.SignaturaResposta;
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

		TipusFirma tipusFirma = TipusFirma.CADES;
		if ("pdf".equals(fitxer.getExtensio())) {
			tipusFirma = TipusFirma.PADES;
		}
		SignaturaResposta firma = pluginHelper.firmaServidorFirmar(document, fitxer, tipusFirma, motiu, "ca");
		ArxiuFirmaDto arxiuFirma = new ArxiuFirmaDto();
//		arxiuFirma.setFitxerNom("firma.cades");
		arxiuFirma.setFitxerNom(firma.getNom());
		arxiuFirma.setContingut(firma.getContingut());
		arxiuFirma.setTipusMime(firma.getMime());
//		arxiuFirma.setTipus(ArxiuFirmaTipusEnumDto.CADES_DET);
		arxiuFirma.setTipus(pluginHelper.toArxiuFirmaTipus(firma.getTipusFirmaEni()));
//		arxiuFirma.setPerfil(ArxiuFirmaPerfilEnumDto.BES);
		ArxiuFirmaPerfilEnumDto perfil = pluginHelper.toArxiuFirmaPerfilEnum(firma.getPerfilFirmaEni());
		arxiuFirma.setPerfil(perfil);
		pluginHelper.arxiuDocumentGuardarFirmaCades(document, fitxer, Arrays.asList(arxiuFirma));
		logAll(document, LogTipusEnumDto.SFIRMA_FIRMA);
		return arxiuFirma;
	}

	/**
	 * Registra el log al document i al expedient on està el document.
	 * Pots especificar directament el document firmat.
	 * Usar quan el document no està associat a l'objecte documentPortafirmes.
	 *   
	 * @param document
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
