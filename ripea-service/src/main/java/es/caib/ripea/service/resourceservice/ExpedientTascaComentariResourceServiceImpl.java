package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.ExpedientTascaComentariResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.ExpedientTascaComentariResource;
import es.caib.ripea.service.intf.resourceservice.ExpedientTascaComentariResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de gestió de comentaris.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExpedientTascaComentariResourceServiceImpl extends BaseMutableResourceService<ExpedientTascaComentariResource, Long, ExpedientTascaComentariResourceEntity> implements ExpedientTascaComentariResourceService {

}
