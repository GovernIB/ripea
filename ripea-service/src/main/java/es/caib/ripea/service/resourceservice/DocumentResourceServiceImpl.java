package es.caib.ripea.service.resourceservice;

import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.resourceentity.DocumentResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.DocumentResource;
import es.caib.ripea.service.intf.resourceservice.DocumentResourceService;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementació del servei de gestió d'expedients.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
public class DocumentResourceServiceImpl extends BaseMutableResourceService<DocumentResource, Long, DocumentResourceEntity> implements DocumentResourceService {}
