package es.caib.ripea.back.controller;

import es.caib.ripea.back.helper.DatatablesHelper;
import es.caib.ripea.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.back.helper.MissatgesHelper;
import es.caib.ripea.service.intf.dto.MonitorTascaInfo;
import es.caib.ripea.service.intf.service.MonitorTasquesService;
import es.caib.ripea.service.intf.service.SegonPlaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/scheduled")
public class ScheduledController extends BaseController {

	@Autowired private SegonPlaService segonPlaService;
	@Autowired private MonitorTasquesService monitorTasquesService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String schedulingGet(HttpServletRequest request, Model model) {
    	return "reiniciarTasquesPeriodiquesList";
    }    		
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request, Model model) {
		List<MonitorTascaInfo> tasques = monitorTasquesService.findAll();
		try {
			Collections.sort(tasques, new Comparator<MonitorTascaInfo>() {
				@Override
				public int compare(MonitorTascaInfo o1, MonitorTascaInfo o2) {
					return o1.getCodi().compareTo(o2.getCodi());
				}
			});
		} catch (Exception ex) {}
		return DatatablesHelper.getDatatableResponse(request, tasques);
	}    
    
    @RequestMapping(value = "/restart/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String schedulingRestart(
			HttpServletRequest request,
			@PathVariable String id) {
    	try {
    		//Posa la tasca en espera
    		monitorTasquesService.reiniciarTasquesEnSegonPla(id);
    		//Reprograma la tasca
    		segonPlaService.restartSchedulledTasks(id);
    		if (!"totes".equals(id)) {
    			String nomTasca = getMessage(request, "monitor.tasques.tasca.codi."+id);
    			MissatgesHelper.success(request, getMessage(request, "monitor.tasques.reiniciat.ok", new Object[]{nomTasca}));
    		} else {
    			MissatgesHelper.success(request, getMessage(request, "monitor.tasques.reiniciat.totes"));
    		}
    	} catch (Exception e) {
    		MissatgesHelper.error(request, getMessage(request, "monitor.tasques.reiniciat.ko"), e);
		}
    	return null;
//        return "redirect:/../../ripea/monitor#tasques";
    }
}