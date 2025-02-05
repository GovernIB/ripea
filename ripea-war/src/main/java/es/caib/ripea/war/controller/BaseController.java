package es.caib.ripea.war.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.support.RequestContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioTipusEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.InteressatTipusEnumDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.ServeiTipusEnumDto;
import es.caib.ripea.core.api.dto.TipusClassificacioEnumDto;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.DadesExternesService;
import es.caib.ripea.core.api.service.ExpedientInteressatService;
import es.caib.ripea.core.api.service.OrganGestorService;
import es.caib.ripea.core.api.service.PinbalServeiService;
import es.caib.ripea.war.command.DocumentNotificacionsCommand;
import es.caib.ripea.war.command.InteressatCommand;
import es.caib.ripea.war.command.NotificacioEnviamentCommand;
import es.caib.ripea.war.helper.AjaxHelper;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.ModalHelper;
import es.caib.ripea.war.helper.RolHelper;

/**
 * Controlador base que implementa funcionalitats comunes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseController implements MessageSourceAware {

	@Autowired private ExpedientInteressatService expedientInteressatService;
	@Autowired private ContingutService contingutService;
	@Autowired private DadesExternesService dadesExternesService;
	@Autowired private PinbalServeiService pinbalServeiService;
	@Autowired private OrganGestorService organGestorService;
	MessageSource messageSource;

	protected String modalUrlTancar() {
		return "redirect:" + ModalHelper.ACCIO_MODAL_TANCAR;
	}
	protected String ajaxUrlOk() {
		return "redirect:" + AjaxHelper.ACCIO_AJAX_OK;
	}

	protected String getAjaxControllerReturnValueSuccess(
			HttpServletRequest request,
			String url,
			String messageKey) {
		return getAjaxControllerReturnValueSuccess(
				request,
				url,
				messageKey,
				null);
	}
	protected String getAjaxControllerReturnValueSuccess(
			HttpServletRequest request,
			String url,
			String messageKey,
			Object[] messageArgs) {
		if (messageKey != null) {
			MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							messageKey,
							messageArgs));
		}
		if (AjaxHelper.isAjax(request)) {
			return ajaxUrlOk();
		} else {
			return url;
		}
	}
	protected String getAjaxControllerReturnValueError(
			HttpServletRequest request,
			String url,
			String messageKey,
			Throwable ex) {
		return getAjaxControllerReturnValueError(
				request,
				url,
				messageKey,
				null,
				ex);
	}
	protected String getAjaxControllerReturnValueError(
			HttpServletRequest request,
			String url,
			String messageKey,
			Object[] messageArgs,
			Throwable ex) {
		if (messageKey != null) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							messageKey,
							messageArgs),
					ex);
		}
		if (AjaxHelper.isAjax(request)) {
			return ajaxUrlOk();
		} else {
			return url;
		}
	}

	protected String getAjaxControllerReturnValueErrorMessage(
			HttpServletRequest request,
			String url,
			String message,
			Throwable ex) {
		if (message != null) {
			MissatgesHelper.error(
					request, 
					message,
					ex);
		}
		if (AjaxHelper.isAjax(request)) {
			return ajaxUrlOk();
		} else {
			return url;
		}
	}

	protected String getModalControllerReturnValueSuccess(
			HttpServletRequest request,
			String url,
			String messageKey) {
		return getModalControllerReturnValueSuccess(
				request,
				url,
				messageKey,
				null);
	}
	protected String getModalControllerReturnValueSuccess(
			HttpServletRequest request,
			String url,
			String messageKey,
			Object[] messageArgs) {
		if (messageKey != null) {
			MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							messageKey,
							messageArgs));
		}
		if (ModalHelper.isModal(request)) {
			//String redirectionPath = redirectFromModal ? url : "";
			return modalUrlTancar();
		} else {
			return url;
		}
	}
	
	protected String getModalControllerReturnValueWarning(
			HttpServletRequest request,
			String url,
			String messageKey) {
		return getModalControllerReturnValueWarning(
				request,
				url,
				messageKey,
				null);
	}
	
	protected String getModalControllerReturnValueWarningText(
			HttpServletRequest request,
			String url,
			String text) {

			MissatgesHelper.warning(
					request, 
					text);
		
		if (ModalHelper.isModal(request)) {
			return modalUrlTancar();
		} else {
			return url;
		}
	}
	
	protected String getModalControllerReturnValueWarning(
			HttpServletRequest request,
			String url,
			String messageKey,
			Object[] messageArgs) {
		if (messageKey != null) {
			MissatgesHelper.warning(
					request, 
					getMessage(
							request, 
							messageKey,
							messageArgs));
		}
		if (ModalHelper.isModal(request)) {
			//String redirectionPath = redirectFromModal ? url : "";
			return modalUrlTancar();
		} else {
			return url;
		}
	}

	protected String getModalControllerReturnValueError(
			HttpServletRequest request,
			String url,
			String messageKey,
			Throwable ex) {
		return getModalControllerReturnValueError(
				request,
				url,
				messageKey,
				null,
				ex);
	}
	protected String getModalControllerReturnValueError(
			HttpServletRequest request,
			String url,
			String messageKey,
			Object[] messageArgs,
			Throwable ex) {
		if (messageKey != null) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							messageKey,
							messageArgs),
					ex);
		}
		if (ModalHelper.isModal(request)) {
			return modalUrlTancar();
		} else {
			return url;
		}
	}

	protected String getModalControllerReturnValueErrorMessageText(
			HttpServletRequest request,
			String url,
			String message,
			Throwable ex) {
		if (message != null) {
			MissatgesHelper.error(
					request, 
					message,
					ex);
		}
		if (ModalHelper.isModal(request)) {
			return modalUrlTancar();
		} else {
			return url;
		}
	}
	
//	protected String getModalControllerReturnValueErrorMessageText(
//			HttpServletRequest request,
//			String url,
//			String message) {
//		if (message != null) {
//			MissatgesHelper.error(
//					request,
//					message);
//		}
//		if (ModalHelper.isModal(request)) {
//			return modalUrlTancar();
//		} else {
//			return url;
//		}
//	}
	

	protected void writeFileToResponse(
			String fileName,
			byte[] fileContent,
			HttpServletResponse response) throws IOException {
		response.setHeader("Pragma", "");
		response.setHeader("Expires", "");
		response.setHeader("Cache-Control", "");
		response.setHeader("Content-Disposition","attachment; filename=\"" + fileName + "\"");
		if (fileName != null && !fileName.isEmpty())
			response.setContentType(new MimetypesFileTypeMap().getContentType(fileName));
		response.getOutputStream().write(fileContent);
	}

	protected String getMessage(
			HttpServletRequest request,
			String key,
			Object[] args) {
		String message = messageSource.getMessage(
				key,
				args,
				"???" + key + "???",
				new RequestContext(request).getLocale());
		return message;
	}

	protected String getMessage(
			HttpServletRequest request,
			String key) {
		return getMessage(request, key, null);
	}

	protected String getRolActual(
			HttpServletRequest request) {
		return RolHelper.getRolActual(request);
	}

	public ExpedientDto emplenarModelNotificacio(
			HttpServletRequest request,
			EntitatDto entitatActual,
			Long documentId,
			DocumentNotificacionsCommand command,
			Model model,
			Boolean notificacioConcatenatEntregaPostal) throws JsonProcessingException {
		DocumentDto document = (DocumentDto)contingutService.findAmbIdUser(
				entitatActual.getId(),
				documentId,
				false,
				false,
				true,
				null,
				null);
		boolean procedimentSenseCodiSia = false;
		if (document.getExpedientPare()!=null) {
			if (document.getExpedientPare().getMetaExpedient().getTipusClassificacio() == TipusClassificacioEnumDto.ID) {
				procedimentSenseCodiSia = true;
			}
			command.setExpedientPareId(document.getExpedientPare().getId());
		}
		model.addAttribute(
				"procedimentSenseCodiSia",
				procedimentSenseCodiSia);
		model.addAttribute(
				"document",
				document);
		model.addAttribute(
				"notificacioTipusEnumOptions",
				EnumHelper.getOptionsForEnum(
						DocumentNotificacioTipusEnumDto.class,
						"notificacio.tipus.enum.",
						new Enum<?>[] {DocumentNotificacioTipusEnumDto.MANUAL}));
		model.addAttribute(
				"interessatTipus",
				EnumHelper.getOptionsForEnum(
						InteressatTipusEnumDto.class,
						"interessat.tipus.enum."));

		model.addAttribute(
				"notificacioEstatEnumOptions",
				EnumHelper.getOptionsForEnum(
						DocumentEnviamentEstatEnumDto.class,
						"notificacio.estat.enum.",
						new Enum<?>[] {DocumentEnviamentEstatEnumDto.PROCESSAT}));
		model.addAttribute(
				"interessats",
				expedientInteressatService.findByExpedient(
						entitatActual.getId(),
						document.getExpedientPare().getId(),
						true));
		model.addAttribute(
				"expedientId",
				document.getExpedientPare().getId());

        boolean isPermetreEnviamentPostal = entitatActual.isPermetreEnviamentPostal();
        if ( document.getExpedientPare().getOrganGestorId() != null ) {
            OrganGestorDto organGestor = organGestorService.findById(
                    entitatActual.getId(),
                    document.getExpedientPare().getOrganGestorId()
            );

            isPermetreEnviamentPostal = isPermetreEnviamentPostal
                    || organGestor.isPermetreEnviamentPostal()
                    || organGestorService.isPermisAntecesor(document.getExpedientPare().getOrganGestorId(), false);
        }

		if (isPermetreEnviamentPostal) {
			if (notificacioConcatenatEntregaPostal != null) {
				model.addAttribute("entregaPostal", (boolean) notificacioConcatenatEntregaPostal);
			} else {
				model.addAttribute("entregaPostal", true);
			}
		} else {
			model.addAttribute("entregaPostal", false);

            if ( !entitatActual.isPermetreEnviamentPostal() ) {
                model.addAttribute("entregaPostalMsg", "notificacio.form.entregapostal.err");
            } else if (document.getExpedientPare().getOrganGestorId() != null){
                model.addAttribute("entregaPostalMsg", "notificacio.form.entregapostal.err.desc");
            }
		}

		model.addAttribute(
				"serveiTipusEstats",
				EnumHelper.getOptionsForEnum(
						ServeiTipusEnumDto.class,
						"notificacio.servei.tipus.enum."));
		if (command != null) {
			List<InteressatDto> interessats = expedientInteressatService.findByExpedient(
					entitatActual.getId(),
					document.getExpedientPare().getId(),
					true);
			command.getEnviaments().clear();

			for (InteressatDto interessatDto : interessats) {
				NotificacioEnviamentCommand notificacioParte = new NotificacioEnviamentCommand();

				notificacioParte.setTitular(InteressatCommand.asCommand(interessatDto));
				if (interessatDto.getRepresentant() != null) {
					notificacioParte.setDestinatari(InteressatCommand.asCommand(interessatDto.getRepresentant()));
				}
				command.getEnviaments().add(notificacioParte);
			}
			if (command.getEnviaments() != null && !command.getEnviaments().isEmpty()) {
				ObjectMapper mapper = new ObjectMapper();
				String notificacions = mapper.writeValueAsString(command.getEnviaments());
				model.addAttribute("notificacions", notificacions);
				ompleDadesAdresa(request, model);
			}
		}
		return document.getExpedientPare();
	}

	private void ompleDadesAdresa(HttpServletRequest request, Model model) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			String paisos = mapper.writeValueAsString(dadesExternesService.findPaisos());
			model.addAttribute("paisos", paisos);
		} catch (Exception e) {
			MissatgesHelper.warning(request, getMessage(request, "interessat.controller.paisos.error"));
		}
		try {
			String provincies = mapper.writeValueAsString(dadesExternesService.findProvincies());
			model.addAttribute("provincies", provincies);
		} catch (Exception e) {
			MissatgesHelper.warning(request, getMessage(request, "interessat.controller.provincies.error"));
		}
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public void loadServeisPinbal(Model model, boolean nomesActius) {
		if (nomesActius) {
			model.addAttribute("pinbalServeiEnumOptions", pinbalServeiService.findActius());
		} else {
			model.addAttribute("pinbalServeiEnumOptions", pinbalServeiService.findAll());
		}
	}

	//Converteix un objecte bindingResult, per retornar com una llista que es pugui
	//passar a la funcio webutil.commons.js. showBindingErrors perque mostri els errors als camps
	public List<ObjectError> bindingResultToJquery(HttpServletRequest request, BindingResult bindingResult) {
		List<ObjectError> resultat = new ArrayList<ObjectError>();
		for (ObjectError obj: bindingResult.getAllErrors()) {
			String objectName = null;
			String defaultMessage = null;
			
			if (obj instanceof FieldError) {
				objectName = ((FieldError)obj).getField();				
			} else {
				//Validacions que no indiquen el nom del camp, pero ho podem deduir per els codis dels missates...
				if (ArrayUtils.contains(obj.getCodes(), "classificacioSia")) {
					objectName = "classificacioSia";
				} else if (ArrayUtils.contains(obj.getCodes(), "CodiMetaExpedientNoRepetit")) {
					objectName = "codi";
				} else if (ArrayUtils.contains(obj.getCodes(), "organGestorId")) {
					objectName = "organGestorId";
				}
			}
			
			if (obj.getCodes()!=null && obj.getCodes().length>0) {
				for (String codi: obj.getCodes()) {
					String aux = getMessage(request, codi);
					if (aux!=null && !"".equals(aux.trim()) && !aux.startsWith("???")) {
						defaultMessage = aux;
						break;
					}
				}
			}
			
			if (defaultMessage==null) {
				//El default message podria ser un codi de traduccions o el literal final a mostrar.
				String dm = getMessage(request, obj.getDefaultMessage());
				if (dm!=null && !"".equals(dm.trim()) && !dm.startsWith("???")) {
					defaultMessage = dm;
				} else {
					defaultMessage = obj.getDefaultMessage();
				}
			}
			
			ObjectError aux = new ObjectError(objectName, defaultMessage);
			resultat.add(aux);
		}
		return resultat;
	}
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}
}
