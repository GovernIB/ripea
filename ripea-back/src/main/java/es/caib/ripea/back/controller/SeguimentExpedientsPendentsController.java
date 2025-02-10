/**
 * 
 */
package es.caib.ripea.back.controller;


import es.caib.ripea.back.command.ExpedientPeticioFiltreCommand;
import es.caib.ripea.back.helper.DatatablesHelper;
import es.caib.ripea.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.back.helper.EntitatHelper;
import es.caib.ripea.back.helper.RequestSessionHelper;
import es.caib.ripea.back.helper.RolHelper;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.SeguimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador per al manteniment de seguiment de expedients pendents de distribució
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/seguimentExpedientsPendents")
public class SeguimentExpedientsPendentsController extends BaseAdminController {
	
	public static final String SESSION_ATTRIBUTE_FILTRE = "seguimentExpedientsPendents.session.filtre";
	
    @Autowired
    private SeguimentService seguimentService;
	@Autowired
	private AplicacioService aplicacioService;


    @RequestMapping(method = RequestMethod.GET)
    public String get(HttpServletRequest request, Model model) {
    	
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ExpedientPeticioFiltreCommand command = getFiltreCommand(request);
		model.addAttribute(command);
		
		List<MetaExpedientDto> metaExpedients = new ArrayList<MetaExpedientDto>();
		MetaExpedientDto opcioBuida = new MetaExpedientDto();
		opcioBuida.setId(0l);
		opcioBuida.setClassificacio(null);
		opcioBuida.setNom(getMessage(request, "anotacio.filtre.noProcediment"));
		metaExpedients.add(opcioBuida);
		metaExpedients.addAll(
				metaExpedientService.findByEntitat(
						entitatActual.getId()
				)
		);
		
		model.addAttribute("metaExpedients", metaExpedients);
    	
        return "seguimentExpedientsPendentsList";
    }
    
	@RequestMapping(value = "/filtrar", method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid ExpedientPeticioFiltreCommand filtreCommand,
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
		return "redirect:../seguimentExpedientsPendents";
	}
    

    @RequestMapping(value = "/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse datatable(HttpServletRequest request) {
        PaginaDto<ExpedientPeticioListDto> docsPortafirmes = new PaginaDto<ExpedientPeticioListDto>();

            EntitatDto entitat = getEntitatActualComprovantPermisos(request);
            
            ExpedientPeticioFiltreCommand filtreCommand = getFiltreCommand(request);

            docsPortafirmes = seguimentService.findAnotacionsPendents(
					entitat.getId(),
					ExpedientPeticioFiltreCommand.asDto(filtreCommand),
					DatatablesHelper.getPaginacioDtoFromRequest(request), 
					RolHelper.getRolActual(request));
			
        return DatatablesHelper.getDatatableResponse(request, docsPortafirmes, "id");
    }
    

	
	private ExpedientPeticioFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		ExpedientPeticioFiltreCommand filtreCommand = (ExpedientPeticioFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new ExpedientPeticioFiltreCommand();
			filtreCommand.setEstat(ExpedientPeticioEstatViewEnumDto.PENDENT);
			filtreCommand.setMetaExpedientId(aplicacioService.getProcedimentPerDefecte(EntitatHelper.getEntitatActual(request).getId(), RolHelper.getRolActual(request)));
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}

	
	
}