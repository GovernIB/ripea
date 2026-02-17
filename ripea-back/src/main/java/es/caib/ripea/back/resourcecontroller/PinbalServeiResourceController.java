package es.caib.ripea.back.resourcecontroller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.caib.ripea.back.base.controller.BaseMutableResourceController;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.PinbalServeiResource;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(BaseConfig.API_PATH + "/serveiPinbal")
@Tag(name = "Servei Pinbal", description = "Gestió de serveis pinbal (super-admin)")
public class PinbalServeiResourceController extends BaseMutableResourceController<PinbalServeiResource, Long> {}