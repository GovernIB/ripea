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
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.api.FirmaTipus;
import es.caib.ripea.core.api.dto.ContingutTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiTipoFirmaEnumDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.ImportacioDto;
import es.caib.ripea.core.api.dto.NtiOrigenEnumDto;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.ImportacioService;
import es.caib.ripea.core.entity.CarpetaEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.helper.ContingutHelper;
import es.caib.ripea.core.helper.ContingutLogHelper;
import es.caib.ripea.core.helper.DocumentHelper;
import es.caib.ripea.core.helper.ExpedientHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.helper.PropertiesHelper;
import es.caib.ripea.core.repository.CarpetaRepository;

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
	@Autowired
	private ExpedientHelper expedientHelper;
	@Autowired
	private CarpetaRepository carpetaRepository;
	
	public static List<DocumentDto> expedientsWithImportacio = new ArrayList<DocumentDto>();
	
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
				false, 
				false);
		// ############### RECUPERAR DOCUMENTS DEL NUMERO D REGISTRE INTRDUIT #########
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
		expedientsWithImportacio = new ArrayList<DocumentDto>();
		// ############### IMPORTAR EL DETALL DE CADA DOCUMENT #########
		outerloop: for (ContingutArxiu contingutArxiu : documentsArxiu) {
			DocumentEntity entity = null;
			CarpetaEntity carpetaEntity = null;
			Document documentArxiu = pluginHelper.importarDocument(
					expedientSuperior.getArxiuUuid(),
					contingutArxiu.getIdentificador(),
					true);

			documents.add(documentArxiu);
			documents = findAndCorrectDuplicates(
					documents,
					idx);	
			String tituloDoc = (String) documentArxiu.getMetadades().getMetadadaAddicional("tituloDoc");
			fitxer.setNom(documentArxiu.getNom());
			fitxer.setContentType(documentArxiu.getContingut().getTipusMime());
			fitxer.setContingut(documentArxiu.getContingut().getContingut());

			// comprovar si el justificant s'ha importat anteriorment
			List<DocumentDto> documentsAlreadyImported = documentHelper.findByArxiuUuid(contingutArxiu.getIdentificador());
			if (documentsAlreadyImported != null && !documentsAlreadyImported.isEmpty()) {
				for (DocumentDto documentAlreadyImported: documentsAlreadyImported) {
					expedientsWithImportacio.add(documentAlreadyImported);
					documentsRepetits++;
				}
				continue outerloop;
//				throw new DocumentAlreadyImportedException();
			}
//			for (ContingutEntity contingut: contingutPare.getFills()) {
//				if (contingut instanceof DocumentEntity && contingut.getEsborrat() == 0) {
//					if (contingut.getNom().equals(tituloDoc)) {
//						documentsRepetits++;
//						continue outerloop;
//					} 
//				}
//			}
			// ############### CREAR CARPETA PARE ON INTRODUIR DOCUMENT #########
			boolean isCarpetaActive = Boolean.parseBoolean(PropertiesHelper.getProperties().getProperty("es.caib.ripea.creacio.carpetes.activa"));
			if (isCarpetaActive) {
				// create carpeta ind db and arxiu if doesnt already exists
				Long carpetaId = expedientHelper.createCarpetaFromExpPeticio(
						expedientSuperior,
						entitatId,
						"Registre entrada: " + dades.getNumeroRegistre());
				carpetaEntity = carpetaRepository.findOne(carpetaId);
			}
			String nomDocument = tituloDoc != null ? (tituloDoc + " - " +  dades.getNumeroRegistre().replace('/', '_')) : documentArxiu.getNom();
			contingutHelper.comprovarNomValid(
					isCarpetaActive ? carpetaEntity : expedientSuperior,
					nomDocument,
					null,
					DocumentEntity.class);
			// ############### CREAR DOCUMENT A LA BBDD #########
			entity = documentHelper.crearDocumentDB(
					DocumentTipusEnumDto.IMPORTAT,
					nomDocument,
					null,
					documentArxiu.getMetadades().getDataCaptura(),
					documentArxiu.getMetadades().getDataCaptura(),
					//Només hi ha un òrgan
					getOrgans(documentArxiu),
					getOrigen(documentArxiu),
					getEstatElaboracio(documentArxiu),
					getTipusDocumental(documentArxiu),
					null, //metaDocumentEntity
					isCarpetaActive ? carpetaEntity : contingutPare,
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
			if (documentArxiu.getFirmes() != null && !documentArxiu.getFirmes().isEmpty()) {
				entity.updateEstat(DocumentEstatEnumDto.CUSTODIAT);
			} else {
				entity.updateEstat(DocumentEstatEnumDto.DEFINITIU);
			}
			// ############### ACTUALITZAR METADADES NTI #########
			entity.updateArxiu(documentArxiu.getIdentificador());
			entity.updateNtiIdentificador(documentArxiu.getMetadades().getIdentificador());
			entity.updateNti(
					obtenirNumeroVersioEniDocument(
							documentArxiu.getMetadades().getVersioNti()),
							documentArxiu.getMetadades().getIdentificador(),
					getOrgans(documentArxiu),
					getOrigen(documentArxiu),
					getEstatElaboracio(documentArxiu),
					getTipusDocumental(documentArxiu),
					documentArxiu.getMetadades().getIdentificadorOrigen(),
					getNtiTipoFirma(documentArxiu),
					getNtiCsv(documentArxiu)[0],
					getNtiCsv(documentArxiu)[1]);
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
	
	@Override
	public List<DocumentDto> consultaExpedientsAmbImportacio() {
		return expedientsWithImportacio;
	}
	
	private static final String ENI_DOCUMENT_PREFIX = "http://administracionelectronica.gob.es/ENI/XSD/v";
	private String obtenirNumeroVersioEniDocument(String versio) {
		if (versio != null) {
			if (versio.startsWith(ENI_DOCUMENT_PREFIX)) {
				int indexBarra = versio.indexOf("/", ENI_DOCUMENT_PREFIX.length());
				return versio.substring(ENI_DOCUMENT_PREFIX.length(), indexBarra);
			}
		}
		return null;
	}
	
	private String getOrgans(Document documentArxiu) {
		String organs = null;
		if (documentArxiu.getMetadades().getOrgans() != null) {
			List<String> metadadaOrgans = documentArxiu.getMetadades().getOrgans();
			StringBuilder organsSb = new StringBuilder();
			boolean primer = true;
			for (String organ: metadadaOrgans) {
				organsSb.append(organ);
				if (primer || metadadaOrgans.size() == 1) {
					primer = false;
				} else {
					organsSb.append(",");
				}
			}
			organs = organsSb.toString();
		}
		return organs;
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

	private DocumentNtiTipoFirmaEnumDto getNtiTipoFirma(Document documentArxiu) {
		DocumentNtiTipoFirmaEnumDto ntiTipoFirma = null;
		if (documentArxiu.getFirmes() != null && !documentArxiu.getFirmes().isEmpty()) {
			FirmaTipus firmaTipus = null;
			for (Firma firma: documentArxiu.getFirmes()) {
				if (firma.getTipus() != FirmaTipus.CSV) {
					firmaTipus = firma.getTipus();
					break;
				}
			}
			switch (firmaTipus) {
			case CSV:
				ntiTipoFirma = DocumentNtiTipoFirmaEnumDto.TF01;
				break;
			case XADES_DET:
				ntiTipoFirma = DocumentNtiTipoFirmaEnumDto.TF02;
				break;
			case XADES_ENV:
				ntiTipoFirma = DocumentNtiTipoFirmaEnumDto.TF03;
				break;
			case CADES_DET:
				ntiTipoFirma = DocumentNtiTipoFirmaEnumDto.TF04;
				break;
			case CADES_ATT:
				ntiTipoFirma = DocumentNtiTipoFirmaEnumDto.TF05;
				break;
			case PADES:
				ntiTipoFirma = DocumentNtiTipoFirmaEnumDto.TF06;
				break;
			case SMIME:
				ntiTipoFirma = DocumentNtiTipoFirmaEnumDto.TF07;
				break;
			case ODT:
				ntiTipoFirma = DocumentNtiTipoFirmaEnumDto.TF08;
				break;
			case OOXML:
				ntiTipoFirma = DocumentNtiTipoFirmaEnumDto.TF09;
				break;
			}
		}
		return ntiTipoFirma;
	}
	
	private String[] getNtiCsv(Document documentArxiu) {
		String [] ntiCsv = new String[2]; 
		if (documentArxiu.getFirmes() != null && !documentArxiu.getFirmes().isEmpty()) {
			for (Firma firma : documentArxiu.getFirmes()) {
				if (firma.getTipus() == FirmaTipus.CSV) {
					ntiCsv[0] = firma.getCsvRegulacio();
					ntiCsv[1] = firma.getContingut() != null ? new String(firma.getContingut()) : null;
				}
			}
		}
		return ntiCsv;
	}
	private static final Logger logger = LoggerFactory.getLogger(ImportacioServiceImpl.class);

}
