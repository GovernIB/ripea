/**
 * 
 */
package es.caib.ripea.war.controller;

import es.caib.ripea.core.api.dto.*;
import es.caib.ripea.core.api.registre.RegistreTipusEnum;
import es.caib.ripea.core.api.service.*;
import es.caib.ripea.plugin.notificacio.EnviamentEstat;
import es.caib.ripea.war.command.ContingutMoureCopiarEnviarCommand;
import es.caib.ripea.war.helper.*;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controlador per a la gestió de contenidors i mètodes compartits entre
 * diferents tipus de contingut.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ContingutController extends BaseUserOAdminOOrganController {

	private static final String CONTENIDOR_VISTA_ICONES = "icones";
	private static final String CONTENIDOR_VISTA_LLISTAT = "llistat";
	private static final String SESSION_ATTRIBUTE_SELECCIO = "ContingutDocumentController.session.seleccio";
	
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private ContingutService contingutService;
	@Autowired
	private MetaExpedientService metaExpedientService;
	@Autowired
	private MetaDocumentService metaDocumentService;
	@Autowired
	private ExpedientInteressatService interessatService;
	@Autowired
	private DocumentEnviamentService documentEnviamentService;	
	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private MetaDadaService metaDadaService;
	@Autowired
	private AlertaService alertaService;
	@Autowired
	private BeanGeneratorHelper beanGeneratorHelper;
	@Autowired
	private DocumentService documentService;

	@RequestMapping(value = "/contingut/{contingutId}", method = RequestMethod.GET)
	public String contingutGet(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		try {
		
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			ContingutDto contingut = contingutService.findAmbIdUser(
					entitatActual.getId(),
					contingutId,
					true,
					true, 
					RolHelper.getRolActual(request), 
					EntitatHelper.getOrganGestorActualId(request));
			omplirModelPerMostrarContingut(
					request,
					entitatActual,
					contingut,
					SessioHelper.desmarcarLlegit(request),
					model);
			model.addAttribute("isContingutDetail", false);
			model.addAttribute("isMostrarImportacio", Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.creacio.importacio.activa")));
			model.addAttribute("isCreacioCarpetesActiva", Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.creacio.carpetes.activa")));
			model.addAttribute("isMostrarCarpetesPerAnotacions", Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.mostrar.carpetes.anotacions")));
			model.addAttribute("isMostrarCopiar", Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.creacio.documents.copiarMoure.activa")));
			model.addAttribute("isMostrarVincular", Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.creacio.documents.vincular.activa")));
			model.addAttribute("isMostrarPublicar", Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.creacio.documents.publicar.activa")));
			model.addAttribute("isFirmaBiometrica", Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.documents.firma.biometrica.activa")));
			model.addAttribute("isUrlValidacioDefinida", aplicacioService.propertyFindByNom("es.caib.ripea.documents.validacio.url") != null ? true : false);
			model.addAttribute("convertirDefinitiu", Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.conversio.definitiu")));
			model.addAttribute("imprimibleNoFirmats", Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.descarregar.imprimible.nofirmats")));
			model.addAttribute("isReobrirPermes", aplicacioService.propertyBooleanFindByKey("es.caib.ripea.expedient.permetre.reobrir", true));
			model.addAttribute("isRolActualAdministrador", RolHelper.isRolActualAdministrador(request));
			model.addAttribute("isOrdenacioPermesa", aplicacioService.propertyBooleanFindByKey("es.caib.ripea.ordenacio.contingut.habilitada", false));
			model.addAttribute("isPermesModificarCustodiats", aplicacioService.propertyBooleanFindByKey("es.caib.ripea.document.modificar.custodiats", false));
			model.addAttribute("isImportacioRelacionatsActiva", Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.importacio.expedient.relacionat.activa")));
			boolean isEntitatUserAdminOrOrgan;
			if (entitatActual.isUsuariActualAdministration() || entitatActual.isUsuariActualTeOrgans()) {
				isEntitatUserAdminOrOrgan = true;
			} else {
				isEntitatUserAdminOrOrgan = false;
			}
			model.addAttribute("isEntitatUserAdminOrOrgan", isEntitatUserAdminOrOrgan);
	
			List<MetaDocumentDto> metaDocumentsPerCreacio = metaDocumentService.findActiusPerCreacio(
					entitatActual.getId(),
					contingutId, 
					null);
			List<MetaDocumentDto> metaDocumentsPinbal = new ArrayList<MetaDocumentDto>();
			List<MetaDocumentDto> metaDocumentsNoPinbal = new ArrayList<MetaDocumentDto>();
			for (MetaDocumentDto metaDocument: metaDocumentsPerCreacio) {
				if (metaDocument.isPinbalActiu()) {
					metaDocumentsPinbal.add(metaDocument);
				} else {
					metaDocumentsNoPinbal.add(metaDocument);
				}
			}
			model.addAttribute("metaDocumentsLeft", metaDocumentsNoPinbal);
			model.addAttribute("metaDocumentsPinbalLeft", metaDocumentsPinbal);
	
			model.addAttribute("notificacioEnviamentEstats",
					EnumHelper.getOptionsForEnum(EnviamentEstat.class,
							"notificacio.enviamentEstat.enum."));
			return "contingut";
		
		
		} catch (Exception e) {
			logger.error("Error al obtenir detalls del contingut", e);
			Throwable root = ExceptionHelper.getRootCauseOrItself(e);
			
			if (root instanceof ConnectException || root.getMessage().contains("timed out")) {
				return getModalControllerReturnValueErrorMessageText(
						request,
						"redirect:../../contingut/" + contingutId,
						getMessage(request, "contingut.controller.descarregar.error") + ": " + getMessage(request, "error.arxiu.connectTimedOut"));
			} else {
				return getModalControllerReturnValueErrorMessageText(
						request,
						"redirect:../../contingut/" + contingutId,
						getMessage(request, "contingut.controller.descarregar.error") + ": " + root.getMessage());
			}
		}
		
		
	}
	
	@RequestMapping(value = "/contingut/{contingutId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) throws IOException {

		String url = "";
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			ContingutDto contingut = contingutService.findAmbIdUser(
					entitatActual.getId(),
					contingutId,
					true,
					false, null, null);
			
			boolean isExpedient = contingut.getPare() == null;
			if (isExpedient) {
				url = "redirect:../../expedient";
			} else {
				url = "redirect:../../contingut/" +
						contingut.getPare().getId();
			}
			
			contingutService.deleteReversible(
					entitatActual.getId(),
					contingutId, 
					RolHelper.getRolActual(request));

			deselect(request, contingutId);
			return getAjaxControllerReturnValueSuccess(
					request,
					url,
					"contingut.controller.element.esborrat.ok");

		} catch (Exception e) {
			logger.error("Error al esborrar el contingut", e);
			Throwable root = ExceptionHelper.getRootCauseOrItself(e);
			if (root instanceof ConnectException || root.getMessage().contains("timed out")) {
				return getModalControllerReturnValueErrorMessageText(
						request,
						"redirect:../../contingut/" + contingutId,
						getMessage(request, "contingut.controller.element.esborrat.error") + ": " + getMessage(request, "error.arxiu.connectTimedOut"));
				
			} else {
				return getModalControllerReturnValueErrorMessageText(
						request,
						"redirect:../../contingut/" + contingutId,
						getMessage(request, "contingut.controller.element.esborrat.error") + ": " + root.getMessage());
			}
		}
	}
	
	private void deselect(HttpServletRequest request, Long id) {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				ExpedientController.SESSION_ATTRIBUTE_SELECCIO);
		if (seleccio != null) {
			seleccio.remove(id);
		}
	}

	@RequestMapping(value = "/contingut/{contingutId}/canviVista/icones", method = RequestMethod.GET)
	public String canviVistaLlistat(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		Set<Long> seleccio = new HashSet<Long>();
		RequestSessionHelper.actualitzarObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO,
				seleccio);
		SessioHelper.updateContenidorVista(
				request,
				CONTENIDOR_VISTA_ICONES);
		return "redirect:../../" + contingutId;
	}

	@RequestMapping(value = "/contingut/{contingutId}/canviVista/llistat", method = RequestMethod.GET)
	public String canviVistaIcones(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		Set<Long> seleccio = new HashSet<Long>();
		RequestSessionHelper.actualitzarObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO,
				seleccio);
		SessioHelper.updateContenidorVista(
				request,
				CONTENIDOR_VISTA_LLISTAT);
		return "redirect:../../" + contingutId;
	}

	@RequestMapping(value = "/contingut/{contingutOrigenId}/moure", method = RequestMethod.GET)
	public String moureForm(
			HttpServletRequest request,
			@PathVariable Long contingutOrigenId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		//Moure múltiples documents a carpetes del mateix expedient
		@SuppressWarnings("unchecked")
		Set<Long> docsIdx = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		
		omplirModelPerMoureOCopiarVincular(
				entitatActual,
				contingutOrigenId,
				docsIdx,
				model);
		ContingutDto contingutOrigen = contingutService.findAmbIdUser(
				entitatActual.getId(), 
				contingutOrigenId, 
				false, 
				false, null, null);
		ContingutMoureCopiarEnviarCommand command = new ContingutMoureCopiarEnviarCommand();
		if (docsIdx != null && !docsIdx.isEmpty() && (contingutOrigen instanceof CarpetaDto || contingutOrigen instanceof ExpedientDto)) {
			command.setOrigenIds(
					docsIdx.toArray(new Long[docsIdx.size()]));
		} else {
			command.setOrigenId(contingutOrigenId);
		}
		model.addAttribute("moureMateixExpedients", Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.creacio.documents.moure.mateix.expedient")));
		model.addAttribute(command);
		return "contingutMoureForm";
	}
	
	@RequestMapping(value = "/contingut/{contingutOrigenId}/moure", method = RequestMethod.POST)
	public String moure(
			HttpServletRequest request,
			@PathVariable Long contingutOrigenId,
			@Valid ContingutMoureCopiarEnviarCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		//Moure múltiples documents a carpetes del mateix expedient
				@SuppressWarnings("unchecked")
				Set<Long> docsIdx = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
						request,
						SESSION_ATTRIBUTE_SELECCIO);
		if (bindingResult.hasErrors()) {
			omplirModelPerMoureOCopiarVincular(
					entitatActual,
					contingutOrigenId,
					docsIdx,
					model);
			return "contingutMoureForm";
		}
		if (docsIdx != null && !docsIdx.isEmpty()) {
			for (Long docIdx : docsIdx) {
				contingutService.move(
						entitatActual.getId(),
						docIdx,
						command.getDestiId(), 
						RolHelper.getRolActual(request));
			}
		} else {
			contingutService.move(
					entitatActual.getId(),
					contingutOrigenId,
					command.getDestiId(), 
					RolHelper.getRolActual(request));
		}
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../" + contingutOrigenId,
				"contingut.controller.element.mogut.ok");
	}
	
	@RequestMapping(value = "/contingut/{contingutOrigenId}/moure/{contingutDestiId}", method = RequestMethod.GET)
	public String moureDragDrop(
			HttpServletRequest request,
			@PathVariable Long contingutOrigenId,
			@PathVariable Long contingutDestiId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutDto contingutOrigen = contingutService.findAmbIdUser(
				entitatActual.getId(),
				contingutOrigenId,
				true,
				false, null, null);
		contingutService.move(
				entitatActual.getId(),
				contingutOrigenId,
				contingutDestiId, 
				RolHelper.getRolActual(request));
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../" + contingutOrigen.getPare().getId(),
				"contingut.controller.element.mogut.ok");
	}
	
	@RequestMapping(value = "/contingut/{contingutId}/ordenar", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public void ordenar(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@RequestBody Map<Integer, Long> orderedElements) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		contingutService.order(
				entitatActual.getId(), 
				contingutId, 
				orderedElements);
	}

	@RequestMapping(value = "/contingut/{contingutOrigenId}/copiar", method = RequestMethod.GET)
	public String copiarForm(
			HttpServletRequest request,
			@PathVariable Long contingutOrigenId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		omplirModelPerMoureOCopiarVincular(
				entitatActual,
				contingutOrigenId,
				null,
				model);
		ContingutMoureCopiarEnviarCommand command = new ContingutMoureCopiarEnviarCommand();
		command.setOrigenId(contingutOrigenId);
		model.addAttribute(command);
		return "contingutCopiarForm";
	}
	
	@RequestMapping(value = "/contingut/{contingutOrigenId}/copiar", method = RequestMethod.POST)
	public String copiar(
			HttpServletRequest request,
			@PathVariable Long contingutOrigenId,
			@Valid ContingutMoureCopiarEnviarCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			omplirModelPerMoureOCopiarVincular(
					entitatActual,
					contingutOrigenId,
					null,
					model);
			return "contingutCopiarForm";
		}
		ContingutDto contingutCreat = contingutService.copy(
				entitatActual.getId(),
				contingutOrigenId,
				command.getDestiId(),
				true);
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../" + contingutCreat.getId(),
				"contingut.controller.element.copiat.ok");
	}

	@RequestMapping(value = "/contingut/{contingutOrigenId}/vincular", method = RequestMethod.GET)
	public String vincularForm(
			HttpServletRequest request,
			@PathVariable Long contingutOrigenId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		omplirModelPerMoureOCopiarVincular(
				entitatActual,
				contingutOrigenId,
				null,
				model);
		ContingutMoureCopiarEnviarCommand command = new ContingutMoureCopiarEnviarCommand();
		command.setOrigenId(contingutOrigenId);
		model.addAttribute("moureMateixExpedients", false);
		model.addAttribute(command);
		return "contingutVincularForm";
	}
	
	@RequestMapping(value = "/contingut/{contingutOrigenId}/vincular", method = RequestMethod.POST)
	public String vincular(
			HttpServletRequest request,
			@PathVariable Long contingutOrigenId,
			@Valid ContingutMoureCopiarEnviarCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			omplirModelPerMoureOCopiarVincular(
					entitatActual,
					contingutOrigenId,
					null,
					model);
			return "contingutVincularForm";
		}
		ContingutDto contingutCreat = contingutService.link(
				entitatActual.getId(),
				contingutOrigenId,
				command.getDestiId(),
				true);
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../" + contingutCreat.getId(),
				"contingut.controller.element.vinculat.ok");
	}
	
	@RequestMapping(value = "/contingut/updateTipusDocumentMassiu/{tipusDocumentId}", method = RequestMethod.GET)
	@ResponseBody
	public JsonResponse updateTipusDocumentMassiu(
			HttpServletRequest request,
			@PathVariable Long tipusDocumentId,
			Model model) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		try {
			@SuppressWarnings("unchecked")
			Set<Long> docsIdx = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO);
			for (Long contingutId: docsIdx) {
				documentService.updateTipusDocumental(
						entitatActual.getId(), 
						contingutId, 
						tipusDocumentId, 
						false);
			}
			return new JsonResponse(new Boolean(true));
			
		} catch (Exception e) {
			logger.error("Error actualitzant els documents amb el nou tipus de document", e);
			return new JsonResponse(true, e.getMessage());
		}
	}
	
	@RequestMapping(value = "/contingut/{contingutId}/errors", method = RequestMethod.GET)
	public String errors(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"contingut",
				contingutService.findAmbIdUser(
						entitatActual.getId(),
						contingutId,
						true,
						false, null, null));
		model.addAttribute(
				"errors",
				contingutService.findErrorsValidacio(
						entitatActual.getId(),
						contingutId));
		return "contingutErrors";
	}
	
	@RequestMapping(value = "/contingut/{contingutId}/alertes", method = RequestMethod.GET)
	public String alertes(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"contingut",
				contingutService.findAmbIdUser(
						entitatActual.getId(),
						contingutId,
						true,
						false, null, null));
		model.addAttribute(
				"alertes",
				contingutService.findAlertes(
						entitatActual.getId(),
						contingutId));
		return "contingutAlertes";
	}

	@RequestMapping(value = "/contingut/{contingutId}/alertes/{alertaId}/llegir", method = RequestMethod.GET)
	public String llegirAlerta(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@PathVariable Long alertaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		AlertaDto alerta = alertaService.find(alertaId);
		alerta.setLlegida(true);
		alertaService.update(alerta);
		
		List<AlertaDto> alertes = contingutService.findAlertes(
				entitatActual.getId(),
				contingutId);
		if (alertes != null && alertes.isEmpty()) {
			MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"contingut.controller.alertes.llegides"));
			return modalUrlTancar();
		}
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../../../modal/contingut/" + contingutId + "/alertes",
				"contingut.controller.alerta.llegida");
	}

	@RequestMapping(value = "/contingut/{contingutId}/interessat/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse interessatDatatable(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		List<InteressatDto> interessats = null;
		ContingutDto contingut = contingutService.findAmbIdUser(
				entitatActual.getId(),
				contingutId,
				true,
				false, null, null);
		if (contingut instanceof ExpedientDto) {
			interessats = interessatService.findByExpedient(
					entitatActual.getId(),
					contingutId,
					false);
		}
		return DatatablesHelper.getDatatableResponse(
				request,
				interessats);
	}

	@RequestMapping(value = "/contingut/{contingutId}/log", method = RequestMethod.GET)
	public String log(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"contingut",
				contingutService.findAmbIdUser(
						entitatActual.getId(),
						contingutId,
						true,
						false, null, null));
		model.addAttribute(
				"logs",
				contingutService.findLogsPerContingutUser(
						entitatActual.getId(),
						contingutId));
		model.addAttribute(
				"moviments",
				contingutService.findMovimentsPerContingutUser(
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

	@RequestMapping(value = "/contingut/{contingutId}/log/{contingutLogId}/detalls", method = RequestMethod.GET)
	@ResponseBody
	public ContingutLogDetallsDto logDetalls(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@PathVariable Long contingutLogId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return contingutService.findLogDetallsPerContingutUser(
				entitatActual.getId(),
				contingutId,
				contingutLogId);
	}

	@RequestMapping(value = "/contingut/{contingutId}/arxiu")
	public String arxiu(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			ContingutDto contingut = contingutService.findAmbIdUser(
					entitatActual.getId(),
					contingutId,
					false,
					false, null, null);
			model.addAttribute("contingut", contingut);
			if (contingut.isReplicatDinsArxiu()) {
				model.addAttribute(
						"arxiuDetall",
						contingutService.getArxiuDetall(
								entitatActual.getId(),
								contingutId));
			}
			return "contingutArxiu";
		} catch (Exception e) {
			
			logger.error("Error al consultar informació arxiu", e);
			Throwable root = ExceptionHelper.getRootCauseOrItself(e);
			if (root instanceof ConnectException || root.getMessage().contains("timed out")) {
				return getModalControllerReturnValueErrorMessageText(
						request,
						"redirect:../../contingut/" + contingutId,
						getMessage(request, "contingut.controller.arxiu.info.error") + ": " + getMessage(request, "error.arxiu.connectTimedOut"));
				
			} else {
				return getModalControllerReturnValueErrorMessageText(
						request,
						"redirect:../../contingut/" + contingutId,
						getMessage(request, "contingut.controller.arxiu.info.error") + ": " + root.getMessage());
			}
		}
	}
	
	

	@RequestMapping(value = "/contingut/{contingutId}/exportar", method = RequestMethod.GET)
	public String exportar(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long contingutId) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		FitxerDto fitxer = contingutService.exportacioEni(
				entitatActual.getId(),
				contingutId);
		writeFileToResponse(
				fitxer.getNom(),
				fitxer.getContingut(),
				response);
		return null;
	}



	@RequestMapping(value = "/contingutDetail/{contingutId}/canviVista/icones", method = RequestMethod.GET)
	public String contingutDetailCanviVistaIcones(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		SessioHelper.updateContenidorVista(
				request,
				CONTENIDOR_VISTA_ICONES);
		return "redirect:../../" + contingutId;
	}

	@RequestMapping(value = "/contingutDetail/{contingutId}/canviVista/llistat", method = RequestMethod.GET)
	public String contingutDetailCanviVistaLlistat(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		SessioHelper.updateContenidorVista(
				request,
				CONTENIDOR_VISTA_LLISTAT);
		return "redirect:../../" + contingutId;
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}



	public void omplirModelPerMostrarContingut(
			HttpServletRequest request,
			EntitatDto entitatActual,
			ContingutDto contingut,
			boolean pipellaAnotacionsRegistre,
			Model model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		model.addAttribute("contingut", contingut);
		model.addAttribute(
				"metaExpedients",
				metaExpedientService.findActiusAmbEntitatPerCreacio(entitatActual.getId(), null));
		model.addAttribute(
				"metaDocuments",
				metaDocumentService.findActiusPerCreacio(
						entitatActual.getId(),
						contingut.getId(), 
						null));
		
		if (contingut instanceof CarpetaDto) {
			contingut = contingut.getExpedientPare();
		}
		if (contingut instanceof ExpedientDto) {
			model.addAttribute("relacionats", expedientService.relacioFindAmbExpedient(
					entitatActual.getId(),
					contingut.getId()));
			
			model.addAttribute(
					"interessatsCount",
					interessatService.findByExpedient(
							entitatActual.getId(),
							contingut.getId(),
							false).size());			
			model.addAttribute("notificacionsCount", documentEnviamentService.enviamentsCount(
					entitatActual.getId(),
					contingut.getId(), DocumentEnviamentTipusEnumDto.NOTIFICACIO));
			
			model.addAttribute("publicacionsCount", documentEnviamentService.enviamentsCount(
					entitatActual.getId(),
					contingut.getId(), DocumentEnviamentTipusEnumDto.PUBLICACIO));
		}
		if (contingut instanceof ExpedientDto || contingut instanceof DocumentDto) {
			model.addAttribute(
					"metaDades",
					metaDadaService.findByNode(
							entitatActual.getId(),
							contingut.getId()));
			model.addAttribute(
					"dadesCommand",
					beanGeneratorHelper.generarCommandDadesNode(
							entitatActual.getId(),
							contingut.getId(),
							((NodeDto)contingut).getDades()));
		} 

		String contingutVista = SessioHelper.getContenidorVista(request);
		if (contingutVista == null)
			contingutVista = CONTENIDOR_VISTA_LLISTAT;
		model.addAttribute(
				"vistaIcones",
				new Boolean(CONTENIDOR_VISTA_ICONES.equals(contingutVista)));
		model.addAttribute(
				"vistaLlistat",
				new Boolean(CONTENIDOR_VISTA_LLISTAT.equals(contingutVista)));
		model.addAttribute(
				"registreTipusEnumOptions",
				EnumHelper.getOptionsForEnum(
						RegistreTipusEnum.class,
						"registre.anotacio.tipus.enum."));
		model.addAttribute(
				"notificacioEstatEnumOptions",
				EnumHelper.getOptionsForEnum(
						DocumentEnviamentEstatEnumDto.class,
						"notificacio.estat.enum.",
						new Enum<?>[] {DocumentEnviamentEstatEnumDto.PROCESSAT}));
		model.addAttribute(
				"publicacioEstatEnumOptions",
				EnumHelper.getOptionsForEnum(
						DocumentEnviamentEstatEnumDto.class,
						"publicacio.estat.enum.",
						new Enum<?>[] {
							DocumentEnviamentEstatEnumDto.ENVIAT,
							DocumentEnviamentEstatEnumDto.PROCESSAT,
							DocumentEnviamentEstatEnumDto.CANCELAT}));
		model.addAttribute(
				"interessatTipusEnumOptions",
				EnumHelper.getOptionsForEnum(
						InteressatTipusEnumDto.class,
						"interessat.tipus.enum."));
		model.addAttribute(
				"pluginArxiuActiu",
				aplicacioService.isPluginArxiuActiu());
		model.addAttribute("pipellaAnotacionsRegistre", pipellaAnotacionsRegistre);
		
		Set<Long> seleccio = new HashSet<Long>();
		RequestSessionHelper.actualitzarObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO,
				seleccio);
	}

	private void omplirModelPerMoureOCopiarVincular(
			EntitatDto entitatActual,
			Long contingutOrigenId,
			Set<Long> docsIdx,
			Model model) {
		ContingutDto contingutOrigen = contingutService.findAmbIdUser(
				entitatActual.getId(),
				contingutOrigenId,
				true,
				false, null, null);
		if (docsIdx != null && !docsIdx.isEmpty() && (contingutOrigen instanceof CarpetaDto || contingutOrigen instanceof ExpedientDto)) {
			List<ContingutDto> documentsOrigen = new ArrayList<ContingutDto>();
			for (Long docIdx : docsIdx) {
				ContingutDto contingut = contingutService.findAmbIdUser(entitatActual.getId(), docIdx, false, false, null, null);
				documentsOrigen.add(contingut);
			}
			model.addAttribute(
					"documentsOrigen",
					documentsOrigen);
		}
		model.addAttribute(
				"contingutOrigen",
				contingutOrigen);
	}

	@RequestMapping(value = "/contingut/orfes/delete", method = RequestMethod.GET)
	@ResponseBody
	public String netejaContingutsOrfes(HttpServletRequest request) {
		Boolean result = contingutService.netejaContingutsOrfes();
		if (!result) {
			logger.error("Procés de neteja de continguts orfes executat amb error");
			return getMessage(request, "contingut.orfe.delete.ko");
		}
		logger.info("Procés de neteja de continguts orfes executat correctament");
		return getMessage(request, "contingut.orfe.delete.ok");
	}

//	@PostConstruct
	public void netejaContingutsOrfes() {
		try {
			Boolean result = contingutService.netejaContingutsOrfes();
			if (result) {
				logger.info("Procés de neteja de continguts orfes executat correctament");
				return;
			}
		} catch (Exception e) {}
		logger.error("Procés de neteja de continguts orfes executat amb error");
	}

	private static final Logger logger = LoggerFactory.getLogger(ContingutController.class);

}
