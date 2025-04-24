package es.caib.ripea.api.interna.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import es.caib.notib.client.domini.NotificacioCanviClient;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador per a les peticions al servei REST de Notib
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/notib")
@Tag(name = "Integració Notib - RIPEA", description = "Recepció de canvi de estat de notificacions")
public class NotibWsController {

	@Autowired private DocumentService documentService;
	@Autowired private AplicacioService aplicacioService;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public String get() {
		return "restNotib";
	}

	@RequestMapping(value = "/notificaCanvi", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	@Operation(
			summary = "Recepció de canvi de estat de notificació enviada previament a Notib.",
			description = "Actualitza les dades de la notificació a la BBDD de RIPEA.")
	public void notificaCanvi(@RequestBody NotificacioCanviClient notificacioCanvi) {
		// Crear un usuario autenticado simulado. En portafib no se puede configurar una autenticación BASIC
        User user = new User("$notib_ripea", "notib_ripea", Collections.singletonList(new SimpleGrantedAuthority("tothom")));
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
		//Guardam el usuari a la taula de BBDD, ja que sino algunes dades d'auditoria donen error
		aplicacioService.processarAutenticacioUsuari(false);
		documentService.notificacioActualitzarEstat(notificacioCanvi.getIdentificador(), notificacioCanvi.getReferenciaEnviament());
	}
}