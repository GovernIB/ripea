package es.caib.ripea.back.base.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.ArtifactNotFoundException;
import es.caib.ripea.service.intf.base.exception.ComponentNotFoundException;
import es.caib.ripea.service.intf.base.model.*;
import es.caib.ripea.service.intf.base.permission.ResourcePermissions;
import es.caib.ripea.service.intf.base.service.MutableResourceService;
import es.caib.ripea.service.intf.base.util.JsonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.mediatype.Affordances;
import org.springframework.hateoas.mediatype.ConfigurableAffordance;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.groups.Default;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * <p>Classe base pels controladors de l'API REST que conté els mètodes
 * necessaris per a modificar recursos.</p>
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
public abstract class BaseMutableResourceController<R extends Resource<? extends Serializable>, ID extends Serializable>
		extends BaseReadonlyResourceController<R, ID>
		implements MutableResourceController<R, ID> {

	@Autowired
	protected SmartValidator validator;

	@Override
	@PostMapping
	@Operation(summary = "Crea un nou recurs")
	@PreAuthorize("this.isPublic() or hasPermission(null, this.getResourceClass().getName(), this.getOperation('CREATE'))")
	public ResponseEntity<EntityModel<R>> create(
			@RequestBody
			final R resource,
			BindingResult bindingResult) throws MethodArgumentNotValidException {
		log.debug("Creant recurs (resource={})", resource);
		validateResource(
				resource,
				bindingResult,
				0,
				Resource.OnCreate.class,
				Default.class);
		R created = getMutableResourceService().create(
				resource,
				getAnswersFromHeaderOrRequest(null));
		final URI uri = MvcUriComponentsBuilder.fromController(getClass()).
				path("/{id}").
				buildAndExpand(created.getId()).
				toUri();
		return ResponseEntity.created(uri).body(
				toEntityModel(
						created,
						buildSingleResourceLinks(
								created.getId(),
								null,
								true,
								null,
								resourceApiService.permissionsCurrentUser(
										getResourceClass(),
										created.getId())).toArray(new Link[0])));
	}

	@Override
	@PutMapping(value = "/{id}")
	@Operation(summary = "Modifica tots els camps d'un recurs")
	@PreAuthorize("this.isPublic() or hasPermission(#id, this.getResourceClass().getName(), this.getOperation('UPDATE'))")
	public ResponseEntity<EntityModel<R>> update(
			@PathVariable
			@Parameter(description = "Identificador del recurs")
			final ID id,
			@RequestBody
			final R resource,
			BindingResult bindingResult) throws MethodArgumentNotValidException {
		log.debug("Modificant recurs (id={}, resource={})", id, resource);
		updateResourceIdAndPk(id, resource);
		validateResource(
				resource,
				bindingResult,
				1,
				Resource.OnUpdate.class,
				Default.class);
		R updated = getMutableResourceService().update(
				id,
				resource,
				getAnswersFromHeaderOrRequest(null));
		return ResponseEntity.ok(
				toEntityModel(
						updated,
						buildSingleResourceLinks(
								updated.getId(),
								null,
								true,
								null,
								resourceApiService.permissionsCurrentUser(
										getResourceClass(),
										id)).toArray(new Link[0])));
	}

	@Override
	@PatchMapping(value = "/{id}")
	@Operation(summary = "Modifica parcialment un recurs")
	@PreAuthorize("this.isPublic() or hasPermission(#id, this.getResourceClass().getName(), this.getOperation('PATCH'))")
	public ResponseEntity<EntityModel<R>> patch(
			@PathVariable
			@Parameter(description = "Identificador del recurs")
			final ID id,
			@RequestBody
			final JsonNode jsonNode,
			BindingResult bindingResult) throws JsonProcessingException, MethodArgumentNotValidException {
		log.debug("Modificant parcialment el recurs (id={}, jsonNode={})", id, jsonNode);
		R resource = getMutableResourceService().getOne(id, null);
		fillResourceWithFieldsMap(
				resource,
				JsonUtil.getInstance().fromJsonToMap(jsonNode, getResourceClass()));
		validateResource(
				resource,
				bindingResult,
				1,
				Resource.OnUpdate.class,
				Default.class);
		R updated = getMutableResourceService().update(
				id,
				resource,
				getAnswersFromHeaderOrRequest(null));
		return ResponseEntity.ok(
				toEntityModel(
						updated,
						buildSingleResourceLinks(
								updated.getId(),
								null,
								true,
								null,
								resourceApiService.permissionsCurrentUser(
										getResourceClass(),
										id)).toArray(new Link[0])));
	}

	@Override
	@DeleteMapping(value = "/{id}")
	@Operation(summary = "Esborra un recurs")
	@PreAuthorize("this.isPublic() or hasPermission(#id, this.getResourceClass().getName(), this.getOperation('DELETE'))")
	public ResponseEntity<?> delete(
			@PathVariable
			@Parameter(description = "Identificador del recurs")
			final ID id) {
		log.debug("Esborrant recurs (id={})", id);
		getMutableResourceService().delete(
				id,
				getAnswersFromHeaderOrRequest(null));
		return ResponseEntity.ok().build();
	}

	@Override
	@PatchMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Processa els canvis en els camps del recurs")
	@PreAuthorize("this.isPublic() or hasPermission(null, this.getResourceClass().getName(), this.getOperation('ONCHANGE'))")
	public ResponseEntity<String> onChange(
			@RequestBody @Valid
			final OnChangeEvent onChangeEvent) throws JsonProcessingException {
		log.debug("Processant canvis en els camps del recurs (onChangeEvent={})", onChangeEvent);
		R previous = getOnChangePrevious(onChangeEvent, getResourceClass());
		Object fieldValue = getOnChangeFieldValue(onChangeEvent, getResourceClass());
		Map<String, AnswerRequiredException.AnswerValue> answers = getAnswersFromHeaderOrRequest(onChangeEvent.getAnswers());
		Map<String, Object> processat = getMutableResourceService().onChange(
				(ID)onChangeEvent.getId(),
				previous,
				onChangeEvent.getFieldName(),
				fieldValue,
				answers);
		if (processat != null) {
			String serialized = objectMapper.writeValueAsString(new OnChangeForSerialization(processat));
			String response = serialized.substring(serialized.indexOf("\":{") + 2, serialized.length() - 1);
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.ok("{}");
		}
	}

	@Override
	@GetMapping(value = "/fields/{fieldName}/options")
	@Operation(summary = "Consulta paginada de les opcions disponibles per a emplenar un camp de tipus ResourceReference")
	@PreAuthorize("this.isPublic() or hasPermission(null, this.getResourceClass().getName(), this.getOperation('OPTIONS'))")
	public <RR extends Resource<?>> ResponseEntity<PagedModel<EntityModel<RR>>> fieldOptionsFind(
			@PathVariable
			@Parameter(description = "Nom del camp")
			final String fieldName,
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
			@Parameter(description = "Paginació dels resultats")
			final Pageable pageable) {
		log.debug("Consultant possibles valors del camp amb filtre i paginació (" +
						"fieldName={}, quickFilter={}, filter={}, namedQueries={}, perspectives={}, pageable={})",
				fieldName,
				quickFilter,
				filter,
				namedQueries,
				perspectives,
				pageable);
		Link resourceCollectionBaseSelfLink = linkTo(methodOn(getClass()).fieldOptionsFind(
				fieldName,
				quickFilter,
				filter,
				namedQueries,
				perspectives,
				pageable)).withSelfRel();
		Link singleResourceBaseSelfLink = linkTo(methodOn(getClass()).fieldOptionsGetOne(
				fieldName,
				SELF_RESOURCE_ID_TOKEN,
				null)).withSelfRel();
		return fieldOptionsFind(
				fieldName,
				quickFilter,
				filter,
				namedQueries,
				perspectives,
				pageable,
				null,
				null,
				resourceCollectionBaseSelfLink,
				singleResourceBaseSelfLink);
	}

	@Override
	@GetMapping(value = "/fields/{fieldName}/options/{id}")
	@Operation(summary = "Consulta d'una de les opcions disponibles per a emplenar un camp de tipus ResourceReferencee")
	@PreAuthorize("this.isPublic() or hasPermission(null, this.getResourceClass().getName(), this.getOperation('OPTIONS'))")
	public <RR extends Resource<RID>, RID extends Serializable> ResponseEntity<EntityModel<RR>> fieldOptionsGetOne(
			@PathVariable
			@Parameter(description = "Nom del camp")
			final String fieldName,
			@PathVariable
			@Parameter(description = "Id de l'element")
			final RID id,
			@RequestParam(value = "perspective", required = false)
			@Parameter(description = "Perspectives de la consulta")
			final String[] perspectives) {
		log.debug("Consultant un dels possibles valors del camp (fieldName={}, id={}, perspectives={})",
				fieldName,
				id,
				perspectives);
		Link singleResourceBaseSelfLink = linkTo(methodOn(getClass()).fieldOptionsGetOne(
				fieldName,
				SELF_RESOURCE_ID_TOKEN,
				null)).withSelfRel();
		return fieldOptionsGetOne(
				fieldName,
				id,
				perspectives,
				null,
				null,
				singleResourceBaseSelfLink);
	}

	@Override
	@GetMapping(value = "/fields/{fieldName}/enumOptions")
	@Operation(summary = "Consulta les opcions disponibles per a emplenar un camp enumerat")
	@PreAuthorize("this.isPublic() or hasPermission(null, this.getResourceClass().getName(), this.getOperation('OPTIONS'))")
	public ResponseEntity<CollectionModel<FieldOption>> fieldEnumOptions(
			@PathVariable
			@Parameter(description = "Nom del camp")
			final String fieldName) {
		log.debug("Consultant possibles valors del camp enumerat (fieldName={})", fieldName);
		List<FieldOption> fieldOptions = getMutableResourceService().fieldEnumOptions(fieldName);
		return ResponseEntity.ok(fieldOptions != null ? CollectionModel.of(fieldOptions) : CollectionModel.empty());
	}

	@Override
	@PostMapping("/artifacts/action/{code}")
	@Operation(summary = "Execució d'una acció associada a un recurs")
	@PreAuthorize("this.isPublic() or hasPermission(null, this.getResourceClass().getName(), this.getOperation('ACTION'))")
	public ResponseEntity<?> artifactActionExec(
			@PathVariable
			@Parameter(description = "Codi de l'acció")
			final String code,
			@RequestBody(required = false)
			final JsonNode params,
			BindingResult bindingResult) throws ArtifactNotFoundException, JsonProcessingException, MethodArgumentNotValidException {
		return artifactActionExec(null, code, params, bindingResult);
	}

	@Override
	@PostMapping("/{id}/artifacts/action/{code}")
	@Operation(summary = "Execució d'una acció associada a un recurs amb id")
	@PreAuthorize("this.isPublic() or hasPermission(#id, this.getResourceClass().getName(), this.getOperation('ACTION'))")
	public ResponseEntity<?> artifactActionExec(
			@PathVariable(required = false)
			@Parameter(description = "Identificador del recurs")
			final ID id,
			@PathVariable
			@Parameter(description = "Codi de l'acció")
			final String code,
			@RequestBody(required = false)
			final JsonNode params,
			BindingResult bindingResult) throws ArtifactNotFoundException, JsonProcessingException, MethodArgumentNotValidException {
		log.debug("Executant acció (id={}, code={}, params={})",
				id,
				code,
				params);
		Class<?> formClass = getArtifactFormClass(ResourceArtifactType.ACTION, code);
		Serializable paramsObject = getArtifactParamsAsObjectWithFormClass(
				formClass,
				params,
				bindingResult);
		Serializable result = getMutableResourceService().artifactActionExec(id, code, paramsObject);
		return ResponseEntity.ok(result);
	}

	@Override
	@GetMapping(value = "/artifacts/action/{code}/fields/{fieldName}/options")
	@Operation(summary = "Consulta paginada de les opcions disponibles per a emplenar un camp de tipus ResourceReference que pertany al formulari de l'acció")
	@PreAuthorize("this.isPublic() or hasPermission(null, this.getResourceClass().getName(), this.getOperation('ACTION'))")
	public <RR extends Resource<?>> ResponseEntity<PagedModel<EntityModel<RR>>> artifactActionFieldOptionsFind(
			@PathVariable
			@Parameter(description = "Codi de l'informe")
			final String code,
			@PathVariable
			@Parameter(description = "Nom del camp")
			final String fieldName,
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
			@Parameter(description = "Paginació dels resultats")
			final Pageable pageable) {
		log.debug("Consultant possibles valors del camp del formulari de l'acció (" +
						"code={}, fieldName={}, quickFilter={}, filter={}, namedQueries={}, perspectives={}, pageable={})",
				code,
				fieldName,
				quickFilter,
				filter,
				namedQueries,
				perspectives,
				pageable);
		Link resourceCollectionBaseSelfLink = linkTo(methodOn(getClass()).artifactActionFieldOptionsFind(
				code,
				fieldName,
				quickFilter,
				filter,
				namedQueries,
				perspectives,
				pageable)).withSelfRel();
		Link singleResourceBaseSelfLink = linkTo(methodOn(getClass()).artifactActionFieldOptionsGetOne(
				code,
				fieldName,
				SELF_RESOURCE_ID_TOKEN,
				null)).withSelfRel();
		return fieldOptionsFind(
				fieldName,
				quickFilter,
				filter,
				namedQueries,
				perspectives,
				pageable,
				ResourceArtifactType.ACTION,
				code,
				resourceCollectionBaseSelfLink,
				singleResourceBaseSelfLink);
	}

	@Override
	@GetMapping(value = "/artifacts/action/{code}/fields/{fieldName}/options/{id}")
	@Operation(summary = "Consulta d'una de les opcions disponibles per a emplenar un camp de tipus ResourceReference que pertany al formulari de l'acció")
	@PreAuthorize("this.isPublic() or hasPermission(null, this.getResourceClass().getName(), this.getOperation('ACTION'))")
	public <RR extends Resource<RID>, RID extends Serializable> ResponseEntity<EntityModel<RR>> artifactActionFieldOptionsGetOne(
			@PathVariable
			@Parameter(description = "Codi de l'informe")
			final String code,
			@PathVariable
			@Parameter(description = "Nom del camp")
			final String fieldName,
			@PathVariable
			@Parameter(description = "Id de l'element")
			final RID id,
			@RequestParam(value = "perspective", required = false)
			@Parameter(description = "Perspectives de la consulta")
			final String[] perspectives) {
		log.debug("Consultant un dels possibles valors del camp del formulari de l'acció (" +
						"code={}, fieldName={}, id={}, perspectives={})",
				code,
				fieldName,
				id,
				perspectives);
		Link singleResourceBaseSelfLink = linkTo(methodOn(getClass()).artifactActionFieldOptionsGetOne(
				code,
				fieldName,
				SELF_RESOURCE_ID_TOKEN,
				null)).withSelfRel();
		return fieldOptionsGetOne(
				fieldName,
				id,
				perspectives,
				ResourceArtifactType.ACTION,
				code,
				singleResourceBaseSelfLink);
	}

	protected MutableResourceService<R, ID> getMutableResourceService() {
		if (readonlyResourceService instanceof MutableResourceService) {
			return (MutableResourceService<R, ID>)readonlyResourceService;
		} else {
			throw new ComponentNotFoundException(MutableResourceService.class, getResourceClass().getName());
		}
	}

	@Override
	protected List<Link> buildSingleResourceLinks(
			Serializable id,
			String[] perspective,
			boolean withDownloadLink,
			Link singleResourceSelfLink,
			ResourcePermissions resourcePermissions) {
		List<Link> links = super.buildSingleResourceLinks(
				id,
				perspective,
				withDownloadLink,
				singleResourceSelfLink,
				resourcePermissions);
		Link selfLink = links.stream().
				filter(l -> l.getRel().value().equals("self")).
				findFirst().orElse(null);
		if (selfLink != null) {
			if (resourcePermissions.isWriteGranted()) {
				ConfigurableAffordance affordance = Affordances.of(selfLink).
						afford(FAKE_DEFAULT_TEMPLATE_HTTP_METHOD).
						withName("default").
						andAfford(HttpMethod.PUT).
						withInputAndOutput(getResourceClass()).
						withName("update").
						andAfford(HttpMethod.PATCH).
						withInputAndOutput(getResourceClass()).
						withName("patch");
				if (resourcePermissions.isDeleteGranted()) {
					affordance = affordance.
							andAfford(HttpMethod.DELETE).
							withName("delete");
				}
				links.set(links.indexOf(selfLink), affordance.toLink());
			} else if (resourcePermissions.isDeleteGranted()) {
				links.set(
						links.indexOf(selfLink),
						Affordances.of(selfLink).
								afford(FAKE_DEFAULT_TEMPLATE_HTTP_METHOD).
								withName("default").
								andAfford(HttpMethod.DELETE).
								withName("delete").
								toLink());
			}
		}
		return links;
	}

	@Override
	protected List<Link> buildResourceCollectionLinks(
			String quickFilter,
			String filter,
			String[] namedQuery,
			String[] perspective,
			Pageable pageable,
			Page<?> page,
			Link resourceCollectionBaseSelfLink,
			ResourcePermissions resourcePermissions) {
		List<Link> links = super.buildResourceCollectionLinks(
				quickFilter,
				filter,
				namedQuery,
				perspective,
				pageable,
				page,
				resourceCollectionBaseSelfLink,
				resourcePermissions);
		Link selfLink = links.stream().
				filter(l -> l.getRel().value().equals("self")).
				findFirst().orElse(null);
		if (selfLink != null && pageable == null) {
			Link fieldOptionsFindLink = linkTo(methodOn(getClass()).fieldOptionsFind(
					null,
					null,
					null,
					null,
					null,
					null)).withRel("fieldOptionsFind");
			links.add(buildFindLinkWithParams(
					fieldOptionsFindLink,
					null,
					null,
					null,
					null,
					null));
			if (resourcePermissions.isCreateGranted() && resourcePermissions.isWriteGranted()) {
				links.set(
						links.indexOf(selfLink),
						Affordances.of(selfLink).
								afford(HttpMethod.POST).
								withInputAndOutput(getResourceClass()).
								withName("create").
								andAfford(HttpMethod.PATCH).
								withInputAndOutput(getResourceClass()).
								withName("onChange").
								toLink());
			} else if (resourcePermissions.isCreateGranted()) {
				links.set(
						links.indexOf(selfLink),
						Affordances.of(selfLink).
								afford(HttpMethod.POST).
								withInputAndOutput(getResourceClass()).
								withName("create").
								toLink());
			} else if (resourcePermissions.isWriteGranted()) {
				links.set(
						links.indexOf(selfLink),
						Affordances.of(selfLink).
								afford(HttpMethod.PATCH).
								withInputAndOutput(getResourceClass()).
								withName("onChange").
								toLink());
			}
		}
		return links;
	}

	@Override
	protected List<Link> buildSingleResourceArtifactLinks(Serializable id) {
		List<Link> superLinks = super.buildSingleResourceArtifactLinks(id);
		List<ResourceArtifact> artifacts = getReadonlyResourceService().artifactFindAll(null);
		List<Link> links = artifacts.stream().
				filter(a -> a.getType() == ResourceArtifactType.ACTION && a.getRequiresId() != null && a.getRequiresId()).
				map(a -> buildActionLinkWithAffordances(a, id)).
				collect(Collectors.toList());
		return Stream.concat(superLinks.stream(), links.stream()).collect(Collectors.toList());
	}

	@Override
	protected List<Link> buildResourceCollectionArtifactLinks() {
		List<Link> superLinks = super.buildResourceCollectionArtifactLinks();
		List<ResourceArtifact> artifacts = getReadonlyResourceService().artifactFindAll(null);
		List<Link> links = artifacts.stream().
				filter(a -> a.getType() == ResourceArtifactType.ACTION && (a.getRequiresId() == null || !a.getRequiresId())).
				map(a -> buildActionLinkWithAffordances(a, null)).
				collect(Collectors.toList());
		return Stream.concat(superLinks.stream(), links.stream()).collect(Collectors.toList());
	}

	@Override
	protected Link[] buildSingleArtifactLinks(ResourceArtifact artifact) {
		List<Link> links = new ArrayList<>(Arrays.asList(super.buildSingleArtifactLinks(artifact)));
		if (artifact.getType() == ResourceArtifactType.ACTION) {
			links.add(buildActionLinkWithAffordances(artifact, null));
		}
		return links.toArray(new Link[0]);
	}

	protected <T extends Resource<?>> void validateResource(
			T resource,
			BindingResult bindingResult,
			int paramIndex,
			Object... validationHints) throws MethodArgumentNotValidException {
		Object[] finalValidationHints = validationHints;
		if (validationHints == null || validationHints.length == 0) {
			finalValidationHints = new Object[] { Default.class };
		}
		validator.validate(
				resource,
				bindingResult,
				finalValidationHints);
		if (bindingResult.hasErrors()) {
			throw new MethodArgumentNotValidException(
					new MethodParameter(
							new Object() {}.getClass().getEnclosingMethod(),
							paramIndex),
					bindingResult);
		}
	}

	protected void fillResourceWithFieldsMap(
			Object resource,
			Map<String, Object> fields) {
		if (fields != null) {
			fields.forEach((k, v) -> {
				Field field = ReflectionUtils.findField(resource.getClass(), k);
				if (field != null) {
					ReflectionUtils.makeAccessible(field);
					ReflectionUtils.setField(field, resource, v);
				}
			});
		}
	}

	private void updateResourceIdAndPk(
			ID id,
			R resource) {
		// Posa valor al camp id del recurs per a assegurar que aquest
		// camp estigui emplenat a l'hora de fer validacions.
		// Això ho feim perquè res ens assegura que aquests camps tenguin valor
		// en la petició que ens arriba del front.
		Field idField = ReflectionUtils.findField(resource.getClass(), "id");
		if (idField != null) {
			ReflectionUtils.makeAccessible(idField);
			ReflectionUtils.setField(
					idField,
					resource,
					id);
		}
	}

	@SneakyThrows
	private Link buildActionLink(ResourceArtifact artifact, Serializable id) {
		String rel = "exec_" + artifact.getCode();
		if (artifact.getRequiresId() != null && artifact.getRequiresId()) {
			return linkTo(methodOn(getClass()).artifactActionExec(id, artifact.getCode(), null, null)).withRel(rel);
		} else {
			return linkTo(methodOn(getClass()).artifactActionExec(artifact.getCode(), null, null)).withRel(rel);
		}
	}
	private Link buildActionLinkWithAffordances(ResourceArtifact artifact, Serializable id) {
		Link actionLink = buildActionLink(artifact, id);
		if (artifact.getFormClass() != null) {
			return Affordances.of(actionLink).
					afford(HttpMethod.POST).
					withInputAndOutput(artifact.getFormClass()).
					withName(actionLink.getRel().value()).
					toLink();
		} else {
			return Affordances.of(actionLink).
					afford(HttpMethod.POST).
					withName(actionLink.getRel().value()).
					toLink();
		}
	}

	@Getter
	@AllArgsConstructor
	public static class OnChangeForSerialization {
		@JsonInclude
		private Map<String, Object> map;
	}

	@Getter
	@AllArgsConstructor
	public static class ProcessedOptionsQueryExpressionContext {
		private Object session;
	}

}
