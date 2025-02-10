package es.caib.ripea.war.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.ExpedientPeticioService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.command.PermisCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.EntitatHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RolHelper;

/**
 * Controlador per al manteniment de permisos de meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/metaExpedient")
public class MetaExpedientPermisController extends BaseAdminController {

	private static final String REQUEST_PARAMETER_STATISTICS_EXPEDIENTS = "ExpedientHelper.teAccesEstadistiques";
	
	@Autowired private MetaExpedientService metaExpedientService;
	@Autowired private AplicacioService aplicacioService;
	@Autowired private ExpedientPeticioService expedientPeticioService;

	@RequestMapping(value = "/{metaExpedientId}/permis", method = RequestMethod.GET)
	public String permis(
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
				metaExpedientService.findById(
						entitatActual.getId(),
						metaExpedientId));
		if (RolHelper.isRolActualDissenyadorOrgan(request))
            throw new SecurityException("No te permisos per accedir com a dissenyador d'organ.");
		if (RolHelper.isRolActualAdministradorOrgan(request) && !Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.procediment.gestio.permis.administrador.organ"))) {
			throw new SecurityException("Per poder gestionar permisos la propietat \"es.caib.ripea.procediment.gestio.permis.administrador.organ\" ha de ser activada pel superusuari ", null);
			
		}
		if (metaExpedient != null // es tracta d'una modificació
				&& RolHelper.isRolActualAdministradorOrgan(request) && metaExpedientService.isRevisioActiva() 
				&& metaExpedient.getRevisioEstat() == MetaExpedientRevisioEstatEnumDto.REVISAT) {
			MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.adminOrgan.bloquejada.alerta"));
			model.addAttribute("bloquejarCamps", true);
		}

		return "metaExpedientPermis";
	}
	@RequestMapping(value = "/{metaExpedientId}/permis/datatable", method = RequestMethod.GET)
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
		return DatatablesHelper.getDatatableResponse(
				request,
				metaExpedientService.permisFind(
						entitatActual.getId(),
						metaExpedientId),
				"id");
	}

	@RequestMapping(value = "/{metaExpedientId}/permis/new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		return get(request, metaExpedientId, null, model);
	}
	@RequestMapping(value = "/{metaExpedientId}/permis/{permisId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long permisId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		comprovarAccesMetaExpedient(request, metaExpedientId);
		model.addAttribute(
				"metaExpedient",
				metaExpedientService.findById(
						entitatActual.getId(),
						metaExpedientId));
		PermisDto permis = findPermisById(entitatActual, metaExpedientId, permisId);

		if (permis != null)
			model.addAttribute("permisCommand", PermisCommand.asCommand(permis));
		else
			model.addAttribute("permisCommand", new PermisCommand());

		return "metaExpedientPermisForm";
	}

	private PermisDto findPermisById(EntitatDto entitatActual, Long metaExpedientId, Long permisId) {
		if (permisId != null) {
			List<PermisDto> permisos = metaExpedientService.permisFind(
					entitatActual.getId(),
					metaExpedientId);
			for (PermisDto p: permisos) {
				if (p.getId().equals(permisId)) {
					return p;
				}
			}
		}
		return null;
	}

	@RequestMapping(value = "/{metaExpedientId}/permis", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@Valid PermisCommand command,
			BindingResult bindingResult,
			Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		String rolActual = RolHelper.getRolActual(request);
		boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaExpedientId);
		
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		
		comprovarAccesMetaExpedient(request, metaExpedientId);
		if (bindingResult.hasErrors()) {
			model.addAttribute(
					"metaExpedient",
					metaExpedientService.findById(
							entitatActual.getId(),
							metaExpedientId));
			return "metaExpedientPermisForm";
		}
		metaExpedientService.permisUpdate(
				entitatActual.getId(),
				metaExpedientId,
				PermisCommand.asDto(command), 
				rolActual, organActual != null ? organActual.getId() : null);
		request.setAttribute(REQUEST_PARAMETER_STATISTICS_EXPEDIENTS, null);
		if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
			MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
		}
		
		expedientPeticioService.evictCountAnotacionsPendents(entitatActual.getId());
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../metaExpedient/" + metaExpedientId + "/permis",
				"metaexpedient.controller.permis.modificat.ok",
				new Object[] { command.getPrincipalTipus()+ " "+command.getPrincipalNom() });
	}

	@RequestMapping(value = "/{metaExpedientId}/permis/{permisId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long permisId,
			@RequestParam(required = false) Long organGestorId,
			Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		String rolActual = RolHelper.getRolActual(request);
		boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaExpedientId);
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		comprovarAccesMetaExpedient(request, metaExpedientId);
		PermisDto permisDto = findPermisById(entitatActual, metaExpedientId, permisId);
		metaExpedientService.permisDelete(
				entitatActual.getId(),
				metaExpedientId,
				permisId,
				organGestorId, 
				rolActual, organActual != null ? organActual.getId() : null);
		
		if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
			MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
		}
		
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../../../metaExpedient/" + metaExpedientId + "/permis",
				"metaexpedient.controller.permis.esborrat.ok",
				new Object[] { permisDto.getPrincipalTipus()+ " "+permisDto.getPrincipalNom() });
	}
}