/**
 * 
 */
package es.caib.ripea.war.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import es.caib.ripea.core.api.dto.*;
import es.caib.ripea.core.api.exception.ExisteixenExpedientsEsborratsException;
import es.caib.ripea.core.api.exception.ExisteixenExpedientsException;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.core.api.service.OrganGestorService;
import es.caib.ripea.core.api.service.PortafirmesFluxService;
import es.caib.ripea.war.command.ExpedientEstatCommand;
import es.caib.ripea.war.command.FileCommand;
import es.caib.ripea.war.command.MetaDocumentCommand;
import es.caib.ripea.war.command.MetaExpedientCommand;
import es.caib.ripea.war.command.MetaExpedientFiltreCommand;
import es.caib.ripea.war.command.MetaExpedientImportEditCommand;
import es.caib.ripea.war.command.MetaExpedientImportRolsacCommand;
import es.caib.ripea.war.command.MetaExpedientTascaCommand;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.EntitatHelper;
import es.caib.ripea.war.helper.ExceptionHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;
import es.caib.ripea.war.helper.RolHelper;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

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

	@Autowired
	private MetaExpedientService metaExpedientService;
	@Autowired
	private OrganGestorService organGestorService;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private PortafirmesFluxService portafirmesFluxService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);

		MetaExpedientFiltreCommand command = getFiltreCommand(request);
		model.addAttribute(command);
		boolean isRolAdmin = RolHelper.isRolActualAdministrador(request);
		model.addAttribute("isRolAdmin", isRolAdmin);
		model.addAttribute("isRolAdminOrgan", RolHelper.isRolActualAdministradorOrgan(request));
		model.addAttribute("isActiveGestioPermisPerAdminOrgan", Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.procediment.gestio.permis.administrador.organ")));
		
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
		getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
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
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		MetaExpedientFiltreCommand filtreCommand = getFiltreCommand(request);
		
		MetaExpedientFiltreDto filtreDto = filtreCommand.asDto();
		filtreDto.setRevisioEstats(new MetaExpedientRevisioEstatEnumDto[] { filtreCommand.getRevisioEstat() });
		
		PaginaDto<MetaExpedientDto> metaExps = metaExpedientService.findByEntitatOrOrganGestor(
				entitatActual.getId(),
				organActual == null ? null : organActual.getId(),
				filtreDto,
				organActual == null ? false : RolHelper.isRolActualAdministradorOrgan(request),
				DatatablesHelper.getPaginacioDtoFromRequest(request),
				rolActual,
				hasPermisAdmComu(request));
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(request, metaExps, "id");
		return dtr;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(HttpServletRequest request, Model model) {
		return get(request, null, model);
	}

	@RequestMapping(value = "/{metaExpedientId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		
		getMetaExpedient(
				request,
				metaExpedientId,
				model);
		
		return "metaExpedientForm";
	}
	
	public void getMetaExpedient(
			HttpServletRequest request,
			Long metaExpedientId,
			Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);

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
		}
		command.setRolAdminOrgan(isRolActualAdminOrgan);
		command.setEntitatId(entitatActual.getId());
		model.addAttribute(command);
		boolean isCarpetesDefecte = Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.carpetes.defecte"));
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
		
		model.addAttribute("isDocumentsGeneralsEnabled", aplicacioService.propertyBooleanFindByKey("es.caib.ripea.habilitar.documentsgenerals", false));
		
		fillFormModel(
				request,
				metaExpedient,
				model);
		
	}
	
	
	@RequestMapping(method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid MetaExpedientCommand command,
			BindingResult bindingResult,
			Model model) throws JsonMappingException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		MetaExpedientDto dto = command.asDto();
		if (!command.isComu() && command.getOrganGestorId() == null) {
			bindingResult.rejectValue("organGestorId", "NotNull");
		}
		if (command.isComu() && !hasPermisAdmComu(request)) {
			bindingResult.reject("metaexpedient.controller.comu.permis.error");
		}
		if (bindingResult.hasErrors()) {
			fillFormModel(request, dto, model);
			return "metaExpedientForm";
		}
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
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
					"metaexpedient.controller.modificat.ok");
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
								"metaexpedient.controller.creat.ok"));
				
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
						new String[] {throwable.getMessage()},
						throwable);
			}
		}
	}
	

	
	@RequestMapping(value = "/{metaExpedientId}/export", method = RequestMethod.GET)
	public String export(HttpServletRequest request, HttpServletResponse response, @PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		
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
		getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);

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
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);

		if (command.getFile().getSize() == 0) {
			bindingResult.rejectValue("file", "NotNull");
		}
		if (bindingResult.hasErrors()) {
			return "importMetaExpedientFileForm";
		}
		
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		objectMapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));

		MetaExpedientExportDto metaExpedientExport = objectMapper.readValue(command.getFile().getBytes(), MetaExpedientExportDto.class);
		MetaExpedientImportEditCommand metaExpedientImportEditCommand = new MetaExpedientImportEditCommand();
		fillImportEditForm(metaExpedientExport, model, entitatActual, request, metaExpedientImportEditCommand);
		
		request.getSession().setAttribute(SESSION_ATTRIBUTE_IMPORT_TEMPORAL, metaExpedientExport);
		
		return "importMetaExpedientEditForm";
		
	}
	
	
	@RequestMapping(value = "/importFitxerEdit", method = RequestMethod.POST)
	public String importFitxerEditPost(
			HttpServletRequest request,
			@Valid MetaExpedientImportEditCommand command,
			BindingResult bindingResult,
			Model model) throws JsonParseException, IOException {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		importEditValidation(request, command, bindingResult);
		
		MetaExpedientExportDto metaExpedientExport = (MetaExpedientExportDto) request.getSession().getAttribute(SESSION_ATTRIBUTE_IMPORT_TEMPORAL);
		
		if (bindingResult.hasErrors()) {
			model.addAttribute("hasPermisAdmComu", hasPermisAdmComu(request));
			return "importMetaExpedientEditForm";
		}
		
		
		try {
			if (command.getMetaDocuments() != null) {
				for (MetaDocumentCommand metaDocumentCommand : command.getMetaDocuments()) {
					for (MetaDocumentDto metaDocumentDto : metaExpedientExport.getMetaDocuments()) {
						if (metaDocumentDto.getId().equals(metaDocumentCommand.getId())) {
							metaDocumentDto.setPortafirmesResponsables(metaDocumentCommand.getPortafirmesResponsables());
						}
						
						if (metaDocumentDto.getPortafirmesFluxId() != null && !metaDocumentDto.getPortafirmesFluxId().isEmpty()) {
							boolean exists = false;
							List<PortafirmesFluxRespostaDto> plantilles = portafirmesFluxService.recuperarPlantillesDisponibles(false);
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
			
			metaExpedientExport.setCodi(command.getCodi());
			metaExpedientExport.setNom(command.getNom());
			metaExpedientExport.setDescripcio(command.getDescripcio());
			metaExpedientExport.setClassificacioSia(command.getClassificacioSia());
			metaExpedientExport.setSerieDocumental(command.getSerieDocumental());

			if (command.getOrganGestorId() != null) {
				OrganGestorDto organ = new OrganGestorDto();
				organ.setId(command.getOrganGestorId());
				metaExpedientExport.setOrganGestor(organ);
			} else {
				metaExpedientExport.setOrganGestor(null);
			}
			
			metaExpedientService.createFromImport(entitatActual.getId(), metaExpedientExport, rolActual, EntitatHelper.getOrganGestorActualId(request));
			

			return getModalControllerReturnValueSuccess(
					request,
					"redirect:metaExpedient",
					"metaexpedient.import.controller.import.ok");
			
		} catch (Exception e) {
			
			logger.error("Error al importar procediment", e);
			return getModalControllerReturnValueError(
					request,
					"redirect:metaExpedient",
					"metaexpedient.import.controller.import.error",
					new Object[] { ExceptionHelper.getRootCauseOrItself(e).getMessage() }, e);
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
		metaExpedientImportEditCommand.setClassificacioSia(metaExpedientExport.getClassificacioSia());
		metaExpedientImportEditCommand.setSerieDocumental(metaExpedientExport.getSerieDocumental());
		
		metaExpedientImportEditCommand.setComu(metaExpedientExport.isComu());
		if (!metaExpedientExport.isComu()) {
			OrganGestorDto organGestorDto = null;
			try {
				organGestorDto = organGestorService.findItemByEntitatAndCodi(entitatActual.getId(), metaExpedientExport.getOrganGestor().getCodi());
				
				if (RolHelper.isRolActualAdministradorOrgan(request)) {
					List<OrganGestorDto> organs = organGestorService.findAccessiblesUsuariActualRolAdmin(entitatActual.getId(), organGestorDto.getId());
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
		model.addAttribute(metaExpedientImportEditCommand);
		model.addAttribute("hasPermisAdmComu", hasPermisAdmComu(request));

	}
	
	
	private void importEditValidation(			
			HttpServletRequest request,
			MetaExpedientImportEditCommand command,
			BindingResult bindingResult) {
		if (!command.isComu() && command.getOrganGestorId() == null) {
			bindingResult.reject("metaexpedient.import.form.validation.organ.obligatori");
		}
		
		for (int i = 0; i < command.getMetaDocuments().size(); i++) {
			MetaDocumentCommand metaDocumentCommand = command.getMetaDocuments().get(i);
			if (metaDocumentCommand.isFirmaPortafirmesActiva() && metaDocumentCommand.getPortafirmesFluxTipus() == MetaDocumentFirmaFluxTipusEnumDto.SIMPLE && (metaDocumentCommand.getPortafirmesResponsables() == null || metaDocumentCommand.getPortafirmesResponsables().length == 0)) {
				bindingResult.rejectValue("metaDocuments[" + i + "].portafirmesResponsables", "NotNull");
			}
		}
		
		List<MetaExpedientDto> metaExpedients = metaExpedientService.findByCodiSia(command.getEntitatId(),
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
			bindingResult.reject("metaexpedient.import.form.validation.codisia.repetit");
		}
	}
	
	
	
	
	@RequestMapping(value = "/importRolsac", method = RequestMethod.GET)
	public String importRolsacGet(
			HttpServletRequest request,
			Model model) {
		getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);

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
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);

		getMetaExpedient(
				request,
				null,
				model);
		
		
		String codiDir3;
		if (RolHelper.isRolActualAdministradorOrgan(request)) {
			codiDir3 = EntitatHelper.getOrganGestorActual(request).getCodi();
		} else {
			codiDir3 = entitatActual.getUnitatArrel();
		}
//		codiDir3 = "A04003003";
//		command.setClassificacioSia("879427");
		
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
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		
		MetaExpedientDto metaExpedient = metaExpedientService.findById(entitatActual.getId(), metaExpedientId);
		model.addAttribute("metaExpedient", metaExpedient);
		return "metaExpedientReglaDetall";
	}
	
	@RequestMapping(value = "/{metaExpedientId}/reglaCrear", method = RequestMethod.GET)
	public String reglaCrear(HttpServletRequest request, @PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);

		CrearReglaResponseDto crearReglaResponseDto = metaExpedientService.reintentarCreacioReglaDistribucio(
				entitatActual.getId(),
				metaExpedientId);

		if (crearReglaResponseDto.getStatus() == StatusEnumDto.OK) {
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../metaExpedient",
					"metaexpedient.controller.regla.crear.result",
					new Object[] { crearReglaResponseDto.getMsgEscapeXML() });

		} else if (crearReglaResponseDto.getStatus() == StatusEnumDto.WARNING) {
			return getModalControllerReturnValueWarning(
					request,
					"redirect:../../metaExpedient",
					"metaexpedient.controller.regla.crear.result",
					new Object[] { crearReglaResponseDto.getMsgEscapeXML() });
		} else {
			return getModalControllerReturnValueError(
					request,
					"redirect:../../metaExpedient",
					"metaexpedient.controller.regla.crear.result",
					new Object[] { crearReglaResponseDto.getMsgEscapeXML() },
					null);
		}
		
	}
	
	

	@RequestMapping(value = "/{metaExpedientCarpetaId}/deleteCarpeta", method = RequestMethod.GET)
	@ResponseBody
	public void deleteCarpeta(
			HttpServletRequest request, 
			@PathVariable Long metaExpedientCarpetaId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		metaExpedientService.deleteCarpetaMetaExpedient(
				entitatActual.getId(),
				metaExpedientCarpetaId);
	}


	private boolean hasPermisAdmComu(HttpServletRequest request) {
		boolean hasPermisAdmComu = RolHelper.isRolActualAdministrador(request);
		if (RolHelper.isRolActualAdministradorOrgan(request)) {
			OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
			hasPermisAdmComu = organGestorService.hasPermisAdminComu(organActual.getId());
		}
		return hasPermisAdmComu;
	}

	@RequestMapping(value = "/{metaExpedientId}/new", method = RequestMethod.GET)
	public String getNewAmbPare(HttpServletRequest request, @PathVariable Long metaExpedientId, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		
		MetaExpedientDto metaExpedient = comprovarAccesMetaExpedient(request, metaExpedientId);
		MetaExpedientCommand command = new MetaExpedientCommand(RolHelper.isRolActualAdministradorOrgan(request));
		command.setPareId(metaExpedientId);
		command.setEntitatId(entitatActual.getId());
		model.addAttribute(command);
		fillFormModel(request, metaExpedient, model);
//		if (RolHelper.isRolActualAdministrador(request)) {
//			model.addAttribute("organsGestors", organGestorService.findByEntitat(entitatActual.getId()));
//		} else {
//			model.addAttribute(
//					"organsGestors",
//					organGestorService.findAccessiblesUsuariActualRolAdmin(
//							entitatActual.getId(),
//							EntitatHelper.getOrganGestorActual(request).getId()));
//		}
		return "metaExpedientForm";
	}

	@RequestMapping(value = "/{metaExpedientId}/enable", method = RequestMethod.GET)
	public String enable(HttpServletRequest request, @PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaExpedientId);
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		comprovarAccesMetaExpedient(request, metaExpedientId);
		metaExpedientService.updateActiu(
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
				"metaexpedient.controller.activat.ok");
	}

	@RequestMapping(value = "/{metaExpedientId}/disable", method = RequestMethod.GET)
	public String disable(HttpServletRequest request, @PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaExpedientId);
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		comprovarAccesMetaExpedient(request, metaExpedientId);
		metaExpedientService.updateActiu(
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
				"metaexpedient.controller.desactivat.ok");
	}

	@RequestMapping(value = "/{metaExpedientId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, @PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		comprovarAccesMetaExpedient(request, metaExpedientId);
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		try {
			metaExpedientService.delete(
					entitatActual.getId(),
					metaExpedientId,
					organActual == null ? null : organActual.getId());
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../../metaExpedient",
					"metaexpedient.controller.esborrat.ok");
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
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
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
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
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
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		return metaExpedientService.findByEntitat(entitatActual.getId());
	}
	

	
	@RequestMapping(value = "/findPerLectura", method = RequestMethod.GET)
	@ResponseBody
	public List<MetaExpedientDto> findPerLectura(
			HttpServletRequest request,
			@RequestParam(value = "organId", required = false) Long organId,
			Model model) {
		long t0 = System.currentTimeMillis();
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		List<MetaExpedientDto> metaExpedientsPermisLectura = new ArrayList<MetaExpedientDto>();
		if (organId != null) {
			metaExpedientsPermisLectura = metaExpedientService.findActius(
					entitatActual.getId(), 
					null, 
					rolActual, 
					true, 
					organId);
			
			logger.trace("findPerLecturaOrgan time: " + (System.currentTimeMillis() - t0) + " ms");
		} else {
			metaExpedientsPermisLectura = metaExpedientService.findActius(
					entitatActual.getId(), 
					null, 
					rolActual, 
					false, 
					null);
			logger.trace("findPerLectura time: " + (System.currentTimeMillis() - t0) + " ms");
		}

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
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
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
		model.addAttribute("isCarpetaDefecte", Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.carpetes.defecte")));
		model.addAttribute("isRevisioActiva", metaExpedientService.isRevisioActiva());
		model.addAttribute("isRolAdmin", RolHelper.isRolActualAdministrador(request));

		List<OrganGestorDto> organGestorsList = new ArrayList<>();
		if (RolHelper.isRolActualAdministradorOrgan(request)) {
			organGestorsList = organGestorService.findAccessiblesUsuariActualRolAdmin(
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
			metaExpedientService.actualitzaProcediments(entitat, request.getLocale());
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
	
	private static final Logger logger = LoggerFactory.getLogger(MetaExpedientController.class);

}
