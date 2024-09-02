/**
 * 
 */
package es.caib.ripea.war.controller;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ResultEnumDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.ExecucioMassivaService;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.command.ContingutMassiuFiltreCommand;
import es.caib.ripea.war.command.ExpedientMassiuCanviPrioritatCommand;
import es.caib.ripea.war.helper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Controlador per canvi estat massiu del expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Controller
@RequestMapping("/massiu/canviPrioritats")
public class ExpedientMassiuCanviPrioritatController extends BaseUserOAdminOOrganController {
	
	@Autowired private MetaExpedientService metaExpedientService;
	@Autowired private ExpedientService expedientService;
	@Autowired private ExecucioMassivaService execucioMassivaService;
	@Autowired private AplicacioService aplicacioService;
	private static final String SESSION_ATTRIBUTE_FILTRE = "ExpedientMassiuCanviPrioritatController.session.filtre";
	private static final String SESSION_ATTRIBUTE_SELECCIO = "ExpedientMassiuCanviPrioritatController.session.seleccio";

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		ContingutMassiuFiltreCommand filtreCommand = getFiltreCommand(request);

		model.addAttribute(filtreCommand);
		model.addAttribute("seleccio", RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_SELECCIO));
		model.addAttribute("metaExpedients", metaExpedientService.findActiusAmbEntitatPerModificacio(entitatActual.getId(), rolActual));

		return "expedientMassiuCanviPrioritatList";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid ContingutMassiuFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model,
			@RequestParam(value = "accio", required = false) String accio) {

		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE);
		} else {
			if (!bindingResult.hasErrors()) {
				RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
			}
		}
		return "redirect:/massiu/canviPrioritats";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesHelper.DatatablesResponse datatable(HttpServletRequest request) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		ContingutMassiuFiltreCommand contingutMassiuFiltreCommand = getFiltreCommand(request);

		try {
			return DatatablesHelper.getDatatableResponse(
					request,
					 expedientService.findExpedientsPerTancamentMassiu(
								entitatActual.getId(),
								ContingutMassiuFiltreCommand.asDto(contingutMassiuFiltreCommand),
								DatatablesHelper.getPaginacioDtoFromRequest(request),
								rolActual),
					 "id",
					SESSION_ATTRIBUTE_SELECCIO);
		} catch (Exception e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/canviar", method = RequestMethod.GET)
	public String canviarPrioritatsGet(HttpServletRequest request, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		Set<Long> seleccio = getSessionAttributeSelecio(request);

		if (seleccio.isEmpty()) {
			return getModalControllerReturnValueError(
					request,
					"redirect:/expedient",
					"accio.massiva.seleccio.buida",
					null);
		} else if (seleccio.size() > 1000) {
			return getModalControllerReturnValueError(
					request,
					"redirect:/expedient",
					"accio.massiva.seleccio.max.error",
					null);
		}

		ExpedientMassiuCanviPrioritatCommand command = new ExpedientMassiuCanviPrioritatCommand();
		model.addAttribute(command);

		return "expedientMassiuCanviPrioritatForm";
	}

	@RequestMapping(value = "/canviar", method = RequestMethod.POST)
	public String canviarsPrioritatsPost(
			HttpServletRequest request,
			ExpedientMassiuCanviPrioritatCommand command,
			BindingResult bindingResult,
			Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			return "expedientEstatsForm";
		}
		Set<Long> seleccio = getSessionAttributeSelecio(request);

		try {
			expedientService.changeExpedientsPrioritat(
					entitatActual.getId(),
					seleccio,
					command.getPrioritat());
			MissatgesHelper.success(request, getMessage(request, "expedient.controller.prioritats.modificades.ok", new Object[] {seleccio.size()}));

		} catch (Exception e) {
			log.error("Error al canviar la prioritat dels expedients seleccionats", e);
			MissatgesHelper.error(request, getMessage(request, "expedient.controller.prioritats.modificades.error", new Object[] {seleccio.size()}));
		}

		return modalUrlTancar();
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/select", method = RequestMethod.GET)
	@ResponseBody
	public int select(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) Long[] ids) {

		Set<Long> seleccio = getSessionAttributeSelecio(request);

		if (ids != null) {
            Collections.addAll(seleccio, ids);
		} else {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			ContingutMassiuFiltreCommand filtreCommand = getFiltreCommand(request);
			String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
			seleccio.addAll(
					expedientService.findIdsExpedientsPerTancamentMassiu(
							entitatActual.getId(),
							ContingutMassiuFiltreCommand.asDto(filtreCommand), rolActual));
		}
		RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_SELECCIO, seleccio);
		return seleccio.size();
	}

	@RequestMapping(value = "/deselect", method = RequestMethod.GET)
	@ResponseBody
	public int deselect(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) Long[] ids) {

		Set<Long> seleccio = getSessionAttributeSelecio(request);

		if (ids != null) {
			for (Long id: ids) {
				seleccio.remove(id);
			}
		} else {
			seleccio.clear();
		}
		RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_SELECCIO, seleccio);
		return seleccio.size();
	}

	private ContingutMassiuFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		ContingutMassiuFiltreCommand filtreCommand = (ContingutMassiuFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new ContingutMassiuFiltreCommand();
			filtreCommand.setMetaExpedientId(aplicacioService.getProcedimentPerDefecte(EntitatHelper.getEntitatActual(request).getId(), RolHelper.getRolActual(request)));
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}

	@SuppressWarnings("unchecked")
	private Set<Long> getSessionAttributeSelecio(HttpServletRequest request) {
		Object listExpSel = request.getSession().getAttribute(SESSION_ATTRIBUTE_SELECCIO);
		if (listExpSel!=null) {
            return (Set<Long>) listExpSel;
		} else {
			return new HashSet<Long>();
		}
	}
}