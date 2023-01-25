/**
 * 
 */
package es.caib.ripea.war.controller;

import es.caib.ripea.core.api.dto.ContingutTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientSelectorDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.exception.ArxiuJaGuardatException;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.command.ContingutMassiuFiltreCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Controlador per accio massiu de custodiar documents de portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/massiu/custodiar")
public class DocumentMassiuCustodiarController extends BaseUserOAdminOOrganController {
	
	
	private static final String SESSION_ATTRIBUTE_FILTRE = "DocumentMassiuCustodiarController.session.filtre";

	private static final String SESSION_ATTRIBUTE_SELECCIO_USER = "DocumentMassiuCustodiarController.session.seleccio.user";
	private static final String SESSION_ATTRIBUTE_SELECCIO_ADMIN = "DocumentMassiuCustodiarController.session.seleccio.admin";
	private static final String SESSION_ATTRIBUTE_SELECCIO_ORGAN = "DocumentMassiuCustodiarController.session.seleccio.organ";

	@Autowired
	private MetaExpedientService metaExpedientService;
	@Autowired
	private MetaDocumentService metaDocumentService;
	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private DocumentService documentService;


	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutMassiuFiltreCommand filtreCommand = getFiltreCommand(request);
		filtreCommand.setTipusElement(ContingutTipusEnumDto.DOCUMENT);
		filtreCommand.setBloquejarTipusElement(true);
		filtreCommand.setBloquejarMetaDada(true);
		filtreCommand.setBloquejarMetaExpedient(false);
		model.addAttribute("portafirmes", true);
		model.addAttribute("seleccio", RequestSessionHelper.obtenirObjecteSessio(request, getSessionAttributeSelecio(request)));
		model.addAttribute(filtreCommand);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		
		boolean checkPerMassiuAdmin = false;
		if (rolActual.equals("IPA_ADMIN") || rolActual.equals("IPA_ORGAN_ADMIN")) {
			checkPerMassiuAdmin = true;
		}
		model.addAttribute("metaExpedients", metaExpedientService.findActiusAmbEntitatPerModificacio(entitatActual.getId(), rolActual));
		List<ExpedientSelectorDto> expedients = new ArrayList<ExpedientSelectorDto>();
		if (filtreCommand.getMetaExpedientId() != null) {
			expedients = expedientService.findPerUserAndProcediment(entitatActual.getId(), filtreCommand.getMetaExpedientId(), rolActual);
		}
		model.addAttribute("expedients", expedients);
		return "documentMassiuCustodiarList";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(HttpServletRequest request, @Valid ContingutMassiuFiltreCommand filtreCommand, BindingResult bindingResult, Model model) {

		if (!bindingResult.hasErrors()) {
			RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
		return "redirect:/massiu/custodiar";
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
					 documentService.findDocumentsPerCustodiarMassiu(
								entitatActual.getId(), 
								rolActual,
								ContingutMassiuFiltreCommand.asDto(contingutMassiuFiltreCommand), 
								DatatablesHelper.getPaginacioDtoFromRequest(request)),
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
					documentService.findDocumentsIdsPerCustodiarMassiu(
							entitatActual.getId(),
							ContingutMassiuFiltreCommand.asDto(filtreCommand), rolActual));
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
	
	
	
	@RequestMapping(value = "/custodiar", method = RequestMethod.GET)
	public String custodiarReintentar(
			HttpServletRequest request) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = ((Set<Long>) RequestSessionHelper.obtenirObjecteSessio(
				request,
				getSessionAttributeSelecio(request)));
		
		if (seleccio == null || seleccio.isEmpty()) {
			return getModalControllerReturnValueError(
					request,
					"redirect:/massiu/custodiar",
					"accio.massiva.seleccio.buida",
					null);
		}
		
		int errors = 0;
		int correctes = 0;
		
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		
		for (Long id : seleccio) {
			Exception exception = null;
			try {
				DocumentDto doc = documentService.findById(entitatActual.getId(), id);
				if (doc.getGesDocAdjuntId() != null) {
					exception = documentService.guardarDocumentArxiu(id);
				} else {
					exception = documentService.portafirmesReintentar(
							entitatActual.getId(),
							id, 
							rolActual);
				}

			} catch (Exception ex) {
				exception = ex;
			}
			if (exception instanceof ArxiuJaGuardatException) {
				exception = null;
			}
			if (exception != null ) {
				logger.error("Error al custodiar document pendent", exception);
				errors++;
			} else {
				correctes++;
			}
		
		}
		
		if (correctes > 0){
			MissatgesHelper.success(request, getMessage(request, "expedient.controller.custodiar.massiu.correctes", new Object[]{correctes}));
		} 
		if (errors > 0) {
			MissatgesHelper.error(request, getMessage(request, "expedient.controller.custodiar.massiu.errors", new Object[]{errors}), null);
		} 
		
		seleccio.clear();
		RequestSessionHelper.actualitzarObjecteSessio(
				request,
				getSessionAttributeSelecio(request),
				seleccio);
		
		return "redirect:../custodiar";
	}
	
	
	@RequestMapping(value = "/metaDocuments/{metaExpedientId}", method = RequestMethod.GET)
	@ResponseBody
	public List<MetaDocumentDto> findMetaDocuments(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		List<MetaDocumentDto> metaDocuments = new ArrayList<MetaDocumentDto>();
		metaDocuments = metaDocumentService.findByMetaExpedient(entitatActual.getId(), metaExpedientId);
		
		return metaDocuments;
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

	private static final Logger logger = LoggerFactory.getLogger(DocumentMassiuCustodiarController.class);
	
}
