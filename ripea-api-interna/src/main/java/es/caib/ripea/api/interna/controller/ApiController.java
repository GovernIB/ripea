package es.caib.ripea.api.interna.controller;

import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/api")
public class ApiController {

	@Autowired
	private DocumentService documentService;
	@Autowired
	private AplicacioService aplicacioService;
	
	@RequestMapping(value = {"/apidoc", "/rest"}, method = RequestMethod.GET)
	public String documentacio(HttpServletRequest request) {
		return "apidoc";
	}

	@RequestMapping(value = "/viaFirmaResponse" , method = RequestMethod.GET)
	public void get(HttpServletRequest request, HttpServletResponse response) {
		doService(request, response);
	}
	@RequestMapping(value = "/viaFirmaResponse" , method = RequestMethod.POST)
	public void post(HttpServletRequest request, HttpServletResponse response) {
		doService(request, response);
	}
	
	private void doService(HttpServletRequest req, HttpServletResponse resp) {
		if (req.getParameter("message")!=null) {
			try {
				// Crear un usuario autenticado simulado. En portafib no se puede configurar una autenticación BASIC
		        User user = new User("$ripea_viafirma", "ripea_viafirma", Collections.singletonList(new SimpleGrantedAuthority("tothom")));
		        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		        SecurityContextHolder.getContext().setAuthentication(authentication);
				//Guardam el usuari a la taula de BBDD, ja que sino algunes dades d'auditoria donen error
				aplicacioService.processarAutenticacioUsuari(false);

				
				String messageJson = req.getParameter("message");
				documentService.processarRespostaViaFirma(messageJson);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("null");
		}
	}

}