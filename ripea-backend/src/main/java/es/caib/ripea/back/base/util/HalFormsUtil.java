package es.caib.ripea.back.base.util;

import es.caib.ripea.service.intf.base.annotation.ResourceField;
import es.caib.ripea.service.intf.base.exception.ComponentNotFoundException;
import es.caib.ripea.service.intf.base.exception.ResourceNotCreatedException;
import es.caib.ripea.service.intf.base.service.MutableResourceService;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utilitats per a HAL-FORMS.
 *
 * @author Límit Tecnologies
 */
public class HalFormsUtil {

	/** Atribut de la petició amb els valors d'instàncies noves ja calculats (classe del recurs → valors). */
	private static final String REQUEST_ATTRIBUTE_NEW_RESOURCE_VALUES = HalFormsUtil.class.getName() + ".newResourceValues";

	/** Camps declarats per classe i nom (Optional.empty() si la classe no declara el camp). */
	private static final Map<Class<?>, Map<String, Optional<Field>>> DECLARED_FIELDS = new ConcurrentHashMap<>();

	/**
	 * Valors dels camps d'una instància nova del recurs (els valors per defecte dels formularis).
	 * Dins d'una mateixa petició es calculen un sol cop per classe: la generació dels formularis
	 * HAL-FORMS en demana els valors per a cada camp. No es guarden entre peticions perquè poden
	 * dependre del moment (p. ex. dates relatives a avui) o de l'usuari.
	 *
	 * @return mapa no modificable.
	 */
	public static Map<String, Object> getNewResourceValues(
			Class<?> resourceClass,
			ResourceServiceLocator resourceServiceLocator) throws ResourceNotCreatedException {
		if (resourceServiceLocator == null) {
			return Collections.emptyMap();
		}
		Map<Class<?>, Map<String, Object>> requestCache = getRequestNewResourceValuesCache();
		if (requestCache == null) {
			return calculateNewResourceValues(resourceClass, resourceServiceLocator);
		}
		Map<String, Object> values = requestCache.get(resourceClass);
		if (values == null) {
			values = calculateNewResourceValues(resourceClass, resourceServiceLocator);
			requestCache.put(resourceClass, values);
		}
		return values;
	}

	public static <T extends Annotation> T getFieldAnnotation(
			Class<?> resourceClass,
			String fieldName,
			Class<T> annotationClass) {
		return getDeclaredField(resourceClass, fieldName).
				map(field -> field.getAnnotation(annotationClass)).
				orElse(null);
	}

	public static boolean isOnChangeActive(Class<?> resourceClass, String fieldName) {
		return getDeclaredField(resourceClass, fieldName).
				map(field -> field.getAnnotation(ResourceField.class)).
				map(ResourceField::onChangeActive).
				orElse(false);
	}

	private static Map<String, Object> calculateNewResourceValues(
			Class<?> resourceClass,
			ResourceServiceLocator resourceServiceLocator) throws ResourceNotCreatedException {
		Map<String, Object> values = new HashMap<>();
		Optional<MutableResourceService<?, ?>> mutableResourceService = resourceServiceLocator.
				findMutableEntityResourceServiceForResourceClass(resourceClass);
		boolean calculated = false;
		if (mutableResourceService.isPresent()) {
			try {
				Object newInstance = mutableResourceService.get().newResourceInstance();
				if (newInstance != null) {
					values.putAll(toMap(newInstance));
				}
				calculated = true;
			} catch (ComponentNotFoundException ignored) {
				// Mateix comportament que abans: es crea la instància amb el constructor per defecte
			}
		}
		if (!calculated) {
			try {
				values.putAll(toMap(resourceClass.getDeclaredConstructor().newInstance()));
			} catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
			}
		}
		return Collections.unmodifiableMap(values);
	}

	@SuppressWarnings("unchecked")
	private static Map<Class<?>, Map<String, Object>> getRequestNewResourceValuesCache() {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes == null) {
			return null;
		}
		Map<Class<?>, Map<String, Object>> cache = (Map<Class<?>, Map<String, Object>>)requestAttributes.getAttribute(
				REQUEST_ATTRIBUTE_NEW_RESOURCE_VALUES,
				RequestAttributes.SCOPE_REQUEST);
		if (cache == null) {
			cache = new ConcurrentHashMap<>();
			requestAttributes.setAttribute(
					REQUEST_ATTRIBUTE_NEW_RESOURCE_VALUES,
					cache,
					RequestAttributes.SCOPE_REQUEST);
		}
		return cache;
	}

	/**
	 * Equivalent a Class.getDeclaredField (només camps declarats a la mateixa classe, no heretats),
	 * però sense llançar ni crear una excepció a cada consulta d'un camp que no hi és.
	 */
	private static Optional<Field> getDeclaredField(Class<?> resourceClass, String fieldName) {
		return DECLARED_FIELDS.
				computeIfAbsent(resourceClass, c -> new ConcurrentHashMap<>()).
				computeIfAbsent(fieldName, name -> {
					try {
						return Optional.of(resourceClass.getDeclaredField(name));
					} catch (NoSuchFieldException e) {
						return Optional.empty();
					}
				});
	}

	private static Map<String, Object> toMap(Object object) {
		Map<String, Object> map = new HashMap<>();
		Field[] fields = object.getClass().getDeclaredFields();
		for (Field field: fields) {
			field.setAccessible(true);
			try {
				Object value = field.get(object);
				if (value != null) {
					map.put(field.getName(), value);
				}
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		return map;
	}

}
