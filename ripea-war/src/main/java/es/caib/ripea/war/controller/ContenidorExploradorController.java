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

import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.ExpedientService;

/**
 * Controlador per a navegar pels contenidors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/contenidor")
public class ContenidorExploradorController extends BaseUserController {

	@Autowired
	private ContingutService contenidorService;
	@Autowired
	private ExpedientService expedientService;



	@RequestMapping(value = "/explora/{contenidorArrelId}/{contenidorId}", method = RequestMethod.GET)
	@ResponseBody
	public ContingutDto get1(
			HttpServletRequest request,
			@PathVariable Long contenidorArrelId,
			@PathVariable Long contenidorId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutDto contenidor = contenidorService.findAmbIdUser(
				entitatActual.getId(),
				contenidorId,
				true,
				false);
		contenidor.setContenidorArrelIdPerPath(contenidorArrelId);
		return contenidor;
	}	
	
	
	@RequestMapping(value = "/explora/{contenidorId}", method = RequestMethod.GET)
	@ResponseBody
	public ContingutDto get(
			HttpServletRequest request,
//			@PathVariable Long contenidorArrelId,
			@PathVariable Long contenidorId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutDto contenidor = contenidorService.findAmbIdUser(
				entitatActual.getId(),
				contenidorId,
				true,
				false);
//		contenidor.setContenidorArrelIdPerPath(contenidorArrelId);
		return contenidor;
	}
	
	
	@RequestMapping(value = "/exploraAllWithSameExpedientType/{contenidorArrelId}/{contenidorId}", method = RequestMethod.GET)
	@ResponseBody
	public List<ContingutDto> getAllWithTheSameType(
			HttpServletRequest request,
			@PathVariable Long contenidorArrelId,
			@PathVariable Long contenidorId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutDto contenidor = contenidorService.findAmbIdUser(
				entitatActual.getId(),
				contenidorId,
				true,
				false);
		contenidor.setContenidorArrelIdPerPath(contenidorArrelId);
		
		Long metaExpedientId;
		ExpedientDto exp;
		if(contenidor.isExpedient()){
			exp = (ExpedientDto) contenidor; 
			metaExpedientId = exp.getMetaNode().getId();
		} else {
			exp = (ExpedientDto) contenidor.getExpedientPare(); 
			metaExpedientId = exp.getMetaNode().getId();
		}
		
		List<ExpedientDto> expedients = expedientService.findByEntitatAndMetaExpedient(entitatActual.getId(), metaExpedientId);
		
		
		List<ContingutDto> expedientsReplaced = new ArrayList<>();
		
		
		for(ContingutDto expedient: expedients){
			if(!expedient.getId().equals(exp.getId())){
				expedientsReplaced.add(expedient);
			} else {
				expedientsReplaced.add(contenidor);
			}
		}

		
		
		return expedientsReplaced;
	}

}
