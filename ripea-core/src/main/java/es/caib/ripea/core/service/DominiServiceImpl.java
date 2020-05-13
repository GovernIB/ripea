/**
 *
 */
package es.caib.ripea.core.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import es.caib.ripea.core.api.dto.DominiDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.DominiService;
import es.caib.ripea.core.entity.DominiEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.repository.DominiRepository;

/**
 * Implementació del servei de gestió de meta-documents.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class DominiServiceImpl implements DominiService {

	@Autowired
	private DominiRepository dominiRepository;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;

	@Override
	public DominiDto create(
			Long entitatId,
			DominiDto domini) throws NotFoundException {
		logger.debug("Creant un nou domini per l'entitat (" +
				"entitatId=" + entitatId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		DominiEntity entity = DominiEntity.getBuilder(
				domini.getCodi(),
				domini.getNom(),
				domini.getDescripcio(),
				domini.getConsulta(),
				domini.getCadena(),
				domini.getUsuari(),
				domini.getContrasenya(),
				entitat).build();
		DominiDto dominiDto = conversioTipusHelper.convertir(
				dominiRepository.save(entity),
				DominiDto.class);
		return dominiDto;
	}

	@Override
	public DominiDto update(
			Long entitatId, 
			DominiDto domini) throws NotFoundException {
		logger.debug("Actualitzant el domini per l'entitat (" +
				"entitatId=" + entitatId +
				"dominiId=" + domini.getId() + ")");

		DominiEntity entity = dominiRepository.findOne(domini.getId());

		if (entity == null || !entity.getId().equals(domini.getId())) {
			throw new NotFoundException(
					domini.getId(),
					DominiEntity.class);
		}

		entity.update(
				domini.getCodi(),
				domini.getNom(),
				domini.getDescripcio(),
				domini.getConsulta(),
				domini.getCadena(),
				domini.getUsuari(),
				domini.getContrasenya());
		DominiDto dominiDto = conversioTipusHelper.convertir(
				entity,
				DominiDto.class);
		return dominiDto;
	}

	@Override
	public DominiDto delete(
			Long entitatId, 
			Long id) throws NotFoundException {
		logger.debug("Esborrant el tipus documental per l'entitat (" +
				"entitatId=" + entitatId +
				"dominiId=" + id + ")");

		DominiEntity entity = dominiRepository.findOne(id);

		dominiRepository.delete(entity);

		DominiDto dominiDto = conversioTipusHelper.convertir(
				entity,
				DominiDto.class);
		return dominiDto;
	}

	@Override
	public DominiDto findById(
			Long entitatId, 
			Long id) throws NotFoundException {
		logger.debug("Consultant el domini per l'entitat (" +
				"entitatId=" + entitatId +
				"dominiId=" + id + ")");
		DominiEntity entity = dominiRepository.findOne(id);

		DominiDto dominiDto = conversioTipusHelper.convertir(
				entity,
				DominiDto.class);
		return dominiDto;
	}

	@Override
	public PaginaDto<DominiDto> findByEntitatPaginat(Long entitatId, PaginacioParamsDto paginacioParams)
			throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);

		Page<DominiEntity> page = dominiRepository.findByEntitat(
				entitat,
				paginacioHelper.toSpringDataPageable(paginacioParams));

		PaginaDto<DominiDto> dominiDto = paginacioHelper.toPaginaDto(
				page,
				DominiDto.class);
		return dominiDto;
	}

	@Override
	public List<DominiDto> findByEntitat(Long entitatId) throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);

		List<DominiEntity> tipusDocumentals = dominiRepository.findByEntitatOrderByNomAsc(entitat);

		List<DominiDto> dominisDto = conversioTipusHelper.convertirList(
				tipusDocumentals,
				DominiDto.class);
		return dominisDto;
	}

	@Override
	public DominiDto findByCodiAndEntitat(String codi, Long entitatId) throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);

		DominiEntity tipusDocumental = dominiRepository.findByCodiAndEntitat(
				codi, 
				entitat);
		DominiDto dominiDto = conversioTipusHelper.convertir(
				tipusDocumental, 
				DominiDto.class);
		return dominiDto;
	}

	private static final Logger logger = LoggerFactory.getLogger(DominiServiceImpl.class);

}