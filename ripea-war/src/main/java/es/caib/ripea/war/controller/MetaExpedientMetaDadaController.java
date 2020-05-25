/**
 * 
 */
package es.caib.ripea.war.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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

import es.caib.ripea.core.api.dto.DominiDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.service.DominiService;
import es.caib.ripea.core.api.service.MetaDadaService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.command.MetaDadaCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per al manteniment de les meta-dades dels meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/metaExpedient")
public class MetaExpedientMetaDadaController extends BaseAdminController {

	@Autowired
	private MetaDadaService metaDadaService;
	@Autowired
	private MetaExpedientService metaExpedientService;
	@Autowired
	private DominiService dominiService;

	@RequestMapping(value = "/{metaExpedientId}/metaDada", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"metaExpedient",
				metaExpedientService.findById(
						entitatActual.getId(),
						metaExpedientId));
		return "metaDadaList";
	}
	@RequestMapping(value = "/{metaExpedientId}/metaDada/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				metaDadaService.findByMetaNodePaginat(
						entitatActual.getId(),
						metaExpedientId,
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id");
		return dtr;
	}
	
	
	@RequestMapping(value = "/metaDada/{metaNodeId}/{metaDadaId}/move/{posicio}", method = RequestMethod.GET)
	public String move(
			HttpServletRequest request,
			@PathVariable Long metaNodeId,
			@PathVariable Long metaDadaId,
			@PathVariable int posicio) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		metaDadaService.moveTo(
				entitatActual.getId(),
				metaNodeId,
				metaDadaId,
				posicio);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:metaDada/"+metaNodeId,
				null);
	}

	@RequestMapping(value = "/{metaExpedientId}/metaDada/new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		return get(request, metaExpedientId, null, model);
	}
	@RequestMapping(value = "/{metaExpedientId}/metaDada/{metaDadaId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long metaDadaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		MetaDadaDto metaDada = null;
		if (metaDadaId != null)
			metaDada = metaDadaService.findById(
					entitatActual.getId(),
					metaExpedientId,
					metaDadaId);
		MetaDadaCommand command = null;
		if (metaDada != null) {
			command = MetaDadaCommand.asCommand(metaDada);
		} else {
			command = new MetaDadaCommand();
		}
		command.setEntitatId(entitatActual.getId());
		command.setMetaNodeId(metaExpedientId);
		model.addAttribute(command);
		return "metaDadaForm";
	}
	@RequestMapping(value = "/{metaExpedientId}/metaDada", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@Valid MetaDadaCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			return "metaDadaForm";
		}
		if (command.getId() != null) {
			metaDadaService.update(
					entitatActual.getId(),
					metaExpedientId,
					MetaDadaCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:metaDada",
					"metadada.controller.modificat.ok");
		} else {
			metaDadaService.create(
					entitatActual.getId(),
					metaExpedientId,
					MetaDadaCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:metaDada",
					"metadada.controller.creat.ok");
		}
	}

	@RequestMapping(value = "/{metaExpedientId}/metaDada/{metaDadaId}/enable", method = RequestMethod.GET)
	public String enable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long metaDadaId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		metaDadaService.updateActiva(
				entitatActual.getId(),
				metaExpedientId,
				metaDadaId,
				true);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaDada",
				"metadada.controller.activada.ok");
	}
	@RequestMapping(value = "/{metaExpedientId}/metaDada/{metaDadaId}/disable", method = RequestMethod.GET)
	public String disable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long metaDadaId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		metaDadaService.updateActiva(
				entitatActual.getId(),
				metaExpedientId,
				metaDadaId,
				false);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaDada",
				"metadada.controller.desactivada.ok");
	}

	@RequestMapping(value = "/{metaExpedientId}/metaDada/{metaDadaId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long metaDadaId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		metaDadaService.delete(
				entitatActual.getId(),
				metaExpedientId,
				metaDadaId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaDada",
				"metadada.controller.esborrat.ok");
	}
	
	@RequestMapping(value = "/{metaExpedientId}/metaDada/domini", method = RequestMethod.GET)
	@ResponseBody
	public List<DominiDto> getDominis(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		List<DominiDto> dominis = dominiService.findByEntitat(entitatActual.getId());
		return dominis;
	}
	
	@RequestMapping(value = "/{metaExpedientId}/metaDada/domini/{dominiCodi}", method = RequestMethod.GET)
	@ResponseBody
	public Map<Long, String> getDomini(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable String dominiCodi){
		Map<Long, String> resultatConsulta = new HashMap<Long, String>();
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DominiDto domini = dominiService.findByCodiAndEntitat(dominiCodi,entitatActual.getId());
		
		resultatConsulta.put(1L, "port de palma");
		resultatConsulta.put(2L, "port de tant");
		return resultatConsulta;
	}

}
