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

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.service.ImportacioService;
import es.caib.ripea.war.command.ContenidorCommand.Create;
import es.caib.ripea.war.command.ContenidorCommand.Update;
import es.caib.ripea.war.command.ImportacioCommand;

/**
 * Controlador per al manteniment d'importació de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/contingut")
public class ContingutImportacioController extends BaseUserController {

	@Autowired
	private ImportacioService importacioService;


	@RequestMapping(value = "/{contingutId}/importacio/new", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		return get(request, contingutId, null, model);
	}
	
	public String get(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@PathVariable Long carpetaId,
			Model model) {
		ImportacioCommand command = new ImportacioCommand();
		
		command.setPareId(contingutId);
		model.addAttribute(command);
		return "contingutImportacioForm";
	}

	@RequestMapping(value = "/{contingutId}/importacio/new", method = RequestMethod.POST)
	public String postNew(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@Validated({Create.class}) ImportacioCommand command,
			BindingResult bindingResult,
			Model model) {
		return postUpdate(
				request,
				contingutId,
				command,
				bindingResult,
				model);
	}
	
	public String postUpdate(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@Validated({Update.class}) ImportacioCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		if (bindingResult.hasErrors()) {
			return "contingutImportacioForm";
		}
		importacioService.getDocuments(
				entitatActual.getId(), 
				contingutId,
				command.getNumeroRegistre());
		
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../../contingut/" + contingutId,
				"document.controller.importat.ok");
		
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
