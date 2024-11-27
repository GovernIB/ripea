/**
 * 
 */
package es.caib.ripea.core.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.plugins.arxiu.api.ArxiuNotFoundException;
import es.caib.plugins.arxiu.api.Document;
import es.caib.portafib.ws.api.v1.WsValidationException;
import es.caib.ripea.core.api.dto.ArbreJsonDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaDetallDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaDto;
import es.caib.ripea.core.api.dto.ConsultaPinbalEstatEnumDto;
import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.ContingutMassiuFiltreDto;
import es.caib.ripea.core.api.dto.ContingutTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.core.api.dto.DocumentPortafirmesDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentViaFirmaDto;
import es.caib.ripea.core.api.dto.FirmaResultatDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentPinbalServeiEnumDto;
import es.caib.ripea.core.api.dto.NtiOrigenEnumDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermissionEnumDto;
import es.caib.ripea.core.api.dto.PinbalConsultaDto;
import es.caib.ripea.core.api.dto.PortafirmesBlockDto;
import es.caib.ripea.core.api.dto.PortafirmesCallbackEstatEnumDto;
import es.caib.ripea.core.api.dto.PortafirmesDocumentTipusDto;
import es.caib.ripea.core.api.dto.PortafirmesPrioritatEnumDto;
import es.caib.ripea.core.api.dto.RespostaJustificantEnviamentNotibDto;
import es.caib.ripea.core.api.dto.Resum;
import es.caib.ripea.core.api.dto.SignatureInfoDto;
import es.caib.ripea.core.api.dto.TascaEstatEnumDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.dto.ViaFirmaCallbackEstatEnumDto;
import es.caib.ripea.core.api.dto.ViaFirmaDispositiuDto;
import es.caib.ripea.core.api.dto.ViaFirmaEnviarDto;
import es.caib.ripea.core.api.dto.ViaFirmaRespostaDto;
import es.caib.ripea.core.api.dto.ViaFirmaUsuariDto;
import es.caib.ripea.core.api.exception.ArxiuNotFoundDocumentException;
import es.caib.ripea.core.api.exception.ContingutNotUniqueException;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ResponsableNoValidPortafirmesException;
import es.caib.ripea.core.api.exception.SistemaExternException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.core.entity.ConsultaPinbalEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DispositiuEnviamentEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentEnviamentInteressatEntity;
import es.caib.ripea.core.entity.DocumentNotificacioEntity;
import es.caib.ripea.core.entity.DocumentViaFirmaEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientTascaEntity;
import es.caib.ripea.core.entity.InteressatAdministracioEntity;
import es.caib.ripea.core.entity.InteressatEntity;
import es.caib.ripea.core.entity.InteressatPersonaFisicaEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.entity.ViaFirmaUsuariEntity;
import es.caib.ripea.core.firma.DocumentFirmaAppletHelper;
import es.caib.ripea.core.firma.DocumentFirmaAppletHelper.ObjecteFirmaApplet;
import es.caib.ripea.core.firma.DocumentFirmaPortafirmesHelper;
import es.caib.ripea.core.firma.DocumentFirmaViaFirmaHelper;
import es.caib.ripea.core.helper.CacheHelper;
import es.caib.ripea.core.helper.ContingutHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.DateHelper;
import es.caib.ripea.core.helper.DocumentHelper;
import es.caib.ripea.core.helper.DocumentNotificacioHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.ExceptionHelper;
import es.caib.ripea.core.helper.IntegracioHelper;
import es.caib.ripea.core.helper.MetaExpedientHelper;
import es.caib.ripea.core.helper.OrganGestorHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PaginacioHelper.Converter;
import es.caib.ripea.core.helper.PinbalHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.helper.SynchronizationHelper;
import es.caib.ripea.core.helper.UsuariHelper;
import es.caib.ripea.core.helper.ViaFirmaHelper;
import es.caib.ripea.core.repository.ConsultaPinbalRepository;
import es.caib.ripea.core.repository.DispositiuEnviamentRepository;
import es.caib.ripea.core.repository.DocumentEnviamentInteressatRepository;
import es.caib.ripea.core.repository.DocumentNotificacioRepository;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.DocumentViaFirmaRepository;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.ExpedientTascaRepository;
import es.caib.ripea.core.repository.InteressatRepository;
import es.caib.ripea.core.repository.UsuariRepository;
import es.caib.ripea.plugin.notificacio.RespostaJustificantEnviamentNotib;

/**
 * Implementació dels mètodes per a gestionar documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class DocumentServiceImpl implements DocumentService {

	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private ConsultaPinbalRepository consultaPinbalRepository;	
	@Autowired
	private DocumentViaFirmaRepository documentViaFirmaRepository;
	@Autowired
	private DispositiuEnviamentRepository dispositiuEnviamentRepository;
	@Resource
	private DocumentNotificacioRepository documentNotificacioRepository;
	@Autowired
	private InteressatRepository interessatRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private ContingutHelper contingutHelper;
	@Autowired
	private DocumentHelper documentHelper;
	@Autowired
	private DocumentFirmaPortafirmesHelper firmaPortafirmesHelper;
	@Autowired
	private DocumentFirmaViaFirmaHelper firmaViaFirmaHelper;
	@Autowired
	private DocumentFirmaAppletHelper firmaAppletHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private ViaFirmaHelper viaFirmaHelper;
	@Autowired
	private DocumentEnviamentInteressatRepository documentEnviamentInteressatRepository;
	@Autowired
	private DocumentNotificacioHelper documentNotificacioHelper;
	@Autowired
	private MetaExpedientHelper metaExpedientHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private PinbalHelper pinbalHelper;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private OrganGestorHelper organGestorHelper;
	@Autowired
	private IntegracioHelper integracioHelper;
	@Autowired
	private ExpedientTascaRepository expedientTascaRepository;
	@Autowired
	private UsuariHelper usuariHelper;
	@Autowired
	private EntitatRepository entitatRepository;

	@Transactional
	@Override
	public DocumentDto create(
			Long entitatId,
			Long pareId,
			DocumentDto document,
			boolean comprovarMetaExpedient, 
			String rolActual, 
			Long tascaId) {
		if (cacheHelper.mostrarLogsCreacioContingut())
			logger.info("[DOC] Creant nou document (" +
				"entitatId=" + entitatId + ", " +
				"pareId=" + pareId + ", " +
				"document=" + document + ", " +
				"tascaId=" + tascaId + ")");
		
		ContingutEntity pare = null;
		if (tascaId == null) {
			pare = contingutHelper.comprovarContingutDinsExpedientModificable(
					entitatId,
					pareId,
					false,
					false,
					false,
					false, 
					false, 
					true, 
					rolActual);
		} else {
			pare = contingutHelper.comprovarContingutPertanyTascaAccesible(
					tascaId,
					pareId);
			
			ExpedientTascaEntity expedientTascaEntity = expedientTascaRepository.findOne(tascaId);
			if (expedientTascaEntity.getEstat() == TascaEstatEnumDto.PENDENT) {
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				expedientTascaEntity.updateEstat(TascaEstatEnumDto.INICIADA);
				UsuariEntity responsableActual = usuariHelper.getUsuariByCodiDades(auth.getName(), true, true);
				expedientTascaEntity.updateResponsableActual(responsableActual);
			}
		}

		if (! checkCarpetaUniqueContraint(document.getNom(), pare, entitatId)) {
			throw new ContingutNotUniqueException();
		}
		ExpedientEntity expedient = pare.getExpedientPare();
		if (cacheHelper.mostrarLogsCreacioContingut())
			logger.info("[DOC] Creant nou document (" +
					"expedient=" + expedient.getNom() + "(" + expedient.getId() + "), " +
					"metaExpedient=" + expedient.getMetaExpedient().getNom() + "(" + expedient.getMetaExpedient().getId() + "))");
		MetaDocumentEntity metaDocument = null;
		if (document.getMetaDocument() != null) {
			metaDocument = entityComprovarHelper.comprovarMetaDocument(
					pare.getEntitat(),
					expedient.getMetaExpedient(),
					document.getMetaDocument().getId(),
					true,
					comprovarMetaExpedient);
		} else {
			throw new ValidationException(
					"<creacio>",
					ExpedientEntity.class,
					"No es pot crear un document sense un meta-document associat");
		}
		DocumentDto documentDto = documentHelper.crearDocument(
				document,
				pare,
				expedient,
				metaDocument,
				true);

		if (cacheHelper.mostrarLogsCreacioContingut())
			logger.info("[DOC] Creat nou document (" +
					"nom=" + documentDto.getNom() + ", " +
					"id=" + document.getId() + ", " +
					"arxiuId=" + document.getArxiuUuid() + ")");
		
		
		return documentDto;
	}

	@Transactional
	@Override
	public DocumentDto update(
			Long entitatId,
			DocumentDto documentDto,
			boolean comprovarMetaExpedient, 
			String rolActual, 
			Long tascaId) {
		logger.debug("Actualitzant el document (" +
				"entitatId=" + entitatId + ", " +
				"id=" + documentDto.getId() + ", " +
				"document=" + documentDto + ")");
		
		DocumentEntity documentEntity = null;
		if (tascaId == null) {
			documentEntity = documentHelper.comprovarDocument(
					entitatId,
					documentDto.getId(),
					false,
					true,
					false,
					false, 
					false, 
					rolActual);
			if (documentDto.getPareId() != null) {
				contingutHelper.comprovarContingutDinsExpedientModificable(
						entitatId,
						documentDto.getPareId(),
						false,
						false,
						false,
						false, 
						false, 
						true, 
						rolActual);
			} 
		} else {
			documentEntity = (DocumentEntity) contingutHelper.comprovarContingutPertanyTascaAccesible(
					tascaId,
					documentDto.getId());
		}
		
		if (! checkCarpetaUniqueContraint(documentDto.getNom(), null, entitatId)) {
			throw new ContingutNotUniqueException();
		}
		return documentHelper.updateDocument(
				entitatId,
				documentEntity,
				documentDto,
				comprovarMetaExpedient);
	}
	
	@Override
	public SignatureInfoDto checkIfSignedAttached(
			byte[] contingut, 
			String contentType) {
		
		if (aplicacioService.getBooleanJbossProperty("es.caib.ripea.firma.detectar.attached.validate.signature", true)) {
			return pluginHelper.detectSignedAttachedUsingValidateSignaturePlugin(
					contingut,
					contentType);
		} else {
			return pluginHelper.detectSignedAttachedUsingPdfReader(
					contingut, 
					contentType);
		}
	}

    @Override
    public Resum getSummarize(byte[] bytes, String contentType) {

		return pluginHelper.getSummarize(bytes, contentType);

//		Resum resum = Resum.builder().build();
//		String documentText = null;
//
//		String resumUrl = configHelper.getConfig("es.caib.notib.summarize.resum.url");
//		String titolUrl = configHelper.getConfig("es.caib.notib.summarize.titol.url");
//
//		if (resumUrl == null && titolUrl == null)
//			return resum;
//
//		// Extreure el text del document
//		if ("application/pdf".equalsIgnoreCase(contentType)) {
//			documentText = extractTextFromPDF(bytes);
//		} else if ("application/vnd.openxmlformats-officedocument.wordprocessingml.document".equalsIgnoreCase(contentType)) {
//			documentText = extractTextFromDocx(bytes);
//		} else if ("application/vnd.oasis.opendocument.text".equalsIgnoreCase(contentType)) {
//			documentText = extractTextFromOdt(bytes);
//		}
//
//		if (documentText != null) {
//			resum = summarize(documentText, resumUrl, titolUrl);
//		}
//        return resum;
    }
	
//	private Resum summarize(String text, String resumUrl, String titolUrl) {
//		Resum resum = Resum.builder().build();
//
//		try {
//			RestTemplate restTemplate = new RestTemplate();
//			String tipusPeticio = configHelper.getConfig("es.caib.notib.summarize.url.tipus");
//
//			String summaryText = null;
//			String titleText = null;
//
//			if ("POST".equalsIgnoreCase(tipusPeticio)) {
//				// Crea els headers de la petició
//				HttpHeaders headers = new HttpHeaders();
//				headers.setContentType(MediaType.TEXT_PLAIN);
//
//				// Crea un objecte HttpEntity que inclou el cos de la petició i els headers
//				HttpEntity<String> requestEntity = new HttpEntity<>(text, headers);
//
//				// POST
//				if (resumUrl != null)
//					summaryText = restTemplate.postForObject(resumUrl, requestEntity, String.class);
//				if (titolUrl != null)
//					titleText = restTemplate.postForObject(titolUrl, requestEntity, String.class);
//
//			} else {
//
//				String params = "?text=" + URLEncoder.encode(text, "UTF-8");
//
//				// GET
//				if (resumUrl != null)
//					summaryText = restTemplate.getForObject(resumUrl + params, String.class);
//				if (titolUrl != null)
//					titleText = restTemplate.getForObject(titolUrl + params, String.class);
//
//			}
//
//			resum = Resum.builder()
//					.titol(titleText)
//					.resum(summaryText)
//					.build();
//
//		} catch (UnsupportedEncodingException e) {
//			logger.error("Error encoding text for summarize URL", e);
//		} catch (Exception e) {
//			logger.error("Error fetching summarized text from remote service", e);
//		}
//		return resum;
//	}


    @Transactional
	@Override
	public boolean updateTipusDocument(
			Long entitatId,
			Long documentId,
			Long tipusDocumentId,
			boolean comprovarMetaExpedient, 
			Long tascaId, 
			String rolActual) {
		logger.debug("Actualitzant el tipus de document del document (" +
				"entitatId=" + entitatId + ", " +
				"id=" + documentId + ", " +
				"tipusDocument=" + tipusDocumentId + ")");
		DocumentEntity document = null;
		
		if (tascaId != null) {
			document = contingutHelper.comprovarDocumentPerTasca(
					tascaId,
					documentId);
		} else {
			document = documentHelper.comprovarDocument(
					entitatId,
					documentId,
					false,
					true,
					false,
					false, 
					false, 
					rolActual);
		}

		
		if (!checkCarpetaUniqueContraint(
				document.getNom(),
				null,
				entitatId)) {
			throw new ContingutNotUniqueException();
		}
		return documentHelper.updateTipusDocumentDocument(
				entitatId,
				document,
				tipusDocumentId,
				comprovarMetaExpedient);
	}

	// Mètode implementat únicament per solucionar error de documents que s'han creat sense el seu tipus, i ja estan com a definitius
	@Transactional
	@Override
	public void updateTipusDocumentDefinitiu(Long entitatId, Long documentId, Long tipusDocumentId) {
		logger.debug("Actualitzant el tipus de document del document definitiu (" +
				"entitatId=" + entitatId + ", " +
				"id=" + documentId + ", " +
				"tipusDocument=" + tipusDocumentId + ")");
		DocumentEntity document = documentHelper.comprovarDocument(
				entitatId,
				documentId,
				false,
				true,
				false,
				false,
				false,
				null);

		documentHelper.updateTipusDocumentDefinitiu(
				document,
				tipusDocumentId);
	}

	@Transactional(readOnly = true)
	@Override
	public DocumentDto findById(
			Long entitatId,
			Long documentId, 
			Long tascaId) {
		logger.debug("Obtenint el document ("
				+ "entitatId=" + entitatId + ", "
				+ "documentId=" + documentId 
				+ "tascaId=" + tascaId + ")");
		
		DocumentEntity document = null;
		if (tascaId == null) {
			document = documentHelper.comprovarDocumentDinsExpedientAccessible(
					entitatId,
					documentId,
					true,
					false);
		} else {
			document = (DocumentEntity) contingutHelper.comprovarContingutPertanyTascaAccesible(
					tascaId,
					documentId);
		}

		return toDocumentDto(document);
	}


	
	
	@Transactional
	@Override
	public Exception guardarDocumentArxiu(
			Long docId) {
		
		Long expedientId = documentRepository.findExpedientId(docId);
		
		synchronized (SynchronizationHelper.get0To99Lock(expedientId, SynchronizationHelper.locksExpedients)) {
			return documentHelper.guardarDocumentArxiu(docId);
		}
	}
	
	
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public Long getAndSaveFitxerTamanyFromArxiu(Long documentId) {
		DocumentEntity documentEntity = null;
		try {
			documentEntity = documentRepository.findOne(documentId);
			Document documentArxiu = pluginHelper.arxiuDocumentConsultar(documentEntity.getArxiuUuid());
			long tamany = documentArxiu.getContingut().getTamany();
			documentEntity.updateFitxerTamany(tamany);
			return tamany;
		} catch (Throwable t) {
			logger.error("Error al descarregar fitxer tamany, documentId=" + documentId + "docuemntNom=" + (documentEntity != null ? documentEntity.getNom() : "") + "documentUuid=" + (documentEntity != null ? documentEntity.getArxiuUuid() : ""));
			return null;
		}
		
	}
	
	
	@Override
	public String firmaSimpleWebStartMassiu(
			Set<Long> ids,
			String motiu, 
			String urlReturnToRipea, 
			Long entitatId) {
		
		List <FitxerDto> fitxersPerFirmar = new ArrayList<>();
		for (Long id : ids) {
			FitxerDto fitxerPerFirmar = documentHelper.convertirPdfPerFirmaClient(
					entitatId,
					id);
			fitxerPerFirmar.setId(id);
			fitxersPerFirmar.add(fitxerPerFirmar);
		}

		UsuariDto usuariActual = aplicacioService.getUsuariActual();
		
		return pluginHelper.firmaSimpleWebStart(
				fitxersPerFirmar,
				motiu,
				usuariActual, 
				urlReturnToRipea);

	}
	
	
	
	
	@Override
	public String firmaSimpleWebStart(
			FitxerDto fitxerPerFirmar,
			String motiu, 
			String urlReturnToRipea) {

		UsuariDto usuariActual = aplicacioService.getUsuariActual();
		
		return pluginHelper.firmaSimpleWebStart(
				Arrays.asList(fitxerPerFirmar),
				motiu,
				usuariActual, 
				urlReturnToRipea);

	}
	
	@Override
	public FirmaResultatDto firmaSimpleWebEnd(
			String transactionID) {

		return pluginHelper.firmaSimpleWebEnd(transactionID);

	}
	
	@Transactional(readOnly = true)
	@Override
	public List<DocumentDto> findDocumentsNoFirmatsOAmbFirmaInvalidaONoGuardatsEnArxiu(
			Long entitatId,
			Long expedientId) {
		
		List<DocumentEntity> documents = documentHelper.findDocumentsNoFirmatsOAmbFirmaInvalidaONoGuardatsEnArxiu(
				entitatId,
				expedientId);
		
		List<DocumentDto> dtos = new ArrayList<DocumentDto>();
		for (DocumentEntity document: documents) {
			dtos.add(
					(DocumentDto)contingutHelper.toContingutDto(document, false, false));
		}
		
		return dtos;
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<Long> findIdsAllDocumentsOfExpedient(
			Long expedientId) {
		
		return documentRepository.findIdByExpedientIdAndEsborrat(expedientId, 0);
	}

//	@Transactional(readOnly = true)
//	@Override
//	public FitxerDto descarregarAllDocumentsOfExpedientWithFolders(
//			Long id, 
//			Long expedientId,
//			String rolActual,
//			Long tascaId) throws IOException {
//		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
//				expedientId, 
//				false, 
//				true, 
//				false, 
//				false, 
//				false, 
//				rolActual);
//		
//		FitxerDto resultat = new FitxerDto();
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		ZipOutputStream zos = new ZipOutputStream(baos);
//		
//		List<Long> documents = documentRepository.findIdByExpedientIdAndEsborrat(expedientId, 0);
//		
//		for (Long documentId : documents) {
//			documentHelper.crearEntradaDocument(
//					zos, 
//					documentId, 
//					tascaId, 
//					rolActual);
//		}
//
//		zos.close();
//		
//		resultat.setNom(expedient.getNom().replaceAll(" ", "_") + ".zip");
//		resultat.setContentType("application/zip");
//		resultat.setContingut(baos.toByteArray());
//		
//		return resultat;
//	}
	
	@Transactional(readOnly = true)
	@Override
	public FitxerDto descarregarAllDocumentsOfExpedientWithSelectedFolders(
			Long entitatId,
			Long expedientId,
			List<ArbreJsonDto> selectedElements,
			String rolActual,
			Long tascaId) throws IOException {
		entityComprovarHelper.comprovarEntitat(
				entitatId, 
				false, 
				false, 
				false, 
				true, 
				false);
		
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				expedientId, 
				false, 
				true, 
				false, 
				false, 
				false, 
				rolActual);
		
		FitxerDto resultat = new FitxerDto();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);
		
		cercarCrearEntradaSelectedDocument(selectedElements, zos, tascaId, rolActual);

		zos.close();
		
		resultat.setNom(expedient.getNom().replaceAll(" ", "_") + ".zip");
		resultat.setContentType("application/zip");
		resultat.setContingut(baos.toByteArray());
		
		return resultat;
	}
	
	private void cercarCrearEntradaSelectedDocument(List<ArbreJsonDto> childrens, ZipOutputStream zos, Long tascaId, String rolActual) throws IOException {
		for (ArbreJsonDto children: childrens) {
			Long contingutId = Long.valueOf(children.getId());
			ContingutEntity contingut = entityComprovarHelper.comprovarContingut(contingutId);
			
			if (contingut instanceof DocumentEntity) {
				// Si és document, crear document dins zip mantenint l'estructura
				documentHelper.crearEntradaDocument(
						zos, 
						contingutId, 
						tascaId, 
						rolActual);
			} else {
				// Fills
				cercarCrearEntradaSelectedDocument(children.getChildren(), zos, tascaId, rolActual);
			}
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<DocumentDto> findByExpedient(Long id, Long expedientId, String rolActual) {
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				expedientId, 
				false, 
				true, 
				false, 
				false, 
				false, 
				rolActual);
		
		List<DocumentEntity> documents = documentRepository.findByExpedientAndEsborrat(
				expedient, 
				0);
		
		List<DocumentDto> documentsDto = new ArrayList<DocumentDto>();
		if (Utils.isNotEmpty(documents)) {
			for (DocumentEntity documentEntity : documents) {
				DocumentDto documentDto = new DocumentDto();
				documentDto.setId(documentEntity.getId());
				documentDto.setNom(documentEntity.getNom());
				documentDto.setPareId(documentEntity.getPareId());
				documentsDto.add(documentDto);
			}
		}
		
		return documentsDto;
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<DocumentDto> findAnnexosAmbExpedient(
			Long entitatId,
			DocumentDto document) {
		if (document.getExpedientPare() == null)
			throw new ValidationException(
					document.getId(),
					DocumentEntity.class,
					"El document seleccionat no disposa d'expedient pare");
		Long expedientId = document.getExpedientPare().getId();
		
		logger.debug("Obtenint els documents (annexos) de l'expedient ("
				+ "entitatId=" + entitatId + ", "
				+ "expedientId=" + expedientId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId, 
				false, 
				false, 
				false, 
				true, false);
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				expedientId,
				false,
				false,
				false,
				false,
				false,
				null);
		List<DocumentEntity> documents = documentRepository.findByExpedientAndTipus(
				entitat, 
				expedient,
				document.getId());
		List<DocumentDto> documentsDto = new ArrayList<DocumentDto>();
		if (Utils.isNotEmpty(documents)) {
			for (DocumentEntity documentEntity : documents) {
				DocumentDto documentDto = new DocumentDto();
				documentDto.setId(documentEntity.getId());
				documentDto.setNom(documentEntity.getNom());
				documentsDto.add(documentDto);
			}
		}
		
		return documentsDto;
	}

	@Transactional(readOnly = true)
	@Override
	public FitxerDto infoDocument(
			Long entitatId,
			Long id,
			String versio) {
		logger.debug("Descarregant contingut del document ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ", "
				+ "versio=" + versio + ")");
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
				entitatId,
				id,
				true,
				false);
		return documentHelper.getFitxerAssociat(
				document,
				versio);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ArxiuFirmaDetallDto> getDetallSignants(
			Long entitatId,
			Long id,
			String versio) throws NotFoundException {
		logger.debug("Consultant el detall de les firmes d'un document ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ", "
				+ "versio=" + versio + ")");
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
				entitatId,
				id,
				true,
				false);
		if (document.getArxiuUuid() != null) {
			Document arxiuDocument = pluginHelper.arxiuDocumentConsultar(
					document,
					null,
					versio,
					true,
					false);
			List<ArxiuFirmaDto> arxiuFirmes = pluginHelper.validaSignaturaObtenirFirmes(
					documentHelper.getContingutFromArxiuDocument(arxiuDocument),
					documentHelper.getFirmaDetachedFromArxiuDocument(arxiuDocument),
					null, 
					false);
			return arxiuFirmes.get(0).getDetalls();
		}
		return null;
	}

	@Transactional(readOnly = true)
	@Override
	public FitxerDto descarregarContingutOriginal(Long entitatId, Long id, Long tascaId) {
		
		logger.debug("Descarregant contingut original del document (entitatId=" + entitatId + ", id=" + id+")");
		
		try {
			DocumentEntity document = null;
			if (tascaId == null) {
				document = documentHelper.comprovarDocumentDinsExpedientAccessible(
						entitatId,
						id,
						true,
						false);
			} else {
				document = (DocumentEntity) contingutHelper.comprovarContingutPertanyTascaAccesible(
						tascaId,
						id);
			}
			
			return documentHelper.getContingutOriginal(document);
			
		} catch (Exception e) {

			if (ExceptionHelper.isExceptionOrCauseInstanceOf(e, ArxiuNotFoundException.class)) {
				throw new ArxiuNotFoundDocumentException();
			} else {
				throw e;
			}
		}
	}
	
	@Transactional(readOnly = true)
	@Override
	public FitxerDto descarregar(
			Long entitatId,
			Long id,
			String versio, 
			Long tascaId) {
		logger.debug("Descarregant contingut del document ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ", "
				+ "versio=" + versio + ")");
		
		try {
			DocumentEntity document = null;
			if (tascaId == null) {
				document = documentHelper.comprovarDocumentDinsExpedientAccessible(
						entitatId,
						id,
						true,
						false);
			} else {
				document = (DocumentEntity) contingutHelper.comprovarContingutPertanyTascaAccesible(
						tascaId,
						id);
			}
			
			return documentHelper.getFitxerAssociat(
					document,
					versio);
			
		} catch (Exception e) {

			if (ExceptionHelper.isExceptionOrCauseInstanceOf(e, ArxiuNotFoundException.class)) {
				throw new ArxiuNotFoundDocumentException();
			} else {
				throw e;
			}
		}
	}

	@Transactional(readOnly = true)
	@Override
	public FitxerDto descarregarImprimible(
			Long entitatId,
			Long id,
			String versio) {
		logger.debug("Descarregant versió imprimible del document ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ", "
				+ "versio=" + versio + ")");
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
				entitatId,
				id,
				true,
				false);
		return pluginHelper.arxiuDocumentVersioImprimible(
				document);
	}

	@Transactional
	@Override
	public Exception pinbalNovaConsulta(
			Long entitatId,
			Long pareId,
			Long metaDocumentId,
			PinbalConsultaDto consulta, 
			String rolActual) {
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(pareId));
		ContingutEntity pare = contingutHelper.comprovarContingutDinsExpedientModificable(
				entitatId,
				pareId,
				false,
				false,
				false,
				false, 
				false, 
				true, 
				rolActual);
		ExpedientEntity expedient = pare.getExpedientPare();
		MetaDocumentEntity metaDocument = null;
		if (metaDocumentId != null) {
			metaDocument = entityComprovarHelper.comprovarMetaDocument(
					pare.getEntitat(),
					expedient.getMetaExpedient(),
					metaDocumentId,
					true,
					true);
		} else {
			throw new ValidationException(
					"<creacio>",
					DocumentEntity.class,
					"No es pot fer una petició PINBAL sense un meta-document associat");
		}
		if (!metaDocument.isPinbalActiu()) {
			throw new ValidationException(
					"<creacio>",
					DocumentEntity.class,
					"No es pot fer una petició PINBAL sense un meta-document amb la integració PINBAL activa");
		}
		InteressatEntity interessat = interessatRepository.findByExpedientAndId(expedient, consulta.getInteressatId());
		if (interessat == null) {
			throw new NotFoundException(consulta.getInteressatId(), InteressatEntity.class);
		} else if (interessat instanceof InteressatAdministracioEntity) {
			throw new ValidationException(
					"<creacio>",
					DocumentEntity.class,
					"S'ha especificat un interessat que no és una persona física o juridica");
		}

		ConsultaPinbalEntity.ConsultaPinbalEntityBuilder consultaPinbalBuilder = ConsultaPinbalEntity.builder();
		try {
			String idPeticion = null;
			if (metaDocument.getPinbalServei() == MetaDocumentPinbalServeiEnumDto.SVDDGPCIWS02) {
				idPeticion = pinbalHelper.novaPeticioSvddgpciws02(
						expedient,
						metaDocument,
						interessat,
						consulta.getFinalitat(),
						consulta.getConsentiment());
			} else if (metaDocument.getPinbalServei() == MetaDocumentPinbalServeiEnumDto.SVDDGPVIWS02) {
				idPeticion = pinbalHelper.novaPeticioSvddgpviws02(
						expedient,
						metaDocument,
						interessat,
						consulta.getFinalitat(),
						consulta.getConsentiment());
			} else if (metaDocument.getPinbalServei() == MetaDocumentPinbalServeiEnumDto.SVDCCAACPASWS01) {
				idPeticion = pinbalHelper.novaPeticioSvdccaacpasws01(
						expedient,
						metaDocument,
						interessat,
						consulta.getFinalitat(),
						consulta.getConsentiment(),
						consulta.getComunitatAutonomaCodi(),
						consulta.getProvinciaCodi());
			} else if (metaDocument.getPinbalServei() == MetaDocumentPinbalServeiEnumDto.SVDSCDDWS01) {
				idPeticion = pinbalHelper.novaPeticioSvdscddws01(
						expedient,
						metaDocument,
						interessat,
						consulta.getFinalitat(),
						consulta.getConsentiment(),
						consulta.getComunitatAutonomaCodi(),
						consulta.getProvinciaCodi(),
						consulta.getDataConsulta(),
						consulta.getDataNaixement(),
						consulta.getConsentimentTipusDiscapacitat());
			} else if (metaDocument.getPinbalServei() == MetaDocumentPinbalServeiEnumDto.SCDCPAJU) {
				idPeticion = pinbalHelper.novaPeticioScdcpaju(
						expedient,
						metaDocument,
						interessat,
						consulta.getFinalitat(),
						consulta.getConsentiment(),
						consulta.getProvinciaCodi(),
						consulta.getMunicipiCodi());
			} else if (metaDocument.getPinbalServei() == MetaDocumentPinbalServeiEnumDto.SVDSCTFNWS01) {
				idPeticion = pinbalHelper.novaPeticioSvdsctfnws01(
						expedient,
						metaDocument,
						interessat,
						consulta);
			} else if (metaDocument.getPinbalServei() == MetaDocumentPinbalServeiEnumDto.SVDCCAACPCWS01) {
				idPeticion = pinbalHelper.novaPeticioSvdccaacpcws01(
						expedient,
						metaDocument,
						interessat,
						consulta);			
			} else if (metaDocument.getPinbalServei() == MetaDocumentPinbalServeiEnumDto.Q2827003ATGSS001) {
				idPeticion = pinbalHelper.novaPeticioQ2827003atgss001(
						expedient,
						metaDocument,
						interessat,
						consulta);		
			} else if (metaDocument.getPinbalServei() == MetaDocumentPinbalServeiEnumDto.SVDDELSEXWS01) {
				idPeticion = pinbalHelper.novaPeticioSvddelsexws01(
						expedient,
						metaDocument,
						interessat,
						consulta);	
			} else if (metaDocument.getPinbalServei() == MetaDocumentPinbalServeiEnumDto.SCDHPAJU) {
				idPeticion = pinbalHelper.novaPeticioScdhpaju(
						expedient,
						metaDocument,
						interessat,
						consulta);		
			} else if (metaDocument.getPinbalServei() == MetaDocumentPinbalServeiEnumDto.NIVRENTI) {
				idPeticion = pinbalHelper.novaPeticioNivrenti(
						expedient,
						metaDocument,
						interessat,
						consulta);			
			} else if (metaDocument.getPinbalServei() == MetaDocumentPinbalServeiEnumDto.ECOT103) {
				idPeticion = pinbalHelper.novaPeticioEcot103(
						expedient,
						metaDocument,
						interessat,
						consulta);		
			} else if (metaDocument.getPinbalServei() == MetaDocumentPinbalServeiEnumDto.SVDDGPRESIDENCIALEGALDOCWS01) {
				idPeticion = pinbalHelper.novaPeticioSvddgpresidencialegaldocws01(
						expedient,
						metaDocument,
						interessat,
						consulta);
			} else if (metaDocument.getPinbalServei() == MetaDocumentPinbalServeiEnumDto.SVDRRCCNACIMIENTOWS01) {
				idPeticion = pinbalHelper.novaPeticioSvdrrccnacimientows01(
						expedient,
						metaDocument,
						interessat,
						consulta);		
			} else if (metaDocument.getPinbalServei() == MetaDocumentPinbalServeiEnumDto.SVDRRCCMATRIMONIOWS01) {
				idPeticion = pinbalHelper.novaPeticioSvdrrccmatrimoniows01(
						expedient,
						metaDocument,
						interessat,
						consulta);		
			} else if (metaDocument.getPinbalServei() == MetaDocumentPinbalServeiEnumDto.SVDRRCCDEFUNCIONWS01) {
				idPeticion = pinbalHelper.novaPeticioSvdrrccdefuncionws01(
						expedient,
						metaDocument,
						interessat,
						consulta);					
			} else if (metaDocument.getPinbalServei() == MetaDocumentPinbalServeiEnumDto.SVDBECAWS01) {
				idPeticion = pinbalHelper.novaPeticioSvdbecaws01(
						expedient,
						metaDocument,
						interessat,
						consulta);
			} else {
				throw new ValidationException(
						"<creacio>",
						DocumentEntity.class,
						"S'ha especificat un servei PINBAL no suportat: " + metaDocument.getPinbalServei());
			}
			
			FitxerDto justificant = pinbalHelper.getJustificante(idPeticion);
			DocumentDto document = new DocumentDto();
			document.setDocumentTipus(DocumentTipusEnumDto.DIGITAL);
			
			if (interessat instanceof InteressatPersonaFisicaEntity) {
				InteressatPersonaFisicaEntity interessatPf = (InteressatPersonaFisicaEntity)interessat;
				StringBuilder nomSencer = new StringBuilder(interessatPf.getNom());
				if (interessatPf.getLlinatge1() != null) {
					nomSencer.append(" ");
					nomSencer.append(interessatPf.getLlinatge1().trim());
				}
				if (interessatPf.getLlinatge2() != null) {
					nomSencer.append(" ");
					nomSencer.append(interessatPf.getLlinatge2().trim());
				}
				document.setNom(idPeticion + " - " + nomSencer.toString());
			} else {
				document.setNom(idPeticion + " - " + interessat.getIdentificador());
			}
			
			
			document.setData(new Date());
			document.setNtiOrgano(expedient.getNtiOrgano());
			document.setNtiOrigen(NtiOrigenEnumDto.O1);
			document.setNtiEstadoElaboracion(DocumentNtiEstadoElaboracionEnumDto.EE01);
			document.setNtiTipoDocumental("TD99");
			document.setFitxerNom(justificant.getNom());
			document.setFitxerContentType(justificant.getContentType());
			document.setFitxerContingut(justificant.getContingut());
			document.setFitxerTamany(justificant.getContingut() != null ? new Long(justificant.getContingut().length) : null);
			document.setAmbFirma(true);
			document.setFirmaSeparada(false);
			document.setPinbalIdpeticion(idPeticion);

			DocumentDto docum = documentHelper.crearDocument(
					document,
					pare,
					expedient,
					metaDocument,
					true);
			
			DocumentEntity doc = documentRepository.findOne(docum.getId());
			
			consultaPinbalBuilder
			.entitat(entitatRepository.findOne(entitatId))
			.servei(metaDocument.getPinbalServei())
			.estat(ConsultaPinbalEstatEnumDto.TRAMITADA)
			.pinbalIdpeticion(idPeticion)
			.expedient(expedient)
			.metaExpedient(expedient.getMetaExpedient())
			.document(doc);
			
			consultaPinbalRepository.save(consultaPinbalBuilder.build());
			
			return null;
			
		} catch (Exception e) {
			
			consultaPinbalBuilder
			.entitat(entitatRepository.findOne(entitatId))
			.servei(metaDocument.getPinbalServei())
			.estat(ConsultaPinbalEstatEnumDto.ERROR)
			.error(Utils.abbreviate(Utils.getRootMsg(e), 4000))
			.expedient(expedient)
			.metaExpedient(expedient.getMetaExpedient());
			consultaPinbalRepository.save(consultaPinbalBuilder.build());
			
			return e;
		}
	}
	
	
	

	@Transactional
	@Override
	public void portafirmesEnviar(
			Long entitatId,
			Long documentId,
			String assumpte,
			PortafirmesPrioritatEnumDto prioritat,
			String portafirmesFluxId,
			String[] portafirmesResponsables,
			MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSeqTipus,
			MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus,
			Long[] annexosIds,
			String transaccioId, 
			String rolActual, 
			Long tascaId,
			boolean avisFirmaParcial,
			boolean firmaParcial) {
		
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(documentId));
		logger.debug("Enviant document a portafirmes (" +
				"entitatId=" + entitatId + ", " +
				"documentId=" + documentId + ", " +
				"assumpte=" + assumpte + ", " +
				"prioritat=" + prioritat + ")");
		DocumentEntity document = null;
		
		if (tascaId == null) {
			document = documentHelper.comprovarDocument(
					entitatId,
					documentId,
					false,
					true,
					false,
					false, 
					false, 
					rolActual);
		} else {
			document = (DocumentEntity) contingutHelper.comprovarContingutPertanyTascaAccesible(
					tascaId,
					documentId);
		}
		
		
		
		try {
			firmaPortafirmesHelper.portafirmesEnviar(
					entitatId,
					document,
					assumpte,
					prioritat,
					null,
					portafirmesFluxId,
					portafirmesResponsables,
					portafirmesSeqTipus,
					portafirmesFluxTipus,
					annexosIds,
					transaccioId,
					avisFirmaParcial,
					firmaParcial);
		} catch (Exception e) {
			Throwable wsValidationException = ExceptionHelper.findThrowableInstance(e, WsValidationException.class, 6);
			if (wsValidationException != null && (wsValidationException.getMessage().contains("Destinatari ID") || wsValidationException.getMessage().contains("ha trobat cap usuari"))
				|| e instanceof SistemaExternException && e.getCause().getMessage().contains("error= No existeix cap usuari") ) {
				throw new ResponsableNoValidPortafirmesException();
			} else {
				throw e;
			}
		}
	}

	@Transactional
	@Override
	public void portafirmesCancelar(
			Long entitatId,
			Long documentId, 
			String rolActual, 
			Long tascaId) {
		logger.debug("Enviant document a portafirmes (" +
				"entitatId=" + entitatId + ", " +
				"documentId=" + documentId + ")");
		DocumentEntity document = null;
		
		if (tascaId == null) {
			document = documentHelper.comprovarDocument(
					entitatId,
					documentId,
					false,
					true,
					false,
					false, 
					false, 
					rolActual);
		} else {
			document = (DocumentEntity) contingutHelper.comprovarContingutPertanyTascaAccesible(
					tascaId,
					documentId);
		}
		

		firmaPortafirmesHelper.portafirmesCancelar(
				entitatId,
				document, 
				rolActual);
	}
	
	@Transactional
	@Override
	public List<PortafirmesBlockDto> recuperarBlocksFirmaEnviament(
			Long entitatId,
			Long documentId, 
			Long enviamentId) {
		logger.debug("Enviant document a portafirmes (" +
				"entitatId=" + entitatId + ", " +
				"documentId=" + documentId + ")");
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
				entitatId,
				documentId,
				true,
				false);

		return firmaPortafirmesHelper.recuperarBlocksFirmaEnviament(
				entitatId,
				document, 
				enviamentId);
	}
	
	@Transactional
	@Override
	public Exception portafirmesCallback(
			long portafirmesId,
			PortafirmesCallbackEstatEnumDto callbackEstat,
			String motiuRebuig,
			String administrationId,
			String name) {
		logger.debug("Processant petició del callback ("
				+ "portafirmesId=" + portafirmesId + ", "
				+ "callbackEstat=" + callbackEstat + ")");
		return firmaPortafirmesHelper.portafirmesCallback(portafirmesId, callbackEstat, motiuRebuig, administrationId, name);
	}
	
	@Transactional
	@Override
	public void portafirmesCallbackIntegracioOk(
			String descripcio,
			Map<String, String> parametres) {

		integracioHelper.addAccioOk(IntegracioHelper.INTCODI_CALLBACK, descripcio, null, parametres, IntegracioAccioTipusEnumDto.RECEPCIO, 0);
	}
	
	@Transactional
	@Override
	public void portafirmesCallbackIntegracioError(
			String descripcio,
			Map<String, String> parametres,
			String errorDescripcio,
			Throwable throwable) {

		integracioHelper.addAccioError(
				IntegracioHelper.INTCODI_CALLBACK,
				descripcio,
				null,
				parametres,
				IntegracioAccioTipusEnumDto.RECEPCIO,
				0,
				errorDescripcio,
				throwable);
	}
	
	@Transactional
	@Override
	public Exception portafirmesReintentar(
			Long entitatId,
			Long documentId, 
			String rolActual, 
			Long tascaId) {
		logger.debug("Reintentant processament d'enviament a portafirmes amb error ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + documentId
				+ "rolActual=" + rolActual +")");
		
		DocumentEntity document = null;
		
		if (tascaId == null) {
			document = documentHelper.comprovarDocument(
					entitatId,
					documentId,
					false,
					true,
					false,
					false, 
					false, 
					rolActual);
		} else {
			document = (DocumentEntity) contingutHelper.comprovarContingutPertanyTascaAccesible(
					tascaId,
					documentId);
		}
		
		
		
		
		return firmaPortafirmesHelper.portafirmesReintentar(
				entitatId,
				document);
	}
	

	
	

	@Transactional(readOnly = true)
	@Override
	public DocumentPortafirmesDto portafirmesInfo(
			Long entitatId,
			Long id, 
			Long enviamentId) {
		logger.debug("Obtenint informació del darrer enviament a portafirmes ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
				entitatId,
				id,
				true,
				false);
		
		organGestorHelper.actualitzarOrganCodi(organGestorHelper.getOrganCodiFromContingutId(document.getId()));
		
		DocumentPortafirmesDto docPortafir = null;
		
		if (enviamentId != null) {
			docPortafir = firmaPortafirmesHelper.portafirmesInfo(enviamentId);
		} else {
			docPortafir = firmaPortafirmesHelper.portafirmesInfo(entitatId, document);
		}

		List<PortafirmesDocumentTipusDto> list = pluginHelper.portafirmesFindDocumentTipus();
		for (PortafirmesDocumentTipusDto doctipus : list) {
			if (Long.toString(doctipus.getId()).equals(docPortafir.getDocumentTipus())) {
				docPortafir.setDocumentTipus(doctipus.getNom());
				break;
			}
		}
		
		return docPortafir;
	}
	
	
	

	
	

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<DocumentDto> findDocumentsPerCustodiarMassiu(
			Long entitatId,
			String rolActual,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams) throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false, 
				false, 
				true, false);
		MetaExpedientEntity metaExpedient = null;
		if (filtre.getMetaExpedientId() != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, filtre.getMetaExpedientId());
		}
		MetaDocumentEntity metaDocument = null;
		if (filtre.getMetaDocumentId() != null) {
			metaDocument = entityComprovarHelper.comprovarMetaDocument(
					filtre.getMetaDocumentId());
		}
		

		boolean nomesAgafats = true;
		if (rolActual.equals("IPA_ADMIN") || rolActual.equals("IPA_ORGAN_ADMIN")) {
			nomesAgafats = false;

		} 
		
		List<MetaExpedientEntity> metaExpedientsPermesos = metaExpedientHelper.findPermesosAccioMassiva(entitatId, rolActual);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!metaExpedientsPermesos.isEmpty()) {
			Date dataInici = DateHelper.toDateInicialDia(filtre.getDataInici());
			Date dataFi = DateHelper.toDateFinalDia(filtre.getDataFi());
			Page<DocumentEntity> paginaDocuments = documentRepository.findDocumentsPerCustodiarMassiu(
					entitat,
					metaExpedientsPermesos, 
					nomesAgafats,
					auth.getName(),
					metaExpedient == null,
					metaExpedient,
					filtre.getExpedientNom() == null,
					filtre.getExpedientNom() != null ? filtre.getExpedientNom().trim() : "",
					metaDocument == null,
					metaDocument,
					filtre.getNom() == null,
					filtre.getNom() != null ? filtre.getNom().trim() : "",
					dataInici == null,
					dataInici,
					dataFi == null,
					dataFi,
					paginacioHelper.toSpringDataPageable(paginacioParams));
			return paginacioHelper.toPaginaDto(
					paginaDocuments,
					DocumentDto.class,
					new Converter<DocumentEntity, DocumentDto>() {
						@Override
						public DocumentDto convert(DocumentEntity source) {
							DocumentDto dto = (DocumentDto)contingutHelper.toContingutDto(
									source,
									true,
									true);
							return dto;
						}
					});
		} else {
			return paginacioHelper.getPaginaDtoBuida(
					DocumentDto.class);
		}
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<Long> findDocumentsIdsPerCustodiarMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre, 
			String rolActual) throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false, 
				false, false);
		boolean checkPerMassiuAdmin = false;
		if (rolActual.equals("IPA_ADMIN") || rolActual.equals("IPA_ORGAN_ADMIN")) {
			checkPerMassiuAdmin = true;
		} 

		
		MetaExpedientEntity metaExpedient = null;
		if (filtre.getMetaExpedientId() != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, filtre.getMetaExpedientId());
		}
		ExpedientEntity expedient = null;
		if (filtre.getExpedientId() != null) {
			expedient = entityComprovarHelper.comprovarExpedient(
					filtre.getExpedientId(),
					false,
					false,
					false,
					false,
					false,
					null);
		}
		MetaDocumentEntity metaDocument = null;
		if (filtre.getMetaDocumentId() != null) {
			metaDocument = entityComprovarHelper.comprovarMetaDocument(
					filtre.getMetaDocumentId());
		}
		List<MetaExpedientEntity> metaExpedientsPermesos = metaExpedientHelper.findPermesosAccioMassiva(entitatId, rolActual);
		

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (!metaExpedientsPermesos.isEmpty()) {
			Date dataInici = DateHelper.toDateInicialDia(filtre.getDataInici());
			Date dataFi = DateHelper.toDateFinalDia(filtre.getDataFi());
			List<Long> documentsIds = documentRepository.findDocumentsIdsPerCustodiarMassiu(
					entitat,
					metaExpedientsPermesos, 
					!checkPerMassiuAdmin,
					auth.getName(),
					metaExpedient == null,
					metaExpedient,
					expedient == null,
					expedient,
					metaDocument == null,
					metaDocument,
					filtre.getNom() == null,
					filtre.getNom() != null ? filtre.getNom().trim() : "",
					dataInici == null,
					dataInici,
					dataFi == null,
					dataFi);
			return documentsIds;
		} else {
			return new ArrayList<>();
		}
	}
	
	@Transactional
	@Override
	public void viaFirmaReintentar(
			Long entitatId,
			Long id) {
		logger.debug("Reintentant processament d'enviament a viaFirma amb error ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		DocumentEntity document = documentHelper.comprovarDocument(
				entitatId,
				id,
				false,
				true,
				false,
				false, false, null);
		List<DocumentViaFirmaEntity> enviamentsPendents = documentViaFirmaRepository.findByDocumentAndEstatInAndErrorOrderByCreatedDateDesc(
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
		DocumentViaFirmaEntity documentPortafirmes = enviamentsPendents.get(0);
		firmaViaFirmaHelper.viaFirmaReintentar(documentPortafirmes);
	}

	@Transactional
	@Override
	public void viaFirmaEnviar(
			Long entitatId, 
			Long documentId, 
			ViaFirmaEnviarDto viaFirmaEnviarDto,
			UsuariDto usuariActual)
			throws NotFoundException, IllegalStateException, SistemaExternException {
		logger.debug("Enviant document a viaFirma (" +
				"entitatId=" + entitatId + ", " +
				"id=" + documentId + ")");
		String contrasenyaUsuariViaFirma;
		try {
			UsuariEntity usuari = usuariRepository.findByCodi(usuariActual.getCodi());
			
			DocumentEntity document = documentHelper.comprovarDocument(
					entitatId,
					documentId,
					false,
					true,
					false,
					false, 
					false, 
					null);
			if (!DocumentTipusEnumDto.DIGITAL.equals(document.getDocumentTipus())) {
				throw new ValidationException(
						document.getId(),
						DocumentEntity.class,
						"El document a enviar a viaFirma no és del tipus " + DocumentTipusEnumDto.DIGITAL);
			}
			if (!cacheHelper.findErrorsValidacioPerNode(document).isEmpty()) {
				throw new ValidationException(
						document.getId(),
						DocumentEntity.class,
						"El document a enviar a viaFirma te alertes de validació");
			}
			if (DocumentEstatEnumDto.FIRMAT.equals(document.getEstat()) ||
					DocumentEstatEnumDto.CUSTODIAT.equals(document.getEstat())) {
				throw new ValidationException(
						document.getId(),
						DocumentEntity.class,
						"No es poden enviar a viaFirma documents firmats o custodiats");
			}
			//Recuperar contrasenya usuari
			for (ViaFirmaUsuariEntity viaFirmaDispositiuDto : usuari.getViaFirmaUsuaris()) {
				if (viaFirmaDispositiuDto.getCodi().equals(viaFirmaEnviarDto.getCodiUsuariViaFirma())) {
					contrasenyaUsuariViaFirma = viaFirmaDispositiuDto.getContrasenya();
					viaFirmaEnviarDto.setContrasenyaUsuariViaFirma(contrasenyaUsuariViaFirma);
				}
			}
			DispositiuEnviamentEntity dispositiuEnviament = null;
			if (viaFirmaEnviarDto.getViaFirmaDispositiu() != null) {
				//Guardar dispositiu associat a l'enviament
				dispositiuEnviament = DispositiuEnviamentEntity.getBuilder(
						viaFirmaEnviarDto.getViaFirmaDispositiu().getCodi(), 
						viaFirmaEnviarDto.getViaFirmaDispositiu().getCodiAplicacio(), 
						viaFirmaEnviarDto.getViaFirmaDispositiu().getDescripcio(),
						viaFirmaEnviarDto.getViaFirmaDispositiu().getLocal(),
						viaFirmaEnviarDto.getViaFirmaDispositiu().getEstat(),
						viaFirmaEnviarDto.getViaFirmaDispositiu().getToken(), 
						viaFirmaEnviarDto.getViaFirmaDispositiu().getIdentificador(),
						viaFirmaEnviarDto.getViaFirmaDispositiu().getTipus(),
						viaFirmaEnviarDto.getViaFirmaDispositiu().getEmailUsuari(),
						viaFirmaEnviarDto.getViaFirmaDispositiu().getCodiUsuari(),
						viaFirmaEnviarDto.getViaFirmaDispositiu().getIdentificadorNacional()).build();
				
				dispositiuEnviamentRepository.save(dispositiuEnviament);
			}
			//Guardar document a enviar
			DocumentViaFirmaEntity documentViaFirma = DocumentViaFirmaEntity.getBuilder(
					DocumentEnviamentEstatEnumDto.PENDENT,
					viaFirmaEnviarDto.getCodiUsuariViaFirma(),
					viaFirmaEnviarDto.getContrasenyaUsuariViaFirma(),
					viaFirmaEnviarDto.getTitol(),
					viaFirmaEnviarDto.getDescripcio(),
					dispositiuEnviament != null ? dispositiuEnviament.getCodi() : null,
					viaFirmaEnviarDto.getSignantNif(),
					viaFirmaEnviarDto.getSignantNom(),
					viaFirmaEnviarDto.getObservacions(),
					dispositiuEnviament,
					document.getMetaDocument().isBiometricaLectura(),
					document.getExpedient(),
					document,
					viaFirmaEnviarDto.isFirmaParcial(),
					viaFirmaEnviarDto.isValidateCodeEnabled(),
					viaFirmaEnviarDto.getValidateCode(),
					viaFirmaEnviarDto.isRebreCorreu()).build();
			
			firmaViaFirmaHelper.viaFirmaEnviar(documentViaFirma);
		
		} catch (Exception ex) {
			logger.error(
					"Error a l'hora d'enviar el document a viaFirma (" +
					"documentId=" + documentId + ")",
					ex);
			throw new RuntimeException(
					"Error a l'hora d'enviar el document a viaFirma: " + ex.getMessage(),
					ex);
		}
	}
	
	@Transactional
	@Override
	public void viaFirmaCancelar(
			Long entitatId,
			Long id) {
		logger.debug("Enviant document a viaFirma (" +
				"entitatId=" + entitatId + ", " +
				"id=" + id + ")");
		DocumentEntity document = documentHelper.comprovarDocument(
				entitatId,
				id,
				false,
				true,
				false,
				false, 
				false, 
				null);
		List<DocumentViaFirmaEntity> enviamentsPendents = documentViaFirmaRepository.findByDocumentAndEstatInOrderByCreatedDateDesc(
				document,
				new DocumentEnviamentEstatEnumDto[] {DocumentEnviamentEstatEnumDto.ENVIAT});
		if (enviamentsPendents.size() == 0) {
			throw new ValidationException(
					document.getId(),
					DocumentEntity.class,
					"Aquest document no te enviaments a viaFirma pendents");
		}
		DocumentViaFirmaEntity documentViaFirma = enviamentsPendents.get(0);
		firmaViaFirmaHelper.viaFirmaCancelar(documentViaFirma);
	}
	
	@Transactional(readOnly = true)
	@Override
	public DocumentViaFirmaDto viaFirmaInfo(
			Long entitatId,
			Long id) {
		logger.debug("Obtenint informació del darrer enviament a viaFirma ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
				entitatId,
				id,
				true,
				false);
		List<DocumentViaFirmaEntity> enviamentsPendents = documentViaFirmaRepository.findByDocumentAndEstatInOrderByCreatedDateDesc(
				document,
				new DocumentEnviamentEstatEnumDto[] {
						DocumentEnviamentEstatEnumDto.PENDENT,
						DocumentEnviamentEstatEnumDto.ENVIAT
				});
		if (enviamentsPendents.size() == 0) {
			throw new ValidationException(
					document.getId(),
					DocumentEntity.class,
					"Aquest document no te enviaments a viaFirma");
		}
		return conversioTipusHelper.convertir(
				enviamentsPendents.get(0),
				DocumentViaFirmaDto.class);
	}
	
	@Transactional
	@Override
	public List<ViaFirmaDispositiuDto> viaFirmaDispositius(
			String viaFirmaUsuari,
			UsuariDto usuariActual)
			throws NotFoundException, IllegalStateException, SistemaExternException {
		logger.debug("Obtenint ( obtenint els dispositius de viaFirma de l'uusuari: " + usuariActual.getCodi() + ")");
		List<ViaFirmaDispositiuDto> viaFirmaDispositiusDto = new ArrayList<ViaFirmaDispositiuDto>();
		String contasenya = null;
		try {
			//Recuperar usuaris viaFirma usuari actual
			UsuariEntity usuari = usuariRepository.findByCodi(usuariActual.getCodi());

			for (ViaFirmaUsuariEntity viaFirmaDispositiuDto : usuari.getViaFirmaUsuaris()) {
				if (viaFirmaDispositiuDto.getCodi().equals(viaFirmaUsuari)) {
					contasenya = viaFirmaDispositiuDto.getContrasenya();
				}
			}
			viaFirmaDispositiusDto = pluginHelper.getDeviceUser(
					viaFirmaUsuari, 
					contasenya);
		} catch (Exception ex) {
			logger.error(
					"Error a l'hora de recuperar els usuaris de viaFirma (" +
					"usuariCodi=" + usuariActual.getCodi() + ")",
					ex);
			throw new RuntimeException(
					"Error a l'hora de recuperar els usuaris de viaFirma (" +
					"usuariCodi=" + usuariActual.getCodi() + ")",
					ex);
		}
		return viaFirmaDispositiusDto;
	}
	
	@Transactional
	@Override
	public List<ViaFirmaUsuariDto> viaFirmaUsuaris(UsuariDto usuariActual)
			throws NotFoundException, IllegalStateException, SistemaExternException {
		logger.debug("Obtenint ( obtenint els usuaris de viaFirma de l'uusuari: " + usuariActual.getCodi() + ")");
		List<ViaFirmaUsuariDto> viaFirmaUsuaris = new ArrayList<ViaFirmaUsuariDto>();
		try {
			//Recuperar usuaris viaFirma usuari actual
			UsuariEntity usuari = usuariRepository.findByCodi(usuariActual.getCodi());
			Set<ViaFirmaUsuariEntity> viaFirmaUsuarisEntity = usuari.getViaFirmaUsuaris();
			
			Set<ViaFirmaUsuariDto> viaFirmaUsuarisDto = conversioTipusHelper.convertirSet(
					viaFirmaUsuarisEntity, 
					ViaFirmaUsuariDto.class);
			
			viaFirmaUsuaris = new ArrayList<ViaFirmaUsuariDto>(viaFirmaUsuarisDto);
		} catch (Exception ex) {
			logger.error(
					"Error a l'hora de recuperar els usuaris de viaFirma (" +
					"usuariCodi=" + usuariActual.getCodi() + ")",
					ex);
			throw new RuntimeException(
					"Error a l'hora de recuperar els usuaris de viaFirma (" +
					"usuariCodi=" + usuariActual.getCodi() + ")",
					ex);
		}
		return viaFirmaUsuaris;
	}
	
	@Transactional
	@Override
	public Exception processarRespostaViaFirma(String messageJson) {
		Exception exception = null;
		try {
			ViaFirmaRespostaDto response = viaFirmaHelper.processarRespostaViaFirma(messageJson);
			
			exception = viaFirmaCallback(
					response.getMessageCode(), 
					response.getStatus());
		} catch (Exception ex) {
			throw new RuntimeException(
					"Error a l'hora de cridar el callback dins del servidor",
					ex);
		}
		return exception;
	}
	
	@Transactional
	@Override
	public Exception viaFirmaCallback(
			String messageCode, 
			ViaFirmaCallbackEstatEnumDto callbackEstat) throws NotFoundException {
		logger.debug("Processant petició del callback ("
				+ "messageCode=" + messageCode + ", "
				+ "callbackEstat=" + callbackEstat + ")");
		DocumentViaFirmaEntity documentViaFirma = documentViaFirmaRepository.findByMessageCode(messageCode);
		if (documentViaFirma == null) {
			return new NotFoundException(
					"(messageCode=" + messageCode + ")",
					DocumentViaFirmaEntity.class);
		}
		return firmaViaFirmaHelper.viaFirmaCallback(documentViaFirma, callbackEstat);
	}
	
	@Transactional
	@Override
	public FitxerDto convertirPdfPerFirmaClient(
			Long entitatId,
			Long id) {
		return documentHelper.convertirPdfPerFirmaClient(
				entitatId,
				id);
	}
	
	@Transactional
	@Override
	//Obtenir el fitxer en PDF, sense estampar ni operacions extres, nomes per previsualitzat al navegador.
	//Si el fitxer ja es un PDF, es retorna directament.
	public FitxerDto getFitxerPDF(
			Long entitatId,
			Long id) {
		return documentHelper.getFitxerPDF(
				entitatId,
				id);
	}

	@Transactional
	@Override
	public String generarIdentificadorFirmaClient(
			Long entitatId,
			Long id) {
		logger.debug("Generar identificador firma al navegador ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		documentHelper.comprovarDocumentDinsExpedientAccessible(
				entitatId,
				id,
				true,
				false);
		try {
			return firmaAppletHelper.firmaClientXifrar(
					firmaAppletHelper.obtainInstanceObjecteFirmaApplet( 
							new Long(System.currentTimeMillis()),
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

	@Transactional
	@Override
	public void processarFirmaClient(
			Long entitatId,
			Long documentId,
			String arxiuNom, 
			byte[] arxiuContingut, 
			String rolActual, 
			Long tascaId) {
		
		String identificador = documentHelper.generarIdentificadorFirmaClient(
				entitatId,
				documentId);
		
		logger.debug("Custodiar identificador firma applet ("
				+ "identificador=" + identificador + ")");
		ObjecteFirmaApplet objecte = null;
		try {
			objecte = firmaAppletHelper.firmaAppletDesxifrar(
					identificador,
					DocumentFirmaAppletHelper.CLAU_SECRETA);
		} catch (Exception ex) {
			throw new RuntimeException(
					"Error al desxifrar l'identificador per la firma via applet (" +
					"identificador=" + identificador + ")",
					ex);
		}
		if (objecte != null) {
			
			DocumentEntity document = null;
			
			if (tascaId == null) {
				document = documentHelper.comprovarDocument(
						objecte.getEntitatId(),
						objecte.getDocumentId(),
						false,
						true,
						false,
						false, 
						false, 
						rolActual);

			} else {
				document = contingutHelper.comprovarDocumentPerTasca(
						tascaId,
						documentId);
			}

			firmaAppletHelper.processarFirmaClient(
					identificador,
					changeExtensioToPdf(arxiuNom),
					arxiuContingut,
					document);
		} else {
			logger.error(
					"No s'han trobat les dades del document amb identificador applet (" +
					"identificador=" + identificador + ")");
			throw new RuntimeException(
					"No s'han trobat les dades del document amb identificador applet (" +
					"identificador=" + identificador + ")");
		}
	}
	
	
	
	@Transactional(readOnly = true)
	@Override
	public DocumentDto findAmbId(
			Long documentId, 
			String rolActual, 
			PermissionEnumDto permission, 
			Long tascaId) {
		
		if (permission != null) {
			if (tascaId == null) {
				contingutHelper.checkIfPermitted(
						documentId,
						rolActual,
						permission);

			} else {
				contingutHelper.comprovarDocumentPerTasca(
						tascaId,
						documentId);
			}
		}
		
		ContingutEntity contingut = documentRepository.findOne(documentId);
		ContingutDto contingutDto = contingutHelper.toContingutDto(
				contingut,
				false,
				false);
		return (DocumentDto) contingutDto;
	}
	
	private String changeExtensioToPdf(String nom) {
		int indexPunt = nom.lastIndexOf(".");
		if (indexPunt != -1 && indexPunt < nom.length() - 1) {
			return nom.substring(0,indexPunt)+ ".pdf";
		} else {
			return nom;
		}
	}
	
	
	@Transactional
	@Override
	public void notificacioActualitzarEstat(
			String identificador, 
			String referencia) {
		logger.debug("Rebre callback de notib: identificador=" + identificador + ", referencia=" + referencia);
		DocumentEnviamentInteressatEntity documentEnviamentInteressatEntity = documentEnviamentInteressatRepository.findByIdentificadorIReferencia(
				identificador, referencia);
		try {
			if (documentEnviamentInteressatEntity == null) {
				logger.error("Callback de notib envia notificació que no existeix a la base de dades: identificador=" + identificador + ", referencia=" + referencia);
				// throw new NotFoundException(documentEnviamentInteressatEntity, DocumentEnviamentInteressatEntity.class);
			} else {
				documentNotificacioHelper.actualitzarEstat(documentEnviamentInteressatEntity);
			}
			
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de notificacions";
			logger.error(errorDescripcio, ex);
			throw new RuntimeException(ex);
		}
	}	
	
	@Transactional
	@Override
	public void notificacioActualitzarEstat(
			String identificador) {
		DocumentNotificacioEntity documentNotificacio = documentNotificacioRepository.findByNotificacioIdentificador(
				identificador);
		try {

			for (DocumentEnviamentInteressatEntity documentEnviamentInteressatEntity : documentNotificacio.getDocumentEnviamentInteressats()) {
				documentNotificacioHelper.actualitzarEstat(documentEnviamentInteressatEntity);
			}
			
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de notificacions";
			logger.error(errorDescripcio, ex);
			throw new RuntimeException(ex);
		}
	}
	
	@Transactional
	@Override
	public void notificacioActualitzarEstat(
			Long id) {
		DocumentNotificacioEntity documentNotificacio = documentNotificacioRepository.findOne(id);
		try {

			for (DocumentEnviamentInteressatEntity documentEnviamentInteressatEntity : documentNotificacio.getDocumentEnviamentInteressats()) {
				documentNotificacioHelper.actualitzarEstat(documentEnviamentInteressatEntity);
			}
			
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de notificacions";
			logger.error(errorDescripcio, ex);
			throw new RuntimeException(ex);
		}
	}
	
	
	
	
	
	@Transactional
	@Override
	public void actualitzarEstatADefinititu(Long documentId) {
		documentHelper.actualitzarEstatADefinititu(documentId);
	}
	
	
	@Transactional
	@Override
	public byte[] notificacioConsultarIDescarregarCertificacio(Long documentEnviamentInteressatId) {
		return documentNotificacioHelper.getCertificacio(documentEnviamentInteressatId);
	}
	
	
	@Override
	@Transactional
	public RespostaJustificantEnviamentNotibDto notificacioDescarregarJustificantEnviamentNotib(Long notificacioId) {
		
		DocumentNotificacioEntity documentNotificacioEntity = documentNotificacioRepository.findOne(notificacioId);

		RespostaJustificantEnviamentNotib resposta = pluginHelper.notificacioDescarregarJustificantEnviamentNotib(documentNotificacioEntity.getNotificacioIdentificador());
		return conversioTipusHelper.convertir(resposta, RespostaJustificantEnviamentNotibDto.class);
	}



	@Override
	@Transactional
	public void documentActualitzarEstat(
			Long entitatId, 
			Long documentId,
			DocumentEstatEnumDto nouEstat) {
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
				entitatId,
				documentId,
				false,
				true);
		documentHelper.actualitzarEstat(document, nouEstat);
	}
	
	
	@Override
	@Transactional(readOnly = true)
	public long countByMetaDocument(
			Long entitatId,
			Long metaDocumentId) {
		
		MetaDocumentEntity entity = entityComprovarHelper.comprovarMetaDocument(
				metaDocumentId);
		
		List<DocumentEntity> documents = documentRepository.findByMetaNode(entity);
		return documents != null ? documents.size() : 0;
	}
	
	
	private DocumentDto toDocumentDto(
			DocumentEntity document) {
		return (DocumentDto) contingutHelper.toContingutDto(
				document,
				false,
				false,
				false,
				true,
				true,
				false,
				null,
				false,
				null,
				false,
				0,
				null,
				null,
				true,
				true,
				false,
				false);
	}
	
	private boolean checkCarpetaUniqueContraint (String nom, ContingutEntity pare, Long entitatId) {
		EntitatEntity entitat = entitatId != null ? entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false, false) : null;
		return  contingutHelper.checkUniqueContraint(nom, pare, entitat, ContingutTipusEnumDto.DOCUMENT);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

	@Override
	public String recuperarUrlViewEstatFluxDeFirmes(long portafirmesId) throws SistemaExternException {
		logger.debug("Recuperant url visualització estat flux de firmes (" +
				"portafirmesId=" + portafirmesId +")");
		String idioma = aplicacioService.getUsuariActual().getIdioma();
		return pluginHelper.portafirmesRecuperarUrlEstatFluxFirmes(portafirmesId, idioma);
	}
	
}