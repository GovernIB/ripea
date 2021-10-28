/**
 * 
 */
package es.caib.ripea.war.controller;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.core.api.service.MetaDadaService;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.war.command.MetaDadaCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
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

	@Autowired
	private MetaDadaService metaDadaService;
	@Autowired
	private MetaDocumentService metaDocumentService;

	@RequestMapping(value = "/{metaDocumentId}/metaDada", method = RequestMethod.GET)
	public String get(HttpServletRequest request, @PathVariable Long metaDocumentId, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		model.addAttribute("metaDocument", metaDocumentService.findById(entitatActual.getId(), null, metaDocumentId));
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		model.addAttribute(
				"esRevisor",
				rolActual.equals("IPA_REVISIO"));
		MetaDocumentDto metaDocument = metaDocumentService.findById(entitatActual.getId(), metaDocumentId);
		MetaExpedientDto metaExpedient = comprovarAccesMetaExpedient(request, metaDocument.getMetaExpedientId());
		if (metaExpedient != null // es tracta d'una modificació
				&& RolHelper.isRolActualAdministradorOrgan(request) && metaExpedientService.isRevisioActiva() 
				&& metaExpedient.getRevisioEstat() == MetaExpedientRevisioEstatEnumDto.REVISAT) {
			MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.adminOrgan.bloquejada.alerta"));
			model.addAttribute("bloquejarCamps", true);
		}
		return "metaDadaList";
	}

	@RequestMapping(value = "/{metaDocumentId}/metaDada/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request, @PathVariable Long metaDocumentId, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
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
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
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
		MetaDocumentDto metaDocument = metaDocumentService.findById(entitatActual.getId(), metaDocumentId);
		MetaExpedientDto metaExpedient = comprovarAccesMetaExpedient(request, metaDocument.getMetaExpedientId());
		if (metaExpedient != null // es tracta d'una modificació
				&& RolHelper.isRolActualAdministradorOrgan(request) && metaExpedientService.isRevisioActiva() 
				&& metaExpedient.getRevisioEstat() == MetaExpedientRevisioEstatEnumDto.REVISAT) {
			MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.adminOrgan.bloquejada.alerta"));
			model.addAttribute("bloquejarCamps", true);
		}
		return "metaDadaForm";
	}

	@RequestMapping(value = "/{metaDocumentId}/metaDada", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@PathVariable Long metaDocumentId,
			@Valid MetaDadaCommand command,
			BindingResult bindingResult,
			Model model) {
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		if (bindingResult.hasErrors()) {
			return "metaDadaForm";
		}
		
		MetaDocumentDto metaDocument = metaDocumentService.findById(entitatActual.getId(), metaDocumentId);
		boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaDocument.getMetaExpedientId());
		
		if (command.getId() != null) {
			metaDadaService.update(entitatActual.getId(), metaDocumentId, MetaDadaCommand.asDto(command), rolActual);
			
			if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
				MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
			}
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:metaDada",
					"metadada.controller.modificat.ok");
		} else {
			metaDadaService.create(entitatActual.getId(), metaDocumentId, MetaDadaCommand.asDto(command), rolActual);
			
			if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
				MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
			}
			return getModalControllerReturnValueSuccess(request, "redirect:metaDada", "metadada.controller.creat.ok");
		}
	}

	@RequestMapping(value = "/{metaDocumentId}/metaDada/{metaDadaId}/enable", method = RequestMethod.GET)
	public String enable(HttpServletRequest request, @PathVariable Long metaDocumentId, @PathVariable Long metaDadaId) {
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		metaDadaService.updateActiva(entitatActual.getId(), metaDocumentId, metaDadaId, true, rolActual);
		
		MetaDocumentDto metaDocument = metaDocumentService.findById(entitatActual.getId(), metaDocumentId);
		boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaDocument.getMetaExpedientId());
		
		if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
			MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
		}
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaDada",
				"metadada.controller.activada.ok");
	}

	@RequestMapping(value = "/{metaDocumentId}/metaDada/{metaDadaId}/disable", method = RequestMethod.GET)
	public String disable(
			HttpServletRequest request,
			@PathVariable Long metaDocumentId,
			@PathVariable Long metaDadaId) {
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		metaDadaService.updateActiva(entitatActual.getId(), metaDocumentId, metaDadaId, false, rolActual);
		
		MetaDocumentDto metaDocument = metaDocumentService.findById(entitatActual.getId(), metaDocumentId);
		boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaDocument.getMetaExpedientId());
		
		if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
			MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
		}
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaDada",
				"metadada.controller.desactivada.ok");
	}

	@RequestMapping(value = "/{metaDocumentId}/metaDada/{metaDadaId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, @PathVariable Long metaDocumentId, @PathVariable Long metaDadaId) {
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		metaDadaService.delete(entitatActual.getId(), metaDocumentId, metaDadaId, rolActual);
		
		MetaDocumentDto metaDocument = metaDocumentService.findById(entitatActual.getId(), metaDocumentId);
		boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaDocument.getMetaExpedientId());
		
		if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
			MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
		}
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaDada",
				"metadada.controller.esborrat.ok");
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

}
