
/**
 * 
 */
package es.caib.ripea.back.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.back.helper.ExceptionHelper;
import es.caib.ripea.back.helper.RequestSessionHelper;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.DigitalitzacioPerfilDto;
import es.caib.ripea.service.intf.dto.DigitalitzacioResultatDto;
import es.caib.ripea.service.intf.dto.DigitalitzacioTransaccioRespostaDto;
import es.caib.ripea.service.intf.exception.SistemaExternException;
import es.caib.ripea.service.intf.model.sse.ScanFinalitzatEvent;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.DigitalitzacioService;
import es.caib.ripea.service.intf.service.EventService;
import es.caib.ripea.service.intf.utils.Utils;

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
	
	@Autowired private DigitalitzacioService digitalitzacioService;
	@Autowired private AplicacioService aplicacioService;
	@Autowired private EventService eventService;
	
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
			HttpServletRequest request,
			@RequestParam(value = "idExpedient", required = false) String idExpedient,
			@RequestParam(value = "idTransaccio", required = false) String idTransaccio,
			Model model) {
		model.addAttribute("idExpedient", idExpedient);
		model.addAttribute("idTransaccio", idTransaccio);
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

	@RequestMapping(value = "/event/resultatScan/{dades}/{idTransaccio}", method = RequestMethod.GET,  produces = "text/plain")
	@ResponseBody
	public ResponseEntity<String> recuperarResultatScanEvent(
			HttpServletRequest request,
			@PathVariable String dades,
			@PathVariable String idTransaccio,
			Model model) {
		// Autenticar un usuari simulat si és necessari
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || "anonymousUser".equals(auth.getName())) {
	        User user = new User("$portafib_ripea", "portafib_ripea", Collections.singletonList(new SimpleGrantedAuthority("tothom")));
	        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
	        SecurityContextHolder.getContext().setAuthentication(authentication);
        }
		String data = Utils.desencripta(dades);
		String[] dataSplri = data.split("#");
		Long idExpedient = Long.parseLong(dataSplri[0]);
		DigitalitzacioResultatDto resposta = recuperaResultatEscaneig(idTransaccio, true, true);
		resposta.setUsuari(dataSplri[2]);
		ScanFinalitzatEvent sfe = new ScanFinalitzatEvent(idExpedient, resposta);
		eventService.notifyScanFinalitzat(sfe);
		return ResponseEntity.ok().header("Content-Type", "text/plain; charset=UTF-8").body("Escaneig finalitzat.");
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
		
		DigitalitzacioResultatDto resposta = recuperaResultatEscaneig(idTransaccio, returnScannedFile, returnSignedFile);
		
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
	
	private DigitalitzacioResultatDto recuperaResultatEscaneig(String idTransaccio, boolean returnScannedFile, boolean returnSignedFile) {
		DigitalitzacioResultatDto resposta = digitalitzacioService.recuperarResultat(
				idTransaccio,
				returnScannedFile,
				returnSignedFile);
		
		if (aplicacioService.propertyBooleanFindByKey(PropertyConfig.DIGITALITZACIO_PLUGIN_DEBUG, false)) {
			logger.info("Recuperar resultat scan: " + ToStringBuilder.reflectionToString(resposta));
		}
		
		return resposta;
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
