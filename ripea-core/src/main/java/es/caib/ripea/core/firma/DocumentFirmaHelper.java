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
import es.caib.ripea.core.persistence.DocumentEntity;
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

	
	/**
	 * Registre el log al expedient on està ubicat el document a firmar/firmat
	 * 
	 * @param document
	 * @param tipusLog
	 */
	protected void logExpedient(DocumentEntity document, LogTipusEnumDto tipusLog) {
		logExpedient(document, tipusLog, null, null);
	}

	/**
	 * Registre el log al expedient on està ubicat el document a firmar/firmat.
	 * Permet especificar els parametres del log
	 * 
	 * @param document
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

	protected void logFirmat(DocumentEntity document) {
		contingutLogHelper.log(
				document,
				LogTipusEnumDto.DOC_FIRMAT,
				"Document firmat",
				null,
				false,
				false);
	}
	private static final Logger logger = LoggerFactory.getLogger(DocumentFirmaHelper.class);

}
