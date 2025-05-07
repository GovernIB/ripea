package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.AlertaResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.AlertaResource;
import es.caib.ripea.service.intf.resourceservice.AlertaResourceService;
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
public class AlertaResourceServiceImpl extends BaseMutableResourceService<AlertaResource, Long, AlertaResourceEntity> implements AlertaResourceService {

}
