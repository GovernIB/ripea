package es.caib.ripea.service.resourceservice;

import java.util.Map;

import org.springframework.stereotype.Service;
import es.caib.ripea.persistence.entity.resourceentity.AvisResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.EventHelper;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.model.AvisResource;
import es.caib.ripea.service.intf.resourceservice.AvisResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvisResourceServiceImpl extends BaseMutableResourceService<AvisResource, Long, AvisResourceEntity> implements AvisResourceService {
	
	private final EventHelper eventHelper;
	
	protected void afterCreateSave(AvisResourceEntity entity, AvisResource resource, Map<String, AnswerRequiredException.AnswerValue> answers, boolean anyOrderChanged) {
		eventHelper.notifyAvisosActius();
	}
	
	protected void afterUpdateSave(AvisResourceEntity entity, AvisResource resource, Map<String, AnswerRequiredException.AnswerValue> answers, boolean anyOrderChanged) {
		eventHelper.notifyAvisosActius();
	}
	
	protected void afterDelete(AvisResourceEntity entity, Map<String, AnswerRequiredException.AnswerValue> answers) {
		eventHelper.notifyAvisosActius();
	}
}