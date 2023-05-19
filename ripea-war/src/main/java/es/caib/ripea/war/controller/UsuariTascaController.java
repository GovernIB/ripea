/**
 * 
 */
package es.caib.ripea.war.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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

import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentPortafirmesDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.core.api.dto.TascaEstatEnumDto;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.service.ExpedientTascaService;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.war.command.ExpedientPeticioFiltreCommand;
import es.caib.ripea.war.command.PortafirmesEnviarCommand;
import es.caib.ripea.war.command.UsuariTascaRebuigCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.RequestSessionHelper;
import es.caib.ripea.war.helper.RolHelper;
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


	@Autowired
	private ExpedientTascaService expedientTascaService;
	@Autowired
	private DocumentService documentService;
	@Autowired
	private MetaDocumentService metaDocumentService;
	

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

	
	private void emplenarModelPortafirmes(
			HttpServletRequest request,
			Long documentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DocumentDto document = documentService.findById(
				entitatActual.getId(),
				documentId, null);
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
