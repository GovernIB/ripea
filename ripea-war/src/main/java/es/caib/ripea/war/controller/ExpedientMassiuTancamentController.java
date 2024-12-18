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
import es.caib.ripea.core.api.dto.ElementTipusEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaContingutDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaTipusDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.PrioritatEnumDto;
import es.caib.ripea.core.api.exception.ExpedientTancarSenseDocumentsDefinitiusException;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.service.ExecucioMassivaService;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.command.ContingutMassiuFiltreCommand;
import es.caib.ripea.war.command.ExpedientMassiuTancamentCommand;
import es.caib.ripea.war.command.ExpedientTancarCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.EntitatHelper;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.ExceptionHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;
import es.caib.ripea.war.helper.RolHelper;

@Controller
@RequestMapping("/massiu/tancament")
public class ExpedientMassiuTancamentController extends BaseUserOAdminOOrganController {
	
	public static final String SESSION_ATTRIBUTE_FILTRE = "ExpedientMassiuTancamentController.session.filtre";
	private static final String SESSION_ATTRIBUTE_SELECCIO_USER = "ExpedientMassiuTancamentController.session.seleccio.user";
	private static final String SESSION_ATTRIBUTE_SELECCIO_ADMIN = "ExpedientMassiuTancamentController.session.seleccio.admin";
	private static final String SESSION_ATTRIBUTE_SELECCIO_ORGAN = "ExpedientMassiuTancamentController.session.seleccio.organ";

	@Autowired private MetaExpedientService metaExpedientService;
	@Autowired private ExpedientService expedientService;
	@Autowired private ContingutService contingutService;
	@Autowired private DocumentService documentService;
	@Autowired private ExecucioMassivaService execucioMassivaService;
	@Autowired private AplicacioService aplicacioService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutMassiuFiltreCommand filtreCommand = getFiltreCommand(request);
		model.addAttribute(filtreCommand);
		model.addAttribute(
				"seleccio",
				RequestSessionHelper.obtenirObjecteSessio(
						request,
						getSessionAttributeSelecio(request)));
		model.addAttribute("prioritatsExpedient",
				EnumHelper.getOptionsForEnum(
						PrioritatEnumDto.class,
						"prioritat.enum.",
						new Enum<?>[] {}));
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);

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
			Model model,
			@RequestParam(value = "accio", required = false) String accio) {
		
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
	public String tancarMassiuGet(
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
					"accio.massiva.seleccio.buida",
					null);
		}
		omplirModelTancarExpedient(request, model);
		ExpedientMassiuTancamentCommand command = new ExpedientMassiuTancamentCommand();
		model.addAttribute(command);
		
		return "expedientMassiuTancamentForm";
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/tancar", method = RequestMethod.POST)
	public String tancarMassiuPost(
			HttpServletRequest request,
			@Valid ExpedientMassiuTancamentCommand command,
			BindingResult bindingResult,
			Model model) {
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
			Date dataInici = new Date();
			List<ExecucioMassivaContingutDto> execucioMassivaElements = new ArrayList<>();
			
			for (ExpedientTancarCommand expedientTancar : command.getExpedientsTancar()) {
				Date dataIniciElement = new Date();
				Throwable throwable = null;
				try {
					expedientService.tancar(
							entitatActual.getId(),
							expedientTancar.getId(),
							command.getMotiu(),
							expedientTancar.getDocumentsPerFirmar(), 
							false);
					
					correctes++;
				} catch (Exception e) {
					logger.error("Error al tancament massiu de expedient amb id=" + expedientTancar.getId(), e);
					throwable = ExceptionHelper.getRootCauseOrItself(e);
					if (throwable.getClass() == ExpedientTancarSenseDocumentsDefinitiusException.class) {
						nodefinitius++;
					} else {
						errors++;
//						ExpedientDto expedientDto = expedientService.findById(entitatActual.getId(), expedientTancar.getId(), RolHelper.getRolActual(request));
//						MissatgesHelper.error(request, getMessage(request, "expedient.controller.tancar.massiu.error", new Object[]{expedientDto.getNom(), throwable.getMessage()}), throwable);
					}
				}
				
				
				execucioMassivaElements.add(
						new ExecucioMassivaContingutDto(
								dataIniciElement,
								new Date(),
								expedientTancar.getId(),
								throwable));
			}
			
			
			execucioMassivaService.saveExecucioMassiva(
					entitatActual.getId(),
					new ExecucioMassivaDto(
							ExecucioMassivaTipusDto.TANCAMENT,
							dataInici,
							new Date(),
							RolHelper.getRolActual(request)),
					execucioMassivaElements,
					ElementTipusEnumDto.EXPEDIENT);
			
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
				MissatgesHelper.error(request, getMessage(request, "expedient.controller.tancar.massiu.errors", new Object[]{errors}), null);
			} 
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
					true, 
					null, 
					null);
			List<DocumentDto> esborranys = documentService.findDocumentsNoFirmatsOAmbFirmaInvalidaONoGuardatsEnArxiu(
					entitatActual.getId(),
					expedientId);
			if (esborranys != null && !esborranys.isEmpty()) {
				expedient.setEsborranys(esborranys);
				expedient.setHasNoFirmatsOAmbFirmaInvalida(true);
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
			filtreCommand.setMetaExpedientId(aplicacioService.getProcedimentPerDefecte(EntitatHelper.getEntitatActual(request).getId(), RolHelper.getRolActual(request)));
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ExpedientMassiuTancamentController.class);

}
