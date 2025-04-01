package es.caib.ripea.back.resourcecontroller;

import es.caib.ripea.back.base.controller.BaseMutableResourceController;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.RegistreAnnexResource;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servei REST de gestió de peticions d'expedients.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/registreAnnexos")
@Tag(name = "RegistreAnnexos", description = "Servei de gestió d'annexos de registres")
public class RegistreAnnexResourceController extends BaseMutableResourceController<RegistreAnnexResource, Long> {

}
