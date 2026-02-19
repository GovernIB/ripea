package es.caib.ripea.back.resourcecontroller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.caib.ripea.back.base.controller.BaseMutableResourceController;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.ConfigTypeResource;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(BaseConfig.API_PATH + "/configType")
@Tag(name = "ConfigType", description = "Servei de gestió de tipus de propietats configurables per l'aplicació")
public class ConfigTypeResourceController extends BaseMutableResourceController<ConfigTypeResource, String> {}