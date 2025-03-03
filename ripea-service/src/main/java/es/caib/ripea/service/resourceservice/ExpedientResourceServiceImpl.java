package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.ExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.InteressatResourceRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.model.ExpedientResource;
import es.caib.ripea.service.intf.model.InteressatResource;
import es.caib.ripea.service.intf.resourceservice.ExpedientResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
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

	private final InteressatResourceRepository interessatResourceRepository;

	@PostConstruct
	public void init() {
		register("INTERESSATS_RESUM", new InteressatsPerspectiveApplicator());
		register("metaExpedient", new MetaExpedientOnchangeLogicProcessor());
	}

	private class InteressatsPerspectiveApplicator implements PerspectiveApplicator<ExpedientResource, ExpedientResourceEntity> {
		@Override
		public void applySingle(String code, ExpedientResourceEntity entity, ExpedientResource resource) throws PerspectiveApplicationException {
			List<InteressatResource> interessats= interessatResourceRepository.findByExpedient(entity).stream()
					.map(interessatResourceEntity -> objectMappingHelper.newInstanceMap(interessatResourceEntity, InteressatResource.class))
					.collect(Collectors.toList());
			StringBuilder interessatsResum = new StringBuilder();
			if (!interessats.isEmpty()) {
				for (InteressatResource interessat: interessats) {
					switch (interessat.getTipus()) {
						case InteressatPersonaFisicaEntity:
							interessatsResum.append(interessat.getNom() == null ? "" : interessat.getNom() + " ");
							interessatsResum.append(interessat.getLlinatge1() == null ? "" : interessat.getLlinatge1() + " ");
							interessatsResum.append(interessat.getLlinatge2() == null ? "" : interessat.getLlinatge2() + " ");
							interessatsResum.append("(").append(interessat.getDocumentNum()).append(")").append("\n");
							break;
						case InteressatPersonaJuridicaEntity:
							interessatsResum.append(interessat.getRaoSocial()).append(" ");
							interessatsResum.append("(").append(interessat.getDocumentNum()).append(")").append("\n");
							break;
						case InteressatAdministracioEntity:
							interessatsResum.append(interessat.getNomComplet()).append(" ");
							interessatsResum.append("(").append(interessat.getDocumentNum()).append(")").append("\n");
							break;
					}
				}
			}
			resource.setInteressatsResum(interessatsResum.toString());
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

		}
	}

}
