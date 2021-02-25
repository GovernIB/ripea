/**
 * 
 */
package es.caib.ripea.core.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
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

import com.sun.jersey.core.util.Base64;

import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.api.FirmaTipus;
import es.caib.ripea.core.api.dto.ArxiuFirmaDto;
import es.caib.ripea.core.api.dto.ContingutTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
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
	
	public DocumentDto crearDocument(
			DocumentDto document,
			ContingutEntity pare,
			ExpedientEntity expedient,
			MetaDocumentEntity metaDocument) {
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
				document.getData(),
				new Date(),
				expedient.getNtiOrgano(),
				metaDocument.getNtiOrigen(),
				document.getNtiEstadoElaboracion(),
				metaDocument.getNtiTipoDocumental(),
				metaDocument,
				pare,
				pare.getEntitat(),
				expedient,
				document.getUbicacio(),
				document.getNtiIdDocumentoOrigen());
		FitxerDto fitxer = new FitxerDto();
		fitxer.setNom(document.getFitxerNom());
		fitxer.setContentType(document.getFitxerContentType());
		fitxer.setContingut(document.getFitxerContingut());
		List<ArxiuFirmaDto> firmes = null;
		if (document.getFitxerContingut() != null) {
			actualitzarFitxerDocument(
					entity,
					fitxer);
			if (document.isAmbFirma()) {
				firmes = validaFirmaDocument(
						entity, 
						fitxer,
						document.getFirmaContingut());
			}
		}
		contingutLogHelper.logCreacio(
				entity,
				true,
				true);
		logger.debug("[CERT] Fitxer nom: " + fitxer.getNom());
		
		
		String gestioDocumentalAdjuntId = document.getGesDocAdjuntId();
		if (document.getFitxerContingut() != null) {
			gestioDocumentalAdjuntId = pluginHelper.gestioDocumentalCreate(
					PluginHelper.GESDOC_AGRUPACIO_DOCS_ADJUNTS,
					new ByteArrayInputStream(document.getFitxerContingut()));
			entity.setGesDocAdjuntId(gestioDocumentalAdjuntId);
		}
		String gestioDocumentalAdjuntFirmaId = document.getGesDocAdjuntFirmaId();
		if (document.isFirmaSeparada()) {
			gestioDocumentalAdjuntFirmaId = pluginHelper.gestioDocumentalCreate(
					PluginHelper.GESDOC_AGRUPACIO_DOCS_ADJUNTS,
					new ByteArrayInputStream(document.getFirmaContingut()));
			entity.setGesDocAdjuntFirmaId(gestioDocumentalAdjuntFirmaId);
		}

		if (document.isAmbFirma()) 
			entity.updateEstat(DocumentEstatEnumDto.ADJUNT_FIRMAT);
		
		try {
			contingutHelper.arxiuPropagarModificacio(
					entity,
					fitxer,
					document.isAmbFirma(),
					document.isFirmaSeparada(),
					firmes);
		
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
			
		} catch (Exception ex) {
			logger.error("Error al custodiar en arxiu document adjunt  (" +
					"id=" + entity.getId() + ")",
					ex);
			Throwable rootCause = ExceptionUtils.getRootCause(ex);
			if (rootCause == null) rootCause = ex;

		}
		
		DocumentDto dto = toDocumentDto(entity);
		return dto;
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
		contingutHelper.comprovarNomValid(
				documentEntity.getPare(),
				document.getNom(),
				documentEntity.getId(),
				DocumentEntity.class);
		cacheHelper.evictErrorsValidacioPerNode(documentEntity);
		String nomOriginal = documentEntity.getNom();
		documentEntity.update(
				metaDocument,
				document.getNom(),
				document.getDescripcio(),
				document.getData(),
				document.getUbicacio(),
				documentEntity.getDataCaptura(),
				documentEntity.getNtiOrgano(),
				metaDocument.getNtiOrigen(),
				document.getNtiEstadoElaboracion(),
				metaDocument.getNtiTipoDocumental(),
				document.getNtiIdDocumentoOrigen(),
				document.getNtiTipoFirma(),
				document.getNtiCsv(),
				document.getNtiCsvRegulacion());
		FitxerDto fitxer = null;
		if (document.getFitxerContingut() != null) {
			fitxer = new FitxerDto();
			fitxer.setNom(document.getFitxerNom());
			fitxer.setContentType(document.getFitxerContentType());
			fitxer.setContingut(document.getFitxerContingut());
		} else if (documentEntity.getArxiuUuid() != null) {
			fitxer = new FitxerDto();
			fitxer.setContentType(documentEntity.getFitxerContentType());
			fitxer.setNom(documentEntity.getFitxerNom());
			Document arxiuDocument = pluginHelper.arxiuDocumentConsultar(
					documentEntity,
					null,
					null,
					true,
					false);
			fitxer.setContingut(getContingutFromArxiuDocument(arxiuDocument));
		}
		if (document.getFitxerContingut() != null) {
			actualitzarFitxerDocument(
					documentEntity,
					fitxer);
			if (document.isAmbFirma()) {
				firmes = validaFirmaDocument(
						documentEntity, 
						fitxer,
						document.getFirmaContingut());
			}
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
		contingutHelper.arxiuPropagarModificacio(
				documentEntity,
				fitxer,
				document.isAmbFirma(),
				document.isFirmaSeparada(),
				firmes);
		return dto;
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
			String ntiIdDocumentoOrigen) {
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
		contingutLogHelper.log(
				document,
				LogTipusEnumDto.CANVI_ESTAT,
				nouEstat.name(),
				null,
				false,
				false);
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
	
	public void actualitzarFitxerDocument(
			DocumentEntity document,
			FitxerDto fitxer) {
		if (pluginHelper.isArxiuPluginActiu()) {
			document.updateFitxer(
					fitxer.getNom(),
					fitxer.getContentType(),
					null);
		} else {
			document.updateFitxer(
					fitxer.getNom(),
					fitxer.getContentType(),
					fitxer.getContingut());
		}
	}

	public FitxerDto getFitxerAssociat(
			DocumentEntity document,
			String versio) {
		FitxerDto fitxer = null;
		if (document.getArxiuUuid() != null) {
			if (pluginHelper.isArxiuPluginActiu() && document.getEstat() != DocumentEstatEnumDto.FIRMAT) {
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
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_ARXIU,
						"S'està intentant obtenir l'arxiu associat a un document pujat a l'arxiu i el plugin d'arxiu no està activat");
			}
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
//		if (versio == null && DocumentEstatEnumDto.CUSTODIAT.equals(document.getEstat())) {
//			fitxer.setNom(
//					pluginHelper.conversioConvertirPdfArxiuNom(
//							document.getFitxerNom()));
//		}
		return fitxer;
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
			boolean comprovarPermisDelete) {
		NodeEntity node = contingutHelper.comprovarNodeDinsExpedientModificable(
				entitatId,
				id,
				comprovarPermisRead,
				comprovarPermisWrite,
				comprovarPermisCreate,
				comprovarPermisDelete);
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

	public List<ArxiuFirmaDto> validaFirmaDocument(
			DocumentEntity document,
			FitxerDto fitxer,
			byte[] contingutFirma) {
		logger.debug("Recuperar la informació de les firmes amb el plugin ValidateSignature ("
				+ "documentID=" + document.getId() + ")");
		List<ArxiuFirmaDto> firmes = pluginHelper.validaSignaturaObtenirFirmes(
				fitxer.getContingut(), 
				(contingutFirma != null && contingutFirma.length > 0) ? contingutFirma : null,
				fitxer.getContentType());
		document.updateEstat(DocumentEstatEnumDto.FIRMAT);
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
					&& document.getDocumentTipus().equals(DocumentTipusEnumDto.DIGITAL)
					&& (document.getEstat().equals(DocumentEstatEnumDto.CUSTODIAT) || document.getEstat().equals(DocumentEstatEnumDto.DEFINITIU))) {
				return true;
			}
		}
		return false;
	}
	

	private static final Logger logger = LoggerFactory.getLogger(DocumentHelper.class);

}
