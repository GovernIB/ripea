package es.caib.ripea.back.controller;

import es.caib.ripea.back.command.AvisCommand;
import es.caib.ripea.back.helper.DatatablesHelper;
import es.caib.ripea.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.back.helper.MissatgesHelper;
import es.caib.ripea.back.helper.RequestSessionHelper;
import es.caib.ripea.service.intf.dto.AvisDto;
import es.caib.ripea.service.intf.service.AvisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Controlador per al manteniment de avisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/avis")
public class AvisController extends BaseAdminController {

	private static final String SESSION_ATTRIBUTE_SELECCIO = "AvisController.session.seleccio";
	@Autowired private AvisService avisService;

	@RequestMapping(method = RequestMethod.GET)
	public String get() {
		return "avisList";
	}
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				avisService.findPaginat(
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id",
				SESSION_ATTRIBUTE_SELECCIO);
		return dtr;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(Model model) {
		return get(null, model);
	}
	
	@RequestMapping(value = "/toggleSelection", method = RequestMethod.GET)
	@ResponseBody
	public int toggleSelection(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) Long[] ids,
			@RequestParam(value="accio", required = false) String accio) {
		
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
		}

		//Volem afegir a la llista en sessio, els ids rebuts
		if ("select".equalsIgnoreCase(accio)) {
			if (ids != null) {
				for (Long id: ids) {
					seleccio.add(id);
				}
			}
		//Volem eliminar de la llista, els ids rebuts
		} else {
			if (ids != null) {
				for (Long id: ids) {
					seleccio.remove(id);
				}
			}
		}
		
		RequestSessionHelper.actualitzarObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO,
				seleccio);
		
		return seleccio.size();
	}
	
	@RequestMapping(value = "/accioMassiva", method = RequestMethod.GET)
	@ResponseBody
	public int select(HttpServletRequest request, @RequestParam(value="accio", required = false) String accio) {
		
		try {
		
			@SuppressWarnings("unchecked")
			Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO);
			
			if (seleccio!=null && seleccio.size()>0) {
			
				for (Long avisId: seleccio) {
				
					if ("activar".equalsIgnoreCase(accio)) {
						avisService.updateActiva(avisId, true);
					} else if ("desactivar".equalsIgnoreCase(accio)) {
						avisService.updateActiva(avisId, false);
					} else if ("eliminar".equalsIgnoreCase(accio)) {
						avisService.delete(avisId);
					}
				}
		
				RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_SELECCIO, null);
				MissatgesHelper.success(request, getMessage(request, "avis.list.accio.massiva.ok", new Object[]{seleccio.size()}));
			} else {
				MissatgesHelper.warning(request, getMessage(request, "avis.list.accio.massiva.ko"));
			}
			
			return 0;
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "avis.list.accio.massiva.err"), ex);
			return -1;
		}
	}
	
	@RequestMapping(value = "/{avisId}", method = RequestMethod.GET)
	public String get(
			@PathVariable Long avisId,
			Model model) {
		AvisDto avis = null;
		if (avisId != null)
			avis = avisService.findById(avisId);
		if (avis != null) {
			model.addAttribute(AvisCommand.asCommand(avis));
		} else {
			AvisCommand avisCommand = new AvisCommand();
			avisCommand.setDataInici(new Date());
			model.addAttribute(avisCommand);
		}
		return "avisForm";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid AvisCommand command,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			request.getSession().setAttribute(MissatgesHelper.SESSION_ATTRIBUTE_BINDING_ERRORS, bindingResult.getGlobalErrors());
			return "avisForm";
		}
		AvisDto resultat = null;
		if (command.getId() != null) {
			resultat = avisService.update(AvisCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:avis",
					"avis.controller.modificat.ok",
					new Object[] { resultat.getAssumpte() });
		} else {
			resultat = avisService.create(AvisCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:avis",
					"avis.controller.creat.ok",
					new Object[] { resultat.getAssumpte() });
		}
	}

	@RequestMapping(value = "/{avisId}/enable", method = RequestMethod.GET)
	public String enable(
			HttpServletRequest request,
			@PathVariable Long avisId) {
		AvisDto resultat = avisService.updateActiva(avisId, true);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../avis",
				"avis.controller.activat.ok",
				new Object[] { resultat.getAssumpte() });
	}

	@RequestMapping(value = "/{avisId}/disable", method = RequestMethod.GET)
	public String disable(
			HttpServletRequest request,
			@PathVariable Long avisId) {
		AvisDto resultat = avisService.updateActiva(avisId, false);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../avis",
				"avis.controller.desactivat.ok",
				new Object[] { resultat.getAssumpte() });
	}

	@RequestMapping(value = "/{avisId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long avisId) {
		AvisDto resultat = avisService.delete(avisId);
		toggleSelection(request, new Long[]{avisId}, "deselect");
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../avis",
				"avis.controller.esborrat.ok",
				new Object[] { resultat.getAssumpte() });
	}
}