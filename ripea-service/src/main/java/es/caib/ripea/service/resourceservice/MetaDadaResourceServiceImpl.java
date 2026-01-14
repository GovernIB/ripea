package es.caib.ripea.service.resourceservice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

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
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.ResourceNotFoundException;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.DominiDto;
import es.caib.ripea.service.intf.dto.MetaDadaDto;
import es.caib.ripea.service.intf.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.service.intf.model.MetaDadaResource;
import es.caib.ripea.service.intf.resourceservice.MetaDadaResourceService;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementació del servei de gestió de tasques.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetaDadaResourceServiceImpl extends BaseMutableResourceService<MetaDadaResource, Long, MetaDadaResourceEntity> implements MetaDadaResourceService {
    
	private final ConfigHelper configHelper;
	private final MetaDadaHelper metaDadaHelper;
	private final EntityComprovarHelper entityComprovarHelper;
	
	private final OrganGestorRepository organGestorRepository;
	private final MetaDadaResourceRepository metaDadaResourceRepository;
	private final DominiRepository dominiRepository;
	
    @PostConstruct
    public void init() {}
	
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
	}
	
	private MetaDadaDto recursToMetaDadaDto(MetaDadaResource resource) {
		MetaDadaDto metaDadaDto = objectMappingHelper.newInstanceMap(resource, MetaDadaDto.class, "valorData", "domini");
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
}