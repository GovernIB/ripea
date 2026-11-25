package es.caib.ripea.back.diag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import es.caib.ripea.service.intf.utils.TempsTraca;

/**
 * Interceptors de la traça de temps de les peticions a l'API (veure {@link TempsTraca}).
 *
 * <p>Una instància d'aquesta classe s'ha de registrar la darrera: el seu postHandle és el primer que s'executa
 * després del controlador, just quan s'acaba d'escriure la resposta, i tanca el pas de serialització que obre
 * {@link TempsTracaResponseBodyAdvice}.</p>
 *
 * <p>{@link #cronometrat(String, HandlerInterceptor)} embolcalla un interceptor de l'aplicació per mesurar el
 * temps dels seus mètodes sense modificar-lo.</p>
 */
public class TempsTracaInterceptor implements AsyncHandlerInterceptor {

	public static HandlerInterceptor cronometrat(String nom, HandlerInterceptor interceptor) {
		return new InterceptorCronometrat(nom, interceptor);
	}

	@Override
	public void postHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler,
			ModelAndView modelAndView) {
		TempsTraca.tancaFase();
	}

	@Override
	public void afterCompletion(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler,
			Exception ex) {
		TempsTraca.tancaFase();
	}

	private static class InterceptorCronometrat implements AsyncHandlerInterceptor {

		private final String nom;
		private final HandlerInterceptor interceptor;

		private InterceptorCronometrat(String nom, HandlerInterceptor interceptor) {
			this.nom = nom;
			this.interceptor = interceptor;
		}

		@Override
		public boolean preHandle(
				HttpServletRequest request,
				HttpServletResponse response,
				Object handler) throws Exception {
			try (TempsTraca.Tram tram = TempsTraca.inicia(nom + ".preHandle")) {
				return interceptor.preHandle(request, response, handler);
			}
		}

		@Override
		public void postHandle(
				HttpServletRequest request,
				HttpServletResponse response,
				Object handler,
				ModelAndView modelAndView) throws Exception {
			try (TempsTraca.Tram tram = TempsTraca.inicia(nom + ".postHandle")) {
				interceptor.postHandle(request, response, handler, modelAndView);
			}
		}

		@Override
		public void afterCompletion(
				HttpServletRequest request,
				HttpServletResponse response,
				Object handler,
				Exception ex) throws Exception {
			try (TempsTraca.Tram tram = TempsTraca.inicia(nom + ".afterCompletion")) {
				interceptor.afterCompletion(request, response, handler, ex);
			}
		}

		@Override
		public void afterConcurrentHandlingStarted(
				HttpServletRequest request,
				HttpServletResponse response,
				Object handler) throws Exception {
			if (interceptor instanceof AsyncHandlerInterceptor) {
				((AsyncHandlerInterceptor)interceptor).afterConcurrentHandlingStarted(request, response, handler);
			}
		}
	}

}
