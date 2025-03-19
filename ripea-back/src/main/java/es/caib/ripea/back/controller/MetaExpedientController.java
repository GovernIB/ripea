package es.caib.ripea.back.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.util.WebUtils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;

import es.caib.ripea.back.command.ExpedientEstatCommand;
import es.caib.ripea.back.command.FileCommand;
import es.caib.ripea.back.command.MetaDocumentCommand;
import es.caib.ripea.back.command.MetaExpedientCommand;
import es.caib.ripea.back.command.MetaExpedientFiltreCommand;
import es.caib.ripea.back.command.MetaExpedientImportEditCommand;
import es.caib.ripea.back.command.MetaExpedientImportRolsacCommand;
import es.caib.ripea.back.command.MetaExpedientTascaCommand;
import es.caib.ripea.back.helper.ConversioTipusHelper;
import es.caib.ripea.back.helper.DatatablesHelper;
import es.caib.ripea.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.back.helper.EntitatHelper;
import es.caib.ripea.back.helper.EnumHelper;
import es.caib.ripea.back.helper.ExceptionHelper;
import es.caib.ripea.back.helper.JsonResponse;
import es.caib.ripea.back.helper.MissatgesHelper;
import es.caib.ripea.back.helper.RequestSessionHelper;
import es.caib.ripea.back.helper.RolHelper;
import es.caib.ripea.back.helper.SessioHelper;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.ArbreDto;
import es.caib.ripea.service.intf.dto.CrearReglaResponseDto;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.ExpedientEstatDto;
import es.caib.ripea.service.intf.dto.MetaDocumentDto;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaExpedientActiuEnumDto;
import es.caib.ripea.service.intf.dto.MetaExpedientCarpetaDto;
import es.caib.ripea.service.intf.dto.MetaExpedientComentariDto;
import es.caib.ripea.service.intf.dto.MetaExpedientDto;
import es.caib.ripea.service.intf.dto.MetaExpedientExportDto;
import es.caib.ripea.service.intf.dto.MetaExpedientFiltreDto;
import es.caib.ripea.service.intf.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.service.intf.dto.MetaExpedientTascaDto;
import es.caib.ripea.service.intf.dto.OrganGestorDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.PortafirmesFluxRespostaDto;
import es.caib.ripea.service.intf.dto.ProcedimentDto;
import es.caib.ripea.service.intf.dto.ProgresActualitzacioDto;
import es.caib.ripea.service.intf.dto.ReglaDistribucioDto;
import es.caib.ripea.service.intf.dto.StatusEnumDto;
import es.caib.ripea.service.intf.dto.TipusClassificacioEnumDto;
import es.caib.ripea.service.intf.dto.UsuariDto;
import es.caib.ripea.service.intf.exception.ExisteixenExpedientsEsborratsException;
import es.caib.ripea.service.intf.exception.ExisteixenExpedientsException;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.MetaExpedientService;
import es.caib.ripea.service.intf.service.PortafirmesFluxService;
import es.caib.ripea.service.intf.utils.Utils;

/**
 * Controlador per al manteniment de meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/metaExpedient")
public class MetaExpedientController extends BaseAdminController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "MetaExpedientController.session.filtre";
	private static final String SESSION_ATTRIBUTE_IMPORT_TEMPORAL = "MetaExpedientController.session.import.temporal";
	private static final String COOKIE_PERMIS_DIRECTE = "permis_directe";

	@Autowired private MetaExpedientService metaExpedientService;
	@Autowired private AplicacioService aplicacioService;
	@Autowired private PortafirmesFluxService portafirmesFluxService;
	@Autowired private Validator validator;
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(
			@CookieValue(value = COOKIE_PERMIS_DIRECTE, defaultValue = "false") boolean permisDirecte,
			HttpServletRequest request,
			Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		MetaExpedientFiltreCommand command = getFiltreCommand(request);
		model.addAttribute(command);
		boolean isRolAdmin = RolHelper.isRolActualAdministrador(request);
		model.addAttribute("isRolAdmin", isRolAdmin);
        model.addAttribute("isRolDissenyadorOrgan", RolHelper.isRolActualDissenyadorOrgan(request));
		model.addAttribute("isRolAdminOrgan", RolHelper.isRolActualAdministradorOrgan(request));
		model.addAttribute("isActiveGestioPermisPerAdminOrgan", Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.ADMIN_ORGAN_GESTIO_PERMISOS)));
		
		model.addAttribute("nomCookiePermisDirecte", COOKIE_PERMIS_DIRECTE);
		model.addAttribute("permisDirecteActive", permisDirecte);
		
		if (isRolAdmin) {
			boolean revisioActiva = metaExpedientService.isRevisioActiva();
			int count = metaExpedientService.countMetaExpedientsPendentRevisar(entitatActual.getId());
			if (revisioActiva && count > 0) {
				MissatgesHelper.info(request, "<a href=\"metaExpedientRevisio\">" + getMessage(request, "metaexpedient.revisio.admin.pendent.revisar.alerta", new Object[] {count}) + "&nbsp;<i class=\"fa fa-external-link\"></i></a>");
			}
		}

		model.addAttribute("isRevisioActiva", metaExpedientService.isRevisioActiva());
		
		return "metaExpedientList";
	}

	@RequestMapping(value = "/filtrar", method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid MetaExpedientFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model,
			@RequestParam(value = "accio", required = false) String accio) {
		getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
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
		return "redirect:../metaExpedient";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		MetaExpedientFiltreCommand filtreCommand = getFiltreCommand(request);
		
		MetaExpedientFiltreDto filtreDto = filtreCommand.asDto();
		filtreDto.setRevisioEstats(new MetaExpedientRevisioEstatEnumDto[] { filtreCommand.getRevisioEstat() });

		boolean filtrePerOrgan = RolHelper.isRolAmbFiltreOrgan(request);
		PaginaDto<MetaExpedientDto> metaExps = metaExpedientService.findByEntitatOrOrganGestor(
				entitatActual.getId(),
				organActual == null ? null : organActual.getId(),
				filtreDto,
				filtrePerOrgan,
				DatatablesHelper.getPaginacioDtoFromRequest(request),
				RolHelper.getRolActual(request),
				hasPermisAdmComu(request));
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(request, metaExps, "id");
		return dtr;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(HttpServletRequest request, Model model) {
		return get(request, null, model);
	}

	@RequestMapping(value = "/{metaExpedientId}", method = RequestMethod.GET)
	public String get(HttpServletRequest request, @PathVariable Long metaExpedientId, Model model) {
		getMetaExpedient(request, metaExpedientId, model);		
		return "metaExpedientForm";
	}
	
	@RequestMapping(value = "/getDadesProcediment/{metaExpedientId}", method = RequestMethod.GET)
	@ResponseBody
	public MetaExpedientDto get(HttpServletRequest request, @PathVariable Long metaExpedientId) {
		return metaExpedientService.findById(
				EntitatHelper.getEntitatActual(request).getId(),
				metaExpedientId);
	}
	
	public void getMetaExpedient(HttpServletRequest request, Long metaExpedientId, Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);

		MetaExpedientDto metaExpedient = null;
		if (metaExpedientId != null)
			metaExpedient = comprovarAccesMetaExpedient(request, metaExpedientId);
		
		MetaExpedientCommand command = null;
		boolean isRolActualAdminOrgan = RolHelper.isRolActualAdministradorOrgan(request);
		
		if (metaExpedient != null) {
			command = MetaExpedientCommand.asCommand(metaExpedient);
		} else {
			command = new MetaExpedientCommand();
			if (isRolActualAdminOrgan)
				command.setComu(false);
			if (RolHelper.isRolActualAdministrador(request)) {
				command.setCrearReglaDistribucio(true);
			}
			command.setTipusClassificacio(TipusClassificacioEnumDto.SIA);
		}
		
		command.setRolAdminOrgan(isRolActualAdminOrgan);
		command.setEntitatId(entitatActual.getId());
		model.addAttribute(command);
		boolean isCarpetesDefecte = Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.CARPETES_PER_DEFECTE));
		
		if (isCarpetesDefecte) {
			List<ArbreDto<MetaExpedientCarpetaDto>> carpetes = null;
			if (metaExpedientId != null)
				carpetes = metaExpedientService.findArbreCarpetesMetaExpedient(entitatActual.getId(), metaExpedientId);
			else
				carpetes = new ArrayList<ArbreDto<MetaExpedientCarpetaDto>>();
			model.addAttribute("carpetes", carpetes);
		}
		
		if (metaExpedient != null && metaExpedientService.isRevisioActiva()) { // es tracta d'una modificació
			if (RolHelper.isRolActualAdministradorOrgan(request)  && metaExpedient.getRevisioEstat() == MetaExpedientRevisioEstatEnumDto.REVISAT){
				MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.adminOrgan.bloquejada.alerta"));
				model.addAttribute("bloquejarCamps", true);
			} else if (RolHelper.isRolActualRevisor(request)){
				model.addAttribute("bloquejarCamps", true);
				model.addAttribute("consultar", true);
				model.addAttribute("isRolActualRevisor", true);
			}
		}
		
		model.addAttribute("isRolActualAdminOrgan", RolHelper.isRolActualAdministradorOrgan(request));
		model.addAttribute("isDocumentsGeneralsEnabled", aplicacioService.propertyBooleanFindByKey(PropertyConfig.DOCUMENTS_GENERALS_ACTIUS, false));
		model.addAttribute("metaExpedientDto", metaExpedient);
		model.addAttribute("tipus", EnumHelper.getOptionsForEnum(TipusClassificacioEnumDto.class, "tipus.classificacio."));
		
		fillFormModel(request, metaExpedient, model);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid MetaExpedientCommand command,
			BindingResult bindingResult,
			Model model) throws JsonMappingException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		MetaExpedientDto dto = command.asDto();
		if (!command.isComu() && command.getOrganGestorId() == null) {
			bindingResult.rejectValue("organGestorId", "NotNull");
		}
		if (command.getTipusClassificacio() == TipusClassificacioEnumDto.SIA && Utils.isEmpty(command.getClassificacioSia())) {
			bindingResult.rejectValue("classificacioSia", "NotNull");
		}
		if (command.isComu() && !hasPermisAdmComu(request)) {
			bindingResult.reject("metaexpedient.controller.comu.permis.error");
		}
		if (bindingResult.hasErrors()) {
			fillFormModel(request, dto, model);
			return "metaExpedientForm";
		}
		String rolActual = RolHelper.getRolActual(request);
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		
		dto.setNotificacioActiva(true);

		if (command.getId() != null) {
			boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), command.getId());

			MetaExpedientDto metaExpedient = comprovarAccesMetaExpedient(request, command.getId());
			MetaExpedientRevisioEstatEnumDto estatAnterior = metaExpedient.getRevisioEstat();
			metaExpedientService.update(entitatActual.getId(), dto, rolActual, estatAnterior, organActual != null ? organActual.getId() : null);
		
			if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
				MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
			}
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:metaExpedient",
					"metaexpedient.controller.modificat.ok",
					new Object[] { command.getNom() });
		} else {

			try {
				MetaExpedientDto metaExpedientDto = metaExpedientService.create(
						entitatActual.getId(),
						dto,
						rolActual,
						organActual != null ? organActual.getId() : null);

				CrearReglaResponseDto crearReglaResponse = metaExpedientDto.getCrearReglaResponse();
				
				MissatgesHelper.success(
						request,
						getMessage(request,
								"metaexpedient.controller.creat.ok",
								new Object[] { command.getNom() }));
				
				if (crearReglaResponse != null) {
					if (crearReglaResponse.getStatus() == StatusEnumDto.OK) {
						MissatgesHelper.success(
								request,
								getMessage(
										request,
										"metaexpedient.controller.regla.crear.result",
										new Object[] { crearReglaResponse.getMsgEscapeXML() }));
					} else if (crearReglaResponse.getStatus() == StatusEnumDto.WARNING) {
						MissatgesHelper.warning(
								request,
								getMessage(
										request,
										"metaexpedient.controller.regla.crear.result",
										new Object[] { crearReglaResponse.getMsgEscapeXML() }));
					} else {
						MissatgesHelper.error(
								request,
								getMessage(
										request,
										"metaexpedient.controller.regla.crear.result",
										new Object[] { crearReglaResponse.getMsgEscapeXML() }),
								null);
					}
				}

				return modalUrlTancar();

			} catch (Exception e) {
				Throwable throwable = ExceptionHelper.getRootCauseOrItself(e);
				return getModalControllerReturnValueError(
						request,
						"redirect:metaExpedient",
						"metaexpedient.controller.creat.error",
						new String[] {command.getNom(), throwable.getMessage()},
						throwable);
			}
		}
	}
	
	@RequestMapping(value = "/calculateClassificacioId/{organGestorId}", method = RequestMethod.GET)
	@ResponseBody
	public String calculateClassificacioId(
			HttpServletRequest request,
			@PathVariable Long organGestorId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		OrganGestorDto organGestor = organGestorService.findById(entitatActual.getId(), organGestorId);
		return organGestor.getCodi() +  "_PRO_" + String.format("%030d", System.currentTimeMillis()) + "3F";
	}
	
	@RequestMapping(value = "/checkIfExistsInRolsac/{codiSia}", method = RequestMethod.GET)
	@ResponseBody
	public JsonResponse checkIfExistsInRolsac(
			HttpServletRequest request,
			@PathVariable String codiSia) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		String codiDir3;
		if (RolHelper.isRolActualAdministradorOrgan(request)) {
			codiDir3 = EntitatHelper.getOrganGestorActual(request).getCodi();
		} else {
			codiDir3 = entitatActual.getUnitatArrel();
		}
		
		ProcedimentDto procedimentDto = null;
		try {
			procedimentDto = metaExpedientService.findProcedimentByCodiSia(
					entitatActual.getId(),
					codiDir3,
					codiSia);
		} catch (Exception e) {
			logger.error("Error al comprobar procediment en ROLSAC", e);
			return new JsonResponse(true, null);
		}
		
		if (procedimentDto == null) {
			return new JsonResponse(false);
		} else {
			return new JsonResponse(true);
		}
	}
	
	@RequestMapping(value = "/{metaExpedientId}/export", method = RequestMethod.GET)
	public String export(HttpServletRequest request, HttpServletResponse response, @PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		
		MetaExpedientDto metaExpedient = comprovarAccesMetaExpedient(request, metaExpedientId);
		
		String json = metaExpedientService.export(
				entitatActual.getId(),
				metaExpedientId,
				EntitatHelper.getOrganGestorActualId(request));

		try {
			
			String fileNom = metaExpedient.getCodi().replaceAll("[^a-zA-Z0-9-]", "_");
			if (fileNom.length() > 60) {
				fileNom = fileNom.substring(0, 60);
			}
			
			writeFileToResponse(
					fileNom + "_export.json",
					json.getBytes(),
					response);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return null;
	}
	
	@RequestMapping(value = "/importFitxer", method = RequestMethod.GET)
	public String importFitxerGet(
			HttpServletRequest request,
			Model model) {
		getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		boolean isRolAdminOrgan = RolHelper.isRolActualAdministradorOrgan(request);
		model.addAttribute("isRolAdminOrgan", isRolAdminOrgan);
		return "importMetaExpedientFileForm";
	}
	
	@RequestMapping(value = "/importFitxer", method = RequestMethod.POST)
	public String importFitxerPost(
			HttpServletRequest request,
			FileCommand command,
			BindingResult bindingResult,
			Model model) throws JsonParseException, IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);

		if (command.getFile().getSize() == 0) {
			bindingResult.rejectValue("file", "NotNull");
		}
		if (bindingResult.hasErrors()) {
			return "importMetaExpedientFileForm";
		}
		
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		objectMapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
		
		String jsonString = new String(command.getFile().getBytes(), StandardCharsets.UTF_8);
		
		MetaExpedientExportDto metaExpedientExport = objectMapper.readValue(jsonString, MetaExpedientExportDto.class);
		MetaExpedientImportEditCommand metaExpedientImportEditCommand = new MetaExpedientImportEditCommand();
		fillImportEditForm(metaExpedientExport, model, entitatActual, request, metaExpedientImportEditCommand);
		
		request.getSession().setAttribute(SESSION_ATTRIBUTE_IMPORT_TEMPORAL, metaExpedientExport);
		
		model.addAttribute("tipus", EnumHelper.getOptionsForEnum(TipusClassificacioEnumDto.class, "tipus.classificacio."));
		
		return "importMetaExpedientEditForm";
	}
	
	@RequestMapping(value = "/importFitxerEditJson", method = RequestMethod.POST)
	@ResponseBody
	public List<ObjectError> importFitxerEditPostJson(
			HttpServletRequest request,
			@RequestBody String commandStr,
			BindingResult bindingResult,
			Model model) throws JsonParseException, IOException {
		
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		MetaExpedientImportEditCommand command = objectMapper.readValue(commandStr, MetaExpedientImportEditCommand.class);
		organGestorService.actualitzarOrganCodi(SessioHelper.getOrganActual(request));
		importEditValidation(request, command, bindingResult);

		if (bindingResult.hasErrors()) {
			return bindingResultToJquery(request, bindingResult);
		}
		
		try {
			
			MetaExpedientExportDto metaExpedientExport = (MetaExpedientExportDto) request.getSession().getAttribute(SESSION_ATTRIBUTE_IMPORT_TEMPORAL);
			String rolActual = RolHelper.getRolActual(request);
			EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
			
			if (command.getMetaDocuments() != null) {
				
				List<PortafirmesFluxRespostaDto> plantilles = portafirmesFluxService.recuperarPlantillesDisponibles(entitatActual.getId(), RolHelper.getRolActual(request), false);
				
				for (MetaDocumentCommand metaDocumentCommand : command.getMetaDocuments()) {
					for (MetaDocumentDto metaDocumentDto : metaExpedientExport.getMetaDocuments()) {
						if (metaDocumentDto.getId().equals(metaDocumentCommand.getId())) {
							metaDocumentDto.setPortafirmesResponsables(metaDocumentCommand.getPortafirmesResponsables());
						}
						
						if (metaDocumentDto.getPortafirmesFluxId() != null && !metaDocumentDto.getPortafirmesFluxId().isEmpty()) {
							boolean exists = false;
							if (plantilles != null) {
								for (PortafirmesFluxRespostaDto portafirmesFlux : plantilles) {
									if (portafirmesFlux.getFluxId().equals(metaDocumentDto.getPortafirmesFluxId())) {
										exists = true;
									}
								}
							}
							if (!exists) {
								MissatgesHelper.warning(
										request, 
										getMessage(
												request,
												"metaexpedient.import.controller.fluxIdNotFound",
												new Object[] {metaDocumentDto.getPortafirmesFluxId(), metaDocumentDto.getCodi()}));
										
								metaDocumentDto.setPortafirmesFluxId(null);
							}
						}
					}
				}
			}
			
			if (command.getEstats() != null) {
				for (ExpedientEstatCommand expedientEstatCommand : command.getEstats()) {
					for (ExpedientEstatDto expedientEstatDto : metaExpedientExport.getEstats()) {
						if (expedientEstatDto.getId().equals(expedientEstatCommand.getId())) {
							expedientEstatDto.setResponsableCodi(expedientEstatCommand.getResponsableCodi());
						}
					}
				}
			}
			
			if (command.getTasques() != null) {
				for (MetaExpedientTascaCommand metaExpedientTascaCommand : command.getTasques()) {
					for (MetaExpedientTascaDto metaExpedientTascaDto : metaExpedientExport.getTasques()) {
						if (metaExpedientTascaDto.getId().equals(metaExpedientTascaCommand.getId())) {
							metaExpedientTascaDto.setResponsable(metaExpedientTascaCommand.getResponsable());
						}
					}
				}
			}
			
			metaExpedientExport.setId(command.getId());
			metaExpedientExport.setCodi(command.getCodi());
			metaExpedientExport.setNom(command.getNom());
			metaExpedientExport.setDescripcio(command.getDescripcio());
			metaExpedientExport.setTipusClassificacio(command.getTipusClassificacio());
			if (command.getTipusClassificacio() == TipusClassificacioEnumDto.SIA) {
				metaExpedientExport.setClassificacio(command.getClassificacioSia());
			} else {
				metaExpedientExport.setClassificacio(command.getClassificacioId());
			}
			metaExpedientExport.setSerieDocumental(command.getSerieDocumental());
			metaExpedientExport.setInteressatObligatori(command.isInteressatObligatori());
			if (command.getOrganGestorId() != null) {
				OrganGestorDto organ = new OrganGestorDto();
				organ.setId(command.getOrganGestorId());
				metaExpedientExport.setOrganGestor(organ);
			} else {
				metaExpedientExport.setOrganGestor(null);
			}
			
			String messageSuccess = null;
			if (metaExpedientExport.getId()==null) {
				metaExpedientService.createFromImport(entitatActual.getId(), metaExpedientExport, rolActual, EntitatHelper.getOrganGestorActualId(request));
				messageSuccess = "metaexpedient.import.controller.create.ok";
			} else {
				metaExpedientService.updateFromImport(entitatActual.getId(), metaExpedientExport, rolActual, EntitatHelper.getOrganGestorActualId(request));
				messageSuccess = "metaexpedient.import.controller.update.ok";
			}

			MissatgesHelper.success(request, getMessage(request, messageSuccess, new String[] {command.getNom() }));			
			return null;
			
		} catch (Exception e) {
			
			logger.error("Error al importar procediment", e);
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"metaexpedient.import.controller.import.error",
							new Object[] { command.getNom(), ExceptionHelper.getRootCauseOrItself(e).getMessage() }),
					e);
			return null;
			
		} finally {
			request.getSession().removeAttribute(SESSION_ATTRIBUTE_IMPORT_TEMPORAL);
		}
	}
	
	private void fillImportEditForm(MetaExpedientExportDto metaExpedientExport, Model model, EntitatDto entitatActual, HttpServletRequest request, MetaExpedientImportEditCommand metaExpedientImportEditCommand) {
		
		for (MetaDocumentDto metaDocumentDto : metaExpedientExport.getMetaDocuments()) {
			if (metaDocumentDto.getPortafirmesFluxTipus() == MetaDocumentFirmaFluxTipusEnumDto.SIMPLE && metaDocumentDto.getPortafirmesResponsables() != null && metaDocumentDto.getPortafirmesResponsables().length != 0) {
				List<String> respons = new ArrayList<>();
				for (int i = 0; i < metaDocumentDto.getPortafirmesResponsables().length; i++) {
					try {
						aplicacioService.findUsuariAmbCodiDades(metaDocumentDto.getPortafirmesResponsables()[i]);
						respons.add(metaDocumentDto.getPortafirmesResponsables()[i]);
					} catch (Exception e) {
					}
				}
				metaDocumentDto.setPortafirmesResponsables(respons.toArray(new String[0]));
				
				metaExpedientImportEditCommand.getMetaDocuments().add(ConversioTipusHelper.convertir(metaDocumentDto, MetaDocumentCommand.class));
			}
		}
		
		for (ExpedientEstatDto expedientEstatDto : metaExpedientExport.getEstats()) {
			if (expedientEstatDto.getResponsableCodi() != null) {
				try {
					aplicacioService.findUsuariAmbCodiDades(expedientEstatDto.getResponsableCodi());
				} catch (Exception e) {
					Throwable root = ExceptionHelper.getRootCauseOrItself(e);
					if (root instanceof NotFoundException) {
						logger.debug("Pocediment import. Responsable per estat no trobat. " + root.getMessage());
					} else {
						logger.error("Pocediment import. Error al cercar usuari per estat", e);
					}
					
					expedientEstatDto.setResponsableCodi(null);
				}
				metaExpedientImportEditCommand.getEstats().add(ConversioTipusHelper.convertir(expedientEstatDto, ExpedientEstatCommand.class));
			}
		}
		
		for (MetaExpedientTascaDto metaExpedientTascaDto : metaExpedientExport.getTasques()) {
			if (metaExpedientTascaDto.getResponsable() != null) {
				try {
					aplicacioService.findUsuariAmbCodiDades(metaExpedientTascaDto.getResponsable());
				} catch (Exception e) {
					Throwable root = ExceptionHelper.getRootCauseOrItself(e);
					if (root instanceof NotFoundException) {
						logger.debug("Pocediment import. Responsable per tasca no trobat. " + root.getMessage());
					} else {
						logger.error("Pocediment import. Error al cercar usuari per tasca", e);
					}
					metaExpedientTascaDto.setResponsable(null);
				}
				metaExpedientImportEditCommand.getTasques().add(ConversioTipusHelper.convertir(metaExpedientTascaDto, MetaExpedientTascaCommand.class));
			}
		}
		
		metaExpedientImportEditCommand.setCodi(metaExpedientExport.getCodi());
		metaExpedientImportEditCommand.setNom(metaExpedientExport.getNom());
		metaExpedientImportEditCommand.setDescripcio(metaExpedientExport.getDescripcio());
		
		TipusClassificacioEnumDto tipus = metaExpedientExport.getTipusClassificacio() != null ? metaExpedientExport.getTipusClassificacio() : TipusClassificacioEnumDto.SIA; 
		metaExpedientImportEditCommand.setTipusClassificacio(tipus);
		if (tipus == TipusClassificacioEnumDto.SIA) {
			metaExpedientImportEditCommand.setClassificacioSia(metaExpedientExport.getClassificacio());
		} else {
			metaExpedientImportEditCommand.setClassificacioId(metaExpedientExport.getClassificacio());
		}

		metaExpedientImportEditCommand.setSerieDocumental(metaExpedientExport.getSerieDocumental());
		
		metaExpedientImportEditCommand.setComu(metaExpedientExport.isComu());
		if (!metaExpedientExport.isComu()) {
			OrganGestorDto organGestorDto = null;
			try {
				organGestorDto = organGestorService.findItemByEntitatAndCodi(entitatActual.getId(), metaExpedientExport.getOrganGestor().getCodi());
				
				if (RolHelper.isRolAmbFiltreOrgan(request)) {
					List<OrganGestorDto> organs = organGestorService.findAccessiblesUsuariActualRolAdminOrDisseny(entitatActual.getId(), organGestorDto.getId());
					if (organs == null || organs.isEmpty()) {
						throw new NotFoundException(organGestorDto.getId(), OrganGestorDto.class);
					}
				}
				metaExpedientImportEditCommand.setOrganGestorId(organGestorDto.getId());
			} catch (Exception e) {
				Throwable root = ExceptionHelper.getRootCauseOrItself(e);
				if (root instanceof NotFoundException) {
					logger.debug("Pocediment import. Organ gestor no trobat. " + root.getMessage());
				} else {
					logger.error("Pocediment import. Error al cercar organ gestor", e);
				}
				metaExpedientImportEditCommand.setOrganGestorId(null);
			}
		}
		metaExpedientImportEditCommand.setEntitatId(entitatActual.getId());
		metaExpedientImportEditCommand.setInteressatObligatori(metaExpedientExport.isInteressatObligatori());
		model.addAttribute("isObligarInteressatActiu", isObligarInteressatActiu());
		model.addAttribute(metaExpedientImportEditCommand);
		model.addAttribute("hasPermisAdmComu", hasPermisAdmComu(request));
		
		//Procediments possibles per actualitzar, depen del rol
		
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		
		PaginacioParamsDto sensePaginacio = new PaginacioParamsDto();
		sensePaginacio.setPaginaNum(0);
		sensePaginacio.setPaginaTamany(Integer.MAX_VALUE);
		
		PaginaDto<MetaExpedientDto> metaExps = metaExpedientService.findByEntitatOrOrganGestor(
				entitatActual.getId(),
				organActual == null ? null : organActual.getId(),
				new MetaExpedientFiltreDto(),
				organActual == null ? false : RolHelper.isRolAmbFiltreOrgan(request),
				sensePaginacio,
				RolHelper.getRolActual(request),
				hasPermisAdmComu(request));
		
		model.addAttribute("procedimentsActuals", metaExps.getContingut());
	}
	
	private void importEditValidation(			
			HttpServletRequest request,
			MetaExpedientImportEditCommand command,
			BindingResult bindingResult) {
		
		validator.validate(command, bindingResult);
		
		if (command.getTipusClassificacio()!=null && 
			command.getTipusClassificacio().equals(TipusClassificacioEnumDto.ID) &&
			Utils.isEmpty(command.getClassificacioId())) {
			bindingResult.reject("classificacioId", "metaexpedient.import.form.validation.notId");
		}
		
		if (command.getTipusClassificacio()!=null && 
			command.getTipusClassificacio().equals(TipusClassificacioEnumDto.SIA) &&
			Utils.isEmpty(command.getClassificacioSia())) {
			bindingResult.reject("classificacioSia", "metaexpedient.import.form.validation.notSia");
		}
		
		if (!command.isComu() && command.getOrganGestorId() == null) {
			bindingResult.reject("organGestorId", "metaexpedient.import.form.validation.organ.obligatori");
		}
		
		for (int i = 0; i < command.getMetaDocuments().size(); i++) {
			MetaDocumentCommand metaDocumentCommand = command.getMetaDocuments().get(i);
			if (metaDocumentCommand.isFirmaPortafirmesActiva() && metaDocumentCommand.getPortafirmesFluxTipus() == MetaDocumentFirmaFluxTipusEnumDto.SIMPLE && (metaDocumentCommand.getPortafirmesResponsables() == null || metaDocumentCommand.getPortafirmesResponsables().length == 0)) {
				bindingResult.rejectValue("metaDocuments[" + i + "].portafirmesResponsables", "NotNull");
			}
		}
		
		List<MetaExpedientDto> metaExpedients = metaExpedientService.findByClassificacio(
				command.getEntitatId(),
				command.getClassificacioSia());
		boolean valid = true;
		if (metaExpedients != null && !metaExpedients.isEmpty()) {
			if (command.getId() == null) {
				valid = false;
			} else {
				if (metaExpedients.size() > 1) {
					valid = false;
				} else {
					if (!command.getId().equals(metaExpedients.get(0).getId())) {
						valid = false;
					}
				}
			}
		}
		if (!valid) {
			bindingResult.reject("classificacioSia", "metaexpedient.import.form.validation.codisia.repetit");
		}
	}
	
	@RequestMapping(value = "/importRolsac", method = RequestMethod.GET)
	public String importRolsacGet(
			HttpServletRequest request,
			Model model) {
		getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);

		boolean isRolAdminOrgan = RolHelper.isRolActualAdministradorOrgan(request);
		model.addAttribute("isRolAdminOrgan", isRolAdminOrgan);
		
		model.addAttribute(new MetaExpedientImportRolsacCommand());
		
		return "importMetaExpedientRolsacForm";
	}
	
	
	@RequestMapping(value = "/importRolsac", method = RequestMethod.POST)
	public String importRolsacPost(
			HttpServletRequest request,
			@Valid MetaExpedientImportRolsacCommand command,
			BindingResult bindingResult,
			Model model) throws JsonParseException, IOException {

		if (bindingResult.hasErrors()) {
			return "importMetaExpedientRolsacForm";
		}
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		getMetaExpedient(request, null, model);
		
		String codiDir3;
		if (RolHelper.isRolActualAdministradorOrgan(request)) {
			codiDir3 = EntitatHelper.getOrganGestorActual(request).getCodi();
		} else {
			codiDir3 = entitatActual.getUnitatArrel();
		}
//		codiDir3 = "A04003003";
//		command.setClassificacio("879427");
		
		try {
			
			ProcedimentDto procedimentDto = metaExpedientService.findProcedimentByCodiSia(entitatActual.getId(), codiDir3, command.getClassificacioSia());
			
			if (procedimentDto == null) {
				return getModalControllerReturnValueWarning(
						request,
						"redirect:metaExpedient",
						"metaexpedient.form.import.rolsac.no.results",
						null);
			}
			
			if (RolHelper.isRolActualAdministradorOrgan(request)) {
				if (procedimentDto.isComu() && !organGestorService.hasPermisAdminComu(EntitatHelper.getOrganGestorActual(request).getId())) {
					MissatgesHelper.warning(
							request, 
							getMessage(
									request, 
									"metaexpedient.form.import.rolsac.no.permis.admin.coumns",
									new Object[] {command.getClassificacioSia()}));
				}
			}
			
			MetaExpedientCommand metaExpedientCommand = (MetaExpedientCommand)model.asMap().get("metaExpedientCommand");
			metaExpedientCommand.setClassificacioSia(command.getClassificacioSia());
			metaExpedientCommand.setNom(procedimentDto.getNom());
			metaExpedientCommand.setDescripcio(procedimentDto.getResum());
			metaExpedientCommand.setComu(procedimentDto.isComu());
			if (!procedimentDto.isComu()) {
				metaExpedientCommand.setOrganGestorId(procedimentDto.getOrganId());
			}
			
		} catch (Exception e) {
			logger.error("Error al importar metaexpedient desde ROLSAC", e);
			return getModalControllerReturnValueError(
					request,
					"redirect:metaExpedient",
					"metaexpedient.form.import.rolsac.error",
					new Object[] {ExceptionHelper.getRootCauseOrItself(e).getMessage()},
					e);
		}
		
		return "metaExpedientForm";
	}
	
	@RequestMapping(value = "/{metaExpedientId}/regla", method = RequestMethod.GET)
	public String getRegla(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
			MetaExpedientDto metaExpedient = metaExpedientService.findById(entitatActual.getId(), metaExpedientId);
			ReglaDistribucioDto regla = metaExpedientService.consultarReglaDistribucio(metaExpedientId);
			model.addAttribute("regla", regla);
			model.addAttribute("metaExpedient", metaExpedient);
		} catch (Exception e) {
			logger.error("Error consultant estat de la regla en Distribució", e);
			return getModalControllerReturnValueError(
					request,
					"redirect:/metaExpedient",
					"metaexpedient.controller.regla.consulta.error",
					new Object[] {ExceptionHelper.getRootCauseOrItself(e).getMessage()},
					e);

		}
		
		return "metaExpedientReglaDetall";
	}
	
	@RequestMapping(value = "/{metaExpedientId}/reglaCrear", method = RequestMethod.GET)
	public String reglaCrear(HttpServletRequest request, @PathVariable Long metaExpedientId, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);

		CrearReglaResponseDto crearReglaResponseDto = metaExpedientService.reintentarCreacioReglaDistribucio(
				entitatActual.getId(),
				metaExpedientId);
		
		String message = getMessage(
				request, 
				"metaexpedient.controller.regla.crear.result",
				new Object[] { crearReglaResponseDto.getMsgEscapeXML() });
		
		if (crearReglaResponseDto.getStatus() == StatusEnumDto.OK) {
			MissatgesHelper.success(request, message);
		} else if (crearReglaResponseDto.getStatus() == StatusEnumDto.WARNING) {
			MissatgesHelper.warning(request, message);
		} else {
			MissatgesHelper.error(request, message);
		}
		
		MetaExpedientDto metaExpedient = metaExpedientService.findById(entitatActual.getId(), metaExpedientId);
		ReglaDistribucioDto regla = metaExpedientService.consultarReglaDistribucio(metaExpedientId);
		model.addAttribute("regla", regla);
		model.addAttribute("metaExpedient", metaExpedient);
		
		return "metaExpedientReglaDetall";
		
	}
	
	@RequestMapping(value = "/{metaExpedientId}/canviarEstatReglaDistribucio/{activa}", method = RequestMethod.GET)
	public String reglaActivar(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable boolean activa,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);

		CrearReglaResponseDto crearReglaResponseDto = metaExpedientService.canviarEstatReglaDistribucio(
				metaExpedientId, 
				activa);
		
		String message = getMessage(
				request, 
				"metaexpedient.controller.regla.crear.result",
				new Object[] { crearReglaResponseDto.getMsgEscapeXML() });
		if (crearReglaResponseDto.getStatus() == StatusEnumDto.OK) {
			
			MissatgesHelper.success(
					request, 
					message);

		} else if (crearReglaResponseDto.getStatus() == StatusEnumDto.WARNING) {
			
			MissatgesHelper.warning(
					request, 
					message);
			

		} else {
			MissatgesHelper.error(
					request, 
					message);
		}
		
		MetaExpedientDto metaExpedient = metaExpedientService.findById(entitatActual.getId(), metaExpedientId);
		ReglaDistribucioDto regla = metaExpedientService.consultarReglaDistribucio(metaExpedientId);
		model.addAttribute("regla", regla);
		model.addAttribute("metaExpedient", metaExpedient);
		
		return "metaExpedientReglaDetall";
		
	}
	
	

	@RequestMapping(value = "/{metaExpedientCarpetaId}/deleteCarpeta", method = RequestMethod.GET)
	@ResponseBody
	public void deleteCarpeta(
			HttpServletRequest request, 
			@PathVariable Long metaExpedientCarpetaId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		metaExpedientService.deleteCarpetaMetaExpedient(
				entitatActual.getId(),
				metaExpedientCarpetaId);
	}

	@RequestMapping(value = "/{metaExpedientId}/new", method = RequestMethod.GET)
	public String getNewAmbPare(HttpServletRequest request, @PathVariable Long metaExpedientId, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		MetaExpedientDto metaExpedient = comprovarAccesMetaExpedient(request, metaExpedientId);
		MetaExpedientCommand command = new MetaExpedientCommand(RolHelper.isRolActualAdministradorOrgan(request));
		command.setPareId(metaExpedientId);
		command.setEntitatId(entitatActual.getId());
		model.addAttribute(command);
		fillFormModel(request, metaExpedient, model);
		return "metaExpedientForm";
	}

	@RequestMapping(value = "/{metaExpedientId}/enable", method = RequestMethod.GET)
	public String enable(HttpServletRequest request, @PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		String rolActual = RolHelper.getRolActual(request);
		boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaExpedientId);
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		comprovarAccesMetaExpedient(request, metaExpedientId);
		MetaExpedientDto metaExpedientDto = metaExpedientService.updateActiu(
				entitatActual.getId(),
				metaExpedientId,
				true, 
				rolActual, organActual != null ? organActual.getId() : null);
		
		if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
			MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
		}
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaExpedient",
				"metaexpedient.controller.activat.ok",
				new Object[] { metaExpedientDto.getNom() });
	}

	@RequestMapping(value = "/{metaExpedientId}/disable", method = RequestMethod.GET)
	public String disable(HttpServletRequest request, @PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		String rolActual = RolHelper.getRolActual(request);
		boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaExpedientId);
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		comprovarAccesMetaExpedient(request, metaExpedientId);
		MetaExpedientDto metaExpedientDto = metaExpedientService.updateActiu(
				entitatActual.getId(),
				metaExpedientId,
				false, 
				rolActual, organActual != null ? organActual.getId() : null);
		
		if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
			MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
		}
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaExpedient",
				"metaexpedient.controller.desactivat.ok",
				new Object[] { metaExpedientDto.getNom() });
	}

	@RequestMapping(value = "/{metaExpedientId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, @PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		comprovarAccesMetaExpedient(request, metaExpedientId);
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		try {
			MetaExpedientDto  metaExpedientDto = metaExpedientService.delete(
					entitatActual.getId(),
					metaExpedientId,
					organActual == null ? null : organActual.getId());
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../../metaExpedient",
					"metaexpedient.controller.esborrat.ok",
					new Object[] { metaExpedientDto.getNom() });
		} catch (Exception ex) {
			logger.error("Error al esborrar metaexpedient", ex);
			Throwable root = ExceptionHelper.getRootCauseOrItself(ex);
			if (ExceptionHelper.isExceptionOrCauseInstanceOf(ex, DataIntegrityViolationException.class) ||
					ExceptionHelper.isExceptionOrCauseInstanceOf(ex, ConstraintViolationException.class)) {
				return getAjaxControllerReturnValueError(
						request,
						"redirect:../../esborrat",
						"metaexpedient.controller.esborrar.error.fk",
						new Object[] { root.getMessage() },
						root);
			} else if (ExceptionHelper.isExceptionOrCauseInstanceOf(ex, ExisteixenExpedientsEsborratsException.class)) {
				return getAjaxControllerReturnValueError(
						request,
						"redirect:../../esborrat",
						"metaexpedient.controller.esborrar.error.fk.esborrats",
						root);
			} else if (ExceptionHelper.isExceptionOrCauseInstanceOf(ex, ExisteixenExpedientsException.class)) {
				return getAjaxControllerReturnValueError(
						request,
						"redirect:../../esborrat",
						"metaexpedient.controller.esborrar.error.fk.expedients",
						root);
			} else {
				throw ex;
			}
		}
	}
	
	@RequestMapping(value = "/{metaExpedientId}/marcarPendentRevisio", method = RequestMethod.GET)
	public String marcarPendentRevisio(HttpServletRequest request, @PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		comprovarAccesMetaExpedient(request, metaExpedientId);
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		metaExpedientService.marcarPendentRevisio(
				entitatActual.getId(),
				metaExpedientId, organActual != null ? organActual.getId() : null);
		
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaExpedient",
				"metaexpedient.controller.marcar.pendent.ok");
	}
	
	@RequestMapping(value = "/{metaExpedientId}/marcarProcesDisseny", method = RequestMethod.GET)
	public String marcarProcesDisseny(HttpServletRequest request, @PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		comprovarAccesMetaExpedient(request, metaExpedientId);
		
		metaExpedientService.marcarProcesDisseny(
				entitatActual.getId(),
				metaExpedientId, 
				EntitatHelper.getOrganGestorActualId(request));
		
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaExpedient",
				"metaexpedient.controller.marcar.disseny.ok");
	}

	@RequestMapping(value = "/findAll", method = RequestMethod.GET)
	@ResponseBody
	public List<MetaExpedientDto> findAll(HttpServletRequest request, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		return metaExpedientService.findByEntitat(entitatActual.getId());
	}
	

	
	@RequestMapping(value = "/findPerLectura", method = RequestMethod.GET)
	@ResponseBody
	public List<MetaExpedientDto> findPerLectura(
			HttpServletRequest request,
			@RequestParam(value = "organId", required = false) Long organId,
			Model model) {
		long t0 = System.currentTimeMillis();
		
		if (aplicacioService.mostrarLogsRendiment())
			logger.info("MetaExpedientController.findPerLectura start ( organId=" + organId +  ")");
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		List<MetaExpedientDto> metaExpedientsPermisLectura = metaExpedientService.findActius(
				entitatActual.getId(),
				null,
				RolHelper.getRolActual(request),
				organId != null,
				organId);
		
    	if (aplicacioService.mostrarLogsRendiment())
    		logger.info("MetaExpedientController.findPerLectura end:  " + (System.currentTimeMillis() - t0) + " ms");

		return metaExpedientsPermisLectura;
	}
	
	
	@RequestMapping(value = "/{metaExpedientId}/comentaris", method = RequestMethod.GET)
	public String comentaris(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"metaExpedient",
				metaExpedientService.findById(
						entitatActual.getId(),
						metaExpedientId));
		
		UsuariDto usuariActual = aplicacioService.getUsuariActual();
		model.addAttribute(
				"usuariActual",
				usuariActual);
		
		return "metaExpedientComentaris";
	}	
	
	
	
	@RequestMapping(value = "/{metaExpedientId}/comentaris/publicar", method = RequestMethod.POST)
	@ResponseBody
	public List<MetaExpedientComentariDto> publicarComentari(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@RequestParam String text,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		if (text != null && !text.isEmpty()) {
			metaExpedientService.publicarComentariPerMetaExpedient(entitatActual.getId(), metaExpedientId, text, RolHelper.getRolActual(request));
		}
			
		List<MetaExpedientComentariDto> coms = metaExpedientService.findComentarisPerMetaExpedient(
				entitatActual.getId(), 
				metaExpedientId,
				RolHelper.getRolActual(request));
		return coms;
	}
	

	private MetaExpedientFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		MetaExpedientFiltreCommand filtreCommand = (MetaExpedientFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new MetaExpedientFiltreCommand();
			filtreCommand.setActiu(MetaExpedientActiuEnumDto.ACTIU);
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		
		Cookie cookie = WebUtils.getCookie(request, COOKIE_PERMIS_DIRECTE);
		filtreCommand.setPermisDirecteActive(cookie != null && "true".equals(cookie.getValue()));
		
		return filtreCommand;
	}

	private void fillFormModel(HttpServletRequest request, MetaExpedientDto dto, Model model) {
		boolean isRolAdminOrgan = false;
		boolean hasPermisAdmComu = RolHelper.isRolActualAdministrador(request);
		boolean hasOrganGestor = dto != null ? dto.getOrganGestor() != null : false;

		if (RolHelper.isRolActualAdministradorOrgan(request)) {
			isRolAdminOrgan = true;
			OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
			hasPermisAdmComu = organGestorService.hasPermisAdminComu(organActual.getId());
		}
		model.addAttribute("isRolAdminOrgan", isRolAdminOrgan);
		model.addAttribute("hasPermisAdmComu", hasPermisAdmComu);
		model.addAttribute("hasOrganGestor", hasOrganGestor);
		model.addAttribute("isCarpetaDefecte", Boolean.parseBoolean(aplicacioService.propertyFindByNom(PropertyConfig.CARPETES_PER_DEFECTE)));
		model.addAttribute("isRevisioActiva", metaExpedientService.isRevisioActiva());
		model.addAttribute("isRolAdmin", RolHelper.isRolActualAdministrador(request));

		List<OrganGestorDto> organGestorsList = new ArrayList<>();
		if (RolHelper.isRolAmbFiltreOrgan(request)) {
			organGestorsList = organGestorService.findAccessiblesUsuariActualRolAdminOrDisseny(
					EntitatHelper.getEntitatActual(request).getId(),
					EntitatHelper.getOrganGestorActual(request).getId());
		} else if (RolHelper.isRolActualAdministrador(request) || RolHelper.isRolActualRevisor(request)){
			organGestorsList = organGestorService.findByEntitat(
					EntitatHelper.getEntitatActual(request).getId());
		}
		int organsSize = organGestorsList.size();
		model.addAttribute("numOrgansDisponibles", organsSize);
		if (organsSize == 1) {
			model.addAttribute("organDisponible", organGestorsList.get(0));
		}
		model.addAttribute("organsGestors", organGestorsList);
		
		model.addAttribute("tipus", EnumHelper.getOptionsForEnum(TipusClassificacioEnumDto.class, "tipus.classificacio."));

		model.addAttribute("isObligarInteressatActiu", isObligarInteressatActiu());
	}

	@RequestMapping(value = "/sincronitzar", method = RequestMethod.GET)
	public String actualitzacioAutomaticaGet(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		model.addAttribute("isUpdatingProcediments", metaExpedientService.isUpdatingProcediments(entitat));
		return "metaExpedientActualitzacioForm";
	}

	@RequestMapping(value = "/sincronitzar", method = RequestMethod.POST)
	public String actualitzacioAutomaticaPost(
			HttpServletRequest request,
			Model model) {

		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		try {
			OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
			metaExpedientService.actualitzaProcediments(
					entitat,
					organActual,
					RolHelper.isRolAmbFiltreOrgan(request),
					hasPermisAdmComu(request),
					new RequestContext(request).getLocale());
		} catch (Exception e) {
			logger.error("Error inesperat al actualitzar els procediments", e);
			model.addAttribute("errors", e.getMessage());
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			MissatgesHelper.error(request, "Error: \n" + sw.toString());
			return "metaExpedientActualitzacioForm";
		}

		return getAjaxControllerReturnValueSuccess(
				request,
				"/metaExpedientActualitzacioForm",
				"procediment.controller.update.auto.ok");
	}

	@RequestMapping(value = "/sincronitzar/progres", method = RequestMethod.GET)
	@ResponseBody
	public ProgresActualitzacioDto getProgresActualitzacio(HttpServletRequest request) {
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		return metaExpedientService.getProgresActualitzacio(entitat.getCodi());
	}
	
	private boolean isObligarInteressatActiu() {
		return aplicacioService.propertyBooleanFindByKey(PropertyConfig.PERMETRE_OBLIGAR_INTERESSAT);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(MetaExpedientController.class);
}