package es.caib.ripea.core.helper;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.DocumentNotificacioDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.persistence.DocumentEntity;
import es.caib.ripea.core.persistence.DocumentEnviamentInteressatEntity;
import es.caib.ripea.core.persistence.DocumentNotificacioEntity;
import es.caib.ripea.core.persistence.ExpedientEntity;
import es.caib.ripea.core.persistence.InteressatEntity;
import es.caib.ripea.core.repository.DocumentEnviamentInteressatRepository;
import es.caib.ripea.core.repository.DocumentNotificacioRepository;
import es.caib.ripea.plugin.notificacio.EnviamentReferencia;
import es.caib.ripea.plugin.notificacio.RespostaEnviar;

/**
 * Utilitat per gestionar l'enviament de notificacions dels documents
 * d'expedients per interessat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class DocumentNotificacioInteressatHelper {
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private DocumentNotificacioRepository documentNotificacioRepository;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private DocumentEnviamentInteressatRepository documentEnviamentInteressatRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private ContingutLogHelper contingutLogHelper;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void crearEnviarNotificacioInteressat(
			Map<String, String> notificacionsWithError, 
			DocumentNotificacioDto notificacioDto, 
			ExpedientEntity expedientEntity,
			DocumentEntity documentEntity, 
			Long interessatId) {
		InteressatEntity interessat = entityComprovarHelper.comprovarInteressat(expedientEntity, interessatId);
		notificacioDto.setServeiTipusEnum(notificacioDto.getServeiTipusEnum());
		notificacioDto.setEntregaPostal(notificacioDto.isEntregaPostal());
		//#1545 NotibRepostaException [1042] Els salts de linia no estan permesos al camp descripció (observacions del notificacioDto)
		if (notificacioDto.getObservacions()!=null) {
			notificacioDto.setObservacions(notificacioDto.getObservacions().replaceAll("\\r?\\n", " "));
		}

		RespostaEnviar respostaEnviar = pluginHelper.notificacioEnviar(
				notificacioDto, 
				expedientEntity, 
				documentEntity,
				interessat);

		DocumentNotificacioEntity notificacioEntity = DocumentNotificacioEntity
				.getBuilder(
						DocumentNotificacioEstatEnumDto.PENDENT, 
						notificacioDto.getAssumpte(),
						notificacioDto.getTipus(), 
						notificacioDto.getDataProgramada(), 
						notificacioDto.getRetard(),
						notificacioDto.getDataCaducitat(), 
						expedientEntity, 
						documentEntity,
						notificacioDto.getServeiTipusEnum(), 
						notificacioDto.isEntregaPostal())
				.observacions(notificacioDto.getObservacions())
				.emisor(documentEntity.getExpedient() != null ? documentEntity.getExpedient().getOrganGestor() : null)
				.build();

		documentNotificacioRepository.save(notificacioEntity);

		DocumentEnviamentInteressatEntity documentEnviamentInteressatEntity;
		documentEnviamentInteressatEntity = DocumentEnviamentInteressatEntity.getBuilder(
				interessat, 
				notificacioEntity).build();
		documentEnviamentInteressatRepository.save(documentEnviamentInteressatEntity);

		if (respostaEnviar.isError()) {
			cacheHelper.evictNotificacionsAmbErrorPerExpedient(expedientEntity);
			notificacioEntity.updateEnviatError(respostaEnviar.getErrorDescripcio(), respostaEnviar.getIdentificador());

			notificacionsWithError.put(interessat.getDocumentNum(), respostaEnviar.getErrorDescripcio());
		} else {
			cacheHelper.evictErrorsValidacioPerNode(expedientEntity);
			cacheHelper.evictNotificacionsPendentsPerExpedient(expedientEntity);
			notificacioEntity.updateEnviat(null, respostaEnviar.getEstat(), respostaEnviar.getIdentificador());
		}

		for (EnviamentReferencia enviamentReferencia : respostaEnviar.getReferencies()) {
			for (DocumentEnviamentInteressatEntity documentEnviamentInteressat : notificacioEntity.getDocumentEnviamentInteressats()) {
				if (documentEnviamentInteressat.getInteressat().getDocumentNum().equals(enviamentReferencia.getTitularNif())) {
					documentEnviamentInteressat.updateEnviamentReferencia(enviamentReferencia.getReferencia());
				}
			}
		}

		DocumentNotificacioDto dto = conversioTipusHelper.convertir(notificacioEntity, DocumentNotificacioDto.class);

		String destinitariAmbDocument = "";
		for (InteressatDto interessatDto : dto.getInteressats()) {
			destinitariAmbDocument += interessatDto.getNomCompletAmbDocument();
		}
		cacheHelper.evictErrorsValidacioPerNode(expedientEntity);
		cacheHelper.evictNotificacionsPendentsPerExpedient(expedientEntity);
		logAll(notificacioEntity, LogTipusEnumDto.NOTIFICACIO_ENVIADA, destinitariAmbDocument);
	}

	private void logAll(DocumentNotificacioEntity notificacioEntity, LogTipusEnumDto tipusLog, String param1) {
		logAll(notificacioEntity, tipusLog, param1, notificacioEntity.getAssumpte());
	}

	private void logAll(DocumentNotificacioEntity notificacioEntity, LogTipusEnumDto tipusLog, String param1,
			String param2) {
		contingutLogHelper.log(notificacioEntity.getDocument(), LogTipusEnumDto.MODIFICACIO, notificacioEntity,
				LogObjecteTipusEnumDto.NOTIFICACIO, tipusLog, param1, param2, false, false);
		contingutLogHelper.log(notificacioEntity.getDocument().getExpedient(), LogTipusEnumDto.MODIFICACIO,
				notificacioEntity, LogObjecteTipusEnumDto.NOTIFICACIO, tipusLog, param1, param2, false, false);

	}
}
