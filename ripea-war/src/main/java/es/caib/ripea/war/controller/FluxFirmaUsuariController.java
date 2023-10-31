/**
 * 
 */
package es.caib.ripea.war.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.FluxFirmaUsuariDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.FluxFirmaUsuariService;
import es.caib.ripea.core.api.service.PortafirmesFluxService;
import es.caib.ripea.war.command.FluxFirmaUsuariCommand;
import es.caib.ripea.war.command.FluxFirmaUsuariFiltreCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;

/**
 * Controlador per al manteniment de fluxos de firma d'usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/fluxusuari")
public class FluxFirmaUsuariController extends BaseAdminController {
	
	private static final String SESSION_ATTRIBUTE_FILTRE = "FluxFirmaUsuariController.session.filtre";
	
	@Autowired
	private FluxFirmaUsuariService fluxFirmaUsuariService;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private PortafirmesFluxService portafirmesFluxService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String get() {
		return "fluxFirmaUsuariList";
	}
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		PaginaDto<FluxFirmaUsuariDto> fluxos = new PaginaDto<FluxFirmaUsuariDto>();
		
        try {
        	EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			
			FluxFirmaUsuariFiltreCommand filtreCommand = getFiltreCommand(request);
			
			fluxos = fluxFirmaUsuariService.findByEntitatAndUsuariPaginat(
					entitatActual.getId(),
					filtreCommand.asDto(),
					DatatablesHelper.getPaginacioDtoFromRequest(request));
	    } catch (SecurityException e) {
	    	logger.error("Error al obtenir el llistat de permisos", e);
	        MissatgesHelper.error(request, getMessage(request, e.getMessage()), e);
	    }
        
		return DatatablesHelper.getDatatableResponse(request, fluxos, "id");
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(HttpServletRequest request, Model model) {
		return get(request, null, model);
	}
	
	@RequestMapping(value = "/{fluxFirmaUsuariId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long fluxFirmaUsuariId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		FluxFirmaUsuariDto fluxFirmaUsuari = null;
		if (fluxFirmaUsuariId != null)
			fluxFirmaUsuari = fluxFirmaUsuariService.findById(entitatActual.getId(), fluxFirmaUsuariId);
		if (fluxFirmaUsuari != null) {
			model.addAttribute(FluxFirmaUsuariCommand.asCommand(fluxFirmaUsuari));

			String urlReturn = aplicacioService.propertyBaseUrl() + "/metaExpedient/metaDocument/flux/returnurl/";
			String urlEdicio = portafirmesFluxService.recuperarUrlEdicioPlantilla(fluxFirmaUsuari.getPortafirmesFluxId(), urlReturn);
			
			model.addAttribute("urlEdicio", urlEdicio);
			
		} else {
			model.addAttribute(new FluxFirmaUsuariCommand());
		}
		return "fluxFirmaUsuariForm";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid FluxFirmaUsuariCommand command,
			BindingResult bindingResult) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		if (bindingResult.hasErrors()) {
			return "fluxFirmaUsuariForm";
		}
		if (command.getId() != null) {
			fluxFirmaUsuariService.update(
					entitatActual.getId(), 
					FluxFirmaUsuariCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:fluxusuari",
					"flux.firma.usuari.controller.modificat.ok");
		} else {
			fluxFirmaUsuariService.create(
					entitatActual.getId(),
					FluxFirmaUsuariCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:fluxusuari",
					"flux.firma.usuari.controller.creat.ok");
		}
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public List<FluxFirmaUsuariDto> list(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return fluxFirmaUsuariService.findByEntitatAndUsuari(entitatActual.getId());
	}
	
	@RequestMapping(value = "/{fluxFirmaUsuariId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long fluxFirmaUsuariId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		fluxFirmaUsuariService.delete(
				entitatActual.getId(), 
				fluxFirmaUsuariId);
		
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../fluxusuari",
				"flux.firma.usuari.controller.esborrat.ok");
	}
	
	private FluxFirmaUsuariFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		FluxFirmaUsuariFiltreCommand filtreCommand = (FluxFirmaUsuariFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new FluxFirmaUsuariFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(FluxFirmaUsuariController.class);
	
}