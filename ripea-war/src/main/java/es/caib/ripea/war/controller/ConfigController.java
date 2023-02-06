package es.caib.ripea.war.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Strings;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.config.ConfigDto;
import es.caib.ripea.core.api.dto.config.ConfigGroupDto;
import es.caib.ripea.core.api.service.ConfigService;
import es.caib.ripea.core.api.service.EntitatService;
import es.caib.ripea.war.command.ConfigCommand;
import es.caib.ripea.war.command.OrganConfigCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
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
    
    
    @ResponseBody
    @RequestMapping(value = "/configurableEntitat", method = RequestMethod.GET)
	public SimpleResponse configurableEntitat(
			HttpServletRequest request,
			@RequestParam(value = "key") String key,
			@RequestParam(value = "configurable") boolean configurable) {

        try {
        	configService.configurableEntitat(key, configurable);
        	
        	 return SimpleResponse.builder().status(1).message(getMessage(request, "config.controller.edit.ok")).build();
        } catch (Exception ex) {
            

            return SimpleResponse.builder().status(0).message(getMessage(request, "config.controller.edit.error")).build();
        }
        
    }
    
    @ResponseBody
    @RequestMapping(value = "/configurableOrgan", method = RequestMethod.GET)
	public SimpleResponse configurableOrgan(
			HttpServletRequest request,
			@RequestParam(value = "key") String key,
			@RequestParam(value = "configurable") boolean configurable) {

        try {
        	
        	configService.configurableOrgan(key, configurable);
        	
        	 return SimpleResponse.builder().status(1).message(getMessage(request, "config.controller.edit.ok")).build();
        } catch (Exception ex) {
            

            return SimpleResponse.builder().status(0).message(getMessage(request, "config.controller.edit.error")).build();
        }
        
    }
    
    
    @ResponseBody
    @RequestMapping(value = "/entitat", method = RequestMethod.GET)
    public List<ConfigDto> getEntitatConfigByKey(HttpServletRequest request, @RequestParam(value = "key") String key, Model model) {

        try {
            return configService.findEntitatsConfigByKey(key);
        } catch (Exception ex) {
            log.error("Error obtinguent les configuracions d'entitat per la key " + key, ex);
            return new ArrayList<>();
        }
    }
    

    
	@RequestMapping(value = "/organ/new", method = RequestMethod.GET)
	public String getOrganNew(
			HttpServletRequest request,
			@RequestParam String key,
			Model model) {

		OrganConfigCommand command = new OrganConfigCommand();
		command.setCrear(true);
		command.setKey(key);
		
		ConfigDto config = configService.findConfig(key);
		command.setJbossProperty(config.isJbossProperty());
		model.addAttribute("config", config);

		model.addAttribute(command);
		
		return "configOrganForm";
	}
	
	@RequestMapping(value = "/organ/new", method = RequestMethod.POST)
	public String postOrganNew(
			HttpServletRequest request,
			@Validated OrganConfigCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {
		if (command.getOrganGestorId() == null) {
			bindingResult.rejectValue("organGestorId", "NotNull");
		}
		if (!command.isJbossProperty() && StringUtils.isEmpty(command.getValue())) {
			bindingResult.rejectValue("organGestorId", "NotEmpty");
		}
		
		if (bindingResult.hasErrors()) {
			command.setCrear(true);
			return "configOrganForm";
		}
		
		configService.createPropertyOrgan(OrganConfigCommand.asDto(command));
		

		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../config",
				"config.controller.organ.new.ok");
			

	}
	
	
	@RequestMapping(value = "/organ/update", method = RequestMethod.GET)
	public String getOrganUpdate(
			HttpServletRequest request,
			@RequestParam String key,
			Model model) {

		OrganConfigCommand command = OrganConfigCommand.asCommand(configService.findConfigOrgan(key));
		model.addAttribute(command);
		
		model.addAttribute("config", configService.findConfig(key));
		
		return "configOrganForm";
	}
	
	
	@RequestMapping(value = "/organ/update", method = RequestMethod.POST)
	public String postOrganUpdate(
			HttpServletRequest request,
			@Validated OrganConfigCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {
		if (!command.isJbossProperty() && StringUtils.isEmpty(command.getValue())) {
			bindingResult.rejectValue("organGestorId", "NotEmpty");
		}
		if (bindingResult.hasErrors()) {
			return "configOrganForm";
		}
		
		configService.modificarPropertyOrgan(OrganConfigCommand.asDto(command));

		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../config",
				"config.controller.organ.edit.ok");
			

	}
	
	
	@RequestMapping(value = "/organ/delete", method = RequestMethod.GET)
	public String organDelete(
			HttpServletRequest request,
			@RequestParam String key,
			Model model) {

		configService.deletePropertyOrgan(key);
		return getAjaxControllerReturnValueSuccess(request,
				"redirect:../../config",
				"config.controller.organ.esborrada.ok");
	}
	
	@RequestMapping(value = "/{keyUnderscore}/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable String keyUnderscore) {

		return DatatablesHelper.getDatatableResponse(
				request,
				configService.findConfigsOrgans(
						keyUnderscore.replace("-", "."),
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id");
	}
	


    private void fillFormsModel(ConfigGroupDto cGroup, Model model, List<EntitatDto> entitats){

        String key = null;
        List<ConfigDto> confs = new ArrayList<>();
        for (ConfigDto config: cGroup.getConfigs()) {
            if (!Strings.isNullOrEmpty(config.getEntitatCodi())) {
                continue;
            }
            model.addAttribute("config_" + config.getKey().replace('.', '-'), ConfigCommand.builder().key(config.getKey()).value(config.getValue()).build());
            for (EntitatDto entitat : entitats) {
                key = config.addEntitatKey(entitat);
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
