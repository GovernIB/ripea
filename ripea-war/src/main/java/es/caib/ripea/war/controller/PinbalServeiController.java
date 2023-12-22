/**
 * 
 */
package es.caib.ripea.war.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.PinbalServeiDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.PinbalServeiService;
import es.caib.ripea.war.command.PinbalServeiCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;


@Controller
@RequestMapping("/pinbalServei")
public class PinbalServeiController extends BaseUserController {
	
	@Autowired
	private PinbalServeiService pinbalServeiService;

	

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

		command.setNom(getMessage(request,"pinbal.servei." + dto.getCodi()));

		model.addAttribute(command);

		return "pinbalServeiForm";
	}

	


	@RequestMapping(method = RequestMethod.POST)
	public String save(HttpServletRequest request, @Valid PinbalServeiCommand command, BindingResult bindingResult) throws NotFoundException, IOException {

		if (bindingResult.hasErrors()) {
			return "pinbalServeiForm";

		}
		
		pinbalServeiService.update(PinbalServeiCommand.asDto(command));

		return getModalControllerReturnValueSuccess(request, "redirect:pinbalServei", "pinbalServei.controller.modificat.ok");
	}
	


}