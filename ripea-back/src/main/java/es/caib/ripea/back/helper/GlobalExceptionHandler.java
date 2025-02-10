package es.caib.ripea.back.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler implements MessageSourceAware {
	
	MessageSource messageSource;
	
    @ExceptionHandler({MultipartException.class})
    public final String handleException(MultipartException ex, HttpServletRequest request) {
    	logger.error("Multipart Excepcion", ex);

    	if (ex.getCause().getMessage().contains("No queda espacio en el dispositivo")){
    		return getModalControllerReturnValueError(request, null, "error.multipart.noQuedaEspacioEnElDispositivo", null, ex.getCause());
    	} else {
    		return getModalControllerReturnValueErrorMessageText(request, null, ex.getCause().getCause().getMessage(), ex.getCause().getCause());
    	}
    }
    
	private String getModalControllerReturnValueError(
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
		if (ModalHelper.isRequestPathModal(request)) {
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
		if (ModalHelper.isRequestPathModal(request)) {
			return modalUrlTancar();
		} else {
			return url;
		}
	}
	
	private String modalUrlTancar() {
		return "redirect:" + ModalHelper.ACCIO_MODAL_TANCAR;
	}
	
	private String getMessage(
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
	
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class); 
}