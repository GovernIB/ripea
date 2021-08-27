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
import es.caib.ripea.core.api.dto.SeguimentDto;
import es.caib.ripea.core.api.service.ExpedientTascaService;
import es.caib.ripea.core.api.service.SeguimentService;
import es.caib.ripea.war.command.SeguimentFiltreCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.RequestSessionHelper;

/**
 * Controlador per al manteniment de seguiment de assignació de tasques
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/seguimentTasques")
public class SeguimentTasquesController extends BaseAdminController {
	
	private static final String SESSION_ATTRIBUTE_FILTRE = "SeguimentTasquesController.session.filtre";
	
    @Autowired
    private SeguimentService seguimentService;
    @Autowired
    private ExpedientTascaService expedientTascaService;


    @RequestMapping(method = RequestMethod.GET)
    public String get(HttpServletRequest request, Model model) {
    	
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
    	
    	SeguimentFiltreCommand command = getFiltreCommand(request);
		model.addAttribute(command);
		
		model.addAttribute(
				"metaexpTasques",
				expedientTascaService.findAmbEntitat(
						entitatActual.getId()));
    	
        return "seguimentTasquesList";
    }
    
	@RequestMapping(value = "/filtrar", method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid SeguimentFiltreCommand filtreCommand,
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
		return "redirect:../seguimentTasques";
	}
    

    @RequestMapping(value = "/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse datatable(HttpServletRequest request) {
        PaginaDto<SeguimentDto> docsPortafirmes = new PaginaDto<SeguimentDto>();

            EntitatDto entitat = getEntitatActualComprovantPermisos(request);
            
            SeguimentFiltreCommand filtreCommand = getFiltreCommand(request);

            docsPortafirmes = seguimentService.findTasques(
					entitat.getId(),
					SeguimentFiltreCommand.asDto(filtreCommand),
					DatatablesHelper.getPaginacioDtoFromRequest(request));
			
        return DatatablesHelper.getDatatableResponse(request, docsPortafirmes, "id");
    }
    
    
    

	
	private SeguimentFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		SeguimentFiltreCommand filtreCommand = (SeguimentFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new SeguimentFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}

    

	
	
}