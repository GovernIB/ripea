package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.RegistreInteressatResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.RegistreInteressatResource;
import es.caib.ripea.service.intf.resourceservice.RegistreInteressatResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de gestió de peticions d'expedients.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegistreInteressatResourceServiceImpl extends BaseMutableResourceService<RegistreInteressatResource, Long, RegistreInteressatResourceEntity> implements RegistreInteressatResourceService {

}
