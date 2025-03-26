package es.caib.ripea.back.controller;

import es.caib.ripea.back.command.RelacionarGrupCommand;
import es.caib.ripea.back.helper.DatatablesHelper;
import es.caib.ripea.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.back.helper.EntitatHelper;
import es.caib.ripea.back.helper.MissatgesHelper;
import es.caib.ripea.back.helper.RolHelper;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.service.GrupService;
import es.caib.ripea.service.intf.service.MetaExpedientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Controlador pel llistat de grups del meta-expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/metaExpedient")
public class MetaExpedientGrupController extends BaseAdminController {

	@Autowired private MetaExpedientService metaExpedientService;
	@Autowired private GrupService grupService;
	
	@RequestMapping(value = "/{metaExpedientId}/grup", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		String rolActual = RolHelper.getRolActual(request);
		MetaExpedientDto metaExpedient = null;
		if (!rolActual.equals("IPA_REVISIO")) {
			metaExpedient = comprovarAccesMetaExpedient(request, metaExpedientId);
		}
		model.addAttribute(
				"esRevisor",
				rolActual.equals("IPA_REVISIO"));
		model.addAttribute(
				"metaExpedient",
				metaExpedientService.findByIdAmbElements(
						entitatActual.getId(),
						metaExpedientId, 
						RolHelper.isRolActualAdministradorOrgan(request) ? EntitatHelper.getOrganGestorActualId(request) : null));
		
		if (metaExpedient != null // es tracta d'una modificació
				&& RolHelper.isRolActualAdministradorOrgan(request) && metaExpedientService.isRevisioActiva() 
				&& metaExpedient.getRevisioEstat() == MetaExpedientRevisioEstatEnumDto.REVISAT) {
			MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.adminOrgan.bloquejada.alerta"));
			model.addAttribute("bloquejarCamps", true);
		}

		return "metaExpedientGrupList";
	}

	@RequestMapping(value = "/{metaExpedientId}/grup/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		String rolActual = RolHelper.getRolActual(request);
		if (!rolActual.equals("IPA_REVISIO")) {
			comprovarAccesMetaExpedient(request, metaExpedientId);
		}
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				grupService.findByEntitatPaginat(
						entitatActual.getId(),
						metaExpedientId,
						DatatablesHelper.getPaginacioDtoFromRequest(request), 
						RolHelper.isRolActualAdministradorOrgan(request) ? EntitatHelper.getOrganGestorActualId(request) : null),
				"id");
		return dtr;
	}
	
	@RequestMapping(value = "/checkIfHasGrupPerDefecte/{metaExpedientId}", method = RequestMethod.GET)
	@ResponseBody
	public boolean checkIfHasGrupPerDefecte(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId) {

		return grupService.checkIfHasGrupPerDefecte(metaExpedientId);
	}
	
	@RequestMapping(value = "/{metaExpedientId}/grup/{grupId}/marcarPerDefecte", method = RequestMethod.GET)
	public String marcarPerDefecte(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long grupId) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);

		grupService.marcarPerDefecte(
				entitatActual.getId(),
				metaExpedientId,
				grupId);
		

		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../grup",
				"metaexpedient.controller.grup.marcarPerDefecte.ok");
	}
	
	@RequestMapping(value = "/{metaExpedientId}/grup/{grupId}/esborrarPerDefecte", method = RequestMethod.GET)
	public String esborrarPerDefecte(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long grupId) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);

		grupService.esborrarPerDefecte(
				entitatActual.getId(),
				metaExpedientId,
				grupId);
		

		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../grup",
				"metaexpedient.controller.grup.esborrarPerDefecte.ok");
	}
	
	
	@RequestMapping(value = "/{metaExpedientId}/grup/relacionar", method = RequestMethod.GET)
	public String relacionar(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		
		List<GrupDto> grups = grupService.findGrupsNoRelacionatAmbMetaExpedient(
				entitatActual.getId(),
				metaExpedientId, 
				RolHelper.isRolActualAdministradorOrgan(request) ? EntitatHelper.getOrganGestorActualId(request) : null);
		model.addAttribute("grups", grups);
		model.addAttribute("metaExpedientId", metaExpedientId);

		RelacionarGrupCommand command = new RelacionarGrupCommand();
		model.addAttribute(command);

		return "metaExpedientRelacionarGrupForm";
	}
	
	
	
	@RequestMapping(value = "/{metaExpedientId}/grup/relacionar/save", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@Valid RelacionarGrupCommand command,
			BindingResult bindingResult,
			Model model) {
		
		if (bindingResult.hasErrors()) {
			EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
			List<GrupDto> grups = grupService.findGrupsNoRelacionatAmbMetaExpedient(
					entitatActual.getId(),
					metaExpedientId, 
					RolHelper.isRolActualAdministradorOrgan(request) ? EntitatHelper.getOrganGestorActualId(request) : null);
			model.addAttribute("grups", grups);
			model.addAttribute("metaExpedientId", metaExpedientId);
			return "metaExpedientRelacionarGrupForm";
		}

		comprovarAccesMetaExpedient(request, metaExpedientId);
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		String rolActual = RolHelper.getRolActual(request);
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		
		grupService.relacionarAmbMetaExpedient(
				entitatActual.getId(),
				metaExpedientId,
				command.getGrupId(), 
				rolActual, 
				organActual != null ? organActual.getId() : null, 
				command.isPerDefecte());
		
		boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaExpedientId);
		if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
			MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
		}
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../grup",
				"metaexpedient.controller.grup.relacionat.ok");
	
	}
	
	@RequestMapping(value = "/{metaExpedientId}/grup/{id}/desvincular", method = RequestMethod.GET)
	public String desvincular(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long id) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		String rolActual = RolHelper.getRolActual(request);
		boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaExpedientId);
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		comprovarAccesMetaExpedient(request, metaExpedientId);
		grupService.desvincularAmbMetaExpedient(
				entitatActual.getId(),
				metaExpedientId,
				id, rolActual, organActual != null ? organActual.getId() : null);
		
		if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
			MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
		}
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../grup",
				"metaexpedient.controller.grup.desvinculat.ok");
	}


	

}
