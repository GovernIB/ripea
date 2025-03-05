package es.caib.ripea.service.base.helper;

import es.caib.ripea.persistence.base.entity.ResourceEntity;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.exception.ObjectMappingException;
import es.caib.ripea.service.intf.base.model.Resource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.base.util.TypeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;

/**
 * Mètodes per al mapeig d'objectes.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Component
public class ObjectMappingHelper {

	/**
	 * Retorna una instància de la classe especificada amb els camps amb el mateix nom mapejats.
	 *
	 * @param source
	 *            l'objecte d'orígen.
	 * @param targetClass
	 *            la classe que es vol obtenir.
	 * @return una nova instància de la classe especificada amb els camps mapejats.
	 * @param <T>
	 *            el tipus que es vol obtenir.
	 * @throws ObjectMappingException
	 *            quan es dona algun error en el mapeig.
	 */
	public <T> T newInstanceMap(
			Object source,
			Class<T> targetClass,
			String... ignoredFields) throws ObjectMappingException {
		if (source != null) {
			try {
				T target = getNewInstance(targetClass);
				if (target != null) {
					map(source, target, ignoredFields);
					return target;
				} else {
					throw new ObjectMappingException(
							source.getClass(),
							targetClass,
							"Couldn't find no args constructor or builder");
				}
			} catch (Exception ex) {
				throw new ObjectMappingException(source.getClass(), targetClass, ex);
			}
		} else {
			return null;
		}
	}

	/**
	 * Mapeja els camps entre l'objecte d'orígen i l'objecte destí que tenguin els mateixos noms.
	 *
	 * @param source
	 *            l'objecte d'orígen.
	 * @param target
	 *            l'objecte de destí.
	 * @throws ObjectMappingException
	 *            quan es dona algun error en el mapeig.
	 */
	public void map(
			Object source,
			Object target,
			String... ignoredFields) throws ObjectMappingException {
		ReflectionUtils.doWithFields(
				source.getClass(),
				sourceField -> {
					ReflectionUtils.makeAccessible(sourceField);
					Field targetField = ReflectionUtils.findField(target.getClass(), sourceField.getName());
					if (targetField != null) {
						if (isSimpleType(sourceField.getType())) {
							ReflectionUtils.makeAccessible(targetField);
							ReflectionUtils.setField(
									targetField,
									target,
									sourceField.get(source));
						} else if (ResourceEntity.class.isAssignableFrom(sourceField.getType()) && ResourceReference.class.isAssignableFrom(targetField.getType())) {
							ResourceEntity<?, ?> entity = (ResourceEntity<?, ?>)sourceField.get(source);
							ResourceReference<?, ?> resourceReference = null;
							if (entity != null) {
								resourceReference = toResourceReference(entity);
							}
							ReflectionUtils.makeAccessible(targetField);
							ReflectionUtils.setField(
									targetField,
									target,
									resourceReference);
						} else {
							ReflectionUtils.makeAccessible(targetField);
							ReflectionUtils.setField(
									targetField,
									target,
									null);
						}
					}
				},
				field -> ignoredFields == null || !Arrays.asList(ignoredFields).contains(field.getName()));
	}

	/**
	 * Crea un clon de l'objecte per a la lògica onChange.
	 *
	 * @param object
	 *            l'objecte d'orígen.
	 * @return el recurs clonat.
	 */
	public <O> O clone(O object) throws ObjectMappingException {
		if (object != null) {
			try {
				O cln = (O)getNewInstance(object.getClass());
				if (cln != null) {
					ReflectionUtils.doWithFields(object.getClass(), field -> {
						if (!isStaticFinal(field)) {
							try {
								Object value = getFieldValue(object, field.getName());
								ReflectionUtils.makeAccessible(field);
								ReflectionUtils.setField(
										field,
										cln,
										value);
							} catch (NoSuchFieldException ignored) {
							}
						}
					});
					return cln;
				} else {
					throw new ObjectMappingException(
							object.getClass(),
							object.getClass(),
							"Couldn't find no args constructor or builder");
				}
			} catch (Exception ex) {
				throw new ObjectMappingException(
						object.getClass(),
						object.getClass(),
						ex);
			}
		} else {
			return null;
		}
	}

	/**
	 * Retorna una nova instància de la classe especificada. Intenta crear la instància
	 * amb el constructor sense arguments o amb el mètode build() del builder.
	 *
	 * @param targetClass
	 *            la classe de la qual es vol crear la instància.
	 * @return la nova instància de la classe especificada o null si no s'ha pogut crear.
	 * @param <T> el tipus de la classe que es vol instanciar
	 */
	private <T> T getNewInstance(
			Class<T> targetClass) throws InvocationTargetException, InstantiationException, IllegalAccessException {
		Constructor<T> noArgsConstructor = Arrays.stream((Constructor<T>[])targetClass.getConstructors()).
				filter(c -> c.getParameterCount() == 0).
				findFirst().
				orElse(null);
		if (noArgsConstructor != null) {
			return noArgsConstructor.newInstance();
		} else {
			Method builderMethod = ReflectionUtils.findMethod(targetClass, "builder");
			if (builderMethod != null) {
				Class<?> builderReturnType = builderMethod.getReturnType();
				Object builderInstance = ReflectionUtils.invokeMethod(builderMethod, null);
				return (T)ReflectionUtils.invokeMethod(
						ReflectionUtils.findMethod(builderReturnType, "build"),
						builderInstance);
			}
		}
		return null;
	}

	private ResourceReference<?, ?> toResourceReference(
			ResourceEntity<?, ?> entity) {
		return ResourceReference.toResourceReference(
				(Serializable)entity.getId(),
				//entity.getEntityDescription());
				getResourceEntityDescription(entity));
	}

	private String getResourceEntityDescription(ResourceEntity<?, ?> persistable) {
		Class<? extends Resource<?>> resourceClass = TypeUtil.getArgumentClassFromGenericSuperclass(
				persistable.getClass(),
				ResourceEntity.class,
				0);
		String descriptionFieldName = getResourceDescriptionFieldName(resourceClass);
		if (descriptionFieldName != null) {
			try {
				return (String)getFieldValue(
						persistable,
						descriptionFieldName);
			} catch (Exception ex) {
				log.warn(
						"Couldn't find description field {} in entity class {}",
						descriptionFieldName,
						persistable.getClass().getName(),
						ex);
			}
		}
		return resourceClass.getSimpleName() + " (id=" + persistable.getId() + ")";
	}

	private String getResourceDescriptionFieldName(Class<? extends Resource<?>> resourceClass) {
		ResourceConfig resourceConfig = resourceClass.getAnnotation(ResourceConfig.class);
		if (resourceConfig != null) {
			String descriptionField = resourceConfig.descriptionField();
			if (!descriptionField.isEmpty()) {
				return descriptionField;
			} else {
				log.warn(
						"Couldn't find description field for resource class {}: ResourceConfig.descriptionField not configured",
						resourceClass.getName());
				return null;
			}
		} else {
			log.warn(
					"Couldn't find description field for resource class {}: ResourceConfig annotation not found",
					resourceClass.getName());
			return null;
		}
	}

	private boolean isSimpleType(Class<?> type) {
		return type.isPrimitive() ||
				Boolean.class == type ||
				Character.class == type ||
				(Serializable.class.isAssignableFrom(type) && !ResourceReference.class.isAssignableFrom(type)) ||
				CharSequence.class.isAssignableFrom(type) ||
				Number.class.isAssignableFrom(type) ||
				Date.class.isAssignableFrom(type) ||
				LocalTime.class.isAssignableFrom(type) ||
				LocalDateTime.class.isAssignableFrom(type) ||
				Enum.class.isAssignableFrom(type);
	}

	private Object getFieldValue(
			Object object,
			String fieldName) throws NoSuchFieldException {
		String getMethodName = methodNameFromField(fieldName, "get");
		Method method = ReflectionUtils.findMethod(object.getClass(), getMethodName);
		if (method != null) {
			return ReflectionUtils.invokeMethod(method, object);
		} else {
			Field field = object.getClass().getDeclaredField(fieldName);
			ReflectionUtils.makeAccessible(field);
			return ReflectionUtils.getField(field, object);
		}
	}

	private boolean isStaticFinal(Field field) {
		int modifiers = field.getModifiers();
		return Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
	}

	private String methodNameFromField(String fieldName, String prefix) {
		return prefix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
	}

}
