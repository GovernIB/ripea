/**
 * 
 */
package es.caib.ripea.war.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.exception.ExisteixenExpedientsEsborratsException;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.core.api.service.OrganGestorService;
import es.caib.ripea.war.command.MetaExpedientCommand;
import es.caib.ripea.war.command.MetaExpedientFiltreCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.EntitatHelper;
import es.caib.ripea.war.helper.ExceptionHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;
import es.caib.ripea.war.helper.RolHelper;

/**
 * Controlador per al manteniment de meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/metaExpedient")
public class MetaExpedientController extends BaseAdminController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "MetaExpedientController.session.filtre";
	
	@Autowired
	private MetaExpedientService metaExpedientService;
	@Autowired
	private OrganGestorService organGestorService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {
		getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrgan(request);
		Boolean mantenirPaginacio = Boolean.parseBoolean(request.getParameter("mantenirPaginacio"));
		if (mantenirPaginacio) {
			model.addAttribute("mantenirPaginacio", true);
		} else {
			model.addAttribute("mantenirPaginacio", false);
		}
		
		MetaExpedientFiltreCommand command = getFiltreCommand(request);
		model.addAttribute(command);
		model.addAttribute("isRolAdminOrgan", RolHelper.isRolActualAdministradorOrgan(request));
		return "metaExpedientList";
	}


	@RequestMapping(value = "/filtrar", method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid MetaExpedientFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model,
			@RequestParam(value = "accio", required = false) String accio) {
		getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrgan(request);
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE);

		} else {
			if (!bindingResult.hasErrors()) {
				RequestSessionHelper.actualitzarObjecteSessio(
						request,
						SESSION_ATTRIBUTE_FILTRE,
						filtreCommand);
			}
		}
		return "redirect:../metaExpedient";
	}
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrgan(request);
		MetaExpedientFiltreCommand filtreCommand = getFiltreCommand(request);
		PaginaDto<MetaExpedientDto> metaExps = null;

		metaExps = metaExpedientService.findByEntitatOrOrganGestor(
				entitatActual.getId(),
				EntitatHelper.getOrganGestorActual(request).getId(),
				filtreCommand.asDto(),
				RolHelper.isRolActualAdministradorOrgan(request),
				DatatablesHelper.getPaginacioDtoFromRequest(request));


		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(request, metaExps, "id");
		return dtr;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(HttpServletRequest request, Model model) {
		return get(request, null, model);
	}

	@RequestMapping(value = "/{metaExpedientId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrgan(request);
		MetaExpedientDto metaExpedient = null;
		if (metaExpedientId != null)
			metaExpedient = comprovarAccesMetaExpedient(request, metaExpedientId);
		MetaExpedientCommand command = null;
		if (metaExpedient != null)
			command = MetaExpedientCommand.asCommand(metaExpedient);
		else
			command = new MetaExpedientCommand();
		command.setRolAdminOrgan(RolHelper.isRolActualAdministradorOrgan(request));
		model.addAttribute(command);
		command.setEntitatId(entitatActual.getId());

		
		
		fillFormModel(
				request,
				metaExpedient,
				model);
		return "metaExpedientForm";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid MetaExpedientCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrgan(request);
		MetaExpedientDto dto = command.asDto();
		if (bindingResult.hasErrors()) {
			fillFormModel(request, dto, model);
			return "metaExpedientForm";
		}
		
				
		if (command.getId() != null) {
			metaExpedientService.update(entitatActual.getId(), dto);
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:metaExpedient",
					"metaexpedient.controller.modificat.ok");
		} else {
			metaExpedientService.create(entitatActual.getId(), dto);
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:metaExpedient",
					"metaexpedient.controller.creat.ok");
		}
	}

	@RequestMapping(value = "/{metaExpedientId}/new", method = RequestMethod.GET)
	public String getNewAmbPare(HttpServletRequest request, @PathVariable Long metaExpedientId, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrgan(request);
		MetaExpedientDto metaExpedient = comprovarAccesMetaExpedient(request, metaExpedientId);
		MetaExpedientCommand command = new MetaExpedientCommand(RolHelper.isRolActualAdministradorOrgan(request));
		command.setPareId(metaExpedientId);
		command.setEntitatId(entitatActual.getId());
		model.addAttribute(command);
		fillFormModel(request, metaExpedient, model);
		if (RolHelper.isRolActualAdministrador(request)) {
			model.addAttribute("organsGestors", organGestorService.findByEntitat(entitatActual.getId()));
		}else {
			model.addAttribute(
					"organsGestors",
					organGestorService.findAccessiblesUsuariActual(
							entitatActual.getId(),
							EntitatHelper.getOrganGestorActual(request).getId()));
		}
		return "metaExpedientForm";
	}

	@RequestMapping(value = "/{metaExpedientId}/enable", method = RequestMethod.GET)
	public String enable(HttpServletRequest request, @PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrgan(request);
		comprovarAccesMetaExpedient(request, metaExpedientId);
		metaExpedientService.updateActiu(
				entitatActual.getId(),
				metaExpedientId,
				true);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaExpedient",
				"metaexpedient.controller.activat.ok");
	}

	@RequestMapping(value = "/{metaExpedientId}/disable", method = RequestMethod.GET)
	public String disable(HttpServletRequest request, @PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrgan(request);
		comprovarAccesMetaExpedient(request, metaExpedientId);
		metaExpedientService.updateActiu(
				entitatActual.getId(),
				metaExpedientId,
				false);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaExpedient",
				"metaexpedient.controller.desactivat.ok");
	}

	@RequestMapping(value = "/{metaExpedientId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, @PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrgan(request);
		comprovarAccesMetaExpedient(request, metaExpedientId);
		try {
			metaExpedientService.delete(
					entitatActual.getId(),
					metaExpedientId);
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../../metaExpedient",
					"metaexpedient.controller.esborrat.ok");
		} catch (Exception ex) {
			if (ExceptionHelper.isExceptionOrCauseInstanceOf(ex, DataIntegrityViolationException.class) ||
					ExceptionHelper.isExceptionOrCauseInstanceOf(ex, ConstraintViolationException.class)) {
				return getAjaxControllerReturnValueError(
						request,
						"redirect:../../esborrat",
						"metaexpedient.controller.esborrar.error.fk");
			} else if (ExceptionHelper.isExceptionOrCauseInstanceOf(ex, ExisteixenExpedientsEsborratsException.class)) {
				return getAjaxControllerReturnValueError(
						request,
						"redirect:../../esborrat",
						"metaexpedient.controller.esborrar.error.fk.esborrats");
			} else {
				throw ex;
			}
		}
	}

	@RequestMapping(value = "/findAll", method = RequestMethod.GET)
	@ResponseBody
	public List<MetaExpedientDto> findAll(HttpServletRequest request, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrgan(request);
		return metaExpedientService.findByEntitat(entitatActual.getId());
	}
	

	private MetaExpedientFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		MetaExpedientFiltreCommand filtreCommand = (MetaExpedientFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new MetaExpedientFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}

		return filtreCommand;
	}
	
	private void fillFormModel(HttpServletRequest request, MetaExpedientDto dto, Model model) {
		model.addAttribute("isRolAdminOrgan", RolHelper.isRolActualAdministradorOrgan(request));
		
		boolean hasOrganGestor = dto != null ? dto.getOrganGestor() != null : false;
		model.addAttribute("hasOrganGestor", hasOrganGestor);
	}

}
