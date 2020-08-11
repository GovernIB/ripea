/**
 * 
 */
package es.caib.ripea.war.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.OrganGestorFiltreDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.service.EntitatService;
import es.caib.ripea.core.api.service.OrganGestorService;
import es.caib.ripea.war.command.OrganGestorFiltreCommand;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;

/**
 * Controlador per al manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/organgestor")
public class OrganGestorController extends BaseUserController {
	
	private final static String ORGANS_FILTRE = "organs_filtre";
	
	@Autowired
	EntitatService entitatService;
	@Autowired
	OrganGestorService organGestorService;
		
	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		model.addAttribute("organGestorFiltreCommand", getFiltreCommand(request));
		return "organGestor";
	}
	
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable( 
			HttpServletRequest request ) {
		
		OrganGestorFiltreCommand organGestorFiltreCommand = getFiltreCommand(request);
		PaginaDto<OrganGestorDto> organs = new PaginaDto<OrganGestorDto>();
		
		try {
			EntitatDto entitat = getEntitatActualComprovantPermisos(request);
			
			organs = organGestorService.findOrgansGestorsAmbFiltrePaginat(
										entitat.getId(),
										ConversioTipusHelper.convertir(organGestorFiltreCommand, 
																		OrganGestorFiltreDto.class),
										DatatablesHelper.getPaginacioDtoFromRequest(request));
		}catch(SecurityException e) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"notificacio.controller.entitat.cap.assignada"));
		}
		return DatatablesHelper.getDatatableResponse(
				request, 
				organs,
				"codi");
	}
	@RequestMapping(value = "/sync/dir3", method = RequestMethod.GET)
	public String syncDir3( 
			HttpServletRequest request ) {
		
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		organGestorService.syncDir3OrgansGestors(entitat.getId());
		
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../organgestor",
				"organgestor.controller.update.nom.ok");
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(	
			HttpServletRequest request,
			OrganGestorFiltreCommand command,
			Model model) {
		
		RequestSessionHelper.actualitzarObjecteSessio(
				request, 
				ORGANS_FILTRE, 
				command);
		
		return "organGestor";
	}
//	
//	@RequestMapping(value = "/{organGestorCodi}/update", method = RequestMethod.GET)
//	public String delete(
//			HttpServletRequest request,
//			@PathVariable String organGestorCodi) {		
//		
//		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
//		
//		try {
//			return getAjaxControllerReturnValueSuccess(
//					request,
//					"redirect:../../organgestor",
//					"organgestor.controller.update.nom.ok");
//		} catch (Exception e) {
//			return getAjaxControllerReturnValueError(
//					request,
//					"redirect:../../organgestor",
//					"organgestor.controller.update.nom.error");
//		}
//	}
//	
//	@RequestMapping(value = "/update", method = RequestMethod.GET)
//	public String delete(
//			HttpServletRequest request) {		
//		
//		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
//		
//		try {
////			procedimentService.updateOrgansGestorsNom(
////					entitat.getId());
//			return getAjaxControllerReturnValueSuccess(
//					request,
//					"redirect:../organgestor",
//					"organgestor.controller.update.nom.tots.ok");
//		} catch (Exception e) {
//			return getAjaxControllerReturnValueError(
//					request,
//					"redirect:../organgestor",
//					"organgestor.controller.update.nom.tots.error");
//		}
//	}
//	
	private OrganGestorFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		OrganGestorFiltreCommand organGestorFiltreCommand = (
				OrganGestorFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
						request,
						ORGANS_FILTRE);
		if (organGestorFiltreCommand == null) {
			organGestorFiltreCommand = new OrganGestorFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					ORGANS_FILTRE,
					organGestorFiltreCommand);
		}
		return organGestorFiltreCommand;
	}
	
}
