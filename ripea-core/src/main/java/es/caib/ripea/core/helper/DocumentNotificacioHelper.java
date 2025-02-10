package es.caib.ripea.core.helper;

import es.caib.ripea.core.api.dto.DocumentEnviamentInteressatDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.MunicipiDto;
import es.caib.ripea.core.api.dto.PaisDto;
import es.caib.ripea.core.api.dto.ProvinciaDto;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.DadesExternesService;
import es.caib.ripea.core.persistence.DocumentEntity;
import es.caib.ripea.core.persistence.DocumentEnviamentInteressatEntity;
import es.caib.ripea.core.persistence.DocumentNotificacioEntity;
import es.caib.ripea.core.persistence.ExpedientEntity;
import es.caib.ripea.core.persistence.InteressatEntity;
import es.caib.ripea.core.repository.DocumentEnviamentInteressatRepository;
import es.caib.ripea.core.repository.DocumentNotificacioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilitat per gestionar l'enviament de notificacions dels documents d'expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DocumentNotificacioHelper {
	
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
	private DocumentHelper documentHelper;
	@Autowired
	private DocumentNotificacioInteressatHelper documentNotificacioInteressatHelper;

	public Map<String, String> crear(
			DocumentNotificacioDto notificacioDto, 
			DocumentEntity documentEntity) {
		ExpedientEntity expedientEntity = validateExpedientPerNotificacio(documentEntity, 
				  notificacioDto.getTipus());
		
		if (!documentEntity.isArxiuEstatDefinitiu() && documentEntity.getDocumentTipus() != DocumentTipusEnumDto.VIRTUAL && !documentEntity.getFitxerContentType().equals("application/zip")) {
			documentHelper.actualitzarEstatADefinititu(documentEntity.getId());
		}

		Map<String, String> notificacionsWithError = new HashMap<>();
		for (Long interessatId : notificacioDto.getInteressatsIds()) {
			documentNotificacioInteressatHelper.crearEnviarNotificacioInteressat(
					notificacionsWithError,
					notificacioDto, 
					expedientEntity, 
					documentEntity, 
					interessatId);
		}
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
		
	public DocumentEnviamentInteressatEntity findDocumentEnviamentInteressatById(Long id) {
		return documentEnviamentInteressatRepository.findOne(id);
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
			if (provinciaCodi != null) {
				for (ProvinciaDto provinciaDto : dadesExternesService.findProvincies()) {
					if (provinciaDto.getCodi().equals(provinciaCodi)) {
						documentEnviamentInteressat.getInteressat().setProvinciaNom(provinciaDto.getNom());
					}
				}
				String municipiCodi = documentEnviamentInteressat.getInteressat().getMunicipi();
				if (municipiCodi != null) {
					for (MunicipiDto municipiDto : dadesExternesService.findMunicipisPerProvincia(provinciaCodi)) {
						if (municipiDto.getCodi().equals(municipiCodi)) {
							documentEnviamentInteressat.getInteressat().setMunicipiNom(municipiDto.getNom());
						}
					}
				}
			}
		}
		
		return documentNotificacioDto;
		
	}
	
	public void actualitzarEstat(DocumentEnviamentInteressatEntity documentEnviamentInteressatEntity) {

		pluginHelper.notificacioConsultarIActualitzarEstat(documentEnviamentInteressatEntity);

	}
		
	public byte[] getCertificacio(Long documentEnviamentInteressatId) {
		DocumentEnviamentInteressatEntity documentEnviamentInteressatEntity = documentEnviamentInteressatRepository.findOne(
				documentEnviamentInteressatId);
		return pluginHelper.notificacioConsultarIDescarregarCertificacio(documentEnviamentInteressatEntity);
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
		if (!document.getDocumentTipus().equals(DocumentTipusEnumDto.VIRTUAL) && !document.isArxiuEstatDefinitiu() && !document.getFitxerContentType().equals("application/zip")) {
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
	
}
