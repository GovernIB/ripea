package es.caib.ripea.back.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import es.caib.ripea.back.helper.FluxFirmaHelper;
import es.caib.ripea.service.intf.config.PropertyConfig;

@Component
public class AccesFluxosFirmaUsuariInterceptor implements AsyncHandlerInterceptor {

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (! FluxFirmaHelper.isCreacioFluxUsuariActiu(request)) {
			throw new SecurityException("Es necessari activar la propietat "+PropertyConfig.PERMETRE_USUARIS_CREAR_FLUX_PORTAFIB+" per accedir a la gestió de fluxos de firma", null);
		}
		return true;
	}
}