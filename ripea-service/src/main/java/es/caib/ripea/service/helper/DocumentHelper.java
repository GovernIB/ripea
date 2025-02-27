package es.caib.ripea.service.helper;

import com.sun.jersey.core.util.Base64;
import es.caib.plugins.arxiu.api.*;
import es.caib.plugins.arxiu.caib.ArxiuPluginCaib;
import es.caib.ripea.persistence.repository.DocumentRepository;
import es.caib.ripea.persistence.entity.*;
import es.caib.ripea.service.firma.DocumentFirmaAppletHelper;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.exception.ArxiuJaGuardatException;
import es.caib.ripea.service.intf.exception.ValidacioFirmaException;
import es.caib.ripea.service.intf.exception.ValidationException;
import es.caib.ripea.service.intf.utils.Utils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.zip.ZipOutputStream;

@Component
public class DocumentHelper {

	@Autowired private DocumentRepository documentRepository;
	@Autowired private ContingutHelper contingutHelper;
	@Autowired private ContingutLogHelper contingutLogHelper;
	@Autowired private PluginHelper pluginHelper;
	@Autowired private CacheHelper cacheHelper;
	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private ConfigHelper configHelper;
	@Autowired private ExpedientHelper expedientHelper;
	@Autowired private OrganGestorHelper organGestorHelper;
	@Autowired private DocumentFirmaAppletHelper firmaAppletHelper;
	
	public DocumentDto crearDocument(
			DocumentDto document,
			ContingutEntity pare,
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument,
			boolean returnDetail) {
		
		//Casos en que han adjuntat document original i document firmat, pero el firmat ja conté el original (firmes attached)
		if (document.getFitxerContingut()!=null && 
			document.getFirmaContingut()!=null && 
			document.getFirmaContingut().length>document.getFitxerContingut().length) {
			//Han adjuntat un XML, i el Xsig del arxiu firmat ja conté tot el contingut del XML original mes la firma
			boolean isXsig = document.getFirmaNom().endsWith(".xsig");
			boolean isCsig = document.getFirmaNom().endsWith(".csig");
			if (isXsig || isCsig) {
				throw new ValidacioFirmaException("validacio.firma.contingut.exception");
				/*document.copiaDadesFirmaAFitxer();
				if (isXsig) {
					//Los archivos con extensión .XSIG son archivos de firma digital en formato XAdES (XML Advanced Electronic Signatures). 
					//El content-type correspondiente para estos archivos es generalmente application/xml
					//https://fileinfobase.com/es/extension/xsighttps://amazingalgorithms.com/file-extensions/xsig/.
					document.setFitxerContentType("application/xml");
				}*/
			}
		} else if (document.getFitxerNom()!=null && document.getFitxerNom().endsWith(".xsig")) {
			//Corregim el content type assignat per el MultipartFormData, de application/octet-stream --> application/xml
			//Igualant així com ens arriben els arxius XSIG de distribució
			document.setFitxerContentType("application/xml");
		}
		
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
		
		DocumentFirmaTipusEnumDto documentFirmaTipus;
		if (!document.isAmbFirma()) {
			documentFirmaTipus = DocumentFirmaTipusEnumDto.SENSE_FIRMA;
		} else if (!document.isFirmaSeparada()) {
			documentFirmaTipus = DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA;
		} else {
			documentFirmaTipus = DocumentFirmaTipusEnumDto.FIRMA_SEPARADA;
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
				document.getPinbalIdpeticion(), 
				documentFirmaTipus, 
				expedient.getEstatAdditional());
		
		entity.setIdioma(document.getIdioma());
		entity.setResolucion(document.getResolucion());
		
		FitxerDto fitxer = new FitxerDto(
				document.getFitxerNom(),
				document.getFitxerContentType(),
				document.getFitxerContingut());

		actualitzarFitxerDB(entity, fitxer);
		
		if (document.isScanned()) {
			contingutLogHelper.log(
					entity,
					LogTipusEnumDto.ESCANEIG,
					entity.getNom(),
					null,
					true,
					true);
		}
		
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
		} else {
			throw new ValidationException("No es pot crear el document sense especificar el contingut");
		}
		
		String gestioDocumentalAdjuntFirmaId = document.getGesDocAdjuntFirmaId();
		if (documentFirmaTipus == DocumentFirmaTipusEnumDto.FIRMA_SEPARADA) {
			if (document.getFirmaContingut() != null) {
				gestioDocumentalAdjuntFirmaId = pluginHelper.gestioDocumentalCreate(
						PluginHelper.GESDOC_AGRUPACIO_DOCS_ADJUNTS,
						new ByteArrayInputStream(document.getFirmaContingut()));
				entity.setGesDocAdjuntFirmaId(gestioDocumentalAdjuntFirmaId);
			} else {
				throw new ValidationException("No es pot crear el document amb firma separada sense especificar el contingut d'aquesta firma");
			}
		}
		
		ArxiuEstatEnumDto arxiuEstat = getArxiuEstat(documentFirmaTipus, null);
		
		try {
			if (entity.getExpedient().getArxiuUuid() != null && expedient.getTancatData()==null) {
				
				List<ArxiuFirmaDto> firmes = null;
				if (isDocumentFromPinbal(entity)) {
					
					ArxiuFirmaDto firma = getArxiuFirmaPades(fitxer.getNom(), fitxer.getContingut());
					firmes = Arrays.asList(firma);
					
				} else if (document.isAmbFirma()) {
					try {
						firmes = validaFirmaDocument(
								entity, 
								fitxer,
								document.getFirmaContingut(), 
								false, 
								true);
					} catch (Exception e) {
						throw new ValidacioFirmaException(e.getMessage());
					}
				}

				if (arxiuEstat == ArxiuEstatEnumDto.ESBORRANY && documentFirmaTipus == DocumentFirmaTipusEnumDto.FIRMA_SEPARADA) {
					pluginHelper.arxiuPropagarFirmaSeparada(
							entity,
							firmes.get(0).getFitxer());
				}
				
				contingutHelper.arxiuPropagarModificacio(
						entity,
						fitxer,
						arxiuEstat == ArxiuEstatEnumDto.ESBORRANY ? DocumentFirmaTipusEnumDto.SENSE_FIRMA : documentFirmaTipus,
						firmes,
						arxiuEstat);
				
				if (gestioDocumentalAdjuntId != null ) {
					pluginHelper.gestioDocumentalDelete(
							gestioDocumentalAdjuntId,
							PluginHelper.GESDOC_AGRUPACIO_DOCS_ADJUNTS);
					entity.setGesDocAdjuntId(null);
				}
			
				if (gestioDocumentalAdjuntFirmaId != null) {
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
			
 			if (ex instanceof ValidacioFirmaException && !configHelper.getAsBoolean(PropertyConfig.DETECCIO_FIRMA_AUTOMATICA)) {
 				throw new ValidationException(
 						document.getId(),
 						DocumentEntity.class,
 						ex.getMessage());
 			}
		}

		entity.updateArxiuIntent(true);

		if (returnDetail)		
			dto = toDocumentDto(entity);
		else
			dto.setId(entity.getId());

		return dto;
	}

	public ArxiuEstatEnumDto getArxiuEstat(DocumentFirmaTipusEnumDto documentFirmaTipus, DocumentEstatEnumDto estatAnterior) {
		boolean isFirmatPujatArxiu = documentFirmaTipus != DocumentFirmaTipusEnumDto.SENSE_FIRMA && isFirmatPujatManualmentDefinitu();
		boolean isEsborranyConvertit = documentFirmaTipus == DocumentFirmaTipusEnumDto.SENSE_FIRMA && (estatAnterior != null && estatAnterior.equals(DocumentEstatEnumDto.DEFINITIU));
		
		return (isFirmatPujatArxiu || isEsborranyConvertit) ? ArxiuEstatEnumDto.DEFINITIU : ArxiuEstatEnumDto.ESBORRANY;
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
		
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(documentEntity.getId()));

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
		if (!isModificacioCustodiatsActiva() && documentEntity.isArxiuEstatDefinitiu()) {
			throw new ValidationException(
					documentEntity.getId(),
					DocumentEntity.class,
					"No es pot actualitzar un document definitiu");
		}
		if (documentEntity.getEstat().equals(DocumentEstatEnumDto.FIRMA_PENDENT)) {
			throw new ValidationException(
					documentEntity.getId(),
					DocumentEntity.class,
					"No es pot modificar un document enviat a portafirmes");
		}

		contingutHelper.comprovarNomValid(
				documentEntity.getPare(),
				document.getNom(),
				documentEntity.getId(),
				DocumentEntity.class);
		cacheHelper.evictErrorsValidacioPerNode(documentEntity);
		cacheHelper.evictErrorsValidacioPerNode(documentEntity.getExpedient());
		
		
		String nomOriginal = documentEntity.getNom();
		
		DocumentFirmaTipusEnumDto documentFirmaTipus;
		if (!document.isAmbFirma()) {
			documentFirmaTipus = DocumentFirmaTipusEnumDto.SENSE_FIRMA;
		} else if (!document.isFirmaSeparada()) {
			documentFirmaTipus = DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA;
		} else {
			documentFirmaTipus = DocumentFirmaTipusEnumDto.FIRMA_SEPARADA;
		}
		
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
				document.getNtiCsvRegulacion(), 
				documentFirmaTipus);
		FitxerDto fitxer = null;

		Document arxiuDocument = null;
		if (documentEntity.getArxiuUuid() != null) {
			boolean ambContingut = isEnviarContingutExistentActiu();
			arxiuDocument = pluginHelper.arxiuDocumentConsultar(
					documentEntity,
					null,
					null,
					ambContingut,
					false);
		}

		boolean newFitxer = document.getFitxerContingut() != null;
		boolean newFirma = document.getFirmaContingut() != null;
		DocumentEstatEnumDto estatAnterior = documentEntity.getEstat();
		
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
			
			// No tornar a validar firma si el document està definitiu a l'arxiu
			if (!isDocumentDefinitiuPujatArxiu(estatAnterior)) {
				firmes = validaFirmaDocument(
						documentEntity, 
						fitxer,
						null, 
						false, 
						true);
			}
			
			documentEntity.updateEstat(DocumentEstatEnumDto.FIRMAT);
			
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
			
			// No tornar a validar firma si el document està definitiu a l'arxiu
			if (!isDocumentDefinitiuPujatArxiu(estatAnterior)) {
				firmes = validaFirmaDocument(
						documentEntity, 
						fitxer,
						firmaContingut, 
						false, 
						true);
			}
			documentEntity.updateEstat(DocumentEstatEnumDto.FIRMAT);
			
		}
		
		if ((estatAnterior.equals(DocumentEstatEnumDto.FIRMA_PENDENT) || estatAnterior.equals(DocumentEstatEnumDto.DEFINITIU) || estatAnterior.equals(DocumentEstatEnumDto.CUSTODIAT)) && isConversioDefinitiuActiu()) {
			documentEntity.updateEstat(estatAnterior);
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

			ArxiuEstatEnumDto arxiuEstat = getArxiuEstat(documentFirmaTipus, estatAnterior);

			if (arxiuEstat == ArxiuEstatEnumDto.ESBORRANY && documentFirmaTipus == DocumentFirmaTipusEnumDto.FIRMA_SEPARADA) {
				pluginHelper.arxiuPropagarFirmaSeparada(
						documentEntity,
						firmes.get(0).getFitxer());
			}
			contingutHelper.arxiuPropagarModificacio(
					documentEntity,
					fitxer,
					arxiuEstat == ArxiuEstatEnumDto.ESBORRANY ? DocumentFirmaTipusEnumDto.SENSE_FIRMA : documentFirmaTipus,
					firmes,
					arxiuEstat);
			
			
		}

		return dto;
	}
	
	private boolean isDocumentDefinitiuPujatArxiu(DocumentEstatEnumDto estatDocument) {
		return isFirmatPujatManualmentDefinitu() && (estatDocument.equals(DocumentEstatEnumDto.DEFINITIU) || estatDocument.equals(DocumentEstatEnumDto.CUSTODIAT));		
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
			if (! isEnviarContingutExistentActiu())
				fitxer.setContingut(null);
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
		
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(documentEntity.getId()));

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
				if (isArxiuCaib(documentEntity.getEntitat()!=null?documentEntity.getEntitat().getCodi():null)) {
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
			boolean documentDefinitiu = documentEntity.getEstat().equals(DocumentEstatEnumDto.DEFINITIU) || documentEntity.getEstat().equals(DocumentEstatEnumDto.CUSTODIAT);
			ArxiuEstatEnumDto arxiuEstat = (isConversioDefinitiuActiu() && documentDefinitiu) ?  ArxiuEstatEnumDto.DEFINITIU : ArxiuEstatEnumDto.ESBORRANY;
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

	// Mètode implementat únicament per solucionar error de documents que s'han creat sense el seu tipus, i ja estan com a definitius
	public void updateTipusDocumentDefinitiu(
			DocumentEntity documentEntity,
			Long metaDocumentId) {

		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(documentEntity.getId()));

		MetaDocumentEntity metaDocument = null;
		if (metaDocumentId != null) {
			metaDocument = entityComprovarHelper.comprovarMetaDocument(
					documentEntity.getEntitat(),
					documentEntity.getMetaDocument() != null ? documentEntity.getMetaDocument().getMetaExpedient() : null,
					metaDocumentId,
					false,
					false);
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
			DocumentFirmaTipusEnumDto documentFirmaTipus, 
			ExpedientEstatEntity expedientEstatAdditional) {
		return crearDocumentDB(
				documentTipus,
				nom,
				descripcio,
				data,
				dataCaptura,
				ntiOrgano,
				ntiOrigen,
				ntiEstadoElaboracion,
				ntiTipoDocumental,
				metaDocument,
				pare,
				entitat,
				expedient,
				ubicacio,
				ntiIdDocumentoOrigen,
				pinbalIdpeticion,
				true,
				null,
				ArxiuEstatEnumDto.DEFINITIU, 
				documentFirmaTipus, 
				expedientEstatAdditional);
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
			ArxiuEstatEnumDto annexArxiuEstat, 
			DocumentFirmaTipusEnumDto documentFirmaTipus, 
			ExpedientEstatEntity expedientEstatAdditional) {
		DocumentEntity documentCrear = DocumentEntity.getBuilder(
				documentTipus,
				documentFirmaTipus == DocumentFirmaTipusEnumDto.SENSE_FIRMA ? DocumentEstatEnumDto.REDACCIO : DocumentEstatEnumDto.FIRMAT,
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
				expedient, 
				documentFirmaTipus, 
				expedientEstatAdditional).
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
	
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void actualitzarEstatADefinititu(Long documentId) {

		try {
			organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(documentId));
			
			DocumentEntity documentEntity = documentRepository.findById(documentId).orElse(null);
			
			Document arxiuDocument = pluginHelper.arxiuDocumentConsultar(
					documentEntity,
					null,
					null,
					true,
					false);
			
			DocumentFirmaTipusEnumDto documentFirmaTipus = documentEntity.getDocumentFirmaTipus();
			
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
				
				byte[] firmaSeparada = null;
				if (documentEntity.getArxiuUuidFirma() != null) {
					firmaSeparada = pluginHelper.arxiuFirmaSeparadaConsultar(documentEntity);
				} else {
					throw new RuntimeException("Firma separada no existeix en arxiu");
				}
				
				firmes = validaFirmaDocument(
						documentEntity, 
						fitxer,
						firmaSeparada, 
						false, 
						true);
				
				fitxer = null; // if we pass fitxer not null to arxiuPropagarModificacio() ArxiuPluginCaib throws ArxiuValidacioException: No és possible marcar el document com a definitiu si es vol modificar el seu contingut.
				
			}

			contingutHelper.arxiuPropagarModificacio(
					documentEntity,
					fitxer,
					documentFirmaTipus,
					firmes,
					ArxiuEstatEnumDto.DEFINITIU);
			
			documentEntity.setArxiuUuidFirma(null);
		} catch (Exception e) {
			logger.error("Error al actualitzar estat de document a definititu, document id=" + documentId, e);
			throw new RuntimeException("Error al actualitzar estat de document a definititu (document id=" + documentId + "): " + e.getMessage());
		}
	}
	




	public void deleteDefinitiu(DocumentEntity document) {

		pluginHelper.gestioDocumentalDelete(
				document.getGesDocAdjuntId(),
				PluginHelper.GESDOC_AGRUPACIO_DOCS_ADJUNTS);
		
		pluginHelper.gestioDocumentalDelete(
				document.getGesDocAdjuntFirmaId(),
				PluginHelper.GESDOC_AGRUPACIO_DOCS_ADJUNTS);

		pluginHelper.gestioDocumentalDelete(
				document.getGesDocFirmatId(),
				PluginHelper.GESDOC_AGRUPACIO_DOCS_FIRMATS_PORTAFIB);

		contingutHelper.fitxerDocumentEsborratEsborrar(document);
		contingutHelper.firmaSeparadaEsborratEsborrar(document);
		
		documentRepository.delete(document);
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
		return (DocumentDto) contingutHelper.toContingutDto(
				document, 
				false, 
				false);
	}

	public void actualitzarVersionsDocument(
			DocumentEntity document) {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(document.getId()));
		if (pluginHelper.arxiuSuportaVersionsDocuments()) {
			try {
				List<ContingutArxiu> versions = pluginHelper.arxiuDocumentObtenirVersions(
						document);
				if (versions != null) {
					ContingutArxiu darreraVersio = null;
					Float versioNum = null;
					for (ContingutArxiu versio: versions) {
						if (versioNum == null || Float.valueOf(versio.getVersio()).floatValue() > versioNum.floatValue()) {
							versioNum = Float.valueOf(versio.getVersio());
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
	
	
	
	
	public void actualitzarFitxerDB(
			DocumentEntity document,
			FitxerDto fitxer) {
		if (fitxer != null) {
			document.updateFitxer(
					fitxer.getNom(),
					fitxer.getContentType(),
					null, 
					fitxer.getTamany());
		}

	}
	
	
	public void actualitzarFitxerFormatDB(
			DocumentEntity document,
			FitxerDto fitxer) {
		if (fitxer != null) {
			if (Utils.isNotEmpty(fitxer.getContentType())) {
				document.updateFitxerContentType(fitxer.getContentType());
			}
			String newExtension = fitxer.getExtensio();
			if (Utils.isNotEmpty(newExtension)) {
				String oldNameWithoutExtension = FilenameUtils.removeExtension(document.getFitxerNom());
				if (Utils.isNotEmpty(oldNameWithoutExtension)) {
					document.updateFitxerNom(oldNameWithoutExtension + "." + newExtension);
				}
			}
		}
	}
	
	public void actualitzarFitxerFormatAPdf(DocumentEntity document) {
		
		document.updateFitxerContentType("application/pdf");
		String oldNameWithoutExtension = FilenameUtils.removeExtension(document.getFitxerNom());
		if (Utils.isNotEmpty(oldNameWithoutExtension)) {
			document.updateFitxerNom(oldNameWithoutExtension + ".pdf");
		}
	}
	
	public FitxerDto getContingutOriginal(DocumentEntity document) {
		FitxerDto fitxer = new FitxerDto();
        String fitxerNom = document.getFitxerNom();
        String fitxerFirmatNom = document.getNomFitxerFirmat();
		fitxer = new FitxerDto();
        fitxer.setContentType(fitxerFirmatNom != null ? "application/pdf" : document.getFitxerContentType());
        fitxer.setNom(fitxerFirmatNom != null ? fitxerFirmatNom : fitxerNom);
        //El contingut pot estar a distribució o a file system
        try {
			ByteArrayOutputStream streamAnnex = new ByteArrayOutputStream();
			pluginHelper.gestioDocumentalGet(
					document.getGesDocOriginalId(),
					PluginHelper.GESDOC_AGRUPACIO_DOCS_ORIGINALS,
					streamAnnex);
			fitxer.setContingut(streamAnnex.toByteArray());
        } catch (Exception ex) {
			Document arxiuDocument = pluginHelper.arxiuDocumentConsultar(
					null,
					document.getGesDocOriginalId(),
					null,
					true,
					false);
			fitxer.setContingut(getContingutFromArxiuDocument(arxiuDocument));
        }
		return fitxer;
	}

    public FitxerDto getFitxerAssociat(Long documentId, String versio) {
        DocumentEntity document = documentRepository.getOne(documentId);
        return getFitxerAssociat(document, versio);
    }

	public FitxerDto getFitxerAssociat(DocumentEntity document, String versio) {

		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(document.getId()));
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
                fitxer.setContingut(
                        getContingutFromArxiuDocument(
                                arxiuDocument,
                                configHelper.getEntitatActualCodi()==null?
                                        document.getEntitat()!=null?
                                                document.getEntitat().getCodi()
                                                :null
                                        :configHelper.getEntitatActualCodi()));
			}

		} else {
			
			fitxer = new FitxerDto();
			fitxer.setNom(document.getFitxerNom());
			fitxer.setContentType(document.getFitxerContentType());
			ByteArrayOutputStream streamAnnex = new ByteArrayOutputStream();
			pluginHelper.gestioDocumentalGet(
					document.getGesDocAdjuntId(),
					PluginHelper.GESDOC_AGRUPACIO_DOCS_ADJUNTS,
					streamAnnex);
			fitxer.setContingut(streamAnnex.toByteArray());
		}

		return fitxer;
	}
	
	public FitxerDto getFitxerAssociatFirmat(
			DocumentEntity document,
			String versio) {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(document.getId()));
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
	
	public List<DocumentEntity> findDocumentsNoFirmatsOAmbFirmaInvalidaONoGuardatsEnArxiu(
			Long entitatId,
			Long expedientId) {
		logger.debug("Obtenint els documents no firmats o amb firma invalida (" +
				"entitatId=" + entitatId + ", " +
				"expedientId=" + expedientId + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				expedientId,
				false,
				false,
				false,
				false,
				false,
				null);
		List<DocumentEntity> documents = documentRepository.findByExpedientAndEsborrat(expedient, 0);
		List<DocumentEntity> documentsChosen = new ArrayList<DocumentEntity>();
		for (DocumentEntity document: documents) {
			if (document.getEstat() == DocumentEstatEnumDto.REDACCIO || document.getArxiuUuid() == null ) {
				documentsChosen.add(document);
			}
		}
		return documentsChosen;
	}

    public byte[] getContingutFromArxiuDocument(Document arxiuDocument) {
        return getContingutFromArxiuDocument(arxiuDocument, null);
    }

    @SuppressWarnings("incomplete-switch")
    public byte[] getContingutFromArxiuDocument(Document arxiuDocument, String codiEntitat) {
        byte[] contingut = null;
		DocumentContingut document = arxiuDocument.getContingut();
		List<Firma> firmes = arxiuDocument.getFirmes();
		
		if (firmes == null || firmes.isEmpty()) {
			contingut = document.getContingut();
		} else {
            boolean isArxiuCaib = isArxiuCaib(codiEntitat);
			for (Firma firma: firmes) {
				if (firma.getTipus() != FirmaTipus.CSV) {
					switch(firma.getTipus()) {
					case CADES_ATT:
					case XADES_ENV:
					case XADES_DET: //Tot i que el nom es detached, es troba en el mateix arxiu de firma. Només que en un node separat del XML original.
					case PADES:
					case ODT:
					case OOXML:
					case SMIME:
						contingut = isArxiuCaib ? firma.getContingut() : document.getContingut();
						break;
					case CADES_DET:
						contingut = isArxiuCaib ? document.getContingut() : firma.getContingut();
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
					case CADES_DET: //És la unica firma realment detached (2 fitxers separats)
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

	public DocumentEntity comprovarDocument(
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
	
	
	public String generarIdentificadorFirmaClient(
			Long entitatId,
			Long id) {
		logger.debug("Generar identificador firma al navegador ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		comprovarDocumentDinsExpedientAccessible(
				entitatId,
				id,
				true,
				false);
		try {
			return firmaAppletHelper.firmaClientXifrar(
					firmaAppletHelper.obtainInstanceObjecteFirmaApplet( 
							Long.valueOf(System.currentTimeMillis()),
							entitatId,
							id));
		} catch (Exception ex) {
			logger.error(
					"Error al generar l'identificador per la firma al navegador (" +
					"entitatId=" + entitatId + ", " +
					"documentId=" + id + ")",
					ex);
			throw new RuntimeException(
					"Error al generar l'identificador per la firma al navegador (" +
					"entitatId=" + entitatId + ", " +
					"documentId=" + id + ")",
					ex);
		}
	}
	
	
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public Exception guardarDocumentArxiu(
			Long docId) {
		if (cacheHelper.mostrarLogsCreacioContingut())
			logger.info("Guardar document arxiu (id=" + docId + ", entitatCodi=" + configHelper.getEntitatActualCodi() + ")");
		Exception exception = null;

		DocumentEntity documentEntity = documentRepository.getOne(docId);
		if (documentEntity.getExpedient().getArxiuUuid() != null) {
			
			if (documentEntity.getArxiuUuid() != null) { // concurrency check
				throw new ArxiuJaGuardatException("El document ja s'ha guardat en arxiu per otra persona o el process en segon pla");
			}
			
			try {
				
				expedientHelper.concurrencyCheckExpedientJaTancat(documentEntity.getExpedient());
				
				FitxerDto fitxer = new FitxerDto();
				fitxer.setNom(documentEntity.getFitxerNom());
				fitxer.setContentType(documentEntity.getFitxerContentType());

				ByteArrayOutputStream streamAnnex = new ByteArrayOutputStream();
				pluginHelper.gestioDocumentalGet(
						documentEntity.getGesDocAdjuntId(),
						PluginHelper.GESDOC_AGRUPACIO_DOCS_ADJUNTS,
						streamAnnex);
				fitxer.setContingut(streamAnnex.toByteArray());
				
				DocumentFirmaTipusEnumDto documentFirmaTipus = documentEntity.getDocumentFirmaTipus();
				
				List<ArxiuFirmaDto> firmes = null;
				if (documentFirmaTipus == DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA) {
					
					firmes = validaFirmaDocument(
							documentEntity, 
							fitxer,
							null, 
							false, 
							true);
					
				} else  if (documentFirmaTipus == DocumentFirmaTipusEnumDto.FIRMA_SEPARADA) {
					
					byte[] firmaSeparada = null;
					if (documentEntity.getGesDocAdjuntFirmaId() != null) {
						ByteArrayOutputStream streamAnnex1 = new ByteArrayOutputStream();
						pluginHelper.gestioDocumentalGet(
								documentEntity.getGesDocAdjuntFirmaId(),
								PluginHelper.GESDOC_AGRUPACIO_DOCS_ADJUNTS,
								streamAnnex1);
						firmaSeparada = streamAnnex1.toByteArray();
					} else {
						throw new RuntimeException("Firma separada no existeix en gestió documental");
					}
					firmes = validaFirmaDocument(
							documentEntity,
							fitxer,
							firmaSeparada, 
							false, 
							true);
				}
		
				ArxiuEstatEnumDto arxiuEstat = getArxiuEstat(documentFirmaTipus, null);
				
				if (arxiuEstat == ArxiuEstatEnumDto.ESBORRANY && documentFirmaTipus == DocumentFirmaTipusEnumDto.FIRMA_SEPARADA) {
					pluginHelper.arxiuPropagarFirmaSeparada(
							documentEntity,
							firmes.get(0).getFitxer());
				}
				contingutHelper.arxiuPropagarModificacio(
						documentEntity,
						fitxer,
						arxiuEstat == ArxiuEstatEnumDto.ESBORRANY ? DocumentFirmaTipusEnumDto.SENSE_FIRMA : documentFirmaTipus,
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
				exception = ExceptionHelper.getRootCauseException(ex);
			}
		} else {
			exception = new RuntimeException("Expedient de aquest document no es guardat en arxiu");
		}

		documentEntity.updateArxiuIntent(true);
		return exception;
	}

	public List<ArxiuFirmaDto> validaFirmaDocument(
			DocumentEntity document,
			FitxerDto fitxer,
			byte[] contingutFirma, 
			boolean updateEstat, 
			boolean throwExceptionIfNotValid) {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(document.getId()));
		logger.debug("Recuperar la informació de les firmes amb el plugin ValidateSignature ("
				+ "documentID=" + document.getId() + ")");
		List<ArxiuFirmaDto> firmes = pluginHelper.validaSignaturaObtenirFirmes(
				document.getFitxerNom(),
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
					(DocumentDto) contingutHelper.toContingutDto(
							document, 
							false, 
							false));
			
		}
		return documentsDto;
	}

	public DocumentEntity findLastDocumentPujatArxiuByExtensio(List<String> contentTypes) {
		Pageable pageable = PageRequest.of(0, 1);
		List<DocumentEntity> l = null;
		
		if (contentTypes!=null && contentTypes.size()>0) {
			l = documentRepository.findLastByUuid(contentTypes, pageable).getContent();
		} else {
			l = documentRepository.findLastByUuid(pageable).getContent();
		}
		
		if (l!=null && l.size()>0) {
			return l.get(0);
		} else {
			return null;
		}
	}
	
	public FitxerDto convertirPdfPerFirmaClient(
			Long entitatId,
			Long id) {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(id));
		logger.debug("Converteix un document en PDF per a la firma client ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		DocumentEntity document = comprovarDocumentDinsExpedientAccessible(
				entitatId,
				id,
				true,
				false);
        if (!document.isFirmat()) {
            return pluginHelper.conversioConvertirPdf(
                    getFitxerAssociat(document, null),
                    null);
        } else {
            return getFitxerAssociat(document, null);
        }
	}
	
	public FitxerDto getFitxerPDF(
			Long entitatId,
			Long id) {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(id));
		logger.debug("Converteix un document en PDF per a la firma client ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		DocumentEntity document = comprovarDocumentDinsExpedientAccessible(
				entitatId,
				id,
				true,
				false);

        if (!document.getFitxerNom().endsWith(".pdf")) {
        	FitxerDto fitxerNoPdf = getFitxerAssociat(document, null);
            return pluginHelper.conversioConvertirPdf(fitxerNoPdf, null);
        } else {
            return getFitxerAssociat(document, null);
        }
	}
	
	public void crearEntradaDocument(
			ZipOutputStream zos,
			Long documentId, 
			Long tascaId, 
			String rolActual) throws IOException {
		DocumentEntity document = documentRepository.getOne(documentId);
		
		if (tascaId == null) {
			contingutHelper.checkIfPermitted(
					documentId,
					rolActual,
					PermissionEnumDto.READ);

		} else {
			contingutHelper.comprovarDocumentPerTasca(
					tascaId,
					documentId);
		}
		
		ContingutEntity pare = document.getPare();
		List<String> estructuraCarpetes = new ArrayList<String>();
		while (pare instanceof CarpetaEntity) {
			estructuraCarpetes.add(pare.getNom());
			if (pare.getPare() instanceof CarpetaEntity)
				pare = (CarpetaEntity) pare.getPare();
			else
				pare = (ExpedientEntity) pare.getPare();
		}
		
		String ruta = "";
		Collections.reverse(estructuraCarpetes);
		for (String folder: estructuraCarpetes) {
			ruta += revisarContingutNom(folder).replace(":", "") + "/";
		}
		
		FitxerDto fitxer = getFitxerAssociat(document, null);
		String rutaDoc = ruta + revisarContingutNom(document.getNom()) + "." + FilenameUtils.getExtension(fitxer.getNom());

		contingutHelper.crearNovaEntrada(
				rutaDoc, 
				fitxer, 
				zos);
	}
	
	private static String revisarContingutNom(String nom) {
		if (nom == null) {
			return null;
		}
		return nom.replace("&", "&amp;").replaceAll("[\\\\/:*?\"<>|]", "_");
	}
	
	public boolean isModificacioCustodiatsActiva() {
		return configHelper.getAsBoolean(PropertyConfig.MODIFICAR_DOCUMENTS_CUSTODIATS);
	}
	public boolean isPropagarConversioDefinitiuActiu() {
		return configHelper.getAsBoolean(PropertyConfig.CONVERSIO_DEFINITIU_PROPAGAR_ARXIU);
	}
	public boolean isConversioDefinitiuActiu() {
		return configHelper.getAsBoolean(PropertyConfig.CONVERSIO_DEFINITIU);
	}
	public boolean isPropagarModificacioDefinitiusActiva() {
		return configHelper.getAsBoolean(PropertyConfig.PROPAGAR_MODIFICACIO_ARXIU);
	}
	public boolean isFirmatPujatManualmentDefinitu(){
		return configHelper.getAsBoolean(PropertyConfig.CREAR_FIRMAT_DEFINITIU);
	}
	public boolean isEnviarContingutExistentActiu(){
		return configHelper.getAsBoolean(PropertyConfig.ENVIAR_CONTINGUT_EXISTENT);
	}
    public boolean isArxiuCaib(String codiEntitat) {
        if (configHelper.getEntitatActualCodi()==null) {
            return pluginHelper.getArxiuPlugin(codiEntitat).getPlugin() instanceof ArxiuPluginCaib;
        } else {
            return pluginHelper.getArxiuPlugin().getPlugin() instanceof ArxiuPluginCaib;
        }
    }
	
	private static final Logger logger = LoggerFactory.getLogger(DocumentHelper.class);

}
