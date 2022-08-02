package es.caib.ripea.war.controller;

import com.google.common.collect.Lists;
import es.caib.ripea.core.api.dto.ContingutMassiuFiltreDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioPendentDist;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.service.ExpedientPeticioService;
import es.caib.ripea.war.command.ContingutMassiuFiltreCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/massiu/anotacionsPendentsCanviEstat")
public class MassiuCanviEstatAnotacionsPendentsDistribucioController extends BaseUserOAdminOOrganController  {

    private static final String SESSION_ATTRIBUTE_FILTRE = "MassiuCanviEstatAnotacionsPendentsDistribucioController.session.filtre";
    private static final String SESSION_ATTRIBUTE_SELECCIO_ADMIN = "ExpedientCanviEstatMassiuController.session.seleccio.admin";

    @Autowired
    private ExpedientPeticioService expedientPeticioService;

    @RequestMapping(method = RequestMethod.GET)
    public String get(HttpServletRequest request, Model model) {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        ContingutMassiuFiltreCommand filtreCommand = getFiltreCommand(request);
        model.addAttribute(filtreCommand);
        model.addAttribute("seleccio", RequestSessionHelper.obtenirObjecteSessio(request, getSessionAttributeSelecio(request)));
        model.addAttribute("metaExpedients", new ArrayList<>());
        return "anotacionsPendentsCanviEstatDistribucio";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String post(HttpServletRequest request, @Valid ContingutMassiuFiltreCommand filtreCommand, BindingResult bindingResult, Model model) {

        if (!bindingResult.hasErrors()) {
            RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
        }
        return "redirect:/massiu/anotacionsPendentsCanviEstat";
    }

    @RequestMapping(value = "/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesHelper.DatatablesResponse datatable(HttpServletRequest request) {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        ContingutMassiuFiltreCommand contingutMassiuFiltreCommand = getFiltreCommand(request);
        try {
            ContingutMassiuFiltreDto filtre = ContingutMassiuFiltreCommand.asDto(contingutMassiuFiltreCommand);
            PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
            PaginaDto<ExpedientPeticioPendentDist > pendents = expedientPeticioService.findPendentsCanviEstatAnotacioDistribucio(entitatActual.getId(), filtre, paginacioParams);
            return DatatablesHelper.getDatatableResponse(request, pendents,"id", getSessionAttributeSelecio(request));
        } catch (Exception ex) {
            throw ex;
        }
    }

    @RequestMapping(value = "/select", method = RequestMethod.GET)
    @ResponseBody
    public int select(HttpServletRequest request, @RequestParam(value="ids[]", required = false) Long[] ids) {

        Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(request, getSessionAttributeSelecio(request));
        if (seleccio == null) {
            seleccio = new HashSet<>();
            RequestSessionHelper.actualitzarObjecteSessio(request, getSessionAttributeSelecio(request), seleccio);
        }
        if (ids != null) {
            for (Long id: ids) {
                seleccio.add(id);
            }
            return seleccio.size();
        }
        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        ContingutMassiuFiltreCommand filtreCommand = getFiltreCommand(request);
        ContingutMassiuFiltreDto filtre = ContingutMassiuFiltreCommand.asDto(filtreCommand);
        seleccio.addAll(expedientPeticioService.findIdsPendentsCanviEstatAnotacioDistribucio(entitatActual.getId(), filtre));
        return seleccio.size();
    }

    @RequestMapping(value = "/deselect", method = RequestMethod.GET)
    @ResponseBody
    public int deselect(HttpServletRequest request, @RequestParam(value="ids[]", required = false) Long[] ids) {
        @SuppressWarnings("unchecked")
        Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(request, getSessionAttributeSelecio(request));
        if (seleccio == null) {
            seleccio = new HashSet<>();
            RequestSessionHelper.actualitzarObjecteSessio(request, getSessionAttributeSelecio(request), seleccio);
        }
        if (ids == null) {
            seleccio.clear();
            return 0;
        }
        for (Long id: ids) {
            seleccio.remove(id);
        }
        return seleccio.size();
    }

    @RequestMapping(value = "/canviarEstat", method = RequestMethod.GET)
    public String canviarEstat(HttpServletRequest request) {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

        @SuppressWarnings("unchecked")
        Set<Long> seleccio = ((Set<Long>) RequestSessionHelper.obtenirObjecteSessio(request, getSessionAttributeSelecio(request)));

        if (seleccio == null || seleccio.isEmpty()) {
            return getModalControllerReturnValueError(request, "redirect:/massiu/custodiar", "accio.massiva.seleccio.buida", null);
        }

        boolean ok = expedientPeticioService.canviarEstatAnotacionsDistribucio(Lists.newArrayList(seleccio));
        String msg = ok ? "massiu.canvi.estat.anotacio.distribucio.ok" : "massiu.canvi.estat.anotacio.distribucio.reintents";
        return getModalControllerReturnValueSuccess(request, "redirect:../anotacionsPendentsCanviEstat", msg);
    }


    private String getSessionAttributeSelecio(HttpServletRequest request) {

        String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
        String sessionAttribute;
        if (!rolActual.equals("IPA_ADMIN")) {
            throw new RuntimeException("No rol permitido");
        }
        sessionAttribute = SESSION_ATTRIBUTE_SELECCIO_ADMIN;
        return sessionAttribute;
    }

    private ContingutMassiuFiltreCommand getFiltreCommand(HttpServletRequest request) {

        ContingutMassiuFiltreCommand filtreCommand = (ContingutMassiuFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE);
        if (filtreCommand == null) {
            filtreCommand = new ContingutMassiuFiltreCommand();
            RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
        }
        return filtreCommand;
    }
}
