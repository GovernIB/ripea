package es.caib.ripea.service.base.service;

import es.caib.ripea.persistence.base.entity.ResourceEntity;
import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.service.base.helper.ObjectMappingHelper;
import es.caib.ripea.service.base.helper.ResourceEntityMappingHelper;
import es.caib.ripea.service.base.springfilter.FilterSpecification;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceField;
import es.caib.ripea.service.intf.base.exception.*;
import es.caib.ripea.service.intf.base.model.*;
import es.caib.ripea.service.intf.base.service.ReadonlyResourceService;
import es.caib.ripea.service.intf.base.util.StringUtil;
import es.caib.ripea.service.intf.base.util.TypeUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Servei amb la funcionalitat básica per a la gestió d'un recurs que només es pot consultar.
 *
 * @param <R> classe del recurs.
 * @param <ID> classe de la clau primària del recurs.
 * @param <E> classe de l'entitat de base de dades del recurs.
 *
 * @author Límit Tecnologies
 */
@Slf4j
public abstract class BaseReadonlyResourceService<R extends Resource<ID>, ID extends Serializable, E extends ResourceEntity<R, ID>>
		implements ReadonlyResourceService<R, ID> {

	@Autowired
	protected BaseRepository<E, ID> entityRepository;
	@Autowired
	protected ObjectMappingHelper objectMappingHelper;
	@Autowired
	protected ResourceEntityMappingHelper resourceEntityMappingHelper;

	private Class<R> resourceClass;
	private Class<E> entityClass;
	private final Map<String, ReportDataGenerator<?, ? extends Serializable>> reportDataGeneratorMap = new HashMap<>();
	private final Map<String, FilterProcessor<?>> filterProcessorMap = new HashMap<>(); // TODO
	private final Map<String, PerspectiveApplicator<R, E>> perspectiveApplicatorMap = new HashMap<>();
	private final Map<String, FieldDownloader<E>> fieldDownloaderMap = new HashMap<>();

	@Override
	@Transactional(readOnly = true)
	public R getOne(
			ID id,
			String[] perspectives) throws ResourceNotFoundException {
		log.debug("Getting single resource (id={}, perspectives={})", id, perspectives);
		beforeGetOne(perspectives);
		E entity = getEntity(id, perspectives);
		beforeConversion(entity);
		R response = entityToResource(entity);
		afterConversion(entity, response);
		if (perspectives != null) {
			applyPerspectives(entity, response, perspectives);
		}
		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<R> findPage(
			String quickFilter,
			String filter,
			String[] namedQueries,
			String[] perspectives,
			Pageable pageable) {
		long t0 = System.currentTimeMillis();
		log.debug(
				"Querying entities page with filter and pagination (quickFilter={}, filter={}, namedQueries={}, perspectives={}, pageable={})",
				quickFilter,
				filter,
				Arrays.toString(namedQueries),
				Arrays.toString(perspectives),
				pageable);
		beforeFind(
				quickFilter,
				filter,
				namedQueries,
				pageable);
		Page<E> resultat = internalFindEntities(
				quickFilter,
				filter,
				namedQueries,
				pageable);
		long elapsedDatabase = System.currentTimeMillis() - t0;
		beforeConversion(resultat.getContent());
		Page<R> response = new PageImpl<>(
				entitiesToResources(resultat.getContent()),
				pageable,
				resultat.getTotalElements());
		afterConversion(resultat.getContent(), response.getContent());
		if (perspectives != null) {
			applyPerspectives(
					resultat.getContent(),
					response.getContent(),
					perspectives);
		}
		long elapsedConversion = System.currentTimeMillis() - t0;
		log.debug(
				"Query elapsed time (database={}ms, conversion={}ms)",
				elapsedDatabase,
				elapsedConversion);
		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public DownloadableFile fieldDownload(
			ID id,
			String fieldName,
			OutputStream out) throws ResourceNotFoundException, ResourceFieldNotFoundException, FieldArtifactNotFoundException, IOException {
		Field field = ReflectionUtils.findField(getResourceClass(), fieldName);
		if (field != null) {
			FieldDownloader<E> fieldDownloader = fieldDownloaderMap.get(fieldName);
			if (fieldDownloader != null) {
				return fieldDownloader.download(
						getEntity(id, null),
						fieldName,
						out);
			} else {
				throw new FieldArtifactNotFoundException(getResourceClass(), FieldArtifactType.DOWNLOAD, fieldName);
			}
		} else {
			throw new ResourceFieldNotFoundException(getResourceClass(), fieldName);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ResourceArtifact> artifactFindAll(ResourceArtifactType type) {
		log.debug("Querying all artifacts (type={})", type);
		List<ResourceArtifact> artifacts = new ArrayList<>();
		if (type == null || type == ResourceArtifactType.PERSPECTIVE) {
			artifacts.addAll(
					perspectiveApplicatorMap.keySet().stream().
							map(pa -> new ResourceArtifact(
									ResourceArtifactType.PERSPECTIVE,
									pa,
									null)).
							collect(Collectors.toList()));
		}
		if (type == null || type == ResourceArtifactType.REPORT) {
			artifacts.addAll(
					reportDataGeneratorMap.keySet().stream().
							map(reportDataGenerator -> new ResourceArtifact(
									ResourceArtifactType.REPORT,
									reportDataGenerator,
									artifactGetFormClass(ResourceArtifactType.REPORT, reportDataGenerator))).
							collect(Collectors.toList()));
		}
		if (type == null || type == ResourceArtifactType.FILTER) {
			artifacts.addAll(
					artifactGetFilterAll().stream().
							map(f -> new ResourceArtifact(
									ResourceArtifactType.FILTER,
									f.code(),
									artifactGetFormClass(ResourceArtifactType.FILTER, f.code()))).
							collect(Collectors.toList()));
		}
		return artifacts;
	}

	@Override
	@Transactional(readOnly = true)
	public ResourceArtifact artifactGetOne(ResourceArtifactType type, String code) throws ArtifactNotFoundException {
		log.debug("Querying artifact form class (type={}, code={})", type, code);
		if (type == ResourceArtifactType.PERSPECTIVE) {
			PerspectiveApplicator<?, ?> perspectiveApplicator = perspectiveApplicatorMap.get(code);
			if (perspectiveApplicator != null) {
				return new ResourceArtifact(
						ResourceArtifactType.PERSPECTIVE,
						code,
						null);
			}
		} else if (type == ResourceArtifactType.REPORT) {
			ReportDataGenerator<?, ?> reportDataGenerator = reportDataGeneratorMap.get(code);
			if (reportDataGenerator != null) {
				return new ResourceArtifact(
						ResourceArtifactType.REPORT,
						code,
						artifactGetFormClass(type, code));
			}
		} else if (type == ResourceArtifactType.FILTER) {
			if (artifactIsPresentInResourceConfig(type, code)) {
				return new ResourceArtifact(
						ResourceArtifactType.FILTER,
						code,
						artifactGetFormClass(type, code));
			}
		}
		throw new ArtifactNotFoundException(getResourceClass(), type, code);
	}

	// TODO
	public <P extends Serializable> Map<String, Object> artifactOnChange(
			ResourceArtifactType type,
			String code,
			P previous,
			String fieldName,
			Object fieldValue,
			Map<String, AnswerRequiredException.AnswerValue> answers) throws ArtifactNotFoundException, ResourceFieldNotFoundException, AnswerRequiredException {
		log.debug("Processing onChange event (previous={}, fieldName={}, fieldValue={}, answers={})",
				previous,
				fieldName,
				fieldValue,
				answers);
		ResourceArtifact artifact = artifactGetOne(type, code);
		if (artifact.getFormClass() != null) {
			Class<P> formClass = (Class<P>)artifact.getFormClass();
			Field field = ReflectionUtils.findField(formClass, fieldName);
			if (field != null) {
				return onChangeLogicProcessRecursive(
						previous,
						fieldName,
						fieldValue,
						null,
						null,
						answers);
			} else {
				throw new ResourceFieldNotFoundException(formClass, fieldName);
			}
		} else {
			log.warn("Couldn't find form class for artifact (resourceClass={}, type={}, code={})", getResourceClass(), type, code);
			return new HashMap<>();
		}
	}

	@Override
	@Transactional(readOnly = true)
	public <P extends Serializable> List<?> reportGenerate(String code, P params) throws ArtifactNotFoundException, ReportGenerationException {
		log.debug("Generating report (code={}, params={})", code, params);
		ReportDataGenerator<P, ?> generator = (ReportDataGenerator<P, ?>)reportDataGeneratorMap.get(code);
		if (generator != null) {
			return generator.generate(code, params);
		} else {
			throw new ArtifactNotFoundException(getResourceClass(), ResourceArtifactType.REPORT, code);
		}
	}

	protected E getEntity(ID id, String[] perspectives) throws ResourceNotFoundException {
		Optional<E> result;
		Specification<E> pkSpec = new PkSpec<>(id);
		String additionalSpringFilter = additionalSpringFilter(null, null);
		if (additionalSpringFilter != null && !additionalSpringFilter.trim().isEmpty()) {
			result = entityRepository.findOne(pkSpec.and(getSpringFilterSpecification(additionalSpringFilter)));
		} else {
			result = entityRepository.findOne(pkSpec);
		}
		if (result.isPresent()) {
			return result.get();
		} else {
			String idToString = id != null ? id.toString() : "<null>";
			String idMessage = idToString;
			if (additionalSpringFilter != null && !additionalSpringFilter.trim().isEmpty()) {
				idMessage = "{id=" + idToString + ", springFilter=" + additionalSpringFilter + "}";
			}
			throw new ResourceNotFoundException(resourceClass, idMessage);
		}
	}

	protected Page<E> internalFindEntities(
			String quickFilter,
			String filter,
			String[] namedFilters,
			Pageable pageable) {
		Page<E> resultat;
		Specification<E> processedSpecification = toProcessedSpecification(
				quickFilter,
				filter,
				namedFilters);
		if (processedSpecification != null) {
			log.debug("Consulta amb specification (specification={})", processedSpecification);
			if (pageable.isUnpaged()) {
				Sort processedSort = toProcessedSort(
						addDefaultSort(pageable.getSort()));
				List<E> resultList = entityRepository.findAll(
						processedSpecification,
						processedSort);
				resultat = new PageImpl<E>(resultList, pageable, resultList.size());
			} else {
				Pageable processedPageable = toProcessedPageableSort(pageable);
				resultat = entityRepository.findAll(
						processedSpecification,
						processedPageable);
			}
		} else {
			log.debug("Consulta sense specification");
			if (pageable.isUnpaged()) {
				Sort processedSort = toProcessedSort(
						addDefaultSort(pageable.getSort()));
				List<E> resultList = entityRepository.findAll(processedSort);
				resultat = new PageImpl<>(resultList, pageable, resultList.size());
			} else {
				Pageable processedPageable = toProcessedPageableSort(pageable);
				resultat = entityRepository.findAll(processedPageable);
			}
		}
		return resultat;
	}

	protected R entityToResource(E entity) {
		return resourceEntityMappingHelper.entityToResource(entity, getResourceClass());
	}

	protected List<R> entitiesToResources(List<E> entities) {
		return entities.stream().map(this::entityToResource).collect(Collectors.toList());
	}

	protected void applyPerspectives(
			List<E> entities,
			List<R> resources,
			String[] perspectives) throws ArtifactNotFoundException {
		Arrays.stream(perspectives).forEach(p -> {
			PerspectiveApplicator<R, E> perspectiveApplicator = perspectiveApplicatorMap.get(p);
			if (perspectiveApplicator != null) {
				boolean modified = perspectiveApplicator.applyMultiple(p, entities, resources);
				if (!modified) {
					IntStream.range(0, entities.size()).forEach(i -> {
						perspectiveApplicator.applySingle(
								p,
								entities.get(i),
								resources.get(i));
					});
				}
			} else {
				throw new ArtifactNotFoundException(getResourceClass(), ResourceArtifactType.PERSPECTIVE, p);
			}
		});
	}

	protected void applyPerspectives(
			E entity,
			R resource,
			String[] perspectives) {
		Arrays.stream(perspectives).forEach(p -> {
			PerspectiveApplicator<R, E> perspectiveApplicator = perspectiveApplicatorMap.get(p);
			if (perspectiveApplicator != null) {
				perspectiveApplicator.applySingle(p, entity, resource);
			} else {
				throw new ArtifactNotFoundException(getResourceClass(), ResourceArtifactType.PERSPECTIVE, p);
			}
		});
	}

	protected <P> Specification<P> toProcessedSpecification(
			String quickFilter,
			String filter,
			String[] namedFilters) {
		Specification<P> processedSpecification = getSpringFilterSpecification(
				buildSpringFilterForQuickFilter(
						getResourceClass(),
						null,
						quickFilter));
		processedSpecification = appendSpecificationWithAnd(
				processedSpecification,
				getSpringFilterSpecification(filter));
		processedSpecification = appendSpecificationWithAnd(
				processedSpecification,
				getSpringFilterSpecification(
						additionalSpringFilter(filter, namedFilters)));
		if (namedFilters != null) {
			for (String namedFilter: namedFilters) {
				Specification<P> namedSpecification = null;
				String namedSpringFilter = namedFilterToSpringFilter(namedFilter);
				if (namedSpringFilter != null) {
					namedSpecification = getSpringFilterSpecification(namedSpringFilter);
				} else {
					namedSpecification = namedFilterToSpecification(namedFilter);
				}
				processedSpecification = appendSpecificationWithAnd(
						processedSpecification,
						namedSpecification);
			}
		}
		Specification<P> finalSpecification = processSpecification(processedSpecification);
		return finalSpecification != null ? finalSpecification : Specification.where(null);
	}

	protected <P> Specification<P> getSpringFilterSpecification(String springFilter) {
		if (springFilter != null) {
			return new FilterSpecification<P>(springFilter);
		} else {
			return null;
		}
	}

	protected <P> Specification<P> appendSpecificationWithAnd(
			Specification<P> currentSpecification,
			Specification<P> specification) {
		if (specification != null) {
			if (currentSpecification != null) {
				return currentSpecification.and(specification);
			} else {
				return specification;
			}
		} else {
			return currentSpecification;
		}
	}

	protected String buildSpringFilterForQuickFilter(
			Class<? extends Resource<?>> resourceClass,
			String prefix,
			String quickFilter) {
		ResourceConfig resourceConfigAnnotation = resourceClass.getAnnotation(ResourceConfig.class);
		if (quickFilter != null) {
			String[] quickFilterFields = quickFilterGetFieldsFromResourceClass(resourceClass);
			if (quickFilterFields != null) {
				log.debug(
						"Construint filtre Spring Filter per quickFilter (resourceClass={}, quickFilter={})",
						getResourceClass(),
						quickFilter);
				StringBuilder quickFilterSpringFilter = new StringBuilder();
				for (String quickFilterField : resourceConfigAnnotation.quickFilterFields()) {
					String springFilter = getSpringFilterFromQuickFilterPath(
							quickFilterField.split("\\."),
							resourceClass,
							quickFilterField,
							quickFilter,
							prefix);
					if (springFilter != null) {
						appendSpringFilter(
								quickFilterSpringFilter,
								springFilter,
								" or ");
					}
				}
				log.debug("Filtre Spring Filter resultant: {}", quickFilterSpringFilter);
				return quickFilterSpringFilter.toString();
			}
		}
		return null;
	}

	protected List<SortedField> getResourceDefaultSortFields(Class<?> resourceClass) {
		ResourceConfig resourceAnnotation = getResourceClass().getAnnotation(ResourceConfig.class);
		if (resourceAnnotation != null && resourceAnnotation.defaultSortFields().length > 0) {
			return Arrays.stream(resourceAnnotation.defaultSortFields()).
					map(s -> new SortedField(s.field(), s.direction())).
					collect(Collectors.toList());
		} else {
			return Collections.emptyList();
		}
	}

	protected <P extends Serializable> Map<String, Object> onChangeLogicProcessRecursive(
			P previous,
			String fieldName,
			Object fieldValue,
			String[] previousFieldNames,
			OnChangeLogicProcessor<P> onChangeLogicProcessor,
			Map<String, AnswerRequiredException.AnswerValue> answers) {
		Map<String, Object> changesToReturn = null;
		P newInstance = (P)newClassInstance(previous.getClass());
		if (newInstance != null) {
			Map<String, Object> changes = new HashMap<>();
			ProxyFactory factory = new ProxyFactory(newInstance);
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
			P target = (P)factory.getProxy();
			onChangeLogicProcessor.onChange(
					previous,
					fieldName,
					fieldValue,
					answers,
					previousFieldNames,
					target);
			if (!changes.isEmpty()) {
				changesToReturn = new HashMap<>(changes);
				for (String changedFieldName : changes.keySet()) {
					Field changedField = ReflectionUtils.findField(previous.getClass(), changedFieldName);
					if (changedField != null) {
						ResourceField fieldAnnotation = changedField.getAnnotation(ResourceField.class);
						if (fieldAnnotation != null && fieldAnnotation.onChangeActive()) {
							P previousWithChanges = (P)cloneObjectWithFieldsMap(
									previous,
									fieldName,
									fieldValue,
									changes,
									changedFieldName);
							List<String> previousFieldNamesWithChangedFieldName = new ArrayList<>();
							if (previousFieldNames != null) {
								previousFieldNamesWithChangedFieldName.addAll(Arrays.asList(previousFieldNames));
							}
							previousFieldNamesWithChangedFieldName.add(fieldName);
							Map<String, Object> changesPerField = onChangeLogicProcessRecursive(
									previousWithChanges,
									changedFieldName,
									changes.get(changedFieldName),
									previousFieldNamesWithChangedFieldName.toArray(new String[0]),
									onChangeLogicProcessor,
									answers);
							if (changesPerField != null) {
								changesToReturn.putAll(changesPerField);
							}
						}
					}
				}
			}
		}
		return changesToReturn;
	}

	protected Object cloneObjectWithFieldsMap(
			Object resource,
			String fieldName,
			Object fieldValue,
			Map<String, Object> fields,
			String excludeField) {
		Object clonedResource = objectMappingHelper.clone(resource);
		try {
			Field field = resource.getClass().getDeclaredField(fieldName);
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

	protected <C> C newClassInstance(Class<C> clazz) {
		try {
			return clazz.getDeclaredConstructor().newInstance();
		} catch (Exception ex) {
			log.error("Couldn't create new resource instance (resourceClass={})", getResourceClass(), ex);
			return null;
		}
	}

	protected String additionalSpringFilter(
			String currentSpringFilter,
			String[] namedQueries) {
		return null;
	}

	protected String namedFilterToSpringFilter(String name) {
		return null;
	}
	protected <P> Specification<P> namedFilterToSpecification(String name) {
		return null;
	}

	protected <P> Specification<P> processSpecification(Specification<P> specification) {
		return specification;
	}

	protected void beforeGetOne(String[] perspectives) {}
	protected void beforeFind(
			String quickFilter,
			String springFilter,
			String[] namedQueries,
			Pageable pageable) {}
	protected void beforeConversion(E entity) {}
	protected void afterConversion(E entity, R resource) {}
	protected void beforeConversion(List<E> entities) {
		if (entities != null) {
			for (E entity: entities) {
				beforeConversion(entity);
			}
		}
	}
	protected void afterConversion(List<E> entities, List<R> resources) {
		if (resources != null) {
			for (int i = 0; i < resources.size(); i++) {
				afterConversion(entities.get(i), resources.get(i));
			}
		}
	}

	protected Class<R> getResourceClass() {
		if (resourceClass == null) {
			resourceClass = TypeUtil.getArgumentClassFromGenericSuperclass(
					getClass(),
					BaseReadonlyResourceService.class,
					0);
		}
		return resourceClass;
	}

	protected Class<E> getEntityClass() {
		if (entityClass == null) {
			entityClass = TypeUtil.getArgumentClassFromGenericSuperclass(
					getClass(),
					BaseReadonlyResourceService.class,
					2);
		}
		return entityClass;
	}

	protected void register(
			String reportCode,
			ReportDataGenerator<?, ?> reportGenerator) {
		if (artifactIsPresentInResourceConfig(ResourceArtifactType.REPORT, reportCode)) {
			reportDataGeneratorMap.put(reportCode, reportGenerator);
		} else {
			log.error("Artifact not registered because it doesn't exist in ResourceConfig annotation (" +
					"resourceClass=" + getResourceClass() + ", " +
					"artifactType=" + ResourceArtifactType.REPORT + ", " +
					"artifactCode=" + reportCode + ")");
		}
	}

	protected void register(
			String filterCode,
			FilterProcessor<?> filterProcessor) {
		if (artifactIsPresentInResourceConfig(ResourceArtifactType.FILTER, filterCode)) {
			filterProcessorMap.put(filterCode, filterProcessor);
		} else {
			log.error("Artifact not registered because it doesn't exist in ResourceConfig annotation (" +
					"resourceClass=" + getResourceClass() + ", " +
					"artifactType=" + ResourceArtifactType.FILTER + ", " +
					"artifactCode=" + filterCode + ")");
		}
	}

	protected void register(
			String perspectiveCode,
			PerspectiveApplicator<R, E> perspectiveApplicator) {
		if (artifactIsPresentInResourceConfig(ResourceArtifactType.PERSPECTIVE, perspectiveCode)) {
			perspectiveApplicatorMap.put(perspectiveCode, perspectiveApplicator);
		} else {
			log.error("Artifact not registered because it doesn't exist in ResourceConfig annotation (" +
					"resourceClass=" + getResourceClass() + ", " +
					"artifactType=" + ResourceArtifactType.PERSPECTIVE + ", " +
					"artifactCode=" + perspectiveCode + ")");
		}
	}

	protected void register(
			String fieldName,
			FieldDownloader<E> fieldDownloader) {
		fieldDownloaderMap.put(fieldName, fieldDownloader);
	}

	protected Class<? extends Serializable> artifactGetFormClass(ResourceArtifactType type, String code) {
		ResourceConfig resourceConfig = getResourceClass().getAnnotation(ResourceConfig.class);
		if (resourceConfig != null) {
			Optional<ResourceConfigArtifact> artifact = Arrays.stream(resourceConfig.artifacts()).
					filter(a -> a.type() == type && a.code().equals(code)).
					findFirst();
			if (artifact.isPresent() && !artifact.get().formClass().equals(Serializable.class)) {
				return artifact.get().formClass();
			}
		}
		return null;
	}

	protected boolean artifactIsPresentInResourceConfig(
			ResourceArtifactType type,
			String code) {
		ResourceConfig resourceConfig = getResourceClass().getAnnotation(ResourceConfig.class);
		if (resourceConfig != null) {
			Optional<ResourceConfigArtifact> artifacts = Arrays.stream(resourceConfig.artifacts()).
					filter(a -> a.type() == type && a.code().equals(code)).
					findFirst();
			return artifacts.isPresent();
		} else {
			return false;
		}
	}

	protected List<ResourceConfigArtifact> artifactGetFilterAll() {
		ResourceConfig resourceConfig = getResourceClass().getAnnotation(ResourceConfig.class);
		if (resourceConfig != null) {
			return Arrays.stream(resourceConfig.artifacts()).
					filter(a -> a.type() == ResourceArtifactType.FILTER).
					collect(Collectors.toList());
		} else {
			return Collections.emptyList();
		}
	}

	private Pageable toProcessedPageableSort(Pageable pageable) {
		return PageRequest.of(
				pageable.getPageNumber(),
				pageable.getPageSize(),
				toProcessedSort(
						addDefaultSort(pageable.getSort())));
	}

	private Sort toProcessedSort(
			Sort sort) {
		Sort resultSort;
		if (sort != null) {
			log.debug("\tProcessant ordenació " + sort);
			if (sort.isSorted()) {
				List<Sort.Order> orders = new ArrayList<>();
				for (Sort.Order order: sort) {
					String[] orderPaths = toProcessedSortPath(
							order.getProperty().split("\\."),
							getEntityClass());
					if (orderPaths != null) {
						for (String orderPath: orderPaths) {
							if (order.isAscending()) {
								log.debug("\t\tAfegint ordre " + orderPath + " asc");
								orders.add(Sort.Order.asc(orderPath));
							} else {
								log.debug("\t\tAfegint ordre " + orderPath + " desc");
								orders.add(Sort.Order.desc(orderPath));
							}
						}
					} else {
						log.debug("\t\tS'ha ignorat l'ordenació pel camp " + order.getProperty());
					}
				}
				resultSort = Sort.by(orders);
			} else {
				resultSort = sort;
			}
		} else {
			resultSort = Sort.unsorted();
		}
		return resultSort;
	}

	private String[] toProcessedSortPath(
			String[] path,
			Class<?> entityClass) {
		if (path.length > 0) {
			log.debug("\t\tProcessant path d'ordenació" + Arrays.toString(path) + " per l'entitat " + entityClass);
			Field entityField = ReflectionUtils.findField(entityClass, path[0]);
			if (entityField != null) {
				if (Persistable.class.isAssignableFrom(entityField.getType())) {
					// Si el camp és una referència a una altra entitat
					if (path.length > 1) {
						// Si no s'ha arribat al final del path es torna a fer el
						// procés amb l'entitat a la que es fa referència.
						log.debug("\t\t\tDetectat camp d'entitat de tipus referencia no final " + path[0] + ", tornant a processar");
						String[] orderPaths = toProcessedSortPath(
								Arrays.copyOfRange(path, 1, path.length),
								entityField.getType());
						if (orderPaths != null) {
							// Retorna els paths afegint de nou el primer camp
							return Arrays.stream(orderPaths).
									filter(p -> !p.isEmpty()).
									map(p -> path[0] + "." + p).
									toArray(String[]::new);
						} else {
							return null;
						}
					} else {
						// Si s'ha arribat al final del path s'agafa l'ordenació
						// definida a l'anotació del recurs de l'entitat.
						return new String[] { String.join(".", path[0], "id") };
					}
				} else {
					// Si el camp no és una referència a una altra entitat
					log.debug("\t\t\tDetectat camp d'entitat normal " + path[0] + ", l'afegim");
					//return new String[] { String.join(".", "embedded", path[0]) };
					return new String[] { path[0] };
				}
			} else {
				log.warn("Ordenació no aplicable pel recurs {}, camp no trobat: {}", resourceClass, path[0]);
				return null;
			}
		} else {
			return null;
		}
	}

	private Sort addDefaultSort(Sort sort) {
		if (sort == null || sort.isEmpty()) {
			List<Sort.Order> orders = new ArrayList<>();
			for (SortedField sortedField: getResourceDefaultSortFields(getResourceClass())) {
				orders.add(new Sort.Order(
						sortedField.getDirection(),
						sortedField.getField()));
			}
			return Sort.by(orders);
		} else {
			return sort;
		}
	}

	private String[] quickFilterGetFieldsFromResourceClass(Class<?> resourceClass) {
		ResourceConfig resourceConfigAnnotation = resourceClass.getAnnotation(ResourceConfig.class);
		if (resourceConfigAnnotation != null && resourceConfigAnnotation.quickFilterFields().length > 0) {
			return resourceConfigAnnotation.quickFilterFields();
		} else {
			log.warn("Quick filter fields not specified for resource (class={})", resourceClass.getName());
			return null;
		}
	}

	private String getSpringFilterFromQuickFilterPath(
			String[] currentPath,
			Class<?> resourceClass,
			String fieldName,
			String quickFilter,
			String filterFieldPrefix) {
		log.debug("\t\tProcessant path de quickFilter" + Arrays.toString(currentPath) + " pel recurs " + resourceClass);
		Field resourceField = ReflectionUtils.findField(resourceClass, currentPath[0]);
		if (resourceField != null) {
			Class<?> processedResourceType = resourceField.getType();
			boolean resourceTypeIsCollection = false;
			Class<?> collectionFieldType = TypeUtil.getCollectionFieldType(resourceField);
			if (collectionFieldType != null) {
				processedResourceType = collectionFieldType;
				resourceTypeIsCollection = true;
			}
			StringBuilder springFilter = new StringBuilder();
			if (ResourceReference.class.isAssignableFrom(processedResourceType)) {
				// Si el camp és una referència a una altra entitat
				if (currentPath.length > 1) {
					// Si no s'ha arribat al final del path es torna a fer el
					// procés amb l'entitat a la que es fa referència.
					log.debug("\t\t\tDetectat camp de tipus referencia no final " + currentPath[0] + ", tornant a cercar");
					if (resourceTypeIsCollection) {
						springFilter.append("exists(");
					}
					springFilter.append(
							getSpringFilterFromQuickFilterPath(
									Arrays.copyOfRange(currentPath, 1, currentPath.length),
									TypeUtil.getReferencedResourceClass(resourceField),
									fieldName,
									quickFilter,
									filterFieldPrefix));
					if (resourceTypeIsCollection) {
						springFilter.append(")");
					}
				} else {
					log.debug("\t\t\tDetectat camp de tipus referencia final. S'inclouran tots els seus camps del quickFilter");
					springFilter.append("(");
					if (resourceTypeIsCollection) {
						springFilter.append("exists(");
					}
					springFilter.append(
							buildSpringFilterForQuickFilter(
									TypeUtil.getReferencedResourceClass(resourceField),
									(filterFieldPrefix != null) ? filterFieldPrefix + resourceField.getName() + "." : resourceField.getName() + ".",
									quickFilter));
					if (resourceTypeIsCollection) {
						springFilter.append(")");
					}
					springFilter.append(")");
				}
			} else {
				log.debug("\t\t\tAfegint camp final");
				// Si el camp no és una referència a una altra entitat
				if (filterFieldPrefix != null) {
					springFilter.append(filterFieldPrefix);
				}
				springFilter.append("lower(");
				springFilter.append(fieldName);
				springFilter.append(")");
				springFilter.append("~");
				springFilter.append("lower(");
				springFilter.append("'");
				springFilter.append("%");
				springFilter.append(cleanReservedFilterCharacters(quickFilter));
				springFilter.append("%");
				springFilter.append("'");
				springFilter.append(")");
			}
			return springFilter.toString();
		} else {
			log.debug("\t\t\tCamp no trobat");
			return null;
		}
	}

	private void appendSpringFilter(
			StringBuilder sb,
			String springFilter,
			String separator) {
		if (springFilter != null && !springFilter.isEmpty()) {
			if (sb.length() > 0) {
				sb.append(separator);
			}
			sb.append("(");
			sb.append(springFilter);
			sb.append(")");
		}
	}

	private String cleanReservedFilterCharacters(String quickFilter) {
		StringBuilder sb = new StringBuilder();
		for (int n = 0; n < quickFilter.length(); n++) {
			char c = quickFilter.charAt(n);
			if (c == '\'') {
				sb.append("\\'");
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	@Getter
	@AllArgsConstructor
	public static class SortedField {
		private String field;
		private Sort.Direction direction;
	}

	@Getter
	@AllArgsConstructor
	private static class PkSpec<E, PK> implements Specification<E> {
		private PK id;
		@Override
		public Predicate toPredicate(
				Root<E> root,
				CriteriaQuery<?> query,
				CriteriaBuilder criteriaBuilder) {
			return criteriaBuilder.equal(root.get("id"), id);
		}
	}

	/**
	 * Interfície a implementar pels artefactes encarregats d'aplicar perspectives als recursos.
	 *
	 * @param <R> classe del recurs suportat.
	 */
	public interface PerspectiveApplicator<R extends Resource<?>, E extends ResourceEntity<R, ?>> {
		/**
		 * Aplica la perspectiva a múltiples recursos. Es pot sobreescriure o deixar sense implementar.
		 * Si es deixa sense implementar s'aplicarà la perspectiva a cada recurs per separat.
		 *
		 * @param code
		 *            el codi de la perspectiva a aplicar.
		 * @param entities
		 *            la llista d'entitats per a aplicar la perspectiva.
		 * @param resources
		 *            la llista de recursos a on aplicar la perspectiva.
		 * @return true si s'ha fet alguna modificació o false en cas contrari.
		 * @throws PerspectiveApplicationException
		 *             si es produeix algun error aplicant la perspectiva.
		 */
		default boolean applyMultiple(
				String code,
				List<E> entities,
				List<R> resources) throws PerspectiveApplicationException {
			return false;
		}
		/**
		 * Aplica la perspectiva a un únic recurs.
		 *
		 * @param code
		 *            el codi de la perspectiva a aplicar.
		 * @param entity
		 *            l'entitat per a aplicar la perspectiva.
		 * @param resource
		 *            el recurs a on aplicar la perspectiva.
		 * @throws PerspectiveApplicationException
		 *             si es produeix algun error aplicant la perspectiva.
		 */
		void applySingle(
				String code,
				E entity,
				R resource) throws PerspectiveApplicationException;
	}

	/**
	 * Interfície a implementar pels processadors de lògica onChange.
	 *
	 * @param <R> classe del recurs.
	 */
	public interface OnChangeLogicProcessor<R extends Serializable> {
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
		 * @param previousFieldNames
		 *            la llista de camps canviats amb anterioritat a l'actual petició onChange.
		 * @param target
		 *            el recurs emmagatzemat a base de dades.
		 */
		void onChange(
				R previous,
				String fieldName,
				Object fieldValue,
				Map<String, AnswerRequiredException.AnswerValue> answers,
				String[] previousFieldNames,
				R target);
	}

	/**
	 * Interfície a implementar pels artefactes encarregats de generar dades pels informes.
	 *
	 * @param <P> classe dels paràmetres necessaris per a generar l'informe.
	 * @param <R> classe de la llista de dades retornades al generar l'informe.
	 */
	public interface ReportDataGenerator<P extends Serializable, R extends Serializable> extends BaseMutableResourceService.OnChangeLogicProcessor<R> {
		/**
		 * Genera les dades per l'informe.
		 *
		 * @param code
		 *            el codi de l'informe.
		 * @param params
		 *            els paràmetres per a la generació.
		 * @return la llista amb les dades generades.
		 * @throws ReportGenerationException
		 *             si es produeix algun error generant les dades.
		 */
		List<R> generate(
				String code,
				P params) throws ReportGenerationException;
	}

	/**
	 * Interfície a implementar pels artefactes encarregats de filtrar els recursos.
	 *
	 * @param <R> classe del recurs que representa el filtre.
	 */
	public interface FilterProcessor<R extends Serializable> extends OnChangeLogicProcessor<R> {
	}

	/**
	 * Interfície a implementar per a retornar els arxius associats a un camp.
	 *
	 * @param <E> classe de l'entitat.
	 */
	public interface FieldDownloader<E extends ResourceEntity<?, ?>> {
		/**
		 * Retorna l'arxiu associat.
		 *
		 * @param entity
		 *            l'entitat amb els valors previs a la modificació.
		 * @param fieldName
		 *            el nom del camp de l'entitat.
		 * @param out
		 *            stream a on posar el fitxer generat.
		 * @throws IOException
		 *             si es produeix algun error de E/S al descarregar l'arxiu.
		 */
		DownloadableFile download(
				E entity,
				String fieldName,
				OutputStream out) throws IOException;
	}

}
