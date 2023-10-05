/**
 * 
 */
package es.caib.ripea.war.controller;

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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.exception.ExceptionUtils;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.AlertaDto;
import es.caib.ripea.core.api.dto.CarpetaDto;
import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.ContingutLogDetallsDto;
import es.caib.ripea.core.api.dto.ContingutVistaEnumDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentEnviamentTipusEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientTascaDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.InteressatTipusEnumDto;
import es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.NodeDto;
import es.caib.ripea.core.api.dto.PermissionEnumDto;
import es.caib.ripea.core.api.dto.ResultDocumentsSenseContingut;
import es.caib.ripea.core.api.registre.RegistreTipusEnum;
import es.caib.ripea.core.api.service.AlertaService;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.DocumentEnviamentService;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.service.ExpedientInteressatService;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.api.service.ExpedientTascaService;
import es.caib.ripea.core.api.service.MetaDadaService;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.core.api.service.OrganGestorService;
import es.caib.ripea.core.api.service.URLInstruccioService;
import es.caib.ripea.plugin.notificacio.EnviamentEstat;
import es.caib.ripea.war.command.ContingutMoureCopiarEnviarCommand;
import es.caib.ripea.war.helper.BeanGeneratorHelper;
import es.caib.ripea.war.helper.CustomDatesEditor;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.ExceptionHelper;
import es.caib.ripea.war.helper.ExpedientHelper;
import es.caib.ripea.war.helper.JsonResponse;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.ModalHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;
import es.caib.ripea.war.helper.RolHelper;
import es.caib.ripea.war.helper.SessioHelper;

/**
 * Controlador per a la gestió de contenidors i mètodes compartits entre
 * diferents tipus de contingut.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ContingutController extends BaseUserOAdminOOrganController {

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
	@Autowired
	private OrganGestorService organGestorService;
	@Autowired
	private ExpedientHelper expedientHelper;
	@Autowired
	private URLInstruccioService urlInstruccioService;
	@Autowired
	private ExpedientTascaService expedientTascaService;

	@RequestMapping(value = "/contingut/{contingutId}", method = RequestMethod.GET)
	public String contingutGet(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@RequestParam(value = "tascaId", required = false) Long tascaId,
			Model model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String organCodi = organGestorService.getOrganCodiFromContingutId(contingutId);
		SessioHelper.setOrganActual(request, organCodi);
		organGestorService.actualitzarOrganCodi(organCodi);
		
		try {
			
	    	if (aplicacioService.mostrarLogsRendiment())
	    		logger.info("contingutGet start (" + contingutId + ")");
	    	
			long t1 = System.currentTimeMillis();
		
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			ContingutDto contingut = null;
			if (tascaId == null) {
				contingutService.checkIfPermitted(contingutId, RolHelper.getRolActual(request), PermissionEnumDto.READ);
				contingut = contingutService.findAmbIdUser(
						entitatActual.getId(),
						contingutId,
						true,
						true, 
						true,
						RolHelper.getRolActual(request), 
						false,
						expedientHelper.isVistaTreetablePerTipusDocuments(request), 
						expedientHelper.isVistaTreetablePerEstats(request));
			} else {
				ExpedientTascaDto expedientTascaDto = expedientTascaService.findOne(tascaId);
				contingut = expedientTascaService.findTascaExpedient(
						entitatActual.getId(),
						expedientTascaDto.getExpedient().getId(),
						expedientTascaDto.getId(),
						true,
						true);
				model.addAttribute("tascaId", tascaId);
				model.addAttribute("tascaNom", expedientTascaDto.getMetaExpedientTasca().getNom());
				model.addAttribute("tascaDescripcio", expedientTascaDto.getMetaExpedientTasca().getDescripcio());
				model.addAttribute("tascaEstat", expedientTascaDto.getEstat());
				model.addAttribute("tasca", expedientTascaDto);
			}
			
			// this is for old documents, for new documents it is saved in db on creation of document
			if (contingut.isDocument()) {
				Long tamany = ((DocumentDto) contingut).getFitxerTamany();
				if (tamany == null) {
					tamany = documentService.getAndSaveFitxerTamanyFromArxiu(contingut.getId());
					((DocumentDto) contingut).setFitxerTamany(tamany);
				}
			}

			omplirModelPerMostrarContingut(
					request,
					entitatActual,
					contingut,
					SessioHelper.desmarcarLlegit(request),
					model, 
					tascaId);
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
			model.addAttribute("isPermesEsborrarFinals", aplicacioService.propertyBooleanFindByKey("es.caib.ripea.document.esborrar.finals", true));
			model.addAttribute("isCreacioCarpetesLogica", Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.carpetes.logiques")));
			model.addAttribute("isGenerarUrlsInstruccioActiu", isGenerarUrlsInstruccioActiu());
			model.addAttribute("isNotificacioMultipleGenerarDocumentVisible", Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.notificacio.multiple.document.generat.visible")));
			model.addAttribute("isConcatentarMultiplePDFs", Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.notificacio.multiple.pdf.concatenar")));
			model.addAttribute("isExportacioExcelActiva", Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.expedient.exportacio.excel")));
			model.addAttribute("isFolderCollapsedDefault", Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.contingut.contreure.carpetes")));
			model.addAttribute("concsvBaseUrl", aplicacioService.propertyFindByNom("es.caib.ripea.concsv.base.url"));
			model.addAttribute("isExportacioInsideActiva", Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.expedient.exportar.inside")));
			
			boolean isEntitatUserAdminOrOrgan;
			if (entitatActual.isUsuariActualAdministration() || entitatActual.isUsuariActualTeOrgans()) {
				isEntitatUserAdminOrOrgan = true;
			} else {
				isEntitatUserAdminOrOrgan = false;
			}
			model.addAttribute("isEntitatUserAdminOrOrgan", isEntitatUserAdminOrOrgan);
	
			long t11 = System.currentTimeMillis();
			List<MetaDocumentDto> metaDocumentsPerCreacio = metaDocumentService.findActiusPerCreacio(
					entitatActual.getId(),
					contingutId, 
					null,
					true);
			List<MetaDocumentDto> metaDocumentsPinbal = new ArrayList<MetaDocumentDto>();
			List<MetaDocumentDto> metaDocumentsNoPinbal = new ArrayList<MetaDocumentDto>();
			for (MetaDocumentDto metaDocument : metaDocumentsPerCreacio) {
				if (metaDocument.isLeftPerCreacio()) {
					if (metaDocument.isPinbalActiu()) {
						metaDocumentsPinbal.add(metaDocument);
					} else {
						metaDocumentsNoPinbal.add(metaDocument);
					}
				}
			}
			model.addAttribute("metaDocumentsNoPinbalLeft", metaDocumentsNoPinbal);
			model.addAttribute("metaDocumentsPinbalLeft", metaDocumentsPinbal);
	
			model.addAttribute("notificacioEnviamentEstats",
					EnumHelper.getOptionsForEnum(EnviamentEstat.class,
							"notificacio.enviamentEstat.enum."));
			
	    	if (aplicacioService.mostrarLogsRendiment())
	    		logger.info("metaDocumentsPerCreacio time (" + contingutId + "):  " + (System.currentTimeMillis() - t11) + " ms");
			
	    	if (aplicacioService.mostrarLogsRendiment())
	    		logger.info("contingutGet end (" + contingutId + "):  " + (System.currentTimeMillis() - t1) + " ms");
			
			return "contingut";
		
		} catch (Exception e) {
			System.out.println("Error al obtenir detalls del contingut" +  ExceptionUtils.getStackTrace(e));
			logger.error("Error al obtenir detalls del contingut", e);
			Throwable root = ExceptionHelper.getRootCauseOrItself(e);
			if (ModalHelper.isModal(request)) {
				if (root instanceof ConnectException || (root.getMessage() != null && root.getMessage().contains("timed out"))) {
					return getModalControllerReturnValueErrorMessageText(
							request,
							"redirect:../../contingut/" + contingutId,
							getMessage(request, "contingut.controller.descarregar.error") + ": " + getMessage(request, "error.arxiu.connectTimedOut"), root);
				} else {
					return getModalControllerReturnValueErrorMessageText(
							request,
						"redirect:../../contingut/" + contingutId,
							getMessage(request, "contingut.controller.descarregar.error") + ": " + root.getMessage(), root);
				}
			} else {
				throw e;
			}
			

		}
		
		
	}
	
	@RequestMapping(value = "/contingut/{contingutId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@RequestParam(value = "contingutNavigationId", required = false) Long contingutNavigationId,
			@RequestParam(value = "tascaId", required = false) Long tascaId,
			Model model) throws IOException {

		String url = "";
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			Long pareId = contingutService.getPareId(contingutId);
			
			boolean isExpedient = pareId == null;
			if (isExpedient) {
				url = "redirect:../../expedient";
			} else {
				url = "redirect:../../contingut/" + (contingutNavigationId != null ? contingutNavigationId : pareId) + "?tascaId=" + (tascaId == null ? "" : tascaId);
			}
			
			contingutService.deleteReversible(
					entitatActual.getId(),
					contingutId, 
					RolHelper.getRolActual(request), 
					tascaId);

			deselect(request, contingutId);
			return getAjaxControllerReturnValueSuccess(
					request,
					url,
					"contingut.controller.element.esborrat.ok");

		} catch (Exception e) {
			logger.error("Error al esborrar el contingut (id=" + contingutId + ")", e);
			Throwable root = ExceptionHelper.getRootCauseOrItself(e);
			if (root instanceof ConnectException || root.getMessage().contains("timed out")) {
				return getModalControllerReturnValueErrorMessageText(
						request,
						"redirect:../../contingut/" + contingutId,
						getMessage(request, "contingut.controller.element.esborrat.error") + ": " + getMessage(request, "error.arxiu.connectTimedOut"), root);
				
			} else {
				return getModalControllerReturnValueErrorMessageText(
						request,
						"redirect:../../contingut/" + contingutId,
						getMessage(request, "contingut.controller.element.esborrat.error") + ": " + root.getMessage(), root);
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
	
	
	@RequestMapping(value = "/contingut/{contingutId}/canviVista/{vista}", method = RequestMethod.GET)
	public String canviVista(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@PathVariable ContingutVistaEnumDto vista,
			@RequestParam(value = "tascaId", required = false) Long tascaId,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		Set<Long> seleccio = new HashSet<Long>();
		RequestSessionHelper.actualitzarObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO,
				seleccio);
		expedientService.setVistaUsuariActual(vista);
		
		SessioHelper.updateContenidorVista(
				request,
				vista);
		
		return "redirect:/contingut/" + contingutId + "?tascaId=" + (tascaId == null ? "" : tascaId);
	}
	

	@RequestMapping(value = "/contingut/{contingutOrigenId}/moure", method = RequestMethod.GET)
	public String moureForm(
			HttpServletRequest request,
			@PathVariable Long contingutOrigenId,
			Model model) {
		
		organGestorService.actualitzarOrganCodi(organGestorService.getOrganCodiFromContingutId(contingutOrigenId));
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
				false, 
				null, 
				null);
		
		boolean isTheSame = false;
		if (contingutOrigen.getPare().getId().equals(contingutDestiId)) {
			isTheSame = true;
		}
		if (!isTheSame) {
			contingutService.move(
					entitatActual.getId(),
					contingutOrigenId,
					contingutDestiId, 
					RolHelper.getRolActual(request));
		}

		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../" + contingutOrigen.getExpedientPare().getId(),
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
				documentService.updateTipusDocument(
						entitatActual.getId(), 
						contingutId, 
						tipusDocumentId, 
						false, 
						null, 
						null);
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
		if (contingutService.isExpedient(contingutId)) {
			interessats = interessatService.findByExpedient(
					entitatActual.getId(),
					contingutId,
					false);
		}
		return DatatablesHelper.getDatatableResponse(
				request,
				interessats);
	}
	
	@RequestMapping(value = "/contingut/{contingutId}/{urlInstruccioId}/valor", method = RequestMethod.GET)
	@ResponseBody
	public String urlInstruccio(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@PathVariable Long urlInstruccioId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

		if (! isGenerarUrlsInstruccioActiu())
			throw new SecurityException("Es necessari activar la propietat 'es.caib.ripea.expedient.generar.urls.instruccio' per accedir a la gestió d'URLs d'instrucció");
		
		String url = urlInstruccioService.getURLInstruccio(entitatActual.getId(), contingutId, urlInstruccioId);
		
		if (! url.isEmpty())
			MissatgesHelper.success(
					request,
					getMessage(request, "url.instruccio.url.copiat.ok"));
		
		return url;
	}

	private boolean isGenerarUrlsInstruccioActiu() {
		return Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.expedient.generar.urls.instruccio"));
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
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		try {
			
			ContingutDto contingut = contingutService.findAmbIdUser(
					entitatActual.getId(),
					contingutId,
					false,
					false, 
					null, 
					null);
			model.addAttribute("contingut", contingut);
			if (contingut instanceof DocumentDto) {
				model.addAttribute("documentInvalid", !((DocumentDto) contingut).isValidacioFirmaCorrecte());
			}
			if (contingut.isReplicatDinsArxiu()) {
				model.addAttribute(
						"arxiuDetall",
						contingutService.getArxiuDetall(
								entitatActual.getId(),
								contingutId));
			}
			return "contingutArxiu";
		} catch (Exception e) {
			
			ContingutDto contingut = contingutService.findAmbIdUser(
					entitatActual.getId(),
					contingutId,
					false,
					false,
					null,
					null);
			
			logger.error("Error al consultar informació arxiu" + " (id: " + contingutId + ", uuid: " + contingut.getArxiuUuid() + ") ", e);
			Throwable root = ExceptionHelper.getRootCauseOrItself(e);
			if (root instanceof ConnectException || root.getMessage().contains("timed out")) {
				return getModalControllerReturnValueErrorMessageText(
						request,
						"redirect:../../contingut/" + contingutId,
						getMessage(request, "contingut.controller.arxiu.info.error") + ": " + getMessage(request, "error.arxiu.connectTimedOut"), root);
				
			} else {
				return getModalControllerReturnValueErrorMessageText(
						request,
						"redirect:../../contingut/" + contingutId,
						getMessage(request, "contingut.controller.arxiu.info.error") + " (uuid: " + contingut.getArxiuUuid() + "): " + root.getMessage(), root);
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


	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	    
	    
	    binder.registerCustomEditor(
	    		Date[].class,
	    		new CustomDatesEditor());
	}


	public void omplirModelPerMostrarContingut(
			HttpServletRequest request,
			EntitatDto entitatActual,
			ContingutDto contingut,
			boolean pipellaAnotacionsRegistre,
			Model model, 
			Long tascaId) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		long t1 = System.currentTimeMillis();
		
		model.addAttribute("contingut", contingut);
		model.addAttribute(
				"metaExpedients",
				metaExpedientService.findActiusAmbEntitatPerCreacio(entitatActual.getId(), null));
		model.addAttribute(
				"metaDocuments",
				metaDocumentService.findActiusPerCreacio(
						entitatActual.getId(),
						contingut.getId(), 
						null, 
						false));
		
		expedientHelper.omplirVistaActiva(request, model);
		model.addAttribute("pipellaAnotacionsRegistre", pipellaAnotacionsRegistre);
		
		if (tascaId == null) {
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


		}

		
		Set<Long> seleccio = new HashSet<Long>();
		RequestSessionHelper.actualitzarObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO,
				seleccio);
		
    	if (aplicacioService.mostrarLogsRendiment())
    		logger.info("omplirModelPerMostrarContingut time (" + contingut.getId() + "):  " + (System.currentTimeMillis() - t1) + " ms");
		
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

	@RequestMapping(value = "/documents/sense/contingut", method = RequestMethod.GET)
	@ResponseBody
	public ResultDocumentsSenseContingut arreglaDocumentsSenseContingut(HttpServletRequest request) {
		return contingutService.arreglaDocumentsSenseContingut();
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
