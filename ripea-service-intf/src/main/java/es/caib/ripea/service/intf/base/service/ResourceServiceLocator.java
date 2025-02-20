package es.caib.ripea.service.intf.base.service;

import es.caib.ripea.service.intf.base.exception.ComponentNotFoundException;
import es.caib.ripea.service.intf.base.util.TypeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Localitzador de serveis de tipus ResourceService donat un recurs.
 * 
 * @author Límit Tecnologies
 */
@Component
public class ResourceServiceLocator implements ApplicationContextAware {

	@Autowired(required = false)
	protected List<ReadonlyResourceService<?, ?>> resourceServices;

	public ReadonlyResourceService<?, ?> getReadOnlyEntityResourceServiceForResourceClass(
			Class<?> resourceClass) throws ComponentNotFoundException {
		ReadonlyResourceService<?, ?> resourceServiceFound = null;
		for (ReadonlyResourceService<?, ?> resourceService: resourceServices) {
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
			throw new ComponentNotFoundException(resourceClass, "readonlyResourceService");
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

	private static ApplicationContext applicationContext;
	public static ResourceServiceLocator getInstance() {
		if (applicationContext != null) {
			return applicationContext.getBean(ResourceServiceLocator.class);
		} else {
			return null;
		}
	}
	@Override
	public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
		ResourceServiceLocator.applicationContext = applicationContext;
	}

}
