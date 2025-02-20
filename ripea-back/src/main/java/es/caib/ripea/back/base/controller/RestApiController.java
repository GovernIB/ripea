package es.caib.ripea.back.base.controller;

import es.caib.ripea.service.intf.base.model.Resource;
import es.caib.ripea.service.intf.base.service.ResourceApiService;
import es.caib.ripea.service.intf.base.util.StringUtil;
import es.caib.ripea.service.intf.base.util.TypeUtil;
import es.caib.ripea.service.intf.config.BaseConfig;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Hidden
@RestController
@RequestMapping(BaseConfig.API_PATH)
public class RestApiController {

	@Autowired
	protected ResourceApiService resourceApiService;

	@Autowired(required = false)
	protected List<ReadonlyResourceController<?, ?>> apiResourceControllers;

	@GetMapping
	@Operation(summary = "Consulta l'índex de serveis de l'aplicació")
	public ResponseEntity<CollectionModel<?>> index() {
		List<Link> indexLinks = resourceApiService.resourceFindAllowed().stream().
				map(this::toApiResourceControllerLink).
				collect(Collectors.toList());
		CollectionModel<?> resources = CollectionModel.of(
				Collections.emptySet(),
				indexLinks.toArray(Link[]::new));
		return ResponseEntity.ok(resources);
	}

	private Link toApiResourceControllerLink(Class<? extends Resource<?>> resourceClass) {
		Optional<ReadonlyResourceController<?, ?>> apiResourceControllerForResource = apiResourceControllers.stream().
				filter(c -> isApiResourceControllerForResource(c, resourceClass)).
				findFirst();
		String rel = StringUtil.decapitalize(resourceClass.getSimpleName());
		return linkTo(ClassUtils.getUserClass(apiResourceControllerForResource.get())).withRel(rel);
	}

	private boolean isApiResourceControllerForResource(
			ReadonlyResourceController<?, ?> apiResourceController,
			Class<? extends Resource<?>> resourceClass) {
		Class<?> controllerResourceClass = TypeUtil.getArgumentClassFromGenericSuperclass(
				apiResourceController.getClass(),
				ReadonlyResourceController.class,
				0);
		return controllerResourceClass.equals(resourceClass);
	}

}
