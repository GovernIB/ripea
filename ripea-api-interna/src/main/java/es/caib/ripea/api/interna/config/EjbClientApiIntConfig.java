package es.caib.ripea.api.interna.config;

import es.caib.ripea.service.intf.base.service.PermissionEvaluatorService;
import es.caib.ripea.service.intf.base.service.ResourceApiService;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWarDeployment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ejb.access.LocalStatelessSessionProxyFactoryBean;

/**
 * Configuració d'accés als services de Spring mitjançant EJBs.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Configuration
public class EjbClientApiIntConfig {

	static final String EJB_JNDI_PREFIX = "java:app/" + BaseConfig.APP_NAME + "-ejb/";
	static final String EJB_JNDI_SUFFIX = "Ejb";

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean documentService() {
		return getLocalEjbFactoyBean(DocumentService.class);
	}
	
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean historicService() {
		return getLocalEjbFactoyBean(HistoricService.class);
	}
	
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean aplicacioService() {
		return getLocalEjbFactoyBean(AplicacioService.class);
	}
	
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean resourceApiService() {
		return getLocalEjbFactoyBean(ResourceApiService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean permissionEvaluatorService() {
		return getLocalEjbFactoyBean(PermissionEvaluatorService.class);
	}
	
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean expedientPeticioService() {
		return getLocalEjbFactoyBean(ExpedientPeticioService.class);
	}
	
	private LocalStatelessSessionProxyFactoryBean getLocalEjbFactoyBean(Class<?> serviceClass) {
		String jndiName = jndiServiceName(serviceClass, false);
		log.info("Creating EJB proxy for " + serviceClass.getSimpleName() + " with JNDI name " + jndiName);
		LocalStatelessSessionProxyFactoryBean factoryBean = new LocalStatelessSessionProxyFactoryBean();
		factoryBean.setBusinessInterface(serviceClass);
		factoryBean.setExpectedType(serviceClass);
		factoryBean.setJndiName(jndiName);
		return factoryBean;
	}

	private String jndiServiceName(Class<?> serviceClass, boolean addServiceClassName) {
		return EJB_JNDI_PREFIX + serviceClass.getSimpleName() + EJB_JNDI_SUFFIX + (addServiceClassName ? "!" + serviceClass.getName() : "");
	}

}