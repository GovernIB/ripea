package es.caib.ripea.war.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.IdNomDto;
import es.caib.ripea.core.api.dto.IdiomaEnumDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.EntitatService;
import es.caib.ripea.core.api.service.OrganGestorService;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.war.command.UsuariCodiCommand;
import es.caib.ripea.war.command.UsuariCommand;
import es.caib.ripea.war.helper.EntitatHelper;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.ModalHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;
import es.caib.ripea.war.helper.RolHelper;
import es.caib.ripea.war.helper.SessioHelper;

/**
 * Controlador per al manteniment de regles.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/usuari")
public class UsuariController  extends BaseAdminController {
	
	@Autowired private AplicacioService aplicacioService;
	@Autowired private OrganGestorService organGestorService;
	@Autowired private EntitatService entitatService;

	@RequestMapping(value = "/configuracio", method = RequestMethod.GET)
	public String getConfiguracio(
			HttpServletRequest request,
			Model model) {
		UsuariDto usuari = aplicacioService.getUsuariActual();
		UsuariCommand usuariCommand = UsuariCommand.asCommand(usuari);
		model.addAttribute(usuariCommand);
		emplenaModel(request, model, usuariCommand);

        model.addAttribute("editEmailsCanviEstatRevisio",
                RolHelper.hasRolAdministrador(request) || RolHelper.hasRolRevisor(request));

		return "usuariForm";
	}

	@RequestMapping(value = "/configuracio", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			HttpServletResponse response,
			@Valid UsuariCommand command,
			BindingResult bindingResult,
			Model model) {

		String emailRegexPatternRFC5322 = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
		if (Utils.isNotEmpty(command.getEmailAlternatiu())) {
			boolean isValidEmail = Pattern.compile(emailRegexPatternRFC5322)
		      .matcher(command.getEmailAlternatiu())
		      .matches();
			if (!isValidEmail) {
				bindingResult.rejectValue("emailAlternatiu", "Pattern");
			}
		}

		if (bindingResult.hasErrors()) {
			emplenaModel(request, model, command);
			return "usuariForm";
		}

		UsuariDto us = aplicacioService.getUsuariActual();

		if (!Objects.equals(us.getProcedimentId(), command.getProcedimentId())) {
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					ExpedientController.SESSION_ATTRIBUTE_FILTRE);

			RequestSessionHelper.esborrarObjecteSessio(
					request,
					ExpedientPeticioController.SESSION_ATTRIBUTE_FILTRE);

			RequestSessionHelper.esborrarObjecteSessio(
					request,
					HistoricController.SESSION_ATTRIBUTE_FILTRE);

			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SeguimentNotificacionsController.SESSION_ATTRIBUTE_FILTRE);

			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SeguimentPinbalController.SESSION_ATTRIBUTE_FILTRE);

			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SeguimentExpedientsPendentsController.SESSION_ATTRIBUTE_FILTRE);

			RequestSessionHelper.esborrarObjecteSessio(
					request,
					DocumentMassiuPortafirmesController.SESSION_ATTRIBUTE_FILTRE);

			RequestSessionHelper.esborrarObjecteSessio(
					request,
					DocumentMassiuFirmaWebController.SESSION_ATTRIBUTE_FILTRE);

			RequestSessionHelper.esborrarObjecteSessio(
					request,
					ExpedientMassiuCanviEstatController.SESSION_ATTRIBUTE_FILTRE);

			RequestSessionHelper.esborrarObjecteSessio(
					request,
					ExpedientMassiuTancamentController.SESSION_ATTRIBUTE_FILTRE);

			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SeguimentArxiuPendentsController.SESSION_ATTRIBUTE_FILTRE_EXPEDIENTS);
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SeguimentArxiuPendentsController.SESSION_ATTRIBUTE_FILTRE_DOCUMENTS);
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SeguimentArxiuPendentsController.SESSION_ATTRIBUTE_FILTRE_INTERESSATS);

			RequestSessionHelper.esborrarObjecteSessio(
					request,
					MassiuAnnexProcesarController.SESSION_ATTRIBUTE_FILTRE);

		}

		SessioHelper.updateContenidorVista(
				request,
				command.getVistaActual());

		SessioHelper.updateMoureVista(
				request,
				command.getVistaMoureActual());
		
		UsuariDto usuari = aplicacioService.updateUsuariActual(UsuariCommand.asDto(command));
		SessioHelper.setUsuariActual(request, usuari);

		return getModalControllerReturnValueSuccess(
					request,
					"redirect:/",
					"usuari.controller.modificat.ok",
				new Object[] { command.getNom() });
	}

	private void emplenaModel(HttpServletRequest request, Model model, UsuariCommand usuari) {
		model.addAttribute(
				"idiomaEnumOptions",
				EnumHelper.getOptionsForEnum(IdiomaEnumDto.class, "usuari.form.camp.idioma.enum."));

		List<IdNomDto> numElementsPagina = new ArrayList<>();
		numElementsPagina.add(new IdNomDto(10l, "10"));
		numElementsPagina.add(new IdNomDto(20l, "20"));
		numElementsPagina.add(new IdNomDto(50l, "50"));
		numElementsPagina.add(new IdNomDto(100l, "100"));
		numElementsPagina.add(new IdNomDto(250l, "250"));

		model.addAttribute("numElementsPagina", numElementsPagina);

		String rolActual = RolHelper.getRolActual(request);

		List<EntitatDto> entitatsAccessibles = EntitatHelper.findEntitatsAccessibles(request, entitatService);
		model.addAttribute("entitats", entitatsAccessibles);

		// Obtindrem els procediments de la entitat per defecte, si hi té permís
		// En cas de no tenir definida entitat per defecte, mirarem si l'usuari només té accés a una entitat
		Long entitatPerObtenirProcediments = null;

		List<MetaExpedientDto> procediments = new ArrayList<>();
		if (usuari.getEntitatPerDefecteId() != null) {
			EntitatDto entitatPerDefecte = entitatService.findById(usuari.getEntitatPerDefecteId());
			if (entitatPerDefecte != null) {
				// Si no té permís sobre l'entitat per defecte configurada, com si no la tingúes configurada!
				if (entitatsAccessibles == null || entitatsAccessibles.isEmpty() || !entitatsAccessibles.contains(entitatPerDefecte)) {
					usuari.setEntitatPerDefecteId(null);
				} else {
					entitatPerObtenirProcediments = usuari.getEntitatPerDefecteId();
				}
			}
		}
		// Si no té entitat per defecte, però només té accés a una entitat
		if (entitatPerObtenirProcediments == null && entitatsAccessibles!= null && entitatsAccessibles.size() == 1) {
			entitatPerObtenirProcediments = entitatsAccessibles.get(0).getId();
		}
		if (entitatPerObtenirProcediments != null) {
			procediments = metaExpedientService.findActius(
					entitatPerObtenirProcediments,
					null,
					rolActual,
					false,
					null);
		}
		model.addAttribute("procediments", procediments);

	}

	@RequestMapping(value = "/entitat/procedimentsAccessibles/{entitatId}", method =RequestMethod.GET)
	@ResponseBody
	public List<MetaExpedientDto> getProcedimentsAccessiblesPerEntitat(
			HttpServletRequest request,
			@PathVariable Long entitatId) {

		if (entitatId == null) {
			return new ArrayList<>();
		}
		return metaExpedientService.findActius(
				entitatId,
				null,
				RolHelper.getRolActual(request),
				false,
				null);
	}
	
	/**
	 * Només per Jboss
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		UsuariDto usuari = aplicacioService.getUsuariActual();
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat != null) {
			organGestorService.evictOrganismesEntitatAmbPermis(entitat.getId(), usuari.getCodi());
		}
		aplicacioService.evictRolsDisponiblesEnAcls();
		entitatService.evictEntitatsAccessiblesUsuari();
		aplicacioService.evictRolsPerUsuari(usuari.getCodi());
		aplicacioService.evictCountAnotacionsPendents(usuari.getCodi());
		
		// Es itera sobre totes les cookies
		for(Cookie c : request.getCookies()) {
			// Es sobre escriu el valor de cada cookie a NULL
			Cookie ck = new Cookie(c.getName(), null);
			ck.setPath(request.getContextPath());
			response.addCookie(ck);
		}
		return "redirect:/";
	}
	
	@RequestMapping(value = "/username", method = RequestMethod.GET)
 	public String canviCodiGet(
 			HttpServletRequest request,
 			UsuariCodiCommand command, 
 			Model model) {
		if (command==null) {
			command = new UsuariCodiCommand();
		}
 		model.addAttribute(command);
 		return "usuariCodiForm";
 	}
	
	@RequestMapping(value = "/username/validar", method = RequestMethod.POST)
	@ResponseBody
	public String canviCodiValidar(
			HttpServletRequest request,
			Model model) {
		String usuarisBatch = request.getParameter("usuarisBatch");
		String usuarisValidacions = "";
		Map<String, String> usuarisMap = processTextArea(usuarisBatch);
		if (usuarisMap.size()>0) {
			int linia = usuarisMap.size();
			UsuariDto usuariActual = aplicacioService.getUsuariActual();
			for (Map.Entry<String, String> entry : usuarisMap.entrySet()) {
				
				String codiActual	= entry.getKey();
				String codiNou		= entry.getValue();
				
				if (Utils.hasValue(codiActual) && Utils.hasValue(codiNou)) {
					UsuariDto usuariAntic = aplicacioService.findUsuariAmbCodi(codiActual);
					boolean validat = false;
		 			if (usuariAntic == null) {
		 				usuarisValidacions += "- ERROR [linia "+linia+"]: El usuari origen '"+codiActual+"' no existeix. No es processarà si continuau.\n";
		 				validat = true;
		 			} else if (usuariAntic.getCodi().equals(usuariActual.getCodi())) {
		 				usuarisValidacions += "- ERROR [linia "+linia+"]: No es pot canviar el codi del usuari actualment loguejat. No es processarà si continuau.\n";
		 				validat = true;
		 			}
		 			
		 			//Si ja no es processará perque el usuari origen no existeix, no fa falta revisar el desti
		 			if (!validat) {
			 			UsuariDto usuariNou = aplicacioService.findUsuariAmbCodi(codiNou);
			 			if (usuariNou != null) {
			 				usuarisValidacions += "- WARN  [linia "+linia+"]: El usuari destí '"+codiNou+"' ja existeix. Es sobreescriurà si continuau.\n";
			 				validat = true;
			 			}
		 			}
		 			
		 			if (!validat) {
		 				usuarisValidacions += "- OK    [linia "+linia+"]: El usuari origen '"+codiActual+"' s'actualitzarà a '"+codiNou+"'.\n";
		 			}
		 			
				} else {
						usuarisValidacions += "- ERROR [linia "+linia+"]: Format incorrecte de les dades: ("+codiActual+"="+codiNou+"). No es processarà si continuau.\n";
				}
				linia--;
			}
		}
		return usuarisValidacions;
	}
	
	@RequestMapping(value = "/username", method = RequestMethod.POST)
 	public String canviCodiPost(
 			HttpServletRequest request,
 			HttpServletResponse response,
 			UsuariCodiCommand command,
 			BindingResult bindingResult,
 			Model model) {
 		try {
 			Long t0 = System.currentTimeMillis();
 			Map<String, String> usuarisMap = processTextArea(command.getUsuarisBatch());
 			String resultat = "<h4><strong>Resultat del procés d'actualització dels codis de "+usuarisMap.size()+" usuari/s.</strong></h4>";
 			if (usuarisMap.size()>0) {
	 			for (Map.Entry<String, String> entry : usuarisMap.entrySet()) {
	 				resultat+= "<h4>Update del usuari <span style='color: brown;'>'"+entry.getKey()+"'</span> per <span style='color: forestgreen;'>'"+entry.getValue()+"'</span></h4><ul>";
	 				try {
	 					resultat+=aplicacioService.updateUsuariCodi(entry.getKey(), entry.getValue());
	 				} catch (Exception e) {
	 					resultat+="<li>Error actualitzant el codi del usuari '"+entry.getKey()+"' per '"+entry.getValue()+"': <span style='color: salmon;'>"+e.getMessage()+"</span></li>";
	 					e.printStackTrace();
	 				}
	 				resultat+="</ul>";
	 			}
	 			resultat += "Finalitzat procés despres de "+((System.currentTimeMillis()-t0)/1000)+" segons.";
	 			command.setResultat(resultat);
	 			model.addAttribute(command);
	 			request.setAttribute(ModalHelper.REQUEST_ATTRIBUTE_MODAL, new Boolean(true));
	 			return "usuariCodiForm";
 			} else {
 				return getModalControllerReturnValueError(request, "usuariCodiForm", "usuari.controller.codi.modificat.error", null);
 			}
 		} catch (Exception e) {
 			return getModalControllerReturnValueError(request, "usuariCodiForm", "usuari.controller.codi.modificat.error", e);
 		}
 	}
	
	private Map<String, String> processTextArea(String input) {
        Map<String, String> usuarisMap = new HashMap<>();

        if (Utils.hasValue(input)) {
        
	        // Divide el texto en líneas
	        String[] lines = input.split("\n");
	
	        for (String line : lines) {
	            // Eliminar espacios innecesarios alrededor de la línea
	            line = line.trim();
	
	            // Comprobar el formato clave=valor
	            if (line.contains("=")) {
	                String[] parts = line.split("=", 2); // Dividir en máximo 2 partes
	                if (parts.length == 2) {
	                    String key = parts[0].trim(); // Eliminar espacios de la clave
	                    String value = parts[1].trim(); // Eliminar espacios del valor
	                    usuarisMap.put(key, value);
	                }
	            }
	        }
        }
        return usuarisMap;
    }
}