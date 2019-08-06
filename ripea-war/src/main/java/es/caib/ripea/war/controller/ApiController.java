package es.caib.ripea.war.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.ripea.core.api.service.DocumentService;

@Controller
@RequestMapping("/api")
public class ApiController extends BaseController {

	@Autowired
	private DocumentService documentService;
	
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