package es.caib.ripea.service.resourceservice;

import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.resourceentity.InteressatResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.InteressatResource;
import es.caib.ripea.service.intf.resourceservice.InteressatResourceService;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementació del servei de gestió d'expedients.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
public class InteressatResourceServiceImpl extends BaseMutableResourceService<InteressatResource, Long, InteressatResourceEntity> implements InteressatResourceService {}