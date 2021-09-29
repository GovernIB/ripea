/**
 * 
 */
package es.caib.ripea.war.controller;


import java.util.List;

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
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.SeguimentArxiuPendentsDto;
import es.caib.ripea.core.api.service.SeguimentService;
import es.caib.ripea.war.command.SeguimentArxiuPendentsFiltreCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.RequestSessionHelper;

/**
 * Controlador per al manteniment de seguiment de elements pendents de guardar a dins l'arxiu
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/seguimentArxiuPendents")
public class SeguimentArxiuPendentsController extends BaseSuperController {
	
	private static final String SESSION_ATTRIBUTE_FILTRE_EXPEDIENTS = "SeguimentPortafirmesController.session.filtre.expedients";
	private static final String SESSION_ATTRIBUTE_FILTRE_DOCUMENTS = "SeguimentPortafirmesController.session.filtre.documents";
	private static final String SESSION_ATTRIBUTE_FILTRE_INTERESSATS = "SeguimentPortafirmesController.session.filtre.interessats";
	
    @Autowired
    private SeguimentService seguimentService;



    @RequestMapping(method = RequestMethod.GET)
    public String get(HttpServletRequest request, Model model) {
    	
		EntitatDto entitatActual = getEntitatActual(request);
		
    	SeguimentArxiuPendentsFiltreCommand commandExp = getFiltreCommandExpedients(request);
    	SeguimentArxiuPendentsFiltreCommand commandDoc = getFiltreCommandDocuments(request);
    	SeguimentArxiuPendentsFiltreCommand commandInt = getFiltreCommandInteressats(request);
		model.addAttribute("commandExpedients", commandExp);
		model.addAttribute("commandDocuments", commandDoc);
		model.addAttribute("commandInteressats", commandInt);
		
		List<MetaExpedientDto> metaExpedients = metaExpedientService.findByEntitat(
				entitatActual.getId());
		model.addAttribute(
				"metaExpedients",
				metaExpedients);
    	
        return "seguimentArxiuPendentsList";
    }
    
	@RequestMapping(value = "/filtrar/expedients", method = RequestMethod.POST)
	public String postExpedients(
			HttpServletRequest request,
			@Valid SeguimentArxiuPendentsFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model,
			@RequestParam(value = "accio", required = false) String accio) {
		getEntitatActual(request);
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE_EXPEDIENTS);
		} else {
			if (!bindingResult.hasErrors()) {
				RequestSessionHelper.actualitzarObjecteSessio(
						request,
						SESSION_ATTRIBUTE_FILTRE_EXPEDIENTS,
						filtreCommand);
			}
		}
		return "redirect:../../seguimentArxiuPendents#expedients";
	}

    @RequestMapping(value = "/expedients/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse datatableExpedients(HttpServletRequest request) {
		PaginaDto<SeguimentArxiuPendentsDto> docsPortafirmes = new PaginaDto<SeguimentArxiuPendentsDto>();

		EntitatDto entitat = getEntitatActual(request);

        SeguimentArxiuPendentsFiltreCommand filtreCommand = getFiltreCommandExpedients(request);

        docsPortafirmes = seguimentService.findArxiuPendentsExpedients(
				entitat.getId(),
				SeguimentArxiuPendentsFiltreCommand.asDto(filtreCommand),
				DatatablesHelper.getPaginacioDtoFromRequest(request));
		
        return DatatablesHelper.getDatatableResponse(request, docsPortafirmes, "id");
    }
    
	private SeguimentArxiuPendentsFiltreCommand getFiltreCommandExpedients(
			HttpServletRequest request) {
		SeguimentArxiuPendentsFiltreCommand filtreCommand = (SeguimentArxiuPendentsFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE_EXPEDIENTS);
		if (filtreCommand == null) {
			filtreCommand = new SeguimentArxiuPendentsFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE_EXPEDIENTS,
					filtreCommand);
		}
		return filtreCommand;
	}

	
	
	@RequestMapping(value = "/filtrar/documents", method = RequestMethod.POST)
	public String postDocuments(
			HttpServletRequest request,
			@Valid SeguimentArxiuPendentsFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model,
			@RequestParam(value = "accio", required = false) String accio) {
		getEntitatActual(request);
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE_DOCUMENTS);
		} else {
			if (!bindingResult.hasErrors()) {
				RequestSessionHelper.actualitzarObjecteSessio(
						request,
						SESSION_ATTRIBUTE_FILTRE_DOCUMENTS,
						filtreCommand);
			}
		}
		return "redirect:../../seguimentArxiuPendents#documents";
	}

    @RequestMapping(value = "/documents/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse datatableDocuments(HttpServletRequest request) {
		PaginaDto<SeguimentArxiuPendentsDto> docsPortafirmes = new PaginaDto<SeguimentArxiuPendentsDto>();

		EntitatDto entitat = getEntitatActual(request);

        SeguimentArxiuPendentsFiltreCommand filtreCommand = getFiltreCommandDocuments(request);

        docsPortafirmes = seguimentService.findArxiuPendentsDocuments(
				entitat.getId(),
				SeguimentArxiuPendentsFiltreCommand.asDto(filtreCommand),
				DatatablesHelper.getPaginacioDtoFromRequest(request));
		
        return DatatablesHelper.getDatatableResponse(request, docsPortafirmes, "id");
    }
    
	private SeguimentArxiuPendentsFiltreCommand getFiltreCommandDocuments(
			HttpServletRequest request) {
		SeguimentArxiuPendentsFiltreCommand filtreCommand = (SeguimentArxiuPendentsFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE_DOCUMENTS);
		if (filtreCommand == null) {
			filtreCommand = new SeguimentArxiuPendentsFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE_DOCUMENTS,
					filtreCommand);
		}
		return filtreCommand;
	}
	
	
	
	
	@RequestMapping(value = "/filtrar/interessats", method = RequestMethod.POST)
	public String postInteressats(
			HttpServletRequest request,
			@Valid SeguimentArxiuPendentsFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model,
			@RequestParam(value = "accio", required = false) String accio) {
		getEntitatActual(request);
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE_DOCUMENTS);
		} else {
			if (!bindingResult.hasErrors()) {
				RequestSessionHelper.actualitzarObjecteSessio(
						request,
						SESSION_ATTRIBUTE_FILTRE_DOCUMENTS,
						filtreCommand);
			}
		}
		return "redirect:../../seguimentArxiuPendents#interessats";
	}

    @RequestMapping(value = "/interessats/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse datatableInteressats(HttpServletRequest request) {
		PaginaDto<SeguimentArxiuPendentsDto> docsPortafirmes = new PaginaDto<SeguimentArxiuPendentsDto>();

		EntitatDto entitat = getEntitatActual(request);

        SeguimentArxiuPendentsFiltreCommand filtreCommand = getFiltreCommandInteressats(request);

        docsPortafirmes = seguimentService.findArxiuPendentsInteressats(
				entitat.getId(),
				SeguimentArxiuPendentsFiltreCommand.asDto(filtreCommand),
				DatatablesHelper.getPaginacioDtoFromRequest(request));
		
        return DatatablesHelper.getDatatableResponse(request, docsPortafirmes, "id");
    }
    
	private SeguimentArxiuPendentsFiltreCommand getFiltreCommandInteressats(
			HttpServletRequest request) {
		SeguimentArxiuPendentsFiltreCommand filtreCommand = (SeguimentArxiuPendentsFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE_INTERESSATS);
		if (filtreCommand == null) {
			filtreCommand = new SeguimentArxiuPendentsFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE_INTERESSATS,
					filtreCommand);
		}
		return filtreCommand;
	}
    

	
	
}
