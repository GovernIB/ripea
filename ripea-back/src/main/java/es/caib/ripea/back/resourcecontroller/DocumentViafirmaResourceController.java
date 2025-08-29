package es.caib.ripea.back.resourcecontroller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.caib.ripea.back.base.controller.BaseMutableResourceController;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.DocumentViaFirmaResource;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(BaseConfig.API_PATH + "/documentViafirma")
@Tag(name = "DocumentViafirma", description = "Servei de gestió de enviaments a Viafirma de un document")
public class DocumentViafirmaResourceController extends BaseMutableResourceController<DocumentViaFirmaResource, Long> {}