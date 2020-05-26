/**
 * 
 */
package es.caib.ripea.core.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.IOUtils;
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
import es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiTipoFirmaEnumDto;
import es.caib.ripea.core.api.dto.DocumentPortafirmesDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;
import es.caib.ripea.core.api.dto.NtiOrigenEnumDto;
import es.caib.ripea.core.api.dto.PortafirmesCallbackEstatEnumDto;
import es.caib.ripea.core.api.dto.PortafirmesPrioritatEnumDto;
import es.caib.ripea.core.api.dto.ViaFirmaCallbackEstatEnumDto;
import es.caib.ripea.core.api.exception.SistemaExternException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentEnviamentInteressatEntity;
import es.caib.ripea.core.entity.DocumentNotificacioEntity;
import es.caib.ripea.core.entity.DocumentPortafirmesEntity;
import es.caib.ripea.core.entity.DocumentViaFirmaEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.NodeEntity;
import es.caib.ripea.core.repository.DocumentPortafirmesRepository;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.plugin.notificacio.RespostaConsultaEstatEnviament;
import es.caib.ripea.plugin.portafirmes.PortafirmesDocument;
import es.caib.ripea.plugin.portafirmes.PortafirmesPrioritatEnum;
import es.caib.ripea.plugin.viafirma.ViaFirmaDocument;

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
	private EmailHelper emailHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private DocumentPortafirmesRepository documentPortafirmesRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	
	public void portafirmesEnviar(
			Long entitatId,
			DocumentEntity document,
			String assumpte,
			PortafirmesPrioritatEnumDto prioritat,
			Date dataCaducitat,
			String[] portafirmesResponsables,
			MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSeqTipus,
			MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus,
			String transaccioId) {
		logger.debug("Enviant document a portafirmes (" +
				"entitatId=" + entitatId + ", " +
				"id=" + document.getId() + ", " +
				"assumpte=" + assumpte + ", " +
				"prioritat=" + prioritat + ", " +
				"dataCaducitat=" + dataCaducitat + ")");
//		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientModificable(
//				entitatId,
//				id,
//				false,
//				true,
//				false,
//				false);
		
		if (!DocumentTipusEnumDto.DIGITAL.equals(document.getDocumentTipus())) {
			throw new ValidationException(
					document.getId(),
					DocumentEntity.class,
					"El document a enviar al portafirmes no és del tipus " + DocumentTipusEnumDto.DIGITAL);
		}
		if (!cacheHelper.findErrorsValidacioPerNode(document).isEmpty()) {
			throw new ValidationException(
					document.getId(),
					DocumentEntity.class,
					"El document a enviar al portafirmes te alertes de validació");
		}
		if (	DocumentEstatEnumDto.FIRMAT.equals(document.getEstat()) ||
				DocumentEstatEnumDto.CUSTODIAT.equals(document.getEstat())) {
			throw new ValidationException(
					document.getId(),
					DocumentEntity.class,
					"No es poden enviar al portafirmes documents firmates o custodiats");
		}
		List<DocumentPortafirmesEntity> enviamentsPendents = documentPortafirmesRepository.findByDocumentAndEstatInOrderByCreatedDateDesc(
				document,
				new DocumentEnviamentEstatEnumDto[] {
						DocumentEnviamentEstatEnumDto.PENDENT,
						DocumentEnviamentEstatEnumDto.ENVIAT
				});
		if (enviamentsPendents.size() > 0) {
			throw new ValidationException(
					document.getId(),
					DocumentEntity.class,
					"Aquest document te enviaments al portafirmes pendents");
		}
		if (!document.getMetaDocument().isFirmaPortafirmesActiva()) {
			throw new ValidationException(
					document.getId(),
					DocumentEntity.class,
					"El document no te activada la firma amb portafirmes");
		}
		DocumentPortafirmesEntity documentPortafirmes = DocumentPortafirmesEntity.getBuilder(
				DocumentEnviamentEstatEnumDto.PENDENT,
				assumpte,
				prioritat,
				dataCaducitat,
				document.getMetaDocument().getPortafirmesDocumentTipus(),
				portafirmesResponsables,
				portafirmesSeqTipus,
				portafirmesFluxTipus,
				document.getMetaDocument().getPortafirmesFluxId(),
				document.getExpedient(),
				document).build();
		// Si l'enviament produeix excepcions la retorna
		SistemaExternException sex = portafirmesEnviar(
				documentPortafirmes,
				transaccioId);
		if (sex != null) {
			throw sex;
		}
		documentPortafirmesRepository.save(documentPortafirmes);
		document.updateEstat(
				DocumentEstatEnumDto.FIRMA_PENDENT);
		contingutLogHelper.log(
				document,
				LogTipusEnumDto.PFIRMA_ENVIAMENT,
				documentPortafirmes.getPortafirmesId(),
				documentPortafirmes.getEstat().name(),
				false,
				false);
	}
	
	
	

	public DocumentPortafirmesDto portafirmesInfo(
			Long entitatId,
			DocumentEntity document) {
		logger.debug("Obtenint informació del darrer enviament a portafirmes ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + document.getId() + ")");

		List<DocumentPortafirmesEntity> enviamentsPendents = documentPortafirmesRepository.findByDocumentAndEstatInOrderByCreatedDateDesc(
				document,
				new DocumentEnviamentEstatEnumDto[] {
						DocumentEnviamentEstatEnumDto.PENDENT,
						DocumentEnviamentEstatEnumDto.ENVIAT
				});
		if (enviamentsPendents.size() == 0) {
			throw new ValidationException(
					document.getId(),
					DocumentEntity.class,
					"Aquest document no te enviaments a portafirmes");
		}
		return conversioTipusHelper.convertir(
				enviamentsPendents.get(0),
				DocumentPortafirmesDto.class);
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
				false,
				false);
		DocumentDto dto = toDocumentDto(documentEntity);
		contingutHelper.arxiuPropagarModificacio(
				documentEntity,
				fitxer,
				document.isAmbFirma(),
				document.isFirmaSeparada(),
				firmes);
		return dto;
	}
	

	public void portafirmesReintentar(
			Long entitatId,
			DocumentEntity document) {
		logger.debug("Reintentant processament d'enviament a portafirmes amb error ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + document.getId() + ")");

		List<DocumentPortafirmesEntity> enviamentsPendents = documentPortafirmesRepository.findByDocumentAndEstatInAndErrorOrderByCreatedDateDesc(
				document,
				new DocumentEnviamentEstatEnumDto[] {
						DocumentEnviamentEstatEnumDto.PENDENT,
						DocumentEnviamentEstatEnumDto.ENVIAT
				},
				true);
		if (enviamentsPendents.size() == 0) {
			throw new ValidationException(
					document.getId(),
					DocumentEntity.class,
					"Aquest document no te enviaments a portafirmes pendents de processar");
		}
		DocumentPortafirmesEntity documentPortafirmes = enviamentsPendents.get(0);
		contingutLogHelper.log(
				documentPortafirmes.getDocument(),
				LogTipusEnumDto.PFIRMA_REINTENT,
				documentPortafirmes.getPortafirmesId(),
				documentPortafirmes.getEstat().name(),
				false,
				false);
		if (DocumentEnviamentEstatEnumDto.PENDENT.equals(documentPortafirmes.getEstat())) {
			portafirmesEnviar(
					documentPortafirmes,
					null);
		} else if (DocumentEnviamentEstatEnumDto.ENVIAT.equals(documentPortafirmes.getEstat())) {
			portafirmesProcessar(documentPortafirmes);
		}
	}
	
	public DocumentDto certificacioToDocumentDto(
			DocumentEnviamentInteressatEntity documentEnviamentInteressatEntity,
			MetaDocumentEntity metaDocument,
			RespostaConsultaEstatEnviament resposta) {
		DocumentNotificacioEntity notificacio = documentEnviamentInteressatEntity.getNotificacio();
		return generarDocumentDto(
					notificacio,
					metaDocument,
					resposta);
		
	}
	
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
		contingutHelper.arxiuPropagarModificacio(
				entity,
				fitxer,
				document.isAmbFirma(),
				document.isFirmaSeparada(),
				firmes);
		DocumentDto dto = toDocumentDto(entity);
		return dto;
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
	
	private DocumentDto generarDocumentDto(
			DocumentNotificacioEntity notificacio,
			MetaDocumentEntity metaDocument,
			RespostaConsultaEstatEnviament resposta) {
		return contingutHelper.generarDocumentDto(
				notificacio,
				metaDocument,
				resposta);
	}
	

	public DocumentEntity crearDocumentDB(
			DocumentTipusEnumDto documentTipus,
			String nom,
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
			if (pluginHelper.isArxiuPluginActiu()) {
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
			fitxer.setNom(document.getFitxerNom());
			fitxer.setContentType(document.getFitxerContentType());
			fitxer.setContingut(document.getFitxerContingut());
		}
		if (versio == null && DocumentEstatEnumDto.CUSTODIAT.equals(document.getEstat())) {
			fitxer.setNom(
					pluginHelper.conversioConvertirPdfArxiuNom(
							document.getFitxerNom()));
		}
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

	public SistemaExternException portafirmesEnviar(
			DocumentPortafirmesEntity documentPortafirmes,
			String transaccioId) {
		DocumentEntity document = documentPortafirmes.getDocument();
		try {
			String portafirmesId = pluginHelper.portafirmesUpload(
					document,
					documentPortafirmes.getAssumpte(),
					PortafirmesPrioritatEnum.valueOf(documentPortafirmes.getPrioritat().name()),
					documentPortafirmes.getCaducitatData(),
					documentPortafirmes.getDocumentTipus(),
					documentPortafirmes.getResponsables(),
					documentPortafirmes.getSequenciaTipus(),
					documentPortafirmes.getFluxId(),
					null,
					transaccioId);
			documentPortafirmes.updateEnviat(
					new Date(),
					portafirmesId);
			return null;
		} catch (SistemaExternException ex) {
			Throwable rootCause = ExceptionUtils.getRootCause(ex);
			if (rootCause == null) rootCause = ex;
			documentPortafirmes.updateEnviatError(
					ExceptionUtils.getStackTrace(rootCause),
					null);
			return ex;
		}
	}

	public Exception portafirmesProcessar(
			DocumentPortafirmesEntity documentPortafirmes) {
		DocumentEntity document = documentPortafirmes.getDocument();
		PortafirmesCallbackEstatEnumDto callbackEstat = documentPortafirmes.getCallbackEstat();
		if (PortafirmesCallbackEstatEnumDto.FIRMAT.equals(callbackEstat)) {
			document.updateEstat(
					DocumentEstatEnumDto.FIRMAT);
			PortafirmesDocument portafirmesDocument = null;
			// Descarrega el document firmat del portafirmes
			try {
				portafirmesDocument = pluginHelper.portafirmesDownload(
						documentPortafirmes);
			} catch (Exception ex) {
				Throwable rootCause = ExceptionUtils.getRootCause(ex);
				if (rootCause == null) rootCause = ex;
				documentPortafirmes.updateProcessatError(
						ExceptionUtils.getStackTrace(rootCause),
						null);
				return null;
			}
			try {
				if (portafirmesDocument.isCustodiat()) {
					// Si el document ja ha estat custodiat pel portafirmes
					// actualitza la informació de custòdia.
					document.updateInformacioCustodia(
							new Date(),
							portafirmesDocument.getCustodiaId(),
							portafirmesDocument.getCustodiaUrl());
					documentPortafirmes.updateProcessat(
							true,
							new Date());
				} else {
					// Si el document no ha estat custodiat pel portafirmes
					// actualitza la informació de firma a l'arxiu.
					FitxerDto fitxer = new FitxerDto();
					fitxer.setNom(document.getFitxerNom());
					fitxer.setContingut(portafirmesDocument.getArxiuContingut());
					fitxer.setContentType("application/pdf");
					documentPortafirmes.updateProcessat(
							true,
							new Date());
					String custodiaDocumentId = pluginHelper.arxiuDocumentGuardarPdfFirmat(
							document,
							fitxer);
					document.updateInformacioCustodia(
							new Date(),
							custodiaDocumentId,
							document.getCustodiaCsv());
					actualitzarVersionsDocument(document);
					actualitzarInformacioFirma(document);
					contingutLogHelper.log(
							documentPortafirmes.getDocument(),
							LogTipusEnumDto.ARXIU_CUSTODIAT,
							custodiaDocumentId,
							null,
							false,
							false);
				}
				emailHelper.canviEstatDocumentPortafirmes(documentPortafirmes);
			} catch (Exception ex) {
				Throwable rootCause = ExceptionUtils.getRootCause(ex);
				if (rootCause == null) rootCause = ex;
				documentPortafirmes.updateProcessatError(
						ExceptionUtils.getStackTrace(rootCause),
						null);
			}
		}
		if (PortafirmesCallbackEstatEnumDto.REBUTJAT.equals(callbackEstat)) {
			try {
				documentPortafirmes.getDocument().updateEstat(
						DocumentEstatEnumDto.REDACCIO);
				documentPortafirmes.updateProcessat(
						false,
						new Date());
				contingutLogHelper.log(
						documentPortafirmes.getDocument(),
						LogTipusEnumDto.PFIRMA_REBUIG,
						documentPortafirmes.getPortafirmesId(),
						null,
						false,
						false);
				emailHelper.canviEstatDocumentPortafirmes(documentPortafirmes);
			} catch (Exception ex) {
				Throwable rootCause = ExceptionUtils.getRootCause(ex);
				if (rootCause == null) rootCause = ex;
				return ex;
			}
		}
		return null;
	}
	
	public void viaFirmaEnviar(DocumentViaFirmaEntity documentViaFirma) throws SistemaExternException {
		DocumentEntity document = documentViaFirma.getDocument();
		try {
			String messageCode = pluginHelper.viaFirmaUpload(
					document,
					documentViaFirma);
			documentViaFirma.updateEnviat(
					new Date(),
					messageCode);
		} catch (Exception ex) {
			Throwable rootCause = ExceptionUtils.getRootCause(ex);
			if (rootCause == null) rootCause = ex;
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_VIAFIRMA,
					rootCause.getMessage());
		}
	}
	
	public Exception viaFirmaProcessar(
			DocumentViaFirmaEntity documentViaFirma) {
		DocumentEntity document = documentViaFirma.getDocument();
		ViaFirmaCallbackEstatEnumDto callbackEstat = documentViaFirma.getCallbackEstat();
		if (ViaFirmaCallbackEstatEnumDto.RESPONSED.equals(callbackEstat)) {
			document.updateEstat(
					DocumentEstatEnumDto.FIRMAT);
			ViaFirmaDocument viaFirmaDocument = null;
			// Descarrega el document firmat del portafirmes
			try {
				viaFirmaDocument = pluginHelper.viaFirmaDownload(
						documentViaFirma);
			} catch (Exception ex) {
				Throwable rootCause = ExceptionUtils.getRootCause(ex);
				if (rootCause == null) rootCause = ex;
				documentViaFirma.updateProcessatError(
						ExceptionUtils.getStackTrace(rootCause),
						null);
				return null;
			}
			try {
				// Actualitza la informació de firma a l'arxiu.
				FitxerDto fitxer = new FitxerDto();
				if (viaFirmaDocument != null) {
					byte [] contingut = IOUtils.toByteArray((new URL(viaFirmaDocument.getLink())).openStream());
					
					fitxer.setNom(viaFirmaDocument.getNomFitxer());
					fitxer.setContingut(contingut);
					fitxer.setContentType("application/pdf");
					documentViaFirma.updateProcessat(
								true,
								new Date());
					String custodiaDocumentId = pluginHelper.arxiuDocumentGuardarPdfFirmat(
							document,
							fitxer);
					document.updateInformacioCustodia(
							new Date(),
							custodiaDocumentId,
							document.getCustodiaCsv());
					actualitzarVersionsDocument(document);
					actualitzarInformacioFirma(document);
					contingutLogHelper.log(
							documentViaFirma.getDocument(),
							LogTipusEnumDto.ARXIU_CUSTODIAT,
							custodiaDocumentId,
							null,
							false,
							false);
				}
			} catch (Exception ex) {
				document.updateEstat(DocumentEstatEnumDto.FIRMA_PENDENT_VIAFIRMA);
				Throwable rootCause = ExceptionUtils.getRootCause(ex);
				if (rootCause == null) rootCause = ex;
				documentViaFirma.updateProcessatError(
						ExceptionUtils.getStackTrace(rootCause),
						null);
			}
		} 
		if (ViaFirmaCallbackEstatEnumDto.WAITING_CHECK.equals(callbackEstat)) {
			try {
				contingutLogHelper.log(
						documentViaFirma.getDocument(),
						LogTipusEnumDto.VFIRMA_WAITING_CHECK,
						documentViaFirma.getMessageCode(),
						null,
						false,
						false);
			} catch (Exception ex) {
				Throwable rootCause = ExceptionUtils.getRootCause(ex);
				if (rootCause == null) rootCause = ex;
				return ex;
			}
		}
		
		if (ViaFirmaCallbackEstatEnumDto.REJECTED.equals(callbackEstat)) {
			try {
				documentViaFirma.getDocument().updateEstat(
						DocumentEstatEnumDto.REDACCIO);
				documentViaFirma.updateProcessat(
						false,
						new Date());
				contingutLogHelper.log(
						documentViaFirma.getDocument(),
						LogTipusEnumDto.VFIRMA_REBUIG,
						documentViaFirma.getMessageCode(),
						null,
						false,
						false);
			} catch (Exception ex) {
				Throwable rootCause = ExceptionUtils.getRootCause(ex);
				if (rootCause == null) rootCause = ex;
				return ex;
			}
		} else if (ViaFirmaCallbackEstatEnumDto.ERROR.equals(callbackEstat)) {
			try {
				documentViaFirma.getDocument().updateEstat(
						DocumentEstatEnumDto.REDACCIO);
				documentViaFirma.updateProcessat(
						false,
						new Date());
				contingutLogHelper.log(
						documentViaFirma.getDocument(),
						LogTipusEnumDto.VFIRMA_ERROR,
						documentViaFirma.getMessageCode(),
						null,
						false,
						false);
			} catch (Exception ex) {
				Throwable rootCause = ExceptionUtils.getRootCause(ex);
				if (rootCause == null) rootCause = ex;
				return ex;
			}
		} else if (ViaFirmaCallbackEstatEnumDto.EXPIRED.equals(callbackEstat)) {
			try {
				documentViaFirma.getDocument().updateEstat(
						DocumentEstatEnumDto.REDACCIO);
				documentViaFirma.updateProcessat(
						false,
						new Date());
				contingutLogHelper.log(
						documentViaFirma.getDocument(),
						LogTipusEnumDto.VFIRMA_EXPIRED,
						documentViaFirma.getMessageCode(),
						null,
						false,
						false);
			} catch (Exception ex) {
				Throwable rootCause = ExceptionUtils.getRootCause(ex);
				if (rootCause == null) rootCause = ex;
				return ex;
			}
		}
		return null;
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

	public void actualitzarInformacioFirma(
			DocumentEntity document) {
		if (pluginHelper.arxiuSuportaVersionsDocuments()) {
			try {
				Document documentArxiu = pluginHelper.arxiuDocumentConsultar(
						document,
						null,
						null,
						false);
				DocumentNtiTipoFirmaEnumDto tipoFirma = null;
				String csv = null;
				String csvDef = null;
				if (documentArxiu.getFirmes() != null) {
					for (Firma firma: documentArxiu.getFirmes()) {
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
						"Error al actualitzar les metadades NTI de firma (" + 
						"entitatId=" + document.getEntitat().getId() + ", " +
						"documentId=" + document.getId() + ", " +
						"documentTitol=" + document.getNom() + ")",
						ex);
			}
		}
	}
	

	public void portafirmesCancelar(
			Long entitatId,
			DocumentEntity document) {
		logger.debug("Enviant document a portafirmes (" +
				"entitatId=" + entitatId + ", " +
				"id=" + document.getId() + ")");

		List<DocumentPortafirmesEntity> enviamentsPendents = documentPortafirmesRepository.findByDocumentAndEstatInOrderByCreatedDateDesc(
				document,
				new DocumentEnviamentEstatEnumDto[] {DocumentEnviamentEstatEnumDto.ENVIAT});
		if (enviamentsPendents.size() == 0) {
			throw new ValidationException(
					document.getId(),
					DocumentEntity.class,
					"Aquest document no te enviaments a portafirmes pendents");
		}
		DocumentPortafirmesEntity documentPortafirmes = enviamentsPendents.get(0);
		if (DocumentEnviamentEstatEnumDto.ENVIAT.equals(documentPortafirmes.getEstat())) {
			pluginHelper.portafirmesDelete(documentPortafirmes);
		}
		documentPortafirmes.updateCancelat(new Date());
		document.updateEstat(
				DocumentEstatEnumDto.REDACCIO);
		contingutLogHelper.log(
				document,
				LogTipusEnumDto.PFIRMA_CANCELACIO,
				documentPortafirmes.getPortafirmesId(),
				documentPortafirmes.getEstat().name(),
				false,
				false);
	}
	

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
			List<DocumentEntity> documents) {
		logger.debug("Consulta els documents esborranys d'un expedient");
		for (DocumentEntity document : documents) {
			if (document.getEsborrat() == 0 
					&& document.getDocumentTipus().equals(DocumentTipusEnumDto.DIGITAL)
					&& document.getEstat().equals(DocumentEstatEnumDto.REDACCIO)) {
				return true;
			}
		}
		return false;
	}
	
	public void processarFirmaClient(
			String identificador,
			String arxiuNom,
			byte[] arxiuContingut,
			DocumentEntity document) {
		logger.debug("Custodiar identificador firma applet ("
				+ "identificador=" + identificador + ")");
		// Registra al log la firma del document
		contingutLogHelper.log(
				document,
				LogTipusEnumDto.FIRMA_CLIENT,
				null,
				null,
				false,
				false);
		// Custodia el document firmat
		FitxerDto fitxer = new FitxerDto();
		fitxer.setNom(arxiuNom);
		fitxer.setContingut(arxiuContingut);
		fitxer.setContentType("application/pdf");
		document.updateEstat(
				DocumentEstatEnumDto.CUSTODIAT);
		String custodiaDocumentId = pluginHelper.arxiuDocumentGuardarPdfFirmat(
				document,
				fitxer);
		document.updateInformacioCustodia(
				new Date(),
				custodiaDocumentId,
				document.getCustodiaCsv());
		actualitzarVersionsDocument(document);
		// Registra al log la custòdia de la firma del document
		contingutLogHelper.log(
				document,
				LogTipusEnumDto.ARXIU_CUSTODIAT,
				custodiaDocumentId,
				null,
				false,
				false);
		
	}
	
	public String firmaClientXifrar(
			ObjecteFirmaApplet objecte) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(baos);
		Long[] array = new Long[] {
				objecte.getSysdate(),
				objecte.getEntitatId(),
				objecte.getDocumentId()};
		os.writeObject(array);
		os.close();
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(
				Cipher.ENCRYPT_MODE,
				buildKey(CLAU_SECRETA));
		byte[] xifrat = cipher.doFinal(baos.toByteArray());
		return new String(Base64.encode(xifrat));
	}

	public static final String CLAU_SECRETA = "R1p3AR1p3AR1p3AR";

	public SecretKeySpec buildKey(String message) throws Exception {
		MessageDigest sha = MessageDigest.getInstance("SHA-1");
		byte[] key = sha.digest(message.getBytes());
		key = Arrays.copyOf(key, 16);
		return new SecretKeySpec(key, "AES");
	}
	
	public ObjecteFirmaApplet obtainInstanceObjecteFirmaApplet(
			Long sysdate,
			Long entitatId,
			Long documentId) {
		return new ObjecteFirmaApplet(
				sysdate,
				entitatId,
				documentId);
	}
	
	public class ObjecteFirmaApplet implements Serializable {
		private Long sysdate;
		private Long entitatId;
		private Long documentId;
		public ObjecteFirmaApplet(
				Long sysdate,
				Long entitatId,
				Long documentId) {
			this.sysdate = sysdate;
			this.entitatId = entitatId;
			this.documentId = documentId;
		}
		public Long getSysdate() {
			return sysdate;
		}
		public void setSysdate(Long sysdate) {
			this.sysdate = sysdate;
		}
		public Long getEntitatId() {
			return entitatId;
		}
		public void setEntitatId(Long entitatId) {
			this.entitatId = entitatId;
		}
		public Long getDocumentId() {
			return documentId;
		}
		public void setDocumentId(Long documentId) {
			this.documentId = documentId;
		}
		private static final long serialVersionUID = -6929597339153341365L;
	}
	
	public ObjecteFirmaApplet firmaAppletDesxifrar(
			String missatge,
			String key) throws Exception {
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(
				Cipher.DECRYPT_MODE,
				buildKey(key));
		ByteArrayInputStream bais = new ByteArrayInputStream(
				cipher.doFinal(
						Base64.decode(missatge.getBytes())));
		ObjectInputStream is = new ObjectInputStream(bais);
		Long[] array = (Long[])is.readObject();
		ObjecteFirmaApplet objecte = obtainInstanceObjecteFirmaApplet(
				array[0],
				array[1],
				array[2]);
		is.close();
		return objecte;
	}


	private static final Logger logger = LoggerFactory.getLogger(DocumentHelper.class);

}
