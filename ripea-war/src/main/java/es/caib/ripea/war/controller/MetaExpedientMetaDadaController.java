/**
 * 
 */
package es.caib.ripea.war.controller;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.dao.DataIntegrityViolationException;
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
import es.caib.ripea.core.api.dto.DominiDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.ResultatConsultaDto;
import es.caib.ripea.core.api.dto.ResultatDominiDto;
import es.caib.ripea.core.api.exception.DominiException;
import es.caib.ripea.core.api.service.DominiService;
import es.caib.ripea.core.api.service.MetaDadaService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.command.MetaDadaCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per al manteniment de les meta-dades dels meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/metaExpedient")
public class MetaExpedientMetaDadaController extends BaseAdminController {

	@Autowired
	private MetaDadaService metaDadaService;
	@Autowired
	private MetaExpedientService metaExpedientService;
	@Autowired
	private DominiService dominiService;

	@RequestMapping(value = "/{metaExpedientId}/metaDada", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		model.addAttribute(
				"esRevisor",
				rolActual.equals("IPA_REVISIO"));
		
		if (!rolActual.equals("IPA_REVISIO")) {
			comprovarAccesMetaExpedient(request, metaExpedientId);
		}
		
		model.addAttribute(
				"metaExpedient",
				metaExpedientService.findById(
						entitatActual.getId(),
						metaExpedientId));

		return "metaDadaList";
	}
	@RequestMapping(value = "/{metaExpedientId}/metaDada/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		
		if (!rolActual.equals("IPA_REVISIO")) {
			comprovarAccesMetaExpedient(request, metaExpedientId);
		}
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				metaDadaService.findByMetaNodePaginat(
						entitatActual.getId(),
						metaExpedientId,
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id");
		return dtr;
	}
	
	
	@RequestMapping(value = "/metaDada/{metaNodeId}/{metaDadaId}/move/{posicio}", method = RequestMethod.GET)
	public String move(
			HttpServletRequest request,
			@PathVariable Long metaNodeId,
			@PathVariable Long metaDadaId,
			@PathVariable int posicio) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		metaDadaService.moveTo(
				entitatActual.getId(),
				metaNodeId,
				metaDadaId,
				posicio);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:metaDada/"+metaNodeId,
				null);
	}

	@RequestMapping(value = "/{metaExpedientId}/metaDada/new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		return get(request, metaExpedientId, null, model);
	}
	@RequestMapping(value = "/{metaExpedientId}/metaDada/{metaDadaId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long metaDadaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		comprovarAccesMetaExpedient(request, metaExpedientId);
		MetaDadaDto metaDada = null;
		if (metaDadaId != null)
			metaDada = metaDadaService.findById(
					entitatActual.getId(),
					metaExpedientId,
					metaDadaId);
		MetaDadaCommand command = null;
		if (metaDada != null) {
			command = MetaDadaCommand.asCommand(metaDada);
			if (metaDada.getTipus().equals(MetaDadaTipusEnumDto.DOMINI))
				model.addAttribute("selectedMetaDada", metaDada.getCodi());
		} else {
			command = new MetaDadaCommand();
		}
		command.setEntitatId(entitatActual.getId());
		command.setMetaNodeId(metaExpedientId);
		model.addAttribute(command);
		return "metaDadaForm";
	}
	@RequestMapping(value = "/{metaExpedientId}/metaDada", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@Valid MetaDadaCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaExpedientId);
		comprovarAccesMetaExpedient(request, metaExpedientId);
		if (bindingResult.hasErrors()) {
			return "metaDadaForm";
		}

		if (command.getId() != null) {
			metaDadaService.update(
					entitatActual.getId(),
					metaExpedientId,
					MetaDadaCommand.asDto(command), rolActual);
			
			if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio) {
				MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
			}
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:metaDada",
					"metadada.controller.modificat.ok");
		} else {
			metaDadaService.create(
					entitatActual.getId(),
					metaExpedientId,
					MetaDadaCommand.asDto(command), rolActual);
			
			if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio) {
				MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
			}
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:metaDada",
					"metadada.controller.creat.ok");
		}
	}


	@RequestMapping(value = "/{metaExpedientId}/metaDada/{metaDadaId}/enable", method = RequestMethod.GET)
	public String enable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long metaDadaId) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaExpedientId);
		
		comprovarAccesMetaExpedient(request, metaExpedientId);
		metaDadaService.updateActiva(
				entitatActual.getId(),
				metaExpedientId,
				metaDadaId,
				true, 
				rolActual);
		
		if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio) {
			MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
		}
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaDada",
				"metadada.controller.activada.ok");
	}
	@RequestMapping(value = "/{metaExpedientId}/metaDada/{metaDadaId}/disable", method = RequestMethod.GET)
	public String disable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long metaDadaId) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaExpedientId);
		
		comprovarAccesMetaExpedient(request, metaExpedientId);
		metaDadaService.updateActiva(
				entitatActual.getId(),
				metaExpedientId,
				metaDadaId,
				false, 
				rolActual);
		
		if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio) {
			MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
		}
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaDada",
				"metadada.controller.desactivada.ok");
	}

	@RequestMapping(value = "/{metaExpedientId}/metaDada/{metaDadaId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long metaDadaId) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaExpedientId);
		
		comprovarAccesMetaExpedient(request, metaExpedientId);
		try {
			metaDadaService.delete(
					entitatActual.getId(),
					metaExpedientId,
					metaDadaId, 
					rolActual);
			
			if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio) {
				MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
			}
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../../metaDada",
					"metadada.controller.esborrat.ok");
		} catch (DataIntegrityViolationException ex) {
			return getAjaxControllerReturnValueError(
					request,
					"redirect:../../esborrat",
					"metadada.controller.esborrar.error.fk");
		}
	}
	
	@RequestMapping(value = "/{metaExpedientId}/metaDada/domini", method = RequestMethod.GET)
	@ResponseBody
	public List<DominiDto> getDominis(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		comprovarAccesMetaExpedient(request, metaExpedientId);
		List<DominiDto> dominis = dominiService.findByEntitat(entitatActual.getId());
		return dominis;
	}
	
	@RequestMapping(value = "/{metaExpedientId}/metaDada/domini/{dominiCodi}", method = RequestMethod.GET)
	@ResponseBody
	public ResultatDominiDto getDomini(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable String dominiCodi,
			@RequestParam(value="filter", required = false) String filter,
			@RequestParam(value="pageSize", required = false) int pageSize,
			@RequestParam(value="page", required = false) int page){
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ResultatDominiDto resultatDomini = null;
		DominiDto domini = dominiService.findByCodiAndEntitat(dominiCodi,entitatActual.getId());
		try {
			resultatDomini = dominiService.getResultDomini(
						entitatActual.getId(),
						domini,
						filter,
						page,
						pageSize);
		} catch (DominiException e) {
			e.printStackTrace();
		}
		
		return resultatDomini;
	}
	
	@RequestMapping(value = "/{metaExpedientId}/metaDada/domini/{dominiCodi}/{dadaValor}", method = RequestMethod.GET)
	@ResponseBody
	public ResultatConsultaDto getDomini(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable String dominiCodi,
			@PathVariable String dadaValor){
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ResultatConsultaDto resultatConsulta = null;
		DominiDto domini = dominiService.findByCodiAndEntitat(dominiCodi, entitatActual.getId());
		resultatConsulta = dominiService.getSelectedDomini(
					entitatActual.getId(),
					domini,
					dadaValor);
		return resultatConsulta;
	}
	
	@RequestMapping(value = "/{metaExpedientId}/metaDadaPermisLectura/domini", method = RequestMethod.GET)
	@ResponseBody
	public List<DominiDto> getDominiMetaExpedientPermisLectura(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId){
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		MetaExpedientDto metaExpedientDto = metaExpedientService.findById(entitatActual.getId(), metaExpedientId);
		List<DominiDto> dominis = dominiService.findByMetaNodePermisLecturaAndTipusDomini(entitatActual.getId(), metaExpedientDto);		
		return dominis;
	}
	
	@RequestMapping(value = "/metaDadaPermisLectura/domini", method = RequestMethod.GET)
	@ResponseBody
	public List<DominiDto> getDominisEntitatPermisLectura(HttpServletRequest request){
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		List<DominiDto> dominis = dominiService.findByEntitat(entitatActual.getId());
		return dominis;
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
