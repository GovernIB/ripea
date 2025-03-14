package es.caib.ripea.back.base.config;

import es.caib.ripea.back.base.controller.MutableResourceController;
import es.caib.ripea.back.base.controller.ReadonlyResourceController;
import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.annotation.ResourceField;
import es.caib.ripea.service.intf.base.model.Resource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.base.util.HalFormsUtil;
import es.caib.ripea.service.intf.base.util.I18nUtil;
import es.caib.ripea.service.intf.base.util.TypeUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.*;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsConfiguration;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsOptions;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Configuració de HAL-FORMS.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Configuration
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL_FORMS)
public class HalFormsConfig {

	@Autowired(required = false)
	private Set<ReadonlyResourceController> resourceControllers;

	@Bean
	HalFormsConfiguration halFormsConfiguration() {
		Set<Class<ReadonlyResourceController>> resourceControllerClasses = null;
		if (resourceControllers != null) {
			resourceControllerClasses = resourceControllers.stream().
				map(rc -> (Class<ReadonlyResourceController>)rc.getClass()).
				collect(Collectors.toSet());
		}
		return createHalFormsConfiguration(resourceControllerClasses);
	}

	private HalFormsConfiguration createHalFormsConfiguration(Set<Class<ReadonlyResourceController>> resourceControllerClasses) {
		HalFormsConfiguration halFormsConfiguration = new HalFormsConfiguration();
		if (resourceControllerClasses != null) {
			for (Class<ReadonlyResourceController> rc: resourceControllerClasses) {
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
			Set<Class<ReadonlyResourceController>> resourceControllerClasses) {
		MutableHolder<HalFormsConfiguration> halFormsConfigurationHolder = new MutableHolder<>(halFormsConfiguration);
		ReflectionUtils.doWithFields(
				resourceClass,
				field -> {
					configurationWithEnumOptions(
							halFormsConfigurationHolder,
							resourceClass,
							null,
							field);
				},
				this::isEnumTypeMultipleAware);
		ReflectionUtils.doWithFields(
				resourceClass,
				field -> {
					configurationWithResourceReferenceOptions(
							halFormsConfigurationHolder,
							resourceClass,
							null,
							field,
							resourceControllerClasses);
				},
				this::isResourceReferenceTypeMultipleAware);
		ResourceConfig resourceConfig = resourceClass.getAnnotation(ResourceConfig.class);
		if (resourceConfig != null) {
			for (ResourceConfigArtifact artifact: resourceConfig.artifacts()) {
				if (!Serializable.class.equals(artifact.formClass())) {
					ReflectionUtils.doWithFields(
							artifact.formClass(),
							field -> {
								configurationWithEnumOptions(
										halFormsConfigurationHolder,
										resourceClass,
										artifact,
										field);
							},
							this::isEnumTypeMultipleAware);
					ReflectionUtils.doWithFields(
							artifact.formClass(),
							field -> {
								configurationWithResourceReferenceOptions(
										halFormsConfigurationHolder,
										resourceClass,
										artifact,
										field,
										resourceControllerClasses);
							},
							this::isResourceReferenceTypeMultipleAware);
				}

			}
		}
		return halFormsConfigurationHolder.getValue();
	}

	private void configurationWithEnumOptions(
			MutableHolder<HalFormsConfiguration> halFormsConfigurationHolder,
			Class<?> resourceClass,
			ResourceConfigArtifact artifact,
			Field formField) {
		Class<?> optionsResourceClass = artifact != null ? artifact.formClass() : resourceClass;
		log.debug("New HAL-FORMS enum options (class={}, field={})", optionsResourceClass, formField.getName());
		halFormsConfigurationHolder.setValue(
				halFormsConfigurationHolder.getValue().withOptions(
						optionsResourceClass,
						formField.getName(),
						metadata -> {
							Map<String, Object> newResourceValues = HalFormsUtil.getNewResourceValues(optionsResourceClass);
							return HalFormsOptions.
									inline(getInlineOptionsEnumConstants(formField)).
									withValueField("id").
									withPromptField("description").
									withMinItems(TypeUtil.isNotNullField(formField) ? 1L : 0L).
									withMaxItems(TypeUtil.isMultipleFieldType(formField) ? null : 1L).
									withSelectedValue(newResourceValues.get(formField.getName()));
						}));
	}

	private void configurationWithResourceReferenceOptions(
			MutableHolder<HalFormsConfiguration> halFormsConfigurationHolder,
			Class<?> resourceClass,
			ResourceConfigArtifact artifact,
			Field formField,
			Set<Class<ReadonlyResourceController>> resourceControllerClasses) {
		Link remoteOptionsLink = getRemoteOptionsLink(
				resourceClass,
				artifact,
				formField,
				resourceControllerClasses);
		if (remoteOptionsLink != null) {
			Class<?> optionsResourceClass = artifact != null ? artifact.formClass() : resourceClass;
			log.debug("New HAL-FORMS resource reference options (class={}, field={})", optionsResourceClass, formField.getName());
			halFormsConfigurationHolder.setValue(
					halFormsConfigurationHolder.getValue().withOptions(
							optionsResourceClass,
							formField.getName(),
							metadata -> {
								// Aquí hem de tornar a calcular el remoteOptionsLink perquè si no ho feim
								// l'enllaç no inclou el prefix 'http://localhost:8080/webcontext'
								Link repeatedRemoteOptionsLink = getRemoteOptionsLink(
										resourceClass,
										artifact,
										formField,
										resourceControllerClasses);
								Map<String, Object> newResourceValues = HalFormsUtil.getNewResourceValues(optionsResourceClass);
								return HalFormsOptions.
										remote(repeatedRemoteOptionsLink).
										withValueField("id").
										withPromptField(getRemoteOptionsPromptField(formField)).
										withMinItems(TypeUtil.isNotNullField(formField) ? 1L : 0L).
										withMaxItems(TypeUtil.isCollectionFieldType(formField) ? null : 1L).
										withSelectedValue(newResourceValues.get(formField.getName()));
							}));
		}
	}

	private boolean isEnumTypeMultipleAware(Field field) {
		Class<?> fieldType = TypeUtil.getFieldTypeMultipleAware(field);
		return fieldType != null && fieldType.isEnum();
	}

	private boolean isResourceReferenceTypeMultipleAware(Field field) {
		Class<?> fieldType = TypeUtil.getFieldTypeMultipleAware(field);
		return fieldType != null && ResourceReference.class.isAssignableFrom(fieldType);
	}

	private FieldOption[] getInlineOptionsEnumConstants(Field field) {
		Class<?> fieldType = TypeUtil.getFieldTypeMultipleAware(field);
		Object[] enumConstants = fieldType.getEnumConstants();
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
			ResourceConfigArtifact artifact,
			Field resourceField,
			Set<Class<ReadonlyResourceController>> resourceControllerClasses) {
		Optional<Class<ReadonlyResourceController>> resourceControllerClass = resourceControllerClasses.stream().
				filter(rc -> {
					Class<?> controllerResourceClass = TypeUtil.getArgumentClassFromGenericSuperclass(
							rc,
							ReadonlyResourceController.class,
							0);
					return controllerResourceClass.equals(resourceClass);
				}).findFirst();
		if (resourceControllerClass.isPresent()) {
			Link findLink = getFindLinkWithSelfRel(
					resourceControllerClass.get(),
					artifact,
					resourceField.getName());
			if (findLink != null) {
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
				Class<?> referencedResourceClass = TypeUtil.getReferencedResourceClass(resourceField);
				log.error("Couldn't generate find link from field (" +
						"resourceClass=" + resourceClass + "," +
						"fieldName=" + resourceField.getName() + "," +
						"referencedResourceClass=" + referencedResourceClass + ")");
				return null;
			}
		} else {
			Class<?> referencedResourceClass = TypeUtil.getReferencedResourceClass(resourceField);
			log.error("Couldn't find resource controller class from field (" +
					"resourceClass=" + resourceClass + "," +
					"fieldName=" + resourceField.getName() + "," +
					"referencedResourceClass=" + referencedResourceClass + ")");
			return null;
		}
	}

	private Link getFindLinkWithSelfRel(
			Class<?> resourceControllerClass,
			ResourceConfigArtifact artifact,
			String resourceFieldName) {
		Class<ReadonlyResourceController> readonlyResourceControllerClass = (Class<ReadonlyResourceController>)resourceControllerClass;
		boolean isMutableResourceController = MutableResourceController.class.isAssignableFrom(resourceControllerClass);
		if (artifact == null) {
			if (isMutableResourceController) {
				Class<MutableResourceController> mutableResourceControllerClass = (Class<MutableResourceController>)resourceControllerClass;
				return linkTo(methodOn(mutableResourceControllerClass).fieldOptionsFind(
						resourceFieldName,
						null,
						null,
						null,
						null,
						null)).withRel(IanaLinkRelations.SELF_VALUE);
			} else {
				return null;
			}
		} else if (artifact.type() == ResourceArtifactType.ACTION) {
			if (isMutableResourceController) {
				Class<MutableResourceController> mutableResourceControllerClass = (Class<MutableResourceController>)resourceControllerClass;
				return linkTo(methodOn(mutableResourceControllerClass).artifactActionFieldOptionsFind(
						artifact.code(),
						resourceFieldName,
						null,
						null,
						null,
						null,
						null)).withRel(IanaLinkRelations.SELF_VALUE);
			} else {
				return null;
			}
		} else if (artifact.type() == ResourceArtifactType.REPORT) {
			return linkTo(methodOn(readonlyResourceControllerClass).artifactReportFieldOptionsFind(
					artifact.code(),
					resourceFieldName,
					null,
					null,
					null,
					null,
					null)).withRel(IanaLinkRelations.SELF_VALUE);
		} else if (artifact.type() == ResourceArtifactType.FILTER) {
			return linkTo(methodOn(readonlyResourceControllerClass).artifactFilterFieldOptionsFind(
					artifact.code(),
					resourceFieldName,
					null,
					null,
					null,
					null,
					null)).withRel(IanaLinkRelations.SELF_VALUE);
		} else {
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