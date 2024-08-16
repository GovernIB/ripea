package es.caib.ripea.war.helper;

import javax.servlet.http.HttpServletRequest;

import es.caib.ripea.core.api.service.AplicacioService;

public class SeguimentEnviamentsUsuariHelper {

    private static final String SESSION_MOSTRAR_SEGUIMENT_ENVIAMENTS_USUARI = "SeguimentEnviamentsHelper.mostrarSeguimentEnviamentsUsuari";

	public static Boolean isMostrarSeguimentEnviamentsUsuariActiu(HttpServletRequest request) {
		return (Boolean)request.getSession().getAttribute(SESSION_MOSTRAR_SEGUIMENT_ENVIAMENTS_USUARI);
	}
	
	public static void setMostrarSeguimentEnviamentsUsuari(
			HttpServletRequest request,
			AplicacioService aplicacioService) {
		boolean isConversioDefinitiuActiva = Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.mostrar.seguiment.enviaments.usuari"));
		request.getSession().setAttribute(
				SESSION_MOSTRAR_SEGUIMENT_ENVIAMENTS_USUARI,
				isConversioDefinitiuActiva);
	}
	
}
