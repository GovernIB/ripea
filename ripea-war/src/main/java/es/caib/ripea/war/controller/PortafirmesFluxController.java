
/**
 * 
 */
package es.caib.ripea.war.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.PortafirmesFluxRespostaDto;
import es.caib.ripea.core.api.dto.PortafirmesIniciFluxRespostaDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.PortafirmesFluxService;
import es.caib.ripea.war.helper.RequestSessionHelper;

/**
 * Controlador per definir fluxos de firma
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/document")
public class PortafirmesFluxController extends BaseUserController {

	private static final String SESSION_ATTRIBUTE_TRANSACCIOID = "DocumentController.session.transaccioID";

	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private PortafirmesFluxService portafirmesFluxService;
	
	@RequestMapping(value = "/portafirmes/iniciarTransaccio", method = RequestMethod.GET)
	@ResponseBody
	public PortafirmesIniciFluxRespostaDto iniciarTransaccio(
			HttpServletRequest request,
			@RequestParam(value="nom", required = false) String nom,
			Model model) {
		PortafirmesIniciFluxRespostaDto transaccioResponse = null;
//		String nomCodificat = new String(nom.getBytes(), StandardCharsets.UTF_8);
//		String descripcio = getMessage(
//				request, 
//				"document.controller.portafirmes.flux.desc");

		String urlReturn = aplicacioService.propertyBaseUrl() + "/document/portafirmes/flux/returnurl/";
		try {
			transaccioResponse = portafirmesFluxService.iniciarFluxFirma(
					urlReturn,
					false);
		} catch (Exception ex) {
			transaccioResponse = new PortafirmesIniciFluxRespostaDto();
			transaccioResponse.setError(true);
			transaccioResponse.setErrorDescripcio(ex.getMessage());
		}
		return transaccioResponse;
	}
	
	@RequestMapping(value = "/portafirmes/flux/mostrar", method = RequestMethod.GET)
	@ResponseBody
	public PortafirmesIniciFluxRespostaDto mostrarFlux(
			HttpServletRequest request,
			@RequestParam(value = "plantillaId", required = false) String plantillaId,
			Model model) {
		PortafirmesIniciFluxRespostaDto transaccioResponse = null;
		try {
			if (plantillaId != null && !plantillaId.isEmpty()) {
				transaccioResponse = new PortafirmesIniciFluxRespostaDto();
				String urlEdicio = portafirmesFluxService.recuperarUrlMostrarPlantilla(plantillaId);
				transaccioResponse.setUrlRedireccio(urlEdicio);
			}
		} catch (Exception ex) {
			transaccioResponse = new PortafirmesIniciFluxRespostaDto();
			transaccioResponse.setError(true);
			transaccioResponse.setErrorDescripcio(ex.getMessage());
		}
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
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}

}
