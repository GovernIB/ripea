package es.caib.ripea.back.base.util;

import es.caib.ripea.service.intf.base.exception.ComponentNotFoundException;
import es.caib.ripea.service.intf.base.service.MutableResourceService;
import es.caib.ripea.service.intf.base.service.ReadonlyResourceService;
import es.caib.ripea.service.intf.base.util.TypeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Localitzador de serveis de tipus ResourceService donat un recurs.
 *
 * @author Límit Tecnologies
 */
@Component
public class ResourceServiceLocator {

	@Autowired(required = false)
	private Collection<ReadonlyResourceService<?, ?>> readonlyResourceServices;

	/**
	 * Servei de cada classe de recurs (Optional.empty() si no en té). Els serveis no canvien mentre
	 * l'aplicació està en marxa, i la cerca recorre tots els serveis resolent-ne els tipus genèrics:
	 * la generació dels formularis HAL-FORMS la repeteix per a cada camp.
	 */
	private final Map<Class<?>, Optional<ReadonlyResourceService<?, ?>>> readonlyResourceServiceByResourceClass = new ConcurrentHashMap<>();

	public ReadonlyResourceService<?, ?> getReadOnlyEntityResourceServiceForResourceClass(
			Class<?> resourceClass) throws ComponentNotFoundException {
		return findReadOnlyEntityResourceServiceForResourceClass(resourceClass).orElseThrow(
				() -> new ComponentNotFoundException(resourceClass, "ReadonlyResourceService for resource class " + resourceClass.getName()));
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

	/** Com getReadOnlyEntityResourceServiceForResourceClass, però sense llançar excepció si no n'hi ha. */
	public Optional<ReadonlyResourceService<?, ?>> findReadOnlyEntityResourceServiceForResourceClass(
			Class<?> resourceClass) {
		return readonlyResourceServiceByResourceClass.computeIfAbsent(
				resourceClass,
				this::searchReadOnlyEntityResourceServiceForResourceClass);
	}

	/** Com getMutableEntityResourceServiceForResourceClass, però sense llançar excepció si no n'hi ha. */
	public Optional<MutableResourceService<?, ?>> findMutableEntityResourceServiceForResourceClass(
			Class<?> resourceClass) {
		return findReadOnlyEntityResourceServiceForResourceClass(resourceClass).
				filter(MutableResourceService.class::isInstance).
				map(service -> (MutableResourceService<?, ?>)service);
	}

	private Optional<ReadonlyResourceService<?, ?>> searchReadOnlyEntityResourceServiceForResourceClass(
			Class<?> resourceClass) {
		if (readonlyResourceServices != null) {
			for (ReadonlyResourceService<?, ?> resourceService: readonlyResourceServices) {
				Class<?> serviceResourceClass = TypeUtil.getArgumentClassFromGenericSuperclass(
						resourceService.getClass(),
						ReadonlyResourceService.class,
						0);
				if (resourceClass.equals(serviceResourceClass)) {
					return Optional.of(resourceService);
				}
			}
		}
		return Optional.empty();
	}

}
