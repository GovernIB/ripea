/**
 * 
 */
package es.caib.ripea.war.controller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.core.api.dto.NodeDto;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.MetaDadaService;
import es.caib.ripea.war.helper.AjaxHelper;
import es.caib.ripea.war.helper.AjaxHelper.AjaxFormResponse;
import es.caib.ripea.war.helper.BeanGeneratorHelper;
import es.caib.ripea.war.helper.MissatgesHelper;

/**
 * Controlador per a la gestió de contenidors i mètodes compartits entre
 * diferents tipus de contingut.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/contingutDada")
public class ContingutDadaController extends BaseUserController {

	@Autowired
	private ContingutService contingutService;
	@Autowired
	private MetaDadaService metaDadaService;

	@Autowired
	private BeanGeneratorHelper beanGeneratorHelper;
	
	/*@Autowired
	private Validator validator;*/



	@ModelAttribute("dadesCommand")
	public Object addDadesCommand(
			@PathVariable Long contingutId,
			HttpServletRequest request) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (contingutId == null)
			return null;
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return beanGeneratorHelper.generarCommandDadesNode(
				entitatActual.getId(),
				contingutId,
				null);
	}

	@RequestMapping(value = "/{contingutId}/save", method = RequestMethod.POST)
	@ResponseBody
	public AjaxFormResponse dadaSavePost(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@ModelAttribute("dadesCommand") Object dadesCommand,
			BindingResult bindingResult,
			Model model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (bindingResult.hasErrors()) {
			MissatgesHelper.error(request, getMessage(request, "contingut.controller.dades.modificades.error"));
			return AjaxHelper.generarAjaxFormErrors(
					null,
					bindingResult);
		} else {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			List<MetaDadaDto> contingutMetaDades = metaDadaService.findByNode(
					entitatActual.getId(),
					contingutId);
			Map<String, Object> valors = new HashMap<String, Object>();
			for (int i = 0; i < contingutMetaDades.size(); i++) {
				MetaDadaDto metaDada = contingutMetaDades.get(i);
				Object valor = PropertyUtils.getSimpleProperty(
						dadesCommand,
						metaDada.getCodi());
				if (valor != null && (!(valor instanceof String) || !((String)valor).isEmpty())) {
					valors.put(
							metaDada.getCodi(),
							valor);
				}
			}
			contingutService.dadaSave(
					entitatActual.getId(),
					contingutId,
					valors);
			MissatgesHelper.success(request, getMessage(request, "contingut.controller.dades.modificades.ok"));
			return AjaxHelper.generarAjaxFormOk();
		}
	}

	@RequestMapping(value = "/{contingutId}/count")
	@ResponseBody
	public int dadaCount(
			HttpServletRequest request,
			@PathVariable Long contingutId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutDto contingut =  contingutService.findAmbIdUser(
				entitatActual.getId(),
				contingutId,
				false,
				false, null);
		if (contingut instanceof NodeDto) {
			return ((NodeDto)contingut).getDadesCount();
		} else {
			return 0;
		}
	}
	
	
	
	@RequestMapping(value = "/{contingutId}/{metaDadaCodi}")
	@ResponseBody
	public Object getDefaultValueForMetaDada(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@PathVariable String metaDadaCodi) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		Long metaNodeId =  metaDadaService.findMetaNodeIdByNodeId(
				entitatActual.getId(),
				contingutId);
		
		
		MetaDadaDto metaDada = metaDadaService.findByCodi(
				entitatActual.getId(),
				metaNodeId,
				metaDadaCodi);	
		Object valor = null;
		if (metaDada.getTipus()==MetaDadaTipusEnumDto.BOOLEA) {
			valor = metaDada.getValorBoolea();
		} else if (metaDada.getTipus()==MetaDadaTipusEnumDto.DATA) {
			valor = metaDada.getValorData();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			valor = simpleDateFormat.format(valor);
		} else if (metaDada.getTipus()==MetaDadaTipusEnumDto.FLOTANT) {
			valor = metaDada.getValorFlotant();
		} else if (metaDada.getTipus()==MetaDadaTipusEnumDto.IMPORT) {
			valor = metaDada.getValorImport();
		} else if (metaDada.getTipus()==MetaDadaTipusEnumDto.SENCER) {
			valor = metaDada.getValorSencer();
		}  else if (metaDada.getTipus()==MetaDadaTipusEnumDto.TEXT) {
			valor = metaDada.getValorString();
		}
		return valor;
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
