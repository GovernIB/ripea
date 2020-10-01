package es.caib.ripea.core.firma;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.api.FirmaTipus;
import es.caib.ripea.core.api.dto.DocumentNtiTipoFirmaEnumDto;
import es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.helper.ContingutLogHelper;
import es.caib.ripea.core.helper.PluginHelper;

/**
 * Contenidor de tots els mètodes relacionats amb la firma de documents
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DocumentFirmaHelper {

	@Autowired
	private ContingutLogHelper contingutLogHelper;
	@Autowired
	private PluginHelper pluginHelper;

	protected void actualitzarInformacioFirma(DocumentEntity document) {
		if (pluginHelper.arxiuSuportaVersionsDocuments()) {
			try {
				Document documentArxiu = pluginHelper.arxiuDocumentConsultar(document, null, null, false);
				DocumentNtiTipoFirmaEnumDto tipoFirma = null;
				String csv = null;
				String csvDef = null;
				if (documentArxiu.getFirmes() != null) {
					for (Firma firma : documentArxiu.getFirmes()) {
						if (FirmaTipus.CSV.equals(firma.getTipus())) {
							csv = new String(firma.getContingut());
							csvDef = firma.getCsvRegulacio();
						} else {
							switch (firma.getTipus()) {
							case CSV:
								tipoFirma = DocumentNtiTipoFirmaEnumDto.TF01;
								break;
							case XADES_DET:
								tipoFirma = DocumentNtiTipoFirmaEnumDto.TF02;
								break;
							case XADES_ENV:
								tipoFirma = DocumentNtiTipoFirmaEnumDto.TF03;
								break;
							case CADES_DET:
								tipoFirma = DocumentNtiTipoFirmaEnumDto.TF04;
								break;
							case CADES_ATT:
								tipoFirma = DocumentNtiTipoFirmaEnumDto.TF05;
								break;
							case PADES:
								tipoFirma = DocumentNtiTipoFirmaEnumDto.TF06;
								break;
							case SMIME:
								tipoFirma = DocumentNtiTipoFirmaEnumDto.TF07;
								break;
							case ODT:
								tipoFirma = DocumentNtiTipoFirmaEnumDto.TF08;
								break;
							case OOXML:
								tipoFirma = DocumentNtiTipoFirmaEnumDto.TF09;
								break;
							}
						}
					}
				}
				document.updateNti(
						document.getNtiVersion(),
						document.getNtiIdentificador(),
						document.getNtiOrgano(),
						document.getNtiOrigen(),
						document.getNtiEstadoElaboracion(),
						document.getNtiTipoDocumental(),
						document.getNtiIdDocumentoOrigen(),
						tipoFirma,
						csv,
						csvDef);
			} catch (Exception ex) {
				logger.error(
						"Error al actualitzar les metadades NTI de firma (" + "entitatId=" +
								document.getEntitat().getId() + ", " + "documentId=" + document.getId() + ", " +
								"documentTitol=" + document.getNom() + ")",
						ex);
			}
		}
	}
	
	/**
	 * Registre el log al expedient on està ubicat el document a firmar/firmat
	 * 
	 * @param documentPortafirmes
	 * @param tipusLog
	 */
	protected void logExpedient(DocumentEntity document, LogTipusEnumDto tipusLog) {
		logExpedient(document, tipusLog, null, null);
	}

	/**
	 * Registre el log al expedient on està ubicat el document a firmar/firmat.
	 * Permet especificar els parametres del log
	 * 
	 * @param documentPortafirmes
	 * @param tipusLog
	 */
	protected void logExpedient(DocumentEntity document, LogTipusEnumDto tipusLog, String param1, String param2) {
		contingutLogHelper.log(
				document.getExpedient(),
				LogTipusEnumDto.MODIFICACIO,
				document,
				LogObjecteTipusEnumDto.DOCUMENT,
				tipusLog,
				param1,
				param2,
				false,
				false);
	}

	private static final Logger logger = LoggerFactory.getLogger(DocumentFirmaHelper.class);

}
