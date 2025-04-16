package es.caib.ripea.api.interna.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.ws.backoffice.AnotacioRegistreId;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.ExpedientPeticioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/distribucio")
@Tag(name = "Integració distribució - RIPEA", description = "Recepció d'anotacions de registre")
public class DistribucioRestController {

	@Autowired private ExpedientPeticioService expedientPeticioService;
	@Autowired private AplicacioService aplicacioService;
	
	@RequestMapping(value = "/comunicarAnotacionsPendents", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Operation(
			summary = "Recepció d'anotacions de registre (referencia i número de registre)",
			description = "Guarda a la Base de dades les dades de identificació de la anotació, per posteriorment processarles i descarregar la informació completa.",
			security = { @SecurityRequirement(name = "basicAuth") })
	public ResponseEntity<String> event(@RequestBody List<AnotacioRegistreId> event) {
		try {
			//Guardam el usuari a la taula de BBDD, aquest en concret ha de existir a Keycloak, i tendrem les dades de auditoria correctes.
			aplicacioService.processarAutenticacioUsuari(false);
			expedientPeticioService.crearExpedientPeticion(event);
			return new ResponseEntity<String>("OK", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			//Eliminam la autenticació ja que nomes hauria de servir per la petició actual
			//Sino despres arriben altres peticions (per portafib) i encara estam autenticats amb $distribucio_ripea
			SecurityContextHolder.clearContext();
		}
	}
}
