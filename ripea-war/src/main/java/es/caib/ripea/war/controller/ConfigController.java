package es.caib.ripea.war.controller;

import java.util.ArrayList;
import java.util.List;

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

import com.google.common.base.Strings;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.config.ConfigDto;
import es.caib.ripea.core.api.dto.config.ConfigGroupDto;
import es.caib.ripea.core.api.service.ConfigService;
import es.caib.ripea.core.api.service.EntitatService;
import es.caib.ripea.war.command.ConfigCommand;
import es.caib.ripea.war.helper.RolHelper;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador per a la gestió de la configuració de l'aplicació.
 * Només accessible amb el rol de superusuari.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Controller
@RequestMapping("/config")
public class ConfigController extends BaseUserController{

    @Autowired
    private ConfigService configService;
    @Autowired
    private EntitatService entitatService;

    @RequestMapping(method = RequestMethod.GET)
    public String get(HttpServletRequest request, Model model) {

        List<ConfigGroupDto> configGroups = configService.findAll();
        List<EntitatDto> entitats = new ArrayList<>();
        entitats = entitatService.findAll();
        model.addAttribute("config_groups", configGroups);
        for (ConfigGroupDto cGroup: configGroups) {
            fillFormsModel(cGroup, model, entitats);
        }
        return "config";
    }

    @ResponseBody
    @RequestMapping(value = "/entitat/{key}", method = RequestMethod.GET)
    public List<ConfigDto> getEntitatConfigByKey(HttpServletRequest request, @PathVariable String key, Model model) {

        try {
            return configService.findEntitatsConfigByKey(key.replace("-", "."));
        } catch (Exception ex) {
            log.error("Error obtinguent les configuracions d'entitat per la key " + key, ex);
            return new ArrayList<>();
        }
    }

    @ResponseBody
    @RequestMapping(value="/update", method = RequestMethod.POST)
    public SimpleResponse updateConfig(HttpServletRequest request, Model model, @Valid ConfigCommand configCommand, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return SimpleResponse.builder().status(0).message(getMessage(request, "config.controller.edit.error")).build();
        }
        String msg = "config.controller.edit.ok";
        int status = 1;
        try {
            configService.updateProperty(configCommand.asDto());
        } catch (Exception e) {
            e.printStackTrace();
            msg = "config.controller.edit.error";
            status = 0;
        }
        return SimpleResponse.builder().status(status).message(getMessage(request, msg)).build();
    }

    @ResponseBody
    @RequestMapping(value="/sync", method = RequestMethod.GET)
    public SyncResponse sync(HttpServletRequest request, Model model) {

        try {
            List<String> editedProperties = configService.syncFromJBossProperties();
            return SyncResponse.builder().status(true).editedProperties(editedProperties).build();
        } catch (Exception e) {
            return SyncResponse.builder().status(false).build();
        }
    }

    private void fillFormsModel(ConfigGroupDto cGroup, Model model, List<EntitatDto> entitats){

        String key = null;
        List<ConfigDto> confs = new ArrayList<>();
        for (ConfigDto config: cGroup.getConfigs()) {
            if (!Strings.isNullOrEmpty(config.getEntitatCodi())) {
                continue;
            }
            model.addAttribute("config_" + config.getKey().replace('.', '_'), ConfigCommand.builder().key(config.getKey()).value(config.getValue()).build());
            for (EntitatDto entitat : entitats) {
                key = config.addEntitatKey(entitat);
//                model.addAttribute("entitat_config_" + key.replace('.', '_'), ConfigCommand.builder().key(config.getKey()).value(config.getValue()).build());
            }
            confs.add(config);
        }
        cGroup.setConfigs(confs);
        if (cGroup.getInnerConfigs() == null || cGroup.getInnerConfigs().isEmpty()){
            return;
        }
        for (ConfigGroupDto child : cGroup.getInnerConfigs()){
            fillFormsModel(child, model, entitats);
        }
    }

    @Builder @Getter
    public static class SyncResponse {
        private boolean status;
        private List<String> editedProperties;
    }

    @Builder @Getter
    public static class SimpleResponse {
        private int status;
        private String message;
    }
}
