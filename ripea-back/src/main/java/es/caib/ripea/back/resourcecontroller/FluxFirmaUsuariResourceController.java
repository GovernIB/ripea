package es.caib.ripea.back.resourcecontroller;

import es.caib.ripea.back.base.controller.BaseMutableResourceController;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.FluxFirmaUsuariResource;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servei REST de gestió dels fluxos de firma d'un usuari.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/fluxosFirmaUsuari")
@Tag(name = "FluxosFirmaUsuari", description = "Servei de gestió dels fluxos de firma d'un usuari")
public class FluxFirmaUsuariResourceController extends BaseMutableResourceController<FluxFirmaUsuariResource, Long> {

}
