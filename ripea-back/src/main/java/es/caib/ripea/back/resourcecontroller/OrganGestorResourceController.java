package es.caib.ripea.back.resourcecontroller;

import es.caib.ripea.back.base.controller.BaseMutableResourceController;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.OrganGestorResource;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servei REST de gestió d'òrgans gestors.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/organsGestors")
@Tag(name = "Òrgans gestors", description = "Servei de gestió d'òrgans gestors")
public class OrganGestorResourceController extends BaseMutableResourceController<OrganGestorResource, Long> {

}
