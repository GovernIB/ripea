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
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentPublicacioDto;
import es.caib.ripea.core.api.dto.InteressatIdiomaEnumDto;
import es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.DocumentEnviamentService;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentNotificacioEntity;
import es.caib.ripea.core.entity.DocumentPublicacioEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.InteressatAdministracioEntity;
import es.caib.ripea.core.entity.InteressatEntity;
import es.caib.ripea.core.helper.AlertaHelper;
import es.caib.ripea.core.helper.ContingutLogHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.DocumentHelper;
import es.caib.ripea.core.helper.EmailHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.MessageHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.repository.DocumentNotificacioRepository;
import es.caib.ripea.core.repository.DocumentPublicacioRepository;

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

	@Transactional
	@Override
	public DocumentNotificacioDto notificacioCreate(
			Long entitatId,
			Long documentId,
			DocumentNotificacioDto notificacio) {
		logger.debug("Creant una notificació del document (" +
				"entitatId=" + entitatId + ", " +
				"documentId=" + documentId + ", " +
				"notificacio=" + notificacio + ")");
		DocumentEntity document = documentHelper.comprovarDocumentDinsExpedientAccessible(
				entitatId,
				documentId,
				false,
				true);
		if (!DocumentEstatEnumDto.CUSTODIAT.equals(document.getEstat())) {
			throw new ValidationException(
					documentId,
					DocumentEntity.class,
					"El document no està custodiat");
		}
		ExpedientEntity expedient = document.getExpedient();
		if (expedient == null) {
			throw new ValidationException(
					documentId,
					DocumentEntity.class,
					"El document no te cap expedient associat (documentId=" + documentId + ")");
		}
		if (	!DocumentNotificacioTipusEnumDto.MANUAL.equals(notificacio.getTipus()) &&
				!expedient.getMetaExpedient().isNotificacioActiva()) {
			throw new ValidationException(
					documentId,
					DocumentEntity.class,
					"El document pertany a un expedient que no te activades les notificacions electròniques");
		}
		InteressatEntity interessat = entityComprovarHelper.comprovarInteressat(
				expedient,
				notificacio.getInteressatId());
		if (interessat == null) {
			throw new ValidationException(
					notificacio.getInteressatId(),
					InteressatEntity.class,
					"L'interessat no existeix o no pertany a l'expedient(" +
					"expedientId=" + expedient.getId() + ", " +
					"interessatId=" + notificacio.getInteressatId() + ")");
		}
		if (	!DocumentNotificacioTipusEnumDto.MANUAL.equals(notificacio.getTipus()) &&
				!interessat.isNotificacioAutoritzat()) {
			throw new ValidationException(
					notificacio.getInteressatId(),
					InteressatEntity.class,
					"L'interessat no ha donat el consentiment per a les notificacions electròniques (" +
					"expedientId=" + expedient.getId() + ", " +
					"interessatId=" + notificacio.getInteressatId() + ")");
		}
		if (interessat instanceof InteressatAdministracioEntity) {
			throw new ValidationException(
					notificacio.getInteressatId(),
					InteressatEntity.class,
					"L'interessat ha de ser una persona física o jurídica(" +
					"interessatId=" + notificacio.getInteressatId() + ", " +
					"interessatClass=" + interessat.getClass().getName() + ")");
		}
		InteressatIdiomaEnumDto notificacioIdioma = interessat.getPreferenciaIdioma();
		if (notificacioIdioma == null) {
			notificacioIdioma = InteressatIdiomaEnumDto.CA;
		}
		DocumentNotificacioEntity notificacioEntity = DocumentNotificacioEntity.getBuilder(
				(notificacio.getEstat() != null) ? notificacio.getEstat() : DocumentEnviamentEstatEnumDto.PENDENT,
				notificacio.getAssumpte(),
				DocumentNotificacioTipusEnumDto.NOTIFICACIO,
				null, // dataProgramada
				null, // retard
				null, // dataCaducitat
				interessat,
				notificacioIdioma,
				notificacio.getSeuAvisTitol(),
				notificacio.getSeuAvisText(),
				notificacio.getSeuOficiTitol(),
				notificacio.getSeuOficiText(),
				expedient,
				document).
				observacions(notificacio.getObservacions()).
				seuAvisTextMobil(notificacio.getSeuAvisTextMobil()).
				build();
		if (!DocumentNotificacioTipusEnumDto.MANUAL.equals(notificacio.getTipus())) {
			pluginHelper.notificacioEnviar(notificacioEntity);
		}
		DocumentNotificacioDto dto = conversioTipusHelper.convertir(
				documentNotificacioRepository.save(notificacioEntity),
				DocumentNotificacioDto.class);
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.MODIFICACIO,
				notificacioEntity,
				LogObjecteTipusEnumDto.NOTIFICACIO,
				LogTipusEnumDto.ENVIAMENT,
				dto.getDestinatariAmbDocument(),
				notificacioEntity.getAssumpte(),
				false,
				false);
		contingutLogHelper.log(
				document,
				LogTipusEnumDto.MODIFICACIO,
				notificacioEntity,
				LogObjecteTipusEnumDto.NOTIFICACIO,
				LogTipusEnumDto.ENVIAMENT,
				dto.getDestinatariAmbDocument(),
				notificacioEntity.getAssumpte(),
				false,
				false);
		return dto;
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
		DocumentNotificacioEntity entity = entityComprovarHelper.comprovarNotificacio(
				expedient,
				null,
				notificacio.getId());
		if (!DocumentNotificacioTipusEnumDto.MANUAL.equals(entity.getTipus())) {
			throw new ValidationException(
					notificacio.getId(),
					DocumentNotificacioEntity.class,
					"No es pot modificar una notificació amb el tipus " + entity.getTipus());
		}
		InteressatEntity interessat = entityComprovarHelper.comprovarInteressat(
				expedient,
				notificacio.getInteressatId());
		if (interessat == null) {
			throw new ValidationException(
					notificacio.getInteressatId(),
					InteressatEntity.class,
					"L'interessat no existeix o no pertany a l'expedient(" +
					"expedientId=" + expedient.getId() + ", " +
					"interessatId=" + notificacio.getInteressatId() + ")");
		}
		if (	!DocumentNotificacioTipusEnumDto.MANUAL.equals(notificacio.getTipus()) &&
				!interessat.isNotificacioAutoritzat()) {
			throw new ValidationException(
					notificacio.getInteressatId(),
					InteressatEntity.class,
					"L'interessat no ha donat el consentiment per a les notificacions electròniques (" +
					"expedientId=" + expedient.getId() + ", " +
					"interessatId=" + notificacio.getInteressatId() + ")");
		}
		if (interessat instanceof InteressatAdministracioEntity) {
			throw new ValidationException(
					notificacio.getInteressatId(),
					InteressatEntity.class,
					"L'interessat ha de ser una persona física o jurídica(" +
					"interessatId=" + notificacio.getInteressatId() + ", " +
					"interessatClass=" + interessat.getClass().getName() + ")");
		}
		DocumentEnviamentEstatEnumDto estat = entity.getEstat();
		if (DocumentNotificacioTipusEnumDto.MANUAL.equals(entity.getTipus())) {
			estat = notificacio.getEstat();
		}
		entity.update(
				estat,
				notificacio.getAssumpte(),
				notificacio.getObservacions(),
				interessat);
		DocumentNotificacioDto dto = conversioTipusHelper.convertir(
				entity,
				DocumentNotificacioDto.class);
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.MODIFICACIO,
				entity,
				LogObjecteTipusEnumDto.NOTIFICACIO,
				LogTipusEnumDto.MODIFICACIO,
				null,
				null,
				false,
				false);
		contingutLogHelper.log(
				entity.getDocument(),
				LogTipusEnumDto.MODIFICACIO,
				entity,
				LogObjecteTipusEnumDto.NOTIFICACIO,
				LogTipusEnumDto.MODIFICACIO,
				dto.getDestinatariAmbDocument(),
				entity.getAssumpte(),
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
	public DocumentNotificacioDto notificacioFindAmbId(
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
		DocumentNotificacioEntity notificacio = entityComprovarHelper.comprovarNotificacio(
				expedient,
				null,
				notificacioId);
		return conversioTipusHelper.convertir(
				notificacio,
				DocumentNotificacioDto.class);
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
		List<DocumentNotificacioEntity> notificacions = documentNotificacioRepository.findByExpedientOrderByEnviatDataAsc(expedient);
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

	@Override
	@Transactional
	public void notificacioActualitzarEstat(String identificador, String referencia) {
		DocumentNotificacioEntity notificacio = documentNotificacioRepository.findByEnviamentIdentificadorAndEnviamentReferencia(
				identificador,
				referencia);
		logger.debug("Actualitzant l'estat de les notificacions");
		if (notificacio == null) {
			throw new NotFoundException(
					"[" + identificador + ", " + referencia + "]",
					DocumentNotificacioEntity.class);
		}
		try {
			DocumentEnviamentEstatEnumDto estatAbans = notificacio.getEstat();
			pluginHelper.notificacioActualitzarEstat(notificacio);
			DocumentEnviamentEstatEnumDto estatDespres = notificacio.getEstat();
			if (estatAbans != estatDespres) {
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
