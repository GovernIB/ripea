/**
 * 
 */
package es.caib.ripea.war.controller;


import java.util.HashSet;
import java.util.Set;

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
import es.caib.ripea.core.api.dto.ResultEnumDto;
import es.caib.ripea.core.api.dto.SeguimentDto;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.service.SeguimentService;
import es.caib.ripea.war.command.SeguimentFiltreCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador per al manteniment de seguiment de documents enviats a Portafib
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Controller
@RequestMapping("/seguimentNotificacions")
public class SeguimentNotificacionsController extends BaseAdminController {
	
	private static final String SESSION_ATTRIBUTE_FILTRE = "SeguimentNotificacionsController.session.filtre";
	private static final String SESSION_ATTRIBUTE_SELECCIO = "SeguimentNotificacionsController.session.seleccio";
	
    @Autowired
    private SeguimentService seguimentService;
	@Autowired
	private DocumentService documentService;

    @RequestMapping(method = RequestMethod.GET)
    public String get(HttpServletRequest request, Model model) {
    	
		model.addAttribute(getFiltreCommand(request));
		
		model.addAttribute(
				"seleccio",
				RequestSessionHelper.obtenirObjecteSessio(
						request,
						getSessionAttributeSelecio(request)));
    	
        return "seguimentNotificacionsList";
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
		return "redirect:../seguimentNotificacions";
	}
    

    @RequestMapping(value = "/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse datatable(HttpServletRequest request) {
        PaginaDto<SeguimentDto> docsPortafirmes = new PaginaDto<SeguimentDto>();

            EntitatDto entitat = getEntitatActualComprovantPermisos(request);
            
            SeguimentFiltreCommand filtreCommand = getFiltreCommand(request);

            docsPortafirmes = seguimentService.findNotificacionsEnviaments(
					entitat.getId(),
					SeguimentFiltreCommand.asDto(filtreCommand),
					DatatablesHelper.getPaginacioDtoFromRequest(request), 
					ResultEnumDto.PAGE).getPagina();
            
		return DatatablesHelper.getDatatableResponse(
				request,
				docsPortafirmes,
				"id",
				getSessionAttributeSelecio(request));
    }
    
    
    
    
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/select", method = RequestMethod.GET)
	@ResponseBody
	public int select(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) Long[] ids) {
		
		
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				getSessionAttributeSelecio(request));
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					getSessionAttributeSelecio(request),
					seleccio);
		}
		if (ids != null) {
			for (Long id: ids) {
				seleccio.add(id);
			}
		} else {
			
			seleccio.addAll(
		            seguimentService.findNotificacionsEnviaments(
		            		getEntitatActualComprovantPermisos(request).getId(),
							SeguimentFiltreCommand.asDto(getFiltreCommand(request)),
							null, 
							ResultEnumDto.IDS).getIds());
		}
		return seleccio.size();
	}

	@RequestMapping(value = "/deselect", method = RequestMethod.GET)
	@ResponseBody
	public int deselect(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) Long[] ids) {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				getSessionAttributeSelecio(request));
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					getSessionAttributeSelecio(request),
					seleccio);
		}
		if (ids != null) {
			for (Long id: ids) {
				seleccio.remove(id);
			}
		} else {
			seleccio.clear();
		}
		return seleccio.size();
	}
	
    

	
	
	
	   @RequestMapping(value = "/actualitzarEstatMassiu", method = RequestMethod.GET)
		public String comunicadaConsultarMassiu(
				HttpServletRequest request) throws Throwable {
			
			
			@SuppressWarnings("unchecked")
			Set<Long> seleccio = ((Set<Long>) RequestSessionHelper.obtenirObjecteSessio(
					request,
					getSessionAttributeSelecio(request)));
			
			if (seleccio == null || seleccio.isEmpty()) {
				return getModalControllerReturnValueError(
						request,
						"redirect:/seguimentNotificacions",
						"accio.massiva.seleccio.buida",
						null);
			}
			
			int errors = 0;
			int correctes = 0;
			
			for (Long id : seleccio) {
				Exception exception = null;
				try {
					documentService.notificacioActualitzarEstat(id);
				} catch (Exception ex) {
					exception = ex;
				}
				if (exception != null ) {
					log.error("Error al actualitzar estat del notificacio massiu", exception);
					errors++;
				} else {
					correctes++;
				}
			}
			
			if (correctes > 0){
				MissatgesHelper.success(request, getMessage(request, "seguiment.list.notificacio.actualitzar.estat.massiu.ok", new Object[]{correctes}));
			} 
			if (errors > 0) {
				MissatgesHelper.error(request, getMessage(request, "seguiment.list.notificacio.actualitzar.estat.massiu.error", new Object[]{errors}), null);
			} 
			
			seleccio.clear();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					getSessionAttributeSelecio(request),
					seleccio);
			
			return "redirect:/seguimentNotificacions";
		}
	    
	
	
	
    
    
	private String getSessionAttributeSelecio(HttpServletRequest request) {
		return SESSION_ATTRIBUTE_SELECCIO;
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
