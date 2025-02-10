package es.caib.ripea.back.controller;

import es.caib.ripea.back.command.TascaCancelarDelegacioCommand;
import es.caib.ripea.back.command.TascaDelegarCommand;
import es.caib.ripea.back.command.UsuariTascaFiltreCommand;
import es.caib.ripea.back.command.UsuariTascaRebuigCommand;
import es.caib.ripea.back.helper.DatatablesHelper;
import es.caib.ripea.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.back.helper.RequestSessionHelper;
import es.caib.ripea.back.helper.RolHelper;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.service.ExpedientService;
import es.caib.ripea.service.intf.service.ExpedientTascaService;
import es.caib.ripea.service.intf.service.MetaExpedientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controlador per al llistat de tasques d'usuaris.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/usuariTasca")
public class UsuariTascaController extends BaseUserController {

    private static final String SESSION_ATTRIBUTE_FILTRE = "UsuariTascaController.session.filtre";

    @Autowired
    private ExpedientTascaService expedientTascaService;
    @Autowired
    private ExpedientService expedientService;
    @Autowired
    private MetaExpedientService metaExpedientService;

    @RequestMapping(method = RequestMethod.GET)
    public String get(
            HttpServletRequest request,
            Model model) {

        UsuariTascaFiltreCommand command = getFiltreCommand(request);
        model.addAttribute(command);

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        String rolActual = RolHelper.getRolActual(request);

        List<MetaExpedientDto> metaExpedientDtoList = metaExpedientService.findActiusAmbEntitatPerModificacio(entitatActual.getId(), rolActual);
        model.addAttribute("metaExpedients", metaExpedientDtoList);

        if (metaExpedientDtoList.size() == 1) {
            command.setMetaExpedientId(metaExpedientDtoList.get(0).getId());
        }

        if (command.getMetaExpedientId() != null) {
            model.addAttribute("metaexpTasques",
                    findMetaExpedientTascaByMetaExpedientId(request, command.getMetaExpedientId(), null));
        }

        return "usuariTascaList";
    }

    @RequestMapping(value = "/filtrar", method = RequestMethod.POST)
    public String post(
            HttpServletRequest request,
            @Valid UsuariTascaFiltreCommand filtreCommand,
            BindingResult bindingResult,
            Model model,
            @RequestParam(value = "accio", required = false) String accio) {
        getEntitatActualComprovantPermisos(request);
        if ("netejar".equals(accio)) {
            RequestSessionHelper.esborrarObjecteSessio(
                    request,
                    SESSION_ATTRIBUTE_FILTRE);
        } else {
            if (!bindingResult.hasErrors()) {
                RequestSessionHelper.actualitzarObjecteSessio(
                        request,
                        SESSION_ATTRIBUTE_FILTRE,
                        filtreCommand);
            }
        }
        return "redirect:../usuariTasca";
    }

    @RequestMapping(value = "/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse datatable(
            HttpServletRequest request) {
        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

        UsuariTascaFiltreCommand filtreCommand = getFiltreCommand(request);

        return DatatablesHelper.getDatatableResponse(
                request,
                expedientTascaService.findAmbAuthentication(
                        entitatActual.getId(),
                        UsuariTascaFiltreCommand.asDto(filtreCommand),
                        DatatablesHelper.getPaginacioDtoFromRequest(request)));
    }

    @RequestMapping(value = "/{expedientTascaId}/iniciar", method = RequestMethod.GET)
    public String expedientTascaIniciar(
            HttpServletRequest request,
            @PathVariable Long expedientTascaId,
            @RequestParam(value = "redirectATasca", required = false) Boolean redirectATasca,
            @RequestParam(value = "origenTasques", required = false) Boolean origenTasques,
            Model model) {
        getEntitatActualComprovantPermisos(request);
        ExpedientTascaDto tasca = expedientTascaService.findOne(expedientTascaId);

        if (!TascaEstatEnumDto.INICIADA.equals(tasca.getEstat())) {
            tasca = expedientTascaService.canviarTascaEstat(
                    expedientTascaId,
                    TascaEstatEnumDto.INICIADA,
                    null,
                    RolHelper.getRolActual(request));
        }

        Long expedientId = tasca.getExpedient().getId();
        String url = "redirect:/usuariTasca";
        if (redirectATasca != null && redirectATasca) {
            url = "redirect:/contingut/" + expedientId + "?tascaId=" + expedientTascaId + "&origenTasques=" + origenTasques;
        } else if (origenTasques == null || !origenTasques) {
            url = "redirect:/contingut/" + expedientId + "#tasques";
        }
        return getAjaxControllerReturnValueSuccess(
                request,
                url,
                "expedient.tasca.controller.iniciada.ok",
                new Object[]{tasca.getTitol() != null ? tasca.getTitol() : tasca.getMetaExpedientTascaDescAbrv()});

    }


    @RequestMapping(value = "/{expedientTascaId}/rebutjar", method = RequestMethod.GET)
    public String getExpedientTascaRebutjar(
            HttpServletRequest request,
            @PathVariable Long expedientTascaId,
            Model model) {
        getEntitatActualComprovantPermisos(request);
        expedientTascaService.findOne(expedientTascaId);

        UsuariTascaRebuigCommand command = new UsuariTascaRebuigCommand();
        command.setId(expedientTascaId);
        model.addAttribute(
                "usuariTascaRebuigCommand",
                command);

        return "usuariTascaRebuigForm";
    }

    @RequestMapping(value = "/rebutjar", method = RequestMethod.POST)
    public String rebutjarPost(
            HttpServletRequest request,
            @Valid UsuariTascaRebuigCommand command,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(
                    "usuariTascaRebuigCommand",
                    command);
            return "usuariTascaRebuigForm";
        }

        ExpedientTascaDto expedientTascaDto = expedientTascaService.canviarTascaEstat(
                command.getId(),
                TascaEstatEnumDto.REBUTJADA,
                command.getMotiu(),
                RolHelper.getRolActual(request));
        return getModalControllerReturnValueSuccess(
                request,
                "redirect:/usuariTasca",
//				redirectAExpedient != null && redirectAExpedient == true ? "redirect:/contingut/" + expedientTascaDto.getExpedient().getId() + "?tascaId=" + command.getId() : "redirect:/usuariTasca",
                "expedient.tasca.controller.rebutjada.ok",
                new Object[]{expedientTascaDto.getTitol() != null ? expedientTascaDto.getTitol() : expedientTascaDto.getMetaExpedientTascaDescAbrv()});
    }


    @RequestMapping(value = "/{expedientTascaId}/finalitzar", method = RequestMethod.GET)
    public String expedientTascaFinalitzar(
            HttpServletRequest request,
            @PathVariable Long expedientTascaId,
            @RequestParam(value = "redirectATasca", required = false) Boolean redirectATasca,
            @RequestParam(value = "origenTasques", required = false) Boolean origenTasques,
            Model model) {

        getEntitatActualComprovantPermisos(request);

        ExpedientTascaDto expedientTascaDto = null;
        List<MetaExpedientTascaValidacioDto> validacionsPendents = expedientTascaService.getValidacionsPendentsTasca(expedientTascaId);

        if (validacionsPendents.size() > 0) {
            expedientTascaDto = expedientTascaService.findOne(expedientTascaId);
        } else {
            expedientTascaDto = expedientTascaService.canviarTascaEstat(
                    expedientTascaId,
                    TascaEstatEnumDto.FINALITZADA,
                    null,
                    RolHelper.getRolActual(request));
        }

        String url = "redirect:/usuariTasca";
        if (redirectATasca != null && redirectATasca) {
            url = "redirect:/contingut/" + expedientTascaDto.getExpedient().getId() + "?tascaId=" + expedientTascaId + "&origenTasques=" + origenTasques;
        } else if (origenTasques == null || !origenTasques) {
            url = "redirect:/contingut/" + expedientTascaDto.getExpedient().getId() + "#tasques";
        }

        if (validacionsPendents.size() > 0) {
            String message = getMessage(
                    request,
                    "expedient.tasca.controller.finalitzada.ko",
                    new Object[]{expedientTascaDto.getTitol() != null ? expedientTascaDto.getTitol() : expedientTascaDto.getMetaExpedientTascaDescAbrv()});

            for (MetaExpedientTascaValidacioDto validacio : validacionsPendents) {
                String itemValidacio = getMessage(request, "metaexpedient.tasca.validacio.tipus." + validacio.getItemValidacio());
                String tipusValidacio = getMessage(request, "metaexpedient.tasca.validacio.enum." + validacio.getTipusValidacio());
                message += "<br/>&nbsp;-&nbsp;" + itemValidacio + " <b>" + validacio.getItemNom() + "</b>: " + tipusValidacio;
            }

            return getAjaxControllerReturnValueErrorMessage(request, url, message, null);

        } else {

            return getAjaxControllerReturnValueSuccess(
                    request,
                    url,
                    "expedient.tasca.controller.finalitzada.ok",
                    new Object[]{expedientTascaDto.getTitol() != null ? expedientTascaDto.getTitol() : expedientTascaDto.getMetaExpedientTascaDescAbrv()});
        }
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(
                Date.class,
                new CustomDateEditor(
                        new SimpleDateFormat("dd/MM/yyyy"),
                        true));
    }

    @RequestMapping(value = "/{expedientTascaId}/delegar", method = RequestMethod.GET)
    public String delegar(
            HttpServletRequest request,
            @PathVariable Long expedientTascaId,
            Model model) {
        getEntitatActualComprovantPermisos(request);

        TascaDelegarCommand command = new TascaDelegarCommand();
        model.addAttribute(command);

        return "expedientTascaDelegar";
    }

    @RequestMapping(value = "/{expedientTascaId}/delegar", method = RequestMethod.POST)
    public String delegarPost(
            HttpServletRequest request,
            @PathVariable Long expedientTascaId,
            @Valid TascaDelegarCommand command,
            BindingResult bindingResult,
            Model model) {

        getEntitatActualComprovantPermisos(request);

        if (bindingResult.hasErrors()) {
            return "expedientTascaDelegar";
        }

        ExpedientTascaDto expedientTascaDto = expedientTascaService.updateDelegat(
                expedientTascaId,
                command.getDelegatCodi(),
                command.getComentari());

        return getModalControllerReturnValueSuccess(
                request,
                "redirect:/expedientTasca",
//				redirectAExpedient != null && redirectAExpedient == true ? "redirect:/contingut/" + expedientTascaDto.getExpedient().getId() + "?tascaId=" + expedientTascaId : "redirect:/usuariTasca",
                "expedient.tasca.controller.delegar.ok",
                new String[]{expedientTascaDto.getTitol() != null ? expedientTascaDto.getTitol() : expedientTascaDto.getMetaExpedientTascaDescAbrv(), command.getDelegatCodi()});
    }

    @RequestMapping(value = "/{expedientTascaId}/retomar", method = RequestMethod.GET)
    public String retomar(
            HttpServletRequest request,
            @PathVariable Long expedientTascaId,
            Model model) {
        getEntitatActualComprovantPermisos(request);

        TascaCancelarDelegacioCommand command = new TascaCancelarDelegacioCommand();
        model.addAttribute(command);

        return "expedientTascaCancelarDelegacio";
    }

    @RequestMapping(value = "/{expedientTascaId}/retomar", method = RequestMethod.POST)
    public String retomarPost(
            HttpServletRequest request,
            @PathVariable Long expedientTascaId,
            @Valid TascaCancelarDelegacioCommand command,
            BindingResult bindingResult,
            Model model) {

        getEntitatActualComprovantPermisos(request);

        if (bindingResult.hasErrors()) {
            return "expedientTascaCancelarDelegacio";
        }

        ExpedientTascaDto expedientTascaDto = expedientTascaService.cancelarDelegacio(
                expedientTascaId,
                command.getComentari());

        return getModalControllerReturnValueSuccess(
                request,
                "redirect:/expedientTasca",
//				redirectAExpedient != null && redirectAExpedient == true ? "redirect:/contingut/" + expedientTascaDto.getExpedient().getId() + "?tascaId=" + expedientTascaId : "redirect:/usuariTasca",
                "expedient.tasca.controller.cancelar.delegacio.ok",
                new Object[]{expedientTascaDto.getTitol() != null ? expedientTascaDto.getTitol() : expedientTascaDto.getMetaExpedientTascaDescAbrv()});
    }

    private UsuariTascaFiltreCommand getFiltreCommand(
            HttpServletRequest request) {
        UsuariTascaFiltreCommand filtreCommand = (UsuariTascaFiltreCommand) RequestSessionHelper.obtenirObjecteSessio(
                request,
                SESSION_ATTRIBUTE_FILTRE);
        if (filtreCommand == null) {
            filtreCommand = new UsuariTascaFiltreCommand();
            filtreCommand.setEstats(new TascaEstatEnumDto[]{TascaEstatEnumDto.PENDENT, TascaEstatEnumDto.INICIADA, TascaEstatEnumDto.AGAFADA});
            RequestSessionHelper.actualitzarObjecteSessio(
                    request,
                    SESSION_ATTRIBUTE_FILTRE,
                    filtreCommand);
        }
        return filtreCommand;
    }

    @RequestMapping(value = "/metaExpedientTasca/{metaExpedientId}", method = RequestMethod.GET)
    @ResponseBody
    public List<MetaExpedientTascaDto> findMetaExpedientTascaByMetaExpedientId(
            HttpServletRequest request,
            @PathVariable Long metaExpedientId,
            Model model) {
        if (metaExpedientId != null) {
            EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

            return expedientTascaService.findAmbMetaExpedient(
                    entitatActual.getId(),
                    metaExpedientId);
        }
        return new ArrayList<>();
    }

    @RequestMapping(value = "/metaExpedient/{expedientId}", method = RequestMethod.GET)
    @ResponseBody
    public MetaExpedientDto findMetaExpedientTascaByExpedientId(
            HttpServletRequest request,
            @PathVariable Long expedientId,
            Model model) {
        if (expedientId != null) {
            EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

            ExpedientDto expedient = expedientService.findById(entitatActual.getId(), expedientId, null);
            return expedient.getMetaExpedient();
        }
        return null;
    }

}