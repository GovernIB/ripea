package es.caib.ripea.back.resourcecontroller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.caib.ripea.back.base.controller.BaseMutableResourceController;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.MetaExpedientEstatResource;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Servei REST de gestió d'expedients.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/expedientEstats")
@Tag(name = "MetaExpedientEstats", description = "Servei de gestió de estats de un procediment")
public class MetaExpedientEstatResourceController extends BaseMutableResourceController<MetaExpedientEstatResource, Long> {}