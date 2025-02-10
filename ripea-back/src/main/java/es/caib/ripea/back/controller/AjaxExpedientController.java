package es.caib.ripea.back.controller;

import es.caib.ripea.back.helper.EntitatHelper;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.ExpedientDto;
import es.caib.ripea.service.intf.service.ExpedientService;
import es.caib.ripea.service.intf.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/expedientajax") 
public class AjaxExpedientController extends BaseUserOAdminOOrganController {

	@Autowired private ExpedientService expedientService;
	
	@RequestMapping(value = "/expedient/{procedimentId}", method = RequestMethod.GET)
	@ResponseBody
	public List<ExpedientDto> get(
			HttpServletRequest request,
			@PathVariable Long procedimentId,
			@RequestParam String term, // you can't use "/" that is part of Número of expedient in @PathVariable even encoded version "%2F" so it was changed to use @RequestParam instead
			Model model) {

		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		List<ExpedientDto> expedients = expedientService.findByText(
				entitat.getId(), 
				Utils.trim(term), 
				getRolActual(request), 
				procedimentId,
				EntitatHelper.getOrganGestorActualId(request));
		
		return expedients;
	}
	
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
				getRolActual(request), 
				null,
				EntitatHelper.getOrganGestorActualId(request));
		
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