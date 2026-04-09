package es.caib.ripea.back.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.back.helper.RolHelper;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.MetaExpedientDto;
import es.caib.ripea.service.intf.service.MetaExpedientService;

/**
 * Controlador per a les consultes ajax dels metaexpedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/metaexpedientajax") // No podem posar "/ajaxuser" per mor del AjaxInterceptor
public class AjaxMetaExpedientController extends BaseUserOAdminOOrganController {

	@Autowired private MetaExpedientService metaExpedientService;

	@RequestMapping(value = "/updateMetaDocumentFluxNom", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> executeRenomFlux(
			HttpServletRequest request,
			Model model) {
		try {
			String resultat = metaExpedientService.initMetaDocumentFlux();
			return ResponseEntity.ok(resultat);
		} catch (Exception e) {
			String message = "Error al actualitzar el nom dels fluxos: "+e.getMessage();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}	
	
	@RequestMapping(value = "/metaexpedients/{text}/{organId}", method = RequestMethod.GET)
	@ResponseBody
	public List<MetaExpedientDto> get(
			HttpServletRequest request,
			@PathVariable String text,
			@PathVariable Long organId,
			Model model) {
		
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		List<MetaExpedientDto> metaExpedientsPermisLectura;

		metaExpedientsPermisLectura = metaExpedientService.findActiusAmbOrganGestorPermisLectura(
				entitat.getId(),
				organId, 
				text);
		
		return metaExpedientsPermisLectura;
	}
	
	@RequestMapping(value = "/metaexpedients/{text}", method = RequestMethod.GET)
	@ResponseBody
	public List<MetaExpedientDto> get(
			HttpServletRequest request,
			@PathVariable String text,
			Model model) {
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		List<MetaExpedientDto> metaExpedientsPermisLectura;
		metaExpedientsPermisLectura = metaExpedientService.findActius(
				entitat.getId(), 
				text, 
				RolHelper.getRolActual(request), 
				false, 
				null);
		return metaExpedientsPermisLectura;
	}
	
	@RequestMapping(value = "/metaexpedients/estadistiques/{text}", method = RequestMethod.GET)
	@ResponseBody
	public List<MetaExpedientDto> getMetaExpedientsAmbPermis(
			HttpServletRequest request,
			@PathVariable String text,
			Model model) {
		String rolActual = RolHelper.getRolActual(request);
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		List<MetaExpedientDto> metaExpedientsPermisLectura;
		if ("tothom".equals(rolActual)) {
			metaExpedientsPermisLectura = metaExpedientService.findActiusAmbEntitatPerConsultaEstadistiques(
					entitat.getId(), 
					text, 
					rolActual);
		} else {
			metaExpedientsPermisLectura = metaExpedientService.findActius(
					entitat.getId(), 
					text, 
					rolActual, 
					false, 
					null);
		}
		return metaExpedientsPermisLectura;
	}
	
	
	@RequestMapping(value = "/metaexpedient/item/{id}", method = RequestMethod.GET)
	@ResponseBody
	public MetaExpedientDto getItem(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		return metaExpedientService.findById(
				getEntitatActualComprovantPermisos(request).getId(),
				id);
	}

}
