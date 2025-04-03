package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.DocumentNotificacioResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.model.DocumentNotificacioResource;
import es.caib.ripea.service.intf.resourceservice.DocumentNotificacioResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Map;

/**
 * Implementació del servei de gestió d'expedients.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentNotificacioResourceServiceImpl extends BaseMutableResourceService<DocumentNotificacioResource, Long, DocumentNotificacioResourceEntity> implements DocumentNotificacioResourceService {

    @PostConstruct
    public void init() {
        register(DocumentNotificacioResource.ACTION_ACTUALITZAR_ESTAT_CODE, new ActualitzarEstatActionExecutor());
    }

    @Override
    protected void afterConversion(DocumentNotificacioResourceEntity entity, DocumentNotificacioResource resource) {
        resource.setFitxerNom(entity.getDocument().getFitxerNom());
    }

    // ActionExecutor
    private class ActualitzarEstatActionExecutor implements ActionExecutor<DocumentNotificacioResourceEntity, Serializable, DocumentNotificacioResource> {

        @Override
        public DocumentNotificacioResource exec(String code, DocumentNotificacioResourceEntity entity, Serializable params) throws ActionExecutionException {
            return null;
        }

        @Override
        public void onChange(Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {

        }
    }
}