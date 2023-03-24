/**
 * 
 */
package es.caib.ripea.war.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.http.MediaType;
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

import es.caib.ripea.core.api.dto.ArxiuEstatEnumDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaDetallDto;
import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.DadaDto;
import es.caib.ripea.core.api.dto.DigitalitzacioEstatDto;
import es.caib.ripea.core.api.dto.DigitalitzacioResultatDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentFirmaTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiTipoDocumentalEnumDto;
import es.caib.ripea.core.api.dto.DocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentTipusFirmaEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.FitxerTemporalDto;
import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.NtiOrigenEnumDto;
import es.caib.ripea.core.api.dto.PermissionEnumDto;
import es.caib.ripea.core.api.dto.SignatureInfoDto;
import es.caib.ripea.core.api.exception.ArxiuJaGuardatException;
import es.caib.ripea.core.api.exception.ArxiuNotFoundDocumentException;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.SistemaExternException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.DigitalitzacioService;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.api.service.MetaDadaService;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.core.api.service.OrganGestorService;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.war.command.DocumentCommand;
import es.caib.ripea.war.command.DocumentCommand.CreateDigital;
import es.caib.ripea.war.command.DocumentCommand.CreateFirmaSeparada;
import es.caib.ripea.war.command.DocumentCommand.DocumentFisicOrigenEnum;
import es.caib.ripea.war.command.DocumentCommand.UpdateDigital;
import es.caib.ripea.war.command.DocumentGenericCommand;
import es.caib.ripea.war.helper.ArxiuTemporalHelper;
import es.caib.ripea.war.helper.BeanGeneratorHelper;
import es.caib.ripea.war.helper.DocumentHelper;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.ExceptionHelper;
import es.caib.ripea.war.helper.FitxerTemporalHelper;
import es.caib.ripea.war.helper.JsonResponse;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;
import es.caib.ripea.war.helper.RolHelper;

/**
 * Controlador per al manteniment de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/contingut")
public class ContingutDocumentController extends BaseUserOAdminOOrganController {

	private static final String SESSION_ATTRIBUTE_SELECCIO = "ContingutDocumentController.session.seleccio";
	private static final String SESSION_ATTRIBUTE_ORDRE = "ContingutDocumentController.session.ordre";
	private static final String SESSION_ATTRIBUTE_RETURN_SCANNED = "DigitalitzacioController.session.scanned";
	private static final String SESSION_ATTRIBUTE_RETURN_SIGNED = "DigitalitzacioController.session.signed";
	private static final String SESSION_ATTRIBUTE_RETURN_IDTRANSACCIO = "DigitalitzacioController.session.idTransaccio";

	
	@Autowired
	private ServletContext servletContext;

	@Autowired
	private ContingutService contingutService;
	@Autowired
	private DocumentService documentService;
	@Autowired
	private MetaDocumentService metaDocumentService;
	@Autowired
	private MetaDadaService metaDadaService;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private ArxiuTemporalHelper arxiuTemporalHelper;
	@Autowired
	private BeanGeneratorHelper beanGeneratorHelper;
	@Autowired 
	private DocumentHelper documentHelper;
	@Autowired
	private DigitalitzacioService digitalitzacioService;
	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private OrganGestorService organGestorService;
	
	@RequestMapping(value = "/{pareId}/document/new", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long pareId,
			Model model) throws ClassNotFoundException, IOException {
		return get(request, pareId, null, model);
	}
	@RequestMapping(value = "/{pareId}/document/modificar/{documentId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long pareId,
			@PathVariable Long documentId,
			Model model) throws ClassNotFoundException, IOException {
		organGestorService.actualitzarOrganCodi(organGestorService.getOrganCodiFromContingutId(pareId));
		FitxerTemporalHelper.esborrarFitxersAdjuntsSessio(request);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DocumentDto document = null;
		if (documentId != null) {
			document = documentService.findById(
					entitatActual.getId(),
					documentId);
		}
		DocumentCommand command = null;
		if (document != null) {
			command = DocumentCommand.asCommand(document);
			
			if(document.getFitxerNom() != null) {
				model.addAttribute("nomDocument", document.getFitxerNom());
			}
			model.addAttribute("documentEstat", document.getEstat());
			
			setTipusFirma(command, document);
			
			model.addAttribute("isPermesPropagarModificacioDefinitius", isPropagarModificacioDefinitiusActiva());
			omplirModelFormulari(request, command, documentId, model);
			
		} else {
			command = new DocumentCommand();
			LocalDateTime ara = new LocalDateTime();
			command.setDataTime(ara);
			command.setTipusFirma(DocumentTipusFirmaEnumDto.ADJUNT);
			omplirModelFormulari(
					request,
					command,
					pareId,
					model);
		}

		command.setEntitatId(entitatActual.getId());
		command.setPareId(pareId);
		command.setOrigen(DocumentFisicOrigenEnum.DISC);
		
		model.addAttribute(command);
		model.addAttribute("contingutId", pareId);
		model.addAttribute("documentId", documentId);		
		return "contingutDocumentForm";
	}
	
	
	private void setTipusFirma(DocumentCommand command, DocumentDto document) {
		
		if (document.getDocumentFirmaTipus() == DocumentFirmaTipusEnumDto.SENSE_FIRMA) {
			command.setAmbFirma(false);
			command.setTipusFirma(DocumentTipusFirmaEnumDto.ADJUNT);
		} else if (document.getDocumentFirmaTipus() == DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA) {
			command.setAmbFirma(true);
			command.setTipusFirma(DocumentTipusFirmaEnumDto.ADJUNT);
		} else if (document.getDocumentFirmaTipus() == DocumentFirmaTipusEnumDto.FIRMA_SEPARADA) {
			command.setAmbFirma(true);
			command.setTipusFirma(DocumentTipusFirmaEnumDto.SEPARAT);
		}

	}
	
	
	@RequestMapping(value = "/{pareId}/document/docNew", method = RequestMethod.POST)
	public String postNew(
			HttpServletRequest request,
			@PathVariable Long pareId,
			@Validated({CreateDigital.class, CreateFirmaSeparada.class}) DocumentCommand command,
			BindingResult bindingResult,
			Model model) throws IOException, ClassNotFoundException, NotFoundException, ValidationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ParseException {
		organGestorService.actualitzarOrganCodi(organGestorService.getOrganCodiFromContingutId(pareId));
		if (!command.getOrigen().equals(DocumentFisicOrigenEnum.ESCANER)) {
			FitxerTemporalHelper.guardarFitxersAdjuntsSessio(
					request,
					command,
					model);
		}
		
		if (command.isOnlyFileSubmit()) {
			fillModelFileSubmit(command, model, request);
			return "fileUploadResult";
		}
		String estatsElaboracioIdentificadorEniObligat = obtenirEstatsElaboracioIdentificadorEniObligat();
		if ((estatsElaboracioIdentificadorEniObligat != null && !estatsElaboracioIdentificadorEniObligat.isEmpty() && command.getNtiEstadoElaboracion() != null && estatsElaboracioIdentificadorEniObligat.contains(command.getNtiEstadoElaboracion().name())) && (command.getNtiIdDocumentoOrigen()==null || command.getNtiIdDocumentoOrigen().isEmpty())) {
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
			return "contingutDocumentForm";
		}
		try {
			return createUpdateDocument(
					request,
					command,
					command.getDocumentTipus().equals(DocumentTipusEnumDto.IMPORTAT) ? false : true,
					RolHelper.getRolActual(request));
		} catch (ValidationException ex) {
			MissatgesHelper.error(request, ex.getMessage(), ex);
			omplirModelFormulari(
					request,
					command,
					pareId,
					model);
			return "contingutDocumentForm";
		} catch (Exception ex) {
			logger.error("Error al crear un document", ex);
			Throwable throwable = ExceptionHelper.findExceptionInstance(ex, SistemaExternException.class, 3);
			if (throwable!=null) {
				SistemaExternException sisExtExc = (SistemaExternException) throwable;
				MissatgesHelper.error(request, sisExtExc.getMessage(), sisExtExc);
				if (command.getOrigen().equals(DocumentFisicOrigenEnum.ESCANER)) {
					return modalUrlTancar();
				} else {
					omplirModelFormulari(
							request,
							command,
							pareId,
							model);
					return "contingutDocumentForm";
				}
			} else {
				throw ex;
			}
		} 
	}
	

	
	@RequestMapping(value = "/{contingutId}/document/docUpdate", method = RequestMethod.POST)
	public String postUpdate(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@Validated({UpdateDigital.class}) DocumentCommand command,
			BindingResult bindingResult,
			Model model) throws IOException, ClassNotFoundException, NotFoundException, ValidationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ParseException {

		organGestorService.actualitzarOrganCodi(organGestorService.getOrganCodiFromContingutId(contingutId));
		FitxerTemporalHelper.guardarFitxersAdjuntsSessio(
				request,
				command,
				model);
		
		if (command.isOnlyFileSubmit()) {
			fillModelFileSubmit(command, model, request);
			return "fileUploadResult";
		}
		
		
		//Recuperar document escanejat
		if (command.getOrigen().equals(DocumentFisicOrigenEnum.ESCANER)) {
			recuperarResultatEscaneig(
					request,
					contingutId,
					command,
					model);
		}
				
		if (bindingResult.hasErrors()) {
			omplirModelFormulari(
					request,
					command,
					contingutId,
					model);
			return "contingutDocumentForm";
		}
		try {
			return createUpdateDocument(
					request,
					command,
					command.getDocumentTipus().equals(DocumentTipusEnumDto.IMPORTAT) ? false : true,
					RolHelper.getRolActual(request));
		} catch (ValidationException ex) {
			MissatgesHelper.error(request, ex.getMessage(), ex);
			omplirModelFormulari(
					request,
					command,
					contingutId,
					model);
			return "contingutDocumentForm";
		}
	}
	
	@RequestMapping(value = "/{contingutId}/document/updateTipusDocument", method = RequestMethod.GET)
	@ResponseBody
	public JsonResponse updateTipusDocument(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@RequestParam(value = "tipusDocumentId", required = false) Long tipusDocumentId,
			Model model) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		try {
			documentService.updateTipusDocument(
					entitatActual.getId(), 
					contingutId, 
					tipusDocumentId, 
					false, 
					null, 
					null);
			return new JsonResponse(new Boolean(true));
			
		} catch (Exception e) {
			logger.error("Error actualitzant el document amb el nou tipus de document", e);
			return new JsonResponse(true, e.getMessage());
		}
		
	}
	
	
	@RequestMapping(value = "/{isTasca}/{id}/updateTipusDocumentDragDrop/{documentId}/{tipusDocumentId}", method = RequestMethod.GET)
	public String updateTipusDocumentDragDrop(
			HttpServletRequest request,
			@PathVariable boolean isTasca,
			@PathVariable Long id,
			@PathVariable Long documentId,
			@PathVariable Long tipusDocumentId,
			Model model) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		String url;
		if (isTasca) {
			url = "redirect:/usuariTasca/" + id + "/tramitar";
		} else {
			url = "redirect:/contingut/" + id;
		}
	
		
		try {
			documentService.updateTipusDocument(
					entitatActual.getId(), 
					documentId, 
					tipusDocumentId, 
					false, 
					isTasca ? id : null, 
					RolHelper.getRolActual(request));


			return getAjaxControllerReturnValueSuccess(
					request,
					url,
					"contingut.controller.element.canviar.tipus.document.ok");
			
			
		} catch (Exception e) {
			logger.error("Error actualitzant el document amb el nou tipus de document", e);
			return getAjaxControllerReturnValueErrorMessage(
					request,
					url,
					"contingut.controller.element.canviar.tipus.document.error",
					e);
			
		}
	}

	
	@RequestMapping(value = "/{pareId}/document/{documentId}/guardarDocumentArxiu", method = RequestMethod.GET)
	public String guardarEnArxiuDocumentAdjunt(
			HttpServletRequest request,
			@PathVariable Long pareId,
			@PathVariable Long documentId,
			@RequestParam(value = "origin") String origin,
			Model model)  {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		DocumentDto document = documentService.findById(entitatActual.getId(), documentId);
		Exception exception = null;
		if (document.getArxiuUuid() == null) {
			
			try {
				exception = documentService.guardarDocumentArxiu(documentId);
			} catch (ArxiuJaGuardatException e) {
				exception = null;
			} catch (Exception e) {
				exception = e;
			}
		} else if (document.isPendentMoverArxiu()) {
			try {
				exception = expedientService.retryMoverAnnexArxiu(document.getAnnexId());
			} catch (Exception e) {
				exception = e;
			}
		} else if (!StringUtils.isEmpty(document.getGesDocFirmatId())) {
			
			try {
				exception = documentService.portafirmesReintentar(
						entitatActual.getId(),
						documentId, 
						RolHelper.getRolActual(request));
			} catch (Exception e) {
				exception = e;
			}
		}
		
		
		String redirect = null;
		if (origin.equals("docDetail")) {
			redirect = "redirect:../../";
		} else if (origin.equals("seguiment")) {
			redirect = "redirect:../../../../seguimentArxiuPendents/#documents";
		}
		
		if (exception == null) {
			return getModalControllerReturnValueSuccess(
					request,
					redirect,
					"document.controller.guardar.arxiu.ok");
		} else {
			System.out.println("Error guardant document en arxiu" +  ExceptionUtils.getStackTrace(exception));
			logger.error("Error guardant document en arxiu", exception);
			
			Throwable root = ExceptionHelper.getRootCauseOrItself(exception);
			String msg = null;
			if (root instanceof ConnectException || (root.getMessage() != null && root.getMessage().contains("timed out"))) {
				msg = getMessage(request,"error.arxiu.connectTimedOut");
			} else {
				msg = root.getMessage();
			}
			return getAjaxControllerReturnValueError(
					request,
					redirect,
					"document.controller.guardar.arxiu.error",
					new Object[] {msg},
					root);
		}
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
				if (!resultat.getEniTipoFirma().equals("TF02") && !resultat.getEniTipoFirma().equals("TF04")) {
					command.setTipusFirma(DocumentTipusFirmaEnumDto.ADJUNT);
				} else {
					command.setTipusFirma(DocumentTipusFirmaEnumDto.SEPARAT);
				}
			}

            FitxerTemporalHelper.guardarFitxersAdjuntsSessio(
                    request,
                    command,
                    model);


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
	
	@RequestMapping(value = "/document/{documentId}/mostraDetallSignants", method = RequestMethod.GET)
	@ResponseBody
	public JsonResponse mostraDetallSignants(
			HttpServletRequest request,
			@PathVariable Long documentId,
			Model model) throws IOException {
		organGestorService.actualitzarOrganCodi(organGestorService.getOrganCodiFromContingutId(documentId));
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		List<ArxiuFirmaDetallDto> detallSignants;
		try {
			detallSignants = documentService.getDetallSignants(
					entitatActual.getId(),
					documentId,
					null);
			return new JsonResponse(detallSignants);
		} catch (Exception e) {
			return new JsonResponse(true, ExceptionHelper.getRootCauseOrItself(e).getMessage());
		}
	}
	
	
	
	@RequestMapping(value = "/{pareId}/document/{documentId}/returnFitxer", method = RequestMethod.GET)
	@ResponseBody
	public JsonResponse returnFitxer(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long pareId,
			@PathVariable Long documentId) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		try {

			FitxerDto convertit = documentService.convertirPdfPerFirmaClient(
					entitatActual.getId(),
					documentId);

			return new JsonResponse(convertit);
				
		} catch (Exception e) {
			logger.error("Error al visualitzar document", e);
			if (ExceptionHelper.isExceptionOrCauseInstanceOf(e, "fr.opensagres.xdocreport.converter.XDocConverterException", 5)) {
				return new JsonResponse(null, true);
			} else {
				return new JsonResponse(true, e.getMessage());
			}
		}
	}
	

	@RequestMapping(value = "/{pareId}/document/{documentId}/descarregar", method = RequestMethod.GET)
	public String descarregar(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long pareId,
			@PathVariable Long documentId) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutDto contingut = contingutService.findAmbIdUser(
				entitatActual.getId(),
				documentId,
				true,
				false, null, null);
		if (contingut instanceof DocumentDto) {
			
			try {
				FitxerDto fitxer = documentService.descarregar(entitatActual.getId(),
						documentId,
						null);
				writeFileToResponse(fitxer.getNom(),
						fitxer.getContingut(),
						response);
				return null;
				
			} catch (Exception e) {
				logger.error("Error al descarregar un document", e);
				
				if (ExceptionHelper.isExceptionOrCauseInstanceOf(e, ArxiuNotFoundDocumentException.class)) {
					return getAjaxControllerReturnValueError(
							request,
							"redirect:../../",
							"document.controller.descarregar.error.arxiuNoTrobat",
							e);
				} else {
					
					Throwable root = ExceptionHelper.getRootCauseOrItself(e);
					
					if (root.getMessage() != null && root.getMessage().contains("timed out")) {
						MissatgesHelper.error(
								request, 
								getMessage(request, "document.controller.descarregar.error") + ": " + getMessage(request, "error.arxiu.connectTimedOut"), root);
					} else {
						MissatgesHelper.error(
								request, 
								getMessage(request, "document.controller.descarregar.error") + ": " + root.getMessage(), root);
					}
					return "redirect:../../../../contingut/" + pareId;
				}
			}
			
		} else {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"document.controller.descarregar.error"),
					null);
			if (contingut.getPare() != null)
				return "redirect:../../contingut/" + pareId;
			else
				return "redirect:../../expedient";
		}

	}
	
	@RequestMapping(value = "/{pareId}/descarregarMultiples", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public void descarregarMultiple(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long pareId) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		ContingutDto expedient = contingutService.findAmbIdUser(
				entitatActual.getId(),
				pareId,
				true,
				false, null, null);
		
		byte[] reportContent = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		@SuppressWarnings("unchecked")
		Set<Long> docsIdx = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		
		documentHelper.generarFitxerZip(
				entitatActual.getId(),
				documentService, 
				contingutService,
				entitatActual, 
				docsIdx,
				baos,
				request);
		
		reportContent = baos.toByteArray();
		response.setHeader("Content-Disposition", "attachment; filename=" + expedient.getNom().replaceAll(" ", "_") + ".zip");
		response.getOutputStream().write(reportContent);
		response.getOutputStream().flush();
	}
	
	@RequestMapping(value = "/{expedientId}/concatenarOGenerarZip", method = RequestMethod.GET)
	public String concatenarOGenerarZip(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) throws ClassNotFoundException, IOException, NotFoundException, ValidationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ParseException {
		
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			
			@SuppressWarnings("unchecked")
			Set<Long> docsIdx = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO);
			
			List<DocumentDto> documents = new ArrayList<DocumentDto>();
			boolean totsDocumentsPdf = true;
			for (Long docId: docsIdx) {
				DocumentDto document = documentService.findAmbId(
						docId,
						RolHelper.getRolActual(request),
						PermissionEnumDto.WRITE);

				
				if (document.getDocumentFirmaTipus() == DocumentFirmaTipusEnumDto.SENSE_FIRMA) {
					throw new RuntimeException("El document amb nom '" + document.getNom() + "' no està firmat");
				}
				//No es possible concatenar els documents que no són pdf
				if (Utils.isNotNullAndEqual(document.getFitxerContentType(), "application/pdf")) {
					if (document.getArxiuEstat() == ArxiuEstatEnumDto.ESBORRANY) {
						documentService.actualitzarEstatADefinititu(docId);
					}
					documents.add(document);
				} else {
					totsDocumentsPdf = false;
				}
			}

			// ========================= CONCATENTAR ===================================
			if (totsDocumentsPdf) {
				
				Map<String, Long> ordre = new LinkedHashMap<String, Long>();
				if (docsIdx != null) {
					for (Long id: docsIdx) {
						ordre.put("document-" + id, id);
					}
				}
				RequestSessionHelper.actualitzarObjecteSessio(
						request,
						SESSION_ATTRIBUTE_ORDRE,
						ordre);
				
				
				model.addAttribute("documents", documents);
				model.addAttribute("expedientId", expedientId);
				
				MissatgesHelper.warning(
						request, 
						getMessage(
								request, 
								"contingut.document.form.titol.concatenacio.info"));
				return "contingutConcatenacioForm";
				
			// ========================= GENERAR ZIP ===================================	
			} else {
				
				DocumentGenericCommand command = documentHelper.generarFitxerZip(
						entitatActual.getId(),
						documentService, 
						contingutService,
						entitatActual, 
						docsIdx,
						null,
						request);
				
				DocumentDto document = documentService.create(
						entitatActual.getId(),
						expedientId,
						DocumentGenericCommand.asDto(command),
						false, 
						RolHelper.getRolActual(request));
				

				MissatgesHelper.warning(
						request, 
						getMessage(
								request, 
								"contingut.document.form.titol.compresio.info"));
				
				return "redirect:../../document/" + document.getId() + "/notificar";
				
			}
		} catch (Exception e) {
			logger.error("Error al concatenarOGenerarZip", e);
			return getModalControllerReturnValueErrorMessageText(
					request,
					"redirect:/contingut/" + expedientId,
					e.getMessage(),
					e);
		}
	}
	
	@RequestMapping(value = "/{expedientId}/doCreateConcatenatedDocument", method = RequestMethod.GET)
	public String concatenarDocuments(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long expedientId,
			Model model) throws IOException, ClassNotFoundException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		@SuppressWarnings("unchecked")
		Map<String, Long> ordre = (Map<String, Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_ORDRE);

		try {
			DocumentGenericCommand command = documentHelper.concatenarDocuments(
					entitatActual.getId(),
					documentService, 
					contingutService,
					entitatActual, 
					ordre);
			
			DocumentDto document = documentService.create(
					entitatActual.getId(),
					expedientId,
					DocumentGenericCommand.asDto(command),
					false, 
					RolHelper.getRolActual(request));
			
			return "redirect:../../document/" + document.getId() + "/notificar";
	
		} catch (Exception exception) {
			return getModalControllerReturnValueErrorMessageText(
					request, 
					null, 
					exception.getMessage(),
					exception);
		}
	}
	
	@RequestMapping(value = "/{contingutId}/defintiu", method = RequestMethod.GET)
	public String defintiu(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) throws ClassNotFoundException, IOException, NotFoundException, ValidationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		boolean existsEsborrat  = false;
		ContingutDto contingut = contingutService.findAmbIdUser(
				entitatActual.getId(),
				contingutId,
				true,
				false, null, null);
		
		@SuppressWarnings("unchecked")
		Set<Long> docsIdx = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		
		for (Long docId: docsIdx) {
			DocumentDto document = (DocumentDto) contingutService.findAmbIdUser(
					entitatActual.getId(),
					docId,
					true,
					false, null, null);
			if (document.getEstat().equals(DocumentEstatEnumDto.REDACCIO) && !document.getDocumentTipus().equals(DocumentTipusEnumDto.IMPORTAT)) {
				existsEsborrat = true;
				documentService.documentActualitzarEstat(
						entitatActual.getId(), 
						docId, 
						DocumentEstatEnumDto.DEFINITIU);
			}
			
		}
		if (existsEsborrat) {
			return this.getModalControllerReturnValueSuccess(
					request,
					"redirect:../../contingut/" + contingut.getId(),
					"document.controller.estat.canviat.ok");
		} else {
			//No s'ha seleccionat cap document de tipus esborrany
			return this.getModalControllerReturnValueError(
					request,
					"redirect:../../contingut/" + contingut.getId(),
					"document.controller.estat.canviat.ko",
					null);
		}
	}

	@RequestMapping(value = "/{pareId}/ordre", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Long> ordre(
			HttpServletRequest request,
			@PathVariable Long pareId,
			@RequestParam(value="ordreId[]", required = false) Long[] ordreId) {
		Map<String, Long> ordre = new LinkedHashMap<String, Long>();
		
		if (ordreId != null) {
			for (Long id: ordreId) {
				ordre.put("document-" + id, id);
			}
		}

		RequestSessionHelper.actualitzarObjecteSessio(
				request,
				SESSION_ATTRIBUTE_ORDRE,
				ordre);
		return ordre;
	}

	@RequestMapping(value = "/{pareId}/inicialitzar/seleccio", method = RequestMethod.GET)
	@ResponseBody
	public int inicialitzar(
			HttpServletRequest request,
			@PathVariable Long pareId) {
		Set<Long> seleccio = new HashSet<Long>();
		
		RequestSessionHelper.actualitzarObjecteSessio(
			request,
			SESSION_ATTRIBUTE_SELECCIO,
			seleccio);
		
		return seleccio.size();
	}
	
	@RequestMapping(value = "/{pareId}/select", method = RequestMethod.GET)
	@ResponseBody
	public int select(
			HttpServletRequest request,
			@PathVariable Long pareId,
			@RequestParam(value="docsIdx[]", required = false) Long[] docsIdx) {
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
		if (docsIdx != null) {
			for (Long id: docsIdx) {
				if(!seleccio.contains(id)) {
					seleccio.add(id);	
				}
			}
		}
		return seleccio.size();
	}

	@RequestMapping(value = "/{pareId}/deselect", method = RequestMethod.GET)
	@ResponseBody
	public int deselect(
			HttpServletRequest request,
			@PathVariable Long pareId,
			@RequestParam(value="docsIdx[]", required = false) Long[] docsIdx) {
		List<Long> idxRemove = new ArrayList<Long>();
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
		if (docsIdx != null) {
			List<Long> docsIdxList = Arrays.asList(docsIdx);
			for (Long id: seleccio) {
				if (docsIdxList.contains(id))
					idxRemove.add(id);
			}
		} else {
			seleccio.clear();
		}
		
		if (!idxRemove.isEmpty())
			seleccio.removeAll(idxRemove);
		
		return seleccio.size();
	}
	
	@RequestMapping(value = "/{pareId}/comprovarContingut", method = RequestMethod.GET)
	@ResponseBody
	public HashMap<String, Boolean> comprovarContingut(
			HttpServletRequest request,
			@PathVariable Long pareId,
			@RequestParam(value="docsIdx[]", required = false) Long[] docsIdx) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		HashMap<String, Boolean> resultat = new HashMap<String, Boolean>();
		Boolean isTotPdfFirmat = true;
		Boolean isTotPdf = true;
		if (docsIdx != null) {
			for (Long docId: docsIdx) {
				ContingutDto contingut = contingutService.findAmbIdUser(
						entitatActual.getId(),
						docId,
						true,
						false, null, null);
				if (contingut instanceof DocumentDto) {
					DocumentDto document = (DocumentDto) contingut;
					if ((!document.isFirmat() || document.isCustodiat())
							&& (document.isFirmat() || !document.isCustodiat())
							&& !document.isDefinitiu()) {
						isTotPdfFirmat = false;
						break;
					}
					if (document.getFitxerContentType() != null && !document.getFitxerContentType().equals("application/pdf")) {
						isTotPdf = false;
					}
				}
			}
		}
		resultat.put("isTotPdf", isTotPdf);
		resultat.put("isTotPdfFirmat", isTotPdfFirmat);
		return resultat;
	}

	@RequestMapping(value = "/{pareId}/comprovarContingut/{contingutId}", method = RequestMethod.GET)
	@ResponseBody
	public boolean comprovarContingut(
			HttpServletRequest request,
			@PathVariable Long pareId,
			@PathVariable Long contingutId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		boolean isDocument = true;
		
		ContingutDto contingut = contingutService.findAmbIdUser(
				entitatActual.getId(),
				contingutId,
				true,
				false, null, null);
		if (!contingut.isCarpeta())
			isDocument = true;
		else
			isDocument = false;
		
		return isDocument;
	}
	
	@RequestMapping(value = "/{contingutId}/document/{documentId}/descarregarImprimible", method = RequestMethod.GET)
	public String descarregarImprimible(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long contingutId,
			@PathVariable Long documentId) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

		try {
			contingutService.findAmbIdUser(
					entitatActual.getId(),
					documentId,
					true,
					false, null, null);
				FitxerDto fitxer = documentService.descarregarImprimible(
						entitatActual.getId(),
						documentId,
						null);
				writeFileToResponse(
						fitxer.getNom(),
						fitxer.getContingut(),
						response);
				return null;
		} catch (Exception e) {
			logger.error("Error al descarregar versio imprimible", e);
			Throwable root = ExceptionHelper.getRootCauseOrItself(e);
			
			if (root instanceof ConnectException || root.getMessage().contains("timed out")) {
				MissatgesHelper.error(
						request, 
						getMessage(request, "document.controller.descarregar.error") + ": " + getMessage(request, "error.arxiu.connectTimedOut"), root);
			} else {
				MissatgesHelper.error(
						request, 
						getMessage(request, "document.controller.descarregar.error") + ": " + root.getMessage(), root);
			}
			return "redirect:../../../../contingut/" + contingutId;
		}
		
	}
	
	@RequestMapping(value = "/{contingutId}/document/{documentId}/versio/{versio}/descarregar", method = RequestMethod.GET)
	public String descarregarVersio(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long contingutId,
			@PathVariable Long documentId,
			@PathVariable String versio) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		FitxerDto fitxer = documentService.descarregar(
				entitatActual.getId(),
				documentId,
				versio);
		writeFileToResponse(
				fitxer.getNom(),
				fitxer.getContingut(),
				response);
		return null;
	}

	@RequestMapping(value = "/{contingutId}/metaDocument/{metaDocumentId}", method = RequestMethod.GET)
	@ResponseBody
	public MetaDocumentDto metaDocumentInfo(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@PathVariable Long metaDocumentId) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		List<MetaDocumentDto> metaDocuments = metaDocumentService.findActiusPerCreacio(
				entitatActual.getId(),
				contingutId, 
				null,
				false);
		for (MetaDocumentDto metaDocument: metaDocuments) {
			if (metaDocument.getId().equals(metaDocumentId))
				return metaDocument;
		}
		return null;
	}

	@RequestMapping(value = "/{contingutId}/metaDocument/{metaDocumentId}/plantilla", method = RequestMethod.GET)
	public String metaDocumentPlantilla(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long contingutId,
			@PathVariable Long metaDocumentId) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		FitxerDto plantilla = metaDocumentService.getPlantilla(
				entitatActual.getId(),
				contingutId,
				metaDocumentId);
		writeFileToResponse(
				plantilla.getNom(),
				plantilla.getContingut(),
				response);
		return null;
	}
	
	@RequestMapping(value = "/{contingutId}/metaDocument/{metaDocumentId}/dadesnti", method = RequestMethod.GET)
	@ResponseBody
	public MetaDocumentDto metaDocumentDadesNti(
			HttpServletRequest request,
			HttpServletResponse response,
			Model model,
			@PathVariable Long contingutId,
			@PathVariable Long metaDocumentId) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		MetaDocumentDto document = metaDocumentService.getDadesNti(
				entitatActual.getId(), 
				contingutId, 
				metaDocumentId);
		return document;
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	    binder.registerCustomEditor(
		        Long[].class,
		        new StringArrayPropertyEditor(null)); 
	}
	
	private String obtenirEstatsElaboracioIdentificadorEniObligat() {
		return aplicacioService.propertyFindByNom("es.caib.ripea.estat.elaboracio.identificador.origen.obligat");
	}
	
	private String isMascaraPermesa() {
		return aplicacioService.propertyFindByNom("es.caib.ripea.identificador.origen.mascara");
	}
	
	private Boolean isPropagarModificacioDefinitiusActiva() {
		return aplicacioService.propertyBooleanFindByKey("es.caib.ripea.document.propagar.modificacio.arxiu");
	}
	
	private Boolean isDeteccioFirmaAutomaticaActiva() {
		return aplicacioService.propertyBooleanFindByKey("es.caib.ripea.document.deteccio.firma.automatica");
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
	
	private String createUpdateDocument(
			HttpServletRequest request,
			DocumentCommand command,
			boolean comprovarMetaExpedient,
			String rolActual) throws NotFoundException, ValidationException, IOException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ParseException {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

		Long pareId = command.getPareId();
		if (command.getId() == null) {
			
			DocumentDto document = documentService.create(
					entitatActual.getId(),
					pareId,
					DocumentCommand.asDto(command),
					comprovarMetaExpedient, 
					rolActual);
			
			crearDadesPerDefecteSiExisteixen(
					entitatActual.getId(),
					document.getId(),
					command.getMetaNodeId());
			

			if (document.getArxiuUuid() != null) {
				return getModalControllerReturnValueSuccess(
						request,
						"redirect:../../contingut/" + pareId,
						"document.controller.creat.ok");
			} else {
				return getModalControllerReturnValueWarning(
						request,
						"redirect:../../contingut/" + pareId,
						"document.controller.creat.error.arxiu",
						null);
			}
				
	
		} else {
			documentService.update(
					entitatActual.getId(),
					DocumentCommand.asDto(command),
					comprovarMetaExpedient, 
					rolActual);
			
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../contingut/" + pareId,
					"document.controller.modificat.ok");
		}
	}
	
	
	private void crearDadesPerDefecteSiExisteixen(
			Long entitatId,
			Long documentId,
			Long metaDocumentId) {
		
		try {
			List<DadaDto> dades = new ArrayList<DadaDto>();
			//Valor per defecte d'algunes metadades
			List<MetaDadaDto> metadades = metaDadaService.findByNode(
					entitatId, 
					documentId);
			for (MetaDadaDto metadada : metadades) {
				DadaDto dada = new DadaDto();
				dada.setMetaDada(metadada);
				dades.add(dada);
			}
			Object dadesCommand = beanGeneratorHelper.generarCommandDadesNode(
					entitatId,
					documentId,
					dades);
			Map<String, Object> valors = new HashMap<String, Object>();
			for (DadaDto dada: dades) {
				MetaDadaDto metaDada = metaDadaService.findById(
						entitatId, 
						metaDocumentId,
						dada.getMetaDada().getId());
				Object valor = PropertyUtils.getSimpleProperty(dadesCommand, metaDada.getCodi());
				if (valor != null && (!(valor instanceof String) || !((String) valor).isEmpty())) {
					valors.put(metaDada.getCodi(), valor);
				}
			}
			contingutService.dadaSave(
					entitatId,
					documentId,
					valors);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}

	
	private void omplirModelFormulari(
			HttpServletRequest request,
			DocumentCommand command,
			Long contingutId,
			Model model) throws ClassNotFoundException, IOException {
		organGestorService.actualitzarOrganCodi(organGestorService.getOrganCodiFromContingutId(contingutId));
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (command.getId() == null) {
			model.addAttribute(
					"metaDocuments",
					metaDocumentService.findActiusPerCreacio(
							entitatActual.getId(),
							contingutId, 
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
		boolean propertyEscanejarActiu = aplicacioService.propertyBooleanFindByKey("es.caib.ripea.document.nou.escanejar.actiu", false);
		model.addAttribute(
				"escanejarActiu",
				propertyEscanejarActiu);
		boolean modificacioCustodiatsActiva = aplicacioService.propertyBooleanFindByKey("es.caib.ripea.document.modificar.custodiats", false);
		model.addAttribute(
				"isPermesModificarCustodiats",
				modificacioCustodiatsActiva);

		model.addAttribute("contingutId", contingutId);
		model.addAttribute("estatsElaboracioIdentificadorEniObligat", obtenirEstatsElaboracioIdentificadorEniObligat());
		model.addAttribute("isMascaraPermesa", isMascaraPermesa() != null ? isMascaraPermesa() : true);
		model.addAttribute("isDeteccioFirmaAutomaticaActiva", isDeteccioFirmaAutomaticaActiva());
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ContingutDocumentController.class); 
}
