package es.caib.ripea.service.service;

import es.caib.ripea.persistence.entity.DominiEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.MetaDadaEntity;
import es.caib.ripea.persistence.repository.DominiRepository;
import es.caib.ripea.persistence.repository.MetaDadaRepository;
import es.caib.ripea.service.helper.*;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.exception.DominiException;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.DominiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class DominiServiceImpl implements DominiService {

	@Autowired private DominiRepository dominiRepository;
	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private PaginacioHelper paginacioHelper;
	@Autowired private CacheHelper cacheHelper;
	@Autowired private DominiHelper dominiHelper;
	@Autowired private MetaDadaRepository metaDadaRepository;
	
	@Transactional
	@Override
	public DominiDto create(
			Long entitatId,
			DominiDto domini) throws NotFoundException {
		
		return dominiHelper.create(
				entitatId,
				domini, true);
	}

	@Transactional
	@Override
	public DominiDto update(
			Long entitatId, 
			DominiDto domini) throws NotFoundException {
		logger.debug("Actualitzant el domini per l'entitat (" +
				"entitatId=" + entitatId +
				"dominiId=" + domini.getId() + ")");

		DominiEntity entity = dominiRepository.findById(domini.getId()).orElse(null);

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
				dominiHelper.xifrarContrasenya(domini.getContrasenya()));
		DominiDto dominiDto = conversioTipusHelper.convertir(
				entity,
				DominiDto.class);
		return dominiDto;
	}

	@Transactional
	@Override
	public DominiDto delete(
			Long entitatId, 
			Long id) throws NotFoundException {
		logger.debug("Esborrant el tipus documental per l'entitat (" +
				"entitatId=" + entitatId +
				"dominiId=" + id + ")");

		DominiEntity entity = dominiRepository.getOne(id);

		dominiRepository.delete(entity);

		DominiDto dominiDto = conversioTipusHelper.convertir(
				entity,
				DominiDto.class);
		return dominiDto;
	}

	@Transactional(readOnly = true)
	@Override
	public DominiDto findById(
			Long entitatId, 
			Long id) throws NotFoundException {
		logger.debug("Consultant el domini per l'entitat (" +
				"entitatId=" + entitatId +
				"dominiId=" + id + ")");
		DominiEntity entity = dominiRepository.getOne(id);
		entity.setContrasenya(dominiHelper.desxifrarContrasenya(entity.getContrasenya()));
		
		DominiDto dominiDto = conversioTipusHelper.convertir(
				entity,
				DominiDto.class);
		return dominiDto;	
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<DominiDto> findByEntitatPaginat(Long entitatId, PaginacioParamsDto paginacioParams)
			throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false, false, false);

		Page<DominiEntity> page = dominiRepository.findByEntitat(
				entitat,
				paginacioParams.getFiltre() == null,
				paginacioParams.getFiltre() != null ? paginacioParams.getFiltre() : "",
				paginacioHelper.toSpringDataPageable(paginacioParams));

		PaginaDto<DominiDto> dominiDto = paginacioHelper.toPaginaDto(
				page,
				DominiDto.class);
		return dominiDto;
	}

	@Transactional(readOnly = true)
	@Override
	public List<DominiDto> findByEntitat(Long entitatId) throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				true, false);

		List<DominiEntity> dominis = dominiRepository.findByEntitatOrderByNomAsc(entitat);

		List<DominiDto> dominisDto = conversioTipusHelper.convertirList(
				dominis,
				DominiDto.class);
		return dominisDto;
	}

	@Transactional(readOnly = true)
	@Override
	public DominiDto findByCodiAndEntitat(String codi, Long entitatId) throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false, false, false);

		DominiEntity tipusDocumental = dominiRepository.findByCodiAndEntitat(
				codi, 
				entitat);
		DominiDto dominiDto = conversioTipusHelper.convertir(
				tipusDocumental, 
				DominiDto.class);
		return dominiDto;
	}
	
	@Transactional
	@Override
	public ResultatDominiDto getResultDomini(Long entitatId, DominiDto domini, String filter, int page, int resultCount) 
	throws NotFoundException, DominiException {
		return dominiHelper.getResultDomini(entitatId, domini, filter, page, resultCount);
	}
	
	public ResultatConsultaDto getSelectedDomini(
			Long entitatId,
			DominiDto domini,
			String dadaValor) throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId, 
				true, 
				false, 
				false, 
				true, false);
		ResultatConsultaDto resultat = new ResultatConsultaDto();
		if (domini == null) {
			return resultat;
		}
		JdbcTemplate jdbcTemplate = null;
		Properties conProps = dominiHelper.getProperties(domini);
		
		if (conProps != null && !conProps.isEmpty()) {
			DataSource dataSource = dominiHelper.createDominiConnexio(
					entitat.getCodi(),
					conProps);
			jdbcTemplate = dominiHelper.setDataSource(dataSource);
		}
		
		try {
			resultat = cacheHelper.getValueSelectedDomini(
					jdbcTemplate,
					domini.getConsulta(),
					dadaValor);
		} catch (Exception ex) {
			logger.error(
					"Hi ha hagut un error creant la connexió del domini " + domini.getNom(),
					ex);
			throw new RuntimeException(
					"Hi ha hagut un error creant la connexió del domini " + domini.getNom(),
					ex);
		}
		return resultat;
	}
	
	@Override
	public List<DominiDto> findByMetaNodePermisLecturaAndTipusDomini(
			Long entitatId, 
			MetaExpedientDto metaExpedient) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId, 
				false, 
				false, 
				false, 
				true, false);
		List<String> dominisCodis = new ArrayList<String>();
		List<DominiEntity> dominis = new ArrayList<DominiEntity>();
		//1. trobar metadades de tipus domini d'aquests metaexpedients
		List<MetaDadaEntity> metaDades = metaDadaRepository.findByMetaNodeIdAndTipusAndActivaTrueOrderByOrdreAsc(
				metaExpedient.getId(), 
				MetaDadaTipusEnumDto.DOMINI);
		//2. recuperar els tipus de domini d'aquestes metadades
		for (MetaDadaEntity metaDadaEntity : metaDades) {
			dominisCodis.add(metaDadaEntity.getCodi() != null ? metaDadaEntity.getCodi(): "");
		}
		if (!dominisCodis.isEmpty()) {
			dominis = dominiRepository.findByEntitatAndCodiInOrderByIdAsc(
					entitat, 
					dominisCodis);
		}
		return conversioTipusHelper.convertirList(
				dominis, 
				DominiDto.class);
	}

	public void evictDominiCache() {
		cacheHelper.evictFindDominisByConsutla();
	}
	private static final Logger logger = LoggerFactory.getLogger(DominiServiceImpl.class);

}
