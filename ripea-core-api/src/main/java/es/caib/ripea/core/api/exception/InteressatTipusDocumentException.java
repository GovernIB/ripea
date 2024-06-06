package es.caib.ripea.core.api.exception;

public class InteressatTipusDocumentException extends RuntimeException {

    public InteressatTipusDocumentException() {
        super("S'està intentant incorporar un interessat que ja està donat d'alta a l'expedient amb un tipus diferent");
    }

    public InteressatTipusDocumentException(String numDocument, String original, String nou, Long expedientId) {
        super("S'està intentant incorporar un interessat de tipus " + nou + " amb número de document " + numDocument + " que ja està donat d'alta a l'expedient " + expedientId + " amb el tipus " + original);
    }

    private static final long serialVersionUID = -5807921492661695439L;
}
