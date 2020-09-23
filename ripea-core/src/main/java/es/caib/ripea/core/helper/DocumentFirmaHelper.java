package es.caib.ripea.core.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
import es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiTipoFirmaEnumDto;
import es.caib.ripea.core.api.dto.DocumentPortafirmesDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.core.api.dto.PortafirmesCallbackEstatEnumDto;
import es.caib.ripea.core.api.dto.PortafirmesPrioritatEnumDto;
import es.caib.ripea.core.api.dto.ViaFirmaCallbackEstatEnumDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.SistemaExternException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentPortafirmesEntity;
import es.caib.ripea.core.entity.DocumentViaFirmaEntity;
import es.caib.ripea.core.repository.DocumentPortafirmesRepository;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.DocumentViaFirmaRepository;
import es.caib.ripea.plugin.portafirmes.PortafirmesDocument;
import es.caib.ripea.plugin.portafirmes.PortafirmesPrioritatEnum;
import es.caib.ripea.plugin.viafirma.ViaFirmaDocument;

/**
 * Contenidor de tots els mètodes relacionats amb la firma de documents
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DocumentFirmaHelper {
	@Autowired
	private EmailHelper emailHelper;
	@Autowired
	private DocumentPortafirmesRepository documentPortafirmesRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private AlertaHelper alertaHelper;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private ContingutLogHelper contingutLogHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private DocumentHelper documentHelper;
	@Autowired
	private DocumentViaFirmaRepository documentViaFirmaRepository;
	
	public static final String CLAU_SECRETA = "R1p3AR1p3AR1p3AR";
	
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
		String custodiaDocumentId = pluginHelper.arxiuDocumentGuardarFirmaPades(
				document,
				fitxer);
		document.updateInformacioCustodia(
				new Date(),
				custodiaDocumentId,
				document.getCustodiaCsv());
		documentHelper.actualitzarVersionsDocument(document);
		// Registra al log la custòdia de la firma del document
		contingutLogHelper.log(
				document,
				LogTipusEnumDto.ARXIU_CUSTODIAT,
				custodiaDocumentId,
				null,
				false,
				false);
		
	}
	

	////
	// FUNCIONS PORTAFIRMES
	////
	
	public void portafirmesEnviar(
			Long entitatId,
			DocumentEntity document,
			String assumpte,
			PortafirmesPrioritatEnumDto prioritat,
			Date dataCaducitat,
			String portafirmesFluxId,
			String[] portafirmesResponsables,
			MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSeqTipus,
			MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus,
			Long[] annexosIds,
			String transaccioId) {
		logger.debug("Enviant document a portafirmes (" +
				"entitatId=" + entitatId + ", " +
				"id=" + document.getId() + ", " +
				"assumpte=" + assumpte + ", " +
				"prioritat=" + prioritat + ", " +
				"dataCaducitat=" + dataCaducitat + ")");
		
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
		if (enviamentsPendents.size() > 0) { // TODO: uep aqui
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
				portafirmesFluxId != null ? portafirmesFluxId : document.getMetaDocument().getPortafirmesFluxId(),
				document.getExpedient(),
				document).build();

		if (annexosIds != null) {
			for (Long annexId : annexosIds) {
				DocumentEntity annex = documentRepository.findOne(annexId);
				documentPortafirmes.addAnnex(annex);
			}
		}
		// Si l'enviament produeix excepcions la retorna
		SistemaExternException sex = portafirmesEnviar(
				documentPortafirmes,
				transaccioId);
		cacheHelper.evictEnviamentsPortafirmesPendentsPerExpedient(document.getExpedientPare());
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
		cacheHelper.evictEnviamentsPortafirmesPendentsPerExpedient(document.getExpedientPare());
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
					documentPortafirmes.getAnnexos(),
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
		DocumentEstatEnumDto documentEstatAnterior = document.getEstat();
		PortafirmesCallbackEstatEnumDto callbackEstat = documentPortafirmes.getCallbackEstat();
		if (PortafirmesCallbackEstatEnumDto.FIRMAT.equals(callbackEstat)) {
			cacheHelper.evictEnviamentsPortafirmesPendentsPerExpedient(document.getExpedientPare());
			document.updateEstat(
					DocumentEstatEnumDto.FIRMAT);
			PortafirmesDocument portafirmesDocument = null;
			// Descarrega el document firmat del portafirmes
			try {
				portafirmesDocument = pluginHelper.portafirmesDownload(
						documentPortafirmes);
			} catch (Exception ex) {
				logger.error("Error al descarregar document de portafirmes (" +
						"id=" + documentPortafirmes.getId() + ", " +
						"portafirmesId=" + documentPortafirmes.getPortafirmesId() + ")",
						ex);
				cacheHelper.evictEnviamentsPortafirmesAmbErrorPerExpedient(document.getExpedientPare());
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
					fitxer.setNomFitxerFirmat(portafirmesDocument.getArxiuNom());
					fitxer.setContingut(portafirmesDocument.getArxiuContingut());
					fitxer.setContentType("application/pdf");
					// Si no ha estat custodiat
					if (!documentEstatAnterior.equals(DocumentEstatEnumDto.CUSTODIAT)) {
						documentPortafirmes.updateProcessat(
								true,
								new Date());
						String custodiaDocumentId = pluginHelper.arxiuDocumentGuardarFirmaPades(
								document,
								fitxer);
						document.updateInformacioCustodia(
								new Date(),
								custodiaDocumentId,
								document.getCustodiaCsv());
						documentHelper.actualitzarVersionsDocument(document);
						actualitzarInformacioFirma(document);
						contingutLogHelper.log(
								documentPortafirmes.getDocument(),
								LogTipusEnumDto.ARXIU_CUSTODIAT,
								custodiaDocumentId,
								null,
								false,
								false);
					}
				}
				DocumentEstatEnumDto documentEstatNou = document.getEstat();
				if (documentEstatAnterior != DocumentEstatEnumDto.CUSTODIAT && (documentEstatAnterior != documentEstatNou)) {
					alertaHelper.crearAlerta(
							"La firma del document " + document.getNom() + " ha finalitzat correctament",
							null,
							document.getExpedient().getId());
					emailHelper.canviEstatDocumentPortafirmes(documentPortafirmes);
				}
			} catch (Exception ex) {
				logger.error("Error al custodiar document de portafirmes (" +
						"id=" + documentPortafirmes.getId() + ", " +
						"portafirmesId=" + documentPortafirmes.getPortafirmesId() + ")",
						ex);
				cacheHelper.evictEnviamentsPortafirmesAmbErrorPerExpedient(document.getExpedientPare());
				Throwable rootCause = ExceptionUtils.getRootCause(ex);
				if (rootCause == null) rootCause = ex;
				documentPortafirmes.updateProcessatError(
						ExceptionUtils.getStackTrace(rootCause),
						null);
			}
		}
		if (PortafirmesCallbackEstatEnumDto.REBUTJAT.equals(callbackEstat)) {
			cacheHelper.evictEnviamentsPortafirmesPendentsPerExpedient(document.getExpedientPare());
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
						documentPortafirmes.getMotiuRebuig(),
						false,
						false);
				alertaHelper.crearAlerta(
						"La firma del document " + document.getNom() + " ha estat rebutjada pel següent motiu: " + documentPortafirmes.getMotiuRebuig(),
						null,
						document.getExpedient().getId());
				emailHelper.canviEstatDocumentPortafirmes(documentPortafirmes);
			} catch (Exception ex) {
				Throwable rootCause = ExceptionUtils.getRootCause(ex);
				if (rootCause == null) rootCause = ex;
				return ex;
			}
		}
		return null;
	}
	
	public void portafirmesCancelar(
			Long entitatId,
			DocumentEntity document) {
		logger.debug("Enviant document a portafirmes (" +
				"entitatId=" + entitatId + ", " +
				"id=" + document.getId() + ")");
		cacheHelper.evictEnviamentsPortafirmesPendentsPerExpedient(document.getExpedientPare());
		cacheHelper.evictEnviamentsPortafirmesAmbErrorPerExpedient(document.getExpedientPare());
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
	public Exception portafirmesCallback(
			long portafirmesId,
			PortafirmesCallbackEstatEnumDto callbackEstat,
			String motiuRebuig) {
		logger.debug("Processant petició del callback ("
				+ "portafirmesId=" + portafirmesId + ", "
				+ "callbackEstat=" + callbackEstat + ")");
		DocumentPortafirmesEntity documentPortafirmes = documentPortafirmesRepository.findByPortafirmesId(
				new Long(portafirmesId).toString());
		if (documentPortafirmes == null) {
			return new NotFoundException(
					"(portafirmesId=" + portafirmesId + ")",
					DocumentPortafirmesEntity.class);
		}
		contingutLogHelper.log(
				documentPortafirmes.getDocument(),
				LogTipusEnumDto.PFIRMA_CALLBACK,
				documentPortafirmes.getPortafirmesId(),
				documentPortafirmes.getEstat().name(),
				false,
				false);
		documentPortafirmes.updateCallbackEstat(callbackEstat);
		documentPortafirmes.updateMotiuRebuig(motiuRebuig);
		return portafirmesProcessar(documentPortafirmes);
	}	

	////
	// FUNCIONS VIA FIRMA
	////
	
	public void viaFirmaEnviar(DocumentViaFirmaEntity documentViaFirma) throws SistemaExternException {
		DocumentEntity document = documentViaFirma.getDocument();
		try {
			String messageCode = pluginHelper.viaFirmaUpload(
					document,
					documentViaFirma);
			documentViaFirma.updateEnviat(
					new Date(),
					messageCode);
			cacheHelper.evictEnviamentsPortafirmesPendentsPerExpedient(document.getExpedientPare());
		} catch (Exception ex) {
			Throwable rootCause = ExceptionUtils.getRootCause(ex);
			if (rootCause == null) rootCause = ex;
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_VIAFIRMA,
					rootCause.getMessage());
		}
		
		documentViaFirmaRepository.save(documentViaFirma);
		document.updateEstat(
				DocumentEstatEnumDto.FIRMA_PENDENT_VIAFIRMA);
		contingutLogHelper.log(
				document,
				LogTipusEnumDto.VFIRMA_ENVIAMENT,
				documentViaFirma.getMessageCode(),
				documentViaFirma.getEstat().name(),
				false,
				false);
	}
	
	public Exception viaFirmaProcessar(
			DocumentViaFirmaEntity documentViaFirma) {
		DocumentEntity document = documentViaFirma.getDocument();
		ViaFirmaCallbackEstatEnumDto callbackEstat = documentViaFirma.getCallbackEstat();
		if (ViaFirmaCallbackEstatEnumDto.RESPONSED.equals(callbackEstat)) {
			cacheHelper.evictEnviamentsPortafirmesPendentsPerExpedient(document.getExpedientPare());
			document.updateEstat(
					DocumentEstatEnumDto.FIRMAT);
			ViaFirmaDocument viaFirmaDocument = null;
			// Descarrega el document firmat del portafirmes
			try {
				viaFirmaDocument = pluginHelper.viaFirmaDownload(
						documentViaFirma);
			} catch (Exception ex) {
				logger.error("Error al descarregar document de Viafirma (id=" + documentViaFirma.getId() + ")", ex);
				cacheHelper.evictEnviamentsPortafirmesAmbErrorPerExpedient(document.getExpedientPare());
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
					fitxer.setNomFitxerFirmat(viaFirmaDocument.getNomFitxer());
					fitxer.setContingut(contingut);
					fitxer.setContentType("application/pdf");
					documentViaFirma.updateProcessat(
								true,
								new Date());
					String custodiaDocumentId = pluginHelper.arxiuDocumentGuardarFirmaPades(
							document,
							fitxer);
					document.updateInformacioCustodia(
							new Date(),
							custodiaDocumentId,
							document.getCustodiaCsv());
					documentHelper.actualitzarVersionsDocument(document);
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
				logger.error("Error al custodiar document de Viafirma (id=" + documentViaFirma.getId() + ")", ex);
				cacheHelper.evictEnviamentsPortafirmesAmbErrorPerExpedient(document.getExpedientPare());
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
				cacheHelper.evictEnviamentsPortafirmesPendentsPerExpedient(document.getExpedientPare());
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
				cacheHelper.evictEnviamentsPortafirmesPendentsPerExpedient(document.getExpedientPare());
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
				cacheHelper.evictEnviamentsPortafirmesPendentsPerExpedient(document.getExpedientPare());
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
				cacheHelper.evictEnviamentsPortafirmesPendentsPerExpedient(document.getExpedientPare());
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

	public void viaFirmaReintentar(DocumentViaFirmaEntity documentPortafirmes) {
		contingutLogHelper.log(
				documentPortafirmes.getDocument(),
				LogTipusEnumDto.VFIRMA_REINTENT,
				documentPortafirmes.getMessageCode(),
				documentPortafirmes.getEstat().name(),
				false,
				false);
		if (DocumentEnviamentEstatEnumDto.PENDENT.equals(documentPortafirmes.getEstat())) {
			viaFirmaEnviar(documentPortafirmes);
		} else if (DocumentEnviamentEstatEnumDto.ENVIAT.equals(documentPortafirmes.getEstat())) {
			viaFirmaProcessar(documentPortafirmes);
		}
	}
		
	public void viaFirmaCancelar(DocumentViaFirmaEntity documentViaFirma) {
		DocumentEntity document = documentViaFirma.getDocument();
		documentViaFirma.updateMessageCode(null);
		documentViaFirma.updateCancelat(new Date());
		document.updateEstat(
				DocumentEstatEnumDto.REDACCIO);
		contingutLogHelper.log(
				document,
				LogTipusEnumDto.VFIRMA_CANCELACIO,
				documentViaFirma.getMessageCode(),
				documentViaFirma.getEstat().name(),
				false,
				false);
	}
	
	public Exception viaFirmaCallback(DocumentViaFirmaEntity documentViaFirma, ViaFirmaCallbackEstatEnumDto callbackEstat) {
		DocumentEntity document = documentViaFirma.getDocument();
		documentViaFirma.updateMessageCode(null);
		documentViaFirma.updateCancelat(new Date());
		document.updateEstat(
				DocumentEstatEnumDto.REDACCIO);
		contingutLogHelper.log(
				document,
				LogTipusEnumDto.VFIRMA_CANCELACIO,
				documentViaFirma.getMessageCode(),
				documentViaFirma.getEstat().name(),
				false,
				false);
		contingutLogHelper.log(
				documentViaFirma.getDocument(),
				LogTipusEnumDto.VFIRMA_CALLBACK,
				documentViaFirma.getMessageCode(),
				documentViaFirma.getEstat().name(),
				false,
				false);
		documentViaFirma.updateCallbackEstat(callbackEstat);
		return viaFirmaProcessar(documentViaFirma);
	}
	
	////
	// FIRMA APPLET
	////

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
	
	////
	//
	////
	
	private void actualitzarInformacioFirma(
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
	
	
	private static final Logger logger = LoggerFactory.getLogger(DocumentHelper.class);
}
