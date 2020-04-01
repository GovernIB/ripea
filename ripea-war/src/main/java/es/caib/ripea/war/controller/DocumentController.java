
/**
 * 
 */
package es.caib.ripea.war.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.fundaciobit.plugins.signature.api.FileInfoSignature;
import org.fundaciobit.plugins.signature.api.StatusSignature;
import org.fundaciobit.plugins.signature.api.StatusSignaturesSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.i18n.LocaleContextHolder;
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

import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEnviamentDto;
import es.caib.ripea.core.api.dto.DocumentPortafirmesDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.core.api.dto.PortafirmesFluxInfoDto;
import es.caib.ripea.core.api.dto.PortafirmesFluxRespostaDto;
import es.caib.ripea.core.api.dto.PortafirmesIniciFluxRespostaDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.dto.ViaFirmaDispositiuDto;
import es.caib.ripea.core.api.dto.ViaFirmaUsuariDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.DocumentEnviamentService;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.service.PortafirmesFluxService;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.war.command.PassarelaFirmaEnviarCommand;
import es.caib.ripea.war.command.PortafirmesEnviarCommand;
import es.caib.ripea.war.command.ViaFirmaEnviarCommand;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.ModalHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;
import es.caib.ripea.war.passarelafirma.PassarelaFirmaConfig;
import es.caib.ripea.war.passarelafirma.PassarelaFirmaHelper;

/**
 * Controlador per al manteniment de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/document")
public class DocumentController extends BaseUserController {

	private static final String SESSION_ATTRIBUTE_TRANSACCIOID = "DocumentController.session.transaccioID";

	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private DocumentService documentService;

	@Autowired
	private PassarelaFirmaHelper passarelaFirmaHelper;
	@Autowired
	private MetaDocumentService metaDocumentService;
	@Autowired
	private DocumentEnviamentService documentEnviamentService;
	@Autowired
	private PortafirmesFluxService portafirmesFluxService;

	@RequestMapping(value = "/{documentId}/portafirmes/upload", method = RequestMethod.GET)
	public String portafirmesUploadGet(
			HttpServletRequest request,
			@PathVariable Long documentId,
			Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DocumentDto document = documentService.findById(
				entitatActual.getId(),
				documentId);
		model.addAttribute("document", document);
		
		PortafirmesEnviarCommand command = new PortafirmesEnviarCommand();
		command.setMotiu(
				getMessage(
						request, 
						"contenidor.document.portafirmes.camp.motiu.default") +
				" [" + document.getExpedientPare().getNom() + "]");
		
		MetaDocumentDto metaDocument = metaDocumentService.findById(
				entitatActual.getId(),
				document.getMetaDocument().getId());		
		
		
		model.addAttribute("fluxTipus", metaDocument.getPortafirmesFluxTipus());
		command.setPortafirmesSequenciaTipus(metaDocument.getPortafirmesSequenciaTipus());
		command.setPortafirmesResponsables(metaDocument.getPortafirmesResponsables());
		setFluxPredefinit(
				metaDocument, 
				model, 
				command);
		RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_TRANSACCIOID);
		model.addAttribute(command);
		return "portafirmesForm";
	}
	@RequestMapping(value = "/{documentId}/portafirmes/upload", method = RequestMethod.POST)
	public String portafirmesUploadPost(
			HttpServletRequest request,
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
		DocumentDto document = documentService.findById(
				entitatActual.getId(),
				documentId);
		
		MetaDocumentDto metaDocument = metaDocumentService.findById(
				entitatActual.getId(),
				document.getMetaDocument().getId());
		
		String transaccioId = null;
		if (command.getPortafirmesFluxTipus().equals(MetaDocumentFirmaFluxTipusEnumDto.PORTAFIB)) {
			transaccioId = (String)RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_TRANSACCIOID);
		}
		
		if ((command.getPortafirmesFluxTipus().equals(MetaDocumentFirmaFluxTipusEnumDto.PORTAFIB) && metaDocument.getPortafirmesFluxId() == null) &&
				(command.getPortafirmesFluxTipus().equals(MetaDocumentFirmaFluxTipusEnumDto.PORTAFIB) && transaccioId == null)) {
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
					"document.controller.portafirmes.flux.ko");
			model.addAttribute("fluxTipus", metaDocument.getPortafirmesFluxTipus());
			return "portafirmesForm";
		}
		documentService.portafirmesEnviar(
				entitatActual.getId(),
				documentId,
				command.getMotiu(),
				command.getPrioritat(),
				command.getDataCaducitat(),
				command.getPortafirmesResponsables(),
				command.getPortafirmesSequenciaTipus(),
				command.getPortafirmesFluxTipus(),
				transaccioId);
		
		return this.getModalControllerReturnValueSuccess(
				request,
				"redirect:../../../contingut/" + documentId,
				"document.controller.portafirmes.upload.ok");
	}

	@RequestMapping(value = "/{documentId}/portafirmes/reintentar", method = RequestMethod.GET)
	public String portafirmesReintentar(
			HttpServletRequest request,
			@PathVariable Long documentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		documentService.portafirmesReintentar(
				entitatActual.getId(),
				documentId);
		return "redirect:./info";
	}

	@RequestMapping(value = "/{documentId}/portafirmes/cancel", method = RequestMethod.GET)
	public String portafirmesCancel(
			HttpServletRequest request,
			@PathVariable Long documentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		documentService.portafirmesCancelar(
				entitatActual.getId(),
				documentId);
		return this.getModalControllerReturnValueSuccess(
				request,
				"redirect:../../../contingut/" + documentId,
				"document.controller.portafirmes.cancel.ok");
	}

	@RequestMapping(value = "/{documentId}/portafirmes/info", method = RequestMethod.GET)
	public String portafirmesInfo(
			HttpServletRequest request,
			@PathVariable Long documentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DocumentPortafirmesDto portafirmes = documentService.portafirmesInfo(
				entitatActual.getId(),
				documentId);
		
		model.addAttribute(
				"portafirmes",
				portafirmes);
		
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
	
	@RequestMapping(value = "/{documentId}/custodia/reintentar", method = RequestMethod.GET)
	public String custodiaReintentar(
			HttpServletRequest request,
			@PathVariable Long documentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		documentService.portafirmesReintentar(
				entitatActual.getId(),
				documentId);
		return this.getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../../../../../../contingut/" + documentId,
				"document.controller.custodia.reintentar.ok");
	}

	@RequestMapping(value = "/{documentId}/custodia/info", method = RequestMethod.GET)
	public String custodiaInfo(
			HttpServletRequest request,
			@PathVariable Long documentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DocumentDto document = documentService.findById(
				entitatActual.getId(),
				documentId);
		return "redirect:" + document.getCustodiaUrl();
	}

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

	@RequestMapping(value = "/{documentId}/firmaPassarela", method = RequestMethod.GET)
	public String firmaPassarelaGet(
			HttpServletRequest request,
			@PathVariable Long documentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DocumentDto document = documentService.findById(
				entitatActual.getId(),
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
	@RequestMapping(value = "/{documentId}/firmaPassarela", method = RequestMethod.POST)
	public String firmaPassarelaPost(
			HttpServletRequest request,
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
			String identificador = documentService.generarIdentificadorFirmaClient(
					entitatActual.getId(),
					documentId);
			documentService.processarFirmaClient(
					identificador,
					command.getFirma().getOriginalFilename(),
					command.getFirma().getBytes());
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
			FitxerDto fitxerPerFirmar = documentService.convertirPdfPerFirmaClient(
					entitatActual.getId(),
					documentId);
			UsuariDto usuariActual = aplicacioService.getUsuariActual();
			String modalStr = (ModalHelper.isModal(request)) ? "/modal" : "";
			String procesFirmaUrl = passarelaFirmaHelper.iniciarProcesDeFirma(
					request,
					fitxerPerFirmar,
					usuariActual.getNif(),
					command.getMotiu(),
					(command.getLloc() != null) ? command.getLloc() : "RIPEA",
					usuariActual.getEmail(),
					LocaleContextHolder.getLocale().getLanguage(),
					modalStr + "/document/" + documentId + "/firmaPassarelaFinal",
					false);
			return "redirect:" + procesFirmaUrl;
		}
	}

	@RequestMapping(value = "/{documentId}/firmaPassarelaFinal")
	public String firmaPassarelaFinal(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@RequestParam("signaturesSetId") String signaturesSetId,
			Model model) throws IOException {
		PassarelaFirmaConfig signaturesSet = passarelaFirmaHelper.finalitzarProcesDeFirma(
				request,
				signaturesSetId);
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
									"document.controller.firma.passarela.final.ok.nofile"));
				} else {
					FileInputStream fis = new FileInputStream(firmaStatus.getSignedData());
					EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
					String identificador = documentService.generarIdentificadorFirmaClient(
							entitatActual.getId(),
							documentId);
					documentService.processarFirmaClient(
							identificador,
							firmaStatus.getSignedData().getName(),
							IOUtils.toByteArray(fis));
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
								"document.controller.firma.passarela.final.ok.statuserr"));
			}
			break;
		case StatusSignaturesSet.STATUS_FINAL_ERROR:
			MissatgesHelper.error(
					request,
					getMessage(
							request, 
							"document.controller.firma.passarela.final.error",
							new Object[] {status.getErrorMsg()}));
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
		passarelaFirmaHelper.closeSignaturesSet(
				request,
				signaturesSet);
		boolean ignorarModal = false;
		String ignorarModalIdsProperty = aplicacioService.propertyPluginPassarelaFirmaIgnorarModalIds();
		if (ignorarModalIdsProperty != null && !ignorarModalIdsProperty.isEmpty()) {
			String[] ignorarModalIds = ignorarModalIdsProperty.split(",");
			for (String ignorarModalId: ignorarModalIds) {
				if (StringUtils.isNumeric(ignorarModalId)) {
					if (new Long(ignorarModalId).longValue() == signaturesSet.getPluginId().longValue()) {
						ignorarModal = true;
						break;
					}
				}
			}
		}
		String forsarTancamentModal = aplicacioService.propertyFindByNom("plugin.passarelafirma.forsar.tancament.modal");
		if (ignorarModal) {
			return "redirect:/contingut/" + documentId;
		} else if (forsarTancamentModal == null || "true".equalsIgnoreCase(forsarTancamentModal)) {
			return "redirect:/passarelaModalTancar";
		} else {
			return getModalControllerReturnValueSuccess(
					request, 
					"redirect:/contingut/" + documentId,
					null);
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
				documentId);
		model.addAttribute("document", document);
		
		ViaFirmaEnviarCommand command = new ViaFirmaEnviarCommand();
		command.setTitol(document.getPare().getNom());
		command.setDescripcio(recuperarMotiu(
				request,
				document));
		
		model.addAttribute(command);
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
			MissatgesHelper.error(request, ex.getMessage());
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
	
	@RequestMapping(value = "/portafirmes/iniciarTransaccio", method = RequestMethod.GET)
	@ResponseBody
	public PortafirmesIniciFluxRespostaDto iniciarTransaccio(
			HttpServletRequest request,
			@RequestParam(value="nom", required = false) String nom,
			Model model) {
		String descripcio = getMessage(
				request, 
				"document.controller.portafirmes.flux.desc");
		
		String urlReturn = aplicacioService.propertyBaseUrl() + "/document/portafirmes/flux/returnurl/";
		PortafirmesIniciFluxRespostaDto transaccioResponse = portafirmesFluxService.iniciarFluxFirma(
				urlReturn,
				nom,
				descripcio,
				false);
		return transaccioResponse;
	}
	
	@RequestMapping(value = "/portafirmes/tancarTransaccio/{idTransaccio}", method = RequestMethod.GET)
	@ResponseBody
	public void tancarTransaccio(
			HttpServletRequest request,
			@PathVariable String idTransaccio,
			Model model) {
		portafirmesFluxService.tancarTransaccio(idTransaccio);
	}

	@RequestMapping(value = "/portafirmes/flux/returnurl/{transactionId}", method = RequestMethod.GET)
	public String transaccioEstat(
			HttpServletRequest request,
			@PathVariable String transactionId,
			Model model) {
		PortafirmesFluxRespostaDto resposta = portafirmesFluxService.recuperarFluxFirma(transactionId);

		if (resposta.isError() && resposta.getEstat() != null) {
			model.addAttribute(
						"FluxError",
						getMessage(
						request,
						"metadocument.form.camp.portafirmes.flux.enum." + resposta.getEstat()));
		} else {
			model.addAttribute(
					"FluxCreat",
					getMessage(
					request,
					"metadocument.form.camp.portafirmes.flux.enum." + resposta.getEstat()));
			model.addAttribute(
					"FluxNom", resposta.getNom());
			model.addAttribute(
					"FluxDescripcio", resposta.getDescripcio());
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_TRANSACCIOID,
					transactionId);
		}
		return "portafirmesModalTancar";
	}
	
	private void setFluxPredefinit(
			MetaDocumentDto metaDocument,
			Model model,
			PortafirmesEnviarCommand command) {
		if (metaDocument.getPortafirmesFluxTipus() != null) {
			command.setPortafirmesFluxTipus(metaDocument.getPortafirmesFluxTipus());
			if (metaDocument.getPortafirmesFluxTipus().equals(MetaDocumentFirmaFluxTipusEnumDto.PORTAFIB) && metaDocument.getPortafirmesFluxId() == null) {
				model.addAttribute("nouFluxDeFirma", true);
			} else {
				model.addAttribute("nouFluxDeFirma", false);
			}
		} else {
			model.addAttribute("nouFluxDeFirma", false);
			command.setPortafirmesFluxTipus(MetaDocumentFirmaFluxTipusEnumDto.SIMPLE);
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
}
