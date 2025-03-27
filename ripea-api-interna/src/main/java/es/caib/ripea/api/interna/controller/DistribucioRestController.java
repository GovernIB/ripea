package es.caib.ripea.api.interna.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.ws.backoffice.AnotacioRegistreId;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.ExpedientPeticioService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/distribucio")
public class DistribucioRestController {

	@Autowired private ExpedientPeticioService expedientPeticioService;
	@Autowired private AplicacioService aplicacioService;
	
	@RequestMapping(value = "/comunicarAnotacionsPendents", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<String> event(@RequestBody List<AnotacioRegistreId> event) {
		try {
			//Guardam el usuari a la taula de BBDD, ja que sino algunes dades d'auditoria podrien donar error
			aplicacioService.processarAutenticacioUsuari();
			expedientPeticioService.crearExpedientPeticion(event);
			return new ResponseEntity<String>("OK", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
