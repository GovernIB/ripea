/**
 * 
 */
package es.caib.ripea.war.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientEstatDto;
import es.caib.ripea.core.api.service.ExpedientEstatService;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.command.ContingutMassiuFiltreCommand;
import es.caib.ripea.war.command.ExpedientMassiuCanviEstatCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.RequestSessionHelper;

/**
 * Controlador per canvi estat massiu del expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/massiu/canviEstat")
public class ExpedientMassiuCanviEstatController extends BaseUserOAdminOOrganController {
	
	private static final String SESSION_ATTRIBUTE_FILTRE = "ExpedientCanviEstatMassiuController.session.filtre";
	private static final String SESSION_ATTRIBUTE_SELECCIO_USER = "ExpedientCanviEstatMassiuController.session.seleccio.user";
	private static final String SESSION_ATTRIBUTE_SELECCIO_ADMIN = "ExpedientCanviEstatMassiuController.session.seleccio.admin";
	private static final String SESSION_ATTRIBUTE_SELECCIO_ORGAN = "ExpedientCanviEstatMassiuController.session.seleccio.organ";


	@Autowired
	private MetaExpedientService metaExpedientService;
	@Autowired
	private ExpedientService expedientService;

	
	@Autowired
	private ExpedientEstatService expedientEstatService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutMassiuFiltreCommand filtreCommand = getFiltreCommand(request);
		
		model.addAttribute(
				filtreCommand);
		
		model.addAttribute(
				"seleccio",
				RequestSessionHelper.obtenirObjecteSessio(
						request,
						getSessionAttributeSelecio(request)));

		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		
		/*boolean checkPerMassiuAdmin = false;
		if (rolActual.equals("IPA_ADMIN") || rolActual.equals("IPA_ORGAN_ADMIN")) {
			checkPerMassiuAdmin = true;
		}*/
		
		model.addAttribute(
				"metaExpedients",
				metaExpedientService.findActiusAmbEntitatPerModificacio(entitatActual.getId(), rolActual));

		return "expedientMassiuCanviEstatList";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid ContingutMassiuFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model) {
		if (!bindingResult.hasErrors()) {
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		
		return "redirect:/massiu/canviEstat";
	}
	

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutMassiuFiltreCommand contingutMassiuFiltreCommand = getFiltreCommand(request);
		
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		
		try {
			return DatatablesHelper.getDatatableResponse(
					request,
					 expedientEstatService.findExpedientsPerCanviEstatMassiu(
								entitatActual.getId(), 
								ContingutMassiuFiltreCommand.asDto(contingutMassiuFiltreCommand),
								DatatablesHelper.getPaginacioDtoFromRequest(request), rolActual),
					 "id",
					 getSessionAttributeSelecio(request));
		} catch (Exception e) {
			throw e;
		}
		
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/select", method = RequestMethod.GET)
	@ResponseBody
	public int select(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) Long[] ids) {
		
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				getSessionAttributeSelecio(request));
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					getSessionAttributeSelecio(request),
					seleccio);
		}
		if (ids != null) {
			for (Long id: ids) {
				seleccio.add(id);
			}
		} else {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			ContingutMassiuFiltreCommand filtreCommand = getFiltreCommand(request);
			String rolActual = (String)request.getSession().getAttribute(
					SESSION_ATTRIBUTE_ROL_ACTUAL);
			
			seleccio.addAll(
					expedientEstatService.findIdsExpedientsPerCanviEstatMassiu(
							entitatActual.getId(),
							ContingutMassiuFiltreCommand.asDto(filtreCommand), 
							rolActual));
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
				getSessionAttributeSelecio(request));
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					getSessionAttributeSelecio(request),
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
	
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/canviarEstat", method = RequestMethod.GET)
	public String canviarEstatGet(
			HttpServletRequest request,
			Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				getSessionAttributeSelecio(request));
		
		if (seleccio == null || seleccio.isEmpty()) {
			model.addAttribute("portafirmes", false);
			return getModalControllerReturnValueError(
					request,
					"redirect:/massiu/definitiu",
					"accio.massiva.seleccio.buida");
		}
		
		ExpedientDto oneOfSelectedExpedients = expedientService.findById(entitatActual.getId(), seleccio.iterator().next(), null);

		List<ExpedientEstatDto> expedientEstats = expedientEstatService.findExpedientEstatsByMetaExpedient(
				entitatActual.getId(),
				oneOfSelectedExpedients.getMetaExpedient().getId());
		ExpedientEstatDto expedientEstatObert = new ExpedientEstatDto();
		expedientEstatObert.setNom("OBERT");
		expedientEstats.add(0, expedientEstatObert);
		model.addAttribute(
				"expedientEstats",
				expedientEstats);
		
		
		ExpedientMassiuCanviEstatCommand command = new ExpedientMassiuCanviEstatCommand();
		model.addAttribute(command);
	
		
		return "expedientMassiuCanviEstatForm";
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/canviarEstat", method = RequestMethod.POST)
	public String canviarEstatPost(
			HttpServletRequest request,
			ExpedientMassiuCanviEstatCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {

			return "expedientEstatsForm";
		}
		
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				getSessionAttributeSelecio(request));
		
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		
		boolean checkPerMassiuAdmin = false;
		if (rolActual.equals("IPA_ADMIN") || rolActual.equals("IPA_ORGAN_ADMIN")) {
			checkPerMassiuAdmin = true;
		} 
		
		for (Long expedientId : seleccio) {
			expedientEstatService.changeEstatOfExpedient(
					entitatActual.getId(),
					expedientId,
					command.getExpedientEstatId(), 
					checkPerMassiuAdmin
					);
		}
		
		
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../expedient",
				"expedient.controller.estatsModificats.ok");
	}
	
	
	
	
	
	
	private String getSessionAttributeSelecio(HttpServletRequest request) {
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		String sessionAttribute;
		if (rolActual.equals("tothom")) {
			sessionAttribute = SESSION_ATTRIBUTE_SELECCIO_USER;
		} else if (rolActual.equals("IPA_ADMIN")) {
			sessionAttribute = SESSION_ATTRIBUTE_SELECCIO_ADMIN;
		} else if (rolActual.equals("IPA_ORGAN_ADMIN")){
			sessionAttribute = SESSION_ATTRIBUTE_SELECCIO_ORGAN;
		} else {
			throw new RuntimeException("No rol permitido");
		}
		return sessionAttribute;
	}
	
	

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}


	private ContingutMassiuFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		ContingutMassiuFiltreCommand filtreCommand = (ContingutMassiuFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new ContingutMassiuFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}

}
