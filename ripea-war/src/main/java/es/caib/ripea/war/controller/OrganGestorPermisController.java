/**
 * 
 */
package es.caib.ripea.war.controller;

import java.util.ArrayList;
import java.util.List;

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
import es.caib.ripea.core.api.dto.PermisOrganGestorDto;
import es.caib.ripea.core.api.service.OrganGestorService;
import es.caib.ripea.war.command.OrganGestorFiltreCommand;
import es.caib.ripea.war.command.PermisOrganGestorCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;

/**
 * Controlador per al manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/organgestor/{organId}/permis")
public class OrganGestorPermisController extends BaseUserController {

	private final static String ORGANS_FILTRE = "organs_filtre";

	@Autowired
	private OrganGestorService organGestorService;

	@RequestMapping(method = RequestMethod.GET)
	public String permisos(
			HttpServletRequest request,
			@PathVariable Long organId,
			OrganGestorFiltreCommand command,
			Model model) {

		RequestSessionHelper.actualitzarObjecteSessio(request, ORGANS_FILTRE, command);

		model.addAttribute("organ", organGestorService.findItem(organId));
		return "organGestorPermis";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse permisosOrganDatatable(HttpServletRequest request, @PathVariable Long organId) {
		List<PermisOrganGestorDto> permisos = new ArrayList<PermisOrganGestorDto>();
		try {
			EntitatDto entitat = getEntitatActualComprovantPermisos(request);
			permisos = organGestorService.findPermisos(entitat.getId(), organId);
		} catch (Exception e) {
			MissatgesHelper.error(request, getMessage(request, "notificacio.controller.entitat.cap.assignada"));
		}
		return DatatablesHelper.getDatatableResponse(request, permisos, "id");
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(HttpServletRequest request, @PathVariable Long organId, Model model) {
		return get(request, organId, null, model);
	}

	@RequestMapping(value = "/{permisId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long organId,
			@PathVariable Long permisId,
			Model model) {
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		PermisOrganGestorDto permis = null;
		if (permisId != null) {
			List<PermisOrganGestorDto> permisos = organGestorService.findPermisos(entitat.getId(), organId);
			for (PermisOrganGestorDto p : permisos) {
				if (p.getId().equals(permisId)) {
					permis = p;
					break;
				}
			}
		}
		model.addAttribute("organsGestors", organGestorService.findByEntitat(entitat.getId()));
		PermisOrganGestorCommand command;
		if (permis != null)
			command = PermisOrganGestorCommand.asCommand(permis);
		else
			command = new PermisOrganGestorCommand(organId);
		model.addAttribute("permisOrganGestorCommand", command);
		return "organGestorPermisForm";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@PathVariable Long organId,
			@Valid PermisOrganGestorCommand command,
			BindingResult bindingResult,
			Model model) {
		command.setOrganGestorId(organId);
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			model.addAttribute("organsGestors", organGestorService.findByEntitat(entitat.getId()));
			return "organGestorPermisForm";
		}

		organGestorService.updatePermis(
				command.getOrganGestorId(),
				PermisOrganGestorCommand.asDto(command),
				entitat.getId());
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:permis",
				"organgestor.controller.permis.modificat.ok");
	}

	@RequestMapping(value = "{permisId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, @PathVariable Long organId, @PathVariable Long permisId, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		organGestorService.deletePermis(organId, permisId, entitatActual.getId());
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../permis",
				"entitat.controller.permis.esborrat.ok");
	}

}
