package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.AlertaResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.AlertaResourceRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.model.AlertaResource;
import es.caib.ripea.service.intf.model.ExpedientResource;
import es.caib.ripea.service.intf.resourceservice.AlertaResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Map;

/**
 * Implementació del servei de gestió de tasques.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertaResourceServiceImpl extends BaseMutableResourceService<AlertaResource, Long, AlertaResourceEntity> implements AlertaResourceService {

	private final AlertaResourceRepository alertaResourceRepository;
	
    @PostConstruct
    public void init() {
        register(AlertaResource.ACTION_MASSIVE_LLEGIT_CODE,	new LlegitActionExecutor());
    }

    private class LlegitActionExecutor implements ActionExecutor<AlertaResourceEntity, ExpedientResource.MassiveAction, Serializable> {

        @Override
        public Serializable exec(String code, AlertaResourceEntity entity, ExpedientResource.MassiveAction params) throws ActionExecutionException {
            if (params!=null) {
            	for (Long id: params.getIds()) {
            		alertaResourceRepository.findById(id).get().setLlegida(true);
            	}
            }
            return null;
        }

        @Override
        public void onChange(Serializable id, ExpedientResource.MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, ExpedientResource.MassiveAction target) {}
    }
}