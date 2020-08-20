/**
 * 
 */
package es.caib.ripea.war.controller;

import javax.servlet.http.HttpServletRequest;
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
import es.caib.ripea.core.api.dto.ExpedientEstatDto;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.command.ExpedientEstatCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per al llistat d'expedients dels usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/expedientEstat")
public class ExpedientEstatController extends BaseAdminController {

	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private MetaExpedientService metaExpedientService;
	
	@RequestMapping(value = "/{metaExpedientId}", method = RequestMethod.GET)
	public String getList(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);

		model.addAttribute(
				"metaExpedient",
				metaExpedientService.findById(
						entitatActual.getId(),
						metaExpedientId));
		return "expedientEstatList";
		
	}

	@RequestMapping(value = "/{metaExpedientId}/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				expedientService.findExpedientEstatByMetaExpedientPaginat(
						entitatActual.getId(),
						metaExpedientId,
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id");
		return dtr;
	}

	@RequestMapping(value = "/{metaExpedientId}/new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		return get(
				request,
				metaExpedientId,
				null,
				model);
	}

	@RequestMapping(value = "/{metaExpedientId}/{estatId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long estatId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		ExpedientEstatDto estat = null;
		if (estatId != null) {
			estat = expedientService.findExpedientEstatById(
					entitatActual.getId(),
					estatId);
			model.addAttribute(estat);	
		}
		ExpedientEstatCommand command = null;
		if (estat != null)
			command = ExpedientEstatCommand.asCommand(estat);
		else
			command = new ExpedientEstatCommand();
		command.setMetaExpedientId(metaExpedientId);
		model.addAttribute(command);
		return "expedientEstatForm";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid ExpedientEstatCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		if (bindingResult.hasErrors()) {
//			emplenarModelFormulari(
//					request,
//					model);
			return "expedientEstatForm";
		}
		if (command.getId() != null) {
			expedientService.updateExpedientEstat(
					entitatActual.getId(),
					ExpedientEstatCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:expedientEstat/"+command.getMetaExpedientId(),
					"expedient.estat.controller.modificat.ok");
		} else {
			expedientService.createExpedientEstat(
					entitatActual.getId(),
					ExpedientEstatCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:expedientEstat/"+command.getMetaExpedientId(),
					"expedient.estat.controller.creat.ok");
		}
	}

	@RequestMapping(value = "/{metaExpedientId}/{expedientEstatId}/move/{posicio}", method = RequestMethod.GET)
	public String move(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long expedientEstatId,
			@PathVariable int posicio) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		expedientService.moveTo(
				entitatActual.getId(),
				metaExpedientId,
				expedientEstatId,
				posicio);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:expedientEstat/"+metaExpedientId,
				null);
	}

	@RequestMapping(value = "/{expedientEstatId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long expedientEstatId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		expedientService.deleteExpedientEstat(
				entitatActual.getId(),
				expedientEstatId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:expedientEstat",
				"expedient.estat.controller.esborrat.ok");
	}

}
