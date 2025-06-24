package es.caib.ripea.back.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.back.helper.EntitatHelper;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.ExpedientDto;
import es.caib.ripea.service.intf.service.ExpedientService;
import es.caib.ripea.service.intf.service.MetaDadaService;
import es.caib.ripea.service.intf.utils.Utils;

@Controller
@RequestMapping("/expedientajax") 
public class AjaxExpedientController extends BaseUserOAdminOOrganController {

	@Autowired private ExpedientService expedientService;
	@Autowired private MetaDadaService metaDadaService;
	
	@RequestMapping(value = "/normalitzaDadesDomini", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> normalitzaDadesDomini(
			HttpServletRequest request,
			HttpServletResponse response) {
		try {
			int num = metaDadaService.normalitzaDadesDomini();
//			return new ResponseEntity<String>("Normalitzades "+num+" dades de tipus domini.", HttpStatus.OK);
			return ResponseEntity
				    .ok()
				    .contentType(MediaType.TEXT_PLAIN)
				    .body("Normalitzades " + num + " dades de tipus domini.");
		} catch (Exception e) {
			e.printStackTrace();
//			return new ResponseEntity<String>("ERROR: "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			return ResponseEntity
				    .internalServerError()
				    .contentType(MediaType.TEXT_PLAIN)
				    .body(e.getMessage());
		}
	}
	
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