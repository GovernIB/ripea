/**
 * 
 */
package es.caib.ripea.war.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonMappingException;

import es.caib.ripea.core.api.dto.ArbreDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.MetaExpedientCarpetaDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaExpedientFiltreDto;
import es.caib.ripea.core.api.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.exception.ExisteixenExpedientsEsborratsException;
import es.caib.ripea.core.api.exception.SistemaExternException;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.core.api.service.OrganGestorService;
import es.caib.ripea.war.command.MetaExpedientCommand;
import es.caib.ripea.war.command.MetaExpedientFiltreCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.EntitatHelper;
import es.caib.ripea.war.helper.ExceptionHelper;
import es.caib.ripea.war.helper.JsonResponse;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;
import es.caib.ripea.war.helper.RolHelper;

/**
 * Controlador per al manteniment de meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/metaExpedient")
public class MetaExpedientController extends BaseAdminController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "MetaExpedientController.session.filtre";

	@Autowired
	private MetaExpedientService metaExpedientService;
	@Autowired
	private OrganGestorService organGestorService;
	@Autowired
	private AplicacioService aplicacioService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		Boolean mantenirPaginacio = Boolean.parseBoolean(request.getParameter("mantenirPaginacio"));
		if (mantenirPaginacio) {
			model.addAttribute("mantenirPaginacio", true);
		} else {
			model.addAttribute("mantenirPaginacio", false);
		}
		MetaExpedientFiltreCommand command = getFiltreCommand(request);
		model.addAttribute(command);
		model.addAttribute("isRolAdminOrgan", RolHelper.isRolActualAdministradorOrgan(request));
		
		if (RolHelper.isRolActualAdministrador(request)) {
			boolean revisioActiva = metaExpedientService.isRevisioActiva();
			int count = metaExpedientService.countMetaExpedientsPendentRevisar(entitatActual.getId());
			if (revisioActiva && count > 0) {
				MissatgesHelper.info(request, "<a href=\"metaExpedientRevisio\">" + getMessage(request, "metaexpedient.revisio.admin.pendent.revisar.alerta", new Object[] {count}) + "&nbsp;<i class=\"fa fa-external-link\"></i></a>");
			}
		}
		
		model.addAttribute("isRevisioActiva", metaExpedientService.isRevisioActiva());
		
		return "metaExpedientList";
	}

	@RequestMapping(value = "/filtrar", method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid MetaExpedientFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model,
			@RequestParam(value = "accio", required = false) String accio) {
		getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
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
		return "redirect:../metaExpedient";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request, Model model) {
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		MetaExpedientFiltreCommand filtreCommand = getFiltreCommand(request);
		
		MetaExpedientFiltreDto filtreDto = filtreCommand.asDto();
		filtreDto.setRevisioEstats(new MetaExpedientRevisioEstatEnumDto[] { filtreCommand.getRevisioEstat() });
		
		PaginaDto<MetaExpedientDto> metaExps = metaExpedientService.findByEntitatOrOrganGestor(
				entitatActual.getId(),
				organActual == null ? null : organActual.getId(),
				filtreDto,
				organActual == null ? false : RolHelper.isRolActualAdministradorOrgan(request),
				DatatablesHelper.getPaginacioDtoFromRequest(request), rolActual);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(request, metaExps, "id");
		return dtr;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(HttpServletRequest request, Model model) {
		return get(request, null, model);
	}

	@RequestMapping(value = "/{metaExpedientId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		
		MetaExpedientDto metaExpedient = null;
		if (metaExpedientId != null)
			metaExpedient = comprovarAccesMetaExpedient(request, metaExpedientId);
		MetaExpedientCommand command = null;
		if (metaExpedient != null)
			command = MetaExpedientCommand.asCommand(metaExpedient);
		else
			command = new MetaExpedientCommand();
		command.setRolAdminOrgan(RolHelper.isRolActualAdministradorOrgan(request));
		model.addAttribute(command);
		command.setEntitatId(entitatActual.getId());
		boolean isCarpetesDefecte = Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.carpetes.defecte"));
		if (isCarpetesDefecte) {
			List<ArbreDto<MetaExpedientCarpetaDto>> carpetes = null;
			if (metaExpedientId != null)
				carpetes = metaExpedientService.findArbreCarpetesMetaExpedient(entitatActual.getId(), metaExpedientId);
			else
				carpetes = new ArrayList<ArbreDto<MetaExpedientCarpetaDto>>();
			model.addAttribute("carpetes", carpetes);
		}
		fillFormModel(
				request,
				metaExpedient,
				model);
		return "metaExpedientForm";
	}

	
	@RequestMapping(value = "/importMetaExpedient/{codiSia}", method = RequestMethod.GET)
	@ResponseBody
	public JsonResponse importMetaExpedient(
			HttpServletRequest request,
			@PathVariable String codiSia,
			Model model) {
		
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
			
			String codiDir3;
//			codiDir3 = "A04026978";
//			codiSia = "874212";
			
			OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
			if (RolHelper.isRolActualAdministradorOrgan(request) && organActual != null) {
				codiDir3 = organActual.getCodi();
			} else {
				codiDir3 = entitatActual.getUnitatArrel();
			}
			
			return new JsonResponse(metaExpedientService.findProcedimentByCodiSia(entitatActual.getId(), codiDir3, codiSia));
		} catch (Exception e) {
			logger.error("Error al importar metaexpedient desde ROLSAC", e);
			
			Exception sysExt = ExceptionHelper.findExceptionInstance(e, SistemaExternException.class, 3);
			if (sysExt != null) {
				return new JsonResponse(true, sysExt.getMessage());
			} else {
				return new JsonResponse(true, e.getMessage());
			}
		}
		
	}
	
	
	@RequestMapping(value = "/{metaExpedientCarpetaId}/deleteCarpeta", method = RequestMethod.GET)
	@ResponseBody
	public void deleteCarpeta(
			HttpServletRequest request, 
			@PathVariable Long metaExpedientCarpetaId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		metaExpedientService.deleteCarpetaMetaExpedient(
				entitatActual.getId(),
				metaExpedientCarpetaId);
	}

	@RequestMapping(method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid MetaExpedientCommand command,
			BindingResult bindingResult,
			Model model) throws JsonMappingException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		MetaExpedientDto dto = command.asDto();
		if (!command.isComu() && command.getOrganGestorId() == null) {
			bindingResult.rejectValue("organGestorId", "NotNull");
		}
		if (bindingResult.hasErrors()) {
			fillFormModel(request, dto, model);
			return "metaExpedientForm";
		}
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		

		if (command.getId() != null) {
			boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), command.getId());
			
			metaExpedientService.update(entitatActual.getId(), dto, rolActual);
			
			if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
				MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
			}
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:metaExpedient",
					"metaexpedient.controller.modificat.ok");
		} else {
			metaExpedientService.create(entitatActual.getId(), dto, rolActual);
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:metaExpedient",
					"metaexpedient.controller.creat.ok");
		}
	}

	@RequestMapping(value = "/{metaExpedientId}/new", method = RequestMethod.GET)
	public String getNewAmbPare(HttpServletRequest request, @PathVariable Long metaExpedientId, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		
		MetaExpedientDto metaExpedient = comprovarAccesMetaExpedient(request, metaExpedientId);
		MetaExpedientCommand command = new MetaExpedientCommand(RolHelper.isRolActualAdministradorOrgan(request));
		command.setPareId(metaExpedientId);
		command.setEntitatId(entitatActual.getId());
		model.addAttribute(command);
		fillFormModel(request, metaExpedient, model);
		if (RolHelper.isRolActualAdministrador(request)) {
			model.addAttribute("organsGestors", organGestorService.findByEntitat(entitatActual.getId()));
		} else {
			model.addAttribute(
					"organsGestors",
					organGestorService.findAccessiblesUsuariActualRolAdmin(
							entitatActual.getId(),
							EntitatHelper.getOrganGestorActual(request).getId()));
		}
		return "metaExpedientForm";
	}

	@RequestMapping(value = "/{metaExpedientId}/enable", method = RequestMethod.GET)
	public String enable(HttpServletRequest request, @PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaExpedientId);

		comprovarAccesMetaExpedient(request, metaExpedientId);
		metaExpedientService.updateActiu(
				entitatActual.getId(),
				metaExpedientId,
				true, 
				rolActual);
		
		if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
			MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
		}
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaExpedient",
				"metaexpedient.controller.activat.ok");
	}

	@RequestMapping(value = "/{metaExpedientId}/disable", method = RequestMethod.GET)
	public String disable(HttpServletRequest request, @PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaExpedientId);

		comprovarAccesMetaExpedient(request, metaExpedientId);
		metaExpedientService.updateActiu(
				entitatActual.getId(),
				metaExpedientId,
				false, 
				rolActual);
		
		if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
			MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
		}
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaExpedient",
				"metaexpedient.controller.desactivat.ok");
	}

	@RequestMapping(value = "/{metaExpedientId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, @PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		comprovarAccesMetaExpedient(request, metaExpedientId);
		try {
			metaExpedientService.delete(
					entitatActual.getId(),
					metaExpedientId);
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../../metaExpedient",
					"metaexpedient.controller.esborrat.ok");
		} catch (Exception ex) {
			if (ExceptionHelper.isExceptionOrCauseInstanceOf(ex, DataIntegrityViolationException.class) ||
					ExceptionHelper.isExceptionOrCauseInstanceOf(ex, ConstraintViolationException.class)) {
				return getAjaxControllerReturnValueError(
						request,
						"redirect:../../esborrat",
						"metaexpedient.controller.esborrar.error.fk");
			} else if (ExceptionHelper.isExceptionOrCauseInstanceOf(ex, ExisteixenExpedientsEsborratsException.class)) {
				return getAjaxControllerReturnValueError(
						request,
						"redirect:../../esborrat",
						"metaexpedient.controller.esborrar.error.fk.esborrats");
			} else {
				throw ex;
			}
		}
	}
	
	@RequestMapping(value = "/{metaExpedientId}/marcarPendentRevisio", method = RequestMethod.GET)
	public String marcarPendent(HttpServletRequest request, @PathVariable Long metaExpedientId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		comprovarAccesMetaExpedient(request, metaExpedientId);
		
		metaExpedientService.marcarPendentRevisio(
				entitatActual.getId(),
				metaExpedientId);
		
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaExpedient",
				"metaexpedient.controller.marcar.pendent.ok");
	}

	@RequestMapping(value = "/findAll", method = RequestMethod.GET)
	@ResponseBody
	public List<MetaExpedientDto> findAll(HttpServletRequest request, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrganOrRevisor(request);
		return metaExpedientService.findByEntitat(entitatActual.getId());
	}
	
	@RequestMapping(value = "/findPerLectura", method = RequestMethod.GET)
	@ResponseBody
	public List<MetaExpedientDto> findPerLectura(
			HttpServletRequest request,
			Model model) {
		
		long t0 = System.currentTimeMillis();
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		List<MetaExpedientDto> metaExpedientsPermisLectura = metaExpedientService.findActiusAmbEntitatPerLectura(
				entitatActual.getId(), 
				null, 
				rolActual);
		
		logger.debug("findPerLectura time: " + (System.currentTimeMillis() - t0) + " ms");
		return metaExpedientsPermisLectura;
	}
	
	@RequestMapping(value = "/findPerLectura/{organId}", method = RequestMethod.GET)
	@ResponseBody
	public List<MetaExpedientDto> findPerLectura(
			HttpServletRequest request,
			@PathVariable Long organId,
			Model model) {
		long t0 = System.currentTimeMillis();
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		List<MetaExpedientDto> metaExpedientsPermisLectura = metaExpedientService.findActiusAmbOrganGestorPermisLectura(
				entitatActual.getId(),
				organId, 
				null);
		logger.debug("findPerLecturaOrgan time: " + (System.currentTimeMillis() - t0) + " ms");
		return metaExpedientsPermisLectura;
	}

	private MetaExpedientFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		MetaExpedientFiltreCommand filtreCommand = (MetaExpedientFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new MetaExpedientFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}

	private void fillFormModel(HttpServletRequest request, MetaExpedientDto dto, Model model) {
		model.addAttribute("isRolAdminOrgan", RolHelper.isRolActualAdministradorOrgan(request));
		boolean hasOrganGestor = dto != null ? dto.getOrganGestor() != null : false;
		model.addAttribute("hasOrganGestor", hasOrganGestor);
		model.addAttribute("isCarpetaDefecte", Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.carpetes.defecte")));
	}
	
	
	private static final Logger logger = LoggerFactory.getLogger(MetaExpedientController.class);

}
