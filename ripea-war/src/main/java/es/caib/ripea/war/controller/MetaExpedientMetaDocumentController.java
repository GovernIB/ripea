/**
 *
 */
package es.caib.ripea.war.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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

import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.NtiOrigenEnumDto;
import es.caib.ripea.core.api.dto.PortafirmesDocumentTipusDto;
import es.caib.ripea.core.api.dto.PortafirmesFluxRespostaDto;
import es.caib.ripea.core.api.dto.PortafirmesIniciFluxRespostaDto;
import es.caib.ripea.core.api.dto.TipusDocumentalDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.core.api.service.PortafirmesFluxService;
import es.caib.ripea.core.api.service.TipusDocumentalService;
import es.caib.ripea.war.command.MetaDocumentCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.EnumHelper;

/**
 * Controlador per al manteniment de meta-documents asociats a un meta-expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/metaExpedient")
public class MetaExpedientMetaDocumentController extends BaseAdminController {

	@Autowired
	private MetaDocumentService metaDocumentService;
	@Autowired
	private MetaExpedientService metaExpedientService;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private PortafirmesFluxService portafirmesFluxService;
	@Autowired
	private TipusDocumentalService tipusDocumentalService;

	@RequestMapping(value = "/{metaExpedientId}/metaDocument", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"metaExpedient",
				metaExpedientService.findById(
						entitatActual.getId(),
						metaExpedientId));
		return "metaExpedientMetaDocument";
	}
	
	@RequestMapping(value = "/{metaExpedientId}/metaDocument/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				metaDocumentService.findByMetaExpedient(
						entitatActual.getId(),
						metaExpedientId,
						DatatablesHelper.getPaginacioDtoFromRequest(request)));
		return dtr;
	}

	@RequestMapping(value = "/{metaExpedientId}/metaDocument/new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		return get(request, metaExpedientId, null, model);
	}
	
	@RequestMapping(value = "/{metaExpedientId}/metaDocument/{metaDocumentId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long metaDocumentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		MetaDocumentDto metaDocument = null;
		if (metaDocumentId != null) {
			metaDocument = metaDocumentService.findById(
					entitatActual.getId(),
					metaExpedientId,
					metaDocumentId);
		}
		MetaDocumentCommand command = null;
		if (metaDocument != null) {
			model.addAttribute("portafirmesFluxSeleccionat", metaDocument.getPortafirmesFluxId());
			command = MetaDocumentCommand.asCommand(metaDocument);
		} else {
			command = new MetaDocumentCommand();
		}
		command.setEntitatId(entitatActual.getId());
		command.setMetaExpedientId(metaExpedientId);
		model.addAttribute(command);
		emplenarModelForm(request,model);
		return "metaExpedientMetaDocumentForm";
	}
	
	@RequestMapping(value = "/{metaExpedientId}/metaDocument", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@Valid MetaDocumentCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			emplenarModelForm(request,model);
			return "metaExpedientMetaDocumentForm";
		}
		if (command.getId() != null) {
			metaDocumentService.update(
					entitatActual.getId(),
					metaExpedientId,
					MetaDocumentCommand.asDto(command),
					command.getPlantilla().getOriginalFilename(),
					command.getPlantilla().getContentType(),
					command.getPlantilla().getBytes());
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:metaDocument",
					"metadocument.controller.modificat.ok");
		} else {
			metaDocumentService.create(
					entitatActual.getId(),
					metaExpedientId,
					MetaDocumentCommand.asDto(command),
					command.getPlantilla().getOriginalFilename(),
					command.getPlantilla().getContentType(),
					command.getPlantilla().getBytes());
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:metaDocument",
					"metadocument.controller.creat.ok");
		}
	}

	@RequestMapping(value = "/{metaExpedientId}/metaDocument/{metaDocumentId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long metaDocumentId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		try {
			metaDocumentService.delete(
					entitatActual.getId(),
					metaExpedientId,
					metaDocumentId);
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../../metaDocument",
					"metadocument.controller.esborrat.ok");
		} catch (DataIntegrityViolationException ex) {
			return getAjaxControllerReturnValueError(
					request,
					"redirect:../../esborrat",
					"metadocument.controller.esborrar.error.fk");
		}
	}

	@RequestMapping(value = "/metaDocument/iniciarTransaccio", method = RequestMethod.GET)
	@ResponseBody
	public PortafirmesIniciFluxRespostaDto iniciarTransaccio(
			HttpServletRequest request,
			@RequestParam(value="nom", required = false) String nom,
			@RequestParam(value="plantillaId", required = false) String plantillaId,
			Model model) throws UnsupportedEncodingException {
		String urlReturn;
		PortafirmesIniciFluxRespostaDto transaccioResponse = null;
		String nomCodificat = new String(nom.getBytes(), "UTF-8");
		String descripcio = getMessage(
				request, 
				"document.controller.portafirmes.flux.desc");
		try {
			urlReturn = aplicacioService.propertyBaseUrl() + "/metaExpedient/metaDocument/flux/returnurl/";
			if (plantillaId != null && !plantillaId.isEmpty()) {
				transaccioResponse = new PortafirmesIniciFluxRespostaDto();
				String urlEdicio = portafirmesFluxService.recuperarUrlEdicioPlantilla(
						plantillaId, 
						urlReturn);
				transaccioResponse.setUrlRedireccio(urlEdicio);
			} else {
				transaccioResponse = portafirmesFluxService.iniciarFluxFirma(
						urlReturn,
						nomCodificat,
						descripcio,
						true);
			}
		} catch (Exception ex) {
			transaccioResponse = new PortafirmesIniciFluxRespostaDto();
			transaccioResponse.setError(true);
			transaccioResponse.setErrorDescripcio(ex.getMessage());
		}
		
		return transaccioResponse;
	}

	@RequestMapping(value = "/metaDocument/tancarTransaccio/{idTransaccio}", method = RequestMethod.GET)
	@ResponseBody
	public void tancarTransaccio(
			HttpServletRequest request,
			@PathVariable String idTransaccio,
			Model model) {
		portafirmesFluxService.tancarTransaccio(idTransaccio);
	}

	@RequestMapping(value = "/metaDocument/flux/returnurl/{transactionId}", method = RequestMethod.GET)
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
					"metadocument.form.camp.portafirmes.flux.enum.FINAL_OK"));
			model.addAttribute("fluxId", resposta.getFluxId());
			model.addAttribute("FluxNom", resposta.getNom());
		}
		return "portafirmesModalTancar";
	}
	
	@RequestMapping(value = "/metaDocument/flux/returnurl/", method = RequestMethod.GET)
	public String transaccioEstat(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(
				"FluxCreat",
				getMessage(
				request,
				"metadocument.form.camp.portafirmes.flux.edicio.enum.FINAL_OK"));
		return "portafirmesModalTancar";
	}
	
	@RequestMapping(value = "/metaDocument/flux/plantilles", method = RequestMethod.GET)
	@ResponseBody
	public List<PortafirmesFluxRespostaDto> getPlantillesDisponibles(
			HttpServletRequest request,
			Model model) {
		List<PortafirmesFluxRespostaDto> resposta = portafirmesFluxService.recuperarPlantillesDisponibles();
		return resposta;
	}
	
	@RequestMapping(value = "/metaDocument/flux/esborrar/{plantillaId}", method = RequestMethod.GET)
	@ResponseBody
	public boolean esborrarPlantilla(
			HttpServletRequest request,
			@PathVariable String plantillaId,
			Model model) {
		return portafirmesFluxService.esborrarPlantilla(plantillaId);
	}

	public void emplenarModelForm(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		List<PortafirmesDocumentTipusDto> tipus = metaDocumentService.portafirmesFindDocumentTipus();
		List<TipusDocumentalDto> tipusDocumental = tipusDocumentalService.findByEntitat(
				entitatActual.getId());
		model.addAttribute(
				"isPortafirmesDocumentTipusSuportat",
				new Boolean(tipus != null));
		model.addAttribute(
				"portafirmesDocumentTipus",
				tipus);
		//Dades nti
		model.addAttribute(
				"ntiOrigenOptions",
				EnumHelper.getOptionsForEnum(
						NtiOrigenEnumDto.class,
						"document.nti.origen.enum."));
		model.addAttribute(
				"ntiTipusDocumentalOptions",
				tipusDocumental);
		model.addAttribute(
				"ntiEstatElaboracioOptions",
				EnumHelper.getOptionsForEnum(
						DocumentNtiEstadoElaboracionEnumDto.class,
						"document.nti.estela.enum."));
		model.addAttribute("isFirmaBiometrica",
				Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.documents.firma.biometrica.activa")));

	}

}