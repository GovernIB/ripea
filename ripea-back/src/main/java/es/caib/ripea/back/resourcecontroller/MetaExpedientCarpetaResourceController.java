package es.caib.ripea.back.resourcecontroller;

import es.caib.ripea.back.base.controller.BaseMutableResourceController;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.MetaExpedientCarpetaResource;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servei REST de gestió d'expedients.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/metaExpedientCarpetes")
@Tag(name = "MetaExpedientCarpetes", description = "Servei de gestió de carpetes de un procediment")
public class MetaExpedientCarpetaResourceController extends BaseMutableResourceController<MetaExpedientCarpetaResource, Long> {}