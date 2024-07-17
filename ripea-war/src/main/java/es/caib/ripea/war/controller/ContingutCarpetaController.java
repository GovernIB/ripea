/**
 * 
 */
package es.caib.ripea.war.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.ripea.core.api.dto.CarpetaDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.service.CarpetaService;
import es.caib.ripea.war.command.CarpetaCommand;
import es.caib.ripea.war.command.ContenidorCommand.Create;
import es.caib.ripea.war.command.ContenidorCommand.Update;

/**
 * Controlador per al manteniment de carpetes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/contingut")
public class ContingutCarpetaController extends BaseUserController {

	@Autowired
	private CarpetaService carpetaService;

	@RequestMapping(value = "/{contingutId}/carpeta/new", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		return get(request, contingutId, null, model);
	}
	@RequestMapping(value = "/{contingutId}/carpeta/{carpetaId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@PathVariable Long carpetaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		CarpetaDto carpeta = null;
		if (carpetaId != null) {
			carpeta = carpetaService.findById(
					entitatActual.getId(),
					carpetaId);
		}
		CarpetaCommand command = null;
		if (carpeta != null)
			command = CarpetaCommand.asCommand(carpeta);
		else
			command = new CarpetaCommand();
		command.setEntitatId(entitatActual.getId());
		command.setPareId(contingutId);
		model.addAttribute(command);
		return "contingutCarpetaForm";
	}

	@RequestMapping(value = "/{contingutId}/carpeta/new", method = RequestMethod.POST)
	public String postNew(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@Validated({Create.class}) CarpetaCommand command,
			BindingResult bindingResult,
			Model model) {
		return postUpdate(
				request,
				contingutId,
				command,
				bindingResult,
				model);
	}
	@RequestMapping(value = "/{contingutId}/carpeta/update", method = RequestMethod.POST)
	public String postUpdate(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@Validated({Update.class}) CarpetaCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			return "contingutCarpetaForm";
		}
		if (command.getId() == null) {
			carpetaService.create(
					entitatActual.getId(),
					contingutId,
					command.getNom());
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../../contingut/" + contingutId,
					"carpeta.controller.creada.ok",
					new Object[] { command.getNom() });
		} else {
			carpetaService.update(
					entitatActual.getId(),
					command.getId(),
					command.getNom());
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../../contingut/" + command.getPareId(),
					"carpeta.controller.modificada.ok",
					new Object[] { command.getNom() });
		}
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}

}
