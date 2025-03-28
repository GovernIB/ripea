package es.caib.ripea.back.config;

import com.opensymphony.sitemesh.webapp.SiteMeshFilter;
import es.caib.ripea.back.interceptor.*;
import es.caib.ripea.service.intf.base.model.UnpagedButSorted;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableArgumentResolver;
import org.springframework.data.web.PageableHandlerMethodArgumentResolverSupport;
import org.springframework.data.web.SortArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.lang.Nullable;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Configuració de Spring MVC.
 * 
 * @author Limit Tecnologies
 */
@Configuration
@DependsOn("ejbClientConfig")
public class WebMvcConfig implements WebMvcConfigurer {

	@Autowired private MetaExpedientInterceptor metaExpedientInterceptor;
	@Autowired private AplicacioInterceptor aplicacioInterceptor;
	@Autowired private SessioInterceptor sessioInterceptor;
	@Autowired private LlistaEntitatsInterceptor llistaEntitatsInterceptor;
	@Autowired private LlistaRolsInterceptor llistaRolsInterceptor;
	@Autowired private ModalInterceptor modalInterceptor;
	@Autowired private NodecoInterceptor nodecoInterceptor;
	@Autowired private AjaxInterceptor ajaxInterceptor;
	@Autowired private ExpedientsInterceptor expedientsInterceptor;
	@Autowired private TasquesPendentsInterceptor tasquesPendentsInterceptor;
	@Autowired private AnotacionsPendentsInterceptor anotacionsPendentsInterceptor;
	@Autowired private SeguimentEnviamentsUsuariInterceptor seguimentEnviamentsUsuariInterceptor;
	@Autowired private AvisosInterceptor avisosInterceptor;
	@Autowired private FluxFirmaInterceptor fluxFirmaInterceptor;
	@Autowired private AccesAdminEntitatInterceptor accesAdminEntitatInterceptor;
	@Autowired private AccesAdminEntitatOAdminOrganORevisorInterceptor accesAdminEntitatOAdminOrganORevisorInterceptor;
	@Autowired private AccesAdminEntitatORevisorInterceptor accesAdminEntitatORevisorInterceptor;
	@Autowired private AccesAdminEntitatOrUsuariInterceptor accesAdminEntitatOrUsuariInterceptor;
	@Autowired private AccesURLsInstruccioInterceptor accesURLsInstruccioInterceptor;
	@Autowired private AccesFluxosFirmaUsuariInterceptor accesFluxosFirmaUsuariInterceptor;
	@Autowired private AccesSuperInterceptor accesSuperInterceptor;

	@Bean
	public FilterRegistrationBean<SiteMeshFilter> sitemeshFilter() {
		FilterRegistrationBean<SiteMeshFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new SiteMeshFilter());
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(2);
		return registrationBean;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		CustomPageableHandlerMethodArgumentResolver resolver = new CustomPageableHandlerMethodArgumentResolver();
		resolver.setFallbackPageable(Pageable.unpaged());
		resolvers.add(resolver);
		WebMvcConfigurer.super.addArgumentResolvers(resolvers);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		String[] excludedPathPatterns = new String [] {
				"/js/**",
				"/css/**",
				"/fonts/**",
				"/img/**",
				"/images/**",
				"/extensions/**",
				"/webjars/**",
				"/**/datatable/**",
				"/**/selection/**",
				"/**/rest/notib**",
				"/**/rest/notib/**",
				"/**/rest/portafib**",
				"/**/rest/portafib/**",
				"/api/historic**",
				"/api/historic/**",
				"/api-docs/**",
				"/**/api-docs/",
				"/public/**",
				"/api",
				"/api/**"
		};
		registry.addInterceptor(metaExpedientInterceptor).excludePathPatterns(excludedPathPatterns);
		registry.addInterceptor(aplicacioInterceptor).excludePathPatterns(excludedPathPatterns);
		registry.addInterceptor(sessioInterceptor).excludePathPatterns(excludedPathPatterns);
		registry.addInterceptor(llistaEntitatsInterceptor).excludePathPatterns(excludedPathPatterns);
		registry.addInterceptor(llistaRolsInterceptor).excludePathPatterns(excludedPathPatterns);
		registry.addInterceptor(modalInterceptor).excludePathPatterns(excludedPathPatterns);
		registry.addInterceptor(nodecoInterceptor).excludePathPatterns(excludedPathPatterns);
		registry.addInterceptor(ajaxInterceptor).excludePathPatterns(excludedPathPatterns);
		registry.addInterceptor(expedientsInterceptor).excludePathPatterns(excludedPathPatterns);
		registry.addInterceptor(tasquesPendentsInterceptor).excludePathPatterns(excludedPathPatterns);
		registry.addInterceptor(anotacionsPendentsInterceptor).excludePathPatterns(excludedPathPatterns);
		registry.addInterceptor(seguimentEnviamentsUsuariInterceptor).excludePathPatterns(excludedPathPatterns);
		registry.addInterceptor(avisosInterceptor).excludePathPatterns(excludedPathPatterns);
		registry.addInterceptor(fluxFirmaInterceptor).excludePathPatterns(excludedPathPatterns);
		registry.addInterceptor(accesAdminEntitatOAdminOrganORevisorInterceptor).
				addPathPatterns(
						"/metaExpedient**",
						"/metaExpedient/**",
						"/grup**",
						"/grup/**").
				excludePathPatterns(
						"/metaExpedient/findPerLectura/**",
						"/metaExpedient/metaDadaPermisLectura/domini",
						"/metaExpedient/**/meta**",
						"/metaExpedient/**/meta**/**",
						"/metaExpedient/**/tasca**",
						"/metaExpedient/**/tasca**/**",
						"/metaExpedient/**/grup**",
						"/metaExpedient/**/grup**/**",
						"/metaExpedient/**/permis**",
						"/metaExpedient/**/permis**/**",
						"/metaExpedientRevisio**",
						"/metaExpedientRevisio/**");
		registry.addInterceptor(accesAdminEntitatInterceptor).
				addPathPatterns(
						"/domini**",
						"/domini/**",
						"/organgestor**",
						"/organgestor/**",
						"/permis**",
						"/permis/**",
						"/contingutAdmin**",
						"/contingutAdmin/**",
						"/seguimentTasques**",
						"/seguimentTasques/**",
						"/seguimentExpedientsPendents**",
						"/seguimentExpedientsPendents/**",
						"/urlInstruccio**",
						"/urlInstruccio/**").
				excludePathPatterns(
						"/urlInstruccio/list**",
						"/urlInstruccio/list/",
						"/metaExpedientRevisio**",
						"/metaExpedientRevisio/**");
		registry.addInterceptor(accesAdminEntitatOrUsuariInterceptor).
				addPathPatterns(
						"/seguimentPortafirmes**",
						"/seguimentPortafirmes/**",
						"/seguimentNotificacions**",
						"/seguimentNotificacions/**");
		registry.addInterceptor(accesAdminEntitatORevisorInterceptor).
				addPathPatterns(
						"/metaExpedientRevisio**",
						"/metaExpedientRevisio/**");
		registry.addInterceptor(accesURLsInstruccioInterceptor).
				addPathPatterns(
						"/urlInstruccio**",
						"/urlInstruccio/**");
		registry.addInterceptor(accesFluxosFirmaUsuariInterceptor).
				addPathPatterns(
						"/fluxusuari**",
						"/fluxusuari/**");
		registry.addInterceptor(accesSuperInterceptor).
				addPathPatterns(
						"/entitat**",
						"/entitat/**",
						"/integracio**",
						"/integracio/**",
						"/excepcio**",
						"/excepcio/**",
						"/config**",
						"/config/**",
						"/avis**",
						"/avis/**").
				excludePathPatterns(
						"/entitat/getEntitatLogo");
	}


	/**
	 * Configura el firewall per permetre caràcters codificats com el % ja que aquests s'usen en la codificació
	 * dels identificadors en els enllaços públics de descàrrega de documents.
	 * 
	 * @return
	 */
	@Bean
	public HttpFirewall getHttpFirewall() {
		StrictHttpFirewall firewall = new StrictHttpFirewall();
		firewall.setAllowSemicolon(true);
		firewall.setAllowUrlEncodedSlash(true);
		firewall.setAllowBackSlash(true);
		firewall.setAllowUrlEncodedPercent(true);
		firewall.setAllowUrlEncodedPeriod(true);
		return firewall;
	}

	public static class CustomPageableHandlerMethodArgumentResolver extends PageableHandlerMethodArgumentResolverSupport implements PageableArgumentResolver {
		private final SortArgumentResolver sortResolver = new SortHandlerMethodArgumentResolver();
		@Override
		public boolean supportsParameter(MethodParameter parameter) {
			return Pageable.class.equals(parameter.getParameterType());
		}
		@Override
		public Pageable resolveArgument(
				MethodParameter methodParameter,
				@Nullable ModelAndViewContainer mavContainer,
				NativeWebRequest webRequest,
				@Nullable WebDataBinderFactory binderFactory) {
			String page = webRequest.getParameter(getParameterNameToUse(getPageParameterName(), methodParameter));
			String pageSize = webRequest.getParameter(getParameterNameToUse(getSizeParameterName(), methodParameter));
			Sort sort = sortResolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
			boolean withPageOrSort = page != null || pageSize != null || sort.isSorted();
			if (!withPageOrSort) {
				return null;
			} else if (page != null && page.equals("UNPAGED")) {
				return new UnpagedButSorted(sort);
			} else {
				Pageable pageable = getPageable(
						methodParameter,
						page == null ? "0" : page,
						pageSize == null || "0".equals(pageSize) ? "10" : pageSize);
				if (sort.isSorted()) {
					return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
				}
				return pageable;
			}
		}
	}

}
