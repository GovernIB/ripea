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
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientEstatDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.core.api.dto.MetaExpedientTascaDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.service.ExpedientEstatService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.command.MetaExpedientTascaCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.EntitatHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RolHelper;

/**
 * Controlador pel llistat de tasques del meta-expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/metaExpedient")
public class MetaExpedientTascaController extends BaseAdminController {

	@Autowired
	private MetaExpedientService metaExpedientService;
	@Autowired
	private ExpedientEstatService expedientEstatService;
	
	@RequestMapping(value = "/{metaExpedientId}/tasca", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);

		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		model.addAttribute(
				"esRevisor",
				rolActual.equals("IPA_REVISIO"));
		
		if (!rolActual.equals("IPA_REVISIO")) {
			comprovarAccesMetaExpedient(request, metaExpedientId);
		}
		MetaExpedientDto metaExpedient = metaExpedientService.findByIdAmbElements(
				entitatActual.getId(),
				metaExpedientId, 
				RolHelper.isRolActualAdministradorOrgan(request) ? EntitatHelper.getOrganGestorActualId(request) : null);
		model.addAttribute(
				"metaExpedient",
				metaExpedient);
		
		if (metaExpedient != null && metaExpedientService.isRevisioActiva()) { // es tracta d'una modificació
			if (RolHelper.isRolActualAdministradorOrgan(request)  && metaExpedient.getRevisioEstat() == MetaExpedientRevisioEstatEnumDto.REVISAT){
				MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.adminOrgan.bloquejada.alerta"));
				model.addAttribute("bloquejarCamps", true);
			} else if (RolHelper.isRolActualRevisor(request)){
				model.addAttribute("bloquejarCamps", true);
				model.addAttribute("consultar", true);
				model.addAttribute("isRolActualRevisor", true);
			}
		}
		
		//TODO rename to metaExpedientTascaList
		return "metaExpedientTasca";
	}

	@RequestMapping(value = "/{metaExpedientId}/tasca/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		model.addAttribute(
				"esRevisor",
				rolActual.equals("IPA_REVISIO"));
		
		if (!rolActual.equals("IPA_REVISIO")) {
			comprovarAccesMetaExpedient(request, metaExpedientId);
		}
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				metaExpedientService.tascaFindPaginatByMetaExpedient(
						entitatActual.getId(),
						metaExpedientId,
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id");
		return dtr;
	}

	@RequestMapping(value = "/{metaExpedientId}/tasca/new", method = RequestMethod.GET)
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

	@RequestMapping(value = "/{metaExpedientId}/tasca/{id}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long id,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		MetaExpedientDto metaExpedient = comprovarAccesMetaExpedient(request, metaExpedientId);
		model.addAttribute(
				"metaExpedient",
				metaExpedientService.findById(
						entitatActual.getId(),
						metaExpedientId));
		MetaExpedientTascaDto tasca = null;
		if (id != null) {
			tasca = metaExpedientService.tascaFindById(
					entitatActual.getId(),
					metaExpedientId,
					id);
			model.addAttribute(tasca);
		}
		
		List<ExpedientEstatDto> expedientEstats = expedientEstatService.findExpedientEstatsByMetaExpedient(
				entitatActual.getId(),
				metaExpedientId);
		
		model.addAttribute(
				"expedientEstats",
				expedientEstats);
		MetaExpedientTascaCommand command = null;
		if (tasca != null)
			command = MetaExpedientTascaCommand.asCommand(tasca);
		else
			command = new MetaExpedientTascaCommand();
		
		command.setEntitatId(entitatActual.getId());
		command.setMetaExpedientId(metaExpedientId);
		model.addAttribute(command);
		
		if (metaExpedient != null && metaExpedientService.isRevisioActiva()) { // es tracta d'una modificació
			if (RolHelper.isRolActualAdministradorOrgan(request)  && metaExpedient.getRevisioEstat() == MetaExpedientRevisioEstatEnumDto.REVISAT){
				MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.adminOrgan.bloquejada.alerta"));
				model.addAttribute("bloquejarCamps", true);
			} else if (RolHelper.isRolActualRevisor(request)){
				model.addAttribute("bloquejarCamps", true);
				model.addAttribute("consultar", true);
				model.addAttribute("isRolActualRevisor", true);
			}
		}
		return "metaExpedientTascaForm";
	}
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}

	@RequestMapping(value = "/{metaExpedientId}/tasca/save", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@Valid MetaExpedientTascaCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaExpedientId);
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		comprovarAccesMetaExpedient(request, metaExpedientId);

		if (bindingResult.hasErrors()) {
			model.addAttribute(
					"metaExpedient",
					metaExpedientService.findById(
							entitatActual.getId(),
							metaExpedientId));
			
			return "metaExpedientTascaForm";
		}
		if (command.getId() == null) {
			metaExpedientService.tascaCreate(
					entitatActual.getId(),
					metaExpedientId,
					MetaExpedientTascaCommand.asDto(command), rolActual, organActual != null ? organActual.getId() : null);
			
			if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
				MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
			}
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:expedientEstat/" + metaExpedientId,
					"metaexpedient.controller.tasca.creada.ok",
					new Object[] { command.getNom() });
		} else {
			metaExpedientService.tascaUpdate(
					entitatActual.getId(),
					metaExpedientId,
					MetaExpedientTascaCommand.asDto(command), rolActual, organActual != null ? organActual.getId() : null);
			
			if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
				MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
			}
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:expedientEstat/" + metaExpedientId,
					"metaexpedient.controller.tasca.modificada.ok",
					new Object[] { command.getNom() });
		}
	}

	@RequestMapping(value = "/{metaExpedientId}/tasca/{id}/enable", method = RequestMethod.GET)
	public String enable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long id) {
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaExpedientId);
		
		comprovarAccesMetaExpedient(request, metaExpedientId);
		MetaExpedientTascaDto metaExpedientTascaDto = metaExpedientService.tascaUpdateActiu(
				entitatActual.getId(),
				metaExpedientId,
				id,
				true, rolActual, organActual != null ? organActual.getId() : null);
		

		if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
			MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
		}
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaExpedient",
				"metaexpedient.controller.tasca.activada.ok",
				new Object[] { metaExpedientTascaDto.getNom() });
	}
	@RequestMapping(value = "/{metaExpedientId}/tasca/{id}/disable", method = RequestMethod.GET)
	public String disable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long id) {
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaExpedientId);
		
		comprovarAccesMetaExpedient(request, metaExpedientId);
		MetaExpedientTascaDto metaExpedientTascaDto = metaExpedientService.tascaUpdateActiu(
				entitatActual.getId(),
				metaExpedientId,
				id,
				false, rolActual, organActual != null ? organActual.getId() : null);
		

		if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
			MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
		}
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaExpedient",
				"metaexpedient.controller.tasca.desactivada.ok",
				new Object[] { metaExpedientTascaDto.getNom() });
	}

	@RequestMapping(value = "/{metaExpedientId}/tasca/{id}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long id) {
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaExpedientId);
		
		comprovarAccesMetaExpedient(request, metaExpedientId);
		MetaExpedientTascaDto metaExpedientTascaDto = metaExpedientService.tascaDelete(
				entitatActual.getId(),
				metaExpedientId,
				id, rolActual, organActual != null ? organActual.getId() : null);
		
		
		if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
			MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
		}
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:expedientEstat",
				"metaexpedient.controller.tasca.esborrada.ok",
				new Object[] { metaExpedientTascaDto.getNom() });
	}

}
