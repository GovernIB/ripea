/**
 * 
 */
package es.caib.ripea.war.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonMappingException;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.MetaExpedientComentariDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaExpedientFiltreDto;
import es.caib.ripea.core.api.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.command.MetaExpedientFiltreCommand;
import es.caib.ripea.war.command.MetaExpedientRevisioCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.EntitatHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;
import es.caib.ripea.war.helper.RolHelper;

/**
 * Controlador per al manteniment de meta-expedient revisions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/metaExpedientRevisio")
public class MetaExpedientRevisioController extends BaseAdminORevisorController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "MetaExpedientRevisioController.session.filtre";

	@Autowired
	private MetaExpedientService metaExpedientRevisioService;
	@Autowired
	private AplicacioService aplicacioService;

	
	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {
		getEntitatActualComprovantPermisos(request);

		MetaExpedientFiltreCommand command = getFiltreCommand(request);
		model.addAttribute(command);
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		model.addAttribute("isRolActualAdmin", rolActual.equals("IPA_ADMIN"));
		
		return "metaExpedientRevisioList";
	}

	@RequestMapping(value = "/filtrar", method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid MetaExpedientFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model,
			@RequestParam(value = "accio", required = false) String accio) {
		getEntitatActualComprovantPermisos(request);
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE);
		} else {
			if (!bindingResult.hasErrors()) {
				RequestSessionHelper.actualitzarObjecteSessio(
						request,
						SESSION_ATTRIBUTE_FILTRE,
						filtreCommand);
			}
		}
		return "redirect:../metaExpedientRevisio";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request, Model model) {
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		MetaExpedientFiltreCommand filtreCommand = getFiltreCommand(request);
//		filtreCommand.setRevisioEstat(MetaExpedientRevisioEstatEnumDto.PENDENT);
		
		MetaExpedientFiltreDto filtreDto = filtreCommand.asDto();
		filtreDto.setRevisioEstats(new MetaExpedientRevisioEstatEnumDto[] { filtreCommand.getRevisioEstat() });
//		if (rolActual.equals("IPA_ADMIN")) {
//			filtreDto.setRevisioEstats(new MetaExpedientRevisioEstatEnumDto[] { MetaExpedientRevisioEstatEnumDto.PENDENT, MetaExpedientRevisioEstatEnumDto.REVISAT });
//		} else {
//			filtreDto.setRevisioEstats(new MetaExpedientRevisioEstatEnumDto[] { MetaExpedientRevisioEstatEnumDto.PENDENT });
//		}
		
		PaginaDto<MetaExpedientDto> metaExps = metaExpedientService.findByEntitatOrOrganGestor(
				entitatActual.getId(),
				organActual == null ? null : organActual.getId(),
				filtreDto,
				organActual == null ? false : RolHelper.isRolActualAdministradorOrgan(request),
				DatatablesHelper.getPaginacioDtoFromRequest(request),
				rolActual,
				true);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(request, metaExps, "id");
		return dtr;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(HttpServletRequest request, Model model) {
		return get(request, null, model);
	}

	@RequestMapping(value = "/{metaExpedientId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		
		MetaExpedientDto metaExpedient = comprovarAccesMetaExpedient(request, metaExpedientId);
		MetaExpedientRevisioCommand command = MetaExpedientRevisioCommand.asCommand(metaExpedient);
		
		model.addAttribute(command);

		return "metaExpedientRevisioForm";
	}

	

	@RequestMapping(method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid MetaExpedientRevisioCommand command,
			BindingResult bindingResult,
			Model model) throws JsonMappingException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		MetaExpedientDto dto = command.asDto();
		
		if (command.getRevisioEstat() == MetaExpedientRevisioEstatEnumDto.REBUTJAT && (command.getRevisioComentari() == null || command.getRevisioComentari().isEmpty())) {
			bindingResult.rejectValue("revisioComentari", "NotNull");
		}
		if (bindingResult.hasErrors()) {
			return "metaExpedientRevisioForm";
		}

		metaExpedientRevisioService.canviarEstatRevisioASellecionat(entitatActual.getId(), dto);
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:metaExpedientRevisio",
				"metaexpedient.revisio.controller.estat.canviat.ok");

	}


	@RequestMapping(value = "/{metaExpedientId}/comentaris", method = RequestMethod.GET)
	public String comentaris(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"metaExpedient",
				metaExpedientService.findById(
						entitatActual.getId(),
						metaExpedientId));
		
		UsuariDto usuariActual = aplicacioService.getUsuariActual();
		model.addAttribute(
				"usuariActual",
				usuariActual);
		
		model.addAttribute(
				"isRevisor",
				true);
		
		return "metaExpedientComentaris";
	}	
	
	
	
	@RequestMapping(value = "/{metaExpedientId}/comentaris/publicar", method = RequestMethod.POST)
	@ResponseBody
	public List<MetaExpedientComentariDto> publicarComentari(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@RequestParam String text,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		if (text != null && !text.isEmpty()) {
			metaExpedientService.publicarComentariPerMetaExpedient(entitatActual.getId(), metaExpedientId, text, RolHelper.getRolActual(request));
		}

		return metaExpedientService.findComentarisPerMetaExpedient(
				entitatActual.getId(), 
				metaExpedientId,
				RolHelper.getRolActual(request));
	}




	private MetaExpedientFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		MetaExpedientFiltreCommand filtreCommand = (MetaExpedientFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new MetaExpedientFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}

}
