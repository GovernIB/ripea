package es.caib.ripea.back.resourcecontroller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.caib.ripea.back.base.controller.BaseMutableResourceController;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.DocumentResource;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Servei REST de gestió d'aplicacions.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/documents")
@Tag(name = "Documents", description = "Servei de gestió de documents")
public class DocumentResourceController extends BaseMutableResourceController<DocumentResource, Long> {

}
