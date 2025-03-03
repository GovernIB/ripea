package es.caib.ripea.back.resourcecontroller;

import es.caib.ripea.back.base.controller.BaseMutableResourceController;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.ExpedientPeticioResource;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servei REST de gestió de peticions d'expedients.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/expedientPeticions")
@Tag(name = "ExpedientPeticios", description = "Servei de gestió de peticions d'expedients")
public class ExpedientPeticioResourceController extends BaseMutableResourceController<ExpedientPeticioResource, Long> {

}
