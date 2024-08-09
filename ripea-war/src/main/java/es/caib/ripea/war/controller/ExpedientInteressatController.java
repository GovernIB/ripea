package es.caib.ripea.war.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.ripea.core.api.dto.*;
import es.caib.ripea.core.api.service.ConfigService;
import es.caib.ripea.core.api.service.DadesExternesService;
import es.caib.ripea.core.api.service.ExpedientInteressatService;
import es.caib.ripea.core.api.service.UnitatOrganitzativaService;
import es.caib.ripea.war.command.InteressatCommand;
import es.caib.ripea.war.command.InteressatCommand.Administracio;
import es.caib.ripea.war.command.InteressatCommand.PersonaFisica;
import es.caib.ripea.war.command.InteressatCommand.PersonaJuridica;
import es.caib.ripea.war.command.InteressatImportCommand;
import es.caib.ripea.war.command.PinbalConsultaCommand;
import es.caib.ripea.war.helper.ExceptionHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RolHelper;
import es.caib.ripea.war.helper.ValidationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

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
	@Autowired
	private ConfigService configService;

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
		//Miram si el formulari actual, s'esta obrint desde una altra modal (PINBAL o NOTIFICACIO)
		if(request.getSession().getAttribute("ContingutPinbalController.command")!=null) {
			interessatCommand.setFormulariAnterior("ContingutPinbalController.command");
		}
		model.addAttribute("interessatCommand", interessatCommand);
		model.addAttribute("expedientId", expedientId);
		ompleModel(request, model, entitatActual.getCodi());
		return "expedientInteressatForm";
	}

	@RequestMapping(value = "/{expedientId}/interessat/exportar", method = RequestMethod.GET)
	public String exportar(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long expedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		List<InteressatDto> interessatsExp = expedientInteressatService.findByExpedient(entitatActual.getId(), expedientId, false);
		if (interessatsExp!=null && interessatsExp.size()>0) {
			try {
				//ByteArrayOutputStream baos = new ByteArrayOutputStream();
				//ObjectOutputStream oos = new ObjectOutputStream(baos);
				//oos.writeObject(interessatsExp);
				//oos.flush();
				//writeFileToResponse("interessats_expedient_"+expedientId+".dat", baos.toByteArray(), response);
				ObjectMapper objectMapper = new ObjectMapper();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				objectMapper.writerWithDefaultPrettyPrinter().writeValue(baos, interessatsExp);
				writeFileToResponse("interessats_expedient_"+expedientId+".json", baos.toByteArray(), response);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@RequestMapping(value = "/{expedientId}/interessat/importar", method = RequestMethod.GET)
	public String importar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		InteressatImportCommand iic = new InteressatImportCommand();
		iic.setExpedientId(expedientId);
		model.addAttribute(iic);
		return "interessatImportForm";
	}

	@RequestMapping(value="/{expedientId}/interessat/importar", method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@ModelAttribute InteressatImportCommand interessatImportCommand,
			Model model) {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		if ("SAVE".equals(interessatImportCommand.getAccio())) {
			List<InteressatDto> lista = new ArrayList<InteressatDto>();
			try {
				if (interessatImportCommand.getInteressatsFisica()!=null) { lista.addAll(interessatImportCommand.getInteressatsFisica()); }
				if (interessatImportCommand.getInteressatsJuridi()!=null) { lista.addAll(interessatImportCommand.getInteressatsJuridi()); }
				if (interessatImportCommand.getInteressatsAdmini()!=null) { lista.addAll(interessatImportCommand.getInteressatsAdmini()); }
				String resultat = expedientInteressatService.importarInteressats(
						getEntitatActualComprovantPermisos(request).getId(),
						expedientId,
						RolHelper.getRolActual(request),
						lista);
				MissatgesHelper.success(request, resultat);
				return modalUrlTancar();
			} catch (Exception e) {
				e.printStackTrace();
				MissatgesHelper.error(request, getMessage(request, "contingut.importar.interessats.err"), e);
			}
		} else {
            try {
				if (interessatImportCommand.getFitxerInteressats() == null || interessatImportCommand.getFitxerInteressats().getSize()==0) {
					MissatgesHelper.error(request, getMessage(request, "contingut.importar.interessats.file"));
				} else {
					List<InteressatDto> lista = objectMapper.readValue(
							interessatImportCommand.getFitxerInteressats().getInputStream(),
							new TypeReference<List<InteressatDto>>() {});
					List<InteressatDto> listaActual = expedientInteressatService.findByExpedient(
							getEntitatActualComprovantPermisos(request).getId(),
							expedientId,
							false);
					interessatImportCommand.setInteressatsFromInteressatDto(lista, listaActual);
					interessatImportCommand.setAccio("SAVE");
					model.addAttribute(interessatImportCommand);
				}
            } catch (IOException e) {
				MissatgesHelper.error(request, getMessage(request, "contingut.importar.interessats.err"), e);
            }
		}

		return "interessatImportForm";
	}

	@RequestMapping(value = "/{expedientId}/interessat/{interessatId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long interessatId,
			@RequestParam(value = "potModificar", required = false) Boolean potModificar,
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
		
		model.addAttribute("potModificar", potModificar);
		
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
			interessatDto.setId(interessat.getId());
		} else {
			expedientInteressatService.update(
					entitatActual.getId(),
					expedientId,
					interessatDto, 
					RolHelper.getRolActual(request));
			msgKey = "interessat.controller.modificat.ok";
		}

		//Sigui el create o el update, s'ha executat correctament, ara en funció del atribut formulariAnterior
		//hem de tancar modal o bé tornar al formulari original
		if ("".equals(interessatCommand.getFormulariAnterior())) {
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../../contingut/" + expedientId,
					msgKey);
		} else {
			String retornar = "contingutPinbalForm";
			omplirModelFormulari(request, expedientId, model);
			model.addAttribute("pinbalConsultaCommand", request.getSession().getAttribute("ContingutPinbalController.command"));
			model.addAttribute("interessatCreat", interessatDto.getId());
			request.getSession().removeAttribute("ContingutPinbalController.command");
			return retornar;
		}
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
			@RequestParam(value = "potModificar", required = false) Boolean potModificar,
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
		model.addAttribute("potModificar", potModificar);
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
		
		
		String msgKey = "interessat.controller.representant.afegit.ok";
		if (interessatCommand.getId() == null) {
			InteressatDto representant = expedientInteressatService.createRepresentant(
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
		expedientInteressatService.deleteRepresentant(
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

	@RequestMapping(value = "/{expedientId}/representant/{documentNum}", method = RequestMethod.GET)
	@ResponseBody
	public InteressatDto getInteressatExpedient(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String documentNum) {
		if (documentNum == null || documentNum.trim().isEmpty()) {
			return null;
		}

		InteressatDto interessatDto = expedientInteressatService.findByExpedientAndDocumentNum(documentNum, expedientId);
		return interessatDto;
	}
	
	
	@RequestMapping(value = "/{expedientId}/guardarInteressatsArxiu", method = RequestMethod.GET)
	public String guardarExpedientArxiu(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@RequestParam(value = "origin") String origin,
			Model model)  {

		Exception exception = null;
		try {
			exception = expedientInteressatService.guardarInteressatsArxiu(expedientId);
		} catch (Exception e) {
			exception = e;
		}
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
					new Object[] {msg},
					root);
			
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
		boolean dehActiu = false;
		try {
			dehActiu = Boolean.parseBoolean(configService.getConfigValue("es.caib.ripea.notificacio.enviament.deh.activa"));
		} catch (Exception e) {}
		model.addAttribute("dehActiu", dehActiu);
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientInteressatController.class);

}