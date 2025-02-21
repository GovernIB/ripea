package es.caib.ripea.back.base.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import es.caib.ripea.service.intf.base.exception.ArtifactNotFoundException;
import es.caib.ripea.service.intf.base.model.Resource;
import es.caib.ripea.service.intf.base.model.ResourceArtifact;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.base.permission.ResourcePermissions;
import es.caib.ripea.service.intf.base.service.PermissionEvaluatorService;
import es.caib.ripea.service.intf.base.service.ReadonlyResourceService;
import es.caib.ripea.service.intf.base.service.ResourceApiService;
import es.caib.ripea.service.intf.base.util.HttpRequestUtil;
import es.caib.ripea.service.intf.base.util.JsonUtil;
import es.caib.ripea.service.intf.base.util.TypeUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.*;
import org.springframework.hateoas.TemplateVariable.VariableType;
import org.springframework.hateoas.mediatype.Affordances;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.groups.Default;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * <p>Classe base pels controladors de l'API REST que conté els mètodes
 * necessaris per a consultar recursos.</p>
 * 
 * @param <R>
 *            el tipus del recurs que ha de gestionar aquest Controller. Aquest
 *            tipus ha d'estendre de EntityResource&lt;ID&gt;.
 * @param <ID>
 *            el tipus de la clau primària del recurs. Aquest tipus ha
 *            d'implementar la interfície Serializable.
 * 
 * @author Límit Tecnologies
 */
@Slf4j
public abstract class BaseReadonlyResourceController<R extends Resource<? extends Serializable>, ID extends Serializable>
	implements ReadonlyResourceController<R, ID> {

	@Autowired
	protected ReadonlyResourceService<R, ID> readonlyResourceService;
	@Autowired
	protected ResourceApiService resourceApiService;
	@Autowired
	protected SmartValidator validator;

	private Class<R> resourceClass;

	@PostConstruct
	public void registerResourceService() {
		resourceApiService.resourceRegister(getResourceClass());
	}

	@Override
	@GetMapping(value = "/{id}")
	@Operation(summary = "Consulta la informació d'un recurs")
	@PreAuthorize("hasPermission(#resourceId, this.getResourceClass().getName(), this.getOperation('GET_ONE'))")
	public ResponseEntity<EntityModel<R>> getOne(
			@PathVariable
			@Parameter(description = "Identificador del recurs")
			final ID id,
			@RequestParam(value = "perspective", required = false)
			final String[] perspectives) {
		log.debug("Obtenint entitat (id={})", id);
		R resource = getReadonlyResourceService().getOne(
				id,
				perspectives);
		EntityModel<R> entityModel = toEntityModel(
				resource,
				buildSingleResourceLinks(
						resource.getId(),
						perspectives,
						null,
						resourceApiService.permissionsCurrentUser(
								getResourceClass(),
								id)).toArray(new Link[0]));
		return ResponseEntity.ok(entityModel);
	}

	@Override
	@GetMapping
	@Operation(summary = "Consulta paginada de recursos")
	@PreAuthorize("hasPermission(null, this.getResourceClass().getName(), this.getOperation('FIND'))")
	public ResponseEntity<PagedModel<EntityModel<R>>> find(
			@RequestParam(value = "quickFilter", required = false)
			@Parameter(description = "Filtre ràpid (text)")
			final String quickFilter,
			@RequestParam(value = "filter", required = false)
			@Parameter(description = "Consulta en format Spring Filter")
			final String filter,
			@RequestParam(value = "namedQuery", required = false)
			@Parameter(description = "Consultes predefinides")
			final String[] namedQueries,
			@RequestParam(value = "perspective", required = false)
			@Parameter(description = "Perspectives de la consulta")
			final String[] perspectives,
			@Parameter(description = "Paginació dels resultats", required = false)
			final Pageable pageable) {
		log.debug("Consultant entitats amb filtre i paginació (quickFilter={},filter={},namedQueries={},perspectives={},pageable={})",
				quickFilter,
				filter,
				Arrays.toString(namedQueries),
				Arrays.toString(perspectives),
				pageable);
		ResourcePermissions resourcePermissions = resourceApiService.permissionsCurrentUser(
				getResourceClass(),
				null);
		if (pageable != null) {
			// Només es fa la consulta de recursos si la petició conté informació de paginació.
			long t0 = System.currentTimeMillis();
			long initialTime = t0;
			String filterWithFieldParams = filterWithFieldParameters(filter, getResourceClass());
			Page<R> page = getReadonlyResourceService().findPage(
					quickFilter,
					filterWithFieldParams,
					namedQueries,
					perspectives,
					pageable);
			long queryTime = System.currentTimeMillis() - t0;
			log.trace("\ttemps de consulta: {}ms", queryTime);
			t0 = System.currentTimeMillis();
			PagedModel<EntityModel<R>> pagedModel = toPagedModel(
					page,
					perspectives,
					null,
					resourcePermissions,
					buildResourceCollectionLinks(
							quickFilter,
							filter,
							namedQueries,
							perspectives,
							pageable,
							page,
							null,
							resourcePermissions).toArray(new Link[0]));
			long conversionTime = System.currentTimeMillis() - t0;
			log.trace("\ttemps de conversió dels resultats: {}ms", conversionTime);
			long totalTime = System.currentTimeMillis() - initialTime;
			log.trace("\ttemps total:{}ms", totalTime);
			log.trace("\tnúm. resultats:{}", page.getTotalElements());
			return ResponseEntity.ok(pagedModel);
		} else {
			// Si la petició no conté informació depaginació únicament es
			// retornen els ellaços a les possibles accions sobre aquest
			// recurs.
			return ResponseEntity.ok(
					PagedModel.empty(
							buildResourceCollectionLinks(
									quickFilter,
									filter,
									namedQueries,
									perspectives,
									null,
									null,
									null,
									resourcePermissions)));
		}
	}

	@Override
	@GetMapping("/artifacts")
	@Operation(summary = "Llista d'artefactes relacionats amb aquest servei")
	@PreAuthorize("hasPermission(null, this.getResourceClass().getName(), this.getOperation('ARTIFACT'))")
	public ResponseEntity<CollectionModel<EntityModel<ResourceArtifact>>> artifacts() {
		List<ResourceArtifact> artifacts = getReadonlyResourceService().artifactGetAllowed(null);
		List<EntityModel<ResourceArtifact>> artifactsAsEntities = artifacts.stream().
				map(a -> EntityModel.of(a, buildReportLink(a))).
				collect(Collectors.toList());
		return ResponseEntity.ok(
				CollectionModel.of(
						artifactsAsEntities,
						buildArtifactsLinks(artifacts)));
	}

	@Override
	@PostMapping("/artifacts/report/{code}")
	@Operation(summary = "Generació d'un informe associat a un recurs")
	@PreAuthorize("hasPermission(null, this.getResourceClass().getName(), this.getOperation('REPORT'))")
	public ResponseEntity<CollectionModel<EntityModel<?>>> artifactReportGenerate(
			@PathVariable
			@Parameter(description = "Codi de l'informe")
			final String code,
			@RequestBody(required = false)
			final JsonNode params,
			BindingResult bindingResult) throws ArtifactNotFoundException, JsonProcessingException, MethodArgumentNotValidException {
		Object paramsObject = getArtifactFormClass(
				ResourceArtifactType.REPORT,
				code,
				params,
				bindingResult);
		List<?> items = getReadonlyResourceService().reportGenerate(code, paramsObject);
		List<EntityModel<?>> itemsAsEntities = items.stream().
				map(i -> EntityModel.of(i, buildReportItemLink(code, items.indexOf(i)))).
				collect(Collectors.toList());
		Link reportLink = linkTo(methodOn(getClass()).artifactReportGenerate(code, null, null)).withSelfRel();
		return ResponseEntity.ok(
				CollectionModel.of(
						itemsAsEntities,
						Link.of(reportLink.toUri().toString()).withSelfRel()));
	}

	public Class<R> getResourceClass() {
		if (resourceClass == null) {
			resourceClass = TypeUtil.getArgumentClassFromGenericSuperclass(
					getClass(),
					BaseReadonlyResourceController.class,
					0);
		}
		return resourceClass;
	}

	public PermissionEvaluatorService.RestApiOperation getOperation(String operationName) {
		return operationName != null ? PermissionEvaluatorService.RestApiOperation.valueOf(operationName) : null;
	}

	protected ReadonlyResourceService<R, ID> getReadonlyResourceService() {
		return readonlyResourceService;
	}

	protected Object getArtifactFormClass(
			ResourceArtifactType artifactType,
			String code,
			JsonNode params,
			BindingResult bindingResult) throws JsonProcessingException, MethodArgumentNotValidException {
		Optional<Class<?>> parameterClass = getReadonlyResourceService().artifactGetFormClass(
				artifactType,
				code);
		Object paramsObject = null;
		if (parameterClass.isPresent()) {
			paramsObject = JsonUtil.getInstance().fromJsonToObjectWithType(
					params,
					parameterClass.get());
			validateResource(paramsObject, 1, bindingResult);
		}
		return paramsObject;
	}

	protected <RR extends Resource<?>> EntityModel<RR> toEntityModel(
			RR resource,
			Link... links) {
		return EntityModel.of(
				resource,
				links);
	}
	protected <RR extends Resource<?>> PagedModel<EntityModel<RR>> toPagedModel(
			Page<RR> page,
			String[] perspectives,
			Link singleResourceSelfLink,
			ResourcePermissions resourcePermissions,
			Link... links) {
		return PagedModel.of(
				page.getContent().stream().map(resource -> {
					Link[] resourceLinks = resource != null ? buildSingleResourceLinks(
							resource.getId(),
							perspectives,
							singleResourceSelfLink,
							resourcePermissions).toArray(new Link[0]) : new Link[0];
					return toEntityModel(resource, resourceLinks);
				}).collect(Collectors.toList()),
				new PagedModel.PageMetadata(
						page.getNumberOfElements(),
						page.getNumber(),
						page.getTotalElements(),
						page.getTotalPages()),
				links);
	}

	protected static final String SELF_RESOURCE_ID_TOKEN = "#resourceId#";
	protected List<Link> buildSingleResourceLinks(
			Serializable id,
			String[] perspective,
			Link singleResourceSelfLink,
			ResourcePermissions resourcePermissions) {
		List<Link> ls = new ArrayList<>();
		Link selfLink;
		if (singleResourceSelfLink != null) {
			String baseSelfResourceLinkHref = singleResourceSelfLink.getHref();
			String token = URLEncoder.encode(SELF_RESOURCE_ID_TOKEN, StandardCharsets.UTF_8);
			baseSelfResourceLinkHref = baseSelfResourceLinkHref.replace(token, id.toString());
			selfLink = Link.of(baseSelfResourceLinkHref).withSelfRel();
		} else {
			selfLink = linkTo(methodOn(getClass()).getOne(id, perspective)).withSelfRel();
		}
		Map<String, Object> expandMap = new HashMap<>();
		expandMap.put("perspective", perspective);
		ls.add(selfLink.expand(expandMap));
		return ls;
	}

	protected List<Link> buildResourceCollectionLinks(
			String quickFilter,
			String filter,
			String[] namedQuery,
			String[] perspective,
			Pageable pageable,
			Page<?> page,
			Link resourceCollectionBaseSelfLink,
			ResourcePermissions resourcePermissions) {
		List<Link> ls = new ArrayList<>();
		if (pageable == null) {
			// Enllaços que es retornen quan no es fa cap consulta
			Link selfLink = buildFindLinkWithParams(
					resourceCollectionBaseSelfLink != null ? resourceCollectionBaseSelfLink : linkTo(getClass()).withSelfRel(),
					null,
					null,
					null,
					null,
					null);
			if (resourcePermissions.isReadGranted()) {
				ls.add(selfLinkWithDefaultProperties(selfLink, true));
				// Els enllaços de les accions find, getOne i create només es
				// retornen si a la petició s'ha especificat informació de
				// paginació.
				ls.add(buildFindLink(resourceCollectionBaseSelfLink));
				Link getOneLink = linkTo(methodOn(getClass()).getOne(null, null)).withRel("getOne");
				String getOneLinkHref = getOneLink.getHref().replace("perspective", "perspective*");
				ls.add(Link.of(UriTemplate.of(getOneLinkHref), "getOne"));
				ls.add(linkTo(methodOn(getClass()).artifacts()).withRel("artifacts"));
			} else {
				ls.add(selfLink);
			}
		} else {
			// Enllaços que es retornen amb els resultats de la consulta
			Link selfLink = buildFindLinkWithParams(
					resourceCollectionBaseSelfLink != null ? resourceCollectionBaseSelfLink : linkTo(getClass()).withSelfRel(),
					quickFilter,
					filter,
					namedQuery,
					perspective,
					pageable);
			if (resourcePermissions.isReadGranted()) {
				ls.add(selfLinkWithDefaultProperties(selfLink, false));
				if (resourcePermissions.isReadGranted() && pageable.isPaged()) {
					if (pageable.getPageNumber() < page.getTotalPages()) {
						if (!page.isFirst()) {
							ls.add(
									buildFindLinkWithParams(
											linkTo(getClass()).withRel("first"),
											quickFilter,
											filter,
											namedQuery,
											perspective,
											pageable.first()));
						}
						if (page.hasPrevious()) {
							ls.add(
									buildFindLinkWithParams(
											linkTo(getClass()).withRel("previous"),
											quickFilter,
											filter,
											namedQuery,
											perspective,
											pageable.previousOrFirst()));
						}
						if (page.hasNext()) {
							ls.add(
									buildFindLinkWithParams(
											linkTo(getClass()).withRel("next"),
											quickFilter,
											filter,
											namedQuery,
											perspective,
											pageable.next()));
						}
						if (!page.isLast()) {
							ls.add(
									buildFindLinkWithParams(
											linkTo(getClass()).withRel("last"),
											quickFilter,
											filter,
											namedQuery,
											perspective,
											PageRequest.of(page.getTotalPages() - 1, pageable.getPageSize())));
						}
						if (page.getTotalElements() > 0 && page.getTotalPages() > 1) {
							Link findLink = buildFindLinkWithParams(
									linkTo(getClass()).withRel("toPageNumber"),
									quickFilter,
									filter,
									namedQuery,
									perspective,
									PageRequest.of(0, pageable.getPageSize()));
							// Al link generat li eliminam la variable page amb el valor 0
							String findLinkHref = findLink.getHref().replace("page=0&", "").replace("page=0", "");
							TemplateVariables findTemplateVariables = new TemplateVariables(
									new TemplateVariable("page", VariableType.REQUEST_PARAM));
							// I a més hi afegim la variable page
							ls.add(
									Link.of(
											UriTemplate.of(findLinkHref).with(findTemplateVariables),
											"toPageNumber"));
						}
					}
				}
			} else {
				ls.add(selfLink);
			}
		}
		return ls;
	}

	protected Link selfLinkWithDefaultProperties(Link selfLink, boolean showProperties) {
		// Aquest mètode proporciona modifica el Link self afegint una Affordance que
		// es mostrarà a dins els _templates de HAL FORMS amb el nom "default".

		// La idea és que si es fa la petició al servei sense cap paràmetre es retorni
		// una llista dels camps del recurs (i que es pugui utilitzar per mostrar, per
		// exemple, les columnes del grid). Si la petició es fa amb paràmetres només
		// s'afegirà aquest Affordance per a que aparegui com a "default" i així les
		// demés Affordances (create, update, patch, ...) apareguin amb el nom que toca.

		// Seria ideal poder utilitzar el mètode HTTP GET per a retornar la informació
		// dels camps del recurs, però la class HalFormsTemplateBuilder filtra el mètode
		// GET evitant que surti als _templates. Per això hem utilitzat el mètode PUT
		// (per posar-ne algun) per quan volem que surtin els properties i el mètode
		// OPTIONS per quan volem ocultar-los.
		// https://github.com/spring-projects/spring-hateoas/issues/1683

		// Hem modificat les classes HalFormsTemplateBuilder i HalFormsPropertyFactory
		// per a que mostrin als templates les Affordances amb mètode GET amb les seves
		// properties.
		HttpMethod showPropertiesMethod = HttpMethod.GET;
		return Affordances.of(selfLink).
				afford(showProperties ? showPropertiesMethod : HttpMethod.OPTIONS).
				withInputAndOutput(getResourceClass()).
				withName("default").
				toLink();
	}

	protected Link buildFindLink(
			Link baseLink) {
		String rel = "find";
		Link findLink = baseLink != null ? baseLink : linkTo(methodOn(getClass()).find(null, null, null, null, null)).withRel(rel);
		// Al link generat li canviam les variables namedQuery i
		// perspective perquè no les posa com a múltiples.
		String findLinkHref = findLink.getHref().
				replace("namedQuery", "namedQuery*").
				replace("perspective", "perspective*")/*.
				replace("field", "field*")*/;
		// I a més hi afegim les variables page, size i sort que no les
		// detecta a partir de la classe de tipus Pageable
		TemplateVariables findTemplateVariables = new TemplateVariables(
				new TemplateVariable("page", VariableType.REQUEST_PARAM),
				new TemplateVariable("size", VariableType.REQUEST_PARAM),
				new TemplateVariable("sort", VariableType.REQUEST_PARAM).composite());
		return Link.of(UriTemplate.of(findLinkHref).with(findTemplateVariables), rel);
	}

	protected Link buildFindLinkWithParams(
			Link baseLink,
			String quickFilter,
			String filter,
			String[] namedQuery,
			String[] perspective,
			Pageable pageable) {
		Map<String, Object> expandMap = new HashMap<>();
		if (pageable != null) {
			if (pageable.isPaged()) {
				expandMap.put("page", pageable.getPageNumber());
				expandMap.put("size", pageable.getPageSize());
			} else {
				expandMap.put("page", "UNPAGED");
			}
		}
		if (pageable != null) {
			expandMap.put(
					"sort",
					pageable.getSort().stream().
					map(o -> o.getProperty() + "," + o.getDirection().name().toLowerCase()).
					collect(Collectors.toList()));
		}
		if (quickFilter != null) {
			expandMap.put("quickFilter", quickFilter);
		}
		if (filter != null) {
			expandMap.put("filter", filter);
		}
		if (namedQuery != null) {
			expandMap.put("namedQuery", Arrays.asList(namedQuery));
		}
		if (perspective != null) {
			expandMap.put("perspective", Arrays.asList(perspective));
		}
		return Link.of(
				UriTemplate.of(
						baseLink.toUri().toString(),
						new TemplateVariables(
								TemplateVariable.requestParameter("page"),
								TemplateVariable.requestParameter("size"),
								TemplateVariable.requestParameter("sort").composite(),
								TemplateVariable.requestParameter("quickFilter"),
								TemplateVariable.requestParameter("query"),
								TemplateVariable.requestParameter("filter"),
								TemplateVariable.requestParameter("namedQuery").composite(),
								TemplateVariable.requestParameter("perspective").composite())),
				baseLink.getRel()).
				expand(expandMap);
	}

	protected Link[] buildArtifactsLinks(List<ResourceArtifact> artifacts) {
		List<Link> ls = new ArrayList<>();
		Link selfLink = linkTo(methodOn(getClass()).artifacts()).withSelfRel();
		ls.add(selfLink);
		artifacts.forEach(a -> {
			if (ResourceArtifactType.FILTER == a.getType()) {
				ls.add(buildFilterLinkWithAffordances(a));
			} else if (ResourceArtifactType.REPORT == a.getType()) {
				ls.add(buildReportLinkWithAffordances(a));
			}
		});
		return ls.toArray(new Link[0]);
	}

	private Link buildFilterLinkWithAffordances(ResourceArtifact artifact) {
		String rel = "filter_" + artifact.getCode();
		Link findLink = buildFindLink(null).withRel(rel);
		if (artifact.getFormClass() != null) {
			return Affordances.of(findLink).
					afford(HttpMethod.GET).
					withInputAndOutput(artifact.getFormClass()).
					withName(rel).
					toLink();
		} else {
			return Affordances.of(findLink).
					afford(HttpMethod.GET).
					withName(rel).
					toLink();
		}
	}

	@SneakyThrows
	private Link buildReportLink(ResourceArtifact artifact) {
		Link reportLink = linkTo(methodOn(getClass()).artifacts()).withSelfRel();
		return Link.of(reportLink.toUri() + "/report/" + artifact.getCode()).withSelfRel();
	}
	private Link buildReportLinkWithAffordances(ResourceArtifact artifact) {
		String rel = "generate_" + artifact.getCode();
		Link reportLink = buildReportLink(artifact).withRel(rel);
		if (artifact.getFormClass() != null) {
			return Affordances.of(reportLink).
					afford(HttpMethod.POST).
					withInputAndOutput(artifact.getFormClass()).
					withName(rel).
					toLink();
		} else {
			return Affordances.of(reportLink).
					afford(HttpMethod.POST).
					withName(rel).
					toLink();
		}
	}
	@SneakyThrows
	private Link buildReportItemLink(String code, int index) {
		Link reportLink = linkTo(methodOn(getClass()).artifacts()).withSelfRel();
		return Link.of(reportLink.toUri().toString() + "/report/" + code + "/item/" + index).withSelfRel();
	}

	private String filterWithFieldParameters(String filter, Class<?> resourceClass) {
		// Retorna l'spring filter emplenat amb paràmetres del request
		Optional<HttpServletRequest> request = HttpRequestUtil.getCurrentHttpRequest();
		if (request.isPresent()) {
			Set<String> paramNames = request.get().getParameterMap().keySet();
			List<String> filterResourceFields = new ArrayList<>();
			ReflectionUtils.doWithFields(resourceClass, field -> {
				int modifiers = field.getModifiers();
				boolean isStaticFinal = Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
				if (!isStaticFinal) {
					String fieldName = field.getName();
					boolean hasLikeParam = paramNames.contains(fieldName + "~");
					boolean hasBetweenParams = paramNames.contains(fieldName + "1") && paramNames.contains(fieldName + "2");
					if (paramNames.contains(fieldName) || hasLikeParam || hasBetweenParams) {
						filterResourceFields.add(field.getName());
					}
				}
			});
			String[] springFilterParts = filterResourceFields.stream().
					map(p -> requestParamResourceFieldToSpringFilter(resourceClass, p, request.get())).
					filter(Objects::nonNull).
					toArray(String[]::new);
			if (springFilterParts.length > 0) {
				String requestParamsFilter = String.join(" and ", springFilterParts);
				if (filter != null && !filter.isBlank()) {
					return "(" + filter + ") and (" + requestParamsFilter + ")";
				} else {
					return requestParamsFilter;
				}
			} else {
				return filter;
			}
		} else {
			return filter;
		}
	}

	private String requestParamResourceFieldToSpringFilter(
			Class<?> resourceClass,
			String requestParamResource,
			HttpServletRequest request) {
		Field field = ReflectionUtils.findField(resourceClass, requestParamResource);
		if (field != null) {
			boolean isFieldReferenceType = field.getType().isAssignableFrom(ResourceReference.class);
			boolean isFieldTextType = field.isEnumConstant() ||
					field.getType().isAssignableFrom(String.class) ||
					field.getType().isAssignableFrom(Date.class) ||
					field.getType().isAssignableFrom(LocalDate.class) ||
					field.getType().isAssignableFrom(LocalDateTime.class) ||
					field.getType().isAssignableFrom(ZonedDateTime.class) ||
					field.getType().isAssignableFrom(Instant.class) ||
					field.getType().isAssignableFrom(YearMonth.class) ||
					field.getType().isAssignableFrom(MonthDay.class);
			if (isFieldReferenceType) {
				String value = request.getParameter(requestParamResource);
				return "(" + requestParamResource + ".id:" + value + ")";
			} else {
				String value = request.getParameter(requestParamResource);
				if (isFieldTextType && value != null) value = "'" + value + "'";
				String value1 = request.getParameter(requestParamResource + "1");
				if (isFieldTextType && value1 != null) value1 = "'" + value1 + "'";
				String value2 = request.getParameter(requestParamResource + "2");
				if (isFieldTextType && value2 != null) value2 = "'" + value2 + "'";
				String valueLike = request.getParameter(requestParamResource + "~");
				if (value != null) {
					return "(" + requestParamResource + ":" + value + ")";
				} else if (value1 != null && value2 != null) {
					return "(" + requestParamResource + ">:" + value1 + " and " + requestParamResource + "<:" + value2 + ")";
				} else if (valueLike != null) {
					return "(" + requestParamResource + "~'*" + valueLike + "*')";
				} else {
					return null;
				}
			}
		} else {
			return null;
		}
	}

	protected <T> void validateResource(
			T resource,
			int paramIndex,
			BindingResult bindingResult,
			Object... validationHints) throws MethodArgumentNotValidException {
		BindingResult resourceBindingResult = new BeanPropertyBindingResult(resource, bindingResult.getObjectName());
		Object[] finalValidationHints;
		if (validationHints == null || validationHints.length == 0) {
			finalValidationHints = new Object[] { Default.class };
		} else {
			finalValidationHints = validationHints;
		}
		validator.validate(
				resource,
				resourceBindingResult,
				finalValidationHints);
		if (resourceBindingResult.hasErrors()) {
			bindingResult.addAllErrors(resourceBindingResult);
			throw new MethodArgumentNotValidException(
					new MethodParameter(
							new Object() {}.getClass().getEnclosingMethod(),
							paramIndex),
					bindingResult);
		}
	}

	@Getter
	@AllArgsConstructor
	private static class FieldAndClass {
		Field field;
		Class<?> clazz;
	}

}
