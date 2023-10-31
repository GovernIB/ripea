/**
 * 
 */
package es.caib.ripea.war.controller;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.ContingutLogDetallsDto;
import es.caib.ripea.core.api.dto.ContingutTipusEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.ResultEnumDto;
import es.caib.ripea.core.api.exception.PermissionDeniedException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.core.api.service.OrganGestorService;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.war.command.ContingutFiltreCommand;
import es.caib.ripea.war.command.ContingutFiltreCommand.ContenidorFiltreOpcionsEsborratEnum;
import es.caib.ripea.war.command.ExpedientAssignarCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.ExceptionHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador per a la consulta d'arxius pels administradors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Controller
@RequestMapping("/contingutAdmin")
public class ContingutAdminController extends BaseAdminController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "ContingutAdminController.session.filtre";
	private static final String SESSION_ATTRIBUTE_SELECCIO = "ContingutAdminController.session.seleccio";
	

	@Autowired
	private ContingutService contingutService;
	@Autowired
	private MetaExpedientService metaExpedientService;
	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private OrganGestorService organGestorService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		ContingutFiltreCommand filtreCommand = getFiltreCommand(request);
		model.addAttribute(
				filtreCommand);
		if (ContingutTipusEnumDto.EXPEDIENT.equals(filtreCommand.getTipus())) {
			model.addAttribute(
					"metaNodes",
					metaExpedientService.findByEntitat(entitatActual.getId()));
		} else if (ContingutTipusEnumDto.DOCUMENT.equals(filtreCommand.getTipus())) {
			/*model.addAttribute(
					"metaNodes",
					metaDocumentService.findByEntitat(entitatActual.getId()));*/
		}
		
		model.addAttribute(
				"seleccio",
				RequestSessionHelper.obtenirObjecteSessio(
						request,
						getSessionAttributeSelecio(request)));
		
		return "contingutAdminList";
	}
	@RequestMapping(method = RequestMethod.POST)
	public String expedientPost(
			HttpServletRequest request,
			@Valid ContingutFiltreCommand filtreCommand,
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio,
			Model model) {
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
		return "redirect:contingutAdmin";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		ContingutFiltreCommand filtreCommand = getFiltreCommand(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				contingutService.findAdmin(
						entitatActual.getId(),
						ContingutFiltreCommand.asDto(filtreCommand),
						DatatablesHelper.getPaginacioDtoFromRequest(request),
						ResultEnumDto.PAGE).getPagina(),
				"id",
				getSessionAttributeSelecio(request));
	}

	@RequestMapping(value = "/{contingutId}/info", method = RequestMethod.GET)
	public String info(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		model.addAttribute(
				"contingut",
				contingutService.findAmbIdAdmin(
						entitatActual.getId(),
						contingutId));
		return "contingutAdminInfo";
	}

	@RequestMapping(value = "/{contingutId}/log", method = RequestMethod.GET)
	public String log(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		model.addAttribute(
				"contingut",
				contingutService.findAmbIdAdmin(
						entitatActual.getId(),
						contingutId));
		model.addAttribute(
				"logs",
				contingutService.findLogsPerContingutAdmin(
						entitatActual.getId(),
						contingutId));
		model.addAttribute(
				"moviments",
				contingutService.findMovimentsPerContingutAdmin(
						entitatActual.getId(),
						contingutId));
		model.addAttribute(
				"logTipusEnumOptions",
				EnumHelper.getOptionsForEnum(
						LogTipusEnumDto.class,
						"log.tipus.enum."));
		model.addAttribute(
				"logObjecteTipusEnumOptions",
				EnumHelper.getOptionsForEnum(
						LogObjecteTipusEnumDto.class,
						"log.objecte.tipus.enum."));		
		return "contingutLog";
	}
	
	@RequestMapping(value = "/{contingutId}/log/{contingutLogId}/detalls", method = RequestMethod.GET)
	@ResponseBody
	public ContingutLogDetallsDto logDetalls(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@PathVariable Long contingutLogId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		return contingutService.findLogDetallsPerContingutUser(
				entitatActual.getId(),
				contingutId,
				contingutLogId);
	}


	@RequestMapping(value = "/{contingutId}/undelete", method = RequestMethod.GET)
	public String undelete(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		organGestorService.actualitzarOrganCodi(organGestorService.getOrganCodiFromContingutId(contingutId));
		try {
			contingutService.undelete(
					entitatActual.getId(),
					contingutId);
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../../esborrat",
					"contingut.admin.controller.recuperat.ok");
		}  catch (Exception ex) {
			logger.error("Error al recuperar element", ex);
			return getAjaxControllerReturnValueError(
					request,
					"redirect:../../esborrat",
					"contingut.admin.controller.recuperat.error",
					new Object[] { Utils.getRootCauseOrItself(ex).getMessage() },
					ex);
		}
	}
	
	
	
	@RequestMapping(value = "/{expedientId}/assignar", method = RequestMethod.GET)
	public String assignar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		getEntitatActualComprovantPermisAdminEntitat(request);

		ExpedientAssignarCommand command = new ExpedientAssignarCommand();
		model.addAttribute(command);
		
		return "expedientAssignarForm";
	}
	
	@RequestMapping(value = "/{expedientId}/assignar", method = RequestMethod.POST)
	public String assignarPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@Valid ExpedientAssignarCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		if (bindingResult.hasErrors()) {
			return "expedientAssignarForm";
		}
		
		try {
			expedientService.assignar(
					entitatActual.getId(),
					expedientId,
					command.getUsuariCodi());
			
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../contingut/" + expedientId,
					"expedient.assignar.controller.assignat.ok");
			
		} catch (Exception e) {
			Exception exc = ExceptionHelper.findExceptionInstance(e, PermissionDeniedException.class, 3);
			if (exc != null) {
				PermissionDeniedException perExc = (PermissionDeniedException) exc;
				if (perExc.getUserName().equals(command.getUsuariCodi()) && perExc.getPermissionName().equals("WRITE")) {
					return getModalControllerReturnValueError(
							request,
							"redirect:../../contingut/" + expedientId,
							"expedient.assignar.controller.no.permis",
							new Object[] {command.getUsuariCodi()},
							e);
				} else {
					throw e;
				}
			} else {
				throw e;
			}
		}

	}

	@RequestMapping(value = "/{contingutId}/deleteDefinitiu", method = RequestMethod.GET)
	public String deleteDefinitiu(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		contingutService.deleteDefinitiu(
				entitatActual.getId(),
				contingutId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../esborrat",
				"contingut.admin.controller.esborrat.definitiu.ok");
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
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
					contingutService.findAdmin(
							getEntitatActualComprovantPermisAdminEntitat(request).getId(),
							ContingutFiltreCommand.asDto(getFiltreCommand(request)),
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
	
    

	
	

   @RequestMapping(value = "/recuperarMassiu", method = RequestMethod.GET)
	public String recuperarMassiu(
			HttpServletRequest request) throws Throwable {

		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = ((Set<Long>) RequestSessionHelper.obtenirObjecteSessio(
				request,
				getSessionAttributeSelecio(request)));
		
		if (seleccio == null || seleccio.isEmpty()) {
			return getModalControllerReturnValueError(
					request,
					"redirect:/contingutAdmin",
					"accio.massiva.seleccio.buida",
					null);
		}
		
		int errors = 0;
		int warnings = 0;
		int correctes = 0;
		
		for (Long id : seleccio) {
			
			if (contingutService.isDeleted(id)) {
				
				try {
					organGestorService.actualitzarOrganCodi(organGestorService.getOrganCodiFromContingutId(id));
					contingutService.undelete(
							entitatActual.getId(),
							id);
					correctes++;
				} catch (Exception ex) {
					log.error("Error al recuperar contingut massiu", ex);
					errors++;
				}
			} else {
				warnings++;
			}
		}
		
		if (correctes > 0){
			MissatgesHelper.success(request, getMessage(request, "contingut.admin.controller.recuperar.massiu.ok", new Object[]{correctes}));
		} 
		if (warnings > 0){
			MissatgesHelper.warning(request, getMessage(request, "contingut.admin.controller.recuperar.massiu.warning", new Object[]{warnings}));
		}
		if (errors > 0) {
			MissatgesHelper.error(request, getMessage(request, "contingut.admin.controller.recuperar.massiu.error", new Object[]{errors}), null);
		} 
		
		seleccio.clear();
		RequestSessionHelper.actualitzarObjecteSessio(
				request,
				getSessionAttributeSelecio(request),
				seleccio);
		
		return "redirect:/contingutAdmin";
	}
   
   
   
   
   @RequestMapping(value = "/deleteDefinitiuMassiu", method = RequestMethod.GET)
	public String deleteDefinitiuMassiu(
			HttpServletRequest request) throws Throwable {

		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = ((Set<Long>) RequestSessionHelper.obtenirObjecteSessio(
				request,
				getSessionAttributeSelecio(request)));
		
		if (seleccio == null || seleccio.isEmpty()) {
			return getModalControllerReturnValueError(
					request,
					"redirect:/contingutAdmin",
					"accio.massiva.seleccio.buida",
					null);
		}
		
		int errors = 0;
		int warnings = 0;
		int correctes = 0;
		
		for (Long id : seleccio) {
			
			if (contingutService.isDeleted(id)) {
				
				try {
					organGestorService.actualitzarOrganCodi(organGestorService.getOrganCodiFromContingutId(id));
					contingutService.deleteDefinitiu(
							entitatActual.getId(),
							id);
					correctes++;
				} catch (Exception ex) {
					log.error("Error al esborrar definitivament massiu", ex);
					errors++;
				}
			} else {
				warnings++;
			}
		}
		
		if (correctes > 0){
			MissatgesHelper.success(request, getMessage(request, "contingut.admin.controller.esborrar.massiu.ok", new Object[]{correctes}));
		} 
		if (warnings > 0){
			MissatgesHelper.warning(request, getMessage(request, "contingut.admin.controller.esborrar.massiu.warning", new Object[]{warnings}));
		}
		if (errors > 0) {
			MissatgesHelper.error(request, getMessage(request, "contingut.admin.controller.esborrar.massiu.error", new Object[]{errors}), null);
		} 
		
		seleccio.clear();
		RequestSessionHelper.actualitzarObjecteSessio(
				request,
				getSessionAttributeSelecio(request),
				seleccio);
		
		return "redirect:/contingutAdmin";
	}
    

	
	
    
    
	private String getSessionAttributeSelecio(HttpServletRequest request) {
		return SESSION_ATTRIBUTE_SELECCIO;
	}


	private ContingutFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		ContingutFiltreCommand filtreCommand = (ContingutFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new ContingutFiltreCommand();
			filtreCommand.setOpcionsEsborrat(ContenidorFiltreOpcionsEsborratEnum.NOMES_NO_ESBORRATS);
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}

	private static final Logger logger = LoggerFactory.getLogger(ContingutAdminController.class);

}
