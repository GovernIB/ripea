package es.caib.ripea.war.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.hibernate.exception.ConstraintViolationException;
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
import es.caib.ripea.core.api.service.PortafirmesFluxService;
import es.caib.ripea.core.api.service.TipusDocumentalService;
import es.caib.ripea.war.command.MetaDocumentCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.ExceptionHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per al manteniment de meta-documents no asociats a cap
 * meta-expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/metaDocument")
public class MetaDocumentController extends BaseAdminController {

	@Autowired
	private MetaDocumentService metaDocumentService;
	@Autowired
	private TipusDocumentalService tipusDocumentalService;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private PortafirmesFluxService portafirmesFluxService;

	@RequestMapping(method = RequestMethod.GET)
	public String getAll(HttpServletRequest request, Model model) {
		return "metaDocumentList";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				metaDocumentService.findWithoutMetaExpedient(
						entitatActual.getId(),
						DatatablesHelper.getPaginacioDtoFromRequest(request)));
		return dtr;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(HttpServletRequest request, Model model) {
		return get(request, null, model);
	}

	@RequestMapping(value = "/{metaDocumentId}", method = RequestMethod.GET)
	public String get(HttpServletRequest request, @PathVariable Long metaDocumentId, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		MetaDocumentDto metaDocument = null;
		if (metaDocumentId != null) {
			metaDocument = metaDocumentService.findById(entitatActual.getId(), null, metaDocumentId);
		}
		MetaDocumentCommand command = null;
		if (metaDocument != null) {
			model.addAttribute("portafirmesFluxSeleccionat", metaDocument.getPortafirmesFluxId());
			command = MetaDocumentCommand.asCommand(metaDocument);
		} else {
			command = new MetaDocumentCommand();
		}
		command.setEntitatId(entitatActual.getId());
		model.addAttribute(command);
		emplenarModelForm(request, model);
		return "metaDocumentForm";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid MetaDocumentCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		if (bindingResult.hasErrors()) {
			emplenarModelForm(request, model);
			return "metaDocumentForm";
		}
		if (command.getId() != null) {
			metaDocumentService.update(
					entitatActual.getId(),
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

	@RequestMapping(value = "/{metaDocumentId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, @PathVariable Long metaDocumentId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		try {
			metaDocumentService.delete(entitatActual.getId(), null, metaDocumentId);
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../../metaDocument",
					"metadocument.controller.esborrat.ok");
		} catch (Exception ex) {
			if (ExceptionHelper.isExceptionOrCauseInstanceOf(ex, DataIntegrityViolationException.class) ||
					ExceptionHelper.isExceptionOrCauseInstanceOf(ex, ConstraintViolationException.class))
				return getAjaxControllerReturnValueError(
						request,
						"redirect:../../esborrat",
						"metadocument.controller.esborrar.error.fk");
			else {
				throw ex;
			}

		}
	}

	@RequestMapping(value = "/{metaDocumentId}/enable", method = RequestMethod.GET)
	public String enable(HttpServletRequest request, @PathVariable Long metaDocumentId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		metaDocumentService.updateActiu(entitatActual.getId(), null, metaDocumentId, true);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaDocument",
				"metadocument.controller.activat.ok");
	}

	@RequestMapping(value = "/{metaDocumentId}/disable", method = RequestMethod.GET)
	public String disable(HttpServletRequest request, @PathVariable Long metaDocumentId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		metaDocumentService.updateActiu(entitatActual.getId(), null, metaDocumentId, false);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaDocument",
				"metadocument.controller.desactivat.ok");
	}

	@RequestMapping(value = "/findAll", method = RequestMethod.GET)
	@ResponseBody
	public List<MetaDocumentDto> findAll(HttpServletRequest request, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		return metaDocumentService.findByEntitat(entitatActual.getId());
	}

	@RequestMapping(value = "/iniciarTransaccio", method = RequestMethod.GET)
	@ResponseBody
	public PortafirmesIniciFluxRespostaDto iniciarTransaccio(
			HttpServletRequest request,
			@RequestParam(value = "nom", required = false) String nom,
			@RequestParam(value = "plantillaId", required = false) String plantillaId,
			Model model) throws UnsupportedEncodingException {
		String urlReturn;
		PortafirmesIniciFluxRespostaDto transaccioResponse = null;
		String nomCodificat = new String(nom.getBytes(), "UTF-8");
		String descripcio = getMessage(request, "document.controller.portafirmes.flux.desc");
		try {
			urlReturn = aplicacioService.propertyBaseUrl() + "/metaExpedient/metaDocument/flux/returnurl/";
			if (plantillaId != null && !plantillaId.isEmpty()) {
				transaccioResponse = new PortafirmesIniciFluxRespostaDto();
				String urlEdicio = portafirmesFluxService.recuperarUrlEdicioPlantilla(plantillaId, urlReturn);
				transaccioResponse.setUrlRedireccio(urlEdicio);
			} else {
				transaccioResponse = portafirmesFluxService.iniciarFluxFirma(urlReturn, nomCodificat, descripcio, true);
			}
		} catch (Exception ex) {
			transaccioResponse = new PortafirmesIniciFluxRespostaDto();
			transaccioResponse.setError(true);
			transaccioResponse.setErrorDescripcio(ex.getMessage());
		}

		return transaccioResponse;
	}

	@RequestMapping(value = "/tancarTransaccio/{idTransaccio}", method = RequestMethod.GET)
	@ResponseBody
	public void tancarTransaccio(HttpServletRequest request, @PathVariable String idTransaccio, Model model) {
		portafirmesFluxService.tancarTransaccio(idTransaccio);
	}

	@RequestMapping(value = "/flux/returnurl/{transactionId}", method = RequestMethod.GET)
	public String transaccioEstat(HttpServletRequest request, @PathVariable String transactionId, Model model) {
		PortafirmesFluxRespostaDto resposta = portafirmesFluxService.recuperarFluxFirma(transactionId);

		if (resposta.isError() && resposta.getEstat() != null) {
			model.addAttribute(
					"FluxError",
					getMessage(request, "metadocument.form.camp.portafirmes.flux.enum." + resposta.getEstat()));
		} else {
			model.addAttribute(
					"FluxCreat",
					getMessage(request, "metadocument.form.camp.portafirmes.flux.enum.FINAL_OK"));
			model.addAttribute("fluxId", resposta.getFluxId());
			model.addAttribute("FluxNom", resposta.getNom());
		}
		return "portafirmesModalTancar";
	}

	@RequestMapping(value = "/flux/returnurl/", method = RequestMethod.GET)
	public String transaccioEstat(HttpServletRequest request, Model model) {
		model.addAttribute(
				"FluxCreat",
				getMessage(request, "metadocument.form.camp.portafirmes.flux.edicio.enum.FINAL_OK"));
		return "portafirmesModalTancar";
	}

	@RequestMapping(value = "/flux/plantilles", method = RequestMethod.GET)
	@ResponseBody
	public List<PortafirmesFluxRespostaDto> getPlantillesDisponibles(HttpServletRequest request, Model model) {
		List<PortafirmesFluxRespostaDto> resposta = portafirmesFluxService.recuperarPlantillesDisponibles();
		return resposta;
	}

	@RequestMapping(value = "/flux/esborrar/{plantillaId}", method = RequestMethod.GET)
	@ResponseBody
	public boolean esborrarPlantilla(HttpServletRequest request, @PathVariable String plantillaId, Model model) {
		return portafirmesFluxService.esborrarPlantilla(plantillaId);
	}

	private void emplenarModelForm(HttpServletRequest request, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		List<PortafirmesDocumentTipusDto> tipus = metaDocumentService.portafirmesFindDocumentTipus();
		List<TipusDocumentalDto> tipusDocumental = tipusDocumentalService.findByEntitat(entitatActual.getId());
		model.addAttribute("isPortafirmesDocumentTipusSuportat", new Boolean(tipus != null));
		model.addAttribute("portafirmesDocumentTipus", tipus);
		// Dades nti
		model.addAttribute(
				"ntiOrigenOptions",
				EnumHelper.getOptionsForEnum(NtiOrigenEnumDto.class, "document.nti.origen.enum."));
		model.addAttribute("ntiTipusDocumentalOptions", tipusDocumental);
		model.addAttribute(
				"ntiEstatElaboracioOptions",
				EnumHelper.getOptionsForEnum(DocumentNtiEstadoElaboracionEnumDto.class, "document.nti.estela.enum."));
		model.addAttribute(
				"isFirmaBiometrica",
				Boolean.parseBoolean(
						aplicacioService.propertyFindByNom("es.caib.ripea.documents.firma.biometrica.activa")));
	}

}
