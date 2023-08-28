/**
 * 
 */
package es.caib.ripea.war.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.api.utils.Utils;


@Controller
@RequestMapping("/expedientajax") 
public class AjaxExpedientController extends BaseUserOAdminOOrganController {

	@Autowired
	private ExpedientService expedientService;


	
	@RequestMapping(value = "/expedient", method = RequestMethod.GET)
	@ResponseBody
	public List<ExpedientDto> get(
			HttpServletRequest request,
			@RequestParam String term, // you can't use "/" that is part of Número of expedient in @PathVariable even encoded version "%2F" so it was changed to use @RequestParam instead
			Model model) {

		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		List<ExpedientDto> expedients = expedientService.findByText(
				entitat.getId(), 
				Utils.trim(term), 
				getRolActual(request));
		
		return expedients;
	}

	
	@RequestMapping(value = "/expedient/item/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ExpedientDto getItem(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		return expedientService.findById(
				entitat.getId(),
				id, null);
	}

}
