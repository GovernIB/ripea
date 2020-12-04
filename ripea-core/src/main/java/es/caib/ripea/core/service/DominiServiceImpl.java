/**
 *
 */
package es.caib.ripea.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.DominiDto;
import es.caib.ripea.core.api.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.ResultatDominiDto;
import es.caib.ripea.core.api.exception.DominiException;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.DominiService;
import es.caib.ripea.core.entity.DominiEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.MetaDadaEntity;
import es.caib.ripea.core.helper.CacheHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.DominiHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.repository.DominiRepository;
import es.caib.ripea.core.repository.MetaDadaRepository;

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
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private DominiHelper dominiHelper;
	@Autowired
	private MetaDadaRepository metaDadaRepository;
	
	@Transactional
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
				dominiHelper.xifrarContrasenya(domini.getContrasenya()),
				entitat).build();
		DominiDto dominiDto = conversioTipusHelper.convertir(
				dominiRepository.save(entity),
				DominiDto.class);
		return dominiDto;
	}

	@Transactional
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

		DominiEntity entity = dominiRepository.findOne(id);

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
		DominiEntity entity = dominiRepository.findOne(id);
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
				false);

		Page<DominiEntity> page = dominiRepository.findByEntitat(
				entitat,
				paginacioParams.getFiltre() == null,
				paginacioParams.getFiltre(),
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
				true,
				false,
				false);

		List<DominiEntity> tipusDocumentals = dominiRepository.findByEntitatOrderByNomAsc(entitat);

		List<DominiDto> dominisDto = conversioTipusHelper.convertirList(
				tipusDocumentals,
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
				false);

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
	public List<ResultatDominiDto> getResultDomini(
			Long entitatId,
			DominiDto domini) throws NotFoundException, DominiException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		if (domini == null) {
			return new ArrayList<ResultatDominiDto>();
		}
		JdbcTemplate jdbcTemplate = null;
		Properties conProps = dominiHelper.getProperties(domini);
		
		if (conProps != null && !conProps.isEmpty()) {
			long t0 = System.currentTimeMillis();
			DataSource dataSource = cacheHelper.createDominiConnexio(
					entitat.getCodi(),
					conProps);
			long t1 = System.currentTimeMillis();
			System.out.println("La creació de la connexió ha tardat: "  + (t1 - t0) + "");
			jdbcTemplate = dominiHelper.setDataSource(dataSource);
		}
		return cacheHelper.findDominisByConsutla(
				jdbcTemplate,
				domini.getConsulta());
	}
	
	@Override
	public List<DominiDto> findByMetaNodePermisLecturaAndTipusDomini(
			Long entitatId, 
			MetaExpedientDto metaExpedient) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId, 
				true, 
				false, 
				false);
		List<String> dominisCodis = new ArrayList<String>();
		List<DominiEntity> dominis = new ArrayList<DominiEntity>();
		//1. trobar metadades de tipus domini d'aquests metaexpedients
		List<MetaDadaEntity> metaDades = metaDadaRepository.findByMetaNodeIdAndTipusOrderByOrdreAsc(
				metaExpedient.getId(), 
				MetaDadaTipusEnumDto.DOMINI);
		//2. recuperar els tipus de domini d'aquestes metadades
		for (MetaDadaEntity metaDadaEntity : metaDades) {
			dominisCodis.add(metaDadaEntity.getValor() != null ? metaDadaEntity.getValor(): "");
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
		cacheHelper.evictCreateDominiConnexio();
		cacheHelper.evictFindDominisByConsutla();
	}
	private static final Logger logger = LoggerFactory.getLogger(DominiServiceImpl.class);

}