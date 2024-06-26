package es.caib.ripea.core.api.exception;

public class InteressatAssociarException extends RuntimeException {

    public InteressatAssociarException() {
        super("S'ha produït un error al associar l'interessat");
    }

    public InteressatAssociarException(String message) {
        super(message);
    }

    private static final long serialVersionUID = -5807921492661695439L;
}
