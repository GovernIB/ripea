package es.caib.ripea.back.resourcecontroller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.caib.ripea.back.base.controller.BaseReadonlyResourceController;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.ExcepcioLogResource;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(BaseConfig.API_PATH + "/excepcio")
@Tag(name = "Excepcions", description = "Monitorització de excepcions de l'aplicació (super-admin)")
public class ExcepcioLogResourceController extends BaseReadonlyResourceController <ExcepcioLogResource, Long> {}