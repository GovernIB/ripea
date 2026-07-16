package es.caib.ripea.back.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.back.helper.EntitatHelper;
import es.caib.ripea.back.helper.EnumHelper.HtmlOption;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.UsuariDto;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.EntitatService;
import es.caib.ripea.service.intf.service.SegonPlaService;

/**
 * Controlador per a les consultes ajax dels usuaris normals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/userajax") // No podem posar "/ajaxuser" per mor del AjaxInterceptor
public class AjaxUserController extends BaseUserController {

	@Autowired private AplicacioService aplicacioService;
	@Autowired private SegonPlaService segonPlaService;
	@Autowired private EntitatService entitatService;

	/**
	 * Cancelar enviament a PF de documents eliminats
	 */
	@RequestMapping(value = "/cancelaPortafirmesEliminats", method = RequestMethod.GET)
	public String cancelaPortafirmesEliminats(HttpServletRequest request, Model model) {
		model.addAttribute("titolProces", "Cancelar enviament a PF de documents eliminats");
		model.addAttribute("urlTotalIteracions", "getPortafirmesEliminats");
		model.addAttribute("urlInteracioIndividual", "executePortafirmesEliminat");
		return "util/processAjax";
	}
	@RequestMapping(value = "/getPortafirmesEliminats", method = RequestMethod.GET)
	@ResponseBody
	public List<Long> getPortafirmesEliminats(HttpServletRequest request, Model model) {
		return aplicacioService.getPortafirmesEliminats();
	}
	@RequestMapping(value = "/executePortafirmesEliminat/{documentPortafirmesId}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> executePortafirmesEliminats(
			HttpServletRequest request,
			@PathVariable Long documentPortafirmesId,
			Model model) {
		try {
			String resultat = aplicacioService.executePortafirmesEliminat(documentPortafirmesId);
			return ResponseEntity.ok(resultat); // HTTP 200
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()); // HTTP 500
		}
	}
	
	/**
	 * Inicialitzar tasques Comanda
	 */
	@RequestMapping(value = "/initTasquesComanda", method = RequestMethod.GET)
	public String initTasquesComanda(HttpServletRequest request, Model model) {
		model.addAttribute("titolProces", "Inicialitzar tasques Comanda");
		model.addAttribute("urlTotalIteracions", "getTasquesComanda");
		model.addAttribute("urlInteracioIndividual", "executeTascaComanda");
		return "util/processAjax";
	}
	@RequestMapping(value = "/getTasquesComanda", method = RequestMethod.GET)
	@ResponseBody
	public List<Long> getTasquesComanda(HttpServletRequest request, Model model) {
		return aplicacioService.getTasquesComanda();
	}
	@RequestMapping(value = "/executeTascaComanda/{tascaId}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> executeTascaComanda(
			HttpServletRequest request,
			@PathVariable Long tascaId,
			Model model) {
		try {
			String resultat = aplicacioService.executeTascaComanda(tascaId);
			return ResponseEntity.ok(resultat); // HTTP 200
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()); // HTTP 500
		}
	}
	
	/**
	 * Inicialitzar tasques Comanda
	 */
	@RequestMapping(value = "/initAvisosComanda", method = RequestMethod.GET)
	public String initAvisosComanda(HttpServletRequest request, Model model) {
		model.addAttribute("titolProces", "Inicialitzar avisos Comanda");
		model.addAttribute("urlTotalIteracions", "getAvisosComanda");
		model.addAttribute("urlInteracioIndividual", "executeAvisComanda");
		return "util/processAjax";
	}
	@RequestMapping(value = "/getAvisosComanda", method = RequestMethod.GET)
	@ResponseBody
	public List<Long> getAvisosComanda(HttpServletRequest request, Model model) {
		return aplicacioService.getAvisosComanda();
	}
	@RequestMapping(value = "/executeAvisComanda/{expedientId}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> executeAvisComanda(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		try {
			String resultat = aplicacioService.executeAvisComanda(expedientId);
			return ResponseEntity.ok(resultat); // HTTP 200
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()); // HTTP 500
		}
	}
	
	
	
	@RequestMapping(value = "/usuari/{codi}", method = RequestMethod.GET)
	@ResponseBody
	public UsuariDto getByCodi(
			HttpServletRequest request,
			@PathVariable String codi,
			Model model) {
		UsuariDto aux = aplicacioService.findUsuariAmbCodi(codi);
		return aux;
	}
	
	@RequestMapping(value = "/getEntitatLogo", method = RequestMethod.GET)
	public String getEntitatLogo(HttpServletRequest request, HttpServletResponse response) throws IOException {
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request, entitatService);
		// If there is logo defined for entitat (in database) return it, if not return logo defined for application (in properties file)
		byte [] logo = entitatActual.getLogoImgBytes() != null ? entitatActual.getLogoImgBytes() : entitatService.getLogo();
		try {
			writeFileToResponse(null, logo, response);
		} catch (Exception ex) {}
		return null;
	}
	
	@RequestMapping(value = "/getFaviconLogo", method = RequestMethod.GET)
	public String getFaviconLogo(HttpServletRequest request, HttpServletResponse response) throws IOException {
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request, entitatService);
		try {
			writeFileToResponse(null, entitatActual.getFaviconImgBytes(), response);
		} catch (Exception ex) {}
		return null;
	}
	
	@RequestMapping(value = "/stats/{dataTextddmmyyyy}", method = RequestMethod.GET)
	@ResponseBody
	public String stats(
			HttpServletRequest request,
			@PathVariable String dataTextddmmyyyy,
			Model model) {
		try {
			SimpleDateFormat formato = new SimpleDateFormat("ddMMyyyy");
			Date fecha = formato.parse(dataTextddmmyyyy);

			segonPlaService.generarEstadistiquesDiaries(fecha);
			return "OK";
		} catch (Exception e) {
			e.printStackTrace();
			return "KO";
		}
	}

	// CERCA USUARIS a BBDD RIPEA (NO PLUGIN). SUGGEST
	@RequestMapping(value = "/usuaris/{text}", method = RequestMethod.GET)
	@ResponseBody
	public List<UsuariDto> get(
			HttpServletRequest request,
			@PathVariable String text,
			Model model) {
		String filter = decodedParam(text);
		return aplicacioService.findUsuariAmbText(filter);
	}

	// CERCA UN USUARI a BBDD RIPEA (NO PLUGIN). VALOR SUGGEST EMPLENAT
	@RequestMapping(value = "/usuari/item/{codi}", method = RequestMethod.GET)
	@ResponseBody
	public UsuariDto getByCodiSelector(
			HttpServletRequest request,
			@PathVariable String codi,
			Model model) {
		try {
			UsuariDto aux = aplicacioService.findUsuariAmbCodi(codi);
			return aux;
		} catch (Exception ex) {
			logger.error("Error al consultar la informació de l'usuari " + codi, ex);
			return null;
		}
	}
	
	// CERCA USUARIS a BBDD RIPEA, ELSE PLUGIN. (SUGGEST)
	@RequestMapping(value = "/usuarisDades/{text}", method = RequestMethod.GET, produces= {"application/json; charset=UTF-8"})
	@ResponseBody
	public List<UsuariDto> getPluginDadesUsuari(
			HttpServletRequest request,
			@PathVariable String text,
			Model model) {
		try {
			String filter = decodedParam(text);
			List<UsuariDto> resultat = new ArrayList<UsuariDto>();
			resultat = aplicacioService.findUsuariAmbText(filter);
			if (resultat==null || resultat.size()==0) {
				resultat = aplicacioService.findUsuariAmbTextDades(filter);
			}
			return resultat;
		} catch (Exception ex) {
			logger.error("Error al consultar la informació dels usuaris amb el filtre \"" + text + "\"", ex);
			return new ArrayList<UsuariDto>();
		}
	}
	
	// CERCA UN USUARI a BBDD RIPEA, ELSE PLUGIN. VALOR SUGGEST EMPLENAT
	@RequestMapping(value = "/usuariDades/item/{codi}", method = RequestMethod.GET)
	@ResponseBody
	public UsuariDto getByCodiPluginDadesUsuari(
			HttpServletRequest request,
			@PathVariable String codi,
			Model model) {
		try {
			UsuariDto aux = aplicacioService.findUsuariCarrecAmbCodiDades(codi);
			return aux;
		} catch (Exception ex) {
			logger.error("Error al consultar la informació de l'usuari " + codi, ex);
			return null;
		}
	}

	@RequestMapping(value = "/enum/{enumClass}", method = RequestMethod.GET)
	@ResponseBody
	public List<HtmlOption> enumValorsAmbText(
			HttpServletRequest request,
			@PathVariable String enumClass) throws ClassNotFoundException {
		Class<?> enumeracio = findEnumDtoClass(enumClass);
		StringBuilder textKeyPrefix = new StringBuilder();
		String[] textKeys = StringUtils.splitByCharacterTypeCamelCase(enumClass);
		for (String textKey: textKeys) {
			if (!"dto".equalsIgnoreCase(textKey)) {
				textKeyPrefix.append(textKey.toLowerCase());
				textKeyPrefix.append(".");
			}
		}
		List<HtmlOption> resposta = new ArrayList<HtmlOption>();
		if (enumeracio.isEnum()) {
			for (Object e: enumeracio.getEnumConstants()) {
				resposta.add(new HtmlOption( 
						((Enum<?>)e).name(),
						getMessage(
								request,
								textKeyPrefix.toString() + ((Enum<?>)e).name(),
								null)));
			}
		}
		return resposta;
	}
	
	private Class<?> findEnumDtoClass(String className) throws ClassNotFoundException{
		try {
			return Class.forName("es.caib.ripea.service.intf.dto." + className);
		} catch(ClassNotFoundException e) {
			// TODO: això hauria de cercar per tots els subpackages de dto
			return Class.forName("es.caib.ripea.service.intf.dto.historic." + className);
		}		
	}

	private String decodedParam(String param) {
		String decodedParam = param;
		if (param != null && !param.isEmpty()) {
			try {
				decodedParam = new String(param.getBytes("ISO-8859-1"), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return decodedParam;
	}

	/**
	 * Inicialitzar tipus documentals per a entitats existents sense registres
	 */
	@RequestMapping(value = "/initTipusDocumentalsEntitats", method = RequestMethod.GET)
	public String initTipusDocumentalsEntitats(HttpServletRequest request, Model model) {
		model.addAttribute("titolProces", "Inicialitzar tipus documentals per a entitats existents");
		model.addAttribute("urlTotalIteracions", "getEntitatsSenseTipusDocumentals");
		model.addAttribute("urlInteracioIndividual", "executeCrearTipusDocumentalsEntitat");
		return "util/processAjax";
	}

	@RequestMapping(value = "/getEntitatsSenseTipusDocumentals", method = RequestMethod.GET)
	@ResponseBody
	public List<Long> getEntitatsSenseTipusDocumentals(HttpServletRequest request, Model model) {
		return aplicacioService.getEntitatsSenseTipusDocumentals();
	}

	@RequestMapping(value = "/executeCrearTipusDocumentalsEntitat/{entitatId}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> executeCrearTipusDocumentalsEntitat(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			Model model) {
		try {
			String resultat = aplicacioService.executeCrearTipusDocumentalsEntitat(entitatId);
			return ResponseEntity.ok(resultat);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	/**
	 * Eliminar configuracions d'entitats inexistents
	 */
	@RequestMapping(value = "/initEliminaConfigsOrfes", method = RequestMethod.GET)
	public String initEliminaConfigsOrfes(HttpServletRequest request, Model model) {
		model.addAttribute("titolProces", "Eliminar configuracions d'entitats inexistents");
		model.addAttribute("urlTotalIteracions", "getConfigsAmbEntitat");
		model.addAttribute("urlInteracioIndividual", "executeEliminaConfigOrfe");
		return "util/processAjax";
	}
	@RequestMapping(value = "/getConfigsAmbEntitat", method = RequestMethod.GET)
	@ResponseBody
	public List<String> getConfigsAmbEntitat(HttpServletRequest request, Model model) {
		return aplicacioService.getConfigsAmbEntitat();
	}
	@RequestMapping(value = "/executeEliminaConfigOrfe/{key}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> executeEliminaConfigOrfe(
			HttpServletRequest request,
			@PathVariable String key,
			Model model) {
		try {
			String resultat = aplicacioService.executeEliminaConfigOrfe(key);
			return ResponseEntity.ok(resultat); // HTTP 200
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()); // HTTP 500
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(AjaxUserController.class);

}
