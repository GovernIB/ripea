package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.OrganGestorResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.OrganGestorResource;
import es.caib.ripea.service.intf.resourceservice.OrganGestorResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de gestió d'òrgans gestors.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
public class OrganGestorResourceServiceImpl extends BaseMutableResourceService<OrganGestorResource, Long, OrganGestorResourceEntity> implements OrganGestorResourceService {

}
