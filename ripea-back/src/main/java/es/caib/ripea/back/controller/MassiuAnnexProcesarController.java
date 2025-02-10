package es.caib.ripea.back.controller;

import es.caib.ripea.back.command.MassiuAnnexProcesarFiltreCommand;
import es.caib.ripea.back.command.RegistreAnnexCommand;
import es.caib.ripea.back.helper.*;
import es.caib.ripea.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.*;


@Controller
@RequestMapping("/massiu/procesarAnnexosPendents")
public class MassiuAnnexProcesarController extends BaseUserOAdminOOrganController {
	
	
	public static final String SESSION_ATTRIBUTE_FILTRE = "MassiuAnnexProcesarController.session.filtre";
	private static final String SESSION_ATTRIBUTE_SELECCIO = "MassiuAnnexProcesarController.session.seleccio";

	@Autowired private ExpedientPeticioService expedientPeticioService;
	@Autowired private ExpedientService expedientService;
	@Autowired private MetaDocumentService metaDocumentService;
	@Autowired private ExecucioMassivaService execucioMassivaService;
	@Autowired private AplicacioService aplicacioService;

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
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		Long organActualId = EntitatHelper.getOrganGestorActualId(request);
		List<MetaExpedientDto> metaExpedientsPermesos = expedientPeticioService.findMetaExpedientsPermesosPerAnotacions(
				entitatActual.getId(),
				organActualId,
				RolHelper.getRolActual(request));
		model.addAttribute(
				"metaExpedients",
				metaExpedientsPermesos);
		
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
	public DatatablesResponse datatable(HttpServletRequest request) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		MassiuAnnexProcesarFiltreCommand massiuAnnexProcesarCommand = getFiltreCommand(request);
		Long organActualId = EntitatHelper.getOrganGestorActualId(request);
		
		try {
			return DatatablesHelper.getDatatableResponse(
					request,
					 expedientPeticioService.findAnnexosPendentsProcesarMassiu(
								entitatActual.getId(), 
								MassiuAnnexProcesarFiltreCommand.asDto(massiuAnnexProcesarCommand), 
								DatatablesHelper.getPaginacioDtoFromRequest(request),
								ResultEnumDto.PAGE, 
								RolHelper.getRolActual(request), 
								organActualId).getPagina(),
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
		
		Long organActualId = EntitatHelper.getOrganGestorActualId(request);
		
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
								ResultEnumDto.IDS, 
								RolHelper.getRolActual(request), 
								organActualId).getIds());
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
		
		getEntitatActualComprovantPermisos(request);
		
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = ((Set<Long>) RequestSessionHelper.obtenirObjecteSessio(
				request,
				getSessionAttributeSelecio(request)));
		
		if (seleccio == null || seleccio.isEmpty()) {
			return getModalControllerReturnValueError(
					request,
					"redirect:/massiu/procesarAnnexosPendents",
					"accio.massiva.seleccio.buida",
					null);
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
								new Object[] { registreAnnex.getTitol(), ExceptionHelper.getRootCauseOrItself(exception).getMessage() }),
						exception);
				errors++;
			} else {
				correctes++;
			}
		}
		
		if (correctes > 0){
			MissatgesHelper.success(request, getMessage(request, "massiu.controller.annex.procesar.correctes", new Object[]{correctes}));
		} 
		if (errors > 0) {
			MissatgesHelper.error(request, getMessage(request, "massiu.controller.annex.procesar.errors", new Object[]{errors}), null);
		} 
		
		seleccio.clear();
		RequestSessionHelper.actualitzarObjecteSessio(
				request,
				getSessionAttributeSelecio(request),
				seleccio);
		
		return "redirect:../procesarAnnexosPendents";
	}
	
	
	
	
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/adjuntarExpedient", method = RequestMethod.GET)
	public String adjuntarExpedientReintentar(
			HttpServletRequest request,
			Model model) {
		
		Set<Long> seleccio = ((Set<Long>) RequestSessionHelper.obtenirObjecteSessio(
				request,
				getSessionAttributeSelecio(request)));
		
		if (seleccio == null || seleccio.isEmpty()) {
			return getModalControllerReturnValueError(
					request,
					"redirect:/massiu/procesarAnnexosPendents",
					"accio.massiva.seleccio.buida",
					null);
		}
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		MassiuAnnexProcesarFiltreCommand filtreCommand = getFiltreCommand(request);
		
		List<MetaDocumentDto> metaDocuments = metaDocumentService.findActiusPerCreacio(
					entitatActual.getId(),
					null, 
					filtreCommand.getMetaExpedientId(),
					false);
		model.addAttribute(
				"metaDocuments",
				metaDocuments);
		

		
		RegistreAnnexCommand registreAnnexCommand = new RegistreAnnexCommand();
		
		MetaDocumentDto metaDocPerDefecte = metaDocumentService.findByMetaExpedientAndPerDefecteTrue(filtreCommand.getMetaExpedientId());
		if (metaDocPerDefecte != null) {
			registreAnnexCommand.setMetaDocumentId(metaDocPerDefecte.getId());
		}
		
		model.addAttribute(
				"registreAnnexCommand",
				registreAnnexCommand);

		return "expedientPeticioReintentarMetaDocMassiu";

	}
	
	
	
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/adjuntarExpedient", method = RequestMethod.POST)
	public String adjuntarExpedientReintentarPost(
			HttpServletRequest request,
			@Valid RegistreAnnexCommand command,
			BindingResult bindingResult,
			Model model) {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			return "expedientPeticioReintentarMetaDocMassiu";
		}
		
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				getSessionAttributeSelecio(request));
		
		try {
			
			int errors = 0;
			int correctes = 0;
			Date dataInici = new Date();
			List<ExecucioMassivaContingutDto> execucioMassivaElements = new ArrayList<>();
			
			for (Long id : seleccio) {
				Date dataIniciElement = new Date();
				Exception exception = null;
				try {
					exception = expedientService.retryCreateDocFromAnnex(
							id,
							command.getMetaDocumentId(), 
							RolHelper.getRolActual(request));

				} catch (Exception ex) {
					exception = ex;
				}
				if (exception != null ) {
					logger.error("Error al procesarAnnexosPendents document pendent", exception);
					RegistreAnnexDto registreAnnex = expedientPeticioService.findAnnexById(id);
					MissatgesHelper.error(request,
							getMessage(request,
									"massiu.controller.annex.procesar.error",
									new Object[] { registreAnnex.getTitol(), ExceptionHelper.getRootCauseOrItself(exception).getMessage() }),
							exception);
					errors++;
				} else {
					correctes++;
				}
				
				execucioMassivaElements.add(
						new ExecucioMassivaContingutDto(
								dataIniciElement,
								new Date(),
								id,
								exception));
			}
			
			execucioMassivaService.saveExecucioMassiva(
					entitatActual.getId(),
					new ExecucioMassivaDto(
							ExecucioMassivaTipusDto.ADJUNTAR_ANNEXOS_PENDENTS,
							dataInici,
							new Date(),
							RolHelper.getRolActual(request)),
					execucioMassivaElements,
					ElementTipusEnumDto.ANNEX);
			
			if (correctes > 0){
				MissatgesHelper.success(request, getMessage(request, "massiu.controller.annex.procesar.correctes", new Object[]{correctes}));
			} 
			if (errors > 0) {
				MissatgesHelper.error(request, getMessage(request, "massiu.controller.annex.procesar.errors", new Object[]{errors}), null);
			} 
			
			seleccio.clear();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO,
					seleccio);
			
			return modalUrlTancar();
			
		} catch (Exception ex) {
			logger.error("Error al tancament massiu", ex);

			return getModalControllerReturnValueErrorMessageText(
					request,
					"redirect:../massiu/tancament",
					ex.getMessage(),
					ex);
		}
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
			filtreCommand.setMetaExpedientId(aplicacioService.getProcedimentPerDefecte(EntitatHelper.getEntitatActual(request).getId(), RolHelper.getRolActual(request)));
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}

	private static final Logger logger = LoggerFactory.getLogger(MassiuAnnexProcesarController.class);
	
}
