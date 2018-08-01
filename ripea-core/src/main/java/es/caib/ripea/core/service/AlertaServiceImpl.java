/**
 * 
 */
package es.caib.ripea.core.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.AlertaDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.AlertaService;
import es.caib.ripea.core.entity.AlertaEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.helper.AlertaHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.repository.AlertaRepository;
import es.caib.ripea.core.repository.ContingutRepository;

/**
 * Implementació del servei de gestió d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class AlertaServiceImpl implements AlertaService {

	@Autowired
	private ContingutRepository contingutRepository;
	@Autowired
	private AlertaRepository alertaRepository;

	@Autowired
	private AlertaHelper alertaHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;



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
		AlertaEntity entity = alertaRepository.findOne(
				alerta.getId());
		if(entity == null ) throw new NotFoundException(alerta.getId(), AlertaEntity.class);
		ContingutEntity contingut = contingutRepository.findOne(
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
		AlertaEntity entity = alertaRepository.findOne(id);
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
		AlertaEntity entity = alertaRepository.findOne(id);
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

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<AlertaDto> findPaginatByLlegida(
			boolean llegida,
			Long contingutId,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta de totes les alertes paginades (paginacioParams=" + paginacioParams + ")");
		PaginaDto<AlertaDto> resposta;
		List<ContingutEntity> continguts = contingutRepository.findRegistresByPareId(contingutId);
		continguts.add(contingutRepository.findOne(contingutId));
		if (paginacioHelper.esPaginacioActivada(paginacioParams)) {	
			resposta = paginacioHelper.toPaginaDto(
					alertaRepository.findByLlegidaAndContinguts(
							llegida,
							continguts,
							paginacioHelper.toSpringDataPageable(paginacioParams)),
					AlertaDto.class);
		} else {
			resposta = paginacioHelper.toPaginaDto(
					alertaRepository.findByLlegidaAndContinguts(
							llegida,
							continguts,
							paginacioHelper.toSpringDataSort(paginacioParams)),
					AlertaDto.class);
		}
		return resposta;
	}

	private static final Logger logger = LoggerFactory.getLogger(AlertaServiceImpl.class);

}
