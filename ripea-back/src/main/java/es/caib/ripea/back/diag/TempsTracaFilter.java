package es.caib.ripea.back.diag;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import es.caib.ripea.service.intf.utils.TempsTraca;

/**
 * Obre la traça de temps de cada petició a l'API REST i, quan acaba, l'escriu al log (veure {@link TempsTraca}).
 *
 * <p>Va davant de tots els filtres de l'aplicació (també del de Spring Security): el temps total inclou la
 * seguretat, els interceptors, el controlador i la serialització de la resposta. No inclou l'autenticació que
 * fa l'adaptador de Keycloak de JBoss abans dels filtres ni la xarxa.</p>
 *
 * <p>Les connexions SSE no es tracen: són peticions de llarga durada.</p>
 */
public class TempsTracaFilter implements Filter {

	private static final int MAX_LONGITUD_PARAMETRES = 2000;

	@Override
	public void doFilter(
			ServletRequest servletRequest,
			ServletResponse servletResponse,
			FilterChain chain) throws IOException, ServletException {
		if (!TempsTraca.isActiva() || !(servletRequest instanceof HttpServletRequest) || !(servletResponse instanceof HttpServletResponse)) {
			chain.doFilter(servletRequest, servletResponse);
			return;
		}
		HttpServletRequest request = (HttpServletRequest)servletRequest;
		HttpServletResponse response = (HttpServletResponse)servletResponse;
		if (request.getRequestURI().contains("/api/sse")) {
			chain.doFilter(servletRequest, servletResponse);
			return;
		}
		try (TempsTraca.Tram tram = TempsTraca.iniciaPeticio(request.getMethod() + " " + request.getRequestURI())) {
			try {
				chain.doFilter(servletRequest, servletResponse);
			} finally {
				TempsTraca.anota("estat " + response.getStatus());
				if (request.getRemoteUser() != null) {
					TempsTraca.anota("usuari " + request.getRemoteUser());
				}
				TempsTraca.parametres(parametres(request.getQueryString()));
			}
		}
	}

	private static String parametres(String queryString) {
		if (queryString == null) {
			return null;
		}
		String parametres;
		try {
			parametres = URLDecoder.decode(queryString, StandardCharsets.UTF_8.name());
		} catch (Exception ex) {
			parametres = queryString;
		}
		return parametres.length() > MAX_LONGITUD_PARAMETRES ? parametres.substring(0, MAX_LONGITUD_PARAMETRES) + "..." : parametres;
	}

}
