package es.caib.ripea.back.controller;

import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.model.sse.CreacioFluxFinalitzatEvent;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.EventService;
import es.caib.ripea.service.intf.service.MetaDocumentService;
import es.caib.ripea.service.intf.service.PortafirmesFluxService;
import es.caib.ripea.service.intf.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Controlador dels serveis de flux de firma de portafirmes dels tipus de document.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */

@Controller
@RequestMapping("/metaDocument")
public class MetaDocumentController extends BaseAdminController {

	@Autowired private MetaDocumentService metaDocumentService;
	@Autowired private AplicacioService aplicacioService;
	@Autowired private PortafirmesFluxService portafirmesFluxService;
	@Autowired private EventService eventService;

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
		try {
			urlReturn = aplicacioService.propertyBaseUrl() + "/metaDocument/flux/returnurl/";
			if (plantillaId != null && !plantillaId.isEmpty()) {
				transaccioResponse = new PortafirmesIniciFluxRespostaDto();
				String urlEdicio = portafirmesFluxService.recuperarUrlEdicioPlantilla(plantillaId, urlReturn);
				transaccioResponse.setUrlRedireccio(urlEdicio);
			} else {
				transaccioResponse = portafirmesFluxService.iniciarFluxFirma(urlReturn, true);
			}
		} catch (Exception ex) {
			logger.error("Error al iniciar transacio", ex);
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
			model.addAttribute("FluxError", getMessage(request, "metadocument.form.camp.portafirmes.flux.enum." + resposta.getEstat()));
		} else {
			model.addAttribute("FluxCreat", getMessage(request, "metadocument.form.camp.portafirmes.flux.enum.FINAL_OK"));
			model.addAttribute("fluxId", resposta.getFluxId());
			model.addAttribute("FluxNom", resposta.getNom());
			model.addAttribute("FluxDescripcio", resposta.getDescripcio());
		}
		return "portafirmesModalTancar";
	}
	
	@RequestMapping(value = "/flux/event/{paramSecure}/returnurl/{transactionId}", method = RequestMethod.GET)
	public void transaccioEstat(
			HttpServletRequest request,
			@PathVariable String paramSecure,
			@PathVariable String transactionId,
			Model model) {
		String data = Utils.desencripta(paramSecure, aplicacioService.propertyFindByNom("es.caib.ripea.encription.key"));
		String[] dataSplri = data.split("#");
		Long metaDocumentId = null;
		if (dataSplri[1]!=null && !"null".equals(dataSplri[1].toString()) && Utils.hasValue(dataSplri[1].toString())) {
			metaDocumentId = Long.parseLong(dataSplri[1].toString());
		}
		PortafirmesFluxRespostaDto resposta = portafirmesFluxService.recuperarFluxFirma(transactionId);
		Long fluxId = portafirmesFluxService.guardarFluxFirmaMetaDocumentRipea(metaDocumentId, resposta);
		resposta.setId(fluxId);
		CreacioFluxFinalitzatEvent fluxEvent = new CreacioFluxFinalitzatEvent(
				null,
				metaDocumentId,
				dataSplri[0], //entitatCodi
				dataSplri[2], //usuariCodi
				resposta);
		eventService.notifyFluxFirmaCreat(fluxEvent);
	}

	@RequestMapping(value = "/flux/returnurl/", method = RequestMethod.GET)
	public String transaccioEstat(HttpServletRequest request, Model model) {
		model.addAttribute("FluxCreat", getMessage(request, "metadocument.form.camp.portafirmes.flux.edicio.enum.FINAL_OK"));
		return "portafirmesModalTancar";
	}

	@RequestMapping(value = "/flux/plantilles", method = RequestMethod.GET)
	@ResponseBody
	public List<PortafirmesFluxRespostaDto> getPlantillesDisponibles(HttpServletRequest request, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		List<PortafirmesFluxRespostaDto> resposta = portafirmesFluxService.recuperarPlantillesDisponibles(
				entitatActual.getId(),
				null,
				false,
				false);
		return resposta;
	}

	@RequestMapping(value = "/flux/esborrar/{plantillaId}", method = RequestMethod.GET)
	@ResponseBody
	public boolean esborrarPlantilla(HttpServletRequest request, @PathVariable String plantillaId, Model model) {
		return portafirmesFluxService.esborrarPlantilla(plantillaId);
	}

	private static final Logger logger = LoggerFactory.getLogger(MetaDocumentController.class);
}