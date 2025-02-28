package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientTascaResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.MetaExpedientTascaResource;
import es.caib.ripea.service.intf.resourceservice.MetaExpedientTascaResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de gestió de tasques.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetaExpedientTascaResourceServiceImpl extends BaseMutableResourceService<MetaExpedientTascaResource, Long, MetaExpedientTascaResourceEntity> implements MetaExpedientTascaResourceService {

}
