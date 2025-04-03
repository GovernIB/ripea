package es.caib.ripea.back.resourcecontroller;

import es.caib.ripea.back.base.controller.BaseMutableResourceController;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.DocumentEnviamentInteressatResource;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servei REST de gestió d'aplicacions.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/documentEnviamentInteressats")
@Tag(name = "DocumentEnviamentInteressats", description = "Servei de gestió de interessats")
public class DocumentEnviamentInteressatResourceController extends BaseMutableResourceController<DocumentEnviamentInteressatResource, Long> {

}
