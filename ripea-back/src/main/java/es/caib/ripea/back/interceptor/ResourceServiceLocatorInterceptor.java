package es.caib.ripea.back.interceptor;

import es.caib.ripea.back.base.util.ResourceServiceLocator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Implementació HandlerInterceptor que configura el ResourceServiceLocator com a variable thread local. Això soluciona
 * els problemes amb el ResourceServiceLocator que retorna les implementacions dels serveis en comptes de retornar la
 * corresponent instància de l'EJB.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceServiceLocatorInterceptor implements AsyncHandlerInterceptor {

	private final ApplicationContext applicationContext;

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) {
		try {
			ResourceServiceLocator instance = applicationContext.getBean(ResourceServiceLocator.class);
			ResourceServiceLocator.setThreadLocalInstance(instance);
		} catch (NoSuchBeanDefinitionException ex) {
			log.warn("Couldn't find any ResourceServiceLocator instance in Spring application context");
		}
		return true;
	}

	public void postHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler,
			@Nullable ModelAndView modelAndView) {
		ResourceServiceLocator.setThreadLocalInstance(null);
	}

}