package es.caib.ripea.back.interceptor;

import es.caib.ripea.service.intf.base.service.ResourceServiceLocator;
import lombok.RequiredArgsConstructor;
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
@Component
@RequiredArgsConstructor
public class ResourceServiceLocatorInterceptor implements AsyncHandlerInterceptor {

	private final ApplicationContext applicationContext;

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		ResourceServiceLocator instance = applicationContext.getBean(ResourceServiceLocator.class);
		ResourceServiceLocator.setThreadLocalInstance(instance);
		return true;
	}

	public void postHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler,
			@Nullable ModelAndView modelAndView) throws Exception {
		ResourceServiceLocator.setThreadLocalInstance(null);
	}

}