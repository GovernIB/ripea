/**
 * 
 */
package es.caib.ripea.war.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.IdNomDto;
import es.caib.ripea.core.api.dto.IdiomaEnumDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.EntitatService;
import es.caib.ripea.core.api.service.OrganGestorService;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.war.command.UsuariCommand;
import es.caib.ripea.war.helper.EntitatHelper;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.SessioHelper;

/**
 * Controlador per al manteniment de regles.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/usuari")
public class UsuariController  extends BaseAdminController {

	
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private OrganGestorService organGestorService;
	@Autowired
	private EntitatService entitatService;

	@RequestMapping(value = "/configuracio", method = RequestMethod.GET)
	public String getConfiguracio(
			HttpServletRequest request,
			Model model) {
		UsuariDto usuari = aplicacioService.getUsuariActual();
		model.addAttribute(UsuariCommand.asCommand(usuari));
		model.addAttribute(
				"idiomaEnumOptions",
				EnumHelper.getOptionsForEnum(
						IdiomaEnumDto.class,
						"usuari.form.camp.idioma.enum."));
		
		List<IdNomDto> numElementsPagina = new ArrayList<>();
		numElementsPagina.add(new IdNomDto(10l, "10"));
		numElementsPagina.add(new IdNomDto(20l, "20"));
		numElementsPagina.add(new IdNomDto(50l, "50"));
		numElementsPagina.add(new IdNomDto(100l, "100"));
		numElementsPagina.add(new IdNomDto(250l, "250"));

		model.addAttribute(
				"numElementsPagina",
				numElementsPagina);

		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		

		model.addAttribute("procediments",
				metaExpedientService.findActius(
						entitatActual.getId(), 
						null, 
						rolActual, 
						false, 
						null));
		
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
			
			model.addAttribute(
					"idiomaEnumOptions",
					EnumHelper.getOptionsForEnum(
							IdiomaEnumDto.class,
							"usuari.form.camp.idioma.enum."));
			
			return "usuariForm";
		}
		UsuariDto usuari = aplicacioService.updateUsuariActual(UsuariCommand.asDto(command));
		SessioHelper.setUsuariActual(request, usuari);
		
		return getModalControllerReturnValueSuccess(
					request,
					"redirect:/",
					"usuari.controller.modificat.ok");
	}

	
	/**
	 * Només per Jboss
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		UsuariDto usuari = aplicacioService.getUsuariActual();
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		organGestorService.evictOrganismesEntitatAmbPermis(entitat.getId(), usuari.getCodi());
		aplicacioService.evictRolsDisponiblesEnAcls();
		entitatService.evictEntitatsAccessiblesUsuari();
		aplicacioService.evictRolsPerUsuari(usuari.getCodi());
		
		// Es itera sobre totes les cookies
		for(Cookie c : request.getCookies()) {
			// Es sobre escriu el valor de cada cookie a NULL
			Cookie ck = new Cookie(c.getName(), null);
			ck.setPath(request.getContextPath());
			response.addCookie(ck);
		}
		return "redirect:/";
	}
}
