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

import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.URLInstruccioFiltreDto;
import es.caib.ripea.core.api.dto.URLInstruccionDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.URLInstruccioService;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.URLInstruccionEntity;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.repository.URLInstruccionRepository;

/**
 * Implementació del servei de gestió de urls d'instrucció.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class URLInstruccioServiceImpl implements URLInstruccioService {

	@Autowired
	private URLInstruccionRepository urlInstruccionRepository;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	
	@Transactional
	@Override
	public URLInstruccionDto create(
			Long entitatId,
			URLInstruccionDto url) throws NotFoundException {
		logger.debug("Creant una nova url (" +
				"url=" + url + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId, 
				false, 
				true, 
				false, 
				false, 
				true);
		
		URLInstruccionEntity entity = URLInstruccionEntity.getBuilder(
				url.getCodi(), 
				url.getNom(), 
				url.getDescripcio(), 
				url.getUrl(),
				entitat).build();
		return conversioTipusHelper.convertir(
				urlInstruccionRepository.save(entity), 
				URLInstruccionDto.class);
	}

	@Transactional
	@Override
	public URLInstruccionDto update(
			Long entitatId, 
			URLInstruccionDto url) throws NotFoundException {
		logger.debug("Actualitzant url existent (" +
				"url=" + url + ")");

		URLInstruccionEntity entity = urlInstruccionRepository.findOne(url.getId());
		entity.update(
				url.getCodi(),
				url.getNom(),
				url.getDescripcio(),
				url.getUrl());
		return conversioTipusHelper.convertir(
				entity,
				URLInstruccionDto.class);
	}

	@Transactional
	@Override
	public URLInstruccionDto delete(Long entitatId, Long id) throws NotFoundException {
		logger.debug("Esborrant url (" +
				"id=" + id +  ")");
		
		URLInstruccionEntity entity = urlInstruccionRepository.findOne(id);
		urlInstruccionRepository.delete(entity);

		return conversioTipusHelper.convertir(
				entity,
				URLInstruccionDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public URLInstruccionDto findById(Long entitatId, Long id) throws NotFoundException {
		logger.debug("Consulta de la url (" +
				"id=" + id + ")");
		
		URLInstruccionEntity entity = urlInstruccionRepository.findOne(id);
		URLInstruccionDto dto = conversioTipusHelper.convertir(
				entity,
				URLInstruccionDto.class);
		return dto;
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<URLInstruccionDto> findByEntitatPaginat(
			Long entitatId, 
			URLInstruccioFiltreDto filtre,
			PaginacioParamsDto paginacioParams)
			throws NotFoundException {
		logger.debug("Consulta de totes les avisos paginades (" +
				"paginacioParams=" + paginacioParams + ")");
		PaginaDto<URLInstruccionDto> resposta;
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId, 
				false, 
				true, 
				false, 
				false, 
				true);
		resposta = paginacioHelper.toPaginaDto(
				urlInstruccionRepository.findByEntitat(
						entitat, 
						filtre.getCodi() == null || filtre.getCodi().isEmpty(),
						filtre.getCodi() != null ? filtre.getCodi().trim() : "",
						filtre.getNom() == null || filtre.getNom().isEmpty(),
						filtre.getNom() != null ? filtre.getNom().trim() : "",
						filtre.getDescripcio() == null || filtre.getDescripcio().isEmpty(),
						filtre.getDescripcio() != null ? filtre.getDescripcio().trim() : "",
						paginacioHelper.toSpringDataPageable(paginacioParams)),
				URLInstruccionDto.class);
		return resposta;
	}

	@Transactional(readOnly = true )
	@Override
	public List<URLInstruccionDto> findByEntitat(Long entitatId) throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId, 
				false, 
				true, 
				false, 
				false, 
				true);
		
		return conversioTipusHelper.convertirList(
				urlInstruccionRepository.findByEntitat(entitat), 
				URLInstruccionDto.class);
	}

	private static final Logger logger = LoggerFactory.getLogger(URLInstruccioServiceImpl.class);
	
}