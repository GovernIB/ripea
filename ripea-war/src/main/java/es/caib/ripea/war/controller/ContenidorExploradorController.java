/**
 * 
 */
package es.caib.ripea.war.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.service.ContingutService;

/**
 * Controlador per a navegar pels contenidors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/contenidor")
public class ContenidorExploradorController extends BaseUserController {

	@Autowired
	private ContingutService contenidorService;



	@RequestMapping(value = "/explora/{contenidorArrelId}/{contenidorId}", method = RequestMethod.GET)
	@ResponseBody
	public ContingutDto get(
			HttpServletRequest request,
			@PathVariable Long contenidorArrelId,
			@PathVariable Long contenidorId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutDto contenidor = contenidorService.findAmbIdUser(
				entitatActual.getId(),
				contenidorId,
				true,
				false);
		contenidor.setContenidorArrelIdPerPath(contenidorArrelId);
		return contenidor;
	}

}
