package es.caib.ripea.back.base.util;

import es.caib.ripea.service.intf.base.exception.ComponentNotFoundException;
import es.caib.ripea.service.intf.base.service.MutableResourceService;
import es.caib.ripea.service.intf.base.service.ReadonlyResourceService;
import es.caib.ripea.service.intf.base.util.TypeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * Localitzador de serveis de tipus ResourceService donat un recurs.
 * 
 * @author Límit Tecnologies
 */
@Component
public class ResourceServiceLocator implements ApplicationContextAware {

	@Autowired
	private ApplicationContext applicationContext;

	private Collection<ReadonlyResourceService> readonlyResourceServices;

	public ReadonlyResourceService<?, ?> getReadOnlyEntityResourceServiceForResourceClass(
			Class<?> resourceClass) throws ComponentNotFoundException {
		ReadonlyResourceService<?, ?> resourceServiceFound = null;
		for (ReadonlyResourceService<?, ?> resourceService: getReadonlyResourceServices()) {
			Class<?> serviceResourceClass = TypeUtil.getArgumentClassFromGenericSuperclass(
					resourceService.getClass(),
					ReadonlyResourceService.class,
					0);
			if (resourceClass.equals(serviceResourceClass)) {
				resourceServiceFound = resourceService;
				break;
			}
		}
		if (resourceServiceFound != null) {
			return resourceServiceFound;
		} else {
			throw new ComponentNotFoundException(resourceClass, "ReadonlyResourceService");
		}
	}

	public MutableResourceService<?, ?> getMutableEntityResourceServiceForResourceClass(
			Class<?> resourceClass) throws ComponentNotFoundException {
		ReadonlyResourceService<?, ?> readOnlyService = getReadOnlyEntityResourceServiceForResourceClass(resourceClass);
		if (readOnlyService instanceof MutableResourceService) {
			return (MutableResourceService<?, ?>)readOnlyService;
		} else {
			throw new ComponentNotFoundException(resourceClass, "mutableResourceService");
		}
	}

	private Collection<ReadonlyResourceService> getReadonlyResourceServices() {
		if (readonlyResourceServices == null) {
			Map<String, ReadonlyResourceService> resourceServiceBeans = applicationContext.getBeansOfType(
					ReadonlyResourceService.class);
			readonlyResourceServices = resourceServiceBeans.values();
		}
		return readonlyResourceServices;
	}

	private static final ThreadLocal<ResourceServiceLocator> threadLocalInstance = new ThreadLocal<>();
	private static ApplicationContext staticApplicationContext;
	public static ResourceServiceLocator getInstance() {
		if (threadLocalInstance.get() != null) {
			return threadLocalInstance.get();
		} else if (staticApplicationContext != null) {
			return staticApplicationContext.getBean(ResourceServiceLocator.class);
		} else {
			throw new ComponentNotFoundException(ResourceServiceLocator.class);
		}
	}
	public static void setThreadLocalInstance(ResourceServiceLocator instance) {
		threadLocalInstance.set(instance);
	}
	@Override
	public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
		ResourceServiceLocator.staticApplicationContext = applicationContext;
	}

}
