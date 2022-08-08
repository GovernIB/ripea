/**
 * 
 */
package es.caib.ripea.war.controller;


import com.fasterxml.jackson.databind.JsonMappingException;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PrediccioSincronitzacio;
import es.caib.ripea.core.api.dto.ProgresActualitzacioDto;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.core.api.service.OrganGestorService;
import es.caib.ripea.war.command.OrganGestorCommand;
import es.caib.ripea.war.command.OrganGestorFiltreCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.ExceptionHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

/**
 * Controlador per al manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/organgestor")
public class OrganGestorController extends BaseUserOAdminController {
	
	private static final String SESSION_ATTRIBUTE_FILTRE = "OrganGestorController.session.filtre";
	
    @Autowired
    private OrganGestorService organGestorService;
	@Autowired
	private MetaExpedientService metaExpedientService;

    @RequestMapping(method = RequestMethod.GET)
    public String get(HttpServletRequest request, Model model) {
    	
    	EntitatDto entitat = getEntitatActualComprovantPermisos(request);
    	OrganGestorFiltreCommand command = getFiltreCommand(request);
    	
    	List<OrganGestorDto> organsSuperior = organGestorService.findOrgansSuperiorByEntitat(entitat.getId());
    	
		model.addAttribute(command);
		model.addAttribute(
				"organsSuperior",
				organsSuperior);
    	
        return "organGestor";
    }
    
	@RequestMapping(value = "/filtrar", method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid OrganGestorFiltreCommand filtreCommand,
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
		return "redirect:../organgestor";
	}
    

    @RequestMapping(value = "/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse datatable(HttpServletRequest request) {
        PaginaDto<OrganGestorDto> organs = new PaginaDto<OrganGestorDto>();

        try {
            EntitatDto entitat = getEntitatActualComprovantPermisos(request);
            
            OrganGestorFiltreCommand filtreCommand = getFiltreCommand(request);

			organs = organGestorService.findAmbFiltrePaginat(
					entitat.getId(),
					filtreCommand.asDto(),
					DatatablesHelper.getPaginacioDtoFromRequest(request));
        } catch (SecurityException e) {
        	logger.error("Error al obtenir el llistat de permisos", e);
            MissatgesHelper.error(request, getMessage(request, e.getMessage()), e);
        }
        return DatatablesHelper.getDatatableResponse(request, organs, "codi");
    }

    @RequestMapping(value = "/sync/dir3", method = RequestMethod.GET)
    public String syncDir3(HttpServletRequest request,
						   Model model) {

        EntitatDto entitat = getEntitatActualComprovantPermisos(request);
        if (entitat.getUnitatArrel() == null || entitat.getUnitatArrel().isEmpty()) {
			return getAjaxControllerReturnValueError(
					request,
					"redirect:../../organgestor",
					"L'entitat actual no té cap codi DIR3 associat",
					null);
		}
		try {

			PrediccioSincronitzacio prediccio = organGestorService.predictSyncDir3OrgansGestors(entitat.getId());

			model.addAttribute("isFirstSincronization", prediccio.isFirstSincronization());
//			model.addAttribute("unitatsVigentsFirstSincro", unitatsVigentsFirstSincro);

			model.addAttribute("splitMap", prediccio.getSplitMap());
			model.addAttribute("mergeMap", prediccio.getMergeMap());
			model.addAttribute("substMap", prediccio.getSubstMap());
			model.addAttribute("unitatsVigents", prediccio.getUnitatsVigents());
			model.addAttribute("unitatsNew", prediccio.getUnitatsNew());
			model.addAttribute("unitatsExtingides", prediccio.getUnitatsExtingides());
//			organGestorService.syncDir3OrgansGestors(entitat.getId());
			
		} catch (Exception e) {
			logger.error("Error al obtenir la predicció de la sincronitzacio", e);
			return getModalControllerReturnValueErrorMessageText(
					request,
					"redirect:../../organgestor",
					e.getMessage(),
					e);
		}

//        return getAjaxControllerReturnValueSuccess(request, "redirect:../../organgestor",
//                "organgestor.controller.update.nom.tots.ok");
		return "synchronizationPrediction";
    }

	@RequestMapping(value = "/saveSynchronize", method = RequestMethod.POST)
	public String synchronizePost(HttpServletRequest request) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		try {
			organGestorService.syncDir3OrgansGestors(entitatActual);
		} catch (Exception e) {
			logger.error("Error al syncronitzar", e);
			return getModalControllerReturnValueErrorMessageText(
					request,
					"redirect:../../organgestor",
					e.getMessage());
		}

		return getModalControllerReturnValueSuccess(
				request,
				"redirect:unitatOrganitzativa",
				"unitat.controller.synchronize.ok");
	}

	@RequestMapping(value = "/update/auto/progres", method = RequestMethod.GET)
	@ResponseBody
	public ProgresActualitzacioDto getProgresActualitzacio(HttpServletRequest request) {

		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		ProgresActualitzacioDto progresActualitzacio = organGestorService.getProgresActualitzacio(entitat.getCodi());


		if (progresActualitzacio == null) {
			logger.error("No s'ha trobat el progres actualització d'organs gestors per a l'entitat {}", entitat.getCodi());
			return new ProgresActualitzacioDto();
		}

		if (progresActualitzacio.getFase() == 2) {
			ProgresActualitzacioDto progresProc = metaExpedientService.getProgresActualitzacio(entitat.getCodi());
			if (progresProc != null && progresProc.getInfo() != null && ! progresProc.getInfo().isEmpty()) {
				ProgresActualitzacioDto progresAcumulat = new ProgresActualitzacioDto();
				progresAcumulat.setProgres(27 + (progresProc.getProgres() * 24 / 100));
				progresAcumulat.getInfo().addAll(progresActualitzacio.getInfo());
				progresAcumulat.getInfo().addAll(progresProc.getInfo());
				logger.info("Progres actualització organs gestors fase 2: {}",  progresAcumulat.getProgres());
				return progresAcumulat;
			}
		}

		logger.info("Progres actualització organs gestors fase {}: {}",progresActualitzacio.getFase(), progresActualitzacio.getProgres());
		return progresActualitzacio;
	}
    

    
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(HttpServletRequest request, Model model) {
		return get(request, null, model);
	}

	@RequestMapping(value = "/{organGestorId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long organGestorId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		OrganGestorDto organ = null;
		if (organGestorId != null)
			organ = organGestorService.findById(entitatActual.getId(), organGestorId);
		if (organ != null)
			model.addAttribute(OrganGestorCommand.asCommand(organ));
		else
			model.addAttribute(new OrganGestorCommand());

		return "organGestorForm";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid OrganGestorCommand command,
			BindingResult bindingResult,
			Model model) throws JsonMappingException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

		if (bindingResult.hasErrors()) {
			return "organGestorForm";
		}

		if (command.getId() != null) {
			organGestorService.update(entitatActual.getId(), OrganGestorCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:organGestor",
					"organgestor.controller.modificat.ok");
		} else {
			organGestorService.create(entitatActual.getId(), OrganGestorCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:organGestor",
					"organgestor.controller.creat.ok");
		}
	}

	@RequestMapping(value = "/{organGestorId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, @PathVariable Long organGestorId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		try {
			organGestorService.delete(
					entitatActual.getId(),
					organGestorId);
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../../organGestor",
					"organgestor.controller.esborrat.ok");
		} catch (Exception ex) {
			logger.error("Error al esborrar organ gestor");
			Throwable root = ExceptionHelper.getRootCauseOrItself(ex);
			if (root instanceof SQLIntegrityConstraintViolationException && root.getMessage().contains("IPA_ORGAN_GESTOR_METAEXP_FK")) {
				return getAjaxControllerReturnValueError(
						request,
						"redirect:../../esborrat",
						"organgestor.controller.esborrar.error.fk.metaexp",
						root);
			} else if (root instanceof SQLIntegrityConstraintViolationException && root.getMessage().contains("IPA_ORGAN_GESTOR_EXP_FK")) {
				return getAjaxControllerReturnValueError(
						request,
						"redirect:../../esborrat",
						"organgestor.controller.esborrar.error.fk.exp",
						root);
			} else {
				return getAjaxControllerReturnValueErrorMessage(
						request,
						"redirect:../../esborrat",
						root.getMessage(),
						root);
			}
		}
	}
    
    
	private OrganGestorFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		OrganGestorFiltreCommand filtreCommand = (OrganGestorFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new OrganGestorFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
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
	
	
	private static final Logger logger = LoggerFactory.getLogger(OrganGestorController.class);
}
