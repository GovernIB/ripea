package es.caib.ripea.war.controller;

import es.caib.ripea.core.api.dto.ContingutMassiuFiltreDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientEstatDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioPendentDist;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.service.ExpedientPeticioService;
import es.caib.ripea.war.command.ContingutMassiuFiltreCommand;
import es.caib.ripea.war.command.ExpedientMassiuCanviEstatCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
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
        String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
//		boolean checkPerMassiuAdmin = false;
//		if (rolActual.equals("IPA_ADMIN") || rolActual.equals("IPA_ORGAN_ADMIN")) {
//			checkPerMassiuAdmin = true;
//		}
        model.addAttribute("metaExpedients", new ArrayList<>());
        return "anotacionsPendentsCanviEstatDistribucio";
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

    @RequestMapping(value = "/canviarEstat", method = RequestMethod.POST)
    public String canviarEstat(HttpServletRequest request, Model model) {

        model.addAttribute("mantenirPaginacio", true);
        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(request, getSessionAttributeSelecio(request));
        String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
        boolean checkPerMassiuAdmin = false;
        if (rolActual.equals("IPA_ADMIN") || rolActual.equals("IPA_ORGAN_ADMIN")) {
            checkPerMassiuAdmin = true;
        }

//        for (Long expedientId : seleccio) {
//            expedientEstatService.changeEstatOfExpedient(
//                    entitatActual.getId(),
//                    expedientId,
//                    command.getExpedientEstatId(),
//                    checkPerMassiuAdmin
//            );
//        }
        return getModalControllerReturnValueSuccess(request, "redirect:../expedient", "expedient.controller.estatsModificats.ok");
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
