package es.caib.ripea.service.intf.exception;

public class ElementNotValidException extends RuntimeException {

    private static final long serialVersionUID = 7251111046083112422L;

	public ElementNotValidException(String message) {
        super(message);
    }

}