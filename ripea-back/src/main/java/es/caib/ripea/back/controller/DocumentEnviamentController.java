package es.caib.ripea.back.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import es.caib.ripea.back.command.DocumentNotificacionsCommand;
import es.caib.ripea.back.command.DocumentPublicacioCommand;
import es.caib.ripea.back.command.NotificacioEnviamentCommand;
import es.caib.ripea.back.helper.EnumHelper;
import es.caib.ripea.back.helper.ExceptionHelper;
import es.caib.ripea.back.helper.MissatgesHelper;
import es.caib.ripea.plugin.NotibRepostaException;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.ContingutService;
import es.caib.ripea.service.intf.service.DocumentEnviamentService;
import es.caib.ripea.service.intf.service.DocumentService;
import es.caib.ripea.service.intf.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.ConnectException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Controlador per als enviaments dels expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/document")
public class DocumentEnviamentController extends BaseUserController {

 	@Autowired private DocumentEnviamentService documentEnviamentService;
	@Autowired private ContingutService contingutService;
	@Autowired private DocumentService documentService;

    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

	@RequestMapping(value = "/{documentId}/notificar", method = RequestMethod.GET)
	public String notificarGet(
			HttpServletRequest request,
			@PathVariable Long documentId,
			Model model) throws JsonProcessingException {
		DocumentNotificacionsCommand command = new DocumentNotificacionsCommand();
		Integer numDies = 10;
		command.setCaducitatDiesNaturals(numDies.toString());
		command.setDataCaducitat(sumarDiesNaturals(numDies));
		command.setDocumentId(documentId);
		model.addAttribute(command);
        try {
            EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
            emplenarModelNotificacio(
                    request,
                    entitatActual,
                    documentId,
                    command,
                    model, null);
        }catch (SecurityException e){
            e.printStackTrace();
            MissatgesHelper.error(
                    request,
                    getMessage(request,"entitat.list.consulta.ajax.error")+": "+e.getMessage(),
                    e);
        }

		return "notificacioForm";
	}

	@RequestMapping(value = "/{documentId}/notificar", method = RequestMethod.POST)
	public String notificarPost(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@Validated({DocumentNotificacionsCommand.Create.class}) DocumentNotificacionsCommand command,
			BindingResult bindingResult,
			Model model) throws JsonProcessingException {

		if (command.getDataCaducitat() == null) {
			bindingResult.rejectValue("dataCaducitat", "NotEmpty");
		}
		
		if (command.getTipus() == DocumentNotificacioTipusEnumDto.COMUNICACIO && documentEnviamentService.checkIfDocumentIsZip(documentId) && documentEnviamentService.checkIfAnyInteressatIsAdministracio(command.getInteressatsIds())) {
			bindingResult.reject("notificacio.controller.reject.comunicacio.zip.administracio");
		}
		
		boolean nifsErrorPostal = false;
		if (command.getEntregaPostal()!=null && command.getEntregaPostal().booleanValue()) {
			for (NotificacioEnviamentCommand enviament: command.getEnviaments()) {
				if (Utils.isEmpty(enviament.getTitular().getPais()) || Utils.isEmpty(enviament.getTitular().getProvincia()) ||
					Utils.isEmpty(enviament.getTitular().getMunicipi()) || Utils.isEmpty(enviament.getTitular().getCodiPostal()) ||
					Utils.isEmpty(enviament.getTitular().getAdresa())) {
						nifsErrorPostal = true;
						break;
				}
			}
		}
		
		if (nifsErrorPostal) {
			bindingResult.reject("notificacio.controller.reject.postal");
		}

        try {
            EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
            if (bindingResult.hasErrors()) {
                emplenarModelNotificacio(request, entitatActual, documentId, command, model, null);
                request.getSession().setAttribute(MissatgesHelper.SESSION_ATTRIBUTE_BINDING_ERRORS, bindingResult.getGlobalErrors());
                return "notificacioForm";
            }

			Map<String, String> errorsNotib = documentEnviamentService.notificacioCreate(
					entitatActual.getId(),
					documentId,
					DocumentNotificacionsCommand.asDto(command));

			if (!errorsNotib.isEmpty()) {
				for (Map.Entry<String, String> errorNotib: errorsNotib.entrySet()) {
					MissatgesHelper.error(
							request, 
							getMessage(
									request, 
									"document.controller.notificacio.ko", 
									new Object[] {errorNotib.getKey(), errorNotib.getValue()}),
							null);
				}
			}
			return this.getModalControllerReturnValueSuccess(
					request,
					"redirect:../../../contingut/" + documentId,
					"document.controller.notificacio.ok");

		} catch (Exception e) {
			logger.error("Error al enviar notificació", e);
			String msg = getMessage(request, "document.controller.notificacio.error") + ": ";
			Throwable root = ExceptionHelper.getRootCauseOrItself(e);
			if (root instanceof NotibRepostaException) {
				msg += getMessage(request, "contingut.enviament.errorReposta.notib") + " " + root.getMessage();
			} else if (root instanceof ConnectException || root.getMessage().contains("timed out")) {
				msg += getMessage(request, "error.notib.connectTimedOut");
			} else if (root instanceof NotFoundException) {
				msg += getMessage(request, "error.notib.postalNotFound");
			} else {
				root.getMessage();
				msg += root.getMessage();
			}

			return getModalControllerReturnValueErrorMessageText(
					request,
					"",
					msg,
					e);
		}
	}

	@RequestMapping(value = "/{documentId}/guardaFormSessio", method = RequestMethod.POST)
	@ResponseBody
	public String guardaFormSessio(
			HttpServletRequest request,
			@PathVariable Long documentId,
			DocumentNotificacionsCommand command) {
		//Guardam en la sessió del usuari actual, el formulari tal i com es troba, no fa falta validar
		request.getSession().setAttribute("DocumentEnviamentController.command", command);
		return "OK";
	}

	@RequestMapping(value = "/{documentId}/notificacio/{notificacioId}/info")
	public String notificacioInfo(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@PathVariable Long notificacioId,
			@RequestParam(value = "contingutNavigationId", required = true) Long contingutNavigationId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"notificacio",
				documentEnviamentService.notificacioFindAmbIdAndDocument(
						entitatActual.getId(),
						documentId,
						notificacioId));
		model.addAttribute(
				"contingutNavigationId",
				contingutNavigationId);
		return "notificacioInfo";
	}
	
	@RequestMapping(value = "/{documentId}/notificacio/{notificacioId}/ampliar", method = RequestMethod.GET)
	public String notificacioAmpliarGet(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@PathVariable Long notificacioId,
			@RequestParam(value = "contingutNavigationId", required = true) Long contingutNavigationId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		DocumentNotificacioDto documentNotificacioDto = documentEnviamentService.notificacioFindAmbIdAndDocument(
				entitatActual.getId(),
				documentId,
				notificacioId);
		AmpliarPlazoForm formData = new AmpliarPlazoForm();
		formData.setId(documentNotificacioDto.getId());
		List<DocumentEnviamentInteressatDto> myList = new ArrayList<>(documentNotificacioDto.getDocumentEnviamentInteressats());
		formData.setDocumentEnviamentInteressats(myList);
		model.addAttribute("documentNotificacioDto", formData);
		model.addAttribute("contingutNavigationId", contingutNavigationId);
		model.addAttribute("documentId", documentId);
		model.addAttribute("notificacioId", notificacioId);
		return "notificacioAmpliar";
	}
	
	@RequestMapping(value = "/notificacio/{documentNotificacioId}/ampliar", method = RequestMethod.POST)
	public String notificacioAmpliarPost(
			HttpServletRequest request,
			@PathVariable Long documentNotificacioId,
			@ModelAttribute AmpliarPlazoForm documentNotificacioDto,
			Model model) {
		try {
			List<RespostaAmpliarPlazo> resultat = documentEnviamentService.ampliarPlazoEnviament(documentNotificacioDto);
			
			String missatge = getMessage(request, "enviament.info.accio.ectualitzar.ampliarOk", new Object[] { resultat.size() });
			
			for (RespostaAmpliarPlazo resposta: resultat) {
				missatge = missatge + "<br/> - <b>"+resposta.getDocumentNum() + "</b> " + resposta.getNomInteressat()+": "+resposta.getRespostaDescripcio();
			}
			
			MissatgesHelper.info(request, missatge);
//			return getModalControllerReturnValueSuccess(
//					request,
//					"notificacioAmpliar",
//					"enviament.info.accio.ectualitzar.ampliarOk",
//					new Object[] { resultat.size() });
			return modalUrlTancar();
		} catch (Exception ex) {
			return getModalControllerReturnValueError(request, "notificacioAmpliar", "enviament.info.accio.ectualitzar.ampliarKo", ex);
		}
	}
	
	@RequestMapping(value = "/notificacio/actualitzarEstat/{identificador}")
	public String notificacioActualitzarEstat(
			HttpServletRequest request,
			@PathVariable String identificador,
			@RequestParam(value = "contingutNavigationId", required = false) Long contingutNavigationId,
			Model model) {
		String url = null;
		if (contingutNavigationId != null) {
			url = "redirect:/contingut/" + contingutNavigationId + "#notificacions";
		} else {
			url = "redirect:/seguimentNotificacions";
		}
		try {
			getEntitatActualComprovantPermisos(request);
			
			documentService.notificacioActualitzarEstat(
					identificador);
			
			return getModalControllerReturnValueSuccess(
					request,
					url,
					"contingut.enviament.actualitzar.estat.ok");
		} catch (Exception e) {
			logger.error("Error al actualitzar estat del enviament", e);
			String msg = getMessage(request, "contingut.enviament.actualitzar.estat.error") + ": ";
			Throwable root = ExceptionHelper.getRootCauseOrItself(e);

			if (root instanceof ConnectException || root.getMessage().contains("timed out")){
				msg += getMessage(request, "error.notib.connectTimedOut");
			} else {
				root.getMessage();
				msg += root.getMessage();
			}

			return getModalControllerReturnValueErrorMessageText(
					request,
					url,
					msg,
					e);
		}
	}

	@RequestMapping(value = "/{documentId}/notificacio/{notificacioId}/descarregarJustificantEnviamentNotib", method = RequestMethod.GET)
	public String notificacioConsultarIDescarregarJustificant(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long documentId,
			@PathVariable Long notificacioId) throws IOException {
		getEntitatActualComprovantPermisos(request);
		RespostaJustificantEnviamentNotibDto info = documentService.notificacioDescarregarJustificantEnviamentNotib(
				notificacioId);

		if (info.getJustificant() != null) {
			writeFileToResponse(
					"justificant.pdf",
					info.getJustificant(),
					response);
		} else {
			return this.getModalControllerReturnValueError(
					request,
					"redirect:../" + documentId +"/notificacio/" + notificacioId + "/info",
					"expedient.controller.notificacio.justificant.ko",
					null);
		}
		return null;
	}



	@RequestMapping(value = "/{documentId}/notificacio/{notificacioId}", method = RequestMethod.GET)
	public String notificacioUpdateGet(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@PathVariable Long notificacioId,
			@RequestParam(required = false) Boolean notificacioConcatenatEntregaPostal,
			Model model) throws JsonProcessingException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		emplenarModelNotificacio(
				request,
				getEntitatActualComprovantPermisos(request),
				documentId,
				null,
				model,
				notificacioConcatenatEntregaPostal);
		DocumentNotificacionsCommand command = DocumentNotificacionsCommand.asCommand(
				documentEnviamentService.notificacioFindAmbIdAndDocument(
						entitatActual.getId(),
						documentId,
						notificacioId));
		model.addAttribute(command);
		
		return "notificacioForm";
	}
	@RequestMapping(value = "/{documentId}/notificacio/{notificacioId}", method = RequestMethod.POST)
	public String notificacioUpdatePost(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@PathVariable Long notificacioId,
			@Validated({DocumentNotificacionsCommand.Update.class}) DocumentNotificacionsCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			emplenarModelNotificacio(request, getEntitatActualComprovantPermisos(request), documentId, null, model, null);
			request.getSession().setAttribute(MissatgesHelper.SESSION_ATTRIBUTE_BINDING_ERRORS, bindingResult.getGlobalErrors());
			return "notificacioForm";
		}
		DocumentNotificacioDto documentNotificacioDto = documentEnviamentService.notificacioUpdate(
				entitatActual.getId(),
				documentId,
				DocumentNotificacionsCommand.asDto(command));
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../contingut/" + documentId,
				"expedient.controller.notificacio.modificada.ok",
				new Object[] { documentNotificacioDto.getRegistreNumeroFormatat() });
	}

	@RequestMapping(value = "/{documentId}/notificacio/{notificacioId}/delete", method = RequestMethod.GET)
	public String notificacioDelete(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@PathVariable Long notificacioId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DocumentNotificacioDto documentNotificacioDto= documentEnviamentService.notificacioDelete(
				entitatActual.getId(),
				documentId,
				notificacioId);
		return this.getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../../../contingut/" + documentId,
				"expedient.controller.notificacio.esborrada.ok",
				new Object[] { documentNotificacioDto.getRegistreNumeroFormatat() });
	}
	
	
	
	
	@RequestMapping(value = "/{pareId}/document/{documentId}/descarregar", method = RequestMethod.GET)
	public String descarregar(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long pareId,
			@PathVariable Long documentId) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutDto contingut = contingutService.findAmbIdUser(
				entitatActual.getId(),
				documentId,
				true,
				false, true, null, null);
		if (contingut instanceof DocumentDto) {
			FitxerDto fitxer = documentService.descarregar(
					entitatActual.getId(),
					documentId,
					null, 
					null);
			writeFileToResponse(
					fitxer.getNom(),
					fitxer.getContingut(),
					response);
			return null;
		}
		MissatgesHelper.error(
				request, 
				getMessage(
						request, 
						"document.controller.descarregar.error"),
				null);
		if (contingut.getPare() != null)
			return "redirect:../../contingut/" + pareId;
		else
			return "redirect:../../expedient";
	}
	
	
	@RequestMapping(value = "/{enviamentId}/descarregarCertificacio", method = RequestMethod.GET)
	public String notificacioConsultarIDescarregarCertificacio(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long enviamentId) throws IOException {

		writeFileToResponse(
				"certificacio.pdf",
				documentService.notificacioConsultarIDescarregarCertificacio(enviamentId),
				response);
		return null;
	}
	
	
	

	/*@RequestMapping(value = "/{documentId}/notificacio/{notificacioId}/refrescar")
	public String notificacioRefrescar(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@PathVariable Long notificacioId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		boolean fetaAmbExit = documentEnviamentService.notificacioRetry(
				entitatActual.getId(),
				documentId,
				notificacioId);
		if (fetaAmbExit) {
			MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.controller.notificacio.reintent.ok"));
		} else {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"expedient.controller.notificacio.reintent.error"));
		}
		return "redirect:info";
	}*/

	@RequestMapping(value = "/{documentId}/publicar", method = RequestMethod.GET)
	public String publicarGet(
			HttpServletRequest request,
			@PathVariable Long documentId,
			Model model) {
		emplenarModelPublicacio(
				request,
				documentId,
				model);
		DocumentPublicacioCommand command = new DocumentPublicacioCommand();
		command.setDocumentId(documentId);
		model.addAttribute(command);
		return "publicacioForm";
	}

	@RequestMapping(value = "/{documentId}/publicar", method = RequestMethod.POST)
	public String publicarPost(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@Validated({DocumentPublicacioCommand.Create.class}) DocumentPublicacioCommand command,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			emplenarModelPublicacio(request, documentId, model);
			request.getSession().setAttribute(MissatgesHelper.SESSION_ATTRIBUTE_BINDING_ERRORS, bindingResult.getGlobalErrors());
			return "publicacioForm";
		}
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		documentEnviamentService.publicacioCreate(
				entitatActual.getId(),
				documentId,
				DocumentPublicacioCommand.asDto(command));
		return this.getModalControllerReturnValueSuccess(
				request,
				"redirect:../../../contingut/" + documentId,
				"document.controller.publicacio.ok");
	}

	@RequestMapping(value = "/{documentId}/publicacio/{publicacioId}/info")
	public String publicacioInfo(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@PathVariable Long publicacioId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"publicacio",
				documentEnviamentService.publicacioFindAmbId(
						entitatActual.getId(),
						documentId,
						publicacioId));
		return "publicacioInfo";
	}

	@RequestMapping(value = "/{documentId}/publicacio/{publicacioId}", method = RequestMethod.GET)
	public String publicacioUpdateGet(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@PathVariable Long publicacioId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		emplenarModelPublicacio(
				request,
				documentId,
				model);
		DocumentPublicacioCommand command = DocumentPublicacioCommand.asCommand(
				documentEnviamentService.publicacioFindAmbId(
						entitatActual.getId(),
						documentId,
						publicacioId));
		model.addAttribute(command);
		return "publicacioForm";
	}
	@RequestMapping(value = "/{documentId}/publicacio/{publicacioId}", method = RequestMethod.POST)
	public String publicacioUpdatePost(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@PathVariable Long publicacioId,
			@Validated({DocumentPublicacioCommand.Update.class}) DocumentPublicacioCommand command,
			BindingResult bindingResult,
			Model model) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			emplenarModelPublicacio(request, documentId, model);
			request.getSession().setAttribute(MissatgesHelper.SESSION_ATTRIBUTE_BINDING_ERRORS, bindingResult.getGlobalErrors());
			return "publicacioForm";
		}
		DocumentPublicacioDto documentPublicacioDto = documentEnviamentService.publicacioUpdate(
				entitatActual.getId(),
				documentId,
				DocumentPublicacioCommand.asDto(command));
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../contingut/" + documentId,
				"expedient.controller.publicacio.modificada.ok",
				new Object[] { documentPublicacioDto.getDocumentNom() });
	}

	@RequestMapping(value = "/{documentId}/publicacio/{publicacioId}/delete", method = RequestMethod.GET)
	public String publicacioDelete(
			HttpServletRequest request,
			@PathVariable Long documentId,
			@PathVariable Long publicacioId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DocumentPublicacioDto documentPublicacioDto = documentEnviamentService.publicacioDelete(
				entitatActual.getId(),
				documentId,
				publicacioId);
		return this.getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../../../contingut/" + documentId,
				"expedient.controller.publicacio.esborrada.ok",
				new Object[] { documentPublicacioDto.getDocumentNom() });
	}

	@RequestMapping(value = "/{documentId}/estat", method = RequestMethod.GET)
	@ResponseBody
	public String getEstatDarreraNotificació(
			HttpServletRequest request,
			@PathVariable Long documentId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DocumentEnviamentDto lastEnviament = null;
		List<DocumentEnviamentDto> enviaments = documentEnviamentService.findAmbDocument(
				entitatActual.getId(), 
				documentId);
		if (enviaments != null && !enviaments.isEmpty()) {
			//Order by date
			Collections.sort(enviaments);
			lastEnviament = enviaments.get(enviaments.size() - 1);
			return lastEnviament.getEstat().name();
		}
		return null;
	}
	
	
    @RequestMapping(value = "/notificacio/caducitatDiesNaturals/{dia}/{mes}/{any}", method = RequestMethod.GET)
    @ResponseBody
    private long getDiesCaducitat(@PathVariable String dia, @PathVariable String mes, @PathVariable String any) throws ParseException {
        Date data = df.parse(dia + "/" + mes + "/" + any);
        return getDiesEntreDates(data);
    }

    @RequestMapping(value = "/notificacio/caducitatData/{dies}", method = RequestMethod.GET)
    @ResponseBody
    private String getDataCaducitat(@PathVariable int dies) {
        return df.format(sumarDiesNaturals(dies));
    }
    
	public Date sumarDiesNaturals(
			int diesCaducitat) {
		return sumarDiesNaturals(new Date(), diesCaducitat);
	}

	public Date sumarDiesNaturals(
			Date dataCaducitat,
			int diesCaducitat) {
		Calendar diaActual = Calendar.getInstance();
		diaActual.setTime(dataCaducitat);
		diaActual.add(Calendar.DATE, diesCaducitat);
		return diaActual.getTime();
	}
    
	public int getDiesEntreDates(Date fi) {
		return getDiesEntreDates(new Date(), fi);
	}

	public int getDiesEntreDates(Date inici, Date fi) {
		long diff = fi.getTime() - inici.getTime();
		return new Long(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) +1).intValue();
	}

	private void emplenarModelPublicacio(
			HttpServletRequest request,
			Long documentId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"publicacioTipusEnumOptions",
				EnumHelper.getOptionsForEnum(
						DocumentPublicacioTipusEnumDto.class,
						"publicacio.tipus.enum."));
		model.addAttribute(
				"publicacioEstatEnumOptions",
				EnumHelper.getOptionsForEnum(
						DocumentEnviamentEstatEnumDto.class,
						"publicacio.estat.enum.",
						new Enum<?>[] {
							DocumentEnviamentEstatEnumDto.ENVIAT,
							DocumentEnviamentEstatEnumDto.PROCESSAT,
							DocumentEnviamentEstatEnumDto.CANCELAT}));
		
		model.addAttribute(
				"document",
				contingutService.findAmbIdUser(
						entitatActual.getId(),
						documentId,
						false,
						false, true, null, null));
		model.addAttribute(
				"publicacioTipusEnumOptions",
				EnumHelper.getOptionsForEnum(
						DocumentPublicacioTipusEnumDto.class,
						"publicacio.tipus.enum."));
		model.addAttribute(
				"publicacioEstatEnumOptions",
				EnumHelper.getOptionsForEnum(
						DocumentEnviamentEstatEnumDto.class,
						"publicacio.estat.enum.",
						new Enum<?>[] {
							DocumentEnviamentEstatEnumDto.ENVIAT,
							DocumentEnviamentEstatEnumDto.PROCESSAT,
							DocumentEnviamentEstatEnumDto.CANCELAT}));
		
	}
	
	private static final Logger logger = LoggerFactory.getLogger(DocumentEnviamentController.class);

}
