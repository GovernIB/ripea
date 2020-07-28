/**
 * 
 */
package es.caib.ripea.core.service;

import java.util.ArrayList;
import java.util.List;


import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.DocumentEnviamentDto;
import es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentEnviamentInteressatDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentPublicacioDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.MunicipiDto;
import es.caib.ripea.core.api.dto.NotificacioEnviamentDto;
import es.caib.ripea.core.api.dto.PaisDto;
import es.caib.ripea.core.api.dto.ProvinciaDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.DadesExternesService;
import es.caib.ripea.core.api.service.DocumentEnviamentService;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentEnviamentInteressatEntity;
import es.caib.ripea.core.entity.DocumentNotificacioEntity;
import es.caib.ripea.core.entity.DocumentPublicacioEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.InteressatEntity;
import es.caib.ripea.core.helper.AlertaHelper;
import es.caib.ripea.core.helper.CacheHelper;
import es.caib.ripea.core.helper.ContingutLogHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.DocumentHelper;
import es.caib.ripea.core.helper.EmailHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.MessageHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.repository.DocumentEnviamentInteressatRepository;
import es.caib.ripea.core.repository.DocumentNotificacioRepository;
import es.caib.ripea.core.repository.DocumentPublicacioRepository;
import es.caib.ripea.plugin.notificacio.EnviamentReferencia;
import es.caib.ripea.plugin.notificacio.RespostaEnviar;

/**
 * Implementació dels mètodes per a gestionar els enviaments
 * de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class DocumentEnviamentServiceImpl implements DocumentEnviamentService {

	@Autowired
	private DocumentNotificacioRepository documentNotificacioRepository;
	@Autowired
	private DocumentPublicacioRepository documentPublicacioRepository;
	@Autowired
	private DocumentHelper documentHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private ContingutLogHelper contingutLogHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private EmailHelper emailHelper;
	@Autowired
	private AlertaHelper alertaHelper;
	@Autowired
	private MessageHelper messageHelper;
	@Autowired
	DocumentEnviamentInteressatRepository documentEnviamentInteressatRepository;
	@Autowired
	private DadesExternesService dadesExternesService;
	@Autowired
	private CacheHelper cacheHelper;
	
	private ExpedientEntity validateExpedientPerNotificacio(DocumentEntity document, DocumentNotificacioTipusEnumDto notificacioTipus) {
		//Document a partir de concatenació (docs firmats/custodiats) i document custodiat
		if (!document.getDocumentTipus().equals(DocumentTipusEnumDto.VIRTUAL) && !DocumentEstatEnumDto.CUSTODIAT.equals(document.getEstat())) {
			throw new ValidationException(
					document.getId(),
					DocumentEntity.class,
					"El document no està custodiat");
		}
		ExpedientEntity expedient = document.getExpedient();
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
	

	@Transactional
	@Override
	public void notificacioCreate(
			Long entitatId,
			Long documentId,
			DocumentNotificacioDto notificacioDto) {
		
		logger.debug("Creant una notificació del document (" +
				"entitatId=" + entitatId + ", " +
				"documentId=" + documentId + ", " +
				"notificacio=" + notificacioDto + ")");
		DocumentEntity documentEntity = documentHelper.comprovarDocumentDinsExpedientAccessible(
				entitatId,
				documentId,
				false,
				true);
		ExpedientEntity expedientEntity = validateExpedientPerNotificacio(documentEntity, 
																		  notificacioDto.getTipus());
//		List<InteressatEntity> interessats = validateInteressatsPerNotificacio(notificacioDto, expedientEntity);
		
		for (NotificacioEnviamentDto notificacioEnviamentDto : notificacioDto.getEnviaments()) {
			
			InteressatEntity interessat = entityComprovarHelper.comprovarInteressat(
					expedientEntity,
					notificacioEnviamentDto.getTitular().getId());
			notificacioDto.setServeiTipusEnum(notificacioEnviamentDto.getServeiTipusEnum());
			notificacioDto.setEntregaPostal(notificacioEnviamentDto.getEntregaPostal());
			
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
					build();
			
			documentNotificacioRepository.save(notificacioEntity);
			
			DocumentEnviamentInteressatEntity documentEnviamentInteressatEntity;
			documentEnviamentInteressatEntity = DocumentEnviamentInteressatEntity.getBuilder(interessat, 
																							 notificacioEntity).build();
			documentEnviamentInteressatRepository.save(documentEnviamentInteressatEntity);
			
//			if (!DocumentNotificacioTipusEnumDto.MANUAL.equals(notificacioDto.getTipus())) {

				if (respostaEnviar.isError()) {
					cacheHelper.evictNotificacionsAmbErrorPerExpedient(expedientEntity);
					notificacioEntity.updateEnviatError(
							respostaEnviar.getErrorDescripcio(),
							respostaEnviar.getIdentificador());
				} else {
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
			
			contingutLogHelper.log(
					expedientEntity,
					LogTipusEnumDto.MODIFICACIO,
					notificacioEntity,
					LogObjecteTipusEnumDto.NOTIFICACIO,
					LogTipusEnumDto.ENVIAMENT,
					destinitariAmbDocument,
					notificacioEntity.getAssumpte(),
					false,
					false);
			contingutLogHelper.log(
					documentEntity,
					LogTipusEnumDto.MODIFICACIO,
					notificacioEntity,
					LogObjecteTipusEnumDto.NOTIFICACIO,
					LogTipusEnumDto.ENVIAMENT,
					destinitariAmbDocument,
					notificacioEntity.getAssumpte(),
					false,
					false);
		}
	}

	@Transactional
	@Override
	public DocumentNotificacioDto notificacioUpdate(
			Long entitatId,
			Long documentId,
			DocumentNotificacioDto notificacio) {
		logger.debug("Modificant una notificació de l'expedient (" +
				"entitatId=" + entitatId + ", " +
				"documentId=" + documentId + ", " +
				"notificacio=" + notificacio + ")");
		
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
				entitatId,
				documentId,
				false,
				true);
		ExpedientEntity expedient = document.getExpedient();
		if (expedient == null) {
			throw new ValidationException(
					documentId,
					DocumentEntity.class,
					"El document no te cap expedient associat (documentId=" + documentId + ")");
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
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.MODIFICACIO,
				documentNotificacioEntity,
				LogObjecteTipusEnumDto.NOTIFICACIO,
				LogTipusEnumDto.MODIFICACIO,
				null,
				null,
				false,
				false);
		contingutLogHelper.log(
				documentNotificacioEntity.getDocument(),
				LogTipusEnumDto.MODIFICACIO,
				documentNotificacioEntity,
				LogObjecteTipusEnumDto.NOTIFICACIO,
				LogTipusEnumDto.MODIFICACIO,
				dto.getDestinatariAmbDocument(),
				documentNotificacioEntity.getAssumpte(),
				false,
				false);
		return dto;
	}

	@Transactional
	@Override
	public DocumentNotificacioDto notificacioDelete(
			Long entitatId,
			Long documentId,
			Long notificacioId) {
		logger.debug("Esborrant una notificació de l'expedient (" +
				"entitatId=" + entitatId + ", " +
				"documentId=" + documentId + ", " +
				"notificacioId=" + notificacioId + ")");
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
				entitatId,
				documentId,
				false,
				true);
		ExpedientEntity expedient = document.getExpedient();
		if (expedient == null) {
			throw new ValidationException(
					documentId,
					DocumentEntity.class,
					"El document no te cap expedient associat (documentId=" + documentId + ")");
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
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.MODIFICACIO,
				notificacio,
				LogObjecteTipusEnumDto.NOTIFICACIO,
				LogTipusEnumDto.ELIMINACIO,
				null,
				null,
				false,
				false);
		contingutLogHelper.log(
				notificacio.getDocument(),
				LogTipusEnumDto.MODIFICACIO,
				notificacio,
				LogObjecteTipusEnumDto.NOTIFICACIO,
				LogTipusEnumDto.ELIMINACIO,
				dto.getDestinatariAmbDocument(),
				notificacio.getAssumpte(),
				false,
				false);
		return dto;
	}
	
	@Transactional(readOnly = true)
	@Override
	public DocumentNotificacioDto notificacioFindAmbIdAndExpedient(
			Long entitatId,
			Long expedientId,
			Long notificacioId) {
		logger.debug("Consulta d'una notificació de l'expedient (" +
				"entitatId=" + entitatId + ", " +
				"expedientId=" + expedientId + ", " +
				"notificacioId=" + notificacioId + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				false,
				true,
				false,
				false);
		
		return notificacioFindAmbId(entitatId, expedient, notificacioId);

	}
	
	
	private DocumentNotificacioDto notificacioFindAmbId(
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

	@Transactional(readOnly = true)
	@Override
	public DocumentNotificacioDto notificacioFindAmbIdAndDocument(
			Long entitatId,
			Long documentId,
			Long notificacioId) {
		logger.debug("Consulta d'una notificació de l'expedient (" +
				"entitatId=" + entitatId + ", " +
				"documentId=" + documentId + ", " +
				"notificacioId=" + notificacioId + ")");
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
				entitatId,
				documentId,
				false,
				true);
		ExpedientEntity expedient = document.getExpedient();
		if (expedient == null) {
			throw new ValidationException(
					documentId,
					DocumentEntity.class,
					"El document no te cap expedient associat (documentId=" + documentId + ")");
		}
		return notificacioFindAmbId(entitatId, expedient, notificacioId);
	}

	@Transactional
	@Override
	public DocumentPublicacioDto publicacioCreate(
			Long entitatId,
			Long documentId,
			DocumentPublicacioDto publicacio) {
		logger.debug("Creant una publicació de l'expedient (" +
				"entitatId=" + entitatId + ", " +
				"documentId=" + documentId + ", " +
				"publicacio=" + publicacio + ")");
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
				entitatId,
				documentId,
				false,
				true);
		ExpedientEntity expedient = document.getExpedient();
		if (expedient == null) {
			throw new ValidationException(
					documentId,
					DocumentEntity.class,
					"El document no te cap expedient associat (documentId=" + documentId + ")");
		}
		DocumentPublicacioEntity publicacioEntity = DocumentPublicacioEntity.getBuilder(
				DocumentEnviamentEstatEnumDto.ENVIAT,
				publicacio.getAssumpte(),
				publicacio.getTipus(),
				expedient,
				document).
				observacions(publicacio.getObservacions()).
				enviatData(publicacio.getEnviatData()).
				processatData(publicacio.getProcessatData()).
				build();
		DocumentPublicacioDto dto = conversioTipusHelper.convertir(
				documentPublicacioRepository.save(publicacioEntity),
				DocumentPublicacioDto.class);
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.MODIFICACIO,
				publicacioEntity,
				LogObjecteTipusEnumDto.PUBLICACIO,
				LogTipusEnumDto.CREACIO,
				document.getNom(),
				publicacio.getTipus().name(),
				false,
				false);
		return dto;
	}

	@Transactional
	@Override
	public DocumentPublicacioDto publicacioUpdate(
			Long entitatId,
			Long documentId,
			DocumentPublicacioDto publicacio) {
		logger.debug("Modificant una publicació de l'expedient (" +
				"entitatId=" + entitatId + ", " +
				"documentId=" + documentId + ", " +
				"publicacio=" + publicacio + ")");
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
				entitatId,
				documentId,
				false,
				true);
		ExpedientEntity expedient = document.getExpedient();
		if (expedient == null) {
			throw new ValidationException(
					documentId,
					DocumentEntity.class,
					"El document no te cap expedient associat (documentId=" + documentId + ")");
		}
		DocumentPublicacioEntity entity = entityComprovarHelper.comprovarPublicacio(
				expedient,
				null,
				publicacio.getId());
		entity.update(
				publicacio.getAssumpte(),
				publicacio.getObservacions(),
				publicacio.getTipus(),
				publicacio.getEnviatData(),
				publicacio.getProcessatData());
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.MODIFICACIO,
				entity,
				LogObjecteTipusEnumDto.PUBLICACIO,
				LogTipusEnumDto.MODIFICACIO,
				null,
				null,
				false,
				false);
		return conversioTipusHelper.convertir(
				entity,
				DocumentPublicacioDto.class);
	}

	@Transactional
	@Override
	public DocumentPublicacioDto publicacioDelete(
			Long entitatId,
			Long documentId,
			Long publicacioId) {
		logger.debug("Esborrant una publicació de l'expedient (" +
				"entitatId=" + entitatId + ", " +
				"documentId=" + documentId + ", " +
				"publicacioId=" + publicacioId + ")");
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
				entitatId,
				documentId,
				false,
				true);
		ExpedientEntity expedient = document.getExpedient();
		if (expedient == null) {
			throw new ValidationException(
					documentId,
					DocumentEntity.class,
					"El document no te cap expedient associat (documentId=" + documentId + ")");
		}
		DocumentPublicacioEntity publicacio = entityComprovarHelper.comprovarPublicacio(
				expedient,
				null,
				publicacioId);
		documentPublicacioRepository.delete(publicacio);
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.MODIFICACIO,
				publicacio,
				LogObjecteTipusEnumDto.PUBLICACIO,
				LogTipusEnumDto.ELIMINACIO,
				null,
				null,
				false,
				false);
		return conversioTipusHelper.convertir(
				publicacio,
				DocumentPublicacioDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public DocumentPublicacioDto publicacioFindAmbId(
			Long entitatId,
			Long documentId,
			Long publicacioId) {
		logger.debug("Consulta d'una publicació de l'expedient (" +
				"entitatId=" + entitatId + ", " +
				"documentId=" + documentId + ", " +
				"publicacioId=" + publicacioId + ")");
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
				entitatId,
				documentId,
				false,
				true);
		ExpedientEntity expedient = document.getExpedient();
		if (expedient == null) {
			throw new ValidationException(
					documentId,
					DocumentEntity.class,
					"El document no te cap expedient associat (documentId=" + documentId + ")");
		}
		DocumentPublicacioEntity publicacio = entityComprovarHelper.comprovarPublicacio(
				expedient,
				null,
				publicacioId);
		return conversioTipusHelper.convertir(
				publicacio,
				DocumentPublicacioDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<DocumentEnviamentDto> findAmbExpedient(
			Long entitatId,
			Long expedientId) {
		logger.debug("Obtenint la llista d'enviaments de l'expedient (" +
				"entitatId=" + entitatId + ", " +
				"expedientId=" + expedientId + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				true,
				false,
				false,
				false);
		List<DocumentEnviamentDto> resposta = new ArrayList<DocumentEnviamentDto>();
		List<DocumentNotificacioEntity> notificacions = documentNotificacioRepository.findByExpedientOrderByCreatedDateDesc(expedient);
		for (DocumentNotificacioEntity notificacio: notificacions) {
			resposta.add(
					conversioTipusHelper.convertir(
							notificacio,
							DocumentNotificacioDto.class));
		}
		List<DocumentPublicacioEntity> publicacions = documentPublicacioRepository.findByExpedientOrderByEnviatDataAsc(
				expedient);
		for (DocumentPublicacioEntity publicacio: publicacions) {
			resposta.add(
					conversioTipusHelper.convertir(
							publicacio,
							DocumentPublicacioDto.class));
		}
		return resposta;
	}
	
	
	@SuppressWarnings("unused")
	@Transactional(readOnly = true)
	@Override
	public int enviamentsCount(
			Long entitatId,
			Long expedientId) {
		logger.debug("Obtenint enviaments count (" +
				"entitatId=" + entitatId + ", " +
				"expedientId=" + expedientId + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				true,
				false,
				false,
				false);
		
		int count = 0;
		List<DocumentNotificacioEntity> notificacions = documentNotificacioRepository.findByExpedientOrderByEnviatDataAsc(expedient);
		for (DocumentNotificacioEntity notificacio: notificacions) {
			count++;
		}
		List<DocumentPublicacioEntity> publicacions = documentPublicacioRepository.findByExpedientOrderByEnviatDataAsc(
				expedient);
		for (DocumentPublicacioEntity publicacio: publicacions) {
			count++;
		}
		return count;
	}
	
	
	

	@Transactional(readOnly = true)
	@Override
	public List<DocumentEnviamentDto> findAmbDocument(
			Long entitatId,
			Long documentId) {
		logger.debug("Obtenint la llista d'enviaments de l'expedient (" +
				"entitatId=" + entitatId + ", " +
				"documentId=" + documentId + ")");
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
				entitatId,
				documentId,
				false,
				true);
		List<DocumentEnviamentDto> resposta = new ArrayList<DocumentEnviamentDto>();
		List<DocumentNotificacioEntity> notificacions = documentNotificacioRepository.findByDocumentOrderByEnviatDataAsc(
				document);
		for (DocumentNotificacioEntity notificacio: notificacions) {
			resposta.add(
					conversioTipusHelper.convertir(
							notificacio,
							DocumentNotificacioDto.class));
		}
		List<DocumentPublicacioEntity> publicacions = documentPublicacioRepository.findByDocumentOrderByEnviatDataAsc(
				document);
		for (DocumentPublicacioEntity publicacio: publicacions) {
			resposta.add(
					conversioTipusHelper.convertir(
							publicacio,
							DocumentPublicacioDto.class));
		}
		return resposta;
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<DocumentEnviamentDto> findNotificacionsAmbDocument(
			Long entitatId,
			Long documentId) {
		logger.debug("Obtenint la llista d'enviaments de l'expedient (" +
				"entitatId=" + entitatId + ", " +
				"documentId=" + documentId + ")");
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
				entitatId,
				documentId,
				false,
				true);
		List<DocumentEnviamentDto> resposta = new ArrayList<DocumentEnviamentDto>();
		List<DocumentNotificacioEntity> notificacions = documentNotificacioRepository.findByDocumentOrderByEnviatDataAsc(
				document);
		for (DocumentNotificacioEntity notificacio: notificacions) {
			DocumentNotificacioDto documentNotificacio = 
					conversioTipusHelper.convertir(
							notificacio,
							DocumentNotificacioDto.class);
			//informació no necessària en aquest cas (acelerar conversió a JSON)
			documentNotificacio.setDocument(null);
			documentNotificacio.setDocumentEnviamentInteressats(null);
			
			resposta.add(documentNotificacio);
		}
		
		return resposta;
	}

	@Override
	@Transactional
	public void notificacioActualitzarEstat(String identificador, String referencia) {
		DocumentEnviamentInteressatEntity documentEnviamentInteressatEntity = documentEnviamentInteressatRepository.findByIdentificadorIReferencia(
				identificador, referencia);
		if (documentEnviamentInteressatEntity == null) {
			throw new NotFoundException(documentEnviamentInteressatEntity, DocumentEnviamentInteressatEntity.class);
		}
		DocumentNotificacioEntity notificacio = documentEnviamentInteressatEntity.getNotificacio();
		try {
			DocumentEnviamentEstatEnumDto estatAbans = notificacio.getEstat();
			pluginHelper.notificacioConsultarIActualitzarEstat(documentEnviamentInteressatEntity);
			DocumentEnviamentEstatEnumDto estatDespres = notificacio.getEstat();
			if (estatAbans != estatDespres) {
				alertaHelper.crearAlerta(
						"La notificació del document " + documentEnviamentInteressatEntity.getNotificacio().getDocument().getNom() + " ha canviat a l'estat " + estatDespres,
						null,
						documentEnviamentInteressatEntity.getNotificacio().getDocument().getExpedient().getId());
				emailHelper.canviEstatNotificacio(notificacio, estatAbans);
			}
		} catch (Exception ex) {
			Throwable rootCause = ExceptionUtils.getRootCause(ex);
			if (rootCause == null) rootCause = ex;
			alertaHelper.crearAlerta(
					messageHelper.getMessage(
							"alertes.segon.pla.notificacions.error",
							new Object[] {notificacio.getId()}),
					ex,
					notificacio.getExpedient().getId());
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(DocumentEnviamentServiceImpl.class);
}
