/**
 * 
 */
package es.caib.ripea.war.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.TipusDestiEnumDto;
import es.caib.ripea.core.api.dto.TipusRegistreEnumDto;
import es.caib.ripea.core.api.exception.ContingutNotUniqueException;
import es.caib.ripea.core.api.service.ImportacioService;
import es.caib.ripea.war.command.ImportacioCommand;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.ExceptionHelper;
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
		command.setDestiTipus(TipusDestiEnumDto.CARPETA_ACTUAL);
		emplenarModelImportacio(contingutId, command, model);
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
			emplenarModelImportacio(contingutId, command, model);
			return "contingutImportacioForm";
		}
		int documentsRepetits = 0;
		try {
			documentsRepetits = importacioService.getDocuments(
						entitatActual.getId(), 
						contingutId,
						ImportacioCommand.asDto(command));
		} catch (Exception ex) {
			emplenarModelImportacio(contingutId, command, model);
			
			if (ExceptionHelper.isExceptionOrCauseInstanceOf(ex, ContingutNotUniqueException.class)) {
				MissatgesHelper.error(
						request, 
						getMessage(
								request, 
								"NomCarpetaNoRepetit"));
			} else {
				MissatgesHelper.error(
						request, 
						getMessage(
								request, 
								"document.controller.importat.ko"));
			}
			return "contingutImportacioForm";
		}
		if (documentsRepetits > 0) {
			addWarningDocumentExists(request);
			return modalUrlTancar();
		}
		
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../../contingut/" + contingutId,
				"document.controller.importat.ok");
		
	}
	
	private void emplenarModelImportacio(
			Long contingutId,
			ImportacioCommand command, 
			Model model) {
		command.setPareId(contingutId);
		model.addAttribute(command);
		model.addAttribute(
			"tipusRegistreOptions",
			EnumHelper.getOptionsForEnum(
					TipusRegistreEnumDto.class,
					"contingut.importacio.tipus.enum."));
		model.addAttribute(
				"tipusDestiOptions",
				EnumHelper.getOptionsForEnum(
						TipusDestiEnumDto.class,
						"contingut.importacio.desti.enum."));
	}

	private void addWarningDocumentExists(HttpServletRequest request) {
		List<DocumentDto> documentsAlreadyImported = importacioService.consultaExpedientsAmbImportacio();
		if (documentsAlreadyImported != null && !documentsAlreadyImported.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append("<ul>");
			for (DocumentDto documentAlreadyImported: documentsAlreadyImported) {
				List<ContingutDto> path = documentAlreadyImported.getPath();
				if (path != null) {
					sb.append("<li>");
					int idx = 0;
					for (ContingutDto pathElement: path) {
						sb.append("<b>/</b>" + pathElement.getNom());
						if (idx == path.size() - 1)
							sb.append("<b>/</b>" + documentAlreadyImported.getNom());
						idx++;
					}
					sb.append("</li>");
				}
			}
			sb.append("</ul>");
			MissatgesHelper.warning(
					request, 
					getMessage(
						request, 
						"expedient.peticio.controller.acceptat.duplicat.warning",
						new Object[] {sb.toString()}));
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
