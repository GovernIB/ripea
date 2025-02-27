package es.caib.ripea.service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.DocumentNotificacioEntity;
import es.caib.ripea.persistence.entity.DocumentPublicacioEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.InteressatAdministracioEntity;
import es.caib.ripea.persistence.entity.InteressatEntity;
import es.caib.ripea.persistence.repository.DocumentNotificacioRepository;
import es.caib.ripea.persistence.repository.DocumentPublicacioRepository;
import es.caib.ripea.persistence.repository.DocumentRepository;
import es.caib.ripea.persistence.repository.InteressatRepository;
import es.caib.ripea.service.firma.DocumentFirmaServidorFirma;
import es.caib.ripea.service.helper.ContingutLogHelper;
import es.caib.ripea.service.helper.ConversioTipusHelper;
import es.caib.ripea.service.helper.DocumentHelper;
import es.caib.ripea.service.helper.DocumentNotificacioHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.HibernateHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.intf.dto.AmpliarPlazoForm;
import es.caib.ripea.service.intf.dto.DocumentEnviamentDto;
import es.caib.ripea.service.intf.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentEnviamentTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNotificacioDto;
import es.caib.ripea.service.intf.dto.DocumentPublicacioDto;
import es.caib.ripea.service.intf.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.service.intf.dto.LogTipusEnumDto;
import es.caib.ripea.service.intf.dto.RespostaAmpliarPlazo;
import es.caib.ripea.service.intf.exception.ValidationException;
import es.caib.ripea.service.intf.service.DocumentEnviamentService;

@Service
public class DocumentEnviamentServiceImpl implements DocumentEnviamentService {

	@Autowired private DocumentNotificacioRepository documentNotificacioRepository;
	@Autowired private DocumentPublicacioRepository documentPublicacioRepository;
	@Autowired private DocumentHelper documentHelper;
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private ContingutLogHelper contingutLogHelper;
	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private DocumentNotificacioHelper documentNotificacioHelper;
	@Autowired private DocumentFirmaServidorFirma documentFirmaServidorFirma;
	@Autowired private InteressatRepository interessatRepository;
	@Autowired private DocumentRepository documentRepository;
	@Autowired private PluginHelper pluginHelper;

	@Transactional
	@Override
	public Map<String, String> notificacioCreate(
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
		Map<String, String> errorsNotificant = documentNotificacioHelper.crear(notificacioDto, documentEntity);
		
		if (documentEntity.getFitxerContentType().equals("application/zip")) {
			documentFirmaServidorFirma.removeFirmesInvalidesAndFirmaServidor(documentEntity.getId(), "Firma de document zip generat per notificar múltiples documents", null);
		}

		return errorsNotificant;
	}
	
	@Transactional
	@Override
	public boolean checkIfAnyInteressatIsAdministracio(List<Long> interessatsIds) {
		boolean isAnyAdministracio = false;
		if (interessatsIds != null) {
			for (Long id : interessatsIds) {
				InteressatEntity inter = interessatRepository.getOne(id);
				if (inter instanceof InteressatAdministracioEntity) {
					isAnyAdministracio = true;
					break;
				}
			}
		}
		return isAnyAdministracio;
	}
	
	@Transactional
	@Override
	public boolean checkIfDocumentIsZip(Long documentId) {
		DocumentEntity doc = documentRepository.getOne(documentId);
		return doc.getFitxerContentType().equals("application/zip");
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

		return documentNotificacioHelper.update(notificacio, document);
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
		return documentNotificacioHelper.delete(notificacioId, document);
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
				expedientId,
				false,
				false,
				true,
				false,
				false,
				null);
		
		return documentNotificacioHelper.findAmbId(entitatId, expedient, notificacioId);
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
		ExpedientEntity expedient = HibernateHelper.deproxy(document.getExpedient());
		if (expedient == null) {
			throw new ValidationException(
					documentId,
					DocumentEntity.class,
					"El document no te cap expedient associat (documentId=" + documentId + ")");
		}
		return documentNotificacioHelper.findAmbId(entitatId, expedient, notificacioId);
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
		
		contingutLogHelper.log(
				document,
				LogTipusEnumDto.MODIFICACIO,
				publicacioEntity,
				LogObjecteTipusEnumDto.PUBLICACIO,
				LogTipusEnumDto.CREACIO,
				publicacio.getAssumpte(),
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
			Long expedientId, 
			DocumentEnviamentTipusEnumDto documentEnviamentTipus) {
		logger.debug("Obtenint la llista d'enviaments de l'expedient (" +
				"entitatId=" + entitatId + ", " +
				"expedientId=" + expedientId + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				expedientId,
				false,
				true,
				false,
				false,
				false,
				null);
		List<DocumentEnviamentDto> resposta = new ArrayList<DocumentEnviamentDto>();

		if (documentEnviamentTipus == DocumentEnviamentTipusEnumDto.NOTIFICACIO) {
			List<DocumentNotificacioEntity> notificacions = documentNotificacioRepository.findByExpedientOrderByCreatedDateDesc(
					expedient);
			for (DocumentNotificacioEntity notificacio: notificacions) {
				resposta.add(
						conversioTipusHelper.convertir(
								notificacio,
								DocumentNotificacioDto.class));
			}
		} else if (documentEnviamentTipus == DocumentEnviamentTipusEnumDto.PUBLICACIO) {
			List<DocumentPublicacioEntity> publicacions = documentPublicacioRepository.findByExpedientOrderByEnviatDataAsc(
					expedient);
			for (DocumentPublicacioEntity publicacio: publicacions) {
				resposta.add(
						conversioTipusHelper.convertir(
								publicacio,
								DocumentPublicacioDto.class));
			}
		}

		return resposta;
	}
	
	
	@SuppressWarnings("unused")
	@Transactional(readOnly = true)
	@Override
	public int enviamentsCount(
			Long entitatId,
			Long expedientId, 
			DocumentEnviamentTipusEnumDto documentEnviamentTipus) {
		logger.debug("Obtenint enviaments count (" +
				"entitatId=" + entitatId + ", " +
				"expedientId=" + expedientId + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				expedientId,
				false,
				true,
				false,
				false,
				false,
				null);
		
		int count = 0;
		if (documentEnviamentTipus == DocumentEnviamentTipusEnumDto.NOTIFICACIO) {
			List<DocumentNotificacioEntity> notificacions = documentNotificacioRepository.findByExpedientOrderByEnviatDataAsc(expedient);
			for (DocumentNotificacioEntity notificacio: notificacions) {
				count++;
			}
		} else if (documentEnviamentTipus == DocumentEnviamentTipusEnumDto.PUBLICACIO) {
			List<DocumentPublicacioEntity> publicacions = documentPublicacioRepository.findByExpedientOrderByEnviatDataAsc(
					expedient);
			for (DocumentPublicacioEntity publicacio: publicacions) {
				count++;
			}
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



	private static final Logger logger = LoggerFactory.getLogger(DocumentEnviamentServiceImpl.class);

	@Override
	public List<RespostaAmpliarPlazo> ampliarPlazoEnviament(AmpliarPlazoForm documentNotificacioDto) {
		return pluginHelper.ampliarPlazoEnviament(documentNotificacioDto);
	}
}
