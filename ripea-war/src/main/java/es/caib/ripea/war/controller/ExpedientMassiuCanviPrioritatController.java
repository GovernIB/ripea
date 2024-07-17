/**
 * 
 */
package es.caib.ripea.war.controller;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.ExecucioMassivaService;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.command.ExpedientMassiuCanviPrioritatCommand;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

/**
 * Controlador per canvi estat massiu del expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Controller
@RequestMapping("/expedient/canviPrioritats")
public class ExpedientMassiuCanviPrioritatController extends BaseUserOAdminOOrganController {
	
	@Autowired
	private MetaExpedientService metaExpedientService;
	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private ExecucioMassivaService execucioMassivaService;

	@Autowired
	private AplicacioService aplicacioService;


	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.GET)
	public String canviarPrioritatsGet(HttpServletRequest request, Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
//		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(request, getSessionAttributeSelecio(request));
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(request, ExpedientController.SESSION_ATTRIBUTE_SELECCIO);

		if (seleccio == null || seleccio.isEmpty()) {
			return getModalControllerReturnValueError(
					request,
					"redirect:/expedient",
					"accio.massiva.seleccio.buida",
					null);
		} else if (seleccio != null && seleccio.size() > 1000) {
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
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.POST)
	public String canviarsPrioritatsPost(
			HttpServletRequest request,
			ExpedientMassiuCanviPrioritatCommand command,
			BindingResult bindingResult,
			Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			return "expedientEstatsForm";
		}
		
//		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(request, getSessionAttributeSelecio(request));
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(request, ExpedientController.SESSION_ATTRIBUTE_SELECCIO);

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
	
	
	
//	private String getSessionAttributeSelecio(HttpServletRequest request) {
//		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
//		String sessionAttribute;
//		if (rolActual.equals("tothom")) {
//			sessionAttribute = SESSION_ATTRIBUTE_SELECCIO_USER;
//		} else if (rolActual.equals("IPA_ADMIN")) {
//			sessionAttribute = SESSION_ATTRIBUTE_SELECCIO_ADMIN;
//		} else if (rolActual.equals("IPA_ORGAN_ADMIN")){
//			sessionAttribute = SESSION_ATTRIBUTE_SELECCIO_ORGAN;
//		} else {
//			throw new RuntimeException("No rol permitido");
//		}
//		return sessionAttribute;
//	}
	
	

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}


//	private ContingutMassiuFiltreCommand getFiltreCommand(
//			HttpServletRequest request) {
//		ContingutMassiuFiltreCommand filtreCommand = (ContingutMassiuFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
//				request,
//				SESSION_ATTRIBUTE_FILTRE);
//		if (filtreCommand == null) {
//			filtreCommand = new ContingutMassiuFiltreCommand();
//			filtreCommand.setMetaExpedientId(aplicacioService.getProcedimentPerDefecte(EntitatHelper.getEntitatActual(request).getId(), RolHelper.getRolActual(request)));
//			RequestSessionHelper.actualitzarObjecteSessio(
//					request,
//					SESSION_ATTRIBUTE_FILTRE,
//					filtreCommand);
//		}
//		return filtreCommand;
//	}

}