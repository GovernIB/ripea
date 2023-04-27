/**
 * 
 */
package es.caib.ripea.war.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.google.common.base.Strings;
import es.caib.ripea.core.api.dto.config.ConfigDto;
import es.caib.ripea.core.api.dto.config.ConfigGroupDto;
import es.caib.ripea.core.api.service.ConfigService;
import es.caib.ripea.war.command.ConfigCommand;
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
import es.caib.ripea.war.helper.ExceptionHelper;

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
	@Autowired
	private ConfigService configService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(Model model, HttpServletRequest request) {

		return "entitatList";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request, Model model) {
		return DatatablesHelper.getDatatableResponse(request, entitatService.findPaginat(DatatablesHelper.getPaginacioDtoFromRequest(request)));
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(Model model) {
		return get(null, model);
	}

	@RequestMapping(value = "/{entitatId}", method = RequestMethod.GET)
	public String get(
			@PathVariable Long entitatId,
			Model model) {

		if (entitatId != null) {
			model.addAttribute(EntitatCommand.asCommand(entitatService.findById(entitatId)));
		} else {
			model.addAttribute(new EntitatCommand());
		}
		return "entitatForm";
	}

	@RequestMapping(value = "/{entitatId}/configurar", method = RequestMethod.GET)
	public String configEntitat(HttpServletRequest request, @PathVariable Long entitatId, Model model) {

		List<ConfigGroupDto> configGroups = configService.findAll();
		EntitatDto entitat = entitatService.findById(entitatId);
		model.addAttribute("entitatNom", entitat.getNom());
		if (entitat == null || Strings.isNullOrEmpty(entitat.getCodi())) {
			return "configEntitat";
		}
		List<ConfigGroupDto> groups = new ArrayList<>();
		int size;
		for (ConfigGroupDto cGroup: configGroups) {
			size = fillFormsModel(cGroup, model, entitat.getCodi());
			if (size == 0) {
				continue;
			}
			groups.add(cGroup);
		}

		model.addAttribute("config_groups", groups);
		return "configEntitat";
	}

	private int fillFormsModel(ConfigGroupDto cGroup, Model model, String entiatCodi){

		List<ConfigDto> confs = new ArrayList<>();
		for (ConfigDto config: cGroup.getConfigs()) {
			if (Strings.isNullOrEmpty(config.getEntitatCodi()) || !config.getEntitatCodi().equals(entiatCodi)) {
				continue;
			}
			model.addAttribute("config_" + config.getKey().replace('.', '_'), ConfigCommand.builder().key(config.getKey()).value(config.getValue()).build());
			confs.add(config);
		}

		cGroup.setConfigs(confs);
		if (cGroup.getInnerConfigs() == null || cGroup.getInnerConfigs().isEmpty()){
			return confs.size();
		}
		int innerSize = 0;
		int size = confs.size();
		for (ConfigGroupDto child : cGroup.getInnerConfigs()){
			innerSize = fillFormsModel(child, model, entiatCodi);
			size = innerSize > size ? innerSize : size;
		}
		return size;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String save(HttpServletRequest request, @Valid EntitatCommand command, BindingResult bindingResult) throws NotFoundException, IOException {

		if (bindingResult.hasErrors()) {
			
			if (bindingResult.getAllErrors().size() == 1 && bindingResult.getAllErrors().get(0).getDefaultMessage().contains("Failed to convert property value of type 'java.lang.String' to required type 'org.springframework.web.multipart.MultipartFile'")) {

//				When selected file is cleared in the view [Netejar] (in this case it is made using Jasny bootstrap implementation with attribute data-dismiss="fileinput") then on form submit this cleared file is passed as an empty string.
//				Spring MVC doesn't allow file to be passed as an empty string for which it gives error in bindingResult: 
//					Failed to convert property value of type 'java.lang.String' to required type 'org.springframework.web.multipart.MultipartFile' for property 'logoImg'; nested exception is java.lang.IllegalStateException: 
//					Cannot convert value of type [java.lang.String] to required type [org.springframework.web.multipart.MultipartFile] for property 'logoImg': no matching editors or conversion strategy found
//		        Because it is not possible to remove error from bindingResult we supress it.
//				In the case of multiple validation errors we cannot supress it in order to show other legitimate validation errors, that's why we use property doNotShowErrors of inputFile.tag to not show errors on this field in the view
             
			} else {
				return "entitatForm";
			}
		}
		if (command.getId() != null) {
			entitatService.update(EntitatCommand.asDto(command));
			entitatService.evictEntitatsAccessiblesUsuari();
			request.getSession().setAttribute("EntitatHelper.entitatActual", null);
			return getModalControllerReturnValueSuccess(request, "redirect:entitat", "entitat.controller.modificada.ok");
		} else {
			entitatService.create(EntitatCommand.asDto(command));
			return getModalControllerReturnValueSuccess(request, "redirect:entitat", "entitat.controller.creada.ok");
		}
	}
	
	@RequestMapping(value = "/getEntitatLogo", method = RequestMethod.GET)
	public String getEntitatLogo(HttpServletRequest request, HttpServletResponse response) throws IOException {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		// If there is logo defined for entitat (in database) return it, if not return logo defined for application (in properties file)
		byte [] logo = entitatActual.getLogoImgBytes() != null ? entitatActual.getLogoImgBytes() : entitatService.getLogo();
		try {
			writeFileToResponse(null, logo, response);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return null;
	}

	@RequestMapping(value = "/{entitatId}/enable", method = RequestMethod.GET)
	public String enable(HttpServletRequest request, @PathVariable Long entitatId) {

		entitatService.updateActiva(entitatId, true);
		return getAjaxControllerReturnValueSuccess(request, "redirect:../../entitat", "entitat.controller.activada.ok");
	}
	@RequestMapping(value = "/{entitatId}/disable", method = RequestMethod.GET)
	public String disable(HttpServletRequest request, @PathVariable Long entitatId) {

		entitatService.updateActiva(entitatId, false);
		return getAjaxControllerReturnValueSuccess(request, "redirect:../../entitat", "entitat.controller.desactivada.ok");
	}

	@RequestMapping(value = "/{entitatId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long entitatId) {

		try {
			entitatService.delete(entitatId);
			return getAjaxControllerReturnValueSuccess(request,
					"redirect:../../entitat",
					"entitat.controller.esborrada.ok");
			
		} catch (Exception e) {
			return getAjaxControllerReturnValueError(
					request,
					"redirect:../../esborrat",
					"entitat.controller.esborrada.error",
					new Object[] { ExceptionHelper.getRootCauseOrItself(e).getMessage() },
					e);
		}
	}

}
