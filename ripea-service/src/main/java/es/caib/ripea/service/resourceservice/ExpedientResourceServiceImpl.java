package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.ExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.InteressatResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.MetaExpedientResourceRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.model.ExpedientResource;
import es.caib.ripea.service.intf.model.InteressatResource;
import es.caib.ripea.service.intf.model.MetaExpedientResource;
import es.caib.ripea.service.intf.resourceservice.ExpedientResourceService;
import es.caib.ripea.service.intf.resourceservice.MetaExpedientResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementació del servei de gestió d'expedients.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExpedientResourceServiceImpl extends BaseMutableResourceService<ExpedientResource, Long, ExpedientResourceEntity> implements ExpedientResourceService {

    private final MetaExpedientResourceRepository metaExpedientResourceRepository;

    @PostConstruct
	public void init() {
		register("INTERESSATS_RESUM", new InteressatsPerspectiveApplicator());
		register("metaExpedient", new MetaExpedientOnchangeLogicProcessor());
	}

    @Override
    protected void afterConversion(ExpedientResourceEntity entity, ExpedientResource resource) {
        resource.setNumComentaris(entity.getComentaris().size());
        resource.setNumSeguidors(entity.getSeguidors().size());
    }

	private class InteressatsPerspectiveApplicator implements PerspectiveApplicator<ExpedientResource, ExpedientResourceEntity> {
		@Override
		public void applySingle(String code, ExpedientResourceEntity entity, ExpedientResource resource) throws PerspectiveApplicationException {
			List<InteressatResource> interessats= entity.getInteressats().stream()
                    .map(interessatResourceEntity -> objectMappingHelper.newInstanceMap(interessatResourceEntity, InteressatResource.class))
                    .collect(Collectors.toList());
            resource.setInteressats(interessats);
		}
	}

	private class MetaExpedientOnchangeLogicProcessor implements OnChangeLogicProcessor<ExpedientResource> {
		@Override
		public void processOnChangeLogic(
				ExpedientResource previous,
				String fieldName,
				Object fieldValue,
				Map<String, AnswerRequiredException.AnswerValue> answers,
				ExpedientResource target) {

            Optional<MetaExpedientResourceEntity> metaExpedientResourceOptional =
                    metaExpedientResourceRepository.findById(previous.getMetaExpedient().getId());

            metaExpedientResourceOptional.ifPresent((metaExpedientResourceEntity)->{
                MetaExpedientResource metaExpedientResource =
                        objectMappingHelper.newInstanceMap(metaExpedientResourceEntity, MetaExpedientResource.class);
                target.setOrganGestor(metaExpedientResource.getOrganGestor());
//                target.getSequencia(metaExpedientResource);
            });
		}
	}
}
