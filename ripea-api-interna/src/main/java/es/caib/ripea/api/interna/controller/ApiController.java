package es.caib.ripea.api.interna.controller;

import es.caib.ripea.service.intf.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/api")
public class ApiController {

	@Autowired
	private DocumentService documentService;

	@RequestMapping(value = {"/apidoc", "/rest"}, method = RequestMethod.GET)
	public String documentacio(HttpServletRequest request) {
		return "apidoc";
	}

	@RequestMapping(value = "/viaFirmaResponse" , method = RequestMethod.GET)
	public void get(HttpServletRequest request, HttpServletResponse response) {
		doService(request, response);
	}
	@RequestMapping(value = "/viaFirmaResponse" , method = RequestMethod.POST)
	public void post(HttpServletRequest request, HttpServletResponse response) {
		doService(request, response);
	}
	
	private void doService(HttpServletRequest req, HttpServletResponse resp) {
		if (req.getParameter("message")!=null) {
			try {
				String messageJson = req.getParameter("message");
				documentService.processarRespostaViaFirma(messageJson);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("null");
		}
	}

}