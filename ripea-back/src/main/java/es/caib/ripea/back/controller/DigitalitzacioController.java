
/**
 * 
 */
package es.caib.ripea.back.controller;

import es.caib.ripea.back.helper.ExceptionHelper;
import es.caib.ripea.back.helper.RequestSessionHelper;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.DigitalitzacioPerfilDto;
import es.caib.ripea.service.intf.dto.DigitalitzacioResultatDto;
import es.caib.ripea.service.intf.dto.DigitalitzacioTransaccioRespostaDto;
import es.caib.ripea.service.intf.exception.SistemaExternException;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.DigitalitzacioService;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
			HttpServletResponse response,
			Model model) {
		List<DigitalitzacioPerfilDto> perfils = new ArrayList<DigitalitzacioPerfilDto>();
		try {
			perfils = digitalitzacioService.getPerfilsDisponibles();
			
		} catch (Exception e) {
			logger.error("Error al initializar digitalitzacio", e);
			Exception sisExtExc = ExceptionHelper.findExceptionInstance(e, SistemaExternException.class, 3);
			if (sisExtExc != null) {
				perfils.add(new DigitalitzacioPerfilDto("SERVER_ERROR", "SERVER_ERROR", sisExtExc.getMessage(), -1));
			} else {
				perfils.add(new DigitalitzacioPerfilDto("SERVER_ERROR", "SERVER_ERROR", e.getMessage(), -1));
			}
		}
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
	
	@RequestMapping(value = "/mock", method = RequestMethod.GET)
	public String mock(
			HttpServletRequest request) {

		return "mockDigitalitzacio";
	}
	
	@RequestMapping(value = "/recuperarResultatMock/{idTransaccio}", method = RequestMethod.GET)
	public String recuperarResultatMock(
			HttpServletRequest request,
			@PathVariable String idTransaccio,
			Model model) {

		
		DigitalitzacioResultatDto resposta = new DigitalitzacioResultatDto();
		
		resposta.setNomDocument("Nom document");

		
		if (resposta.isError() && resposta.getEstat() != null) {
			model.addAttribute(
						"digitalizacioError",
						getMessage(
						request,
						"document.digitalitzacio.estat.enum."+ resposta.getEstat()));
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
		
		boolean debug = aplicacioService.propertyBooleanFindByKey(PropertyConfig.DIGITALITZACIO_LOGS, false);
		if (debug) {
			logger.info("Recuperar resultat scan: " + ToStringBuilder.reflectionToString(resposta));
		}
		
		if (resposta.isError() && resposta.getEstat() != null) {
			model.addAttribute(
						"digitalizacioError",
						getMessage(
						request,
						"document.digitalitzacio.estat.enum."+ resposta.getEstat()));
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

	private static final Logger logger = LoggerFactory.getLogger(DigitalitzacioController.class);

}
