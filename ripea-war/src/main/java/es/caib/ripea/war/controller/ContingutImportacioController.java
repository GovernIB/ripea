/**
 * 
 */
package es.caib.ripea.war.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.TipusRegistreEnumDto;
import es.caib.ripea.core.api.service.ImportacioService;
import es.caib.ripea.war.command.ImportacioCommand;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.MissatgesHelper;

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
		model.addAttribute(
			"tipusRegistreOptions",
			EnumHelper.getOptionsForEnum(
					TipusRegistreEnumDto.class,
					"contingut.importacio.tipus.enum."));
		return "contingutImportacioForm";
	}

	@RequestMapping(value = "/{contingutId}/importacio/new", method = RequestMethod.POST)
	public String postNew(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@Valid ImportacioCommand command,
			BindingResult bindingResult,
			Model model) throws Exception {
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
			@Valid ImportacioCommand command,
			BindingResult bindingResult,
			Model model) throws Exception {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		if (bindingResult.hasErrors()) {
			command.setPareId(contingutId);
			model.addAttribute(command);
			model.addAttribute(
				"tipusRegistreOptions",
				EnumHelper.getOptionsForEnum(
						TipusRegistreEnumDto.class,
						"contingut.importacio.tipus.enum."));
			return "contingutImportacioForm";
		}

		int documentsRepetits = 0;
		
		try {
			documentsRepetits = importacioService.getDocuments(
						entitatActual.getId(), 
						contingutId,
						ImportacioCommand.asDto(command));
		} catch (Exception e) {
			command.setPareId(contingutId);
			model.addAttribute(command);
			model.addAttribute(
				"tipusRegistreOptions",
				EnumHelper.getOptionsForEnum(
						TipusRegistreEnumDto.class,
						"contingut.importacio.tipus.enum."));
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"document.controller.importat.ko"));
			return "contingutImportacioForm";
		}
		if (documentsRepetits > 0) {
			MissatgesHelper.warning(
					request, 
					getMessage(
							request, 
							"document.controller.importacio.repetit",
							new Object[] {documentsRepetits}));
			return modalUrlTancar();
		}
	
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
