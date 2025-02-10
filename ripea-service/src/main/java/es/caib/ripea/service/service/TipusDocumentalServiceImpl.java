/**
 *
 */
package es.caib.ripea.service.service;

import es.caib.ripea.core.persistence.entity.EntitatEntity;
import es.caib.ripea.core.persistence.entity.TipusDocumentalEntity;
import es.caib.ripea.core.persistence.repository.TipusDocumentalRepository;
import es.caib.ripea.service.helper.*;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.TipusDocumentalDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.TipusDocumentalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private ConfigHelper configHelper;
	
	
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
				false, false, false);
		
		TipusDocumentalEntity entity = TipusDocumentalEntity.getBuilder(
				tipusDocumental.getCodi(),
				tipusDocumental.getNomEspanyol(),
				entitat,
				tipusDocumental.getNomCatala(), 
				tipusDocumental.getCodiEspecific()).build();
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
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false, false, false);
		
		TipusDocumentalEntity tipusDocumentalEntity = tipusDocumentalRepository.getOne(tipusDocumental.getId());

		if (tipusDocumentalEntity == null || !tipusDocumentalEntity.getId().equals(tipusDocumental.getId())) {
			throw new NotFoundException(
					tipusDocumental.getId(),
					TipusDocumentalEntity.class);
		}

		tipusDocumentalEntity.update(
				tipusDocumental.getCodi(),
				tipusDocumental.getNomEspanyol(), 
				tipusDocumental.getNomCatala(), 
				tipusDocumental.getCodiEspecific());
		TipusDocumentalDto dto = conversioTipusHelper.convertir(
				tipusDocumentalEntity,
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
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false, false, false);
		
		TipusDocumentalEntity tipusDocumentalEntity = tipusDocumentalRepository.getOne(id);

		tipusDocumentalRepository.delete(tipusDocumentalEntity);

		TipusDocumentalDto dto = conversioTipusHelper.convertir(
				tipusDocumentalEntity,
				TipusDocumentalDto.class);
		return dto;
	}

	@Transactional
	@Override
	public TipusDocumentalDto findById(Long entitatId, Long id) throws NotFoundException {
		logger.debug("Consultant el tipus documental per l'entitat (" +
				"entitatId=" + entitatId +
				"tipusDocumentalId=" + id + ")");
		TipusDocumentalEntity entity = tipusDocumentalRepository.getOne(id);

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
				false, false, false);

		Page<TipusDocumentalEntity> page = tipusDocumentalRepository.findByEntitat(
				entitat,
				paginacioParams.getFiltre() == null,
				paginacioParams.getFiltre() != null ? paginacioParams.getFiltre() : "",
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
				false,
				false, 
				true, false);

		List<TipusDocumentalEntity> tipusDocumentalsEntity = tipusDocumentalRepository.findByEntitatOrderByNomEspanyolAsc(entitat);
		List<TipusDocumentalDto> tipusDocumentalsDto =  conversioTipusHelper.convertirList(
				tipusDocumentalsEntity,
				TipusDocumentalDto.class);
		
		List<TipusDocumentalDto> docsAddicionals = pluginHelper.documentTipusAddicionals();
		
		if (docsAddicionals != null  && !docsAddicionals.isEmpty()) {
			tipusDocumentalsDto.addAll(docsAddicionals);
		}

		return tipusDocumentalsDto;
	}

	@Override
	public TipusDocumentalDto findByCodiAndEntitat(String codi, Long entitatId) throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false, false, false);

		TipusDocumentalEntity tipusDocumental = tipusDocumentalRepository.findByCodiAndEntitat(
				codi, 
				entitat);
		return conversioTipusHelper.convertir(tipusDocumental, TipusDocumentalDto.class);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(TipusDocumentalServiceImpl.class);

}