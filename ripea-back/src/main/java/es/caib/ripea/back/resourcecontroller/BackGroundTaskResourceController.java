package es.caib.ripea.back.resourcecontroller;

import es.caib.ripea.back.base.controller.BaseMutableResourceController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.caib.ripea.back.base.controller.BaseReadonlyResourceController;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.BackGroundTaskResource;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(BaseConfig.API_PATH + "/tasks")
@Tag(name = "Tasques en segon plà", description = "Monitorització de tasques en segon plà (super-admin)")
public class BackGroundTaskResourceController extends BaseMutableResourceController<BackGroundTaskResource, String> {}