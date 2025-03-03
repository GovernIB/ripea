package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.ExpedientTascaResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.UsuariResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.model.ExpedientTascaResource;
import es.caib.ripea.service.intf.resourceservice.ExpedientTascaResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementació del servei de gestió de tasques.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExpedientTascaResourceServiceImpl extends BaseMutableResourceService<ExpedientTascaResource, Long, ExpedientTascaResourceEntity> implements ExpedientTascaResourceService {

	@PostConstruct
	public void init() {
		register("RESPONSABLES_RESUM", new ExpedientTascaResourceServiceImpl.ResponsablesPerspectiveApplicator());
	}

	private class ResponsablesPerspectiveApplicator implements PerspectiveApplicator<ExpedientTascaResource, ExpedientTascaResourceEntity> {
		@Override
		public void applySingle(String code, ExpedientTascaResourceEntity entity, ExpedientTascaResource resource) throws PerspectiveApplicationException {
			if (entity.getResponsables() != null && !entity.getResponsables().isEmpty()) {
				List<String> responsablesStr = new ArrayList<>();
				for (UsuariResourceEntity responsable: entity.getResponsables()) {
					responsablesStr.add(responsable.getCodi());
				}
				resource.setResponsablesStr(StringUtils.join(responsablesStr, ","));
			}
		}
	}

}
