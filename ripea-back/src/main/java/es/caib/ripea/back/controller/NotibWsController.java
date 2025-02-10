package es.caib.ripea.back.controller;

import es.caib.notib.client.domini.NotificacioCanviClient;
import es.caib.ripea.service.intf.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador per a les peticions al servei REST de Notib
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/rest/notib")
public class NotibWsController {

	@Autowired
	private DocumentService documentService;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public String get() {
		return "restNotib";
	}

	@RequestMapping(value = "/notificaCanvi", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void notificaCanvi(
			@RequestBody NotificacioCanviClient notificacioCanvi) {
		documentService.notificacioActualitzarEstat(
				notificacioCanvi.getIdentificador(), 
				notificacioCanvi.getReferenciaEnviament());
	}

}