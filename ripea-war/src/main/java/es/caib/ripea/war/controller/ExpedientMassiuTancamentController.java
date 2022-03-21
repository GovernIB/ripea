package es.caib.ripea.war.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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

import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.exception.ExpedientTancarSenseDocumentsDefinitiusException;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.core.helper.ExceptionHelper;
import es.caib.ripea.war.command.ContingutMassiuFiltreCommand;
import es.caib.ripea.war.command.ExpedientMassiuTancamentCommand;
import es.caib.ripea.war.command.ExpedientTancarCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.RequestSessionHelper;

/**
 * Controlador per tancament massiu d'expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/massiu/tancament")
public class ExpedientMassiuTancamentController extends BaseUserOAdminOOrganController {
	
	private static final String SESSION_ATTRIBUTE_FILTRE = "ExpedientMassiuTancamentController.session.filtre";
	private static final String SESSION_ATTRIBUTE_SELECCIO_USER = "ExpedientMassiuTancamentController.session.seleccio.user";
	private static final String SESSION_ATTRIBUTE_SELECCIO_ADMIN = "ExpedientMassiuTancamentController.session.seleccio.admin";
	private static final String SESSION_ATTRIBUTE_SELECCIO_ORGAN = "ExpedientMassiuTancamentController.session.seleccio.organ";


	@Autowired
	private MetaExpedientService metaExpedientService;
	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private ContingutService contingutService;
	@Autowired
	private DocumentService documentService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutMassiuFiltreCommand filtreCommand = getFiltreCommand(request);
		
		model.addAttribute(
				filtreCommand);
		
		model.addAttribute(
				"seleccio",
				RequestSessionHelper.obtenirObjecteSessio(
						request,
						getSessionAttributeSelecio(request)));

		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		
		/*boolean checkPerMassiuAdmin = false;
		if (rolActual.equals("IPA_ADMIN") || rolActual.equals("IPA_ORGAN_ADMIN")) {
			checkPerMassiuAdmin = true;
		}*/
		
		model.addAttribute(
				"metaExpedients",
				metaExpedientService.findActiusAmbEntitatPerModificacio(entitatActual.getId(), rolActual));

		return "expedientMassiuTancamentList";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid ContingutMassiuFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model) {
		if (!bindingResult.hasErrors()) {
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		
		return "redirect:/massiu/tancament";
	}
	

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutMassiuFiltreCommand contingutMassiuFiltreCommand = getFiltreCommand(request);
		
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		try {
			return DatatablesHelper.getDatatableResponse(
					request,
					 expedientService.findExpedientsPerTancamentMassiu(
								entitatActual.getId(), 
								ContingutMassiuFiltreCommand.asDto(contingutMassiuFiltreCommand),
								DatatablesHelper.getPaginacioDtoFromRequest(request), rolActual),
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
			ContingutMassiuFiltreCommand filtreCommand = getFiltreCommand(request);
			String rolActual = (String)request.getSession().getAttribute(
					SESSION_ATTRIBUTE_ROL_ACTUAL);
			
			seleccio.addAll(
					expedientService.findIdsExpedientsPerTancamentMassiu(
							entitatActual.getId(),
							ContingutMassiuFiltreCommand.asDto(filtreCommand), rolActual));
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
	
	
	
	
	
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/tancar", method = RequestMethod.GET)
	public String canviarEstatGet(
			HttpServletRequest request,
			Model model) {
		
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				getSessionAttributeSelecio(request));
		
		if (seleccio == null || seleccio.isEmpty()) {
			model.addAttribute("portafirmes", false);
			return getModalControllerReturnValueError(
					request,
					"redirect:/massiu/definitiu",
					"accio.massiva.seleccio.buida");
		}
		omplirModelTancarExpedient(request, model);
		ExpedientMassiuTancamentCommand command = new ExpedientMassiuTancamentCommand();
		model.addAttribute(command);
		
		return "expedientMassiuTancamentForm";
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/tancar", method = RequestMethod.POST)
	public String canviarEstatPost(
			HttpServletRequest request,
			@Valid ExpedientMassiuTancamentCommand command,
			BindingResult bindingResult,
			Model model) {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			omplirModelTancarExpedient(request, model);
			return "expedientMassiuTancamentForm";
		}
		
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				getSessionAttributeSelecio(request));
		
		try {
			
			int errors = 0;
			int nodefinitius = 0;
			int correctes = 0;
			
			for (ExpedientTancarCommand expedientTancar : command.getExpedientsTancar()) {
				
				try {
					expedientService.tancar(
							entitatActual.getId(),
							expedientTancar.getId(),
							command.getMotiu(),
							expedientTancar.getDocumentsPerFirmar(), 
							false);
					
					correctes++;
				} catch (Exception e) {
					Throwable throwable = ExceptionHelper.getRootCauseOrItself(e);
					if (throwable.getClass() == ExpedientTancarSenseDocumentsDefinitiusException.class) {
						nodefinitius++;
					} else {
						errors++;
					}
				}
			}
			
			seleccio.clear();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					getSessionAttributeSelecio(request),
					seleccio);
			
			if (correctes > 0) {
				MissatgesHelper.success(request, getMessage(request, "expedient.controller.tancar.massiu.correctes", new Object[]{correctes}));
			} 
			if (nodefinitius > 0) {
				MissatgesHelper.warning(request, getMessage(request, "expedient.controller.tancar.massiu.nodefinitius", new Object[]{nodefinitius}));
			} 
			if (errors > 0) {
				MissatgesHelper.error(request, getMessage(request, "expedient.controller.tancar.massiu.errors", new Object[]{errors}));
			} 
			return modalUrlTancar();
			
		} catch (Exception ex) {
			logger.error("Error al tancament massiu", ex);

			return getModalControllerReturnValueErrorMessageText(
					request,
					"redirect:../massiu/tancament",
					ex.getMessage());
		}
	}
	
	
	
	private String getSessionAttributeSelecio(HttpServletRequest request) {
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		String sessionAttribute;
		if (rolActual.equals("tothom")) {
			sessionAttribute = SESSION_ATTRIBUTE_SELECCIO_USER;
		} else if (rolActual.equals("IPA_ADMIN")) {
			sessionAttribute = SESSION_ATTRIBUTE_SELECCIO_ADMIN;
		} else if (rolActual.equals("IPA_ORGAN_ADMIN")){
			sessionAttribute = SESSION_ATTRIBUTE_SELECCIO_ORGAN;
		} else {
			throw new RuntimeException("No rol permitido");
		}
		return sessionAttribute;
	}
	
	
	private void omplirModelTancarExpedient(
			HttpServletRequest request,
			Model model) {
		
		boolean hasEsborranys = false;
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		List<ExpedientDto> expedients = new ArrayList<>();
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				getSessionAttributeSelecio(request));
		
		for (Long expedientId : seleccio) {
			ExpedientDto expedient = (ExpedientDto)contingutService.findAmbIdUser(
					entitatActual.getId(),
					expedientId,
					true,
					false, 
					null, 
					null);
			if (expedient.isHasEsborranys()) {
				List<DocumentDto> esborranys = documentService.findAmbExpedientIEstat(
						entitatActual.getId(),
						expedientId,
						DocumentEstatEnumDto.REDACCIO);
				expedient.setEsborranys(esborranys);
				hasEsborranys = true;
			}
			expedients.add(expedient);
		}
		model.addAttribute("expedients", expedients);
		model.addAttribute("hasEsborranys", hasEsborranys);

	}
	
	

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}


	private ContingutMassiuFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		ContingutMassiuFiltreCommand filtreCommand = (ContingutMassiuFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new ContingutMassiuFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ExpedientMassiuTancamentController.class);

}
