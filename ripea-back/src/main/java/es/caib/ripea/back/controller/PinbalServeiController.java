/**
 * 
 */
package es.caib.ripea.back.controller;

import es.caib.ripea.back.command.PinbalServeiCommand;
import es.caib.ripea.back.helper.DatatablesHelper;
import es.caib.ripea.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.service.intf.dto.PinbalServeiDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.PinbalServeiService;
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
import java.io.IOException;


@Controller
@RequestMapping("/pinbalServei")
public class PinbalServeiController extends BaseUserController {
	
	@Autowired private PinbalServeiService pinbalServeiService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(Model model, HttpServletRequest request) {
		return "pinbalServeiList";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request, Model model) {
		return DatatablesHelper.getDatatableResponse(
				request,
				pinbalServeiService.findPaginat(DatatablesHelper.getPaginacioDtoFromRequest(request)));
	}

	@RequestMapping(value = "/{serveiId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long serveiId,
			Model model) {
		PinbalServeiDto dto = pinbalServeiService.findById(serveiId);
		PinbalServeiCommand command = PinbalServeiCommand.asCommand(dto);
		model.addAttribute(command);
		return "pinbalServeiForm";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String save(HttpServletRequest request, @Valid PinbalServeiCommand command, BindingResult bindingResult) throws NotFoundException, IOException {

		if (bindingResult.hasErrors()) {
			return "pinbalServeiForm";
		}
		
		PinbalServeiDto resultat = pinbalServeiService.update(PinbalServeiCommand.asDto(command));

		return getModalControllerReturnValueSuccess(request,
				"redirect:pinbalServei",
				"pinbalServei.controller.modificat.ok",
				new Object[] { resultat.getCodi() });
	}
}