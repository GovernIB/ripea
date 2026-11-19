package es.caib.ripea.back.resourcecontroller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.caib.ripea.back.base.controller.BaseReadonlyResourceController;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.IniciResource;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(BaseConfig.API_PATH + "/inici")
@Tag(name = "Inici", description = "Pantalla d'inici: indicadors i llistes resum del rol actiu")
public class IniciResourceController extends BaseReadonlyResourceController<IniciResource, Long> {}
