/**
 *
 */
package es.caib.ripea.core.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.Document;
import es.caib.ripea.core.api.dto.ArxiuEstatEnumDto;
import es.caib.ripea.core.api.dto.CarpetaDto;
import es.caib.ripea.core.api.dto.ContingutTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiTipoFirmaEnumDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.ImportacioDto;
import es.caib.ripea.core.api.dto.TipusDestiEnumDto;
import es.caib.ripea.core.api.dto.TipusImportEnumDto;
import es.caib.ripea.core.api.exception.DocumentAlreadyImportedException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.CarpetaService;
import es.caib.ripea.core.api.service.ImportacioService;
import es.caib.ripea.core.entity.CarpetaEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.helper.ArxiuConversions;
import es.caib.ripea.core.helper.ConfigHelper;
import es.caib.ripea.core.helper.ContingutHelper;
import es.caib.ripea.core.helper.ContingutLogHelper;
import es.caib.ripea.core.helper.DocumentHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.repository.CarpetaRepository;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.MetaDocumentRepository;

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
	private CarpetaService carpetaService;
	@Autowired
	private CarpetaRepository carpetaRepository;
	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private MetaDocumentRepository metaDocumentRepository;
	
	public static List<DocumentDto> expedientsWithImportacio = new ArrayList<DocumentDto>();
	
	@Transactional
	@Override
	public int importarDocuments(
			Long entitatId,
			Long contingutId,
			ImportacioDto params) {
		logger.debug("Important documents de l'arxiu digital (" +
				"numeroRegistre=" + params.getNumeroRegistre() + ")");
		ExpedientEntity expedientSuperior;
		FitxerDto fitxer = new FitxerDto();;
		int documentsRepetits = 0;
		boolean usingNumeroRegistre = params.getTipusImportacio().equals(TipusImportEnumDto.NUMERO_REGISTRE);
		boolean crearNovaCarpeta = params.getDestiTipus().equals(TipusDestiEnumDto.CARPETA_NOVA);
		String numeroRegistre = params.getNumeroRegistre();
		CarpetaEntity carpetaEntity = null;
		ContingutEntity pareActual = contingutHelper.comprovarContingutDinsExpedientModificable(
				entitatId,
				contingutId,
				false,
				false,
				false,
				false, 
				false, true, null);
		
		if (ContingutTipusEnumDto.EXPEDIENT.equals(pareActual.getTipus())) {
			expedientSuperior = (ExpedientEntity)pareActual;
		} else {
			expedientSuperior = pareActual.getExpedient();
		}
		
		List<ContingutArxiu> documentsTrobats = cercarDocumentsArxiu(params);

		// IMPORTAR DETALLS DE CADA DOCUMENT I CREACIÓ DOCUMENT A L'EXPEDIENT
		int idx = 1;
		List<Document> documents = new ArrayList<Document>();
		expedientsWithImportacio = new ArrayList<DocumentDto>();
		
		// CREAR NOVA CARPETA SI ÉS EL CAS ON IMPORTAR DOCUMENT
		if (crearNovaCarpeta) {
			CarpetaDto carpeta = carpetaService.create(
					entitatId,
					expedientSuperior.getId(),
					params.getCarpetaNom());
			carpetaEntity = carpetaRepository.findOne(carpeta.getId());
		}
		
		outerloop: for (ContingutArxiu contingutArxiu : documentsTrobats) {
			Document documentArxiu = pluginHelper.arxiuDocumentConsultar(
					null,
					contingutArxiu.getIdentificador(), 
					null, 
					true, 
					false);
			documents.add(documentArxiu);
			documents = findAndCorrectDuplicates(
					documents,
					idx);	
			String tituloDoc = (String) documentArxiu.getMetadades().getMetadadaAddicional("tituloDoc");
			fitxer.setNom(documentArxiu.getNom());
			fitxer.setContentType(documentArxiu.getContingut().getTipusMime());
			fitxer.setContingut(documentArxiu.getContingut().getContingut());

			// COMPROVAR SI S'HA IMPORTAT PRÈVIAMENT I ES PERMET DUPLICAR CONTINGUT
			List<DocumentDto> documentsAlreadyImported = documentHelper.findByArxiuUuid(contingutArxiu.getIdentificador());
			if (documentsAlreadyImported != null && !documentsAlreadyImported.isEmpty() && ! isIncorporacioDuplicadaPermesa()) {
				for (DocumentDto documentAlreadyImported: documentsAlreadyImported) {
					expedientsWithImportacio.add(documentAlreadyImported);
					documentsRepetits++;
				}
				continue outerloop;
			}
			String nomDocument = null;
			if (tituloDoc != null && usingNumeroRegistre) {
				nomDocument = formatTitulo(tituloDoc, numeroRegistre);
			} else {
				nomDocument = documentArxiu.getNom();
			}
			contingutHelper.comprovarNomValid(
					crearNovaCarpeta ? carpetaEntity : expedientSuperior,
					nomDocument,
					null,
					DocumentEntity.class);
			// CREAR DOCUMENT A LA BBDD
			if (!checkDocumentUniqueContraint(nomDocument, crearNovaCarpeta ? carpetaEntity : pareActual, entitatId)) {
				throw new DocumentAlreadyImportedException();
			}
			crearDocumentActualitzarMetadades(
					nomDocument, 
					documentArxiu, 
					crearNovaCarpeta ? carpetaEntity : pareActual,
					pareActual,
					expedientSuperior,
					fitxer,
					usingNumeroRegistre,
					params.getCodiEni());
			
		}
		return documentsRepetits;
	}

	@Override
	public List<DocumentDto> consultaExpedientsAmbImportacio() {
		return expedientsWithImportacio;
	}
	
	private List<ContingutArxiu> cercarDocumentsArxiu(ImportacioDto params) {
		// IMPORTAR DE L'ARXIU ELS DOCUMENTS
		List<ContingutArxiu> documentsArxiu = pluginHelper.importarDocumentsArxiu(params);
		if (documentsArxiu != null && documentsArxiu.isEmpty())
			throw new ValidationException("No s'han trobat registres amb les dades especificades");
		return documentsArxiu;
	}
	
	
	private void crearDocumentActualitzarMetadades(
			String nomDocument,
			Document documentArxiu,
			ContingutEntity contenidor,
			ContingutEntity pareActual,
			ExpedientEntity expedientSuperior,
			FitxerDto fitxer,
			boolean usingNumeroRegistre,
			String codiEniOrigen) {
		// TIPUS DE DOCUMENT PER DEFECTE
		MetaDocumentEntity metaDocument = metaDocumentRepository.findByMetaExpedientAndPerDefecteTrue(expedientSuperior.getMetaExpedient());
		
		DocumentNtiTipoFirmaEnumDto documentNtiTipoFirmaEnum = ArxiuConversions.getNtiTipoFirma(documentArxiu);
		
		DocumentEntity entity = documentHelper.crearDocumentDB(
				DocumentTipusEnumDto.IMPORTAT,
				nomDocument,
				null,
				documentArxiu.getMetadades().getDataCaptura(),
				documentArxiu.getMetadades().getDataCaptura(),
				//Només hi ha un òrgan
				getOrgans(documentArxiu),
				ArxiuConversions.getOrigen(documentArxiu),
				ArxiuConversions.getEstatElaboracio(documentArxiu),
				ArxiuConversions.getTipusDocumental(documentArxiu),
				metaDocument, //metaDocumentEntity
				contenidor,
				pareActual.getEntitat(),
				expedientSuperior,
				null,
				expedientSuperior.getArxiuUuid(),
				null, 
				documentHelper.getDocumentFirmaTipus(documentNtiTipoFirmaEnum));
		if (fitxer != null) {
			entity.updateFitxer(
					fitxer.getNom(),
					fitxer.getContentType(),
					null);
		}
		
		// POSAR COM A CUSTODIAT O DEFINITIU
		if (documentArxiu.getFirmes() != null && !documentArxiu.getFirmes().isEmpty()) {
			entity.updateEstat(DocumentEstatEnumDto.CUSTODIAT);
		} else {
			entity.updateEstat(DocumentEstatEnumDto.DEFINITIU);
		}
		entity.updateArxiuEstat(ArxiuEstatEnumDto.DEFINITIU);

		// MOU/COPIA EL DOCUMENT
		documentArxiu = pluginHelper.importarDocument(
				expedientSuperior.getArxiuUuid(),
				documentArxiu.getIdentificador(),
				usingNumeroRegistre);
		
		// ACTUALITZAR METADADES NTI DEL DOCUMENT CREAT
		entity.updateArxiu(documentArxiu.getIdentificador());
		entity.updateNtiIdentificador(documentArxiu.getMetadades().getIdentificador());
		entity.updateNti(
				obtenirNumeroVersioEniDocument(documentArxiu.getMetadades().getVersioNti()),
				documentArxiu.getMetadades().getIdentificador(),
				getOrgans(documentArxiu),
				ArxiuConversions.getOrigen(documentArxiu),
				ArxiuConversions.getEstatElaboracio(documentArxiu),
				ArxiuConversions.getTipusDocumental(documentArxiu),
				documentArxiu.getMetadades().getIdentificadorOrigen(),
				documentNtiTipoFirmaEnum,
				ArxiuConversions.getNtiCsv(documentArxiu)[0],
				ArxiuConversions.getNtiCsv(documentArxiu)[1]);
		contingutLogHelper.logCreacio(
				entity,
				true,
				true);
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
	
	private String formatTitulo(String tituloDoc, String numeroRegistre) {
		String extension = FilenameUtils.getExtension(tituloDoc);
		if (extension != null && !extension.isEmpty()) {
			return FilenameUtils.removeExtension(tituloDoc) + " - " + numeroRegistre.replace('/', '_') + "." + extension;
		} else {
			return tituloDoc + " - " + numeroRegistre.replace('/', '_');
		}
	}

	private boolean checkDocumentUniqueContraint (String nom, ContingutEntity pare, Long entitatId) {
		EntitatEntity entitat = entitatId != null ? entitatRepository.getOne(entitatId) : null;
		return  contingutHelper.checkUniqueContraint(nom, pare, entitat, ContingutTipusEnumDto.DOCUMENT);
	}
	
	private boolean isIncorporacioDuplicadaPermesa() {
		return configHelper.getAsBoolean("es.caib.ripea.incorporacio.anotacions.duplicada");
	}
	private static final Logger logger = LoggerFactory.getLogger(ImportacioServiceImpl.class);

}
