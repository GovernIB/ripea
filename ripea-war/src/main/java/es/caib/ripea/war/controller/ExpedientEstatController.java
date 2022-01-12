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
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.ExpedientEstatService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.command.ExpedientEstatCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.EntitatHelper;
import es.caib.ripea.war.helper.ExceptionHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RolHelper;

/**
 * Controlador per al llistat d'expedients dels usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/expedientEstat")
public class ExpedientEstatController extends BaseAdminController {

	@Autowired
	private MetaExpedientService metaExpedientService;
	@Autowired
	private ExpedientEstatService expedientEstatService;
	
	@RequestMapping(value = "/{metaExpedientId}", method = RequestMethod.GET)
	public String getList(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);

		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		model.addAttribute(
				"esRevisor",
				rolActual.equals("IPA_REVISIO"));
		MetaExpedientDto metaExpedient = comprovarAccesMetaExpedient(request, metaExpedientId);
		model.addAttribute(
				"metaExpedient",
				metaExpedientService.findById(
						entitatActual.getId(),
						metaExpedientId));
		if (metaExpedient != null // es tracta d'una modificació
				&& RolHelper.isRolActualAdministradorOrgan(request) && metaExpedientService.isRevisioActiva() 
				&& metaExpedient.getRevisioEstat() == MetaExpedientRevisioEstatEnumDto.REVISAT) {
			MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.adminOrgan.bloquejada.alerta"));
			model.addAttribute("bloquejarCamps", true);
		}

		return "expedientEstatList";
		
	}

	@RequestMapping(value = "/{metaExpedientId}/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				expedientEstatService.findExpedientEstatByMetaExpedientPaginat(
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
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		ExpedientEstatDto estat = null;
		if (estatId != null) {
			estat = expedientEstatService.findExpedientEstatById(
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
		command.setComu(metaExpedientService.findById(entitatActual.getId(), metaExpedientId).isComu());
		model.addAttribute(command);
		model.addAttribute("metaExpedientId", metaExpedientId);
		MetaExpedientDto metaExpedient = comprovarAccesMetaExpedient(request, metaExpedientId);
		if (metaExpedient != null // es tracta d'una modificació
				&& RolHelper.isRolActualAdministradorOrgan(request) && metaExpedientService.isRevisioActiva() 
				&& metaExpedient.getRevisioEstat() == MetaExpedientRevisioEstatEnumDto.REVISAT) {
			MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.adminOrgan.bloquejada.alerta"));
			model.addAttribute("bloquejarCamps", true);
		}

		return "expedientEstatForm";
	}

	@RequestMapping(value = "/{metaExpedientId}/save", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@Valid ExpedientEstatCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaExpedientId);
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		if (bindingResult.hasErrors()) {

			return "expedientEstatForm";
		}
		if (command.getId() != null) {
			expedientEstatService.updateExpedientEstat(
					entitatActual.getId(),
					ExpedientEstatCommand.asDto(command), 
					rolActual, organActual != null ? organActual.getId() : null);
			
			if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
				MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
			}
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:expedientEstat/" + command.getMetaExpedientId(),
					"expedient.estat.controller.modificat.ok");
		} else {
			expedientEstatService.createExpedientEstat(
					entitatActual.getId(),
					ExpedientEstatCommand.asDto(command), 
					rolActual, organActual != null ? organActual.getId() : null);
			
			if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
				MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
			}
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:expedientEstat/" + command.getMetaExpedientId(),
					"expedient.estat.controller.creat.ok");
		}
	}

	@RequestMapping(value = "/{metaExpedientId}/{expedientEstatId}/move/{posicio}", method = RequestMethod.GET)
	public String move(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long expedientEstatId,
			@PathVariable int posicio) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		
		expedientEstatService.moveTo(
				entitatActual.getId(),
				metaExpedientId,
				expedientEstatId,
				posicio, 
				rolActual);
		

		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:expedientEstat",
				null);
	}

	@RequestMapping(value = "/{metaExpedientId}/{expedientEstatId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long expedientEstatId) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaExpedientId);
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		try {
			expedientEstatService.deleteExpedientEstat(
					entitatActual.getId(),
					expedientEstatId, 
					rolActual, organActual != null ? organActual.getId() : null);
		}catch (Exception e) {
			if (ExceptionHelper.isExceptionOrCauseInstanceOf(e, ValidationException.class))
				return getAjaxControllerReturnValueError(request, "redirect:expedientEstat", 
						"expedient.estat.controller.esborrat.error.restriccio");
		}
		
		if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
			MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
		}
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:expedientEstat",
				"expedient.estat.controller.esborrat.ok");
	}

}
