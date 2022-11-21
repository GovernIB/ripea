/**
 * 
 */
package es.caib.ripea.core.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sun.jersey.core.util.Base64;

import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentEstat;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.api.FirmaTipus;
import es.caib.plugins.arxiu.caib.ArxiuPluginCaib;
import es.caib.ripea.core.api.dto.ArxiuEstatEnumDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaPerfilEnumDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaTipusEnumDto;
import es.caib.ripea.core.api.dto.ContingutTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentFirmaTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiTipoFirmaEnumDto;
import es.caib.ripea.core.api.dto.DocumentOrigenEnumDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;
import es.caib.ripea.core.api.dto.NtiOrigenEnumDto;
import es.caib.ripea.core.api.exception.SistemaExternException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.NodeEntity;
import es.caib.ripea.core.repository.DocumentRepository;

/**
 * Mètodes per a gestionar els arxius associats a un document
 * tenint en compte que hi pot haver configurat (o no) un plugin
 * de gestió documental.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DocumentHelper {

	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private ContingutHelper contingutHelper;
	@Autowired
	private ContingutLogHelper contingutLogHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private ExpedientHelper expedientHelper;
	
	public DocumentDto crearDocument(
			DocumentDto document,
			ContingutEntity pare,
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			boolean returnDetail) {
		DocumentDto dto =  new DocumentDto();
		if (expedient != null) {
			cacheHelper.evictErrorsValidacioPerNode(expedient);
		}
		contingutHelper.comprovarNomValid(
				pare,
				document.getNom(),
				null,
				DocumentEntity.class);
		List<DocumentEntity> documents = documentRepository.findByExpedientAndMetaNodeAndEsborrat(
				expedient,
				metaDocument,
				0);
		if (documents.size() > 0 && (metaDocument.getMultiplicitat().equals(MultiplicitatEnumDto.M_1) || metaDocument.getMultiplicitat().equals(MultiplicitatEnumDto.M_0_1))) {
			throw new ValidationException(
					"<creacio>",
					ExpedientEntity.class,
					"La multiplicitat del meta-document no permet crear nous documents a dins l'expedient (" +
					"metaExpedientId=" + expedient.getMetaExpedient().getId() + ", " +
					"metaDocumentId=" + document.getMetaDocument().getId() + ", " +
					"metaDocumentMultiplicitat=" + metaDocument.getMultiplicitat() + ", " +
					"expedientId=" + expedient.getId() + ")");
		}
		DocumentEntity entity = crearDocumentDB(
				document.getDocumentTipus(),
				document.getNom(),
				document.getDescripcio(),
				new Date(),
				document.getData(), //data captura
				expedient.getNtiOrgano(),
				document.getNtiOrigen(),
				document.getNtiEstadoElaboracion(),
				metaDocument.getNtiTipoDocumental(),
				metaDocument,
				pare,
				pare.getEntitat(),
				expedient,
				document.getUbicacio(),
				document.getNtiIdDocumentoOrigen(),
				document.getPinbalIdpeticion());
		
		FitxerDto fitxer = new FitxerDto();
		fitxer.setNom(document.getFitxerNom());
		fitxer.setContentType(document.getFitxerContentType());
		fitxer.setContingut(document.getFitxerContingut());
		actualitzarFitxerDB(
				entity,
				fitxer);

		contingutLogHelper.logCreacio(
				entity,
				true,
				true);
		
		String gestioDocumentalAdjuntId = document.getGesDocAdjuntId();
		if (document.getFitxerContingut() != null) {
			gestioDocumentalAdjuntId = pluginHelper.gestioDocumentalCreate(
					PluginHelper.GESDOC_AGRUPACIO_DOCS_ADJUNTS,
					new ByteArrayInputStream(document.getFitxerContingut()));
			entity.setGesDocAdjuntId(gestioDocumentalAdjuntId);
		}
		String gestioDocumentalAdjuntFirmaId = document.getGesDocAdjuntFirmaId();
		if (document.isAmbFirma() && document.isFirmaSeparada()) {
			gestioDocumentalAdjuntFirmaId = pluginHelper.gestioDocumentalCreate(
					PluginHelper.GESDOC_AGRUPACIO_DOCS_ADJUNTS,
					new ByteArrayInputStream(document.getFirmaContingut()));
			entity.setGesDocAdjuntFirmaId(gestioDocumentalAdjuntFirmaId);
		}

		if (document.isAmbFirma()) 
			entity.updateEstat(DocumentEstatEnumDto.ADJUNT_FIRMAT);
		
		try {
			if (entity.getExpedient().getArxiuUuid() != null) {
				
				List<ArxiuFirmaDto> firmes = null;
				if (isDocumentFromPinbal(entity)) {
					ArxiuFirmaDto firma = getArxiuFirmaPades(null, null);
					firmes = Arrays.asList(firma);
					
				} else if (document.isAmbFirma()) {
					firmes = validaFirmaDocument(
							entity, 
							fitxer,
							document.getFirmaContingut(), 
							false, 
							true);
				}
				
				
				
				DocumentFirmaTipusEnumDto documentFirmaTipus;
				if (!document.isAmbFirma()) {
					documentFirmaTipus = DocumentFirmaTipusEnumDto.SENSE_FIRMA;
				} else if (!document.isFirmaSeparada()) {
					documentFirmaTipus = DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA;
				} else {
					documentFirmaTipus = DocumentFirmaTipusEnumDto.FIRMA_SEPARADA;
				}
				
				ArxiuEstatEnumDto arxiuEstat = ArxiuEstatEnumDto.ESBORRANY;
				
				contingutHelper.arxiuPropagarModificacio(
						entity,
						fitxer,
						documentFirmaTipus,
						firmes,
						arxiuEstat);
				
				if (gestioDocumentalAdjuntId != null ) {
					pluginHelper.gestioDocumentalDelete(
							gestioDocumentalAdjuntId,
							PluginHelper.GESDOC_AGRUPACIO_DOCS_ADJUNTS);
					entity.setGesDocAdjuntId(null);
				}
				if (gestioDocumentalAdjuntFirmaId != null ) {
					pluginHelper.gestioDocumentalDelete(
							gestioDocumentalAdjuntFirmaId,
							PluginHelper.GESDOC_AGRUPACIO_DOCS_ADJUNTS);
					entity.setGesDocAdjuntFirmaId(null);
				}	
			}

			
		} catch (Exception ex) {
			logger.error("Error al custodiar document en arxiu  (" +
					"id=" + entity.getId() + ")",
					ex);

		}
		entity.updateArxiuIntent();
		
		if (returnDetail)		
			dto = toDocumentDto(entity);
		else
			dto.setId(entity.getId());
		return dto;
	}
	
	
	public ArxiuFirmaDto getArxiuFirmaPades(String nom, byte[] contingut){
		ArxiuFirmaDto firma = new ArxiuFirmaDto();
		
		firma.setTipus(ArxiuFirmaTipusEnumDto.PADES);
		firma.setPerfil(ArxiuFirmaPerfilEnumDto.EPES);
		firma.setTipusMime("application/pdf");
		
		firma.setFitxerNom(nom);
		firma.setContingut(contingut);
		return firma;
	}
	
	
	public static boolean isDocumentFromRipea(DocumentEntity document) {
		return getDocumentOrigen(document) == DocumentOrigenEnumDto.RIPEA;
	}
	
	public static boolean isDocumentFromDistribucio(DocumentEntity document) {
		return getDocumentOrigen(document) == DocumentOrigenEnumDto.DISTRIBUCIO;
	}
	
	public static boolean isDocumentFromPinbal(DocumentEntity document) {
		return getDocumentOrigen(document) == DocumentOrigenEnumDto.PINBAL;
	}
	
	
	public static DocumentOrigenEnumDto getDocumentOrigen(DocumentEntity document) {
		if (document.getPinbalIdpeticion() != null) {
			return DocumentOrigenEnumDto.PINBAL;
		} else if (document.getAnnexos() != null && !document.getAnnexos().isEmpty()) {
			return DocumentOrigenEnumDto.DISTRIBUCIO;
		} else if (document.getDocumentTipus() == DocumentTipusEnumDto.IMPORTAT){
			return DocumentOrigenEnumDto.ANOTHER;
		} else {
			return DocumentOrigenEnumDto.RIPEA;
		}
	}
	
	
	public DocumentDto updateDocument(
			Long entitatId,
			DocumentEntity documentEntity,
			DocumentDto document,
			boolean comprovarMetaExpedient) {

		MetaDocumentEntity metaDocument = null;
		List<ArxiuFirmaDto> firmes = null;
		if (document.getMetaDocument() != null) {
			metaDocument = entityComprovarHelper.comprovarMetaDocument(
					documentEntity.getEntitat(),
					documentEntity.getMetaDocument() != null ? documentEntity.getMetaDocument().getMetaExpedient() : null,
					document.getMetaDocument().getId(),
					false,
					comprovarMetaExpedient);
		} else {
			throw new ValidationException(
					documentEntity.getId(),
					DocumentEntity.class,
					"No es pot actualitzar un document sense un meta-document associat");
		}
		if (!isModificacioCustodiatsActiva() && documentEntity.isArxiuEstatDefinitu()) {
			throw new ValidationException(
					documentEntity.getId(),
					DocumentEntity.class,
					"No es poden actualitzar un document definitiu");
		}
		contingutHelper.comprovarNomValid(
				documentEntity.getPare(),
				document.getNom(),
				documentEntity.getId(),
				DocumentEntity.class);
		cacheHelper.evictErrorsValidacioPerNode(documentEntity);
		cacheHelper.evictErrorsValidacioPerNode(documentEntity.getExpedient());
		String nomOriginal = documentEntity.getNom();
		documentEntity.update(
				metaDocument,
				document.getNom(),
				document.getDescripcio(),
				documentEntity.getDataCaptura(),
				document.getUbicacio(),
				document.getData(),
				documentEntity.getNtiOrgano(),
				document.getNtiOrigen(),
				document.getNtiEstadoElaboracion(),
				metaDocument.getNtiTipoDocumental(),
				document.getNtiIdDocumentoOrigen(),
				document.getNtiTipoFirma(),
				document.getNtiCsv(),
				document.getNtiCsvRegulacion());
		FitxerDto fitxer = null;

		Document arxiuDocument = null;
		if (documentEntity.getArxiuUuid() != null) {
			arxiuDocument = pluginHelper.arxiuDocumentConsultar(
					documentEntity,
					null,
					null,
					true,
					false);
		}
		
		
		DocumentFirmaTipusEnumDto documentFirmaTipus;
		if (!document.isAmbFirma()) {
			documentFirmaTipus = DocumentFirmaTipusEnumDto.SENSE_FIRMA;
		} else if (!document.isFirmaSeparada()) {
			documentFirmaTipus = DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA;
		} else {
			documentFirmaTipus = DocumentFirmaTipusEnumDto.FIRMA_SEPARADA;
		}
		
		boolean newFitxer = document.getFitxerContingut() != null;
		boolean newFirma = document.getFirmaContingut() != null;
		
		
		if (documentFirmaTipus == DocumentFirmaTipusEnumDto.SENSE_FIRMA) {
			
			fitxer = fitxer(
					newFitxer,
					documentEntity,
					document,
					arxiuDocument);
	
			documentEntity.updateEstat(DocumentEstatEnumDto.REDACCIO);
			
		} else  if (documentFirmaTipus == DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA) {
			
			fitxer = fitxer(
					newFitxer,
					documentEntity,
					document,
					arxiuDocument);
			
			firmes = validaFirmaDocument(
					documentEntity, 
					fitxer,
					null, 
					false, 
					true);
			
		} else  if (documentFirmaTipus == DocumentFirmaTipusEnumDto.FIRMA_SEPARADA) {
			
			fitxer = fitxer(
					newFitxer,
					documentEntity,
					document,
					arxiuDocument);
			
			byte[]  firmaContingut;
			if (newFirma) {
				firmaContingut = document.getFirmaContingut();
			} else {
				firmaContingut = getFirmaDetachedFromArxiuDocument(arxiuDocument);
			}
			
			firmes = validaFirmaDocument(
					documentEntity, 
					fitxer,
					firmaContingut, 
					false, 
					true);
			
		}
		
		
		// Al modificar el document, eliminam l'alerta de document invàlid,
		// i el passam de importat a digital, ja que no és el mateix document que haviem importat
		if (!documentEntity.isValidacioFirmaCorrecte()) {
			documentEntity.setValidacioFirmaCorrecte(true);
//			documentEntity.setDocumentTipus(DocumentTipusEnumDto.DIGITAL);
		}
		
		// Registra al log la modificació del document
		contingutLogHelper.log(
				documentEntity,
				LogTipusEnumDto.MODIFICACIO,
				(!nomOriginal.equals(document.getNom())) ? document.getNom() : null,
				(document.getFitxerContingut() != null) ? "VERSIO_NOVA" : null,
				true,
				true);
		DocumentDto dto = toDocumentDto(documentEntity);

		if (arxiuDocument == null || arxiuDocument.getEstat() == DocumentEstat.ESBORRANY || isPropagarModificacioDefinitiusActiva()) {

			ArxiuEstatEnumDto arxiuEstat = ArxiuEstatEnumDto.ESBORRANY;
			contingutHelper.arxiuPropagarModificacio(
					documentEntity,
					fitxer,
					documentFirmaTipus,
					firmes,
					arxiuEstat);
		}

		return dto;
	}
	
	private FitxerDto fitxer(
			boolean newFitxer,
			DocumentEntity documentEntity,
			DocumentDto document,
			Document arxiuDocument) {
		FitxerDto fitxer = null;
		if (newFitxer) {
			fitxer = getFitxerNew(
					documentEntity,
					document);
		} else {
			fitxer = getFitxerExisting(
					documentEntity,
					arxiuDocument);

		}
		return fitxer;
	}
	
	private FitxerDto getFitxerNew(
			DocumentEntity documentEntity,
			DocumentDto document) {
		FitxerDto fitxer = null;
		fitxer = new FitxerDto(
				document.getFitxerNom(),
				document.getFitxerContentType(),
				document.getFitxerContingut());

		actualitzarFitxerDB(
				documentEntity,
				fitxer);

		return fitxer;
	}
	
	private FitxerDto getFitxerExisting(
			DocumentEntity documentEntity,
			Document arxiuDocument) {
		FitxerDto fitxer = null;

		fitxer = new FitxerDto(
				documentEntity.getFitxerNom(),
				documentEntity.getFitxerContentType(),
				getContingutFromArxiuDocument(arxiuDocument));
		
		return fitxer;
	}
	
	
	
	
	
	
	public boolean updateTipusDocumentDocument(
			Long entitatId,
			DocumentEntity documentEntity,
			Long metaDocumentId,
			boolean comprovarMetaExpedient) {

		MetaDocumentEntity metaDocument = null;
		if (metaDocumentId != null) {
			metaDocument = entityComprovarHelper.comprovarMetaDocument(
					documentEntity.getEntitat(),
					documentEntity.getMetaDocument() != null ? documentEntity.getMetaDocument().getMetaExpedient() : null,
					metaDocumentId,
					false,
					comprovarMetaExpedient);
		} 
//		else {
//			throw new ValidationException(
//					documentEntity.getId(),
//					DocumentEntity.class,
//					"No es pot actualitzar un document sense un meta-document associat");
//		}
		if (!isModificacioCustodiatsActiva() && (
				documentEntity.getEstat().equals(DocumentEstatEnumDto.CUSTODIAT) || 
				documentEntity.getEstat().equals(DocumentEstatEnumDto.FIRMAT) ||
				documentEntity.getEstat().equals(DocumentEstatEnumDto.FIRMA_PARCIAL)) && !documentEntity.isDocFromAnnex()) {
			throw new ValidationException(
					documentEntity.getId(),
					DocumentEntity.class,
					"No es pot actualitzar un document custodiat");
		}
		if (metaDocument != null) {
			documentEntity.updateTipusDocument(
					metaDocument,
					metaDocument.getNtiOrigen(),
					metaDocument.getNtiEstadoElaboracion(),
					metaDocument.getNtiTipoDocumental());
		} 

		cacheHelper.evictErrorsValidacioPerNode(documentEntity);
		cacheHelper.evictErrorsValidacioPerNode(documentEntity.getExpedient());
		// Registra al log la modificació del document
		contingutLogHelper.log(
				documentEntity,
				LogTipusEnumDto.MODIFICACIO,
				documentEntity.getNom(),
				null,
				true,
				true);
		
		
		if (pluginHelper.getPropertyArxiuMetadadesAddicionalsActiu()) {
		
			FitxerDto fitxer = null;
			List<ArxiuFirmaDto> firmes = null;
			if (documentEntity.getArxiuUuid() != null) {
				fitxer = new FitxerDto();
				fitxer.setContentType(documentEntity.getFitxerContentType());
				fitxer.setNom(documentEntity.getFitxerNom());
				if (pluginHelper.getArxiuPlugin() instanceof ArxiuPluginCaib) {
					Document arxiuDocument = pluginHelper.arxiuDocumentConsultar(
							documentEntity,
							null,
							null,
							true,
							false);
					fitxer.setContingut(getContingutFromArxiuDocument(arxiuDocument));
			//		##no validar firma en actualitzar tipus document
			//		if (documentEntity.isFirmat()) {
			//			firmes = validaFirmaDocument(
			//					documentEntity, 
			//					fitxer,
			//					null);
			//		}
				}
			}
			ArxiuEstatEnumDto arxiuEstat = ArxiuEstatEnumDto.ESBORRANY;
			contingutHelper.arxiuPropagarModificacio(
					documentEntity,
					fitxer,
					DocumentFirmaTipusEnumDto.SENSE_FIRMA, //##no validar firma en actualitzar tipus document
					firmes,
					arxiuEstat);
			return true;
		} else {
			return true;
		}
		
	}

	public DocumentEntity crearDocumentDB(
			DocumentTipusEnumDto documentTipus,
			String nom,
			String descripcio,
			Date data,
			Date dataCaptura,
			String ntiOrgano,
			NtiOrigenEnumDto ntiOrigen,
			DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			String ntiTipoDocumental,
			MetaDocumentEntity metaDocument,
			ContingutEntity pare,
			EntitatEntity entitat,
			ExpedientEntity expedient,
			String ubicacio,
			String ntiIdDocumentoOrigen,
			String pinbalIdpeticion) {
		return crearDocumentDB(documentTipus, nom, descripcio, data, dataCaptura, ntiOrgano, ntiOrigen, ntiEstadoElaboracion, ntiTipoDocumental, metaDocument, pare, entitat, expedient, ubicacio, ntiIdDocumentoOrigen, pinbalIdpeticion, true, null, ArxiuEstatEnumDto.DEFINITIU);
	}

	public DocumentEntity crearDocumentDB(
			DocumentTipusEnumDto documentTipus,
			String nom,
			String descripcio,
			Date data,
			Date dataCaptura,
			String ntiOrgano,
			NtiOrigenEnumDto ntiOrigen,
			DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			String ntiTipoDocumental,
			MetaDocumentEntity metaDocument,
			ContingutEntity pare,
			EntitatEntity entitat,
			ExpedientEntity expedient,
			String ubicacio,
			String ntiIdDocumentoOrigen,
			String pinbalIdpeticion,
			boolean validacioFirmaCorrecte,
			String validacioFirmaErrorMsg,
			ArxiuEstatEnumDto annexArxiuEstat) {
		DocumentEntity documentCrear = DocumentEntity.getBuilder(
				documentTipus,
				DocumentEstatEnumDto.REDACCIO,
				nom,
				descripcio,
				data,
				dataCaptura,
				ntiIdDocumentoOrigen,
				"1.0",
				ntiOrgano,
				ntiOrigen,
				ntiEstadoElaboracion,
				ntiTipoDocumental,
				metaDocument,
				pare,
				entitat,
				expedient).
				ubicacio(ubicacio).
				pinbalIdpeticion(pinbalIdpeticion).
				validacioFirmaCorrecte(validacioFirmaCorrecte).
				validacioFirmaErrorMsg(validacioFirmaErrorMsg).
				annexArxiuEstat(annexArxiuEstat).
				build();
		DocumentEntity documentCreat = documentRepository.save(documentCrear);
		calcularIdentificadorDocument(
				documentCreat,
				entitat.getUnitatArrel());
		if (expedient != null) {
			cacheHelper.evictErrorsValidacioPerNode(expedient);
		}
		return documentCreat;
	}
	
	public void actualitzarEstat(DocumentEntity document, DocumentEstatEnumDto nouEstat) {
		
		if ((document.getEstat().equals(DocumentEstatEnumDto.FIRMA_PARCIAL) || document.getEstat().equals(DocumentEstatEnumDto.REDACCIO) || document.getEstat().equals(DocumentEstatEnumDto.ADJUNT_FIRMAT)) && !document.getDocumentTipus().equals(DocumentTipusEnumDto.IMPORTAT)) {
			document.updateEstat(nouEstat);
		}
		if (nouEstat.equals(DocumentEstatEnumDto.DEFINITIU)) {
			document.updateArxiuEstat(ArxiuEstatEnumDto.DEFINITIU);
		}
		
		if (isPropagarConversioDefinitiuActiu() && nouEstat.equals(DocumentEstatEnumDto.DEFINITIU)) {
			FitxerDto fitxer = null;
			if (document.getArxiuUuid() != null) {
				fitxer = new FitxerDto();
				fitxer.setContentType(document.getFitxerContentType());
				fitxer.setNom(document.getFitxerNom());
			}
			ArxiuEstatEnumDto arxiuEstat = ArxiuEstatEnumDto.DEFINITIU;
			contingutHelper.arxiuPropagarModificacio(
					document,
					fitxer,
					DocumentFirmaTipusEnumDto.SENSE_FIRMA,
					null,
					arxiuEstat);
		}
		contingutLogHelper.log(
				document,
				LogTipusEnumDto.CANVI_ESTAT,
				nouEstat.name(),
				null,
				false,
				false);
	}
	
	public void actualitzarEstatADefinititu(DocumentEntity documentEntity) {
		

		Document arxiuDocument = pluginHelper.arxiuDocumentConsultar(
				documentEntity,
				null,
				null,
				true,
				false);
		
		DocumentFirmaTipusEnumDto documentFirmaTipus = getDocumentFirmaTipus(documentEntity.getNtiTipoFirma());
		
		FitxerDto fitxer = null;
		List<ArxiuFirmaDto> firmes = null;
		
		if (documentFirmaTipus == DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA) {
			
			fitxer = getFitxerExisting(
					documentEntity,
					arxiuDocument);
			
			firmes = validaFirmaDocument(
					documentEntity, 
					fitxer,
					null, 
					false, 
					true);
			
			
		} else  if (documentFirmaTipus == DocumentFirmaTipusEnumDto.FIRMA_SEPARADA) {
			
			fitxer = getFitxerExisting(
					documentEntity,
					arxiuDocument);
			
			byte[]  firmaContingut = getFirmaDetachedFromArxiuDocument(arxiuDocument);
			
			firmes = validaFirmaDocument(
					documentEntity, 
					fitxer,
					firmaContingut, 
					false, 
					true);
			

		}

		contingutHelper.arxiuPropagarModificacio(
				documentEntity,
				fitxer,
				documentFirmaTipus,
				firmes,
				ArxiuEstatEnumDto.DEFINITIU);
	
		
	}
	
	
	public DocumentFirmaTipusEnumDto getDocumentFirmaTipus(DocumentNtiTipoFirmaEnumDto documentNtiTipoFirmaEnumDto) {
		
		DocumentFirmaTipusEnumDto documentFirmaTipus = null;
		if (documentNtiTipoFirmaEnumDto == null) {
			documentFirmaTipus = DocumentFirmaTipusEnumDto.SENSE_FIRMA;
		} else if (documentNtiTipoFirmaEnumDto == DocumentNtiTipoFirmaEnumDto.TF04) {
			documentFirmaTipus = DocumentFirmaTipusEnumDto.FIRMA_SEPARADA;
		} else {
			documentFirmaTipus = DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA;
		}
		return documentFirmaTipus;
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
				false, null, false, null, false, 0, null, null, true);
	}



	public void actualitzarVersionsDocument(
			DocumentEntity document) {
		if (pluginHelper.arxiuSuportaVersionsDocuments()) {
			try {
				List<ContingutArxiu> versions = pluginHelper.arxiuDocumentObtenirVersions(
						document);
				if (versions != null) {
					ContingutArxiu darreraVersio = null;
					Float versioNum = null;
					for (ContingutArxiu versio: versions) {
						if (versioNum == null || new Float(versio.getVersio()).floatValue() > versioNum.floatValue()) {
							versioNum = new Float(versio.getVersio());
							darreraVersio = versio;
						}
					}
					document.updateVersio(
							darreraVersio.getVersio(),
							versions.size());
				}
			} catch (Exception ex) {
				logger.error(
						"Error al actualitzar les versions del document (" + 
						"entitatId=" + document.getEntitat().getId() + ", " +
						"documentId=" + document.getId() + ", " +
						"documentTitol=" + document.getNom() + ")",
						ex);
			}
		}
	}
	
	////
	// FITXER DEL DOCUMENT - ARXIU
	////
	
	public void actualitzarFitxerDB(
			DocumentEntity document,
			FitxerDto fitxer) {
		document.updateFitxer(
				fitxer.getNom(),
				fitxer.getContentType(),
				null);
	}

	public FitxerDto getFitxerAssociat(
			DocumentEntity document,
			String versio) {
		FitxerDto fitxer = null;
		if (document.getArxiuUuid() != null) {
			
			if (document.getGesDocFirmatId() != null && !document.getGesDocFirmatId().isEmpty()) {
				
				ByteArrayOutputStream streamAnnex = new ByteArrayOutputStream();
				pluginHelper.gestioDocumentalGet(
						document.getGesDocFirmatId(),
						PluginHelper.GESDOC_AGRUPACIO_DOCS_FIRMATS_PORTAFIB,
						streamAnnex);
				fitxer = new FitxerDto();
				fitxer.setContingut(streamAnnex.toByteArray());
				fitxer.setNom(document.getNomFitxerFirmat());
				fitxer.setContentType(document.getFitxerContentType());
			} else {
				
                String fitxerNom = document.getFitxerNom();
                String fitxerFirmatNom = document.getNomFitxerFirmat();
				fitxer = new FitxerDto();
                fitxer.setContentType(fitxerFirmatNom != null ? "application/pdf" : document.getFitxerContentType());
                fitxer.setNom(fitxerFirmatNom != null ? fitxerFirmatNom : fitxerNom);
				Document arxiuDocument = pluginHelper.arxiuDocumentConsultar(
						document,
						null,
						versio,
						true,
						false);
				fitxer.setContingut(getContingutFromArxiuDocument(arxiuDocument));
			}

		} else {
			fitxer = new FitxerDto();

			fitxer.setNom(document.getFitxerNom());
			fitxer.setContentType(document.getFitxerContentType());
//			fitxer.setContingut(document.getFitxerContingut());

			ByteArrayOutputStream streamAnnex = new ByteArrayOutputStream();
			pluginHelper.gestioDocumentalGet(
					document.getGesDocAdjuntId(),
					PluginHelper.GESDOC_AGRUPACIO_DOCS_ADJUNTS,
					streamAnnex);
			fitxer.setContingut(streamAnnex.toByteArray());
			
		
		}
//		if (versio == null && DocumentEstatEnumDto.CUSTODIAT.equals(document.getEstat())) {
//			fitxer.setNom(
//					pluginHelper.conversioConvertirPdfArxiuNom(
//							document.getFitxerNom()));
//		}
		return fitxer;
	}
	
	public FitxerDto getFitxerAssociatFirmat(
			DocumentEntity document,
			String versio) {
		FitxerDto fitxer = null;
		if (document.getArxiuUuid() != null) {
			fitxer = new FitxerDto();
			fitxer.setContentType(document.getFitxerContentType());
			fitxer.setNom(document.getFitxerNom());
			Document arxiuDocument = pluginHelper.arxiuDocumentConsultar(
					document,
					null,
					versio,
					true,
					false);
			fitxer.setContingut(getContingutFromArxiuDocument(arxiuDocument));

		} else {
			fitxer = new FitxerDto();
			if (document.getGesDocFirmatId() != null && !document.getGesDocFirmatId().isEmpty()) {
				ByteArrayOutputStream streamAnnex = new ByteArrayOutputStream();
				pluginHelper.gestioDocumentalGet(
						document.getGesDocFirmatId(),
						PluginHelper.GESDOC_AGRUPACIO_DOCS_FIRMATS_PORTAFIB,
						streamAnnex);
				fitxer.setContingut(streamAnnex.toByteArray());
				fitxer.setNom(document.getNomFitxerFirmat());
				fitxer.setContentType(document.getFitxerContentType());
			} else {
				fitxer.setNom(document.getFitxerNom());
				fitxer.setContentType(document.getFitxerContentType());
				fitxer.setContingut(document.getFitxerContingut());
			}
			
		}
		return fitxer;
	}
	
	
	public List<DocumentEntity> findDocumentsNoFirmatsOAmbFirmaInvalida(
			Long entitatId,
			Long expedientId) {
		logger.debug("Obtenint els documents no firmats o amb firma invalida (" +
				"entitatId=" + entitatId + ", " +
				"expedientId=" + expedientId + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				false,
				false,
				false,
				false, 
				false, 
				null);
		List<DocumentEntity> documents = documentRepository.findByExpedientAndEsborrat(expedient, 0);
		List<DocumentEntity> documentsChosen = new ArrayList<DocumentEntity>();
		for (DocumentEntity document: documents) {
			if (document.getEstat() == DocumentEstatEnumDto.REDACCIO || document.getArxiuUuid() == null) {
				documentsChosen.add(document);
			}
		}
		return documentsChosen;
	}
	

	@SuppressWarnings("incomplete-switch")
	public byte[] getContingutFromArxiuDocument(Document arxiuDocument) {
		byte[] contingut = null;
		if (arxiuDocument.getFirmes() == null) {
			contingut = arxiuDocument.getContingut().getContingut();
		} else {
			for (Firma firma: arxiuDocument.getFirmes()) {
				if (firma.getTipus() != FirmaTipus.CSV) {
					switch(firma.getTipus()) {
					case CADES_ATT:
					case XADES_ENV:
					case PADES:
					case ODT:
					case OOXML:
					case SMIME:
						contingut = firma.getContingut();
						break;
					case CADES_DET:
					case XADES_DET:
						contingut = arxiuDocument.getContingut().getContingut();
						break;
					}
				}
			}
		}
		return contingut;
	}
	@SuppressWarnings("incomplete-switch")
	public byte[] getFirmaDetachedFromArxiuDocument(Document arxiuDocument) {
		byte[] firmaDetached = null;
		if (arxiuDocument.getFirmes() != null) {
			for (Firma firma: arxiuDocument.getFirmes()) {
				if (firma.getTipus() != FirmaTipus.CSV) {
					switch(firma.getTipus()) {
					case CADES_DET:
					case XADES_DET:
						firmaDetached = firma.getContingut();
						break;
					}
				}
			}
		}
		return firmaDetached;
	}

	public void calcularIdentificadorDocument(
			DocumentEntity document,
			String organCodi) {
		int any = Calendar.getInstance().get(Calendar.YEAR);
		UUID uuid = UUID.randomUUID();
		ByteBuffer bb = ByteBuffer.wrap(new byte[20]);
		bb.putLong(uuid.getMostSignificantBits());
		bb.putLong(uuid.getLeastSignificantBits());
		Random rand = new Random(System.currentTimeMillis());
		bb.putInt(rand.nextInt());
		String identificador = "ES_" + organCodi + "_" + any + "_" +  new String(Base64.encode(bb.array()));
		document.updateNtiIdentificador(identificador);
	}

	public String getExtensioArxiu(String arxiuNom) {
		int indexPunt = arxiuNom.lastIndexOf(".");
		if (indexPunt != -1 && indexPunt < arxiuNom.length() - 1) {
			return arxiuNom.substring(indexPunt + 1);
		} else {
			return null;
		}
	}


	////
	// COMPROVACIONS PERMISOS
	////

	public DocumentEntity comprovarDocumentDinsExpedientModificable(
			Long entitatId,
			Long id,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			boolean comprovarPermisCreate,
			boolean comprovarPermisDelete, 
			boolean checkPerMassiuAdmin, 
			String rolActual) {
		NodeEntity node = contingutHelper.comprovarNodeDinsExpedientModificable(
				entitatId,
				id,
				comprovarPermisRead,
				comprovarPermisWrite,
				comprovarPermisCreate,
				comprovarPermisDelete, 
				checkPerMassiuAdmin, 
				rolActual);
		if (!ContingutTipusEnumDto.DOCUMENT.equals(node.getTipus())) {
			throw new ValidationException(
					id,
					DocumentEntity.class,
					"El contingut especificat no és un document");
		}
		return (DocumentEntity)node;
	}

	public DocumentEntity comprovarDocumentDinsExpedientAccessible(
			Long entitatId,
			Long id,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite) {
		NodeEntity node = contingutHelper.comprovarNodeDinsExpedientAccessible(
				entitatId,
				id,
				comprovarPermisRead,
				comprovarPermisWrite);
		if (!ContingutTipusEnumDto.DOCUMENT.equals(node.getTipus())) {
			throw new ValidationException(
					id,
					DocumentEntity.class,
					"El contingut especificat no és un document");
		}
		return (DocumentEntity)node;
	}
	
	
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public Exception guardarDocumentArxiu(
			Long docId) {
		
		logger.info("Guardar document arxiu (id=" + docId + ", entitatCodi=" + configHelper.getEntitatActualCodi() + ")");
		Exception exception = null;

		DocumentEntity documentEntity = documentRepository.findOne(docId);
		if (documentEntity.getExpedient().getArxiuUuid() != null) {
			
			if (documentEntity.getArxiuUuid() != null) { // concurrency check
				throw new RuntimeException("El document ja s'ha guardat en arxiu per otra persona o el process en segon pla");
			}
			expedientHelper.concurrencyCheckExpedientJaTancat(documentEntity.getExpedient());
			
			try {
				FitxerDto fitxer = new FitxerDto();
				fitxer.setNom(documentEntity.getFitxerNom());
				fitxer.setContentType(documentEntity.getFitxerContentType());

				ByteArrayOutputStream streamAnnex = new ByteArrayOutputStream();
				pluginHelper.gestioDocumentalGet(
						documentEntity.getGesDocAdjuntId(),
						PluginHelper.GESDOC_AGRUPACIO_DOCS_ADJUNTS,
						streamAnnex);
				fitxer.setContingut(streamAnnex.toByteArray());
				
				List<ArxiuFirmaDto> firmes = null;
				if (documentEntity.getEstat() == DocumentEstatEnumDto.ADJUNT_FIRMAT) {
					byte[] firmaSeparada = null;
					if (documentEntity.getGesDocAdjuntFirmaId() != null) {
						ByteArrayOutputStream streamAnnex1 = new ByteArrayOutputStream();
						pluginHelper.gestioDocumentalGet(
								documentEntity.getGesDocAdjuntFirmaId(),
								PluginHelper.GESDOC_AGRUPACIO_DOCS_ADJUNTS,
								streamAnnex1);
						firmaSeparada = streamAnnex1.toByteArray();
					}
					firmes = validaFirmaDocument(
							documentEntity,
							fitxer,
							firmaSeparada, 
							false, 
							true);
				}
				
				DocumentFirmaTipusEnumDto documentFirmaTipus;
				if (documentEntity.getEstat() != DocumentEstatEnumDto.ADJUNT_FIRMAT) {
					documentFirmaTipus = DocumentFirmaTipusEnumDto.SENSE_FIRMA;
				} else if (documentEntity.getGesDocAdjuntFirmaId() == null) {
					documentFirmaTipus = DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA;
				} else {
					documentFirmaTipus = DocumentFirmaTipusEnumDto.FIRMA_SEPARADA;
				}
				
		
				ArxiuEstatEnumDto arxiuEstat = ArxiuEstatEnumDto.ESBORRANY;
				contingutHelper.arxiuPropagarModificacio(
						documentEntity,
						fitxer,
						documentFirmaTipus,
						firmes,
						arxiuEstat);
			
				if (documentEntity.getGesDocAdjuntId() != null ) {
					pluginHelper.gestioDocumentalDelete(
							documentEntity.getGesDocAdjuntId(),
							PluginHelper.GESDOC_AGRUPACIO_DOCS_ADJUNTS);
					documentEntity.setGesDocAdjuntId(null);
				}
				if (documentEntity.getGesDocAdjuntFirmaId() != null ) {
					pluginHelper.gestioDocumentalDelete(
							documentEntity.getGesDocAdjuntFirmaId(),
							PluginHelper.GESDOC_AGRUPACIO_DOCS_ADJUNTS);
					documentEntity.setGesDocAdjuntFirmaId(null);
				}
			} catch (Exception ex) {
				logger.error("Error al custodiar en arxiu document adjunt  (" +
						"id=" + documentEntity.getId() + ")",
						ex);

				Throwable e = ExceptionHelper.findThrowableInstance(ex, SistemaExternException.class, 3);
				if (e != null) {
					exception = (Exception) e;
				} else {
					exception = (Exception) ExceptionUtils.getRootCause(ex);
					if (exception == null)
						exception = ex;
				}
			}
		} else {
			exception = new RuntimeException("Expedient de aquest document no es guardat en arxiu");
		}

		documentEntity.updateArxiuIntent();
		return exception;
	}
	

	public List<ArxiuFirmaDto> validaFirmaDocument(
			DocumentEntity document,
			FitxerDto fitxer,
			byte[] contingutFirma, 
			boolean updateEstat, 
			boolean throwExceptionIfNotValid) {
		logger.debug("Recuperar la informació de les firmes amb el plugin ValidateSignature ("
				+ "documentID=" + document.getId() + ")");
		List<ArxiuFirmaDto> firmes = pluginHelper.validaSignaturaObtenirFirmes(
				fitxer.getContingut(), 
				(contingutFirma != null && contingutFirma.length > 0) ? contingutFirma : null,
				fitxer.getContentType(), 
				throwExceptionIfNotValid);
		if (updateEstat) {
			document.updateEstat(DocumentEstatEnumDto.FIRMAT);
		}
		contingutLogHelper.log(
				document,
				LogTipusEnumDto.DOC_FIRMAT,
				null,
				null,
				false,
				false);
		return firmes;
	}

	public boolean hasFillsEsborranys(
			ExpedientEntity expedient) {
		logger.debug("Consulta els documents esborranys d'un expedient");
		List<DocumentEntity> documents = documentRepository.findByExpedientAndEsborrat(expedient, 0);
		for (DocumentEntity document : documents) {
			if (document.getEsborrat() == 0 
					&& document.getDocumentTipus().equals(DocumentTipusEnumDto.DIGITAL)
					&& document.getEstat().equals(DocumentEstatEnumDto.REDACCIO)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasAllDocumentsDefinitiu(
			ExpedientEntity expedient) {
		List<DocumentEntity> documents = documentRepository.findByExpedientAndEsborrat(expedient, 0);
		for (DocumentEntity document : documents) {
			if (document.getDocumentTipus().equals(DocumentTipusEnumDto.DIGITAL) && 
					!(document.getEstat().equals(DocumentEstatEnumDto.CUSTODIAT) || document.getEstat().equals(DocumentEstatEnumDto.DEFINITIU))) {
				return false;
			}
		}
		return true;
	}

	public boolean hasAnyDocumentDefinitiu(
			ExpedientEntity expedient) {
		List<DocumentEntity> documents = documentRepository.findByExpedientAndEsborrat(expedient, 0);
		for (DocumentEntity document : documents) {
			if (document.getEsborrat() == 0 
					&& (document.getDocumentTipus().equals(DocumentTipusEnumDto.DIGITAL) || document.getDocumentTipus().equals(DocumentTipusEnumDto.IMPORTAT))
					&& (document.getEstat().equals(DocumentEstatEnumDto.CUSTODIAT) || document.getEstat().equals(DocumentEstatEnumDto.DEFINITIU))) {
				return true;
			}
		}
		return false;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<DocumentDto> findByArxiuUuid(String arxiuUuid) {
		List<DocumentDto> documentsDto = new ArrayList<DocumentDto>();
		List<DocumentEntity> documents = documentRepository.findByArxiuUuidAndEsborrat(arxiuUuid, 0);
		for (DocumentEntity document : documents) {
			documentsDto.add(
					(DocumentDto)contingutHelper.toContingutDto(
							document, 
							false, 
							false, 
							false, 
							false, 
							true, 
							false, 
							false, null, false, null, false, 0, null, null, true));
			
		}
		return documentsDto;
	}

	public boolean isModificacioCustodiatsActiva() {
		return configHelper.getAsBoolean("es.caib.ripea.document.modificar.custodiats");
	}
	public boolean isPropagarConversioDefinitiuActiu() {
		return configHelper.getAsBoolean("es.caib.ripea.conversio.definitiu.propagar.arxiu");
	}
	public boolean isPropagarModificacioDefinitiusActiva() {
		return configHelper.getAsBoolean("es.caib.ripea.document.propagar.modificacio.arxiu");
	}
	private static final Logger logger = LoggerFactory.getLogger(DocumentHelper.class);

}
