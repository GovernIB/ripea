package es.caib.ripea.war.controller;

import es.caib.ripea.core.api.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/plugin")
public class PluginController extends BaseController {

    @Autowired private ConfigService configService;

    @RequestMapping(value = "/restart/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String pluginRestart(HttpServletRequest request, @PathVariable String id) {
        configService.resetPlugin(id);
        return "Plugin/s reiniciats.";
    }
}