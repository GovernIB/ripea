package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.ExpedientResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.ExpedientResource;
import es.caib.ripea.service.intf.resourceservice.ExpedientResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Implementació del servei de gestió d'expedients.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
public class ExpedientResourceServiceImpl extends BaseMutableResourceService<ExpedientResource, Long, ExpedientResourceEntity> implements ExpedientResourceService {

	@PostConstruct
	public void init() {
		register(new ExpedientFilterProcessor());
	}

	private static class ExpedientFilterProcessor implements FilterProcessor<ExpedientResource.ExpedientFilterForm> {
		@Override
		public String[] getSupportedPerspectiveCodes() {
			return new String[] { "EXPEDIENT_FILTER" };
		}
		@Override
		public Class<ExpedientResource.ExpedientFilterForm> getResourceClass() {
			return ExpedientResource.ExpedientFilterForm.class;
		}
	}

}
