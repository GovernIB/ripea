/**
 * 
 */
package es.caib.ripea.war.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.WebUtils;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientEstatEnumDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.command.ExpedientFiltreCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;

/**
 * Controlador per al llistat d'expedients dels usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/expedientDetail")
public class ExpedientDetailController extends BaseUserController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "ExpedientUserController.session.filtre";
	private static final String SESSION_ATTRIBUTE_SELECCIO = "ExpedientUserController.session.seleccio";
	private static final String SESSION_ATTRIBUTE_METAEXP_ID = "ExpedientUserController.session.metaExpedient.id";
	private static final String COOKIE_MEUS_EXPEDIENTS = "meus_expedients";

	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private MetaExpedientService metaExpedientService;



	@RequestMapping(method = RequestMethod.GET)
	public String get(
			@CookieValue(value = COOKIE_MEUS_EXPEDIENTS, defaultValue = "false") boolean meusExpedients,
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		List<MetaExpedientDto> metaExpedientsPermisLectura = metaExpedientService.findActiusAmbEntitatPerLectura(
				entitatActual.getId(), 
				null);
		model.addAttribute(
				"metaExpedientsPermisLectura",
				metaExpedientsPermisLectura);
		List<MetaExpedientDto> metaExpedientsPermisCreacio = metaExpedientService.findActiusAmbEntitatPerCreacio(entitatActual.getId());
		model.addAttribute(
				"metaExpedientsPermisCreacio",
				metaExpedientsPermisCreacio);
		model.addAttribute(
				getFiltreCommand(request));
		model.addAttribute(
				"seleccio",
				RequestSessionHelper.obtenirObjecteSessio(
						request,
						SESSION_ATTRIBUTE_SELECCIO));
		model.addAttribute(
				"expedientEstatEnumOptions",
				EnumHelper.getOptionsForEnum(
						ExpedientEstatEnumDto.class,
						"expedient.estat.enum."));
		model.addAttribute("nomCookieMeusExpedients", COOKIE_MEUS_EXPEDIENTS);
		model.addAttribute("meusExpedients", meusExpedients);
		if (metaExpedientsPermisLectura == null || metaExpedientsPermisLectura.size() <= 0) {
			MissatgesHelper.warning(
					request, 
					getMessage(
							request, 
							"expedient.controller.sense.permis.lectura"));
		}
		return "expedientDetailList";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid ExpedientFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model,
			@RequestParam(value = "accio", required = false) String accio) {
		getEntitatActualComprovantPermisos(request);
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE);
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO);
		} else {
			if (!bindingResult.hasErrors()) {
				RequestSessionHelper.actualitzarObjecteSessio(
						request,
						SESSION_ATTRIBUTE_FILTRE,
						filtreCommand);
				Long metaExpedientId = (Long)RequestSessionHelper.obtenirObjecteSessio(
						request,
						SESSION_ATTRIBUTE_METAEXP_ID);
				if (metaExpedientId == null || !metaExpedientId.equals(filtreCommand.getMetaExpedientId())) {
					RequestSessionHelper.esborrarObjecteSessio(
							request,
							SESSION_ATTRIBUTE_SELECCIO);
					RequestSessionHelper.actualitzarObjecteSessio(
							request,
							SESSION_ATTRIBUTE_METAEXP_ID,
							filtreCommand.getMetaExpedientId());
				}
			}
		}
		return "redirect:expedientDetail";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ExpedientFiltreCommand filtreCommand = getFiltreCommand(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				expedientService.findAmbFiltreUser(
						entitatActual.getId(),
						ExpedientFiltreCommand.asDto(filtreCommand),
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id",
				SESSION_ATTRIBUTE_SELECCIO);
	}

	@RequestMapping(value = "/select", method = RequestMethod.GET)
	@ResponseBody
	public int select(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) Long[] ids) {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO,
					seleccio);
		}
		if (ids != null) {
			for (Long id: ids) {
				seleccio.add(id);
			}
		} else {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			ExpedientFiltreCommand filtreCommand = getFiltreCommand(request);
			seleccio.addAll(
					expedientService.findIdsAmbFiltre(
							entitatActual.getId(),
							ExpedientFiltreCommand.asDto(filtreCommand)));
		}
		return seleccio.size();
	}
	@RequestMapping(value = "/deselect", method = RequestMethod.GET)
	@ResponseBody
	public int deselect(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) Long[] ids) {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO,
					seleccio);
		}
		if (ids != null) {
			for (Long id: ids) {
				seleccio.remove(id);
			}
		} else {
			seleccio.clear();
		}
		return seleccio.size();
	}

	@RequestMapping(value = "/export/{format}", method = RequestMethod.GET)
	public String export(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable String format) throws IOException {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		ExpedientFiltreCommand command = getFiltreCommand(request);
		if (seleccio == null || seleccio.isEmpty() || command == null) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"expedient.controller.exportacio.seleccio.buida"));
			return "redirect:../../expedient";
		} else {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			FitxerDto fitxer = expedientService.exportacio(
					entitatActual.getId(),
					seleccio,
					format);
			writeFileToResponse(
					fitxer.getNom(),
					fitxer.getContingut(),
					response);
			return null;
		}
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}



	private ExpedientFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		ExpedientFiltreCommand filtreCommand = (ExpedientFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new ExpedientFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		Cookie cookie = WebUtils.getCookie(request, COOKIE_MEUS_EXPEDIENTS);
		filtreCommand.setMeusExpedients(cookie != null && "true".equals(cookie.getValue()));
		return filtreCommand;
	}

}
