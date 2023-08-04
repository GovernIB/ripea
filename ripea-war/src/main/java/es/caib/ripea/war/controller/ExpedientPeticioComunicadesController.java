/**
 * 
 */
package es.caib.ripea.war.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioFiltreDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.ResultEnumDto;
import es.caib.ripea.core.api.service.ExpedientPeticioService;
import es.caib.ripea.war.command.ContingutMassiuFiltreCommand;
import es.caib.ripea.war.command.ExpedientPeticioFiltreCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.ExceptionHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import lombok.extern.slf4j.Slf4j;
import es.caib.ripea.war.helper.RequestSessionHelper;

@Slf4j
@Controller
@RequestMapping("/expedientPeticioComunicades")
public class ExpedientPeticioComunicadesController extends BaseUserOAdminOOrganController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "ExpedientPeticioComunicadesController.session.filtre";
	private static final String SESSION_ATTRIBUTE_SELECCIO = "ExpedientPeticioComunicadesController.session.seleccio";

	@Autowired
	private ExpedientPeticioService expedientPeticioService;


	
	@RequestMapping(method = RequestMethod.GET)
	public String getComunicadas(HttpServletRequest request, Model model) {
		
		model.addAttribute(
				"seleccio",
				RequestSessionHelper.obtenirObjecteSessio(
						request,
						getSessionAttributeSelecio(request)));

		model.addAttribute(getFiltreCommand(request));
		return "expedientPeticioComunicadaList";
	}
	
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse comunicadasDatatable(HttpServletRequest request) {

		ExpedientPeticioFiltreCommand expedientPeticioFiltreCommand = getFiltreCommand(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				expedientPeticioService.findComunicadesAmbFiltre(
						ExpedientPeticioFiltreCommand.asDto(expedientPeticioFiltreCommand),
						DatatablesHelper.getPaginacioDtoFromRequest(request), 
						ResultEnumDto.PAGE).getPagina(),
				"id",
				getSessionAttributeSelecio(request));
	}

    
	
	@RequestMapping(method = RequestMethod.POST)
	public String postComunicadas(
			HttpServletRequest request,
			ExpedientPeticioFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model,
			@RequestParam(value = "accio", required = false) String accio) {

		getEntitatActualComprovantPermisos(request);
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE);
		} else {
			if (!bindingResult.hasErrors()) {
				RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
			}
		}
		return "redirect:expedientPeticioComunicades";
	}


	
	
	@RequestMapping(value = "/comunicadaConsultar/{expedientPeticioId}", method = RequestMethod.GET)
	public String comunicadaConsultar(
			HttpServletRequest request,
			@PathVariable Long expedientPeticioId,
			Model model) throws Throwable {
		try {
			expedientPeticioService.comunicadaReprocessar(expedientPeticioId);
			
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/expedientPeticioComunicades",
					"expedient.peticio.comunicades.descarregar.ok");
		} catch (Exception e) {
			
			return getModalControllerReturnValueErrorMessageText(
					request,
					"redirect:/expedientPeticioComunicades",
					getMessage(request, "expedient.peticio.comunicades.descarregar.error") + ": " + e.getMessage(),
					e);
		}

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
			ExpedientPeticioFiltreCommand filtreCommand = getFiltreCommand(request);
			
			seleccio.addAll(
					expedientPeticioService.findComunicadesAmbFiltre(
							ExpedientPeticioFiltreCommand.asDto(filtreCommand),
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
	
	
	   @RequestMapping(value = "/comunicadaConsultarMassiu", method = RequestMethod.GET)
		public String comunicadaConsultarMassiu(
				HttpServletRequest request) throws Throwable {
			
			
			@SuppressWarnings("unchecked")
			Set<Long> seleccio = ((Set<Long>) RequestSessionHelper.obtenirObjecteSessio(
					request,
					getSessionAttributeSelecio(request)));
			
			if (seleccio == null || seleccio.isEmpty()) {
				return getModalControllerReturnValueError(
						request,
						"redirect:/expedientPeticioComunicades",
						"accio.massiva.seleccio.buida",
						null);
			}
			
			int errors = 0;
			int correctes = 0;
			
			for (Long id : seleccio) {
				Exception exception = null;
				try {
					expedientPeticioService.comunicadaReprocessar(id);
				} catch (Exception ex) {
					exception = ex;
				}
				if (exception != null ) {
					log.error("Error al custodiar document pendent", exception);
					
					MissatgesHelper.error(
							request,
							getMessage(request, "expedient.peticio.comunicades.consultar.error", new Object[]{id}),
							exception);
					
					errors++;
				} else {
					correctes++;
				}
			
			}
			
			if (correctes > 0){
				MissatgesHelper.success(request, getMessage(request, "expedient.peticio.comunicades.consultar.massiu.ok", new Object[]{correctes}));
			} 
			if (errors > 0) {
				MissatgesHelper.error(request, getMessage(request, "expedient.peticio.comunicades.consultar.massiu.error", new Object[]{errors}), null);
			} 
			
			seleccio.clear();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					getSessionAttributeSelecio(request),
					seleccio);
			
			return "redirect:/expedientPeticioComunicades";
		}
	    
	

	
	

	
	
	
	private String getSessionAttributeSelecio(HttpServletRequest request) {
		return SESSION_ATTRIBUTE_SELECCIO;
	}
	

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}

	/**
	 * Gets filtreCommand from session, if it doesnt exist it creates new one in session
	 * @param request
	 * @return 
	 */
	private ExpedientPeticioFiltreCommand getFiltreCommand(HttpServletRequest request) {

		ExpedientPeticioFiltreCommand filtreCommand = (ExpedientPeticioFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new ExpedientPeticioFiltreCommand();
			filtreCommand.setEstatAll(ExpedientPeticioEstatEnumDto.CREAT);
			RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
			filtreCommand.setNomesAmbErrorsConsulta(true);
		}
		return filtreCommand;
	}
	

	private static final Logger logger = LoggerFactory.getLogger(ExpedientPeticioComunicadesController.class);

}
