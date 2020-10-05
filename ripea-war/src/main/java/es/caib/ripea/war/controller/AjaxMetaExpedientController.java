/**
 * 
 */
package es.caib.ripea.war.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.service.MetaExpedientService;

/**
 * Controlador per a les consultes ajax dels metaexpedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/metaexpedientajax") // No podem posar "/ajaxuser" per mor del AjaxInterceptor
public class AjaxMetaExpedientController extends BaseUserController {

	@Autowired
	private MetaExpedientService metaExpedientService;


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
		metaExpedientsPermisLectura = metaExpedientService.findActiusAmbEntitatPerLectura(
				entitat.getId(), 
				text);
		
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
	


	private static final Logger logger = LoggerFactory.getLogger(AjaxMetaExpedientController.class);

}
