package es.caib.ripea.service.resourceservice;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import es.caib.ripea.persistence.entity.resourceentity.AvisResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.AvisResourceRepository;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.model.AvisResource;
import es.caib.ripea.service.intf.model.NodeResource;
import org.springframework.stereotype.Service;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.EventHelper;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.resourceservice.AvisResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvisResourceServiceImpl extends BaseMutableResourceService<AvisResource, Long, AvisResourceEntity> implements AvisResourceService {

    private final AvisResourceRepository avisResourceRepository;
	private final EventHelper eventHelper;

    @PostConstruct
    public void init() {
        register(AvisResource.ACTION_MASSIVE_ACTIVE_CODE,	new MassiveActiveActionExecutor());
        register(AvisResource.ACTION_MASSIVE_DELETE_CODE,	new MassiveDeleteActionExecutor());
    }
	
	protected void afterCreateSave(AvisResourceEntity entity, AvisResource resource, Map<String, AnswerRequiredException.AnswerValue> answers, boolean anyOrderChanged) {
		eventHelper.notifyAvisosActius();
	}
	
	protected void afterUpdateSave(AvisResourceEntity entity, AvisResource resource, Map<String, AnswerRequiredException.AnswerValue> answers, boolean anyOrderChanged) {
		eventHelper.notifyAvisosActius();
	}
	
	protected void afterDelete(AvisResourceEntity entity, Map<String, AnswerRequiredException.AnswerValue> answers) {
		eventHelper.notifyAvisosActius();
	}

	@Override
	protected void completeResource(AvisResource resource) {
		resource.setEntitat(null); //Els avisos creats per un superadmin no van assocciats a cap entitat.
	}
	
    private class MassiveActiveActionExecutor implements ActionExecutor<AvisResourceEntity, AvisResource.MassiveActiveFormAction, Serializable> {

        @Override
        public void onChange(Serializable id, AvisResource.MassiveActiveFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, AvisResource.MassiveActiveFormAction target) {}

        @Override
        public Serializable exec(String code, AvisResourceEntity entity, AvisResource.MassiveActiveFormAction params) throws ActionExecutionException {
            List<AvisResourceEntity> avisEntityList = avisResourceRepository.findAllById(params.getIds());
            for (AvisResourceEntity avisEntity : avisEntityList) {
                avisEntity.setActiu(params.getActive());
                avisResourceRepository.save(avisEntity);
                eventHelper.notifyAvisosActius();
            }
            return null;
        }
    }

    private class MassiveDeleteActionExecutor implements ActionExecutor<AvisResourceEntity, NodeResource.MassiveAction, Serializable> {

        @Override
        public void onChange(Serializable id, NodeResource.MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, NodeResource.MassiveAction target) {}

        @Override
        public Serializable exec(String code, AvisResourceEntity entity, NodeResource.MassiveAction params) throws ActionExecutionException {
            avisResourceRepository.deleteAllById(params.getIds());
            eventHelper.notifyAvisosActius();
            return null;
        }
    }
}