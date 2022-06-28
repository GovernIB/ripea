/**
 * 
 */
package es.caib.ripea.war.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.RegistreAnnexDto;
import es.caib.ripea.core.api.dto.ResultEnumDto;
import es.caib.ripea.core.api.service.ExpedientPeticioService;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.war.command.MassiuAnnexProcesarFiltreCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.ExceptionHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;


@Controller
@RequestMapping("/massiu/procesarAnnexosPendents")
public class MassiuAnnexProcesarController extends BaseUserOAdminOOrganController {
	
	
	private static final String SESSION_ATTRIBUTE_FILTRE = "MassiuAnnexProcesarController.session.filtre";
	private static final String SESSION_ATTRIBUTE_SELECCIO = "MassiuAnnexProcesarController.session.seleccio";

	@Autowired
	private ExpedientPeticioService expedientPeticioService;
	@Autowired
	private ExpedientService expedientService;


	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		MassiuAnnexProcesarFiltreCommand filtreCommand = getFiltreCommand(request);
		model.addAttribute(
				"seleccio",
				RequestSessionHelper.obtenirObjecteSessio(
						request,
						getSessionAttributeSelecio(request)));
		model.addAttribute(
				filtreCommand);
		return "massiuAnnexProcesarList";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid MassiuAnnexProcesarFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model,
			@RequestParam(value = "accio", required = false) String accio) {
	
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE);
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO);
		} else {
			
			if (!bindingResult.hasErrors()) {
				RequestSessionHelper.actualitzarObjecteSessio(
						request,
						SESSION_ATTRIBUTE_FILTRE,
						filtreCommand);
			}
		}
		return "redirect:/massiu/procesarAnnexosPendents";
	}
	

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		MassiuAnnexProcesarFiltreCommand massiuAnnexProcesarCommand = getFiltreCommand(request);

		
		try {
			return DatatablesHelper.getDatatableResponse(
					request,
					 expedientPeticioService.findAnnexosPendentsProcesarMassiu(
								entitatActual.getId(), 
								MassiuAnnexProcesarFiltreCommand.asDto(massiuAnnexProcesarCommand), 
								DatatablesHelper.getPaginacioDtoFromRequest(request),
								ResultEnumDto.PAGE).getPagina(),
					 "id",
					 getSessionAttributeSelecio(request));
		} catch (Exception e) {
			throw e;
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
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			MassiuAnnexProcesarFiltreCommand filtreCommand = getFiltreCommand(request);
			
			seleccio.addAll(
					 expedientPeticioService.findAnnexosPendentsProcesarMassiu(
								entitatActual.getId(), 
								MassiuAnnexProcesarFiltreCommand.asDto(filtreCommand), 
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
	
	
	
	@RequestMapping(value = "/procesarAnnexosPendents", method = RequestMethod.GET)
	public String procesarAnnexosPendentsReintentar(
			HttpServletRequest request) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = ((Set<Long>) RequestSessionHelper.obtenirObjecteSessio(
				request,
				getSessionAttributeSelecio(request)));
		
		if (seleccio == null || seleccio.isEmpty()) {
			return getModalControllerReturnValueError(
					request,
					"redirect:/massiu/procesarAnnexosPendents",
					"accio.massiva.seleccio.buida");
		}
		
		int errors = 0;
		int correctes = 0;
		
		for (Long id : seleccio) {
			Exception exception = null;
			try {
				exception = expedientService.retryMoverAnnexArxiu(id);

			} catch (Exception ex) {
				exception = ex;
			}
			if (exception != null ) {
				logger.error("Error al procesarAnnexosPendents document pendent", exception);
				RegistreAnnexDto registreAnnex = expedientPeticioService.findAnnexById(id);
				MissatgesHelper.error(request,
						getMessage(request,
								"massiu.controller.annex.procesar.error",
								new Object[] { registreAnnex.getTitol(), ExceptionHelper.getRootCauseOrItself(exception).getMessage() }));
				errors++;
			} else {
				correctes++;
			}
		}
		
		if (correctes > 0){
			MissatgesHelper.success(request, getMessage(request, "massiu.controller.annex.procesar.correctes", new Object[]{correctes}));
		} 
		if (errors > 0) {
			MissatgesHelper.error(request, getMessage(request, "massiu.controller.annex.procesar.errors", new Object[]{errors}));
		} 
		
		seleccio.clear();
		RequestSessionHelper.actualitzarObjecteSessio(
				request,
				getSessionAttributeSelecio(request),
				seleccio);
		
		return "redirect:../procesarAnnexosPendents";
	}
	

	
	private String getSessionAttributeSelecio(HttpServletRequest request) {
		return SESSION_ATTRIBUTE_SELECCIO;
	}
	

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}


	private MassiuAnnexProcesarFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		MassiuAnnexProcesarFiltreCommand filtreCommand = (MassiuAnnexProcesarFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new MassiuAnnexProcesarFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}

	private static final Logger logger = LoggerFactory.getLogger(MassiuAnnexProcesarController.class);
	
}
