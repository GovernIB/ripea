/**
 * 
 */
package es.caib.ripea.war.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.war.command.PassarelaFirmaEnviarCommand;
import es.caib.ripea.war.command.PortafirmesEnviarCommand;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.ModalHelper;
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

	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private DocumentService documentService;

	@Autowired
	private PassarelaFirmaHelper passarelaFirmaHelper;
	@Autowired
	private MetaDocumentService metaDocumentService;


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
		
		command.setPortafirmesFluxTipus(metaDocument.getPortafirmesFluxTipus());
		command.setPortafirmesResponsables(metaDocument.getPortafirmesResponsables());
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
		documentService.portafirmesEnviar(
				entitatActual.getId(),
				documentId,
				command.getMotiu(),
				command.getPrioritat(),
				command.getDataCaducitat(),
				command.getPortafirmesResponsables(),
				command.getPortafirmesFluxTipus());
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
		return "redirect:../firma/info";
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
		model.addAttribute(
				"portafirmes",
				documentService.portafirmesInfo(
						entitatActual.getId(),
						documentId));
		return "portafirmesInfo";
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
		if (ignorarModal) {
			return "redirect:/contingut/" + documentId;
		} else {
			return getModalControllerReturnValueSuccess(
					request, 
					"redirect:/contingut/" + documentId,
					null);
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

}
