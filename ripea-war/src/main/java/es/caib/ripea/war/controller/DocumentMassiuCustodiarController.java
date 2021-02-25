/**
 * 
 */
package es.caib.ripea.war.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import es.caib.ripea.core.api.dto.ContingutTipusEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientSelectorDto;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.command.ContingutMassiuFiltreCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.RequestSessionHelper;

/**
 * Controlador per accio massiu de custodiar documents de portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/massiu/custodiar")
public class DocumentMassiuCustodiarController extends BaseUserOAdminController {
	
	
	private static final String SESSION_ATTRIBUTE_FILTRE = "DocumentMassiuCustodiarController.session.filtre";
	private static final String SESSION_ATTRIBUTE_SELECCIO = "DocumentMassiuCustodiarController.session.seleccio";


	@Autowired
	private MetaExpedientService metaExpedientService;
	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private DocumentService documentService;


	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutMassiuFiltreCommand filtreCommand = getFiltreCommand(request);
		filtreCommand.setTipusElement(ContingutTipusEnumDto.DOCUMENT);
		filtreCommand.setBloquejarTipusElement(true);
		filtreCommand.setBloquejarMetaDada(true);
		filtreCommand.setBloquejarMetaExpedient(false);
		model.addAttribute("portafirmes", true);
		model.addAttribute(
				"seleccio",
				RequestSessionHelper.obtenirObjecteSessio(
						request,
						SESSION_ATTRIBUTE_SELECCIO));

		model.addAttribute(
				filtreCommand);
		model.addAttribute(
				"metaExpedients",
				metaExpedientService.findActiusAmbEntitatPerModificacio(entitatActual.getId()));
		List<ExpedientSelectorDto> expedients = new ArrayList<ExpedientSelectorDto>();
		if (filtreCommand.getMetaExpedientId() != null)
			expedients = expedientService.findPerUserAndTipus(entitatActual.getId(), filtreCommand.getMetaExpedientId());
		model.addAttribute(
				"expedients",
				expedients);
		return "documentMassiuCustodiarList";
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
		
		return "redirect:/massiu/custodiar";
	}
	

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutMassiuFiltreCommand contingutMassiuFiltreCommand = getFiltreCommand(request);

		
		try {
			return DatatablesHelper.getDatatableResponse(
					request,
					 documentService.findDocumentsPerCustodiarMassiu(
								entitatActual.getId(), 
								ContingutMassiuFiltreCommand.asDto(contingutMassiuFiltreCommand),
								DatatablesHelper.getPaginacioDtoFromRequest(request)),
					 "id",
					 SESSION_ATTRIBUTE_SELECCIO);
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
			ContingutMassiuFiltreCommand filtreCommand = getFiltreCommand(request);
			seleccio.addAll(
					documentService.findDocumentsIdsPerCustodiarMassiu(
							entitatActual.getId(),
							ContingutMassiuFiltreCommand.asDto(filtreCommand)));
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
	
	
	
	

	
	@RequestMapping(value = "/custodiar", method = RequestMethod.GET)
	public String portafirmesReintentar(
			HttpServletRequest request) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = ((Set<Long>) RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO));
		
		if (seleccio == null || seleccio.isEmpty()) {
			return getModalControllerReturnValueError(
					request,
					"redirect:/massiu/custodiar",
					"accio.massiva.seleccio.buida");
		}
		
		Exception exception = documentService.portafirmesReintentar(
				entitatActual.getId(),
				seleccio);
		
		seleccio.clear();
		RequestSessionHelper.actualitzarObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO,
				seleccio);
		
		if (exception == null) {
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/massiu/custodiar",
					"expedient.controller.custodiar.massiu.ok");
		} else {
			return getModalControllerReturnValueError(
					request,
					"redirect:/massiu/custodiar",
					"expedient.controller.custodiar.massiu.error",
					new Object[] {exception.getMessage()});
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
