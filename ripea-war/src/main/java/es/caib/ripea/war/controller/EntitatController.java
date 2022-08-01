/**
 * 
 */
package es.caib.ripea.war.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.EntitatService;
import es.caib.ripea.war.command.EntitatCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per al manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/entitat")
public class EntitatController extends BaseUserController {

	@Autowired
	private EntitatService entitatService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(Model model, HttpServletRequest request) {

		return "entitatList";
	}
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			Model model) {
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				entitatService.findPaginat(
						DatatablesHelper.getPaginacioDtoFromRequest(request)));
		return dtr;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(Model model) {
		return get(null, model);
	}
	@RequestMapping(value = "/{entitatId}", method = RequestMethod.GET)
	public String get(
			@PathVariable Long entitatId,
			Model model) {
		EntitatDto entitat = null;
		if (entitatId != null)
			entitat = entitatService.findById(entitatId);
		if (entitat != null)
			model.addAttribute(EntitatCommand.asCommand(entitat));
		else
			model.addAttribute(new EntitatCommand());
		return "entitatForm";
	}
	@RequestMapping(method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid EntitatCommand command,
			BindingResult bindingResult) throws NotFoundException, IOException {
		if (bindingResult.hasErrors()) {
			return "entitatForm";
		}
		if (command.getId() != null) {
			entitatService.update(EntitatCommand.asDto(command));
			entitatService.evictEntitatsAccessiblesUsuari();
			request.getSession().setAttribute(
					"EntitatHelper.entitatActual",
					null);
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:entitat",
					"entitat.controller.modificada.ok");
		} else {
			entitatService.create(EntitatCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:entitat",
					"entitat.controller.creada.ok");
		}
	}
	
	
	@RequestMapping(value = "/getEntitatLogo", method = RequestMethod.GET)
	public String getEntitatLogo(
			HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		// If there is logo defined for entitat (in database) return it, if not return logo defined for application (in properties file)
		if (entitatActual.getLogoImgBytes() != null) {
			writeFileToResponse(
					null,
					entitatActual.getLogoImgBytes(),
					response);
		} else {
			try {
				writeFileToResponse(
						null, 
						entitatService.getLogo(), 
						response);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
		return null;
	}
	

	@RequestMapping(value = "/{entitatId}/enable", method = RequestMethod.GET)
	public String enable(
			HttpServletRequest request,
			@PathVariable Long entitatId) {
		entitatService.updateActiva(entitatId, true);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../entitat",
				"entitat.controller.activada.ok");
	}
	@RequestMapping(value = "/{entitatId}/disable", method = RequestMethod.GET)
	public String disable(
			HttpServletRequest request,
			@PathVariable Long entitatId) {
		entitatService.updateActiva(entitatId, false);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../entitat",
				"entitat.controller.desactivada.ok");
	}

	@RequestMapping(value = "/{entitatId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long entitatId) {
		entitatService.delete(entitatId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../entitat",
				"entitat.controller.esborrada.ok");
	}

}
