package es.caib.ripea.back.resourcecontroller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.caib.ripea.back.base.controller.BaseMutableResourceController;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.AvisResource;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(BaseConfig.API_PATH + "/avis")
@Tag(name = "Avisos", description = "Gestió de avisos de l'aplicació (super-admin)")
public class AvisResourceController extends BaseMutableResourceController<AvisResource, Long> {}