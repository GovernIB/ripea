package es.caib.ripea.service.resourceservice;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.DominiEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaDadaResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.MetaDadaResourceRepository;
import es.caib.ripea.persistence.repository.DominiRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.MetaDadaHelper;
import es.caib.ripea.service.helper.MetaNodeHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.exception.ResourceNotFoundException;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.DominiDto;
import es.caib.ripea.service.intf.dto.MetaDadaDto;
import es.caib.ripea.service.intf.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.model.MetaDadaResource;
import es.caib.ripea.service.intf.model.MetaNodeResource;
import es.caib.ripea.service.intf.resourceservice.MetaDadaResourceService;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaDadaResourceServiceImpl extends BaseMutableResourceService<MetaDadaResource, Long, MetaDadaResourceEntity> implements MetaDadaResourceService {
    
	/** Nomes les metadades actives al procediment (pestanya "Dades" d'un expedient). */
	private static final String NAMED_QUERY_ACTIVES = "ACTIVES";

	private final ConfigHelper configHelper;
	private final MetaDadaHelper metaDadaHelper;
	private final MetaNodeHelper metaNodeHelper;
	private final EntityComprovarHelper entityComprovarHelper;
	
	private final OrganGestorRepository organGestorRepository;
	private final MetaDadaResourceRepository metaDadaResourceRepository;
	private final DominiRepository dominiRepository;
	
    @PostConstruct
    public void init() {
    	register(MetaDadaResource.ACTION_REORDENAR_CODE,		new ReordenarActionExecutor());
    }
	
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	
        String entitatActualCodi = configHelper.getEntitatActualCodi();
    	
        Filter filtreBase = FilterBuilder.and(
                (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null,
                FilterBuilder.equal(MetaDadaResource.Fields.metaNode + "." + MetaNodeResource.Fields.entitat + "." + EntitatResource.Fields.codi, 
                		entitatActualCodi != null?entitatActualCodi:"................................................................................")
        );
        
        Map<String, String> mapaNamedQueries = Utils.namedQueriesToMap(namedQueries);
        if (mapaNamedQueries.containsKey(NAMED_QUERY_ACTIVES)) {
        	/**
        	 * S'utilitza a la pestanya "Dades" d'un expedient, a on nomes s'hi han de mostrar les
        	 * metadades que estan actives al procediment. Equivalent al que fa la interficie JSP a
        	 * MetaDadaServiceImpl.findByNode -> findByMetaNodeAndActivaTrueOrderByOrdreAsc.
        	 */
        	filtreBase = FilterBuilder.and(
        			filtreBase,
        			FilterBuilder.equal(MetaDadaResource.Fields.activa, true));
        }
        
        return filtreBase.generate();
    }
    
	@Override
	public MetaDadaResource create(MetaDadaResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
		
		String entitatActualCodi = configHelper.getEntitatActualCodi();
		EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true, false);
		
		String organActualCodi	 = configHelper.getOrganActualCodi();
		Long organId = null;
		if (organActualCodi!=null) {
			organId = organGestorRepository.findByEntitatIdAndCodi(entitatEntity.getId(), organActualCodi).getId();
		}
		
		metaDadaHelper.create(
				entitatEntity.getId(),
				resource.getMetaNode().getId(),
				recursToMetaDadaDto(resource),
				configHelper.getRolActual(),
				organId);

		return resource;
	}

	@Override
	public MetaDadaResource update(
			Long id,
			MetaDadaResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException {
		
		String entitatActualCodi = configHelper.getEntitatActualCodi();
		EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true, false);
		
		String organActualCodi	 = configHelper.getOrganActualCodi();
		Long organId = null;
		if (organActualCodi!=null) {
			organId = organGestorRepository.findByEntitatIdAndCodi(entitatEntity.getId(), organActualCodi).getId();
		}
		
		MetaDadaResourceEntity metaDada = metaDadaResourceRepository.findById(id).get();
		
		if (metaDada.isActiva()!=resource.isActiva()) {
			metaDadaHelper.updateActiva(
					entitatEntity.getId(),
					resource.getMetaNode().getId(),
					id,
					resource.isActiva(),
					configHelper.getRolActual(),
					organId);
		} else {
			metaDadaHelper.update(
					entitatEntity.getId(),
					resource.getMetaNode().getId(),
					recursToMetaDadaDto(resource),
					configHelper.getRolActual(),
					organId);
		}

		return resource;
	}

	@Override
	public void delete(
			Long id,
			Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException {
		
		String entitatActualCodi = configHelper.getEntitatActualCodi();
		EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true, false);
		
		String organActualCodi	 = configHelper.getOrganActualCodi();
		Long organId = null;
		if (organActualCodi!=null) {
			organId = organGestorRepository.findByEntitatIdAndCodi(entitatEntity.getId(), organActualCodi).getId();
		}
		
		MetaDadaResourceEntity metaDada = metaDadaResourceRepository.findById(id).get();
		
		metaDadaHelper.delete(
				entitatEntity.getId(),
				metaDada.getMetaNode().getId(),
				id,
				configHelper.getRolActual(),
				organId);
		//L'evict de validacions el fa internament metaDadaHelper.delete (i create/update/updateActiva),
		//amb els guards d'obligatorietat. No cal repetir-lo aquí (evitava el doble evict i saltava els guards).
	}
	
	private MetaDadaDto recursToMetaDadaDto(MetaDadaResource resource) {
		MetaDadaDto metaDadaDto = objectMappingHelper.newInstanceMap(resource, MetaDadaDto.class, "valorData", "domini", "serialVersionUID");
		metaDadaDto.setValorData(Utils.localDateTimeToDateJava(resource.getValorData()));
		if (resource.getTipus().equals(MetaDadaTipusEnumDto.DOMINI) && resource.getDomini()!=null) {
			DominiDto dominiDto = new DominiDto();
			dominiDto.setId(resource.getDomini().getId());
			dominiDto.setNom(resource.getDomini().getDescription());
			metaDadaDto.setDomini(dominiDto);
			DominiEntity domini = dominiRepository.findById(resource.getDomini().getId()).get();
			metaDadaDto.setValorString(domini.getCodi());
		}
		return metaDadaDto;
	}
	
    @Override
    protected void afterConversion(MetaDadaResourceEntity entity, MetaDadaResource resource) {
    	if (Utils.hasValue(entity.getValor())) {
			if (entity.getTipus()==MetaDadaTipusEnumDto.BOOLEA) {
				Boolean valor = new Boolean(entity.getValor());
				resource.setValorBoolea(valor);
			} else if (entity.getTipus()==MetaDadaTipusEnumDto.DATA) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				LocalDate fecha = LocalDate.parse(entity.getValor(), formatter);
				LocalDateTime fechaHora = fecha.atStartOfDay();
				resource.setValorData(fechaHora);
			} else if (entity.getTipus()==MetaDadaTipusEnumDto.FLOTANT) {
				Double valor = new Double(entity.getValor());
				resource.setValorFlotant(valor);
			} else if (entity.getTipus()==MetaDadaTipusEnumDto.IMPORT) {
				BigDecimal valor = new BigDecimal(entity.getValor());
				resource.setValorImport(valor);
			} else if (entity.getTipus()==MetaDadaTipusEnumDto.SENCER) {
				Long valor = new Long(entity.getValor());
				resource.setValorSencer(valor);
			}  else if (entity.getTipus()==MetaDadaTipusEnumDto.TEXT) {
				resource.setValorString(entity.getValor());
			}  else if (entity.getTipus()==MetaDadaTipusEnumDto.DOMINI) {
				String entitatActualCodi = configHelper.getEntitatActualCodi();
				EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true, false);
				DominiEntity domini = dominiRepository.findByCodiAndEntitatId(entity.getValor(), entitatEntity.getId());
				resource.setDomini(ResourceReference.toResourceReference(domini.getId(), domini.getNom()));
			}
    	}
    }
    
    private class ReordenarActionExecutor implements ActionExecutor<MetaDadaResourceEntity, Integer, Serializable> {
		@Override
		public void onChange(Serializable id, Integer previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, Integer target) {
		}
		@Override
		public Serializable exec(String code, MetaDadaResourceEntity entity, Integer params) throws ActionExecutionException {
			EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
			metaNodeHelper.moureMetaNodeMetaDada(entitatEntity.getId(), entity.getMetaNode().getId(), entity.getId(), params);
			return "{\"resultado\": \"OK\"}";
		}
    }
}