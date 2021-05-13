package es.caib.ripea.war.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.OrganGestorService;
import es.caib.ripea.war.helper.EntitatHelper;
import es.caib.ripea.war.helper.RolHelper;

/**
 * Controlador per a les consultes ajax dels usuaris normals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/organgestorajax") // No podem posar "/ajaxuser" per mor del AjaxInterceptor
public class AjaxOrganGestorController extends BaseAdminController{

	@Autowired
	private OrganGestorService organGestorService;

	@RequestMapping(value = "/organgestor", method = RequestMethod.GET)
	@ResponseBody
	public List<OrganGestorDto> get(HttpServletRequest request, Model model) {
		return get(request, null, model);
	}
	
	@RequestMapping(value = "/organgestor/{text}", method = RequestMethod.GET)
	@ResponseBody
	public List<OrganGestorDto> get(HttpServletRequest request, @PathVariable String text, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		
		try {
			text = URLDecoder.decode(request.getRequestURI().split("/")[4], StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) { }
		
		List<OrganGestorDto> organGestorsList = new ArrayList<OrganGestorDto>();
 		if (RolHelper.isRolActualAdministradorOrgan(request)) {
			organGestorsList = organGestorService.findAccessiblesUsuariActualRolAdmin(
					entitatActual.getId(),
					EntitatHelper.getOrganGestorActual(request).getId(),
					text);
		} else if (RolHelper.isRolActualAdministrador(request)){
			organGestorsList = organGestorService.findByEntitat(
					entitatActual.getId(),
					text);
		} else if (RolHelper.isRolActualUsuari(request)) {
			organGestorsList = organGestorService.findAccessiblesUsuariActualRolUsuari(entitatActual.getId(), text);
		}
		
		if (text == null) {
			return organGestorsList.subList(0, 5);
		}

		return organGestorsList;
	}
		
	@RequestMapping(value = "/organgestor/item/{id}", method = RequestMethod.GET)
	@ResponseBody
	public OrganGestorDto getItem(HttpServletRequest request, @PathVariable Long id, Model model) {
		getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		
		try {
			return organGestorService.findItem(id);
		} catch (NotFoundException e) {
			return null;
		} 
	}
	
	
	@RequestMapping(value = "/organgestorcodi", method = RequestMethod.GET)
	@ResponseBody
	public List<OrganGestorDto> getAmbCodi(HttpServletRequest request, Model model) {
		return get(request, null, model);
	}
	
	@RequestMapping(value = "/organgestorcodi/{text}", method = RequestMethod.GET)
	@ResponseBody
	public List<OrganGestorDto> getAmbCodi(HttpServletRequest request, @PathVariable String text, Model model) {
		return get(request, text, model);
	}
		
	@RequestMapping(value = "/organgestorcodi/item/{codi}", method = RequestMethod.GET)
	@ResponseBody
	public OrganGestorDto getItemAmbCodi(HttpServletRequest request, @PathVariable String codi, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		
		try {
			return organGestorService.findItemByEntitatAndCodi(entitatActual.getId(), codi);
		} catch (NotFoundException e) {
			return null;
		} 
	}
	
	
	
	
	
}
