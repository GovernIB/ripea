package es.caib.ripea.back.resourcecontroller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.caib.ripea.back.base.controller.BaseMutableResourceController;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.ExpedientSeguidorId;
import es.caib.ripea.service.intf.model.ExpedientSeguidorResource;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(BaseConfig.API_PATH + "/expedientSeguidors")
@Tag(name = "ExpedientSeguidors", description = "Servei de gestió de seguidors de un expedient")
public class ExpedientSeguidorResourceController extends BaseMutableResourceController<ExpedientSeguidorResource, ExpedientSeguidorId> {}