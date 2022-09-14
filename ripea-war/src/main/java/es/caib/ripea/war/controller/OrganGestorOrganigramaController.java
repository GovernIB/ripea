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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import es.caib.ripea.core.api.dto.ArbreDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.OrganEstatEnumDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.service.OrganGestorService;
import es.caib.ripea.war.command.OrganGestorFiltreCommand;
import es.caib.ripea.war.helper.RequestSessionHelper;


@Controller
@RequestMapping("/organGestorOrganigrama")
public class OrganGestorOrganigramaController extends BaseUserOAdminController {
	
	private static final String SESSION_ATTRIBUTE_FILTRE = "OrganGestorOrganigramaController.session.filtre";
	
    @Autowired
    private OrganGestorService organGestorService;


    @RequestMapping(method = RequestMethod.GET)
    public String get(HttpServletRequest request, Model model) {

    	omplirModel(request, model);
        return "organGestorOrganigrama";
    }
    
	@RequestMapping(value = "/filtrar", method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid OrganGestorFiltreCommand filtreCommand,
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
		return "redirect:../organGestorOrganigrama";
	}
    
    
    
	private OrganGestorFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		OrganGestorFiltreCommand filtreCommand = (OrganGestorFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new OrganGestorFiltreCommand();
			filtreCommand.setEstat(OrganEstatEnumDto.V);
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}

    
	private void omplirModel(
			HttpServletRequest request,
			Model model) {
		
    	EntitatDto entitat = getEntitatActualComprovantPermisos(request);
    	OrganGestorFiltreCommand command = getFiltreCommand(request);
		model.addAttribute(command);
		
    	List<OrganGestorDto> organsSuperior = organGestorService.findOrgansSuperiorByEntitat(entitat.getId());
		model.addAttribute(
				"organsSuperior",
				organsSuperior);
		
		
		ArbreDto<OrganGestorDto> arbreOrgans = organGestorService.findOrgansArbreAmbFiltre(
				entitat.getId(),
				command.asDto());
		
		model.addAttribute(
				"arbreOrgans",
				arbreOrgans);
	
	}
	
	
	private static final Logger logger = LoggerFactory.getLogger(OrganGestorOrganigramaController.class);
}
