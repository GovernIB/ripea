package es.caib.ripea.back.base.util;

import es.caib.ripea.service.intf.base.exception.ComponentNotFoundException;
import es.caib.ripea.service.intf.base.service.MutableResourceService;
import es.caib.ripea.service.intf.base.service.ReadonlyResourceService;
import es.caib.ripea.service.intf.base.util.TypeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Localitzador de serveis de tipus ResourceService donat un recurs.
 * 
 * @author Límit Tecnologies
 */
@Component
public class ResourceServiceLocator {

	@Autowired(required = false)
	private Collection<ReadonlyResourceService<?, ?>> readonlyResourceServices;

	public ReadonlyResourceService<?, ?> getReadOnlyEntityResourceServiceForResourceClass(
			Class<?> resourceClass) throws ComponentNotFoundException {
		ReadonlyResourceService<?, ?> resourceServiceFound = null;
		if (readonlyResourceServices != null) {
			for (ReadonlyResourceService<?, ?> resourceService: readonlyResourceServices) {
				Class<?> serviceResourceClass = TypeUtil.getArgumentClassFromGenericSuperclass(
						resourceService.getClass(),
						ReadonlyResourceService.class,
						0);
				if (resourceClass.equals(serviceResourceClass)) {
					resourceServiceFound = resourceService;
					break;
				}
			}
		}
		if (resourceServiceFound != null) {
			return resourceServiceFound;
		} else {
			throw new ComponentNotFoundException(resourceClass, "ReadonlyResourceService for resource class " + resourceClass.getName());
		}
	}

	public MutableResourceService<?, ?> getMutableEntityResourceServiceForResourceClass(
			Class<?> resourceClass) throws ComponentNotFoundException {
		ReadonlyResourceService<?, ?> readOnlyService = getReadOnlyEntityResourceServiceForResourceClass(resourceClass);
		if (readOnlyService instanceof MutableResourceService) {
			return (MutableResourceService<?, ?>)readOnlyService;
		} else {
			throw new ComponentNotFoundException(resourceClass, "MutableResourceService for resource class " + resourceClass.getName());
		}
	}

}
