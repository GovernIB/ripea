/**
 * 
 */
package es.caib.ripea.war.controller;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.MunicipiDto;
import es.caib.ripea.core.api.dto.ProvinciaDto;
import es.caib.ripea.core.api.dto.UnitatOrganitzativaDto;
import es.caib.ripea.core.api.service.DadesExternesService;
import es.caib.ripea.core.api.service.ExpedientInteressatService;
import es.caib.ripea.core.api.service.UnitatOrganitzativaService;
import es.caib.ripea.war.command.InteressatCommand;
import es.caib.ripea.war.command.InteressatCommand.Administracio;
import es.caib.ripea.war.command.InteressatCommand.PersonaFisica;
import es.caib.ripea.war.command.InteressatCommand.PersonaJuridica;
import es.caib.ripea.war.helper.ExceptionHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RolHelper;
import es.caib.ripea.war.helper.ValidationHelper;

/**
 * Controlador per als interessats dels expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/expedient")
public class ExpedientInteressatController extends BaseUserOAdminOOrganController {

	@Autowired
	private ExpedientInteressatService expedientInteressatService;
	@Autowired
	private UnitatOrganitzativaService unitatOrganitzativaService;
	@Autowired
	private DadesExternesService dadesExternesService;

	@Autowired(required = true)
	private javax.validation.Validator validator;

	@RequestMapping(value = "/{expedientId}/interessat/new", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		InteressatCommand interessatCommand = new InteressatCommand();
		interessatCommand.setEntitatId(entitatActual.getId());
		interessatCommand.setPais("724");
		interessatCommand.setExpedientId(expedientId);
		model.addAttribute("interessatCommand", interessatCommand);
		model.addAttribute("expedientId", expedientId);
		ompleModel(request, model, entitatActual.getCodi());
		return "expedientInteressatForm";
	}

	@RequestMapping(value = "/{expedientId}/interessat/{interessatId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long interessatId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		InteressatDto interessatDto = expedientInteressatService.findById(interessatId, false); 
		InteressatCommand interessatCommand = InteressatCommand.asCommand(interessatDto);
		interessatCommand.setEntitatId(entitatActual.getId());
		model.addAttribute("interessatCommand", interessatCommand);
		model.addAttribute("expedientId", expedientId);
		ompleModel(request, model, entitatActual.getCodi());
		if (interessatDto.getProvincia() != null) {
			model.addAttribute("municipis", dadesExternesService.findMunicipisPerProvincia(interessatDto.getProvincia()));
		}
		if (interessatDto.isAdministracio()) {
			List<UnitatOrganitzativaDto> unitats = new ArrayList<UnitatOrganitzativaDto>();
			try {
				UnitatOrganitzativaDto unitat = unitatOrganitzativaService.findByCodi(
						interessatCommand.getOrganCodi());
				unitats.add(unitat);
			} catch (Exception e) {
				MissatgesHelper.warning(request, getMessage(request, "interessat.controller.unitat.error"));
			}
			model.addAttribute("unitatsOrganitzatives", unitats);
		}
		return "expedientInteressatForm";
	}
	
	@RequestMapping(value="/{expedientId}/interessat", method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@ModelAttribute InteressatCommand interessatCommand,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		interessatCommand.setNotificacioAutoritzat(true);
		interessatCommand.setIncapacitat(false);
		
		List<Class<?>> grups = new ArrayList<Class<?>>();
		if (interessatCommand.getTipus() != null) {
			switch (interessatCommand.getTipus()) {
			case PERSONA_FISICA:
				grups.add(PersonaFisica.class);
				break;
			case PERSONA_JURIDICA:
				grups.add(PersonaJuridica.class);
				break;
			case ADMINISTRACIO:
				grups.add(Administracio.class);
				break;
			}
		}
		new ValidationHelper(validator).isValid(
				interessatCommand,
				bindingResult,
				grups.toArray(new Class[grups.size()]));
		
		if (bindingResult.hasErrors()) {
			model.addAttribute("expedientId", expedientId);
			ompleModel(request, model, entitatActual.getCodi());
			if (interessatCommand.getProvincia() != null) {
				model.addAttribute("municipis", dadesExternesService.findMunicipisPerProvincia(interessatCommand.getProvincia()));
			}
			model.addAttribute("interessatCommand", interessatCommand);
			model.addAttribute("netejar", false);
			return "expedientInteressatForm";
		}
		
		InteressatDto interessatDto = null;
		switch (interessatCommand.getTipus()) {
		case PERSONA_FISICA:
			interessatDto = InteressatCommand.asPersonaFisicaDto(interessatCommand);
			break;
		case PERSONA_JURIDICA:
			interessatDto = InteressatCommand.asPersonaJuridicaDto(interessatCommand);
			break;
		case ADMINISTRACIO:
			interessatDto = InteressatCommand.asAdministracioDto(interessatCommand);
			break;
		}
		
		String msgKey = "interessat.controller.afegit.ok";
		if (interessatCommand.getId() == null) {
			InteressatDto interessat = expedientInteressatService.create(
					entitatActual.getId(),
					expedientId,
					interessatDto, 
					RolHelper.getRolActual(request));	
			if (!interessat.isArxiuPropagat()) {
				return getModalControllerReturnValueWarning(
						request,
						"redirect:../../../contingut/" + expedientId,
						"interessat.controller.creat.error.arxiu",
						null);
			}
			
		} else {
			expedientInteressatService.update(
					entitatActual.getId(),
					expedientId,
					interessatDto, 
					RolHelper.getRolActual(request));
			msgKey = "interessat.controller.modificat.ok";
		}
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../../contingut/" + expedientId,
				msgKey);
	}

	@RequestMapping(value = "/{expedientId}/interessat/{interessatId}/delete", method = RequestMethod.GET)
	public String deleteFromExpedient(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long interessatId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		expedientInteressatService.delete(
				entitatActual.getId(),
				expedientId,
				interessatId, 
				RolHelper.getRolActual(request));
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../../contingut/" + expedientId,
				"interessat.controller.eliminat.ok");
	}
	
	@RequestMapping(value = "/interessat/{interessatId}", method = RequestMethod.GET)
	@ResponseBody
	public InteressatDto getInteressat(
			HttpServletRequest request,
			@PathVariable Long interessatId,
			@RequestParam(value = "dadesExternes", defaultValue = "false") boolean dadesExternes,
			Model model) {
		InteressatDto interessatDto = expedientInteressatService.findById(interessatId, dadesExternes);
		return interessatDto;
	}
	
	// REPRESENTANT
	//////////////////////////////////////////////////////////////////
	
	@RequestMapping(value = "/{expedientId}/interessat/{interessatId}/representant/new", method = RequestMethod.GET)
	public String getRepresentant(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long interessatId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		InteressatCommand interessatCommand = new InteressatCommand();
		interessatCommand.setEntitatId(entitatActual.getId());
		interessatCommand.setPais("724");
		model.addAttribute("interessatCommand", interessatCommand);
		model.addAttribute("expedientId", expedientId);
		model.addAttribute("esRepresentant", true);
		model.addAttribute("interessatId", interessatId);
		ompleModel(request, model, entitatActual.getCodi());
		return "expedientInteressatForm";
	}

	@RequestMapping(value = "/{expedientId}/interessat/{interessatId}/representant/{representantId}", method = RequestMethod.GET)
	public String getRepresentant(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long interessatId,
			@PathVariable Long representantId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		InteressatDto representantDto = expedientInteressatService.findRepresentantById(
				interessatId, 
				representantId);
//		InteressatDto representantDto = interessatService.findById(entitatActual.getId(), representantId);
		InteressatCommand interessatCommand = InteressatCommand.asCommand(representantDto);
		interessatCommand.setEntitatId(entitatActual.getId());
		model.addAttribute("interessatCommand", interessatCommand);
		model.addAttribute("expedientId", expedientId);
		model.addAttribute("esRepresentant", true);
		model.addAttribute("interessatId", interessatId);
		ompleModel(request, model, entitatActual.getCodi());
		if (representantDto.getProvincia() != null) {
			model.addAttribute("municipis", dadesExternesService.findMunicipisPerProvincia(representantDto.getProvincia()));
		}
		return "expedientInteressatForm";
	}
	
	@RequestMapping(value="/{expedientId}/interessat/{interessatId}/representant", method = RequestMethod.POST)
	public String postRepresentant(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long interessatId,
			@ModelAttribute InteressatCommand interessatCommand,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		interessatCommand.setExpedientId(expedientId);
		List<Class<?>> grups = new ArrayList<Class<?>>();
		if (interessatCommand.getTipus() != null) {
			switch (interessatCommand.getTipus()) {
			case PERSONA_FISICA:
				grups.add(PersonaFisica.class);
				break;
			case PERSONA_JURIDICA:
				grups.add(PersonaJuridica.class);
				break;
			case ADMINISTRACIO:
				grups.add(Administracio.class);
				break;
			}
		}
		new ValidationHelper(validator).isValid(
				interessatCommand,
				bindingResult,
				grups.toArray(new Class[grups.size()]));
		
		if (bindingResult.hasErrors()) {
			model.addAttribute("expedientId", expedientId);
			ompleModel(request, model, entitatActual.getCodi());
			if (interessatCommand.getProvincia() != null) {
				model.addAttribute(
						"municipis",
						dadesExternesService.findMunicipisPerProvincia(
								interessatCommand.getProvincia()));
			}
			model.addAttribute("esRepresentant", true);
			model.addAttribute("interessatId", interessatId);
			model.addAttribute("interessatCommand", interessatCommand);
			model.addAttribute("netejar", false);
			return "expedientInteressatForm";
		}
		
		InteressatDto representantDto = null;
		switch (interessatCommand.getTipus()) {
		case PERSONA_FISICA:
			representantDto = InteressatCommand.asPersonaFisicaDto(interessatCommand);
			break;
		case PERSONA_JURIDICA:
			representantDto = InteressatCommand.asPersonaJuridicaDto(interessatCommand);
			break;
		case ADMINISTRACIO:
			representantDto = InteressatCommand.asAdministracioDto(interessatCommand);
			break;
		}
		
		representantDto.setNotificacioAutoritzat(true);
		
		String msgKey = "interessat.controller.representant.afegit.ok";
		if (interessatCommand.getId() == null) {
			InteressatDto representant = expedientInteressatService.create(
					entitatActual.getId(),
					expedientId,
					interessatId,
					representantDto,
					true, 
					RolHelper.getRolActual(request));	
			
			if (!representant.isArxiuPropagat()) {
				return getModalControllerReturnValueWarning(
						request,
						"redirect:../../../contingut/" + expedientId,
						"interessat.controller.creat.error.arxiu.representant",
						null);
			}
			
		} else {
			expedientInteressatService.update(
					entitatActual.getId(),
					expedientId,
					interessatId,
					representantDto, 
					RolHelper.getRolActual(request));
			msgKey = "interessat.controller.representant.modificat.ok";
		}
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../contenidor/" + expedientId,
				msgKey);
	}

	@RequestMapping(value = "/{expedientId}/interessat/{interessatId}/representant/{representantId}/delete", method = RequestMethod.GET)
	public String deleteRepresentant(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long interessatId,
			@PathVariable Long representantId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		expedientInteressatService.delete(
				entitatActual.getId(),
				expedientId,
				interessatId,
				representantId, 
				RolHelper.getRolActual(request));
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../../../../../contenidor/" + expedientId,
				"interessat.controller.representant.eliminat.ok");
		
	}
	
	
	@RequestMapping(value = "/{expedientId}/guardarInteressatsArxiu", method = RequestMethod.GET)
	public String guardarExpedientArxiu(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@RequestParam(value = "origin") String origin,
			Model model)  {

		Exception exception = expedientInteressatService.guardarInteressatsArxiu(expedientId);
		String redirect = null;
		if (origin.equals("docDetail")) {
			redirect = "redirect:../../contingut/" + expedientId + "#interessats";
		} else if (origin.equals("seguiment")) {
			redirect = "redirect:../../seguimentArxiuPendents#interessats";
		}
		if (exception == null) {
			return getModalControllerReturnValueSuccess(
					request,
					redirect,
					"interessat.controller.guardar.arxiu.ok");
		} else {
			logger.error("Error guardant document en arxiu", exception);
			
			Throwable root = ExceptionHelper.getRootCauseOrItself(exception);
			String msg = null;
			if (root instanceof ConnectException || root.getMessage().contains("timed out")) {
				msg = getMessage(request,"error.arxiu.connectTimedOut");
			} else {
				msg = ExceptionHelper.getRootCauseOrItself(exception).getMessage();
			}
			return getAjaxControllerReturnValueError(
					request,
					redirect,
					"interessat.controller.guardar.arxiu.error",
					new Object[] {msg});
			
		}
	}
	
	
	@RequestMapping(value = "/organ/{codi}", method = RequestMethod.GET)
	@ResponseBody
	public UnitatOrganitzativaDto getByCodi(
			HttpServletRequest request,
			@PathVariable String codi,
			Model model) {
		return unitatOrganitzativaService.findByCodi(codi);
	}
	
	@RequestMapping(value = "/provincies/{codiComunitat}", method = RequestMethod.GET)
	@ResponseBody
	public List<ProvinciaDto> getProvincieByCodiComunitat(
			HttpServletRequest request,
			@PathVariable String codiComunitat,
			Model model) {
		return dadesExternesService.findProvinciesPerComunitat(codiComunitat);
	}
	
	@RequestMapping(value = "/municipis/{codiProvincia}", method = RequestMethod.GET)
	@ResponseBody
	public List<MunicipiDto> getMunicipisByCodiProvincia(
			HttpServletRequest request,
			@PathVariable String codiProvincia,
			Model model) {
		return dadesExternesService.findMunicipisPerProvincia(codiProvincia);
	}
	
	@RequestMapping(value = "/organs", method = RequestMethod.GET)
	@ResponseBody
	public List<UnitatOrganitzativaDto> getOrgansEntitat(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return unitatOrganitzativaService.findByEntitat(entitatActual.getCodi());
	}
	
	@RequestMapping(value = "/organ/filtre", method = RequestMethod.POST)
	@ResponseBody
	public List<UnitatOrganitzativaDto> getOrgansFiltrats(
			HttpServletRequest request,
			@RequestParam(value = "codiDir3", required = false) String codiDir3,
			@RequestParam(value = "denominacio", required = false) String denominacio,
			@RequestParam(value = "nivellAdm", required = false) String nivellAdm,
			@RequestParam(value = "comunitat", required = false) String comunitat,
			@RequestParam(value = "provincia", required = false) String provincia,
			@RequestParam(value = "localitat", required = false) String localitat,
			@RequestParam(value = "arrel", required = false) String arrel,
			Model model) {
		return unitatOrganitzativaService.findByFiltre(
				codiDir3,
				denominacio,
				nivellAdm,
				comunitat,
				provincia,
				localitat,
				"true".equals(arrel));
	}



	private void ompleModel(HttpServletRequest request, Model model, String entitatActualCodi) {
		try {
			model.addAttribute("paisos", dadesExternesService.findPaisos());
		} catch (Exception e) {
			MissatgesHelper.warning(request, getMessage(request, "interessat.controller.paisos.error"));
		}
		try {
			model.addAttribute("comunitats", dadesExternesService.findComunitats());
		} catch (Exception e) {
			MissatgesHelper.warning(request, getMessage(request, "interessat.controller.comunitats.error"));
		}
		try {
			model.addAttribute("provincies", dadesExternesService.findProvincies());
		} catch (Exception e) {
			MissatgesHelper.warning(request, getMessage(request, "interessat.controller.provincies.error"));
		}
		try {
			model.addAttribute("nivellAdministracions", dadesExternesService.findNivellAdministracions());
		} catch (Exception e) {
			MissatgesHelper.warning(request, getMessage(request, "interessat.controller.nivell.administracio.error"));
		}
	}
	private static final Logger logger = LoggerFactory.getLogger(ExpedientInteressatController.class);

}
