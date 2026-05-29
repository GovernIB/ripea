/**
 * 
 */
package es.caib.ripea.ejb.base;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.springframework.context.ConfigurableApplicationContext;

/**
 * Singleton utilitzat per a forçar la inicialització del context de Spring.
 *
 * @author Límit Tecnologies
 */
@Startup
@Singleton
@TransactionManagement(TransactionManagementType.BEAN)
public class EjbContextStartup {

	@PostConstruct
	private void startup() {
		EjbContextConfig.getApplicationContext();
	}

	@PreDestroy
	private void shutdown() {
		ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) EjbContextConfig.getApplicationContext();
		if (ctx != null && ctx.isActive()) {
			ctx.close();
		}
	}

}
