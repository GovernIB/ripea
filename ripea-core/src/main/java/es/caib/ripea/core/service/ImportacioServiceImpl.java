/**
 *
 */
package es.caib.ripea.core.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.Document;
import es.caib.ripea.core.api.dto.ContingutTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.ImportacioDto;
import es.caib.ripea.core.api.dto.NtiOrigenEnumDto;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.ImportacioService;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.helper.ContingutHelper;
import es.caib.ripea.core.helper.ContingutLogHelper;
import es.caib.ripea.core.helper.DocumentHelper;
import es.caib.ripea.core.helper.PluginHelper;

/**
 * Implementació dels mètodes per importar documents desde l'arxiu.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ImportacioServiceImpl implements ImportacioService {

	@Autowired
	private ContingutHelper contingutHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private DocumentHelper documentHelper;
	@Autowired
	private ContingutLogHelper contingutLogHelper;
	
	@Transactional
	@Override
	public int getDocuments(
			Long entitatId,
			Long contingutId,
			ImportacioDto dades) throws ValidationException {
		logger.debug("Important documents de l'arxiu digital (" +
				"numeroRegistre=" + dades.getNumeroRegistre() + ")");
		ExpedientEntity expedientSuperior;
		FitxerDto fitxer = new FitxerDto();;
		int documentsRepetits = 0;
		List<DocumentDto> listDto = new ArrayList<DocumentDto>();
		ContingutEntity contingutPare = contingutHelper.comprovarContingutDinsExpedientModificable(
				entitatId,
				contingutId,
				false,
				false,
				false,
				false);

		List<ContingutArxiu> documentsArxiu = pluginHelper.getCustodyIdDocuments(
				dades.getNumeroRegistre(),
				dades.getDataPresentacioFormatted(),
				dades.getTipusRegistre());
		if (documentsArxiu != null && documentsArxiu.isEmpty())
			throw new ValidationException("No s'han trobat registres amb les dades especificades");
		
		if (ContingutTipusEnumDto.EXPEDIENT.equals(contingutPare.getTipus())) {
			expedientSuperior = (ExpedientEntity)contingutPare;
		} else {
			expedientSuperior = contingutPare.getExpedient();
		}
		int idx = 1;
		List<Document> documents = new ArrayList<Document>();
		outerloop: for (ContingutArxiu documentArxiu : documentsArxiu) {
			DocumentEntity entity = null;
			Document document = pluginHelper.importarDocument(
					expedientSuperior.getArxiuUuid(),
					documentArxiu.getIdentificador(),
					true);

			documents.add(document);
			documents = findAndCorrectDuplicates(
					documents,
					idx);	
			String tituloDoc = (String) document.getMetadades().getMetadadaAddicional("tituloDoc");
			fitxer.setNom(document.getNom());
			fitxer.setContentType(document.getContingut().getTipusMime());
			fitxer.setContingut(document.getContingut().getContingut());

			for (ContingutEntity contingut: contingutPare.getFills()) {
				if (contingut instanceof DocumentEntity && contingut.getEsborrat() == 0) {
					if (contingut.getNom().equals(tituloDoc)) {
						documentsRepetits++;
						continue outerloop;
					} 
				}
			}
			String nomDocument = tituloDoc != null ? (tituloDoc + " - " +  dades.getNumeroRegistre().replace('/', '_')): document.getNom();
			entity = documentHelper.crearDocumentDB(
					DocumentTipusEnumDto.IMPORTAT,
					nomDocument,
					null,
					document.getMetadades().getDataCaptura(),
					document.getMetadades().getDataCaptura(),
					//Només hi ha un òrgan
					document.getMetadades().getOrgans().get(0),
					getOrigen(document),
					getEstatElaboracio(document),
					getTipusDocumental(document),
					null, //metaDocumentEntity
					contingutPare,
					contingutPare.getEntitat(),
					expedientSuperior,
					null,
					expedientSuperior.getArxiuUuid());
		
			if (fitxer != null) {
				entity.updateFitxer(
						fitxer.getNom(),
						fitxer.getContentType(),
						null);
			}
			if (document.getFirmes() != null && !document.getFirmes().isEmpty()) {
				entity.updateEstat(DocumentEstatEnumDto.CUSTODIAT);
			}
			entity.updateArxiu(document.getIdentificador());
			entity.updateNtiIdentificador(document.getMetadades().getIdentificador());
			contingutLogHelper.logCreacio(
					entity,
					true,
					true);
			try {
				listDto.add(toDocumentDto(entity));
			} catch (DataIntegrityViolationException e) {
					documentsRepetits++;
					logger.error("No s'ha pogut importar el document", e);
			}
		}
		return documentsRepetits;
	}

	private List<Document> findAndCorrectDuplicates(
			List<Document> documents,
			int idx) {

	    List<Document> corrected = new ArrayList<Document>();
	    Set<String> uniques = new HashSet<>();

	    for(Document document : documents) {
	    	String tituloDoc = (String)document.getMetadades().getMetadadaAddicional("tituloDoc");
	        if(!uniques.add(tituloDoc)) {
	            document.getMetadades().addMetadadaAddicional("tituloDoc", tituloDoc + "_" + idx);
	        }
	        corrected.add(document);
	    }

	    return corrected;
	}
	
	private DocumentDto toDocumentDto(
			DocumentEntity document) {
		return (DocumentDto)contingutHelper.toContingutDto(
				document,
				false,
				false,
				false,
				false,
				true,
				true,
				false);
	}

	private static NtiOrigenEnumDto getOrigen(Document document) {
		NtiOrigenEnumDto origen = null;

		switch (document.getMetadades().getOrigen()) {
		case CIUTADA:
			origen = NtiOrigenEnumDto.O0;
			break;
		case ADMINISTRACIO:
			origen = NtiOrigenEnumDto.O1;
			break;
		}
		return origen;
	}

	private static DocumentNtiEstadoElaboracionEnumDto getEstatElaboracio(Document document) {
		DocumentNtiEstadoElaboracionEnumDto estatElaboracio = null;

		switch (document.getMetadades().getEstatElaboracio()) {
		case ORIGINAL:
			estatElaboracio = DocumentNtiEstadoElaboracionEnumDto.EE01;
			break;
		case COPIA_CF:
			estatElaboracio = DocumentNtiEstadoElaboracionEnumDto.EE02;
			break;
		case COPIA_DP:
			estatElaboracio = DocumentNtiEstadoElaboracionEnumDto.EE03;
			break;
		case COPIA_PR:
			estatElaboracio = DocumentNtiEstadoElaboracionEnumDto.EE04;
			break;
		case ALTRES:
			estatElaboracio = DocumentNtiEstadoElaboracionEnumDto.EE99;
			break;
		}
		return estatElaboracio;
	}

	private static String getTipusDocumental(Document document) {
		String tipusDocumental = null;

		if (document.getMetadades().getTipusDocumental() != null) {
			switch (document.getMetadades().getTipusDocumental()) {
			case RESOLUCIO:
				tipusDocumental = "TD01";
				break;
			case ACORD:
				tipusDocumental = "TD02";
				break;
			case CONTRACTE:
				tipusDocumental = "TD03";
				break;
			case CONVENI:
				tipusDocumental = "TD04";
				break;
			case DECLARACIO:
				tipusDocumental = "TD05";
				break;
			case COMUNICACIO:
				tipusDocumental = "TD06";
				break;
			case NOTIFICACIO:
				tipusDocumental = "TD07";
				break;
			case PUBLICACIO:
				tipusDocumental = "TD08";
				break;
			case JUSTIFICANT_RECEPCIO:
				tipusDocumental = "TD09";
				break;
			case ACTA:
				tipusDocumental = "TD10";
				break;
			case CERTIFICAT:
				tipusDocumental = "TD11";
				break;
			case DILIGENCIA:
				tipusDocumental = "TD12";
				break;
			case INFORME:
				tipusDocumental = "TD13";
				break;
			case SOLICITUD:
				tipusDocumental = "TD14";
				break;
			case DENUNCIA:
				tipusDocumental = "TD15";
				break;
			case ALEGACIO:
				tipusDocumental = "TD16";
				break;
			case RECURS:
				tipusDocumental = "TD17";
				break;
			case COMUNICACIO_CIUTADA:
				tipusDocumental = "TD18";
				break;
			case FACTURA:
				tipusDocumental = "TD19";
				break;
			case ALTRES_INCAUTATS:
				tipusDocumental = "TD20";
				break;
			case ALTRES:
				tipusDocumental = "TD99";
				break;
			}
		} else if (document.getMetadades().getTipusDocumentalAddicional() != null) {
			tipusDocumental = document.getMetadades().getTipusDocumentalAddicional();
		}

		return tipusDocumental;
	}
	private static final Logger logger = LoggerFactory.getLogger(ImportacioServiceImpl.class);

}
