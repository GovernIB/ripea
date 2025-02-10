/**
 * 
 */
package es.caib.ripea.back.interceptor;

import es.caib.ripea.back.helper.ExpedientHelper;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.MetaExpedientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor per a gestionar la llista d'entitats a cada pàgina.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ExpedientsInterceptor implements AsyncHandlerInterceptor {

	@Autowired
	private MetaExpedientService metaExpedientService;
	@Autowired
	private AplicacioService aplicacioService;

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		ExpedientHelper.accesUsuariExpedients(
				request, 
				metaExpedientService);
		ExpedientHelper.accesUsuariEstadistiques(
				request, 
				metaExpedientService);
		ExpedientHelper.setConversioDefinitiu(
				request, 
				aplicacioService);
		ExpedientHelper.setUrlValidacioDefinida(
				request, 
				aplicacioService);
		ExpedientHelper.setUrlsInstruccioActiu(
				request, 
				aplicacioService);
		return true;
	}

}
