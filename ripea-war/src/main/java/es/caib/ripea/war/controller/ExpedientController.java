/**
 * 
 */
package es.caib.ripea.war.controller;

import es.caib.ripea.core.api.dto.CodiValorDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEnviamentInteressatDto;
import es.caib.ripea.core.api.dto.DocumentEnviamentTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientComentariDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientEstatDto;
import es.caib.ripea.core.api.dto.ExpedientEstatEnumDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.GrupDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.OrganismeDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PermissionEnumDto;
import es.caib.ripea.core.api.dto.RespostaPublicacioComentariDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.exception.ArxiuJaGuardatException;
import es.caib.ripea.core.api.exception.ExpedientTancarSenseDocumentsDefinitiusException;
import es.caib.ripea.core.api.exception.FirmaServidorException;
import es.caib.ripea.core.api.exception.PermissionDeniedException;
import es.caib.ripea.core.api.exception.SistemaExternException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.DocumentEnviamentService;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.service.ExpedientEstatService;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.api.service.GrupService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.core.api.service.OrganGestorService;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.war.command.ContenidorCommand.Create;
import es.caib.ripea.war.command.ContenidorCommand.Update;
import es.caib.ripea.war.command.ExpedientAssignarCommand;
import es.caib.ripea.war.command.ExpedientCommand;
import es.caib.ripea.war.command.ExpedientFiltreCommand;
import es.caib.ripea.war.command.ExpedientTancarCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.EntitatHelper;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.ExceptionHelper;
import es.caib.ripea.war.helper.JsonResponse;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;
import es.caib.ripea.war.helper.RolHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Controlador per al llistat d'expedients dels usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/expedient")
public class ExpedientController extends BaseUserOAdminOOrganController {

	public static final String SESSION_ATTRIBUTE_FILTRE = "ExpedientUserController.session.filtre";
	public static final String SESSION_ATTRIBUTE_SELECCIO = "ExpedientUserController.session.seleccio";
	public static final String SESSION_ATTRIBUTE_RELACIONAR_SELECCIO = "ExpedientUserController.relacionar.session.seleccio";
	private static final String SESSION_ATTRIBUTE_METAEXP_ID = "ExpedientUserController.session.metaExpedient.id";
	private static final String COOKIE_MEUS_EXPEDIENTS = "meus_expedients";
	private static final String COOKIE_FIRMA_PENDENT = "firma_pendent";
	
	private static final String SESSION_ATTRIBUTE_RELACIONAR_FILTRE = "ExpedientUserController.session.relacionar.filtre";
	private static final String SESSION_ATTRIBUTE_RELACIONATS_FILTRE = "ExpedientUserController.session.relacionats.filtre";
	
	@Autowired
	private ContingutService contingutService;
	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private DocumentService documentService;
	@Autowired
	private MetaExpedientService metaExpedientService;
	@Autowired
	private DocumentEnviamentService documentEnviamentService;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private ExpedientEstatService expedientEstatService;
	@Autowired
	private OrganGestorService organGestorService;
	@Autowired
	private GrupService grupService;

	
	@RequestMapping(method = RequestMethod.GET)
	public String get(
			@CookieValue(value = COOKIE_MEUS_EXPEDIENTS, defaultValue = "false") boolean meusExpedients,
			@CookieValue(value = COOKIE_FIRMA_PENDENT, defaultValue = "false") boolean firmaPendent,
			HttpServletRequest request,
			Model model) {
		
		long t0 = System.currentTimeMillis();

		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		
		ExpedientFiltreCommand filtreCommand = getFiltreCommand(request);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		if (aplicacioService.mostrarLogsRendiment())
			logger.info("ExpedientController.get start ( entitatId=" + entitatActual.getId() + " rolActual=" + rolActual + " filtreOrganId=" + filtreCommand.getOrganGestorId() + ")");
		
		long t2 = System.currentTimeMillis();
		model.addAttribute(
				"rolActual",
				rolActual);
		
		model.addAttribute(
				"usuariActual",
				aplicacioService.getUsuariActual());

		model.addAttribute(
				"hasCreatePermissionForAnyProcediment",
				metaExpedientService.hasPermissionForAnyProcediment(
						entitatActual.getId(),
						rolActual,
						PermissionEnumDto.CREATE));
		model.addAttribute(
				filtreCommand);
		model.addAttribute(
				"seleccio",
				RequestSessionHelper.obtenirObjecteSessio(
						request,
						SESSION_ATTRIBUTE_SELECCIO));
		if (aplicacioService.mostrarLogsRendiment())
			logger.info("findActiusAmbEntitatPerCreacio time:  " + (System.currentTimeMillis() - t2) + " ms");
		
		long t3 = System.currentTimeMillis();
		//putting enums from ExpedientEstatEnumDto and ExpedientEstatDto into one class, need to have all estats from enums and database in one class 
		List<ExpedientEstatDto> expedientEstatsOptions = new ArrayList<>();
		Long metaExpedientId = (Long)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_METAEXP_ID);
		expedientEstatsOptions.add(new ExpedientEstatDto(getMessage(request, "expedient.estat.enum." + ExpedientEstatEnumDto.values()[0].name()), Long.valueOf(0)));
		expedientEstatsOptions.addAll(expedientEstatService.findExpedientEstatsByMetaExpedient(entitatActual.getId(), metaExpedientId));
		expedientEstatsOptions.add(new ExpedientEstatDto(getMessage(request, "expedient.estat.enum." + ExpedientEstatEnumDto.values()[1].name()), Long.valueOf(-1)));
		model.addAttribute(
				"expedientEstatsOptions",
				expedientEstatsOptions);
		model.addAttribute("nomCookieMeusExpedients", COOKIE_MEUS_EXPEDIENTS);
		model.addAttribute("meusExpedients", meusExpedients);
		model.addAttribute("convertirDefinitiu", Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.conversio.definitiu")));
		
		model.addAttribute("isDominisEnabled", aplicacioService.propertyBooleanFindByKey("es.caib.ripea.habilitar.dominis"));
		model.addAttribute("nomCookieFirmaPendent", COOKIE_FIRMA_PENDENT);
		model.addAttribute("firmaPendent", firmaPendent);
		
		String separador = aplicacioService.propertyFindByNom("es.caib.ripea.numero.expedient.separador");
		model.addAttribute("separadorDefinit", (separador != null && ! separador.equals("/") ? true : false));
		
		model.addAttribute("isExportacioExcelActiva", Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.expedient.exportacio.excel")));
		model.addAttribute("isExportacioInsideActiva", Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.expedient.exportar.inside")));
		
		model.addAttribute("grups", grupService.findGrupsPermesosProcedimentsGestioActiva(entitatActual.getId(), rolActual, RolHelper.isRolActualAdministradorOrgan(request) ? EntitatHelper.getOrganGestorActualId(request) : null));
		
		if (!expedientService.hasReadPermissionsAny(rolActual, entitatActual.getId())) {
			MissatgesHelper.warning(
					request, 
					getMessage(
							request, 
							"expedient.controller.sense.permis.lectura"));
		}
		if (aplicacioService.mostrarLogsRendiment())
			logger.info("findEstats time:  " + (System.currentTimeMillis() - t3) + " ms");
		
		if (aplicacioService.mostrarLogsRendiment())
			logger.info("ExpedientController.get end " + (System.currentTimeMillis() - t0) + " ms");
		
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
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_METAEXP_ID);
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
		
		long t0 = System.currentTimeMillis();
		if (aplicacioService.mostrarLogsRendiment())
			logger.info("ExpedientController.datatable start");
		
		PaginaDto<ExpedientDto> pagina = expedientService.findAmbFiltreUser(
				entitatActual.getId(),
				ExpedientFiltreCommand.asDto(filtreCommand),
				DatatablesHelper.getPaginacioDtoFromRequest(request), 
				RolHelper.getRolActual(request));
		
		if (aplicacioService.mostrarLogsRendiment())
			logger.info("ExpedientController.datatable end " + (System.currentTimeMillis() - t0) + " ms");
		
		return DatatablesHelper.getDatatableResponse(
				request,
				pagina,
				"id",
				SESSION_ATTRIBUTE_SELECCIO);

	}

	
	@RequestMapping(value = "/select", method = RequestMethod.GET)
	@ResponseBody
	public int select(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) Long[] ids) {
		
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		
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
							ExpedientFiltreCommand.asDto(filtreCommand), rolActual));
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
							"expedient.controller.exportacio.seleccio.buida"),
					null);
			return "redirect:../../expedient";
		} else {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			FitxerDto fitxer = expedientService.exportacio(
					entitatActual.getId(),
					seleccio,
					format);
			writeFileToResponse(
					fitxer.getNom(),
					fitxer.getContingut(),
					response);
			return null;
		}
	}
	
	@RequestMapping(value = "/{expedientId}/generarIndex/{format}", method = RequestMethod.GET)
	public void generarIndex(
			@PathVariable Long expedientId,
			@PathVariable String format,
			HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		if (! isExportacioExcelActiva() && format != null && format.equals("XLSX"))
			throw new SecurityException("És necessari activar la propietat 'es.caib.ripea.expedient.exportacio.excel' per realitzar la exportació a excel");
		
		FitxerDto fitxer = expedientService.exportIndexExpedient(
				entitatActual.getId(),
				new HashSet<>(Arrays.asList(expedientId)),
				false,
				format);

		response.setHeader("Set-cookie", "contentLoaded=true; path=/");
		
		writeFileToResponse(
				fitxer.getNom(),
				fitxer.getContingut(),
				response);
	}

	@RequestMapping(value = "/{expedientId}/generarExportarIndex", method = RequestMethod.GET)
	public void generarExportarIndex(
			@PathVariable Long expedientId,
			HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		FitxerDto fitxer = expedientService.exportIndexExpedient(
				entitatActual.getId(),
				new HashSet<>(Arrays.asList(expedientId)),
				true,
				"PDF");

		response.setHeader("Set-cookie", "contentLoaded=true; path=/");
		
		writeFileToResponse(
				fitxer.getNom(),
				fitxer.getContingut(),
				response);
	}
	
	@RequestMapping(value = "/{expedientId}/exportarEni", method = RequestMethod.GET)
	public void exportarEni(
			@PathVariable Long expedientId,
			@RequestParam(required = false) boolean ambDocuments,
			HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		FitxerDto fitxer = expedientService.exportarEniExpedient(
				entitatActual.getId(), 
				new HashSet<>(Arrays.asList(expedientId)),
				ambDocuments);

		response.setHeader("Set-cookie", "contentLoaded=true; path=/");
		
		writeFileToResponse(
				fitxer.getNom(),
				fitxer.getContingut(),
				response);
	}
	
	@RequestMapping(value = "/exportarEni", method = RequestMethod.GET)
	public String exportarEniMassiu(
			@RequestParam(required = false) boolean ambDocuments,
			HttpServletRequest request,
			HttpServletResponse response) throws IOException {
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
							"expedient.controller.exportacio.seleccio.buida"),
					null);
			return "redirect:../expedient";
		} else {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			FitxerDto fitxer = expedientService.exportarEniExpedient(
					entitatActual.getId(), 
					seleccio,
					ambDocuments);
	
			response.setHeader("Set-cookie", "contentLoaded=true; path=/");
			
			writeFileToResponse(
					fitxer.getNom(),
					fitxer.getContingut(),
					response);
			return null;
		}
	}
	
	@RequestMapping(value = "/generarIndex/{format}", method = RequestMethod.GET)
	public String generarIndexMultiple(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable String format) throws IOException {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		if (seleccio == null || seleccio.isEmpty()) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"expedient.controller.exportacio.seleccio.buida"),
					null);
			return "redirect:../../expedient";
		} else {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			FitxerDto fitxer = expedientService.exportIndexExpedients(
					entitatActual.getId(),
					seleccio,
					format);
				
			response.setHeader("Set-cookie", "contentLoaded=true; path=/");
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
		return get(request, null, model);
	}
	@RequestMapping(value = "/{expedientId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

		ExpedientCommand command = null;
		if (expedientId != null) {
			ExpedientDto expedient = expedientService.findById(
					entitatActual.getId(),
					expedientId, null);
			command = ExpedientCommand.asCommand(expedient);
			
			List<GrupDto> grups = metaExpedientService.findGrupsAmbMetaExpedient(
					entitatActual.getId(),
					command.getMetaNodeId(),
					rolActual);
			model.addAttribute("grups", grups);
			command.setGestioAmbGrupsActiva(expedient.getMetaExpedient().isGestioAmbGrupsActiva());
		} else {
			command = new ExpedientCommand();
			command.setAny(Calendar.getInstance().get(Calendar.YEAR));
		}
		command.setEntitatId(entitatActual.getId());
		model.addAttribute(command);
		
		List<MetaExpedientDto> metaExpedients = null;
		if (expedientId != null) {
			metaExpedients = metaExpedientService.findActiusAmbEntitatPerModificacio(entitatActual.getId(), RolHelper.getRolActual(request));
		} else {
			metaExpedients = metaExpedientService.findActiusAmbEntitatPerCreacio(entitatActual.getId(), RolHelper.getRolActual(request));
		}
		
		model.addAttribute("metaExpedients", metaExpedients);
		
		return "contingutExpedientForm";
	}
	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String postNew(
			HttpServletRequest request,
			@Validated({Create.class}) ExpedientCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		if (Utils.isBiggerThan(command.getNom(), 239)) {
			bindingResult.rejectValue("nom", "Size", new Object[] { null, 239, 0 }, null);
		}
		
		if (bindingResult.hasErrors()) {
			model.addAttribute(
					"metaExpedients",
					metaExpedientService.findActiusAmbEntitatPerCreacio(entitatActual.getId(), RolHelper.getRolActual(request)));
			
			if (command.getMetaNodeId() != null) {
				List<GrupDto> grups = metaExpedientService.findGrupsAmbMetaExpedient(
						entitatActual.getId(),
						command.getMetaNodeId(),
						RolHelper.getRolActual(request));
				model.addAttribute("grups", grups);
			}
			
			return "contingutExpedientForm";
		}
		try {
			ExpedientDto expedientDto = expedientService.create(
					entitatActual.getId(),
					command.getMetaNodeId(),
					command.getMetaNodeDominiId(),
					command.getOrganGestorId(),
					command.getAny(),
					command.getNom(),
					null,
					false,
					command.getGrupId(),
					RolHelper.getRolActual(request),
					null, 
					null);
			
			model.addAttribute("redirectUrlAfterClosingModal", "contingut/" + expedientDto.getId());
			
			if (expedientDto.getArxiuUuid() != null) {
				return getModalControllerReturnValueSuccess(
						request,
						"redirect:../expedient",
						"expedient.controller.creat.ok");
			} else {
				return getModalControllerReturnValueWarning(
						request,
						"redirect:../expedient",
						"expedient.controller.creat.error.arxiu",
						null);
			}

			
		} catch (ValidationException ex) {
			MissatgesHelper.error(request, ex.getMessage(), ex);
			model.addAttribute(
					"metaExpedients",
					metaExpedientService.findActiusAmbEntitatPerCreacio(entitatActual.getId(), null));
			return "contingutExpedientForm";
		} catch (Exception ex) {
			logger.error("Error al crear expedient", ex);
			Exception e = ExceptionHelper.findExceptionInstance(ex, SistemaExternException.class, 3);
			if (e != null) {
				if (e.getMessage().contains("Serie documental no trobat")) {
					return getModalControllerReturnValueError(
							request,
							"redirect:../expedient",
							"expedient.controller.crear.error.serie.documental.not.found",
							e);
				} else {
					return getModalControllerReturnValueError(
							request,
							"redirect:../expedient",
							"expedient.controller.creat.error",
							new Object[] {e.getMessage()},
							e);
				}

			} else { 
				return getModalControllerReturnValueError(
						request,
						"redirect:../expedient",
						"expedient.controller.creat.error",
						new Object[] { ExceptionHelper.getRootCauseOrItself(ex).getMessage() },
						e);
			}
		}
	}
	@RequestMapping(value = "/{expedientId}/update", method = RequestMethod.POST)
	public String postUpdate(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@Validated({Update.class}) ExpedientCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (Utils.isBiggerThan(command.getNom(), 239)) {
			bindingResult.rejectValue("nom", "Size", new Object[] { null, 239, 0 }, null);
		}
		if (bindingResult.hasErrors()) {
			model.addAttribute(
					"metaExpedients",
					metaExpedientService.findActiusAmbEntitatPerModificacio(entitatActual.getId(), "tothom"));
			
			List<GrupDto> grups = metaExpedientService.findGrupsAmbMetaExpedient(
					entitatActual.getId(),
					command.getMetaNodeId(),
					RolHelper.getRolActual(request));
			model.addAttribute("grups", grups);
			
			return "contingutExpedientForm";
		}
		try {
			expedientService.update(
					entitatActual.getId(),
					command.getId(),
					command.getNom(),
					command.getAny(),
					command.getMetaNodeDominiId(), 
					command.getOrganGestorId(), 
					RolHelper.getRolActual(request), 
					command.getGrupId());
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../expedient",
					"expedient.controller.modificat.ok",
					new Object[] { command.getNom() });
		} catch (ValidationException ex) {
			logger.error("Error al modificar expedient", ex);
			MissatgesHelper.error(request, ex.getMessage(), ex);
			model.addAttribute(
					"metaExpedients",
					metaExpedientService.findActiusAmbEntitatPerModificacio(entitatActual.getId(), "tothom"));
			return "contingutExpedientForm";
		}		
	}
	
	
	@RequestMapping(value = "/{expedientId}/guardarExpedientArxiu", method = RequestMethod.GET)
	public String guardarExpedientArxiu(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@RequestParam(value = "origin") String origin,
			Model model)  {

		
		Exception exception = null;
		try {
			exception = expedientService.guardarExpedientArxiu(expedientId);
		} catch (ArxiuJaGuardatException e) {
			exception = null;
		} catch (Exception e) {
			exception = e;
		}
		
		String redirect = null;
		if (origin.equals("expDetail")) {
			redirect = "redirect:../../contingut/" + expedientId;
		} else if (origin.equals("seguiment")) {
			redirect = "redirect:../../seguimentArxiuPendents";
		} else if (origin.equals("expedientList")) {
			redirect = "redirect:../../expedient";
		}
		
		if (exception == null) {
			return getAjaxControllerReturnValueSuccess(
					request,
					redirect,
					"expedient.controller.guardar.arxiu.ok");
		} else {
			logger.error("Error guardant document en arxiu", exception);
			Throwable root = ExceptionHelper.getRootCauseOrItself(exception);
			String msg = null;
			if (root instanceof ConnectException || (root.getMessage() != null && root.getMessage().contains("timed out"))) {
				msg = getMessage(request,"error.arxiu.connectTimedOut");
			} else {
				msg = ExceptionHelper.getRootCauseOrItself(exception).getMessage();
			}
			return getAjaxControllerReturnValueError(
					request,
					redirect,
					"expedient.controller.guardar.arxiu.error",
					new Object[] {msg},
					exception);
		}
	}
	
	
	@RequestMapping(value = "/metaExpedient/{metaExpedientId}/grup", method = RequestMethod.GET)
	@ResponseBody
	public List<GrupDto> grups(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId) {
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return metaExpedientService.findGrupsAmbMetaExpedient(
				entitatActual.getId(),
				metaExpedientId, 
				rolActual);
	}

	@RequestMapping(value = "/metaExpedient/{metaExpedientId}/gestioAmbGrupsActiva", method = RequestMethod.GET)
	@ResponseBody
	public boolean gestioAmbGrupsActiva(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return metaExpedientService.findById(
				entitatActual.getId(),
				metaExpedientId).isGestioAmbGrupsActiva();
	}

	@RequestMapping(value = "/metaExpedient", method = RequestMethod.GET)
	@ResponseBody
	public List<MetaExpedientDto> metaExpedients(
			HttpServletRequest request) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		return metaExpedientService.findActius(
				entitatActual.getId(), 
				null, 
				rolActual, 
				false, 
				null);
	}
	
	@RequestMapping(value = "/metaExpedient/{metaExpedientId}/organsGestorsPermesos", method = RequestMethod.GET)
	@ResponseBody
	public List<OrganismeDto> organsGestorsPermesos(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@RequestParam(required = false) String filter) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		return organGestorService.findPermesosByEntitatAndExpedientTipusIdAndFiltre(
				entitatActual.getId(),
				metaExpedientId,
				filter, 
				null,
				RolHelper.getRolActual(request), 
				EntitatHelper.getOrganGestorActualId(request));

	}

	@RequestMapping(value = "/metaExpedient/{metaExpedientId}/organsGestorsPermesos/{expedientId}", method = RequestMethod.GET)
	@ResponseBody
	public List<OrganismeDto> organsGestorsPermesos(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long expedientId,
			@RequestParam(required = false) String filter) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		return organGestorService.findPermesosByEntitatAndExpedientTipusIdAndFiltre(
				entitatActual.getId(),
				metaExpedientId,
				filter, 
				expedientId,
				RolHelper.getRolActual(request), 
				EntitatHelper.getOrganGestorActualId(request));

	}

	@RequestMapping(value = "/organGestor/{organGestorId}/metaExpedient", method = RequestMethod.GET)
	@ResponseBody
	public List<MetaExpedientDto> organGestorMetaExpedients(
			HttpServletRequest request,
			@PathVariable Long organGestorId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return metaExpedientService.findActiusAmbOrganGestorPermisLectura(
				entitatActual.getId(),
				organGestorId, 
				null);
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

	@RequestMapping(value = "/{expedientId}/agafar", method = RequestMethod.GET)
	public String agafar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@RequestParam(required = false) String contingutId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		String url = null;
		String expNom = null;
		try {
			if (contingutId != null) {
				url = "redirect:../../contingut/" + contingutId;
			} else {
				url = "redirect:../../contingut/" + expedientId;
			}
			
			if (RolHelper.isRolActualAdministrador(request)) {
				expNom = expedientService.agafarAdmin(entitatActual.getId(),
						null,
						expedientId,
						aplicacioService.getUsuariActual().getCodi());

			} else if (RolHelper.isRolActualAdministradorOrgan(request)) {
				if (expedientService.isOrganGestorPermes(expedientId, RolHelper.getRolActual(request))) {
					expNom = expedientService.agafarAdmin(entitatActual.getId(),
							null,
							expedientId,
							aplicacioService.getUsuariActual().getCodi());
				} else {
					expNom = expedientService.agafarUser(
							entitatActual.getId(),
							expedientId);
				}
			} else {
				expNom = expedientService.agafarUser(
						entitatActual.getId(),
						expedientId);
			}
			return getAjaxControllerReturnValueSuccess(
					request,
					url,
					"expedient.controller.agafat.ok",
					new Object[] { expNom!=null?expNom:"" });
		} catch (Exception e) {
			logger.error("Error agafant expedient", e);
			Exception permisExcepcion = ExceptionHelper.findExceptionInstance(e, PermissionDeniedException.class, 3);
			if (permisExcepcion != null) {
				return getAjaxControllerReturnValueError(
						request,
						url,
						"expedient.controller.agafat.error.perm",
						e);
			} else {
				throw e;
			}
		}
	}
	
	@RequestMapping(value = "/agafar", method = RequestMethod.GET)
	public String agafarMultiple(
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException {
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
							"expedient.controller.exportacio.seleccio.buida"),
					null);
			return "redirect:/expedient";
		} else {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			
			int ok = 0;
			int errors = 0;
			for (Long expedientId : seleccio) {
				try {
					if (RolHelper.isRolActualAdministrador(request)) {
						expedientService.agafarAdmin(entitatActual.getId(),
								null,
								expedientId,
								aplicacioService.getUsuariActual().getCodi());

					} else if (RolHelper.isRolActualAdministradorOrgan(request)) {
						if (expedientService.isOrganGestorPermes(expedientId, RolHelper.getRolActual(request))) {
							expedientService.agafarAdmin(entitatActual.getId(),
									null,
									expedientId,
									aplicacioService.getUsuariActual().getCodi());
						} else {
							expedientService.agafarUser(
									entitatActual.getId(),
									expedientId);
						}
					} else {
						expedientService.agafarUser(
								entitatActual.getId(),
								expedientId);
					}
					ok++;
				} catch (Exception e) {
					errors++;
					logger.error("Error al agafar expedeint", e);
					ExpedientDto expedientDto = expedientService.findById(entitatActual.getId(), expedientId, RolHelper.getRolActual(request));
					MissatgesHelper.error(request, getMessage(request, "expedient.controller.agafat.error.multiple.one.msg", new Object[]{expedientDto.getNom(), ExceptionHelper.getRootCauseOrItself(e).getMessage()}), e);
					
				}
			}
			deselect(request, null);
			if (errors > 0) {
				MissatgesHelper.error(request, getMessage(request, "expedient.controller.agafat.error.multiple", new Object[]{errors}), null);
			}
			if (ok > 0) {
				MissatgesHelper.success(request, getMessage(request, "expedient.controller.agafat.ok.multiple", new Object[]{ok}));
			}
			
			return "redirect:/expedient";

		}
	}

	@RequestMapping(value = "/{expedientId}/comentaris", method = RequestMethod.GET)
	public String comentaris(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"contingut",
				contingutService.getBasicInfo(
						expedientId,
						true));
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
	public RespostaPublicacioComentariDto<ExpedientComentariDto> publicarComentari(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@RequestParam String text,
			Model model) {
		RespostaPublicacioComentariDto<ExpedientComentariDto> resposta = new RespostaPublicacioComentariDto<ExpedientComentariDto>();
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		if (text != null && !text.isEmpty()) {
			resposta = expedientService.publicarComentariPerExpedient(entitatActual.getId(), contingutId, text, RolHelper.getRolActual(request));
		}
			
		List<ExpedientComentariDto> comentaris = expedientService.findComentarisPerContingut(
				entitatActual.getId(), 
				contingutId);
		resposta.setComentaris(comentaris);
		return resposta;
	}

	@RequestMapping(value = "/{expedientId}/alliberar", method = RequestMethod.GET)
	public String alliberar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@RequestParam(required = false) String contingutId,
			Model model) {
		
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			String expNom = expedientService.alliberarUser(
					entitatActual.getId(),
					expedientId);
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../../contingut/" + (contingutId != null ? contingutId : expedientId),
					"expedient.controller.alliberat.ok",
					new Object[] { expNom });
			
		} catch (Exception e) {
			logger.error("Error al alliberar un expedient", e);
			
			return getAjaxControllerReturnValueErrorMessage(
					request,
					"redirect:../../contingut/" + (contingutId != null ? contingutId : expedientId),
					ExceptionHelper.getRootCauseOrItself(e).getMessage(),
					e);

		}
	}
	
	@RequestMapping(value = "/alliberar", method = RequestMethod.GET)
	public String alliberarMultiple(
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException {
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
							"expedient.controller.exportacio.seleccio.buida"),
					null);
			return "redirect:/expedient";
		} else {
			
			
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			
			int ok = 0;
			int errors = 0;
			for (Long expedientId : seleccio) {
				try {
					expedientService.alliberarUser(
							entitatActual.getId(),
							expedientId);
					ok++;
				} catch (Exception e) {
					errors++;
					logger.error("Error al alliberar expedeint", e);
					ExpedientDto expedientDto = expedientService.findById(entitatActual.getId(), expedientId, RolHelper.getRolActual(request));
					MissatgesHelper.error(request, getMessage(request, "expedient.controller.alliberat.error.multiple.one.msg", new Object[]{expedientDto.getNom(), ExceptionHelper.getRootCauseOrItself(e).getMessage()}), e);
				
				}
			}
			deselect(request, null);
			if (errors > 0) {
				MissatgesHelper.error(request, getMessage(request, "expedient.controller.alliberat.error.multiple", new Object[]{errors}), null);
			}
			if (ok > 0) {
				MissatgesHelper.success(request, getMessage(request, "expedient.controller.alliberat.ok.multiple", new Object[]{ok}));
			}
			
			return "redirect:/expedient";
			
			

		}
	}
	
	@RequestMapping(value = "/{expedientId}/assignar", method = RequestMethod.GET)
	public String assignar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		getEntitatActualComprovantPermisos(request);

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
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			return "expedientAssignarForm";
		}
		try {
			expedientService.assignar(
					entitatActual.getId(),
					expedientId,
					command.getUsuariCodi());
		} catch (Exception e) {
			Exception exc = ExceptionHelper.findExceptionInstance(e, PermissionDeniedException.class, 3);
			if (exc != null) {
				PermissionDeniedException perExc = (PermissionDeniedException) exc;
				if (perExc.getUserName().equals(command.getUsuariCodi()) && perExc.getPermissionName().equals("WRITE")) {
					return getModalControllerReturnValueError(
							request,
							"redirect:../../contingut/" + expedientId,
							"expedient.assignar.controller.no.permis",
							new Object[] { command.getUsuariCodi() },
							e);
				} else {
					throw e;
				}
			} else {
				throw e;
			}
		}
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../contingut/" + expedientId,
				"expedient.assignar.controller.assignat.ok");
	}

	@RequestMapping(value = "/{expedientId}/tancar", method = RequestMethod.GET)
	public String expedientTancarGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		ExpedientTancarCommand command = new ExpedientTancarCommand();
		command.setId(expedientId);
		model.addAttribute(command);
		omplirModelTancarExpedient(
				expedientId,
				request,
				model);
		return "expedientTancarForm";
	}
	@RequestMapping(value = "/{expedientId}/tancar", method = RequestMethod.POST)
	public String expedientTancarPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@Valid ExpedientTancarCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			omplirModelTancarExpedient(
					expedientId,
					request,
					model);
			return "expedientTancarForm";
		}
		try {
			String expNom = expedientService.tancar(
					entitatActual.getId(),
					expedientId,
					command.getMotiu(),
					command.getDocumentsPerFirmar(), false);
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../contingut/" + expedientId,
					"expedient.controller.tancar.ok",
					new Object[] { expNom });
		} catch (Exception ex) {
			
			logger.error("Error al tancar expedient amb id=" + command.getId(), ex);
			
			if (ExceptionHelper.isExceptionOrCauseInstanceOf(
					ex,
					ExpedientTancarSenseDocumentsDefinitiusException.class)) {
				omplirModelTancarExpedient(
						expedientId,
						request,
						model);
				MissatgesHelper.error(
						request, 
						getMessage(
								request, 
								"expedient.controller.tancar.nodefinitius",
								null),
						ex);
				return "expedientTancarForm";
			} else if (ExceptionHelper.isExceptionOrCauseInstanceOf(
					ex,
					FirmaServidorException.class)) {
				
				FirmaServidorException fe = (FirmaServidorException) ExceptionHelper.findExceptionInstance(ex, FirmaServidorException.class, 3);
				
				return getModalControllerReturnValueError(
						request,
						"redirect:../../contingut/" + expedientId,
						"expedient.controller.tancar.error",
						new Object[] {fe.getDocumentNom()},
						ex);
			} else {
				throw ex;
			}
			

		}
	}
	
	@RequestMapping(value = "/{expedientId}/reobrir", method = RequestMethod.GET)
	public String expedientReobrirGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		try {
			expedientService.reobrir(
					entitatActual.getId(),
					expedientId);
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../contingut/" + expedientId,
					"expedient.controller.reobrir.ok");
		} catch (Exception ex) {
			logger.error("Error al reobrir expedient amb id=" + expedientId, ex);
			throw ex;			
		}
	}

	@RequestMapping(value = "/estatValues/{metaExpedientId}", method = RequestMethod.GET)
	@ResponseBody
	public List<ExpedientEstatDto> findExpedientEstatByMetaExpedient(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		long t0 = System.currentTimeMillis();
		List<ExpedientEstatDto> expedientEstatsOptions = new ArrayList<>();
		if (metaExpedientId == 0) { //TODO change 0 to null and @RequesParam
			expedientEstatsOptions.add(new ExpedientEstatDto(getMessage(request, "expedient.estat.enum." + ExpedientEstatEnumDto.values()[0].name()), Long.valueOf(0)));
			expedientEstatsOptions.add(new ExpedientEstatDto(getMessage(request, "expedient.estat.enum." + ExpedientEstatEnumDto.values()[1].name()), Long.valueOf(-1)));
		} else {
			
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

			List<ExpedientEstatDto> estatsFromDatabase = expedientEstatService.findExpedientEstatsByMetaExpedient(
					entitatActual.getId(),
					metaExpedientId);
			
			expedientEstatsOptions.add(new ExpedientEstatDto(getMessage(request, "expedient.estat.enum." + ExpedientEstatEnumDto.values()[0].name()), Long.valueOf(0)));
			expedientEstatsOptions.addAll(estatsFromDatabase);
			expedientEstatsOptions.add(new ExpedientEstatDto(getMessage(request, "expedient.estat.enum." + ExpedientEstatEnumDto.values()[1].name()), Long.valueOf(-1)));
			
		}

		logger.debug("findExpedientEstatByMetaExpedient time: " + (System.currentTimeMillis() - t0) + " ms");
		return expedientEstatsOptions;
	}
	
	@RequestMapping(value = "/findAll", method = RequestMethod.GET)
	@ResponseBody
	public List<CodiValorDto> findAll(HttpServletRequest request, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return expedientService.findByEntitat(entitatActual.getId());
	}

	@RequestMapping(value = "/{expedientId}/canviarEstat", method = RequestMethod.GET)
	public String canviarEstatGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ExpedientDto expedient = null;
		if (expedientId != null) {
			expedient = expedientService.findById(
					entitatActual.getId(),
					expedientId, null);
		}
		List<ExpedientEstatDto> expedientEstats = expedientEstatService.findExpedientEstats(
				entitatActual.getId(),
				expedientId, 
				RolHelper.getRolActual(request));
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
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
//			model.addAttribute(
//					"metaExpedients",
//					metaExpedientService.findActiusAmbEntitatPerCreacio(entitatActual.getId()));
			return "expedientEstatsForm";
		}
		ExpedientDto expedientDto = expedientEstatService.changeExpedientEstat(
				entitatActual.getId(),
				command.getId(),
				command.getExpedientEstatId());
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../expedient",
				"expedient.controller.estatModificat.ok",
				new Object[] { expedientDto.getNom() });
	}
	
	@RequestMapping(value = "/{expedientId}/relacionarList/select", method = RequestMethod.GET)
	@ResponseBody
	public int relacionarSelect(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@RequestParam(value="ids[]", required = false) Long[] ids) {
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_RELACIONAR_SELECCIO + "_" + expedientId);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_RELACIONAR_SELECCIO + "_" + expedientId,
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
							ExpedientFiltreCommand.asDto(filtreCommand), rolActual));
		}
		return seleccio.size();
	}
	
	@RequestMapping(value = "/{expedientId}/relacionarList/deselect", method = RequestMethod.GET)
	@ResponseBody
	public int relacionarDeselect(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@RequestParam(value="ids[]", required = false) Long[] ids) {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_RELACIONAR_SELECCIO + "_" + expedientId);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_RELACIONAR_SELECCIO + "_" + expedientId,
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
	
	@RequestMapping(value = "/{expedientId}/relacionarList", method = RequestMethod.GET)
	public String expedientRelacionarGetList(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		emplenarFiltreRelacionats(request, model, expedientId);
		
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
		expedientEstatsOptions.addAll(expedientEstatService.findExpedientEstatsByMetaExpedient(entitatActual.getId(), expedientFiltreCommand.getMetaExpedientId()));
		expedientEstatsOptions.add(new ExpedientEstatDto(ExpedientEstatEnumDto.values()[1].name(), Long.valueOf(-1)));
		model.addAttribute(
				"expedientEstatsOptions",
				expedientEstatsOptions);
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
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_RELACIONAR_SELECCIO + "_" + expedientId);
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

	@RequestMapping(value = "/{expedientId}/relacionar", method = RequestMethod.GET)
	public String expedientRelacionar(
			HttpServletRequest request,
			@PathVariable Long expedientId) throws IOException {

		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

			@SuppressWarnings("unchecked")
			Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
					request,
					SESSION_ATTRIBUTE_RELACIONAR_SELECCIO + "_" + expedientId);
			if (seleccio == null || seleccio.isEmpty()) {
				return getModalControllerReturnValueWarning(
						request,
						"redirect:/../../contingut/" + expedientId,
						"expedient.controller.relacio.seleccio.buida");
			} else {
				for (Long relacionatId : seleccio) {
					expedientService.relacioCreate(
							entitatActual.getId(),
							expedientId,
							relacionatId, 
							RolHelper.getRolActual(request));
				}
				
				RequestSessionHelper.esborrarObjecteSessio(
						request,
						SESSION_ATTRIBUTE_RELACIONAR_SELECCIO + "_" + expedientId);
				
				return getModalControllerReturnValueSuccess(
						request,
						"redirect:/../../contingut/" + expedientId,
						"expedient.controller.relacionat.ok");
			}
			
		} catch (Exception e) {
			logger.error("Error al relacionar expedients", e);
			return getModalControllerReturnValueErrorMessageText(
					request,
					"redirect:../../esborrat",
					e.getMessage(),
					e);

		}
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
					relacionatId, 
					RolHelper.getRolActual(request));
			
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_RELACIONAR_SELECCIO + "_" + expedientId);
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/../../contingut/" + expedientId,
					"expedient.controller.relacionat.ok");
			
		} catch (Exception e) {
			logger.error("Error al relacionar expedient", e);
			return getModalControllerReturnValueErrorMessageText(
					request,
					"redirect:../../esborrat",
					e.getMessage(),
					e);

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
						DatatablesHelper.getPaginacioDtoFromRequest(request), 
						RolHelper.getRolActual(request)),
				"id",
				SESSION_ATTRIBUTE_RELACIONAR_SELECCIO + "_" + expedientId);
		
	}

	@RequestMapping(value = "/{expedientId}/relacio/{relacionatId}/delete", method = RequestMethod.GET)
	public String expedientRelacioDelete(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long relacionatId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (expedientService.relacioDelete(
				entitatActual.getId(),
				expedientId,
				relacionatId, 
				RolHelper.getRolActual(request))) {
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
							"expedient.controller.relacio.esborrada.error"),
					null);
		}
		return "redirect:/contingut/" + expedientId;
	}



	@RequestMapping(value = "/{expedientId}/notificacio/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse notificacioDatatable(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				documentEnviamentService.findAmbExpedient(
						entitatActual.getId(),
						expedientId, 
						DocumentEnviamentTipusEnumDto.NOTIFICACIO));		
	}
	
	@RequestMapping(value = "/{expedientId}/publicacio/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse publicacioDatatable(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				documentEnviamentService.findAmbExpedient(
						entitatActual.getId(),
						expedientId, 
						DocumentEnviamentTipusEnumDto.PUBLICACIO));		
	}
	
	

	
	@RequestMapping(value = "/{expedientId}/enviaments/{notificacioId}", method = RequestMethod.GET)
	@ResponseBody
	public JsonResponse enviamentList(
			HttpServletRequest request, 
			Model model,
			@PathVariable Long expedientId,
			@PathVariable Long notificacioId) {
		
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			DocumentNotificacioDto notificacio = documentEnviamentService.notificacioFindAmbIdAndExpedient(
					entitatActual.getId(),
					expedientId,
					notificacioId);
			notificacio.setDocument(null); //to prevent circular depndency
			
			return new JsonResponse(notificacio);
			
		} catch (Exception e) {
			logger.error("Error descarregant enviaments", e);
			return new JsonResponse(true, e.getMessage());
		}
	}
	
	@RequestMapping(value = "/{expedientId}/enviamentDetails/{notificacioId}/enviamentInfo/{enviamentId}", method = RequestMethod.GET)
	public String enviamentInfo(
			HttpServletRequest request, 
			@PathVariable Long expedientId,
			@PathVariable Long notificacioId,
			@PathVariable Long enviamentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		DocumentNotificacioDto documentNotificacioDto = documentEnviamentService.notificacioFindAmbIdAndExpedient(
				entitatActual.getId(),
				expedientId,
				notificacioId);
		
		model.addAttribute(
				"notificacio",
				documentNotificacioDto);
		DocumentEnviamentInteressatDto documentEnviamentInteressatDto = documentNotificacioDto.getDocumentEnviamentInteressats().iterator().next();
		
		model.addAttribute(
				"enviament",
				documentEnviamentInteressatDto);
		
		model.addAttribute(
				"entregaNif",
				documentNotificacioDto.getCreatedBy().getNif());
		
		model.addAttribute(
				"classificacio",
				expedientService.findById(entitatActual.getId(), expedientId, null).getMetaExpedient().getClassificacio());
		
		return "enviamentInfo";
	}
	
	@RequestMapping(value = "/metaExpedient/{metaExpedientId}/list", method = RequestMethod.GET)
	public String getByMetaExpedient(HttpServletRequest request,
								   @PathVariable Long metaExpedientId,
								   Model model) {
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		MetaExpedientDto metaExpedient = metaExpedientService.findById(entitat.getId(), metaExpedientId);
		
		model.addAttribute("metaExpedient", metaExpedient);
		return "expedientListModal";
	}
	
	@RequestMapping(value = "/metaExpedient/{metaExpedientId}/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ExpedientFiltreCommand filtreCommand = new ExpedientFiltreCommand();
		filtreCommand.setMetaExpedientId(metaExpedientId);

		return DatatablesHelper.getDatatableResponse(
				request,
				expedientService.findExpedientMetaExpedientPaginat(
						entitatActual.getId(),
						metaExpedientId,
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id");
	}
	
	@RequestMapping(value = "/{expedientId}/relacionats/{destiId}/list", method = RequestMethod.GET)
	public String getRelacionatsByExpedientGetList(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long destiId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		emplenarFiltreRelacionats(request, model, expedientId);
		model.addAttribute("destiId", destiId);
		ExpedientFiltreCommand expedientFiltreCommand = getRelacionatsFiltreCommand(request);
		model.addAttribute(
				"expedientFiltreCommand",
				expedientFiltreCommand);
		//putting enums from ExpedientEstatEnumDto and ExpedientEstatDto into one class, need to have all estats from enums and database in one type 
		List<ExpedientEstatDto> expedientEstatsOptions = new ArrayList<>();
		expedientEstatsOptions.add(new ExpedientEstatDto(ExpedientEstatEnumDto.values()[0].name(), Long.valueOf(0)));
		expedientEstatsOptions.addAll(expedientEstatService.findExpedientEstatsByMetaExpedient(entitatActual.getId(), expedientFiltreCommand.getMetaExpedientId()));
		expedientEstatsOptions.add(new ExpedientEstatDto(ExpedientEstatEnumDto.values()[1].name(), Long.valueOf(-1)));
		model.addAttribute(
				"expedientEstatsOptions",
				expedientEstatsOptions);
		return "expedientRelacionatsList";
	}
	
	@RequestMapping(value = "/{expedientId}/relacionats/{destiId}/list", method = RequestMethod.POST)
	public String getRelacionatsByExpedientPostList(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long destiId,
			@Valid ExpedientFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model,
			@RequestParam(value = "accio", required = false) String accio) {
		getEntitatActualComprovantPermisos(request);
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_RELACIONATS_FILTRE);
		} else {
			if (!bindingResult.hasErrors()) {
				RequestSessionHelper.actualitzarObjecteSessio(
						request,
						SESSION_ATTRIBUTE_RELACIONATS_FILTRE,
						filtreCommand);
			}
		}
		return "redirect:/modal/expedient/" + expedientId + "/relacionats/" + destiId + "/list";
	}
	
	@RequestMapping(value = "/{expedientId}/relacionats/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse getRelacionatsByExpedientDatatable(HttpServletRequest request,
								   @PathVariable Long expedientId,
								   Model model) {
		ExpedientFiltreCommand filtreCommand = getRelacionatsFiltreCommand(request);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				expedientService.relacioFindAmbExpedientPaginat(
						entitatActual.getId(),
						ExpedientFiltreCommand.asDto(filtreCommand),
						expedientId,
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id");
	}
	
	@RequestMapping(value = "/{expedientPareId}/relacionats/{destiId}/importarExpedient/{expedientId}", method = RequestMethod.GET)
	public String importarExpedient(
			HttpServletRequest request,
			@PathVariable Long expedientPareId,
			@PathVariable Long destiId,
			@PathVariable Long expedientId) throws IOException {
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			expedientService.importarExpedient(
					entitatActual.getId(),
					destiId,
					expedientId, 
					RolHelper.getRolActual(request));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/../../contingut/" + destiId,
					"expedient.controller.importacio.relacionat.ok");
			
		} catch (Exception e) {
			logger.error("Error al importar expedient relacionat", e);
			return getModalControllerReturnValueErrorMessageText(
					request,
					"redirect:/../../contingut/" + destiId,
					e.getMessage(),
					e);

		}
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public String deleteMultiple(
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		ExpedientFiltreCommand command = getFiltreCommand(request);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (seleccio == null || seleccio.isEmpty() || command == null) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"expedient.controller.exportacio.seleccio.buida"),
					null);
			return "redirect:/expedient";
		} else {
			int borrados = 0;
			int errors = 0;
			for (Long contingutId : seleccio) {
				try {
					contingutService.deleteReversible(
							entitatActual.getId(),
							contingutId, 
							RolHelper.getRolActual(request), 
							null);
					borrados++;
				} catch (Exception e) {
					errors++;
					ExpedientDto expedientDto = expedientService.findById(entitatActual.getId(), contingutId, RolHelper.getRolActual(request));
					logger.error("Error al esborrar el contingut multiple (id=" + contingutId + ")", e);
					Throwable root = ExceptionHelper.getRootCauseOrItself(e);
					if (root instanceof ConnectException || root.getMessage().contains("timed out")) {
						MissatgesHelper.error(
								request, 
								getMessage(request, "expedient.controller.esborrar.error.multiple.one.msg", new Object[]{expedientDto.getNom(), getMessage(request, "error.arxiu.connectTimedOut")}), e);
					} else {
						MissatgesHelper.error(
								request, 
								getMessage(request, "expedient.controller.esborrar.error.multiple.one.msg", new Object[]{expedientDto.getNom(), root.getMessage()}),
								e);
					}
				}
			}
			deselect(request, null);
			if (errors > 0) {
				MissatgesHelper.error(request, getMessage(request, "contingut.controller.element.esborrat.error.multiple", new Object[]{errors}), null);
			}
			if (borrados > 0) {
				MissatgesHelper.success(request, getMessage(request, "contingut.controller.element.esborrat.ok.multiple", new Object[]{borrados}));
			}
			return "redirect:/expedient";
		}
	}
	
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}

	private void emplenarFiltreRelacionats(
			HttpServletRequest request, 
			Model model, 
			Long expedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"expedient",
				contingutService.findAmbIdUser(
						entitatActual.getId(),
						expedientId,
						true,
						false, true, null, null));
		model.addAttribute("expedientId", expedientId);
		ExpedientFiltreCommand filtre = new ExpedientFiltreCommand();
		model.addAttribute(filtre);
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);

		model.addAttribute(
				"expedientEstatEnumOptions",
				EnumHelper.getOptionsForEnum(
						ExpedientEstatEnumDto.class,
						"expedient.estat.enum."));
		List<MetaExpedientDto> metaExpedientsPermisLectura = metaExpedientService.findActius(
				entitatActual.getId(), 
				null, 
				rolActual, 
				false, 
				null);
		model.addAttribute(
				"metaExpedientsPermisLectura",
				metaExpedientsPermisLectura);
		if (!expedientService.hasReadPermissionsAny(rolActual, entitatActual.getId())) {
			MissatgesHelper.warning(
					request, 
					getMessage(
							request, 
							"expedient.controller.sense.permis.lectura"));
		}
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
			
			if (isFiltreDataCreacioActiu()) {
				Date now = new Date();
				Calendar c = Calendar.getInstance(); 
				c.setTime(now); 
				c.add(Calendar.MONTH, -3);
		        c.set(Calendar.HOUR, 0);
		        c.set(Calendar.MINUTE, 0);
		        c.set(Calendar.SECOND, 0);
				filtreCommand.setDataCreacioInici(c.getTime());
			}
			filtreCommand.setExpedientEstatId(Long.valueOf(0));
			
			filtreCommand.setMetaExpedientId(aplicacioService.getProcedimentPerDefecte(EntitatHelper.getEntitatActual(request).getId(), RolHelper.getRolActual(request)));
			
		}
		Cookie cookie = WebUtils.getCookie(request, COOKIE_MEUS_EXPEDIENTS);
		filtreCommand.setMeusExpedients(cookie != null && "true".equals(cookie.getValue()));
		
		Cookie cookieFirmaPendent = WebUtils.getCookie(request, COOKIE_FIRMA_PENDENT);
		filtreCommand.setAmbFirmaPendent(cookieFirmaPendent != null && "true".equals(cookieFirmaPendent.getValue()));
		
		return filtreCommand;
	}
	
	private boolean isFiltreDataCreacioActiu() {
		return Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.filtre.data.creacio.actiu"));
	}
	
	private boolean isExportacioExcelActiva() {
		return Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.expedient.exportacio.excel"));
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

	private ExpedientFiltreCommand getRelacionatsFiltreCommand(
			HttpServletRequest request) {
		ExpedientFiltreCommand filtreCommand = (ExpedientFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_RELACIONATS_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new ExpedientFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_RELACIONATS_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}
	
	private void omplirModelTancarExpedient(
			Long expedientId,
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ExpedientDto expedient = (ExpedientDto)contingutService.findAmbIdUser(
				entitatActual.getId(),
				expedientId,
				true,
				false, true, null, null);
		model.addAttribute("expedient", expedient);
		List<DocumentDto> esborranys = documentService.findDocumentsNoFirmatsOAmbFirmaInvalidaONoGuardatsEnArxiu(
				entitatActual.getId(),
				expedientId);
		model.addAttribute("esborranys", esborranys);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ExpedientController.class);

}