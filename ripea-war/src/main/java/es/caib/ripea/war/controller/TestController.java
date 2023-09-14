/**
 * 
 */
package es.caib.ripea.war.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.ripea.core.api.service.AlertaService;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.DocumentEnviamentService;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.service.EntitatService;
import es.caib.ripea.core.api.service.ExpedientInteressatService;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.api.service.ExpedientTascaService;
import es.caib.ripea.core.api.service.MetaDadaService;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.core.api.service.OrganGestorService;
import es.caib.ripea.core.api.service.URLInstruccioService;
import es.caib.ripea.war.command.TestCommand;
import es.caib.ripea.war.helper.BeanGeneratorHelper;
import es.caib.ripea.war.helper.CustomDatesEditor;
import es.caib.ripea.war.helper.ExpedientHelper;


@Controller
@RequestMapping("/test")
public class TestController  {

	
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private ContingutService contingutService;
	@Autowired
	private MetaExpedientService metaExpedientService;
	@Autowired
	private MetaDocumentService metaDocumentService;
	@Autowired
	private ExpedientInteressatService interessatService;
	@Autowired
	private DocumentEnviamentService documentEnviamentService;	
	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private MetaDadaService metaDadaService;
	@Autowired
	private AlertaService alertaService;
	@Autowired
	private BeanGeneratorHelper beanGeneratorHelper;
	@Autowired
	private DocumentService documentService;
	@Autowired
	private OrganGestorService organGestorService;
	@Autowired
	private ExpedientHelper expedientHelper;
	@Autowired
	private URLInstruccioService urlInstruccioService;
	@Autowired
	private ExpedientTascaService expedientTascaService;
	@Autowired
	private EntitatService entitatService;
	

	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String contingutGet(
			HttpServletRequest request,
			Model model) {

		TestCommand testCommand = new TestCommand();
		testCommand.setDataSingle(new Date());
		testCommand.setText("aaaaa");

		model.addAttribute("testCommand", testCommand);

		return "test";

	}

	
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	    
	    binder.registerCustomEditor(
	    		Date[].class,
	    		new CustomDatesEditor());

	}
	


//	@InitBinder
//	public void initBinder(WebDataBinder binder) {
//		binder.registerCustomEditor(
//				byte[].class,
//				new ByteArrayMultipartFileEditor());
//	}
	

	private static final Logger logger = LoggerFactory.getLogger(TestController.class);

}
