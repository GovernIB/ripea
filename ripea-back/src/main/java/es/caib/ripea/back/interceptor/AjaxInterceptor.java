/**
 * 
 */
package es.caib.ripea.back.interceptor;

import es.caib.ripea.back.helper.AjaxHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor per a redirigir les peticions fetes amb AJAX.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class AjaxInterceptor implements AsyncHandlerInterceptor {

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		boolean resposta = AjaxHelper.comprovarAjaxInterceptor(request, response);
		//System.out.println(">>> AJAX: " + request.getRequestURI() + ", " + AjaxHelper.isAjax(request));
		return resposta;
	}

}
