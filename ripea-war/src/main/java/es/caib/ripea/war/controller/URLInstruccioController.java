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
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.URLInstruccioDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.URLInstruccioService;
import es.caib.ripea.war.command.URLInstruccioCommand;
import es.caib.ripea.war.command.URLInstruccioFiltreCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;

/**
 * Controlador per al manteniment de avisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/urlInstruccio")
public class URLInstruccioController extends BaseAdminController {
	
	private static final String SESSION_ATTRIBUTE_FILTRE = "URLInstruccioController.session.filtre";
	
	@Autowired
	private URLInstruccioService urlInstruccioService;

	@RequestMapping(method = RequestMethod.GET)
	public String get() {
		return "urlInstruccioList";
	}
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		PaginaDto<URLInstruccioDto> urls = new PaginaDto<URLInstruccioDto>();
		
        try {
        	EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			
			URLInstruccioFiltreCommand filtreCommand = getFiltreCommand(request);
			
			urls = urlInstruccioService.findByEntitatPaginat(
					entitatActual.getId(),
					filtreCommand.asDto(),
					DatatablesHelper.getPaginacioDtoFromRequest(request));
	    } catch (SecurityException e) {
	    	logger.error("Error al obtenir el llistat de permisos", e);
	        MissatgesHelper.error(request, getMessage(request, e.getMessage()), e);
	    }
        
		return DatatablesHelper.getDatatableResponse(request, urls, "id");
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(HttpServletRequest request, Model model) {
		return get(request, null, model);
	}
	
	@RequestMapping(value = "/{urlInstruccioId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long urlInstruccioId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		URLInstruccioDto urlInstruccio = null;
		if (urlInstruccioId != null)
			urlInstruccio = urlInstruccioService.findById(entitatActual.getId(), urlInstruccioId);
		if (urlInstruccio != null) {
			model.addAttribute(URLInstruccioCommand.asCommand(urlInstruccio));
		} else {
			model.addAttribute(new URLInstruccioCommand());
		}
		return "urlInstruccioForm";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid URLInstruccioCommand command,
			BindingResult bindingResult) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		if (bindingResult.hasErrors()) {
			return "urlInstruccioForm";
		}
		if (command.getId() != null) {
			urlInstruccioService.update(
					entitatActual.getId(), 
					URLInstruccioCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:urlInstruccio",
					"url.instruccio.controller.modificat.ok",
					new Object[] { command.getNom() });
		} else {
			urlInstruccioService.create(
					entitatActual.getId(),
					URLInstruccioCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:urlInstruccio",
					"url.instruccio.controller.creat.ok",
					new Object[] { command.getNom() });
		}
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public List<URLInstruccioDto> list(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return urlInstruccioService.findByEntitat(entitatActual.getId());
	}
	
//	@RequestMapping(value = "/{avisId}/enable", method = RequestMethod.GET)
//	public String enable(
//			HttpServletRequest request,
//			@PathVariable Long avisId) {
//		avisService.updateActiva(avisId, true);
//		return getAjaxControllerReturnValueSuccess(
//				request,
//				"redirect:../../avis",
//				"avis.controller.activat.ok");
//	}
//	@RequestMapping(value = "/{avisId}/disable", method = RequestMethod.GET)
//	public String disable(
//			HttpServletRequest request,
//			@PathVariable Long avisId) {
//		avisService.updateActiva(avisId, false);
//		return getAjaxControllerReturnValueSuccess(
//				request,
//				"redirect:../../avis",
//				"avis.controller.desactivat.ok");
//	}

	@RequestMapping(value = "/{urlInstruccioId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long urlInstruccioId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

		URLInstruccioDto  urlInstruccioDto = urlInstruccioService.delete(
				entitatActual.getId(), 
				urlInstruccioId);
		
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../urlInstruccio",
				"url.instruccio.controller.esborrat.ok",
				new Object[] { urlInstruccioDto.getNom() });
	}
	
	private URLInstruccioFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		URLInstruccioFiltreCommand filtreCommand = (URLInstruccioFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new URLInstruccioFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(URLInstruccioController.class);
	
}
