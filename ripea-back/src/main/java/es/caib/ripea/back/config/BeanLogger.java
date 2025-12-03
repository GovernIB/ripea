package es.caib.ripea.back.config;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class BeanLogger {

    @Autowired
    private ApplicationContext applicationContext;
    private static final Logger logger = LoggerFactory.getLogger(BeanLogger.class);
    
    @PostConstruct
    public void logBeans() {
    	if (logger.isDebugEnabled()) {
	        String[] beanNames = applicationContext.getBeanDefinitionNames();
	        logger.debug("=== Beans en el contexto Spring del WAR ===");
	        for (String name : beanNames) {
	            Object bean = applicationContext.getBean(name);
	            if (bean!=null) {
	            	if (bean.getClass()!=null && !bean.getClass().getName().startsWith("com.sun.proxy")) {
	            		logger.debug(name + " -> " + bean.getClass().getName());
	            	} else {
	            		logger.debug(name + " -> " + bean.toString());
	            	}
	            }
	        }
    	}
    }
}