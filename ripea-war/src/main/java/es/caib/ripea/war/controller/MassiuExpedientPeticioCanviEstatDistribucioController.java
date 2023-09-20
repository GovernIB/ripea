package es.caib.ripea.war.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.ElementTipusEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaContingutDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaTipusDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioFiltreDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.ResultEnumDto;
import es.caib.ripea.core.api.service.ExecucioMassivaService;
import es.caib.ripea.core.api.service.ExpedientPeticioService;
import es.caib.ripea.war.command.ExpedientPeticioFiltreCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.ExceptionHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;
import es.caib.ripea.war.helper.RolHelper;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Controller
@RequestMapping("/massiu/expedientPeticioCanviEstatDistribucio")
public class MassiuExpedientPeticioCanviEstatDistribucioController extends BaseUserOAdminOOrganController  {

    private static final String SESSION_ATTRIBUTE_FILTRE = "MassiuCanviEstatAnotacionsPendentsDistribucioController.session.filtre";
    private static final String SESSION_ATTRIBUTE_SELECCIO_ADMIN = "ExpedientCanviEstatMassiuController.session.seleccio.admin";

    @Autowired
    private ExpedientPeticioService expedientPeticioService;
	@Autowired
	private ExecucioMassivaService execucioMassivaService;

    @RequestMapping(method = RequestMethod.GET)
    public String get(HttpServletRequest request, Model model) {

        getEntitatActualComprovantPermisos(request);
        ExpedientPeticioFiltreCommand filtreCommand = getFiltreCommand(request);
        model.addAttribute(filtreCommand);
        model.addAttribute("seleccio", RequestSessionHelper.obtenirObjecteSessio(request, getSessionAttributeSelecio(request)));
        model.addAttribute("metaExpedients", new ArrayList<>());
        return "massiuExpedientPeticioCanviEstatDistribucioList";
    }

    
	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			ExpedientPeticioFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model,
			@RequestParam(value = "accio", required = false) String accio) {

		getEntitatActualComprovantPermisos(request);
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE);
		} else {
			if (!bindingResult.hasErrors()) {
				RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
			}
		}
		return "redirect:/massiu/expedientPeticioCanviEstatDistribucio";
	}
    
    

    @RequestMapping(value = "/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse datatable(HttpServletRequest request) {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        ExpedientPeticioFiltreCommand expedientPeticioFiltreCommand = getFiltreCommand(request);
        try {
            ExpedientPeticioFiltreDto filtre = ExpedientPeticioFiltreCommand.asDto(expedientPeticioFiltreCommand);
            PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);

			return DatatablesHelper.getDatatableResponse(
					request,
					expedientPeticioService.findPendentsCanviEstatDistribucio(
							entitatActual.getId(),
							filtre,
							paginacioParams,
							ResultEnumDto.PAGE).getPagina(),
					"id",
					getSessionAttributeSelecio(request));
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
        ExpedientPeticioFiltreCommand filtreCommand = getFiltreCommand(request);
        ExpedientPeticioFiltreDto filtre = ExpedientPeticioFiltreCommand.asDto(filtreCommand);
		seleccio.addAll(
				expedientPeticioService.findPendentsCanviEstatDistribucio(
						entitatActual.getId(),
						filtre,
						null,
						ResultEnumDto.IDS).getIds());
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
	public String custodiarReintentar(
			HttpServletRequest request) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = ((Set<Long>) RequestSessionHelper.obtenirObjecteSessio(
				request,
				getSessionAttributeSelecio(request)));
		
		if (seleccio == null || seleccio.isEmpty()) {
			return getModalControllerReturnValueError(
					request,
					"redirect:/massiu/custodiar",
					"accio.massiva.seleccio.buida",
					null);
		}
		
		int errors = 0;
		int correctes = 0;
		Date dataInici = new Date();
		List<ExecucioMassivaContingutDto> execucioMassivaElements = new ArrayList<>();
		
		for (Long id : seleccio) {
			Date dataIniciElement = new Date();
			Exception exception = null;
			try {
				exception = expedientPeticioService.canviarEstatAnotacioDistribucio(
						entitatActual.getId(), 
						id);
			} catch (Exception ex) {
				exception = ex;
			}
			if (exception != null ) {
				log.error("Error al canviar estadó de anotació en Distribució", exception);
				
				ExpedientPeticioDto expedientPeticio = expedientPeticioService.findOne(id);
				MissatgesHelper.error(
						request,
						getMessage(
								request,
								"massiu.canvi.estat.anotacio.distribucio.error",
								new Object[] { expedientPeticio.getIdentificador(), ExceptionHelper.getRootCauseOrItself(exception).getMessage() }),
						exception);
				
				errors++;
			} else {
				correctes++;
			}
			
			execucioMassivaElements.add(
					new ExecucioMassivaContingutDto(
							dataIniciElement,
							new Date(),
							id,
							exception));
			
			execucioMassivaService.saveExecucioMassiva(
					entitatActual.getId(),
					new ExecucioMassivaDto(
							ExecucioMassivaTipusDto.ACTUALITZAR_ESTAT_ANOTACIONS,
							dataInici,
							new Date(),
							RolHelper.getRolActual(request)),
					execucioMassivaElements,
					ElementTipusEnumDto.ANOTACIO);
		}
		
		if (correctes > 0){
			MissatgesHelper.success(request, getMessage(request, "massiu.canvi.estat.anotacio.distribucio.ok", new Object[]{correctes}));
		} 
		if (errors > 0) {
			MissatgesHelper.error(request, getMessage(request, "massiu.canvi.estat.anotacio.distribucio.errors", new Object[]{errors}), null);
		} 
		
		seleccio.clear();
		RequestSessionHelper.actualitzarObjecteSessio(
				request,
				getSessionAttributeSelecio(request),
				seleccio);
		
		return "redirect:../expedientPeticioCanviEstatDistribucio";
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

    private ExpedientPeticioFiltreCommand getFiltreCommand(HttpServletRequest request) {

        ExpedientPeticioFiltreCommand filtreCommand = (ExpedientPeticioFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE);
        if (filtreCommand == null) {
            filtreCommand = new ExpedientPeticioFiltreCommand();
            filtreCommand.setNomesPendentEnviarDistribucio(true);
            RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
        }
        return filtreCommand;
    }
}
