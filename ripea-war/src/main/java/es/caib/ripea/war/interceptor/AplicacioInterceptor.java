/**
 * 
 */
package es.caib.ripea.war.interceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Interceptor per a les accions de context d'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AplicacioInterceptor extends HandlerInterceptorAdapter {

	public static final String REQUEST_ATTRIBUTE_MANIFEST_ATRIBUTES = "manifestAtributes";

	@Autowired
	private ServletContext servletContext;

	private Map<String, Object> manifestAtributsMap;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (manifestAtributsMap == null) {
			Manifest manifest = new Manifest(servletContext.getResourceAsStream("/" + JarFile.MANIFEST_NAME));
			Attributes manifestAtributs = manifest.getMainAttributes();
			manifestAtributsMap = new HashMap<String, Object>();
			for (Object key: new HashMap(manifestAtributs).keySet()) {
				manifestAtributsMap.put(key.toString(), manifestAtributs.get(key));
			}
		}
		request.setAttribute(
				REQUEST_ATTRIBUTE_MANIFEST_ATRIBUTES,
				manifestAtributsMap);
		request.setAttribute(
				"requestLocale",
				RequestContextUtils.getLocale(request).getLanguage());
		return true;
	}

}
