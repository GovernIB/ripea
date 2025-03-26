/**
 * 
 */
package es.caib.ripea.back.controller;

import es.caib.ripea.back.helper.AjaxHelper;
import es.caib.ripea.back.helper.AjaxHelper.AjaxFormResponse;
import es.caib.ripea.back.helper.BeanGeneratorHelper;
import es.caib.ripea.back.helper.CustomDatesEditor;
import es.caib.ripea.back.helper.MissatgesHelper;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.service.ContingutService;
import es.caib.ripea.service.intf.service.MetaDadaService;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Controlador per a la gestió de contenidors i mètodes compartits entre
 * diferents tipus de contingut.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/contingutDada")
public class ContingutDadaController extends BaseUserOAdminOOrganController {

	@Autowired private ContingutService contingutService;
	@Autowired private MetaDadaService metaDadaService;
	@Autowired private BeanGeneratorHelper beanGeneratorHelper;

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
			MissatgesHelper.error(request, getMessage(request, "contingut.controller.dades.modificades.error"), null);
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
					valors, 
					null);
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
				false, true, null, null);
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
}