
/**
 * 
 */
package es.caib.ripea.war.controller;

import java.io.IOException;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import es.caib.ripea.core.api.dto.ArxiuDetallDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEnviamentDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentPortafirmesDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.FirmaResultatDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.core.api.dto.StatusEnumDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.dto.ViaFirmaDispositiuDto;
import es.caib.ripea.core.api.dto.ViaFirmaUsuariDto;
import es.caib.ripea.core.api.exception.ResponsableNoValidPortafirmesException;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.DocumentEnviamentService;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.service.ExpedientInteressatService;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.core.api.service.OrganGestorService;
import es.caib.ripea.war.command.FirmaSimpleWebCommand;
import es.caib.ripea.war.command.PortafirmesEnviarCommand;
import es.caib.ripea.war.command.ViaFirmaEnviarCommand;
import es.caib.ripea.war.helper.ExceptionHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;
import es.caib.ripea.war.helper.RolHelper;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador per al manteniment de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Controller
@RequestMapping("/document")
public class DocumentController extends BaseUserOAdminOOrganController {

	private static final String SESSION_ATTRIBUTE_TRANSACCIOID = "DocumentController.session.transaccioID";

	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private DocumentService documentService;
	@Autowired
	private MetaDocumentService metaDocumentService;
	@Autowired
	private DocumentEnviamentService documentEnviamentService;
	@Autowired
	private ContingutService contingutService;
	@Autowired
	private ExpedientInteressatService expedientInteressatService;
	@Autowired
	private OrganGestorService organGestorService;
	
	@RequestMapping(value = "/{documentId}/portafirmes/upload", method = RequestMethod.GET)
	public String portafirmesUploadGet(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@RequestParam(value = "tascaId", required = false) Long tascaId,
			Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DocumentDto document = documentService.findById(
				entitatActual.getId(),
				documentId, 
				tascaId);
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
				document.getMetaDocument().getId());		
		
		command.setPortafirmesSequenciaTipus(metaDocument.getPortafirmesSequenciaTipus());
		command.setPortafirmesResponsables(metaDocument.getPortafirmesResponsables());
		setFluxPredefinit(
				metaDocument, 
				model, 
				command);
		RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_TRANSACCIOID);
		model.addAttribute("isNouEnviament", true);
		model.addAttribute("tascaId", tascaId);
		model.addAttribute(command);
		model.addAttribute("isHabilitarAvisFirmaParcialActiu", isHabilitarAvisFirmaParcialActiu());
		model.addAttribute("isCreacioFluxUsuariActiu", isCreacioFluxUsuariActiu());
		return "portafirmesForm";
	}
	
	@RequestMapping(value = "/{documentId}/portafirmes/upload", method = RequestMethod.POST)
	public String portafirmesUploadPost(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@RequestParam(value = "tascaId", required = false) Long tascaId,
			@Valid PortafirmesEnviarCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DocumentDto document = documentService.findById(
				entitatActual.getId(),
				documentId, 
				tascaId);
		MetaDocumentDto metaDocument = metaDocumentService.findById(
				document.getMetaDocument().getId());
		
		if (command.getPortafirmesFluxTipus() == MetaDocumentFirmaFluxTipusEnumDto.SIMPLE && (command.getPortafirmesResponsables() == null || command.getPortafirmesResponsables().length == 0)) {
			bindingResult.rejectValue("portafirmesResponsables", "NotNull");
		}
		
		if (bindingResult.hasErrors()) {
			setFluxPredefinit(
					metaDocument, 
					model, 
					command);
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
		if (command.getPortafirmesFluxTipus().equals(MetaDocumentFirmaFluxTipusEnumDto.PORTAFIB) && 
				(metaDocument.getPortafirmesFluxId() == null || metaDocument.getPortafirmesFluxId().isEmpty()) &&
				(transaccioId == null || transaccioId.isEmpty()) && 
				(command.getPortafirmesEnviarFluxId() == null || command.getPortafirmesEnviarFluxId().isEmpty())) {
			emplenarModelPortafirmes(
					request,
					documentId,
					model);
			setFluxPredefinit(
					metaDocument, 
					model, 
					command);
			MissatgesHelper.error(
					request, 
					getMessage(
							request,
							"document.controller.portafirmes.flux.ko"),
					null);
			return "portafirmesForm";
		}
		try {
			documentService.portafirmesEnviar(entitatActual.getId(),
					documentId,
					command.getMotiu(),
					command.getPrioritat(),
					command.getPortafirmesEnviarFluxId(), //selecció flux
					command.getPortafirmesResponsables(),
					command.getPortafirmesSequenciaTipus(),
					command.getPortafirmesFluxTipus(),
					command.getAnnexos(),
					transaccioId, 
					RolHelper.getRolActual(request), 
					tascaId,
					command.isAvisFirmaParcial());
			
			return this.getModalControllerReturnValueSuccess(
					request,
					"redirect:../../../contingut/" + documentId,
					"document.controller.portafirmes.upload.ok",
					new Object[] {document.getNom()});	
			
		} catch (Exception ex) {
			String missatge = ExceptionHelper.isExceptionOrCauseInstanceOf(ex, ResponsableNoValidPortafirmesException.class)
					? getMessage(request,"document.controller.portafirmes.upload.error.responsableNoValidPortafrimes") : ex.getCause().getMessage();
			MissatgesHelper.error(request, missatge, ex);
			setFluxPredefinit(metaDocument, model, command);
			emplenarModelPortafirmes(request, documentId, model);
			log.error("Error al upload del document ", ex);
			return "portafirmesForm";
		}
	}

	@RequestMapping(value = "/{documentId}/portafirmes/reintentar", method = RequestMethod.GET)
	public String portafirmesReintentar(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@RequestParam(value = "tascaId", required = false) Long tascaId,
			@RequestParam(value = "readOnly", required = true) boolean readOnly,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		Exception exc = documentService.portafirmesReintentar(
				entitatActual.getId(),
				documentId,
				RolHelper.getRolActual(request),
				tascaId);
		DocumentDto doc = documentService.findById(entitatActual.getId(), documentId, null);
		if (exc != null || doc.getGesDocFirmatId() != null) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"firma.info.processat.ko"),
					exc);
			return "redirect:./info?readOnly=" + readOnly + "&tascaId=" + (tascaId == null ? "" : tascaId);
		} else {
			return this.getModalControllerReturnValueSuccess(
					request,
					"redirect:../../../contingut/" + doc.getExpedientPare().getId() + "?tascaId=" + (tascaId == null ? "" : tascaId),
					"firma.info.processat.ok");
		}
	}
	
	@RequestMapping(value = "/{documentId}/portafirmes/reintentarGuardarArxiu", method = RequestMethod.GET)
	public String portafirmesReintentarGuardarArxiu(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@RequestParam(value = "tascaId", required = false) Long tascaId,
			Model model) {
		
		model.addAttribute(
				"tascaId", 
				tascaId);
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		Exception exc = documentService.portafirmesReintentar(
				entitatActual.getId(),
				documentId,
				RolHelper.getRolActual(request), 
				tascaId);
		DocumentDto doc = documentService.findById(entitatActual.getId(), documentId, null);
		if (exc == null) {
			return this.getModalControllerReturnValueSuccess(
					request,
					"redirect:../../../contingut/" + doc.getExpedientPare().getId() + "?tascaId=" + (tascaId == null ? "" : tascaId),
					"document.controller.guardar.arxiu.ok");

		} else {
			Throwable root = ExceptionHelper.getRootCauseOrItself(exc);
			String msg = null;
			if (root instanceof ConnectException || (root.getMessage() != null && root.getMessage().contains("timed out"))) {
				msg = getMessage(request,"error.arxiu.connectTimedOut");
			} else {
				msg = root.getMessage();
			}
			return getAjaxControllerReturnValueError(
					request,
					"redirect:../../../contingut/" + doc.getExpedientPare().getId() + "?tascaId=" + (tascaId == null ? "" : tascaId),
					"document.controller.guardar.arxiu.error",
					new Object[] {msg},
					root);
		}
	}
	
	

	@RequestMapping(value = "/{documentId}/portafirmes/cancel", method = RequestMethod.GET)
	public String portafirmesCancel(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@RequestParam(value = "tascaId", required = false) Long tascaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		documentService.portafirmesCancelar(
				entitatActual.getId(),
				documentId, 
				RolHelper.getRolActual(request), 
				tascaId);
		return this.getModalControllerReturnValueSuccess(
				request,
				"redirect:../../../contingut/" + documentId + "?tascaId=" + (tascaId == null ? "" : tascaId),
				"document.controller.portafirmes.cancel.ok");
	}

	@RequestMapping(value = "/{documentId}/portafirmes/info", method = RequestMethod.GET)
	public String portafirmesInfo(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@RequestParam(value = "readOnly", required = true) boolean readOnly,
			@RequestParam(value = "tascaId", required = false) Long tascaId,
			Model model) {
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			
			DocumentPortafirmesDto portafirmes = documentService.portafirmesInfo(
					entitatActual.getId(),
					documentId, 
					null);
			model.addAttribute(
					"portafirmes",
					portafirmes);
			
			String urlFluxFirmes = null;
			try {
				urlFluxFirmes = documentService.recuperarUrlViewEstatFluxDeFirmes(Long.valueOf(portafirmes.getPortafirmesId()));
			} catch (Exception e) {
				logger.error("Error al recuperar urlFluxFirmes: " +  portafirmes.getPortafirmesId() + ", " + portafirmes.getId()+ ", " + documentId);
			}
			model.addAttribute(
					"urlFluxFirmes", 
					urlFluxFirmes);

			model.addAttribute(
					"document", 
					documentService.findById(entitatActual.getId(), documentId, tascaId));
			
			model.addAttribute(
					"tascaId", 
					tascaId);
			
			model.addAttribute(
					"readOnly", 
					readOnly);
			
		} catch (Exception e) {
			return getModalControllerReturnValueErrorMessageText(
					request,
					"",
					e.getMessage(),
					e);
			}
		return "portafirmesInfo";
	}
	
	@RequestMapping(value = "/{documentId}/viafirma/info", method = RequestMethod.GET)
	public String viaFirmaInfo(
			HttpServletRequest request,
			@PathVariable Long documentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"viafirma",
				documentService.viaFirmaInfo(
						entitatActual.getId(),
						documentId));
		return "viaFirmaInfo";
	}

	@RequestMapping(value = "/{documentId}/viafirma/reintentar", method = RequestMethod.GET)
	public String viaFirmaReintentar(
			HttpServletRequest request,
			@PathVariable Long documentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		documentService.viaFirmaReintentar(
				entitatActual.getId(),
				documentId);
		return this.getModalControllerReturnValueSuccess(
				request,
				"redirect:../../../contingut/" + documentId,
				"document.controller.viafirma.reintentar.ok");
	}
//	
//	@RequestMapping(value = "/{documentId}/custodia/reintentar", method = RequestMethod.GET)
//	public String custodiaReintentar(
//			HttpServletRequest request,
//			@PathVariable Long documentId,
//			Model model) {
//		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
//		documentService.portafirmesReintentar(
//				entitatActual.getId(),
//				documentId, 
//				RolHelper.getRolActual(request), 
//				null);
//		return this.getAjaxControllerReturnValueSuccess(
//				request,
//				"redirect:../../../../../../../contingut/" + documentId,
//				"document.controller.custodia.reintentar.ok");
//	}
//
//	@RequestMapping(value = "/{documentId}/custodia/info", method = RequestMethod.GET)
//	public String custodiaInfo(
//			HttpServletRequest request,
//			@PathVariable Long documentId,
//			Model model) {
//		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
//		DocumentDto document = documentService.findById(
//				entitatActual.getId(),
//				documentId, 
//				null);
//		return "redirect:" + document.getCustodiaUrl();
//	}

	@RequestMapping(value = "/{documentId}/pdf", method = RequestMethod.GET)
	public String convertirPdf(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long documentId) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		FitxerDto convertit = documentService.convertirPdfPerFirmaClient(
				entitatActual.getId(),
				documentId);
		writeFileToResponse(
				convertit.getNom(),
				convertit.getContingut(),
				response);
		return null;
	}

	@RequestMapping(value = "/{documentId}/firmaSimpleWeb", method = RequestMethod.GET)
	public String firmaSimpleWebGet(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@RequestParam(value = "tascaId", required = false) Long tascaId,
			@RequestParam(value = "massiu", required = false, defaultValue = "false") boolean massiu,
			Model model) {
		
		RequestSessionHelper.actualitzarObjecteSessio(
				request,
				"massiu",
				massiu);
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DocumentDto document = documentService.findById(
				entitatActual.getId(),
				documentId, 
				null);
		model.addAttribute("document", document);
		FirmaSimpleWebCommand command = new FirmaSimpleWebCommand();
		command.setMotiu(getMessage(
						request, 
						"contenidor.document.portafirmes.camp.motiu.default") +
				" [" + document.getExpedientPare().getNom() + "]");
		model.addAttribute(command);
		model.addAttribute("tascaId", tascaId);
		return "firmaSimpleWebForm";
	}
	
	
	
	
	@RequestMapping(value = "/{documentId}/firmaSimpleWebStart", method = RequestMethod.POST)
	public String firmaSimpleWebStart(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@RequestParam(value = "tascaId", required = false) Long tascaId,
			@Valid FirmaSimpleWebCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {

		Long entitatActualId = getEntitatActualComprovantPermisos(request).getId();
		organGestorService.actualitzarOrganCodi(organGestorService.getOrganCodiFromContingutId(documentId));
		
		if (bindingResult.hasErrors()) {
			emplenarModelFirmaClient(
					request,
					documentId,
					model);
			return "firmaSimpleWebForm";
		}

		FitxerDto fitxerPerFirmar = documentService.convertirPdfPerFirmaClient(
				entitatActualId,
				documentId);
		fitxerPerFirmar.setId(documentId);
		
		String urlReturnToRipea = aplicacioService.propertyBaseUrl() + "/document/" + documentId + "/firmaSimpleWebEnd?tascaId=" + (tascaId == null ? "" : tascaId);
		
		String urlRedirectToPortafib = documentService.firmaSimpleWebStart(
				fitxerPerFirmar,
				command.getMotiu(),
				urlReturnToRipea);
		
		return "redirect:" + urlRedirectToPortafib;

	}

	
	@RequestMapping(value = "/{documentId}/firmaSimpleWebEnd")
	public String firmaSimpleWebEnd(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long documentId,
			@RequestParam(value = "transactionID", required = true) String transactionID,
			@RequestParam(value = "tascaId", required = false) Long tascaId) throws Exception {
		
		Long entitatActualId = getEntitatActualComprovantPermisos(request).getId();
		organGestorService.actualitzarOrganCodi(organGestorService.getOrganCodiFromContingutId(documentId));
		
		FirmaResultatDto firmaResultat =  documentService.firmaSimpleWebEnd(transactionID);
		
		if (firmaResultat.getStatus() == StatusEnumDto.OK && firmaResultat.getSignatures().get(0).getStatus() == StatusEnumDto.OK) {
			
			documentService.processarFirmaClient(
					entitatActualId,
					documentId,
					firmaResultat.getSignatures().get(0).getFitxerFirmatNom(), 
					firmaResultat.getSignatures().get(0).getFitxerFirmatContingut(), 
					RolHelper.getRolActual(request), 
					tascaId);
			
			MissatgesHelper.success(
					request,
					getMessage(
							request, 
							"document.controller.firma.passarela.final.ok"));

		} else if (firmaResultat.getStatus() == StatusEnumDto.WARNING) {
			MissatgesHelper.warning(
					request,
					firmaResultat.getMsg());
			
		} else if (firmaResultat.getStatus() == StatusEnumDto.ERROR) {
			MissatgesHelper.error(
					request,
					firmaResultat.getMsg());
		}
		
		
		boolean massiu = (boolean) RequestSessionHelper.obtenirObjecteSessio(
				request,
				"massiu");

		if (massiu) {
			return "redirect:/massiu/firmasimpleweb";
		} else {
			return "redirect:/contingut/" + contingutService.getExpedientId(documentId) + "?tascaId=" + (tascaId == null ? "" : tascaId);
		}

	}
	


	@RequestMapping(value = "/{documentId}/viafirma/upload", method = RequestMethod.GET)
	public String viaFirmaUploadGet(
			HttpServletRequest request,
			@PathVariable Long documentId,
			Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DocumentDto document = documentService.findById(
				entitatActual.getId(),
				documentId, 
				null);
		model.addAttribute("document", document);
		
		ViaFirmaEnviarCommand command = new ViaFirmaEnviarCommand();
		command.setTitol(document.getPare().getNom());
		command.setDescripcio(recuperarMotiu(
				request,
				document));
		model.addAttribute(
				"interessats",
				expedientInteressatService.findByExpedient(
						entitatActual.getId(),
						document.getExpedientPare().getId(),
						false));
		model.addAttribute(command);
		model.addAttribute("isDispositiusEnabled", isViaFirmaDispositiusEnabled());
		return "viaFirmaForm";
	}
	
	@RequestMapping(value = "/{documentId}/viafirma/cancel", method = RequestMethod.GET)
	public String viaFirmaCancel(
			HttpServletRequest request,
			@PathVariable Long documentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		documentService.viaFirmaCancelar(
				entitatActual.getId(),
				documentId);
		return this.getModalControllerReturnValueSuccess(
				request,
				"redirect:../../../contingut/" + documentId,
				"document.controller.viafirma.cancel.ok");
	}

	
	@RequestMapping(value = "/{documentId}/viafirma/upload", method = RequestMethod.POST)
	public String viaFirmaUploadPost(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@Valid ViaFirmaEnviarCommand command,
			BindingResult bindingResult,
			Model model) {
		UsuariDto usuariActual = aplicacioService.getUsuariActual();
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			emplenarModelPortafirmes(
					request,
					documentId,
					model);
			return "viaFirmaForm";
		}
		try {
			documentService.viaFirmaEnviar(
					entitatActual.getId(),
					documentId,
					ViaFirmaEnviarCommand.asDto(command),
					usuariActual);
		} catch (Exception ex) {
			emplenarModelPortafirmes(
					request,
					documentId,
					model);
			MissatgesHelper.error(request, ex.getMessage(), ex);
			return "viaFirmaForm";
		}
		return this.getModalControllerReturnValueSuccess(
				request,
				"redirect:../../../contingut/" + documentId,
				"document.controller.viafirma.upload.ok");
	}

	@RequestMapping(value = "/viafirma/usuaris", method = RequestMethod.GET)
	@ResponseBody
	public List<ViaFirmaUsuariDto> getUsuarisViaFirma(
			HttpServletRequest request,
			Model model) {
		UsuariDto usuariActual = aplicacioService.getUsuariActual();
		List<ViaFirmaUsuariDto> viaFirmaUsuarisDto = documentService.viaFirmaUsuaris(usuariActual);
		return viaFirmaUsuarisDto;
	}
	
	@RequestMapping(value = "/viafirma/dispositius/{viaFirmaUsuari}", method = RequestMethod.GET)
	@ResponseBody
	public List<ViaFirmaDispositiuDto> getDispositiusViaFirma(
			HttpServletRequest request,
			Model model,
			@PathVariable String viaFirmaUsuari) {
		List<ViaFirmaDispositiuDto> dispositius = new ArrayList<ViaFirmaDispositiuDto>();
		UsuariDto usuariActual = aplicacioService.getUsuariActual();
		dispositius = documentService.viaFirmaDispositius(
				viaFirmaUsuari,
				usuariActual);
		return dispositius;
	}
	
	@RequestMapping(value = "/{documentId}/enviament/datatable", method = RequestMethod.GET)
	@ResponseBody
	public List<DocumentEnviamentDto> enviamentDatatable(
			HttpServletRequest request,
			@PathVariable Long documentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return documentEnviamentService.findNotificacionsAmbDocument(
						entitatActual.getId(),
						documentId);		
	}
	
	@RequestMapping(value = "/{documentId}/urlValidacio", method = RequestMethod.GET)
	@ResponseBody
	public String getUrlValidacio(
			HttpServletRequest request,
			@PathVariable Long documentId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		String urlValidacio = aplicacioService.propertyFindByNom("es.caib.ripea.documents.validacio.url");
		
		ArxiuDetallDto arxiuDetall = contingutService.getArxiuDetall(
				entitatActual.getId(),
				documentId);
		
		if (urlValidacio != null) {
			return urlValidacio + arxiuDetall.getMetadadesAddicionals().get("csv");
		}
		return urlValidacio;
	}
	
	@RequestMapping(value = "/{documentId}/convertir", method = RequestMethod.GET)
	public String convertir(
			HttpServletRequest request,
			@PathVariable Long documentId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		Long pareId = contingutService.getPareId(
				documentId);
		documentService.documentActualitzarEstat(
				entitatActual.getId(), 
				documentId, 
				DocumentEstatEnumDto.DEFINITIU);
		return this.getModalControllerReturnValueSuccess(
				request,
				"redirect:../../contingut/" + pareId,
				"document.controller.estat.canviat.ok");
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
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}



	private void emplenarModelPortafirmes(
			HttpServletRequest request,
			Long documentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DocumentDto document = documentService.findById(
				entitatActual.getId(),
				documentId, 
				null);
		model.addAttribute("document", document);
		model.addAttribute("annexos", 
				documentService.findAnnexosAmbExpedient(
						entitatActual.getId(), 
						document));
		model.addAttribute(
				"interessats",
				expedientInteressatService.findByExpedient(
						entitatActual.getId(),
						document.getExpedientPare().getId(),
						false));
		model.addAttribute("isDispositiusEnabled", isViaFirmaDispositiusEnabled());
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

	private String recuperarMotiu(
			HttpServletRequest request,
			DocumentDto document) {
		return getMessage(request, "document.controller.viafirma.motiu") + document.getNom() + " [" + document.getMetaNode().getNom() + "]";
	}
	
	private boolean isViaFirmaDispositiusEnabled() {
		return Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.plugin.viafirma.caib.dispositius.enabled"));
	}
	
	private boolean isHabilitarAvisFirmaParcialActiu() {
		return Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.portafirmes.avis.firma.parcial"));
	}
	
	private boolean isCreacioFluxUsuariActiu() {
		return Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.plugin.portafirmes.fluxos.usuaris"));
	}
	
	private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);
	
}
