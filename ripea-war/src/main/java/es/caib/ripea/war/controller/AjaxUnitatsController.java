/**
 * 
 */
package es.caib.ripea.war.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.UnitatOrganitzativaDto;
import es.caib.ripea.core.api.service.UnitatOrganitzativaService;

/**
 * Controlador per a les consultes ajax dels usuaris normals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/unitatajax") // No podem posar "/ajaxuser" per mor del AjaxInterceptor
public class AjaxUnitatsController extends BaseAdminController {
	
	@Autowired
	private UnitatOrganitzativaService unitatOrganitzativaService;
	
	@RequestMapping(value = "/unitat/item/{codi}", method = RequestMethod.GET)
	@ResponseBody
	public UnitatOrganitzativaDto getByCodi(
			HttpServletRequest request,
			@PathVariable String codi,
			Model model) {
		getEntitatActualComprovantPermisAdminEntitat(request);
		UnitatOrganitzativaDto unitat = unitatOrganitzativaService.findByCodi(codi);
		if (unitat == null) {
			unitat = new UnitatOrganitzativaDto();
			unitat.setCodi(codi);
			unitat.setDenominacio(codi);
		}
		return unitat;
	}

	@RequestMapping(value = "/unitats/{text}", method = RequestMethod.GET)
	@ResponseBody
	public List<UnitatOrganitzativaDto> get(
			HttpServletRequest request,
			@PathVariable String text,
			Model model) {
		List<UnitatOrganitzativaDto> unitatsFiltrades = new ArrayList<UnitatOrganitzativaDto>();
		
		if (text != null) {
			EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
			List<UnitatOrganitzativaDto> unitatsEntitat = unitatOrganitzativaService.findByEntitat(entitatActual.getCodi());
			
			text = text.toUpperCase();
			for (UnitatOrganitzativaDto unitat: unitatsEntitat) {
				if (unitat.getCodi().matches("(?i:.*" + text + ".*)") || unitat.getDenominacio().matches("(?i:.*" + text + ".*)"))
					unitatsFiltrades.add(unitat);
			}
		}
		
		return unitatsFiltrades;
	}

}
