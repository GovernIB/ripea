package es.caib.ripea.back.resourcecontroller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.caib.ripea.back.base.controller.BaseMutableResourceController;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.InteressatGrupResource;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Servei REST de gestió de grups dels interessats.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/interessatsGrups")
@Tag(name = "InteressatsGrups", description = "Servei de gestió de grups dels interessats")
public class InteressatGrupResourceController extends BaseMutableResourceController<InteressatGrupResource, Long> {

}
