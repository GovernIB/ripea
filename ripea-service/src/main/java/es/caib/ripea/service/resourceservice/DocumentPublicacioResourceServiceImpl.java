package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.DocumentPublicacioResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.DocumentPublicacioResource;
import es.caib.ripea.service.intf.resourceservice.DocumentPublicacioResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de gestió d'expedients.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentPublicacioResourceServiceImpl extends BaseMutableResourceService<DocumentPublicacioResource, Long, DocumentPublicacioResourceEntity> implements DocumentPublicacioResourceService {

}