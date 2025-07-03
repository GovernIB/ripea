package es.caib.ripea.service.base.helper;

import es.caib.ripea.persistence.base.entity.ResourceEntity;
import es.caib.ripea.service.intf.base.exception.ResourceNotCreatedException;
import es.caib.ripea.service.intf.base.model.FileReference;
import es.caib.ripea.service.intf.base.model.Resource;
import es.caib.ripea.service.intf.base.util.TypeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Persistable;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Mètodes per a transformar recursos en entitats i a l'inrevés.
 *
 * @author Límit Tecnologies
 */
@Component
public class ResourceEntityMappingHelper {

	private final ObjectMappingHelper objectMappingHelper;

	@Autowired
	public ResourceEntityMappingHelper(ObjectMappingHelper objectMappingHelper) {
		this.objectMappingHelper = objectMappingHelper;
	}

	public <E extends ResourceEntity<R, ?>, R extends Resource<?>> E resourceToEntity(
			R resource,
			Serializable pkFromResource,
			Class<E> entityClass,
			Map<String, Persistable<?>> referencedEntities) throws ResourceNotCreatedException {
		Method builderMethod = ReflectionUtils.findMethod(entityClass, "builder");
		if (builderMethod != null) {
			Object builderInstance = ReflectionUtils.invokeMethod(builderMethod, null);
			Class<?> builderReturnType = builderMethod.getReturnType();
			callBuilderMethodForResource(
					resource,
					entityClass,
					builderInstance,
					builderReturnType);
			if (referencedEntities != null) {
				// Es criden els altres mètodes del builder que accepten només 1 argument de tipus Persistable
				for (Method builderCallableMethod: ReflectionUtils.getDeclaredMethods(builderReturnType)) {
					if (builderCallableMethod.getParameterTypes().length == 1) {
						String builderMethodName = builderCallableMethod.getName();
						Class<?> builderMethodArgType = builderCallableMethod.getParameterTypes()[0];
						if (Persistable.class.isAssignableFrom(builderMethodArgType)) {
							Persistable<?> referencedEntity = referencedEntities.get(builderMethodName);
							if (referencedEntity != null) {
								ReflectionUtils.invokeMethod(builderCallableMethod, builderInstance, referencedEntity);
							}
						}
					}
				}
			}
			// Es crida el mètode build per a crear la instància de l'entitat
			E entity = (E)ReflectionUtils.invokeMethod(
					ReflectionUtils.findMethod(builderReturnType, "build"),
					builderInstance);
			// Només inicialitza el camp id si la pk te un valor diferent a null
			if (pkFromResource != null) {
				Field idField = ReflectionUtils.findField(entityClass, "id");
				if (idField != null) {
					idField.setAccessible(true);
					ReflectionUtils.setField(
							idField,
							entity,
							pkFromResource);
				}
			}
			return entity;
		} else {
			try {
				return entityClass.getConstructor().newInstance();
			} catch (Exception ex) {
				throw new ResourceNotCreatedException(
						resource.getClass(),
						"Couldn't create new entity instance (resourceClass=" + entityClass + ")",
						ex);
			}
		}
	}

	public <E extends ResourceEntity<R, ?>, R extends Resource<?>> void updateEntityWithResource(
			E entity,
			R resource,
			Map<String, Persistable<?>> referencedEntities) {
		Set<String> ignoredFieldNames = new HashSet<>();
		// Actualitza els camps de l'entitat que son de tipus Persistable, FileReference o Collection
		ReflectionUtils.doWithFields(entity.getClass(), field -> {
			if (Persistable.class.isAssignableFrom(field.getType()) && referencedEntities != null) {
				// Es modifica el valor dels camps de tipus de Persistable amb la referencia especificada al resource.
				Persistable<?> referencedEntity = referencedEntities.get(field.getName());
				String setMethodName = "set" + TypeUtil.getMethodSuffixFromField(field);
				Method setMethod = ReflectionUtils.findMethod(
						entity.getClass(),
						setMethodName,
						field.getType());
				if (setMethod != null) {
					ReflectionUtils.invokeMethod(
							setMethod,
							entity,
							referencedEntity);
				}
				ignoredFieldNames.add(field.getName());
			} else if (FileReference.class.isAssignableFrom(field.getType())) {
				// Es modifica el valor dels camps de tipus de FileReference amb el contingut de l'arxiu.
				setFileReferenceFieldValue(field, entity);
				ignoredFieldNames.add(field.getName());
			} else if (Collection.class.isAssignableFrom(field.getType())) {
				// Ignora els camps de tipus Collection.
				ignoredFieldNames.add(field.getName());
			}
		});
		// Actualitza els demés camps de l'entitat
		objectMappingHelper.map(
				resource,
				entity,
				ignoredFieldNames.toArray(new String[0]));
	}

	public <E extends ResourceEntity<R, ?>, R extends Resource<?>> R entityToResource(
			E entity,
			Class<R> resourceClass) {
		return objectMappingHelper.newInstanceMap(
				entity,
				resourceClass);
	}

	private <R, E> boolean callBuilderMethodForResource(
			R resource,
			Class<E> entityClass,
			Object builderInstance,
			Class<?> builderReturnType) {
		boolean builderMethodCalled = false;
		Optional<Method> resourceMethod = Arrays.stream(ReflectionUtils.getAllDeclaredMethods(builderReturnType)).
				filter(m -> {
					Class<?>[] parameterTypes = m.getParameterTypes();
					return parameterTypes.length > 0 && parameterTypes[0].isAssignableFrom(resource.getClass());
				}).findFirst();
		if (resourceMethod.isPresent()) {
			ReflectionUtils.invokeMethod(
					resourceMethod.get(),
					builderInstance,
					resource);
			builderMethodCalled = true;
		}
		return builderMethodCalled;
	}

	private void setFileReferenceFieldValue(Field field, Object target) {
		// Només es modifica el valor dels camps de tipus byte[] (arxius adjunts) si
		// el valor del camp al recurs és null o si te una llargada major que 0.
		String methodSuffix = TypeUtil.getMethodSuffixFromField(field);
		String getMethodName = "get" + methodSuffix;
		Method getMethod = ReflectionUtils.findMethod(target.getClass(), getMethodName);
		if (getMethod != null) {
			String setMethodName = "set" + methodSuffix;
			Method setMethod = ReflectionUtils.findMethod(
					target.getClass(),
					setMethodName,
					byte[].class);
			if (setMethod != null) {
				byte[] fileValue = null;
				if (FileReference.class.isAssignableFrom(field.getType())) {
					FileReference fileReference = (FileReference)ReflectionUtils.invokeMethod(getMethod, target);
					if (fileReference != null) fileValue = fileReference.getContent();
				} else {
					fileValue = (byte[])ReflectionUtils.invokeMethod(getMethod, target);
				}
				if (fileValue == null || fileValue.length != 0) {
					ReflectionUtils.invokeMethod(
							setMethod,
							target,
							fileValue);
				}
			}
		}
	}

}
