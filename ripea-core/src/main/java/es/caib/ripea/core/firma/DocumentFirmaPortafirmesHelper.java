package es.caib.ripea.core.firma;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.ArxiuEstatEnumDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaDetallDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaPerfilEnumDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentFirmaTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentPortafirmesDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.FitxerAmbFirmaArxiuDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.core.api.dto.PortafirmesBlockDto;
import es.caib.ripea.core.api.dto.PortafirmesCallbackEstatEnumDto;
import es.caib.ripea.core.api.dto.PortafirmesPrioritatEnumDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.SistemaExternException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentPortafirmesEntity;
import es.caib.ripea.core.entity.DocumentViaFirmaEntity;
import es.caib.ripea.core.entity.PortafirmesBlockEntity;
import es.caib.ripea.core.entity.PortafirmesBlockInfoEntity;
import es.caib.ripea.core.helper.AlertaHelper;
import es.caib.ripea.core.helper.CacheHelper;
import es.caib.ripea.core.helper.ConfigHelper;
import es.caib.ripea.core.helper.ContingutHelper;
import es.caib.ripea.core.helper.ContingutLogHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.DocumentHelper;
import es.caib.ripea.core.helper.EmailHelper;
import es.caib.ripea.core.helper.ExceptionHelper;
import es.caib.ripea.core.helper.OrganGestorHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.repository.DocumentPortafirmesRepository;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.DocumentViaFirmaRepository;
import es.caib.ripea.core.repository.PortafirmesBlockInfoRepository;
import es.caib.ripea.core.repository.PortafirmesBlockRepository;
import es.caib.ripea.plugin.portafirmes.PortafirmesDocument;
import es.caib.ripea.plugin.portafirmes.PortafirmesDocumentFirmant;
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
	@Autowired
	private DocumentViaFirmaRepository documentViaFirmaRepository;
	@Autowired
	private ContingutHelper contingutHelper;
	@Autowired
	private OrganGestorHelper organGestorHelper;
    @Autowired
	private ConfigHelper configHelper;
	
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
		
		// Activar l'ús del tipus de document de portafirmes
		boolean tipusDocumentPortafirmes = configHelper.getAsBoolean("es.caib.ripea.activar.tipus.document.portafirmes");
		
		DocumentPortafirmesEntity documentPortafirmes = DocumentPortafirmesEntity.getBuilder(
				DocumentEnviamentEstatEnumDto.PENDENT,
				assumpte,
				prioritat,
				dataCaducitat,
				tipusDocumentPortafirmes ? document.getMetaDocument().getPortafirmesDocumentTipus() : StringUtils.stripStart(document.getMetaDocument().getNtiTipoDocumental(), "TD0"),
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
		cacheHelper.evictEnviamentsPortafirmesPendentsPerExpedient(document.getExpedient());
		if (sex != null) {
			throw sex;
		}
		documentPortafirmesRepository.save(documentPortafirmes);
		document.updateEstat(
				DocumentEstatEnumDto.FIRMA_PENDENT);
		logAll(document, documentPortafirmes, LogTipusEnumDto.PFIRMA_ENVIAMENT);
		
//		String idioma = aplicacioService.getUsuariActual().getIdioma();
//		List<PortafirmesBlockDto> portafirmesBlocks = pluginHelper.portafirmesRecuperarBlocksFirma(
//				(portafirmesFluxId != null && !portafirmesFluxId.isEmpty()) ? portafirmesFluxId : document.getMetaDocument().getPortafirmesFluxId(),
//				transaccioId,
//				portafirmesFluxTipus.equals(MetaDocumentFirmaFluxTipusEnumDto.PORTAFIB),
//				documentPortafirmes.getPortafirmesId(),
//				idioma);
//
//		if (portafirmesBlocks != null) {
//			int i = 1;
//			for (PortafirmesBlockDto portafirmesBlock : portafirmesBlocks) {
//				PortafirmesBlockEntity portafirmesBlockEntity = PortafirmesBlockEntity.getBuilder(
//						documentPortafirmes,
//						i).build();
//	
//				portafirmesBlockRepository.save(portafirmesBlockEntity);
//				for (PortafirmesBlockInfoDto portafirmesBlockInfo : portafirmesBlock.getSigners()) {
//					PortafirmesBlockInfoEntity portafirmesBlockInfoEntity = PortafirmesBlockInfoEntity.getBuilder(
//							portafirmesBlockEntity, 
//							portafirmesBlockInfo.getSignerNom(),
//							portafirmesBlockInfo.getSignerCodi(),
//							portafirmesBlockInfo.getSignerId(),
//							false).build();
//					portafirmesBlockInfoRepository.save(portafirmesBlockInfoEntity);
//				}
//				i++;
//			}
//		}
	}
	
	public DocumentPortafirmesDto portafirmesInfo(
			Long enviamentId) {

		DocumentPortafirmesEntity enviament = documentPortafirmesRepository.findOne(
				enviamentId);
		return conversioTipusHelper.convertir(
				enviament,
				DocumentPortafirmesDto.class);
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
	
	public Exception portafirmesReintentar(
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
		Exception exception = null;
		if (DocumentEnviamentEstatEnumDto.PENDENT.equals(documentPortafirmes.getEstat())) {
			exception = portafirmesEnviar(
					documentPortafirmes,
					null);
		} else if (DocumentEnviamentEstatEnumDto.ENVIAT.equals(documentPortafirmes.getEstat())) {
			exception = portafirmesProcessar(documentPortafirmes);
		}
		cacheHelper.evictEnviamentsPortafirmesPendentsPerExpedient(document.getExpedient());
		return exception;
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

	/**
	 * Processes document received in callback from portafirmes
	 */
	public Exception portafirmesProcessar(
			DocumentPortafirmesEntity documentPortafirmes) {
		
		try {
			DocumentEntity document = documentPortafirmes.getDocument();
			organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(document.getId()));
			DocumentEstatEnumDto documentEstatAnterior = document.getEstat();
			PortafirmesCallbackEstatEnumDto callbackEstat = documentPortafirmes.getCallbackEstat();
			
			// =============================================== DOCUMENT WAS FIRMAT EN PORTAFIRMES ============================================
			if (PortafirmesCallbackEstatEnumDto.FIRMAT.equals(callbackEstat)) {
				PortafirmesDocument portafirmesDocument = null;
				
				portafirmesDocument = getDocumentFirmatPortafirmes(documentPortafirmes);
				
				try {
						
					String gestioDocumentalId = document.getGesDocFirmatId();
					if (gestioDocumentalId == null ) {
						gestioDocumentalId = pluginHelper.gestioDocumentalCreate(
								PluginHelper.GESDOC_AGRUPACIO_DOCS_FIRMATS_PORTAFIB,
								new ByteArrayInputStream(portafirmesDocument.getArxiuContingut()));
						document.setGesDocFirmatId(gestioDocumentalId);
						document.setNomFitxerFirmat(portafirmesDocument.getArxiuNom());
					}
					
					// ============================== SAVE IN ARXIU ==========================
					ArxiuEstatEnumDto arxiuEstat = documentHelper.getArxiuEstat(DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA, null);
					if (portafirmesDocument.getTipusFirma() == null || portafirmesDocument.getTipusFirma().isEmpty() || portafirmesDocument.getTipusFirma().equals("PAdES")) {
						
						List<ArxiuFirmaDto> firmes = null;
						if (pluginHelper.getPropertyArxiuFirmaDetallsActiu()) {
							firmes = pluginHelper.validaSignaturaObtenirFirmes(portafirmesDocument.getArxiuContingut(), null, "application/pdf", true);
						} else {
							ArxiuFirmaDto firma = documentHelper.getArxiuFirmaPades(portafirmesDocument.getArxiuNom(), portafirmesDocument.getArxiuContingut());
							firmes = Arrays.asList(firma);
						}
						
						document.updateDocumentFirmaTipus(DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA);
						
						contingutHelper.arxiuPropagarModificacio(
								document,
								firmes.get(0).getFitxer(),
								arxiuEstat == ArxiuEstatEnumDto.ESBORRANY ? DocumentFirmaTipusEnumDto.SENSE_FIRMA : DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA,
								firmes,
								arxiuEstat);


					} else { // i am not sure if cades is supported
						FitxerDto fitxer = documentHelper.getFitxerAssociatFirmat(
								document, 
								null);
						
						ArxiuFirmaDto arxiuFirma = new ArxiuFirmaDto();
						arxiuFirma.setFitxerNom(portafirmesDocument.getArxiuNom());
						arxiuFirma.setContingut(portafirmesDocument.getArxiuContingut());
						arxiuFirma.setTipusMime(portafirmesDocument.getArxiuMime());
						arxiuFirma.setTipus(ArxiuFirmaTipusEnumDto.CADES_DET);
						arxiuFirma.setPerfil(ArxiuFirmaPerfilEnumDto.BES);
						List<ArxiuFirmaDetallDto> detalls = new ArrayList<ArxiuFirmaDetallDto>();
						for (PortafirmesDocumentFirmant firmant: portafirmesDocument.getFirmants()) {
							ArxiuFirmaDetallDto detall = new ArxiuFirmaDetallDto();
							detall.setData(firmant.getData());
							detall.setEmissorCertificat(firmant.getEmissorCertificat());
							detall.setResponsableNif(firmant.getResponsableNif());
							detall.setResponsableNom(firmant.getResponsableNom());
							detalls.add(detall);
						}
						arxiuFirma.setDetalls(detalls);
						arxiuFirma.setAutofirma(true);
						
						
						if (arxiuEstat == ArxiuEstatEnumDto.ESBORRANY) {
							pluginHelper.arxiuPropagarFirmaSeparada(
									document,
									arxiuFirma.getFitxer());
						}
						contingutHelper.arxiuPropagarModificacio(
								document,
								fitxer,
								arxiuEstat == ArxiuEstatEnumDto.ESBORRANY ? DocumentFirmaTipusEnumDto.SENSE_FIRMA : DocumentFirmaTipusEnumDto.FIRMA_SEPARADA,
								Arrays.asList(arxiuFirma),
								arxiuEstat);
					}
					
					String gestioDocumentalDeleteId = document.getGesDocFirmatId();
					if (gestioDocumentalDeleteId != null ) {
						pluginHelper.gestioDocumentalDelete(
								gestioDocumentalDeleteId,
								PluginHelper.GESDOC_AGRUPACIO_DOCS_FIRMATS_PORTAFIB);
						document.setGesDocFirmatId(null);
					}
					
					actualitzarInfoDocumentPortafirmesGuardatArxiu(
							documentPortafirmes,
							documentEstatAnterior);
					

				} catch (Exception ex) {
					logger.error("Error al custodiar document de portafirmes (" +
							"id=" + documentPortafirmes.getId() + ", " +
							"portafirmesId=" + documentPortafirmes.getPortafirmesId() + ")",
							ex);
					cacheHelper.evictEnviamentsPortafirmesAmbErrorPerExpedient(document.getExpedient());
					documentPortafirmes.updateProcessatError(
							ExceptionUtils.getStackTrace(ExceptionHelper.getRootCauseOrItself(ex)),
							null);
					throw ex;
				}
				
				
			// ========================================== DOCUMENT WAS REBUTJAT EN PORTAFIRMES ==============================================
			} else if (PortafirmesCallbackEstatEnumDto.REBUTJAT.equals(callbackEstat)) {
				cacheHelper.evictEnviamentsPortafirmesPendentsPerExpedient(document.getExpedient());
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
			}
			return null;
		} catch (Exception e) {
			return e;
		}
	}
	
	
	private void actualitzarInfoDocumentPortafirmesGuardatArxiu(
			DocumentPortafirmesEntity documentPortafirmes,
			DocumentEstatEnumDto documentEstatAnterior) {
		DocumentEntity document = documentPortafirmes.getDocument();
		documentPortafirmes.updateProcessat(
				true,
				new Date());
		
		DocumentEstatEnumDto documentEstatNou = document.getEstat();
		if ((documentEstatAnterior != documentEstatNou)) {
			alertaHelper.crearAlerta(
					"La firma del document " + document.getNom() + " ha finalitzat correctament",
					null,
					document.getExpedient().getId());
			emailHelper.canviEstatDocumentPortafirmes(documentPortafirmes);
		}
		
	}
	
	private PortafirmesDocument getDocumentFirmatPortafirmes(DocumentPortafirmesEntity documentPortafirmes) {
		
		DocumentEntity document = documentPortafirmes.getDocument();
		
		cacheHelper.evictEnviamentsPortafirmesPendentsPerExpedient(document.getExpedient());
		document.updateEstat(DocumentEstatEnumDto.FIRMAT);
		logFirmat(document);

		PortafirmesDocument portafirmesDocument = null;
		try {
			String gestioDocumentalId = document.getGesDocFirmatId();
			
			if (gestioDocumentalId == null || gestioDocumentalId.isEmpty()) {
				// Descarrega el document firmat del portafirmes
				portafirmesDocument = pluginHelper.portafirmesDownload(
						documentPortafirmes);
			} else {
				portafirmesDocument = new PortafirmesDocument();
				ByteArrayOutputStream streamAnnex = new ByteArrayOutputStream();
				pluginHelper.gestioDocumentalGet(
						gestioDocumentalId,
						PluginHelper.GESDOC_AGRUPACIO_DOCS_FIRMATS_PORTAFIB,
						streamAnnex);
				portafirmesDocument.setArxiuNom(document.getNomFitxerFirmat());
				portafirmesDocument.setArxiuContingut(streamAnnex.toByteArray());
			}

		} catch (Exception ex) {
			logger.error("Error al descarregar document de portafirmes (" +
					"id=" + documentPortafirmes.getId() + ", " +
					"portafirmesId=" + documentPortafirmes.getPortafirmesId() + ")",
					ex);
			cacheHelper.evictEnviamentsPortafirmesAmbErrorPerExpedient(document.getExpedient());
			documentPortafirmes.updateProcessatError(
					ExceptionUtils.getStackTrace(ExceptionHelper.getRootCauseOrItself(ex)),
					null);
			throw ex;
		}
		return portafirmesDocument;
	}
	
	
	private FitxerAmbFirmaArxiuDto getFitxerAmbFirma(PortafirmesDocument portafirmesDocument, DocumentEntity document) {
		FitxerAmbFirmaArxiuDto fitxerAmbFirmaArxiuDto = new FitxerAmbFirmaArxiuDto();
		FitxerDto fitxer = new FitxerDto();
		
		ArxiuFirmaDto arxiuFirma = new ArxiuFirmaDto();
		
		fitxer = documentHelper.getFitxerAssociatFirmat(
				document, 
				null);
		arxiuFirma.setFitxerNom(portafirmesDocument.getArxiuNom());
		arxiuFirma.setContingut(portafirmesDocument.getArxiuContingut());
		arxiuFirma.setTipusMime(portafirmesDocument.getArxiuMime());
		arxiuFirma.setTipus(ArxiuFirmaTipusEnumDto.CADES_DET);
		arxiuFirma.setPerfil(ArxiuFirmaPerfilEnumDto.BES);
		
		List<ArxiuFirmaDetallDto> detalls = new ArrayList<ArxiuFirmaDetallDto>();
		for (PortafirmesDocumentFirmant firmant: portafirmesDocument.getFirmants()) {
			ArxiuFirmaDetallDto detall = new ArxiuFirmaDetallDto();
			detall.setData(firmant.getData());
			detall.setEmissorCertificat(firmant.getEmissorCertificat());
			detall.setResponsableNif(firmant.getResponsableNif());
			detall.setResponsableNom(firmant.getResponsableNom());
			detalls.add(detall);
		}
		arxiuFirma.setDetalls(detalls);
		arxiuFirma.setAutofirma(true);
		
		
		fitxerAmbFirmaArxiuDto.setFitxer(fitxer);
		fitxerAmbFirmaArxiuDto.setArxiuFirma(arxiuFirma);
		
		return fitxerAmbFirmaArxiuDto;
	}
	
	
	public void portafirmesCancelar(
			Long entitatId,
			DocumentEntity document, String rolActual) {
		logger.debug("Enviant document a portafirmes (" +
				"entitatId=" + entitatId + ", " +
				"id=" + document.getId() + ")");
		cacheHelper.evictEnviamentsPortafirmesPendentsPerExpedient(document.getExpedient());
		cacheHelper.evictEnviamentsPortafirmesAmbErrorPerExpedient(document.getExpedient());
		boolean hasFirmaParcial = false;
		List<DocumentViaFirmaEntity> enviamentsViaFirmaProcessats = documentViaFirmaRepository.findByDocumentAndEstatInOrderByCreatedDateDesc(
				document, 
				new DocumentEnviamentEstatEnumDto[] {DocumentEnviamentEstatEnumDto.PROCESSAT});
		if (enviamentsViaFirmaProcessats != null && ! enviamentsViaFirmaProcessats.isEmpty()) {
			hasFirmaParcial = enviamentsViaFirmaProcessats.get(0).isFirmaParcial();
		}
		
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
		if (!hasFirmaParcial)
			document.updateEstat(DocumentEstatEnumDto.REDACCIO);
		else
			document.updateEstat(DocumentEstatEnumDto.FIRMA_PARCIAL);
		
		logAll(document, documentPortafirmes, LogTipusEnumDto.PFIRMA_CANCELACIO);
	}
	
	public List<PortafirmesBlockDto> recuperarBlocksFirmaEnviament(
			Long entitatId,
			DocumentEntity document, 
			Long enviamentId) {
		logger.debug("Recuperar els blocks de firma d'un enviament a Portafirmes (" +
				"entitatId=" + entitatId + ", " +
				"id=" + document.getId() + ")");
		List<PortafirmesBlockDto> portafirmesBlockDto = null;
		DocumentPortafirmesEntity documentPortafirmes = null;
		if (enviamentId != null) {
			documentPortafirmes = documentPortafirmesRepository.findOne(enviamentId);
		} else {
			List<DocumentPortafirmesEntity> enviamentsPendents = documentPortafirmesRepository.findByDocumentAndEstatInOrderByCreatedDateDesc(
					document,
					new DocumentEnviamentEstatEnumDto[] {DocumentEnviamentEstatEnumDto.ENVIAT});
			if (enviamentsPendents.size() == 0) {
				throw new ValidationException(
						document.getId(),
						DocumentEntity.class,
						"Aquest document no te enviaments a portafirmes pendents");
			}
			documentPortafirmes = enviamentsPendents.get(0);
		}
		
		
		if (DocumentEnviamentEstatEnumDto.ENVIAT.equals(documentPortafirmes.getEstat())) {
			List<PortafirmesBlockEntity> portafirmesBlocks = portafirmesBlockRepository.findByEnviament(documentPortafirmes);
			Collections.sort(portafirmesBlocks, new Comparator<PortafirmesBlockEntity>() {
				@Override
				public int compare(PortafirmesBlockEntity o1, PortafirmesBlockEntity o2) {
					if (o1.getOrder() < o2.getOrder())
						return -1;
					else
						return 1;
				}
			});
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
			String motiuRebuig,
			String administrationId,
			String name) {
		logger.debug("Processant petició del callback ("
				+ "portafirmesId=" + portafirmesId + ", "
				+ "callbackEstat=" + callbackEstat + ")");
		DocumentPortafirmesEntity documentPortafirmes = documentPortafirmesRepository.findByPortafirmesId(
				new Long(portafirmesId).toString());
		if (documentPortafirmes == null) {
			logger.error("Se ha recibido al callback de portafib petició de firma que no existe en ripea ("
				+ "portafirmesId=" + portafirmesId + ", "
				+ "callbackEstat=" + callbackEstat + ")");
			return null;
		} else {
			
			EntitatDto entitat = conversioTipusHelper.convertir(documentPortafirmes.getDocument().getEntitat(), EntitatDto.class);
			ConfigHelper.setEntitat(entitat);
			
			logAll(documentPortafirmes, LogTipusEnumDto.PFIRMA_CALLBACK);
			// Només actualitzam el estat si l'estat del document a base de dades no és un
			// estat final (FIRMAT o REBUTJAT).
			boolean isAlreadyFinal = (documentPortafirmes.getCallbackEstat() == PortafirmesCallbackEstatEnumDto.FIRMAT && documentPortafirmes.getDocument().getGesDocFirmatId() == null) || documentPortafirmes.getCallbackEstat() == PortafirmesCallbackEstatEnumDto.REBUTJAT;
			if (!isAlreadyFinal) {
				documentPortafirmes.updateCallbackEstat(callbackEstat);
				documentPortafirmes.updateMotiuRebuig(motiuRebuig);
				documentPortafirmes.updateAdministrationId(administrationId);
				documentPortafirmes.updateName(name);
				return portafirmesProcessar(documentPortafirmes);
			} else {
				return null;
			}
		}
		

	}

//	private void actualitzarBlocksPortafirmes(
//			PortafirmesCallbackEstatEnumDto callbackEstat,
//			DocumentPortafirmesEntity documentPortafirmes,
//			String administrationId) {
//		List<PortafirmesBlockEntity> portafirmesBlocks = null;
//		switch (callbackEstat) {
//		case PARCIAL:
//			if (administrationId != null) {
//				PortafirmesDocument portafirmesDocument = pluginHelper.portafirmesDownload(documentPortafirmes);
//				if ((portafirmesDocument.getTipusFirma() != null && portafirmesDocument.getTipusFirma().equals("PAdES"))
//						|| (portafirmesDocument.getFirmants() != null && !portafirmesDocument.getFirmants().isEmpty())) {
//					for (PortafirmesDocumentFirmant firmant: portafirmesDocument.getFirmants()) {
//						// Actualitza data firma blocs
//						actualitzarDataFirmaBlocksPortafirmes(
//								documentPortafirmes, 
//								firmant.getResponsableNif(), 
//								firmant.getData());
//					}
//				} 
//				List<PortafirmesBlockEntity> portafirmesBlocksEntity = portafirmesBlockRepository.findByEnviament(documentPortafirmes);
//				if (portafirmesBlocksEntity != null && !portafirmesBlocksEntity.isEmpty()) {
//					for (PortafirmesBlockEntity portafirmesBlockEntity : portafirmesBlocksEntity) {
//						PortafirmesBlockInfoEntity portafirmesBlockInfoEntity = portafirmesBlockInfoRepository.findBySignerIdAndPortafirmesBlock(
//								administrationId,
//								portafirmesBlockEntity);
//						if (portafirmesBlockInfoEntity != null && portafirmesBlockInfoEntity.getSignerId() != null && portafirmesBlockInfoEntity.getSignerId().equals(administrationId))
//							portafirmesBlockInfoEntity.updateSigned(true);
//					}
//				}
//			}
//			break;
//		case REBUTJAT:
//			portafirmesBlocks = portafirmesBlockRepository.findByEnviament(documentPortafirmes);
//			if (portafirmesBlocks != null) {
//				for (PortafirmesBlockEntity portafirmesBlock : portafirmesBlocks) {
//					portafirmesBlockRepository.delete(portafirmesBlock);
//				}
//			} else {
//				logger.error(
//						"No s'ha trobat cap block de firma relacionat amb aquest enviament", 
//						new NotFoundException(
//								"(portafirmesId=" + documentPortafirmes.getId() + ")",
//								PortafirmesBlockEntity.class));
//			}
//			break;
//		case FIRMAT:
//			portafirmesBlocks = portafirmesBlockRepository.findByEnviament(documentPortafirmes);
//			if (portafirmesBlocks != null) {
//				for (PortafirmesBlockEntity portafirmesBlock : portafirmesBlocks) {
//					portafirmesBlockRepository.delete(portafirmesBlock);
//				}
//			} else {
//				logger.error(
//						"No s'ha trobat cap block de firma relacionat amb aquest enviament", 
//						new NotFoundException(
//								"(portafirmesId=" + documentPortafirmes.getId() + ")",
//								PortafirmesBlockEntity.class));
//			}
//			break;
//		default:
//			break;
//		}
//	}
	
//	private void actualitzarDataFirmaBlocksPortafirmes(
//			DocumentPortafirmesEntity documentPortafirmes, 
//			String administrationId,
//			Date signDate) {
//		try {
//			List<PortafirmesBlockEntity> portafirmesBlocksEntity = portafirmesBlockRepository.findByEnviament(documentPortafirmes);
//			if (portafirmesBlocksEntity != null && !portafirmesBlocksEntity.isEmpty()) {
//				for (PortafirmesBlockEntity portafirmesBlockEntity : portafirmesBlocksEntity) {
//					PortafirmesBlockInfoEntity portafirmesBlockInfoEntity = portafirmesBlockInfoRepository.findBySignerIdAndPortafirmesBlock(
//							administrationId,
//							portafirmesBlockEntity);
//					if (portafirmesBlockInfoEntity != null && portafirmesBlockInfoEntity.getSignerId() != null && portafirmesBlockInfoEntity.getSignerId().equals(administrationId))
//						portafirmesBlockInfoEntity.updateSignDate(signDate);
//				}
//			}
//		} catch (Exception e) {
//			logger.error("Hi ha hagut un problema actualitzant el bloc de firma amb la data de firma [administrationId=" + administrationId + ", signDate=" + signDate + "]");
//		}
//	}

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
