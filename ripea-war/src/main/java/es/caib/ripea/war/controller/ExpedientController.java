/**
 * 
 */
package es.caib.ripea.war.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientComentariDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientEstatDto;
import es.caib.ripea.core.api.dto.ExpedientEstatEnumDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.DocumentEnviamentService;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.command.ContenidorCommand.Create;
import es.caib.ripea.war.command.ContenidorCommand.Update;
import es.caib.ripea.war.command.ExpedientCommand;
import es.caib.ripea.war.command.ExpedientFiltreCommand;
import es.caib.ripea.war.command.ExpedientRelacionarCommand;
import es.caib.ripea.war.command.ExpedientRelacionarCommand.Relacionar;
import es.caib.ripea.war.command.ExpedientTancarCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.MissatgesHelper;

/**
 * Controlador per al llistat d'expedients dels usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/expedient")
public class ExpedientController extends BaseUserController {

	@Autowired
	private ContingutService contingutService;
	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private MetaExpedientService metaExpedientService;
	@Autowired
	private DocumentEnviamentService documentEnviamentService;
	@Autowired
	private AplicacioService aplicacioService;

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		model.addAttribute("mantenirPaginacio", true);
		return get(request, null, model);
	}
	@RequestMapping(value = "/{expedientId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ExpedientDto expedient = null;
		if (expedientId != null) {
			expedient = expedientService.findById(
					entitatActual.getId(),
					expedientId);
		}
		ExpedientCommand command = null;
		if (expedient != null) {
			command = ExpedientCommand.asCommand(expedient);
		} else {
			command = new ExpedientCommand();
			command.setAny(Calendar.getInstance().get(Calendar.YEAR));
		}
		command.setEntitatId(entitatActual.getId());
		model.addAttribute(command);
		model.addAttribute(
				"metaExpedients",
				metaExpedientService.findActiusAmbEntitatPerCreacio(entitatActual.getId()));
		return "contingutExpedientForm";
	}
	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String postNew(
			HttpServletRequest request,
			@Validated({Create.class}) ExpedientCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			model.addAttribute(
					"metaExpedients",
					metaExpedientService.findActiusAmbEntitatPerCreacio(entitatActual.getId()));
			return "contingutExpedientForm";
		}
		expedientService.create(
				entitatActual.getId(),
				command.getMetaNodeId(),
				null,
				command.getAny(),
				command.getNom());
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../expedient",
				"expedient.controller.creat.ok");
	}
	@RequestMapping(value = "/{expedientId}/update", method = RequestMethod.POST)
	public String postUpdate(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@Validated({Update.class}) ExpedientCommand command,
			BindingResult bindingResult,
			Model model) {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			model.addAttribute(
					"metaExpedients",
					metaExpedientService.findActiusAmbEntitatPerCreacio(entitatActual.getId()));
			return "contingutExpedientForm";
		}
		expedientService.update(
				entitatActual.getId(),
				command.getId(),
				command.getNom(),
				command.getAny());
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../expedient",
				"expedient.controller.modificat.ok");
	}

	@RequestMapping(value = "/{expedientId}/agafar", method = RequestMethod.GET)
	public String agafar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@RequestParam(required = false) String contingutId,
			Model model) {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		expedientService.agafarUser(
				entitatActual.getId(),
				expedientId);
		String url;
		if (contingutId != null) {
			url = "redirect:../../contingut/" + contingutId;
		} else {
			url = "redirect:../../contingut/" + expedientId;
		}
		return getAjaxControllerReturnValueSuccess(
				request,
				url,
				"expedient.controller.agafat.ok");
	}
	
	@RequestMapping(value = "/{expedientId}/comentaris", method = RequestMethod.GET)
	public String comentaris(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"contingut",
				contingutService.findAmbIdUser(
						entitatActual.getId(),
						expedientId,
						true,
						false));
		
		boolean hasWritePermisions = expedientService.hasWritePermission(expedientId);
		model.addAttribute(
				"hasWritePermisions",
				hasWritePermisions);
		
		UsuariDto usuariActual = aplicacioService.getUsuariActual();
		model.addAttribute(
				"usuariActual",
				usuariActual);
		
		return "expedientComentaris";
	}	
	
	
	
	@RequestMapping(value = "/{contingutId}/comentaris/publicar", method = RequestMethod.POST)
	@ResponseBody
	public List<ExpedientComentariDto> publicarComentari(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@RequestParam String text,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		if (text != null && !text.isEmpty()) {
			expedientService.publicarComentariPerExpedient(entitatActual.getId(), contingutId, text);
		}
			
		return expedientService.findComentarisPerContingut(
				entitatActual.getId(), 
				contingutId);
	}



	@RequestMapping(value = "/{expedientId}/alliberar", method = RequestMethod.GET)
	public String alliberar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		expedientService.alliberarUser(
				entitatActual.getId(),
				expedientId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../contingut/" + expedientId,
				"expedient.controller.alliberat.ok");
	}

	@RequestMapping(value = "/{expedientId}/tancar", method = RequestMethod.GET)
	public String expedientTancarGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ExpedientTancarCommand command = new ExpedientTancarCommand();
		command.setId(expedientId);
		model.addAttribute(command);
		model.addAttribute(
				"expedient",
				contingutService.findAmbIdUser(
						entitatActual.getId(),
						expedientId,
						true,
						false));
		return "expedientTancarForm";
	}
	@RequestMapping(value = "/{expedientId}/tancar", method = RequestMethod.POST)
	public String expedientTancarPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@Valid ExpedientTancarCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			model.addAttribute(
					"expedient",
					contingutService.findAmbIdUser(
							entitatActual.getId(),
							expedientId,
							true,
							false));
			return "expedientTancarForm";
		}
		expedientService.tancar(
				entitatActual.getId(),
				expedientId,
				command.getMotiu());
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../contingut/" + expedientId,
				"expedient.controller.tancar.ok");
	}

	@RequestMapping(value = "/{expedientId}/relacionar", method = RequestMethod.GET)
	public String expedientRelacionarGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ExpedientRelacionarCommand command = new ExpedientRelacionarCommand();
		command.setEntitatId(entitatActual.getId());
		command.setExpedientId(expedientId);
		model.addAttribute(command);
		model.addAttribute(
				"expedient",
				contingutService.findAmbIdUser(
						entitatActual.getId(),
						expedientId,
						true,
						false));
		model.addAttribute("expedientId", expedientId);
		ExpedientFiltreCommand filtre = new ExpedientFiltreCommand();
		model.addAttribute(filtre);
		model.addAttribute(
				"metaExpedients",
				metaExpedientService.findActiusAmbEntitatPerLectura(
						entitatActual.getId()));
		model.addAttribute(
				"expedientEstatEnumOptions",
				EnumHelper.getOptionsForEnum(
						ExpedientEstatEnumDto.class,
						"expedient.estat.enum."));
		return "expedientRelacionarForm";
	}
	

	
	@RequestMapping(value = "/{expedientId}/canviarEstat", method = RequestMethod.GET)
	public String canviarEstatGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		model.addAttribute("mantenirPaginacio", true);
		
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ExpedientDto expedient = null;
		if (expedientId != null) {
			expedient = expedientService.findById(
					entitatActual.getId(),
					expedientId);
		}
		
		List<ExpedientEstatDto> expedientEstats = expedientService.findExpedientEstats(
				entitatActual.getId(),
				expedientId);
		ExpedientEstatDto expedientEstatObert = new ExpedientEstatDto();
		expedientEstatObert.setNom("OBERT");
		expedientEstats.add(0, expedientEstatObert);
		
		
		ExpedientCommand command = null;
		if (expedient != null) {
			command = ExpedientCommand.asCommand(expedient);
			if(expedient.getExpedientEstatNextInOrder()!=null){ 
				command.setExpedientEstatId(expedient.getExpedientEstatNextInOrder());
			} else { // if the state is obert
				if(expedientEstats.size()>1){ //if there are custom states to choose from
					command.setExpedientEstatId(expedientEstats.get(1).getId());
				} else {
					command.setExpedientEstatId(null);
				}
				
			}
			
		} else {
			command = new ExpedientCommand();
		}
		command.setEntitatId(entitatActual.getId());
		model.addAttribute(command);
		
		

		
		model.addAttribute(
				"expedientEstats",
				expedientEstats);
		return "expedientChooseEstatForm";
		
	}
	
	
	
	
	@RequestMapping(value = "/canviarEstat", method = RequestMethod.POST)
	public String canviarEstatPost(
			HttpServletRequest request,
			ExpedientCommand command,
			BindingResult bindingResult,
			Model model) {
		model.addAttribute("mantenirPaginacio", true);
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
//			model.addAttribute(
//					"metaExpedients",
//					metaExpedientService.findActiusAmbEntitatPerCreacio(entitatActual.getId()));
			return "expedientEstatsForm";
		}
		expedientService.changeEstatOfExpedient(
				entitatActual.getId(),
				command.getId(),
				command.getExpedientEstatId()
				);
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../expedient",
				"expedient.controller.estatModificat.ok");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@RequestMapping(value = "/{expedientId}/relacionar", method = RequestMethod.POST)
	public String expedientRelacionarPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			ExpedientFiltreCommand filtre,
			@Validated(Relacionar.class) ExpedientRelacionarCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			model.addAttribute(
					"expedient",
					contingutService.findAmbIdUser(
							entitatActual.getId(),
							expedientId,
							true,
							false));
			model.addAttribute(filtre);
			model.addAttribute(
					"expedientEstatEnumOptions",
					EnumHelper.getOptionsForEnum(
							ExpedientEstatEnumDto.class,
							"expedient.estat.enum."));
			return "expedientRelacionarForm";
		}
		expedientService.relacioCreate(
				entitatActual.getId(),
				command.getExpedientId(),
				command.getRelacionatId());
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:/../../contingut/" + expedientId,
				"expedient.controller.relacionat.ok");
	}
	@RequestMapping(value = "/{expedientId}/relacio/{relacionatId}/delete", method = RequestMethod.GET)
	public String expedientRelacioDelete(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long relacionatId,
			Model model) {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (expedientService.relacioDelete(
				entitatActual.getId(),
				expedientId,
				relacionatId)) {
			MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.controller.relacio.esborrada.ok"));
		} else {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"expedient.controller.relacio.esborrada.error"));
		}
		return "redirect:/contingut/" + expedientId;
	}

	@RequestMapping(value = "/{expedientId}/relacio/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse relacioDatatable(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			ExpedientFiltreCommand filtre,
			Model model) {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				expedientService.findAmbFiltreNoRelacionat(
						entitatActual.getId(), 
						ExpedientFiltreCommand.asDto(filtre), 
						expedientId,
						DatatablesHelper.getPaginacioDtoFromRequest(request)));		
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}

	@RequestMapping(value = "/{expedientId}/enviament/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse enviamentDatatable(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				documentEnviamentService.findAmbExpedient(
						entitatActual.getId(),
						expedientId));		
	}

}
