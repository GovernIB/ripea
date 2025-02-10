/**
 * 
 */
package es.caib.ripea.back.controller;

import es.caib.ripea.back.command.FluxFirmaUsuariCommand;
import es.caib.ripea.back.command.FluxFirmaUsuariFiltreCommand;
import es.caib.ripea.back.helper.DatatablesHelper;
import es.caib.ripea.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.back.helper.MissatgesHelper;
import es.caib.ripea.back.helper.RequestSessionHelper;
import es.caib.ripea.back.helper.SessioHelper;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.FluxFirmaUsuariService;
import es.caib.ripea.service.intf.service.OrganGestorService;
import es.caib.ripea.service.intf.service.PortafirmesFluxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
	@Autowired
	private OrganGestorService organGestorService;
	
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
		String urlReturn = aplicacioService.propertyBaseUrl() + "/fluxusuari/returnurl/";
		
		if (fluxFirmaUsuariId != null)
			fluxFirmaUsuari = fluxFirmaUsuariService.findById(entitatActual.getId(), fluxFirmaUsuariId);
		if (fluxFirmaUsuari != null) {
			model.addAttribute(FluxFirmaUsuariCommand.asCommand(fluxFirmaUsuari));
			urlReturn += fluxFirmaUsuariId + "/" + fluxFirmaUsuari.getPortafirmesFluxId();
			
			String urlEdicio = portafirmesFluxService.recuperarUrlEdicioPlantilla(fluxFirmaUsuari.getPortafirmesFluxId(), urlReturn);
			
			model.addAttribute("urlEdicio", urlEdicio);
			
		} else {
			model.addAttribute(new FluxFirmaUsuariCommand());
			PortafirmesIniciFluxRespostaDto transaccioResponse = portafirmesFluxService.iniciarFluxFirma(urlReturn, true);
			
			model.addAttribute("urlCreacio", transaccioResponse.getUrlRedireccio());
		}
		return "fluxFirmaUsuariForm";
	}
	
	@RequestMapping(value = "/returnurl/{fluxFirmaUsuariId}/{plantillaId}", method = RequestMethod.GET)
	public String returnRipeaModificacio(
			HttpServletRequest request,
			@PathVariable Long fluxFirmaUsuariId,
			@PathVariable String plantillaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"FluxCreat",
				getMessage(request, "metadocument.form.camp.portafirmes.flux.edicio.enum.FINAL_OK"));
		model.addAttribute("isEdicio", true);
		
		try {
			PortafirmesFluxInfoDto fluxDetall = portafirmesFluxService.recuperarDetallFluxFirma(plantillaId, true);
			
			fluxFirmaUsuariService.update(fluxFirmaUsuariId, entitatActual.getId(), fluxDetall);
		} catch (Exception ex) {
			logger.error("Error actualitzant el flux de firmes", ex);
	        model.addAttribute(
					"FluxError",
					getMessage(request, getMessage(request, ex.getMessage())));
		}
		
		return "portafirmesModalTancar";
	}
	
	@RequestMapping(value = "/returnurl/{transactionId}", method = RequestMethod.GET)
	public String returnRipeaCreacio(HttpServletRequest request, @PathVariable String transactionId, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		PortafirmesFluxRespostaDto resposta = portafirmesFluxService.recuperarFluxFirma(transactionId);
		organGestorService.actualitzarOrganCodi(SessioHelper.getOrganActual(request));

		model.addAttribute("isCreacio", true);
		
		if (resposta.isError()) {
			model.addAttribute(
					"FluxError",
					getMessage(request, "metadocument.form.camp.portafirmes.flux.enum." + resposta.getEstat()));

			return "portafirmesModalTancar";
		} else {
			PortafirmesFluxInfoDto fluxDetall = null;
			FluxFirmaUsuariCommand command = new FluxFirmaUsuariCommand();
			
			command.setNom(resposta.getNom());
			command.setDescripcio(resposta.getDescripcio());
			command.setPortafirmesFluxId(resposta.getFluxId());
			
			try {
				fluxDetall = portafirmesFluxService.recuperarDetallFluxFirma(resposta.getFluxId(), true);
			} catch (Exception ex) {
				logger.error("Error recuperant firmants flux", ex);
			}
			
			fluxFirmaUsuariService.create(
					entitatActual.getId(),
					FluxFirmaUsuariCommand.asDto(command),
					fluxDetall);
			
			
			model.addAttribute(
					"FluxCreat",
					getMessage(request, "metadocument.form.camp.portafirmes.flux.enum.FINAL_OK"));
			
			return "portafirmesModalTancar";
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
