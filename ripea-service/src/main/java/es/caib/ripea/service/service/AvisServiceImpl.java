package es.caib.ripea.service.service;

import es.caib.ripea.persistence.entity.AvisEntity;
import es.caib.ripea.persistence.repository.AvisRepository;
import es.caib.ripea.service.helper.ConversioTipusHelper;
import es.caib.ripea.service.helper.PaginacioHelper;
import es.caib.ripea.service.intf.dto.AvisDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.service.AvisService;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class AvisServiceImpl implements AvisService {

	@Autowired private AvisRepository avisRepository;
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private PaginacioHelper paginacioHelper;

	@Transactional
	@Override
	public AvisDto create(AvisDto avis) {
		logger.debug("Creant una nova avis (" +
				"avis=" + avis + ")");
		AvisEntity entity = AvisEntity.getBuilder(
				avis.getAssumpte(),
				avis.getMissatge(),
				avis.getDataInici(),
				avis.getDataFinal(),
				avis.getAvisNivell(),
				avis.getAvisAdministrador(),
				avis.getEntitatId()).build();
		return conversioTipusHelper.convertir(
				avisRepository.save(entity),
				AvisDto.class);
	}

	@Transactional
	@Override
	public AvisDto update(
			AvisDto avis) {
		logger.debug("Actualitzant avis existent (" +
				"avis=" + avis + ")");

		AvisEntity avisEntity = avisRepository.getOne(avis.getId());
		avisEntity.update(
				avis.getAssumpte(),
				avis.getMissatge(),
				avis.getDataInici(),
				avis.getDataFinal(),
				avis.getAvisNivell());
		return conversioTipusHelper.convertir(
				avisEntity,
				AvisDto.class);
	}

	@Transactional
	@Override
	public AvisDto updateActiva(
			Long id,
			boolean activa) {
		logger.debug("Actualitzant propietat activa d'una avis existent (" +
				"id=" + id + ", " +
				"activa=" + activa + ")");
		AvisEntity avisEntity = avisRepository.getOne(id);
		avisEntity.updateActiva(activa);
		return conversioTipusHelper.convertir(
				avisEntity,
				AvisDto.class);
	}

	@Transactional
	@Override
	public AvisDto delete(
			Long id) {
		logger.debug("Esborrant avis (" +
				"id=" + id +  ")");
		
		AvisEntity avisEntity = avisRepository.getOne(id);
		avisRepository.delete(avisEntity);

		return conversioTipusHelper.convertir(
				avisEntity,
				AvisDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public AvisDto findById(Long id) {
		logger.debug("Consulta de l'avis (" +
				"id=" + id + ")");
		
		AvisEntity avisEntity = avisRepository.getOne(id);
		AvisDto dto = conversioTipusHelper.convertir(
				avisEntity,
				AvisDto.class);
		return dto;
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public PaginaDto<AvisDto> findPaginat(PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta de totes les avisos paginades (" +
				"paginacioParams=" + paginacioParams + ")");
		PaginaDto<AvisDto> resposta;
		if (paginacioHelper.esPaginacioActivada(paginacioParams)) {
			resposta = paginacioHelper.toPaginaDto(
					avisRepository.findAll(
							paginacioHelper.toSpringDataPageable(paginacioParams)),
					AvisDto.class);
		} else {
			resposta = paginacioHelper.toPaginaDto(
					avisRepository.findAll(
							paginacioHelper.toSpringDataSort(paginacioParams)),
					AvisDto.class);
		}

		return resposta;
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public List<AvisDto> findActive() {
		return conversioTipusHelper.convertirList(
				avisRepository.findActive(DateUtils.truncate(new Date(), Calendar.DATE)), 
				AvisDto.class);
	}

	@Override
	public List<AvisDto> findActiveAdmin(Long entitatId) {
		return conversioTipusHelper.convertirList(
				avisRepository.findActiveAdmin(DateUtils.truncate(new Date(), Calendar.DATE), entitatId),
				AvisDto.class);
	}

	private static final Logger logger = LoggerFactory.getLogger(AvisServiceImpl.class);

}
