package es.caib.ripea.service.resourceservice;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientTascaResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.MetaExpedientTascaValidacioResourceRepository;
import es.caib.ripea.persistence.repository.MetaExpedientTascaRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.ContingutLogHelper;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.service.intf.dto.LogTipusEnumDto;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.model.MetaExpedientTascaResource;
import es.caib.ripea.service.intf.model.MetaNodeResource;
import es.caib.ripea.service.intf.resourceservice.MetaExpedientTascaResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaExpedientTascaResourceServiceImpl extends BaseMutableResourceService<MetaExpedientTascaResource, Long, MetaExpedientTascaResourceEntity> implements MetaExpedientTascaResourceService {

	private final ConfigHelper configHelper;
	private final ContingutLogHelper contingutLogHelper;
	private final MetaExpedientTascaValidacioResourceRepository metaExpedientTascaValidacioResourceRepository;
	private final MetaExpedientTascaRepository metaExpedientTascaRepository;
	
    @PostConstruct
    public void init() {
    	register(MetaExpedientTascaResource.PERSPECTIVE_COUNT_VALIDACIONS,	new CountValidacionsTascaPerspectiveApplicator());
    	register(MetaExpedientTascaResource.PERSPECTIVE_REVISIO_ESTAT,	new RevisioEstatPerspectiveApplicator());
    }
	
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	
        String entitatActualCodi = configHelper.getEntitatActualCodi();
    	
        Filter filtreBase = FilterBuilder.and(
                (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null,
                FilterBuilder.equal(MetaExpedientTascaResource.Fields.metaExpedient + "." + MetaNodeResource.Fields.entitat + "." + EntitatResource.Fields.codi, 
                		entitatActualCodi != null?entitatActualCodi:"................................................................................")
        );
        
        return filtreBase.generate();
    }
    
    private class CountValidacionsTascaPerspectiveApplicator implements PerspectiveApplicator<MetaExpedientTascaResourceEntity, MetaExpedientTascaResource> {
		@Override
		public void applySingle(String code, MetaExpedientTascaResourceEntity entity, MetaExpedientTascaResource resource) throws PerspectiveApplicationException {
			resource.setNumValidacio(metaExpedientTascaValidacioResourceRepository.countByMetaExpedientTascaId(entity.getId()));
		}
    }

    private class RevisioEstatPerspectiveApplicator implements PerspectiveApplicator<MetaExpedientTascaResourceEntity, MetaExpedientTascaResource> {
		@Override
		public void applySingle(String code, MetaExpedientTascaResourceEntity entity, MetaExpedientTascaResource resource) throws PerspectiveApplicationException {
            if (entity.getMetaExpedient() != null) {
                resource.setMetaExpedientRevisioEstat(entity.getMetaExpedient().getRevisioEstat());
            }
		}
    }

    @Override
    protected void afterConversion(MetaExpedientTascaResourceEntity entity, MetaExpedientTascaResource resource) {
        if (entity.getEstatCrearTasca()!=null) {
            resource.setEstatColorCrearTasca(entity.getEstatCrearTasca().getColor());
        }
        if (entity.getEstatFinalitzarTasca()!=null) {
            resource.setEstatColorFinalitzarTasca(entity.getEstatFinalitzarTasca().getColor());
        }
    }

    protected void afterCreateSave(MetaExpedientTascaResourceEntity entity, MetaExpedientTascaResource resource, Map<String, AnswerRequiredException.AnswerValue> answers, boolean anyOrderChanged) {
    	logProcedimentTascaAccio(entity, LogTipusEnumDto.CREACIO);
    }
    
    protected void afterUpdateSave(MetaExpedientTascaResourceEntity entity, MetaExpedientTascaResource resource, Map<String, AnswerRequiredException.AnswerValue> answers, boolean anyOrderChanged) {
    	logProcedimentTascaAccio(entity, LogTipusEnumDto.MODIFICACIO);
    }
    
    protected void afterDelete(MetaExpedientTascaResourceEntity entity, Map<String, AnswerRequiredException.AnswerValue> answers) {
    	logProcedimentTascaAccio(entity, LogTipusEnumDto.ELIMINACIO);
    }
    
    private void logProcedimentTascaAccio(MetaExpedientTascaResourceEntity entity, LogTipusEnumDto accio) {
    	contingutLogHelper.logProcedimentObjecte(
    			entity.getMetaExpedient().getId(),
    			LogTipusEnumDto.MODIFICACIO,
    			(LogTipusEnumDto.ELIMINACIO.equals(accio))?null:metaExpedientTascaRepository.findById(entity.getId()).get(),
    			LogObjecteTipusEnumDto.METATASCA,
    			accio,
    			entity.getCodi(),
    			entity.getNom());
    }
}