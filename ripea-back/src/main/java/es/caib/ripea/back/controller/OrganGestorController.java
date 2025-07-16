package es.caib.ripea.back.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import es.caib.ripea.back.command.OrganGestorCommand;
import es.caib.ripea.back.command.OrganGestorFiltreCommand;
import es.caib.ripea.back.helper.DatatablesHelper;
import es.caib.ripea.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.back.helper.ExceptionHelper;
import es.caib.ripea.back.helper.MissatgesHelper;
import es.caib.ripea.back.helper.RequestSessionHelper;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.service.OrganGestorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.RequestContext;

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

	@Autowired private OrganGestorService organGestorService;

    @RequestMapping(method = RequestMethod.GET)
    public String get(HttpServletRequest request, Model model) {
    	
    	EntitatDto entitat = getEntitatActualComprovantPermisos(request);
    	OrganGestorFiltreCommand command = getFiltreCommand(request);
    	
    	List<OrganGestorDto> organsSuperior = organGestorService.findOrgansSuperiorByEntitat(entitat.getId());
    	
		model.addAttribute(command);
		model.addAttribute("organsSuperior", organsSuperior);
    	
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
			if (prediccio.isNoCanvis()) {
				return getModalControllerReturnValueSuccess(
						request,
						"redirect:../organgestor",
						"unitat.controller.synchronize.no.changes");
			}
			model.addAttribute("isFirstSincronization", prediccio.isFirstSincronization());
			model.addAttribute("splitMap", prediccio.getSplitMap().asMap());
			model.addAttribute("mergeMap", prediccio.getMergeMap().asMap());
			model.addAttribute("substMap", prediccio.getSubstMap().asMap());
			model.addAttribute("unitatsVigents", prediccio.getUnitatsVigents());
			model.addAttribute("unitatsNew", prediccio.getUnitatsNew());
			model.addAttribute("unitatsExtingides", prediccio.getUnitatsExtingides());
		} catch (Exception e) {
 			logger.error("Error al obtenir la predicció de la sincronitzacio", e);
			return getModalControllerReturnValueErrorMessageText(
					request,
					"redirect:../../organgestor",
					e.getMessage(),
					e);
		}
		return "synchronizationPrediction";
    }

	@RequestMapping(value = "/saveSynchronize", method = RequestMethod.POST)
	public String synchronizePost(HttpServletRequest request) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		try {
			organGestorService.syncDir3OrgansGestors(entitatActual, new RequestContext(request).getLocale());
		} catch (Exception e) {
			logger.error("Error al syncronitzar", e);
			return getModalControllerReturnValueErrorMessageText(
					request,
					"redirect:../../organgestor",
					getMessage(request, "unitat.controller.syncronize.ko") + e.getMessage(),
					e);
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

//		if (progresActualitzacio.getFase() == 2) {
//			ProgresActualitzacioDto progresProc = metaExpedientService.getProgresActualitzacio(entitat.getCodi());
//			if (progresProc != null && progresProc.getInfo() != null && ! progresProc.getInfo().isEmpty()) {
//				ProgresActualitzacioDto progresAcumulat = new ProgresActualitzacioDto();
//				progresAcumulat.setProgres(27 + (progresProc.getProgres() * 24 / 100));
//				progresAcumulat.getInfo().addAll(progresActualitzacio.getInfo());
//				progresAcumulat.getInfo().addAll(progresProc.getInfo());
//				logger.info("Progres actualització organs gestors fase 2: {}",  progresAcumulat.getProgres());
//				return progresAcumulat;
//			}
//		}

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
			@RequestParam(value = "redirectAOrganigrama", required = false) Boolean redirectAOrganigrama,
			Model model) throws JsonMappingException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

		if (bindingResult.hasErrors()) {
			return "organGestorForm";
		}

		if (command.getId() != null) {
			organGestorService.update(entitatActual.getId(), OrganGestorCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					redirectAOrganigrama != null && redirectAOrganigrama == true ? "redirect:organGestorOrganigrama" : "redirect:organgestor",
					"organgestor.controller.modificat.ok",
					new Object[] { command.getNom() });
		} else {
			organGestorService.create(entitatActual.getId(), OrganGestorCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:organGestor",
					"organgestor.controller.creat.ok",
					new Object[] { command.getNom() });
		}
	}

	@RequestMapping(value = "/{organGestorId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request, 
			@PathVariable Long organGestorId,
			@RequestParam(value = "redirectAOrganigrama", required = false) Boolean redirectAOrganigrama) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		try {
			String organNom = organGestorService.delete(
					entitatActual.getId(),
					organGestorId);
			return getAjaxControllerReturnValueSuccess(
					request,
					redirectAOrganigrama != null && redirectAOrganigrama == true ? "redirect:../../organGestorOrganigrama" : "redirect:../../organgestor",
					"organgestor.controller.esborrat.ok",
					new Object[] { organNom });
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
			filtreCommand.setEstat(OrganEstatEnumDto.V);
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(OrganGestorController.class);
}