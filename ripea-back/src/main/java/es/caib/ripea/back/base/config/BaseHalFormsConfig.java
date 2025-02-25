package es.caib.ripea.back.base.config;

import es.caib.ripea.back.base.controller.MutableResourceController;
import es.caib.ripea.back.base.controller.ReadonlyResourceController;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceField;
import es.caib.ripea.service.intf.base.model.Resource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.base.util.I18nUtil;
import es.caib.ripea.service.intf.base.util.TypeUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.*;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsConfiguration;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsOptions;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Configuració de HAL-FORMS.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL_FORMS)
public abstract class BaseHalFormsConfig {

	@Bean
	HalFormsConfiguration halFormsConfiguration() {
		return createHalFormsConfiguration(
				TypeUtil.findAssignableClasses(
						MutableResourceController.class,
						getControllerPackages()));
	}

	protected abstract String[] getControllerPackages();

	private HalFormsConfiguration createHalFormsConfiguration(Set<Class<MutableResourceController>> resourceControllerClasses) {
		HalFormsConfiguration halFormsConfiguration = new HalFormsConfiguration();
		if (resourceControllerClasses != null) {
			for (Class<MutableResourceController> rc: resourceControllerClasses) {
				Class<?> resourceClass = TypeUtil.getArgumentClassFromGenericSuperclass(
						rc,
						ReadonlyResourceController.class,
						0);
				halFormsConfiguration = withResourceClass(halFormsConfiguration, resourceClass, resourceControllerClasses);
			}
		}
		return halFormsConfiguration;
	}

	private HalFormsConfiguration withResourceClass(
			HalFormsConfiguration halFormsConfiguration,
			Class<?> resourceClass,
			Set<Class<MutableResourceController>> resourceControllerClasses) {
		MutableHolder<HalFormsConfiguration> halFormsConfigurationHolder = new MutableHolder<>(halFormsConfiguration);
		ReflectionUtils.doWithFields(
				resourceClass,
				field -> {
					configurationWithEnumOptions(
							halFormsConfigurationHolder,
							resourceClass,
							field);
				},
				this::isEnumField);
		ReflectionUtils.doWithFields(
				resourceClass,
				field -> {
					configurationWithResourceReferenceOptions(
							halFormsConfigurationHolder,
							resourceClass,
							field,
							resourceControllerClasses);
				},
				field -> ResourceReference.class.isAssignableFrom(field.getType()));
		ResourceConfig resourceConfig = resourceClass.getAnnotation(ResourceConfig.class);
		if (resourceConfig != null && resourceConfig.artifactFormClasses().length > 0) {
			for (Class<?> artifactFormClass: resourceConfig.artifactFormClasses()) {
				ReflectionUtils.doWithFields(
						artifactFormClass,
						field -> {
							configurationWithEnumOptions(
									halFormsConfigurationHolder,
									artifactFormClass,
									field);
						},
						this::isEnumField);
				ReflectionUtils.doWithFields(
						artifactFormClass,
						field -> {
							configurationWithResourceReferenceOptions(
									halFormsConfigurationHolder,
									artifactFormClass,
									field,
									resourceControllerClasses);
						},
						field -> ResourceReference.class.isAssignableFrom(field.getType()));
			}
		}
		return halFormsConfigurationHolder.getValue();
	}

	private void configurationWithEnumOptions(
			MutableHolder<HalFormsConfiguration> halFormsConfigurationHolder,
			Class<?> resourceClass,
			Field resourceField) {
		log.info("New HAL-FORMS enum options (class={}, field={})", resourceClass, resourceField.getName());
		halFormsConfigurationHolder.setValue(
				halFormsConfigurationHolder.getValue().withOptions(
						resourceClass,
						resourceField.getName(),
						metadata -> HalFormsOptions.
								inline(getInlineOptionsEnumConstants(resourceField)).
								withValueField("id").
								withPromptField("description").
								withMinItems(TypeUtil.isNotNullField(resourceField) ? 1L : 0L).
								withMaxItems(TypeUtil.isMultipleFieldType(resourceField) ? null : 1L)));
	}

	private void configurationWithResourceReferenceOptions(
			MutableHolder<HalFormsConfiguration> halFormsConfigurationHolder,
			Class<?> resourceClass,
			Field resourceField,
			Set<Class<MutableResourceController>> resourceControllerClasses) {
		log.info("New HAL-FORMS resource reference options (class={}, field={})", resourceClass, resourceField.getName());
		Link remoteOptionsLink = getRemoteOptionsLink(
				resourceClass,
				resourceField,
				resourceControllerClasses);
		if (remoteOptionsLink != null) {
			halFormsConfigurationHolder.setValue(
					halFormsConfigurationHolder.getValue().withOptions(
							resourceClass,
							resourceField.getName(),
							metadata -> HalFormsOptions.
									remote(remoteOptionsLink).
									withValueField("id").
									withPromptField(getRemoteOptionsPromptField(resourceField)).
									withMinItems(TypeUtil.isNotNullField(resourceField) ? 1L : 0L).
									withMaxItems(TypeUtil.isCollectionFieldType(resourceField) ? null : 1L)));
		}
	}

	private boolean isEnumField(Field field) {
		if (field.getType().isArray()) {
			return field.getType().getComponentType().isEnum();
		} else {
			return field.getType().isEnum();
		}
	}

	private FieldOption[] getInlineOptionsEnumConstants(Field field) {
		Object[] enumConstants;
		if (field.getType().isArray()) {
			enumConstants = field.getType().getComponentType().getEnumConstants();
		} else {
			enumConstants = field.getType().getEnumConstants();
		}
		return Arrays.stream(enumConstants).
				map(e -> new FieldOption(
						e.toString(),
						I18nUtil.getInstance().getI18nEnumDescription(
								field,
								e.toString()))).
				toArray(FieldOption[]::new);
	}

	private Link getRemoteOptionsLink(
			Class<?> resourceClass,
			Field resourceField,
			Set<Class<MutableResourceController>> resourceControllerClasses) {
		Class<?> referencedResourceClass = TypeUtil.getReferencedResourceClass(resourceField);
		Optional<Class<MutableResourceController>> resourceControllerClass = resourceControllerClasses.stream().
				filter(rc -> {
					Class<?> controllerResourceClass = TypeUtil.getArgumentClassFromGenericSuperclass(
							rc,
							ReadonlyResourceController.class,
							0);
					return controllerResourceClass.equals(referencedResourceClass);
				}).findFirst();
		if (resourceControllerClass.isPresent()) {
			Link findLink = linkTo(methodOn(resourceControllerClass.get()).fieldOptionsFind(
					resourceField.getName(),
					null,
					null,
					null,
					null,
					null)).withRel(IanaLinkRelations.SELF_VALUE);
			// Al link generat li canviam les variables namedQuery i
			// perspective perquè no les posa com a múltiples.
			String findLinkHref = findLink.getHref().
					replace("namedQuery", "namedQuery*").
					replace("perspective", "perspective*");
			// I a més hi afegim les variables page, size i sort que no les
			// detecta a partir de la classe de tipus Pageable
			TemplateVariables findTemplateVariables = new TemplateVariables(
					new TemplateVariable("page", TemplateVariable.VariableType.REQUEST_PARAM),
					new TemplateVariable("size", TemplateVariable.VariableType.REQUEST_PARAM),
					new TemplateVariable("sort", TemplateVariable.VariableType.REQUEST_PARAM).composite());
			return Link.of(UriTemplate.of(findLinkHref).with(findTemplateVariables), findLink.getRel());
		} else {
			log.error("Couldn't find resource controller class from field (" +
					"resourceClass=" + resourceClass + "," +
					"fieldName=" + resourceField.getName() + "," +
					"referencedResourceClass=" + referencedResourceClass + ")");
			return null;
		}
	}

	private String getRemoteOptionsPromptField(Field field) {
		String descriptionField = null;
		ResourceField fieldAnnotation = field.getAnnotation(ResourceField.class);
		if (fieldAnnotation != null && !fieldAnnotation.descriptionField().isEmpty()) {
			descriptionField = fieldAnnotation.descriptionField();
		} else {
			Class<? extends Resource<?>> referencedResourceClass = TypeUtil.getReferencedResourceClass(field);
			ResourceConfig configAnnotation = referencedResourceClass.getAnnotation(ResourceConfig.class);
			if (configAnnotation != null && !configAnnotation.descriptionField().isEmpty()) {
				descriptionField = configAnnotation.descriptionField();
			} else {
				descriptionField = "id";
			}
		}
		return descriptionField;
	}

	@Getter
	@Setter
	@AllArgsConstructor
	public static class MutableHolder<T> {
		private T value;
	}

	@Getter
	@AllArgsConstructor
	public static class FieldOption {
		private String id;
		private String description;
	}

}
