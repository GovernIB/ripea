package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.EntitatResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.resourceservice.EntitatResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de gestió d'entitats.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EntitatResourceServiceImpl extends BaseMutableResourceService<EntitatResource, Long, EntitatResourceEntity> implements EntitatResourceService {

}