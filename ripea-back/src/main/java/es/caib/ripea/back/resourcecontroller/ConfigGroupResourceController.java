package es.caib.ripea.back.resourcecontroller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.caib.ripea.back.base.controller.BaseMutableResourceController;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.ConfigGroupResource;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(BaseConfig.API_PATH + "/configGroup")
@Tag(name = "ConfigGroup", description = "Servei de gestió de grups de propietats configurables per l'aplicació")
public class ConfigGroupResourceController extends BaseMutableResourceController<ConfigGroupResource, String> {}