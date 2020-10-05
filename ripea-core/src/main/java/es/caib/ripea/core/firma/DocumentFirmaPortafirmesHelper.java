package es.caib.ripea.core.firma;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentPortafirmesDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.core.api.dto.PortafirmesBlockDto;
import es.caib.ripea.core.api.dto.PortafirmesBlockInfoDto;
import es.caib.ripea.core.api.dto.PortafirmesCallbackEstatEnumDto;
import es.caib.ripea.core.api.dto.PortafirmesPrioritatEnumDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.SistemaExternException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentPortafirmesEntity;
import es.caib.ripea.core.entity.PortafirmesBlockEntity;
import es.caib.ripea.core.entity.PortafirmesBlockInfoEntity;
import es.caib.ripea.core.helper.AlertaHelper;
import es.caib.ripea.core.helper.CacheHelper;
import es.caib.ripea.core.helper.ContingutLogHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.DocumentHelper;
import es.caib.ripea.core.helper.EmailHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.repository.DocumentPortafirmesRepository;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.PortafirmesBlockInfoRepository;
import es.caib.ripea.core.repository.PortafirmesBlockRepository;
import es.caib.ripea.plugin.portafirmes.PortafirmesDocument;
import es.caib.ripea.plugin.portafirmes.PortafirmesPrioritatEnum;

@Component
public class DocumentFirmaPortafirmesHelper extends DocumentFirmaHelper{

	@Autowired
	private DocumentRepository documentRepository;
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
	private ContingutLogHelper contingutLogHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private DocumentHelper documentHelper;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private PortafirmesBlockRepository portafirmesBlockRepository;
	@Autowired
	private PortafirmesBlockInfoRepository portafirmesBlockInfoRepository;
	
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
				(portafirmesFluxId != null && !portafirmesFluxId.isEmpty()) ? portafirmesFluxId : document.getMetaDocument().getPortafirmesFluxId(),
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
		logAll(document, documentPortafirmes, LogTipusEnumDto.PFIRMA_ENVIAMENT);
		
		String idioma = aplicacioService.getUsuariActual().getIdioma();
		List<PortafirmesBlockDto> portafirmesBlocks = pluginHelper.portafirmesRecuperarBlocksFirma(
				(portafirmesFluxId != null && !portafirmesFluxId.isEmpty()) ? portafirmesFluxId : document.getMetaDocument().getPortafirmesFluxId(),
				transaccioId,
				portafirmesFluxTipus.equals(MetaDocumentFirmaFluxTipusEnumDto.PORTAFIB),
				documentPortafirmes.getPortafirmesId(),
				idioma);

		if (portafirmesBlocks != null) {
			int i = 1;
			for (PortafirmesBlockDto portafirmesBlock : portafirmesBlocks) {
				PortafirmesBlockEntity portafirmesBlockEntity = PortafirmesBlockEntity.getBuilder(
						documentPortafirmes,
						i).build();
	
				portafirmesBlockRepository.save(portafirmesBlockEntity);
				for (PortafirmesBlockInfoDto portafirmesBlockInfo : portafirmesBlock.getSigners()) {
					PortafirmesBlockInfoEntity portafirmesBlockInfoEntity = PortafirmesBlockInfoEntity.getBuilder(
							portafirmesBlockEntity, 
							portafirmesBlockInfo.getSignerNom(),
							portafirmesBlockInfo.getSignerCodi(),
							portafirmesBlockInfo.getSignerId(),
							false).build();
					portafirmesBlockInfoRepository.save(portafirmesBlockInfoEntity);
				}
				i++;
			}
		}
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
		logAll(documentPortafirmes, LogTipusEnumDto.PFIRMA_REINTENT);
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
						logExpedient(documentPortafirmes, LogTipusEnumDto.ARXIU_CUSTODIAT);
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
				logAll(documentPortafirmes, LogTipusEnumDto.PFIRMA_REBUIG);
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
			List<PortafirmesBlockEntity> portafirmesBlocks = portafirmesBlockRepository.findByEnviament(documentPortafirmes);
			if (portafirmesBlocks != null && !portafirmesBlocks.isEmpty()) {
				for (PortafirmesBlockEntity portafirmesBlock : portafirmesBlocks) {
					portafirmesBlockRepository.delete(portafirmesBlock);
				}
			}
		}
		documentPortafirmes.updateCancelat(new Date());
		document.updateEstat(
				DocumentEstatEnumDto.REDACCIO);
		logAll(document, documentPortafirmes, LogTipusEnumDto.PFIRMA_CANCELACIO);
	}
	
	public List<PortafirmesBlockDto> recuperarBlocksFirmaEnviament(
			Long entitatId,
			DocumentEntity document) {
		logger.debug("Recuperar els blocks de firma d'un enviament a Portafirmes (" +
				"entitatId=" + entitatId + ", " +
				"id=" + document.getId() + ")");
		List<PortafirmesBlockDto> portafirmesBlockDto = null;
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
			List<PortafirmesBlockEntity> portafirmesBlocks = portafirmesBlockRepository.findByEnviament(documentPortafirmes);
			if (portafirmesBlocks != null) {
				portafirmesBlockDto = conversioTipusHelper.convertirList(
							portafirmesBlocks, 
							PortafirmesBlockDto.class);
			}
		}
		return portafirmesBlockDto;
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
		logAll(documentPortafirmes, LogTipusEnumDto.PFIRMA_CALLBACK);
		documentPortafirmes.updateCallbackEstat(callbackEstat);
		documentPortafirmes.updateMotiuRebuig(motiuRebuig);
		return portafirmesProcessar(documentPortafirmes);
	}	


	/**
	 * Registra el log al document i al expedient on està el document.
	 *  
	 * @param documentPortafirmes 
	 * @param tipusLog
	 */
	private void logAll(DocumentPortafirmesEntity documentPortafirmes, LogTipusEnumDto tipusLog) {
		logAll(documentPortafirmes.getDocument(), documentPortafirmes, tipusLog);
	}

	/**
	 * Registra el log al document i al expedient on està el document.
	 * Pots especificar directament el document firmat.
	 * Usar quan el document no està associat a l'objecte documentPortafirmes.
	 *   
	 * @param documentPortafirmes 
	 * @param tipusLog
	 */
	private void logAll(DocumentEntity document, DocumentPortafirmesEntity documentPortafirmes, LogTipusEnumDto tipusLog) {
		contingutLogHelper.log(
				document,
				tipusLog,
				documentPortafirmes.getPortafirmesId(),
				documentPortafirmes.getEstat().name(),
				false,
				false);
		logExpedient(documentPortafirmes, tipusLog);
	}
	
	/**
	 * Registre el log al expedient on està ubicat el document a firmar/firmat
	 * @param documentPortafirmes
	 * @param tipusLog
	 */
	private void logExpedient(DocumentPortafirmesEntity documentPortafirmes, LogTipusEnumDto tipusLog) {
		contingutLogHelper.log(
				documentPortafirmes.getExpedient(),
				LogTipusEnumDto.MODIFICACIO,
				documentPortafirmes,
				LogObjecteTipusEnumDto.DOCUMENT,
				tipusLog,
				documentPortafirmes.getPortafirmesId(),
				documentPortafirmes.getEstat().name(),
				false,
				false);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(DocumentFirmaPortafirmesHelper.class);
}
