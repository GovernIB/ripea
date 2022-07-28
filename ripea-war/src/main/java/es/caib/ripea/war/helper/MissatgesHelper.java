/**
 * 
 */
package es.caib.ripea.war.helper;

import org.apache.commons.lang.exception.ExceptionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper per a mostrar missatges d'alerta o informació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MissatgesHelper {

	public static final String SESSION_ATTRIBUTE_ERROR = "MissatgesHelper.Error";
	public static final String SESSION_ATTRIBUTE_WARNING = "MissatgesHelper.Warning";
	public static final String SESSION_ATTRIBUTE_SUCCESS = "MissatgesHelper.Success";
	public static final String SESSION_ATTRIBUTE_INFO = "MissatgesHelper.Info";

	public static void error(
			HttpServletRequest request,
			String text,
			Throwable ex) {
		newAlert(
				request,
				SESSION_ATTRIBUTE_ERROR,
				text,
				ex);
	}
	public static void warning(
			HttpServletRequest request,
			String text) {
		newAlert(
				request,
				SESSION_ATTRIBUTE_WARNING,
				text);
	}
	public static void success(
			HttpServletRequest request,
			String text) {
		newAlert(
				request,
				SESSION_ATTRIBUTE_SUCCESS,
				text);
	}
	public static void info(
			HttpServletRequest request,
			String text) {
		newAlert(
				request,
				SESSION_ATTRIBUTE_INFO,
				text);
	}

	public List<Alert> getErrors(
			HttpServletRequest request,
			boolean delete) {
		return getAlerts(
				request,
				SESSION_ATTRIBUTE_ERROR,
				delete);
	}
	public List<Alert> getWarnings(
			HttpServletRequest request,
			boolean delete) {
		return getAlerts(
				request,
				SESSION_ATTRIBUTE_WARNING,
				delete);
	}
	public List<Alert> getSuccesses(
			HttpServletRequest request,
			boolean delete) {
		return getAlerts(
				request,
				SESSION_ATTRIBUTE_SUCCESS,
				delete);
	}
	public List<Alert> getInfos(
			HttpServletRequest request,
			boolean delete) {
		return getAlerts(
				request,
				SESSION_ATTRIBUTE_INFO,
				delete);
	}

	@SuppressWarnings("unchecked")
	private static void newAlert(
			HttpServletRequest request,
			String attributeName,
			String text) {
		HttpSession session = request.getSession();
		List<Alert> alerts = (List<Alert>)session.getAttribute(attributeName);
		if (alerts == null) {
			alerts = new ArrayList<>();
			session.setAttribute(attributeName, alerts);
		}
		alerts.add(Alert.builder().text(text).build());
	}
	private static void newAlert(
			HttpServletRequest request,
			String attributeName,
			String message,
			Throwable ex) {
		newAlert(request, attributeName, message, ex != null ? ExceptionUtils.getStackTrace(ex) : null);
	}

	@SuppressWarnings("unchecked")
	private static void newAlert(
			HttpServletRequest request,
			String attributeName,
			String text,
			String trace) {
		HttpSession session = request.getSession();
		List<Alert> alerts = (List<Alert>)session.getAttribute(attributeName);
		if (alerts == null) {
			alerts = new ArrayList<>();
			session.setAttribute(attributeName, alerts);
		}
		alerts.add(Alert.builder().text(text).trace(trace).build());
	}

	@SuppressWarnings("unchecked")
	private static List<Alert> getAlerts(
			HttpServletRequest request,
			String attributeName,
			boolean delete) {
		HttpSession session = request.getSession();
		List<Alert> alerts = (List<Alert>)session.getAttribute(attributeName);
		if (delete)
			session.removeAttribute(attributeName);
		return alerts;
	}

}
