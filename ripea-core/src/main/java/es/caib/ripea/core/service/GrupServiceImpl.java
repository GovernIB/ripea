package es.caib.ripea.core.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.GrupDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.GrupService;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.GrupEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.repository.GrupRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;

@Service
public class GrupServiceImpl implements GrupService {
	
	
	@Autowired
	private GrupRepository grupRepository;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private MetaExpedientRepository metaExpedientRepository;
	
	@Transactional
	@Override
	public GrupDto create(
			Long entitatId,
			GrupDto grupDto) throws NotFoundException {
		logger.debug("Creant un nou grup per l'entitat (" +
				"entitatId=" + entitatId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		
		GrupEntity enitity = GrupEntity.getBuilder(
				grupDto.getRol(),
				grupDto.getDescripcio(),
				entitat).build();

		GrupDto dto = conversioTipusHelper.convertir(
				grupRepository.save(enitity),
				GrupDto.class);
		return dto;
	}

	@Transactional
	@Override
	public GrupDto update(
			Long entitatId,
			GrupDto grupDto) throws NotFoundException {
		logger.debug("Actualitzant el grup per l'entitat (" +
				"entitatId=" + entitatId +
				"grupId=" + grupDto.getId() + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		
		GrupEntity grupEntity = grupRepository.findOne(grupDto.getId());

		grupEntity.update(
				grupDto.getRol(),
				grupDto.getDescripcio());
		
		GrupDto dto = conversioTipusHelper.convertir(
				grupEntity,
				GrupDto.class);
		return dto;
	}

	@Transactional
	@Override
	public GrupDto delete(
			Long entitatId,
			Long id) throws NotFoundException {
		logger.debug("Esborrant el tipus documental per l'entitat (" +
				"entitatId=" + entitatId +
				"grupId=" + id + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);

		GrupEntity grupEntity = grupRepository.findOne(id);
		
		grupRepository.delete(grupEntity);

		GrupDto dto = conversioTipusHelper.convertir(
				grupEntity,
				GrupDto.class);
		return dto;
	}

	@Transactional
	@Override
	public GrupDto findById(Long entitatId, Long id) throws NotFoundException {
		logger.debug("Consultant el tipus documental per l'entitat (" +
				"entitatId=" + entitatId +
				"grupId=" + id + ")");
		GrupEntity entity = grupRepository.findOne(id);

		GrupDto dto = conversioTipusHelper.convertir(
				entity,
				GrupDto.class);
		return dto;
	}

	@Transactional
	@Override
	public PaginaDto<GrupDto> findByEntitatPaginat(
			Long entitatId,
			Long metaExpedientId, 
			PaginacioParamsDto paginacioParams) throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);

		Page<GrupEntity> page = grupRepository.findByEntitat(
				entitat,
				paginacioParams.getFiltre() == null,
				paginacioParams.getFiltre(),
				paginacioHelper.toSpringDataPageable(paginacioParams));
		
		
		PaginaDto<GrupDto> pageDto = paginacioHelper.toPaginaDto(
				page,
				GrupDto.class);
		
		if (metaExpedientId != null) {
			for (GrupDto grup : pageDto.getContingut()) {
				boolean relacionat = false;
//				List<MetaExpedientEntity> metaExpds = grupRepository.findByGrup(grupRepository.findOne(grup.getId()));
				for (MetaExpedientEntity metaExpedientEntity : grupRepository.findOne(grup.getId()).getMetaExpedients()) {
					if (metaExpedientEntity.getId().equals(metaExpedientId)) {
						relacionat = true;
					}
				}
				grup.setRelacionat(relacionat);
			}
		}

		return pageDto;
	}
	
	

	
	
	
	
	@Transactional
	@Override
	public void relacionarAmbMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			Long id) {
		logger.debug("Relacionant un grup amb metaxpedient (" +
				"metaExpedientId=" + metaExpedientId + ", " +
				"id=" + id + ")");
		
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		
		MetaExpedientEntity metaExpedientEntity = metaExpedientRepository.findOne(metaExpedientId);
		
		GrupEntity grupEntity = grupRepository.findOne(id);
		
		metaExpedientEntity.addGrup(grupEntity);

	}
	
	
	@Override
	@Transactional
	public void desvincularAmbMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			Long id) throws NotFoundException {
		logger.debug("Desvinculant un grup amb metaxpedient (" +
				"metaExpedientId=" + metaExpedientId + ", " +
				"id=" + id + ")");
		
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		
		MetaExpedientEntity metaExpedientEntity = metaExpedientRepository.findOne(metaExpedientId);
		
		GrupEntity grupEntity = grupRepository.findOne(id);
		
		metaExpedientEntity.removeGrup(grupEntity);
	}
	
	
	
	
	private static final Logger logger = LoggerFactory.getLogger(GrupServiceImpl.class);
	
	
}
