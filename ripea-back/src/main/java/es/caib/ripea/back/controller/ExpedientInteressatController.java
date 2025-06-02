package es.caib.ripea.back.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.ripea.back.command.DocumentNotificacionsCommand;
import es.caib.ripea.back.command.InteressatCommand;
import es.caib.ripea.back.command.InteressatCommand.Administracio;
import es.caib.ripea.back.command.InteressatCommand.PersonaFisica;
import es.caib.ripea.back.command.InteressatCommand.PersonaJuridica;
import es.caib.ripea.back.command.InteressatCommand.Repres;
import es.caib.ripea.back.command.InteressatExportCommand;
import es.caib.ripea.back.command.InteressatImportCommand;
import es.caib.ripea.back.helper.ExceptionHelper;
import es.caib.ripea.back.helper.MissatgesHelper;
import es.caib.ripea.back.helper.RolHelper;
import es.caib.ripea.back.helper.ValidationHelper;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.InteressatDto;
import es.caib.ripea.service.intf.dto.InteressatImportacioTipusDto;
import es.caib.ripea.service.intf.dto.InteressatTipusEnumDto;
import es.caib.ripea.service.intf.dto.MunicipiDto;
import es.caib.ripea.service.intf.dto.ProvinciaDto;
import es.caib.ripea.service.intf.dto.UnitatOrganitzativaDto;
import es.caib.ripea.service.intf.service.ConfigService;
import es.caib.ripea.service.intf.service.DadesExternesService;
import es.caib.ripea.service.intf.service.ExpedientInteressatService;
import es.caib.ripea.service.intf.service.UnitatOrganitzativaService;

@Controller
@RequestMapping("/expedient")
public class ExpedientInteressatController extends BaseUserOAdminOOrganController {

	@Autowired private ExpedientInteressatService expedientInteressatService;
	@Autowired private UnitatOrganitzativaService unitatOrganitzativaService;
	@Autowired private DadesExternesService dadesExternesService;
	@Autowired private ConfigService configService;
	@Autowired(required = true) private javax.validation.Validator validator;

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
		} else if(request.getSession().getAttribute("DocumentEnviamentController.command")!=null) {
            interessatCommand.setFormulariAnterior("DocumentEnviamentController.command");
        }
		model.addAttribute("interessatCommand", interessatCommand);
		model.addAttribute("expedientId", expedientId);
		ompleModel(request, model, entitatActual.getCodi());
		return "expedientInteressatForm";
	}

	@RequestMapping(value = "/{expedientId}/interessat/exportar", method = RequestMethod.GET)
	public String exportarGet(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long expedientId,
			Model model) {
		InteressatExportCommand iec = new InteressatExportCommand();
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		iec.setInteressatsFromInteressatDto(expedientInteressatService.findByExpedient(entitatActual.getId(), expedientId, false));
		model.addAttribute(iec);
		return "interessatExportForm";
	}
	
	@RequestMapping(value = "/{expedientId}/interessat/exportar", method = RequestMethod.POST)
	public String exportarPost(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long expedientId,
			@ModelAttribute InteressatExportCommand interessatExportCommand,
			Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		List<InteressatDto> interessatsExp = expedientInteressatService.findByExpedient(entitatActual.getId(), expedientId, false);
		List<Long> interessatsSeleccionats = interessatExportCommand.getSeleccionats();
//		List<InteressatDto> representants = new ArrayList<InteressatDto>();
		List<InteressatDto> interessatsExportar = new ArrayList<InteressatDto>();

		if (interessatsExp!=null && interessatsExp.size()>0) {
			for (InteressatDto iDto: interessatsExp) {
				//Ens quedam només amb els seleccionats
				if (interessatsSeleccionats.contains(iDto.getId())) {
					interessatsExportar.add(iDto);
				}
			}
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.writerWithDefaultPrettyPrinter().writeValue(baos, interessatsExportar);
				request.getSession().setAttribute("DOCUMENT_EXPORT_INTERESSATS_"+expedientId, baos.toByteArray());
			} catch (Exception e) {
				MissatgesHelper.error(request, getMessage(request, "contingut.exportar.interessats.err"), e);
			} finally {
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return modalUrlTancar();
	}
	
	@RequestMapping(value = "/{expedientId}/interessat/descarregarDocumentExport", method = RequestMethod.GET)
	public void exportarPost(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long expedientId) {
		try {
			writeFileToResponse("Interessats_expedient_"+expedientId+".json", (byte[])request.getSession().getAttribute("DOCUMENT_EXPORT_INTERESSATS_"+expedientId), response);
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	@SuppressWarnings("unchecked")
	@RequestMapping(value="/{expedientId}/interessat/importar", method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@ModelAttribute InteressatImportCommand interessatImportCommand,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		if ("SAVE".equals(interessatImportCommand.getAccio())) {
			try {
				List<InteressatDto> lista = (List<InteressatDto>)request.getSession().getAttribute("FITXER_IMPORT_INTERESSATS");
				String resultat = expedientInteressatService.importarInteressats(
						getEntitatActualComprovantPermisos(request).getId(),
						expedientId,
						RolHelper.getRolActual(request),
						lista,
						interessatImportCommand.getSeleccionats());
				
				MissatgesHelper.success(request, resultat);				
				
			} catch (Exception e) {
				e.printStackTrace();
				MissatgesHelper.error(request, getMessage(request, "contingut.importar.interessats.err"), e);
			}
			return modalUrlTancar();
		} else {
            try {
            	List<InteressatDto> lista = null;
            	InteressatImportacioTipusDto fitxerTipus = interessatImportCommand.getTipus();
            	MultipartFile fitxer = interessatImportCommand.getFitxerInteressats();
            	
				if (fitxer == null || fitxer.getSize()==0) {
					MissatgesHelper.error(request, getMessage(request, "contingut.importar.interessats.file"));
				} else {
					String fitxerNom = fitxer.getOriginalFilename();
					
					if (!esTipusFitxerValid(fitxerNom, fitxerTipus)) {
						MissatgesHelper.error(request, getMessage(request, "contingut.importar.interessats.tipus.error"));
						return "interessatImportForm";
					}
					
					if (fitxerTipus.equals(InteressatImportacioTipusDto.JSON)) {
						lista = objectMapper.readValue(
								interessatImportCommand.getFitxerInteressats().getInputStream(),
								new TypeReference<List<InteressatDto>>() {});
					} else if (fitxerTipus.equals(InteressatImportacioTipusDto.EXCEL)) {
						lista = expedientInteressatService.extreureInteressatsExcel(fitxer.getInputStream());
						
						StringBuilder validacions = validarLlistaInteressats(
								lista, 
								entitatActual.getId(), 
								expedientId);
						
						if (validacions.length() > 0) {
					        MissatgesHelper.error(request, validacions.toString());
					        return "interessatImportForm";
					    }
					}
					
					List<InteressatDto> listaActual = expedientInteressatService.findByExpedient(
							getEntitatActualComprovantPermisos(request).getId(),
							expedientId,
							false);
					interessatImportCommand.setInteressatsFromInteressatDto(lista, listaActual);
					interessatImportCommand.setAccio("SAVE");
					model.addAttribute(interessatImportCommand);
					request.getSession().setAttribute("FITXER_IMPORT_INTERESSATS", lista);
				}
            } catch (Exception e) {
				MissatgesHelper.error(request, getMessage(request, "contingut.importar.interessats.err"), e);
            }
		}

		return "interessatImportForm";
	}

    @GetMapping(value = "/getModelDadesInteressatsExcel")
    @ResponseBody
    public void getModelDadesInteressatsExcel(HttpServletResponse response) throws IOException {
        response.setHeader("Set-cookie", "contentLoaded=true; path=/");
        try {
            writeFileToResponse("model_dades_interessats.xlsx", expedientInteressatService.getModelDadesInteressatsExcel(), response);
        } catch (Exception ex) {
            logger.debug("Error al obtenir la plantilla de el model de dades CSV de càrrega massiva", ex);
        }
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
			Model model) throws JsonProcessingException {
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
			request.getSession().setAttribute(MissatgesHelper.SESSION_ATTRIBUTE_BINDING_ERRORS, bindingResult.getGlobalErrors());
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

		InteressatDto interessatCreat = null;
		String msgKey = "interessat.controller.afegit.ok";
		if (interessatCommand.getId() == null) {
			interessatCreat = expedientInteressatService.create(
					entitatActual.getId(),
					expedientId,
					interessatDto, 
					RolHelper.getRolActual(request));
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
		if ("ContingutPinbalController.command".equals(interessatCommand.getFormulariAnterior())) {
			if (interessatCreat!=null && !interessatCreat.isArxiuPropagat()) {
				MissatgesHelper.warning(request, getMessage(request, "interessat.controller.creat.error.arxiu", null));
			}
			String retornar = "contingutPinbalForm";
			omplirModelFormulari(request, expedientId, model);
			model.addAttribute("pinbalConsultaCommand", request.getSession().getAttribute("ContingutPinbalController.command"));
			model.addAttribute("interessatCreat", interessatCreat.getId());
			request.getSession().removeAttribute("ContingutPinbalController.command");
			return retornar;
        } else if ("DocumentEnviamentController.command".equals(interessatCommand.getFormulariAnterior())) {
			if (interessatCreat!=null && !interessatCreat.isArxiuPropagat()) {
				MissatgesHelper.warning(request, getMessage(request, "interessat.controller.creat.error.arxiu", null));
			}
            String retornar = "notificacioForm";
			DocumentNotificacionsCommand documentNotificacionsCommand = (DocumentNotificacionsCommand)request.getSession().getAttribute("DocumentEnviamentController.command");
			model.addAttribute("documentNotificacionsCommand", documentNotificacionsCommand);
			emplenarModelNotificacio(
					request,
					getEntitatActualComprovantPermisos(request),
					documentNotificacionsCommand.getDocumentId(),
					documentNotificacionsCommand,
					model, null);
            model.addAttribute("interessatCreat", interessatCreat.getId());
            request.getSession().removeAttribute("DocumentEnviamentController.command");
            return retornar;
            
        } else {
        	
        	//Només en cas de trobar-nos al formulari de interessat, retornarm aquest warning. La modal de notificació ja en té un parescut.
    		if (interessatDto.getTipus()!=null && interessatDto.getTipus().equals(InteressatTipusEnumDto.PERSONA_FISICA) &&
				interessatDto.getDocumentTipus()!=null && !InteressatDocumentTipusEnumDto.isNotificableTelematic(interessatDto.getDocumentTipus())) {
				MissatgesHelper.warning(request, getMessage(request, "interessat.controller.creat.warn.notib", null));
			}
        	
			if (interessatCreat!=null && !interessatCreat.isArxiuPropagat()) {
				return getModalControllerReturnValueWarning(
						request,
						"redirect:../../../contingut/" + expedientId,
						"interessat.controller.creat.error.arxiu",
						null);
			} else {
				return getModalControllerReturnValueSuccess(
						request,
						"redirect:../../../contingut/" + expedientId,
						msgKey,
						new Object[] { interessatCommand.getDocumentNum() });
			}
        }
	}

	@RequestMapping(value = "/{expedientId}/interessat/{interessatId}/delete", method = RequestMethod.GET)
	public String deleteFromExpedient(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long interessatId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		InteressatDto interessatDto = expedientInteressatService.findById(interessatId, false);
		expedientInteressatService.delete(
				entitatActual.getId(),
				expedientId,
				interessatId, 
				RolHelper.getRolActual(request));
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../../contingut/" + expedientId,
				"interessat.controller.eliminat.ok",
				new Object[] { interessatDto.getNomComplet() });
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
		//interessatId = Interessat al qual es representa
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		InteressatCommand interessatCommand = new InteressatCommand();
		interessatCommand.setEntitatId(entitatActual.getId());
		interessatCommand.setInteressatId(interessatId);
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
		//interessatId		= Interessat al qual es representa
		//representantId	= representant que s'esrà modificant (no poden ser el mateix)
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		InteressatDto representantDto = expedientInteressatService.findRepresentantById(
				interessatId, 
				representantId);
		InteressatCommand interessatCommand = InteressatCommand.asCommand(representantDto);
		interessatCommand.setEntitatId(entitatActual.getId());
		interessatCommand.setInteressatId(interessatId);
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
			grups.add(Repres.class);
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
			request.getSession().setAttribute(MissatgesHelper.SESSION_ATTRIBUTE_BINDING_ERRORS, bindingResult.getGlobalErrors());
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
		InteressatDto representant = null;
		
		if (interessatCommand.getId() == null) {
			representant = expedientInteressatService.createRepresentant(
					entitatActual.getId(),
					expedientId,
					interessatId,
					representantDto,
					true, 
					RolHelper.getRolActual(request));	
		} else {
			representant = expedientInteressatService.update(
					entitatActual.getId(),
					expedientId,
					interessatId,
					representantDto, 
					RolHelper.getRolActual(request));
			msgKey = "interessat.controller.representant.modificat.ok";
		}

		//Mostrar un warning si el representant no podrà ser notificat telematicament.
		if (representantDto.getTipus()!=null && representantDto.getTipus().equals(InteressatTipusEnumDto.PERSONA_FISICA) &&
			representantDto.getDocumentTipus()!=null && !InteressatDocumentTipusEnumDto.isNotificableTelematic(representantDto.getDocumentTipus())) {
				MissatgesHelper.warning(request, getMessage(request, "interessat.controller.creat.warn.notib", null));
		}
		
		if (!representant.isArxiuPropagat()) {
			return getModalControllerReturnValueWarning(
					request,
					"redirect:../../../contingut/" + expedientId,
					"interessat.controller.creat.error.arxiu.representant",
					new Object[] { interessatCommand.getDocumentNum() });
		} else {
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../contenidor/" + expedientId,
					msgKey,
					new Object[] { interessatCommand.getDocumentNum() });
		}
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

	@RequestMapping(value = "/{expedientId}/comprovarDoc/{documentNum}", method = RequestMethod.GET)
	@ResponseBody
	public InteressatDto comprovarDocInteressatExpedient(
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


	private StringBuilder validarLlistaInteressats(List<InteressatDto> lista, Long entitatId, Long expedientId) {
		StringBuilder validacions = new StringBuilder();

		for (InteressatDto interessatDto : lista) {
			InteressatCommand interessatCommand = InteressatCommand.asCommand(interessatDto);
			interessatCommand.setEntitatId(entitatId);
			interessatCommand.setExpedientId(expedientId);
			interessatCommand.setNotificacioAutoritzat(true);
			interessatCommand.setIncapacitat(false);

	        Set<ConstraintViolation<InteressatCommand>> violations = validarInteressat(interessatCommand);

	        if (!violations.isEmpty()) {
	            validacions.append("Fila ").append(interessatDto.getFila()).append(":<ul>");
	            for (ConstraintViolation<InteressatCommand> v : violations) {
	            	String camp = v.getPropertyPath().toString();
	            	
	            	if (! camp.isBlank())
	            		validacions.append("<li>").append(camp).append(": ").append(v.getMessage()).append("</li>");
	            	else
	            		validacions.append("<li>").append(v.getMessage()).append("</li>");
	            }
	            validacions.append("</ul>");
	        }
		}
		
		return validacions;
	}

	private Set<ConstraintViolation<InteressatCommand>> validarInteressat(InteressatCommand command) {
	    if (command.isPersonaFisica()) {
	        return validator.validate(command, PersonaFisica.class);
	    } else if (command.isPersonaJuridica()) {
	        return validator.validate(command, PersonaJuridica.class);
	    } else if (command.isAdministracio()) {
	        return validator.validate(command, Administracio.class);
	    }
	    return Collections.emptySet();
	}
	
	private boolean esTipusFitxerValid(String fileName, InteressatImportacioTipusDto tipus) {
	    if (fileName == null) return false;

	    Set<String> extensionsExcel = Set.of(".xlsx", ".xls", ".ods");
	    Set<String> extensionsJson = Set.of(".json");

	    if (tipus == InteressatImportacioTipusDto.EXCEL) {
	        return extensionsExcel.stream().anyMatch(fileName::endsWith);
	    } else if (tipus == InteressatImportacioTipusDto.JSON) {
	        return extensionsJson.stream().anyMatch(fileName::endsWith);
	    }
	    return false;
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
			dehActiu = Boolean.parseBoolean(configService.getConfigValue(PropertyConfig.NOTIB_PLUGIN_DEH_ACTIVA));
		} catch (Exception e) {}
		model.addAttribute("dehActiu", dehActiu);
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientInteressatController.class);

}