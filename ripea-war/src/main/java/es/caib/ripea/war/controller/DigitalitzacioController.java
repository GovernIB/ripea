
/**
 * 
 */
package es.caib.ripea.war.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.DigitalitzacioPerfilDto;
import es.caib.ripea.core.api.dto.DigitalitzacioResultatDto;
import es.caib.ripea.core.api.dto.DigitalitzacioTransaccioRespostaDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.DigitalitzacioService;
import es.caib.ripea.war.helper.RequestSessionHelper;

/**
 * Controlador per al manteniment de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/digitalitzacio")
public class DigitalitzacioController extends BaseUserController {

	private static final String SESSION_ATTRIBUTE_RETURN_SCANNED = "DigitalitzacioController.session.scanned";
	private static final String SESSION_ATTRIBUTE_RETURN_SIGNED = "DigitalitzacioController.session.signed";
	private static final String SESSION_ATTRIBUTE_RETURN_IDTRANSACCIO = "DigitalitzacioController.session.idTransaccio";
	
	@Autowired
	private DigitalitzacioService digitalitzacioService;
	@Autowired
	private AplicacioService aplicacioService;
	
	@RequestMapping(value = "/perfils", method = RequestMethod.GET)
	@ResponseBody
	public List<DigitalitzacioPerfilDto> digitalitzaciGetPerfils(
			HttpServletRequest request,
			Model model) {
		List<DigitalitzacioPerfilDto> perfils = digitalitzacioService.getPerfilsDisponibles();
		
		return perfils;
	}
	@RequestMapping(value = "/iniciarDigitalitzacio/{codiPerfil}", method = RequestMethod.GET)
	@ResponseBody
	public DigitalitzacioTransaccioRespostaDto iniciarDigitalitzacio(
			HttpServletRequest request,
			@PathVariable String codiPerfil,
			Model model) {
		RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_RETURN_SCANNED);
		RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_RETURN_SIGNED);
		
		String urlReturn = aplicacioService.propertyBaseUrl() + "/digitalitzacio/recuperarResultat/";
		DigitalitzacioTransaccioRespostaDto transaccioResponse = digitalitzacioService.iniciarDigitalitzacio(
				codiPerfil, 
				urlReturn);
		
		RequestSessionHelper.actualitzarObjecteSessio(
				request, 
				SESSION_ATTRIBUTE_RETURN_SCANNED, 
				transaccioResponse.isReturnScannedFile());
		RequestSessionHelper.actualitzarObjecteSessio(
				request, 
				SESSION_ATTRIBUTE_RETURN_SIGNED, 
				transaccioResponse.isReturnSignedFile());
		RequestSessionHelper.actualitzarObjecteSessio(
				request, 
				SESSION_ATTRIBUTE_RETURN_IDTRANSACCIO, 
				transaccioResponse.getIdTransaccio());
		return transaccioResponse;
	}

	@RequestMapping(value = "/recuperarResultat/{idTransaccio}", method = RequestMethod.GET)
	public String recuperarResultat(
			HttpServletRequest request,
			@PathVariable String idTransaccio,
			Model model) {
		boolean returnScannedFile = (boolean) RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_RETURN_SCANNED);
		boolean returnSignedFile = (boolean) RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_RETURN_SIGNED);
		
		DigitalitzacioResultatDto resposta = digitalitzacioService.recuperarResultat(
				idTransaccio,
				returnScannedFile,
				returnSignedFile);
		
		if (resposta.isError() && resposta.getErrorTipus() != null) {
			model.addAttribute(
						"digitalizacioError",
						getMessage(
						request,
						"document.digitalitzacio.estat.enum."+ resposta.getErrorTipus()));
		} else {
			model.addAttribute(
					"digitalizacioFinalOk",
					getMessage(
					request,
					"document.digitalitzacio.estat.enum.FINAL_OK"));
			model.addAttribute("nomDocument", resposta.getNomDocument());
		}
		return "digitalitzacioIframeTancar";
	}
	
	@RequestMapping(value = "/descarregarResultat/{idTransaccio}", method = RequestMethod.GET)
	public void descarregarResultat(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable String idTransaccio,
			Model model) throws IOException {
		boolean returnScannedFile = (boolean) RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_RETURN_SCANNED);
		boolean returnSignedFile = (boolean) RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_RETURN_SIGNED);
		
		DigitalitzacioResultatDto resposta = digitalitzacioService.recuperarResultat(
				idTransaccio,
				returnScannedFile,
				returnSignedFile);
		writeFileToResponse(
				resposta.getNomDocument(),
				resposta.getContingut(),
				response);
	}	
	
	@RequestMapping(value = "/tancarTransaccio/{idTransaccio}", method = RequestMethod.GET)
	@ResponseBody
	public void tancarTransaccio(
			HttpServletRequest request,
			@PathVariable String idTransaccio) throws IOException {
		RequestSessionHelper.esborrarObjecteSessio(
				request, 
				SESSION_ATTRIBUTE_RETURN_SCANNED);
		RequestSessionHelper.esborrarObjecteSessio(
				request, 
				SESSION_ATTRIBUTE_RETURN_SIGNED);
		RequestSessionHelper.esborrarObjecteSessio(
				request, 
				SESSION_ATTRIBUTE_RETURN_IDTRANSACCIO);
		
		digitalitzacioService.tancarTransaccio(
				idTransaccio);
	}	


}
