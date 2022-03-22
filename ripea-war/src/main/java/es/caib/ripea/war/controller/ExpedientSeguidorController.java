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

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.ExpedientSeguidorService;
import es.caib.ripea.core.api.service.ExpedientService;

/**
 * Controlador per als seguidors dels expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/expedient")
public class ExpedientSeguidorController extends BaseUserController {

	@Autowired
	private ExpedientSeguidorService expedientSeguidorService;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private ExpedientService expedientService;
	
	@RequestMapping(value = "/{expedientId}/follow", method = RequestMethod.GET)
	public String follow(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@RequestParam(required = false) String contingutId,
			Model model) {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		expedientSeguidorService.follow(
				entitatActual.getId(),
				expedientId);
		ExpedientDto expedient = expedientService.findById(
				entitatActual.getId(),
				expedientId, null);
		String url;
		if (contingutId != null) {
			url = "redirect:../../contingut/" + contingutId;
		} else {
			url = "redirect:../../contingut/" + expedientId;
		}
		return getAjaxControllerReturnValueSuccess(
				request,
				url,
				"expedient.controller.follow.ok",
				new Object[] {expedient.getNom()});
	}
	
	@RequestMapping(value = "/{expedientId}/unfollow", method = RequestMethod.GET)
	public String unfollow(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@RequestParam(required = false) String contingutId,
			Model model) {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		expedientSeguidorService.unfollow(
				entitatActual.getId(),
				expedientId);
		ExpedientDto expedient = expedientService.findById(
				entitatActual.getId(),
				expedientId, null);
		String url;
		if (contingutId != null) {
			url = "redirect:../../contingut/" + contingutId;
		} else {
			url = "redirect:../../contingut/" + expedientId;
		}
		return getAjaxControllerReturnValueSuccess(
				request,
				url,
				"expedient.controller.unfollow.ok",
				new Object[] {expedient.getNom()});
	}
	
	@RequestMapping(value="/{expedientId}/seguidors", method = RequestMethod.GET)
	public String getFollowers(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		UsuariDto usuariActual = aplicacioService.getUsuariActual();
		List<UsuariDto> followers = expedientSeguidorService.getFollowersExpedient(
				entitatActual.getId(),
				expedientId);
	
		model.addAttribute(
				"expedient",
				expedientService.findById(
						entitatActual.getId(),
						expedientId, null));
		
		model.addAttribute("followers", followers);
		model.addAttribute("usuariActual", usuariActual);		
		return "expedientSeguidors";
	}
}
