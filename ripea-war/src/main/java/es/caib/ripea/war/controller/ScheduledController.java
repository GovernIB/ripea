package es.caib.ripea.war.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.service.MonitorTasquesService;
import es.caib.ripea.core.api.service.SegonPlaService;

@Controller
@RequestMapping("/scheduled")
public class ScheduledController extends BaseController {

	@Autowired private SegonPlaService segonPlaService;
	@Autowired private MonitorTasquesService monitorTasquesService;

    @RequestMapping(value = "/restart/{id}", method = RequestMethod.GET)
    public String schedulingRestart(
			HttpServletRequest request,
			@PathVariable String id) {
		monitorTasquesService.reiniciarTasquesEnSegonPla(id);
    	segonPlaService.restartSchedulledTasks(id);
        return "redirect:/../../ripea/monitor#tasques";
    }
}