package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.UsuariResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.UsuariResource;
import es.caib.ripea.service.intf.resourceservice.UsuariResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de gestió d'usuaris.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UsuariResourceServiceImpl extends BaseMutableResourceService<UsuariResource, String, UsuariResourceEntity> implements UsuariResourceService {

}
