/**
 * 
 */
package es.caib.ripea.core.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Persistable;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.ContingutLogDetallsDto;
import es.caib.ripea.core.api.dto.ContingutLogDto;
import es.caib.ripea.core.api.dto.ContingutMovimentDto;
import es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.entity.CarpetaEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.ContingutLogEntity;
import es.caib.ripea.core.entity.ContingutLogEntity.Builder;
import es.caib.ripea.core.entity.ContingutMovimentEntity;
import es.caib.ripea.core.entity.DadaEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentNotificacioEntity;
import es.caib.ripea.core.entity.DocumentPublicacioEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.InteressatEntity;
import es.caib.ripea.core.entity.RegistreEntity;
import es.caib.ripea.core.repository.ContingutLogRepository;
import es.caib.ripea.core.repository.ContingutMovimentRepository;
import es.caib.ripea.core.repository.ContingutRepository;
import es.caib.ripea.core.repository.DadaRepository;
import es.caib.ripea.core.repository.DocumentNotificacioRepository;
import es.caib.ripea.core.repository.DocumentPublicacioRepository;
import es.caib.ripea.core.repository.InteressatRepository;

/**
 * Utilitat per a gestionar el registre d'accions dels contenidors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ContingutLogHelper {

	@Resource
	private ContingutRepository contingutRepository;
	@Resource
	private DadaRepository dadaRepository;
	@Resource
	private InteressatRepository interessatRepository;
	@Resource
	private DocumentNotificacioRepository documentNotificacioRepository;
	@Resource
	private DocumentPublicacioRepository documentPublicacioRepository;
	@Resource
	private ContingutLogRepository contingutLogRepository;
	@Resource
	private ContingutMovimentRepository contingutMovimentRepository;

	@Resource
	private ContingutHelper contenidorHelper;
	@Resource
	private UsuariHelper usuariHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;



	public ContingutLogEntity logCreacio(
			ContingutEntity contingut,
			boolean logContingutPare,
			boolean logExpedientSuperior) {
		return log(
				contingut,
				LogTipusEnumDto.CREACIO,
				null,
				contingut,
				getLogObjecteTipusPerContingut(contingut),
				null,
				contingut.getNom(),
				(contingut.getPare() != null) ? contingut.getPare().getId().toString() : null,
				logContingutPare,
				logExpedientSuperior);
	}

	public ContingutLogEntity log(
			ContingutEntity contingut,
			LogTipusEnumDto tipus,
			String param1,
			String param2,
			boolean logContingutPare,
			boolean logExpedientSuperior) {
		return log(
				contingut,
				tipus,
				null,
				null,
				null,
				null,
				param1,
				param2,
				logContingutPare,
				logExpedientSuperior);
	}
	public ContingutLogEntity log(
			ContingutEntity contingut,
			LogTipusEnumDto tipus,
			Persistable<? extends Serializable> objecte,
			LogObjecteTipusEnumDto objecteTipus,
			LogTipusEnumDto objecteLogTipus,
			String param1,
			String param2,
			boolean logContingutPare,
			boolean logExpedientSuperior) {
		return log(
				contingut,
				tipus,
				null,
				objecte,
				objecteTipus,
				objecteLogTipus,
				param1,
				param2,
				logContingutPare,
				logExpedientSuperior);
	}
	public ContingutLogEntity log(
			ContingutEntity contingut,
			LogTipusEnumDto tipus,
			ContingutMovimentEntity contingutMoviment,
			boolean logContingutPare,
			boolean logExpedientSuperior) {
		return log(
				contingut,
				tipus,
				contingutMoviment,
				null,
				null,
				null,
				null,
				null,
				logContingutPare,
				logExpedientSuperior);
	}

	public List<ContingutLogDto> findLogsContingut(
			ContingutEntity contingut) {
		List<ContingutLogEntity> logs = contingutLogRepository.findByContingutIdOrderByCreatedDateAsc(
				contingut.getId());
		List<ContingutLogDto> dtos = new ArrayList<ContingutLogDto>();
		for (ContingutLogEntity log: logs) {
			ContingutLogDto dto = new ContingutLogDto();
			emplenarLogDto(log, dto);
			dtos.add(dto);
		}
		return dtos;
	}

	public ContingutLogDetallsDto findLogDetalls(
			Long contingutId,
			Long contingutLogId) {
		ContingutLogEntity log = contingutLogRepository.findOne(contingutLogId);
		if (!log.getContingutId().equals(contingutId)) {
			throw new ValidationException(
					contingutLogId,
					ContingutLogEntity.class,
					"El contingut del log (id=" + log.getContingutId() + ") no coincideix amb el contingut (id=" + contingutId + ") expecificat");
		}
		ContingutLogDetallsDto detalls = new ContingutLogDetallsDto();
		emplenarLogDto(log, detalls);
		if (log.getContingutMoviment() != null) {
			detalls.setContingutMoviment(
					toContingutMovimentDto(
							log.getContingutMoviment(),
							log.getContingutMoviment().getContingutId()));
		}
		if (log.getPare() != null) {
			ContingutLogDto pare = new ContingutLogDto();
			emplenarLogDto(log.getPare(), pare);
			detalls.setPare(pare);
		}
//		if (log.getObjecteId() != null) {
//			String objecteNom = null;
//			switch (log.getObjecteTipus()) {
//			case ContingutId:
//			case CARPETA:
//			case DOCUMENT:
//			case EXPEDIENT:
//			case REGISTRE:
//				ContingutEntity c = contingutRepository.findOne(
//						new Long(log.getObjecteId()));
//				objecteNom = c.getNom();
//				break;
//			case DADA:
//				DadaEntity dada = dadaRepository.findOne(
//						new Long(log.getObjecteId()));
//				objecteNom = dada.getMetaDada().getNom();
//				break;
//			case INTERESSAT:
//				InteressatEntity interessat = interessatRepository.findOne(
//						new Long(log.getObjecteId()));
//				objecteNom = interessat.getIdentificador();
//				break;
//			case NOTIFICACIO:
//				DocumentNotificacioEntity notificacio = documentNotificacioRepository.findOne(
//						new Long(log.getObjecteId()));
//				objecteNom = notificacio.getAssumpte();
//				break;
//			case PUBLICACIO:
//				DocumentPublicacioEntity publicacio = documentPublicacioRepository.findOne(
//						new Long(log.getObjecteId()));
//				objecteNom = publicacio.getAssumpte();
//				break;
//			case RELACIO:
//				String[] ids = log.getObjecteId().split("#");
//				if (ids.length >= 2) {
//					ContingutEntity exp1 = contingutRepository.findOne(
//							new Long(ids[0]));
//					ContingutEntity exp2 = contingutRepository.findOne(
//							new Long(ids[1]));
//					objecteNom = exp1.getNom() + " <-> " + exp2.getNom();
//					break;
//				}
//			case ALTRES:
//			default:
//				objecteNom = "???" + log.getObjecteTipus().name() + "#" + log.getObjecteId() + "???";
//				break;
//			}
//			detalls.setObjecteNom(objecteNom);
//		}
		return detalls;
	}

	public List<ContingutMovimentDto> findMovimentsContingut(
			ContingutEntity contingut) {
		List<ContingutMovimentEntity> moviments = contingutMovimentRepository.findByContingutIdOrderByCreatedDateAsc(
				contingut.getId());

		List<ContingutMovimentDto> dtos = new ArrayList<ContingutMovimentDto>();
		for (ContingutMovimentEntity moviment: moviments) {
			dtos.add(
					toContingutMovimentDto(
							moviment,
							contingut.getId()));
		}
		return dtos;
	}



	private ContingutLogEntity log(
			ContingutEntity contingut,
			LogTipusEnumDto tipus,
			ContingutMovimentEntity contingutMoviment,
			Persistable<? extends Serializable> objecte,
			LogObjecteTipusEnumDto objecteTipus,
			LogTipusEnumDto objecteLogTipus,
			String param1,
			String param2,
			boolean logContingutPare,
			boolean logExpedientSuperior) {
		ContingutLogEntity logPare = logSave(
				contingut.getId(),
				tipus,
				null,
				contingutMoviment,
				objecte,
				objecteTipus,
				objecteLogTipus,
				param1,
				param2);
		if (logContingutPare) {
			if (contingutMoviment == null) {
				if (contingut.getPare() != null) {
					logContingutSuperior(
							contingut,
							tipus,
							contingut.getPare().getId(),
							logPare);
				}
			} else {
				if (contingutMoviment.getOrigenId() != null) {
					logContingutSuperior(
							contingut,
							tipus,
							contingutMoviment.getOrigenId(),
							logPare);
				}
				if (contingutMoviment.getDestiId() != null) {
					logContingutSuperior(
							contingut,
							tipus,
							contingutMoviment.getDestiId(),
							logPare);
				}
			}
		}
		if (logExpedientSuperior) {
			// Si el pare és l'expedient superior i logContingutPare == true
			// ja no cream mes logs
			if (	!logContingutPare ||
					contingut.getPare() == null ||
					!(contingut.getPare() instanceof ExpedientEntity)) {
				if (contingutMoviment == null) {
					logExpedientSuperior(
						contingut,
						tipus,
						contingut.getId(),
						logPare);
				} else {
					if (contingutMoviment.getOrigenId() != null) {
						logExpedientSuperior(
								contingut,
								tipus,
								contingutMoviment.getOrigenId(),
								logPare);
					}
					if (contingutMoviment.getDestiId() != null) {
						logExpedientSuperior(
								contingut,
								tipus,
								contingutMoviment.getDestiId(),
								logPare);
					}
				}
			}
		}
		return logPare;
	}

	private void logExpedientSuperior(
			ContingutEntity contingut,
			LogTipusEnumDto tipus,
			Long contingutSuperiorId,
			ContingutLogEntity contingutLogPare) {
//		ExpedientEntity expedientSuperior = contenidorHelper.getExpedientSuperior(
//				contingutSuperior,
//				false,
//				false,
//				false);
		if (contingutSuperiorId != null) {
			logContingutSuperior(
					contingut,
					tipus,
					contingutSuperiorId,
					contingutLogPare);
		}
	}
	private void logContingutSuperior(
			ContingutEntity contingut,
			LogTipusEnumDto tipus,
			Long contingutSuperiorId,
			ContingutLogEntity contingutLogPare) {
		logSave(
				contingutSuperiorId,
				LogTipusEnumDto.MODIFICACIO,
				contingutLogPare,
				null,
				contingut,
				getLogObjecteTipusPerContingut(contingut),
				tipus,
				null,
				null);
	}

	private ContingutLogEntity logSave(
			Long contingutId,
			LogTipusEnumDto tipus,
			ContingutLogEntity pare,
			ContingutMovimentEntity contingutMoviment,
			Persistable<? extends Serializable> objecte,
			LogObjecteTipusEnumDto objecteTipus,
			LogTipusEnumDto objecteLogTipus,
			String param1,
			String param2) {
		logger.debug("Guardant log per contenidor (" +
				"contingutId=" + contingutId + ", " +
				"tipus=" + tipus + ", " +
				"logPareId=" + ((pare != null) ? pare.getId() : null) + ", " +
				"contingutMovimentId=" + ((contingutMoviment != null) ? contingutMoviment.getId() : null) + ", " +
				"objecte=" + ((objecte != null) ? objecte.getId() : "null") + ", " +
				"objecteLogTipus=" + ((objecteLogTipus != null) ? objecteLogTipus.name() : "null") + ", " +
				"param1=" + param1 + ", " +
				"param2=" + param2 + ")");
		Builder logBuilder = ContingutLogEntity.getBuilder(
				tipus,
				contingutId).
				param1(param1).
				param2(param2).
				pare(pare).
				contingutMoviment(contingutMoviment);
		if (objecte != null) {
			logBuilder.
			objecte(objecte).
			objecteTipus(objecteTipus).
			objecteLogTipus(objecteLogTipus);
		}
		return contingutLogRepository.save(
				logBuilder.build());
	}

	private void emplenarLogDto(
			ContingutLogEntity log,
			ContingutLogDto dto) {
		dto.setId(log.getId());
		if (log.getCreatedDate() != null)
			dto.setCreatedDate(log.getCreatedDate().toDate());
		dto.setCreatedBy(
				conversioTipusHelper.convertir(
						log.getCreatedBy(),
						UsuariDto.class));
		if (log.getLastModifiedDate() != null)
			dto.setLastModifiedDate(log.getLastModifiedDate().toDate());
		dto.setLastModifiedBy(
				conversioTipusHelper.convertir(
						log.getLastModifiedBy(),
						UsuariDto.class));
		dto.setTipus(
				LogTipusEnumDto.valueOf(
						log.getTipus().name()));
		if (log.getObjecteId() != null) {
			dto.setObjecteId(log.getObjecteId());
			dto.setObjecteTipus(
					LogObjecteTipusEnumDto.valueOf(
							log.getObjecteTipus().name()));
			if (log.getObjecteLogTipus() != null) {
				dto.setObjecteLogTipus(
						LogTipusEnumDto.valueOf(
								log.getObjecteLogTipus().name()));
			}
		}
		dto.setParam1(log.getParam1());
		dto.setParam2(log.getParam2());
	}

	private ContingutMovimentDto toContingutMovimentDto(
			ContingutMovimentEntity moviment,
			Long contingutId) {
		ContingutMovimentDto dto = new ContingutMovimentDto();
		dto.setId(moviment.getId());
		if (moviment.getCreatedDate() != null)
			dto.setData(moviment.getCreatedDate().toDate());
		dto.setComentari(moviment.getComentari());
		dto.setContingutId(contingutId);
		dto.setRemitent(
				conversioTipusHelper.convertir(
						moviment.getRemitent(),
						UsuariDto.class));
		if (moviment.getOrigenId() != null) {
			dto.setOrigenId(
					moviment.getOrigenId());
		}
		dto.setDestiId(
				moviment.getDestiId());
		return dto;
	}

	private LogObjecteTipusEnumDto getLogObjecteTipusPerContingut(
			ContingutEntity contingut) {
		LogObjecteTipusEnumDto objecteTipus;
		if (contingut instanceof ExpedientEntity) {
			objecteTipus = LogObjecteTipusEnumDto.EXPEDIENT;
		} else if (contingut instanceof DocumentEntity) {
			objecteTipus = LogObjecteTipusEnumDto.DOCUMENT;
		} else if (contingut instanceof CarpetaEntity) {
			objecteTipus = LogObjecteTipusEnumDto.CARPETA;
		} else if (contingut instanceof RegistreEntity) {
			objecteTipus = LogObjecteTipusEnumDto.REGISTRE;
		} else {
			objecteTipus = LogObjecteTipusEnumDto.CONTINGUT;
		}
		return objecteTipus;
	}

	private static final Logger logger = LoggerFactory.getLogger(ContingutLogHelper.class);

}
