package es.caib.ripea.service.service;

import es.caib.ripea.persistence.entity.AlertaEntity;
import es.caib.ripea.persistence.entity.ContingutEntity;
import es.caib.ripea.persistence.repository.AlertaRepository;
import es.caib.ripea.persistence.repository.ContingutRepository;
import es.caib.ripea.service.helper.AlertaHelper;
import es.caib.ripea.service.helper.ConversioTipusHelper;
import es.caib.ripea.service.helper.PaginacioHelper;
import es.caib.ripea.service.intf.dto.AlertaDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.AlertaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlertaServiceImpl implements AlertaService {

	@Autowired private ContingutRepository contingutRepository;
	@Autowired private AlertaRepository alertaRepository;
	@Autowired private AlertaHelper alertaHelper;
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private PaginacioHelper paginacioHelper;

	@Override
	@Transactional
	public AlertaDto create(AlertaDto alerta) {
		logger.debug("Creant una nova alerta (alerta=" + alerta + ")");
		AlertaEntity entity = alertaHelper.crearAlerta(
				alerta.getText(),
				alerta.getError(),
				alerta.isLlegida(),
				alerta.getContingutId());
		return conversioTipusHelper.convertir(
				alertaRepository.save(entity),
				AlertaDto.class);
	}

	@Override
	@Transactional
	public AlertaDto update(
			AlertaDto alerta) throws NotFoundException {
		logger.debug("Actualitzant alerta existent (alerta=" + alerta + ")");
		AlertaEntity entity = alertaRepository.findById(
				alerta.getId()).orElse(null);
		if (entity == null) {
			throw new NotFoundException(alerta.getId(), AlertaEntity.class);
		}
		ContingutEntity contingut = contingutRepository.getOne(
				alerta.getContingutId());
		entity.update(
				alerta.getText(),
				alerta.getError(),
				alerta.isLlegida());
		entity.updateContingut(
				contingut);
		return conversioTipusHelper.convertir(
				entity,
				AlertaDto.class);
	}

	@Override
	@Transactional
	public AlertaDto delete(
			Long id) throws NotFoundException {
		logger.debug("Esborrant alerta (id=" + id +  ")");
		AlertaEntity entity = alertaRepository.findById(id).orElse(null);
		if(entity == null ) throw new NotFoundException(id, AlertaEntity.class);
		alertaRepository.delete(entity);
		return conversioTipusHelper.convertir(
				entity,
				AlertaDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public AlertaDto find(
			Long id) {
		logger.debug("Cercant alerta (id=" + id +  ")");
		AlertaEntity entity = alertaRepository.getOne(id);
		return conversioTipusHelper.convertir(
				entity,
				AlertaDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<AlertaDto> findPaginat(
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta de totes les alertes paginades (paginacioParams=" + paginacioParams + ")");
		PaginaDto<AlertaDto> resposta;
		if (paginacioHelper.esPaginacioActivada(paginacioParams)) {
			resposta = paginacioHelper.toPaginaDto(
					alertaRepository.findAll(
							paginacioHelper.toSpringDataPageable(paginacioParams)),
					AlertaDto.class);
		} else {
			resposta = paginacioHelper.toPaginaDto(
					alertaRepository.findAll(
							paginacioHelper.toSpringDataSort(paginacioParams)),
					AlertaDto.class);
		}
		return resposta;
	}



	private static final Logger logger = LoggerFactory.getLogger(AlertaServiceImpl.class);

}
