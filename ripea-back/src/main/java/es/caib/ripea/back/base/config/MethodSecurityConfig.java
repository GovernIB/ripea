package es.caib.ripea.back.base.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * Configuració de la seguretat a nivell de mètode.
 * 
 * @author Límit Tecnologies
 */
@Configuration
@DependsOn({ "permissionEvaluatorService" })
//@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class MethodSecurityConfig /*extends GlobalMethodSecurityConfiguration*/ {

	/*@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private PermissionEvaluatorService permissionEvaluatorService;

	@Override
	protected MethodSecurityExpressionHandler createExpressionHandler() {
		DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
		expressionHandler.setApplicationContext(applicationContext);
		expressionHandler.setPermissionEvaluator(permissionEvaluatorService);
		return expressionHandler;
	}*/

}
