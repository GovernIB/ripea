package es.caib.ripea.back.controller;

import java.util.ArrayList;
import java.util.List;
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

import es.caib.ripea.back.command.UsuariCodiCommand;
import es.caib.ripea.back.command.UsuariCommand;
import es.caib.ripea.back.helper.EntitatHelper;
import es.caib.ripea.back.helper.EnumHelper;
import es.caib.ripea.back.helper.RequestSessionHelper;
import es.caib.ripea.back.helper.RolHelper;
import es.caib.ripea.back.helper.SessioHelper;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.IdNomDto;
import es.caib.ripea.service.intf.dto.IdiomaEnumDto;
import es.caib.ripea.service.intf.dto.MetaExpedientDto;
import es.caib.ripea.service.intf.dto.UsuariDto;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.EntitatService;
import es.caib.ripea.service.intf.service.OrganGestorService;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.Builder;
import lombok.Data;

@Controller
@RequestMapping("/usuari")
public class UsuariController  extends BaseAdminController {
	
	@Autowired private AplicacioService aplicacioService;
	@Autowired private OrganGestorService organGestorService;
	@Autowired private EntitatService entitatService;

	@RequestMapping(value = "/configuracio", method = RequestMethod.GET)
	public String getConfiguracio(HttpServletRequest request, Model model) {
		UsuariDto usuari = aplicacioService.getUsuariActual();
		UsuariCommand usuariCommand = UsuariCommand.asCommand(usuari);
		List<String> filteredRoles = new ArrayList<>();
        for (String role : usuari.getRols()) {
            if (role.startsWith("IPA") || role.equalsIgnoreCase("tothom")) {
                filteredRoles.add(role);
            }
        }
        usuariCommand.setRols(filteredRoles.toArray(new String[0]));
		model.addAttribute(usuariCommand);
		emplenaModel(request, model, usuariCommand);
        model.addAttribute("editEmailsCanviEstatRevisio", RolHelper.hasRolAdministrador(request) || RolHelper.hasRolRevisor(request));
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
	
	/**
	 * CANVI DE CODIS D'USUARI
	 */
	@RequestMapping(value = "/username", method = RequestMethod.GET)
 	public String canviCodiGet(
 			HttpServletRequest request,
 			Model model) {
		UsuariCodiCommand usuariCodiCommand = new UsuariCodiCommand();
		usuariCodiCommand.setUsuariActualCodi(aplicacioService.getUsuariActual().getCodi());
 		model.addAttribute("usuariCodiCommand", usuariCodiCommand);
 		return "usuariCodiForm";
 	}
	
	@RequestMapping(value = "/username/{codiAntic}/validateTo/{codiNou}", method = RequestMethod.POST, produces = "application/json" )
	@ResponseBody
	public UsuariChangeValidation validarCodiPost(
 			HttpServletRequest request,
 			HttpServletResponse response,
 			@PathVariable("codiAntic") String codiAntic,
 			@PathVariable("codiNou") String codiNou) {
		UsuariDto usuariAntic = null;
		UsuariDto usuariNou = null;
		try {
			usuariAntic = aplicacioService.findUsuariAmbCodi(codiAntic);
		} catch (Exception ex) {}
		try {
			usuariNou = aplicacioService.findUsuariAmbCodi(codiNou);	
		} catch (Exception ex) {}
		
		return UsuariChangeValidation.builder()
				.usuariAnticExists(usuariAntic != null)
				.usuariNouExists(usuariNou != null)
				.build();
	}
	
	@RequestMapping(value = "/username/{codiAntic}/changeTo/{codiNou}", method = RequestMethod.POST, produces = "application/json" )
	@ResponseBody
	public UsuariChangeResponse canviCodiPost(
 			HttpServletRequest request,
 			HttpServletResponse response,
 			@PathVariable("codiAntic") String codiAntic,
 			@PathVariable("codiNou") String codiNou) {
		Long t0 = System.currentTimeMillis();
		try {
			Long registresModificats = aplicacioService.updateUsuariCodi(codiAntic, codiNou);
			return UsuariChangeResponse.builder()
					.estat(ResultatEstatEnum.OK)
					.registresModificats(registresModificats)
					.duracio(System.currentTimeMillis() - t0)
					.build();
		} catch (Exception e) {
			return UsuariChangeResponse.builder()
					.estat(ResultatEstatEnum.ERROR)
					.errorMessage(getMessage(request, "usuari.controller.codi.modificat.error", null) + ": " + e.getMessage())
					.duracio(System.currentTimeMillis() - t0)
					.build();
		}
	}
	
	@Data
	@Builder
	public static class UsuariChangeValidation {
		private boolean usuariAnticExists;
		private boolean usuariNouExists;
	}

	@Data
	@Builder
	public static class UsuariChangeResponse {
		private ResultatEstatEnum estat;
		private String errorMessage;
		private Long registresModificats;
		private Long duracio;
	}

	public enum ResultatEstatEnum { OK, ERROR }
}
