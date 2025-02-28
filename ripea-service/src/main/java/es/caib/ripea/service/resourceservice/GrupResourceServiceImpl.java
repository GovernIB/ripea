package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.GrupResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.GrupResource;
import es.caib.ripea.service.intf.resourceservice.GrupResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de gestió de grups.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GrupResourceServiceImpl extends BaseMutableResourceService<GrupResource, Long, GrupResourceEntity> implements GrupResourceService {

}
