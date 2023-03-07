/**
 * 
 */
package es.caib.ripea.war.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.fundaciobit.plugins.signature.api.FileInfoSignature;
import org.fundaciobit.plugins.signature.api.StatusSignature;
import org.fundaciobit.plugins.signature.api.StatusSignaturesSet;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.DigitalitzacioEstatDto;
import es.caib.ripea.core.api.dto.DigitalitzacioResultatDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiTipoDocumentalEnumDto;
import es.caib.ripea.core.api.dto.DocumentPortafirmesDto;
import es.caib.ripea.core.api.dto.DocumentTipusFirmaEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientTascaDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.FitxerTemporalDto;
import es.caib.ripea.core.api.dto.InteressatTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.core.api.dto.NtiOrigenEnumDto;
import es.caib.ripea.core.api.dto.SignatureInfoDto;
import es.caib.ripea.core.api.dto.TascaEstatEnumDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.registre.RegistreTipusEnum;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.DigitalitzacioService;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.service.ExpedientTascaService;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.core.api.service.OrganGestorService;
import es.caib.ripea.war.command.DocumentCommand;
import es.caib.ripea.war.command.DocumentCommand.CreateDigital;
import es.caib.ripea.war.command.DocumentCommand.CreateFirmaSeparada;
import es.caib.ripea.war.command.DocumentCommand.DocumentFisicOrigenEnum;
import es.caib.ripea.war.command.DocumentCommand.UpdateDigital;
import es.caib.ripea.war.command.ExpedientPeticioFiltreCommand;
import es.caib.ripea.war.command.PassarelaFirmaEnviarCommand;
import es.caib.ripea.war.command.PortafirmesEnviarCommand;
import es.caib.ripea.war.command.UsuariTascaRebuigCommand;
import es.caib.ripea.war.helper.ArxiuTemporalHelper;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.ExceptionHelper;
import es.caib.ripea.war.helper.ExpedientHelper;
import es.caib.ripea.war.helper.FitxerTemporalHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.ModalHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;
import es.caib.ripea.war.helper.RolHelper;
import es.caib.ripea.war.helper.SessioHelper;
import es.caib.ripea.war.passarelafirma.SignaturesSetExtend;
import es.caib.ripea.war.passarelafirma.PassarelaFirmaHelper;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador per al llistat d'expedients tasques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
// TODO: merge repeatable methods with ContingutController
@Slf4j
@Controller
@RequestMapping("/usuariTasca")
public class UsuariTascaController extends BaseUserController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "ExpedientTascaController.session.filtre";
	private static final String SESSION_ATTRIBUTE_TRANSACCIOID = "DocumentController.session.transaccioID";
	
	private static final String SESSION_ATTRIBUTE_RETURN_SCANNED = "DigitalitzacioController.session.scanned";
	private static final String SESSION_ATTRIBUTE_RETURN_SIGNED = "DigitalitzacioController.session.signed";
	private static final String SESSION_ATTRIBUTE_RETURN_IDTRANSACCIO = "DigitalitzacioController.session.idTransaccio";

	@Autowired
	private ExpedientTascaService expedientTascaService;
	@Autowired
	private ContingutService contingutService;
	@Autowired
	private ServletContext servletContext;
	@Autowired
	private DocumentService documentService;
	@Autowired
	private MetaDocumentService metaDocumentService;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private ArxiuTemporalHelper arxiuTemporalHelper;
	@Autowired
	private MetaExpedientService metaExpedientService;
	@Autowired
	private PassarelaFirmaHelper passarelaFirmaHelper;
	@Autowired
	private DigitalitzacioService digitalitzacioService;
	@Autowired
	private OrganGestorService organGestorService;
	@Autowired
	private ExpedientHelper expedientHelper;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(
				getFiltreCommand(request));
		return "usuariTascaList";
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
		return "redirect:expedientTasca";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		return DatatablesHelper.getDatatableResponse(
				request,
				expedientTascaService.findAmbAuthentication(
						entitatActual.getId(), DatatablesHelper.getPaginacioDtoFromRequest(request)));		
	}
	
	@RequestMapping(value = "/{expedientTascaId}/tramitar", method = RequestMethod.GET)
	public String tramitarTasca(
			HttpServletRequest request,
			@PathVariable Long expedientTascaId,
			Model model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ExpedientTascaDto expedientTascaDto = expedientTascaService.findOne(expedientTascaId);

		ContingutDto expedient = expedientTascaService.findTascaExpedient(
				entitatActual.getId(),
				expedientTascaDto.getExpedient().getId(),
				expedientTascaDto.getId(),
				true,
				true);
		
		omplirModelPerMostrarContingut(
				request,
				entitatActual,
				expedient,
				SessioHelper.desmarcarLlegit(request),
				model);
		
		model.addAttribute("tascaId", expedientTascaId);
		model.addAttribute("tascaNom", expedientTascaDto.getMetaExpedientTasca().getNom());
		model.addAttribute("tascaDescripcio", expedientTascaDto.getMetaExpedientTasca().getDescripcio());
		model.addAttribute("tascaEstat", expedientTascaDto.getEstat());
		model.addAttribute("tasca", expedientTascaDto);
		model.addAttribute("isOrdenacioPermesa", aplicacioService.propertyBooleanFindByKey("es.caib.ripea.ordenacio.contingut.habilitada", false));
		return "contingut";
	}
	
	@RequestMapping(value = "/{expedientTascaId}/iniciar", method = RequestMethod.GET)
	public String expedientTascaIniciar(
			HttpServletRequest request,
			@PathVariable Long expedientTascaId,
			@RequestParam(value = "redirectATasca", required = false) Boolean redirectATasca,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		expedientTascaService.canviarTascaEstat(expedientTascaId, TascaEstatEnumDto.INICIADA, null);
		
		return getAjaxControllerReturnValueSuccess(
				request,
				redirectATasca != null && redirectATasca == true ? "redirect:/usuariTasca/" + expedientTascaId + "/tramitar" : "redirect:/usuariTasca",
				"expedient.tasca.controller.iniciada.ok");
		
	}
	
	
	@RequestMapping(value = "/{expedientTascaId}/rebutjar", method = RequestMethod.GET)
	public String getExpedientTascaDetall(
			HttpServletRequest request,
			@PathVariable Long expedientTascaId,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		expedientTascaService.findOne(expedientTascaId);
		
		UsuariTascaRebuigCommand command = new UsuariTascaRebuigCommand();
		command.setId(expedientTascaId);
		model.addAttribute(
				"usuariTascaRebuigCommand",
				command);
		
		return "usuariTascaRebuigForm";
	}
	
	@RequestMapping(value = "/rebutjar", method = RequestMethod.POST)
	public String rebutjarPost(
			HttpServletRequest request,
			@Valid UsuariTascaRebuigCommand command,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute(
					"usuariTascaRebuigCommand",
					command);
			return "usuariTascaRebuigForm";
		}
		
		expedientTascaService.canviarTascaEstat(
				command.getId(),
				TascaEstatEnumDto.REBUTJADA,
				command.getMotiu());
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:/usuariTasca",
				"expedient.tasca.controller.rebutjada.ok");
	}
	
	
	

	@RequestMapping(value = "/{expedientTascaId}/finalitzar", method = RequestMethod.GET)
	public String expedientTascaFinalitzar(
			HttpServletRequest request,
			@PathVariable Long expedientTascaId,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		expedientTascaService.canviarTascaEstat(expedientTascaId, TascaEstatEnumDto.FINALITZADA, null);
		
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:/usuariTasca",
				"expedient.tasca.controller.finalitzada.ok");
	}
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}
	
	@RequestMapping(value = "/{tascaId}/pare/{pareId}/document/new", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long tascaId,
			@PathVariable Long pareId,
			Model model) throws ClassNotFoundException, IOException {
		return get(request, tascaId, pareId, null, model);
	}
	@RequestMapping(value = "/{tascaId}/pare/{pareId}/document/{documentId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long tascaId,
			@PathVariable Long pareId,
			@PathVariable Long documentId,
			Model model) throws ClassNotFoundException, IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DocumentDto document = null;
		if (documentId != null) {
			document = expedientTascaService.findDocumentById(
					entitatActual.getId(),
					tascaId,
					documentId);
		}
		DocumentCommand command = null;
		if (document != null) {
			command = DocumentCommand.asCommand(document);
			omplirModelFormulariAmbDocument(
					request,
					command,
					documentId,
					model,
					document);
		} else {
			command = new DocumentCommand();
			LocalDateTime ara = new LocalDateTime();
			command.setDataTime(ara);

			omplirModelFormulari(
					request,
					command,
					pareId,
					model);
		}
		command.setEntitatId(entitatActual.getId());
		command.setPareId(pareId);
		command.setOrigen(DocumentFisicOrigenEnum.DISC);
		command.setTipusFirma(DocumentTipusFirmaEnumDto.ADJUNT);
		model.addAttribute(command);
		model.addAttribute("contingutId", pareId);
		model.addAttribute("documentId", documentId);
		model.addAttribute("tascaId", tascaId);
		return "contingutDocumentForm";
	}
	
	@RequestMapping(value = "/{tascaId}/pare/{pareId}/document/docNew", method = RequestMethod.POST)
	public String postNew(
			HttpServletRequest request,
			@PathVariable Long tascaId,
			@PathVariable Long pareId,
			@Validated({CreateDigital.class, CreateFirmaSeparada.class}) DocumentCommand command,
			BindingResult bindingResult,
			Model model) throws IOException, ClassNotFoundException, NotFoundException, ValidationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		organGestorService.actualitzarOrganCodi(organGestorService.getOrganCodiFromContingutId(pareId));
		//TODO: make one method for ContingutDocumentController.postNew() and UsuariTascaController.postNew()
		
		FitxerTemporalHelper.guardarFitxersAdjuntsSessio(
				request,
				command,
				model);
		
		if (command.isOnlyFileSubmit()) {
			fillModelFileSubmit(command, model, request);
			return "fileUploadResult";
		}

		if ((command.getNtiEstadoElaboracion() == DocumentNtiEstadoElaboracionEnumDto.EE02 || command.getNtiEstadoElaboracion() == DocumentNtiEstadoElaboracionEnumDto.EE03 || command.getNtiEstadoElaboracion() == DocumentNtiEstadoElaboracionEnumDto.EE04) && (command.getNtiIdDocumentoOrigen()==null || command.getNtiIdDocumentoOrigen().isEmpty())) {
			bindingResult.rejectValue("ntiIdDocumentoOrigen", "NotNull");
		}
		
		//Recuperar document escanejat
		if (command.getOrigen().equals(DocumentFisicOrigenEnum.ESCANER)) {
			recuperarResultatEscaneig(
					request,
					pareId,
					command,
					model);
		}
		
		if (bindingResult.hasErrors()) {
			omplirModelFormulari(
					request,
					command,
					pareId,
					model);
			model.addAttribute("contingutId", pareId);
			return "contingutDocumentForm";
		}
		try {
			return createUpdateDocument(
					request,
					command,
					tascaId,
					bindingResult,
					model);
		} catch (Exception exception) {
			MissatgesHelper.error(request, exception.getMessage(), exception);
			omplirModelFormulari(
					request,
					command,
					pareId,
					model);
			return "contingutDocumentForm";
		} finally {
			FitxerTemporalHelper.esborrarFitxersAdjuntsSessio(request);
		}
	}
	
	@RequestMapping(value = "/{tascaId}/pare/{pareId}/document/docUpdate", method = RequestMethod.POST)
	public String postUpdate(
			HttpServletRequest request,
			@PathVariable Long tascaId,
			@PathVariable Long pareId,
			@Validated({UpdateDigital.class}) DocumentCommand command,
			BindingResult bindingResult,
			Model model) throws IOException, ClassNotFoundException, NotFoundException, ValidationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (bindingResult.hasErrors()) {
			omplirModelFormulari(
					request,
					command,
					pareId,
					model);
			model.addAttribute("contingutId", pareId);
			return "contingutDocumentForm";
		}
		try {
			return createUpdateDocument(
					request,
					command,
					tascaId,
					bindingResult,
					model);
		} catch (Exception exception) {
			MissatgesHelper.error(request, exception.getMessage(), exception);
			omplirModelFormulari(
					request,
					command,
					pareId,
					model);
			return "contingutDocumentForm";
		}
	}
	
	
	
	@RequestMapping(value = "/{tascaId}/contingut/{contingutId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long tascaId,
			@PathVariable Long contingutId,
			Model model) throws IOException {
	
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

			expedientTascaService.deleteTascaReversible(
					entitatActual.getId(),
					tascaId,
					contingutId);
			
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:/usuariTasca/" + tascaId + "/tramitar",
					"contingut.controller.element.esborrat.ok");
		} catch (Exception e) {
			log.error("Error al esborrar el contingut (id=" + contingutId + ")", e);
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
	
	
	
	@RequestMapping(value = "/{tascaId}/pare/{pareId}/document/{documentId}/descarregar", method = RequestMethod.GET)
	public String descarregar(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long tascaId,
			@PathVariable Long pareId,
			@PathVariable Long documentId) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

		FitxerDto fitxer = expedientTascaService.descarregar(
				entitatActual.getId(),
				documentId,
				tascaId,
				null);
		writeFileToResponse(
				fitxer.getNom(),
				fitxer.getContingut(),
				response);

		if (pareId != null)
			return "redirect:../../contingut/" + pareId;
		else
			return "redirect:../../expedient";
	}
	
	
	private String createUpdateDocument(
			HttpServletRequest request,
			DocumentCommand command,
			Long tascaId,
			BindingResult bindingResult,
			Model model) throws NotFoundException, ValidationException, IOException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ParseException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

//		List<DadaDto> dades = new ArrayList<DadaDto>();
//		Map<String, Object> valors = new HashMap<String, Object>();

		if (command.getId() == null) {
			expedientTascaService.createDocument(
					entitatActual.getId(),
					command.getPareId(),
					tascaId,
					DocumentCommand.asDto(command),
					true);
			
//			//Valor per defecte d'algunes metadades
//			List<MetaDadaDto> metadades = metaDadaService.findByNode(
//					entitatActual.getId(), 
//					document.getId());
//			for (MetaDadaDto metadada : metadades) {
//				DadaDto dada = new DadaDto();
//				dada.setMetaDada(metadada);
//				dada.setValor(metadada.getValor());
//				dades.add(dada);
//			}
//			Object dadesCommand = beanGeneratorHelper.generarCommandDadesNode(
//					entitatActual.getId(),
//					document.getId(),
//					dades);
//			for (DadaDto dada: dades) {
//				MetaDadaDto metaDada = metaDadaService.findById(
//						entitatActual.getId(), 
//						command.getMetaNodeId(),
//						dada.getMetaDada().getId());
//				Object valor = PropertyUtils.getSimpleProperty(dadesCommand, metaDada.getCodi());
//				if (valor != null && (!(valor instanceof String) || !((String) valor).isEmpty())) {
//					valors.put(metaDada.getCodi(), valor);
//				}
//			}
//			
//			contingutService.dadaSave(
//					entitatActual.getId(),
//					document.getId(),
//					valors);
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../../contingut/" + command.getPareId(),
					"document.controller.creat.ok");
		} else {
			expedientTascaService.updateDocument(
					entitatActual.getId(),
					tascaId,
					DocumentCommand.asDto(command),
					true);
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../contingut/" + command.getPareId(),
					"document.controller.modificat.ok");
		}
	}
	
	
	
	
	
	
	
	private void omplirModelFormulariAmbDocument(
			HttpServletRequest request,
			DocumentCommand command,
			Long contingutId,
			Model model,
			DocumentDto document) throws ClassNotFoundException, IOException {
		organGestorService.actualitzarOrganCodi(organGestorService.getOrganCodiFromContingutId(contingutId));
		if(document.getFitxerNom() != null) {
			model.addAttribute("nomDocument", document.getFitxerNom());
		}
		omplirModelFormulari(request, command, contingutId, model);
	}
	
	private void omplirModelFormulari(
			HttpServletRequest request,
			DocumentCommand command,
			Long pareId,
			Model model) throws ClassNotFoundException, IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (command.getId() == null) {
			model.addAttribute(
					"metaDocuments",
					metaDocumentService.findActiusPerCreacio(
							entitatActual.getId(),
							pareId, 
							null, 
							false));
		} else {
			model.addAttribute(
					"metaDocuments",
					metaDocumentService.findActiusPerModificacio(
							entitatActual.getId(),
							command.getId()));
		}
		model.addAttribute(
				"digitalOrigenOptions",
				EnumHelper.getOptionsForEnum(
						DocumentFisicOrigenEnum.class,
						"document.fisic.origen.enum."));
		model.addAttribute(
				"tipusFirmaOptions",
				EnumHelper.getOptionsForEnum(
						DocumentTipusFirmaEnumDto.class,
						"document.tipus.firma.enum."));
		String tempId = command.getEscanejatTempId();
		if (tempId != null) {
			model.addAttribute(
					"escanejat",
					arxiuTemporalHelper.llegirFitxerSenseContingut(
							servletContext,
							tempId));
		}
		model.addAttribute(
				"ntiOrigenOptions",
				EnumHelper.getOptionsForEnum(
						NtiOrigenEnumDto.class,
						"document.nti.origen.enum."));
		model.addAttribute(
				"ntiEstatElaboracioOptions",
				EnumHelper.getOptionsForEnum(
						DocumentNtiEstadoElaboracionEnumDto.class,
						"document.nti.estela.enum."));
		model.addAttribute(
				"ntiTipusDocumentalOptions",
				EnumHelper.getOptionsForEnum(
						DocumentNtiTipoDocumentalEnumDto.class,
						"document.nti.tipdoc.enum."));
		String propertyEscanejarActiu = aplicacioService.propertyFindByNom("es.caib.ripea.document.nou.escanejar.actiu");
		model.addAttribute(
				"escanejarActiu",
				(propertyEscanejarActiu == null) ? false : new Boolean(propertyEscanejarActiu));
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
						null, 
						false));

		expedientHelper.omplirVistaActiva(request, model);
		
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

		model.addAttribute("pipellaAnotacionsRegistre", pipellaAnotacionsRegistre);
	}
	
	@RequestMapping(value = "/{tascaId}/document/{documentId}/portafirmes/upload", method = RequestMethod.GET)
	public String portafirmesUploadGet(
			HttpServletRequest request,
			@PathVariable Long tascaId,
			@PathVariable Long documentId,
			Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DocumentDto document = expedientTascaService.findDocumentById(
				entitatActual.getId(),
				tascaId,
				documentId);
		
		model.addAttribute("document", document);
		model.addAttribute("annexos", 
				documentService.findAnnexosAmbExpedient(
						entitatActual.getId(), 
						document));
		
		PortafirmesEnviarCommand command = new PortafirmesEnviarCommand();
		command.setMotiu(
				getMessage(
						request, 
						"contenidor.document.portafirmes.camp.motiu.default") +
				" [" + document.getExpedientPare().getNom() + "]");
		
		MetaDocumentDto metaDocument = metaDocumentService.findById(
				entitatActual.getId(),
				document.getMetaDocument().getId());		
		
		command.setPortafirmesSequenciaTipus(metaDocument.getPortafirmesSequenciaTipus());
		command.setPortafirmesResponsables(metaDocument.getPortafirmesResponsables());
		setFluxPredefinit(
				metaDocument, 
				model, 
				command);
		RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_TRANSACCIOID);
		model.addAttribute("isNouEnviament", true);
		model.addAttribute(command);
		
		model.addAttribute("tascaId", tascaId);
		return "portafirmesForm";
	}
	
	@RequestMapping(value = "/{tascaId}/document/{documentId}/portafirmes/upload", method = RequestMethod.POST)
	public String portafirmesUploadPost(
			HttpServletRequest request,
			@PathVariable Long tascaId,
			@PathVariable Long documentId,
			@Valid PortafirmesEnviarCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			emplenarModelPortafirmes(
					request,
					documentId,
					model);
			return "portafirmesForm";
		}
		String transaccioId = null;
		if (command.getPortafirmesFluxTipus().equals(MetaDocumentFirmaFluxTipusEnumDto.PORTAFIB)) {
			transaccioId = (String)RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_TRANSACCIOID);
		}
		
		expedientTascaService.portafirmesEnviar(
				entitatActual.getId(),
				documentId,
				command.getMotiu(),
				command.getPrioritat(),
				null,
				command.getPortafirmesResponsables(),
				command.getPortafirmesSequenciaTipus(),
				command.getPortafirmesFluxTipus(),
				command.getAnnexos(),
				tascaId,
				transaccioId);
		return this.getModalControllerReturnValueSuccess(
				request,
				"redirect:../../../contingut/" + documentId,
				"document.controller.portafirmes.upload.ok");
	}

	@RequestMapping(value = "/{tascaId}/document/{documentId}/portafirmes/reintentar", method = RequestMethod.GET)
	public String portafirmesReintentar(
			HttpServletRequest request,
			@PathVariable Long tascaId,
			@PathVariable Long documentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		expedientTascaService.portafirmesReintentar(
				entitatActual.getId(),
				documentId,
				tascaId);
		return "redirect:./info";
	}

	@RequestMapping(value = "{tascaId}/document/{documentId}/portafirmes/cancel", method = RequestMethod.GET)
	public String portafirmesCancel(
			HttpServletRequest request,
			@PathVariable Long tascaId,
			@PathVariable Long documentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		expedientTascaService.portafirmesCancelar(
				entitatActual.getId(),
				tascaId,
				documentId, 
				RolHelper.getRolActual(request));
		return this.getModalControllerReturnValueSuccess(
				request,
				"redirect:../../../contingut/" + documentId,
				"document.controller.portafirmes.cancel.ok");
	}

	@RequestMapping(value = "/{tascaId}/document/{documentId}/portafirmes/info", method = RequestMethod.GET)
	public String portafirmesInfo(
			HttpServletRequest request,
			@PathVariable Long tascaId,
			@PathVariable Long documentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DocumentPortafirmesDto portafirmes = documentService.portafirmesInfo(
				entitatActual.getId(),
				documentId, 
				null);
		
		model.addAttribute(
				"portafirmes",
				portafirmes);
		
		model.addAttribute("tascaId", tascaId);
		return "portafirmesInfo";
	}
	
	@RequestMapping(value = "/{tascaId}/document/{documentId}/firmaPassarela", method = RequestMethod.GET)
	public String firmaPassarelaGet(
			HttpServletRequest request,
			@PathVariable Long tascaId,
			@PathVariable Long documentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DocumentDto document = expedientTascaService.findDocumentById(
				entitatActual.getId(),
				tascaId,
				documentId);
		model.addAttribute("document", document);
		PassarelaFirmaEnviarCommand command = new PassarelaFirmaEnviarCommand();
		command.setMotiu(getMessage(
						request, 
						"contenidor.document.portafirmes.camp.motiu.default") +
				" [" + document.getExpedientPare().getNom() + "]");
		model.addAttribute(command);
		return "passarelaFirmaForm";
	}
	
	
	
	@RequestMapping(value = "/{tascaId}/document/{documentId}/firmaPassarela", method = RequestMethod.POST)
	public String firmaPassarelaPost(
			HttpServletRequest request,
			@PathVariable Long tascaId,
			@PathVariable Long documentId,
			@Valid PassarelaFirmaEnviarCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			emplenarModelFirmaClient(
					request,
					documentId,
					model);
			return "passarelaFirmaForm";
		}
		if (!command.getFirma().isEmpty()) {
			expedientTascaService.processarFirmaClient(
					null,
					null,
					command.getFirma().getOriginalFilename(),
					command.getFirma().getBytes(), tascaId);
			MissatgesHelper.success(
					request,
					getMessage(
							request, 
							"document.controller.firma.passarela.final.ok"));
			return getModalControllerReturnValueSuccess(
					request, 
					"redirect:/contingut/" + documentId,
					null);
		} else {
			FitxerDto fitxerPerFirmar = expedientTascaService.convertirPdfPerFirmaClient(
					entitatActual.getId(),
					tascaId,
					documentId);
			UsuariDto usuariActual = aplicacioService.getUsuariActual();
			String modalStr = (ModalHelper.isModal(request)) ? "/modal" : "";
			String procesFirmaUrl = passarelaFirmaHelper.generateSignaturesSet(
					request,
					fitxerPerFirmar,
					usuariActual.getNif(),
					command.getMotiu(),
					(command.getLloc() != null) ? command.getLloc() : "RIPEA",
					usuariActual.getEmail(),
					LocaleContextHolder.getLocale().getLanguage(),
					modalStr + "/usuariTasca/" + tascaId + "/document/" + documentId +  "/firmaPassarelaFinal",
					false);
			return "redirect:" + procesFirmaUrl;
		}
	}
	
	
	
	@RequestMapping(value = "/{tascaId}/document/{documentId}/firmaPassarelaFinal")
	public String firmaPassarelaFinal(
			HttpServletRequest request,
			@PathVariable Long tascaId,
			@PathVariable Long documentId,
			@RequestParam("signaturesSetId") String signaturesSetId,
			Model model) throws IOException {
		SignaturesSetExtend signaturesSet = passarelaFirmaHelper.getSignaturesSet(
				request,
				signaturesSetId);
		passarelaFirmaHelper.setStatusFinalitzat(signaturesSet);
		
		StatusSignaturesSet status = signaturesSet.getStatusSignaturesSet();
		switch (status.getStatus()) {
		case StatusSignaturesSet.STATUS_FINAL_OK:
			FileInfoSignature firmaInfo = signaturesSet.getFileInfoSignatureArray()[0];
			StatusSignature firmaStatus = firmaInfo.getStatusSignature();
			if (firmaStatus.getStatus() == StatusSignature.STATUS_FINAL_OK) {
				if (firmaStatus.getSignedData() == null || !firmaStatus.getSignedData().exists()) {
					firmaStatus.setStatus(StatusSignature.STATUS_FINAL_ERROR);
					String msg = "L'estat indica que ha finalitzat correctament però el fitxer firmat o no s'ha definit o no existeix";
					firmaStatus.setErrorMsg(msg);
					MissatgesHelper.error(
							request,
							getMessage(
									request, 
									"document.controller.firma.passarela.final.ok.nofile"),
							null);
				} else {
					FileInputStream fis = new FileInputStream(firmaStatus.getSignedData());
					EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
					String identificador = expedientTascaService.generarIdentificadorFirmaClient(
							entitatActual.getId(),
							tascaId,
							documentId);
					expedientTascaService.processarFirmaClient(
							null,
							null,
							firmaStatus.getSignedData().getName(),
							IOUtils.toByteArray(fis), tascaId);
					MissatgesHelper.success(
							request,
							getMessage(
									request, 
									"document.controller.firma.passarela.final.ok"));
				}
			} else {
				MissatgesHelper.error(
						request,
						getMessage(
								request, 
								"document.controller.firma.passarela.final.ok.statuserr"),
						null);
			}
			break;
		case StatusSignaturesSet.STATUS_FINAL_ERROR:
			MissatgesHelper.error(
					request,
					getMessage(
							request, 
							"document.controller.firma.passarela.final.error",
							new Object[] {status.getErrorMsg()}),
					null);
			break;
		case StatusSignaturesSet.STATUS_CANCELLED:
			MissatgesHelper.warning(
					request,
					getMessage(
							request, 
							"document.controller.firma.passarela.final.cancel"));
			break;
		default:
			MissatgesHelper.warning(
					request,
					getMessage(
							request, 
							"document.controller.firma.passarela.final.desconegut"));
		}
		passarelaFirmaHelper.closeTransactionInWS(
				request,
				signaturesSet);
		boolean ignorarModal = false;
		String ignorarModalIdsProperty = aplicacioService.propertyPluginPassarelaFirmaIgnorarModalIds();
		if (ignorarModalIdsProperty != null && !ignorarModalIdsProperty.isEmpty()) {
			String[] ignorarModalIds = ignorarModalIdsProperty.split(",");
			for (String ignorarModalId: ignorarModalIds) {
				if (StringUtils.isNumeric(ignorarModalId)) {
					if (ignorarModalId == signaturesSet.getPluginId()) {
						ignorarModal = true;
						break;
					}
				}
			}
		}
		if (ignorarModal) {
			return "redirect:/contingut/" + documentId;
		} else {
			return getModalControllerReturnValueSuccess(
					request, 
					"redirect:/contingut/" + documentId,
					null);
		}
	}
	
	private void fillModelFileSubmit(DocumentCommand command, Model model, HttpServletRequest request) {
		if (command.isUnselect()) {
			request.getSession().setAttribute(FitxerTemporalHelper.SESSION_ATTRIBUTE_DOCUMENT, null);
		}
		FitxerTemporalDto fitxerTemp = (FitxerTemporalDto) request.getSession().getAttribute(FitxerTemporalHelper.SESSION_ATTRIBUTE_DOCUMENT);
		if (fitxerTemp != null) {
			SignatureInfoDto signatureInfoDto = documentService.checkIfSignedAttached(fitxerTemp.getBytes(), fitxerTemp.getContentType());
			model.addAttribute("isSignedAttached", signatureInfoDto.isSigned());
			model.addAttribute("isError", signatureInfoDto.isError());
			model.addAttribute("errorMsg", signatureInfoDto.getErrorMsg());
		}
	}
	
	private void emplenarModelFirmaClient(
			HttpServletRequest request,
			Long documentId,
			Model model) {
		emplenarModelPortafirmes(
				request,
				documentId,
				model);
	}
	
	private void setFluxPredefinit(
			MetaDocumentDto metaDocument,
			Model model,
			PortafirmesEnviarCommand command) {
		if (metaDocument.getPortafirmesFluxTipus() != null) {
			command.setPortafirmesFluxTipus(metaDocument.getPortafirmesFluxTipus());
			model.addAttribute("portafirmesFluxId", metaDocument.getPortafirmesFluxId());
			if (metaDocument.getPortafirmesFluxTipus().equals(MetaDocumentFirmaFluxTipusEnumDto.PORTAFIB) && metaDocument.getPortafirmesFluxId() == null) {
				model.addAttribute("nouFluxDeFirma", true);
			} else {
				model.addAttribute("nouFluxDeFirma", false);
			}
		} else {
			model.addAttribute("nouFluxDeFirma", false);
			command.setPortafirmesFluxTipus(MetaDocumentFirmaFluxTipusEnumDto.SIMPLE);
		}
		model.addAttribute("fluxTipus", metaDocument.getPortafirmesFluxTipus());
	}
	
	private String recuperarResultatEscaneig(
			HttpServletRequest request,
			Long contingutId,
			DocumentCommand command,
			Model model) throws ClassNotFoundException, IOException {
		boolean returnScannedFile = false;
		boolean returnSignedFile = false;
		
		String idTransaccio = (String) RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_RETURN_IDTRANSACCIO);
		
		Object scannedFile = RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_RETURN_SCANNED);
		Object signedFile = RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_RETURN_SIGNED);
		
		if (scannedFile != null) {
			returnScannedFile = (boolean) RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_RETURN_SCANNED);
		}
		if (signedFile != null) {
			returnSignedFile = (boolean) RequestSessionHelper.obtenirObjecteSessio(
					request,
					SESSION_ATTRIBUTE_RETURN_SIGNED);
		}
		if (idTransaccio != null) { 
			DigitalitzacioResultatDto resultat = digitalitzacioService.recuperarResultat(
					idTransaccio, 
					returnScannedFile, 
					returnSignedFile);
			if (resultat != null && resultat.isError() && !resultat.getEstat().equals(DigitalitzacioEstatDto.FINAL_OK)) {
				MissatgesHelper.error(
						request,
						getMessage(
								request, 
								"document.digitalitzacio.estat.enum."+ resultat.getEstat()),
						null);
				omplirModelFormulari(
						request,
						command,
						contingutId,
						model);
				model.addAttribute("contingutId", contingutId);
				return "contingutDocumentForm";
			}
			model.addAttribute("nomDocument", resultat.getNomDocument());
			model.addAttribute("idTransaccio", idTransaccio);
			command.setFitxerNom(resultat.getNomDocument());
			command.setFitxerContentType(resultat.getMimeType());
			command.setFitxerContingut(resultat.getContingut());
				
			//Amb firma?
			if (returnSignedFile) {
				command.setAmbFirma(true);
			}
		} else {
			omplirModelFormulari(
					request,
					command,
					contingutId,
					model);
			model.addAttribute("contingutId", contingutId);
			model.addAttribute("noFileScanned", "no s'ha seleccionat cap document");	
			return "contingutDocumentForm";
		}
		return idTransaccio;
	}
	
	private void emplenarModelPortafirmes(
			HttpServletRequest request,
			Long documentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DocumentDto document = documentService.findById(
				entitatActual.getId(),
				documentId);
		model.addAttribute("document", document);
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
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}


}
