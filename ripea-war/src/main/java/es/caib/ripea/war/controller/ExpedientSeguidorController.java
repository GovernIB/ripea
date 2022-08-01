package es.caib.ripea.war.controller;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.WebUtils;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.ExpedientSeguidorService;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.war.command.ExpedientFiltreCommand;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;

/**
 * Controlador per als seguidors dels expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/expedient")
public class ExpedientSeguidorController extends BaseUserController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "ExpedientUserController.session.filtre";
	private static final String SESSION_ATTRIBUTE_SELECCIO = "ExpedientUserController.session.seleccio";
	private static final String COOKIE_MEUS_EXPEDIENTS = "meus_expedients";
	
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
	
	@RequestMapping(value = "/follow", method = RequestMethod.GET)
	public String followMultiple(
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		ExpedientFiltreCommand command = getFiltreCommand(request);
		if (seleccio == null || seleccio.isEmpty() || command == null) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"expedient.controller.exportacio.seleccio.buida"));
			return ajaxUrlOk();
		} else {
			for (Long expedientId : seleccio) {
				this.follow(request, expedientId, null, model);
			}
			return ajaxUrlOk();
		}
	}
	
	@RequestMapping(value = "/{expedientId}/unfollow", method = RequestMethod.GET)
	public String unfollow(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@RequestParam(required = false) String contingutId,
			Model model) {
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
	
	@RequestMapping(value = "/unfollow", method = RequestMethod.GET)
	public String unfollowMultiple(
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		ExpedientFiltreCommand command = getFiltreCommand(request);
		if (seleccio == null || seleccio.isEmpty() || command == null) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"expedient.controller.exportacio.seleccio.buida"));
			return ajaxUrlOk();
		} else {
			for (Long expedientId : seleccio) {
				this.unfollow(request, expedientId, null, model);
			}
			return ajaxUrlOk();
		}
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
	
	private ExpedientFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		ExpedientFiltreCommand filtreCommand = (ExpedientFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new ExpedientFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
			
			Date now = new Date();
			Calendar c = Calendar.getInstance(); 
			c.setTime(now); 
			c.add(Calendar.MONTH, -3);
	        c.set(Calendar.HOUR, 0);
	        c.set(Calendar.MINUTE, 0);
	        c.set(Calendar.SECOND, 0);
			filtreCommand.setDataCreacioInici(c.getTime());
			filtreCommand.setExpedientEstatId(Long.valueOf(0));
		}
		Cookie cookie = WebUtils.getCookie(request, COOKIE_MEUS_EXPEDIENTS);
		filtreCommand.setMeusExpedients(cookie != null && "true".equals(cookie.getValue()));

		return filtreCommand;
	}
}
