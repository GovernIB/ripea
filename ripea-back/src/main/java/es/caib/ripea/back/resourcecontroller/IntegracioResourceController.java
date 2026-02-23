package es.caib.ripea.back.resourcecontroller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.caib.ripea.back.base.controller.BaseReadonlyResourceController;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.IntegracioResource;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(BaseConfig.API_PATH + "/integracio")
@Tag(name = "Integracions", description = "Monitorització d'integracions de l'aplicació (super-admin)")
public class IntegracioResourceController extends BaseReadonlyResourceController <IntegracioResource, Long> {}