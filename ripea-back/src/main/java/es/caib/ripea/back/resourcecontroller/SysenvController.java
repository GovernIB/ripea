package es.caib.ripea.back.resourcecontroller;

import es.caib.ripea.back.base.controller.BaseSysenvController;
import es.caib.ripea.service.intf.config.PropertyConfig;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador per retornar la informació de les variables d'entorn.
 * 
 * @author Limit Tecnologies
 */
@Hidden
@RestController
public class SysenvController extends BaseSysenvController {

	protected boolean isReactAppMappedFrontProperty(String propertyName) {
		return propertyName.startsWith(PropertyConfig.PROPERTY_PREFIX_FRONT) && PropertyConfig.REACT_APP_PROPS_MAP.containsKey(propertyName);
	}
	protected String getReactAppMappedFrontProperty(String propertyName) {
		return PropertyConfig.REACT_APP_PROPS_MAP.get(propertyName);
	}
	protected boolean isViteMappedFrontProperty(String propertyName) {
		return propertyName.startsWith(PropertyConfig.PROPERTY_PREFIX_FRONT) && PropertyConfig.VITE_PROPS_MAP.containsKey(propertyName);
	}
	protected String getViteMappedFrontProperty(String propertyName) {
		return PropertyConfig.VITE_PROPS_MAP.get(propertyName);
	}

}
