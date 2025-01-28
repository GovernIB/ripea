package es.caib.ripea.war.controller;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.GrupDto;
import es.caib.ripea.core.api.dto.ResultEnumDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.GrupService;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.war.command.GrupCommand;
import es.caib.ripea.war.command.GrupFiltreCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.EntitatHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;
import es.caib.ripea.war.helper.RolHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
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
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Controller
@RequestMapping("/grup")
public class GrupController extends BaseAdminController {
	
	private static final String SESSION_ATTRIBUTE_FILTRE = "GrupController.session.filtre";
	private static final String SESSION_ATTRIBUTE_SELECCIO = "GrupController.session.seleccio";

	@Autowired private GrupService grupService;
	@Autowired private AplicacioService aplicacioService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {
		getEntitatActualComprovantPermisAdminEntitatOrganOrDissenyador(request);
		GrupFiltreCommand command = getFiltreCommand(request);
		model.addAttribute(command);
		model.addAttribute("isRolAdminOrgan", RolHelper.isRolActualAdministradorOrgan(request));
		model.addAttribute("isActiveGestioPermisPerAdminOrgan", Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.procediment.gestio.permis.administrador.organ")));
		return "grupList";
	}
	
	@RequestMapping(value = "/filtrar", method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid GrupFiltreCommand filtreCommand,
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
		return "redirect:../grup";
	}	
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrganOrDissenyador(request);
        GrupFiltreCommand filtreCommand = getFiltreCommand(request);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				grupService.findByEntitat(
						entitatActual.getId(),
						null,
						DatatablesHelper.getPaginacioDtoFromRequest(request), 
						RolHelper.isRolAmbFiltreOrgan(request) ? EntitatHelper.getOrganGestorActualId(request) : null, 
						filtreCommand.asDto(), 
						ResultEnumDto.PAGE).getPagina(),
				"id",
				SESSION_ATTRIBUTE_SELECCIO);
		return dtr;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			Model model) {
		return get(request, null, model);
	}
	
	@RequestMapping(value = "/{grupId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long grupId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrganOrDissenyador(request);
		GrupDto grup = null;
		if (grupId != null)
			grup = grupService.findById(
					grupId);
		GrupCommand command = null;
		if (grup != null) {
			command = GrupCommand.asCommand(grup);
			command.setEntitatId(entitatActual.getId());
		} else {
			command = new GrupCommand();
			command.setEntitatId(entitatActual.getId());
		}
		model.addAttribute(command);
		addAttributesToModel(model, request);
		return "grupForm";
	}
	
	//Afegir els attributs al model que no son el command.
	//Funcio que es cridarà desde el get de la pagina, com desde el post (save), en cas de error.
	private void addAttributesToModel(Model model, HttpServletRequest request) {
		model.addAttribute("esAdminOrgan", RolHelper.isRolAmbFiltreOrgan(request));
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid GrupCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrganOrDissenyador(request);
		
		if (Utils.isNotEmpty(command.getCodi()) && grupService.checkIfAlreadyExistsWithCodi(entitatActual.getId(), command.getCodi(), command.getId())) {
			bindingResult.rejectValue("codi", "GrupCodiRepetit");
		}
		if (command.getOrganGestorId() == null && RolHelper.isRolAmbFiltreOrgan(request)) {
			bindingResult.rejectValue("organGestorId", "NotNull");
		}
		
		if (bindingResult.hasErrors()) {
			addAttributesToModel(model, request);
			return "grupForm";
		}
		if (command.getId() != null) {
			grupService.update(
					entitatActual.getId(), 
					GrupCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../grup",
					"grup.controller.modificat.ok",
					new Object[] { command.getCodi() });
		} else {
			grupService.create(
					entitatActual.getId(), 
					GrupCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../grup",
					"grup.controller.creat.ok",
					new Object[] { command.getCodi() });
		}
	}
	
	@RequestMapping(value = "/{grupId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long grupId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrganOrDissenyador(request);
		try {
			GrupDto grupDto = grupService.delete(
					entitatActual.getId(),
					grupId);
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../../grup",
					"grup.controller.esborrat.ok",
					new Object[] { grupDto.getCodi() });
		} catch (Exception e) {
			return getAjaxControllerReturnValueErrorMessage(
					request,
					"redirect:../../grup",
					ExceptionUtils.getRootCause(e).getMessage(),
					e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/select", method = RequestMethod.GET)
	@ResponseBody
	public int select(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) Long[] ids) {
		
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO,
					seleccio);
		}
		if (ids != null) {
			for (Long id: ids) {
				seleccio.add(id);
			}
		} else {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			GrupFiltreCommand filtreCommand = getFiltreCommand(request);
			
			seleccio.addAll(
					grupService.findByEntitat(
							entitatActual.getId(),
							null,
							null, 
							RolHelper.isRolActualAdministradorOrgan(request) ? EntitatHelper.getOrganGestorActualId(request) : null, 
							filtreCommand.asDto(), 
							ResultEnumDto.IDS).getIds());
		}
		return seleccio.size();
	}

	@RequestMapping(value = "/deselect", method = RequestMethod.GET)
	@ResponseBody
	public int deselect(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) Long[] ids) {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO,
					seleccio);
		}
		if (ids != null) {
			for (Long id: ids) {
				seleccio.remove(id);
			}
		} else {
			seleccio.clear();
		}
		return seleccio.size();
	}
	
	
    @RequestMapping(value = "/esborrar", method = RequestMethod.GET)
	public String esborrar(
			HttpServletRequest request) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = ((Set<Long>) RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO));
		
		if (seleccio == null || seleccio.isEmpty()) {
			return getModalControllerReturnValueError(
					request,
					"redirect:/grup",
					"accio.massiva.seleccio.buida",
					null);
		}
		
		int errors = 0;
		int correctes = 0;
//		Date dataInici = new Date();
//		List<ExecucioMassivaContingutDto> execucioMassivaElements = new ArrayList<>();
		
		for (Long id : seleccio) {
//			Date dataIniciElement = new Date();
			Exception exception = null;
			try {
				grupService.delete(
						entitatActual.getId(), 
						id);
			} catch (Exception ex) {
				exception = ex;
			}
			if (exception != null ) {
				log.error("Error al esborrar grup massiu", exception);
				
				errors++;
			} else {
				correctes++;
			}
			
//			execucioMassivaElements.add(
//					new ExecucioMassivaContingutDto(
//							dataIniciElement,
//							new Date(),
//							id,
//							exception));
//			
//			execucioMassivaService.saveExecucioMassiva(
//					entitatActual.getId(),
//					new ExecucioMassivaDto(
//							ExecucioMassivaTipusDto.ACTUALITZAR_ESTAT_ANOTACIONS,
//							dataInici,
//							new Date(),
//							RolHelper.getRolActual(request)),
//					execucioMassivaElements,
//					ElementTipusEnumDto.ANOTACIO);
		}
		
		if (correctes > 0){
			MissatgesHelper.success(request, getMessage(request, "massiu.esborrar.grup.ok", new Object[]{correctes}));
		} 
		if (errors > 0) {
			MissatgesHelper.error(request, getMessage(request, "massiu.esborrar.grup.error", new Object[]{errors}), null);
		} 
		
		seleccio.clear();
		RequestSessionHelper.actualitzarObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO,
				seleccio);
		
		return "redirect:../grup";
	}
    
	

	private GrupFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		GrupFiltreCommand filtreCommand = (GrupFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new GrupFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}


}
