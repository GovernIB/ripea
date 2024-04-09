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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.SeguimentConsultaPinbalDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.SeguimentService;
import es.caib.ripea.war.command.SeguimentConsultaFiltreCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.EntitatHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.RequestSessionHelper;
import es.caib.ripea.war.helper.RolHelper;


@Controller
@RequestMapping("/seguimentPinbal")
public class SeguimentPinbalController extends BaseAdminController {
	
	public static final String SESSION_ATTRIBUTE_FILTRE = "SeguimentPinbalController.session.filtre";
	
    @Autowired
    private SeguimentService seguimentService;
	@Autowired
	private AplicacioService aplicacioService;


    @RequestMapping(method = RequestMethod.GET)
    public String get(HttpServletRequest request, Model model) {
    	
    	EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
    	
    	SeguimentConsultaFiltreCommand command = getFiltreCommand(request);
		model.addAttribute(command);
		
		model.addAttribute(
				"metaExpedients",
				metaExpedientService.findActius(
						entitatActual.getId(),
						null,
						RolHelper.getRolActual(request),
						false,
						null));
    	
        return "seguimentPinbalList";
    }
    
	@RequestMapping(value = "/filtrar", method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid SeguimentConsultaFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model,
			@RequestParam(value = "accio", required = false) String accio) {
		getEntitatActualComprovantPermisos(request);
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
		return "redirect:../seguimentPinbal";
	}
    

    @RequestMapping(value = "/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse datatable(HttpServletRequest request) {
        PaginaDto<SeguimentConsultaPinbalDto> seguiment = new PaginaDto<SeguimentConsultaPinbalDto>();

            EntitatDto entitat = getEntitatActualComprovantPermisos(request);
            
            SeguimentConsultaFiltreCommand filtreCommand = getFiltreCommand(request);

            seguiment = seguimentService.findConsultesPinbal(
					entitat.getId(),
					SeguimentConsultaFiltreCommand.asDto(filtreCommand),
					DatatablesHelper.getPaginacioDtoFromRequest(request));
			
        return DatatablesHelper.getDatatableResponse(request, seguiment, "id");
    }
    
    
    

	
	private SeguimentConsultaFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		SeguimentConsultaFiltreCommand filtreCommand = (SeguimentConsultaFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new SeguimentConsultaFiltreCommand();
			filtreCommand.setMetaExpedientId(aplicacioService.getProcedimentPerDefecte(EntitatHelper.getEntitatActual(request).getId(), RolHelper.getRolActual(request)));
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}

    

	
	
}