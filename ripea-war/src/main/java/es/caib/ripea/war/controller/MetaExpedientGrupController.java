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
import es.caib.ripea.core.api.dto.GrupDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.service.GrupService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.command.RelacionarGrupCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.EntitatHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RolHelper;

/**
 * Controlador pel llistat de grups del meta-expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/metaExpedient")
public class MetaExpedientGrupController extends BaseAdminController {

	@Autowired
	private MetaExpedientService metaExpedientService;
	@Autowired
	private GrupService grupService;
	
	@RequestMapping(value = "/{metaExpedientId}/grup", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
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
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
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

	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
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
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);

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
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
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
