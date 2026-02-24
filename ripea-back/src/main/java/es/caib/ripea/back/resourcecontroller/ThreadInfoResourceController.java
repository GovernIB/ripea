package es.caib.ripea.back.resourcecontroller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.caib.ripea.back.base.controller.BaseMutableResourceController;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.ThreadInfoResource;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(BaseConfig.API_PATH + "/threadInfo")
@Tag(name = "Threads", description = "Monitorització de sistema i fils de execució (super-admin)")
public class ThreadInfoResourceController extends BaseMutableResourceController <ThreadInfoResource, Long> {}