package es.caib.ripea.back.controller;

import es.caib.ripea.back.command.ContingutMassiuFiltreCommand;
import es.caib.ripea.back.command.ExpedientMassiuCanviEstatCommand;
import es.caib.ripea.back.helper.*;
import es.caib.ripea.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.service.*;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Controller
@RequestMapping("/massiu/canviEstat")
public class ExpedientMassiuCanviEstatController extends BaseUserOAdminOOrganController {
	
	public static final String SESSION_ATTRIBUTE_FILTRE = "ExpedientCanviEstatMassiuController.session.filtre";
	private static final String SESSION_ATTRIBUTE_SELECCIO_USER = "ExpedientCanviEstatMassiuController.session.seleccio.user";
	private static final String SESSION_ATTRIBUTE_SELECCIO_ADMIN = "ExpedientCanviEstatMassiuController.session.seleccio.admin";
	private static final String SESSION_ATTRIBUTE_SELECCIO_ORGAN = "ExpedientCanviEstatMassiuController.session.seleccio.organ";

	@Autowired private MetaExpedientService metaExpedientService;
	@Autowired private ExpedientService expedientService;
	@Autowired private ExecucioMassivaService execucioMassivaService;
	@Autowired private ExpedientEstatService expedientEstatService;
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

		model.addAttribute(
				"metaExpedients",
				metaExpedientService.findActiusAmbEntitatPerModificacio(entitatActual.getId(), RolHelper.getRolActual(request)));

		//putting enums from ExpedientEstatEnumDto and ExpedientEstatDto into one class, need to have all estats from enums and database in one class 
		List<ExpedientEstatDto> expedientEstatsOptions = new ArrayList<>();
		Long metaExpedientId = filtreCommand != null ? filtreCommand.getMetaExpedientId() : null;
		expedientEstatsOptions.add(new ExpedientEstatDto(getMessage(request, "expedient.estat.enum." + ExpedientEstatEnumDto.values()[0].name()), Long.valueOf(0)));
		expedientEstatsOptions.addAll(expedientEstatService.findExpedientEstatsByMetaExpedient(entitatActual.getId(), metaExpedientId));
		model.addAttribute("expedientEstatsOptions", expedientEstatsOptions);
		MissatgesHelper.info(
				request,
				getMessage(request, "accio.massiva.list.filtre.procediment.comment"));
		return "expedientMassiuCanviEstatList";
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
		return "redirect:/massiu/canviEstat";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutMassiuFiltreCommand contingutMassiuFiltreCommand = getFiltreCommand(request);
		try {
			return DatatablesHelper.getDatatableResponse(
					request,
					 expedientEstatService.findExpedientsPerCanviEstatMassiu(
								entitatActual.getId(), 
								ContingutMassiuFiltreCommand.asDto(contingutMassiuFiltreCommand),
								DatatablesHelper.getPaginacioDtoFromRequest(request), 
								RolHelper.getRolActual(request), 
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
			ContingutMassiuFiltreCommand filtreCommand = getFiltreCommand(request);
			seleccio.addAll(
					expedientEstatService.findExpedientsPerCanviEstatMassiu(
							entitatActual.getId(),
							ContingutMassiuFiltreCommand.asDto(filtreCommand), 
							null,
							RolHelper.getRolActual(request),
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
	
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/canviarEstat", method = RequestMethod.GET)
	public String canviarEstatGet(
			HttpServletRequest request,
			Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
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
		
		ExpedientDto oneOfSelectedExpedients = expedientService.findById(entitatActual.getId(), seleccio.iterator().next(), null);

		List<ExpedientEstatDto> expedientEstats = expedientEstatService.findExpedientEstatsByMetaExpedient(
				entitatActual.getId(),
				oneOfSelectedExpedients.getMetaExpedient().getId());
		ExpedientEstatDto expedientEstatObert = new ExpedientEstatDto();
		expedientEstatObert.setNom("OBERT");
		expedientEstats.add(0, expedientEstatObert);
		model.addAttribute(
				"expedientEstats",
				expedientEstats);
		
		
		ExpedientMassiuCanviEstatCommand command = new ExpedientMassiuCanviEstatCommand();
		model.addAttribute(command);
	
		
		return "expedientMassiuCanviEstatForm";
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/canviarEstat", method = RequestMethod.POST)
	public String canviarEstatPost(
			HttpServletRequest request,
			ExpedientMassiuCanviEstatCommand command,
			BindingResult bindingResult,
			Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			return "expedientEstatsForm";
		}
		
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				getSessionAttributeSelecio(request));
		
		
		int errors = 0;
		int correctes = 0;
		Date dataInici = new Date();
		List<ExecucioMassivaContingutDto> execucioMassivaElements = new ArrayList<>();
		
		for (Long expedientId : seleccio) {
			Date dataIniciElement = new Date();
			Throwable throwable = null;
			
			try {
				expedientEstatService.changeExpedientEstat(
						entitatActual.getId(),
						expedientId,
						command.getExpedientEstatId());
				correctes++;
				
			} catch (Exception e) {
				log.error("Error al canviar estat de expedient massiu amb id=" + expedientId, e);
				errors++;
				throwable = ExceptionHelper.getRootCauseOrItself(e);
			}
			
			execucioMassivaElements.add(
					new ExecucioMassivaContingutDto(
							dataIniciElement,
							new Date(),
							expedientId,
							throwable));
		}
		
		
		execucioMassivaService.saveExecucioMassiva(
				entitatActual.getId(),
				new ExecucioMassivaDto(
						ExecucioMassivaTipusDto.CANVI_ESTAT,
						dataInici,
						new Date(),
						RolHelper.getRolActual(request)),
				execucioMassivaElements, 
				ElementTipusEnumDto.EXPEDIENT);
		
		if (correctes > 0) {
			MissatgesHelper.success(request, getMessage(request, "expedient.controller.estatsModificats.ok", new Object[]{correctes}));
		}  
		if (errors > 0) {
			MissatgesHelper.error(request, getMessage(request, "expedient.controller.estatsModificats.error", new Object[]{errors}), null);
		} 
		
		return modalUrlTancar();
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

}