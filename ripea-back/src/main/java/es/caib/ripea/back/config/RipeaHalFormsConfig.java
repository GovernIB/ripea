package es.caib.ripea.back.config;

import es.caib.ripea.back.base.config.BaseHalFormsConfig;
import es.caib.ripea.back.resourcecontroller.ExpedientResourceController;
import org.springframework.context.annotation.Configuration;

/**
 * Configuració de HAL-FORMS.
 * 
 * @author Límit Tecnologies
 */
@Configuration
public class RipeaHalFormsConfig extends BaseHalFormsConfig {

	@Override
	protected String[] getControllerPackages() {
		return new String[] {
				ExpedientResourceController.class.getPackageName()
		};
	}

}
