package es.caib.ripea.back.controller;

import es.caib.ripea.service.intf.dto.InteressatDto;
import es.caib.ripea.service.intf.service.ExpedientInteressatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Controlador per a les consultes ajax de interessats
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/interessatajax") // No podem posar "/ajaxuser" per mor del AjaxInterceptor
public class AjaxInteressatController {

	@Autowired
	private ExpedientInteressatService interessatService;
	
	@RequestMapping(value = "/interessat/{text}", method = RequestMethod.GET)
	@ResponseBody
	public List<InteressatDto> get(
			HttpServletRequest request,
			@PathVariable String text,
			Model model) {
		return interessatService.findByText(text);
	}

	@RequestMapping(value = "/interessat/item/{documentnum}", method = RequestMethod.GET)
	@ResponseBody
	public InteressatDto getByCodiPluginDadesUsuari(
			HttpServletRequest request,
			@PathVariable String documentnum,
			Model model) {
		try {
			return interessatService.findByDocumentNum(documentnum);
		} catch (Exception ex) {
			logger.error("Error al consultar la informació de l'interessat " + documentnum, ex);
			return null;
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(AjaxUserController.class);
}
