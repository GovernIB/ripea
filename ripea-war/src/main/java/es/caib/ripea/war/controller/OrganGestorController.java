/**
 * 
 */
package es.caib.ripea.war.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.service.OrganGestorService;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.MissatgesHelper;

/**
 * Controlador per al manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/organgestor")
public class OrganGestorController extends BaseUserController {

    @Autowired
    private OrganGestorService organGestorService;

    @RequestMapping(method = RequestMethod.GET)
    public String get(HttpServletRequest request, Model model) {
        return "organGestor";
    }

    @RequestMapping(value = "/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse datatable(HttpServletRequest request) {
        PaginaDto<OrganGestorDto> organs = new PaginaDto<OrganGestorDto>();

        try {
            EntitatDto entitat = getEntitatActualComprovantPermisos(request);

            organs = organGestorService.findOrgansGestorsAmbFiltrePaginat(entitat.getId(),
                    DatatablesHelper.getPaginacioDtoFromRequest(request));
        } catch (SecurityException e) {
            MissatgesHelper.error(request,
                    getMessage(request, "notificacio.controller.entitat.cap.assignada"));
        }
        return DatatablesHelper.getDatatableResponse(request, organs, "codi");
    }

    @RequestMapping(value = "/sync/dir3", method = RequestMethod.GET)
    public String syncDir3(HttpServletRequest request) {

        EntitatDto entitat = getEntitatActualComprovantPermisos(request);
        if (entitat.getUnitatArrel() == null || entitat.getUnitatArrel().isEmpty()) {
			return getAjaxControllerReturnValueError(
					request,
					"redirect:../../organgestor",
					"L'entitat actual no té cap codi DIR3 associat");
		}
		try {
			organGestorService.syncDir3OrgansGestors(entitat.getId());
			
		} catch (Exception e) {
			return getAjaxControllerReturnValueError(
					request,
					"redirect:../../organgestor",
					e.getMessage());
		}

        return getAjaxControllerReturnValueSuccess(request, "redirect:../../organgestor",
                "organgestor.controller.update.nom.tots.ok");
    }
//
//    @RequestMapping(value = "/permis", method = RequestMethod.GET)
//    public String permisos(HttpServletRequest request, OrganGestorFiltreCommand command, Model model) {
//
//        RequestSessionHelper.actualitzarObjecteSessio(request, ORGANS_FILTRE, command);
//
//        return "organGestorPermis";
//    }
//
//    @RequestMapping(value = "/permis/datatable", method = RequestMethod.GET)
//    @ResponseBody
//    public DatatablesResponse permisDatatable(HttpServletRequest request) {
//        List<PermisOrganGestorDto> permisos = new ArrayList<PermisOrganGestorDto>();
//        try {
//            EntitatDto entitat = getEntitatActualComprovantPermisos(request);
//            permisos = organGestorService.findPermisos(entitat.getId());
//        } catch (Exception e) {
//            MissatgesHelper.error(request,
//                    getMessage(request, "notificacio.controller.entitat.cap.assignada"));
//        }
//        return DatatablesHelper.getDatatableResponse(request, permisos, "id");
//    }
//
//    @RequestMapping(value = "/permis/new", method = RequestMethod.GET)
//    public String getNew(HttpServletRequest request, Model model) {
//        return get(request, null, model);
//    }
//
//    @RequestMapping(value = "/permis/{permisId}", method = RequestMethod.GET)
//    public String get(HttpServletRequest request, @PathVariable Long permisId, Model model) {
//        EntitatDto entitat = getEntitatActualComprovantPermisos(request);
//        PermisOrganGestorDto permis = null;
//        if (permisId != null) {
//            List<PermisOrganGestorDto> permisos = organGestorService.findPermisos(entitat.getId());
//            for (PermisOrganGestorDto p : permisos) {
//                if (p.getId().equals(permisId)) {
//                    permis = p;
//                    break;
//                }
//            }
//        }
//        model.addAttribute("organsGestors", organGestorService.findByEntitat(entitat.getId()));
//        if (permis != null)
//            model.addAttribute(PermisOrganGestorCommand.asCommand(permis));
//        else
//            model.addAttribute(new PermisOrganGestorCommand());
//        return "organGestorPermisForm";
//    }
//
//    @RequestMapping(value = "/permis", method = RequestMethod.POST)
//    public String save(HttpServletRequest request, @Valid PermisOrganGestorCommand command,
//                       BindingResult bindingResult, Model model) {
//        EntitatDto entitat = getEntitatActualComprovantPermisos(request);
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("organsGestors", organGestorService.findByEntitat(entitat.getId()));
//            return "organGestorPermisForm";
//        }
//
//        organGestorService.updatePermis(command.getOrganGestorId(), PermisOrganGestorCommand.asDto(command),
//                entitat.getId());
//        return getModalControllerReturnValueSuccess(request, "redirect:permis",
//                "organgestor.controller.permis.modificat.ok");
//    }
//
//    @RequestMapping(value = "/permis/{permisId}/delete", method = RequestMethod.GET)
//    public String delete(HttpServletRequest request, @PathVariable Long permisId, Model model) {
//        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
//        PermisOrganGestorDto permis = null;
//        if (permisId != null) {
//            List<PermisOrganGestorDto> permisos = organGestorService.findPermisos(entitatActual.getId());
//            for (PermisOrganGestorDto p : permisos) {
//                if (p.getId().equals(permisId)) {
//                    permis = p;
//                    break;
//                }
//            }
//        }
//        organGestorService.deletePermis(permis.getOrganGestor().getId(), permisId, entitatActual.getId());
//        return getAjaxControllerReturnValueSuccess(request, "redirect:../../permis",
//                "entitat.controller.permis.esborrat.ok");
//    }
}
