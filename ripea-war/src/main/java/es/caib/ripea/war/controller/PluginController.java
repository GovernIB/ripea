package es.caib.ripea.war.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.GenericDto;
import es.caib.ripea.core.api.service.ConfigService;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.MissatgesHelper;

@Controller
@RequestMapping("/plugin")
public class PluginController extends BaseController {

    @Autowired private ConfigService configService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String pluginGet(HttpServletRequest request, Model model) {
    	return "reiniciarPluginsList";
    }        
    
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request, Model model) {
    	List<GenericDto> plugins = new ArrayList<GenericDto>();
    	plugins.add(new GenericDto(null, "ax", getMessage(request, "decorator.menu.reinici.plugin.ax")));
    	plugins.add(new GenericDto(null, "di", getMessage(request, "decorator.menu.reinici.plugin.di")));
    	plugins.add(new GenericDto(null, "no", getMessage(request, "decorator.menu.reinici.plugin.no")));
    	plugins.add(new GenericDto(null, "cd", getMessage(request, "decorator.menu.reinici.plugin.cd")));
    	plugins.add(new GenericDto(null, "us", getMessage(request, "decorator.menu.reinici.plugin.us")));
    	plugins.add(new GenericDto(null, "pi", getMessage(request, "decorator.menu.reinici.plugin.pi")));
    	plugins.add(new GenericDto(null, "de", getMessage(request, "decorator.menu.reinici.plugin.de")));
    	plugins.add(new GenericDto(null, "ro", getMessage(request, "decorator.menu.reinici.plugin.ro")));
    	plugins.add(new GenericDto(null, "dg", getMessage(request, "decorator.menu.reinici.plugin.dg")));
    	plugins.add(new GenericDto(null, "pf", getMessage(request, "decorator.menu.reinici.plugin.pf")));
    	plugins.add(new GenericDto(null, "vf", getMessage(request, "decorator.menu.reinici.plugin.vf")));
    	plugins.add(new GenericDto(null, "gd", getMessage(request, "decorator.menu.reinici.plugin.gd")));
    	plugins.add(new GenericDto(null, "fs", getMessage(request, "decorator.menu.reinici.plugin.fs")));
    	plugins.add(new GenericDto(null, "si", getMessage(request, "decorator.menu.reinici.plugin.si")));
    	plugins.add(new GenericDto(null, "vi", getMessage(request, "decorator.menu.reinici.plugin.vi")));
		try {
			Collections.sort(plugins, new Comparator<GenericDto>() {
				@Override
				public int compare(GenericDto o1, GenericDto o2) {
					return o1.getTexte().compareTo(o2.getTexte());
				}
			});
		} catch (Exception ex) {}
		return DatatablesHelper.getDatatableResponse(request, plugins);
	}  
    
    @RequestMapping(value = "/restart/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String pluginRestart(HttpServletRequest request, @PathVariable String id) {
    	try {
    		configService.resetPlugin(id);
    		if (!"xx".equals(id)) {
    			String nomPlugin = getMessage(request, "decorator.menu.reinici.plugin."+id);
    			MissatgesHelper.success(request, getMessage(request, "monitor.plugin.reiniciat.ok", new Object[]{nomPlugin}));
    		} else {
    			MissatgesHelper.success(request, getMessage(request, "monitor.plugin.reiniciat.tots"));
    		}
    	} catch (Exception e) {
    		MissatgesHelper.error(request, getMessage(request, "monitor.plugin.reiniciat.ko"), e);
		}
    	return null;
    }
}