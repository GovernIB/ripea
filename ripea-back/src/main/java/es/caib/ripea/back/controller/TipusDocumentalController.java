/**
 * 
 */
package es.caib.ripea.back.controller;

import es.caib.ripea.back.command.TipusDocumentalCommand;
import es.caib.ripea.back.helper.DatatablesHelper;
import es.caib.ripea.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.TipusDocumentalDto;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.TipusDocumentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Controlador per al manteniment de les meta-dades dels meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/tipusDocumental")
public class TipusDocumentalController extends BaseAdminController {

	@Autowired
	private TipusDocumentalService tipusDocumentalService;
	@Autowired
	private AplicacioService aplicacioService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		getEntitatActualComprovantPermisAdminEntitat(request); 
		model.addAttribute("tipusDocumentalsNtiCodiEspecific", Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.tipus.documentals.nti.codi.especific")));
		return "tipusDocumentalList";
	}
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				tipusDocumentalService.findByEntitatPaginat(
						entitatActual.getId(),
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id");
		return dtr;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			Model model) {
		return get(request, null, model);
	}
	
	@RequestMapping(value = "/{tipusDocumentalId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long tipusDocumentalId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		TipusDocumentalDto tipusDocumental = null;
		if (tipusDocumentalId != null)
			tipusDocumental = tipusDocumentalService.findById(
					entitatActual.getId(),
					tipusDocumentalId);
		TipusDocumentalCommand command = null;
		if (tipusDocumental != null) {
			command = TipusDocumentalCommand.asCommand(tipusDocumental);
			command.setEntitatId(entitatActual.getId());
		} else {
			command = new TipusDocumentalCommand();
			command.setEntitatId(entitatActual.getId());
		}
		model.addAttribute(command);
		return "tipusDocumentalForm";
	}
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid TipusDocumentalCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		if (bindingResult.hasErrors()) {
			return "tipusDocumentalForm";
		}
		if (command.getId() != null) {
			tipusDocumentalService.update(
					entitatActual.getId(), 
					TipusDocumentalCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../tipusDocumental",
					"tipusdocumental.controller.modificat.ok",
					new Object[] { command.getNomCatala()!=null?command.getNomCatala():command.getNomEspanyol() });
		} else {
			tipusDocumentalService.create(
					entitatActual.getId(), 
					TipusDocumentalCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../tipusDocumental",
					"tipusdocumental.controller.creat.ok",
					new Object[] { command.getNomCatala()!=null?command.getNomCatala():command.getNomEspanyol() });
		}
	}
	
	@RequestMapping(value = "/{tipusDocumentalId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long tipusDocumentalId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitat(request);
		TipusDocumentalDto tipusDoc = tipusDocumentalService.delete(
				entitatActual.getId(),
				tipusDocumentalId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../tipusDocumental",
				"tipusdocumental.controller.esborrat.ok",
				new Object[] { tipusDoc.getNomCatala()!=null?tipusDoc.getNomCatala():tipusDoc.getNomEspanyol() });
	}

}
