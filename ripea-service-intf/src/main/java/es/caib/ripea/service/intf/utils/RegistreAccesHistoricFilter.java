package es.caib.ripea.service.intf.utils;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import es.caib.ripea.service.intf.dto.historic.AccesHistoricDto;
import es.caib.ripea.service.intf.service.HistoricService;
import lombok.extern.slf4j.Slf4j;

/**
 * Filtre que registra cada petició a la consulta d'estadístiques (històrics) per saber si algú en fa ús: la pantalla
 * JSP ({@code /historic/*} de ripea-back) i l'API interna ({@code /api/historic/*} de ripea-api-interna). Envia les
 * dades de la petició a {@link HistoricService#registrarAcces(AccesHistoricDto)}, que les desa si el registre està
 * actiu.
 *
 * S'ha de registrar darrere el filtre de Spring Security (ordre per defecte d'un {@code FilterRegistrationBean}): així
 * l'usuari ja està autenticat quan es crida el servei. Les peticions que rebutja la seguretat abans d'arribar-hi no es
 * registren.
 *
 * Un error del registre mai no afecta la petició: només es deixa al log.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class RegistreAccesHistoricFilter implements Filter {

	/** Mida màxima dels camps de text llargs (paràmetres, user-agent, error). */
	static final int MIDA_MAXIMA_CAMP = 2000;

	private final HistoricService historicService;
	private final String aplicacio;
	private final Function<HttpServletRequest, String> rolActualResolver;

	/**
	 * @param historicService   servei que desa l'accés.
	 * @param aplicacio         {@link AccesHistoricDto#APLICACIO_JSP} o {@link AccesHistoricDto#APLICACIO_API_INTERNA}.
	 * @param rolActualResolver obté el rol actual de l'usuari de la petició, o null si l'aplicació no en té.
	 */
	public RegistreAccesHistoricFilter(
			HistoricService historicService,
			String aplicacio,
			Function<HttpServletRequest, String> rolActualResolver) {
		this.historicService = historicService;
		this.aplicacio = aplicacio;
		this.rolActualResolver = rolActualResolver;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
			chain.doFilter(request, response);
			return;
		}
		long inici = System.currentTimeMillis();
		Throwable error = null;
		try {
			chain.doFilter(request, response);
		} catch (IOException | ServletException | RuntimeException | Error ex) {
			error = ex;
			throw ex;
		} finally {
			registrar((HttpServletRequest) request, (HttpServletResponse) response, inici, error);
		}
	}

	private void registrar(HttpServletRequest request, HttpServletResponse response, long inici, Throwable error) {
		try {
			historicService.registrarAcces(crearAcces(request, response, inici, error));
		} catch (Exception ex) {
			log.error("No s'ha pogut registrar l'accés a la consulta d'estadístiques " + request.getRequestURI(), ex);
		}
	}

	AccesHistoricDto crearAcces(HttpServletRequest request, HttpServletResponse response, long inici, Throwable error) {
		AccesHistoricDto acces = new AccesHistoricDto();
		acces.setData(new Date(inici));
		acces.setAplicacio(aplicacio);
		acces.setMetodeHttp(request.getMethod());
		acces.setRuta(request.getRequestURI());
		acces.setParametres(retallar(parametres(request.getParameterMap())));
		acces.setIpRemota(request.getRemoteAddr());
		acces.setXForwardedFor(request.getHeader("X-Forwarded-For"));
		acces.setUsuari(request.getRemoteUser());
		acces.setRolActual(rolActual(request));
		acces.setUserAgent(retallar(request.getHeader("User-Agent")));
		// Si el controlador ha llançat una excepció, la resposta encara no té l'estat d'error: el posa el contenidor
		acces.setEstatHttp(error != null ? HttpServletResponse.SC_INTERNAL_SERVER_ERROR : response.getStatus());
		acces.setDuradaMs(System.currentTimeMillis() - inici);
		if (error != null) {
			acces.setError(retallar(error.getClass().getName() + ": " + error.getMessage()));
		}
		return acces;
	}

	private String rolActual(HttpServletRequest request) {
		if (rolActualResolver == null) {
			return null;
		}
		try {
			return rolActualResolver.apply(request);
		} catch (Exception ex) {
			log.debug("No s'ha pogut obtenir el rol actual per al registre d'accessos a la consulta d'estadístiques", ex);
			return null;
		}
	}

	/** Paràmetres de la petició (query string i formulari) com a {@code nom=valor1,valor2&nom2=valor}. */
	static String parametres(Map<String, String[]> parametres) {
		if (parametres == null || parametres.isEmpty()) {
			return null;
		}
		StringBuilder text = new StringBuilder();
		for (Map.Entry<String, String[]> parametre : parametres.entrySet()) {
			if (text.length() > 0) {
				text.append('&');
			}
			text.append(parametre.getKey()).append('=');
			if (parametre.getValue() != null) {
				text.append(String.join(",", parametre.getValue()));
			}
		}
		return text.toString();
	}

	static String retallar(String text) {
		if (text == null || text.length() <= MIDA_MAXIMA_CAMP) {
			return text;
		}
		return text.substring(0, MIDA_MAXIMA_CAMP) + "…";
	}

}
