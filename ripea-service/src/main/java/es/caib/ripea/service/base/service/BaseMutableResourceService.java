package es.caib.ripea.service.base.service;

import es.caib.ripea.persistence.base.repository.JpaRepositoryLocator;
import es.caib.ripea.service.intf.base.annotation.ResourceField;
import es.caib.ripea.service.intf.base.exception.*;
import es.caib.ripea.service.intf.base.model.*;
import es.caib.ripea.service.intf.base.service.MutableResourceService;
import es.caib.ripea.service.intf.base.util.CompositePkUtil;
import es.caib.ripea.service.intf.base.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.hibernate.Hibernate;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseMutableResourceService<R extends Resource<ID>, ID extends Serializable, E extends Persistable<ID>>
		extends BaseReadonlyResourceService<R, ID, E>
		implements MutableResourceService<R, ID> {

	private final Map<String, OnChangeLogicProcessor<R>> onChangeLogicProcessorMap = new HashMap<>();
	private final Map<String, ActionExecutor<?, ?>> actionExecutorMap = new HashMap<>();

	@Override
	public R newResourceInstance() {
		R resourceInstance = null;
		try {
			resourceInstance = getResourceClass().getDeclaredConstructor().newInstance();
		} catch (Exception ex) {
			log.error("Couldn't create new resource instance (resourceClass={})", getResourceClass(), ex);
		}
		return resourceInstance;
	}

	@Override
	@Transactional
	public R create(
			R resource,
			Map<String, AnswerRequiredException.AnswerValue> answers) {
		log.debug("Creant recurs (resource={})", resource);
		completeResource(resource);
		E entity = buildNewEntity(resource, false);
		reorderIfReorderable(entity, resource, reorderGetSequenceFromResource(resource));
		beforeCreateEntity(entity, resource);
		updateEntity(entity, resource);
		beforeCreateSave(entity, resource);
		E saved = saveFlushAndRefresh(entity);
		afterCreateSave(saved, resource);
		resourceRepository.detach(saved);
		R response = entityToResource(saved);
		resourceRepository.merge(saved);
		return response;
	}

	@Override
	@Transactional
	public R update(
			ID id,
			R resource,
			Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException {
		log.debug("Modificant recurs amb id (id={}, resource={})", id, resource);
		completeResource(resource);
		E entity = getEntity(id, null);
		reorderIfReorderable(entity, resource, reorderGetSequenceFromResource(resource));
		boolean proceedWithUpdate = beforeUpdateEntity(entity, resource);
		E saved;
		if (proceedWithUpdate) {
			updateEntity(entity, resource);
			beforeUpdateSave(entity, resource);
			saved = saveFlushAndRefresh(entity);
			afterUpdateSave(saved, resource);
		} else {
			saved = entity;
		}
		resourceRepository.detach(saved);
		R response = entityToResource(saved);
		resourceRepository.merge(saved);
		return response;
	}

	@Override
	@Transactional
	public void delete(
			ID id,
			Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException {
		log.debug("Esborrant recurs amb id (id={})", id);
		E entity = getEntity(id, null);
		reorderIfReorderable(entity, null, null);
		beforeDelete(entity);
		resourceRepository.delete(entity);
		resourceRepository.flush();
		afterDelete(entity);
	}

	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> onChange(
			R previous,
			String fieldName,
			Object fieldValue,
			Map<String, AnswerRequiredException.AnswerValue> answers) throws AnswerRequiredException {
		log.debug("Processant event onChange (previous={}, fieldName={}, fieldValue={}, answers={})",
				previous,
				fieldName,
				fieldValue,
				answers);
		return onChangeLogicProcessRecursive(
				previous,
				fieldName,
				fieldName,
				answers,
				0);
	}

	@Override
	@Transactional
	public <P> Object actionExec(String code, P params) throws ArtifactNotFoundException, ActionExecutionException {
		log.debug(
				"Executing action (code={}, params={})",
				code,
				params);
		ActionExecutor<P, ?> executor = (ActionExecutor<P, ?>)actionExecutorMap.get(code);
		if (executor != null) {
			return executor.exec(code, params);
		} else {
			throw new ArtifactNotFoundException(getResourceClass(), ResourceArtifactType.ACTION, code);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ResourceArtifact> artifactFindAll(ResourceArtifactType type) {
		log.debug("Querying allowed artifacts (type={})", type);
		List<ResourceArtifact> artifacts = new ArrayList<>(super.artifactFindAll(type));
		if (type == null || type == ResourceArtifactType.ACTION) {
			artifacts.addAll(
					actionExecutorMap.entrySet().stream().
							map(r -> new ResourceArtifact(
									ResourceArtifactType.ACTION,
									r.getKey(),
									artifactGetFormClass(ResourceArtifactType.ACTION, r.getKey()))).
							collect(Collectors.toList()));
		}
		return artifacts;
	}

	@Override
	@Transactional(readOnly = true)
	public ResourceArtifact artifactGetOne(ResourceArtifactType type, String code) throws ArtifactNotFoundException {
		log.debug("Querying artifact form class (type={}, code={})", type, code);
		if (type == ResourceArtifactType.ACTION) {
			ActionExecutor<?, ?> generator = actionExecutorMap.get(code);
			if (generator != null) {
				return new ResourceArtifact(
						ResourceArtifactType.ACTION,
						code,
						artifactGetFormClass(type, code));
			}
		}
		return super.artifactGetOne(type, code);
	}

	protected void updateEntityWithResource(E entity, R resource) {
		objectMappingHelper.map(
				resource,
				entity);
	}
	protected ID getPkFromResource(R resource) {
		if (resource.getId() == null) {
			return null;
		}
		return (ID)resource.getId();
	}

	protected Object newResourceInstance(String action, String report, String filter) {
		return null;
	}
	protected void completeResource(R resource) {}
	protected void beforeCreateEntity(E entity, R resource) throws ResourceNotCreatedException {}
	protected void beforeCreateSave(E entity, R resource) {}
	protected void afterCreateSave(E entity, R resource) {}
	protected boolean beforeUpdateEntity(E entity, R resource) throws ResourceNotUpdatedException {
		// Si es retorna true vol dir que s'ha de procedir amb l'update.
		// Si es retorna false vol dir que l'update no s'ha de fer.
		return true;
	}
	protected void beforeUpdateSave(E entity, R resource) {}
	protected void afterUpdateSave(E entity, R resource) {}
	protected void beforeDelete(E entity) throws ResourceNotDeletedException {}
	protected void afterDelete(E entity) {}

	protected boolean throwResourceAlreadyExistsExceptionOnCreate() {
		return true;
	}

	protected void processOnChangeLogic(
			R previous,
			String fieldName,
			Object fieldValue,
			Map<String, AnswerRequiredException.AnswerValue> answers,
			R target) {
	}

	protected E buildNewEntity(
			R resource,
			boolean forceExceptionIfAlreadyExists) {
		Method builderMethod = ReflectionUtils.findMethod(getEntityClass(), "builder");
		if (builderMethod != null) {
			Object builderInstance = ReflectionUtils.invokeMethod(builderMethod, null);
			Class<?> builderReturnType = builderMethod.getReturnType();
			// Es crida el mètode "embedded" del builder si l'entitat és de tipus EmbeddedEntity
			boolean builderEmbeddedMethodCalled = false;
			// Es criden els altres mètodes del builder que accepten només 1 argument de tipus Persistable
			for (Method builderCallableMethod : ReflectionUtils.getDeclaredMethods(builderReturnType)) {
				if (builderCallableMethod.getParameterTypes().length == 1) {
					String builderMethodName = builderCallableMethod.getName();
					Class<?> builderMethodArgType = builderCallableMethod.getParameterTypes()[0];
					if (Persistable.class.isAssignableFrom(builderMethodArgType)) {
						Class<? extends Persistable<?>> persistableArgType = (Class<? extends Persistable<?>>) builderMethodArgType;
						Object referencedEntity = getReferencedEntityForResourceField(
								resource,
								builderMethodName,
								persistableArgType);
						if (referencedEntity != null) {
							ReflectionUtils.invokeMethod(builderCallableMethod, builderInstance, referencedEntity);
						}
					}
				}
			}
			// Es crida el mètode build per a crear la instància de l'entitat
			E entity = (E) ReflectionUtils.invokeMethod(
					ReflectionUtils.findMethod(builderReturnType, "build"),
					builderInstance);
			// Es crea la pk a partir de la informació del recurs
			ID pk = getPkFromResource(resource);
			if (pk != null) {
				Optional<E> existingEntity = resourceRepository.findById(pk);
				if (existingEntity.isPresent()) {
					if (forceExceptionIfAlreadyExists || throwResourceAlreadyExistsExceptionOnCreate()) {
						throw new ResourceAlreadyExistsException(
								getResourceClass(),
								pk.toString());
					} else {
						return existingEntity.get();
					}
				}
			}
			// Només inicialitza el camp id si el mètode getPkFromResource retorna un valor diferent a null
			if (pk != null) {
				Field idField = ReflectionUtils.findField(getEntityClass(), "id");
				idField.setAccessible(true);
				ReflectionUtils.setField(
						idField,
						entity,
						pk);
			}
			return entity;
		} else {
			try {
				return getEntityClass().getConstructor().newInstance();
			} catch (Exception ex) {
				throw new ResourceNotCreatedException(
						getResourceClass(),
						"Couldn't create entity instance for class " + getEntityClass().getName() + ": " + ex.getMessage(),
						ex);
			}
		}
	}

	protected void updateEntity(E entity, R resource) {
		// Actualitza els camps de l'entitat que son de tipus Persistable o byte[]
		ReflectionUtils.doWithFields(getEntityClass(), new ReflectionUtils.FieldCallback() {
			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				// Es modifica el valor de cada camp de l'entitat que és de tipus de Persistable
				// amb la referencia especificada al resource.
				if (Persistable.class.isAssignableFrom(field.getType())) {
					Class<? extends Persistable<?>> persistableFieldType = (Class<? extends Persistable<?>>)field.getType();
					Persistable<?> referencedEntity = getReferencedEntityForResourceField(
							resource,
							field.getName(),
							persistableFieldType);
					String setMethodName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
					Method setMethod = ReflectionUtils.findMethod(
							getEntityClass(),
							setMethodName,
							field.getType());
					if (setMethod != null) {
						ReflectionUtils.invokeMethod(
								setMethod,
								entity,
								referencedEntity);
					} else {
						String updateMethodName = "update" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
						Method updateMethod = ReflectionUtils.findMethod(
								getEntityClass(),
								updateMethodName,
								field.getType());
						if (updateMethod != null) {
							ReflectionUtils.invokeMethod(
									updateMethod,
									entity,
									referencedEntity);
						}
					}
				}
				if (byte[].class.isAssignableFrom(field.getType())) {
					String getMethodName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
					Method getMethod = ReflectionUtils.findMethod(getResourceClass(), getMethodName);
					if (getMethod != null) {
						byte[] fileValue = (byte[])ReflectionUtils.invokeMethod(getMethod, resource);
						String setMethodName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
						Method setMethod = ReflectionUtils.findMethod(
								getEntityClass(),
								setMethodName,
								byte[].class);
						if (setMethod != null) {
							// Només es modifica el contingut del fitxer si fileValue és null o si te una llargada major que 0
							if (fileValue == null || fileValue.length != 0) {
								ReflectionUtils.invokeMethod(
										setMethod,
										entity,
										fileValue);
							}
						}
					}
				}
			}
		});
		// Actualitza els demés camps de l'entitat
		updateEntityWithResource(entity, resource);
	}

	protected Persistable<?> getReferencedEntityForResourceField(
			R resource,
			String fieldName,
			Class<? extends Persistable<?>> entityClass) {
		Field field = ReflectionUtils.findField(getResourceClass(), fieldName);
		Object referenceId = null;
		if (field != null) {
			field.setAccessible(true);
			if (ResourceReference.class.isAssignableFrom(field.getType())) {
				ResourceReference<?, ?> fieldValue = (ResourceReference<?, ?>)ReflectionUtils.getField(field, resource);
				if (fieldValue != null) {
					referenceId = fieldValue.getId();
				}
			} else if (Resource.class.isAssignableFrom(field.getType())) {
				Resource<?> fieldValue = (Resource<?>)ReflectionUtils.getField(field, resource);
				if (fieldValue != null) {
					referenceId = fieldValue.getId();
				}
			}
		}
		if (referenceId != null) {
			JpaRepository<?, ?> referencedRepository = JpaRepositoryLocator.getInstance().getEmbeddableRepositoryForEmbeddableEntityClass(
					entityClass);
			Class<?> pkClass = getRepositoryPkClass(referencedRepository.getClass());
			boolean isCompositePk = CompositePkUtil.isCompositePkClass(pkClass);
			if (isCompositePk) {
				referenceId = CompositePkUtil.getCompositePkFromSerializedId((String)referenceId, pkClass);
			}
			Method getByIdMethod = ReflectionUtils.findMethod(referencedRepository.getClass(), "getReferenceById", Object.class);
			if (getByIdMethod != null) {
				Object referencedObject = ReflectionUtils.invokeMethod(
						getByIdMethod,
						referencedRepository,
						referenceId);
				return (Persistable<?>) Hibernate.unproxy(referencedObject);
			}
		}
		return null;
	}

	protected Class<?> getRepositoryPkClass(Class<?> repositoryClass) {
		return GenericTypeResolver.resolveTypeArguments(repositoryClass, JpaRepository.class)[1];
	}

	protected List<E> reorderFindByEntity(E entity) {
		log.warn("Method reorderFindByEntity not implemented in service for resource {}", getResourceClass());
		return List.of();
	}
	protected Integer reorderGetIncrement() {
		return null;
	}
	protected void reorderSetOrder(E entity, R resource, long nextValue) {
		Reorderable entityReorderable = null;
		if (entity instanceof Reorderable) {
			entityReorderable = (Reorderable)entity;
		}
		if (entityReorderable != null) {
			entityReorderable.setOrder(nextValue);
		} else {
			log.error("Couldn't set order on entity class {}", getEntityClass());
		}
		if (resource != null) {
			((Reorderable)resource).setOrder(nextValue);
		}
	}

	private Long reorderGetSequenceFromResource(R resource) {
		if (resource instanceof Reorderable) {
			return ((Reorderable)resource).getOrder();
		} else {
			return null;
		}
	}
	private Long reorderGetSequenceFromEntity(E entity) {
		if (entity instanceof Reorderable) {
			return ((Reorderable)entity).getOrder();
		} else {
			log.error("Couldn't get order from entity class {}", getEntityClass());
			return null;
		}
	}
	private long reorderSetNextSequence(E entity, R resource, long index, Integer increment) {
		long nextValue = index * (increment != null ? increment : 1);
		reorderSetOrder(entity, resource, nextValue);
		return nextValue;
	}
	private void reorderIfReorderable(E entity, R resource, Long newSequence) {
		if (resource instanceof Reorderable) {
			// Si només feim la reordenació quan newSequence és null o quan ha canviat aleshores
			// hi ha casos que no funcionen. Per exemple, si s'han de reordenar dos elements amb
			// el mateix número de seqüència. Per això forçam sempre la reordenació.
			boolean hasToSort = true; //newSequence == null || !newSequence.equals(sortGetSequence(entity.getEmbedded()));
			if (hasToSort) {
				Integer increment = reorderGetIncrement();
				List<E> lines = reorderFindByEntity(entity);
				Long entitysequence = reorderGetSequenceFromEntity(entity);
				boolean moureCapAmunt = entitysequence != null && newSequence != null && entitysequence > newSequence;
				log.debug("Processant reordenació de {} línies cap {}", lines.size(), moureCapAmunt ? "amunt" : "avall");
				boolean inserted = false;
				long index = 1;
				for (int i = 0; i < lines.size(); i++) {
					E line = lines.get(i);
					if (!line.equals(entity)) {
						log.debug("\tRecalculant ordre de la línia {}: {}", i, line);
						Long currentSequence = reorderGetSequenceFromEntity(line);
						boolean insertarAbans = newSequence != null && (moureCapAmunt ?
								currentSequence != null && currentSequence.compareTo(newSequence) >= 0 :
								currentSequence != null && currentSequence.compareTo(newSequence) > 0);
						if (!inserted && newSequence != null && insertarAbans) {
							long sequence = reorderSetNextSequence(entity, resource, index++, increment);
							inserted = true;
							log.debug("\t\tInserció d'entitat amb ordre " + sequence);
						}
						long sequence = reorderSetNextSequence(line, null, index++, increment);
						log.debug("\t\tReordenació feta amb ordre {} (abans {})", sequence, currentSequence);
					} else {
						log.debug("\tIgnorant línia {}: {}", i, line);
					}
				}
				if (!inserted || newSequence == null) {
					long sequence = reorderSetNextSequence(entity, resource, index, increment);
					log.debug("\tAfegint nova línia amb ordre {}: {}", sequence, entity);
				}
			}
		}
	}

	protected void register(
			String actionCode,
			ActionExecutor<?, ?> actionExecutor) {
		if (artifactIsPresentInResourceConfig(ResourceArtifactType.ACTION, actionCode)) {
			actionExecutorMap.put(actionCode, actionExecutor);
		} else {
			log.error("Artifact not registered because it doesn't exist in ResourceConfig annotation (" +
					"resourceClass=" + getResourceClass() + ", " +
					"artifactType=" + ResourceArtifactType.ACTION + ", " +
					"artifactCode=" + actionCode + ")");
		}
	}

	protected void register(
			String fieldName,
			OnChangeLogicProcessor<R> logicProcessor) {
		onChangeLogicProcessorMap.put(fieldName, logicProcessor);
	}

	private Map<String, Object> onChangeLogicProcessRecursive(
			R previous,
			String fieldName,
			Object fieldValue,
			Map<String, AnswerRequiredException.AnswerValue> answers,
			int level) {
		Map<String, Object> changes = new HashMap<>();
		ProxyFactory factory = new ProxyFactory(newResourceInstance());
		factory.setProxyTargetClass(true);
		factory.addAdvice((MethodInterceptor) invocation -> {
			String methodName = invocation.getMethod().getName();
			Object[] arguments = invocation.getArguments();
			if (methodName.startsWith("set") && arguments.length > 0) {
				changes.put(
						StringUtil.decapitalize(methodName.substring("set".length())),
						arguments[0]);
			}
			return invocation.proceed();
		});
		R target = (R)factory.getProxy();
		if (onChangeLogicProcessorMap.get(fieldName) != null) {
			onChangeLogicProcessorMap.get(fieldName).processOnChangeLogic(
					previous,
					fieldName,
					fieldValue,
					answers,
					target);
		} else {
			processOnChangeLogic(
					previous,
					fieldName,
					fieldValue,
					answers,
					target);
		}
		if (!changes.isEmpty()) {
			Map<String, Object> changesToReturn = new HashMap<>(changes);
			for (String changedFieldName: changes.keySet()) {
				Field changedField = ReflectionUtils.findField(getResourceClass(), changedFieldName);
				if (changedField != null) {
					ResourceField fieldAnnotation = changedField.getAnnotation(ResourceField.class);
					if (fieldAnnotation != null && fieldAnnotation.onChangeActive()) {
						R previousPerField = cloneResourceWithFieldsMap(
								previous,
								fieldName,
								fieldValue,
								changes,
								changedFieldName);
						Map<String, Object> changesPerField = onChangeLogicProcessRecursive(
								previousPerField,
								changedFieldName,
								changes.get(changedFieldName),
								answers,
								level + 1);
						if (changesPerField != null) {
							changesToReturn.putAll(changesPerField);
						}
					}
				}
			}
			return changesToReturn;
		} else {
			return null;
		}
	}

	private R cloneResourceWithFieldsMap(
			R resource,
			String fieldName,
			Object fieldValue,
			Map<String, Object> fields,
			String excludeField) {
		R clonedResource = objectMappingHelper.clone(resource);
		try {
			Field field = resource.getClass().getField(fieldName);
			ReflectionUtils.makeAccessible(field);
			ReflectionUtils.setField(field, clonedResource, fieldValue);
		} catch (Exception ex) {
			log.error("Processing onChange request: couldn't find field {} on resource {}",
					fieldName,
					resource.getClass().getName(),
					ex);
		}
		fields.forEach((k, v) -> {
			if (!k.equals(excludeField)) {
				Field field = ReflectionUtils.findField(resource.getClass(), k);
				if (field != null) {
					ReflectionUtils.makeAccessible(field);
					ReflectionUtils.setField(field, clonedResource, v);
				}
			}
		});
		return clonedResource;
	}

	private E saveFlushAndRefresh(E entity) {
		E saved = resourceRepository.saveAndFlush(entity);
		resourceRepository.refresh(saved);
		return saved;
	}

	/**
	 * Interfície a implementar pels processadors de lògica onChange.
	 *
	 * @param <R> classe del recurs.
	 */
	public interface OnChangeLogicProcessor <R extends Serializable> {
		/**
		 * Processa la lògica onChange d'un camp.
		 *
		 * @param previous
		 *            el recurs amb els valors previs a la modificació.
		 * @param fieldName
		 *            el nom del camp modificat.
		 * @param fieldValue
		 *            el valor del camp modificat.
		 * @param answers
		 *            les respostes associades a la petició actual.
		 * @param target
		 *            el recurs emmagatzemat a base de dades.
		 */
		void processOnChangeLogic(
				R previous,
				String fieldName,
				Object fieldValue,
				Map<String, AnswerRequiredException.AnswerValue> answers,
				R target);
	}

	/**
	 * Interfície a implementar pels artefactes encarregats d'executar accions.
	 *
	 * @param <P> classe dels paràmetres necessaris per a executar l'acció.
	 * @param <R> classe de la resposta retornada com a resultat.
	 */
	public interface ActionExecutor<P, R extends Serializable> extends OnChangeLogicProcessor<R> {
		/**
		 * Executa l'acció.
		 *
		 * @param code
		 *            el codi de l'acció.
		 * @param params
		 *            els paràmetres per a l'execució.
		 * @return el resultat de l'execució (pot ser null).
		 * @throws ActionExecutionException
		 *             si es produeix algun error generant les dades.
		 */
		R exec(String code, P params) throws ActionExecutionException;
		default void processOnChangeLogic(
				R previous,
				String fieldName,
				Object fieldValue,
				Map<String, AnswerRequiredException.AnswerValue> answers,
				R target) {
		}
	}

}
