/**
 * 
 */
package es.caib.ripea.war.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.GrupDto;
import es.caib.ripea.core.api.service.GrupService;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.war.command.GrupCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.EntitatHelper;
import es.caib.ripea.war.helper.RolHelper;


@Controller
@RequestMapping("/grup")
public class GrupController extends BaseAdminController {

	@Autowired
	private GrupService grupService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		getEntitatActualComprovantPermisAdminEntitatOrgan(request);
		return "grupList";
	}
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrgan(request);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				grupService.findByEntitatPaginat(
						entitatActual.getId(),
						null,
						DatatablesHelper.getPaginacioDtoFromRequest(request), 
						RolHelper.isRolActualAdministradorOrgan(request) ? EntitatHelper.getOrganGestorActualId(request) : null),
				"id");
		return dtr;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			Model model) {
		return get(request, null, model);
	}
	
	@RequestMapping(value = "/{grupId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long grupId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrgan(request);
		GrupDto grup = null;
		if (grupId != null)
			grup = grupService.findById(
					grupId);
		GrupCommand command = null;
		if (grup != null) {
			command = GrupCommand.asCommand(grup);
			command.setEntitatId(entitatActual.getId());
		} else {
			command = new GrupCommand();
			command.setEntitatId(entitatActual.getId());
		}
		model.addAttribute(command);
		model.addAttribute("esAdminOrgan", RolHelper.isRolActualAdministradorOrgan(request));
		return "grupForm";
	}
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid GrupCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrgan(request);
		
		if (Utils.isNotEmpty(command.getCodi()) && grupService.checkIfAlreadyExistsWithCodi(entitatActual.getId(), command.getCodi(), command.getId())) {
			bindingResult.rejectValue("codi", "GrupCodiRepetit");
		}
		if (command.getOrganGestorId() == null && RolHelper.isRolActualAdministradorOrgan(request)) {
			bindingResult.rejectValue("organGestorId", "NotNull");
		}
		
		if (bindingResult.hasErrors()) {
			return "grupForm";
		}
		if (command.getId() != null) {
			grupService.update(
					entitatActual.getId(), 
					GrupCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../grup",
					"grup.controller.modificat.ok");
		} else {
			grupService.create(
					entitatActual.getId(), 
					GrupCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../grup",
					"grup.controller.creat.ok");
		}
	}
	
	@RequestMapping(value = "/{grupId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long grupId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrgan(request);
		try {
			grupService.delete(
					entitatActual.getId(),
					grupId);
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../../grup",
					"grup.controller.esborrat.ok");
		} catch (Exception e) {
			return getAjaxControllerReturnValueErrorMessage(
					request,
					"redirect:../../grup",
					ExceptionUtils.getRootCause(e).getMessage(),
					e);
		}
	}
	


}
