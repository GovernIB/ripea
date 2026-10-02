package es.caib.ripea.back.interceptor;

import es.caib.ripea.back.helper.SessioHelper;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.EntitatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class SessioInterceptor implements AsyncHandlerInterceptor {

	@Autowired private AplicacioService aplicacioService;
    @Autowired private EntitatService entitatService;

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {

		String redireccio = SessioHelper.processarAutenticacio(request, response, aplicacioService, entitatService);

		if (redireccio==null) {
			return true;
		} else if (SessioHelper.isRedireccioUsuariInactiu(request, redireccio) && request.getServletPath().startsWith("/api/")) {
			// Les crides de la interfície REACT no poden seguir una redirecció a una pàgina HTML: es retorna
			// un 403 amb un codi que el client reconeix per dur l'usuari a la pàgina d'usuari inactiu.
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write("{\"code\":\"USUARI_INACTIU\"}");
			return false;
		} else {
			response.sendRedirect(redireccio);
			return false;
		}
	}

	@Override
	public void postHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler,
			ModelAndView modelAndView) throws Exception {
		// Com que l'usuari es cacheja a la sessió (SESSION_ATTRIBUTE_USUARI_ACTUAL) per no consultar-lo a
		// BD en cada petició, quan es modifica el perfil propi via l'API REST (p.ex. l'idioma) cal invalidar
		// aquesta còpia perquè la següent petició la recarregui fresca i el Locale (grids/filtres localitzats
		// pel backend) sigui el correcte. La recàrrega la fa el fallback defensiu de SessioHelper.
		String method = request.getMethod();
		String path = request.getServletPath();
		if (("PUT".equals(method) || "PATCH".equals(method))
				&& path != null && path.startsWith("/api/usuari/")
				&& response.getStatus() >= 200 && response.getStatus() < 300) {
			javax.servlet.http.HttpSession session = request.getSession(false);
			if (session != null) {
				session.removeAttribute(SessioHelper.SESSION_ATTRIBUTE_USUARI_ACTUAL);
			}
		}
	}
}
