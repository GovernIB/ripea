package es.caib.ripea.service.intf.base.util;

import es.caib.ripea.service.intf.base.annotation.ResourceField;
import es.caib.ripea.service.intf.base.exception.ComponentNotFoundException;
import es.caib.ripea.service.intf.base.exception.ResourceNotCreatedException;
import es.caib.ripea.service.intf.base.service.MutableResourceService;
import es.caib.ripea.service.intf.base.service.ResourceServiceLocator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilitats per a HAL-FORMS.
 * 
 * @author Límit Tecnologies
 */
public class HalFormsUtil {

	public static Map<String, Object> getNewResourceValues(Class<?> resourceClass) throws ResourceNotCreatedException {
		Map<String, Object> values = new HashMap<>();
		ResourceServiceLocator resourceServiceLocator = ResourceServiceLocator.getInstance();
		if (resourceServiceLocator != null) {
			try {
				MutableResourceService<?, ?> mutableResourceService = ResourceServiceLocator.getInstance().
						getMutableEntityResourceServiceForResourceClass(resourceClass);
				Object newInstance = mutableResourceService.newResourceInstance();
				if (newInstance != null) {
					values.putAll(toMap(newInstance));
				}
			} catch (ComponentNotFoundException ignored) {}
		}
		return values;
	}

	public static <T extends Annotation> T getFieldAnnotation(
			Class<?> resourceClass,
			String fieldName,
			Class<T> annotationClass) {
		try {
			return resourceClass.getDeclaredField(fieldName).getAnnotation(annotationClass);
		} catch (NoSuchFieldException e) {
			return null;
		}
	}

	public static boolean isOnChangeActive(Class<?> resourceClass, String fieldName) {
		try {
			ResourceField resourceField = resourceClass.
					getDeclaredField(fieldName).
					getAnnotation(ResourceField.class);
			return resourceField != null && resourceField.onChangeActive();
		} catch (NoSuchFieldException e) {
			return false;
		}
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
