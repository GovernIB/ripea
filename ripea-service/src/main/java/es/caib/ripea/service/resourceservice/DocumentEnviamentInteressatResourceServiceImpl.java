package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.DocumentEnviamentInteressatResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.UsuariResourceRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.model.DocumentEnviamentInteressatResource;
import es.caib.ripea.service.intf.model.DocumentNotificacioResource;
import es.caib.ripea.service.intf.model.ExpedientResource;
import es.caib.ripea.service.intf.model.InteressatResource;
import es.caib.ripea.service.intf.resourceservice.DocumentEnviamentInteressatResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
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
public class DocumentEnviamentInteressatResourceServiceImpl extends BaseMutableResourceService<DocumentEnviamentInteressatResource, Long, DocumentEnviamentInteressatResourceEntity> implements DocumentEnviamentInteressatResourceService {

    private final UsuariResourceRepository usuariResourceRepository;

    @PostConstruct
    public void init() {
        register(DocumentEnviamentInteressatResource.PERSPECTIVE_DETAIL_CODE, new DetailPerspectiveApplicator());
        register(DocumentEnviamentInteressatResource.ACTION_AMPLIAR_PLAC_CODE, new AmpliarPalacActionExecutor());
    }

    // PerspectiveApplicator
    private class DetailPerspectiveApplicator implements PerspectiveApplicator<DocumentEnviamentInteressatResourceEntity, DocumentEnviamentInteressatResource> {

        @Override
        public void applySingle(String code, DocumentEnviamentInteressatResourceEntity entity, DocumentEnviamentInteressatResource resource) throws PerspectiveApplicationException {
            resource.setInteressatInfo(objectMappingHelper.newInstanceMap(Hibernate.unproxy(entity.getInteressat()), InteressatResource.class));
            if (entity.getInteressat().getRepresentant()!=null){
                resource.setRepresentantInfo(objectMappingHelper.newInstanceMap(Hibernate.unproxy(entity.getInteressat().getRepresentant()), InteressatResource.class));
            }
            resource.setNotificacioInfo(objectMappingHelper.newInstanceMap(Hibernate.unproxy(entity.getNotificacio()), DocumentNotificacioResource.class));

            usuariResourceRepository.findById(entity.getNotificacio().getCreatedBy())
                    .ifPresent(usu->resource.setEntregaNif(usu.getNif()));
            resource.setClassificacio(entity.getNotificacio().getExpedient().getMetaExpedient().getClassificacio());
        }
    }

    // ActionExecutor
    private class AmpliarPalacActionExecutor implements ActionExecutor<DocumentEnviamentInteressatResourceEntity, DocumentEnviamentInteressatResource.AmpliarPalacFormAction, DocumentEnviamentInteressatResource> {

        @Override
        public DocumentEnviamentInteressatResource exec(String code, DocumentEnviamentInteressatResourceEntity entity, DocumentEnviamentInteressatResource.AmpliarPalacFormAction params) throws ActionExecutionException {
            return null;
        }

        @Override
        public void onChange(Serializable id, DocumentEnviamentInteressatResource.AmpliarPalacFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, DocumentEnviamentInteressatResource.AmpliarPalacFormAction target) {

        }
    }
}