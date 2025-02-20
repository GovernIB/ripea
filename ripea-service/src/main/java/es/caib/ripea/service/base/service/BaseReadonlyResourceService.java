package es.caib.ripea.service.base.service;

import es.caib.ripea.persistence.base.entity.EmbeddableEntity;
import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.service.base.helper.ObjectMappingHelper;
import es.caib.ripea.service.base.springfilter.FilterSpecification;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.exception.ArtifactNotFoundException;
import es.caib.ripea.service.intf.base.exception.ReportGenerationException;
import es.caib.ripea.service.intf.base.exception.ResourceNotFoundException;
import es.caib.ripea.service.intf.base.model.Resource;
import es.caib.ripea.service.intf.base.model.ResourceArtifact;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.base.service.ReadonlyResourceService;
import es.caib.ripea.service.intf.base.util.TypeUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servei amb la funcionalitat básica per a la gestió d'un recurs en mode només lectura.
 *
 * @param <R> classe del recurs.
 * @param <ID> classe de la clau primària del recurs.
 *
 * @author Límit Tecnologies
 */
@Slf4j
public abstract class BaseReadonlyResourceService<R extends Resource<ID>, ID extends Serializable, E extends Persistable<ID>>
		implements ReadonlyResourceService<R, ID> {

	@Autowired
	protected BaseRepository<E, ID> resourceRepository;
	@Autowired
	protected ObjectMappingHelper objectMappingHelper;

	private Class<R> resourceClass;
	private Class<E> entityClass;
	private final Map<String, ReportDataGenerator<?, ?>> reportGeneratorMap = new HashMap<>();

	@Override
	@Transactional(readOnly = true)
	public R getOne(
			ID id,
			String[] perspectives) throws ResourceNotFoundException {
		log.debug("Consultant recurs amb id (id={}, perspectives={})", id, perspectives);
		beforeGetOne(perspectives);
		E entity = getEntity(id, perspectives);
		beforeConversion(entity);
		R response = entityToResource(entity);
		afterConversion(entity, response);
		if (perspectives != null) {
			R perspectivesResponse = applyPerspectives(entity, response, perspectives);
			if (perspectivesResponse != null) {
				return perspectivesResponse;
			} else {
				return response;
			}
		} else {
			return response;
		}
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
				"Consulta de pàgina d'entitats amb filtre i paginació (filter={}, namedQueries={}, perspectives={}, pageable={})",
				filter,
				Arrays.toString(namedQueries),
				Arrays.toString(perspectives),
				pageable);
		beforeFind(
				filter,
				namedQueries,
				perspectives);
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
			List<R> perspectivesResponse = applyPerspectives(
					resultat.getContent(),
					response.getContent(),
					perspectives);
			if (perspectivesResponse != null) {
				response = new PageImpl<>(
						perspectivesResponse,
						pageable,
						response.getTotalElements());
			}
		}
		long elapsedConversion = System.currentTimeMillis() - t0;
		log.debug(
				"Temps consumit en la consulta (database={}ms, conversion={}ms)",
				elapsedDatabase,
				elapsedConversion);
		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ResourceArtifact> artifactGetAllowed(ResourceArtifactType type) {
		log.debug("Consultant els artefactes permesos (type={})", type);
		List<ResourceArtifact> artifacts = new ArrayList<>();
		if (type == null || type == ResourceArtifactType.REPORT) {
			return reportGeneratorMap.entrySet().stream().
					map(r -> new ResourceArtifact(
							ResourceArtifactType.REPORT,
							r.getKey(),
							r.getValue().getParameterClass())).
					collect(Collectors.toList());
		}
		return artifacts;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Class<?>> artifactGetFormClass(ResourceArtifactType type, String code) throws ArtifactNotFoundException {
		log.debug("Consultant la classe de formulari per l'artefacte (type={}, code={})", type, code);
		if (type == ResourceArtifactType.REPORT) {
			ReportDataGenerator<?, ?> generator = reportGeneratorMap.get(code);
			if (generator != null) {
				return generator.getParameterClass() != null ? Optional.of(generator.getParameterClass()) : Optional.empty();
			}
		}
		throw new ArtifactNotFoundException(getResourceClass(), type, code);
	}

	@Override
	@Transactional(readOnly = true)
	public <P> List<?> reportGenerate(String code, P params) throws ArtifactNotFoundException, ReportGenerationException {
		log.debug(
				"Generant l'informe(code={}, params={})",
				code,
				params);
		ReportDataGenerator<P, ?> generator = (ReportDataGenerator<P, ?>)reportGeneratorMap.get(code);
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
			result = resourceRepository.findOne(pkSpec.and(getSpringFilterSpecification(additionalSpringFilter)));
		} else {
			result = resourceRepository.findOne(pkSpec);
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
				List<E> resultList = resourceRepository.findAll(
						processedSpecification,
						processedSort);
				resultat = new PageImpl<E>(resultList, pageable, resultList.size());
			} else {
				Pageable processedPageable = toProcessedPageableSort(pageable);
				resultat = resourceRepository.findAll(
						processedSpecification,
						processedPageable);
			}
		} else {
			log.debug("Consulta sense specification");
			if (pageable.isUnpaged()) {
				Sort processedSort = toProcessedSort(
						addDefaultSort(pageable.getSort()));
				List<E> resultList = resourceRepository.findAll(processedSort);
				resultat = new PageImpl<>(resultList, pageable, resultList.size());
			} else {
				Pageable processedPageable = toProcessedPageableSort(pageable);
				resultat = resourceRepository.findAll(processedPageable);
			}
		}
		return resultat;
	}

	protected List<R> applyPerspectives(
			List<E> entities,
			List<R> resources,
			String[] perspectives) {
		List<R> resourcesWithPerspectives = new ArrayList<>();
		for (int i = 0; i < entities.size(); i++) {
			R resourceWithPerspectives = applyPerspectives(entities.get(i), resources.get(i), perspectives);
			if (resourceWithPerspectives != null) {
				resourcesWithPerspectives.add(resourceWithPerspectives);
			} else {
				resourcesWithPerspectives.add(resources.get(i));
			}
		}
		return resourcesWithPerspectives;
	}

	protected Specification<E> toProcessedSpecification(
			String quickFilter,
			String filter,
			String[] namedFilters) {
		Specification<E> processedSpecification = getSpringFilterSpecification(
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
				Specification<E> namedSpecification = null;
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
		Specification<E> finalSpecification = processSpecification(processedSpecification);
		return finalSpecification != null ? finalSpecification : Specification.where(null);
	}

	protected Specification<E> getSpringFilterSpecification(String springFilter) {
		if (springFilter != null) {
			return new FilterSpecification<E>(springFilter);
		} else {
			return null;
		}
	}

	protected Specification<E> appendSpecificationWithAnd(
			Specification<E> currentSpecification,
			Specification<E> specification) {
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

	private String buildSpringFilterForQuickFilter(
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

	protected R entityToResource(E entity) {
		return objectMappingHelper.newInstanceMap(
				entity,
				getResourceClass());
	}
	protected List<R> entitiesToResources(List<E> entities) {
		return entities.stream().
				map(this::entityToResource).
				collect(Collectors.toList());
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

	protected String additionalSpringFilter(
			String currentSpringFilter,
			String[] namedQueries) {
		return null;
	}

	protected R applyPerspectives(
			E entity,
			R resource,
			String[] perspectives) {
		return null;
	}

	protected String namedFilterToSpringFilter(String name) {
		return null;
	}
	protected Specification<E> namedFilterToSpecification(String name) {
		return null;
	}

	protected Specification<E> processSpecification(Specification<E> specification) {
		return specification;
	}

	protected Sort getSortWithPerspectives(
			String[] perspectives,
			Sort currentSort) {
		return null;
	}

	protected void beforeGetOne(String[] perspectives) {}
	protected void beforeFind(
			String springFilter,
			String[] namedQueries,
			String[] perspectives) {}
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

	protected void register(ReportDataGenerator<?, ?> reportGenerator) {
		Arrays.stream(reportGenerator.getSupportedReportCodes()).
				forEach(c -> reportGeneratorMap.put(c, reportGenerator));
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
						if (EmbeddableEntity.class.isAssignableFrom(entityField.getType())) {
							log.debug("\t\t\tDetectat camp d'entitat que és referencia final " + path[0] + " de tipus EmbeddableEntity, s'afegeix ordenació de l'anotació del recurs");
							Class<?> resourceClass = TypeUtil.getArgumentClassFromGenericSuperclass(
									entityField.getType(),
									EmbeddableEntity.class,
									0);
							List<String> sortPaths = new ArrayList<String>();
							for (SortedField sortedField: getResourceDefaultSortFields(resourceClass)) {
								String[] ops = toProcessedSortPath(
										sortedField.getField().split("\\."),
										entityField.getType());
								if (ops != null) {
									sortPaths.addAll(Arrays.asList(ops));
								}
							}
							if (!sortPaths.isEmpty()) {
								// Retorna els paths afegint de nou el primer camp
								return sortPaths.stream().
										filter(p -> !p.isEmpty()).
										map(p -> path[0] + "." + p).
										toArray(String[]::new);
							} else {
								return new String[] { String.join(".", path[0], "id") };
							}
						} else {
							return new String[] { String.join(".", path[0], "id") };
						}
					}
				} else {
					// Si el camp no és una referència a una altra entitat
					log.debug("\t\t\tDetectat camp d'entitat normal " + path[0] + ", l'afegim");
					//return new String[] { String.join(".", "embedded", path[0]) };
					return new String[] { path[0] };
				}
			} else {
				if (EmbeddableEntity.class.isAssignableFrom(entityClass)) {
					Class<?> resourceClass = TypeUtil.getArgumentClassFromGenericSuperclass(
							entityClass,
							EmbeddableEntity.class,
							0);
					log.debug("\t\t\tDetectat camp que no pertany a l'entitat " + path[0] + ", el cercam al recurs " + resourceClass);
					Field resourceField = ReflectionUtils.findField(resourceClass, path[0]);
					if (resourceField != null) {
						// Si el camp pertany al recurs aleshores hi afegeix .embedded just abans
						log.debug("\t\t\t\t Camp " + path[0] + " trobat al recurs, l'afegim posant '.embedded'");
						return new String[]{String.join(".", "embedded", path[0])};
					} else {
						// Si el camp tampoc pertany al recurs aleshores retorna null
						log.warn("Ordenació no aplicable pel recurs {}, camp no trobat: {}", resourceClass, path[0]);
						return null;
					}
				} else {
					log.warn("Ordenació no aplicable pel recurs {}, camp no trobat: {}", resourceClass, path[0]);
					return null;
				}
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
				springFilter.append(fieldName);
				springFilter.append('~');
				springFilter.append("'");
				springFilter.append("*");
				springFilter.append(cleanReservedFilterCharacters(quickFilter));
				springFilter.append("*");
				springFilter.append("'");
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

	public interface ReportDataGenerator <P, D> {
		String[] getSupportedReportCodes();
		Class<P> getParameterClass();
		List<D> generate(
				String code,
				P params) throws ReportGenerationException;
	}

}
