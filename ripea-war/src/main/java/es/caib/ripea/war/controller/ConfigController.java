package es.caib.ripea.war.controller;

import es.caib.ripea.core.api.dto.config.ConfigDto;
import es.caib.ripea.core.api.dto.config.ConfigGroupDto;
import es.caib.ripea.core.api.service.ConfigService;
import es.caib.ripea.war.command.ConfigCommand;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

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

    @RequestMapping(method = RequestMethod.GET)
    public String get(
            HttpServletRequest request,
            Model model) {
        List<ConfigGroupDto> configGroups = configService.findAll();
        model.addAttribute("config_groups", configGroups);
        for (ConfigGroupDto cGroup: configGroups) {
            fillFormsModel(cGroup, model);
        }
        return "config";
    }

    @RequestMapping(value="/update", method = RequestMethod.POST)
    public String updateConfig(
            HttpServletRequest request,
            Model model,
			@Valid ConfigCommand configCommand,
			BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return get(request, model);
        }

        String msg;
        try {
            configService.updateProperty(configCommand.asDto());
            msg = "config.controller.edit.ok";
        } catch (Exception e) {
            e.printStackTrace();
            msg = "config.controller.edit.error";
        }
        return getModalControllerReturnValueSuccess(
                request,
                "redirect:.",
                msg);
    }

    @ResponseBody
    @RequestMapping(value="/sync", method = RequestMethod.GET)
    public SyncResponse sync(
            HttpServletRequest request,
            Model model) {
        try {
            List<String> editedProperties = configService.syncFromJBossProperties();
            return SyncResponse.builder()
                    .status(true)
                    .editedProperties(editedProperties)
                    .build();
        } catch (Exception e) {
            return SyncResponse.builder()
                    .status(false)
                    .build();
        }
    }

    private void fillFormsModel(ConfigGroupDto cGroup, Model model){
        for (ConfigDto config: cGroup.getConfigs()) {
            model.addAttribute("config_" + config.getKey().replace('.', '_'),
                    ConfigCommand.builder().key(config.getKey()).value(config.getValue()).build());
        }
        if (cGroup.getInnerConfigs() == null || cGroup.getInnerConfigs().isEmpty()){
            return;
        }
        for (ConfigGroupDto child : cGroup.getInnerConfigs()){
            fillFormsModel(child, model);
        }
    }

    @Builder @Getter
    public static class SyncResponse {
        private boolean status;
        private List<String> editedProperties;
    }
}
