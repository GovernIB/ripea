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
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.TipusDocumentalDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.TipusDocumentalService;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.TipusDocumentalEntity;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.repository.TipusDocumentalRepository;

/**
 * Implementació del servei de gestió de meta-documents.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class TipusDocumentalServiceImpl implements TipusDocumentalService {

	@Autowired
	private TipusDocumentalRepository tipusDocumentalRepository;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;

	@Transactional
	@Override
	public TipusDocumentalDto create(
			Long entitatId,
			TipusDocumentalDto tipusDocumental) throws NotFoundException {
		logger.debug("Creant un nou tipus documental per l'entitat (" +
				"entitatId=" + entitatId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		TipusDocumentalEntity entity = TipusDocumentalEntity.getBuilder(
				tipusDocumental.getCodi(),
				tipusDocumental.getNom(),
				entitat).build();
		TipusDocumentalDto dto = conversioTipusHelper.convertir(
				tipusDocumentalRepository.save(entity),
				TipusDocumentalDto.class);
		return dto;
	}

	@Transactional
	@Override
	public TipusDocumentalDto update(
			Long entitatId,
			TipusDocumentalDto tipusDocumental) throws NotFoundException {
		logger.debug("Actualitzant el tipus documental per l'entitat (" +
				"entitatId=" + entitatId +
				"tipusDocumentalId=" + tipusDocumental.getId() + ")");

		TipusDocumentalEntity entity = tipusDocumentalRepository.findOne(tipusDocumental.getId());

		if (entity == null || !entity.getId().equals(tipusDocumental.getId())) {
			throw new NotFoundException(
					tipusDocumental.getId(),
					TipusDocumentalEntity.class);
		}

		entity.update(
				tipusDocumental.getCodi(),
				tipusDocumental.getNom());
		TipusDocumentalDto dto = conversioTipusHelper.convertir(
				entity,
				TipusDocumentalDto.class);
		return dto;
	}

	@Transactional
	@Override
	public TipusDocumentalDto delete(
			Long entitatId,
			Long id) throws NotFoundException {
		logger.debug("Esborrant el tipus documental per l'entitat (" +
				"entitatId=" + entitatId +
				"tipusDocumentalId=" + id + ")");

		TipusDocumentalEntity entity = tipusDocumentalRepository.findOne(id);

		tipusDocumentalRepository.delete(entity);

		TipusDocumentalDto dto = conversioTipusHelper.convertir(
				entity,
				TipusDocumentalDto.class);
		return dto;
	}

	@Transactional
	@Override
	public TipusDocumentalDto findById(Long entitatId, Long id) throws NotFoundException {
		logger.debug("Consultant el tipus documental per l'entitat (" +
				"entitatId=" + entitatId +
				"tipusDocumentalId=" + id + ")");
		TipusDocumentalEntity entity = tipusDocumentalRepository.findOne(id);

		TipusDocumentalDto dto = conversioTipusHelper.convertir(
				entity,
				TipusDocumentalDto.class);
		return dto;
	}

	@Transactional
	@Override
	public PaginaDto<TipusDocumentalDto> findByEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams) throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);

		Page<TipusDocumentalEntity> page = tipusDocumentalRepository.findByEntitat(
				entitat,
				paginacioHelper.toSpringDataPageable(paginacioParams));

		return paginacioHelper.toPaginaDto(
				page,
				TipusDocumentalDto.class);
	}

	@Override
	public List<TipusDocumentalDto> findByEntitat(Long entitatId) throws NotFoundException {

		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);

		List<TipusDocumentalEntity> tipusDocumentals = tipusDocumentalRepository.findByEntitatOrderByNomAsc(entitat);

		return conversioTipusHelper.convertirList(
				tipusDocumentals,
				TipusDocumentalDto.class);
	}

	@Override
	public TipusDocumentalDto findByCodiAndEntitat(String codi, Long entitatId) throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);

		TipusDocumentalEntity tipusDocumental = tipusDocumentalRepository.findByCodiAndEntitat(
				codi, 
				entitat);
		return conversioTipusHelper.convertir(tipusDocumental, TipusDocumentalDto.class);
	}
	private static final Logger logger = LoggerFactory.getLogger(TipusDocumentalServiceImpl.class);
}