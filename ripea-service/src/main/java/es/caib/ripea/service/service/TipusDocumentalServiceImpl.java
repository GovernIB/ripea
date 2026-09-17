package es.caib.ripea.service.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.TipusDocumentalEntity;
import es.caib.ripea.persistence.repository.TipusDocumentalRepository;
import es.caib.ripea.service.helper.ConversioTipusHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.MessageHelper;
import es.caib.ripea.service.helper.PaginacioHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.helper.TipusDocumentalHelper;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.TipusDocumentalDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.TipusDocumentalService;

@Service
public class TipusDocumentalServiceImpl implements TipusDocumentalService {

	@Autowired private TipusDocumentalRepository tipusDocumentalRepository;
	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private PaginacioHelper paginacioHelper;
	@Autowired private PluginHelper pluginHelper;
	@Autowired private TipusDocumentalHelper tipusDocumentalHelper;
	@Autowired private MessageHelper messageHelper;

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
				tipusDocumental.getNomCatala()).build();
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
		
		TipusDocumentalEntity tipusDocumentalEntity = tipusDocumentalRepository.findById(tipusDocumental.getId()).orElse(null);

		if (tipusDocumentalEntity == null || !tipusDocumentalEntity.getId().equals(tipusDocumental.getId())) {
			throw new NotFoundException(
					tipusDocumental.getId(),
					TipusDocumentalEntity.class);
		}

		tipusDocumentalHelper.comprovarCanviCodi(
				tipusDocumentalEntity.getEntitat().getId(),
				tipusDocumentalEntity.getCodi(),
				tipusDocumental.getCodi());

		tipusDocumentalEntity.update(
				tipusDocumental.getCodi(),
				tipusDocumental.getNomEspanyol(), 
				tipusDocumental.getNomCatala());
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

		// La relació amb tipus de document i documents és pel codi (sense FK): cal comprovar-ho aquí.
		tipusDocumentalHelper.comprovarEsborrable(
				tipusDocumentalEntity.getEntitat().getId(),
				tipusDocumentalEntity.getCodi());

		tipusDocumentalRepository.delete(tipusDocumentalEntity);

		TipusDocumentalDto dto = conversioTipusHelper.convertir(
				tipusDocumentalEntity,
				TipusDocumentalDto.class);
		return dto;
	}

	@Transactional
	@Override
	public TipusDocumentalDto updateActiu(
			Long entitatId,
			Long id,
			boolean actiu) throws NotFoundException {
		logger.debug("Actualitzant la propietat actiu del tipus documental (" +
				"entitatId=" + entitatId +
				", tipusDocumentalId=" + id +
				", actiu=" + actiu + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false, false, false);

		TipusDocumentalEntity tipusDocumentalEntity = tipusDocumentalRepository.findById(id).orElse(null);
		if (tipusDocumentalEntity == null || !tipusDocumentalEntity.getEntitat().getId().equals(entitat.getId())) {
			throw new NotFoundException(
					id,
					TipusDocumentalEntity.class);
		}
		tipusDocumentalEntity.updateActiu(actiu);

		return conversioTipusHelper.convertir(
				tipusDocumentalEntity,
				TipusDocumentalDto.class);
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
	public List<TipusDocumentalDto> findSeleccionablesByEntitat(Long entitatId, String codiActual) throws NotFoundException {

		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false,
				true, false);

		List<TipusDocumentalEntity> tipusDocumentalsEntity = tipusDocumentalRepository.findSeleccionablesByEntitat(entitat, codiActual);
		List<TipusDocumentalDto> tipusDocumentalsDto =  conversioTipusHelper.convertirList(
				tipusDocumentalsEntity,
				TipusDocumentalDto.class);
		// Només hi pot haver un desactivat (el que ja té assignat el tipus de document): es marca perquè es vegi a l'opció.
		for (TipusDocumentalDto tipusDocumentalDto : tipusDocumentalsDto) {
			if (!tipusDocumentalDto.isActiu()) {
				tipusDocumentalDto.setNom(tipusDocumentalDto.getNom() + " " + messageHelper.getMessage("tipusdocumental.opcio.inactiu"));
			}
		}

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
