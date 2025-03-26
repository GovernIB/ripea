/**
 * 
 */
package es.caib.ripea.back.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.ripea.back.command.TestCommand;

@Controller
@RequestMapping("/test")
public class TestController  {
	
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

	private static final Logger logger = LoggerFactory.getLogger(TestController.class);
}