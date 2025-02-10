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
import es.caib.ripea.core.api.dto.URLInstruccioDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.URLInstruccioService;
import es.caib.ripea.core.persistence.ContingutEntity;
import es.caib.ripea.core.persistence.EntitatEntity;
import es.caib.ripea.core.persistence.ExpedientEntity;
import es.caib.ripea.core.persistence.URLInstruccioEntity;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.repository.URLInstruccioRepository;

/**
 * Implementació del servei de gestió de urls d'instrucció.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class URLInstruccioServiceImpl implements URLInstruccioService {

	@Autowired
	private URLInstruccioRepository urlInstruccionRepository;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	
	@Transactional
	@Override
	public URLInstruccioDto create(
			Long entitatId,
			URLInstruccioDto url) throws NotFoundException {
		logger.debug("Creant una nova url (" +
				"url=" + url + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId, 
				false, 
				true, 
				false, 
				false, 
				true);
		
		URLInstruccioEntity entity = URLInstruccioEntity.getBuilder(
				url.getCodi(), 
				url.getNom(), 
				url.getDescripcio(), 
				url.getUrl(),
				entitat).build();
		return conversioTipusHelper.convertir(
				urlInstruccionRepository.save(entity), 
				URLInstruccioDto.class);
	}

	@Transactional
	@Override
	public URLInstruccioDto update(
			Long entitatId, 
			URLInstruccioDto url) throws NotFoundException {
		logger.debug("Actualitzant url existent (" +
				"url=" + url + ")");

		URLInstruccioEntity entity = urlInstruccionRepository.findOne(url.getId());
		entity.update(
				url.getCodi(),
				url.getNom(),
				url.getDescripcio(),
				url.getUrl());
		return conversioTipusHelper.convertir(
				entity,
				URLInstruccioDto.class);
	}

	@Transactional
	@Override
	public URLInstruccioDto delete(Long entitatId, Long id) throws NotFoundException {
		logger.debug("Esborrant url (" +
				"id=" + id +  ")");
		
		URLInstruccioEntity entity = urlInstruccionRepository.findOne(id);
		urlInstruccionRepository.delete(entity);

		return conversioTipusHelper.convertir(
				entity,
				URLInstruccioDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public URLInstruccioDto findById(Long entitatId, Long id) throws NotFoundException {
		logger.debug("Consulta de la url (" +
				"id=" + id + ")");
		
		URLInstruccioEntity entity = urlInstruccionRepository.findOne(id);
		URLInstruccioDto dto = conversioTipusHelper.convertir(
				entity,
				URLInstruccioDto.class);
		return dto;
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<URLInstruccioDto> findByEntitatPaginat(
			Long entitatId, 
			URLInstruccioFiltreDto filtre,
			PaginacioParamsDto paginacioParams)
			throws NotFoundException {
		logger.debug("Consulta de totes les avisos paginades (" +
				"paginacioParams=" + paginacioParams + ")");
		PaginaDto<URLInstruccioDto> resposta;
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
				URLInstruccioDto.class);
		return resposta;
	}

	@Transactional(readOnly = true )
	@Override
	public List<URLInstruccioDto> findByEntitat(Long entitatId) throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false, false, false);
		
		return conversioTipusHelper.convertirList(
				urlInstruccionRepository.findByEntitat(entitat), 
				URLInstruccioDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public String getURLInstruccio(Long entitatId, Long contingutId, Long urlInstruccioId) {
		logger.debug("Generant URLs instrucció ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ", "
				+ "urlInstruccioId=" + urlInstruccioId + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false, false, false);
		
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				contingutId);
		String urlValor = "";
		if (contingut instanceof ExpedientEntity) {
			ExpedientEntity expedient = (ExpedientEntity)contingut;
		
			String eni = expedient.getNtiIdentificador();
			URLInstruccioEntity urlInstruccio = urlInstruccionRepository.findOne(urlInstruccioId);
			
			if (urlInstruccio != null) {
				String url = urlInstruccio.getUrl();
		 		if (url.contains("[ENI]") && eni != null && ! eni.isEmpty()) {
		 			urlValor = url.replaceAll("\\[ENI\\]", eni);
				}
			}
			
		}
		return urlValor;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(URLInstruccioServiceImpl.class);

	
}