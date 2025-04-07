package es.caib.ripea.back.base.config;

import es.caib.ripea.service.intf.base.service.PermissionEvaluatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

/**
 * Configuració de la seguretat a nivell de mètode.
 * 
 * @author Límit Tecnologies
 */
@Configuration
@DependsOn({ "permissionEvaluatorService" })
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private PermissionEvaluatorService permissionEvaluatorService;

	@Override
	protected MethodSecurityExpressionHandler createExpressionHandler() {
		DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
		expressionHandler.setApplicationContext(applicationContext);
		expressionHandler.setPermissionEvaluator(permissionEvaluatorService);
		return expressionHandler;
	}

}
