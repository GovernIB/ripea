package es.caib.ripea.war.controller;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.service.MetaDadaService;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.war.command.MetaDadaCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.EntitatHelper;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.EnumHelper.HtmlOption;
import es.caib.ripea.war.helper.ExceptionHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RolHelper;

/**
 * Controlador per al manteniment de les meta-dades dels meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
/*
 *  TODO: 	Aquesta classe i la classe MetaExpedientMetaDadaController tenen practicament el mateix codi.
 *  		S'haurien d'unificar en un mateix codi.
 */
@Controller
@RequestMapping("/metaDocument")
public class MetaDocumentMetaDadaController extends BaseAdminController {

	@Autowired private MetaDadaService metaDadaService;
	@Autowired private MetaDocumentService metaDocumentService;
	@Autowired private DocumentService documentService;
	@Autowired private AplicacioService aplicacioService;

	@RequestMapping(value = "/{metaDocumentId}/metaDada", method = RequestMethod.GET)
	public String get(HttpServletRequest request, @PathVariable Long metaDocumentId, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		model.addAttribute("metaDocument", metaDocumentService.findById(entitatActual.getId(), null, metaDocumentId));
		String rolActual = RolHelper.getRolActual(request);
		model.addAttribute("esRevisor", rolActual.equals("IPA_REVISIO"));
		MetaDocumentDto metaDocument = metaDocumentService.findById(metaDocumentId);
		if (metaDocument.getMetaExpedientId() != null) {
			MetaExpedientDto metaExpedient = comprovarAccesMetaExpedient(request, metaDocument.getMetaExpedientId());
			if (metaExpedient != null && metaExpedientService.isRevisioActiva()) { // es tracta d'una modificació
				if (RolHelper.isRolActualAdministradorOrgan(request)  && metaExpedient.getRevisioEstat() == MetaExpedientRevisioEstatEnumDto.REVISAT){
					MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.adminOrgan.bloquejada.alerta"));
					model.addAttribute("bloquejarCamps", true);
				} else if (RolHelper.isRolActualRevisor(request)){
					model.addAttribute("bloquejarCamps", true);
					model.addAttribute("consultar", true);
					model.addAttribute("isRolActualRevisor", true);
				}
			}
		}

		return "metaDadaList";
	}

	@RequestMapping(value = "/{metaDocumentId}/metaDada/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request, @PathVariable Long metaDocumentId, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				metaDadaService.findByMetaNodePaginat(
						entitatActual.getId(),
						metaDocumentId,
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id");
		return dtr;
	}

	@RequestMapping(value = "/{metaDocumentId}/metaDada/new", method = RequestMethod.GET)
	public String getNew(HttpServletRequest request, @PathVariable Long metaDocumentId, Model model) {
		return get(request, metaDocumentId, null, model);
	}

	@RequestMapping(value = "/{metaDocumentId}/metaDada/{metaDadaId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaDocumentId,
			@PathVariable Long metaDadaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		MetaDadaDto metaDada = null;
		if (metaDadaId != null)
			metaDada = metaDadaService.findById(entitatActual.getId(), metaDocumentId, metaDadaId);
		MetaDadaCommand command = null;
		if (metaDada != null) {
			command = MetaDadaCommand.asCommand(metaDada);
		} else {
			command = new MetaDadaCommand();
		}
		command.setEntitatId(entitatActual.getId());
		command.setMetaNodeId(metaDocumentId);
		model.addAttribute(command);
		
		model.addAttribute("existContingut",  documentService.countByMetaDocument(entitatActual.getId(), metaDocumentId) != 0);
		MetaDocumentDto metaDocument = metaDocumentService.findById(metaDocumentId);
		if (metaDocument.getMetaExpedientId() != null) {
			MetaExpedientDto metaExpedient = comprovarAccesMetaExpedient(request, metaDocument.getMetaExpedientId());
			if (metaExpedient != null && metaExpedientService.isRevisioActiva()) { // es tracta d'una modificació
				if (RolHelper.isRolActualAdministradorOrgan(request)  && metaExpedient.getRevisioEstat() == MetaExpedientRevisioEstatEnumDto.REVISAT){
					MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.adminOrgan.bloquejada.alerta"));
					model.addAttribute("bloquejarCamps", true);
				} else if (RolHelper.isRolActualRevisor(request)){
					model.addAttribute("bloquejarCamps", true);
					model.addAttribute("consultar", true);
					model.addAttribute("isRolActualRevisor", true);
				}
			}
		}
		List<HtmlOption> tipus = EnumHelper.getOptionsForEnum(MetaDadaTipusEnumDto.class, "meta.dada.tipus.enum.");
		if (!aplicacioService.propertyBooleanFindByKey("es.caib.ripea.habilitar.dominis")) {
			tipus.remove(new HtmlOption("DOMINI", null));
		}
		model.addAttribute("tipus", tipus);

		return "metaDadaForm";
	}

	@RequestMapping(value = "/{metaDocumentId}/metaDada", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@PathVariable Long metaDocumentId,
			@Valid MetaDadaCommand command,
			BindingResult bindingResult,
			Model model) {
		String rolActual = RolHelper.getRolActual(request);
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		if (bindingResult.hasErrors()) {
			model.addAttribute("existContingut",  documentService.countByMetaDocument(entitatActual.getId(), metaDocumentId) != 0);
			return "metaDadaForm";
		}
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		MetaDocumentDto metaDocument = metaDocumentService.findById(metaDocumentId);
		boolean metaExpedientPendentRevisio = metaDocument.getMetaExpedientId() != null ? metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaDocument.getMetaExpedientId()) : false;
		
		if (command.getId() != null) {
			metaDadaService.update(entitatActual.getId(), metaDocumentId, MetaDadaCommand.asDto(command), rolActual, organActual != null ? organActual.getId() : null);
			
			if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
				MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
			}
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:metaDada",
					"metadada.controller.modificat.ok",
					new Object[] { command.getNom() });
		} else {
			metaDadaService.create(entitatActual.getId(), metaDocumentId, MetaDadaCommand.asDto(command), rolActual, organActual != null ? organActual.getId() : null);
			
			if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
				MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
			}
			return getModalControllerReturnValueSuccess(request,
					"redirect:metaDada",
					"metadada.controller.creat.ok",
					new Object[] { command.getNom() });
		}
	}

	@RequestMapping(value = "/{metaDocumentId}/metaDada/{metaDadaId}/enable", method = RequestMethod.GET)
	public String enable(HttpServletRequest request, @PathVariable Long metaDocumentId, @PathVariable Long metaDadaId) {
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		String rolActual = RolHelper.getRolActual(request);
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		MetaDadaDto metaDadaDto = metaDadaService.updateActiva(entitatActual.getId(), metaDocumentId, metaDadaId, true, rolActual, organActual != null ? organActual.getId() : null);
		MetaDocumentDto metaDocument = metaDocumentService.findById(metaDocumentId);
		
		if (metaDocument.getMetaExpedientId() != null) {
			boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaDocument.getMetaExpedientId());
			if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
				MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
			}
		}
		
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaDada",
				"metadada.controller.activada.ok",
				new Object[] { metaDadaDto.getNom() });
	}

	@RequestMapping(value = "/{metaDocumentId}/metaDada/{metaDadaId}/disable", method = RequestMethod.GET)
	public String disable(
			HttpServletRequest request,
			@PathVariable Long metaDocumentId,
			@PathVariable Long metaDadaId) {
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		String rolActual = RolHelper.getRolActual(request);
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		MetaDadaDto metaDadaDto = metaDadaService.updateActiva(entitatActual.getId(), metaDocumentId, metaDadaId, false, rolActual, organActual != null ? organActual.getId() : null);
		MetaDocumentDto metaDocument = metaDocumentService.findById(metaDocumentId);
		
		if (metaDocument.getMetaExpedientId() != null) {
			boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaDocument.getMetaExpedientId());
			if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
				MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
			}
		}

		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaDada",
				"metadada.controller.desactivada.ok",
				new Object[] { metaDadaDto.getNom() });
	}

	@RequestMapping(value = "/{metaDocumentId}/metaDada/{metaDadaId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, @PathVariable Long metaDocumentId, @PathVariable Long metaDadaId) {
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		try {
			String rolActual = RolHelper.getRolActual(request);	
			EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
			MetaDadaDto metaDadaDto = metaDadaService.delete(entitatActual.getId(), metaDocumentId, metaDadaId, rolActual, organActual != null ? organActual.getId() : null);
			
			MetaDocumentDto metaDocument = metaDocumentService.findById(metaDocumentId);
			boolean metaExpedientPendentRevisio = metaDocument.getMetaExpedientId() != null ? metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaDocument.getMetaExpedientId()) : false;

			if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
				MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
			}
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../../metaDada",
					"metadada.controller.esborrat.ok",
					new Object[] { metaDadaDto.getNom() });
		} catch (Exception e) {
			logger.error("Error al esborrar metadada", e);
			
			return getAjaxControllerReturnValueErrorMessage(
					request,
					"redirect:../../metaDada",
					ExceptionHelper.getRootCauseOrItself(e).getMessage(),
					e);

		}
	}
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	    binder.registerCustomEditor(
	    		BigDecimal.class,
	    		new CustomNumberEditor(
	    				BigDecimal.class,
	    				NumberFormat.getInstance(new Locale("es","ES")),
	    				true));
	    binder.registerCustomEditor(
	    		Double.class,
	    		new CustomNumberEditor(
	    				Double.class,
	    				NumberFormat.getInstance(new Locale("es","ES")),
	    				true));
	}
	
	private static final Logger logger = LoggerFactory.getLogger(MetaDocumentMetaDadaController.class);
}
