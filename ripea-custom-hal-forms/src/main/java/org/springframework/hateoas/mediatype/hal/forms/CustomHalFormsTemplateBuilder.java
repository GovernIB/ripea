// S'ha llevat el filtre que evita que les Affordances amb el mètode GET apareguin als templates
package org.springframework.hateoas.mediatype.hal.forms;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.hateoas.*;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class CustomHalFormsTemplateBuilder extends HalFormsTemplateBuilder {

	private final MessageResolver resolver;
	private final CustomHalFormsPropertyFactory customFactory;

	public CustomHalFormsTemplateBuilder(HalFormsConfiguration configuration, MessageResolver resolver) {
		super(configuration, resolver);
		this.resolver = resolver;
		this.customFactory = new CustomHalFormsPropertyFactory(configuration, resolver);
	}

	/**
	 * Extract template details from a {@link RepresentationModel}'s {@link Affordance}s.
	 *
	 * @param resource
	 * @return
	 */
	@Override
	public Map<String, HalFormsTemplate> findTemplates(RepresentationModel<?> resource) {

		Map<String, HalFormsTemplate> templates = new HashMap<>();
		Link selfLink = resource.getLink(IanaLinkRelations.SELF).orElse(null);

		resource.getLinks().stream() //
				.flatMap(it -> it.getAffordances().stream()) //
				.map(it -> it.getAffordanceModel(MediaTypes.HAL_FORMS_JSON)) //
				.peek(it -> {
					Assert.notNull(it, "No HAL Forms affordance model found but expected!");
				}) //
				.map(HalFormsAffordanceModel.class::cast) //
				.filter(it -> {
					List<HalFormsProperty> properties = customFactory.createProperties(it);
					boolean exclude = it.hasHttpMethod(HttpMethod.GET) && properties.isEmpty();
					return !exclude;
				}) // .filter(it -> !it.hasHttpMethod(HttpMethod.GET)) //
				.forEach(it -> {

					HalFormsTemplate template = HalFormsTemplate.forMethod(it.getHttpMethod()) //
							.withProperties(customFactory.createProperties(it))
							.withContentType(it.getInput().getPrimaryMediaType());

					String target = it.getLink().expand().getHref();

					if (selfLink == null || !target.equals(selfLink.getHref())) {
						template = template.withTarget(target);
					}

					template = applyTo(template, TemplateTitle.of(it, templates.isEmpty()));
					templates.put(templates.isEmpty() ? "default" : it.getName(), template);
				});

		return templates;
	}

	private HalFormsTemplate applyTo(HalFormsTemplate template, TemplateTitle templateTitle) {

		return Optional.ofNullable(resolver.resolve(templateTitle)) //
				.filter(StringUtils::hasText) //
				.map(template::withTitle) //
				.orElse(template);
	}

	private static class TemplateTitle implements MessageSourceResolvable {

		private static final String TEMPLATE_TEMPLATE = "_templates.%s.title";

		private final HalFormsAffordanceModel affordance;
		private final boolean soleTemplate;

		private TemplateTitle(HalFormsAffordanceModel affordance, boolean soleTemplate) {

			this.affordance = affordance;
			this.soleTemplate = soleTemplate;
		}

		public static TemplateTitle of(HalFormsAffordanceModel affordance, boolean soleTemplate) {
			return new TemplateTitle(affordance, soleTemplate);
		}

		/*
		 * (non-Javadoc)
		 * @see org.springframework.context.MessageSourceResolvable#getCodes()
		 */
		@NonNull
		@Override
		public String[] getCodes() {

			Stream<String> seed = Stream.concat(//
					Stream.of(affordance.getName()), //
					soleTemplate ? Stream.of("default") : Stream.empty());

			return seed.flatMap(it -> getCodesFor(it, affordance.getInput())) //
					.toArray(String[]::new);
		}

		private static Stream<String> getCodesFor(String name, AffordanceModel.InputPayloadMetadata type) {

			String global = String.format(TEMPLATE_TEMPLATE, name);

			Stream<String> inputBased = type.getI18nCodes().stream() //
					.map(it -> String.format("%s.%s", it, global));

			return Stream.concat(inputBased, Stream.of(global));
		}

		/*
		 * (non-Javadoc)
		 * @see org.springframework.context.MessageSourceResolvable#getDefaultMessage()
		 */
		@Nullable
		@Override
		public String getDefaultMessage() {
			return "";
		}
	}
}
