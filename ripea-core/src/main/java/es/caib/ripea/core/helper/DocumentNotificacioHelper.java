package es.caib.ripea.core.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEnviamentInteressatDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentTipusGenericEnumDto;
import es.caib.ripea.core.api.dto.MunicipiDto;
import es.caib.ripea.core.api.dto.NotificacioInfoRegistreDto;
import es.caib.ripea.core.api.dto.PaisDto;
import es.caib.ripea.core.api.dto.ProvinciaDto;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.DadesExternesService;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentEnviamentInteressatEntity;
import es.caib.ripea.core.entity.DocumentNotificacioEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.InteressatEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.repository.DocumentEnviamentInteressatRepository;
import es.caib.ripea.core.repository.DocumentNotificacioRepository;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.MetaDocumentRepository;
import es.caib.ripea.core.service.DocumentServiceImpl;
import es.caib.ripea.plugin.notificacio.EnviamentReferencia;
import es.caib.ripea.plugin.notificacio.RespostaConsultaEstatEnviament;
import es.caib.ripea.plugin.notificacio.RespostaConsultaInfoRegistre;
import es.caib.ripea.plugin.notificacio.RespostaEnviar;

/**
 * Utilitat per gestionar l'enviament de notificacions dels documents d'expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DocumentNotificacioHelper {
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private DocumentNotificacioRepository documentNotificacioRepository;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private ContingutLogHelper contingutLogHelper;
	@Autowired
	private DocumentEnviamentInteressatRepository documentEnviamentInteressatRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private DadesExternesService dadesExternesService;
	@Autowired
	private MetaDocumentRepository metaDocumentRepository;
	@Autowired
	private EmailHelper emailHelper;
	@Autowired
	private DocumentHelper documentHelper;
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private ContingutHelper contingutHelper;
	@Autowired
	private ConfigHelper configHelper;

	public static Map<String, String> notificacionsWithError = new HashMap<String, String>();
	
	public void crear(
			DocumentNotificacioDto notificacioDto, 
			DocumentEntity documentEntity) {
//		List<InteressatEntity> interessats = validateInteressatsPerNotificacio(notificacioDto, expedientEntity);
		ExpedientEntity expedientEntity = validateExpedientPerNotificacio(documentEntity, 
				  notificacioDto.getTipus());
		notificacionsWithError = new HashMap<String, String>();
		for (Long interessatId : notificacioDto.getInteressatsIds()) {
			
			InteressatEntity interessat = entityComprovarHelper.comprovarInteressat(
					expedientEntity,
					interessatId);
			notificacioDto.setServeiTipusEnum(notificacioDto.getServeiTipusEnum());
			notificacioDto.setEntregaPostal(notificacioDto.isEntregaPostal());
			
			RespostaEnviar respostaEnviar = new RespostaEnviar();
//			if (!DocumentNotificacioTipusEnumDto.MANUAL.equals(notificacioDto.getTipus())) {
				respostaEnviar = pluginHelper.notificacioEnviar(
						notificacioDto,
						expedientEntity,
						documentEntity,
						interessat);
//			}
			
			DocumentNotificacioEntity notificacioEntity = DocumentNotificacioEntity.getBuilder(
					DocumentNotificacioEstatEnumDto.PENDENT,
					notificacioDto.getAssumpte(),
					notificacioDto.getTipus(),
					notificacioDto.getDataProgramada(),
					notificacioDto.getRetard(),
					notificacioDto.getDataCaducitat(), 
					expedientEntity,
					documentEntity,
					notificacioDto.getServeiTipusEnum(),
					notificacioDto.isEntregaPostal()).
					observacions(notificacioDto.getObservacions()).
					emisor(documentEntity.getExpedient() != null ? documentEntity.getExpedient().getOrganGestor() : null).
					build();
			
			documentNotificacioRepository.save(notificacioEntity);
			
			DocumentEnviamentInteressatEntity documentEnviamentInteressatEntity;
			documentEnviamentInteressatEntity = DocumentEnviamentInteressatEntity.getBuilder(
					interessat, 
					notificacioEntity).build();
			documentEnviamentInteressatRepository.save(documentEnviamentInteressatEntity);
			
//			if (!DocumentNotificacioTipusEnumDto.MANUAL.equals(notificacioDto.getTipus())) {

				if (respostaEnviar.isError()) {
					cacheHelper.evictNotificacionsAmbErrorPerExpedient(expedientEntity);
					notificacioEntity.updateEnviatError(
							respostaEnviar.getErrorDescripcio(),
							respostaEnviar.getIdentificador());
					
					notificacionsWithError.put(interessat.getDocumentNum(), respostaEnviar.getErrorDescripcio());
				} else {
					cacheHelper.evictErrorsValidacioPerNode(expedientEntity);
					cacheHelper.evictNotificacionsPendentsPerExpedient(expedientEntity);
					notificacioEntity.updateEnviat(
							null,
							respostaEnviar.getEstat(),
							respostaEnviar.getIdentificador());
				}

				for (EnviamentReferencia enviamentReferencia : respostaEnviar.getReferencies()) {
					for (DocumentEnviamentInteressatEntity documentEnviamentInteressat : notificacioEntity.getDocumentEnviamentInteressats()) {
						if(documentEnviamentInteressat.getInteressat().getDocumentNum().equals(enviamentReferencia.getTitularNif())) {
							documentEnviamentInteressat.updateEnviamentReferencia(enviamentReferencia.getReferencia());
							pluginHelper.actualitzarDadesRegistre(documentEnviamentInteressat);
						}
					}
				}
//			}
				
			DocumentNotificacioDto dto = conversioTipusHelper.convertir(
					notificacioEntity,
					DocumentNotificacioDto.class);
			
			String destinitariAmbDocument = "";
			for (InteressatDto interessatDto : dto.getInteressats()) {
				destinitariAmbDocument += interessatDto.getNomCompletAmbDocument();
			}
			cacheHelper.evictErrorsValidacioPerNode(expedientEntity);
			cacheHelper.evictNotificacionsPendentsPerExpedient(expedientEntity);
			logAll(notificacioEntity, LogTipusEnumDto.NOTIFICACIO_ENVIADA, destinitariAmbDocument);
		}
	}
	
	public Map<String, String> consultaErrorsNotificacio() {
		return notificacionsWithError;
	}
	
	public DocumentNotificacioDto update (DocumentNotificacioDto notificacio, DocumentEntity document) {
		ExpedientEntity expedient = document.getExpedient();
		if (expedient == null) {
			throw new ValidationException(
					document.getId(),
					DocumentEntity.class,
					"El document no te cap expedient associat (documentId=" + document.getId() + ")");
		}
		DocumentNotificacioEntity documentNotificacioEntity = entityComprovarHelper.comprovarNotificacio(
				expedient,
				null,
				notificacio.getId());
		if (!DocumentNotificacioTipusEnumDto.MANUAL.equals(documentNotificacioEntity.getTipus())) {
			throw new ValidationException(
					notificacio.getId(),
					DocumentNotificacioEntity.class,
					"No es pot modificar una notificació amb el tipus " + documentNotificacioEntity.getTipus());
		}
		DocumentNotificacioDto documentNotificacioDto = conversioTipusHelper.convertir(documentNotificacioEntity, DocumentNotificacioDto.class);
		
		List<InteressatEntity> interessats = validateInteressatsPerNotificacio(
				documentNotificacioDto,
				expedient);
		
		DocumentNotificacioEstatEnumDto estat = documentNotificacioEntity.getNotificacioEstat();
		documentNotificacioEntity.update(
				estat,
				notificacio.getAssumpte(),
				notificacio.getObservacions());
		
		for (InteressatEntity interessatEntity : interessats) {
			DocumentEnviamentInteressatEntity documentEnviamentInteressatEntity = DocumentEnviamentInteressatEntity.getBuilder(interessatEntity, documentNotificacioEntity).build();
			documentEnviamentInteressatRepository.save(documentEnviamentInteressatEntity);
		}
		
		
		DocumentNotificacioDto dto = conversioTipusHelper.convertir(
				documentNotificacioEntity,
				DocumentNotificacioDto.class);
		logAll(documentNotificacioEntity, LogTipusEnumDto.MODIFICACIO, dto.getDestinatariAmbDocument());
		return dto;
	}
	
	public DocumentNotificacioDto delete(Long notificacioId, DocumentEntity document) {
		ExpedientEntity expedient = document.getExpedient();
		if (expedient == null) {
			throw new ValidationException(
					document.getId(),
					DocumentEntity.class,
					"El document no te cap expedient associat (documentId=" + document.getId() + ")");
		}
		DocumentNotificacioEntity notificacio = entityComprovarHelper.comprovarNotificacio(
				expedient,
				null,
				notificacioId);
		if (!DocumentNotificacioTipusEnumDto.MANUAL.equals(notificacio.getTipus())) {
			throw new ValidationException(
					notificacioId,
					DocumentNotificacioEntity.class,
					"No es pot esborrar una notificació que no te el tipus " + DocumentNotificacioTipusEnumDto.MANUAL);
		}
		documentNotificacioRepository.delete(notificacio);
		DocumentNotificacioDto dto = conversioTipusHelper.convertir(
				notificacio,
				DocumentNotificacioDto.class);
		logAll(notificacio, LogTipusEnumDto.MODIFICACIO, dto.getDestinatariAmbDocument());
		return dto;
	}
		
	public DocumentNotificacioDto findAmbId(
			Long entitatId,
			ExpedientEntity expedient,
			Long notificacioId) {
		
		DocumentNotificacioEntity documentNotificacioEntity = entityComprovarHelper.comprovarNotificacio(
				expedient,
				null,
				notificacioId);
		
		DocumentNotificacioDto documentNotificacioDto = conversioTipusHelper.convertir(
				documentNotificacioEntity,
				DocumentNotificacioDto.class);
		
		for (DocumentEnviamentInteressatEntity documentEnviamentInteressatEntity : documentNotificacioEntity.getDocumentEnviamentInteressats()) {
			documentNotificacioDto.getInteressats().add(
					conversioTipusHelper.convertir(
							documentEnviamentInteressatEntity.getInteressat(), 
							InteressatDto.class));
		}

		for (DocumentEnviamentInteressatDto documentEnviamentInteressat: documentNotificacioDto.getDocumentEnviamentInteressats()) {
			String provinciaCodi = documentEnviamentInteressat.getInteressat().getProvincia();
			
			for (PaisDto paisDto : dadesExternesService.findPaisos()) {
				if (paisDto.getCodi().equals(documentEnviamentInteressat.getInteressat().getPais())) {
					documentEnviamentInteressat.getInteressat().setPaisNom(paisDto.getNom());
				}
			}
			for (ProvinciaDto provinciaDto : dadesExternesService.findProvincies()) {
				if (provinciaDto.getCodi().equals(documentEnviamentInteressat.getInteressat().getProvincia())) {
					documentEnviamentInteressat.getInteressat().setProvinciaNom(provinciaDto.getNom());
				}
			}
			for (MunicipiDto municipiDto : dadesExternesService.findMunicipisPerProvincia(provinciaCodi)) {
				if (municipiDto.getCodi().equals(documentEnviamentInteressat.getInteressat().getMunicipi())) {
					documentEnviamentInteressat.getInteressat().setMunicipiNom(municipiDto.getNom());
				}
			}
		}
		
		return documentNotificacioDto;
		
	}
	
	public void actualitzarEstat(DocumentEnviamentInteressatEntity documentEnviamentInteressatEntity) {
		DocumentNotificacioEntity notificacio = documentEnviamentInteressatEntity.getNotificacio();
		DocumentNotificacioEstatEnumDto estatAnterior = notificacio.getNotificacioEstat();
		logger.debug("Estat anterior: " + estatAnterior);
		RespostaConsultaEstatEnviament resposta = pluginHelper.notificacioConsultarIActualitzarEstat(documentEnviamentInteressatEntity);
		if (getPropertyGuardarCertificacioExpedient() && documentEnviamentInteressatEntity.getEnviamentCertificacioData() == null && resposta.getCertificacioData() != null) {
			logger.debug("[CERT] Guardant certificació rebuda de Notib...");
			MetaDocumentEntity metaDocument = metaDocumentRepository.findByEntitatAndTipusGeneric(
					true, 
					null, 
					MetaDocumentTipusGenericEnumDto.ACUSE_RECIBO_NOTIFICACION);
			DocumentDto document = certificacioToDocumentDto(
					documentEnviamentInteressatEntity,
					metaDocument,
					resposta);
			DocumentDto documentCreat = documentHelper.crearDocument(
					document, 
					documentEnviamentInteressatEntity.getNotificacio().getDocument().getPare(), 
					documentEnviamentInteressatEntity.getNotificacio().getDocument().getExpedient(), 
					metaDocument,
					false);
			logAll(notificacio, LogTipusEnumDto.NOTIFICACIO_CERTIFICADA, null);
			
			DocumentEntity documentEntity = documentRepository.findOne(documentCreat.getId());
			documentEntity.updateEstat(DocumentEstatEnumDto.CUSTODIAT);
			logger.debug("[CERT] La certificació s'ha guardat correctament...");
		} else {
			logAll(notificacio, LogTipusEnumDto.NOTIFICACIO_REBUTJADA, null);
		}
		documentEnviamentInteressatEntity.updateEnviamentCertificacioData(resposta.getCertificacioData());
		DocumentNotificacioEstatEnumDto estatDespres = documentEnviamentInteressatEntity.getNotificacio().getNotificacioEstat();
		logger.debug("Estat després: " + estatDespres);
		if (estatAnterior != estatDespres 
				&& (estatAnterior != DocumentNotificacioEstatEnumDto.FINALITZADA && estatDespres != DocumentNotificacioEstatEnumDto.PROCESSADA)) {
			emailHelper.canviEstatNotificacio(notificacio, estatAnterior);
		}
		cacheHelper.evictErrorsValidacioPerNode(documentEnviamentInteressatEntity.getNotificacio().getExpedient());
		cacheHelper.evictNotificacionsPendentsPerExpedient(documentEnviamentInteressatEntity.getNotificacio().getExpedient());
	}
		
	public byte[] getCertificacio(Long documentEnviamentInteressatId) {
		DocumentEnviamentInteressatEntity documentEnviamentInteressatEntity = documentEnviamentInteressatRepository.findOne(
				documentEnviamentInteressatId);
		return pluginHelper.notificacioConsultarIDescarregarCertificacio(documentEnviamentInteressatEntity);
	}
	
	public NotificacioInfoRegistreDto notificacioConsultarIDescarregarJustificant(DocumentEntity document, Long documentEnviamentId) {
		ExpedientEntity expedient = document.getExpedient();
		if (expedient == null) {
			throw new ValidationException(
					document.getId(),
					DocumentEntity.class,
					"El document no te cap expedient associat (documentId=" + document.getId() + ")");
		}
		DocumentEnviamentInteressatEntity enviament = documentEnviamentInteressatRepository.findOne(documentEnviamentId);
		
		RespostaConsultaInfoRegistre resposta = pluginHelper.notificacioConsultarIDescarregarJustificant(
				enviament);
		
		NotificacioInfoRegistreDto infoRegistre = new NotificacioInfoRegistreDto();
		if (!resposta.isError() && resposta != null) {
			infoRegistre.setDataRegistre(resposta.getDataRegistre());
			infoRegistre.setNumRegistreFormatat(resposta.getNumRegistreFormatat());
			infoRegistre.setJustificant(resposta.getJustificant());
		} else {
			infoRegistre.setError(true);
			infoRegistre.setErrorData(resposta.getErrorData());
			infoRegistre.setErrorDescripcio(resposta.getErrorDescripcio());
			
		}
		return infoRegistre;
	}





	
	private boolean getPropertyGuardarCertificacioExpedient() {
		return configHelper.getAsBoolean("es.caib.ripea.notificacio.guardar.certificacio.expedient");
	}
	
	private DocumentDto certificacioToDocumentDto(
			DocumentEnviamentInteressatEntity documentEnviamentInteressatEntity,
			MetaDocumentEntity metaDocument,
			RespostaConsultaEstatEnviament resposta) {
		return contingutHelper.generarDocumentDto(documentEnviamentInteressatEntity, metaDocument, resposta);
	}
	
	private void logAll(DocumentNotificacioEntity notificacioEntity, LogTipusEnumDto tipusLog, String param1) {
		logAll(notificacioEntity, tipusLog, param1, notificacioEntity.getAssumpte());
	}
	
	private void logAll(DocumentNotificacioEntity notificacioEntity, LogTipusEnumDto tipusLog, String param1, String param2) {
		contingutLogHelper.log(
				notificacioEntity.getDocument(),
				LogTipusEnumDto.MODIFICACIO,
				notificacioEntity,
				LogObjecteTipusEnumDto.NOTIFICACIO,
				tipusLog,
				param1,
				param2,
				false,
				false);	
		contingutLogHelper.log(
				notificacioEntity.getDocument().getExpedient(),
				LogTipusEnumDto.MODIFICACIO,
				notificacioEntity,
				LogObjecteTipusEnumDto.NOTIFICACIO,
				tipusLog,
				param1,
				param2,
				false,
				false);

	}
	
	private ExpedientEntity validateExpedientPerNotificacio(DocumentEntity document, DocumentNotificacioTipusEnumDto notificacioTipus) {
		//Document a partir de concatenació (docs firmats/custodiats) i document custodiat
		if ((!document.getDocumentTipus().equals(DocumentTipusEnumDto.VIRTUAL) && !DocumentEstatEnumDto.CUSTODIAT.equals(document.getEstat()))
			&& !DocumentEstatEnumDto.DEFINITIU.equals(document.getEstat())) {
			throw new ValidationException(
					document.getId(),
					DocumentEntity.class,
					"El document no està custodiat");
		}
		ExpedientEntity expedient = HibernateHelper.deproxy(document.getExpedient());
		if (expedient == null) {
			throw new ValidationException(
					document.getId(),
					DocumentEntity.class,
					"El document no te cap expedient associat (documentId=" + document.getId() + ")");
		}
		if (	!DocumentNotificacioTipusEnumDto.MANUAL.equals(notificacioTipus) &&
				!expedient.getMetaExpedient().isNotificacioActiva()) {
			throw new ValidationException(
					document.getId(),
					DocumentEntity.class,
					"El document pertany a un expedient que no te activades les notificacions electròniques");
		}
		return expedient;
	}
	
	private List<InteressatEntity> validateInteressatsPerNotificacio(DocumentNotificacioDto notificacio, ExpedientEntity expedient) {
		
		List<InteressatEntity> interessats = new ArrayList<>();
		for (Long interessatId : notificacio.getInteressatsIds()) {
			
			InteressatEntity interessat = entityComprovarHelper.comprovarInteressat(
					expedient,
					interessatId);
			if (interessat == null) {
				throw new ValidationException(
						interessatId,
						InteressatEntity.class,
						"L'interessat no existeix o no pertany a l'expedient(" +
						"expedientId=" + expedient.getId() + ", " +
						"interessatId=" + interessatId + ")");
			}
			if (	!DocumentNotificacioTipusEnumDto.MANUAL.equals(notificacio.getTipus()) &&
					!interessat.isNotificacioAutoritzat()) {
				throw new ValidationException(
						interessatId,
						InteressatEntity.class,
						"L'interessat no ha donat el consentiment per a les notificacions electròniques (" +
						"expedientId=" + expedient.getId() + ", " +
						"interessatId=" + interessatId + ")");
			}	
			interessats.add(interessat);		
		}
		return interessats;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);
}
