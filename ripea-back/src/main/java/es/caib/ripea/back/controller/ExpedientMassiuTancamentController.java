package es.caib.ripea.back.controller;

import es.caib.ripea.back.command.ContingutMassiuFiltreCommand;
import es.caib.ripea.back.command.ExpedientMassiuTancamentCommand;
import es.caib.ripea.back.command.ExpedientTancarCommand;
import es.caib.ripea.back.helper.*;
import es.caib.ripea.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.exception.ExpedientTancarSenseDocumentsDefinitiusException;
import es.caib.ripea.service.intf.exception.ValidationException;
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
	@Autowired private ExpedientEstatService expedientEstatService;
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

		List<ExpedientEstatDto> expedientEstatsOptions = new ArrayList<>();
		Long metaExpedientId = filtreCommand != null ? filtreCommand.getMetaExpedientId() : null;
		expedientEstatsOptions.add(new ExpedientEstatDto(getMessage(request, "expedient.estat.enum." + ExpedientEstatEnumDto.values()[0].name()), Long.valueOf(0)));
		expedientEstatsOptions.addAll(expedientEstatService.findExpedientEstatsByMetaExpedient(entitatActual.getId(), metaExpedientId));
		model.addAttribute("expedientEstatsOptions", expedientEstatsOptions);
		
		model.addAttribute(
				"metaExpedients",
				metaExpedientService.findActiusAmbEntitatPerModificacio(entitatActual.getId(), RolHelper.getRolActual(request)));

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
	public DatatablesResponse datatable(HttpServletRequest request) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutMassiuFiltreCommand contingutMassiuFiltreCommand = getFiltreCommand(request);
		try {
			return DatatablesHelper.getDatatableResponse(
					request,
					 expedientService.findExpedientsPerTancamentMassiu(
								entitatActual.getId(), 
								ContingutMassiuFiltreCommand.asDto(contingutMassiuFiltreCommand),
								DatatablesHelper.getPaginacioDtoFromRequest(request),
								RolHelper.getRolActual(request)),
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
			seleccio.addAll(
					expedientService.findIdsExpedientsPerTancamentMassiu(
							entitatActual.getId(),
							ContingutMassiuFiltreCommand.asDto(filtreCommand), 
							RolHelper.getRolActual(request)));
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
			int validacio = 0;
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
					if (ExceptionHelper.isExceptionOrCauseInstanceOf(e, ValidationException.class)) {
						validacio++;
					} else if (throwable.getClass() == ExpedientTancarSenseDocumentsDefinitiusException.class) {
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
			if (validacio > 0) {
				MissatgesHelper.error(request, getMessage(request, "expedient.controller.tancar.massiu.validacio", new Object[]{validacio}), null);
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
		String rolActual = RolHelper.getRolActual(request);
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