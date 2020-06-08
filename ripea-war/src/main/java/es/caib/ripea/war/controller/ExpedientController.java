/**
 * 
 */
package es.caib.ripea.war.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.WebUtils;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientComentariDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientEstatDto;
import es.caib.ripea.core.api.dto.ExpedientEstatEnumDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.MetaExpedientDominiDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.DocumentEnviamentService;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.command.ContenidorCommand.Create;
import es.caib.ripea.war.command.ContenidorCommand.Update;
import es.caib.ripea.war.command.ExpedientCommand;
import es.caib.ripea.war.command.ExpedientFiltreCommand;
import es.caib.ripea.war.command.ExpedientTancarCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;

/**
 * Controlador per al llistat d'expedients dels usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/expedient")
public class ExpedientController extends BaseUserController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "ExpedientUserController.session.filtre";
	private static final String SESSION_ATTRIBUTE_SELECCIO = "ExpedientUserController.session.seleccio";
	private static final String SESSION_ATTRIBUTE_METAEXP_ID = "ExpedientUserController.session.metaExpedient.id";
	private static final String COOKIE_MEUS_EXPEDIENTS = "meus_expedients";

	private static final String SESSION_ATTRIBUTE_RELACIONAR_FILTRE = "ExpedientUserController.session.relacionar.filtre";
	@Autowired
	private ContingutService contingutService;
	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private MetaExpedientService metaExpedientService;
	@Autowired
	private DocumentEnviamentService documentEnviamentService;
	@Autowired
	private AplicacioService aplicacioService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			@CookieValue(value = COOKIE_MEUS_EXPEDIENTS, defaultValue = "false") boolean meusExpedients,
			HttpServletRequest request,
			Model model) {
		Boolean mantenirPaginacio = Boolean.parseBoolean(request.getParameter("mantenirPaginacio"));
		if (mantenirPaginacio) {
			model.addAttribute("mantenirPaginacio", true);
		} else {
			model.addAttribute("mantenirPaginacio", false);
		}
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		List<MetaExpedientDto> metaExpedientsPermisLectura = metaExpedientService.findActiusAmbEntitatPerLectura(
				entitatActual.getId());
		model.addAttribute(
				"metaExpedientsPermisLectura",
				metaExpedientsPermisLectura);
		List<MetaExpedientDto> metaExpedientsPermisCreacio = metaExpedientService.findActiusAmbEntitatPerCreacio(
				entitatActual.getId());
		model.addAttribute(
				"metaExpedientsPermisCreacio",
				metaExpedientsPermisCreacio);
		model.addAttribute(
				getFiltreCommand(request));
		model.addAttribute(
				"seleccio",
				RequestSessionHelper.obtenirObjecteSessio(
						request,
						SESSION_ATTRIBUTE_SELECCIO));
		//putting enums from ExpedientEstatEnumDto and ExpedientEstatDto into one class, need to have all estats from enums and database in one class 
		List<ExpedientEstatDto> expedientEstatsOptions = new ArrayList<>();
		Long metaExpedientId = (Long)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_METAEXP_ID);
		expedientEstatsOptions.add(new ExpedientEstatDto(getMessage(request, "expedient.estat.enum." + ExpedientEstatEnumDto.values()[0].name()), Long.valueOf(0)));
		expedientEstatsOptions.addAll(expedientService.findExpedientEstatByMetaExpedient(entitatActual.getId(), metaExpedientId));
		expedientEstatsOptions.add(new ExpedientEstatDto(getMessage(request, "expedient.estat.enum." + ExpedientEstatEnumDto.values()[1].name()), Long.valueOf(-1)));
		model.addAttribute(
				"expedientEstatsOptions",
				expedientEstatsOptions);
		model.addAttribute("nomCookieMeusExpedients", COOKIE_MEUS_EXPEDIENTS);
		model.addAttribute("meusExpedients", meusExpedients);
		model.addAttribute("metaExpedientDominisOptions", metaExpedientService.dominiFindByMetaExpedient(entitatActual.getId(), metaExpedientId));
		model.addAttribute("convertirDefinitiu", Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.conversio.definitiu")));
		if (metaExpedientsPermisLectura == null || metaExpedientsPermisLectura.size() <= 0) {
			MissatgesHelper.warning(
					request, 
					getMessage(
							request, 
							"expedient.controller.sense.permis.lectura"));
		}
		return "expedientUserList";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid ExpedientFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model,
			@RequestParam(value = "accio", required = false) String accio) {
		getEntitatActualComprovantPermisos(request);
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
				Long metaExpedientId = (Long)RequestSessionHelper.obtenirObjecteSessio(
						request,
						SESSION_ATTRIBUTE_METAEXP_ID);
				if (metaExpedientId == null || !metaExpedientId.equals(filtreCommand.getMetaExpedientId())) {
					RequestSessionHelper.esborrarObjecteSessio(
							request,
							SESSION_ATTRIBUTE_SELECCIO);
					RequestSessionHelper.actualitzarObjecteSessio(
							request,
							SESSION_ATTRIBUTE_METAEXP_ID,
							filtreCommand.getMetaExpedientId());
				}
			}
		}
		return "redirect:expedient";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ExpedientFiltreCommand filtreCommand = getFiltreCommand(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				expedientService.findAmbFiltreUser(
						entitatActual.getId(),
						ExpedientFiltreCommand.asDto(filtreCommand),
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id",
				SESSION_ATTRIBUTE_SELECCIO);
	}

	@RequestMapping(value = "/select", method = RequestMethod.GET)
	@ResponseBody
	public int select(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) Long[] ids) {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO,
					seleccio);
		}
		if (ids != null) {
			for (Long id: ids) {
				seleccio.add(id);
			}
		} else {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			ExpedientFiltreCommand filtreCommand = getFiltreCommand(request);
			seleccio.addAll(
					expedientService.findIdsAmbFiltre(
							entitatActual.getId(),
							ExpedientFiltreCommand.asDto(filtreCommand)));
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
				SESSION_ATTRIBUTE_SELECCIO);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO,
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

	@RequestMapping(value = "/export/{format}", method = RequestMethod.GET)
	public String export(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable String format) throws IOException {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		ExpedientFiltreCommand command = getFiltreCommand(request);
		if (seleccio == null || seleccio.isEmpty() || command == null) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"expedient.controller.exportacio.seleccio.buida"));
			return "redirect:../../expedient";
		} else {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			FitxerDto fitxer = expedientService.exportacio(
					entitatActual.getId(),
					command.getMetaExpedientId(),
					seleccio,
					format);
			writeFileToResponse(
					fitxer.getNom(),
					fitxer.getContingut(),
					response);
			return null;
		}
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		model.addAttribute("mantenirPaginacio", true);
		return get(request, null, model);
	}
	@RequestMapping(value = "/{expedientId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ExpedientDto expedient = null;
		if (expedientId != null) {
			expedient = expedientService.findById(
					entitatActual.getId(),
					expedientId);
		}
		ExpedientCommand command = null;
		if (expedient != null) {
			command = ExpedientCommand.asCommand(expedient);
		} else {
			command = new ExpedientCommand();
			command.setAny(Calendar.getInstance().get(Calendar.YEAR));
		}
		command.setEntitatId(entitatActual.getId());
		model.addAttribute(command);
		model.addAttribute(
				"metaExpedients",
				metaExpedientService.findActiusAmbEntitatPerCreacio(entitatActual.getId()));
		return "contingutExpedientForm";
	}
	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String postNew(
			HttpServletRequest request,
			@Validated({Create.class}) ExpedientCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			model.addAttribute(
					"metaExpedients",
					metaExpedientService.findActiusAmbEntitatPerCreacio(entitatActual.getId()));
			return "contingutExpedientForm";
		}
		try {
			ExpedientDto expedientDto = expedientService.create(
					entitatActual.getId(),
					command.getMetaNodeId(),
					command.getMetaNodeDominiId(),
					null,
					command.getAny(),
					null,
					command.getNom(),
					null,
					false);
			model.addAttribute("redirectUrlAfterClosingModal", "contingut/" + expedientDto.getId());
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../expedient",
					"expedient.controller.creat.ok");
		} catch (Exception exception) {
			MissatgesHelper.error(request, exception.getMessage());
			model.addAttribute(
					"metaExpedients",
					metaExpedientService.findActiusAmbEntitatPerCreacio(entitatActual.getId()));
			return "contingutExpedientForm";
		}
	}
	@RequestMapping(value = "/{expedientId}/update", method = RequestMethod.POST)
	public String postUpdate(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@Validated({Update.class}) ExpedientCommand command,
			BindingResult bindingResult,
			Model model) {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			model.addAttribute(
					"metaExpedients",
					metaExpedientService.findActiusAmbEntitatPerCreacio(entitatActual.getId()));
			return "contingutExpedientForm";
		}
		try {
			expedientService.update(
					entitatActual.getId(),
					command.getId(),
					command.getNom(),
					command.getAny(),
					command.getMetaNodeDominiId());
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../expedient",
					"expedient.controller.modificat.ok");
		} catch (Exception exception) {
			MissatgesHelper.error(request, exception.getMessage());
			model.addAttribute(
					"metaExpedients",
					metaExpedientService.findActiusAmbEntitatPerCreacio(entitatActual.getId()));
			return "contingutExpedientForm";
		}		
	}
	
	
	@RequestMapping(value = "/metaExpedient/{metaExpedientId}/proximNumeroSequencia/{any}", method = RequestMethod.GET)
	@ResponseBody
	public long proximNumeroSequencia(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable int any) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return metaExpedientService.getProximNumeroSequencia(
				entitatActual.getId(),
				metaExpedientId,
				any);
	}
	
	@RequestMapping(value = "/metaExpedient/{metaExpedientId}/findMetaExpedientDominis", method = RequestMethod.GET)
	@ResponseBody
	public List<MetaExpedientDominiDto> findMetaExpedientDominis(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return metaExpedientService.dominiFindByMetaExpedient(
				entitatActual.getId(), 
				metaExpedientId);
	}

	@RequestMapping(value = "/{expedientId}/agafar", method = RequestMethod.GET)
	public String agafar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@RequestParam(required = false) String contingutId,
			Model model) {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		expedientService.agafarUser(
				entitatActual.getId(),
				expedientId);
		String url;
		if (contingutId != null) {
			url = "redirect:../../contingut/" + contingutId;
		} else {
			url = "redirect:../../contingut/" + expedientId;
		}
		return getAjaxControllerReturnValueSuccess(
				request,
				url,
				"expedient.controller.agafat.ok");
	}

	@RequestMapping(value = "/{expedientId}/comentaris", method = RequestMethod.GET)
	public String comentaris(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"contingut",
				contingutService.findAmbIdUser(
						entitatActual.getId(),
						expedientId,
						true,
						false));
		
		boolean hasWritePermisions = expedientService.hasWritePermission(expedientId);
		model.addAttribute(
				"hasWritePermisions",
				hasWritePermisions);
		
		UsuariDto usuariActual = aplicacioService.getUsuariActual();
		model.addAttribute(
				"usuariActual",
				usuariActual);
		
		return "expedientComentaris";
	}	

	@RequestMapping(value = "/{contingutId}/comentaris/publicar", method = RequestMethod.POST)
	@ResponseBody
	public List<ExpedientComentariDto> publicarComentari(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@RequestParam String text,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		if (text != null && !text.isEmpty()) {
			expedientService.publicarComentariPerExpedient(entitatActual.getId(), contingutId, text);
		}
			
		return expedientService.findComentarisPerContingut(
				entitatActual.getId(), 
				contingutId);
	}

	@RequestMapping(value = "/{expedientId}/alliberar", method = RequestMethod.GET)
	public String alliberar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		expedientService.alliberarUser(
				entitatActual.getId(),
				expedientId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../contingut/" + expedientId,
				"expedient.controller.alliberat.ok");
	}

	@RequestMapping(value = "/{expedientId}/tancar", method = RequestMethod.GET)
	public String expedientTancarGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ExpedientTancarCommand command = new ExpedientTancarCommand();
		command.setId(expedientId);
		model.addAttribute(command);
		model.addAttribute(
				"expedient",
				contingutService.findAmbIdUser(
						entitatActual.getId(),
						expedientId,
						true,
						false));
		return "expedientTancarForm";
	}
	@RequestMapping(value = "/{expedientId}/tancar", method = RequestMethod.POST)
	public String expedientTancarPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@Valid ExpedientTancarCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			model.addAttribute(
					"expedient",
					contingutService.findAmbIdUser(
							entitatActual.getId(),
							expedientId,
							true,
							false));
			return "expedientTancarForm";
		}
		expedientService.tancar(
				entitatActual.getId(),
				expedientId,
				command.getMotiu());
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../contingut/" + expedientId,
				"expedient.controller.tancar.ok");
	}

	@RequestMapping(value = "/estatValues/{metaExpedientId}", method = RequestMethod.GET)
	@ResponseBody
	public List<ExpedientEstatDto> findExpedientEstatByMetaExpedient(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		List<ExpedientEstatDto> expedientEstatsOptions = new ArrayList<>();
		List<ExpedientEstatDto> estatsFromDatabase = expedientService.findExpedientEstatByMetaExpedient(
				entitatActual.getId(),
				metaExpedientId);
		expedientEstatsOptions.add(new ExpedientEstatDto(ExpedientEstatEnumDto.values()[0].name(), Long.valueOf(0)));
		expedientEstatsOptions.addAll(estatsFromDatabase);
		expedientEstatsOptions.add(new ExpedientEstatDto(ExpedientEstatEnumDto.values()[1].name(), Long.valueOf(-1)));		
		return expedientEstatsOptions;
	}

	@RequestMapping(value = "/{expedientId}/canviarEstat", method = RequestMethod.GET)
	public String canviarEstatGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ExpedientDto expedient = null;
		if (expedientId != null) {
			expedient = expedientService.findById(
					entitatActual.getId(),
					expedientId);
		}
		List<ExpedientEstatDto> expedientEstats = expedientService.findExpedientEstats(
				entitatActual.getId(),
				expedientId);
		ExpedientEstatDto expedientEstatObert = new ExpedientEstatDto();
		expedientEstatObert.setNom("OBERT");
		expedientEstats.add(0, expedientEstatObert);
		ExpedientCommand command = null;
		if (expedient != null) {
			command = ExpedientCommand.asCommand(expedient);
			if (expedient.getExpedientEstatNextInOrder() != null) {
				command.setExpedientEstatId(expedient.getExpedientEstatNextInOrder());
			} else { // if the state is obert
				if (expedientEstats.size() > 1) { // if there are custom states to choose from
					command.setExpedientEstatId(expedientEstats.get(1).getId());
				} else {
					command.setExpedientEstatId(null);
				}

			}
			
		} else {
			command = new ExpedientCommand();
		}
		command.setEntitatId(entitatActual.getId());
		model.addAttribute(command);
		model.addAttribute(
				"expedientEstats",
				expedientEstats);
		return "expedientChooseEstatForm";
	}

	@RequestMapping(value = "/canviarEstat", method = RequestMethod.POST)
	public String canviarEstatPost(
			HttpServletRequest request,
			ExpedientCommand command,
			BindingResult bindingResult,
			Model model) {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
//			model.addAttribute(
//					"metaExpedients",
//					metaExpedientService.findActiusAmbEntitatPerCreacio(entitatActual.getId()));
			return "expedientEstatsForm";
		}
		expedientService.changeEstatOfExpedient(
				entitatActual.getId(),
				command.getId(),
				command.getExpedientEstatId()
				);
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../expedient",
				"expedient.controller.estatModificat.ok");
	}

	@RequestMapping(value = "/{expedientId}/relacionarList", method = RequestMethod.GET)
	public String expedientRelacionarGetList(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"expedient",
				contingutService.findAmbIdUser(
						entitatActual.getId(),
						expedientId,
						true,
						false));
		model.addAttribute("expedientId", expedientId);
		ExpedientFiltreCommand filtre = new ExpedientFiltreCommand();
		model.addAttribute(filtre);
		model.addAttribute(
				"metaExpedients",
				metaExpedientService.findActiusAmbEntitatPerLectura(
						entitatActual.getId()));
		model.addAttribute(
				"expedientEstatEnumOptions",
				EnumHelper.getOptionsForEnum(
						ExpedientEstatEnumDto.class,
						"expedient.estat.enum."));
		List<MetaExpedientDto> metaExpedientsPermisLectura = metaExpedientService.findActiusAmbEntitatPerLectura(
				entitatActual.getId());
		model.addAttribute(
				"metaExpedientsPermisLectura",
				metaExpedientsPermisLectura);
		ExpedientFiltreCommand expedientFiltreCommand = getRelacionarFiltreCommand(request);
//		Long metaExpedientId = null;
//		if (expedientFiltreCommand != null) {
//			metaExpedientId = expedientFiltreCommand.getMetaExpedientId();
//		}
		model.addAttribute(
				"expedientFiltreCommand",
				expedientFiltreCommand);
		//putting enums from ExpedientEstatEnumDto and ExpedientEstatDto into one class, need to have all estats from enums and database in one type 
		List<ExpedientEstatDto> expedientEstatsOptions = new ArrayList<>();
		expedientEstatsOptions.add(new ExpedientEstatDto(ExpedientEstatEnumDto.values()[0].name(), Long.valueOf(0)));
		expedientEstatsOptions.addAll(expedientService.findExpedientEstatByMetaExpedient(entitatActual.getId(), expedientFiltreCommand.getMetaExpedientId()));
		expedientEstatsOptions.add(new ExpedientEstatDto(ExpedientEstatEnumDto.values()[1].name(), Long.valueOf(-1)));
		model.addAttribute(
				"expedientEstatsOptions",
				expedientEstatsOptions);
		if (metaExpedientsPermisLectura == null || metaExpedientsPermisLectura.size() <= 0) {
			MissatgesHelper.warning(
					request, 
					getMessage(
							request, 
							"expedient.controller.sense.permis.lectura"));
		}
		return "expedientRelacionarForm";
	}

	@RequestMapping(value = "/{expedientId}/relacionarList", method = RequestMethod.POST)
	public String expedientRelacionarPostList(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@Valid ExpedientFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model,
			@RequestParam(value = "accio", required = false) String accio) {
		getEntitatActualComprovantPermisos(request);
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_RELACIONAR_FILTRE);
		} else {
			if (!bindingResult.hasErrors()) {
				RequestSessionHelper.actualitzarObjecteSessio(
						request,
						SESSION_ATTRIBUTE_RELACIONAR_FILTRE,
						filtreCommand);
			}
		}
		return "redirect:/modal/expedient/"+expedientId+"/relacionarList";
	}

	@RequestMapping(value = "/{expedientId}/relacionar/{relacionatId}", method = RequestMethod.GET)
	public String expedientRelacionar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long relacionatId) throws IOException {

		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

			expedientService.relacioCreate(
					entitatActual.getId(),
					expedientId,
					relacionatId);
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/../../contingut/" + expedientId,
					"expedient.controller.relacionat.ok");
			
		} catch (Exception e) {
			return getModalControllerReturnValueErrorMessageText(
					request,
					"redirect:../../esborrat",
					e.getMessage());

		}
	}

	@RequestMapping(value = "/{expedientId}/relacio/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse relacioDatatable(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {

		ExpedientFiltreCommand filtreCommand = getRelacionarFiltreCommand(request);
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				expedientService.findAmbFiltreNoRelacionat(
						entitatActual.getId(), 
						ExpedientFiltreCommand.asDto(filtreCommand), 
						expedientId,
						DatatablesHelper.getPaginacioDtoFromRequest(request)));		
	}

	@RequestMapping(value = "/{expedientId}/relacio/{relacionatId}/delete", method = RequestMethod.GET)
	public String expedientRelacioDelete(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long relacionatId,
			Model model) {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (expedientService.relacioDelete(
				entitatActual.getId(),
				expedientId,
				relacionatId)) {
			MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.controller.relacio.esborrada.ok"));
		} else {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"expedient.controller.relacio.esborrada.error"));
		}
		return "redirect:/contingut/" + expedientId;
	}



	@RequestMapping(value = "/{expedientId}/enviament/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse enviamentDatatable(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				documentEnviamentService.findAmbExpedient(
						entitatActual.getId(),
						expedientId));		
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}

	private ExpedientFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		ExpedientFiltreCommand filtreCommand = (ExpedientFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new ExpedientFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		Cookie cookie = WebUtils.getCookie(request, COOKIE_MEUS_EXPEDIENTS);
		filtreCommand.setMeusExpedients(cookie != null && "true".equals(cookie.getValue()));
		return filtreCommand;
	}

	private ExpedientFiltreCommand getRelacionarFiltreCommand(
			HttpServletRequest request) {
		ExpedientFiltreCommand filtreCommand = (ExpedientFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_RELACIONAR_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new ExpedientFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_RELACIONAR_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}

}
