/**
 * 
 */
package es.caib.ripea.war.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;

import es.caib.ripea.core.api.dto.ArbreDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientEstatDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaExpedientCarpetaDto;
import es.caib.ripea.core.api.dto.MetaExpedientComentariDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaExpedientExportDto;
import es.caib.ripea.core.api.dto.MetaExpedientFiltreDto;
import es.caib.ripea.core.api.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.core.api.dto.MetaExpedientTascaDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PortafirmesFluxRespostaDto;
import es.caib.ripea.core.api.dto.ProcedimentDto;
import es.caib.ripea.core.api.dto.UsuariDto;
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
		Boolean mantenirPaginacio = Boolean.parseBoolean(request.getParameter("mantenirPaginacio"));
		if (mantenirPaginacio) {
			model.addAttribute("mantenirPaginacio", true);
		} else {
			model.addAttribute("mantenirPaginacio", false);
		}
		MetaExpedientFiltreCommand command = getFiltreCommand(request);
		model.addAttribute(command);
		model.addAttribute("isRolAdminOrgan", RolHelper.isRolActualAdministradorOrgan(request));
		model.addAttribute("isActiveGestioPermisPerAdminOrgan", Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.procediment.gestio.permis.administrador.organ")));
		
		if (RolHelper.isRolActualAdministrador(request)) {
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
		
		if (metaExpedient != null // es tracta d'una modificació
				&& RolHelper.isRolActualAdministradorOrgan(request) && metaExpedientService.isRevisioActiva() 
				&& metaExpedient.getRevisioEstat() == MetaExpedientRevisioEstatEnumDto.REVISAT) {
			MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.adminOrgan.bloquejada.alerta"));
			model.addAttribute("bloquejarCamps", true);
		}
		
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
		if (command.getRevisioEstat() == MetaExpedientRevisioEstatEnumDto.REBUTJAT && (command.getRevisioComentari() == null || command.getRevisioComentari().isEmpty())) {
			bindingResult.rejectValue("revisioComentari", "NotNull");
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

			if (rolActual.equals("IPA_ORGAN_ADMIN") && metaExpedientService.isRevisioActiva()) {
				MetaExpedientDto metaExpedient = comprovarAccesMetaExpedient(request, command.getId());
				MetaExpedientRevisioEstatEnumDto estatAnterior = metaExpedient.getRevisioEstat();
				boolean isCanviEstatDissenyAPendentByOrganAdmin = !dto.getRevisioEstat().equals(estatAnterior) 
						&& estatAnterior.equals(MetaExpedientRevisioEstatEnumDto.DISSENY) 
						&& dto.getRevisioEstat().equals(MetaExpedientRevisioEstatEnumDto.PENDENT);
				metaExpedientService.update(entitatActual.getId(), dto, rolActual, isCanviEstatDissenyAPendentByOrganAdmin, organActual != null ? organActual.getId() : null);
			} else {
				metaExpedientService.update(entitatActual.getId(), dto, rolActual, false, organActual != null ? organActual.getId() : null);
			}
			
			if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
				MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
			}
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:metaExpedient",
					"metaexpedient.controller.modificat.ok");
		} else {

			metaExpedientService.create(
					entitatActual.getId(),
					dto,
					rolActual,
					organActual != null ? organActual.getId() : null);
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:metaExpedient",
					"metaexpedient.controller.creat.ok");
		}
	}

	
	@RequestMapping(value = "/{metaExpedientId}/export", method = RequestMethod.GET)
	public String export(HttpServletRequest request, HttpServletResponse response, @PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		
		MetaExpedientDto metaExpedient = comprovarAccesMetaExpedient(request, metaExpedientId);
		
		String json = metaExpedientService.export(
				entitatActual.getId(),
				metaExpedientId,
				organActual.getId());

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
			metaExpedientExport.setClassificacioSia(command.getClassificacioSia());

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
					new Object[] { ExceptionHelper.getRootCauseOrItself(e).getMessage() });
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
		metaExpedientImportEditCommand.setClassificacioSia(metaExpedientExport.getClassificacioSia());
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
//		command.setClassificacioSia("874212");
		
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
			metaExpedientCommand.setOrganGestorId(procedimentDto.getOrganId());
			
			
		} catch (Exception e) {
			logger.error("Error al importar metaexpedient desde ROLSAC", e);
			return getModalControllerReturnValueError(
					request,
					"redirect:metaExpedient",
					"metaexpedient.form.import.rolsac.error",
					new Object[] {ExceptionHelper.getRootCauseOrItself(e).getMessage()});
		}
		
		return "metaExpedientForm";
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
		if (RolHelper.isRolActualAdministrador(request)) {
			model.addAttribute("organsGestors", organGestorService.findByEntitat(entitatActual.getId()));
		} else {
			model.addAttribute(
					"organsGestors",
					organGestorService.findAccessiblesUsuariActualRolAdmin(
							entitatActual.getId(),
							EntitatHelper.getOrganGestorActual(request).getId()));
		}
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
			if (ExceptionHelper.isExceptionOrCauseInstanceOf(ex, DataIntegrityViolationException.class) ||
					ExceptionHelper.isExceptionOrCauseInstanceOf(ex, ConstraintViolationException.class)) {
				return getAjaxControllerReturnValueError(
						request,
						"redirect:../../esborrat",
						"metaexpedient.controller.esborrar.error.fk",
						new Object[] { ExceptionHelper.getRootCauseOrItself(ex).getMessage() });
			} else if (ExceptionHelper.isExceptionOrCauseInstanceOf(ex, ExisteixenExpedientsEsborratsException.class)) {
				return getAjaxControllerReturnValueError(
						request,
						"redirect:../../esborrat",
						"metaexpedient.controller.esborrar.error.fk.esborrats");
			} else if (ExceptionHelper.isExceptionOrCauseInstanceOf(ex, ExisteixenExpedientsException.class)) {
				return getAjaxControllerReturnValueError(
						request,
						"redirect:../../esborrat",
						"metaexpedient.controller.esborrar.error.fk.expedients");
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
				metaExpedientId);
		
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
			
			logger.debug("findPerLecturaOrgan time: " + (System.currentTimeMillis() - t0) + " ms");
		} else {
			metaExpedientsPermisLectura = metaExpedientService.findActius(
					entitatActual.getId(), 
					null, 
					rolActual, 
					false, 
					null);
			logger.debug("findPerLectura time: " + (System.currentTimeMillis() - t0) + " ms");
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
			
		return metaExpedientService.findComentarisPerMetaExpedient(
				entitatActual.getId(), 
				metaExpedientId,
				RolHelper.getRolActual(request));
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
	}
	
	
	private static final Logger logger = LoggerFactory.getLogger(MetaExpedientController.class);

}
