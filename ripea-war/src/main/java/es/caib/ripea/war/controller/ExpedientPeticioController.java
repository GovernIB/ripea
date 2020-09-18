/**
 * 
 */
package es.caib.ripea.war.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioAccioEnumDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioEstatViewEnumDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.RegistreAnnexDto;
import es.caib.ripea.core.api.dto.RegistreAnnexEstatEnumDto;
import es.caib.ripea.core.api.service.EntitatService;
import es.caib.ripea.core.api.service.ExpedientPeticioService;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.command.ExpedientPeticioAcceptarCommand;
import es.caib.ripea.war.command.ExpedientPeticioFiltreCommand;
import es.caib.ripea.war.command.ExpedientPeticioRebutjarCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;

/**
 * Controlador per al llistat d'expedients peticions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/expedientPeticio")
public class ExpedientPeticioController extends BaseUserController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "ExpedientPeticioController.session.filtre";

	@Autowired
	private ExpedientPeticioService expedientPeticioService;
	@Autowired
	private EntitatService entitatService;
	@Autowired
	private MetaExpedientService metaExpedientService;	
	@Autowired
	private ExpedientService expedientService;		

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(
				getFiltreCommand(request));
		return "expedientPeticioList";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			ExpedientPeticioFiltreCommand filtreCommand,
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
		return "redirect:expedientPeticio";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ExpedientPeticioFiltreCommand expedientPeticioFiltreCommand = getFiltreCommand(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				expedientPeticioService.findAmbFiltre(
						entitatActual.getId(),
						ExpedientPeticioFiltreCommand.asDto(expedientPeticioFiltreCommand),
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id");
	}

	@RequestMapping(value = "/{registreAnnexId}/{expedientPeticioId}/reintentar", method = RequestMethod.GET)
	public String retryCreateDocFromAnnex(
			HttpServletRequest request,
			@PathVariable Long registreAnnexId,
			@PathVariable Long expedientPeticioId,
			Model model) {
		boolean processatOk = expedientService.retryCreateDocFromAnnex(
				registreAnnexId,
				expedientPeticioId);
		if (processatOk) {
			MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedientPeticio.detalls.controller.reintentat.ok",
							null));
		} else {
			MissatgesHelper.error(
					request,
					getMessage(
							request, 
							"expedientPeticio.detalls.controller.reintentat.error",
							null));
		}
		return "redirect:/modal/expedientPeticio/" + expedientPeticioId;
	}

	@RequestMapping(value = "/{expedientPeticioId}/reintentarNotificar", method = RequestMethod.GET)
	public String retryNotificarDistribucio(
			HttpServletRequest request,
			@PathVariable Long expedientPeticioId,
			Model model) {
		boolean processatOk = expedientService.retryNotificarDistribucio(expedientPeticioId);
		if (processatOk) {
			MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedientPeticio.detalls.controller.reintentat.notificar.ok",
							null));
		} else {
			MissatgesHelper.error(
					request,
					getMessage(
							request, 
							"expedientPeticio.detalls.controller.reintentat.notificar.error",
							null));
		}
		return "redirect:/modal/expedientPeticio/" + expedientPeticioId;
	}

	@RequestMapping(value = "/{expedientPeticioId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long expedientPeticioId,
			Model model) {
		ExpedientPeticioDto expedientPeticioDto = expedientPeticioService.findOne(expedientPeticioId);
		
		boolean isErrorDocuments = false;
		for (RegistreAnnexDto registreAnnexDto : expedientPeticioDto.getRegistre().getAnnexos()) {
			if (registreAnnexDto.getEstat() == RegistreAnnexEstatEnumDto.PENDENT && registreAnnexDto.getError() != null && !registreAnnexDto.getError().isEmpty()) {
				isErrorDocuments = true;
			}
		}
		if (isErrorDocuments) {
			
			MissatgesHelper.warning(
					request, 
					getMessage(
							request, 
							"expedientPeticio.controller.acceptat.warning"));
		}
		
		model.addAttribute(
				"isErrorDocuments",
				isErrorDocuments);
		
		model.addAttribute(
				"peticio",
				expedientPeticioDto);
		return "expedientPetcioDetall";
	}

	@RequestMapping(value = "/{expedientId}/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse peticionsDatatable(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				expedientPeticioService.findByExpedient(
						entitatActual.getId(),
						expedientId));		
	}

	@RequestMapping(value = "/rebutjar/{expedientPeticioId}", method = RequestMethod.GET)
	public String rebutjarGet(
			HttpServletRequest request,
			@PathVariable Long expedientPeticioId,
			Model model) {
		ExpedientPeticioDto expedientPeticioDto = expedientPeticioService.findOne(expedientPeticioId);
		ExpedientPeticioRebutjarCommand command = new ExpedientPeticioRebutjarCommand();
		command.setId(expedientPeticioId);
		model.addAttribute(
				"expedientPeticioRebutjarCommand",
				command);
		model.addAttribute(
				"expedientPeticioId",
				expedientPeticioDto.getId());
		return "expedientPeticioRebutjar";
	}

	@RequestMapping(value = "/rebutjar", method = RequestMethod.POST)
	public String rebutjarPost(
			HttpServletRequest request,
			@Valid ExpedientPeticioRebutjarCommand command,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute(
					"expedientPeticioRebutjarCommand",
					command);
			model.addAttribute(
					"expedientPeticioId",
					command.getId());
			return "expedientPeticioRebutjar";
		}
		expedientPeticioService.rebutjar(
				command.getId(),
				command.getObservacions());
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:expedientPeticio",
				"expedientPeticio.controller.rebutjat.ok");
	}

	@RequestMapping(value = "/acceptar/{expedientPeticioId}", method = RequestMethod.GET)
	public String acceptar(
			HttpServletRequest request,
			@PathVariable Long expedientPeticioId,
			Model model) {
		ExpedientPeticioDto expedientPeticioDto = expedientPeticioService.findOne(expedientPeticioId);
		ExpedientPeticioAcceptarCommand command = new ExpedientPeticioAcceptarCommand();
		ExpedientDto expedient = null;
		EntitatDto entitat = entitatService.findByUnitatArrel(expedientPeticioDto.getRegistre().getEntitatCodi());
		model.addAttribute(
				"entitatId",
				entitat.getId());
		List<MetaExpedientDto> metaExpedients =  metaExpedientService.findActiusAmbEntitatPerLectura(
				entitat.getId());
		model.addAttribute(
				"metaExpedients",
				metaExpedients);
		MetaExpedientDto metaExpedientDto = expedientPeticioService.findMetaExpedientByEntitatAndProcedimentCodi(
				expedientPeticioDto.getRegistre().getEntitatCodi(),
				expedientPeticioDto.getRegistre().getProcedimentCodi());
		List<ExpedientDto> expedients = null;
		// if exists metaExpedient with matching codi procediment
		if (metaExpedientDto!=null) {
			boolean hasPermissions = false;
			for(MetaExpedientDto metaExpDto : metaExpedients) {
				if (metaExpDto.getId().equals(metaExpedientDto.getId())) {
					hasPermissions = true;
				}
			}
			// if current user has create permissions for this metaexpedient
			if (hasPermissions) {
				command.setMetaExpedientId(metaExpedientDto.getId());
				expedients = (List<ExpedientDto>) expedientService.findByEntitatAndMetaExpedient(entitat.getId(), metaExpedientDto.getId());
				String expedientNumero = expedientPeticioDto.getRegistre().getExpedientNumero();
				if (expedientNumero != null && !expedientNumero.isEmpty()) {
					expedient = expedientPeticioService.findByEntitatAndMetaExpedientAndExpedientNumero(
							entitat.getId(),
							metaExpedientDto.getId(),
							expedientNumero);
					
					if (expedient == null) {
						MissatgesHelper.warning(
								request, 
								getMessage(
										request, 
										"expedientPeticio.form.acceptar.expedient.noTorbat"));
					}
				}
			}
		}
		command.setExpedientPeticioAccioEnumDto(expedient != null ? ExpedientPeticioAccioEnumDto.INCORPORAR : ExpedientPeticioAccioEnumDto.CREAR);
		model.addAttribute(
				"accio",
				expedient != null ? ExpedientPeticioAccioEnumDto.INCORPORAR : ExpedientPeticioAccioEnumDto.CREAR);
		command.setExpedientId(expedient != null ? expedient.getId() : null);
		model.addAttribute(
				"expedients",
				expedients);
		model.addAttribute("accios",
				EnumHelper.getOptionsForEnum(ExpedientPeticioAccioEnumDto.class,
						"expedient.peticio.accio.enum."));
		command.setId(expedientPeticioDto.getId());
		command.setAssociarInteressats(true);
//		command.setNewExpedientTitol(expedientPeticioDto.getIdentificador());
		command.setAny(Calendar.getInstance().get(Calendar.YEAR));
		model.addAttribute(
				"expedientPeticioAcceptarCommand",
				command);
		return "expedientPeticioAccept";

	}

	@RequestMapping(value = "/expedients/{entitatId}/{metaExpedientId}", method = RequestMethod.GET)
	@ResponseBody
	public List<ExpedientDto> findExpedientEstatByMetaExpedient(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			@PathVariable Long metaExpedientId,
			Model model) {
		return (List<ExpedientDto>) expedientService.findByEntitatAndMetaExpedient(entitatId, metaExpedientId);
	}

	@RequestMapping(value = "/acceptar/{expedientPeticioId}", method = RequestMethod.POST)
	public String acceptarPost(
			HttpServletRequest request,
			@Valid ExpedientPeticioAcceptarCommand command,
			@PathVariable Long expedientPeticioId,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute(
			"expedientPeticioAcceptarCommand",
			command);
			return "expedientPeticioAcceptNoExp";
		}
		boolean processatOk = true;
		ExpedientPeticioDto expedientPeticioDto = expedientPeticioService.findOne(expedientPeticioId);
		EntitatDto entitat = entitatService.findByUnitatArrel(expedientPeticioDto.getRegistre().getEntitatCodi());
		if (command.getExpedientPeticioAccioEnumDto() == ExpedientPeticioAccioEnumDto.CREAR) {
			ExpedientDto expedientDto = expedientService.create(
					entitat.getId(),
					command.getMetaExpedientId(),
					null,
					null,
					command.getAny(),
					null,
					command.getNewExpedientTitol(),
					expedientPeticioDto.getId(),
					command.isAssociarInteressats(),
					null);
			processatOk = expedientDto.isProcessatOk();
		} else if (command.getExpedientPeticioAccioEnumDto() == ExpedientPeticioAccioEnumDto.INCORPORAR) {
			processatOk = expedientService.incorporar(
					entitat.getId(),
					command.getExpedientId(),
					expedientPeticioDto.getId(),
					command.isAssociarInteressats());
		}
		
		if (!processatOk) {
			MissatgesHelper.warning(
					request, 
					getMessage(
							request, 
							"expedientPeticio.controller.acceptat.warning"));
		}
		
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:expedientPeticio",
				"expedientPeticio.controller.acceptat.ok");
	}

	@RequestMapping(value = "/descarregarAnnex/{annexId}", method = RequestMethod.GET)
	public String descarregarAnnex(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long annexId) throws IOException {
		try{
			FitxerDto fitxer = expedientPeticioService.getAnnexContent(annexId);
			writeFileToResponse(
					fitxer.getNom(),
					fitxer.getContingut(),
					response);
		} catch (Exception ex) {
			logger.error("Error descarregant el document", ex);
			return getModalControllerReturnValueError(
					request,
					"/expedientPeticio",
					"contingut.controller.document.descarregar.error");
		}
		return null;
	}

	@RequestMapping(value = "/firmaInfo/{annexId}", method = RequestMethod.GET)
	public String firmaInfo(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long annexId,
			Model model) {
		try {
			RegistreAnnexDto registreAnnexDto = expedientPeticioService.findAnnexById(
					annexId);
			model.addAttribute(
					"annexId",
					registreAnnexDto.getId());

			model.addAttribute(
					"firmes",
					expedientPeticioService.annexFirmaInfo(
							registreAnnexDto.getUuid()));

		} catch (Exception ex) {
			logger.error(
					"Error recuperant informació de firma",
					ex);
			model.addAttribute(
					"missatgeError",
					ex.getMessage());
			return "ajaxErrorPage";
		}
		return "registreAnnexFirmes";
	}

	@RequestMapping(value = "/descarregarFirma/{annexId}", method = RequestMethod.GET)
	public String descarregarFirma(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long annexId ) throws IOException {
		FitxerDto fitxer = expedientPeticioService.getAnnexFirmaContingut(annexId);
		writeFileToResponse(
				fitxer.getNom(),
				fitxer.getContingut(),
				response);
		return null;
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}

	/**
	 * Gets filtreCommand from session, if it doesnt exist it creates new one in session
	 * @param request
	 * @return 
	 */
	private ExpedientPeticioFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		ExpedientPeticioFiltreCommand filtreCommand = (ExpedientPeticioFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new ExpedientPeticioFiltreCommand();
			filtreCommand.setEstat(ExpedientPeticioEstatViewEnumDto.PENDENT);
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientPeticioController.class);

}
